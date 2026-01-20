package com.dam.dao;

import com.dam.model.Deportista;
import java.util.List;

public interface DeportistaDao {

    void guardar(Deportista deportista);

    Deportista buscarPorId(Long id);

    List<Deportista> buscarTodos();

    // Método extra: Muy útil para saber quién juega en qué equipo
    List<Deportista> buscarPorEquipo(Long idEquipo);

    void eliminar(Deportista deportista);
}