package com.duoc.cursos.bff.repository;

import com.duoc.cursos.bff.model.Inscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Carga la inscripcion junto con estudiante, detalles y cursos para armar el resumen completo.
    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    List<Inscripcion> findAllByOrderByIdAsc();

    // Permite obtener una inscripcion completa al consultar por su identificador.
    @Override
    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    Optional<Inscripcion> findById(Long id);

    @Query("""
            select case when count(detalle) > 0 then true else false end
            from Inscripcion inscripcion
            join inscripcion.detalles detalle
            where inscripcion.estudiante.id = :estudianteId
              and detalle.curso.id = :cursoId
            """)
    boolean existsCursoInscrito(Long estudianteId, Long cursoId);
}
