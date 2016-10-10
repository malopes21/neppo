package com.neppo.authenticatorserver.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.core.AuthnRequest;
//import org.apache.shiro.cache.Cache;
//import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.domain.AccountStatus;
import com.neppo.authenticatorserver.domain.AuthenticationRequest;
import com.neppo.authenticatorserver.domain.AuthenticationResponse;
import com.neppo.authenticatorserver.domain.AuthenticationRule;
import com.neppo.authenticatorserver.domain.AuthenticationRuleType;
import com.neppo.authenticatorserver.domain.SamlSsoConfig;
import com.neppo.authenticatorserver.domain.User;
import com.neppo.authenticatorserver.domain.exception.DaoException;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.service.exception.AccountNotFoundException;
import com.neppo.authenticatorserver.service.exception.AccountStatusNotValidException;
import com.neppo.authenticatorserver.service.exception.AuthenticationPolicyException;
import com.neppo.authenticatorserver.service.exception.CredentialsNotValidException;
import com.neppo.authenticatorserver.service.exception.JsonParseException;
import com.neppo.authenticatorserver.session.LoginSessionManager;
import com.neppo.authenticatorserver.session.Session;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class LogoutController {

	@Autowired
	protected AuthenticationService authenticationService;
	protected LoginSessionManager sessionManager;

	public static final String LOGIN_USERNAME = "user.email";
	public static final String LOGIN_PASSWORD = "user.password";
	public static final String THROTTLING_COUNT = "THROTTLING_COUNT";
	public static final int REMEMBER_TIME = 60 * 60 * 24 * 365;

	protected SAMLSignature signature;

	@RequestMapping("/login-form-old")
	public String loginForm(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		model.addAttribute("erro", "");
		return "login";
	}

	@RequestMapping("/login-old")
	public String login(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		Integer throttlingCount = (Integer) req.getSession().getAttribute(THROTTLING_COUNT);
		if(throttlingCount == null) {
			req.getSession().setAttribute(THROTTLING_COUNT, 0);
		}

		AuthenticationRequest authnRequest = null;
		try {

			authnRequest = createAuthenticationRequest(req, resp);

		} catch (Exception ex) {

			model.addAttribute("erro", "Erro 1: " + ex.getMessage());
			return "login";
		}

		AuthenticationResponse authnResponse = null;
		try {

			authnResponse = authenticationService.authenticateUser(authnRequest);

		} catch (AccountStatusNotValidException ex) {

			model.addAttribute("erro", "Erro 0: " + ex.getMessage());
			return "login";

		} catch (JsonParseException ex) {

			model.addAttribute("erro", "Erro 1: " + ex.getMessage());
			return "login";

		} catch (CredentialsNotValidException ex) {

			String retorno = validateThrottling(req, ex.getAuthnResponse());
			model.addAttribute("erro", "Erro 2: " + (retorno == null ? ex.getMessage() : retorno));
			return "login";

		} catch (AuthenticationPolicyException ex) {

			model.addAttribute("erro", "Erro 3: " + ex.getMessage());
			return "login";

		} catch (AccountNotFoundException ex) {

			model.addAttribute("erro", "Erro 4: " + ex.getMessage());
			return "login";

		} catch (DaoException ex) {

			model.addAttribute("erro", "Erro 5: " + ex.getMessage());
			return "login";

		} catch (Exception ex) {

			model.addAttribute("erro", "Erro 6: " + ex.getMessage());
			return "login";
		}

		return processLogin(req, resp, model, authnResponse);
	}
	
	private String processLogin(HttpServletRequest req, HttpServletResponse resp, Model model,
			AuthenticationResponse authnResponse) throws ServletException, IOException {

		if (authnResponse.isSucess()) {

			if (haveMFA(authnResponse.getRules())) {

				if (haveRemember(authnResponse.getRules())) {
					req.getSession().setAttribute(String.valueOf(REMEMBER_TIME), true);
				}
				req.getSession().setAttribute("authnResponse", authnResponse);           //cookies?
				return "mfa";

			} else {

				if (haveRemember(authnResponse.getRules())) {
					Cookie sessionIdCookie = getSessionIdCookie(req.getCookies());
					if (sessionIdCookie != null) {
						sessionIdCookie.setMaxAge(REMEMBER_TIME);
						resp.addCookie(sessionIdCookie);
					}
				}
				User user = new User();
				user.setUsername(authnResponse.getAccount().getUsername());
				user.setEmail(authnResponse.getAccount().getDescription());
				user.setFirstName(authnResponse.getAccount().getName());
				user.setName(authnResponse.getAccount().getName());
				user.setSurName(authnResponse.getAccount().getName());
				Subject.authenticate(user);

				req.getRequestDispatcher("/sso").forward(req, resp);
				return null;
			}
		} else {

			String error = authenticationService.createErrorMessage(authnResponse).toString();
			model.addAttribute("erro", "Não foi possível efetuar login. " + error == null ? "" : error);
			return "login";
		}
	}

	private String validateThrottling(HttpServletRequest req, AuthenticationResponse authnResponse) {

		try {

			AuthenticationRule throttlingRule = getThrottlingRule(authnResponse.getRules());

			if (throttlingRule != null) {

				int throttlingCount = (Integer) req.getSession().getAttribute(THROTTLING_COUNT) + 1;
				req.getSession().setAttribute(THROTTLING_COUNT, throttlingCount);

				int attempts = (Integer) throttlingRule.getParams().get("attempts");
				if (throttlingCount > attempts) {
					authnResponse.getAccount().setStatus(AccountStatus.DEACTIVATED);
					authenticationService.updateAccountStatus(authnResponse.getAccount().getId(),
							AccountStatus.DEACTIVATED.toString());
					return "Conta DESATIVADA!";
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	private AuthenticationRule getThrottlingRule(List<AuthenticationRule> rules) {

		if (rules == null || rules.isEmpty()) {
			return null;
		}

		for (AuthenticationRule rule : rules) {
			if (rule.getType() == AuthenticationRuleType.CHECK_THROTTLING) {
				return rule;
			}
		}
		return null;
	}
	
	public static Cookie getSessionIdCookie(Cookie[] cookies) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("JSESSIONID")) {
				return cookie;
			}
		}
		return null;
	}

	private boolean haveRemember(List<AuthenticationRule> rules) {

		if (rules == null || rules.isEmpty()) {
			return false;
		}

		for (AuthenticationRule rule : rules) {
			if (rule.getType() == AuthenticationRuleType.CHECK_REMEMBER) {
				return rule.isValidated();
			}
		}
		return false;
	}

	private boolean haveMFA(List<AuthenticationRule> rules) {

		if (rules == null || rules.isEmpty()) {
			return false;
		}

		for (AuthenticationRule rule : rules) {
			if (rule.getType() == AuthenticationRuleType.CHECK_MFA) {
				return rule.isValidated();
			}
		}
		return false;
	}

	private AuthenticationRequest createAuthenticationRequest(HttpServletRequest req, HttpServletResponse resp) {

		AuthnRequest samlAuthnRequest = (AuthnRequest) Session.getAttribute(SamlUtils.REQUEST);
		SamlSsoConfig samlConfig = authenticationService.findSamlConfig(samlAuthnRequest.getIssuer().getValue());

		AuthenticationRequest authnRequest = new AuthenticationRequest();
		authnRequest.setIssuer(samlConfig.getIssuer());
		authnRequest.setUsername(req.getParameter(LOGIN_USERNAME));
		authnRequest.setPassword(req.getParameter(LOGIN_PASSWORD));
		authnRequest.setRemoteHost(req.getRemoteHost());
		authnRequest.setRemoteAddr(req.getRemoteAddr());
		authnRequest.setRemoteUser(req.getRemoteUser());
		authnRequest.setRequestUri(req.getRequestURI());
		authnRequest.setSessionId(req.getRequestedSessionId());
		authnRequest.setUserAgent(req.getHeader("User-Agent"));
		authnRequest.setDate(req.getHeader("Date"));
		authnRequest.setOrigin(req.getHeader("Origin"));
		authnRequest.setHost(req.getHeader("Host"));
		authnRequest.setRequestMethod(req.getMethod());

		return authnRequest;
	}

}
