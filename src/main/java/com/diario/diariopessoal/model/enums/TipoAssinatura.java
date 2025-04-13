package com.diario.diariopessoal.model.enums;

public enum TipoAssinatura {
    MENSAL(1),
    TRIMESTRAL(3),
    ANUAL(12);

    private final int duracao;

    TipoAssinatura(int duracao) {
        this.duracao = duracao;
    }

    public int getDuracao() {
        return duracao;
    }
}