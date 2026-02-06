package com.dam.util;

import com.dam.dao.hibernate.EquipoDaoHibernate;
import com.dam.dao.hibernate.PatrocinadorDaoHibernate;
import com.dam.model.Deportista;
import com.dam.model.Equipo;
import com.dam.model.GamingHouse;
import com.dam.model.Patrocinador;
import com.github.javafaker.Faker;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CargaDatosIniciales {

    private static final String[] CLANES_REALES = {
            "GamerLegion", "Aftermath", "Team Suomi", "White Wolf Palace",
            "Heresy", "Amigos", "Barles & Cia", "Suicide Squad"
    };

    private static final String[] JUGADORES_PRO = {
            "TheViper", "Hera", "TaToH", "Liereyy", "Daut", "JorDan", "Nicov",
            "Capoch", "Villese", "Yo", "ACCM", "Mr_Yo", "Sitaux", "Ganji"
    };

    // Sesión
    public static void cargarDatos(Session session) {
        Faker faker = new Faker();
        Random random = new Random();

        // CAMBIO: Instanciamos los DAOs de Hibernate
        EquipoDaoHibernate equipoDao = new EquipoDaoHibernate(session);
        PatrocinadorDaoHibernate patrocinadorDao = new PatrocinadorDaoHibernate(session);

        System.out.println("--- INICIO CARGA DE DATOS (AoE2 Real Data - Hibernate Nativo) ---");

        //  PATROCINADORES
        List<Patrocinador> listaPatrocinadores = new ArrayList<>();
        String[] sponsorsReales = {"Red Bull", "Microsoft", "CaptureAge", "Twitch"};

        for (String nombre : sponsorsReales) {
            Patrocinador p = new Patrocinador(nombre);
            patrocinadorDao.guardar(p);
            listaPatrocinadores.add(p);
        }
        for (int i = 0; i < 6; i++) {
            Patrocinador p = new Patrocinador(faker.company().name());
            patrocinadorDao.guardar(p);
            listaPatrocinadores.add(p);
        }

        //  EQUIPOS
        int indexJugador = 0;

        for (String nombreClan : CLANES_REALES) {
            Equipo equipo = new Equipo(nombreClan, faker.country().name());

            // Gaming House
            GamingHouse gh = new GamingHouse(faker.address().city(), random.nextInt(10) + 5);
            equipo.asignarGamingHouse(gh);

            // Patrocinadores
            equipo.addPatrocinador(listaPatrocinadores.get(random.nextInt(listaPatrocinadores.size())));
            equipo.addPatrocinador(listaPatrocinadores.get(random.nextInt(listaPatrocinadores.size())));

            // Jugadores
            if (indexJugador < JUGADORES_PRO.length) {
                String nombrePro = JUGADORES_PRO[indexJugador++];
                crearJugador(equipo, nombrePro, faker);
            }

            for (int j = 0; j < 3; j++) {
                crearJugador(equipo, faker.esports().player(), faker);
            }

            equipoDao.guardar(equipo);
            System.out.println("Generado Clan: " + nombreClan);
        }

        System.out.println("--- FIN CARGA DE DATOS ---");
    }

    private static void crearJugador(Equipo equipo, String nick, Faker faker) {
        LocalDate nacimiento = faker.date().birthday(18, 35).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        Deportista jugador = new Deportista(nick, equipo.getNombre().substring(0,3).toUpperCase(), nacimiento);
        equipo.addDeportista(jugador);
    }
}