package com.diario.diariopessoal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String paginaInicial(Model model) {
        model.addAttribute("titulo", "Diário Pessoal - Bem-vindo");
        return "home";
    }
}