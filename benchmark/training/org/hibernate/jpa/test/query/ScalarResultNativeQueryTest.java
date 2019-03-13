/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import java.util.List;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for selecting scalar value from native queries.
 *
 * @author Gunnar Morling
 */
@RequiresDialect(H2Dialect.class)
public class ScalarResultNativeQueryTest extends BaseEntityManagerFunctionalTestCase {
    @Entity(name = "Person")
    @Table(name = "person")
    @NamedNativeQuery(name = "personAge", query = "select p.age from person p", resultSetMapping = "ageStringMapping")
    @SqlResultSetMapping(name = "ageStringMapping", columns = { @ColumnResult(name = "age", type = String.class) })
    public static class Person {
        @Id
        private Integer id;

        @SuppressWarnings("unused")
        @Column(name = "age")
        private int age;

        public Person() {
        }

        public Person(Integer id, int age) {
            this.id = id;
            this.age = age;
        }
    }

    @Test
    public void shouldApplyConfiguredTypeForProjectionOfScalarValue() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(new ScalarResultNativeQueryTest.Person(1, 29));
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        List<String> results = em.createNamedQuery("personAge", String.class).getResultList();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("29", results.get(0));
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Person").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }
}

