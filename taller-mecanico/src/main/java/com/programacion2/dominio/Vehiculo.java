package com.programacion2.dominio;

import java.util.ArrayList;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

@PersistenceCapable(detachable = "true")
public class Vehiculo {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private int id;

    @Unique
    private String patente;

    private String marca;
    private String modelo;
    private int anio;

    /*
     * Lado "muchos" de Cliente -> Vehiculo. Es el propietario de la
     * relación: la clave foránea se guarda en la columna CLIENTE_ID.
     */
    @Persistent(defaultFetchGroup = "true")
    @Column(name = "CLIENTE_ID")
    private Cliente cliente;

    /*
     * Uno a muchos bidireccional: un Vehiculo tiene muchas
     * OrdenesDeTrabajo. La clave foránea está en ORDENDETRABAJO.
     */
    @Persistent(mappedBy = "vehiculo", defaultFetchGroup = "true")
    private ArrayList<OrdenDeTrabajo> ordenes;

    public Vehiculo() {
        ordenes = new ArrayList<>();
    }

    public Vehiculo(String patente, String marca, String modelo, int anio) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.ordenes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ArrayList<OrdenDeTrabajo> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(ArrayList<OrdenDeTrabajo> ordenes) {
        this.ordenes = ordenes;
    }

    public void agregarOrden(OrdenDeTrabajo orden) {
        ordenes.add(orden);
        orden.setVehiculo(this);
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "id=" + id +
                ", patente='" + patente + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                '}';
    }
}
