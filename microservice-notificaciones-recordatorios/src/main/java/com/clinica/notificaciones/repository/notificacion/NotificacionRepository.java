package com.clinica.notificaciones.repository.notificacion;

import com.clinica.notificaciones.model.notificacion.EstadoNotificacion;
import com.clinica.notificaciones.model.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

// Repositorio JPA de notificaciones
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Notificacion> findByDestinatarioContactoIgnoreCaseOrderByFechaCreacionDesc(String destinatarioContacto);

    List<Notificacion> findByEstadoOrderByFechaProgramadaAsc(EstadoNotificacion estado);

    List<Notificacion> findByEstadoAndFechaProgramadaLessThanEqual(
            EstadoNotificacion estado, LocalDateTime fechaLimite);
}
