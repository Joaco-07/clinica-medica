
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Detalle de prestación | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">

    <script src="${pageContext.request.contextPath}/js/htmx.min.js"></script>
</head>
<body>

    <jsp:include page="/WEB-INF/vistas/fragmentos/navegacion.jsp"/>

    <main class="contenedor-pagina">

        <header class="encabezado-pagina">
            <h1>Detalle de prestación</h1>
            <p>Consultá la información y las características de esta prestación.</p>
        </header>

        <c:choose>
            <c:when test="${not empty prestacion}">

                <section class="panel detalle-prestacion">

                    <div class="detalle-prestacion-cabecera">
                        <div>
                            <span class="detalle-prestacion-etiqueta">
                                Prestación #<c:out value="${prestacion.id}"/>
                            </span>

                            <h2><c:out value="${prestacion.nombre}"/></h2>
                        </div>

                        <span class="etiqueta-tipo">
                            <c:out value="${prestacion.tipo}"/>
                        </span>
                    </div>

                    <div class="detalle-prestacion-precio">
                        <span>Precio</span>
                        <strong>
                            $<c:out value="${prestacion.precio}"/>
                        </strong>
                    </div>

                    <div class="detalle-prestacion-datos">

                        <div class="detalle-prestacion-dato">
                            <span>Franja horaria</span>
                            <strong><c:out value="${prestacion.franja}"/></strong>
                        </div>

                        <c:if test="${prestacion.tipo == 'ESTUDIO'}">
                            <div class="detalle-prestacion-dato">
                                <span>Duración</span>
                                <strong>
                                    <c:out value="${prestacion.duracionMinutos}"/> minutos
                                </strong>
                            </div>
                        </c:if>

                        <c:if test="${prestacion.tipo == 'TERAPIA'}">
                            <div class="detalle-prestacion-dato">
                                <span>Cantidad de sesiones</span>
                                <strong>
                                    <c:out value="${prestacion.cantidadSesiones}"/>
                                </strong>
                            </div>

                            <div class="detalle-prestacion-dato">
                                <span>Requiere derivación</span>
                                <strong>
                                    <c:choose>
                                        <c:when test="${prestacion.requiereDerivacion}">
                                            Sí
                                        </c:when>
                                        <c:otherwise>No</c:otherwise>
                                    </c:choose>
                                </strong>
                            </div>
                        </c:if>

                    </div>

                    <c:if test="${sessionScope.usuario.tipo eq 'PACIENTE'}">
                        <div class="detalle-seguir">

                            <c:choose>
                                <c:when test="${not estadoSeguidasDisponible}">
                                    <button type="button"
                                            class="boton-no-verificada"
                                            disabled>
                                        No se pudo verificar
                                    </button>
                                </c:when>

                                <c:when test="${yaSeguia}">
                                    <button type="button"
                                            class="boton-ya-seguida"
                                            disabled>
                                        ✓ Ya la seguís
                                    </button>
                                </c:when>

                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/seguidas"
                                          method="post"
                                          hx-post="${pageContext.request.contextPath}/seguidas"
                                          hx-target="#aviso-seguidas"
                                          hx-swap="innerHTML"
                                          data-prestacion-id="${prestacion.id}">

                                        <input type="hidden"
                                               name="accion"
                                               value="agregar">

                                        <input type="hidden"
                                               name="id"
                                               value="${prestacion.id}">

                                        <button type="submit" class="boton">
                                            ♡ Seguir prestación
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>

                        </div>
                    </c:if>

                </section>

                <div id="aviso-seguidas" aria-live="polite"></div>

            </c:when>

            <c:otherwise>
                <section class="panel">
                    <h2>Prestación no disponible</h2>
                    <p>No se encontró la información de esta prestación.</p>
                </section>
            </c:otherwise>
        </c:choose>

        <div class="acciones-formulario detalle-prestacion-acciones">
            <a class="boton-secundario"
               href="${pageContext.request.contextPath}/catalogo">
                Volver al catálogo
            </a>
        </div>

    </main>

    <style>
        .detalle-prestacion {
            max-width: 760px;
            margin: 0 auto;
            padding: 28px;
        }

        .detalle-prestacion-cabecera {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 16px;
            margin-bottom: 26px;
        }

        .detalle-prestacion-etiqueta {
            color: #64748b;
            font-size: 0.9rem;
        }

        .detalle-prestacion-cabecera h2 {
            margin: 8px 0 0;
            font-size: 1.7rem;
            overflow-wrap: anywhere;
        }

        .detalle-prestacion-precio {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 24px;
            padding: 20px 22px;
            border: 1px solid #d9e5fa;
            border-radius: 16px;
            background: #f2f6ff;
            color: #244b85;
        }

        .detalle-prestacion-precio strong {
            font-size: 1.6rem;
        }

        .detalle-prestacion-datos {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 14px;
        }

        .detalle-prestacion-dato {
            display: flex;
            flex-direction: column;
            gap: 8px;
            padding: 17px;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            background: #fff;
            overflow-wrap: anywhere;
        }

        .detalle-prestacion-dato span {
            color: #64748b;
            font-size: 0.9rem;
        }

        .detalle-prestacion-dato strong {
            color: #26364b;
        }

        .detalle-seguir {
            display: flex;
            justify-content: center;
            margin-top: 26px;
        }

        .detalle-seguir form {
            width: 100%;
            display: flex;
            justify-content: center;
        }

        .detalle-seguir button {
            min-width: 220px;
            padding: 10px 16px;
            border-radius: 12px;
            font-weight: 600;
        }

        .detalle-seguir form button {
            width: 100%;
        }

        .boton-ya-seguida {
            border: 1px solid #b8dfc7;
            background: #e8f5ed;
            color: #216b45;
            cursor: default;
            opacity: 1;
        }

        .boton-no-verificada {
            border: 1px solid #cbd5e1;
            background: #f1f5f9;
            color: #64748b;
            cursor: not-allowed;
        }

        .detalle-prestacion-acciones {
            justify-content: center;
            margin-top: 24px;
        }

        @media (max-width: 560px) {
            .detalle-prestacion {
                padding: 20px;
            }

            .detalle-prestacion-datos {
                grid-template-columns: 1fr;
            }

            .detalle-prestacion-cabecera h2 {
                font-size: 1.4rem;
            }

            .detalle-prestacion-precio strong {
                font-size: 1.35rem;
            }

            .detalle-seguir button {
                width: 100%;
            }
        }
    </style>

    <script>
        document.body.addEventListener(
            "prestacionSeguidasActualizada",
            function (event) {
                const id = String(event.detail.id);

                document.querySelectorAll(
                    "form[data-prestacion-id]"
                ).forEach(function (form) {
                    if (form.dataset.prestacionId !== id) {
                        return;
                    }

                    const boton = form.querySelector("button");

                    if (!boton) {
                        return;
                    }

                    boton.type = "button";
                    boton.disabled = true;
                    boton.className = "boton-ya-seguida";
                    boton.textContent = "✓ Ya la seguís";
                });
            }
        );
    </script>

</body>
</html>

