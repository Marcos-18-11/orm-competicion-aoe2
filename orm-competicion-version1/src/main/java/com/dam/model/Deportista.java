package com.dam.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/***
 * Esta clase representa a los jugadores profesionales.
 * @author Marcos
 * @version 1.0
 */
@Entity
@Table(name = "players")
@NamedQuery(
        name = "Deportista.buscarPorNacionalidad",
        query = "SELECT d FROM Deportista d WHERE d.nacionalidad = :nacionalidad"
)
public class Deportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nick;

    @Column(columnDefinition = "double default 50000") // 50k salario base
    private double salario;

    @Column(nullable = false)
    private String nacionalidad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Equipo equipo;

    protected Deportista() {}

    public Deportista(String nick, String nacionalidad, LocalDate fechaNacimiento) {
        this.nick = nick;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Setter protected/package para usarlo en el helper del Equipo
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
    public Long getId() {
        return id;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public Equipo getEquipo() { return equipo; }
    public String getNick() { return nick; }

    @Override
    public String toString() {
        return "Deportista{id=" + id + ", nick='" + nick + "'}";
    }
}