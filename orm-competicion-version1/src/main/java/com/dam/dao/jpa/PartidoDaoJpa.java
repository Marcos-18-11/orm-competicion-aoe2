package com.dam.dao.jpa;

import com.dam.dao.PartidoDao;
import com.dam.model.Partido;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PartidoDaoJpa implements PartidoDao {
    private final EntityManager em;

    public PartidoDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public void guardar(Partido partido) {
        if(partido.getId() == null) em.persist(partido);
        else em.merge(partido);
    }

    @Override
    public List<Partido> buscarTodos() {
        return em.createQuery("SELECT p FROM Partido p", Partido.class).getResultList();
    }
}