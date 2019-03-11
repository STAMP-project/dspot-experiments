/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialect(HSQLDialect.class)
public class HSQLStoreProcedureTest extends BaseEntityManagerFunctionalTestCase {
    EntityManagerFactory entityManagerFactory;

    @Test
    @TestForIssue(jiraKey = "HHH-10515")
    public void testNamedStoredProcedureExecution() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("User.inoutproc");
            query.setParameter("arg1", 1);
            query.execute();
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10915")
    public void testGetNamedParameters() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("User.inoutproc");
            final Set<Parameter<?>> parameters = query.getParameters();
            MatcherAssert.assertThat(parameters.size(), Is.is(2));
            MatcherAssert.assertThat(query.getParameter("arg1"), IsNot.not(IsNull.nullValue()));
            MatcherAssert.assertThat(query.getParameter("res"), IsNot.not(IsNull.nullValue()));
            MatcherAssert.assertThat(query.getParameter("arg1", Integer.class), IsNot.not(IsNull.nullValue()));
            try {
                query.getParameter("arg1", String.class);
                Assert.fail("An IllegalArgumentException is expected, A parameter with name arg1 and type String does not exist");
            } catch (IllegalArgumentException iae) {
                // expected
            }
            try {
                query.getParameter("arg2");
                Assert.fail("An IllegalArgumentException is expected, A parameter with name arg2 does not exist");
            } catch (IllegalArgumentException iae) {
                // expected
            }
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10915")
    public void testGetPositionalParameters() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("User.inoutproc");
            final Set<Parameter<?>> parameters = query.getParameters();
            MatcherAssert.assertThat(parameters.size(), Is.is(2));
            try {
                query.getParameter(1);
                Assert.fail("An IllegalArgumentException is expected, The stored procedure has named parameters not positional");
            } catch (IllegalArgumentException iae) {
                // expected
            }
            try {
                query.getParameter(1, String.class);
                Assert.fail("An IllegalArgumentException is expected, The stored procedure has named parameters not positional");
            } catch (IllegalArgumentException iae) {
                // expected
            }
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10915")
    public void testGetPositionalParameters2() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("User.inoutprocpositional");
            final Set<Parameter<?>> parameters = query.getParameters();
            MatcherAssert.assertThat(parameters.size(), Is.is(2));
            MatcherAssert.assertThat(query.getParameter(1), IsNot.not(IsNull.nullValue()));
            MatcherAssert.assertThat(query.getParameter(2), IsNot.not(IsNull.nullValue()));
            MatcherAssert.assertThat(query.getParameter(1, Integer.class), IsNot.not(IsNull.nullValue()));
            try {
                query.getParameter(3);
                Assert.fail("An IllegalArgumentException is expected, A parameter at position 3 does not exist");
            } catch (IllegalArgumentException iae) {
                // expected
            }
            try {
                query.getParameter(1, String.class);
                Assert.fail("An IllegalArgumentException is expected, The parameter at position 1 is of type Integer not String");
            } catch (IllegalArgumentException iae) {
                // expected
            }
        } finally {
            em.close();
        }
    }

    @Entity(name = "User")
    @NamedStoredProcedureQueries({ @NamedStoredProcedureQuery(name = "User.inoutproc", procedureName = "inoutproc", parameters = { @StoredProcedureParameter(mode = ParameterMode.IN, name = "arg1", type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.OUT, name = "res", type = Integer.class) }), @NamedStoredProcedureQuery(name = "User.inoutprocpositional", procedureName = "inoutproc", parameters = { @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.OUT, type = Integer.class) }) })
    @Table(name = "USERS")
    public class User {
        @Id
        @GeneratedValue
        private Integer id;
    }
}

