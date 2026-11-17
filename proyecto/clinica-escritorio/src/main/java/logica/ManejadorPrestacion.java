package logica;

import java.util.List;

import datatypes.DtPrestacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import persistencia.Conexion;

// Las cosas que no estan comentadas son practicamente iguales a las de ManejadorUsuario q hizo euge, solo q adaptadas a Prestacion
public class ManejadorPrestacion {

    private static ManejadorPrestacion instancia = null;

    private ManejadorPrestacion() {
    }

    public static ManejadorPrestacion getInstancia() {
        if (instancia == null)
            instancia = new ManejadorPrestacion();
        return instancia;
    }

    public void agregarPrestacion(Prestacion prestacion) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        em.getTransaction().begin();
        em.persist(prestacion);
        em.getTransaction().commit();
    }

    public void actualizarPrestacion(Prestacion prestacion) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        em.getTransaction().begin();
        em.merge(prestacion);
        em.getTransaction().commit();
    }

    public void eliminarPrestacion(Prestacion prestacion) { 
        EntityManager em = Conexion.getInstancia().getEntityManager(); // Trae eñ EntityManager q permite trabajar con la base de datos
        em.getTransaction().begin(); // Comienza una transaccion para q todas las eliminaciones se confirmen juntas
        Query consulta = em.createQuery("delete from Seguido s where s.prestacion = :prestacion"); // Crea una consulta q elimina los asociados a la prestacion
        consulta.setParameter("prestacion", prestacion); // Cambia el parametro prestacion por la prestacion q nos dan
        consulta.executeUpdate(); // Ejecuta la eliminacion 
        em.remove(prestacion); // Elimina la prestacion despues de haber eliminado sus seguidos
        em.getTransaction().commit(); // Confirma la transaccion y guarda definitivamente las eliminaciones
    }

    public Prestacion buscarPrestacion(Long id) {
        return Conexion.getInstancia().getEntityManager().find(Prestacion.class, id);
    }

    public Prestacion buscarPrestacionPorNombre(String nombre) { // Busca una prestacion utilizando su nombre
        EntityManager em = Conexion.getInstancia().getEntityManager(); 
        TypedQuery<Prestacion> consulta = em.createQuery("select p from Prestacion p where p.nombre = :nombre", Prestacion.class); // Crea una consulta para buscar prestaciones con el nombre 
        consulta.setParameter("nombre", nombre); // Cambiaa el parametro nombre por el texto q nos da
        return consulta.getResultStream().findFirst().orElse(null); // Devuelve la primera coincidencia o null cuando no existe ninguna
    }

    public List<Prestacion> listarPrestaciones() { // Obtiene todas las prestaciones guardadas.
        EntityManager em = Conexion.getInstancia().getEntityManager(); 
        TypedQuery<Prestacion> consulta = em.createQuery("select p from Prestacion p", Prestacion.class); //Consulta que selecciona todas las prestaciones
        return consulta.getResultList(); // Ejecuta la consulta y devuelve el resultado como una lista
    }

    // Es necesaria para antes de eliminar una prestacion, para ver si esta fue ordenada alguna vez, si fue ordenada no se puede eliminar
    public boolean fueOrdenada(Prestacion prestacion) { // Ve si una prestacion aparece en alguna linea de orden
        EntityManager em = Conexion.getInstancia().getEntityManager(); 
        TypedQuery<Long> consulta = em.createQuery("select count(l) from LineaOrden l where l.prestacion = :prestacion", Long.class); // Crea una consulta que cuenta las lineas que contienen la prestacion
        consulta.setParameter("prestacion", prestacion); // Cambia el parametro de prestacion por la prestacion recibida
        Long cantidadLineas = consulta.getSingleResult(); // Ejecuta la consulta y guarda la cantidad de lineas encontradas
        return cantidadLineas > 0; // Devuelve true si aparece al menos una vez y false si nunca fue ordenada
    }



//Opcionales

//2 Ordenar el catálogo por precio y alfabéticamente
public List<Prestacion> listarPrestacionesPorPrecio() {
    EntityManager em = Conexion.getInstancia().getEntityManager();

    TypedQuery<Prestacion> consulta = em.createQuery("select p from Prestacion p order by p.precio, lower(p.nombre)", Prestacion.class);
    return consulta.getResultList();
}

public List<Prestacion> listarPrestacionesAlfabeticamente() {
    EntityManager em = Conexion.getInstancia().getEntityManager();

    TypedQuery<Prestacion> consulta = em.createQuery("select p from Prestacion p order by lower(p.nombre), p.precio", Prestacion.class);
    return consulta.getResultList();
}

//3 Buscar prestaciones por parte del nombre, sin distinguir mayúsculas

public List<Prestacion> buscarPrestacionesPorParteNombre(String nombre) {
    EntityManager em = Conexion.getInstancia().getEntityManager();

    //Busco en la tabla, 
    // lower(p.nombre) convierte el nombre de la prestación a minúsculas 
    // lower(:nombre) convierte en minuscula lo que entra por parametro 
    // el operador like busca una parte del nombre que coincide
    TypedQuery<Prestacion> consulta = em.createQuery(
        "select p from Prestacion p " +
        "where lower(p.nombre) like lower(:nombre) " +
        "order by p.nombre",
        Prestacion.class
    );

    consulta.setParameter("nombre", "%" + nombre + "%");

    return consulta.getResultList();
}


//4 Ver el detalle de una prestación, con los datos propios de su tipo

public DtPrestacion obtenerDetallePrestacion(Long id) {
    Prestacion prestacion = buscarPrestacion(id); //Busco la prestacion

    if (prestacion == null) {
        return null;
    }

    return prestacion.getDtPrestacion(); //Si existe la prestacion retorno el datatype con los datos
}
}
