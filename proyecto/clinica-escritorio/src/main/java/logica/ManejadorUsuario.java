package logica;

import java.util.List;

import excepciones.PersistenciaException;
import jakarta.persistence.EntityManager;
import persistencia.Conexion;

public class ManejadorUsuario {

    private static ManejadorUsuario instancia = null;

    private ManejadorUsuario() {
    }

    public static ManejadorUsuario getInstancia() {
        if (instancia == null)
            instancia = new ManejadorUsuario();
        return instancia;
    }

    public void agregarUsuario(Usuario usuario) {
        EntityManager em = Conexion.getInstancia().getEntityManager(); //Conexion.getInstancia() ->Obtiene la única instancia de tu clase Conexion, porque Conexion está implementada como Singleton.
                                                                       //De esa conexión obtenemos el EntityManager
        try {
            em.getTransaction().begin();         //Empieza una operación que modifica la base de datos
            em.persist(usuario);        //Quiero que este objeto usuario sea persistido en la base de datos
            em.getTransaction().commit();   //La operación se confirma en la base de datos
        } catch (RuntimeException excepcion) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new PersistenciaException("No se pudo guardar el usuario en la base de datos.", excepcion);
        }
    }



    public Usuario buscarUsuario(String email) {
        try {
            return Conexion.getInstancia().getEntityManager().find(Usuario.class, email);
        } catch (PersistenciaException excepcion) {
            throw excepcion;
        } catch (RuntimeException excepcion) {
            throw new PersistenciaException("No se pudo consultar el usuario en la base de datos.", excepcion);
        }
    }

    public List<String> listarEmails() {
        try {
            return Conexion.getInstancia().getEntityManager()
                    .createQuery("select u.email from Usuario u order by u.email", String.class)
                    .getResultList();
        } catch (PersistenciaException excepcion) {
            throw excepcion;
        } catch (RuntimeException excepcion) {
            throw new PersistenciaException("No se pudieron consultar los usuarios en la base de datos.", excepcion);
        }
    }
}
