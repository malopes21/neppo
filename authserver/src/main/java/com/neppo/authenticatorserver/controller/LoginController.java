package com.neppo.authenticatorserver.controller;

import java.io.IOException;

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

import com.neppo.authenticatorserver.domain.AuthenticationRequest;
import com.neppo.authenticatorserver.domain.AuthenticationResponse;
import com.neppo.authenticatorserver.domain.SamlSsoConfig;
import com.neppo.authenticatorserver.domain.User;
import com.neppo.authenticatorserver.domain.exception.DaoException;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.service.exception.AccountStatusNotValidException;
import com.neppo.authenticatorserver.service.exception.AuthenticationPolicyException;
import com.neppo.authenticatorserver.service.exception.CredentialsNotValidException;
import com.neppo.authenticatorserver.service.exception.JsonParseException;
import com.neppo.authenticatorserver.session.Session;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class LoginController {

	@Autowired
	protected AuthenticationService authenticationService;
	
	public static final String LOGIN_USERNAME = "user.email";
	public static final String LOGIN_PASSWORD = "user.password";
	public static final String THROTTLING_COUNT = "THROTTLING_COUNT";
	public static final int REMEMBER_TIME = 60 * 60 * 24 * 365;

	protected SAMLSignature signature;

	@RequestMapping("/login-form")
	public String loginForm(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		model.addAttribute("erro", "");
		return "login";
	}

	@RequestMapping("/login")
	public String login(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {
		
		setThrottlingCountAttribute();

		AuthenticationRequest authnRequest = null;
		try {

			authnRequest = createAuthenticationRequest(req, resp);

		} catch (Exception ex) {

			ex.printStackTrace();
			model.addAttribute("erro", "Erro: não foi possível criar a requisição para autenticação!");
			return "login";
		}

		AuthenticationResponse authnResponse = null;
		try {

			authnResponse = authenticationService.authenticateUser(authnRequest);

		} catch (AccountStatusNotValidException ex) {

			ex.printStackTrace();
			model.addAttribute("erro", ex.getMessage());
			return "login";

		} catch (JsonParseException ex) {

			ex.printStackTrace();
			model.addAttribute("erro", ex.getMessage());
			return "login";

		} catch (CredentialsNotValidException ex) {

			ex.printStackTrace();
			String retorno = authenticationService.validateThrottling(req, ex.getAuthnResponse());
			String errorMsg = retorno == null ? ex.getMessage() : retorno;
			model.addAttribute("erro", errorMsg);
			return "login";

		} catch (AuthenticationPolicyException ex) {

			ex.printStackTrace();
			model.addAttribute("erro", ex.getMessage());
			return "login";

		} catch (DaoException ex) {

			ex.printStackTrace();
			model.addAttribute("erro", ex.getMessage());
			return "login";

		} catch (Exception ex) {

			ex.printStackTrace();
			model.addAttribute("erro", "Ops, algum problema no processo de autenticação.");
			return "login";
		}

		return processLogin(req, resp, model, authnResponse);
	}
	
	
	private void setThrottlingCountAttribute() {
		
		Integer throttlingCount = (Integer) Session.getAttribute(THROTTLING_COUNT);
		if(throttlingCount == null) {
			Session.addAttribute(THROTTLING_COUNT, 0);
		}
	}

	
	private String processLogin(HttpServletRequest req, HttpServletResponse resp, Model model, AuthenticationResponse authnResponse) 
			throws ServletException, IOException {

		if (authnResponse.isSucess()) {

			if (authenticationService.haveMFA(authnResponse.getAuthnPolicy().getRulesList())) {

				setHaveRememberAttribute(authnResponse);
				Session.addAttribute("authnResponse", authnResponse);
				return "mfa";

			} else {

				if (authenticationService.haveRemember(authnResponse.getAuthnPolicy().getRulesList())) {
					setCookieSessionId(req, resp);
				}
				
				User user = authenticationService.createUser(authnResponse);
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



	private void setHaveRememberAttribute( AuthenticationResponse authnResponse) {
		
		if (authenticationService.haveRemember(authnResponse.getAuthnPolicy().getRulesList())) {
			Session.addAttribute(String.valueOf(REMEMBER_TIME), true);
		}
	}


	private void setCookieSessionId(HttpServletRequest req, HttpServletResponse resp) {
		
		Cookie sessionIdCookie = authenticationService.getSessionIdCookie(req.getCookies());
		if (sessionIdCookie != null) {
			sessionIdCookie.setMaxAge(REMEMBER_TIME);
			resp.addCookie(sessionIdCookie);
		}
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
