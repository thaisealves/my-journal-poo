package com.diario.diariopessoal.model.entity;

import com.diario.diariopessoal.model.enums.Humor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "entrada")
@Inheritance(strategy = InheritanceType.JOINED)
public class Entrada extends EntidadeBase {
    @NotBlank
    @Lob
    @Column(nullable = false)
    private String conteudo;

    // agregação de categoria
    @NotNull
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;

    @Enumerated(EnumType.STRING)
    @Column(name = "humor")
    private Humor humor;

    public Entrada() {
        ContadorEntradas.incrementarEntradas();
    }

    public Entrada(String conteudo, Usuario autor) {
        this.conteudo = conteudo;
        this.autor = autor;
        this.categoria = Categoria.criarCategoriaPadrao();
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

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean validar() {
        if (conteudo == null || conteudo.isEmpty()) {
            return false;
        }
        return true;
    }
}
