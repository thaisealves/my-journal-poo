package com.diario.diariopessoal.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.diario.diariopessoal.model.enums.PerfilUsuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario extends EntidadeBase {
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String senha;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private PerfilUsuario perfil;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiarioBase> diarios = new ArrayList<>();

    // Construtor
    // sobrecarga de construtores, um com parâmetros e outro vazio, polimorfismo
    public Usuario(String username, String email, String senha) {
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.perfil = PerfilUsuario.REGULAR;
    }

    public Usuario() {
    }


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }
    
    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
    public void adicionarDiarios(DiarioBase diario) {
        if (diario != null) {
            diarios.add(diario);
            diario.setUsuario(this);
        }
    }

    public List<DiarioBase> getDiarios() {
        return diarios;
    }
    public boolean verificarSenha(String senhaInformada) {
        // verificando a validade da senha comparando com a senha informada
        return senha.equals(senhaInformada);
    }

    public boolean alterarSenha(String senhaAntiga, String novaSenha) {
        if (!verificarSenha(senhaAntiga)) {
            return false;
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            return false;
        }
        this.senha = novaSenha;
        return true;
    }

    // utilização de sobrescrita
    @Override
    public boolean validar() {
        if (username == null || username.isEmpty()) {
            return false;
        }

        // usando regex pro email
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return false;
        }

        // Validação da senha (deve ter pelo menos 6 caracteres)
        if (senha == null || senha.length() < 6) {
            return false;
        }

        return true;
    }
}
