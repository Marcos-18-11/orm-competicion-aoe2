package com.dam;

import com.dam.config.JpaUtil;
import com.dam.service.ConsultasCompeticion;
import com.dam.service.SimulacionService;
import jakarta.persistence.EntityManager;

public class App {

    public static void main(String[] args) {

        EntityManager em = null;

        try {
            // 1. Conexión
            em = JpaUtil.getEntityManagerFactory().createEntityManager();

            // 2. Simulación (Transacción 1)
            em.getTransaction().begin();

            SimulacionService simulacion = new SimulacionService(em);
            simulacion.inicializarTemporada();

            em.getTransaction().commit();

            // 3. Consultas (No necesitan transacción obligatoria si son solo lecturas,
            // pero el EM debe estar abierto)
            System.out.println("\n\n*********************************************");
            System.out.println("      EJECUTANDO BATERÍA DE CONSULTAS");
            System.out.println("*********************************************");

            ConsultasCompeticion consultas = new ConsultasCompeticion(em);

            consultas.consulta01_CaracteristicasCompeticion();
            consultas.consulta02_TodosLosEquipos();
            consultas.consulta03_DeportistasPorEquipo("GamerLegion");

            // Intermedias (LAS QUE TE FALTABAN)
            consultas.consulta04_PatrocinadoresDeEquipo("GamerLegion");
            consultas.consulta05_ResumenEquipo("GamerLegion");
            consultas.consulta06_EdadPromedio("GamerLegion");

            // Agrupación y Ordenación
            consultas.consulta07_Mayores23PorNacionalidad();
            consultas.consulta08_ClasificacionGeneral();
            consultas.consulta09_Top3_Bottom3();

            // Avanzadas (NUEVAS: NamedQuery, Totales y Criteria)
            consultas.consulta10_NuevasIncorporaciones(); // NamedQuery
            consultas.consulta11_FichajesRealizados();
            consultas.consulta12_TotalDeportistas();
            consultas.consulta13_PatrocinadoresComunes("GamerLegion", "Aftermath");
            consultas.consulta14_BusquedaCriterios();

        } catch (Exception e) {
            e.printStackTrace();
            // Si falla algo y la transacción seguía abierta, deshacer cambios
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            // Cerrar siempre
            if (em != null) {
                em.close();
            }
            JpaUtil.close();
        }
    }
}