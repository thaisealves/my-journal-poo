package com.diario.diariopessoal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/diarios")
public class DiarioController {

    @GetMapping
    public String listarDiarios(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("nomeUsuario", auth.getName());
        model.addAttribute("titulo", "Meus Diários");
        return "diarios";
    }
}