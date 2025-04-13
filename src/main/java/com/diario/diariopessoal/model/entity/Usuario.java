package com.diario.diariopessoal.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.diario.diariopessoal.model.enums.PerfilUsuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.OneToMany;

@Entity
@Inheritance(strategy = jakarta.persistence.InheritanceType.JOINED)
public class Usuario extends EntidadeBase {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    // cascade: ir excluindo me cascata
    // usuario possui um (ou mais) diario base, relação de agregação
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<DiarioBase> diarios = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfil;

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
        }
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
