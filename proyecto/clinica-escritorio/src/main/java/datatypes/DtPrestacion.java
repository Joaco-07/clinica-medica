package datatypes;

public sealed interface DtPrestacion permits DtEstudio, DtTerapia {

    Long id();

    String nombre();

    double precio();

    Franja franja();
}
