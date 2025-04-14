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
import com.diario.diariopessoal.model.entity.DiarioFactory;
import com.diario.diariopessoal.model.entity.DiarioPremium;
import com.diario.diariopessoal.model.entity.DiarioTexto;
import com.diario.diariopessoal.model.entity.Entrada;
import com.diario.diariopessoal.model.entity.GerenciadorDiarios;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
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
    public String exibirFormularioCriarDiario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean isPremium = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PREMIUM"));

        model.addAttribute("nomeUsuario", username);
        model.addAttribute("isPremium", isPremium);

        return "diario-form";
    }

    /**
     * Processa o formulário para criar um novo diário
     */
    @PostMapping("/novo")
    public String criarNovoDiario(
            @RequestParam("nome") String nome,
            @RequestParam("descricao") String descricao,
            @RequestParam(name = "premium", defaultValue = "false") boolean isPremium,
            RedirectAttributes redirectAttributes) {

        try {
            // Obter o usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Buscar usuário no banco de dados
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Criar diário usando o Factory Pattern
            DiarioBase diario;

            if (isPremium) {
                // Verificar se o usuário é premium
                if (!(usuario instanceof UsuarioPremium)) {
                    redirectAttributes.addFlashAttribute("erro",
                            "Apenas usuários premium podem criar diários premium");
                    return "redirect:/diarios/novo";
                }

                // Criar diário premium usando o factory
                diario = DiarioFactory.criarDiarioPremium(nome, descricao, usuario);
            } else {
                // Criar diário de texto usando o factory
                diario = DiarioFactory.criarDiarioTexto(nome, descricao, usuario);
            }

            // Salvar no repositório
            diario = diarioRepository.save(diario);

            // Adicionar ao gerenciador de diários
            gerenciadorDiarios.adicionarDiario(nome, diario);

            // Adicionar ao usuário
            usuario.adicionarDiario(diario);

            // Salvar o usuário atualizado
            usuarioRepository.save(usuario);

            // Mensagem de sucesso
            redirectAttributes.addFlashAttribute("mensagem",
                    "Diário '" + nome + "' criado com sucesso!");

            return "redirect:/diarios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao criar diário: " + e.getMessage());
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