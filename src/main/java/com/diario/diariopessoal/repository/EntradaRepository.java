package com.diario.diariopessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diario.diariopessoal.model.entity.Entrada;
import com.diario.diariopessoal.model.enums.Humor;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    List<Entrada> findByDiarioIdOrderByDataCriacaoDesc(Long diarioId);
    List<Entrada> findByDiarioIdAndCategoriaId(Long diarioId, Long categoriaId);
    List<Entrada> findByDiarioIdAndHumor(Long diarioId, Humor humor);
}
