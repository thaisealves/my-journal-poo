package com.diario.diariopessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diario.diariopessoal.model.entity.Entrada;
import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.Categoria;
import com.diario.diariopessoal.model.enums.Humor;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    // Buscar por autor (usuário)
    List<Entrada> findByAutorOrderByDataCriacaoDesc(Usuario autor);

    // Buscar por autor e categoria
    List<Entrada> findByAutorAndCategoria(Usuario autor, Categoria categoria);

    // Buscar por autor e humor
    List<Entrada> findByAutorAndHumor(Usuario autor, Humor humor);

}
