package com.programacion2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.programacion2.dominio.Cliente;
import com.programacion2.servicio.ClienteServicio;
import com.programacion2.servicio.OrdenServicio;
import com.programacion2.servicio.VehiculoServicio;

/*
 * Carga datos de ejemplo solo si taller.cargar-datos-ejemplo=true
 * y la base todavia no tiene clientes.
 */
@Component
@ConditionalOnProperty(name = "taller.cargar-datos-ejemplo", havingValue = "true")
public class CargaDatosEjemplo implements CommandLineRunner {

    private final ClienteServicio clienteServicio;
    private final VehiculoServicio vehiculoServicio;
    private final OrdenServicio ordenServicio;

    public CargaDatosEjemplo(ClienteServicio clienteServicio,
                             VehiculoServicio vehiculoServicio,
                             OrdenServicio ordenServicio) {
        this.clienteServicio = clienteServicio;
        this.vehiculoServicio = vehiculoServicio;
        this.ordenServicio = ordenServicio;
    }

    @Override
    public void run(String... args) {

        if (!clienteServicio.listar().isEmpty()) {
            return;
        }

        clienteServicio.crear("Juan", "Perez", "2994001122");
        clienteServicio.crear("Maria", "Gomez", "2994003344");

        Cliente juan = clienteServicio.buscar("Perez").get(0);
        Cliente maria = clienteServicio.buscar("Gomez").get(0);

        vehiculoServicio.crear(juan.getId(), "AB123CD", "Ford", "Fiesta", 2018);
        vehiculoServicio.crear(juan.getId(), "AC456EF", "Fiat", "Cronos", 2021);
        vehiculoServicio.crear(maria.getId(), "AA789GH", "Renault", "Kangoo", 2015);

        ordenServicio.abrir(vehiculoServicio.buscar("AB123CD").get(0).getId(),
                "Cambio de aceite y filtros");
        ordenServicio.abrir(vehiculoServicio.buscar("AA789GH").get(0).getId(),
                "Cambio de correa de distribución");
    }
}
