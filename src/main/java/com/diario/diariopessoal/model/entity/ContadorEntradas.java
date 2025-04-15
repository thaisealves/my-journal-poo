package com.diario.diariopessoal.model.entity;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;


@Component
public class ContadorEntradas {
    
    private static final AtomicLong totalEntradas = new AtomicLong(0);
    
    // Flag para controlar inicialização
    private static boolean inicializado = false;
    
    /**
     * Obtém o total atual de entradas.
     */
    public static long getTotalEntradas() {
        return totalEntradas.get();
    }
    
    /**
     * Incrementa o contador de entradas.
     */
    public static long incrementar() {
        return totalEntradas.incrementAndGet();
    }
    
    /**
     * Decrementa o contador de entradas.
     */
    public static long decrementar() {
        // Garante que não fique negativo
        while (true) {
            long atual = totalEntradas.get();
            if (atual <= 0) return 0;
            
            if (totalEntradas.compareAndSet(atual, atual - 1)) {
                return atual - 1;
            }
        }
    }
    

    public static synchronized boolean inicializar(long total) {
        if (inicializado) {
            return false;
        }
        
        totalEntradas.set(Math.max(0, total));
        inicializado = true;
        return true;
    }
    

    public static synchronized void resetar(boolean confirmacao) {
        if (confirmacao) {
            totalEntradas.set(0);
            inicializado = false;
        }
    }
}
