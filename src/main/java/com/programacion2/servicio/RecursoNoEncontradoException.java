package com.programacion2.servicio;

/* Se lanza cuando se pide un cliente, vehiculo u orden que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
