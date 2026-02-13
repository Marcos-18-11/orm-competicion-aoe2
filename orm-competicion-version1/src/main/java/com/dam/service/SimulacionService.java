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
        System.out.println(" Cargando datos iniciales...");
        CargaDatosIniciales.cargarDatos(em);
    }

    public void jugarLiga() {
        System.out.println(" S COMIENZA LA LIGA AOE2 2026 ");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();
        int jornada = 1;

        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {

                Equipo local = equipos.get(i);
                Equipo visit = equipos.get(j);

                int scoreLocal = random.nextInt(4); // 0 a 3
                int scoreVisit = random.nextInt(4);


                if (scoreLocal == scoreVisit) scoreLocal++;

                Equipo ganador = (scoreLocal > scoreVisit) ? local : visit;
                String marcador = scoreLocal + "-" + scoreVisit;

                Partido match = new Partido(jornada, local, visit, marcador, ganador);
                partidoDao.guardar(match);


                ganador.setPuntos(ganador.getPuntos() + 3);
                equipoDao.guardar(ganador);

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


        for (int k = 0; k < 3; k++) {
            Equipo origen = equipos.get(random.nextInt(equipos.size()));
            Equipo destino = equipos.get(random.nextInt(equipos.size()));

            if (origen.equals(destino) || origen.getDeportistas().isEmpty()) continue;


            Deportista jugador = origen.getDeportistas().get(0);

            System.out.println("   🚨 BREAKING NEWS: " + jugador.getNick() + " abandona " + origen.getNombre() + " y ficha por " + destino.getNombre());


            origen.removeDeportista(jugador);
            destino.addDeportista(jugador);


            equipoDao.guardar(origen);
            equipoDao.guardar(destino);
        }
    }
}