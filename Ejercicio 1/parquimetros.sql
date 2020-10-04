###################################################################


#       Proyecto Nro 2
#       Materia: Bases de Datos - 2do cuatrimestre 2020
#       Integrantes:
#           Velazquez Bruno
#           Kraus Rodrigo



###################################################################

#   Creación de la base de datos
CREATE DATABASE parquimetros;

#   Selección de la base de datos sobre la cual se harán modificaciones
USE parquimetros;

#------------------------------------------------------------------

#   Creación de tablas para las entidades

CREATE TABLE conductores
(
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(45) NOT NULL,
    apellido VARCHAR(45) NOT NULL,
    direccion VARCHAR(45) NOT NULL,
    telefono VARCHAR(45),
    registro INT UNSIGNED NOT NULL,

    CONSTRAINT pk_conductores
    PRIMARY KEY (dni),
) ENGINE=InnoDB;

CREATE TABLE automoviles
(
    patente VARCHAR(6) NOT NULL,
    marca VARCHAR(45),
    modelo VARCHAR(45),
    color VARCHAR(45),
    dni INT UNSIGNED NOT NULL,

    CONSTRAINT pk_automoviles
    PRIMARY KEY (patente),

    CONSTRAINT FK_automoviles_conductores
    FOREIGN KEY (dni) REFERENCES conductores(dni)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tipos_tarjeta
(
    tipo VARCHAR(45) NOT NULL,
    descuento DECIMAL(3,2) NOT NULL DEFAULT 0,

    CONSTRAINT check_descuento 
    CHECK (descuento <= 1 AND descuento >= 0),

    CONSTRAINT pk_tipos_tarjeta
    PRIMARY KEY (tipo)
) ENGINE=InnoDB;

CREATE TABLE tarjeta
(
    id_tarjeta INT UNSIGNED NOT NULL,
    saldo FLOAT(5,2) NOT NULL DEFAULT 0,
    tipo VARCHAR(45) NOT NULL,
    patente VARCHAR(6) NOT NULL,

    CONSTRAINT pk_tarjeta
    PRIMARY KEY (id_tarjeta),

    CONSTRAINT fk_tarjeta_tipos_tarjeta
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta(tipo)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_tarjeta_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE inspectores
(
    legajo INT UNSIGNED NOT NULL,
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(45) NOT NULL,
    apellido VARCHAR(45) NOT NULL,
    password CHAR(32), # Usando MD5

    CONSTRAINT pk_inspectores
    PRIMARY KEY (legajo)
) ENGINE=InnoDB;

CREATE TABLE ubicaciones
(
    calle VARCHAR(45) NOT NULL,
    alura INT UNSIGNED NOT NULL,
    tarifa FLOAT(5,2) NOT NULL,

    CONSTRAINT check_tarifa 
    CHECK (tarifa > 0),

    CONSTRAINT pk_ubicaciones_calle
    PRIMARY KEY (calle),

    CONSTRAINT pk_ubicaciones_altura
    PRIMARY KEY (altura)
) ENGINE=InnoDB;

CREATE TABLE parquimetros
(
    id_parq INT UNSIGNED NOT NULL,
    numero INT UNSIGNED,
    calle VARCHAR(45) NOT NULL,
    alura INT UNSIGNED NOT NULL,

    CONSTRAINT pk_parquimetros
    PRIMARY KEY (id_parq),

    CONSTRAINT fk_parquimetros_calle
    FOREIGN KEY (calle) REFERENCES ubicaciones(calle)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_parquimetros_altura
    FOREIGN KEY (altura) REFERENCES ubicaciones(altura)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB

CREATE TABLE estacionamientos
(
    id_tarjeta INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha_ent DATE NOT NULL,
    hora_ent TIME NOT NULL,
    fecha_sal DATE,
    hora_sal TIME,


    CONSTRAINT pk_estacionamientos_id_parq
    PRIMARY KEY (id_parq),

    CONSTRAINT pk_estacionamientos_fecha_ent
    PRIMARY KEY (fecha_ent),

    CONSTRAINT pk_estacionamientos_hora_ent
    PRIMARY KEY (hora_ent),

    CONSTRAINT fk_estacionamientos_tarjeta
    FOREIGN KEY (id_tarjeta) REFERENCES tarjeta(id_tarjeta)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_estacionamientos_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
        ON DELETE RESTRICT ON UPDATE RESTRICT
)

CREATE TABLE accede
(
    legajo INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_accede_id_parq
    PRIMARY KEY (id_parq),

    CONSTRAINT pk_accede_fecha
    PRIMARY KEY (fecha),

    CONSTRAINT pk_accede_hora
    PRIMARY KEY (hora),

    CONSTRAINT fk_accede_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_accede_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
        ON DELETE RESTRICT ON UPDATE RESTRICT
)

CREATE TABLE asociado_con
(
    id_asociado_con INT UNSIGNED NOT NULL,
    legajo INT UNSIGNED NOT NULL,
    calle VARCHAR(45) NOT NULL,
    alura INT UNSIGNED NOT NULL,
    dia ENUM ('Lu','Ma','Mi','Ju','Vi','Sa','Do'),
    turno ENUM ('M','T'),

    CONSTRAINT pk_asociado_con
    PRIMARY KEY (id_asociado_con),

    CONSTRAINT fk_asociado_con_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_asociado_con_calle
    FOREIGN KEY (calle) REFERENCES ubicaciones(calle)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_asociado_con_altura
    FOREIGN KEY (altura) REFERENCES ubicaciones(altura)
        ON DELETE RESTRICT ON UPDATE RESTRICT
)

CREATE TABLE multa
(
    numero INT UNSIGNED NOT NULL,
    patente VARCHAR(6) NOT NULL,
    id_asociado_con INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_multa
    PRIMARY KEY (numero),

    CONSTRAINT fk_multa_patente
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_multa_asociado_con
    FOREIGN KEY (id_asociado_con) REFERENCES asociado_con(id_asociado_con)
        ON DELETE RESTRICT ON UPDATE RESTRICT
)

#------------------------------------------------------------------

# Creación de vistas
    # Nombre: estacionados
    # Contiene la información de cada ubicación en la siguiente manera:
        # Calle y altura de la ubicación
        # Patente de todos los autos que tienen un estacionamiento abierto en dicha ubicación.

    CREATE VIEW estacionados AS
    SELECT Est_ab_parq.calle, Est_ab_parq.altura, T.patente
    FROM
        (   # Asocio los estacionamientos abiertos con sus parquímetros, según su id_parq
            SELECT Est_ab.*,P.calle calle,P.altura altura
            FROM
                (   # Selecciono todos los estacionamientos abiertos
                    SELECT id_tarjeta,id_parq
                    FROM estacionamientos
                    WHERE (fecha_sal IS NULL) AND (hora_sal IS NULL)
                )   Est_ab 
                NATURAL JOIN
                parquimetros P
        ) Est_ab_parq
        NATURAL JOIN
        tarjeta T
    GROUP BY Est_ab_parq.calle, Est_ab_parq.altura;

#------------------------------------------------------------------

# Creación de usuario y otorgamiento de privelgios

# Creación del usuario admin
    # Nombre: admin
    # Accesible solamente desde el localhost
    # Contraseña del usuario: admin

    CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';

    # Otorgamiento de privilegios: 
        # Crear usuarios y otorgar privilegios sobre los mismos
    # Otorgamiento de acceso total a las tablas del DB parquimetros

    GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;

# Creación del usuario venta
    # Nombre: venta
    # Accesible desde cualquier computadora conectada al servidor
    # Contraseña del usuario: venta

    CREATE USER 'venta'@'%' IDENTIFIED BY 'venta';

    # Otorgamiento de privilegios:
        # Adición de nuevas tarjetas, de cualquier tipo y con saldo inicial
    # Otorgamiento de acceso a la tabla Tarjeta de DB
         
    GRANT INSERT PRIVILEGES ON parquimetros.tarjeta TO 'venta'@'%';
    GRANT SELECT PRIVILEGES ON parquimetros.tipos_tarjeta TO 'venta'@'%';
    
# Creación del usuario inspector
    # Nombre: inspector
    # Accesible desde cualquier computadora conectada al servidor
    # Contraseña del usuario: ispector

    CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';

    # Otorgamiento de privilegios:

        # Acceso a la tabla estacionados

    GRANT SELECT PRIVILEGES ON parquimetros.estacionados TO 'inspector'@'%';

        # Carga de multas

    GRANT INSERT PRIVILEGES ON parquimetros.multa TO 'inspector'@'%';

        # Registrar accesos a parquímetros.

    GRANT INSERT PRIVILEGES ON parquimetros.parquimetros TO 'inspector'@'%';

        # Dado un parquímetro: la obteción de patentes de los automóviles con un estacionamiento abierto en la ubicación del parquímetro

        # Validación de números de legajo y password de un inspector

    
# Preguntas
    # Cómo dar privilegios de ingresar ciertos valores en una tabla
    # Cómo obtener el "dado un parquímetro"

# Asumimos patentes cargadas en la base de dato para hacer multas