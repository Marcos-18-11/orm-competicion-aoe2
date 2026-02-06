package com.dam.model;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/***
 * Representa a un patrocinador, este podrá representar a avarios equipos
 * @author Marcos
 */
@Entity
@Table(name = "sponsors")
public class Patrocinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // MappedBy indica que la tabla intermedia la gestiona 'Equipo'
    //Solo cargará equipos cuando se anecesario, relación many to many, por que como se explicó mas a riba
    @ManyToMany(mappedBy = "patrocinadores", fetch = FetchType.LAZY)
    private Set<Equipo> equipos = new LinkedHashSet<>();
    // Constructor protegido → Hibernate lo usa para crear objetos
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