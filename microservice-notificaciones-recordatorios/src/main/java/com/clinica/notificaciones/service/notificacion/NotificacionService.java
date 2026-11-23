package com.clinica.notificaciones.service.notificacion;

import com.clinica.notificaciones.client.UsuarioClient;
import com.clinica.notificaciones.client.UsuarioClientDto;
import com.clinica.notificaciones.dto.notificacion.NotificacionRequestDto;
import com.clinica.notificaciones.dto.notificacion.NotificacionResponseDto;
import com.clinica.notificaciones.exception.BusinessRuleException;
import com.clinica.notificaciones.exception.ResourceNotFoundException;
import com.clinica.notificaciones.mapper.notificacion.NotificacionMapper;
import com.clinica.notificaciones.model.notificacion.CanalNotificacion;
import com.clinica.notificaciones.model.notificacion.EstadoNotificacion;
import com.clinica.notificaciones.model.notificacion.Notificacion;
import com.clinica.notificaciones.repository.notificacion.NotificacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//gestiona programacion, envio y cancelacion de notificaciones clinicas
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioClient usuarioClient;
    private final NotificacionMapper notificacionMapper;

    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarTodas() {
        return notificacionRepository.findAll().stream()
                .map(notificacionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificacionResponseDto obtenerPorId(Long id) {
        return notificacionMapper.toDto(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPorDestinatarioId(Long destinatarioId) {
        return notificacionRepository.findByDestinatarioIdOrderByFechaCreacionDesc(destinatarioId).stream()
                .map(notificacionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPorContacto(String contacto) {
        return notificacionRepository.findByDestinatarioContactoIgnoreCaseOrderByFechaCreacionDesc(contacto.trim())
                .stream()
                .map(notificacionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPendientes() {
        return listarPorEstado(EstadoNotificacion.PENDIENTE);
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPorEstado(EstadoNotificacion estado) {
        return notificacionRepository.findByEstadoOrderByFechaProgramadaAsc(estado).stream()
                .map(notificacionMapper::toDto)
                .toList();
    }

    @Transactional
    public NotificacionResponseDto programarOEnviar(NotificacionRequestDto request) {
        Notificacion notificacion = notificacionMapper.toEntity(request);
        completarDatosDestinatario(request, notificacion);

        notificacion.setAsunto(request.getAsunto().trim());
        notificacion.setMensaje(request.getMensaje().trim());

        LocalDateTime ahora = LocalDateTime.now();
        notificacion.setFechaProgramada(request.getFechaProgramada() != null ? request.getFechaProgramada() : ahora);

        if (Boolean.TRUE.equals(request.getEnviarInmediatamente())) {
            aplicarEnvioExitoso(notificacion, ahora);
        } else {
            notificacion.setEstado(EstadoNotificacion.PENDIENTE);
            notificacion.setIntentos(0);
        }

        return notificacionMapper.toDto(notificacionRepository.save(notificacion));
    }

    @Transactional
    public NotificacionResponseDto marcarComoEnviada(Long id) {
        Notificacion notificacion = obtenerEntidad(id);
        if (notificacion.getEstado() == EstadoNotificacion.CANCELADA) {
            throw new BusinessRuleException("No se puede enviar una notificación que fue cancelada");
        }
        if (notificacion.getEstado() == EstadoNotificacion.ENVIADA) {
            throw new BusinessRuleException("La notificación ya fue enviada previamente");
        }
        aplicarEnvioExitoso(notificacion, LocalDateTime.now());
        return notificacionMapper.toDto(notificacionRepository.save(notificacion));
    }

    @Transactional
    public NotificacionResponseDto marcarComoFallida(Long id, String detalleError) {
        Notificacion notificacion = obtenerEntidad(id);
        if (notificacion.getEstado() == EstadoNotificacion.CANCELADA) {
            throw new BusinessRuleException("No se puede marcar como fallida una notificación cancelada");
        }
        notificacion.setEstado(EstadoNotificacion.FALLIDA);
        notificacion.setIntentos((notificacion.getIntentos() == null ? 0 : notificacion.getIntentos()) + 1);
        notificacion.setDetalleError(detalleError);
        return notificacionMapper.toDto(notificacionRepository.save(notificacion));
    }

    @Transactional
    public NotificacionResponseDto cancelar(Long id) {
        Notificacion notificacion = obtenerEntidad(id);
        if (notificacion.getEstado() == EstadoNotificacion.ENVIADA) {
            throw new BusinessRuleException("No se puede cancelar una notificación que ya fue enviada");
        }
        if (notificacion.getEstado() == EstadoNotificacion.CANCELADA) {
            throw new BusinessRuleException("La notificación ya se encuentra cancelada");
        }
        notificacion.setEstado(EstadoNotificacion.CANCELADA);
        return notificacionMapper.toDto(notificacionRepository.save(notificacion));
    }

    @Transactional
    public List<NotificacionResponseDto> procesarPendientesVencidas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Notificacion> pendientes = notificacionRepository
                .findByEstadoAndFechaProgramadaLessThanEqual(EstadoNotificacion.PENDIENTE, ahora);

        for (Notificacion notificacion : pendientes) {
            aplicarEnvioExitoso(notificacion, ahora);
        }

        return notificacionRepository.saveAll(pendientes).stream()
                .map(notificacionMapper::toDto)
                .toList();
    }

    private Notificacion obtenerEntidad(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));
    }

    //completa nombre y contacto desde microservice-citas cuando faltan datos
    private void completarDatosDestinatario(NotificacionRequestDto request, Notificacion notificacion) {
        String nombre = textoONulo(request.getDestinatarioNombre());
        String contacto = textoONulo(request.getDestinatarioContacto());

        if (request.getDestinatarioId() != null && (nombre == null || contacto == null)) {
            UsuarioClientDto usuario = consultarUsuarioRemoto(request.getDestinatarioId());
            if (nombre == null) {
                nombre = (usuario.getNombres() + " " + usuario.getApellidos()).trim();
            }
            if (contacto == null) {
                contacto = request.getCanal() == CanalNotificacion.EMAIL
                        ? textoONulo(usuario.getEmail())
                        : textoONulo(usuario.getTelefono());
            }
        }

        if (nombre == null || contacto == null) {
            throw new BusinessRuleException(
                    "Debe proporcionar destinatarioNombre y destinatarioContacto válidos (o un destinatarioId con ese dato registrado)");
        }

        notificacion.setDestinatarioNombre(nombre);
        notificacion.setDestinatarioContacto(contacto);
    }

    private UsuarioClientDto consultarUsuarioRemoto(Long destinatarioId) {
        try {
            UsuarioClientDto usuario = usuarioClient.obtenerUsuarioPorId(destinatarioId);
            if (usuario == null) {
                throw new ResourceNotFoundException("Usuario no encontrado con id: " + destinatarioId);
            }
            return usuario;
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Usuario no encontrado en microservice-citas con id: " + destinatarioId);
        } catch (FeignException ex) {
            throw new BusinessRuleException(
                    "No fue posible consultar al usuario en microservice-citas (código " + ex.status() + ")");
        }
    }

    private void aplicarEnvioExitoso(Notificacion notificacion, LocalDateTime momentoEnvio) {
        notificacion.setEstado(EstadoNotificacion.ENVIADA);
        notificacion.setFechaEnvio(momentoEnvio);
        notificacion.setIntentos((notificacion.getIntentos() == null ? 0 : notificacion.getIntentos()) + 1);
        notificacion.setDetalleError(null);
        log.info("Notificación enviada [canal={}, evento={}, destino={}]",
                notificacion.getCanal(), notificacion.getTipoEvento(), notificacion.getDestinatarioContacto());
    }

    private String textoONulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
