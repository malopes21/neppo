package com.neppo.authenticatorserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.model.AuthenticationResponse;
import com.neppo.authenticatorserver.model.User;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class MfaController {

	@RequestMapping("/mfa")
	public String mfaForm(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		//validar MFA

		AuthenticationResponse authnResponse = (AuthenticationResponse) req.getSession().getAttribute("authnResponse");
		if(authnResponse == null) {
			return "login";
		}
		
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

}
