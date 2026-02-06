package com.dam.model;

import jakarta.persistence.*;

/***
 * Relación 1 a 1 con Equipo (Sede de entrenamiento).
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
    /*
    * Una GH pertenece a un equipo solamente
    * Con Lazy solo carga a su equipo cuando es necesario y así optimizamos rendimiento
    * Siempre a de tener un equipo asociado
    * Clave foranea que apunta al id del equipo
    * unique = true ; solo un equipo puede tener esta gaming house
    * no puede existir una GH sin equipo
    * */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", unique = true, nullable = false)
    private Equipo equipo;
/*
* El constructor vacío protected es obligatorio para Hibernate.
    Hibernate lo usa para crear objetos desde la base de datos.
    No debe ser público para evitar que el programador cree objetos incompletos.
* */
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