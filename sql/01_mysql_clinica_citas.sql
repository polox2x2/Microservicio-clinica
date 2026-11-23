CREATE DATABASE IF NOT EXISTS clinica_citas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE clinica_citas;

CREATE TABLE IF NOT EXISTS especialidades (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NULL,
    tarifa_base DECIMAL(10, 2) NOT NULL,
    duracion_estandar_minutos INT NOT NULL DEFAULT 30,
    activo BIT(1) NOT NULL DEFAULT b'1',
    PRIMARY KEY (id),
    UNIQUE KEY uk_especialidades_nombre (nombre)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NULL,
    password_hash VARCHAR(120) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    especialidad_id BIGINT NULL,
    numero_colegiatura VARCHAR(30) NULL,
    activo BIT(1) NOT NULL DEFAULT b'1',
    fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_dni (dni),
    UNIQUE KEY uk_usuarios_email (email),
    UNIQUE KEY uk_usuarios_colegiatura (numero_colegiatura),
    KEY idx_usuarios_especialidad (especialidad_id),
    CONSTRAINT fk_usuarios_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidades (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS horarios_doctores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    dia_semana VARCHAR(15) NOT NULL,
    hora_inicio TIME(6) NOT NULL,
    hora_fin TIME(6) NOT NULL,
    duracion_minutos INT NOT NULL DEFAULT 30,
    consultorio VARCHAR(50) NOT NULL,
    activo BIT(1) NOT NULL DEFAULT b'1',
    PRIMARY KEY (id),
    KEY idx_horarios_doctor (doctor_id),
    CONSTRAINT fk_horarios_doctor FOREIGN KEY (doctor_id) REFERENCES usuarios (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS citas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paciente_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    especialidad_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME(6) NOT NULL,
    hora_fin TIME(6) NOT NULL,
    motivo VARCHAR(300) NOT NULL,
    observaciones VARCHAR(500) NULL,
    notas_clinicas VARCHAR(1000) NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'RESERVADA',
    fecha_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_citas_paciente (paciente_id),
    KEY idx_citas_doctor (doctor_id),
    KEY idx_citas_especialidad (especialidad_id),
    CONSTRAINT fk_citas_paciente FOREIGN KEY (paciente_id) REFERENCES usuarios (id),
    CONSTRAINT fk_citas_doctor FOREIGN KEY (doctor_id) REFERENCES usuarios (id),
    CONSTRAINT fk_citas_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidades (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 5 Especialidades médicas
INSERT IGNORE INTO
    especialidades (
        id,
        nombre,
        descripcion,
        tarifa_base,
        duracion_estandar_minutos,
        activo
    )
VALUES (
        1,
        'Cardiología',
        'Diagnóstico y tratamiento de enfermedades cardiovasculares',
        150.00,
        30,
        b'1'
    ),
    (
        2,
        'Pediatría',
        'Atención médica integral para niños y adolescentes',
        110.00,
        30,
        b'1'
    ),
    (
        3,
        'Dermatología',
        'Cuidado, diagnóstico y tratamiento de enfermedades de la piel',
        130.00,
        30,
        b'1'
    ),
    (
        4,
        'Medicina General',
        'Evaluación médica primaria, preventiva y chequeos integrales',
        80.00,
        20,
        b'1'
    ),
    (
        5,
        'Traumatología',
        'Tratamiento de lesiones musculoesqueléticas y articulares',
        140.00,
        30,
        b'1'
    );

-- 8 Usuarios (contraseñas BCrypt: Admin123!, Doctor123!, Paciente123!)
INSERT IGNORE INTO
    usuarios (
        id,
        nombres,
        apellidos,
        dni,
        email,
        telefono,
        password_hash,
        rol,
        especialidad_id,
        numero_colegiatura,
        activo,
        fecha_creacion
    )
VALUES (
        1,
        'Admin',
        'Clínica',
        '10203040',
        'admin@clinica.pe',
        '+51999888777',
        '$2a$10$olW2kcEGksfFfPyU0N8h1Ou4/sghvCnOdixtKdXx80djS7a7NWWqi',
        'ADMIN',
        NULL,
        NULL,
        b'1',
        NOW()
    ),
    (
        2,
        'Carlos',
        'Mendoza Ruiz',
        '45678912',
        'cmendoza@clinica.pe',
        '+51987654321',
        '$2a$10$XDQwbMy09ysreUz7Dil5T.WO9xGEKAP09IANV6jD71c5G3y1yXQnm',
        'DOCTOR',
        1,
        'CMP-045821',
        b'1',
        NOW()
    ),
    (
        3,
        'Lucía',
        'Torres Vega',
        '71234567',
        'ltorres@correo.com',
        '+51912345678',
        '$2a$10$IVeCv6tQD35RUTYmY3Ebu.UlPqBYPWZgllEA/xm8XaPltm0CgCFDW',
        'PACIENTE',
        NULL,
        NULL,
        b'1',
        NOW()
    ),
    (
        4,
        'Elena',
        'Vargas Soto',
        '46789012',
        'evargas@clinica.pe',
        '+51987111222',
        '$2a$10$XDQwbMy09ysreUz7Dil5T.WO9xGEKAP09IANV6jD71c5G3y1yXQnm',
        'DOCTOR',
        2,
        'CMP-051904',
        b'1',
        NOW()
    ),
    (
        5,
        'Ricardo',
        'Paredes Luna',
        '47890123',
        'rparedes@clinica.pe',
        '+51987333444',
        '$2a$10$XDQwbMy09ysreUz7Dil5T.WO9xGEKAP09IANV6jD71c5G3y1yXQnm',
        'DOCTOR',
        3,
        'CMP-062310',
        b'1',
        NOW()
    ),
    (
        6,
        'Sofía',
        'Castro Ríos',
        '48901234',
        'scastro@clinica.pe',
        '+51987555666',
        '$2a$10$XDQwbMy09ysreUz7Dil5T.WO9xGEKAP09IANV6jD71c5G3y1yXQnm',
        'DOCTOR',
        4,
        'CMP-073418',
        b'1',
        NOW()
    ),
    (
        7,
        'Jorge',
        'Ramírez Quispe',
        '72345678',
        'jramirez@correo.com',
        '+51923456789',
        '$2a$10$IVeCv6tQD35RUTYmY3Ebu.UlPqBYPWZgllEA/xm8XaPltm0CgCFDW',
        'PACIENTE',
        NULL,
        NULL,
        b'1',
        NOW()
    ),
    (
        8,
        'María Fernanda',
        'Salazar',
        '73456789',
        'msalazar@correo.com',
        '+51934567890',
        '$2a$10$IVeCv6tQD35RUTYmY3Ebu.UlPqBYPWZgllEA/xm8XaPltm0CgCFDW',
        'PACIENTE',
        NULL,
        NULL,
        b'1',
        NOW()
    );

-- 10 Horarios de doctores
INSERT IGNORE INTO
    horarios_doctores (
        id,
        doctor_id,
        dia_semana,
        hora_inicio,
        hora_fin,
        duracion_minutos,
        consultorio,
        activo
    )
VALUES (
        1,
        2,
        'MONDAY',
        '08:00:00',
        '14:00:00',
        30,
        'Consultorio 204',
        b'1'
    ),
    (
        2,
        2,
        'WEDNESDAY',
        '08:00:00',
        '14:00:00',
        30,
        'Consultorio 204',
        b'1'
    ),
    (
        3,
        2,
        'FRIDAY',
        '08:00:00',
        '14:00:00',
        30,
        'Consultorio 204',
        b'1'
    ),
    (
        4,
        4,
        'TUESDAY',
        '08:00:00',
        '14:00:00',
        30,
        'Consultorio 102',
        b'1'
    ),
    (
        5,
        4,
        'THURSDAY',
        '08:00:00',
        '14:00:00',
        30,
        'Consultorio 102',
        b'1'
    ),
    (
        6,
        5,
        'MONDAY',
        '14:00:00',
        '19:00:00',
        30,
        'Consultorio 305',
        b'1'
    ),
    (
        7,
        5,
        'WEDNESDAY',
        '14:00:00',
        '19:00:00',
        30,
        'Consultorio 305',
        b'1'
    ),
    (
        8,
        6,
        'MONDAY',
        '08:00:00',
        '18:00:00',
        20,
        'Consultorio 101',
        b'1'
    ),
    (
        9,
        6,
        'TUESDAY',
        '08:00:00',
        '18:00:00',
        20,
        'Consultorio 101',
        b'1'
    ),
    (
        10,
        6,
        'FRIDAY',
        '08:00:00',
        '18:00:00',
        20,
        'Consultorio 101',
        b'1'
    );

-- 5 Citas médicas (se deja libre el lunes 2026-11-23 de 09:00 a 09:30 para pruebas de reserva)
INSERT IGNORE INTO
    citas (
        id,
        paciente_id,
        doctor_id,
        especialidad_id,
        fecha,
        hora_inicio,
        hora_fin,
        motivo,
        observaciones,
        notas_clinicas,
        estado,
        fecha_creacion
    )
VALUES (
        1,
        3,
        2,
        1,
        '2026-11-23',
        '10:00:00',
        '10:30:00',
        'Evaluación cardiológica preventiva',
        'Traer electrocardiograma previo',
        NULL,
        'RESERVADA',
        NOW()
    ),
    (
        2,
        7,
        4,
        2,
        '2026-11-24',
        '09:30:00',
        '10:00:00',
        'Control de crecimiento y vacunación',
        'Traer carnet de vacunación',
        NULL,
        'CONFIRMADA',
        NOW()
    ),
    (
        3,
        8,
        5,
        3,
        '2026-11-25',
        '15:00:00',
        '15:30:00',
        'Evaluación de dermatitis atópica',
        'Evitar aplicar cremas 12h antes',
        NULL,
        'CONFIRMADA',
        NOW()
    ),
    (
        4,
        3,
        6,
        4,
        '2026-11-20',
        '11:00:00',
        '11:20:00',
        'Chequeo médico general anual',
        'Paciente atendida con resultados normales',
        'Signos vitales estables; hemograma y perfil lipídico dentro de parámetros normales.',
        'ATENDIDA',
        NOW()
    ),
    (
        5,
        7,
        2,
        1,
        '2026-11-25',
        '11:00:00',
        '11:30:00',
        'Lectura de Holter 24h',
        'Reprogramada por viaje del paciente',
        NULL,
        'CANCELADA',
        NOW()
    );
