package com.dam.dao.jpa;

import com.dam.dao.EquipoDao;
import com.dam.model.Equipo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EquipoDaoJpa implements EquipoDao {

    private final EntityManager entityManager;

    public EquipoDaoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void guardar(Equipo equipo) {

        if (equipo.getId() == null) {
            entityManager.persist(equipo);
        } else {
            entityManager.merge(equipo);
        }
    }

    @Override
    public Equipo buscarPorId(Long id) {
        return entityManager.find(Equipo.class, id);
    }

    @Override
    public List<Equipo> buscarTodos() {

        return entityManager.createQuery("SELECT e FROM Equipo e", Equipo.class)
                .getResultList();
    }

    @Override
    public void eliminar(Equipo equipo) {
        // Antes de borrar hay que asegurarse de que el objeto está "gestionado" por Hibernate
        Equipo managed = equipo;
        if (!entityManager.contains(equipo)) {
            managed = entityManager.find(Equipo.class, equipo.getId());
        }
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    public Equipo buscarPorNombre(String nombre) {
        try {
            TypedQuery<Equipo> query = entityManager.createQuery(
                    "SELECT e FROM Equipo e WHERE e.nombre = :nombre", Equipo.class);
            query.setParameter("nombre", nombre);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}