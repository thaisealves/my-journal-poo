package com.diario.diariopessoal.model.entity;

import com.diario.diariopessoal.model.enums.TipoConteudo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "entrada_enriquecida")
public class EntradaEnriquecida extends Entrada {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conteudo", nullable = false)
    private TipoConteudo tipoConteudo;

    @Column(name = "url_conteudo", length = 1024)
    private String urlConteudo;

    @Column(name = "descricao_midia", length = 500)
    private String descricaoMidia;

    @Lob
    @Column(name = "conteudo_binario")
    private byte[] conteudoBinario;

    public EntradaEnriquecida() {
        super();
    }

    public EntradaEnriquecida(String titulo, String conteudo, Categoria categoria, Usuario autor,
            TipoConteudo tipoConteudo,
            String urlConteudo) {
        super(titulo, conteudo, categoria, autor);
        verificarUsuarioPremium(autor);

        this.tipoConteudo = tipoConteudo;
        this.urlConteudo = urlConteudo;
    }

    public EntradaEnriquecida(String titulo, String conteudo, Categoria categoria, Usuario autor,
            TipoConteudo tipoConteudo, String urlConteudo,
            String descricaoMidia, byte[] conteudoBinario) {
        super(titulo, conteudo, categoria, autor);

        verificarUsuarioPremium(autor);

        this.tipoConteudo = tipoConteudo;
        this.urlConteudo = urlConteudo;
        this.descricaoMidia = descricaoMidia;
        this.conteudoBinario = conteudoBinario;
    }

    private void verificarUsuarioPremium(Usuario usuario) {
        if (!(usuario instanceof UsuarioPremium)) {
            throw new IllegalStateException("Apenas usuários premium podem criar entradas enriquecidas");
        }
    }

    @Override
    public void setAutor(Usuario autor) {
        verificarUsuarioPremium(autor);
        super.setAutor(autor);
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

    public void setTipoConteudo(TipoConteudo tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
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
        if (!(getAutor() instanceof UsuarioPremium)) {
            return false;
        }
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
