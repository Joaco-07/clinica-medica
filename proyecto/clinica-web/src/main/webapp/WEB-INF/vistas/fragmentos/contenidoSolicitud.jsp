<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div id="contenido-solicitud">

    <c:if test="${not empty aviso}">
        <div class="mensaje solicitud-mensaje
             ${avisoError ? 'mensaje-error' : 'mensaje-exito'}"
             role="${avisoError ? 'alert' : 'status'}">
            <c:out value="${aviso}"/>
        </div>
    </c:if>

    <section class="solicitud-panel">
        <div class="solicitud-listado-encabezado">
            <div>
                <h2>Prestaciones seleccionadas</h2>
                <p class="solicitud-descripcion">
                    Revisá las cantidades y los importes de tu solicitud.
                </p>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty solicitud}">
                <div class="solicitud-vacia">
                    <div class="solicitud-vacia-icono" aria-hidden="true">
                        <span>＋</span>
                    </div>

                    <h3>Tu solicitud está vacía</h3>
                    <p>
                        Agregá una prestación desde el formulario de arriba
                        para comenzar a preparar tu orden médica.
                    </p>
                </div>
            </c:when>

            <c:otherwise>
                <div class="solicitud-listado">
                    <c:forEach var="item" items="${solicitud}">
                        <article class="solicitud-item">
                            <div class="solicitud-item-principal">
                                <div class="solicitud-item-info">
                                    <span class="solicitud-etiqueta">
                                        Prestación seleccionada
                                    </span>

                                    <h3>
                                        <c:out value="${item.nombre}"/>
                                    </h3>

                                    <p class="solicitud-precio-unitario">
                                        Precio unitario:
                                        <strong>
                                            $
                                            <fmt:formatNumber
                                                value="${item.precio}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                                        </strong>
                                    </p>
                                </div>

                                <div class="solicitud-subtotal">
                                    <span>Subtotal</span>
                                    <strong>
                                        $
                                        <fmt:formatNumber
                                            value="${item.subtotal}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </strong>
                                </div>
                            </div>

                            <div class="solicitud-item-acciones">
                                <form class="solicitud-cantidad"
                                      action="${pageContext.request.contextPath}/solicitud"
                                      method="post"
                                      hx-post="${pageContext.request.contextPath}/solicitud"
                                      hx-target="#contenido-solicitud"
                                      hx-swap="outerHTML">

                                    <input type="hidden"
                                           name="id"
                                           value="${item.idPrestacion}">

                                    <span class="solicitud-cantidad-label">
                                        Cantidad
                                    </span>

                                    <button type="submit"
                                            name="accion"
                                            value="restar"
                                            class="solicitud-boton-cantidad"
                                            aria-label="Restar una unidad">
                                        −
                                    </button>

                                    <strong class="solicitud-cantidad-valor">
                                        <c:out value="${item.cantidad}"/>
                                    </strong>

                                    <button type="submit"
                                            name="accion"
                                            value="sumar"
                                            class="solicitud-boton-cantidad"
                                            aria-label="Sumar una unidad">
                                        +
                                    </button>
                                </form>

                                <form class="solicitud-formulario-quitar"
                                      action="${pageContext.request.contextPath}/solicitud"
                                      method="post"
                                      hx-post="${pageContext.request.contextPath}/solicitud"
                                      hx-target="#contenido-solicitud"
                                      hx-swap="outerHTML">

                                    <input type="hidden"
                                           name="id"
                                           value="${item.idPrestacion}">

                                    <button type="submit"
                                            name="accion"
                                            value="quitar"
                                            class="solicitud-boton-quitar">
                                        Quitar prestación
                                    </button>
                                </form>
                            </div>
                        </article>
                    </c:forEach>
                </div>

                <div class="solicitud-resumen">
                    <div>
                        <span>Total estimado</span>
                        <p>
                            Importe de las prestaciones seleccionadas
                        </p>
                    </div>

                    <strong class="solicitud-total">
                        $
                        <fmt:formatNumber
                            value="${total}"
                            minFractionDigits="2"
                            maxFractionDigits="2"/>
                    </strong>
                </div>

                <form class="solicitud-confirmar"
                      action="${pageContext.request.contextPath}/solicitud"
                      method="post">

                    <button type="submit"
                            name="accion"
                            value="confirmar">
                        Confirmar orden médica
                    </button>
                </form>

                <p class="solicitud-nota">
                    El importe es estimado y puede estar sujeto a confirmación.
                </p>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<style>
    .solicitud-listado-encabezado {
        margin-bottom: 24px;
    }

    .solicitud-listado-encabezado h2 {
        margin-bottom: 8px;
    }

    .solicitud-listado-encabezado .solicitud-descripcion {
        margin-bottom: 0;
    }

    .solicitud-listado {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .solicitud-item {
        padding: 22px;
        border: 1px solid var(--color-borde);
        border-radius: 12px;
        background: #fff;
        transition: border-color 0.2s, box-shadow 0.2s;
    }

    .solicitud-item:hover {
        border-color: #b8cfe0;
        box-shadow: 0 4px 14px rgba(28, 55, 80, 0.05);
    }

    .solicitud-item-principal {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 20px;
    }

    .solicitud-item-info {
        min-width: 0;
    }

    .solicitud-etiqueta {
        display: inline-block;
        margin-bottom: 8px;
        color: var(--color-secundario);
        font-size: 0.8rem;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 0.04em;
    }

    .solicitud-item h3 {
        margin: 0 0 8px;
        overflow-wrap: anywhere;
    }

    .solicitud-precio-unitario {
        margin: 0;
        color: var(--color-texto-secundario);
        font-size: 0.92rem;
    }

    .solicitud-precio-unitario strong {
        color: var(--color-texto);
    }

    .solicitud-subtotal {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 4px;
        flex-shrink: 0;
    }

    .solicitud-subtotal span,
    .solicitud-resumen > div > span {
        color: var(--color-texto-secundario);
        font-size: 0.9rem;
    }

    .solicitud-subtotal strong {
        color: var(--color-principal);
        font-size: 1.2rem;
        white-space: nowrap;
    }

    .solicitud-item-acciones {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 14px;
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px solid #edf1f5;
    }

    .solicitud-cantidad {
        display: flex;
        align-items: center;
        gap: 12px;
        margin: 0;
    }

    .solicitud-cantidad-label {
        color: var(--color-texto-secundario);
        font-size: 0.9rem;
    }

    button.solicitud-boton-cantidad {
        min-width: 36px;
        min-height: 36px;
        padding: 0;
        border: 1px solid var(--color-borde);
        border-radius: 10px;
        background: #f5f8fb;
        color: var(--color-principal);
        font-size: 1.25rem;
        line-height: 1;
    }

    button.solicitud-boton-cantidad:hover {
        background: #e4eff8;
        color: var(--color-principal-hover);
    }

    .solicitud-cantidad-valor {
        min-width: 18px;
        text-align: center;
    }

    .solicitud-formulario-quitar {
        margin: 0;
    }

    button.solicitud-boton-quitar {
        min-height: 36px;
        padding: 7px 13px;
        border: 1px solid #f0c8c5;
        border-radius: 20px;
        background: #fff;
        color: var(--color-error);
        font-size: 0.9rem;
    }

    button.solicitud-boton-quitar:hover {
        background: #fef0ef;
        color: #8f1c13;
    }

    .solicitud-resumen {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 20px;
        margin-top: 24px;
        padding: 24px;
        border: 1px solid #cbddeb;
        border-radius: 12px;
        background: #f1f7fc;
    }

    .solicitud-resumen > div > span {
        font-weight: 700;
    }

    .solicitud-resumen p {
        margin: 4px 0 0;
        color: var(--color-texto-secundario);
        font-size: 0.88rem;
    }

    .solicitud-total {
        color: var(--color-principal);
        font-size: clamp(1.5rem, 4vw, 2rem);
        white-space: nowrap;
    }

    .solicitud-confirmar {
        display: flex;
        justify-content: flex-end;
        margin: 20px 0 0;
    }

    .solicitud-confirmar button {
        min-height: 48px;
        padding: 12px 26px;
    }

    .solicitud-nota {
        margin: 12px 0 0;
        color: var(--color-texto-secundario);
        font-size: 0.85rem;
        text-align: right;
    }

    .solicitud-vacia {
        padding: 34px 20px;
        text-align: center;
        border: 1px dashed #cbd8e4;
        border-radius: 12px;
        background: #f8fafc;
    }

    .solicitud-vacia-icono {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 56px;
        height: 56px;
        margin: 0 auto 16px;
        border-radius: 50%;
        background: #eaf3fa;
        color: var(--color-principal);
        font-size: 2rem;
    }

    .solicitud-vacia h3 {
        margin: 0 0 8px;
    }

    .solicitud-vacia p {
        max-width: 440px;
        margin: 0 auto;
        color: var(--color-texto-secundario);
    }

    @media (max-width: 600px) {
        .solicitud-item {
            padding: 17px;
        }

        .solicitud-item-principal {
            flex-direction: column;
            gap: 14px;
        }

        .solicitud-subtotal {
            align-items: flex-start;
        }

        .solicitud-item-acciones {
            align-items: flex-start;
            flex-direction: column;
        }

        .solicitud-cantidad {
            flex-wrap: wrap;
            gap: 10px;
        }

        .solicitud-resumen {
            align-items: flex-start;
            flex-direction: column;
            padding: 20px;
        }

        .solicitud-confirmar,
        .solicitud-confirmar button {
            width: 100%;
        }

        .solicitud-nota {
            text-align: left;
        }
    }
</style>

