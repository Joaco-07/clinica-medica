package datatypes;

public sealed interface DtUsuario permits DtMedico, DtPaciente {

    String email();

    String nombre();
}
