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

    // Relación ManyToOne: Un partido tiene un equipo local
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Equipo local;

    // Relación ManyToOne: Un partido tiene un equipo visitante
    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Equipo visitante;

    // Resultado (ej: "3-1" en mapas de AoE2)
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Equipo ganador; // Puede ser null si es empate (aunque en AoE2 pro no suele haber empate)

    public Partido() {}

    public Partido(int jornada, Equipo local, Equipo visitante, String resultado, Equipo ganador) {
        this.jornada = jornada;
        this.local = local;
        this.visitante = visitante;
        this.resultado = resultado;
        this.ganador = ganador;
    }

    // Getters y Setters necesarios
    public Equipo getGanador() { return ganador; }
    public String getResultado() { return resultado; }
}