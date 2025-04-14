package com.diario.diariopessoal.controller;

import com.diario.diariopessoal.dto.UsuarioDTO;
import com.diario.diariopessoal.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // utilizando o verbo get
    @GetMapping("/cadastro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        return "cadastro-usuario";
    }

    // utilizando o verbo post
    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute("usuarioDTO") @Valid UsuarioDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cadastro-usuario";
        }

        usuarioService.salvarUsuario(dto);
        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Usuário cadastrado com sucesso! Faça login para continuar.");
        return "redirect:/login";
    }
}
