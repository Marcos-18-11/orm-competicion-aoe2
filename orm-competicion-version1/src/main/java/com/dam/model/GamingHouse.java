package com.dam.model;

import jakarta.persistence.*;

/***
 * Relación 1 a 1 con Equipo (Sede de entrenamiento).
 * @author Tu Nombre
 */
@Entity
@Table(name = "gaming_houses")
public class GamingHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ciudad;

    @Column(name = "capacidad_pcs")
    private int capacidad;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", unique = true, nullable = false)
    private Equipo equipo;

    protected GamingHouse() {}

    public GamingHouse(String ciudad, int capacidad) {
        this.ciudad = ciudad;
        this.capacidad = capacidad;
    }

    void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
    public Long getId() {
        return id;
    }

    public String getCiudad() {
        return ciudad;
    }

    public int getCapacidad() {
        return capacidad;
    }

    @Override
    public String toString() {
        return "GamingHouse{ciudad='" + ciudad + "'}";
    }
}