###################################################################


#       Proyecto Nro 2
#       Materia: Bases de Datos - 2do cuatrimestre 2020
#       Integrantes:
#           Velazquez Bruno
#           Kraus Rodrigo


###################################################################

#   Selección de la base de datos sobre la cual se harán modificaciones
USE parquimetros;

#-------------------------------------------------------------------------
# Carga de datos de Prueba

INSERT INTO conductores(dni, nombre, apellido, direccion, telefono, registro) VALUES(12547879, "Roberto", "Carlos", "Brown", 154879865, 450);
INSERT INTO conductores(dni, nombre, apellido, direccion, telefono, registro) VALUES(10458744, "Alberto", "Kels", "Castelli", 159547862, 840);										
INSERT INTO conductores(dni, nombre, apellido, direccion, telefono, registro) VALUES(40578954, "Oscar", "Nodorio", "Bolivia", 152124578, 320);

INSERT INTO automoviles(patente, marca, modelo, color, dni) VALUES("ABC123", "Audi", "A4", "Negro", 12547879);
INSERT INTO automoviles(patente, marca, modelo, color, dni) VALUES("ZDS879", "Chevrolet", "Spark", "Blanco", 10458744);
INSERT INTO automoviles(patente, marca, modelo, color, dni) VALUES("RGA879", "Citroen", "C4", "Gris", 40578954);

INSERT INTO tipos_tarjeta(tipo, descuento) VALUES("Silver", 0.05);
INSERT INTO tipos_tarjeta(tipo, descuento) VALUES("Premium", 0.3);
INSERT INTO tipos_tarjeta(tipo, descuento) VALUES("Diamond", 0.5);

INSERT INTO tarjetas(id_tarjeta, saldo, tipo, patente) VALUES(1212, 450.01, "Silver", "ABC123");
INSERT INTO tarjetas(id_tarjeta, saldo, tipo, patente) VALUES(5487, 870.95, "Silver", "ZDS879");
INSERT INTO tarjetas(id_tarjeta, saldo, tipo, patente) VALUES(6598, 140.15, "Diamond", "RGA879");

INSERT INTO inspectores(legajo, dni, nombre, apellido, password) VALUES(12, 15487612, "Ricardo", "Carlisa", "pass1");
INSERT INTO inspectores(legajo, dni, nombre, apellido, password) VALUES(13, 15487612, "Omar", "Donofrio", "pass2");
INSERT INTO inspectores(legajo, dni, nombre, apellido, password) VALUES(14, 15487612, "Alan", "Schen", "pass3");

INSERT INTO ubicaciones(calle, altura, tarifa) VALUES("Almafuerte", 1000, 100.15);
INSERT INTO ubicaciones(calle, altura, tarifa) VALUES("Colon", 200, 150.05);
INSERT INTO ubicaciones(calle, altura, tarifa) VALUES("Caronti", 100, 248.04);

INSERT INTO parquimetros(id_parq, numero, calle, altura) VALUES(150, 1, "Almafuerte", 1000);
INSERT INTO parquimetros(id_parq, numero, calle, altura) VALUES(120, 2, "Colon", 200);
INSERT INTO parquimetros(id_parq, numero, calle, altura) VALUES(190, 3, "Caronti", 100);

INSERT INTO estacionamientos(id_tarjeta, id_parq, fecha_ent, hora_ent,fecha_sal, hora_sal) VALUES(1212, 150, "2017/06/06", "23:05:59", "2017-06-07", "23:15:05");
INSERT INTO estacionamientos(id_tarjeta, id_parq, fecha_ent, hora_ent,fecha_sal, hora_sal) VALUES(5487, 120, "2018/01/02", "13:51:02", NULL, NULL);
INSERT INTO estacionamientos(id_tarjeta, id_parq, fecha_ent, hora_ent,fecha_sal, hora_sal) VALUES(6598, 190, "2019/01/08", "14:40:12", NULL, NULL);

INSERT INTO accede(legajo, id_parq, fecha, hora) VALUES(12, 150, "2017/06/06", "21:15:05");
INSERT INTO accede(legajo, id_parq, fecha, hora) VALUES(13, 120, "2018/01/02", "13:11:05");
INSERT INTO accede(legajo, id_parq, fecha, hora) VALUES(14, 190, "2019/01/08", "12:20:01");

INSERT INTO asociado_con(id_asociado_con, legajo, calle, altura, dia, turno) VALUES(5050, 12, "Almafuerte", 1000, "Lu", "M");
INSERT INTO asociado_con(id_asociado_con, legajo, calle, altura, dia, turno) VALUES(6060, 13, "Colon", 200, "Ma", "T");
INSERT INTO asociado_con(id_asociado_con, legajo, calle, altura, dia, turno) VALUES(7070, 14, "Caronti", 100, "Mi", "M");

INSERT INTO multa(numero, fecha, hora, patente, id_asociado_con) VALUES(115, "2017-06-06", "23:05:59", "ABC123", 5050);
INSERT INTO multa(numero, fecha, hora, patente, id_asociado_con) VALUES(116, "2018-01-02", "13:51:02", "ZDS879", 6060);
INSERT INTO multa(numero, fecha, hora, patente, id_asociado_con) VALUES(117, "2019-01-08", "14:40:12", "RGA879", 7070);