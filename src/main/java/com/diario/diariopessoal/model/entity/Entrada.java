package com.diario.diariopessoal.model.entity;

import com.diario.diariopessoal.model.enums.Humor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Entrada extends EntidadeBase {
    @Column(nullable = false, length = 10000)
    private String conteudo;

    // agregação de categoria
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;

    @Enumerated(EnumType.STRING)
    private Humor humor;

    public Entrada() {
        ContadorEntradas.incrementarEntradas();
    }

    public Entrada(String conteudo, Categoria categoria, Usuario autor) {
        this.conteudo = conteudo;
        this.categoria = categoria;
        this.autor = autor;
        ContadorEntradas.incrementarEntradas();
    }

    public String getConteudo() {
        return conteudo;
    }

    public Humor getHumor() {
        return humor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public void setHumor(Humor humor) {
        this.humor = humor;
    }

    @Override
    public boolean validar() {
        if (conteudo == null || conteudo.isEmpty()) {
            return false;
        }
        return true;
    }
}
