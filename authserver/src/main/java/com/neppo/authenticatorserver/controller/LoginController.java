package com.neppo.authenticatorserver.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.neppo.authenticatorserver.model.dao.SamlSsoConfigDAO;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.session.LoginSessionManager;

@Controller
public class LoginController {

	@Autowired
	protected AuthenticationService authenticationService;
	protected LoginSessionManager sessionManager;

	public static final String loginUsername = "user.email";
	public static final String loginPassword = "user.password";
	
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
		
		AuthenticationRequest authnData = createAuthenticationData(req, resp);
		AuthenticationResponse authnDataResponse = null;
		try {
			
			authnDataResponse = authenticationService.authenticateUser(authnData);
			
		}catch(Exception ex) {
			
			model.addAttribute("erro", ex.getMessage());			
			return "login";
		}
		
		//mudar para tratamento de exceção
		if(authnDataResponse == null || authnDataResponse.getAccount() == null) {
			
			String username = authnData.getUsername();
			String errorMessage = "Invalid username/password! Username: " + ( username == null ? "" : "'"+username+"'");
			model.addAttribute("erro", errorMessage);
			return "login";
		}
		
		if(!authnDataResponse.isSucess()) {
			
			StringBuilder message = new StringBuilder();
			message.append("Authentication Policy Error! ");
			if(authnDataResponse.getRules() != null) {
				for(AuthenticationRule rule: authnDataResponse.getRules()) {
					if(!rule.isValidated()) {
						message.append("\n"+rule.getType());
					}
				}
			}
			String errorMessage = message.toString();
			model.addAttribute("erro", errorMessage);
			return "login";
		}
		
		if(authnDataResponse.isSucess()) {

			if(haveMFA(authnDataResponse.getRules())) {
				return "mfa";
			}
			
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
		
		SamlSsoConfig samlConfig = samlConfigDAO.findByIssuer("exampleidp");
				//(SamlSsoConfig) req.getSession().getAttribute(SamlUtils.SAML_SSO_CONFIG);  //usar o SAML_REQUEST
		
		AuthenticationRequest authnData = new AuthenticationRequest();
		authnData.setIssuer(samlConfig.getIssuer());
		authnData.setUsername(req.getParameter(loginUsername));
		authnData.setPassword(req.getParameter(loginPassword));
		authnData.setRemoteHost(req.getRemoteHost());
		authnData.setRemoteAddr(req.getRemoteAddr());
		authnData.setRemoteUser(req.getRemoteUser());
		authnData.setRequestUri(req.getRequestURI());
		authnData.setSessionId(req.getRequestedSessionId());
		authnData.setUserAgent(req.getHeader("User-Agent"));
		
		return authnData;
	}
	
}
