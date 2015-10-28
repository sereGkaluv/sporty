package at.sporty.team1.persistence.api;

import org.hibernate.criterion.Criterion;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IGenericDAO<T> {
    /**
     * Returns all existing objects of the given type in the data store.
     * @return a list of all objects of the type {@code T}.
     * @throws PersistenceException
     */
    List<T> findAll() throws PersistenceException;

    /**
     * Returns all existing objects of the given type in the data store that
     * match the given criterion(s).
     * @param criterion criterion to be matched.
     * @return a list of all objects of the type {@code T} that match the
     * given criterion(s).
     * @throws PersistenceException
     */
    List<T> findByCriteria(Criterion... criterion) throws PersistenceException;

    /**
     * Returns a list of objects that are matched by a given hql query.
     * @param hql The hibernate query with named parameters.
     * @return The found objects.
     * @throws SQLException
     */
    List<T> findByHQL(String hql);

    /**
     * Returns a list of objects that are matched by a given hql query.
     * @param hql The hibernate query with named parameters.
     * @param map The map with the named parameters. Example: {@code HashMap<String,Object>}
     * @return The found objects.
     * @throws SQLException
     */
    List<T> findByHQL(String hql, Map map);

    /**
     * Returns a specific object identified by its id from the data store.
     * @param id an object representing the id of the specific entity
     * (usually it is a unique {@code Integer} value).
     * @return the entity with the matching id.
     * @throws PersistenceException
     */
    T findById(Serializable id) throws PersistenceException;

    /**
     * Refreshes the given object to the actual state from the data store.
     * All dirty (unsaved) changes may be lost.
     * @param object the object to be refreshed.
     * @throws PersistenceException
     */
    void refreshToActualState(T object) throws PersistenceException;

    /**
     * Saves the given object to the data store or updates the entry, if it
     * already exists.
     * @param object the object to be saved or updated.
     * @throws PersistenceException
     */
    void saveOrUpdate(T object) throws PersistenceException;

    /**
     * Deletes an existing object from the data store.
     * @param object the object to be deleted from the data store.
     * @throws PersistenceException
     */
    void delete(T object) throws PersistenceException;
}