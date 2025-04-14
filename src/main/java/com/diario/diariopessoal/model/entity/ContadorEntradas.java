package com.diario.diariopessoal.model.entity;

public class ContadorEntradas {
    //atributo estático
    public static int totalEntradas = 0;

    //metodos estáticos
    public static int getTotalEntradas() {
        return totalEntradas;
    }

    public static void incrementarEntradas() {
        totalEntradas++;
    }

    public static int obterTotal() {
        return totalEntradas;
    }

    public static void resetar() {
        totalEntradas = 0;
    }

}
