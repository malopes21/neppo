package com.neppo.authenticatorserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.shiro.cache.Cache;
//import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.saml.util.SAMLSignature;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.session.LoginSessionManager;

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


	@RequestMapping("/logout")
	public String login(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		return "ok";
	}
	
}
