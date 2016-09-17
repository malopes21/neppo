package com.neppo.authenticatorserver.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.neppo.authenticatorserver.model.Subject;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.model.dao.AccountDAO;
import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.session.LoginSessionManager;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LoginServlet.class);

	@Autowired
	protected IdentityService identityService;
	protected Cache<String, String> cache;
	protected LoginSessionManager sessionManager;

	public static final String loginUsername = "user.email";
	public static final String loginPassword = "user.password";
	
	protected static final String loginRedirectUrl = "./login.html";
	protected SAMLSignature signature;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String username = req.getParameter(loginUsername);
		String password = req.getParameter(loginPassword);
		
		User user = getIdentityService().getUser(username);
		
		if(user != null && password.equals(user.getPassword()) ) {
			
			Subject subject = new Subject();
			subject.setPrincipal(username);
			subject.setSession(new HttpServletSession(req.getSession(), req.getRemoteHost()));
			subject.setAuthenticated(true);
			
			getIdentityService().setSubject(subject);
			
			req.getRequestDispatcher("/sso").forward(req, resp);
		
		} else {

			String redirect = loginRedirectUrl;
			resp.sendRedirect(redirect);
		}

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public IdentityService getIdentityService() {
		if (identityService==null) {
			ApplicationContext context = WebApplicationContextUtils.
					getRequiredWebApplicationContext(getServletContext()); 
			identityService = (IdentityService) context.getBean("identityService");
		}		

		return identityService;
	}
}
