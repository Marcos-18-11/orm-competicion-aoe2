package com.dam.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// Clase que sustituye a JpaUtil

public class HibernateUtil {

    // Pieza clave de Hibernnate nativo, sustituye al EntityManagerfactory, consume mucha RAM y a de ser static(patrón singleton)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Crea la SessionFactory leyendo el archivo hibernate.cfg.xml /
            // Aquí Hibernate carga:
            // -la conexión a la BD
            // las entidades @Entity
            //  las propiedades (dialecto, driver, etc.)
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Fallo al crear la SessionFactory inicial." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    // usamos este método, cada vez que tengamos que interactuar con la bbdd
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Cierra cachés y conexiones, necesario al final del main
        getSessionFactory().close();
    }
}
