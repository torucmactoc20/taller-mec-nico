package com.programacion2.repositorio;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.springframework.stereotype.Repository;

import com.programacion2.dominio.Cliente;
import com.programacion2.dominio.Vehiculo;

@Repository
public class VehiculoRepositorio extends Repositorio<Vehiculo> {

    public VehiculoRepositorio(PersistenceManagerFactory pmf) {
        super(pmf);
    }

    /* Busca vehiculos cuya patente contiene el texto. */
    public List<Vehiculo> buscarPorPatente(String texto) {
        return consultarLista(
                Vehiculo.class,
                "patente.contains(:texto)",
                Map.of("texto", texto));
    }

    /* Devuelve el vehiculo con esa patente exacta, o null si no existe. */
    public Vehiculo buscarExacta(String patente) {
        List<Vehiculo> resultados = consultarLista(
                Vehiculo.class,
                "patente == :patente",
                Map.of("patente", patente));
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    /* Guarda el vehiculo y lo vincula con su cliente en la misma transaccion. */
    public void guardarParaCliente(Vehiculo vehiculo, int clienteId) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Cliente cliente = pm.getObjectById(Cliente.class, clienteId);
            cliente.agregarVehiculo(vehiculo);
            pm.makePersistent(vehiculo);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            pm.close();
        }
    }

    /* Elimina el vehiculo y lo saca de la lista de su cliente. */
    public void eliminarVehiculo(int id) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Vehiculo vehiculo = pm.getObjectById(Vehiculo.class, id);
            if (vehiculo.getCliente() != null) {
                vehiculo.getCliente().getVehiculos().remove(vehiculo);
            }
            pm.deletePersistent(vehiculo);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            pm.close();
        }
    }
}
