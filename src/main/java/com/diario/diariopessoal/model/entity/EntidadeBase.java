package com.diario.diariopessoal.model.entity;

import java.time.LocalDateTime;

import com.diario.diariopessoal.model.interfaces.Persistente;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

//implementação de interface
@MappedSuperclass // anotação que indica que esta classe é uma superclasse mapeada, usando campos
                  // comuns para as subclasses
// classe abstrata que representa uma entidade base para o sistema
public abstract class EntidadeBase implements Persistente {
    @Id // chave primaria do banco
    @GeneratedValue(strategy = GenerationType.IDENTITY) // geração automática do id
    protected Long id;

    @Column(name = "data_criacao") // uma coluna no banco com o nome data_criacao
    protected LocalDateTime dataCriacao = LocalDateTime.now();

    public Long getId() { // retorna o id, uso de encapsulamento
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public abstract boolean validar(); // método abstrato que deve ser implementado pelas subclasses, cada entidade
                                       // deve criar sua validação personalizada
}
