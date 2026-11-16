package com.programacion2.repositorio;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.springframework.stereotype.Repository;

import com.programacion2.dominio.EstadoOrden;
import com.programacion2.dominio.OrdenDeTrabajo;
import com.programacion2.dominio.Vehiculo;

@Repository
public class OrdenRepositorio extends Repositorio<OrdenDeTrabajo> {

    public OrdenRepositorio(PersistenceManagerFactory pmf) {
        super(pmf);
    }

    public List<OrdenDeTrabajo> listarPorEstado(EstadoOrden estado) {
        return consultarLista(
                OrdenDeTrabajo.class,
                "estado == :estado",
                Map.of("estado", estado));
    }

    /* Guarda la orden y la vincula con su vehiculo en la misma transaccion. */
    public void guardarParaVehiculo(OrdenDeTrabajo orden, int vehiculoId) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Vehiculo vehiculo = pm.getObjectById(Vehiculo.class, vehiculoId);
            vehiculo.agregarOrden(orden);
            pm.makePersistent(orden);

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
