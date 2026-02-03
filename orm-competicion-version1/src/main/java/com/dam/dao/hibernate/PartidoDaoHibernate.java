package com.dam.dao.hibernate;

import com.dam.dao.PartidoDao;
import com.dam.model.Partido;
import org.hibernate.Session;
import java.util.List;

public class PartidoDaoHibernate implements PartidoDao {

    private final Session session;

    public PartidoDaoHibernate(Session session) {
        this.session = session;
    }

    @Override
    public void guardar(Partido partido) {
        session.saveOrUpdate(partido);
    }

    @Override
    public List<Partido> buscarTodos() {
        return session.createQuery("FROM Partido", Partido.class).list();
    }
}