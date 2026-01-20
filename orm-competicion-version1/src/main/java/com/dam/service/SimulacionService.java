package com.dam.service;

import com.dam.dao.jpa.EquipoDaoJpa;
import com.dam.dao.jpa.PartidoDaoJpa;
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.Partido;
import com.dam.util.CargaDatosIniciales;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Random;

public class SimulacionService {

    private final EntityManager em;
    private final EquipoDaoJpa equipoDao;
    private final PartidoDaoJpa partidoDao;

    public SimulacionService(EntityManager em) {
        this.em = em;
        this.equipoDao = new EquipoDaoJpa(em);
        this.partidoDao = new PartidoDaoJpa(em);
    }

    public void inicializarTemporada() {
        System.out.println(">>> SERVICE: Cargando datos iniciales...");
        CargaDatosIniciales.cargarDatos(em);
    }

    public void jugarLiga() {
        System.out.println(">>> SERVICE: ⚔️ COMIENZA LA LIGA AOE2 2026 ⚔️");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();
        int jornada = 1;

        // Algoritmo: Todos contra Todos (Solo ida)
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {

                Equipo local = equipos.get(i);
                Equipo visit = equipos.get(j);

                // 1. Simular resultado (Bo5 - Best of 5)
                int scoreLocal = random.nextInt(4); // 0 a 3
                int scoreVisit = random.nextInt(4);

                // Evitamos empate (en e-sports suele haber ganador)
                if (scoreLocal == scoreVisit) scoreLocal++;

                Equipo ganador = (scoreLocal > scoreVisit) ? local : visit;
                String marcador = scoreLocal + "-" + scoreVisit;

                // 2. Guardar Partido
                Partido match = new Partido(jornada, local, visit, marcador, ganador);
                partidoDao.guardar(match);

                // 3. Sumar Puntos (+3 al ganador)
                ganador.setPuntos(ganador.getPuntos() + 3);
                equipoDao.guardar(ganador); // Actualizamos en BD

                System.out.println("   [J" + jornada + "] " + local.getNombre() + " " + marcador + " " + visit.getNombre());
            }
            jornada++;
        }
        System.out.println(">>> SERVICE: Liga finalizada. Puntos actualizados.");
    }

    public void gestionarFichajes() {
        System.out.println("\n>>> SERVICE: 💰 ABRIENDO MERCADO DE FICHAJES 2027 💰");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();

        // Realizamos 3 movimientos aleatorios
        for (int k = 0; k < 3; k++) {
            Equipo origen = equipos.get(random.nextInt(equipos.size()));
            Equipo destino = equipos.get(random.nextInt(equipos.size()));

            if (origen.equals(destino) || origen.getDeportistas().isEmpty()) continue;

            // Cogemos al primer jugador de la lista
            Deportista jugador = origen.getDeportistas().get(0);

            System.out.println("   🚨 BREAKING NEWS: " + jugador.getNick() + " abandona " + origen.getNombre() + " y ficha por " + destino.getNombre());

            // IMPORTANTE: Gestión JPA de la relación
            origen.removeDeportista(jugador);
            destino.addDeportista(jugador);

            // Guardamos cambios
            equipoDao.guardar(origen);
            equipoDao.guardar(destino);
        }
    }
}