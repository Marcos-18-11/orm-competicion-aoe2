package com.dam.service;

import com.dam.dao.hibernate.DeportistaDaoHibernate;
import com.dam.dao.hibernate.EquipoDaoHibernate;
import com.dam.dao.hibernate.PatrocinadorDaoHibernate;
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.Patrocinador;
import org.hibernate.Session;
import org.hibernate.query.Query; // Query de Hibernate

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ConsultasCompeticion {

    private final Session session;
    private final EquipoDaoHibernate equipoDao;
    private final DeportistaDaoHibernate deportistaDao;
    private final PatrocinadorDaoHibernate patrocinadorDao;

    public ConsultasCompeticion(Session session) {
        this.session = session;
        this.equipoDao = new EquipoDaoHibernate(session);
        this.deportistaDao = new DeportistaDaoHibernate(session);
        this.patrocinadorDao = new PatrocinadorDaoHibernate(session);
    }

    // 1. NATIVE QUERY
    public void consulta01_CaracteristicasCompeticion() {
        System.out.println("## 1. Características (Native Query Hibernate)");
        String sql = "SELECT (SELECT COUNT(*) FROM teams) as te, (SELECT COUNT(*) FROM players) as pl";

        // En Hibernate Nativo moderno, createNativeQuery también funciona
        Object[] resultado = (Object[]) session.createNativeQuery(sql, Object[].class).uniqueResult(); // uniqueResult() es nativo

        System.out.println("Total Equipos: " + resultado[0]);
        System.out.println("Total Jugadores: " + resultado[1]);
    }

    // 2. TODOS LOS EQUIPOS
    public void consulta02_TodosLosEquipos() {
        System.out.println("## 2. Todos los equipos");
        List<Equipo> equipos = equipoDao.buscarTodos();
        for (Equipo e : equipos) System.out.println("- " + e.getNombre());
    }

    // 3. DEPORTISTAS POR EQUIPO
    public void consulta03_DeportistasPorEquipo(String nombreEquipo) {
        System.out.println("## 3. Deportistas de: " + nombreEquipo);
        Query<Deportista> query = session.createQuery("FROM Deportista d WHERE d.equipo.nombre = :nombre", Deportista.class);
        query.setParameter("nombre", nombreEquipo);

        List<Deportista> deportistas = query.list(); // .list() es lo nativo
        deportistas.forEach(d -> System.out.println("-> " + d.getNick()));
    }

    // 4. PATROCINADORES DE UN EQUIPO
    public void consulta04_PatrocinadoresDeEquipo(String nombreEquipo) {
        System.out.println("## 4. Sponsors de: " + nombreEquipo);
        Query<Patrocinador> query = session.createQuery("SELECT p FROM Patrocinador p JOIN p.equipos e WHERE e.nombre = :nombre", Patrocinador.class);
        query.setParameter("nombre", nombreEquipo);

        List<Patrocinador> list = query.list();
        list.forEach(p -> System.out.println("- " + p.getNombre()));
    }

    // 5. RESUMEN
    public void consulta05_ResumenEquipo(String nombreEquipo) {
        System.out.println("## 5. Resumen: " + nombreEquipo);
        Equipo equipo = equipoDao.buscarPorNombre(nombreEquipo);
        if (equipo != null) {
            System.out.println("TEAM: " + equipo.getNombre());
            equipo.getDeportistas().forEach(d -> System.out.println(" * " + d.getNick()));
        }
    }

    // 6. EDAD PROMEDIO
    public void consulta06_EdadPromedio(String nombreEquipo) {
        System.out.println("## 6. Edad media");
        Double avg = session.createQuery("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(d.fechaNacimiento)) FROM Deportista d WHERE d.equipo.nombre = :nombre", Double.class)
                .setParameter("nombre", nombreEquipo)
                .uniqueResult();
        System.out.println("Media: " + avg);
    }

    // 7. MAYORES 23
    public void consulta07_Mayores23PorNacionalidad() {
        System.out.println("## 7. Mayores de 23");
        List<Object[]> res = session.createQuery("SELECT d.nacionalidad, COUNT(d) FROM Deportista d WHERE (YEAR(CURRENT_DATE) - YEAR(d.fechaNacimiento)) > 23 GROUP BY d.nacionalidad", Object[].class)
                .list();
        res.forEach(r -> System.out.println(r[0] + ": " + r[1]));
    }

    // 8. CLASIFICACION
    public void consulta08_ClasificacionGeneral() {
        System.out.println("## 8. Ranking");
        List<Equipo> rank = session.createQuery("FROM Equipo ORDER BY puntos DESC", Equipo.class).list();
        rank.forEach(e -> System.out.println(e.getNombre() + " - " + e.getPuntos()));
    }

    // 9. TOP 3 BOTTOM 3
    public void consulta09_Top3_Bottom3() {
        System.out.println("## 9. Top/Bottom 3");
        List<Equipo> top = session.createQuery("FROM Equipo ORDER BY puntos DESC", Equipo.class).setMaxResults(3).list();
        List<Equipo> bot = session.createQuery("FROM Equipo ORDER BY puntos ASC", Equipo.class).setMaxResults(3).list();

        System.out.println("TOP:"); top.forEach(e -> System.out.println(e.getNombre()));
        System.out.println("BOT:"); bot.forEach(e -> System.out.println(e.getNombre()));
    }

    // 10. NAMED QUERY
    public void consulta10_NuevasIncorporaciones() {
        System.out.println("## 10. NamedQuery (Spain)");
        // session.createNamedQuery funciona igual que en JPA
        List<Deportista> res = session.createNamedQuery("Deportista.buscarPorNacionalidad", Deportista.class)
                .setParameter("nacionalidad", "Spain")
                .list();
        res.forEach(d -> System.out.println(d.getNick()));
    }

    // 11. FICHAJES
    public void consulta11_FichajesRealizados() {
        System.out.println("## 11. Total Movimientos");
        System.out.println("Total: " + deportistaDao.buscarTodos().size());
    }

    // 12. TOTAL JUGADORES
    public void consulta12_TotalDeportistas() {
        System.out.println("## 12. Total Jugadores");
        Long total = session.createQuery("SELECT COUNT(d) FROM Deportista d", Long.class).uniqueResult();
        System.out.println("Total: " + total);
    }

    // 13. COMUNES
    public void consulta13_PatrocinadoresComunes(String eqA, String eqB) {
        System.out.println("## 13. Sponsors Comunes");
        List<Patrocinador> res = session.createQuery("SELECT DISTINCT p FROM Patrocinador p JOIN p.equipos e1 JOIN p.equipos e2 WHERE e1.nombre = :nA AND e2.nombre = :nB", Patrocinador.class)
                .setParameter("nA", eqA).setParameter("nB", eqB)
                .list();
        res.forEach(p -> System.out.println(p.getNombre()));
    }

    // 14. CRITERIA (Hibernate usa la API JPA Criteria builder)
    public void consulta14_BusquedaCriterios() {
        System.out.println("## 14. Criteria");
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Deportista> cq = cb.createQuery(Deportista.class);
        Root<Deportista> root = cq.from(Deportista.class);

        Predicate p = cb.or(cb.equal(root.get("nacionalidad"), "Spain"), cb.equal(root.get("nacionalidad"), "Finland"));
        cq.select(root).where(p);

        List<Deportista> res = session.createQuery(cq).list();
        res.forEach(d -> System.out.println(d.getNick()));
    }
    // -------------------------------------------------------------------------
    // 15. EQUIPO CON MAYOR PRESUPUESTO (ECONOMÍA)
    // -------------------------------------------------------------------------
    public void consulta15_EquipoMasRico() {
        System.out.println("## 15. El equipo más rico");

        Equipo rico = session.createQuery("FROM Equipo ORDER BY presupuesto DESC", Equipo.class)
                .setMaxResults(1)
                .uniqueResult();

        if (rico != null) {
            System.out.printf("🤑 TOP 1: %s con %.2f€ de presupuesto%n", rico.getNombre(), rico.getPresupuesto());
        }
    }

    // -------------------------------------------------------------------------
    // 16. JUGADOR MEJOR PAGADO (ECONOMÍA)
    // -------------------------------------------------------------------------
    public void consulta16_JugadorMejorPagado() {
        System.out.println("## 16. El jugador mejor pagado");

        Deportista pro = session.createQuery("FROM Deportista ORDER BY salario DESC", Deportista.class)
                .setMaxResults(1)
                .uniqueResult();

        if (pro != null) {
            System.out.printf("💎 ESTRELLA: %s (%s) cobra %.2f€ anuales%n",
                    pro.getNick(), pro.getEquipo().getNombre(), pro.getSalario());
        }
    }

    // -------------------------------------------------------------------------
    // 17. ALERTA DE BANCARROTA (EQUIPOS EN NÚMEROS ROJOS)
    // -------------------------------------------------------------------------
    public void consulta17_AlertaQuiebra() {
        System.out.println("## 17. Equipos en riesgo de quiebra (Presupuesto < 0)");

        List<Equipo> arruinados = session.createQuery("FROM Equipo WHERE presupuesto < 0", Equipo.class)
                .list();

        if (arruinados.isEmpty()) {
            System.out.println("✅ Todos los equipos tienen cuentas saneadas.");
        } else {
            for (Equipo e : arruinados) {
                System.out.printf("🆘 %s tiene una deuda de %.2f€%n", e.getNombre(), e.getPresupuesto());
            }
        }
    }

}