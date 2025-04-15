package com.diario.diariopessoal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diario.diariopessoal.dto.UsuarioDTO;
import com.diario.diariopessoal.model.entity.DiarioBase;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.model.enums.PerfilUsuario;
import com.diario.diariopessoal.model.enums.TipoAssinatura;
import com.diario.diariopessoal.repository.DiarioRepository;
import com.diario.diariopessoal.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DiarioRepository diarioRepository;

    @Autowired
    private EntityManager entityManager;

    public boolean verificarSenha(Usuario usuario, String senhaInformada) {
        return passwordEncoder.matches(senhaInformada, usuario.getSenha());
    }

    public boolean alterarSenha(Usuario usuario, String senhaAntiga, String novaSenha) {
        if (!verificarSenha(usuario, senhaAntiga)) {
            return false;
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            return false;
        }
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        return true;
    }

    public Usuario salvarUsuario(UsuarioDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Usuario usuario = new Usuario(dto.getUsername(), dto.getEmail(), passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(PerfilUsuario.REGULAR);

        return usuarioRepository.save(usuario);
    }

    /**
     * Transforma um usuário normal em um usuário premium ou renova um usuário
     * premium existente, tratando adequadamente problemas de concorrência.
     */
    @Transactional
    public UsuarioPremium tornarPremium(String username) {
        try {
            // Buscar usuário por username
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Se já for premium, apenas atualizar e retornar
            if (usuario instanceof UsuarioPremium) {
                UsuarioPremium premium = (UsuarioPremium) usuario;
                premium.setDataAssinatura(LocalDate.now());
                premium.setPlano(TipoAssinatura.TRIMESTRAL);
                return usuarioRepository.save(premium);
            }

            // Coletar todos os dados do usuário que serão necessários
            Long usuarioId = usuario.getId();
            String usernameOriginal = usuario.getUsername();
            String email = usuario.getEmail();
            String senha = usuario.getSenha();
            PerfilUsuario perfil = usuario.getPerfil();
            LocalDateTime dataCriacao = usuario.getDataCriacao();
            
            // Armazenar apenas os IDs dos diários
            List<Long> diarioIds = diarioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(DiarioBase::getId)
                .toList();

            // Remover o usuário antigo
            usuarioRepository.delete(usuario);
            entityManager.flush();
            
            // Criar novo usuário premium
            UsuarioPremium premium = new UsuarioPremium();
            premium.setUsername(usernameOriginal);
            premium.setEmail(email);
            premium.setSenha(senha);
            premium.setPerfil(perfil);
            premium.setDataCriacao(dataCriacao);
            premium.setDataAssinatura(LocalDate.now());
            premium.setPlano(TipoAssinatura.TRIMESTRAL);
            
            // Salvar o usuário premium
            UsuarioPremium saved = usuarioRepository.save(premium);
            entityManager.flush();

            // IMPORTANTE: Limpar o contexto de persistência 
            // para forçar reload das entidades
            entityManager.clear();
            
            // Agora atualizar cada diário individualmente com uma nova consulta
            if (!diarioIds.isEmpty()) {
                for (Long diarioId : diarioIds) {
                    // Buscar cada diário novamente do banco de dados
                    DiarioBase diario = diarioRepository.findById(diarioId)
                        .orElse(null);
                    
                    if (diario != null) {
                        // Atualizar a referência para o novo usuário premium
                        diario.setUsuario(saved);
                        diarioRepository.save(diario);
                    }
                }
                entityManager.flush();
            }
            
            return saved;
            
        } catch (Exception e) {
            System.err.println("Erro ao converter para premium: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Verifica se a assinatura premium é válida
     */
    public boolean isPremiumValid(UsuarioPremium usuarioPremium) {
        if (usuarioPremium == null || usuarioPremium.obterDiasRestantes() <= 0) {
            return false;
        }
        return usuarioPremium.isAssinaturaAtiva();
    }

    /**
     * Calcula dias restantes da assinatura
     */
    public long getDiasRestantesPremium(UsuarioPremium usuarioPremium) {
        if (usuarioPremium == null || usuarioPremium.obterDiasRestantes() <= 0) {
            return 0;
        }

        long dias = usuarioPremium.obterDiasRestantes();
        return Math.max(0, dias);
    }

    /**
     * Reverte um usuário premium para usuário normal quando a assinatura expira
     */
    @Transactional
    public Usuario downgradeToPadrao(Long usuarioPremiumId) {
        UsuarioPremium usuarioPremium = (UsuarioPremium) usuarioRepository.findById(usuarioPremiumId)
                .orElseThrow(() -> new RuntimeException("Usuário Premium não encontrado"));

        // Criar novo usuário padrão
        Usuario usuario = new Usuario();

        // Copiar atributos
        usuario.setId(usuarioPremium.getId());
        usuario.setUsername(usuarioPremium.getUsername());
        usuario.setSenha(usuarioPremium.getSenha());
        usuario.setEmail(usuarioPremium.getEmail());
        usuario.setDataCriacao(usuarioPremium.getDataCriacao());

        // Salvar o novo usuário normal
        usuarioRepository.delete(usuarioPremium);
        Usuario saved = usuarioRepository.save(usuario);

        // Atualizar referências nos diários
        List<DiarioBase> diarios = diarioRepository.findByUsuarioId(usuarioPremium.getId());
        for (DiarioBase diario : diarios) {
            diario.setUsuario(saved);
        }
        diarioRepository.saveAll(diarios);

        return saved;
    }
}
