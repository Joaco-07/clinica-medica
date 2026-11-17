package presentacion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import datatypes.DtEstudio;
import datatypes.DtPrestacion;
import datatypes.DtTerapia;

/** Define como se presentan precios, fechas y prestaciones en pantalla. */
public final class Formato {

    private static final Locale URUGUAY = Locale.forLanguageTag("es-UY");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", URUGUAY);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", URUGUAY);

    private Formato() {
    }

    public static String precio(double precio) {
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(URUGUAY));
        return "$ " + formato.format(precio);
    }

    public static String tipo(DtPrestacion prestacion) {
        if (prestacion instanceof DtEstudio) {
            return "Estudio";
        }
        if (prestacion instanceof DtTerapia) {
            return "Terapia";
        }
        return "Prestación";
    }

    public static String detalle(DtPrestacion prestacion) {
        if (prestacion instanceof DtEstudio estudio) {
            return estudio.duracionMinutos() + " min de duración";
        }
        if (prestacion instanceof DtTerapia terapia) {
            String derivacion = terapia.requiereDerivacion() ? "requiere derivación" : "no requiere derivación";
            return terapia.cantidadSesiones() + " sesiones · " + derivacion;
        }
        return "Sin detalles adicionales";
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "—" : fecha.format(FECHA_HORA);
    }
}
