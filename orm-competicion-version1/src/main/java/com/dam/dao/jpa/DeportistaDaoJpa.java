package com.dam.dao.jpa;

import com.dam.dao.DeportistaDao;
import com.dam.model.Deportista;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DeportistaDaoJpa implements DeportistaDao {

    private final EntityManager entityManager;

    public DeportistaDaoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void guardar(Deportista deportista) {
        if (deportista.getId() == null) {
            entityManager.persist(deportista);
        } else {
            entityManager.merge(deportista);
        }
    }

    @Override
    public Deportista buscarPorId(Long id) {
        return entityManager.find(Deportista.class, id);
    }

    @Override
    public List<Deportista> buscarTodos() {
        return entityManager.createQuery("SELECT d FROM Deportista d", Deportista.class)
                .getResultList();
    }

    @Override
    public List<Deportista> buscarPorEquipo(Long idEquipo) {
        // JPQL: Buscar deportistas donde el equipo.id sea igual al parámetro
        TypedQuery<Deportista> query = entityManager.createQuery(
                "SELECT d FROM Deportista d WHERE d.equipo.id = :idEquipo", Deportista.class);
        query.setParameter("idEquipo", idEquipo);
        return query.getResultList();
    }

    @Override
    public void eliminar(Deportista deportista) {
        Deportista managed = deportista;
        if (!entityManager.contains(deportista)) {
            managed = entityManager.find(Deportista.class, deportista.getId());
        }
        if (managed != null) {
            entityManager.remove(managed);
        }
    }
}