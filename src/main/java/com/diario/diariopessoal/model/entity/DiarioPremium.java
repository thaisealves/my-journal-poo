package com.diario.diariopessoal.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.diario.diariopessoal.model.enums.TipoConteudo;
import com.diario.diariopessoal.model.interfaces.DiarioService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//utilizando classe abstrata pra criar herança e usando interface
@Entity
@Table(name = "diario_premium")
public class DiarioPremium extends DiarioBase implements DiarioService {

    // uso de coleção List e ArrayList
    // uso de herança, um diário premium possui várias entradas enriquecidas
    // uso de composição, diário premium contém entradas enriquecidas
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "diario_id")
    private List<EntradaEnriquecida> entradasEnriquecidas = new ArrayList<>();

    public DiarioPremium() {
        super();
    }

    public List<EntradaEnriquecida> getEntradasEnriquecidas() {
        return entradasEnriquecidas;
    }

    public DiarioPremium(String nome, String descricao, Usuario usuario) {
        super(nome, descricao, usuario);

        // Verificação se o usuário é premium
        if (!(usuario instanceof UsuarioPremium)) {
            throw new IllegalStateException("Apenas usuários premium podem criar diários premium");
        }
    }

    public void adicionarEntradaEnriquecida(EntradaEnriquecida entrada) {
        if (entrada == null) {
            throw new IllegalArgumentException("Entrada não pode ser nula");
        }

        // autor é o mesmo usuário do diário
        entrada.setAutor(getUsuario());
        entradasEnriquecidas.add(entrada);
    }

    // sobrescrita de adicionarEntrada
    // polimorfismo e sobrecarga de métodos (adicionar entrada)
    @Override
    public void adicionarEntrada(String titulo, String conteudo) {
        EntradaEnriquecida entrada = new EntradaEnriquecida(titulo,
                conteudo,
                Categoria.criarCategoriaPadrao(),
                getUsuario(),
                TipoConteudo.TEXTO,
                null);
        entradasEnriquecidas.add(entrada);
    }

    public void adicionarEntrada(String titulo, String conteudo, Categoria categoria,
            TipoConteudo tipoConteudo, String urlConteudo) {
        EntradaEnriquecida entrada = new EntradaEnriquecida(titulo,
                conteudo, categoria, getUsuario(), tipoConteudo, urlConteudo);
        entradasEnriquecidas.add(entrada);
    }

    public void apagarEntrada(Long id) {
        if (id == null) {
            return;
        }

        entradasEnriquecidas.removeIf(entrada -> entrada.getId().equals(id));
    }

    // usando sobrescrita ds métodos de classe abstrata e de interface
    @Override
    public EntradaEnriquecida buscarEntradaPorId(Long id) {
        if (id == null) {
            return null;
        }

        for (EntradaEnriquecida entrada : entradasEnriquecidas) {
            if (entrada.getId().equals(id)) {
                return entrada;
            }
        }

        return null;
    }

    // polimorfismo verificação de tipo de usuário, dependendo de qual é o usuario
    // pode ou nao criar o diario premium
    @Override
    public void setUsuario(Usuario usuario) {
        if (!(usuario instanceof UsuarioPremium)) {
            throw new IllegalStateException("Apenas usuários premium podem possuir diários premium");
        }
        super.setUsuario(usuario);
    }

    @Override
    public List<Entrada> listarEntradas() {
        return new ArrayList<>(entradasEnriquecidas);
    }

    @Override
    public void salvarEntrada(Entrada entrada) {
        if (entrada instanceof EntradaEnriquecida) {
            adicionarEntradaEnriquecida((EntradaEnriquecida) entrada);
        } else {
            throw new IllegalArgumentException("A entrada deve ser do tipo EntradaEnriquecida");
        }
    }
}