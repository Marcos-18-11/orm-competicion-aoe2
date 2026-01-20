package com.dam.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/***
 * Esta clase se encarga de gestionar la factoría de EntityManager
 * @author Tu Nombre
 */
public class JpaUtil {

    private static final EntityManagerFactory entityMangerFactory = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory("aoe2Persistencia");
        } catch (Exception e) {
            System.err.println("Error al crear EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityMangerFactory;
    }

    public static void close() {
        if (entityMangerFactory != null && entityMangerFactory.isOpen())
            entityMangerFactory.close();
    }
}