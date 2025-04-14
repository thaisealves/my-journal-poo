package com.diario.diariopessoal.model.entity;

import java.time.LocalDate;

import com.diario.diariopessoal.model.enums.PerfilUsuario;
import com.diario.diariopessoal.model.enums.TipoAssinatura;
import com.diario.diariopessoal.model.enums.TipoConteudo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "usuario_premium")
public class UsuarioPremium extends Usuario {
    
    @Column(name = "data_assinatura")
    private LocalDate dataAssinatura;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "plano", nullable = false)
    private TipoAssinatura plano;

    // construtores
    public UsuarioPremium() {
        super();
    }

    public UsuarioPremium(String nome, String email, String senha, TipoAssinatura plano) {
        super(nome, email, senha);
        this.plano = plano;
        this.dataAssinatura = LocalDate.now();
        super.setPerfil(PerfilUsuario.PREMIUM);
    }

    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }

    
    public int obterDiasRestantes() {
        LocalDate dataAtual = LocalDate.now();
        return (int) (dataAssinatura.plusMonths(plano.getDuracao()).toEpochDay() - dataAtual.toEpochDay());
    }

    public TipoAssinatura getPlano() {
        return plano;
    }

    public boolean isAssinaturaAtiva() {
        return obterDiasRestantes() > 0;
    }

    // considerar a renovação de acordo com o plano atual, adicionando os dias
    // restantes
    public void renovarAssinatura(TipoAssinatura novoPlano) {
        int diasRestantes = obterDiasRestantes();

        if (diasRestantes > 0) {
            this.dataAssinatura = LocalDate.now().minusDays(diasRestantes);
        } else {
            this.dataAssinatura = LocalDate.now();
        }

        this.plano = novoPlano;
    }

   
    public DiarioPremium criarDiarioPremium(String nome, String descricao) {
        DiarioPremium diario = new DiarioPremium(nome, descricao, this);
        super.getDiarios().add(diario);
        return diario;
    }
    
   
    public EntradaEnriquecida criarEntradaEnriquecida(String conteudo, Categoria categoria, 
                                                    TipoConteudo tipoConteudo, String urlConteudo) {
        return new EntradaEnriquecida(conteudo, categoria, this, tipoConteudo, urlConteudo);
    }
    

    public void verificarAssinaturaAtiva() {
        if (!isAssinaturaAtiva()) {
            throw new IllegalStateException("Sua assinatura premium expirou. Por favor, renove para continuar usando recursos premium.");
        }
    }

}