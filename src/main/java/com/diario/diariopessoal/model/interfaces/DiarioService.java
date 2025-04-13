package com.diario.diariopessoal.model.interfaces;

import java.util.List;

import com.diario.diariopessoal.model.entity.Entrada;

//utilização de interfaces
public interface DiarioService {
    public void salvarEntrada(Entrada entrada);
    public List<Entrada> listarEntradas();
    public Entrada buscarEntradaPorId(Long id);
    public void apagarEntrada(Long id);
}
