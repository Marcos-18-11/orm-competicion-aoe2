package com.dam.model;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "sponsors")
public class Patrocinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToMany(mappedBy = "patrocinadores", fetch = FetchType.LAZY)
    private Set<Equipo> equipos = new LinkedHashSet<>();

    protected Patrocinador() {}

    public Patrocinador(String nombre) {
        this.nombre = nombre;
    }
    public Long getId() {
        return id;
    }

    public Set<Equipo> getEquipos() { return equipos; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return "Patrocinador{id=" + id + ", nombre='" + nombre + "'}";
    }
}