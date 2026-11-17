package logica;

import java.util.List;

import excepciones.PersistenciaException;
import excepciones.SeguidoRepetidoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import persistencia.Conexion;


public class ManejadorSeguido {

    private static ManejadorSeguido instancia = null;

    private ManejadorSeguido() {
    }

    public static ManejadorSeguido getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorSeguido();
        }
        return instancia;
    }

    public void agregarSeguido(String emailPaciente, Long idPrestacion)
            throws SeguidoRepetidoException {

        EntityManager em = Conexion.getInstancia().getEntityManager();

        if (emailPaciente == null || emailPaciente.isBlank() || idPrestacion == null || idPrestacion <= 0) {
            throw new IllegalArgumentException("El paciente y el identificador de la prestación deben ser válidos.");
        }
        Paciente paciente = em.find(Paciente.class, emailPaciente);
        Prestacion prestacion = em.find(Prestacion.class, idPrestacion);
        if (paciente == null) {
            throw new IllegalArgumentException("No se encontró el paciente.");
        }
        if (prestacion == null) {
            throw new IllegalArgumentException("La prestación ya no está disponible.");
        }

        TypedQuery<Seguido> consulta = em.createQuery(
                "select s from Seguido s " +
                "where s.paciente.email = :email " +
                "and s.prestacion.id = :id",
                Seguido.class
        );

        consulta.setParameter("email", emailPaciente);
        consulta.setParameter("id", idPrestacion);

        boolean existe = !consulta.getResultList().isEmpty();

        if (existe) {
            throw new SeguidoRepetidoException(
                    "El paciente ya tiene esta prestación en seguidas."
            );
        }

        Seguido seguido = new Seguido(paciente, prestacion);

        try {
            em.getTransaction().begin();
            em.persist(seguido);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.clear();
            throw new PersistenciaException("No se pudo guardar la prestación seguida.", e);
        }
    }

    public void quitarSeguido(String emailPaciente, Long idPrestacion) {

        EntityManager em = Conexion.getInstancia().getEntityManager();

        TypedQuery<Seguido> consulta = em.createQuery(
                "select s from Seguido s " +
                "where s.paciente.email = :email " +
                "and s.prestacion.id = :id",
                Seguido.class
        );

        consulta.setParameter("email", emailPaciente);
        consulta.setParameter("id", idPrestacion);

        Seguido seguido = consulta.getResultStream()
                .findFirst()
                .orElse(null);

        if (seguido == null) {
            return;
        }

        try {
            em.getTransaction().begin();
            em.remove(seguido);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.clear();
            throw new PersistenciaException("No se pudo quitar la prestación seguida.", e);
        }
    }

    public List<Seguido> listarSeguidas(String emailPaciente) {

        EntityManager em = Conexion.getInstancia().getEntityManager();

        TypedQuery<Seguido> consulta = em.createQuery(
                "select s from Seguido s " +
                "where s.paciente.email = :email " +
                "order by s.prestacion.nombre",
                Seguido.class
        );

        consulta.setParameter("email", emailPaciente);

        return consulta.getResultList();
    }
}
