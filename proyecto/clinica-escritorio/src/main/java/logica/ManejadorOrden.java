package logica;

import java.util.List;

import excepciones.PersistenciaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import persistencia.Conexion;

public class ManejadorOrden {

    private static ManejadorOrden instancia = null;

    private ManejadorOrden() {
    }

    public static ManejadorOrden getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorOrden();
        }

        return instancia;
    }

    public void agregarOrden(OrdenMedica orden) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(orden);
            em.getTransaction().commit();
        } catch (RuntimeException excepcion) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new PersistenciaException("No se pudo guardar la orden médica en la base de datos.", excepcion);
        }
    }

    public List<OrdenMedica> listarOrdenesPaciente(Paciente paciente) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            //requisito de la letra order by o.fecha desc del historial
            TypedQuery<OrdenMedica> consulta = em.createQuery("select o from OrdenMedica o where o.paciente = :paciente order by o.fecha desc", OrdenMedica.class);
            consulta.setParameter("paciente", paciente);
            return consulta.getResultList();
        } catch (RuntimeException excepcion) {
            throw new PersistenciaException("No se pudo consultar el historial de órdenes médicas.", excepcion);
        }
    }
}


