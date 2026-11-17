<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not peticionHtmx}">
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Prestaciones | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">
</head>
<body>

<jsp:include page="/WEB-INF/vistas/fragmentos/navegacion.jsp"/>

<main class="contenedor-pagina">

    <header class="encabezado-pagina">
        <h1>Prestaciones de la clínica</h1>
        <p>Consultá los estudios y las terapias disponibles.</p>
    </header>


    <c:if test="${sessionScope.usuario.tipo == 'MEDICO'}">
        <section class="panel" style="margin-bottom: 24px;">
            <h2>Administración</h2>
            <p>
                Desde aquí podés registrar nuevas prestaciones para la clínica.
            </p>

            <a class="boton"
               href="${pageContext.request.contextPath}/prestaciones/nueva">
                + Agregar prestación
            </a>
        </section>
    </c:if>

    <section class="panel" style="margin-bottom: 24px;">
        <h2>Buscar prestaciones</h2>

        <form action="${pageContext.request.contextPath}/prestaciones"
              method="get"
              class="formulario-filtros"
              id="formularioFiltros"
              hx-get="${pageContext.request.contextPath}/prestaciones"
              hx-trigger="input changed delay:400ms from:#busqueda, change from:#orden"
              hx-target="#resultadoPrestaciones"
              hx-swap="innerHTML">

            <div class="campo">
                <label for="busqueda">Buscar por nombre</label>

                <input type="text"
                       id="busqueda"
                       name="busqueda"
                       value="<c:out value='${busqueda}'/>"
                       placeholder="Escribí el nombre de una prestación...">
            </div>

            <div class="campo">
                <label for="orden">Ordenar por</label>

                <select id="orden" name="orden">
                    <option value="ALFABETICO"
                        ${orden == 'ALFABETICO' ? 'selected' : ''}>
                        Nombre (A-Z)
                    </option>

                    <option value="PRECIO"
                        ${orden == 'PRECIO' ? 'selected' : ''}>
                        Precio
                    </option>
                </select>
            </div>

            <div class="acciones-formulario">
                <button type="submit">Buscar</button>

                <a class="boton-secundario"
                   href="${pageContext.request.contextPath}/prestaciones">
                    Limpiar filtros
                </a>
            </div>
        </form>
    </section>

    <section>
        <header class="encabezado-pagina">
            <h2>Prestaciones disponibles</h2>
        </header>

</c:if>

<c:if test="${not peticionHtmx}">
<div id="resultadoPrestaciones">
</c:if>

    <c:if test="${not empty error}">
        <div class="mensaje mensaje-error" role="alert">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty prestaciones}">
            <div class="panel">
                <p>No hay prestaciones para mostrar.</p>
            </div>
        </c:when>

        <c:otherwise>
            <div class="tabla-contenedor panel">
                <table>
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Precio</th>
                            <th>Tipo</th>
                            <th>Detalle</th>

                            <c:if test="${sessionScope.usuario.tipo == 'MEDICO'}">
                                <th>Modificar</th>
                                <th>Eliminar</th>
                            </c:if>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="p" items="${prestaciones}">
                            <tr>
                                <td>
                                    <strong>
                                        <c:out value="${p.nombre}"/>
                                    </strong>
                                </td>

                                <td>
                                    $ <c:out value="${p.precio}"/>
                                </td>

                                <td>
                                    <span class="etiqueta-tipo">
                                        <c:out value="${p.tipo}"/>
                                    </span>
                                </td>

                                <td>
                                    <a class="boton-secundario"
                                       href="${pageContext.request.contextPath}/prestaciones?accion=detalle&amp;id=${p.id}">
                                        Ver detalle
                                    </a>
                                </td>

                                <c:if test="${sessionScope.usuario.tipo == 'MEDICO'}">
                                    <td>
                                        <a class="boton-secundario"
                                           href="${pageContext.request.contextPath}/prestaciones/modificar?id=${p.id}">
                                            Modificar
                                        </a>
                                    </td>

                                    <td>
                                        <button type="button"
                                                class="boton-peligro"
                                                onclick="abrirConfirmacionEliminar(this)"
                                                data-id="${p.id}"
                                                data-nombre="<c:out value='${p.nombre}'/>">
                                            Eliminar
                                        </button>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

<c:if test="${not peticionHtmx}">
</div>
    </section>
</main>

<div id="modalEliminar"
     class="modal-confirmacion"
     hidden>

    <div class="modal-contenido"
         role="dialog"
         aria-modal="true"
         aria-labelledby="tituloModalEliminar">

        <h2 id="tituloModalEliminar">
            Eliminar prestación
        </h2>

        <p id="textoModalEliminar">
            ¿Seguro que deseas eliminar esta prestación?
        </p>

        <form id="formEliminar"
              action="${pageContext.request.contextPath}/prestaciones"
              method="post">

            <input type="hidden"
                   name="accion"
                   value="eliminar">

            <input type="hidden"
                   name="id"
                   id="idPrestacionEliminar">

            <div class="acciones-formulario">
                <button type="button"
                        class="boton-secundario"
                        onclick="cerrarConfirmacionEliminar()">
                    Cancelar
                </button>

                <button type="submit" class="boton-peligro">
                    Sí, eliminar
                </button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/htmx.min.js"></script>

<script>
    const modalEliminar = document.getElementById("modalEliminar");
    const idEliminar = document.getElementById("idPrestacionEliminar");
    const textoEliminar = document.getElementById("textoModalEliminar");

    function abrirConfirmacionEliminar(boton) {
        idEliminar.value = boton.dataset.id;

        textoEliminar.textContent =
            '¿Seguro que deseas eliminar "' +
            boton.dataset.nombre +
            '"? Esta acción no se puede deshacer.';

        modalEliminar.hidden = false;
    }

    function cerrarConfirmacionEliminar() {
        modalEliminar.hidden = true;
    }

    modalEliminar.addEventListener("click", function (evento) {
        if (evento.target === modalEliminar) {
            cerrarConfirmacionEliminar();
        }
    });

    document.addEventListener("keydown", function (evento) {
        if (evento.key === "Escape" && !modalEliminar.hidden) {
            cerrarConfirmacionEliminar();
        }
    });
</script>

</body>
</html>
</c:if>
