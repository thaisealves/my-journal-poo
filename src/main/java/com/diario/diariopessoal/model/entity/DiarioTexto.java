package com.diario.diariopessoal.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.diario.diariopessoal.model.enums.Humor;
import com.diario.diariopessoal.model.interfaces.DiarioService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "diario_texto")
public class DiarioTexto extends DiarioBase implements DiarioService {
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "diario_id")
    private List<Entrada> entradas = new ArrayList<>(); // composição

    // construtores
    public DiarioTexto() {
        super();
    }

    public DiarioTexto(String nome, String descricao, Usuario usuario) {
        super(nome, descricao, usuario);
    }

    // métodos
    @Override
    // polimorfismo e sobrecarga de métodos (adicionar entrada)
    // sobrescrita de metodos de DiarioBase e DiarioService
    public void adicionarEntrada(String conteudo) {
        Entrada entrada = new Entrada(conteudo, Categoria.criarCategoriaPadrao(), getUsuario());
        entradas.add(entrada);

    }

    public void adicionarEntrada(String conteudo, Categoria categoria) {
        Entrada entrada = new Entrada(conteudo, categoria, getUsuario());
        entradas.add(entrada);
    }

    public void adicionarEntrada(String conteudo, Categoria categoria, Humor humor) {
        Entrada entrada = new Entrada(conteudo, categoria, getUsuario());
        entrada.setHumor(humor);
        entradas.add(entrada);
    }

    @Override
    public List<Entrada> listarEntradas() {
        return entradas;
    }

    @Override
    public Entrada buscarEntradaPorId(Long id) {
        if (id == null) {
            return null;
        }

        for (Entrada entrada : entradas) {
            if (entrada.getId().equals(id)) {
                return entrada;
            }
        }

        return null;
    }

    @Override
    public void apagarEntrada(Long id) {
        if (id == null) {
            return;
        }

        entradas.removeIf(entrada -> entrada.getId().equals(id));

    }

    @Override
    public void salvarEntrada(Entrada entrada) {
        if (entrada == null || !entrada.validar()) {
            throw new IllegalArgumentException("Entrada inválida");
        }

        if (!getUsuario().equals(entrada.getAutor())) {
            entrada.setAutor(getUsuario());
        }

        if (entrada.getId() != null) {
            // atualização da entrada existente
            boolean entradaExistente = false;
            for (int i = 0; i < entradas.size(); i++) {
                if (entradas.get(i).getId().equals(entrada.getId())) {
                    entradas.set(i, entrada);
                    entradaExistente = true;
                    break;
                }
            }

            if (!entradaExistente) {
                entradas.add(entrada);
            }
        } else {
            // se nao existir entrada, só adiciona uma nova
            entradas.add(entrada);
        }

    }

}
