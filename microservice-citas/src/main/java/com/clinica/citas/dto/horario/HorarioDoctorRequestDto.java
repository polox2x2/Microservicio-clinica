package com.clinica.citas.dto.horario;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDoctorRequestDto {

    @NotNull(message = "El doctorId es obligatorio")
    private Long doctorId;

    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "La duración en minutos es obligatoria")
    @Min(value = 10, message = "La duración mínima por cupo es 10 minutos")
    @Max(value = 180, message = "La duración máxima por cupo es 180 minutos")
    private Integer duracionMinutos;

    @NotBlank(message = "El consultorio es obligatorio")
    @Size(max = 50, message = "El consultorio no debe exceder 50 caracteres")
    private String consultorio;

    private Boolean activo;
}
