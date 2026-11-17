<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Modificar prestación | Clínica</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=2">

    <style>
        .modificar-pagina {
            width: min(680px, calc(100% - 32px));
            margin: 36px auto 56px;
        }

        .modificar-encabezado {
            margin-bottom: 24px;
        }

        .modificar-descripcion {
            color: var(--color-texto-secundario);
        }

        .modificar-panel {
            padding: 28px;
        }

        .modificar-acciones {
            display: flex;
            justify-content: center;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 28px;
        }

        .modificar-acciones > * {
            min-width: 150px;
            text-align: center;
        }

        .modificar-nota {
            margin-top: 20px;
            padding: 14px 16px;
            border-radius: 10px;
            background: #f1f7fc;
            color: var(--color-texto-secundario);
            font-size: 0.92rem;
        }

        @media (max-width: 520px) {
            .modificar-panel {
                padding: 20px;
            }

            .modificar-acciones {
                flex-direction: column;
            }

            .modificar-acciones > * {
                width: 100%;
                box-sizing: border-box;
            }
        }
    </style>
</head>

<body>
    <jsp:include page="/WEB-INF/vistas/fragmentos/navegacion.jsp"/>

    <main class="modificar-pagina">

        <header class="modificar-encabezado">
            <h1>Modificar prestación</h1>
            <p class="modificar-descripcion">
                Actualizá el nombre o el precio de la prestación seleccionada.
            </p>
        </header>

        <c:if test="${not empty error}">
            <div class="mensaje mensaje-error" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <section class="panel modificar-panel">

            <p>
                <strong>Identificador:</strong>
                <c:out value="${prestacion.id}"/>
            </p>

            <form action="${pageContext.request.contextPath}/prestaciones/modificar"
                  method="post">

                <input type="hidden"
                       name="accion"
                       value="modificar">

                <input type="hidden"
                       name="id"
                       value="<c:out value='${prestacion.id}'/>">

                <div class="campo">
                    <label for="nombre">Nombre de la prestación</label>

                    <input type="text"
                           id="nombre"
                           name="nombre"
                           value="<c:out value='${prestacion.nombre}'/>"
                           maxlength="150"
                           required>
                </div>

                <div class="campo">
                    <label for="precio">Precio</label>

                    <input type="number"
                           id="precio"
                           name="precio"
                           value="${prestacion.precio}"
                           min="0"
                           step="0.01"
                           required>
                </div>

                <div class="modificar-nota">
                    El tipo y los demás datos específicos de la prestación
                    se mantienen sin cambios.
                </div>

                <div class="modificar-acciones">
                    <button type="submit">
                        Guardar cambios
                    </button>

                    <a class="boton-secundario"
                       href="${pageContext.request.contextPath}/prestaciones">
                        Cancelar
                    </a>
                </div>

            </form>
        </section>
    </main>
</body>
</html>

