package com.programacion2.repositorio;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManagerFactory;

import org.springframework.stereotype.Repository;

import com.programacion2.dominio.Cliente;

@Repository
public class ClienteRepositorio extends Repositorio<Cliente> {

    public ClienteRepositorio(PersistenceManagerFactory pmf) {
        super(pmf);
    }

    /* Busca clientes cuyo nombre o apellido contiene el texto (sin distinguir mayusculas). */
    public List<Cliente> buscarPorNombreOApellido(String texto) {
        return consultarLista(
                Cliente.class,
                "apellido.toLowerCase().contains(:texto) || nombre.toLowerCase().contains(:texto)",
                Map.of("texto", texto.toLowerCase()));
    }
}
