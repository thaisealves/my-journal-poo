package com.diario.diariopessoal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
                
                if (usuario != null) {
                    boolean isPremium = usuario instanceof UsuarioPremium && 
                            ((UsuarioPremium) usuario).isAssinaturaAtiva();
                    
                    model.addAttribute("nomeUsuario", usuario.getUsername());
                    model.addAttribute("isPremium", isPremium);
                    model.addAttribute("usuario", usuario);
                } else {
                    // Valores padrão
                    model.addAttribute("isPremium", false);
                }
            } else {
                // Valores padrão para usuários não autenticados
                model.addAttribute("isPremium", false);
            }
        } catch (Exception e) {
            // Em caso de erro, defina valores padrão
            model.addAttribute("isPremium", false);
        }
    }
}