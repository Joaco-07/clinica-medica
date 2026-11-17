<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav aria-label="Navegación principal">

    <a href="${pageContext.request.contextPath}/inicio" class="marca-logo">
        <img
            src="${pageContext.request.contextPath}/img/logo-clinica.png"
            alt="Logo de la clínica">
    </a>

    <a href="${pageContext.request.contextPath}/catalogo"
       class="${pageContext.request.servletPath eq '/catalogo' ? 'activo' : ''}">
        Catálogo
    </a>

    <c:choose>

        <c:when test="${not empty sessionScope.usuario}">

            <a href="${pageContext.request.contextPath}/inicio"
               class="${pageContext.request.servletPath eq '/inicio' ? 'activo' : ''}">
                Inicio
            </a>

            <c:if test="${sessionScope.usuario.tipo eq 'MEDICO'}">
                <a href="${pageContext.request.contextPath}/prestaciones"
                   class="${pageContext.request.servletPath eq '/prestaciones' ? 'activo' : ''}">
                    Gestionar prestaciones
                </a>
            </c:if>

            <c:if test="${sessionScope.usuario.tipo eq 'PACIENTE'}">

                <a href="${pageContext.request.contextPath}/seguidas"
                   class="${pageContext.request.servletPath eq '/seguidas' ? 'activo' : ''}">
                    Mis seguidas
                </a>

                <a href="${pageContext.request.contextPath}/solicitud"
                   class="${pageContext.request.servletPath eq '/solicitud' ? 'activo' : ''}">
                    Mi solicitud
                </a>

                <a href="${pageContext.request.contextPath}/historial-ordenes"
                   class="${pageContext.request.servletPath eq '/historial-ordenes' ? 'activo' : ''}">
                    Mis órdenes
                </a>

            </c:if>

            <div class="usuario-sesion">

                <div class="usuario-info">

                    <span class="usuario-avatar">
                        <i class="fa-solid fa-user"></i>
                    </span>

                    <div class="usuario-datos">

                        <strong><c:out value="${sessionScope.usuario.nombre}"/></strong>

                        <c:choose>
                            <c:when test="${sessionScope.usuario.tipo eq 'MEDICO'}">
                                <span>Médico</span>
                            </c:when>

                            <c:otherwise>
                                <span>Paciente</span>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/logout" method="post">
                    <button type="submit" class="boton-cerrar-sesion">
                        Cerrar sesión
                    </button>
                </form>

            </div>

        </c:when>

        <c:otherwise>

            <a href="${pageContext.request.contextPath}/login"
               class="${pageContext.request.servletPath eq '/login' ? 'activo' : ''}">
                Iniciar sesión
            </a>

            <a href="${pageContext.request.contextPath}/registro"
               class="${pageContext.request.servletPath eq '/registro' ? 'activo' : ''}">
                Crear una cuenta
            </a>

        </c:otherwise>

    </c:choose>

</nav>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const nav = document.querySelector(
            'nav[aria-label="Navegación principal"]'
        );

        if (!nav) return;

        const rutaActual = window.location.pathname;
        const enlaces = nav.querySelectorAll(':scope > a:not(.marca-logo)');

        let enlaceActivo = null;
        let coincidenciaMasLarga = 0;

        enlaces.forEach(function (enlace) {
            const rutaEnlace = new URL(enlace.href).pathname;

            const coincide =
                rutaActual === rutaEnlace ||
                rutaActual.startsWith(rutaEnlace + '/');

            if (coincide && rutaEnlace.length > coincidenciaMasLarga) {
                enlaceActivo = enlace;
                coincidenciaMasLarga = rutaEnlace.length;
            }
        });

        if (enlaceActivo) {
            enlaceActivo.classList.add('activo');
        }
    });
</script>
