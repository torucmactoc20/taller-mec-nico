package com.programacion2.repositorio;

import java.util.List;
import java.util.function.Consumer;

public interface IRepositorio<T> {

    void guardar(T objeto);

    void guardar(List<T> objetos);

    /* Devuelve null si no existe un objeto con ese id. */
    T consultar(Class<T> clase, int id);

    List<T> listar(Class<T> clase);

    /* Aplica los cambios sobre el objeto administrado, dentro de una transaccion. */
    void actualizar(Class<T> clase, int id, Consumer<T> cambios);

    void eliminar(Class<T> clase, int id);
}
