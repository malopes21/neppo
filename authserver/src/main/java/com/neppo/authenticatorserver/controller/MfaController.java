package com.neppo.authenticatorserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.domain.AuthenticationResponse;
import com.neppo.authenticatorserver.domain.User;
import com.neppo.authenticatorserver.service.AuthenticationService;
import com.neppo.authenticatorserver.service.exception.OtpInvalidTokenException;
import com.neppo.authenticatorserver.session.Session;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class MfaController {
	
	@Autowired
	protected AuthenticationService authenticationService;

	@RequestMapping("/mfa")
	public String mfaForm(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		AuthenticationResponse authnResponse = (AuthenticationResponse) Session.getAttribute("authnResponse");
		if(authnResponse == null) {
			return "login";
		}
		
		try {
		
			if(!authenticationService.validateMFA(req)) {
				
				model.addAttribute("erro", "Codigo incorreto! Tente novamente!");
				return "mfa";
			}
			
		} catch(OtpInvalidTokenException ex) {
			
			model.addAttribute("erro", ex.getMessage());
			return "mfa";
		} catch(Exception ex) {
			
			model.addAttribute("erro", "OTP não configurado para esse usuário!");
			return "mfa";
		}
		
		authenticationService.validateRemember(req, resp);
		
		User user = authenticationService.createUser(authnResponse);
		Subject.authenticate(user);
		
		req.getRequestDispatcher("/sso").forward(req, resp);
		return null;
	}
	

}
