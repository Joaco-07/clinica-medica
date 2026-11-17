package datatypes;

public enum Franja {

    MANANA("Mañana"),
    TARDE("Tarde"),
    NOCHE("Noche");

    private final String texto;

    Franja(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
