package com.diario.diariopessoal.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.diario.diariopessoal.model.enums.PerfilUsuario;
import com.diario.diariopessoal.model.interfaces.IDiario;

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
    // encapsulamento: atributos com getters e setters, privados
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
    private String senha; // guardando hash da senha

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private PerfilUsuario perfil;

    // uso de coleção List e ArrayList
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = DiarioBase.class)
    private List<IDiario> diarios = new ArrayList<>();

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

    public String getSenha() {
        return senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    // utilização de polimorfismo na interface IDiario, diferentes implementações de
    // IDiario podem ser usadas aq
    public void adicionarDiario(IDiario diario) {
        if (diario != null) {
            diarios.add(diario);
            diario.setUsuario(this);
        }
    }

    public List<IDiario> getDiarios() {
        return diarios;
    }

    public void alterarSenha(String novaSenhaCriptografada) {
        this.senha = novaSenhaCriptografada;
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
