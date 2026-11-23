package com.clinica.notificaciones.config;

import com.clinica.notificaciones.model.notificacion.CanalNotificacion;
import com.clinica.notificaciones.model.notificacion.EstadoNotificacion;
import com.clinica.notificaciones.model.notificacion.Notificacion;
import com.clinica.notificaciones.model.notificacion.TipoEventoNotificacion;
import com.clinica.notificaciones.repository.notificacion.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final NotificacionRepository notificacionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (notificacionRepository.count() > 0) {
            log.debug("La base de datos clinica_notificaciones ya contiene datos. Se omite carga inicial.");
            return;
        }

        log.info("Inicializando datos de prueba en clinica_notificaciones...");

        notificacionRepository.saveAll(List.of(
                Notificacion.builder()
                        .destinatarioId(3L)
                        .destinatarioNombre("Lucía Torres Vega")
                        .destinatarioContacto("+51912345678")
                        .canal(CanalNotificacion.WHATSAPP)
                        .tipoEvento(TipoEventoNotificacion.RECORDATORIO_CITA)
                        .referenciaId(1L)
                        .asunto("Recordatorio de Cita Médica")
                        .mensaje("Estimada Lucía, le recordamos su cita de Cardiología con el Dr. Carlos Mendoza el 2026-11-23 a las 10:00 hrs.")
                        .estado(EstadoNotificacion.PENDIENTE)
                        .fechaProgramada(LocalDateTime.of(2026, 11, 22, 9, 0, 0))
                        .intentos(0)
                        .build(),
                Notificacion.builder()
                        .destinatarioId(7L)
                        .destinatarioNombre("Jorge Ramírez Quispe")
                        .destinatarioContacto("+51923456789")
                        .canal(CanalNotificacion.WHATSAPP)
                        .tipoEvento(TipoEventoNotificacion.CONFIRMACION_CITA)
                        .referenciaId(2L)
                        .asunto("Cita Médica Confirmada")
                        .mensaje("Estimado Jorge, su cita de Pediatría con la Dra. Elena Vargas para el 2026-11-24 de 09:30 a 10:00 ha sido confirmada.")
                        .estado(EstadoNotificacion.ENVIADA)
                        .fechaProgramada(LocalDateTime.of(2026, 11, 20, 10, 0, 0))
                        .fechaEnvio(LocalDateTime.of(2026, 11, 20, 10, 0, 5))
                        .intentos(1)
                        .build(),
                Notificacion.builder()
                        .destinatarioId(8L)
                        .destinatarioNombre("María Fernanda Salazar")
                        .destinatarioContacto("msalazar@correo.com")
                        .canal(CanalNotificacion.EMAIL)
                        .tipoEvento(TipoEventoNotificacion.CONFIRMACION_CITA)
                        .referenciaId(3L)
                        .asunto("Confirmación de Cita - Dermatología")
                        .mensaje("Estimada María Fernanda, su cita con el Dr. Ricardo Paredes el 2026-11-25 a las 15:00 hrs en el Consultorio 305 está confirmada.")
                        .estado(EstadoNotificacion.ENVIADA)
                        .fechaProgramada(LocalDateTime.of(2026, 11, 20, 11, 15, 0))
                        .fechaEnvio(LocalDateTime.of(2026, 11, 20, 11, 15, 2))
                        .intentos(1)
                        .build(),
                Notificacion.builder()
                        .destinatarioId(7L)
                        .destinatarioNombre("Jorge Ramírez Quispe")
                        .destinatarioContacto("jramirez@correo.com")
                        .canal(CanalNotificacion.EMAIL)
                        .tipoEvento(TipoEventoNotificacion.PAGO_ORDEN)
                        .referenciaId(2L)
                        .asunto("Pago Confirmado - Orden ORD-2026-0002")
                        .mensaje("Estimado Jorge, confirmamos el pago de su orden ORD-2026-0002 por S/ 221.00.")
                        .estado(EstadoNotificacion.PENDIENTE)
                        .fechaProgramada(LocalDateTime.of(2026, 11, 20, 12, 0, 0))
                        .intentos(0)
                        .build(),
                Notificacion.builder()
                        .destinatarioId(7L)
                        .destinatarioNombre("Jorge Ramírez Quispe")
                        .destinatarioContacto("+51923456789")
                        .canal(CanalNotificacion.SMS)
                        .tipoEvento(TipoEventoNotificacion.CANCELACION_CITA)
                        .referenciaId(5L)
                        .asunto("Cita Médica Cancelada")
                        .mensaje("Estimado Jorge, su cita de Cardiología del 2026-11-25 a las 11:00 hrs ha sido cancelada según lo solicitado.")
                        .estado(EstadoNotificacion.ENVIADA)
                        .fechaProgramada(LocalDateTime.of(2026, 11, 20, 14, 30, 0))
                        .fechaEnvio(LocalDateTime.of(2026, 11, 20, 14, 30, 1))
                        .intentos(1)
                        .build()
        ));

        log.info("Datos iniciales cargados en clinica_notificaciones: 5 notificaciones.");
    }
}
