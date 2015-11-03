package at.sporty.team1.persistence.daos;

import at.sporty.team1.domain.Member;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Represents a concrete DAO for Members
 */
public class MemberDAO extends HibernateGenericDAO<Member> {
    /**
     * Creates a new patient DAO.
     */
    public MemberDAO() {
        super(Member.class);
    }

    /**
     * Find member by name.
     *
     * @param name          The name (firstname or lastname).
     * @param caseSensitive True if search should be case sensitive.
     * @return The found member.
     */
    public List<Member> findByName(String name, boolean caseSensitive) throws PersistenceException {
        Criterion criterion;
        if (caseSensitive) {
            criterion = Restrictions.or(
                    Restrictions.like("fname", name, MatchMode.EXACT),
                    Restrictions.like("lname", name, MatchMode.EXACT));
        } else {
            criterion = Restrictions.or(
                    Restrictions.like("fname", name, MatchMode.ANYWHERE),
                    Restrictions.like("lname", name, MatchMode.ANYWHERE));
        }

        return super.findByCriteria(criterion);
    }

    /**
     * Find member with a given id.
     *
     * @param id            memberId
     * @param caseSensitive True if search should be case sensitive.
     * @return The list of found memberList.
     */
    public List<Member> findById(String id, boolean caseSensitive) throws PersistenceException {
        Criterion criterion;
        if (caseSensitive) {
            criterion = Restrictions.like("memberId", id, MatchMode.EXACT);
        } else {
            criterion = Restrictions.like("memberId", id, MatchMode.ANYWHERE);
        }
        return super.findByCriteria(criterion);
    }
    // http://docs.jboss.org/hibernate/search/4.1/reference/en-US/html/search-query.html

    /**
     * Find memberList by name or id.
     *
     * @param nameOrId      The name (firstname or lastname) or id.
     * @param caseSensitive True if search should be case sensitive.
     *
     * @return The found memberList.
     */
    public List<Member> findByNameOrId(String nameOrId, boolean caseSensitive) throws PersistenceException {
        Criterion criterion;
        if (caseSensitive) {
            criterion = Restrictions.or(
                    Restrictions.like("fname", nameOrId, MatchMode.EXACT),
                    Restrictions.like("lname", nameOrId, MatchMode.EXACT),
                    Restrictions.like("memberId", nameOrId, MatchMode.EXACT));
        } else {
            criterion = Restrictions.or(
                    Restrictions.like("fname", nameOrId, MatchMode.ANYWHERE),
                    Restrictions.like("lname", nameOrId, MatchMode.ANYWHERE),
                    Restrictions.like("memberId", nameOrId, MatchMode.ANYWHERE));
        }

        return super.findByCriteria(criterion);
    }

    /**
     * Find by arbitrary String
     *
     * @param string ...Name, Department, Birthdate, memberId
     *
     * @return List<Member>
     *
     * @throws SQLException
     */
    public List<Member> findByString(String string) throws PersistenceException {
        Criterion criterion = Restrictions.or(
                Restrictions.like("firstName", string, MatchMode.ANYWHERE),
                Restrictions.like("lastName", string, MatchMode.ANYWHERE)
//                Restrictions.like("team", string, MatchMode.ANYWHERE),
//                Restrictions.like("department", string, MatchMode.ANYWHERE)
        );

        return super.findByCriteria(criterion);
    }

    /**
     * Find Member(s) by department.
     *
     * @param   department
     *
     * @return  List<Member>
     *
     * @throws  SQLException
     */
    public List<Member> findByDepartment(String department) throws PersistenceException {

        Criterion criterion;
        criterion = Restrictions.or(Restrictions.like("department",department, MatchMode.ANYWHERE));

        return  super.findByCriteria(criterion);
    }

    /**
     * Find Member(s) by sport.
     *
     * @param sport
     *
     * @return List<Member>
     *
     * @throws SQLException
     */
    public List<Member> findBySport(String sport) throws PersistenceException {

        Criterion criterion;
        criterion = Restrictions.or(Restrictions.like("sport", sport, MatchMode.ANYWHERE));

        return super.findByCriteria(criterion);
    }

    /**
     * Find Member(s) by birthdate
     *
     * @param birthdate SQL_DATE format: yyy-mm-dd
     *
     * @return List<Member>
     *
     * @throws SQLException
     */
    public List<Member> findByBirthday(String birthdate) throws PersistenceException {
        Criterion criterion;
        criterion = Restrictions.or(Restrictions.like("dateOfBirth", birthdate ,MatchMode.ANYWHERE));

        return super.findByCriteria(criterion);
    }
}