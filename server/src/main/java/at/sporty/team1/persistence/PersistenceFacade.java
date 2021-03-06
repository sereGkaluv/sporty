package at.sporty.team1.persistence;

import at.sporty.team1.persistence.api.*;
import at.sporty.team1.persistence.daos.*;
import at.sporty.team1.persistence.util.HibernateSessionUtil;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.function.Function;

/**
 * Created by sereGkaluv on 27-Oct-15.
 */
public class PersistenceFacade {

    /**
     * Returns an implementation of the IGenericDAO interface for the specified
     * class.
     *
     * @param domainClass the class of the domain object.
     * @param <T> the type of the domain object.
     * @return an instance of IGenericDAO with operations for the specified class.
     */
    public static <T> IGenericDAO<T> getNewGenericDAO(Class<T> domainClass) {
        return new HibernateGenericDAO<>(domainClass);
    }

    /**
     * Returns an implementation of the IMemberDAO interface providing
     * further operations with memberList.
     *
     * @return an instance of IMemberDAO.
     */
    public static IMemberDAO getNewMemberDAO() {
        return new MemberDAO();
    }

    /**
     * Returns an implementation of the ITeamDAO interface providing
     * further operations with teams.
     *
     * @return an instance of ITeamDAO.
     */
    public static ITeamDAO getNewTeamDAO() {
        return new TeamDAO();
    }

    /**
     * Returns an implementation of the IDepartmentDAO interface providing
     * further operations with teams.
     *
     * @return an instance of IDepartmentDAO.
     */
    public static IDepartmentDAO getNewDepartmentDAO() {
        return new DepartmentDAO();
    }

    /**
     * Returns an implementation of the ITournamentDAO interface providing
     * further operations with teams (remove, add..).
     *
     * @return an instance of ITournamentDAO.
     */
    public static ITournamentDAO getNewTournamentDAO() {
        return new TournamentDAO();
    }

    /**
     * Initializes lazy-loaded properties and collections.
     *
     * @param entityObject instance of the target entity of type <T>.
     * @param proxyFunction getterMethod for lazy property.
     */
    public static <T> void forceLoadLazyProperty(T entityObject, Function<T, ?> proxyFunction) {
        try {
            HibernateSessionUtil.getInstance().makeSimpleTransaction(session -> {
                session.update(entityObject);
                Hibernate.initialize(proxyFunction.apply(entityObject));
            });
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Returns id of the entity-proxy object without initializing it.
     *
     * @param proxy entity-proxy of type type <T> object id of which will be returned.
     * @return {@code Serializable} id of the proxy object.
     */
    public static <T> Serializable getProxyId(T proxy) {
        if (proxy != null && proxy instanceof HibernateProxy) {

            LazyInitializer lazyInitializer = ((HibernateProxy) proxy).getHibernateLazyInitializer();
            if (lazyInitializer.isUninitialized()) {
                return lazyInitializer.getIdentifier();
            }
        }
        return null;
    }
}
