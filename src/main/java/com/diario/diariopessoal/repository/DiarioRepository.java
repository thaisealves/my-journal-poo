package com.diario.diariopessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diario.diariopessoal.model.entity.DiarioBase;

@Repository
public interface DiarioRepository extends JpaRepository<DiarioBase, Long> {
    List<DiarioBase> findByUsuarioId(Long usuarioId);
}