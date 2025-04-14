package com.diario.diariopessoal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NovoDiarioDTO {
    
    @NotBlank(message = "O nome do diário é obrigatório")
    @Size(min = 1, max = 100, message = "O nome deve ter entre 1 e 100 caracteres")
    private String nome;
    
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    private boolean premium;
    
    // Getters e Setters
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public boolean isPremium() {
        return premium;
    }
    
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
