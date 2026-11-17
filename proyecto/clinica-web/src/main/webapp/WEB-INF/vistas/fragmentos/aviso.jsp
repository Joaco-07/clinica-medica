
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div id="aviso" role="status" aria-live="polite">
    <c:if test="${not empty aviso}">
        <div class="aviso-personalizado ${avisoError ? 'aviso-error' : 'aviso-exito'}">

            <span class="aviso-icono" aria-hidden="true">
                <c:choose>
                    <c:when test="${avisoError}">!</c:when>
                    <c:otherwise>✓</c:otherwise>
                </c:choose>
            </span>

            <div class="aviso-contenido">
                <strong>
                    <c:choose>
                        <c:when test="${avisoError}">No se pudo completar la acción</c:when>
                        <c:otherwise>¡Listo!</c:otherwise>
                    </c:choose>
                </strong>

                <p><c:out value="${aviso}"/></p>
            </div>
        </div>
    </c:if>
</div>

<style>
    .aviso-personalizado {
        display: flex;
        align-items: flex-start;
        gap: 14px;
        margin: 0 0 22px;
        padding: 17px 20px;
        border: 1px solid;
        border-radius: 16px;
        animation: aparecer-aviso 0.25s ease-out;
    }

    .aviso-exito {
        color: #216b45;
        background: #edf8f1;
        border-color: #c6e8d2;
    }

    .aviso-error {
        color: #a52e37;
        background: #fff0f0;
        border-color: #f2cccc;
    }

    .aviso-icono {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        font-size: 18px;
        font-weight: 700;
    }

    .aviso-exito .aviso-icono {
        color: white;
        background: #278653;
    }

    .aviso-error .aviso-icono {
        color: white;
        background: #c43d46;
    }

    .aviso-contenido strong {
        display: block;
        margin: 2px 0 5px;
        font-size: 1rem;
    }

    .aviso-contenido p {
        margin: 0;
        line-height: 1.5;
    }

    @keyframes aparecer-aviso {
        from {
            opacity: 0;
            transform: translateY(-5px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @media (prefers-reduced-motion: reduce) {
        .aviso-personalizado {
            animation: none;
        }
    }
</style>

