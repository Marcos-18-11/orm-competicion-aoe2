package com.dam;

import com.dam.config.HibernateUtil;
import com.dam.service.ConsultasCompeticion;
import com.dam.service.SimulacionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
/*
* Clase principal
*
* @author Marcos Bautista 2 DAM Vespertino
*@version 2.0
* */
public class App {

    // Declaramos el Logger de Log4j
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        Session session = null;
        Transaction tx = null;

        try {
            logger.info("=== INICIANDO SISTEMA DE GESTIÓN DEPORTIVA (V2) ===");

            //  Obtener Sesión de Hibernate
            session = HibernateUtil.getSessionFactory().openSession();

            //  Iniciar Transacción
            tx = session.beginTransaction();

            // SIMULACIÓN ---
            SimulacionService simulacion = new SimulacionService(session);

            // CARGAR DATOS
            logger.info(">> Cargando datos iniciales (Equipos, Jugadores, Sponsors)...");
            simulacion.inicializarTemporada();

            // JUGAR LA LIGA
            logger.info(">> Disputando la temporada 2026...");
            simulacion.jugarLiga();

            //  GESTIÓN ECONÓMICA (NUEVO V2)
            logger.info(">> Gestionando pagos de nóminas y premios...");
            simulacion.gestionarEconomia();

            //  FICHAJES
            logger.info(">> Abriendo mercado de fichajes de fin de temporada...");
            simulacion.gestionarFichajes();

            //  Commit (Guardar todo en BD)
            tx.commit();
            logger.info("=== SIMULACIÓN FINALIZADA Y GUARDADA CON ÉXITO ===");

            //  CONSULTAS ---
            System.out.println("\n\n###########################################################");
            System.out.println("      EJECUTANDO BATERÍA DE CONSULTAS (Hibernate Nativo)");
            System.out.println("###########################################################");

            ConsultasCompeticion consultas = new ConsultasCompeticion(session);

            //  Generales
            consultas.consulta01_CaracteristicasCompeticion();
            consultas.consulta02_TodosLosEquipos();
            consultas.consulta03_DeportistasPorEquipo("GamerLegion");
            consultas.consulta08_ClasificacionGeneral();
            consultas.consulta10_NuevasIncorporaciones();
            consultas.consulta14_BusquedaCriterios();

            // Económicas
            System.out.println("\n--- CONSULTAS ECONÓMICAS (V2) ---");
            consultas.consulta15_EquipoMasRico();
            consultas.consulta16_JugadorMejorPagado();
            consultas.consulta17_AlertaQuiebra();

        } catch (Exception e) {
            logger.error("CRITICAL ERROR: Se ha producido un fallo en la simulación.", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
                logger.warn("Se ha realizado un Rollback de la transacción.");
            }
        } finally {
            if (session != null) {
                session.close();
            }
            HibernateUtil.shutdown();
            logger.info("Conexión cerrada. Aplicación terminada.");
        }
    }
}