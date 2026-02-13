package com.dam.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "teams")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "int default 0")
    private int puntos = 0;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "pais_origen", length = 60)
    private String pais;


    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Deportista> deportistas = new ArrayList<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "team_sponsors",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    private Set<Patrocinador> patrocinadores = new LinkedHashSet<>();


    @OneToOne(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private GamingHouse gamingHouse;

    protected Equipo() {}

    public Equipo(String nombre, String pais) {
        this.nombre = nombre;
        this.pais = pais;
    }




    public void addDeportista(Deportista deportista) {
        if (deportista == null) return;
        deportistas.add(deportista);
        deportista.setEquipo(this);
    }

    public void removeDeportista(Deportista deportista) {
        if (deportista == null) return;
        deportistas.remove(deportista);
        deportista.setEquipo(null);
    }


    public void addPatrocinador(Patrocinador patrocinador) {
        if (patrocinador == null) return;
        patrocinadores.add(patrocinador);
        patrocinador.getEquipos().add(this);
    }

    public void removePatrocinador(Patrocinador patrocinador) {
        if (patrocinador == null) return;
        patrocinadores.remove(patrocinador);
        patrocinador.getEquipos().remove(this);
    }

    // Helper 1:1 Gaming House
    public void asignarGamingHouse(GamingHouse gh) {
        if (gh == null) return;
        this.gamingHouse = gh;
        gh.setEquipo(this);
    }
    public String getPais() {
        return pais;
    }
    // Getters básicos
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public List<Deportista> getDeportistas() { return deportistas; }
    public Set<Patrocinador> getPatrocinadores() { return patrocinadores; }
    public GamingHouse getGamingHouse() { return gamingHouse; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    @Override
    public String toString() {
        return "Equipo{id=" + id + ", nombre='" + nombre + "'}";
    }
}