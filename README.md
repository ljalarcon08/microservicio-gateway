# microservicio-gateway

Es un servidor de tipo portal usado para centralizar los llamados a microservicios y securitizarlos

## Contiene

- Controlador para autenticación
- Filtros de seguridad
- Manejo de sesiones
- Registro de aplicaciones y su redirección

## Ejecución

1. Crear ejecutable: mvn clean package
2. java -jar microservicio-gateway.jar --spring.profiles.active=dev
3. Desde navegador, ingresar a http://localhost:8090
4. Servicios registrados:
	- http://localhost:8090/auth/login
	- http://localhost:8090/api/usuario
	- http://localhost:8090/api/producto

## Swagger

http://localhost:8090/auth/swagger-ui.html

## Docker

Para creacion en docker revisar Dockerfile