
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis prestaciones seguidas | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">

    <script src="${pageContext.request.contextPath}/js/htmx.min.js"></script>
</head>
<body>

    <jsp:include page="/WEB-INF/vistas/fragmentos/navegacion.jsp"/>

    <main class="contenedor-pagina">

        <header class="encabezado-pagina">
            <h1>Mis prestaciones seguidas</h1>
            <p>
                Consultá las prestaciones que seguís y dejá de seguirlas
                cuando quieras.
            </p>
        </header>

        <section class="panel">
            <jsp:include page="/WEB-INF/vistas/fragmentos/listaSeguidas.jsp"/>
        </section>

        <div class="acciones-formulario" style="margin-top: 24px;">
            <a class="boton-secundario"
            href="${pageContext.request.contextPath}/catalogo">
                Volver al catálogo de prestaciones
            </a>
        </div>

    </main>

</body>
</html>

