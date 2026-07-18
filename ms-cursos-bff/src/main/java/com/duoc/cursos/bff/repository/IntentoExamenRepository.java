package com.duoc.cursos.bff.repository;

import com.duoc.cursos.bff.model.IntentoExamen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntentoExamenRepository extends JpaRepository<IntentoExamen, Long> {

    @EntityGraph(attributePaths = {"examen", "examen.curso", "estudiante"})
    List<IntentoExamen> findByExamenIdOrderByIdAsc(Long examenId);

    @EntityGraph(attributePaths = {"examen", "examen.curso", "estudiante"})
    List<IntentoExamen> findByEstudianteIdOrderByIdAsc(Long estudianteId);

    @EntityGraph(attributePaths = {"examen", "examen.curso", "estudiante"})
    List<IntentoExamen> findByExamenCursoIdOrderByIdAsc(Long cursoId);

    @Override
    @EntityGraph(attributePaths = {"examen", "examen.curso", "estudiante"})
    Optional<IntentoExamen> findById(Long id);
}
