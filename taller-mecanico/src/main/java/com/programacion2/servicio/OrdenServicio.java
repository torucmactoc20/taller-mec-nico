package com.programacion2.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.programacion2.dominio.EstadoOrden;
import com.programacion2.dominio.OrdenDeTrabajo;
import com.programacion2.dominio.Vehiculo;
import com.programacion2.repositorio.OrdenRepositorio;

/*
 * Operaciones propias del negocio: abrir una orden, iniciar la reparacion
 * y finalizarla. Cada una valida las reglas antes de tocar la base.
 */
@Service
public class OrdenServicio {

    private final OrdenRepositorio repositorio;
    private final VehiculoServicio vehiculoServicio;

    public OrdenServicio(OrdenRepositorio repositorio, VehiculoServicio vehiculoServicio) {
        this.repositorio = repositorio;
        this.vehiculoServicio = vehiculoServicio;
    }

    /* Si estado es null devuelve todas las ordenes. */
    public List<OrdenDeTrabajo> listar(EstadoOrden estado) {
        if (estado == null) {
            return repositorio.listar(OrdenDeTrabajo.class);
        }
        return repositorio.listarPorEstado(estado);
    }

    public OrdenDeTrabajo obtener(int id) {
        OrdenDeTrabajo orden = repositorio.consultar(OrdenDeTrabajo.class, id);
        if (orden == null) {
            throw new RecursoNoEncontradoException("No existe la orden de trabajo con id " + id + ".");
        }
        return orden;
    }

    /*
     * Abre una orden para un vehiculo.
     * Regla: un vehiculo no puede tener mas de una orden en curso
     * (abierta o en reparacion) al mismo tiempo.
     */
    public void abrir(Integer vehiculoId, String descripcion) {

        if (vehiculoId == null) {
            throw new ReglaNegocioException("Debe seleccionar el vehículo.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaNegocioException("La descripción del trabajo es obligatoria.");
        }
        String texto = descripcion.trim();
        if (texto.length() < 5 || texto.length() > 255) {
            throw new ReglaNegocioException("La descripción debe tener entre 5 y 255 caracteres.");
        }

        Vehiculo vehiculo = vehiculoServicio.obtener(vehiculoId);

        boolean tieneOrdenEnCurso = vehiculo.getOrdenes().stream()
                .anyMatch(o -> o.getEstado() != EstadoOrden.FINALIZADA);
        if (tieneOrdenEnCurso) {
            throw new ReglaNegocioException(
                    "El vehículo " + vehiculo.getPatente() + " ya tiene una orden de trabajo en curso. "
                            + "Debe finalizarla antes de abrir otra.");
        }

        repositorio.guardarParaVehiculo(new OrdenDeTrabajo(texto, LocalDate.now(), 0), vehiculoId);
    }

    /* ABIERTA -> EN_REPARACION */
    public void iniciarReparacion(int id) {
        OrdenDeTrabajo orden = obtener(id);

        if (orden.getEstado() != EstadoOrden.ABIERTA) {
            throw new ReglaNegocioException("Solo se puede iniciar la reparación de una orden abierta.");
        }

        repositorio.actualizar(OrdenDeTrabajo.class, id,
                o -> o.setEstado(EstadoOrden.EN_REPARACION));
    }

    /* EN_REPARACION -> FINALIZADA, registrando el costo final. */
    public void finalizar(int id, Double costo) {
        OrdenDeTrabajo orden = obtener(id);

        if (orden.getEstado() != EstadoOrden.EN_REPARACION) {
            throw new ReglaNegocioException(
                    "Solo se puede finalizar una orden que está en reparación.");
        }
        if (costo == null || costo.isNaN() || costo <= 0) {
            throw new ReglaNegocioException("Debe ingresar un costo final mayor a cero.");
        }
        if (costo > 100_000_000) {
            throw new ReglaNegocioException("El costo ingresado es demasiado alto.");
        }

        repositorio.actualizar(OrdenDeTrabajo.class, id, o -> {
            o.setCosto(costo);
            o.setEstado(EstadoOrden.FINALIZADA);
        });
    }
}
