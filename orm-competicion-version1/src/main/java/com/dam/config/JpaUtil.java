package com.dam.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/***
 * Clase singleton dedicada a validar tablas, leer persistence...
 * crea una variable estática que proporciona al resto del programa
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