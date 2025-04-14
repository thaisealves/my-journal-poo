package com.diario.diariopessoal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.diario.diariopessoal.model.entity.DiarioBase;

@Repository
public interface DiarioRepository extends JpaRepository<DiarioBase, Long> {
    List<DiarioBase> findByUsuarioId(Long usuarioId);

    @Query("SELECT d FROM DiarioBase d LEFT JOIN FETCH d.entradas WHERE d.id = :id")
    Optional<DiarioBase> findByIdWithEntradas(@PathVariable Long id);

}