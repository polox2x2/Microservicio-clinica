package com.clinica.citas.dto.usuario;

import com.clinica.citas.model.usuario.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {

    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private Long especialidadId;
    private String especialidadNombre;
    private String numeroColegiatura;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
