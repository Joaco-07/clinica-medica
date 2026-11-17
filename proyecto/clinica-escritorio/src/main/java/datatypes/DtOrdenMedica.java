package datatypes;

import java.time.LocalDateTime;
import java.util.List;

public record DtOrdenMedica(
        Long id,
        LocalDateTime fecha,
        List<DtLineaOrden> lineas) {

    public double total() {
        return lineas.stream()
                .mapToDouble(DtLineaOrden::subtotal)
                .sum();
    }
}
