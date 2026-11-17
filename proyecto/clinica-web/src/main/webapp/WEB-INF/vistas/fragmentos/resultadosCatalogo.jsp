
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
    .resultados-catalogo {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
        gap: 20px;
    }

    .tarjeta-prestacion {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        gap: 16px;
        padding: 22px;
        border: 1px solid #e2e8f0;
        border-radius: 16px;
        background: #ffffff;
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.04);
    }

    .tarjeta-prestacion h3 {
        margin: 0 0 10px;
        color: #1e293b;
        font-size: 1.15rem;
    }

    .tarjeta-prestacion .tipo-prestacion {
        display: inline-block;
        margin-bottom: 12px;
        padding: 5px 10px;
        border-radius: 20px;
        background: #eef2ff;
        color: #4338ca;
        font-size: 0.8rem;
        font-weight: 600;
    }

    .tarjeta-prestacion .precio-prestacion {
        margin: 12px 0;
        color: #0f766e;
        font-size: 1.35rem;
        font-weight: 700;
    }

    .tarjeta-prestacion .acciones-prestacion {
        display: flex;
        flex-direction: column;
        gap: 10px;
        margin-top: auto;
    }

    .tarjeta-prestacion .acciones-prestacion a,
    .tarjeta-prestacion .acciones-prestacion button {
        box-sizing: border-box;
        display: block;
        width: 100%;
        padding: 10px 16px;
        border-radius: 12px;
        font: inherit;
        font-weight: 600;
        text-align: center;
        text-decoration: none;
        cursor: pointer;
    }

    .tarjeta-prestacion .acciones-prestacion a {
        border: 1px solid #cbd5e1;
        background: #f8fafc;
        color: #334155;
    }

    .tarjeta-prestacion .acciones-prestacion a:hover {
        background: #f1f5f9;
    }

    .tarjeta-prestacion .acciones-prestacion .boton-seguir {
        border: 1px solid #2563eb;
        background: #2563eb;
        color: #ffffff;
    }

    .tarjeta-prestacion .acciones-prestacion .boton-seguir:hover {
        background: #1d4ed8;
    }

    .tarjeta-prestacion .acciones-prestacion .boton-dejar-seguir {
        border: 1px solid #dc2626;
        background: #fff1f2;
        color: #b91c1c;
    }

    .tarjeta-prestacion .acciones-prestacion .boton-dejar-seguir:hover {
        background: #ffe4e6;
    }

    .tarjeta-prestacion .acciones-prestacion .boton-no-verificada {
        border: 1px solid #cbd5e1;
        background: #f1f5f9;
        color: #64748b;
        cursor: not-allowed;
    }

    .tarjeta-prestacion .aviso-ya-seguida {
        display: inline-block;
        align-self: center;
        padding: 4px 8px;
        border: 1px solid #b8dfc7;
        border-radius: 6px;
        background: #e8f5ed;
        color: #216b45;
        font-size: 0.75rem;
        font-weight: 600;
        line-height: 1.2;
        text-align: center;
    }

    .tarjeta-prestacion .acciones-prestacion .aviso-ya-seguida {
        display: block;
        width: fit-content;
        max-width: 100%;
        box-sizing: border-box;
        align-self: center;
        margin-left: auto;
        margin-right: auto;
    }

    .tarjeta-prestacion .acciones-prestacion form {
        display: flex;
        flex-direction: column;
        align-items: stretch;
        gap: 10px;
        margin-bottom: 0;
    }

    @media (max-width: 600px) {
        .resultados-catalogo {
            grid-template-columns: 1fr;
        }
    }
</style>

<c:if test="${not empty error}">
    <div class="mensaje error" role="alert">
        <c:out value="${error}" />
    </div>
</c:if>

<c:choose>
    <c:when test="${empty prestaciones}">
        <div class="panel">
            <p>No se encontraron prestaciones para mostrar.</p>
        </div>
    </c:when>

    <c:otherwise>
        <div class="resultados-catalogo" id="lista-resultados-catalogo">
            <c:forEach var="prestacion" items="${prestaciones}">
                <article class="tarjeta-prestacion">
                    <div>
                        <span class="tipo-prestacion">
                            <c:out value="${prestacion.tipo}" />
                        </span>

                        <h3>
                            <c:out value="${prestacion.nombre}" />
                        </h3>

                        <p class="precio-prestacion">
                            $ <c:out value="${prestacion.precio}" />
                        </p>
                    </div>

                    <div class="acciones-prestacion">
                        <a href="${pageContext.request.contextPath}/prestaciones?accion=detalle&amp;id=${prestacion.id}">
                            Ver detalle
                        </a>

                        <c:if test="${sessionScope.usuario.tipo eq 'PACIENTE' and not empty prestacion.id}">
                            <form action="${pageContext.request.contextPath}/seguidas"
                                  method="post"
                                  hx-post="${pageContext.request.contextPath}/seguidas"
                                  hx-target="#aviso-seguidas"
                                  hx-swap="innerHTML"
                                  data-prestacion-id="${prestacion.id}">

                                <input type="hidden"
                                       name="accion"
                                       value="${idsSeguidas.contains(prestacion.id) ? 'quitar' : 'agregar'}">

                                <input type="hidden"
                                       name="id"
                                       value="${prestacion.id}">

                                <c:choose>
                                    <c:when test="${not estadoSeguidasDisponible}">
                                        <button type="button"
                                                class="boton-no-verificada"
                                                disabled>
                                            No se pudo verificar
                                        </button>
                                    </c:when>

                                    <c:when test="${idsSeguidas.contains(prestacion.id)}">
                                        <div class="aviso-ya-seguida">
                                            ✓ Ya seguís esta prestación
                                        </div>

                                        <button type="submit"
                                                class="boton-dejar-seguir">
                                            Dejar de seguir
                                        </button>
                                    </c:when>

                                    <c:otherwise>
                                        <button type="submit"
                                                class="boton-seguir">
                                            ♡ Seguir prestación
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </c:if>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

