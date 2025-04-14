package com.diario.diariopessoal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.diario.diariopessoal.model.entity.*;
import com.diario.diariopessoal.model.enums.Humor;
import com.diario.diariopessoal.model.enums.TipoConteudo;
import com.diario.diariopessoal.repository.CategoriaRepository;
import com.diario.diariopessoal.repository.DiarioRepository;
import com.diario.diariopessoal.repository.EntradaRepository;
import com.diario.diariopessoal.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/diarios/{diarioId}/entradas")
public class EntradaController {

    @Autowired
    private DiarioRepository diarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @SuppressWarnings("unused")
    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Listar todas as entradas de um diário
     */
    @GetMapping
    public String listarEntradas(@PathVariable Long diarioId, Model model) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Lista para armazenar as entradas
            List<Entrada> entradas;

            // Verificar o tipo de diário e chamar o método apropriado
            if (diario instanceof DiarioTexto) {
                entradas = ((DiarioTexto) diario).listarEntradas().stream()
                        .sorted((e1, e2) -> e2.getDataCriacao().compareTo(e1.getDataCriacao()))
                        .toList();
            } else if (diario instanceof DiarioPremium) {
                entradas = ((DiarioPremium) diario).listarEntradas().stream()
                        .sorted((e1, e2) -> e2.getDataCriacao().compareTo(e1.getDataCriacao()))
                        .toList();
            } else {
                // Caso seja um tipo de diário não esperado
                entradas = new ArrayList<>();
            }

            model.addAttribute("diario", diario);
            model.addAttribute("entradas", entradas);
            model.addAttribute("nomeUsuario", usuario.getUsername());
            model.addAttribute("isPremium", diario instanceof DiarioPremium);

            return "entradas";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao listar entradas: " + e.getMessage());
            return "redirect:/diarios";
        }
    }

    /**
     * Mostrar formulário para criar nova entrada
     */
    @GetMapping("/nova")
    public String formularioNovaEntrada(@PathVariable Long diarioId, Model model) {
        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Buscar todas as categorias existentes
            List<Categoria> todasCategorias = categoriaRepository.findAll();

            model.addAttribute("diario", diario);
            model.addAttribute("nomeUsuario", usuario.getUsername());
            model.addAttribute("isPremium", diario instanceof DiarioPremium);
            model.addAttribute("humores", Humor.values());
            model.addAttribute("tiposConteudo", TipoConteudo.values());
            model.addAttribute("categorias", todasCategorias);
            model.addAttribute("isEdicao", false);

            // Para o título da página
            model.addAttribute("titulo", "Nova Entrada");

            return "entrada-form";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao criar entrada: " + e.getMessage());
            return "redirect:/diarios/" + diarioId + "/entradas";
        }
    }

    /**
     * Processar o formulário de criação de entrada
     */
    @PostMapping("/nova")
    public String criarEntrada(
            @PathVariable Long diarioId,
            @RequestParam("conteudo") String conteudo,
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "categoria", required = false) String categoriaNome,
            @RequestParam(name = "descricaoCategoria", required = false) String descricaoCategoria,
            @RequestParam(name = "corCategoria", required = false, defaultValue = "#3498db") String corCategoria,
            @RequestParam(name = "humor", required = false) Humor humor,
            @RequestParam(name = "tipoConteudo", required = false) TipoConteudo tipoConteudo,
            @RequestParam(name = "urlConteudo", required = false) String urlConteudo,
            RedirectAttributes redirectAttributes) {

        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Buscar ou criar categoria
            Categoria categoria;
            if (categoriaNome != null && !categoriaNome.trim().isEmpty()) {
                // Buscar a categoria pelo nome
                Optional<Categoria> categoriaExistente = categoriaRepository.findByNome(categoriaNome.trim());

                if (categoriaExistente.isPresent()) {
                    // Se a categoria já existir, use-a
                    categoria = categoriaExistente.get();
                } else {
                    // Se não existir, crie uma nova com os parâmetros fornecidos
                    String descricao = (descricaoCategoria != null && !descricaoCategoria.trim().isEmpty())
                            ? descricaoCategoria
                            : "Categoria: " + categoriaNome;

                    categoria = new Categoria(categoriaNome.trim(), descricao, corCategoria);
                    categoria = categoriaRepository.save(categoria); // Persistir a categoria
                }
            } else {
                // Se nenhum nome de categoria foi fornecido, use ou crie a categoria padrão
                categoria = categoriaRepository.findByNome("Geral")
                        .orElseGet(() -> {
                            Categoria padrao = Categoria.criarCategoriaPadrao();
                            return categoriaRepository.save(padrao);
                        });
            }

            // Adicionar entrada ao diário com base no tipo
            if (diario instanceof DiarioTexto) {
                DiarioTexto diarioTexto = (DiarioTexto) diario;
                String conteudoCompleto = titulo != null && !titulo.trim().isEmpty()
                        ? titulo + "\n\n" + conteudo
                        : conteudo;

                if (humor != null) {
                    diarioTexto.adicionarEntrada(titulo, conteudoCompleto, categoria, humor);
                } else {
                    diarioTexto.adicionarEntrada(titulo, conteudoCompleto, categoria);
                }
            } else if (diario instanceof DiarioPremium) {
                DiarioPremium diarioPremium = (DiarioPremium) diario;
                String conteudoCompleto = titulo != null && !titulo.trim().isEmpty()
                        ? titulo + "\n\n" + conteudo
                        : conteudo;

                if (tipoConteudo != null && !TipoConteudo.TEXTO.equals(tipoConteudo)) {
                    // É uma entrada enriquecida
                    if (urlConteudo == null || urlConteudo.trim().isEmpty()) {
                        throw new RuntimeException("URL do conteúdo é obrigatória para entradas multimídia");
                    }

                    // Adicionar entrada enriquecida
                    diarioPremium.adicionarEntrada(titulo, conteudoCompleto, categoria, tipoConteudo, urlConteudo);
                } else {
                    diarioPremium.adicionarEntrada(titulo, conteudoCompleto);
                }
            } else {
                throw new RuntimeException("Tipo de diário não suportado");
            }

            // Incrementar o contador geral
            ContadorEntradas.incrementarEntradas();

            // Salvar o diário atualizado
            diarioRepository.save(diario);

            redirectAttributes.addFlashAttribute("mensagem", "Entrada criada com sucesso!");
            return "redirect:/diarios/" + diarioId + "/entradas";

        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar entrada: " + e.getMessage());
            return "redirect:/diarios/" + diarioId + "/entradas/nova";
        }
    }

    /**
     * Visualizar uma entrada específica
     */
    @GetMapping("/{entradaId}")
    public String visualizarEntrada(
            @PathVariable Long diarioId,
            @PathVariable Long entradaId,
            Model model) {

        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Buscar entrada com base no tipo de diário
            Entrada entrada = null;

            if (diario instanceof DiarioTexto) {
                entrada = ((DiarioTexto) diario).buscarEntradaPorId(entradaId);
            } else if (diario instanceof DiarioPremium) {
                entrada = ((DiarioPremium) diario).buscarEntradaPorId(entradaId);
            }

            if (entrada == null) {
                throw new RuntimeException("Entrada não encontrada");
            }

            // Determinar se a entrada é enriquecida (para exibição adequada no template)
            boolean isEnriquecida = entrada instanceof EntradaEnriquecida;

            model.addAttribute("diario", diario);
            model.addAttribute("entrada", entrada);
            model.addAttribute("nomeUsuario", usuario.getUsername());
            model.addAttribute("isPremium", diario instanceof DiarioPremium);
            model.addAttribute("isEnriquecida", isEnriquecida);

            // Para entrada enriquecida, adicionar atributos específicos
            if (isEnriquecida) {
                EntradaEnriquecida entradaEnriquecida = (EntradaEnriquecida) entrada;
                model.addAttribute("tipoConteudo", entradaEnriquecida.getTipoConteudo());
                model.addAttribute("urlConteudo", entradaEnriquecida.getUrlConteudo());
            }

            return "visualizar-entrada";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao visualizar entrada: " + e.getMessage());
            return "redirect:/diarios/" + diarioId + "/entradas";
        }
    }

    /**
     * Excluir uma entrada
     */
    @GetMapping("/{entradaId}/excluir")
    public String excluirEntrada(
            @PathVariable Long diarioId,
            @PathVariable Long entradaId,
            RedirectAttributes redirectAttributes) {

        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Excluir entrada com base no tipo de diário
            if (diario instanceof DiarioTexto) {
                ((DiarioTexto) diario).apagarEntrada(entradaId);
            } else if (diario instanceof DiarioPremium) {
                ((DiarioPremium) diario).apagarEntrada(entradaId);
            } else {
                throw new RuntimeException("Tipo de diário não suportado");
            }
            // Decrementar o contador geral
            ContadorEntradas.decrementarContador();

            // Salvar o diário atualizado
            diarioRepository.save(diario);

            redirectAttributes.addFlashAttribute("mensagem", "Entrada excluída com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir entrada: " + e.getMessage());
        }

        return "redirect:/diarios/" + diarioId + "/entradas";
    }

    /**
     * Mostrar formulário para editar entrada existente
     */
    @GetMapping("/{entradaId}/editar")
    public String formularioEditarEntrada(
            @PathVariable Long diarioId,
            @PathVariable Long entradaId,
            Model model) {

        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Buscar entrada
            Entrada entrada = null;

            if (diario instanceof DiarioTexto) {
                entrada = ((DiarioTexto) diario).buscarEntradaPorId(entradaId);
            } else if (diario instanceof DiarioPremium) {
                entrada = ((DiarioPremium) diario).buscarEntradaPorId(entradaId);
            }

            if (entrada == null) {
                throw new RuntimeException("Entrada não encontrada");
            }

            // Verificar se é entrada enriquecida
            boolean isEnriquecida = entrada instanceof EntradaEnriquecida;

            // Buscar todas as categorias existentes
            List<Categoria> todasCategorias = categoriaRepository.findAll();

            model.addAttribute("diario", diario);
            model.addAttribute("entrada", entrada);
            model.addAttribute("categorias", todasCategorias);
            model.addAttribute("humores", Humor.values());
            model.addAttribute("tiposConteudo", TipoConteudo.values());
            model.addAttribute("nomeUsuario", usuario.getUsername());
            model.addAttribute("isPremium", diario instanceof DiarioPremium);
            model.addAttribute("isEnriquecida", isEnriquecida);
            model.addAttribute("titulo", "Editar Entrada");
            model.addAttribute("isEdicao", true);

            // Para entrada enriquecida, adicionar atributos específicos
            if (isEnriquecida) {
                EntradaEnriquecida entradaEnriquecida = (EntradaEnriquecida) entrada;
                model.addAttribute("tipoConteudoSelecionado", entradaEnriquecida.getTipoConteudo());
                model.addAttribute("urlConteudo", entradaEnriquecida.getUrlConteudo());
            }

            return "entrada-form"; // Reutilizar o mesmo formulário usado para criar

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao editar entrada: " + e.getMessage());
            return "redirect:/diarios/" + diarioId + "/entradas";
        }
    }

    /**
     * Processar o formulário de edição de entrada
     */
    @PostMapping("/{entradaId}/editar")
    public String editarEntrada(
            @PathVariable Long diarioId,
            @PathVariable Long entradaId,
            @RequestParam("conteudo") String conteudo,
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "categoria", required = false) String categoriaNome,
            @RequestParam(name = "descricaoCategoria", required = false) String descricaoCategoria,
            @RequestParam(name = "corCategoria", required = false, defaultValue = "#3498db") String corCategoria,
            @RequestParam(name = "humor", required = false) Humor humor,
            @RequestParam(name = "tipoConteudo", required = false) TipoConteudo tipoConteudo,
            @RequestParam(name = "urlConteudo", required = false) String urlConteudo,
            RedirectAttributes redirectAttributes) {

        try {
            // Obter usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o diário
            DiarioBase diario = diarioRepository.findById(diarioId)
                    .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

            // Verificar se o diário pertence ao usuário
            if (!diario.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para acessar este diário");
            }

            // Buscar entrada a ser editada
            Entrada entrada = null;

            if (diario instanceof DiarioTexto) {
                entrada = ((DiarioTexto) diario).buscarEntradaPorId(entradaId);
            } else if (diario instanceof DiarioPremium) {
                entrada = ((DiarioPremium) diario).buscarEntradaPorId(entradaId);
            }

            if (entrada == null) {
                throw new RuntimeException("Entrada não encontrada");
            }

            // Buscar ou criar categoria
            Categoria categoria;
            if (categoriaNome != null && !categoriaNome.trim().isEmpty()) {
                // Buscar a categoria pelo nome
                Optional<Categoria> categoriaExistente = categoriaRepository.findByNome(categoriaNome.trim());

                if (categoriaExistente.isPresent()) {
                    // Categoria existe - IMPORTANTE: Atualizar a cor se for diferente
                    categoria = categoriaExistente.get();

                    // Verificar se a cor é diferente e atualizar se necessário
                    if (corCategoria != null && !corCategoria.equals(categoria.getCor())) {
                        System.out.println("Atualizando cor da categoria: " + categoria.getNome() +
                                " de " + categoria.getCor() + " para " + corCategoria);
                        categoria.setCor(corCategoria);
                        categoria = categoriaRepository.save(categoria); // Salvar a atualização
                    }
                } else {
                    // Se não existir, crie uma nova com os parâmetros fornecidos
                    String descricao = (descricaoCategoria != null && !descricaoCategoria.trim().isEmpty())
                            ? descricaoCategoria
                            : "Categoria: " + categoriaNome;

                    categoria = new Categoria(categoriaNome.trim(), descricao, corCategoria);
                    categoria = categoriaRepository.save(categoria); // Persistir a categoria
                }
            } else {
                // Se nenhum nome de categoria foi fornecido, use ou crie a categoria padrão
                categoria = categoriaRepository.findByNome("Geral")
                        .orElseGet(() -> {
                            Categoria padrao = Categoria.criarCategoriaPadrao();
                            return categoriaRepository.save(padrao);
                        });
            }

            // Atualizar os dados básicos da entrada
            entrada.setTitulo(titulo);
            entrada.setConteudo(conteudo);
            entrada.setCategoria(categoria);
            entrada.setHumor(humor);

            // Se for entrada enriquecida, atualizar campos específicos
            if (entrada instanceof EntradaEnriquecida && tipoConteudo != null) {
                EntradaEnriquecida entradaEnriquecida = (EntradaEnriquecida) entrada;

                // Para conteúdo não-texto, a URL é obrigatória
                if (!TipoConteudo.TEXTO.equals(tipoConteudo) && (urlConteudo == null || urlConteudo.trim().isEmpty())) {
                    throw new RuntimeException("URL do conteúdo é obrigatória para entradas multimídia");
                }

                entradaEnriquecida.setTipoConteudo(tipoConteudo);
                entradaEnriquecida.setUrlConteudo(urlConteudo);
            }

            // Salvar as alterações
            diarioRepository.save(diario);

            redirectAttributes.addFlashAttribute("mensagem", "Entrada atualizada com sucesso!");
            return "redirect:/diarios/" + diarioId + "/entradas/" + entradaId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao editar entrada: " + e.getMessage());
            return "redirect:/diarios/" + diarioId + "/entradas/" + entradaId + "/editar";
        }
    }
}