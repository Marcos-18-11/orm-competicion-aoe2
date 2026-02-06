package com.dam.dao.hibernate;

import com.dam.dao.EquipoDao;
import com.dam.model.Equipo;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
// CRUD sobre entidad Equipo
public class EquipoDaoHibernate implements EquipoDao {

    private final Session session; // abrimos sesión

    // cONSTRUCTOR DE LA CLASE QUE RECIBE UNA SESIÓN
    public EquipoDaoHibernate(Session session) {
        this.session = session;
    }

    @Override
    public void guardar(Equipo equipo) {
        // saveOrUpdate es exclusivo de Hibernate nativo (muy cómodo)
        session.saveOrUpdate(equipo);
    }
    /*
     * Busca   equipo por atributo identificador.
     *  @param idEquipo ID del equipo.
     *  @return equipo.
     */
    @Override
    public Equipo buscarPorId(Long id) {
        return session.get(Equipo.class, id);
    }

    /*
     * Busca  todos los equipos
     *  @return todos los equipos.
     */

    @Override
    public List<Equipo> buscarTodos() {
        // HQL es casi igual a JPQL. .list() devuelve la lista directamente.
        return session.createQuery("FROM Equipo", Equipo.class).list();
    }
    /*
     * equipo de AoE que se retira de liga profesional
     *  @param equipo

     */
    @Override
    public void eliminar(Equipo equipo) {
        session.delete(equipo);
    }

    /*
     * Busca equipo por nombre
     *  @param  equipo.
     *  @return  equipo.
     */

    @Override
    public Equipo buscarPorNombre(String nombre) {
        Query<Equipo> query = session.createQuery("FROM Equipo WHERE nombre = :nombre", Equipo.class);
        query.setParameter("nombre", nombre);
        return query.uniqueResult(); // uniqueResult es nativo de Hibernate
    }
}