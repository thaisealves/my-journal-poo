package com.diario.diariopessoal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Categoria extends EntidadeBase {
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "cor")
    private String cor;

    public Categoria() {
    }

    public Categoria(String nome, String descricao, String cor) {
        this.nome = nome;
        this.descricao = descricao;
        this.cor = cor;
    }

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

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    //utilização de método estático para criar uma categoria padrão
    public static Categoria criarCategoriaPadrao() {
        return new Categoria("Padrão", "Categoria padrão", "#FFFFFF");
    }

    @Override
    public boolean validar() {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            return false;
        }

        if (cor == null || !cor.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            return false;
        }

        return true;
    }
}
