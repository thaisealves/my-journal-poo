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

    public static TipoAssinatura getPlanoPorDuracao(int meses) {
        for (TipoAssinatura plano : TipoAssinatura.values()) {
            if (plano.getDuracao() == meses) {
                return plano;
            }
        }
        throw new IllegalArgumentException("Nenhum plano encontrado para a duração especificada: " + meses);
    }
}