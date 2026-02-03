package com.dam.service;

import com.dam.dao.hibernate.DeportistaDaoHibernate; // NUEVO IMPORT
import com.dam.dao.hibernate.EquipoDaoHibernate;
import com.dam.dao.hibernate.PartidoDaoHibernate;
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.Partido;
import com.dam.util.CargaDatosIniciales;
import org.hibernate.Session;

import java.util.List;
import java.util.Random;

public class SimulacionService {

    private final Session session;
    private final EquipoDaoHibernate equipoDao;
    private final PartidoDaoHibernate partidoDao;
    private final DeportistaDaoHibernate deportistaDao; // NUEVO DAO

    public SimulacionService(Session session) {
        this.session = session;
        this.equipoDao = new EquipoDaoHibernate(session);
        this.partidoDao = new PartidoDaoHibernate(session);
        this.deportistaDao = new DeportistaDaoHibernate(session); // INICIALIZAR
    }

    public void inicializarTemporada() {
        // (Igual que antes)
        System.out.println(">>> SERVICE: Cargando datos iniciales...");
        CargaDatosIniciales.cargarDatos(session);
    }

    public void jugarLiga() {
        // (Igual que antes)
        System.out.println(">>> SERVICE: ⚔️ COMIENZA LA LIGA AOE2 2026 (NATIVO) ⚔️");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();
        int jornada = 1;

        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                Equipo local = equipos.get(i);
                Equipo visit = equipos.get(j);

                int scoreLocal = random.nextInt(4);
                int scoreVisit = random.nextInt(4);
                if (scoreLocal == scoreVisit) scoreLocal++;

                Equipo ganador = (scoreLocal > scoreVisit) ? local : visit;
                String marcador = scoreLocal + "-" + scoreVisit;

                Partido match = new Partido(jornada, local, visit, marcador, ganador);
                partidoDao.guardar(match);

                ganador.setPuntos(ganador.getPuntos() + 3);
                ganador.setPresupuesto(ganador.getPresupuesto() + 10000);
                equipoDao.guardar(ganador);

                System.out.println("   [J" + jornada + "] " + local.getNombre() + " " + marcador + " " + visit.getNombre());
            }
            jornada++;
        }
    }

    public void gestionarEconomia() {
        // (Igual que antes)
        System.out.println("\n>>> SERVICE: 💸 GESTIÓN ECONÓMICA Y PAGO DE NÓMINAS 💸");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();

        for (Equipo equipo : equipos) {
            double gastosSalariales = 0;
            for (Deportista d : equipo.getDeportistas()) {
                double nuevoSalario = 30000 + (70000 * random.nextDouble());
                d.setSalario(nuevoSalario);
                gastosSalariales += nuevoSalario;
            }
            double presupuestoAnterior = equipo.getPresupuesto();
            if (presupuestoAnterior == 0) presupuestoAnterior = 500000 + (1500000 * random.nextDouble());

            double balanceFinal = presupuestoAnterior - gastosSalariales;
            equipo.setPresupuesto(balanceFinal);
            equipoDao.guardar(equipo);

            System.out.printf("   [ECONOMÍA] %s: Presupuesto: %.2f€ - Nóminas: %.2f€ = Restante: %.2f€%n",
                    equipo.getNombre(), presupuestoAnterior, gastosSalariales, balanceFinal);

            if (balanceFinal < 0) {
                System.out.println("      ⚠️ ALERTA DE BANCARROTA ⚠️");
            }
        }
    }

    public void gestionarFichajes() {
        System.out.println("\n>>> SERVICE: 💰 MERCADO DE FICHAJES 2027 💰");
        List<Equipo> equipos = equipoDao.buscarTodos();

        if (equipos == null || equipos.size() < 2) {
            System.out.println("   ⚠️ No hay suficientes equipos para realizar fichajes.");
            return;
        }

        Random random = new Random();

        for (int k = 0; k < 3; k++) {
            Equipo origen = equipos.get(random.nextInt(equipos.size()));
            Equipo destino = equipos.get(random.nextInt(equipos.size()));

            if (origen.equals(destino) || origen.getDeportistas().isEmpty()) continue;

            Deportista jugador = origen.getDeportistas().get(0);

            System.out.println("   🚨 TRASPASO: " + jugador.getNick() + " deja " + origen.getNombre() + " y ficha por " + destino.getNombre());

            // --- CORRECCIÓN CLAVE ---
            // 1. Actualizamos la relación en memoria para mantener coherencia (opcional pero recomendable)
            origen.getDeportistas().remove(jugador);
            destino.getDeportistas().add(jugador);

            // 2. Vinculamos el jugador al nuevo equipo
            jugador.setEquipo(destino);

            // 3. GUARDAMOS AL JUGADOR (No al equipo origen).
            // Esto actualiza la FK 'team_id' sin disparar el orphanRemoval del equipo origen.
            deportistaDao.guardar(jugador);
            equipoDao.guardar(destino); // Guardamos destino para asegurar consistencia
        }
    }
}