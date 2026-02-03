package com.dam.dao.hibernate;

import com.dam.dao.EquipoDao;
import com.dam.model.Equipo;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class EquipoDaoHibernate implements EquipoDao {

    private final Session session;

    public EquipoDaoHibernate(Session session) {
        this.session = session;
    }

    @Override
    public void guardar(Equipo equipo) {
        // saveOrUpdate es exclusivo de Hibernate nativo (muy cómodo)
        session.saveOrUpdate(equipo);
    }

    @Override
    public Equipo buscarPorId(Long id) {
        return session.get(Equipo.class, id);
    }

    @Override
    public List<Equipo> buscarTodos() {
        // HQL es casi igual a JPQL. .list() devuelve la lista directamente.
        return session.createQuery("FROM Equipo", Equipo.class).list();
    }

    @Override
    public void eliminar(Equipo equipo) {
        session.delete(equipo);
    }

    @Override
    public Equipo buscarPorNombre(String nombre) {
        Query<Equipo> query = session.createQuery("FROM Equipo WHERE nombre = :nombre", Equipo.class);
        query.setParameter("nombre", nombre);
        return query.uniqueResult(); // uniqueResult es nativo de Hibernate
    }
}