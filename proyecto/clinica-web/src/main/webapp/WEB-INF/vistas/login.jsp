
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=3">
</head>
<body>

    <jsp:include page="fragmentos/navegacion.jsp"/>

    <main class="pagina-autenticacion">

        <section class="tarjeta-autenticacion">

            <header class="marca-clinica">
                <h1>Clínica</h1>
                <p>Ingresá a tu cuenta para continuar.</p>
            </header>

            <h2>Iniciar sesión</h2>

            <c:if test="${sessionScope.registroExitoso}">
                <div class="mensaje mensaje-exito" role="status">
                    Cuenta creada correctamente.
                    Ya podés iniciar sesión.
                </div>

                <c:remove var="registroExitoso" scope="session"/>
            </c:if>

            <c:if test="${not empty error}">
                <div class="mensaje mensaje-error" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <form class="formulario-autenticacion"
                  action="${pageContext.request.contextPath}/login"
                  method="post">

                <div class="campo">
                    <label for="email">Correo electrónico</label>

                    <input type="email"
                           id="email"
                           name="email"
                           autocomplete="email"
                           placeholder="nombre@ejemplo.com"
                           required>
                </div>

                <div class="campo">
                    <label for="password">Contraseña</label>

                    <div class="contenedor-password">
                        <input type="password"
                               id="password"
                               name="password"
                               autocomplete="current-password"
                               placeholder="Ingresá tu contraseña"
                               required>

                        <button type="button"
                            id="mostrarPassword"
                            class="boton-mostrar-password"
                            aria-label="Mostrar contraseña"
                            aria-pressed="false">
                        Mostrar
                    </button>
                    </div>
                </div>

                <button type="submit">Ingresar</button>

            </form>

            <p class="enlace-secundario">
                ¿Todavía no tenés una cuenta?

                <a href="${pageContext.request.contextPath}/registro">
                    Crear una cuenta
                </a>
            </p>

        </section>

    </main>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const campoPassword = document.getElementById("password");
            const botonMostrar = document.getElementById("mostrarPassword");

            if (!campoPassword || !botonMostrar) return;

            botonMostrar.addEventListener("click", function () {
                const mostrar = campoPassword.type === "password";

                campoPassword.type = mostrar ? "text" : "password";
                botonMostrar.textContent = mostrar ? "Ocultar" : "Mostrar";

                botonMostrar.setAttribute(
                    "aria-label",
                    mostrar ? "Ocultar contraseña" : "Mostrar contraseña"
                );

                botonMostrar.setAttribute("aria-pressed", String(mostrar));
            });
        });
    </script>
</body>
</html>
