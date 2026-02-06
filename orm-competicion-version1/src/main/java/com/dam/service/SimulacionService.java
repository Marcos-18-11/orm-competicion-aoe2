package com.dam.service;

import com.dam.dao.hibernate.DeportistaDaoHibernate; // DAO para gestionar jugadores
import com.dam.dao.hibernate.EquipoDaoHibernate;      // DAO para equipos
import com.dam.dao.hibernate.PartidoDaoHibernate;     // DAO para partidos
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.Partido;
import com.dam.util.CargaDatosIniciales;               // Clase que carga datos de ejemplo
import org.hibernate.Session;

import java.util.List;
import java.util.Random;

public class SimulacionService {

    private final Session session;                     // Sesión de Hibernate para consultas y transacciones
    private final EquipoDaoHibernate equipoDao;        // Acceso a tabla teams
    private final PartidoDaoHibernate partidoDao;      // Acceso a tabla matches
    private final DeportistaDaoHibernate deportistaDao; // Acceso a tabla players

    public SimulacionService(Session session) {
        this.session = session;
        // Inyectamos la misma sesión en todos los DAOs
        this.equipoDao = new EquipoDaoHibernate(session);
        this.partidoDao = new PartidoDaoHibernate(session);
        this.deportistaDao = new DeportistaDaoHibernate(session);
    }

    public void inicializarTemporada() {
        System.out.println(">>> SERVICE: Cargando datos iniciales...");
        // Carga datos desde una clase auxiliar (equipos, jugadores, etc.)
        CargaDatosIniciales.cargarDatos(session);
    }

    public void jugarLiga() {
        System.out.println(">>> SERVICE: ⚔ COMIENZA LA LIGA AOE2 2026 (NATIVO) ️");

        List<Equipo> equipos = equipoDao.buscarTodos(); // Recupera todos los equipos
        Random random = new Random();
        int jornada = 1;

        // Doble bucle → todos contra todos
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {

                Equipo local = equipos.get(i);
                Equipo visit = equipos.get(j);

                // Marcadores aleatorios (0–3)
                int scoreLocal = random.nextInt(4);
                int scoreVisit = random.nextInt(4);
                if (scoreLocal == scoreVisit) scoreLocal++; // Evita empates

                Equipo ganador = (scoreLocal > scoreVisit) ? local : visit;
                String marcador = scoreLocal + "-" + scoreVisit;

                // Creamos el partido y lo guardamos
                Partido match = new Partido(jornada, local, visit, marcador, ganador);
                partidoDao.guardar(match);

                // Actualizamos puntos y presupuesto del ganador
                ganador.setPuntos(ganador.getPuntos() + 3);
                ganador.setPresupuesto(ganador.getPresupuesto() + 10000);
                equipoDao.guardar(ganador);

                System.out.println("   [J" + jornada + "] " + local.getNombre() + " " + marcador + " " + visit.getNombre());
            }
            jornada++;
        }
    }

    public void gestionarEconomia() {
        System.out.println("\n>>> SERVICE:  GESTIÓN ECONÓMICA Y PAGO DE NÓMINAS ");

        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();

        for (Equipo equipo : equipos) {

            double gastosSalariales = 0;

            // Recalcula salarios aleatorios para cada jugador
            for (Deportista d : equipo.getDeportistas()) {
                double nuevoSalario = 30000 + (70000 * random.nextDouble());
                d.setSalario(nuevoSalario);
                gastosSalariales += nuevoSalario;
            }

            // Si el equipo no tenía presupuesto, se le asigna uno inicial
            double presupuestoAnterior = equipo.getPresupuesto();
            if (presupuestoAnterior == 0)
                presupuestoAnterior = 500000 + (1500000 * random.nextDouble());

            // Presupuesto final tras pagar nóminas
            double balanceFinal = presupuestoAnterior - gastosSalariales;
            equipo.setPresupuesto(balanceFinal);
            equipoDao.guardar(equipo);

            System.out.printf("   [ECONOMÍA] %s: Presupuesto: %.2f€ - Nóminas: %.2f€ = Restante: %.2f€%n",
                    equipo.getNombre(), presupuestoAnterior, gastosSalariales, balanceFinal);

            if (balanceFinal < 0) {
                System.out.println(" ALERTA DE BANCARROTA ");
            }
        }
    }

    public void gestionarFichajes() {
        System.out.println("\n>>> SERVICE:  MERCADO DE FICHAJES 2027 ");

        List<Equipo> equipos = equipoDao.buscarTodos();

        if (equipos == null || equipos.size() < 2) {
            System.out.println("   No hay suficientes equipos para realizar fichajes.");
            return;
        }

        Random random = new Random();

        // Realiza 3 fichajes aleatorios
        for (int k = 0; k < 3; k++) {

            Equipo origen = equipos.get(random.nextInt(equipos.size()));
            Equipo destino = equipos.get(random.nextInt(equipos.size()));

            // Evita fichajes inválidos
            if (origen.equals(destino) || origen.getDeportistas().isEmpty()) continue;

            Deportista jugador = origen.getDeportistas().get(0); // Primer jugador del equipo origen

            System.out.println(" TRASPASO: " + jugador.getNick() + " deja " + origen.getNombre() + " y ficha por " + destino.getNombre());

            // --- CORRECCIÓN IMPORTANTE ---
            // 1. Actualizamos listas en memoria (coherencia)
            origen.getDeportistas().remove(jugador);
            destino.getDeportistas().add(jugador);

            // 2. Cambiamos la FK del jugador
            jugador.setEquipo(destino);

            // 3. Guardamos SOLO al jugador → actualiza team_id sin borrar nada
            deportistaDao.guardar(jugador);

            // Guardamos destino por si cambió su lista de jugadores
            equipoDao.guardar(destino);
        }
    }
}
