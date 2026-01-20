package com.dam.dao;
import com.dam.model.Partido;
import java.util.List;

public interface PartidoDao {
    void guardar(Partido partido);
    List<Partido> buscarTodos();
}