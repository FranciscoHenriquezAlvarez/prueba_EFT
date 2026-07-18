package com.duoc.eventos.academicos.consumidor.repository;

import com.duoc.eventos.academicos.consumidor.model.EventoAcademicoMq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoAcademicoMqRepository extends JpaRepository<EventoAcademicoMq, Long> {

    List<EventoAcademicoMq> findByEstadoProcesamientoOrderByFechaProcesamientoDesc(String estadoProcesamiento);

    boolean existsByMensajeId(String mensajeId);
}
