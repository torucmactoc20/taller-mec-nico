package com.programacion2.dominio;

import java.time.LocalDate;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class OrdenDeTrabajo {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private int id;

    private String descripcion;
    private LocalDate fechaIngreso;
    private EstadoOrden estado;
    private double costo;

    /*
     * Lado "muchos" de Vehiculo -> OrdenDeTrabajo. Propietario de la
     * relación: la clave foránea se guarda en la columna VEHICULO_ID.
     */
    @Persistent(defaultFetchGroup = "true")
    @Column(name = "VEHICULO_ID")
    private Vehiculo vehiculo;

    public OrdenDeTrabajo() {
    }

    public OrdenDeTrabajo(String descripcion, LocalDate fechaIngreso, double costo) {
        this.descripcion = descripcion;
        this.fechaIngreso = fechaIngreso;
        this.costo = costo;
        this.estado = EstadoOrden.ABIERTA;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    @Override
    public String toString() {
        return "OrdenDeTrabajo{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", estado=" + estado +
                ", costo=" + costo +
                '}';
    }
}
