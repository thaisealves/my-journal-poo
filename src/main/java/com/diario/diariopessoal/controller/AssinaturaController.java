package com.diario.diariopessoal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.repository.UsuarioRepository;
import com.diario.diariopessoal.service.UsuarioService;


@Controller
@RequestMapping("/assinatura")
public class AssinaturaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String mostrarPaginaAssinatura(Model model) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            model.addAttribute("usuario", usuario);
            model.addAttribute("nomeUsuario", usuario.getUsername());

            boolean isPremium = usuario instanceof UsuarioPremium;
            model.addAttribute("isPremium", isPremium);

            if (isPremium) {
                UsuarioPremium premium = (UsuarioPremium) usuario;
                model.addAttribute("diasRestantes", premium.obterDiasRestantes());
                model.addAttribute("assinaturaAtiva", premium.isAssinaturaAtiva());
            }

            return "assinatura";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar informações: " + e.getMessage());
            return "redirect:/diarios";
        }
    }

    /**
     * Para fins acadêmicos - Tornar usuário premium com um simples clique
     */
    @GetMapping("/mock")
    public String mockPremium(RedirectAttributes redirectAttributes) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Usar o método simplificado para converter para premium
            usuarioService.tornarPremium(username);
            
            // Limpar o contexto de segurança para forçar re-autenticação
            SecurityContextHolder.clearContext();
            
            redirectAttributes.addFlashAttribute("mensagem",
                    "Assinatura premium ativada com sucesso. Você tem agora 90 dias de acesso!");
                    
            return "redirect:/diarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao ativar premium: " + e.getMessage());
            return "redirect:/diarios";
        }
    }

    @PostMapping("/cancelar")
    public String cancelarAssinatura(RedirectAttributes redirectAttributes) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!(usuario instanceof UsuarioPremium)) {
                throw new RuntimeException("Você não possui uma assinatura premium para cancelar");
            }

            // Realizar o downgrade para usuário normal
            usuarioService.downgradeToPadrao(usuario.getId());

            // Forçar atualização da autenticação
            SecurityContextHolder.clearContext();

            redirectAttributes.addFlashAttribute("mensagem", "Assinatura premium cancelada com sucesso!");
            return "redirect:/assinatura";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar assinatura: " + e.getMessage());
            return "redirect:/assinatura";
        }
    }
}