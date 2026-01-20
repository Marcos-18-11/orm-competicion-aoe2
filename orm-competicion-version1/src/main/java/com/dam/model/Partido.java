package com.dam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int jornada;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Equipo local;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Equipo visitante;

    private String resultado; // Ej: "3-1"

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Equipo ganador;

    public Partido() {}

    public Partido(int jornada, Equipo local, Equipo visitante, String resultado, Equipo ganador) {
        this.jornada = jornada;
        this.local = local;
        this.visitante = visitante;
        this.resultado = resultado;
        this.ganador = ganador;
    }

    // Getters necesarios para consultas
    public Long getId() { return id; }
    public String getResultado() { return resultado; }
    public Equipo getGanador() { return ganador; }
    public Equipo getLocal() { return local; }
    public Equipo getVisitante() { return visitante; }
}