package com.dam.dao;

import com.dam.model.Equipo;
import java.util.List;

public interface EquipoDao {

    void guardar(Equipo equipo);

    Equipo buscarPorId(Long id);

    List<Equipo> buscarTodos();

    void eliminar(Equipo equipo);


    Equipo buscarPorNombre(String nombre);
}