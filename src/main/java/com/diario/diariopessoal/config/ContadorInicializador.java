package com.diario.diariopessoal.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.diario.diariopessoal.model.entity.ContadorEntradas;
import com.diario.diariopessoal.repository.EntradaRepository;

import jakarta.annotation.PostConstruct;

@Component
public class ContadorInicializador {

    @Autowired
    private EntradaRepository entradaRepository;

    @PostConstruct
    public void inicializarContador() {
        // Obter o número total de entradas no banco de dados
        long totalEntradas = entradaRepository.count();

        // Sincronizar o valor estático
        ContadorEntradas.resetar();
        for (int i = 0; i < totalEntradas; i++) {
            ContadorEntradas.incrementarEntradas();
        }

        System.out.println("Contador inicializado com " + ContadorEntradas.getTotalEntradas() + " entradas.");
    }
}