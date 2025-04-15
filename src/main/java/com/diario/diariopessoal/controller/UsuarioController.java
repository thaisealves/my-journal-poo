package com.diario.diariopessoal.controller;

import com.diario.diariopessoal.dto.UsuarioDTO;
import com.diario.diariopessoal.model.entity.DiarioBase;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.model.enums.TipoAssinatura;
import com.diario.diariopessoal.service.UsuarioService;
import com.diario.diariopessoal.repository.UsuarioRepository;
import com.diario.diariopessoal.repository.DiarioRepository;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DiarioRepository diarioRepository;

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

    /**
     * Converte o usuário atual para Premium com plano trimestral (mockado)
     */
    @GetMapping("/tornar-premium")
    public String tornarPremium(RedirectAttributes redirectAttributes) {
        try {
            // Obter usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Verificar se já é premium
            if (usuario instanceof UsuarioPremium) {
                redirectAttributes.addFlashAttribute("mensagem", "Você já é um usuário Premium!");
                return "redirect:/diarios";
            }
            
            // Criar novo usuário premium
            UsuarioPremium usuarioPremium = new UsuarioPremium(
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getSenha(),
                TipoAssinatura.TRIMESTRAL
            );
            
            // Copiar todos os atributos existentes
            usuarioPremium.setId(usuario.getId());
            usuarioPremium.setUsername(usuario.getUsername());
            usuarioPremium.setDataCriacao(usuario.getDataCriacao());
            
            // Definir data de assinatura como hoje
            usuarioPremium.setDataAssinatura(LocalDate.now());
            
            // Salvar o novo usuário premium
            usuarioRepository.delete(usuario);
            usuarioRepository.save(usuarioPremium);
            
            // Atualizar os diários para apontar para o novo usuário
            List<DiarioBase> diarios = diarioRepository.findByUsuarioId(usuarioPremium.getId());
            for (DiarioBase diario : diarios) {
                diario.setUsuario(usuarioPremium);
            }
            diarioRepository.saveAll(diarios);
            
            SecurityContextHolder.clearContext();
            
            redirectAttributes.addFlashAttribute("mensagem", 
                "Parabéns! Você agora é um usuário Premium com plano Trimestral!");
            
            return "redirect:/diarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", 
                "Erro ao converter para Premium: " + e.getMessage());
            return "redirect:/diarios";
        }
    }
    
    /**
     * Renova a assinatura Premium atual (mockado)
     */
    @GetMapping("/renovar-premium")
    public String renovarPremium(RedirectAttributes redirectAttributes) {
        try {
            // Obter usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Verificar se é premium
            if (!(usuario instanceof UsuarioPremium)) {
                redirectAttributes.addFlashAttribute("erro", 
                    "Você não é um usuário Premium!");
                return "redirect:/diarios";
            }
            
            // Renovar assinatura
            UsuarioPremium premium = (UsuarioPremium) usuario;
            premium.renovarAssinatura(premium.getPlano().getDuracao());
            usuarioRepository.save(premium);
            
            redirectAttributes.addFlashAttribute("mensagem", 
                "Assinatura Premium renovada com sucesso por mais " + 
                premium.getPlano().getDuracao() + " meses!");
            
            return "redirect:/diarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", 
                "Erro ao renovar assinatura: " + e.getMessage());
            return "redirect:/diarios";
        }
    }
    
    /**
     * Atualiza o plano do usuário Premium (mockado)
     */
    @PostMapping("/atualizar-plano")
    public String atualizarPlano(
            @RequestParam("plano") String planoStr,
            RedirectAttributes redirectAttributes) {
        try {
            // Obter usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Verificar se é premium
            if (!(usuario instanceof UsuarioPremium)) {
                redirectAttributes.addFlashAttribute("erro", 
                    "Você não é um usuário Premium!");
                return "redirect:/diarios";
            }
            
            // Converter string para enum
            TipoAssinatura novoPlano = TipoAssinatura.valueOf(planoStr);
            
            // Atualizar plano
            UsuarioPremium premium = (UsuarioPremium) usuario;
            premium.renovarAssinatura(novoPlano.getDuracao());
            usuarioRepository.save(premium);
            
            redirectAttributes.addFlashAttribute("mensagem", 
                "Plano atualizado para " + novoPlano.name() + " com sucesso!");
            
            return "redirect:/diarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", 
                "Erro ao atualizar plano: " + e.getMessage());
            return "redirect:/diarios";
        }
    }
}
