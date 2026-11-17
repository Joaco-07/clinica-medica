
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Crear cuenta | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">
</head>

<body>

    <jsp:include page="fragmentos/navegacion.jsp"/>

    <main class="pagina-autenticacion">

        <section class="tarjeta-autenticacion">

            <header class="marca-clinica">
                <h1>Clínica</h1>
                <p>Creá tu cuenta para acceder a los servicios.</p>
            </header>

            <h2>Crear una cuenta</h2>

            <c:if test="${not empty error}">
                <div class="mensaje mensaje-error" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <form class="formulario-autenticacion"
                  action="${pageContext.request.contextPath}/registro"
                  method="post">

                <div class="campo">
                    <label for="tipo">Tipo de usuario</label>

                    <select id="tipo" name="tipo" required>
                        <option value="PACIENTE"
                            ${tipo ne 'MEDICO' ? 'selected' : ''}>
                            Paciente
                        </option>

                        <option value="MEDICO"
                            ${tipo eq 'MEDICO' ? 'selected' : ''}>
                            Médico
                        </option>
                    </select>
                </div>

                <div class="campo">
                    <label for="nombre">Nombre completo</label>

                    <input type="text"
                           id="nombre"
                           name="nombre"
                           autocomplete="name"
                           value="<c:out value='${nombre}'/>"
                           placeholder="Ingresá tu nombre"
                           required>
                </div>

                <div class="campo">
                    <label for="email">Correo electrónico</label>

                    <input type="email"
                           id="email"
                           name="email"
                           autocomplete="email"
                           value="<c:out value='${email}'/>"
                           placeholder="nombre@ejemplo.com"
                           required>
                </div>

                <div class="campo">
                    <label for="password">Contraseña</label>

                    <input type="password"
                           id="password"
                           name="password"
                           autocomplete="new-password"
                           placeholder="Creá una contraseña"
                           required>
                </div>

                <div class="campo">
                    <label for="extra" id="etiquetaExtra">
                        ${tipo eq 'MEDICO' ? 'Especialidad' : 'Mutualista'}
                    </label>

                    <input type="text"
                           id="extra"
                           name="extra"
                           value="<c:out value='${extra}'/>"
                           placeholder="${tipo eq 'MEDICO' ? 'Ingresá tu especialidad' : 'Ingresá tu mutualista'}"
                           required>

                    <small id="ayudaExtra" style="color: var(--color-texto-secundario);">
                        ${tipo eq 'MEDICO' ? 'Indicá tu especialidad médica.' : 'Indicá tu mutualista.'}
                    </small>
                </div>

                <button type="submit">Crear cuenta</button>

            </form>

            <p class="enlace-secundario">
                ¿Ya tenés una cuenta?

                <a href="${pageContext.request.contextPath}/login">
                    Iniciar sesión
                </a>
            </p>

        </section>

    </main>

    <script>
        const tipoUsuario = document.getElementById("tipo");
        const etiquetaExtra = document.getElementById("etiquetaExtra");
        const campoExtra = document.getElementById("extra");
        const ayudaExtra = document.getElementById("ayudaExtra");

        function actualizarCampoExtra() {
            const esMedico = tipoUsuario.value === "MEDICO";
            etiquetaExtra.textContent = esMedico ? "Especialidad" : "Mutualista";
            campoExtra.placeholder = esMedico ? "Ingresá tu especialidad" : "Ingresá tu mutualista";
            ayudaExtra.textContent = esMedico ? "Indicá tu especialidad médica." : "Indicá tu mutualista.";
        }

        tipoUsuario.addEventListener("change", actualizarCampoExtra);
        actualizarCampoExtra();
    </script>
</body>
</html>

