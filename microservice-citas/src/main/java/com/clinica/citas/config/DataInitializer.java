package com.clinica.citas.config;

import com.clinica.citas.model.cita.Cita;
import com.clinica.citas.model.cita.EstadoCita;
import com.clinica.citas.model.especialidad.Especialidad;
import com.clinica.citas.model.horario.HorarioDoctor;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.cita.CitaRepository;
import com.clinica.citas.repository.especialidad.EspecialidadRepository;
import com.clinica.citas.repository.horario.HorarioDoctorRepository;
import com.clinica.citas.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Carga datos iniciales de prueba si la base de datos está vacía
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioDoctorRepository horarioDoctorRepository;
    private final CitaRepository citaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0 || especialidadRepository.count() > 0) {
            log.debug("La base de datos clinica_citas ya contiene datos. Se omite carga inicial.");
            return;
        }

        log.info("Inicializando datos de prueba en clinica_citas...");

        Especialidad cardiologia = especialidadRepository.save(Especialidad.builder()
                .nombre("Cardiología")
                .descripcion("Diagnóstico y tratamiento de enfermedades cardiovasculares")
                .activo(true)
                .build());
        Especialidad pediatria = especialidadRepository.save(Especialidad.builder()
                .nombre("Pediatría")
                .descripcion("Atención médica integral para niños y adolescentes")
                .activo(true)
                .build());
        Especialidad dermatologia = especialidadRepository.save(Especialidad.builder()
                .nombre("Dermatología")
                .descripcion("Cuidado, diagnóstico y tratamiento de enfermedades de la piel")
                .activo(true)
                .build());
        Especialidad medicinaGeneral = especialidadRepository.save(Especialidad.builder()
                .nombre("Medicina General")
                .descripcion("Evaluación médica primaria, preventiva y chequeos integrales")
                .activo(true)
                .build());
        especialidadRepository.save(Especialidad.builder()
                .nombre("Traumatología")
                .descripcion("Tratamiento de lesiones musculoesqueléticas y articulares")
                .activo(true)
                .build());

        usuarioRepository.save(Usuario.builder()
                .nombres("Admin")
                .apellidos("Clínica")
                .dni("10203040")
                .email("admin@clinica.pe")
                .telefono("+51999888777")
                .passwordHash(passwordEncoder.encode("Admin123!"))
                .rol(RolUsuario.ADMIN)
                .activo(true)
                .build());

        Usuario drMendoza = usuarioRepository.save(Usuario.builder()
                .nombres("Carlos")
                .apellidos("Mendoza Ruiz")
                .dni("45678912")
                .email("cmendoza@clinica.pe")
                .telefono("+51987654321")
                .passwordHash(passwordEncoder.encode("Doctor123!"))
                .rol(RolUsuario.DOCTOR)
                .especialidad(cardiologia)
                .activo(true)
                .build());

        Usuario pacLucia = usuarioRepository.save(Usuario.builder()
                .nombres("Lucía")
                .apellidos("Torres Vega")
                .dni("71234567")
                .email("ltorres@correo.com")
                .telefono("+51912345678")
                .passwordHash(passwordEncoder.encode("Paciente123!"))
                .rol(RolUsuario.PACIENTE)
                .activo(true)
                .build());

        Usuario draVargas = usuarioRepository.save(Usuario.builder()
                .nombres("Elena")
                .apellidos("Vargas Soto")
                .dni("46789012")
                .email("evargas@clinica.pe")
                .telefono("+51987111222")
                .passwordHash(passwordEncoder.encode("Doctor123!"))
                .rol(RolUsuario.DOCTOR)
                .especialidad(pediatria)
                .activo(true)
                .build());

        Usuario drParedes = usuarioRepository.save(Usuario.builder()
                .nombres("Ricardo")
                .apellidos("Paredes Luna")
                .dni("47890123")
                .email("rparedes@clinica.pe")
                .telefono("+51987333444")
                .passwordHash(passwordEncoder.encode("Doctor123!"))
                .rol(RolUsuario.DOCTOR)
                .especialidad(dermatologia)
                .activo(true)
                .build());

        Usuario draCastro = usuarioRepository.save(Usuario.builder()
                .nombres("Sofía")
                .apellidos("Castro Ríos")
                .dni("48901234")
                .email("scastro@clinica.pe")
                .telefono("+51987555666")
                .passwordHash(passwordEncoder.encode("Doctor123!"))
                .rol(RolUsuario.DOCTOR)
                .especialidad(medicinaGeneral)
                .activo(true)
                .build());

        Usuario pacJorge = usuarioRepository.save(Usuario.builder()
                .nombres("Jorge")
                .apellidos("Ramírez Quispe")
                .dni("72345678")
                .email("jramirez@correo.com")
                .telefono("+51923456789")
                .passwordHash(passwordEncoder.encode("Paciente123!"))
                .rol(RolUsuario.PACIENTE)
                .activo(true)
                .build());

        Usuario pacMaria = usuarioRepository.save(Usuario.builder()
                .nombres("María Fernanda")
                .apellidos("Salazar")
                .dni("73456789")
                .email("msalazar@correo.com")
                .telefono("+51934567890")
                .passwordHash(passwordEncoder.encode("Paciente123!"))
                .rol(RolUsuario.PACIENTE)
                .activo(true)
                .build());

        horarioDoctorRepository.saveAll(List.of(
                crearHorario(drMendoza, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(14, 0), "Consultorio 204"),
                crearHorario(drMendoza, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(14, 0), "Consultorio 204"),
                crearHorario(drMendoza, DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(14, 0), "Consultorio 204"),
                crearHorario(draVargas, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(14, 0), "Consultorio 102"),
                crearHorario(draVargas, DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(14, 0), "Consultorio 102"),
                crearHorario(drParedes, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(19, 0), "Consultorio 305"),
                crearHorario(drParedes, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(19, 0), "Consultorio 305"),
                crearHorario(draCastro, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), "Consultorio 101"),
                crearHorario(draCastro, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), "Consultorio 101"),
                crearHorario(draCastro, DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), "Consultorio 101")
        ));

        citaRepository.saveAll(List.of(
                Cita.builder()
                        .paciente(pacLucia)
                        .doctor(drMendoza)
                        .especialidad(cardiologia)
                        .fecha(LocalDate.of(2026, 11, 23))
                        .horaInicio(LocalTime.of(10, 0))
                        .horaFin(LocalTime.of(10, 30))
                        .motivo("Evaluación cardiológica preventiva")
                        .observaciones("Traer electrocardiograma previo")
                        .estado(EstadoCita.PENDIENTE)
                        .build(),
                Cita.builder()
                        .paciente(pacJorge)
                        .doctor(draVargas)
                        .especialidad(pediatria)
                        .fecha(LocalDate.of(2026, 11, 24))
                        .horaInicio(LocalTime.of(9, 30))
                        .horaFin(LocalTime.of(10, 0))
                        .motivo("Control de crecimiento y vacunación")
                        .observaciones("Traer carnet de vacunación")
                        .estado(EstadoCita.CONFIRMADA)
                        .build(),
                Cita.builder()
                        .paciente(pacMaria)
                        .doctor(drParedes)
                        .especialidad(dermatologia)
                        .fecha(LocalDate.of(2026, 11, 25))
                        .horaInicio(LocalTime.of(15, 0))
                        .horaFin(LocalTime.of(15, 40))
                        .motivo("Evaluación de dermatitis atópica")
                        .observaciones("Evitar aplicar cremas 12h antes")
                        .estado(EstadoCita.CONFIRMADA)
                        .build(),
                Cita.builder()
                        .paciente(pacLucia)
                        .doctor(draCastro)
                        .especialidad(medicinaGeneral)
                        .fecha(LocalDate.of(2026, 11, 20))
                        .horaInicio(LocalTime.of(11, 0))
                        .horaFin(LocalTime.of(11, 30))
                        .motivo("Chequeo médico general anual")
                        .observaciones("Paciente atendida con resultados normales")
                        .estado(EstadoCita.ATENDIDA)
                        .build(),
                Cita.builder()
                        .paciente(pacJorge)
                        .doctor(drMendoza)
                        .especialidad(cardiologia)
                        .fecha(LocalDate.of(2026, 11, 25))
                        .horaInicio(LocalTime.of(11, 0))
                        .horaFin(LocalTime.of(11, 30))
                        .motivo("Lectura de Holter 24h")
                        .observaciones("Reprogramada por viaje del paciente")
                        .estado(EstadoCita.CANCELADA)
                        .build()
        ));

        log.info("Datos iniciales cargados en clinica_citas: 5 especialidades, 8 usuarios, 10 horarios y 5 citas.");
    }

    private HorarioDoctor crearHorario(
            Usuario doctor, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin, String consultorio) {
        return HorarioDoctor.builder()
                .doctor(doctor)
                .diaSemana(diaSemana)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .consultorio(consultorio)
                .activo(true)
                .build();
    }
}
