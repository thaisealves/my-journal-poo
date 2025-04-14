package com.diario.diariopessoal.model.entity;

import com.diario.diariopessoal.model.interfaces.IDiario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//usando classe abstrata
@Entity
@Table(name = "diario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DiarioBase implements IDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    protected String nome;

    @Size(max = 500)
    @Column(length = 500)
    protected String descricao;

    // Associação com Usuario
    // o diario é de um usuario, mas o usuario pode ter varios diarios
    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    protected Usuario usuario;

    // uso de metodo abstrato
    public abstract void adicionarEntrada(String conteudo);

    // construtores
    public DiarioBase() {
    }

    public DiarioBase(String nome, String descricao, Usuario usuario) {
        this.nome = nome;
        this.descricao = descricao;
        this.usuario = usuario;
    }

    // getter e setter
    @Override
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }
}
