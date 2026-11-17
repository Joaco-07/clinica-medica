
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mi solicitud | Clínica</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">
    <script src="${pageContext.request.contextPath}/js/htmx.min.js"></script>

    <style>
        .solicitud-pagina {
            width: min(1000px, calc(100% - 40px));
            margin: 36px auto 56px;
        }

        .solicitud-encabezado {
            margin-bottom: 28px;
        }

        .solicitud-encabezado p {
            color: var(--color-texto-secundario);
            margin-bottom: 12px;
        }

        .solicitud-enlaces {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 16px;
        }

        .solicitud-panel {
            padding: 28px;
            margin-bottom: 24px;
            background: var(--color-superficie);
            border: 1px solid var(--color-borde);
            border-radius: var(--radio);
            box-shadow: var(--sombra);
        }

        .solicitud-panel h2 {
            margin-bottom: 8px;
        }

        .solicitud-descripcion {
            color: var(--color-texto-secundario);
            margin-bottom: 24px;
        }

        .solicitud-formulario {
            display: grid;
            grid-template-columns: minmax(0, 2fr) minmax(120px, 0.7fr) auto;
            align-items: end;
            gap: 18px;
            margin: 0;
        }

        .solicitud-formulario .campo {
            min-width: 0;
        }

        .solicitud-formulario button {
            white-space: nowrap;
        }

        .solicitud-mensaje {
            margin: 0 0 20px;
        }

        @media (max-width: 650px) {
            .solicitud-pagina {
                width: calc(100% - 24px);
                margin: 24px auto 36px;
            }

            .solicitud-panel {
                padding: 20px;
            }

            .solicitud-formulario {
                grid-template-columns: 1fr;
                gap: 14px;
            }

            .solicitud-formulario button {
                width: 100%;
            }
        }
    </style>
</head>

<body>
    <jsp:include page="fragmentos/navegacion.jsp"/>

    <main class="solicitud-pagina">
        <header class="solicitud-encabezado">
            <h1>Mi solicitud</h1>
            <p>
                Seleccioná las prestaciones que necesitás y revisá
                los importes antes de confirmar tu orden médica.
            </p>

            <div class="solicitud-enlaces">
                <a class="boton-secundario"
                   href="${pageContext.request.contextPath}/inicio">
                    Volver al inicio
                </a>

                <a class="boton-secundario"
                   href="${pageContext.request.contextPath}/catalogo">
                    Explorar catálogo
                </a>

                <a class="boton"
                   href="${pageContext.request.contextPath}/historial-ordenes">
                    Ver historial de órdenes
                </a>
            </div>
        </header>

        <section class="solicitud-panel">
            <h2>Agregar una prestación</h2>
            <p class="solicitud-descripcion">
                Elegí una prestación e indicá cuántas unidades querés solicitar.
            </p>

            <c:choose>
                <c:when test="${catalogoNoDisponible}">
                    <div class="mensaje mensaje-error" role="alert">
                        No se pudo consultar el catálogo. Tu selección se conserva.
                    </div>
                </c:when>

                <c:when test="${empty prestaciones}">
                    <p class="solicitud-descripcion">
                        No hay prestaciones disponibles en este momento.
                    </p>
                </c:when>

                <c:otherwise>
                    <form class="solicitud-formulario"
                          action="${pageContext.request.contextPath}/solicitud"
                          method="post"
                          hx-post="${pageContext.request.contextPath}/solicitud"
                          hx-target="#contenido-solicitud"
                          hx-swap="outerHTML">

                        <div class="campo">
                            <label for="prestacion">Prestación</label>
                            <select id="prestacion" name="id" required>
                                <c:forEach var="p" items="${prestaciones}">
                                    <option value="${p.id}">
                                        <c:out value="${p.nombre}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="campo">
                            <label for="cantidad">Cantidad</label>
                            <input id="cantidad"
                                   name="cantidad"
                                   type="number"
                                   min="1"
                                   value="1"
                                   required>
                        </div>

                        <button type="submit" name="accion" value="agregar">
                            + Agregar
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>
        </section>

        <jsp:include page="fragmentos/contenidoSolicitud.jsp"/>
    </main>
</body>
</html>

