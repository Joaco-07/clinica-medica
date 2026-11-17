# Clínica Médica — Sistema de gestión

## 1. Descripción del proyecto

Sistema de gestión de una clínica médica desarrollado en Java, compuesto por dos aplicaciones:

- **clinica-escritorio:** aplicación Java de escritorio que contiene la lógica de negocio, entidades y acceso a datos.
- **clinica-web:** aplicación web que permite a pacientes y médicos utilizar las funcionalidades del sistema desde un navegador.

Ambas aplicaciones se comunican mediante un servicio web SOAP.

### Tecnologías utilizadas

- Java 21
- Maven
- PostgreSQL
- Hibernate 6
- JPA
- JAX-WS / SOAP
- Apache Tomcat 10.1
- JSP, Servlets, HTML, CSS y HTMX
- Docker

## 2. Estructura del proyecto

El repositorio contiene los siguientes módulos:

papTarea/
│
├── clinica-escritorio/
│   ├── src/
│   └── pom.xml
│
├── clinica-web/
│   ├── src/
│   └── pom.xml
│
└── README.md

## 3. Requisitos previos

Antes de ejecutar el proyecto, se necesita tener instalado:

- JDK 21.
- Apache Maven.
- Docker Desktop con Docker Engine funcionando.
- Navegador web.

Para verificar las instalaciones, ejecutar:

java -version
mvn -version
docker --version

## 4. Configuración y ejecución

**IMPORTANTE:** Los componentes deben iniciarse en el orden indicado, ya que existen dependencias entre ellos.

Orden de ejecución:

1. Base de datos PostgreSQL.
2. Servicio SOAP publicado.
3. Aplicación web en Tomcat.

### Paso 1. Iniciar la base de datos

La aplicación utiliza PostgreSQL para almacenar la información.

Configuración de la base de datos:

- **Nombre:** clinicadb
- **Puerto:** 5432
- **Motor:** PostgreSQL

Iniciar el contenedor:

docker start clinica-pg

Comprobar que está funcionando:

docker ps

La configuración de conexión se encuentra en el archivo `persistence.xml` del módulo de escritorio.

La base de datos debe estar disponible antes de iniciar el servicio SOAP.

### Paso 2. Iniciar el servicio SOAP

El módulo de escritorio publica el servicio SOAP que utiliza la aplicación web para acceder a la lógica de negocio.

Abrir una terminal en la carpeta `clinica-escritorio`:

cd clinica-escritorio

Ejecutar:

java -cp target/clinica.jar publicadores.Servidor

El servicio queda publicado en:

http://localhost:9876/controlador

Su WSDL se encuentra disponible en:

http://localhost:9876/controlador?wsdl

**IMPORTANTE:** Mantener esta terminal abierta mientras se utiliza la aplicación web. Si se cierra, el servicio deja de estar disponible.

### Paso 3. Compilar y desplegar la aplicación web

Una vez que PostgreSQL y el servicio SOAP están funcionando, abrir otra terminal en la carpeta `clinica-web`:

cd clinica-web

Compilar el proyecto:

mvn clean package

La compilación genera el archivo:

target/clinica.war

Copiar el archivo WAR al contenedor de Tomcat:

docker cp target/clinica.war clinica-tomcat:/usr/local/tomcat/webapps/clinica.war

Iniciar o reiniciar Tomcat para desplegar la aplicación:

docker restart clinica-tomcat

Una vez iniciado, acceder desde el navegador:

http://localhost:8080/clinica/


## 5. Dependencias entre los componentes

El funcionamiento del sistema depende de la disponibilidad de los tres componentes.

| Componente    | Función                               |
|               |                                       |
| PostgreSQL    | Almacena los datos del sistema.       |
| Servicio SOAP | Expone las operaciones de negocio.    |
| Tomcat        | Ejecuta y publica la aplicación web.  |

La aplicación web consume el servicio SOAP, por lo que este debe estar iniciado para que las operaciones que requieren comunicación con el servidor puedan funcionar.

### ¿Qué sucede si el servicio SOAP no está iniciado?

Si se intenta utilizar la aplicación web sin que el servicio SOAP esté disponible:

- Las operaciones que necesitan comunicarse con el servicio no podrán completarse.
- Se pueden producir errores de conexión o disponibilidad del servicio.
- La aplicación no podrá recuperar ni modificar información a través de SOAP.

**Solución:**

1. Verificar que PostgreSQL esté funcionando.
2. Iniciar nuevamente el servicio SOAP desde `clinica-escritorio`.
3. Mantener el servidor en ejecución.
4. Volver a intentar la operación desde la aplicación web.

No es necesario iniciar nuevamente toda la infraestructura si solamente se detuvo el servicio SOAP.


## 6. Acceso a la aplicación

Una vez completados los pasos anteriores, ingresar desde el navegador a:

http://localhost:8080/clinica/

La aplicación permite acceder a las funcionalidades correspondientes según el tipo de usuario.

### Pacientes

- Consultar el catálogo de prestaciones.
- Buscar y ordenar prestaciones.
- Seguir prestaciones.
- Consultar y gestionar las prestaciones seguidas.
- Armar solicitudes de órdenes médicas.
- Consultar el historial de órdenes.

### Médicos

- Consultar el catálogo de prestaciones.
- Agregar prestaciones.
- Modificar prestaciones.
- Acceder a las funcionalidades habilitadas para su rol.


## 7. Control de acceso y permisos (HTTP 403)

El sistema implementa controles de acceso según el rol del usuario autenticado.

Las operaciones disponibles dependen del tipo de usuario:

- **Paciente:** puede consultar prestaciones, seguir prestaciones, armar solicitudes y consultar su historial de órdenes.
- **Médico:** puede realizar las operaciones de gestión de prestaciones, incluyendo agregar y modificar prestaciones.

### Protección de operaciones restringidas

Las restricciones se verifican en el servidor, no solamente ocultando opciones o botones de la interfaz.

Si un paciente intenta acceder directamente mediante la URL a una operación reservada para médicos, el servidor rechaza la solicitud y devuelve:

HTTP 403 - Forbidden (Prohibido)

Este comportamiento también se aplica cuando se intenta acceder directamente a las operaciones de modificación de prestaciones.

El código HTTP 403 indica que el servidor recibió la solicitud, pero el usuario autenticado no cuenta con los permisos necesarios para realizar esa operación.


## 8. Problemas frecuentes

### Error: Address already in use

Significa que el puerto utilizado por el servicio SOAP ya está ocupado, posiblemente porque existe otra instancia del servidor en ejecución.

Verificar si hay otro proceso utilizando el puerto 9876 antes de iniciar una nueva instancia.

### Error de conexión con PostgreSQL

Verificar que el contenedor `clinica-pg` esté iniciado y que PostgreSQL esté escuchando en el puerto 5432.

### Error de conexión con SOAP

Verificar que el servidor publicado esté ejecutándose y que la dirección configurada sea:

http://localhost:9876/controlador?wsdl

Si el servicio no está iniciado, ejecutar nuevamente el comando indicado en el Paso 2.

### Error 404 en Tomcat

Verificar que el archivo `clinica.war` se haya copiado correctamente y que Tomcat haya completado el despliegue.

También comprobar que se esté utilizando la URL correcta:

http://localhost:8080/clinica/

### Error 403 - Forbidden

Este error aparece cuando un usuario intenta acceder a una operación para la cual no tiene permisos.

Por ejemplo, si un paciente intenta agregar o modificar una prestación mediante el acceso directo a la URL, el servidor rechaza la solicitud.

Para realizar estas operaciones es necesario iniciar sesión con un usuario que tenga el rol de médico.

## 9. Consideraciones finales

Para garantizar el funcionamiento correcto del sistema, respetar el orden de inicio:

1. PostgreSQL.
2. Servicio SOAP.
3. Compilación y despliegue de la aplicación web en Tomcat.

Mantener activo el servicio SOAP durante toda la utilización de la aplicación web.

El sistema cuenta con controles de acceso por roles y validaciones realizadas en el servidor para proteger las operaciones restringidas.