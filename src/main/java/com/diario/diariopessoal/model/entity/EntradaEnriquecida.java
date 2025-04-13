package com.diario.diariopessoal.model.entity;

import com.diario.diariopessoal.model.enums.TipoConteudo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;

@Entity
public class EntradaEnriquecida extends Entrada {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conteudo", nullable = false)
    private TipoConteudo tipoConteudo;

    @Column(name = "url_conteudo")
    private String urlConteudo;

    @Column(name = "descricao_midia", length = 500)
    private String descricaoMidia;

    @Lob
    @Column(name = "conteudo_binario")
    private byte[] conteudoBinario;

    public EntradaEnriquecida() {
    }

    public EntradaEnriquecida(String conteudo, Categoria categoria, Usuario autor, TipoConteudo tipoConteudo,
            String urlConteudo) {
        super(conteudo, categoria, autor);
        this.tipoConteudo = tipoConteudo;
        this.urlConteudo = urlConteudo;
    }

    public String getUrlConteudo() {
        return urlConteudo;
    }

    public void setUrlConteudo(String urlConteudo) {
        this.urlConteudo = urlConteudo;
    }

    public TipoConteudo getTipoConteudo() {
        return tipoConteudo;
    }

    public String getDescricaoMidia() {
        return descricaoMidia;
    }

    public void setDescricaoMidia(String descricaoMidia) {
        this.descricaoMidia = descricaoMidia;
    }

    public byte[] getConteudoBinario() {
        return conteudoBinario;
    }

    public void setConteudoBinario(byte[] conteudoBinario) {
        this.conteudoBinario = conteudoBinario;
    }

    @Override
    public boolean validar() {
        if (!super.validar()) {
            return false;
        }

        if (tipoConteudo == null) {
            return false;
        }

        // Validação específica por tipo
        switch (tipoConteudo) {
            case TEXTO:
                return true;

            case LINK_IMAGEM:
            case LINK_VIDEO:
            case LINK_AUDIO:
            case LINK_DOCUMENTO:
                return urlConteudo != null && !urlConteudo.isEmpty();

            case CONTEUDO_EMBUTIDO:
                return conteudoBinario != null && conteudoBinario.length > 0;

            default:
                return false;
        }
    }

}
