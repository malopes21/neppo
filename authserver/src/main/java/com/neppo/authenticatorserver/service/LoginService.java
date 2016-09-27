package com.neppo.authenticatorserver.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.shiro.cache.Cache;
//import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.model.Account;
import com.neppo.authenticatorserver.model.AuthenticationData;
import com.neppo.authenticatorserver.model.SamlSsoConfig;
import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.saml.util.SamlUtils;
import com.neppo.authenticatorserver.session.LoginSessionManager;

@Controller
public class LoginService {

	@Autowired
	protected IdentityService identityService;
	protected LoginSessionManager sessionManager;

	public static final String loginUsername = "user.email";
	public static final String loginPassword = "user.password";
	
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
		
		AuthenticationData authnData = createAuthenticationData(req, resp);
		Account account = null;
		try {
			
			account = identityService.getAccount(authnData);
			
		}catch(Exception ex) {
			
			model.addAttribute("erro", ex.getMessage());			
			return "login";
		}
		
		if(account != null) {
			
			Subject subject = new Subject();					//TODO: fix it!
			subject.setPrincipal(authnData.getUsername());
			subject.setSession(req.getSession());
			subject.setAuthenticated(true);
			
			identityService.setSubject(subject);
			
			req.getRequestDispatcher("/sso").forward(req, resp);
			return null;
		
		} else {

			String username = authnData.getUsername();
			String errorMessage = "Invalid username/password! Username: " + ( username == null ? "" : "'"+username+"'");
			model.addAttribute("erro", errorMessage);
			return "login";
		}

	}
	
	private AuthenticationData createAuthenticationData(HttpServletRequest req, HttpServletResponse resp) {
		
		SamlSsoConfig samlConfig = (SamlSsoConfig) req.getSession().getAttribute(SamlUtils.SAML_SSO_CONFIG);
		
		AuthenticationData authnData = new AuthenticationData();
		authnData.setIssuer(samlConfig.getIssuer());
		authnData.setUsername(req.getParameter(loginUsername));
		authnData.setPassword(req.getParameter(loginPassword));
		authnData.setRemoteHost(req.getRemoteHost());
		authnData.setRemoteAddr(req.getRemoteAddr());
		authnData.setRemoteUser(req.getRemoteUser());
		authnData.setRequestUri(req.getRequestURI());
		authnData.setSessionId(req.getRequestedSessionId());
		authnData.setUserAgent(req.getHeader("HTTP_USER_AGENT"));
		
		return authnData;
	}
	
}
