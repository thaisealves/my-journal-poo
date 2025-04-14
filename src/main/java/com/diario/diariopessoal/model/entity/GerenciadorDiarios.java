package com.diario.diariopessoal.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//uso de classe generica
public class GerenciadorDiarios<T extends DiarioBase> {
    private Map<String, T> diarios = new HashMap<>(); //usando coleção generica

    // usando parametros genericos
    public void adicionarDiario(String nome, T diario) {
        if (diario == null) {
            throw new IllegalArgumentException("Diário não pode ser nulo");
        }
        diario.setNome(nome);
        diarios.put(nome, diario);
    }

    public void removerDiario(String nome) {
        if (!diarios.containsKey(nome)) {
            throw new IllegalArgumentException("Diário não encontrado: " + nome);
        }
        diarios.remove(nome);
    }

    public T getDiario(String nome) {
        if (!diarios.containsKey(nome)) {
            throw new IllegalArgumentException("Diário não encontrado: " + nome);
        }
        return diarios.get(nome);
    }

    public List<T> listarDiarios() {
        return new ArrayList<>(diarios.values());
    }
}
