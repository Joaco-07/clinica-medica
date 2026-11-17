
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inicio | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">

    <style>
        .bienvenida {
            padding: 32px;
            margin-bottom: 28px;
            border-radius: 18px;
            background: linear-gradient(120deg, #245a81, #2876ad);
            color: white;
        }

        .bienvenida h1 {
            color: white;
            margin-bottom: 10px;
        }

        .bienvenida p {
            margin-bottom: 0;
            color: #edf6ff;
        }

        .etiqueta-rol {
            display: inline-block;
            margin-bottom: 14px;
            padding: 5px 12px;
            border: 1px solid rgba(255, 255, 255, 0.45);
            border-radius: 20px;
            color: white;
            font-size: 0.85rem;
        }

        .tarjeta-inicio {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            gap: 12px;
        }

        .icono-funcionalidad {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 48px;
            height: 48px;
            border-radius: 12px;
            background: #eaf3fa;
            color: #245a81;
            font-size: 23px;
        }

        .tarjeta-inicio h3 {
            margin: 0;
        }

        .tarjeta-inicio p {
            flex: 1;
            margin: 0 0 8px;
            color: var(--color-texto-secundario);
        }

        .seccion-funcionalidades {
            margin-bottom: 32px;
        }

        .seccion-funcionalidades > p {
            color: var(--color-texto-secundario);
        }
    </style>
</head>

<body>

    <jsp:include page="fragmentos/navegacion.jsp"/>

    <main class="contenedor-pagina">

        <header class="bienvenida">
            <c:choose>
                <c:when test="${sessionScope.usuario.tipo eq 'MEDICO'}">
                    <span class="etiqueta-rol">Panel médico</span>
                    <h1>¡Bienvenido/a, <c:out value="${sessionScope.usuario.nombre}"/>!</h1>
                    <p>
                        Desde acá podés acceder a las herramientas
                        de gestión de la clínica.
                    </p>
                </c:when>

                <c:otherwise>
                    <span class="etiqueta-rol">Panel del paciente</span>
                    <h1>¡Bienvenido/a, <c:out value="${sessionScope.usuario.nombre}"/>!</h1>
                    <p>
                        Consultá las prestaciones médicas y accedé
                        a tus solicitudes y órdenes.
                    </p>
                </c:otherwise>
            </c:choose>
        </header>

        <section class="seccion-funcionalidades">
            <h2>Menú principal</h2>
            <p>Seleccioná una opción para continuar.</p>

            <div class="tarjetas">

                <article class="tarjeta tarjeta-inicio">
                    <div class="icono-funcionalidad" aria-hidden="true">
                        &#9829;
                    </div>

                    <h3>Catálogo de prestaciones</h3>

                    <p>
                        Explorá las prestaciones médicas disponibles,
                        buscá por nombre y ordená los resultados.
                    </p>

                    <a class="boton"
                       href="${pageContext.request.contextPath}/catalogo">
                        Explorar catálogo
                    </a>
                </article>

                <c:choose>
                    <c:when test="${sessionScope.usuario.tipo eq 'MEDICO'}">

                        <article class="tarjeta tarjeta-inicio">
                            <div class="icono-funcionalidad" aria-hidden="true">
                                &#9881;
                            </div>

                            <h3>Gestionar prestaciones</h3>

                            <p>
                                Accedé a las herramientas para administrar
                                las prestaciones de la clínica.
                            </p>

                            <a class="boton"
                               href="${pageContext.request.contextPath}/prestaciones">
                                Gestionar prestaciones
                            </a>
                        </article>

                    </c:when>

                    <c:when test="${sessionScope.usuario.tipo eq 'PACIENTE'}">

                        <article class="tarjeta tarjeta-inicio">
                            <div class="icono-funcionalidad" aria-hidden="true">
                                &#9733;
                            </div>

                            <h3>Mis prestaciones seguidas</h3>

                            <p>
                                Consultá las prestaciones que guardaste
                                para encontrarlas fácilmente.
                            </p>

                            <a class="boton"
                               href="${pageContext.request.contextPath}/seguidas">
                                Ver mis seguidas
                            </a>
                        </article>

                        <article class="tarjeta tarjeta-inicio">
                            <div class="icono-funcionalidad" aria-hidden="true">
                                &#9776;
                            </div>

                            <h3>Mi solicitud</h3>

                            <p>
                                Consultá y gestioná las prestaciones
                                de tu solicitud médica.
                            </p>

                            <a class="boton"
                               href="${pageContext.request.contextPath}/solicitud">
                                Ver mi solicitud
                            </a>
                        </article>

                        <article class="tarjeta tarjeta-inicio">
                            <div class="icono-funcionalidad" aria-hidden="true">
                                &#128196;
                            </div>

                            <h3>Mis órdenes médicas</h3>

                            <p>
                                Accedé a tus órdenes médicas
                                y consultá tu historial.
                            </p>

                            <a class="boton"
                               href="${pageContext.request.contextPath}/historial-ordenes">
                                Ver mis órdenes
                            </a>
                        </article>

                    </c:when>
                </c:choose>

            </div>
        </section>

    </main>

</body>
</html>

