package com.diario.diariopessoal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.diario.diariopessoal.dto.UsuarioDTO;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.enums.PerfilUsuario;
import com.diario.diariopessoal.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

}
