package persistencia;
import interfaces.Fabrica;
import interfaces.IControlador;
import datatypes.Franja;
import logica.Medico;
import logica.ManejadorUsuario;
import logica.Usuario;
import datatypes.DtEstudio;
import jakarta.persistence.EntityManager;
import logica.Paciente;
import logica.Prestacion;
import logica.Seguido;
import logica.OrdenMedica;

public class PruebaConexion {

    public static void main(String[] args) {
        
        System.out.println("Iniciando prueba de conexion...");

        Conexion conexion = Conexion.getInstancia();

        IControlador controlador = Fabrica.getInstancia().getIControlador();

        System.out.println("Conexion iniciada correctamente.");

        //ManejadorUsuario manejadorUsuario = ManejadorUsuario.getInstancia();

        //Medico medico = new Medico(
        //"medico@prueba.com",
        //"Medico Prueba",
        //"clave123",
        //"Cardiologia"
//);
  //  manejadorUsuario.agregarUsuario(medico);

//    System.out.println("Medico guardado correctamente.");
   // Usuario usuarioEncontrado = manejadorUsuario.buscarUsuario("medico@prueba.com");

//if (usuarioEncontrado != null) {
   // System.out.println("Usuario encontrado correctamente: " + usuarioEncontrado.getEmail());
//} else {
 //   System.out.println("ERROR: No se encontro el usuario.");
//}
        try {
    controlador.registrarMedico(
        "medico3@prueba.com",
        "Medico Dos",
        "clave456",
        "Pediatria"
    );
    System.out.println("Medico registrado correctamente desde el controlador.");

    controlador.registrarMedico(
    "medico3@prueba.com",
    "Otro Medico",
    "otraClave",
    "Traumatologia"
    );  

    

} catch (Exception e) {
    System.out.println("ERROR: " + e.getMessage());
}
try {
    controlador.registrarPaciente(
       "pacienteSeguro@prueba.com",
        "Paciente Prueba",
        "clave789",
        "CASMU"
    );

    System.out.println("Paciente registrado correctamente desde el controlador.");

} catch (Exception e) {
    System.out.println("ERROR al registrar paciente: " + e.getMessage());
}
try {
    controlador.iniciarSesion(
       "pacienteSeguro@prueba.com",
        "clave789"
    );

    System.out.println("Inicio de sesion correcto.");

} catch (Exception e) {
    System.out.println("ERROR en inicio de sesion: " + e.getMessage());
}
try {
    controlador.iniciarSesion(
        "pacienteSeguro@prueba.com",
        "claveIncorrecta"
    );

    System.out.println("ERROR: El inicio de sesion deberia haber fallado.");

} catch (Exception e) {
    System.out.println("Inicio de sesion incorrecto detectado: " + e.getMessage());
}
Usuario usuarioSeguro =
        ManejadorUsuario.getInstancia()
                .buscarUsuario("pacienteSeguro@prueba.com");

if (usuarioSeguro != null) {
    System.out.println("Password almacenado: " + usuarioSeguro.getPassword());

    if (!usuarioSeguro.getPassword().equals("clave789")) {
        System.out.println("Password almacenado correctamente, no esta en texto plano.");
    } else {
        System.out.println("ERROR: La contraseña quedo guardada en texto plano.");
    }
}
try {
    controlador.agregarEstudio(
        "Ecografia",
        1500.0,
        Franja.MANANA,
        30
    );

    System.out.println("Estudio agregado correctamente.");

} catch (Exception e) {
    System.out.println("ERROR al agregar estudio: " + e.getMessage());
}
try {
    controlador.agregarTerapia(
        "Fisioterapia",
        2200.0,
        Franja.TARDE,
        true,
        10
    );

    System.out.println("Terapia agregada correctamente.");

} catch (Exception e) {
    System.out.println("ERROR al agregar terapia: " + e.getMessage());
}
System.out.println("Listado de prestaciones:");

controlador.listarPrestaciones().forEach(prestacion ->
    System.out.println(
        prestacion.id() + " - "
        + prestacion.nombre() + " - "
        + prestacion.precio() + " - "
        + prestacion.franja()
    )
);
try {
    controlador.modificarPrestacion(
        new DtEstudio(
            1L,
            "Ecografia abdominal",
            1800.0,
            Franja.TARDE,
            45
        )
    );

    System.out.println("Prestacion modificada correctamente.");

} catch (Exception e) {
    System.out.println("ERROR al modificar prestacion: " + e.getMessage());
}
System.out.println("Listado despues de modificar:");

controlador.listarPrestaciones().forEach(prestacion ->
    System.out.println(
        prestacion.id() + " - "
        + prestacion.nombre() + " - "
        + prestacion.precio() + " - "
        + prestacion.franja()
    )
);
try {
    controlador.eliminarPrestacion(2L);

    System.out.println("Prestacion eliminada correctamente.");

} catch (Exception e) {
    System.out.println("ERROR al eliminar prestacion: " + e.getMessage());
}
System.out.println("Listado despues de eliminar:");

controlador.listarPrestaciones().forEach(prestacion ->
    System.out.println(
        prestacion.id() + " - "
        + prestacion.nombre() + " - "
        + prestacion.precio() + " - "
        + prestacion.franja()
    )
);
try {
    EntityManager em = conexion.getEntityManager();

    Usuario usuarioSeguido =
            ManejadorUsuario.getInstancia()
                    .buscarUsuario("pacienteSeguro@prueba.com");

    Prestacion prestacionSeguido =
            logica.ManejadorPrestacion.getInstancia()
                    .buscarPrestacion(2L);

    if (usuarioSeguido instanceof Paciente paciente &&
        prestacionSeguido != null) {

        em.getTransaction().begin();

        Seguido seguido = new Seguido(
                paciente,
                prestacionSeguido
        );

        em.persist(seguido);

        em.getTransaction().commit();

        System.out.println("Seguido creado correctamente.");
    }

} catch (Exception e) {
    System.out.println("ERROR al crear seguido: " + e.getMessage());
}
try {
    EntityManager em = conexion.getEntityManager();

    Long cantidadSeguidos = em.createQuery(
            "select count(s) from Seguido s where s.prestacion.id = :idPrestacion",
            Long.class
    )
    .setParameter("idPrestacion", 2L)
    .getSingleResult();

    System.out.println("Seguidos asociados a la prestacion eliminada: " + cantidadSeguidos);

    if (cantidadSeguidos == 0) {
        System.out.println("CORRECTO: los Seguidos asociados tambien fueron eliminados.");
    } else {
        System.out.println("ERROR: quedaron Seguidos asociados.");
    }

} catch (Exception e) {
    System.out.println("ERROR al verificar Seguidos: " + e.getMessage());
}
try {
    EntityManager em = conexion.getEntityManager();

    Paciente pacienteOrden =
            (Paciente) ManejadorUsuario.getInstancia()
                    .buscarUsuario("pacienteSeguro@prueba.com");

    Prestacion prestacionOrden =
            logica.ManejadorPrestacion.getInstancia()
                    .buscarPrestacionPorNombre("Ecografia");

    if (pacienteOrden != null && prestacionOrden != null) {

        OrdenMedica orden = new OrdenMedica(pacienteOrden);

        orden.agregarLinea(prestacionOrden, 1);

        em.getTransaction().begin();
        em.persist(orden);
        em.getTransaction().commit();

        System.out.println("Orden medica creada correctamente.");
    }

} catch (Exception e) {
    System.out.println("ERROR al crear orden medica: " + e.getMessage());
}
try {
    Prestacion prestacionOrden =
            logica.ManejadorPrestacion.getInstancia()
                    .buscarPrestacionPorNombre("Ecografia");

    if (prestacionOrden != null) {
        controlador.eliminarPrestacion(prestacionOrden.getId());

        System.out.println(
                "ERROR: se elimino una prestacion que ya estaba en una orden."
        );
    }

} catch (excepciones.PrestacionEnOrdenException e) {
    System.out.println(
            "CORRECTO: no se puede eliminar una prestacion que ya fue ordenada."
    );

} catch (Exception e) {
    System.out.println(
            "ERROR inesperado al intentar eliminar prestacion ordenada: "
            + e.getMessage()
    );
}
        conexion.cerrar();

    }
}
