package persistencia;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import excepciones.PersistenciaException;


public class Conexion {

    private static Conexion instancia = null;   //Es la única instancia de Conexion que va a existir.
                                                // Esto se hace porque Conexion utiliza el patrón Singleton.
    private final EntityManagerFactory emf;
    private final EntityManager em;

    private Conexion() {
        // Hibernate escribe mucha informacion tecnica al arrancar: dejamos solo los errores.
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        try {
            emf = Persistence.createEntityManagerFactory("clinica");
            em = emf.createEntityManager();
        } catch (RuntimeException excepcion) {
            throw new PersistenciaException("No se pudo conectar con la base de datos. Verificá que Docker Desktop esté abierto y ejecutá 'docker compose up -d'.", excepcion);
        }
    }

    public static Conexion getInstancia() {
        if (instancia == null)
            instancia = new Conexion();
        return instancia;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void cerrar() {
        em.close();
        emf.close();
    }
}
