package com.diario.diariopessoal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarTelaLogin(Model model) {
        model.addAttribute("titulo", "Login - Diário Pessoal");
        return "login";
    }
}
