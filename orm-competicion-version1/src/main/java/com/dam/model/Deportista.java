package com.dam.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/***
 * Esta clase representa a los jugadores profesionales.
 * @author Marcos
 * @version 1.0
 */

@Entity // Esta clase será una tabla en la base de datos
@Table(name = "players") // Nombre real de la tabla en la base de datos

// Consulta predefinida (NamedQuery) para buscar deportistas por nacionalidad
@NamedQuery(
        name = "Deportista.buscarPorNacionalidad", // Nombre para usarla en el código
        query = "SELECT d FROM Deportista d WHERE d.nacionalidad = :nacionalidad" // Consulta JPQL
)
public class Deportista {

    @Id // clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincremental generado por la BD
    private Long id;

    @Column(nullable = false, length = 50)
    // Columna obligatoria (no NULL) y máximo 50 caracteres
    private String nick;

    @Column(columnDefinition = "double default 50000")
    // Valor por defecto en la BD  salario base de 50k
    private double salario;

    @Column(nullable = false)
    // Nacionalidad obligatoria (no puede ser NULL)
    private String nacionalidad;

    @Column(name = "fecha_nacimiento")
    // Nombre real de la columna en la BD
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    // Muchos deportistas pueden pertenecer a un mismo equipo
    // LAZY = solo carga el equipo cuando se necesita (más eficiente)
    @JoinColumn(name = "team_id")
    // Clave foránea en la tabla players  apunta al id del equipo
    private Equipo equipo;

    // Constructor protegido para Hibernate (lo necesita para crear objetos)
    protected Deportista() {}

    // Constructor principal para crear deportistas desde el código
    public Deportista(String nick, String nacionalidad, LocalDate fechaNacimiento) {
        this.nick = nick;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Setter usado desde el helper de Equipo (addDeportista)
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getNacionalidad() { return nacionalidad; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public Equipo getEquipo() { return equipo; }
    public String getNick() { return nick; }

    @Override
    public String toString() {
        return "Deportista{id=" + id + ", nick='" + nick + "'}";
    }
}
