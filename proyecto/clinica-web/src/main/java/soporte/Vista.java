package soporte;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Vista {

    private Vista() {
    }

    public static boolean esHtmx(HttpServletRequest request) {
        return "true".equals(request.getHeader("HX-Request"));
    }

    public static void pagina(
            HttpServletRequest request,
            HttpServletResponse response,
            String jsp)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/vistas/" + jsp)
                .forward(request, response);
    }

    public static void fragmento(
            HttpServletRequest request,
            HttpServletResponse response,
            String jsp)
            throws ServletException, IOException {

        pagina(request, response, jsp);
    }

    public static void aviso(
            HttpServletRequest request,
            String texto,
            boolean esError) {

        request.setAttribute("aviso", texto);
        request.setAttribute("avisoError", esError);
    }
}