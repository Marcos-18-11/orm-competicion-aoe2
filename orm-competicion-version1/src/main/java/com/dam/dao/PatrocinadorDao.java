package com.dam.dao;

import com.dam.model.Patrocinador;
import java.util.List;

public interface PatrocinadorDao {

    void guardar(Patrocinador patrocinador);

    Patrocinador buscarPorId(Long id);

    List<Patrocinador> buscarTodos();

    void eliminar(Patrocinador patrocinador);
}