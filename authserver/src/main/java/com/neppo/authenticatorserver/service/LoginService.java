package com.neppo.authenticatorserver.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//import org.apache.shiro.cache.Cache;
//import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.session.LoginSessionManager;

@Controller
public class LoginService {

	/**
	 * 
	 */
	private static final Logger log = Logger.getLogger(LoginService.class);

	@Autowired
	protected IdentityService identityService;
	//protected Cache<String, String> cache;
	protected LoginSessionManager sessionManager;

	public static final String loginUsername = "user.email";
	public static final String loginPassword = "user.password";
	
	protected static final String loginRedirectUrl = "./login.html";
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

		
		String username = req.getParameter(loginUsername);
		String password = req.getParameter(loginPassword);
		
		User user = getIdentityService().getUser(username);
		
		if(user != null && password.equals(user.getPassword()) ) {
			
			Subject subject = new Subject();
			subject.setPrincipal(username);
			subject.setSession(req.getSession());
			subject.setAuthenticated(true);
			
			getIdentityService().setSubject(subject);
			
			req.getRequestDispatcher("/sso").forward(req, resp);
			return null;
		
		} else {

			String errorMessage = "Invalid username/password! Username: " + ( username == null ? "" : "'"+username+"'");
			model.addAttribute("erro", errorMessage);
			
			return "login";
		}

	}
	

	public IdentityService getIdentityService() {
		/*if (identityService==null) {
			ApplicationContext context = WebApplicationContextUtils.
					getRequiredWebApplicationContext(getServletContext()); 
			identityService = (IdentityService) context.getBean("identityService");
		}	*/	

		return identityService;
	}
}
