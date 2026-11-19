package com.programacion2.repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

/*
 * Repositorio generico: es la unica capa que habla con JDO.
 * El PersistenceManagerFactory lo crea Spring una sola vez y se inyecta.
 *
 * Cada operacion abre su PersistenceManager, usa una transaccion
 * (en las escrituras) y lo cierra siempre en el bloque finally.
 */
public class Repositorio<T> implements IRepositorio<T> {

    /* Profundidad de navegacion de relaciones al devolver objetos desconectados. */
    private static final int PROFUNDIDAD = 3;

    protected final PersistenceManagerFactory pmf;

    public Repositorio(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    @Override
    public void guardar(T objeto) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            pm.makePersistent(objeto);
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

    @Override
    public void guardar(List<T> objetos) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            pm.makePersistentAll(objetos);
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

    @Override
    public T consultar(Class<T> clase, int id) {

        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setDetachAllOnCommit(true);
        pm.getFetchPlan().setMaxFetchDepth(PROFUNDIDAD);
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            T objeto = pm.getObjectById(clase, id);
            pm.retrieve(objeto);
            tx.commit();
            return objeto;

        } catch (JDOObjectNotFoundException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            return null;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            pm.close();
        }
    }

    @Override
    public List<T> listar(Class<T> clase) {
        return consultarLista(clase, null, null);
    }

    @Override
    public void actualizar(Class<T> clase, int id, Consumer<T> cambios) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            T objeto = pm.getObjectById(clase, id);
            cambios.accept(objeto);
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

    @Override
    public void eliminar(Class<T> clase, int id) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            T objeto = pm.getObjectById(clase, id);
            pm.deletePersistent(objeto);
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

    /*
     * Consulta JDOQL con filtro y parametros con nombre (":nombre").
     * Si filtro es null devuelve todos los objetos de la clase.
     * Los resultados salen ordenados por id y desconectados del PersistenceManager.
     */
    protected List<T> consultarLista(Class<T> clase, String filtro, Map<String, Object> parametros) {

        PersistenceManager pm = pmf.getPersistenceManager();
        pm.getFetchPlan().setMaxFetchDepth(PROFUNDIDAD);
        Transaction tx = pm.currentTransaction();
        Query<T> query = null;

        try {
            tx.begin();

            query = pm.newQuery(clase);
            if (filtro != null) {
                query.filter(filtro);
            }
            query.setOrdering("id ascending");
            if (parametros != null) {
                query.setNamedParameters(parametros);
            }

            List<T> resultados = query.executeList();

            pm.retrieveAll(resultados, true);

            List<T> resultadosSeparados = new ArrayList<>(pm.detachCopyAll(resultados));

            tx.commit();

            return resultadosSeparados;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            if (query != null) {
                query.closeAll();
            }
            pm.close();
        }
    }
}
