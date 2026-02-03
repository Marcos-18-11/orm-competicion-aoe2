package com.dam.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // <--- ¡ESTO ES LO QUE HIBERNATE NO ENCONTRABA!
@Table(name = "teams")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "pais_origen")
    private String pais;

    private int puntos;

    // V2: Presupuesto
    @Column(columnDefinition = "double default 1000000")
    private double presupuesto;

    // RELACIONES
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gaming_house_id", referencedColumnName = "id")
    private GamingHouse gamingHouse;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "team_sponsors",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    private List<Patrocinador> patrocinadores = new ArrayList<>();

    // --- AQUÍ ESTABA EL CAMBIO ---
    // Asegúrate de que está así (SIN orphanRemoval):
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    private List<Deportista> deportistas = new ArrayList<>();

    // CONSTRUCTORES
    public Equipo() {}

    public Equipo(String nombre, String pais) {
        this.nombre = nombre;
        this.pais = pais;
        this.puntos = 0;
        this.presupuesto = 1000000;
    }

    // MÉTODOS DE AYUDA (Helpers)
    public void asignarGamingHouse(GamingHouse gh) {
        this.gamingHouse = gh;
        if (gh != null) {
            gh.setEquipo(this);
        }
    }

    public void addPatrocinador(Patrocinador p) {
        if (!patrocinadores.contains(p)) {
            patrocinadores.add(p);
            p.getEquipos().add(this);
        }
    }

    public void addDeportista(Deportista d) {
        deportistas.add(d);
        d.setEquipo(this);
    }

    public void removeDeportista(Deportista d) {
        deportistas.remove(d);
        d.setEquipo(null);
    }

    // GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }
    public GamingHouse getGamingHouse() { return gamingHouse; }
    public void setGamingHouse(GamingHouse gamingHouse) { this.gamingHouse = gamingHouse; }
    public List<Patrocinador> getPatrocinadores() { return patrocinadores; }
    public void setPatrocinadores(List<Patrocinador> patrocinadores) { this.patrocinadores = patrocinadores; }
    public List<Deportista> getDeportistas() { return deportistas; }
    public void setDeportistas(List<Deportista> deportistas) { this.deportistas = deportistas; }

    // equals y hashcode (simplificados por id o nombre)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipo)) return false;
        Equipo equipo = (Equipo) o;
        return getNombre() != null && getNombre().equals(equipo.getNombre());
    }
    @Override
    public int hashCode() { return 31; }
}