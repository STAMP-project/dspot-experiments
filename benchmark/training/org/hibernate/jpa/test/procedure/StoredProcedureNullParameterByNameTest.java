/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-10761")
@RequiresDialect(Oracle10gDialect.class)
public class StoredProcedureNullParameterByNameTest extends BaseEntityManagerFunctionalTestCase {
    EntityManagerFactory entityManagerFactory;

    @Test
    public void testPassNull() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        StoredProcedureNullParameterByNameTest.User user1 = new StoredProcedureNullParameterByNameTest.User();
        user1.id = 1;
        user1.name = "aName";
        user1.age = 99;
        em.persist(user1);
        em.getTransaction().commit();
        em.clear();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("User.findNameById");
            query.setParameter("ID_PARAM", 1);
            // null is passed for EXTRA_IN_PARAM
            Assert.assertEquals("aName", query.getOutputParameterValue("NAME_PARAM"));
            Assert.assertEquals(null, query.getOutputParameterValue("EXTRA_OUT_PARAM"));
        } finally {
            em.close();
        }
    }

    @NamedStoredProcedureQueries(@NamedStoredProcedureQuery(name = "User.findNameById", resultClasses = StoredProcedureNullParameterByNameTest.User.class, procedureName = "PROC_EXAMPLE_ONE_BASIC_OUT", parameters = { @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_PARAM", type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.IN, name = "EXTRA_IN_PARAM", type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.OUT, name = "NAME_PARAM", type = String.class), @StoredProcedureParameter(mode = ParameterMode.OUT, name = "EXTRA_OUT_PARAM", type = Integer.class) }))
    @Entity(name = "User")
    @Table(name = "USERS")
    public static class User {
        @Id
        private Integer id;

        @Column(name = "NAME")
        private String name;

        @Column(name = "AGE")
        private Integer age;
    }
}

