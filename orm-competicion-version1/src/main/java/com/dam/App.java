package com.dam;

import com.dam.config.JpaUtil;
import com.dam.service.ConsultasCompeticion;
import com.dam.service.SimulacionService;
import jakarta.persistence.EntityManager;


/***
 *
 * @author Marcos
 * @version 1.0
 */

public class App {

    public static void main(String[] args) {

        EntityManager em = null;

        try {

            em = JpaUtil.getEntityManagerFactory().createEntityManager();


            em.getTransaction().begin();

            SimulacionService simulacion = new SimulacionService(em);
            simulacion.inicializarTemporada();
            simulacion.jugarLiga();


            simulacion.gestionarFichajes();

            em.getTransaction().commit();


            System.out.println("\n\n*********************************************");
            System.out.println("      EJECUTANDO BATERÍA DE CONSULTAS");
            System.out.println("*********************************************");

            ConsultasCompeticion consultas = new ConsultasCompeticion(em);

            consultas.consulta01_CaracteristicasCompeticion();
            consultas.consulta02_TodosLosEquipos();
            consultas.consulta03_DeportistasPorEquipo("GamerLegion");


            consultas.consulta04_PatrocinadoresDeEquipo("GamerLegion");
            consultas.consulta05_ResumenEquipo("GamerLegion");
            consultas.consulta06_EdadPromedio("GamerLegion");


            consultas.consulta07_Mayores23PorNacionalidad();
            consultas.consulta08_ClasificacionGeneral();
            consultas.consulta09_Top3_Bottom3();


            consultas.consulta10_NuevasIncorporaciones(); // NamedQuery
            consultas.consulta11_FichajesRealizados();
            consultas.consulta12_TotalDeportistas();
            consultas.consulta13_PatrocinadoresComunes("GamerLegion", "Aftermath");
            consultas.consulta14_BusquedaCriterios();

        } catch (Exception e) {
            e.printStackTrace();

            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {

            if (em != null) {
                em.close();
            }
            JpaUtil.close();
        }
    }
}