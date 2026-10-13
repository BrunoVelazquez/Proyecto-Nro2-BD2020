# Usuarios
## Administrador
Este usuario administra la base de datos `parquimetros` con acceso total sobre todas las tablas, con la opción de crear usuarios y otorgar privilegios sobre las mismas. Para no comprometer la seguridad se restringe el acceso de este usuario sólo en la máquina local donde se encuentra el servidor MySQL. El password de este usuario es: _admin_.

la interfaz de usuario muestra un área de texto que permite introducir sentencias SQL arbitrarias (select, insert, delete, update, etc) mostrando el resultado (en el caso que corresponda) de la ejecución de dicha consulta en una tabla. En caso que la consulta produzca un error, se muestra un mensaje explicando el error.
Además muestra una lista que contiene los nombres de todas las tablas presentes en la B.D. `parquimetros`. Cuando se selecciona un ítem (nombre de una tabla) de esta lista, se muestra en otra lista los nombres de todos los atributos de la tabla seleccionada.

## Venta
Este usuario está destinado a permitir el acceso de la aplicación de venta de tarjetas y privilegios mínimos para cargar una nueva tarjeta con un saldo inicial y de cualquier tipo. El password de este usuario es: _venta_.

## Inspector
Este usuario está destinado a permitir el acceso de las unidades personales que utilizan los inspectores para controlar los estacionamientos. Con el objetivo de ocultar la estructura de la base de datos, el usuario inspector tiene una visión restringida de la misma, que le permita ver la patente de todos los autos estacionados en cada ubicación. A tal efecto, hay una vista con el nombre _estacionados_ que tiene la siguiente información para cada ubicación:
* Calle y altura de la ubicación
* Patente de todos los autos que tienen un estacionamiento abierto en dicha ubicación.

Un estacionamiento se considera _abierto_ si tiene fecha y hora de entrada pero no tiene fecha y hora de salida (valores nulos).
El usuario inspector tiene privilegios mínimos para:
* Validar número de legajo y password de un inspector (ver Ejercicio 2b),
* Dado un parquímetro, obtener las patentes de los automóviles con un estacionamiento abierto en la ubicación del parquímetro, cargar multas, y registrar accesos a parquímetros.
El password de este usuario es: _inspector_.

Cada inspector cuenta con una unidad personal que utiliza para controlar las ubicaciones que tiene asignadas. Las unidades personales permiten cargar un número de legajo y password del inspector que las utiliza. De esta forma cuando una unidad se conecta a un parquímetro se puede identificar el inspector que la esta utilizando.

Para controlar una ubicación, el inspector comienza recorriendo la ubicación a pie y carga en su unidad la patente de cada auto estacionado. Cuando termina de recorrer la ubicación
conecta su unidad a un parquímetro para comparar las patentes cargadas con los estacionamientos abiertos en la ubicación donde se encuentra el parquímetro. En este momento se generan automáticamente las multas para aquellos autos que no tienen un estacionamiento abierto en dicha ubicación.

Una vez ingresados un número de legajo y un password correctos, la aplicación permite lo siguiente:
1) Ingresar una lista patentes.
2) Seleccionar una ubicación y un parquímetro de la misma.
Luego se conecta la unidad al parquímetro seleccionado:
* Registrando el acceso del inspector al parquímetro,
* Generando las multas correspondientes, y
* Mostrando un listado de las multas labradas con la siguiente información: `número de multa`, `fecha`, `hora`, `calle`, `altura`, `patente del auto` y `legajo del inspector`

Se controla que el inspector tenga asociada la ubicación correspondiente al parquímetro seleccionado, para el dia y hora en que se realiza la conexión. De no ser así, no se generan las multas y se muestra un mensaje indicando que el inspector no esta autorizado para labrar multas en esa ubicación. Considere que el turno mañana es de 8 a 13:59 hs. y el turno tarde es de 14 a 20 hs.
