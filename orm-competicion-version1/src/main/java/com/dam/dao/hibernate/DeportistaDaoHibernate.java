package com.dam.dao.hibernate;

import com.dam.dao.DeportistaDao;
import com.dam.model.Deportista;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class DeportistaDaoHibernate implements DeportistaDao {

    private final Session session;

    public DeportistaDaoHibernate(Session session) {
        this.session = session;
    }

    @Override
    public void guardar(Deportista deportista) {
        session.saveOrUpdate(deportista);
    }

    @Override
    public Deportista buscarPorId(Long id) {
        return session.get(Deportista.class, id);
    }

    @Override
    public List<Deportista> buscarTodos() {
        return session.createQuery("FROM Deportista", Deportista.class).list();
    }

    @Override
    public List<Deportista> buscarPorEquipo(Long idEquipo) {
        Query<Deportista> query = session.createQuery("FROM Deportista d WHERE d.equipo.id = :idEquipo", Deportista.class);
        query.setParameter("idEquipo", idEquipo);
        return query.list();
    }

    @Override
    public void eliminar(Deportista deportista) {
        session.delete(deportista);
    }
}