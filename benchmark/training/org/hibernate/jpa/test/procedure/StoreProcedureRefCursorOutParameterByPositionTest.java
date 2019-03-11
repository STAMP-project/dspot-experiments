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
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-9286")
@RequiresDialect(Oracle10gDialect.class)
public class StoreProcedureRefCursorOutParameterByPositionTest extends BaseEntityManagerFunctionalTestCase {
    EntityManagerFactory entityManagerFactory;

    @Test
    public void testNamedStoredProcedureExecution() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("User.findByName");
            query.setParameter(1, "my_name");
            query.getResultList();
        } finally {
            em.close();
        }
    }

    @NamedStoredProcedureQuery(name = "User.findByName", resultClasses = StoreProcedureRefCursorOutParameterByPositionTest.User.class, procedureName = "PROC_EXAMPLE", parameters = { @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class), @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = Class.class) })
    @Entity(name = "Message")
    @Table(name = "USERS")
    public static class User {
        @Id
        private Integer id;

        @Column(name = "NAME")
        private String name;
    }
}

