package com.neppo.authenticatorserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {

    @RequestMapping("/ops")
    public String errorPage(@RequestParam(value="erro", required=false, defaultValue="") String erro, Model model) {
        model.addAttribute("erro", erro);
        //System.out.println("\nOOOOOPPPPSSS!\n");
        return "authn_error";
    }

}
