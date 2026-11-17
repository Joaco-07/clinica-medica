
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Catálogo | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">

    <script src="${pageContext.request.contextPath}/js/htmx.min.js"></script>
</head>
<body>

    <jsp:include page="fragmentos/navegacion.jsp"/>

    <main class="contenedor-pagina">

        <header class="encabezado-pagina">
            <h1>Catálogo de prestaciones</h1>
            <p>
                Explorá las prestaciones médicas disponibles.
                Podés buscar por nombre y ordenar los resultados.
            </p>
        </header>

        <section class="panel">

            <h2>Buscar prestaciones</h2>

            <form class="formulario-filtros"
                  action="${pageContext.request.contextPath}/catalogo"
                  method="get"
                  hx-get="${pageContext.request.contextPath}/catalogo"
                  hx-trigger="submit, keyup changed delay:300ms from:#busqueda, change from:#orden"
                  hx-target="#resultados-catalogo"
                  hx-swap="innerHTML">

                <div class="campo">
                    <label for="busqueda">Nombre de la prestación</label>

                    <input type="text"
                           id="busqueda"
                           name="busqueda"
                           value="<c:out value='${busqueda}'/>"
                           placeholder="Ej.: ecografía">
                </div>

                <div class="campo">
                    <label for="orden">Ordenar resultados</label>

                    <select id="orden" name="orden">
                        <option value="ALFABETICO"
                            ${orden eq 'ALFABETICO' ? 'selected' : ''}>
                            Nombre (A-Z)
                        </option>

                        <option value="PRECIO"
                            ${orden eq 'PRECIO' ? 'selected' : ''}>
                            Precio
                        </option>
                    </select>
                </div>

                <div class="acciones-formulario">
                    <button type="submit">Buscar</button>

                    <a class="boton-secundario"
                       href="${pageContext.request.contextPath}/catalogo">
                        Limpiar
                    </a>
                </div>

            </form>

        </section>

        <div id="aviso-seguidas" aria-live="polite"></div>

        <section style="margin-top: 25px;">
            <div id="resultados-catalogo">
                <jsp:include page="fragmentos/resultadosCatalogo.jsp"/>
            </div>
        </section>

    </main>

    <script>
        // Cuando el paciente sigue una prestación.
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

                    const accion = form.querySelector(
                        'input[name="accion"]'
                    );
                    const boton = form.querySelector("button");

                    if (!boton || !accion) {
                        return;
                    }

                    // La siguiente acción será dejar de seguir.
                    accion.value = "quitar";

                    boton.type = "submit";
                    boton.disabled = false;
                    boton.className = "boton-dejar-seguir";
                    boton.textContent = "Dejar de seguir";

                    // Mostrar el cartel verde.
                    if (!form.querySelector(".aviso-ya-seguida")) {
                        const aviso = document.createElement("div");

                        aviso.className = "aviso-ya-seguida";
                        aviso.textContent =
                            "✓ Ya seguís esta prestación";

                        form.insertBefore(aviso, boton);
                    }
                });
            }
        );

        // Cuando el paciente deja de seguir una prestación.
        document.body.addEventListener(
            "prestacionSeguidasQuitada",
            function (event) {
                const id = String(event.detail.id);

                document.querySelectorAll(
                    "form[data-prestacion-id]"
                ).forEach(function (form) {
                    if (form.dataset.prestacionId !== id) {
                        return;
                    }

                    const accion = form.querySelector(
                        'input[name="accion"]'
                    );
                    const boton = form.querySelector("button");

                    if (!accion || !boton) {
                        return;
                    }

                    // Eliminar el cartel verde.
                    const aviso = form.querySelector(
                        ".aviso-ya-seguida"
                    );

                    if (aviso) {
                        aviso.remove();
                    }

                    // La siguiente acción será seguir.
                    accion.value = "agregar";

                    boton.type = "submit";
                    boton.disabled = false;
                    boton.className = "boton-seguir";
                    boton.textContent = "♡ Seguir prestación";
                });
            }
        );
    </script>

</body>
</html>

