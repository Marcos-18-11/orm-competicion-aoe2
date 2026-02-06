package com.dam.dao.hibernate;

import com.dam.dao.PatrocinadorDao;
import com.dam.model.Patrocinador;
import org.hibernate.Session;
import java.util.List;
// CRUD sobre patrocinadores
public class PatrocinadorDaoHibernate implements PatrocinadorDao {

    private final Session session;

    //Constructor con parámetro de sesión
    public PatrocinadorDaoHibernate(Session session) {
        this.session = session;
    }
    // @param patrocinador; a registrar como uno de los patrocinadores oficiales
    @Override
    public void guardar(Patrocinador patrocinador) {
        session.saveOrUpdate(patrocinador);
    }

    //@param id d e patrocinador
    // @return patrocinador requerido
    @Override
    public Patrocinador buscarPorId(Long id) {
        return session.get(Patrocinador.class, id);
    }

    //@return todos los patrocinadores
    @Override
    public List<Patrocinador> buscarTodos() {
        return session.createQuery("FROM Patrocinador", Patrocinador.class).list();
    }
    /*
     * patrocinador sin patrocinio en la liga
     *  @param patrocinador

     */
    @Override
    public void eliminar(Patrocinador patrocinador) {
        session.delete(patrocinador);
    }
}
