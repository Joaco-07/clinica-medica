
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div id="zona-seguidas">

    <jsp:include page="aviso.jsp"/>

    <c:choose>
        <c:when test="${empty seguidas}">
            <p>Todavía no seguís ninguna prestación.</p>
            <a class="boton-secundario"
               href="${pageContext.request.contextPath}/catalogo">
                Explorar prestaciones
            </a>
        </c:when>

        <c:otherwise>
            <div class="resumen-seguidas">
                <span class="resumen-seguidas-icono" aria-hidden="true">♡</span>

                <div>
                    <strong>
                        Tenés ${fn:length(seguidas)}
                        <c:choose>
                            <c:when test="${fn:length(seguidas) == 1}">
                                prestación seguida
                            </c:when>
                            <c:otherwise>
                                prestaciones seguidas
                            </c:otherwise>
                        </c:choose>
                    </strong>

                    <p>Encontrá acá las prestaciones que guardaste para consultar después.</p>
                </div>
            </div>

            <div class="tabla-contenedor">
                <table>
                    <thead>
                        <tr>
                            <th>Prestación</th>
                            <th>Tipo</th>
                            <th>Precio</th>
                            <th>Fecha de seguimiento</th>
                            <th>Acción</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="seguido" items="${seguidas}">
                            <c:if test="${not empty seguido.prestacion}">
                                <tr>
                                    <td>
                                        <c:out value="${seguido.prestacion.nombre}"/>
                                    </td>
                                    <td>
                                        <c:out value="${seguido.prestacion.tipo}"/>
                                    </td>
                                    <td>
                                        $<fmt:formatNumber
                                            value="${seguido.prestacion.precio}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </td>
                                    <td>
                                        <c:out value="${seguido.fecha}"/>
                                    </td>
                                    <td>
                                        <c:if test="${not empty seguido.prestacion.id}">
                                            <form action="${pageContext.request.contextPath}/seguidas"
                                                  method="post"
                                                  class="form-quitar-seguida"
                                                  hx-post="${pageContext.request.contextPath}/seguidas"
                                                  hx-target="#zona-seguidas"
                                                  hx-swap="outerHTML">

                                                <input type="hidden"
                                                       name="accion"
                                                       value="quitar">

                                                <input type="hidden"
                                                       name="id"
                                                       value="${seguido.prestacion.id}">

                                                <button type="button"
                                                        class="boton-secundario boton-abrir-confirmacion">
                                                    Dejar de seguir
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <div id="modal-confirmacion-seguida"
         class="modal-confirmacion"
         role="dialog"
         aria-modal="true"
         aria-labelledby="titulo-confirmacion-seguida"
         hidden>

        <div class="modal-confirmacion-contenido">
            <h2 id="titulo-confirmacion-seguida">
                ¿Dejar de seguir esta prestación?
            </h2>

            <p>
                Ya no aparecerá en tu lista de prestaciones seguidas.
            </p>

            <div class="acciones-formulario">
                <button type="button"
                        id="cancelar-quitar-seguida"
                        class="boton-secundario">
                    Cancelar
                </button>

                <button type="button"
                        id="confirmar-quitar-seguida"
                        class="boton">
                    Sí, dejar de seguir
                </button>
            </div>
        </div>
    </div>

</div>

<style>
    .modal-confirmacion[hidden] {
        display: none !important;
    }

    .modal-confirmacion {
        position: fixed;
        inset: 0;
        z-index: 10000;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
        background: rgba(0, 0, 0, 0.45);
    }

    .modal-confirmacion-contenido {
        width: 100%;
        max-width: 430px;
        padding: 28px;
        border-radius: 18px;
        background: white;
        color: #222;
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
        text-align: center;
    }

    .modal-confirmacion-contenido h2 {
        margin-top: 0;
        font-size: 1.35rem;
    }

    .modal-confirmacion-contenido p {
        margin: 16px 0 24px;
        line-height: 1.5;
    }

    .modal-confirmacion-contenido .acciones-formulario {
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
        gap: 12px;
    }

    .modal-confirmacion-contenido button {
        cursor: pointer;
    }

    .resumen-seguidas {
        display: flex;
        align-items: center;
        gap: 15px;
        margin: 18px 0 24px;
        padding: 18px 22px;
        border: 1px solid #d9e5fa;
        border-radius: 16px;
        background: #f2f6ff;
        color: #244b85;
    }

    .resumen-seguidas-icono {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 44px;
        height: 44px;
        border-radius: 14px;
        background: #dce8ff;
        color: #315fb5;
        font-size: 30px;
    }

    .resumen-seguidas strong {
        display: block;
        margin-bottom: 5px;
        font-size: 1.05rem;
    }

    .resumen-seguidas p {
        margin: 0;
        line-height: 1.5;
        color: #49658f;
    }

    @media (max-width: 480px) {
        .resumen-seguidas {
            align-items: flex-start;
            padding: 15px;
        }
    }
</style>

<script>
    (() => {
        const zona = document.getElementById("zona-seguidas");
        if (!zona || zona.dataset.confirmacionInicializada === "true") {
            return;
        }

        zona.dataset.confirmacionInicializada = "true";

        const modal = zona.querySelector("#modal-confirmacion-seguida");
        const cancelar = zona.querySelector("#cancelar-quitar-seguida");
        const confirmar = zona.querySelector("#confirmar-quitar-seguida");

        let formularioPendiente = null;

        zona.addEventListener("click", (evento) => {
            const boton = evento.target.closest(".boton-abrir-confirmacion");

            if (!boton || !zona.contains(boton)) {
                return;
            }

            formularioPendiente = boton.closest("form");
            modal.hidden = false;
            cancelar.focus();
        });

        cancelar.addEventListener("click", () => {
            modal.hidden = true;
            formularioPendiente = null;
        });

        confirmar.addEventListener("click", () => {
            if (!formularioPendiente) {
                modal.hidden = true;
                return;
            }

            const formulario = formularioPendiente;
            formularioPendiente = null;
            modal.hidden = true;

            if (window.htmx) {
                htmx.trigger(formulario, "submit");
            } else {
                formulario.submit();
            }
        });

        modal.addEventListener("click", (evento) => {
            if (evento.target === modal) {
                modal.hidden = true;
                formularioPendiente = null;
            }
        });
    })();
</script>

