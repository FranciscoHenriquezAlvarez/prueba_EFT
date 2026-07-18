package com.duoc.cursos.bff.repository;

import com.duoc.cursos.bff.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findAllByOrderByIdAsc();
}
