package com.dam.service;

import com.dam.dao.jpa.DeportistaDaoJpa;
import com.dam.dao.jpa.EquipoDaoJpa;
import com.dam.dao.jpa.PatrocinadorDaoJpa;
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.Patrocinador;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/***
 * Esta clase realiza las 14 consultas solicitadas en la práctica.
 * Se encarga de gestionar las queries JPQL, Nativas, NamedQueries y Criteria.
 * @author Tu Nombre
 */
public class ConsultasCompeticion {

    private final EntityManager em;
    private final EquipoDaoJpa equipoDao;
    private final DeportistaDaoJpa deportistaDao;
    private final PatrocinadorDaoJpa patrocinadorDao;

    public ConsultasCompeticion(EntityManager em) {
        this.em = em;
        this.equipoDao = new EquipoDaoJpa(em);
        this.deportistaDao = new DeportistaDaoJpa(em);
        this.patrocinadorDao = new PatrocinadorDaoJpa(em);
    }

    // -------------------------------------------------------------------------
    // 1. CARACTERÍSTICAS DE LA COMPETICIÓN (NATIVE QUERY)
    // -------------------------------------------------------------------------
    public void consulta01_CaracteristicasCompeticion() {
        System.out.println("## 1. Características de la competición (Native Query)");

        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM teams) as total_equipos, " +
                "(SELECT COUNT(*) FROM players) as total_jugadores";

        Query nativeQuery = em.createNativeQuery(sql);
        Object[] resultado = (Object[]) nativeQuery.getSingleResult();

        System.out.println("## Resultado query:");
        System.out.println("Total Equipos inscritos: " + resultado[0]);
        System.out.println("Total Jugadores inscritos: " + resultado[1]);
        System.out.println("##################### FIN Consulta 1 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 2. RECUPERAR TODOS LOS EQUIPOS
    // -------------------------------------------------------------------------
    public void consulta02_TodosLosEquipos() {
        System.out.println("## 2. Consulta y recupera todos los equipos participantes");

        List<Equipo> equipos = equipoDao.buscarTodos();

        System.out.println("## Resultado query:");
        for (Equipo e : equipos) {
            System.out.println("- " + e.getNombre() + " (" + e.getPais() + ")");
        }
        System.out.println("##################### FIN Consulta 2 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 3. LISTA DE DEPORTISTAS DE UN EQUIPO ESPECÍFICO
    // -------------------------------------------------------------------------
    public void consulta03_DeportistasPorEquipo(String nombreEquipo) {
        System.out.println("## 3. Obtén la lista de deportistas de: " + nombreEquipo);

        String jpql = "SELECT d FROM Deportista d WHERE d.equipo.nombre = :nombre";

        List<Deportista> deportistas = em.createQuery(jpql, Deportista.class)
                .setParameter("nombre", nombreEquipo)
                .getResultList();

        System.out.println("## Resultado query:");
        if (deportistas.isEmpty()) {
            System.out.println("No se encontró el equipo o no tiene jugadores.");
        } else {
            for (Deportista d : deportistas) {
                System.out.println("-> " + d.getNick() + " (Nacionalidad: " + d.getNacionalidad() + ")");
            }
        }
        System.out.println("##################### FIN Consulta 3 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 4. PATROCINADORES ASOCIADOS A UN EQUIPO
    // -------------------------------------------------------------------------
    public void consulta04_PatrocinadoresDeEquipo(String nombreEquipo) {
        System.out.println("## 4. Patrocinadores del equipo: " + nombreEquipo);

        String jpql = "SELECT p FROM Patrocinador p JOIN p.equipos e WHERE e.nombre = :nombre";

        List<Patrocinador> patrocinadores = em.createQuery(jpql, Patrocinador.class)
                .setParameter("nombre", nombreEquipo)
                .getResultList();

        System.out.println("## Resultado query:");
        if (patrocinadores.isEmpty()) {
            System.out.println("El equipo no tiene patrocinadores.");
        } else {
            for (Patrocinador p : patrocinadores) {
                System.out.println("- Sponsor: " + p.getNombre());
            }
        }
        System.out.println("##################### FIN Consulta 4 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 5. LISTA DE DEPORTISTAS Y PATROCINADORES DE UN EQUIPO
    // -------------------------------------------------------------------------
    public void consulta05_ResumenEquipo(String nombreEquipo) {
        System.out.println("## 5. Resumen completo (Jugadores + Sponsors) de: " + nombreEquipo);

        Equipo equipo = equipoDao.buscarPorNombre(nombreEquipo);

        System.out.println("## Resultado query:");
        if (equipo != null) {
            System.out.println("EQUIPO: " + equipo.getNombre());

            System.out.println("  [JUGADORES]:");
            for (Deportista d : equipo.getDeportistas()) {
                System.out.println("   * " + d.getNick());
            }

            System.out.println("  [SPONSORS]:");
            for (Patrocinador p : equipo.getPatrocinadores()) {
                System.out.println("   $ " + p.getNombre());
            }
        } else {
            System.out.println("Equipo no encontrado.");
        }
        System.out.println("##################### FIN Consulta 5 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 6. EDAD PROMEDIO DE DEPORTISTAS DE UN EQUIPO
    // -------------------------------------------------------------------------
    public void consulta06_EdadPromedio(String nombreEquipo) {
        System.out.println("## 6. Edad promedio en el equipo: " + nombreEquipo);

        String jpql = "SELECT AVG(YEAR(CURRENT_DATE) - YEAR(d.fechaNacimiento)) FROM Deportista d WHERE d.equipo.nombre = :nombre";

        Double edadMedia = (Double) em.createQuery(jpql)
                .setParameter("nombre", nombreEquipo)
                .getSingleResult();

        System.out.println("## Resultado query:");
        if (edadMedia != null) {
            System.out.printf("Edad media: %.2f años%n", edadMedia);
        } else {
            System.out.println("No hay datos para calcular.");
        }
        System.out.println("##################### FIN Consulta 6 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 7. DEPORTISTAS > 23 AÑOS AGRUPADOS POR NACIONALIDAD
    // -------------------------------------------------------------------------
    public void consulta07_Mayores23PorNacionalidad() {
        System.out.println("## 7. Deportistas > 23 años por Nacionalidad");

        String jpql = "SELECT d.nacionalidad, COUNT(d) FROM Deportista d " +
                "WHERE (YEAR(CURRENT_DATE) - YEAR(d.fechaNacimiento)) > 23 " +
                "GROUP BY d.nacionalidad";

        List<Object[]> resultados = em.createQuery(jpql, Object[].class).getResultList();

        System.out.println("## Resultado query:");
        if (resultados.isEmpty()) {
            System.out.println("No hay deportistas mayores de 23 años.");
        } else {
            for (Object[] fila : resultados) {
                String pais = (String) fila[0];
                Long cantidad = (Long) fila[1];
                System.out.println("-> " + pais + ": " + cantidad + " jugadores");
            }
        }
        System.out.println("##################### FIN Consulta 7 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 8. CLASIFICACIÓN GENERAL (INICIO, MITAD, FINAL)
    // -------------------------------------------------------------------------
    public void consulta08_ClasificacionGeneral() {
        System.out.println("## 8. Clasificación General (Ordenada por Puntos)");

        List<Equipo> ranking = em.createQuery("SELECT e FROM Equipo e ORDER BY e.puntos DESC", Equipo.class)
                .getResultList();

        System.out.println("## Resultado query:");
        int posicion = 1;
        for (Equipo e : ranking) {
            System.out.println(posicion + "º " + e.getNombre() + " - " + e.getPuntos() + " pts");
            posicion++;
        }
        System.out.println("##################### FIN Consulta 8 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 9. TOP 3 MEJORES Y PEORES EQUIPOS
    // -------------------------------------------------------------------------
    public void consulta09_Top3_Bottom3() {
        System.out.println("## 9. Top 3 Mejores y Peores");

        // Top 3
        List<Equipo> top3 = em.createQuery("SELECT e FROM Equipo e ORDER BY e.puntos DESC", Equipo.class)
                .setMaxResults(3)
                .getResultList();

        // Bottom 3
        List<Equipo> bottom3 = em.createQuery("SELECT e FROM Equipo e ORDER BY e.puntos ASC", Equipo.class)
                .setMaxResults(3)
                .getResultList();

        System.out.println("## Resultado query:");
        System.out.println("--- PODIO (TOP 3) ---");
        for (Equipo e : top3) System.out.println("🏆 " + e.getNombre() + " (" + e.getPuntos() + ")");

        System.out.println("--- DESCENSO (BOTTOM 3) ---");
        for (Equipo e : bottom3) System.out.println("🔻 " + e.getNombre() + " (" + e.getPuntos() + ")");

        System.out.println("##################### FIN Consulta 9 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 10. NUEVAS INCORPORACIONES (NAMED QUERY)
    // -------------------------------------------------------------------------
    public void consulta10_NuevasIncorporaciones() {
        System.out.println("## 10. Nuevas Incorporaciones (Usando NamedQuery: 'Deportista.buscarPorNacionalidad')");

        // Se requiere haber definido @NamedQuery en la clase Deportista
        // Probamos buscando una nacionalidad común, ej: "Spain" o "Germany"
        List<Deportista> resultados = em.createNamedQuery("Deportista.buscarPorNacionalidad", Deportista.class)
                .setParameter("nacionalidad", "Spain")
                .getResultList();

        System.out.println("## Resultado query (Fichajes Españoles):");
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron jugadores con esa nacionalidad en los fichajes.");
        } else {
            for (Deportista d : resultados) {
                System.out.println("-> Fichaje: " + d.getNick() + " (" + d.getEquipo().getNombre() + ")");
            }
        }
        System.out.println("##################### FIN Consulta 10 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 11. ENUMERAR FICHAJES REALIZADOS
    // -------------------------------------------------------------------------
    public void consulta11_FichajesRealizados() {
        System.out.println("## 11. Historial de Fichajes");
        // En V1 no hay tabla de histórico. Mostramos el total de inscritos como "Fichajes Iniciales"

        List<Deportista> todos = deportistaDao.buscarTodos();

        System.out.println("## Resultado query:");
        System.out.println("Total movimientos de mercado (Inscripciones): " + todos.size());
        System.out.println("##################### FIN Consulta 11 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 12. RECUENTO TOTAL DE DEPORTISTAS
    // -------------------------------------------------------------------------
    public void consulta12_TotalDeportistas() {
        System.out.println("## 12. Recuento total de participantes");

        Long total = em.createQuery("SELECT COUNT(d) FROM Deportista d", Long.class).getSingleResult();

        System.out.println("## Resultado query:");
        System.out.println("TOTAL JUGADORES ACTIVOS: " + total);
        System.out.println("##################### FIN Consulta 12 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 13. PATROCINADORES COMUNES ENTRE DOS EQUIPOS
    // -------------------------------------------------------------------------
    public void consulta13_PatrocinadoresComunes(String equipoA, String equipoB) {
        System.out.println("## 13. Sponsors comunes entre " + equipoA + " y " + equipoB);

        String jpql = "SELECT DISTINCT p FROM Patrocinador p " +
                "JOIN p.equipos e1 " +
                "JOIN p.equipos e2 " +
                "WHERE e1.nombre = :nombreA AND e2.nombre = :nombreB";

        List<Patrocinador> comunes = em.createQuery(jpql, Patrocinador.class)
                .setParameter("nombreA", equipoA)
                .setParameter("nombreB", equipoB)
                .getResultList();

        System.out.println("## Resultado query:");
        if (comunes.isEmpty()) {
            System.out.println("No comparten ningún patrocinador.");
        } else {
            for (Patrocinador p : comunes) {
                System.out.println("$$ Marca compartida: " + p.getNombre());
            }
        }
        System.out.println("##################### FIN Consulta 13 #################################\n");
    }

    // -------------------------------------------------------------------------
    // 14. CRITERIA QUERY (FILTRADO DINÁMICO)
    // -------------------------------------------------------------------------
    public void consulta14_BusquedaCriterios() {
        System.out.println("## 14. CriteriaQuery (API de Criterios - Sin SQL)");

        // 1. Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // 2. Query type
        CriteriaQuery<Deportista> cq = cb.createQuery(Deportista.class);
        // 3. Root (FROM)
        Root<Deportista> root = cq.from(Deportista.class);

        // 4. Predicados (WHERE)
        // Ejemplo: Jugadores de "Spain" o "Finland"
        Predicate esSpain = cb.equal(root.get("nacionalidad"), "Spain");
        Predicate esFinland = cb.equal(root.get("nacionalidad"), "Finland");
        Predicate spainOrFinland = cb.or(esSpain, esFinland);

        // 5. Construcción
        cq.select(root)
                .where(spainOrFinland)
                .orderBy(cb.asc(root.get("nick")));

        // 6. Ejecución
        List<Deportista> resultados = em.createQuery(cq).getResultList();

        System.out.println("## Resultado Criteria (Jugadores de Spain o Finland ordenados):");
        for (Deportista d : resultados) {
            System.out.println(">> " + d.getNick() + " [" + d.getNacionalidad() + "]");
        }
        System.out.println("##################### FIN Consulta 14 #################################\n");
    }
}