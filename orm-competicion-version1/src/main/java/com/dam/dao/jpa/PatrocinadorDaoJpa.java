package com.dam.dao.jpa;

import com.dam.dao.PatrocinadorDao;
import com.dam.model.Patrocinador;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PatrocinadorDaoJpa implements PatrocinadorDao {

    private final EntityManager entityManager;

    public PatrocinadorDaoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void guardar(Patrocinador patrocinador) {
        if (patrocinador.getId() == null) {
            entityManager.persist(patrocinador);
        } else {
            entityManager.merge(patrocinador);
        }
    }

    @Override
    public Patrocinador buscarPorId(Long id) {
        return entityManager.find(Patrocinador.class, id);
    }

    @Override
    public List<Patrocinador> buscarTodos() {
        return entityManager.createQuery("SELECT p FROM Patrocinador p", Patrocinador.class)
                .getResultList();
    }

    @Override
    public void eliminar(Patrocinador patrocinador) {
        Patrocinador managed = patrocinador;
        if (!entityManager.contains(patrocinador)) {
            managed = entityManager.find(Patrocinador.class, patrocinador.getId());
        }
        if (managed != null) {
            entityManager.remove(managed);
        }
    }
}