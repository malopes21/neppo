package com.neppo.authenticatorserver.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neppo.authenticatorserver.domain.AuthenticationResponse;
import com.neppo.authenticatorserver.domain.User;
import com.neppo.authenticatorserver.session.Subject;

@Controller
public class MfaController {

	@RequestMapping("/mfa")
	public String mfaForm(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		AuthenticationResponse authnResponse = (AuthenticationResponse) req.getSession().getAttribute("authnResponse");
		if(authnResponse == null) {
			return "login";
		}
		
		//VALIDAR MFA
		try {
		
			String sCode = req.getParameter("code");
			Integer code = Integer.parseInt(sCode);
			if(!code.equals(120)) {
				model.addAttribute("erro", "Codigo incorreto! Tente novamente!");
				return "mfa";
			}
			
		}catch(Exception ex) {
			
			model.addAttribute("erro", "Valor inválido! Tente novamente!");
			return "mfa";
		}

		//MFA VALIDO E COM SESSAO - LOGANDO!
		
		Boolean remember = (Boolean) req.getSession().getAttribute(String.valueOf(LoginController.REMEMBER_TIME));
		if(remember != null && remember) {
			Cookie sessionIdCookie = LoginController.getSessionIdCookie(req.getCookies());
			if(sessionIdCookie != null) {
				sessionIdCookie.setMaxAge(LoginController.REMEMBER_TIME);
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

}
