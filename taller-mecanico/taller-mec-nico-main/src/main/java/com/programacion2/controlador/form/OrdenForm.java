package com.programacion2.controlador.form;

/* Datos del formulario para abrir una orden de trabajo. */
public class OrdenForm {

    private Integer vehiculoId;
    private String descripcion;

    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Integer vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
