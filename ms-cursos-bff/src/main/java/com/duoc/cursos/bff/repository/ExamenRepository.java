package com.duoc.cursos.bff.repository;

import com.duoc.cursos.bff.model.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByCursoIdOrderByIdAsc(Long cursoId);
}
