package com.programacion2.servicio;

import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;

import com.programacion2.dominio.Vehiculo;
import com.programacion2.repositorio.VehiculoRepositorio;

@Service
public class VehiculoServicio {

    private static final int ANIO_MINIMO = 1950;

    private final VehiculoRepositorio repositorio;
    private final ClienteServicio clienteServicio;

    public VehiculoServicio(VehiculoRepositorio repositorio, ClienteServicio clienteServicio) {
        this.repositorio = repositorio;
        this.clienteServicio = clienteServicio;
    }

    public List<Vehiculo> listar() {
        return repositorio.listar(Vehiculo.class);
    }

    public List<Vehiculo> buscar(String patente) {
        if (patente == null || patente.isBlank()) {
            return listar();
        }
        return repositorio.buscarPorPatente(normalizarPatente(patente));
    }

    public Vehiculo obtener(int id) {
        Vehiculo vehiculo = repositorio.consultar(Vehiculo.class, id);
        if (vehiculo == null) {
            throw new RecursoNoEncontradoException("No existe el vehículo con id " + id + ".");
        }
        return vehiculo;
    }

    public void crear(Integer clienteId, String patente, String marca, String modelo, Integer anio) {

        if (clienteId == null) {
            throw new ReglaNegocioException("Debe seleccionar el cliente dueño del vehículo.");
        }
        clienteServicio.obtener(clienteId);

        String patenteNormalizada = validar(patente, marca, modelo, anio);

        if (repositorio.buscarExacta(patenteNormalizada) != null) {
            throw new ReglaNegocioException("Ya existe un vehículo con la patente " + patenteNormalizada + ".");
        }

        repositorio.guardarParaCliente(
                new Vehiculo(patenteNormalizada, marca.trim(), modelo.trim(), anio),
                clienteId);
    }

    public void modificar(int id, String patente, String marca, String modelo, Integer anio) {

        obtener(id);

        String patenteNormalizada = validar(patente, marca, modelo, anio);

        Vehiculo existente = repositorio.buscarExacta(patenteNormalizada);
        if (existente != null && existente.getId() != id) {
            throw new ReglaNegocioException("Ya existe otro vehículo con la patente " + patenteNormalizada + ".");
        }

        String nuevaMarca = marca.trim();
        String nuevoModelo = modelo.trim();

        repositorio.actualizar(Vehiculo.class, id, vehiculo -> {
            vehiculo.setPatente(patenteNormalizada);
            vehiculo.setMarca(nuevaMarca);
            vehiculo.setModelo(nuevoModelo);
            vehiculo.setAnio(anio);
        });
    }

    /* No se permite eliminar un vehiculo que tiene ordenes de trabajo registradas. */
    public void eliminar(int id) {
        Vehiculo vehiculo = obtener(id);

        int cantidad = vehiculo.getOrdenes().size();
        if (cantidad > 0) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el vehículo " + vehiculo.getPatente()
                            + " porque tiene " + cantidad + " orden(es) de trabajo registrada(s).");
        }

        repositorio.eliminarVehiculo(id);
    }

    /* Valida los datos y devuelve la patente normalizada (mayusculas, sin espacios ni guiones). */
    private String validar(String patente, String marca, String modelo, Integer anio) {

        if (patente == null || patente.isBlank()) {
            throw new ReglaNegocioException("La patente es obligatoria.");
        }
        String patenteNormalizada = normalizarPatente(patente);
        if (!patenteNormalizada.matches("[A-Z]{2}[0-9]{3}[A-Z]{2}|[A-Z]{3}[0-9]{3}")) {
            throw new ReglaNegocioException(
                    "La patente no es válida. Use el formato AB123CD (Mercosur) o ABC123 (anterior).");
        }
        if (marca == null || marca.isBlank()) {
            throw new ReglaNegocioException("La marca es obligatoria.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new ReglaNegocioException("El modelo es obligatorio.");
        }
        if (marca.trim().length() > 50 || modelo.trim().length() > 50) {
            throw new ReglaNegocioException("La marca y el modelo no pueden superar los 50 caracteres.");
        }
        if (anio == null) {
            throw new ReglaNegocioException("El año es obligatorio.");
        }
        int anioMaximo = Year.now().getValue() + 1;
        if (anio < ANIO_MINIMO || anio > anioMaximo) {
            throw new ReglaNegocioException("El año debe estar entre " + ANIO_MINIMO + " y " + anioMaximo + ".");
        }
        return patenteNormalizada;
    }

    private String normalizarPatente(String patente) {
        return patente.trim().toUpperCase().replace(" ", "").replace("-", "");
    }
}
