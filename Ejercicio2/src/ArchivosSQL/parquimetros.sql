###################################################################


#       Proyecto Nro 2
#       Materia: Bases de Datos - 2do cuatrimestre 2020
#       Integrantes:
#           Velazquez Bruno
#           Kraus Rodrigo



###################################################################

#   Eliminación de la Base de Datos si existiera

USE parquimetros;

DROP PROCEDURE conectar;

DROP DATABASE parquimetros;

DROP USER admin@localhost;
DROP USER venta@'%';
DROP USER inspector@'%';
DROP USER parquimetro@'%';

FLUSH PRIVILEGES;


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
    PRIMARY KEY (dni)
) ENGINE=InnoDB;

CREATE TABLE automoviles
(
    patente VARCHAR(6) NOT NULL,
    marca VARCHAR(45) NOT NULL,
    modelo VARCHAR(45) NOT NULL,
    color VARCHAR(45) NOT NULL,
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
    descuento DECIMAL(3,2) UNSIGNED NOT NULL DEFAULT 0,

    CONSTRAINT check_descuento 
    CHECK (descuento <= 1 AND descuento >= 0),

    CONSTRAINT pk_tipos_tarjeta
    PRIMARY KEY (tipo)
) ENGINE=InnoDB;

CREATE TABLE tarjetas
(
    id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
    saldo DECIMAL(5,2) NOT NULL DEFAULT 0,
    tipo VARCHAR(45) NOT NULL,
    patente VARCHAR(6) NOT NULL,

    CONSTRAINT pk_tarjeta
    PRIMARY KEY (id_tarjeta),

    CONSTRAINT fk_tarjetas_tipos_tarjeta
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta(tipo)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_tarjetas_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE inspectores
(
    legajo INT UNSIGNED NOT NULL,
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(45) NOT NULL,
    apellido VARCHAR(45) NOT NULL,
    password CHAR(32) NOT NULL, # Usando MD5

    CONSTRAINT pk_inspectores
    PRIMARY KEY (legajo)
) ENGINE=InnoDB;

CREATE TABLE ubicaciones
(
    calle VARCHAR(45) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

    CONSTRAINT check_tarifa 
    CHECK (tarifa > 0),

    CONSTRAINT pk_ubicaciones
    PRIMARY KEY (calle,altura)
) ENGINE=InnoDB;

CREATE TABLE parquimetros
(
    id_parq INT UNSIGNED NOT NULL,
    numero INT UNSIGNED NOT NULL,
    calle VARCHAR(45) NOT NULL,
    altura INT UNSIGNED NOT NULL,

    CONSTRAINT pk_parquimetros
    PRIMARY KEY (id_parq),

    CONSTRAINT fk_parquimetros_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones (calle,altura)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE estacionamientos
(
    id_tarjeta INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha_ent DATE NOT NULL,
    hora_ent TIME NOT NULL,
    fecha_sal DATE,
    hora_sal TIME,


    CONSTRAINT pk_estacionamientos
    PRIMARY KEY (id_parq,fecha_ent,hora_ent),

    CONSTRAINT fk_estacionamientos_tarjetas
    FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_estacionamientos_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE accede
(
    legajo INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_accede
    PRIMARY KEY (id_parq,fecha,hora),

    CONSTRAINT fk_accede_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_accede_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE asociado_con
(
    id_asociado_con INT UNSIGNED NOT NULL AUTO_INCREMENT,
    legajo INT UNSIGNED NOT NULL,
    calle VARCHAR(45) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    dia ENUM ('do','lu','ma','mi','ju','vi','sa') NOT NULL,
    turno ENUM ('M','T') NOT NULL,

    CONSTRAINT pk_asociado_con
    PRIMARY KEY (id_asociado_con),

    CONSTRAINT fk_asociado_con_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_asociado_con_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE multas
(
    numero INT UNSIGNED NOT NULL AUTO_INCREMENT,
    patente VARCHAR(6) NOT NULL,
    id_asociado_con INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_multa
    PRIMARY KEY (numero),

    CONSTRAINT fk_multa_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_multa_asociado_con
    FOREIGN KEY (id_asociado_con) REFERENCES asociado_con(id_asociado_con)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

#------------------------------------------------------------------

# Creación de vistas
    # Nombre: estacionados
    # Contiene la información de cada ubicación de la siguiente manera:
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
        tarjetas T
    ORDER BY Est_ab_parq.calle, Est_ab_parq.altura;

#------------------------------------------------------------------

# Creación de usuario y otorgamiento de privilegios

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
        # Otorgamiento de acceso a la tabla Tarjetas de DB
         
    GRANT INSERT ON parquimetros.tarjetas TO 'venta'@'%';
    GRANT SELECT ON parquimetros.tipos_tarjeta TO 'venta'@'%';
    
# Creación del usuario inspector
    # Nombre: inspector
    # Accesible desde cualquier computadora conectada al servidor
    # Contraseña del usuario: inspector

    CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';

    # Otorgamiento de privilegios:

        # Acceso a la tabla estacionados

    GRANT SELECT ON parquimetros.estacionados TO 'inspector'@'%';

        # Carga de multas

    GRANT INSERT, SELECT ON parquimetros.multas TO 'inspector'@'%';

        # Registrar accesos a parquímetros.

    GRANT INSERT ON parquimetros.accede TO 'inspector'@'%';

        # Dado un parquímetro: la obtención de patentes de los automóviles con un estacionamiento abierto en la ubicación del parquímetro

    GRANT SELECT ON parquimetros.parquimetros TO 'inspector'@'%';

        # Validación de números de legajo y password de un inspector

    GRANT SELECT ON parquimetros.inspectores TO 'inspector'@'%';

# Asumimos patentes cargadas en la base de datos para hacer multas y nuevas tarjetas

#------------------------------------------------------------------

    # Corrección observaciones de Ejercicio 1
    
    GRANT SELECT ON parquimetros.asociado_con TO 'inspector'@'%';

#------------------------------------------------------------------

    # PROYECTO 3

# --------- Punto 1 ----------

    DELIMITER !
    CREATE PROCEDURE conectar(IN id_tarjeta INTEGER , IN id_parq INTEGER) #Simula la conexión de la tarjeta id_tarjeta al parquimetro id_parq
    BEGIN

    DECLARE fechaSalida TIME;
    DECLARE horaSalida TIME;
    DECLARE descuentoTarjeta DECIMAL(3,2); #Variable donde guardo el descuento de la tarjeta pasada por parametro
    DECLARE tarifaUbicacion DECIMAL(5,2); #Variable donde guardo el costo de la ubicacion del parquimetro pasado por parametro
    DECLARE fechaEntrada DATE;
    DECLARE horaEntrada TIME;
    DECLARE tiempo INTEGER;
    DECLARE saldoActual DECIMAL(5, 2);
    DECLARE saldoNuevo DECIMAL(5, 2);

    DECLARE codigo_SQL CHAR(5) DEFAULT '00000';
    DECLARE codigo_MYSQL INT DEFAULT 0;
    DECLARE mensaje_error TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1 codigo_MYSQL= MYSQL_ERRNO, codigo_SQL= RETURNED_SQLSTATE, mensaje_error= MESSAGE_TEXT;
        SELECT 'SQLEXCEPTION!, transaccion abortada' AS resultado, codigo_MySQL, codigo_SQL, mensaje_error;
        ROLLBACK;
    END;

    START TRANSACTION;
    SELECT descuento INTO descuentoTarjeta FROM tipos_tarjeta NATURAL JOIN tarjetas WHERE (tarjetas.id_tarjeta = id_tarjeta);
    SELECT tarifa INTO tarifaUbicacion FROM parquimetros NATURAL JOIN ubicaciones WHERE (parquimetros.id_parq = id_parq);    

    #Si existe un estacionamiento abierto, lo cierro
    IF EXISTS( SELECT * FROM estacionamientos WHERE (estacionamientos.id_tarjeta = id_tarjeta AND estacionamientos.fecha_sal IS NULL AND estacionamientos.hora_sal IS NULL )) THEN #Actualizo la fecha y hora de salida (hora actual) y le cobro

        SELECT saldo INTO saldoNuevo FROM tarjetas WHERE (tarjetas.id_tarjeta = id_tarjeta) FOR UPDATE;
        SELECT hora_sal INTO horaSalida FROM estacionamientos WHERE (estacionamientos.id_tarjeta = id_tarjeta) AND (estacionamientos.hora_sal IS NULL) FOR UPDATE;
        SELECT fecha_sal INTO fechaSalida FROM estacionamientos WHERE (estacionamientos.id_tarjeta = id_tarjeta) AND (estacionamientos.fecha_sal IS NULL) FOR UPDATE;

        SELECT fecha_ent INTO fechaEntrada FROM estacionamientos WHERE (estacionamientos.id_tarjeta = id_tarjeta) AND (estacionamientos.fecha_sal IS NULL);
        SELECT hora_ent INTO horaEntrada FROM estacionamientos WHERE (estacionamientos.id_tarjeta = id_tarjeta) AND (estacionamientos.hora_sal IS NULL);

        UPDATE estacionamientos SET fecha_sal = CURDATE() WHERE (estacionamientos.id_tarjeta = id_tarjeta) AND (estacionamientos.fecha_ent = fechaEntrada);
        UPDATE estacionamientos SET hora_sal = CURTIME() WHERE (estacionamientos.id_tarjeta = id_tarjeta) AND (estacionamientos.hora_ent = horaEntrada);

        #Defino a la variable tiempo como la diferencia de tiempo en minutos desde la fecha actual y la fecha en la que se estaciono
    SET tiempo = TIMESTAMPDIFF(MINUTE, TIMESTAMP(fechaEntrada, horaEntrada), TIMESTAMP(CURDATE(), CURTIME())); #Diferencia en minutos

        UPDATE tarjetas SET saldo = GREATEST(saldo-(tiempo*tarifaUbicacion*(1-descuentoTarjeta)), -999.99) WHERE (tarjetas.id_tarjeta = id_tarjeta);

        #Muestro por pantalla el cierre del estacionamiento
        SELECT 'Cierre exitoso' AS Operacion_Realizada, tiempo AS Tiempo_en_minutos, saldo AS Saldo_nuevo FROM tarjetas WHERE (tarjetas.id_tarjeta = id_tarjeta);

    ELSE #La salida no es nula. Si el saldo de la tarjeta es > 0 se abre un nuevo estacionamiento para la tarjeta y parq involucrados, tomando como fecha y hora de entrada la fecha y hora actual.

        SELECT saldo INTO saldoActual FROM tarjetas WHERE tarjetas.id_tarjeta=id_tarjeta;
        IF(saldoActual > 0) THEN
            INSERT INTO estacionamientos VALUES(id_tarjeta, id_parq, CURDATE(), CURTIME(), NULL, NULL);
            SELECT 'Apertura exitosa' AS Operacion, saldoActual/(tarifaUbicacion*(1-descuentoTarjeta)) AS Minutos_disponibles_para_estar_estacionado;
        ELSE #Muestro error de que no tiene saldo
            SELECT 'Saldo insuficiente para abrir un parquimetro' AS Error;
        END IF;
    END IF;
    COMMIT;
    END; !
    DELIMITER ;

# --------- Punto 2 ----------

# Creación del usuario parquimetro
    # Nombre: parquimetro
    # Accesible desde cualquier computadora conectada al servidor
    # Contraseña del usuario: parq

    CREATE USER 'parquimetro'@'%' IDENTIFIED BY 'parq';

    GRANT EXECUTE ON PROCEDURE parquimetros.conectar TO 'parquimetro'@'%';
    GRANT SELECT ON parquimetros.ubicaciones TO 'parquimetro'@'%';
    GRANT SELECT ON parquimetros.parquimetros TO 'parquimetro'@'%';
    GRANT SELECT ON parquimetros.tarjetas TO 'parquimetro'@'%';
# --------- Punto 3 ----------

CREATE TABLE ventas
(
    id_tarjeta INT UNSIGNED NOT NULL,
    tipo_tarjeta VARCHAR(45) NOT NULL,
    saldo DECIMAL(5,2) NOT NULL DEFAULT 0,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_ventas
    PRIMARY KEY (id_tarjeta,tipo_tarjeta),

    CONSTRAINT fk_ventas_tarjetas
    FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_ventas_tipos_tarjeta
    FOREIGN KEY (tipo_tarjeta) REFERENCES tipos_tarjeta(tipo)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;


DELIMITER !

CREATE TRIGGER nueva_tarjeta
AFTER INSERT ON tarjetas
FOR EACH ROW
BEGIN
    INSERT INTO ventas VALUES (NEW.id_tarjeta,NEW.tipo,NEW.saldo,CURDATE(),CURTIME());
END; !

DELIMITER ;