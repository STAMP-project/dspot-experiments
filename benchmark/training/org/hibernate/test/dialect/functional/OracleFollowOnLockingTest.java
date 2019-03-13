/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.LockModeType;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceException;
import javax.persistence.QueryHint;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect({ Oracle8iDialect.class })
@TestForIssue(jiraKey = "HHH-9486")
public class OracleFollowOnLockingTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testPessimisticLockWithMaxResultsThenNoFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p", OracleFollowOnLockingTest.Product.class).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)).setMaxResults(10).getResultList();
        Assert.assertEquals(10, products.size());
        Assert.assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithFirstResultsThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p", OracleFollowOnLockingTest.Product.class).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)).setFirstResult(40).setMaxResults(10).getResultList();
        Assert.assertEquals(10, products.size());
        Assert.assertEquals(11, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithNamedQueryExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createNamedQuery("product_by_name", OracleFollowOnLockingTest.Product.class).getResultList();
        Assert.assertEquals(50, products.size());
        Assert.assertEquals(51, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithCountDistinctThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p where ( select count(distinct p1.id) from Product p1 ) > 0 ", OracleFollowOnLockingTest.Product.class).setLockOptions(setFollowOnLocking(false)).getResultList();
        Assert.assertEquals(50, products.size());
        Assert.assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithFirstResultsWhileExplicitlyDisablingFollowOnLockingThenFails() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        try {
            List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p", OracleFollowOnLockingTest.Product.class).setLockOptions(setFollowOnLocking(false)).setFirstResult(40).setMaxResults(10).getResultList();
            Assert.fail("Should throw exception since Oracle does not support ORDER BY if follow on locking is disabled");
        } catch (PersistenceException expected) {
            Assert.assertEquals(SQLGrammarException.class, expected.getCause().getClass());
        }
    }

    @Test
    public void testPessimisticLockWithFirstResultsWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p", OracleFollowOnLockingTest.Product.class).setLockOptions(setFollowOnLocking(true)).setFirstResult(40).setMaxResults(10).getResultList();
        Assert.assertEquals(10, products.size());
        Assert.assertEquals(11, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithMaxResultsAndOrderByThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p order by p.id", OracleFollowOnLockingTest.Product.class).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)).setMaxResults(10).getResultList();
        Assert.assertEquals(10, products.size());
        Assert.assertEquals(11, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithMaxResultsAndOrderByWhileExplicitlyDisablingFollowOnLockingThenFails() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        try {
            List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p order by p.id", OracleFollowOnLockingTest.Product.class).setLockOptions(setFollowOnLocking(false)).setMaxResults(10).getResultList();
            Assert.fail("Should throw exception since Oracle does not support ORDER BY if follow on locking is disabled");
        } catch (PersistenceException expected) {
            Assert.assertEquals(SQLGrammarException.class, expected.getCause().getClass());
        }
    }

    @Test
    public void testPessimisticLockWithMaxResultsAndOrderByWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select p from Product p order by p.id", OracleFollowOnLockingTest.Product.class).setLockOptions(setFollowOnLocking(true)).setMaxResults(10).getResultList();
        Assert.assertEquals(10, products.size());
        Assert.assertEquals(11, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithDistinctThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select distinct p from Product p", OracleFollowOnLockingTest.Product.class).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)).getResultList();
        Assert.assertEquals(50, products.size());
        Assert.assertEquals(51, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithDistinctWhileExplicitlyDisablingFollowOnLockingThenFails() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        try {
            List<OracleFollowOnLockingTest.Product> products = session.createQuery("select distinct p from Product p where p.id > 40", OracleFollowOnLockingTest.Product.class).setLockOptions(setFollowOnLocking(false)).getResultList();
            Assert.fail("Should throw exception since Oracle does not support DISTINCT if follow on locking is disabled");
        } catch (PersistenceException expected) {
            Assert.assertEquals(SQLGrammarException.class, expected.getCause().getClass());
        }
    }

    @Test
    public void testPessimisticLockWithDistinctWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Product> products = session.createQuery("select distinct p from Product p where p.id > 40").setLockOptions(setFollowOnLocking(true)).setMaxResults(10).getResultList();
        Assert.assertEquals(10, products.size());
        Assert.assertEquals(11, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithGroupByThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<Object[]> products = session.createQuery(("select count(p), p " + ("from Product p " + "group by p.id, p.name "))).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)).getResultList();
        Assert.assertEquals(50, products.size());
        Assert.assertEquals(51, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithGroupByWhileExplicitlyDisablingFollowOnLockingThenFails() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        try {
            List<Object[]> products = session.createQuery(("select count(p), p " + ("from Product p " + "group by p.id, p.name "))).setLockOptions(setFollowOnLocking(false)).getResultList();
            Assert.fail("Should throw exception since Oracle does not support GROUP BY if follow on locking is disabled");
        } catch (PersistenceException expected) {
            Assert.assertEquals(SQLGrammarException.class, expected.getCause().getClass());
        }
    }

    @Test
    public void testPessimisticLockWithGroupByWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<Object[]> products = session.createQuery(("select count(p), p " + ("from Product p " + "group by p.id, p.name "))).setLockOptions(setFollowOnLocking(true)).getResultList();
        Assert.assertEquals(50, products.size());
        Assert.assertEquals(51, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithUnionThenFollowOnLocking() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        List<OracleFollowOnLockingTest.Vehicle> vehicles = session.createQuery("select v from Vehicle v").setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)).getResultList();
        Assert.assertEquals(3, vehicles.size());
        Assert.assertEquals(4, sqlStatementInterceptor.getSqlQueries().size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPessimisticLockWithUnionWhileExplicitlyDisablingFollowOnLockingThenFails() {
        final Session session = openSession();
        session.beginTransaction();
        sqlStatementInterceptor.getSqlQueries().clear();
        try {
            List<OracleFollowOnLockingTest.Vehicle> vehicles = session.createQuery("select v from Vehicle v").setLockOptions(setFollowOnLocking(false)).getResultList();
            Assert.fail("Should throw exception since Oracle does not support UNION if follow on locking is disabled");
        } catch (PersistenceException expected) {
            Assert.assertEquals(SQLGrammarException.class, expected.getCause().getClass());
        }
    }

    @NamedQuery(name = "product_by_name", query = "select p from Product p where p.name is not null", lockMode = LockModeType.PESSIMISTIC_WRITE, hints = @QueryHint(name = QueryHints.FOLLOW_ON_LOCKING, value = "true"))
    @Entity(name = "Product")
    public static class Product {
        @Id
        @GeneratedValue
        private Long id;

        private String name;
    }

    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    @Entity(name = "Vehicle")
    public static class Vehicle {
        @Id
        @GeneratedValue
        private Long id;

        private String name;
    }

    @Entity(name = "SportsCar")
    public static class SportsCar extends OracleFollowOnLockingTest.Vehicle {
        private double speed;
    }

    @Entity(name = "Truck")
    public static class Truck extends OracleFollowOnLockingTest.Vehicle {
        private double torque;
    }
}

