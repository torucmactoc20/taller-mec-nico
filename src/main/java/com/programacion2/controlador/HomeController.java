package com.programacion2.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.programacion2.dominio.EstadoOrden;
import com.programacion2.servicio.ClienteServicio;
import com.programacion2.servicio.OrdenServicio;
import com.programacion2.servicio.VehiculoServicio;

@Controller
public class HomeController {

    private final ClienteServicio clienteServicio;
    private final VehiculoServicio vehiculoServicio;
    private final OrdenServicio ordenServicio;

    public HomeController(ClienteServicio clienteServicio,
                          VehiculoServicio vehiculoServicio,
                          OrdenServicio ordenServicio) {
        this.clienteServicio = clienteServicio;
        this.vehiculoServicio = vehiculoServicio;
        this.ordenServicio = ordenServicio;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clienteServicio.listar().size());
        model.addAttribute("totalVehiculos", vehiculoServicio.listar().size());
        model.addAttribute("ordenesAbiertas", ordenServicio.listar(EstadoOrden.ABIERTA).size());
        model.addAttribute("ordenesEnReparacion", ordenServicio.listar(EstadoOrden.EN_REPARACION).size());
        model.addAttribute("ordenesFinalizadas", ordenServicio.listar(EstadoOrden.FINALIZADA).size());
        return "index";
    }
}
