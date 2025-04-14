package com.diario.diariopessoal.model.interfaces;

import com.diario.diariopessoal.model.entity.Usuario;

public interface IDiario {
    Long getId();

    String getNome();

    String getDescricao();

    Usuario getUsuario();

    void setUsuario(Usuario usuario);

    void adicionarEntrada(String titulo, String conteudo);
}
