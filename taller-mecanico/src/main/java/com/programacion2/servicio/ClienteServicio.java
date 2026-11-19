package com.programacion2.servicio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.programacion2.dominio.Cliente;
import com.programacion2.repositorio.ClienteRepositorio;

@Service
public class ClienteServicio {

    private final ClienteRepositorio repositorio;

    public ClienteServicio(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Cliente> listar() {
        return repositorio.listar(Cliente.class);
    }

    public List<Cliente> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        return repositorio.buscarPorNombreOApellido(texto.trim());
    }

    public Cliente obtener(int id) {
        Cliente cliente = repositorio.consultar(Cliente.class, id);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("No existe el cliente con id " + id + ".");
        }
        return cliente;
    }

    public void crear(String nombre, String apellido, String telefono) {
        validar(nombre, apellido, telefono);
        repositorio.guardar(new Cliente(nombre.trim(), apellido.trim(), telefono.trim()));
    }

    public void modificar(int id, String nombre, String apellido, String telefono) {
        validar(nombre, apellido, telefono);
        obtener(id);

        String nuevoNombre = nombre.trim();
        String nuevoApellido = apellido.trim();
        String nuevoTelefono = telefono.trim();

        repositorio.actualizar(Cliente.class, id, cliente -> {
            cliente.setNombre(nuevoNombre);
            cliente.setApellido(nuevoApellido);
            cliente.setTelefono(nuevoTelefono);
        });
    }

    /* No se permite eliminar un cliente que tiene vehiculos registrados. */
    public void eliminar(int id) {
        Cliente cliente = obtener(id);

        int cantidad = cliente.getVehiculos().size();
        if (cantidad > 0) {
            throw new ReglaNegocioException(
                    "No se puede eliminar a " + cliente.getNombre() + " " + cliente.getApellido()
                            + " porque tiene " + cantidad + " vehículo(s) registrado(s). "
                            + "Elimine primero los vehículos.");
        }

        repositorio.eliminar(Cliente.class, id);
    }

    private void validar(String nombre, String apellido, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre es obligatorio.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new ReglaNegocioException("El apellido es obligatorio.");
        }
        if (nombre.trim().length() > 50 || apellido.trim().length() > 50) {
            throw new ReglaNegocioException("El nombre y el apellido no pueden superar los 50 caracteres.");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new ReglaNegocioException("El teléfono es obligatorio.");
        }
        if (!telefono.trim().matches("[0-9+()\\- ]{6,20}")) {
            throw new ReglaNegocioException(
                    "El teléfono solo puede contener números, espacios, +, - y paréntesis (entre 6 y 20 caracteres).");
        }
    }
}
