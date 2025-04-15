package com.diario.diariopessoal.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.diario.diariopessoal.model.entity.ContadorEntradas;
import com.diario.diariopessoal.model.entity.DiarioBase;
import com.diario.diariopessoal.model.entity.DiarioPremium;
import com.diario.diariopessoal.model.entity.DiarioTexto;
import com.diario.diariopessoal.model.entity.Entrada;
import com.diario.diariopessoal.model.entity.GerenciadorDiarios;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.dto.DiarioCriacaoDTO;
import com.diario.diariopessoal.repository.DiarioRepository;
import com.diario.diariopessoal.repository.UsuarioRepository;

@Controller
@RequestMapping("/diarios")
public class DiarioController {

    @Autowired
    private DiarioRepository diarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Usando GerenciadorDiarios como componente do controller
    private GerenciadorDiarios<DiarioBase> gerenciadorDiarios = new GerenciadorDiarios<>();

    @GetMapping
    public String listarDiarios(Model model) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Buscar diários do usuário
            List<DiarioBase> diarios = diarioRepository.findByUsuarioId(usuario.getId());
            
            // Adicionar contador de entradas ao modelo
            model.addAttribute("totalEntradas", ContadorEntradas.getTotalEntradas());
            model.addAttribute("diarios", diarios);
            model.addAttribute("nomeUsuario", usuario.getUsername());
            
            return "diarios";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao listar diários: " + e.getMessage());
            return "redirect:/";
        }
    }

    /**
     * Exibe o formulário para criar um novo diário
     */
    @GetMapping("/novo")
    public String formularioCriarDiario(Model model) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            model.addAttribute("diarioDTO", new DiarioCriacaoDTO());
            model.addAttribute("nomeUsuario", usuario.getUsername());
            
            // Verificar se é usuário premium e possui assinatura válida
            boolean isPremium = false;
            if (usuario instanceof UsuarioPremium) {
                UsuarioPremium premium = (UsuarioPremium) usuario;
                isPremium = premium.isAssinaturaAtiva();
            }
            model.addAttribute("isPremium", isPremium);

            return "diario-form";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar formulário: " + e.getMessage());
            return "redirect:/diarios";
        }
    }

    /**
     * Processa o formulário para criar um novo diário
     */
    @PostMapping("/novo")
    public String processarCriacaoDiario(@ModelAttribute DiarioCriacaoDTO diarioDTO, 
                                         RedirectAttributes redirectAttributes) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Verificar se é diário premium
            if ("premium".equals(diarioDTO.getTipo())) {
                // Verificar se usuário é premium
                if (!(usuario instanceof UsuarioPremium)) {
                    redirectAttributes.addFlashAttribute("erro", 
                        "É necessário ser um usuário Premium para criar diários enriquecidos");
                    return "redirect:/assinatura/mock";
                }
                
                // Verificar se assinatura está ativa
                UsuarioPremium premium = (UsuarioPremium) usuario;
                if (!premium.isAssinaturaAtiva()) {
                    redirectAttributes.addFlashAttribute("erro", 
                        "Sua assinatura Premium expirou. Por favor, renove para criar diários premium");
                    return "redirect:/assinatura/mock";
                }
                
                // Criar diário premium
                DiarioPremium diario = new DiarioPremium();
                diario.setNome(diarioDTO.getNome());
                diario.setDescricao(diarioDTO.getDescricao());
                diario.setUsuario(premium);
                diarioRepository.save(diario);
            } else {
                // Criar diário normal
                DiarioTexto diario = new DiarioTexto();
                diario.setNome(diarioDTO.getNome());
                diario.setDescricao(diarioDTO.getDescricao());
                diario.setUsuario(usuario);
                diarioRepository.save(diario);
            }
            
            redirectAttributes.addFlashAttribute("mensagem", "Diário criado com sucesso!");
            return "redirect:/diarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar diário: " + e.getMessage());
            return "redirect:/diarios/novo";
        }
    }

    @GetMapping("/{id}/excluir")
    public String excluirDiario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Obter o usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Buscar usuário no banco de dados
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Buscar o diário
            DiarioBase diario = diarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para excluir este diário");
            }

            // Remover do gerenciador de diários (se necessário)
            try {
                gerenciadorDiarios.removerDiario(diario.getNome());
            } catch (Exception e) {
                // Ignorar erro se o diário não estiver no gerenciador
            }

            // Excluir do repositório
            diarioRepository.delete(diario);

            redirectAttributes.addFlashAttribute("mensagem", "Diário excluído com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir diário: " + e.getMessage());
        }

        return "redirect:/diarios";
    }

    @GetMapping("/{id}")
    public String visualizarDiario(@PathVariable Long id, Model model) {
        try {
            // Obter o usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Buscar o diário com entradas
            DiarioBase diario = diarioRepository.findByIdWithEntradas(id)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Lista para armazenar as entradas
            List<Entrada> entradas;

            // Buscar entradas com base no tipo de diário
            if (diario instanceof DiarioTexto) {
                DiarioTexto diarioTexto = (DiarioTexto) diario;
                entradas = diarioTexto.getEntradas().stream()
                        .sorted((e1, e2) -> e2.getDataCriacao().compareTo(e1.getDataCriacao()))
                        .toList();
            } else if (diario instanceof DiarioPremium) {
                DiarioPremium diarioPremium = (DiarioPremium) diario;
                entradas = new ArrayList<>(diarioPremium.getEntradasEnriquecidas().stream()
                        .sorted((e1, e2) -> e2.getDataCriacao().compareTo(e1.getDataCriacao()))
                        .toList());
            } else {
                entradas = new ArrayList<>();
            }

            model.addAttribute("diario", diario);
            model.addAttribute("entradas", entradas);
            model.addAttribute("isPremium", diario instanceof DiarioPremium);
            model.addAttribute("nomeUsuario", usuario.getUsername());

            return "visualizar-diario";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao visualizar diário: " + e.getMessage());
            return "redirect:/diarios";
        }
    }
}