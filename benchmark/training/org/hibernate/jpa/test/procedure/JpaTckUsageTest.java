/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests various JPA usage scenarios for performing stored procedures.  Inspired by the awesomely well-done JPA TCK
 *
 * @author Steve Ebersole
 */
public class JpaTckUsageTest extends BaseUnitTestCase {
    @Test
    public void testMultipleGetUpdateCountCalls() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findOneUser");
            // this is what the TCK attempts to do, don't shoot the messenger...
            query.getUpdateCount();
            // yep, twice
            query.getUpdateCount();
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void testBasicScalarResults() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findOneUser");
            boolean isResult = query.execute();
            Assert.assertTrue(isResult);
            int updateCount = query.getUpdateCount();
            boolean results = false;
            do {
                List list = query.getResultList();
                Assert.assertEquals(1, list.size());
                results = query.hasMoreResults();
                // and it only sets the updateCount once lol
            } while (results || (updateCount != (-1)) );
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    @FailureExpected(jiraKey = "HHH-8416", message = "JPA TCK challenge")
    public void testHasMoreResultsHandlingTckChallenge() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findOneUser", User.class);
            Assert.assertTrue(query.execute());
            Assert.assertTrue(query.hasMoreResults());
            query.getResultList();
            Assert.assertFalse(query.hasMoreResults());
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void testHasMoreResultsHandling() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findOneUser", User.class);
            Assert.assertTrue(query.execute());
            query.getResultList();
            Assert.assertFalse(query.hasMoreResults());
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void testResultClassHandling() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findOneUser", User.class);
            boolean isResult = query.execute();
            Assert.assertTrue(isResult);
            int updateCount = query.getUpdateCount();
            boolean results = false;
            do {
                List list = query.getResultList();
                Assert.assertEquals(1, list.size());
                ExtraAssertions.assertTyping(User.class, list.get(0));
                results = query.hasMoreResults();
                // and it only sets the updateCount once lol
            } while (results || (updateCount != (-1)) );
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void testSettingInParamDefinedOnNamedStoredProcedureQuery() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("positional-param");
            query.setParameter(1, 1);
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void testSettingNonExistingParams() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            // non-existing positional param
            try {
                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("positional-param");
                query.setParameter(99, 1);
                Assert.fail("Expecting an exception");
            } catch (IllegalArgumentException expected) {
                // this is the expected condition
            }
            // non-existing named param
            try {
                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("positional-param");
                query.setParameter("does-not-exist", 1);
                Assert.fail("Expecting an exception");
            } catch (IllegalArgumentException expected) {
                // this is the expected condition
            }
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    @FailureExpected(jiraKey = "HHH-8395", message = "Out of the frying pan into the fire: https://issues.apache.org/jira/browse/DERBY-211")
    public void testExecuteUpdate() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("deleteAllUsers");
            int count = query.executeUpdate();
            // this fails because the Derby EmbeddedDriver is returning zero here rather than the actual updateCount :(
            // https://issues.apache.org/jira/browse/DERBY-211
            Assert.assertEquals(1, count);
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    // todo : look at ways to allow "Auxiliary DB Objects" to the db via EMF bootstrapping.
    // public static final String findOneUser_CREATE_CMD = "CREATE ALIAS findOneUser AS $$\n" +
    // "import org.h2.tools.SimpleResultSet;\n" +
    // "import java.sql.*;\n" +
    // "@CODE\n" +
    // "ResultSet findOneUser() {\n" +
    // "    SimpleResultSet rs = new SimpleResultSet();\n" +
    // "    rs.addColumn(\"ID\", Types.INTEGER, 10, 0);\n" +
    // "    rs.addColumn(\"NAME\", Types.VARCHAR, 255, 0);\n" +
    // "    rs.addRow(1, \"Steve\");\n" +
    // "    return rs;\n" +
    // "}\n" +
    // "$$";
    // public static final String findOneUser_DROP_CMD = "DROP ALIAS findOneUser IF EXISTS";
    // 
    // public static final String deleteAllUsers_CREATE_CMD = "CREATE ALIAS deleteAllUsers AS $$\n" +
    // "@CODE\n" +
    // "int deleteAllUsers() {\n" +
    // "    return 156;" +
    // "}\n" +
    // "$$";
    // public static final String deleteAllUsers_DROP_CMD = "DROP ALIAS deleteAllUsers IF EXISTS";
    HibernateEntityManagerFactory entityManagerFactory;
}

