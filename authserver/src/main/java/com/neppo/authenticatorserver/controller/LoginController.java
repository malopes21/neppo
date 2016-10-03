package com.neppo.authenticatorserver.controller;

import java.util.List;

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

import com.neppo.authenticatorserver.model.AuthenticationRequest;
import com.neppo.authenticatorserver.model.AuthenticationResponse;
import com.neppo.authenticatorserver.model.AuthenticationRule;
import com.neppo.authenticatorserver.model.AuthenticationRuleType;
import com.neppo.authenticatorserver.model.SamlSsoConfig;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.model.dao.SamlSsoConfigDAO;
import com.neppo.authenticatorserver.model.exception.DaoException;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.service.exception.AccountNotFoundException;
import com.neppo.authenticatorserver.service.exception.AuthenticationPolicyException;
import com.neppo.authenticatorserver.session.LoginSessionManager;
import com.neppo.authenticatorserver.session.Session;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class LoginController {

	@Autowired
	protected AuthenticationService authenticationService;
	protected LoginSessionManager sessionManager;

	public static final String LOGIN_USERNAME = "user.email";
	public static final String LOGIN_PASSWORD = "user.password";
	
	@Autowired
	private SamlSsoConfigDAO samlConfigDAO; 
	
	protected SAMLSignature signature;

	
	@RequestMapping("/login-form")
    public String loginForm(HttpServletRequest req, HttpServletResponse resp,
    		@RequestParam(value="erro", required=false, defaultValue="") String erro, 
    		Model model) throws Exception {
		
		model.addAttribute("erro", "");
		return "login";
	}
	
	
	@RequestMapping("/login")
    public String login(HttpServletRequest req, HttpServletResponse resp,
    		@RequestParam(value="erro", required=false, defaultValue="") String erro, 
    		Model model) throws Exception {
		
		AuthenticationRequest authnRequest = createAuthenticationData(req, resp);
		AuthenticationResponse authnResponse = null;
		try {
			
			authnResponse = authenticationService.authenticateUser(authnRequest);
			
			if(authnResponse.isSucess() && haveMFA(authnResponse.getRules())) {
				req.getSession().setAttribute("authnResponse", authnResponse);
				return "mfa";
			}
			
		} catch(AuthenticationPolicyException ex) {
			
			model.addAttribute("erro", ex.getMessage());			
			return "login";
		
		} catch(AccountNotFoundException ex) {
			
			model.addAttribute("erro", ex.getMessage());			
			return "login";
		
		} catch(DaoException ex) {
			
			model.addAttribute("erro", ex.getMessage());			
			return "login";
		
		} catch(Exception ex) {
			
			model.addAttribute("erro", ex.getMessage());			
			return "login";
		}

		if(authnResponse.isSucess() && ! haveMFA(authnResponse.getRules())) {

			User user = new User();
			user.setUsername(authnResponse.getAccount().getUsername());
			user.setEmail(authnResponse.getAccount().getDescription());
			user.setFirstName(authnResponse.getAccount().getName());
			user.setName(authnResponse.getAccount().getName());
			user.setSureName(authnResponse.getAccount().getName());
			Subject.authenticate(user);
			
			req.getRequestDispatcher("/sso").forward(req, resp);
			return null;
		} 
		
		return null;

	}


	private boolean haveMFA(List<AuthenticationRule> rules) {

		for(AuthenticationRule rule: rules) {
			if(rule.getType() == AuthenticationRuleType.CHECK_MFA) {
				return true;
			}
		}
		return false;
	}


	private AuthenticationRequest createAuthenticationData(HttpServletRequest req, HttpServletResponse resp) {
		
		AuthnRequest authnRequest = (AuthnRequest) Session.getAttribute(SamlUtils.REQUEST);
		SamlSsoConfig samlConfig = samlConfigDAO.findByIssuer(authnRequest.getIssuer().getValue());
				
		AuthenticationRequest authnData = new AuthenticationRequest();
		authnData.setIssuer(samlConfig.getIssuer());
		authnData.setUsername(req.getParameter(LOGIN_USERNAME));
		authnData.setPassword(req.getParameter(LOGIN_PASSWORD));
		authnData.setRemoteHost(req.getRemoteHost());
		authnData.setRemoteAddr(req.getRemoteAddr());
		authnData.setRemoteUser(req.getRemoteUser());
		authnData.setRequestUri(req.getRequestURI());
		authnData.setSessionId(req.getRequestedSessionId());
		authnData.setUserAgent(req.getHeader("User-Agent"));
		
		return authnData;
	}
	
}
