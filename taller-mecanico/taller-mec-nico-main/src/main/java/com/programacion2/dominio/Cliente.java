package com.programacion2.dominio;

import java.util.ArrayList;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Cliente {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private int id;

    private String nombre;
    private String apellido;
    private String telefono;

    /*
     * Uno a muchos bidireccional: un Cliente tiene muchos Vehiculos.
     * La clave foránea (CLIENTE_ID) está en la tabla VEHICULO,
     * por eso acá se usa mappedBy = "cliente".
     */
    @Persistent(mappedBy = "cliente", defaultFetchGroup = "true")
    private ArrayList<Vehiculo> vehiculos;

    public Cliente() {
        vehiculos = new ArrayList<>();
    }

    public Cliente(String nombre, String apellido, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.vehiculos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public ArrayList<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(ArrayList<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
        vehiculo.setCliente(this);
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id
                + ", nombre=" + nombre
                + ", apellido=" + apellido
                + ", telefono=" + telefono + "}";
    }
}