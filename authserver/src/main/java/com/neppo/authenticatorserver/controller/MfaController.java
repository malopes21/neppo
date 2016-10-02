package com.neppo.authenticatorserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MfaController {

	@RequestMapping("/mfa")
	public String mfaForm(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "erro", required = false, defaultValue = "") String erro, Model model)
			throws Exception {

		req.getRequestDispatcher("/sso").forward(req, resp);
		return null;
	}

}
