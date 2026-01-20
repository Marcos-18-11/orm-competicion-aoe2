package com.dam.service;
import java.util.Random;
import com.dam.dao.jpa.EquipoDaoJpa;
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.Partido;
import com.dam.util.CargaDatosIniciales;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Esta clase contiene la lógica de negocio de la simulación.
 * Gestiona el flujo del torneo.
 */
public class SimulacionService {

    private final EntityManager em;
    private final EquipoDaoJpa equipoDao;

    public SimulacionService(EntityManager em) {
        this.em = em;
        // Inicializamos los DAOs necesarios
        this.equipoDao = new EquipoDaoJpa(em);
    }

    /**
     * Método principal que prepara todo el entorno
     */
    public void inicializarTemporada() {
        System.out.println(">>> SERVICE: Iniciando preparación de la temporada 2026...");

        // 1. Cargar los datos iniciales (Usando la clase Util que creamos)
        // No necesitamos abrir transacción aquí porque CargaDatosIniciales usa el EM
        // pero quien llame a este método (el Main) debe gestionar la transacción.
        CargaDatosIniciales.cargarDatos(em);

        System.out.println(">>> SERVICE: Datos cargados. Verificando inscripciones...");

        // 2. Verificar inscritos (Lógica de negocio simple)
        mostrarResumenEquipos();
    }

    private void mostrarResumenEquipos() {
        List<Equipo> equipos = equipoDao.buscarTodos();

        System.out.println("\n------------------------------------------------");
        System.out.println("       LISTADO OFICIAL DE CLANES INSCRITOS      ");
        System.out.println("------------------------------------------------");

        if (equipos.isEmpty()) {
            System.out.println("¡ALERTA! No se han encontrado equipos.");
        } else {
            for (Equipo equipo : equipos) {
                System.out.println("CLAN: " + equipo.getNombre() + " (" + equipo.getDeportistas().size() + " jugadores)");
                System.out.println("   -> Gaming House en: " + equipo.getGamingHouse().getCiudad());
                System.out.println("   -> Sponsors: " + equipo.getPatrocinadores().size());
                System.out.println("------------------------------------------------");
            }
        }
        System.out.println("Total equipos inscritos: " + equipos.size());
    }
    public void jugarTemporada() {
        System.out.println(">>> SERVICE: Comienza la temporada 2026...");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();

        // LIGA SIMPLE (Todos contra todos, una sola vuelta para simplificar V1)
        int jornada = 1;

        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                Equipo local = equipos.get(i);
                Equipo visit = equipos.get(j);

                // Simular resultado (AoE2 suele ser al mejor de 5 o 7)
                int scoreLocal = random.nextInt(5);
                int scoreVisit = random.nextInt(5);

                // Evitamos empate exacto para tener ganador (regla simple)
                if(scoreLocal == scoreVisit) scoreLocal++;

                Equipo ganador = (scoreLocal > scoreVisit) ? local : visit;
                String marcador = scoreLocal + "-" + scoreVisit;

                // Crear objeto Partido y guardarlo (necesitarías un PartidoDao, o persistirlo con em.persist aquí)
                Partido match = new Partido(jornada, local, visit, marcador, ganador);
                em.persist(match); // Guardamos el partido

                // ACTUALIZAR PUNTOS (+3 al ganador)
                ganador.setPuntos(ganador.getPuntos() + 3);
                equipoDao.guardar(ganador); // Actualizamos el equipo en BD

                System.out.println("J" + jornada + ": " + local.getNombre() + " vs " + visit.getNombre() + " -> " + marcador);
            }
            jornada++;
        }
        System.out.println(">>> SERVICE: Temporada finalizada. Clasificación actualizada.");
    }
    public void gestionarFichajesFinTemporada() {
        System.out.println(">>> SERVICE: Abriendo mercado de fichajes 2027...");
        List<Equipo> equipos = equipoDao.buscarTodos();
        Random random = new Random();

        // Simular 3 fichajes aleatorios
        for(int i=0; i<3; i++) {
            // Elegir equipo origen y destino aleatorios
            Equipo origen = equipos.get(random.nextInt(equipos.size()));
            Equipo destino = equipos.get(random.nextInt(equipos.size()));

            if(origen == destino || origen.getDeportistas().isEmpty()) continue;

            // Coger un jugador del origen
            Deportista jugador = origen.getDeportistas().get(0); // Cogemos el primero por simplificar

            // LA CLAVE: Usar los helpers para mantener la coherencia
            origen.removeDeportista(jugador);
            destino.addDeportista(jugador);

            // Guardar cambios
            equipoDao.guardar(origen);
            equipoDao.guardar(destino);

            System.out.println("FICHAJE BOMBA: " + jugador.getNick() + " pasa de " + origen.getNombre() + " a " + destino.getNombre());
        }
    }
}