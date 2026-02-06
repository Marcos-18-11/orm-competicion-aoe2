package com.dam.dao.hibernate;

import com.dam.dao.DeportistaDao;
import com.dam.model.Deportista;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

/*
* Clase para realizar operaciones CRUD sobre la entidad deportista
*
* */
public class DeportistaDaoHibernate implements DeportistaDao {

    // variable para interactuar con la base de datos
    private final Session session;

   // @param sesión activa para CRUD
    public DeportistaDaoHibernate(Session session) {
        this.session = session;
    }


    // @param deportista jugador que se convierte en pro
    @Override
    public void guardar(Deportista deportista) {
        session.saveOrUpdate(deportista);
    }
    // @param id de deportist almacenado; buscar
    // @return el deportista a buscar en el parámetor
    @Override
    public Deportista buscarPorId(Long id) {
        return session.get(Deportista.class, id);
    }
    /**
     * Obtiene todos los deportistas almacenados en la base de datos.
     * @return Lista de deportistas.
     */
    @Override
    public List<Deportista> buscarTodos() {
        return session.createQuery("FROM Deportista", Deportista.class).list();
    }
    /*
     * Busca todos los deportistas que pertenecen a un equipo concreto.
     *  @param idEquipo ID del equipo.
     *  @return Lista de deportistas del equipo.
     */
    @Override
    public List<Deportista> buscarPorEquipo(Long idEquipo) {
        Query<Deportista> query = session.createQuery("FROM Deportista d WHERE d.equipo.id = :idEquipo", Deportista.class);
        query.setParameter("idEquipo", idEquipo);
        return query.list();
    }
    /*
     * Jugador de AoE que se retira de liga profesional
     *  @param deportista

     */
    @Override
    public void eliminar(Deportista deportista) {
        session.delete(deportista);
    }
}