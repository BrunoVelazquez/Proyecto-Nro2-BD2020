use parquimetros;

drop procedure conectar;

drop database parquimetros;

drop user admin@localhost;
drop user venta@'%';
drop user inspector@'%';
drop user parquimetro@'%';

flush privileges;

