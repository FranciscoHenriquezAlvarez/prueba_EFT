package com.duoc.cursos.bff.repository;

import com.duoc.cursos.bff.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findAllByOrderByIdAsc();

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);

    boolean existsByIdentificadorIdaasIgnoreCase(String identificadorIdaas);

    boolean existsByIdentificadorIdaasIgnoreCaseAndIdNot(String identificadorIdaas, Long id);

    Optional<Estudiante> findByIdentificadorIdaasIgnoreCase(String identificadorIdaas);

    Optional<Estudiante> findByCorreoIgnoreCase(String correo);
}
