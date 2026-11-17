
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Agregar prestación | Clínica</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">
</head>
<body>

    <jsp:include page="/WEB-INF/vistas/fragmentos/navegacion.jsp"/>

    <main class="contenedor-pagina">

        <header class="encabezado-pagina">
            <h1>Agregar prestación</h1>
            <p>Registrá un estudio o una terapia en la clínica.</p>
        </header>

        <c:if test="${not empty error}">
            <div class="mensaje mensaje-error" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <section class="panel">
            <form id="formPrestacion"
                  action="${pageContext.request.contextPath}/prestaciones/nueva"
                  method="post"
                  class="formulario-autenticacion">

                <input type="hidden"
                       id="accion"
                       name="accion"
                       value="agregarEstudio">

                <div class="campo">
                    <label for="tipoPrestacion">Tipo de prestación</label>
                    <select id="tipoPrestacion" name="tipoPrestacion" required>
                        <option value="ESTUDIO"
                            ${tipoPrestacion ne 'TERAPIA' ? 'selected' : ''}>
                            Estudio
                        </option>
                        <option value="TERAPIA"
                            ${tipoPrestacion eq 'TERAPIA' ? 'selected' : ''}>
                            Terapia
                        </option>
                    </select>
                </div>

                <div class="campo">
                    <label for="nombre">Nombre</label>
                    <input type="text"
                           id="nombre"
                           name="nombre"
                           maxlength="150"
                           required>
                </div>

                <div class="campo">
                    <label for="precio">Precio</label>
                    <input type="number"
                           id="precio"
                           name="precio"
                           min="0"
                           step="0.01"
                           required>
                </div>

                <div class="campo">
                    <label for="franja">Franja horaria</label>
                    <select id="franja" name="franja" required>
                        <option value="MANANA">Mañana</option>
                        <option value="TARDE">Tarde</option>
                        <option value="NOCHE">Noche</option>
                    </select>
                </div>

                <div id="camposEstudio" class="campo">
                    <label for="duracionMinutos">Duración (minutos)</label>
                    <input type="number"
                           id="duracionMinutos"
                           name="duracionMinutos"
                           min="1"
                           required>
                </div>

                <div id="camposTerapia" hidden>
                    <div class="campo">
                        <label for="requiereDerivacion">
                            ¿Requiere derivación?
                        </label>
                        <select id="requiereDerivacion"
                                name="requiereDerivacion"
                                disabled>
                            <option value="true">Sí</option>
                            <option value="false" selected>No</option>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="cantidadSesiones">
                            Cantidad de sesiones
                        </label>
                        <input type="number"
                               id="cantidadSesiones"
                               name="cantidadSesiones"
                               min="1"
                               disabled>
                    </div>
                </div>

                <div class="acciones-formulario">
                    <button type="submit">Guardar prestación</button>

                    <a class="boton-secundario"
                       href="${pageContext.request.contextPath}/prestaciones">
                        Cancelar
                    </a>
                </div>

            </form>
        </section>
    </main>

    <script>
        const tipo = document.getElementById("tipoPrestacion");
        const accion = document.getElementById("accion");
        const camposEstudio = document.getElementById("camposEstudio");
        const camposTerapia = document.getElementById("camposTerapia");

        const duracion = document.getElementById("duracionMinutos");
        const derivacion = document.getElementById("requiereDerivacion");
        const sesiones = document.getElementById("cantidadSesiones");

        function actualizarFormulario() {
            const esEstudio = tipo.value === "ESTUDIO";

            accion.value = esEstudio
                ? "agregarEstudio"
                : "agregarTerapia";

            camposEstudio.hidden = !esEstudio;
            camposTerapia.hidden = esEstudio;

            duracion.disabled = !esEstudio;
            duracion.required = esEstudio;

            derivacion.disabled = esEstudio;
            derivacion.required = !esEstudio;

            sesiones.disabled = esEstudio;
            sesiones.required = !esEstudio;
        }

        tipo.addEventListener("change", actualizarFormulario);

        actualizarFormulario();
    </script>

</body>
</html>

