package com.duoc.cursos.bff.repository;

import com.duoc.cursos.bff.model.ContenidoCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoCursoRepository extends JpaRepository<ContenidoCurso, Long> {

    List<ContenidoCurso> findByCursoIdOrderByIdAsc(Long cursoId);
}
