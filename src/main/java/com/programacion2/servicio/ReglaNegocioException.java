package com.programacion2.servicio;

/* Se lanza cuando se incumple una validacion o regla del negocio. El mensaje se muestra al usuario. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
