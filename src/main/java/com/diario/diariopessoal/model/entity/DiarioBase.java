package com.diario.diariopessoal.model.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

//uso de classe abstrata
@MappedSuperclass
public abstract class DiarioBase {
    protected String nome; // utilização de encapsulamento
    protected String descricao;

    // associação de um diário com um usuário
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
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
