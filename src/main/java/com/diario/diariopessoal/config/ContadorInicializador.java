package com.diario.diariopessoal.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.diario.diariopessoal.model.entity.ContadorEntradas;

/**
 * Inicializa o contador estático de entradas na inicialização da aplicação.
 * Consulta o banco de dados para obter o número atual de entradas.
 */
@Component
public class ContadorInicializador implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        // Consulta nativa para contar todas as entradas
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM entrada");
        Long total = ((Number) query.getSingleResult()).longValue();
        
        // Inicializa o contador com o valor atual
        boolean sucesso = ContadorEntradas.inicializar(total);
        
        System.out.println("Contador de entradas " + (sucesso ? "inicializado" : "já inicializado") + 
                           " com: " + ContadorEntradas.getTotalEntradas() + " entradas");
    }
}