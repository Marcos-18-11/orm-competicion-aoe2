package com.dam.dao.hibernate;

import com.dam.dao.PatrocinadorDao;
import com.dam.model.Patrocinador;
import org.hibernate.Session;
import java.util.List;

public class PatrocinadorDaoHibernate implements PatrocinadorDao {

    private final Session session;

    public PatrocinadorDaoHibernate(Session session) {
        this.session = session;
    }

    @Override
    public void guardar(Patrocinador patrocinador) {
        session.saveOrUpdate(patrocinador);
    }

    @Override
    public Patrocinador buscarPorId(Long id) {
        return session.get(Patrocinador.class, id);
    }

    @Override
    public List<Patrocinador> buscarTodos() {
        return session.createQuery("FROM Patrocinador", Patrocinador.class).list();
    }

    @Override
    public void eliminar(Patrocinador patrocinador) {
        session.delete(patrocinador);
    }
}
