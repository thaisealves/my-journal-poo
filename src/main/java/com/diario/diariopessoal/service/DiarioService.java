package com.diario.diariopessoal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diario.diariopessoal.model.entity.DiarioBase;
import com.diario.diariopessoal.model.entity.DiarioFactory;
import com.diario.diariopessoal.model.entity.GerenciadorDiarios;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.repository.DiarioRepository;
import com.diario.diariopessoal.exception.AcessoNegadoException;
import com.diario.diariopessoal.exception.RecursoNaoEncontradoException;

import java.util.List;

@Service
public class DiarioService {

    @Autowired
    private DiarioRepository diarioRepository;
    
    // Cria e mantém um gerenciador de diários por sessão
    private GerenciadorDiarios<DiarioBase> gerenciador = new GerenciadorDiarios<>();
    
    /**
     * Lista todos os diários do usuário
     */
    public List<DiarioBase> listarDiariosPorUsuario(Usuario usuario) {
        return diarioRepository.findByUsuarioId(usuario.getId());
    }
    
    /**
     * Cria um novo diário usando o DiarioFactory e o GerenciadorDiarios
     */
    @Transactional
    public DiarioBase criarDiario(String nome, String descricao, boolean premium, Usuario usuario) {
        DiarioBase diario;
        
        try {
            // Usa o factory pattern para criar o diário apropriado
            if (premium) {
                if (!(usuario instanceof UsuarioPremium)) {
                    throw new AcessoNegadoException("Apenas usuários premium podem criar diários premium");
                }
                diario = DiarioFactory.criarDiarioPremium(nome, descricao, usuario);
            } else {
                diario = DiarioFactory.criarDiarioTexto(nome, descricao, usuario);
            }
            
            // Salva no repositório
            diario = diarioRepository.save(diario);
            
            // Adiciona ao gerenciador para operações em memória
            gerenciador.adicionarDiario(nome, diario);
            
            // Adiciona o diário ao usuário
            usuario.adicionarDiario(diario);
            
            return diario;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro ao criar diário: " + e.getMessage());
        }
    }
    
    /**
     * Busca um diário por ID, verificando se pertence ao usuário
     */
    public DiarioBase buscarDiarioPorId(Long id, Usuario usuario) {
        DiarioBase diario = diarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Diário não encontrado"));
        
        // Verifica se o diário pertence ao usuário
        if (!diario.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este diário");
        }
        
        return diario;
    }
    
    /**
     * Exclui um diário
     */
    @Transactional
    public void excluirDiario(Long id, Usuario usuario) {
        DiarioBase diario = buscarDiarioPorId(id, usuario);
        
        // Remove do gerenciador
        try {
            gerenciador.removerDiario(diario.getNome());
        } catch (IllegalArgumentException e) {
            // Ignora se não estiver no gerenciador
        }
        
        // Remove do repositório
        diarioRepository.delete(diario);
    }
}