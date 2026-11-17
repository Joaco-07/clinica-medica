# Sistema de gestión de clínica médica

Proyecto académico desarrollado **en equipo**. Combina una aplicación de escritorio en Java, un servicio web SOAP y un cliente web desplegado en Apache Tomcat.

## Funcionalidades
- Registro e inicio de sesión de pacientes y médicos, con permisos por rol.
- Catálogo de prestaciones con búsqueda, ordenamiento y detalle.
- Administración de prestaciones.
- Seguimiento de prestaciones por pacientes.
- Solicitudes de servicios e historial de órdenes.
- Interfaz web con JSP, CSS y actualizaciones parciales mediante htmx.

## Tecnologías
**Java 21 · Maven · PostgreSQL · Hibernate/JPA · JAX-WS/SOAP · Jakarta Servlets/JSP · Apache Tomcat 10.1 · Docker · HTML/CSS · htmx**

## Arquitectura
```text
Navegador → Web (Servlets + JSP + htmx / Tomcat)
                          ↓
                      SOAP / WSDL
                          ↓
           Servidor Java / lógica de negocio
                          ↓
                Hibernate + PostgreSQL
```

## Código fuente
Los módulos se encuentran en la carpeta [`proyecto/`](./proyecto/):

- [`clinica-escritorio`](./proyecto/clinica-escritorio): aplicación Swing, lógica de negocio, persistencia y servicio SOAP.
- [`clinica-web`](./proyecto/clinica-web): servlets, JSP, CSS, htmx y cliente SOAP.

Ver el [README técnico](./proyecto/README.md) para detalles de configuración y ejecución.

## Ejecución local
Requiere JDK 21, Maven, Docker y Tomcat 10.1. Se inicia PostgreSQL, luego el servicio SOAP, y finalmente se compila y despliega el cliente web. El WSDL debe estar disponible en `http://localhost:9876/controlador?wsdl` al compilar `clinica-web`.

**Nota:** Las contraseñas de la configuración incluida en este portfolio son valores de demostración. No deben usarse en producción.

## Participación y créditos
Proyecto desarrollado colaborativamente. Mi contribución se centró en la **aplicación web** (servlets, JSP, autenticación y sesiones), el despliegue en Tomcat y las pruebas de integración. Los restantes módulos y funcionalidades se construyeron junto al equipo.

Repositorio grupal original: [maurihre/papTarea](https://github.com/maurihre/papTarea).

> Proyecto académico de demostración; no está preparado para gestionar datos clínicos reales en producción.
