<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Mis órdenes médicas | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">

    <style>
        .historial-pagina {
            width: min(1050px, calc(100% - 40px));
            margin: 36px auto 56px;
        }

        .historial-encabezado {
            margin-bottom: 28px;
        }

        .historial-encabezado p {
            color: var(--color-texto-secundario);
        }

        .historial-descripcion {
            max-width: 650px;
        }

        .historial-volver {
            display: inline-flex;
            margin-top: 8px;
        }

        .historial-alerta {
            margin-bottom: 24px;
        }

        .historial-vacio {
            padding: 42px 24px;
            text-align: center;
            background: var(--color-superficie);
            border: 1px dashed #cbd8e4;
            border-radius: var(--radio);
        }

        .historial-vacio-icono {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 64px;
            height: 64px;
            margin: 0 auto 18px;
            border-radius: 50%;
            background: #eaf3fa;
            color: var(--color-principal);
            font-size: 1.8rem;
        }

        .historial-vacio h2 {
            margin-bottom: 8px;
        }

        .historial-vacio p {
            max-width: 480px;
            margin: 0 auto 22px;
            color: var(--color-texto-secundario);
        }

        .historial-lista {
            display: flex;
            flex-direction: column;
            gap: 24px;
        }

        .orden-tarjeta {
            overflow: hidden;
            background: var(--color-superficie);
            border: 1px solid var(--color-borde);
            border-radius: var(--radio);
            box-shadow: var(--sombra);
        }

        .orden-encabezado {
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 16px;
            padding: 22px 26px;
            background: #f5f9fc;
            border-bottom: 1px solid var(--color-borde);
        }

        .orden-identificacion {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .orden-icono {
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            width: 46px;
            height: 46px;
            border-radius: 12px;
            background: #e2eef8;
            color: var(--color-principal);
            font-size: 1.35rem;
        }

        .orden-encabezado h2 {
            margin: 0 0 4px;
            font-size: 1.25rem;
        }

        .orden-fecha {
            margin: 0;
            color: var(--color-texto-secundario);
            font-size: 0.92rem;
        }

        .orden-etiqueta {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 7px 12px;
            border: 1px solid #b8e0c7;
            border-radius: 20px;
            background: #eaf7ef;
            color: var(--color-exito);
            font-size: 0.85rem;
            font-weight: 700;
        }

        .orden-contenido {
            padding: 24px 26px;
        }

        .orden-contenido h3 {
            margin: 0 0 18px;
            font-size: 1.05rem;
        }

        .orden-tabla-contenedor {
            width: 100%;
            overflow-x: auto;
            border: 1px solid var(--color-borde);
            border-radius: 10px;
        }

        .orden-tabla {
            min-width: 570px;
        }

        .orden-tabla th {
            padding: 13px 16px;
            background: #edf4fa;
            font-size: 0.88rem;
            white-space: nowrap;
        }

        .orden-tabla td {
            padding: 14px 16px;
            font-size: 0.94rem;
        }

        .orden-tabla td:not(:first-child),
        .orden-tabla th:not(:first-child) {
            text-align: right;
        }

        .orden-tabla tbody tr:last-child td {
            border-bottom: none;
        }

        .orden-total {
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 22px;
            padding: 20px 22px;
            border: 1px solid #cbddeb;
            border-radius: 12px;
            background: #f1f7fc;
        }

        .orden-total span {
            color: var(--color-texto-secundario);
            font-weight: 600;
        }

        .orden-total strong {
            color: var(--color-principal);
            font-size: 1.6rem;
            white-space: nowrap;
        }

        @media (max-width: 600px) {
            .historial-pagina {
                width: calc(100% - 24px);
                margin: 24px auto 36px;
            }

            .orden-encabezado {
                align-items: flex-start;
                padding: 18px;
            }

            .orden-contenido {
                padding: 18px;
            }

            .orden-total {
                padding: 17px;
            }

            .orden-total strong {
                font-size: 1.4rem;
            }
        }
    </style>
</head>

<body>
    <jsp:include page="fragmentos/navegacion.jsp"/>

    <main class="historial-pagina">

        <header class="historial-encabezado">
            <h1>Mis órdenes médicas</h1>

            <p class="historial-descripcion">
                Consultá tus órdenes confirmadas, las prestaciones incluidas
                y el detalle de sus importes.
            </p>

            <a class="boton-secundario historial-volver"
               href="${pageContext.request.contextPath}/inicio">
                ← Volver al inicio
            </a>
        </header>

        <c:if test="${not empty errorHistorial}">
            <div class="mensaje mensaje-error historial-alerta" role="alert">
                <c:out value="${errorHistorial}"/>
            </div>
        </c:if>

        <c:if test="${empty errorHistorial}">
            <c:choose>
                <c:when test="${empty ordenes}">
                    <section class="historial-vacio">
                        <div class="historial-vacio-icono" aria-hidden="true">
                            <span>▤</span>
                        </div>

                        <h2>Todavía no tenés órdenes médicas</h2>

                        <p>
                            Cuando confirmes una solicitud, vas a poder
                            consultar acá la orden y el detalle de sus
                            prestaciones.
                        </p>

                        <a class="boton"
                           href="${pageContext.request.contextPath}/solicitud">
                            Ir a mi solicitud
                        </a>
                    </section>
                </c:when>

                <c:otherwise>
                    <div class="historial-lista">

                        <c:forEach var="orden" items="${ordenes}">
                            <article class="orden-tarjeta">

                                <header class="orden-encabezado">
                                    <div class="orden-identificacion">
                                        <div class="orden-icono"
                                             aria-hidden="true">
                                            ▤
                                        </div>

                                        <div>
                                            <h2>
                                                Orden médica #
                                                <c:out value="${orden.id}"/>
                                            </h2>

                                            <p class="orden-fecha">
                                            Fecha:
                                            <c:choose>
                                                <c:when test="${not empty orden.fecha and fn:length(orden.fecha) >= 16}">
                                                    <c:out value="${fn:substring(orden.fecha, 8, 10)}"/>/<c:out value="${fn:substring(orden.fecha, 5, 7)}"/>/<c:out value="${fn:substring(orden.fecha, 0, 4)}"/>
                                                    <c:out value="${fn:substring(orden.fecha, 11, 16)}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${orden.fecha}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        </div>
                                    </div>

                                    <span class="orden-etiqueta">
                                        ✓ Confirmada
                                    </span>
                                </header>

                                <div class="orden-contenido">
                                    <h3>Detalle de prestaciones</h3>

                                    <div class="orden-tabla-contenedor">
                                        <table class="orden-tabla">
                                            <thead>
                                                <tr>
                                                    <th>Prestación</th>
                                                    <th>Cantidad</th>
                                                    <th>Precio unitario</th>
                                                    <th>Subtotal</th>
                                                </tr>
                                            </thead>

                                            <tbody>
                                                <c:forEach var="linea"
                                                           items="${orden.lineas}">
                                                    <tr>
                                                        <td>
                                                            <c:out value="${linea.nombrePrestacion}"/>
                                                        </td>

                                                        <td>
                                                            <c:out value="${linea.cantidad}"/>
                                                        </td>

                                                        <td>
                                                            $
                                                            <fmt:formatNumber
                                                                value="${linea.precioUnitario}"
                                                                minFractionDigits="2"
                                                                maxFractionDigits="2"/>
                                                        </td>

                                                        <td>
                                                            $
                                                            <fmt:formatNumber
                                                                value="${linea.subtotal}"
                                                                minFractionDigits="2"
                                                                maxFractionDigits="2"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="orden-total">
                                        <span>Total de la orden</span>

                                        <strong>
                                            $
                                            <fmt:formatNumber
                                                value="${orden.total}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                                        </strong>
                                    </div>
                                </div>

                            </article>
                        </c:forEach>

                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

    </main>
</body>
</html>
