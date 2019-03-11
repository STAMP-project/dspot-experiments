/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.nationalized;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Nationalized;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "10495")
@RequiresDialect({ Oracle10gDialect.class, PostgreSQL81Dialect.class })
public class StringNationalizedTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSaveEntityWithNationalizedProperty() {
        Session s = openSession();
        s.getTransaction().begin();
        try {
            StringNationalizedTest.NationalizedEntity ne = new StringNationalizedTest.NationalizedEntity();
            ne.name = "Hello";
            s.save(ne);
            s.getTransaction().commit();
        } catch (RuntimeException e) {
            if ((s.getTransaction().getStatus()) == (TransactionStatus.ACTIVE)) {
                s.getTransaction().rollback();
            }
            throw e;
        } finally {
            s.close();
        }
        s = openSession();
        try {
            final Query query = s.createQuery("from NationalizedEntity where name = :name");
            query.setString("name", "Hello");
            final List list = query.list();
            Assert.assertThat(list.size(), Is.is(1));
        } finally {
            s.close();
        }
    }

    @Entity(name = "NationalizedEntity")
    @Table(name = "NATIONALIZED_ENTITY")
    public static class NationalizedEntity {
        @Id
        @GeneratedValue
        private long id;

        @Nationalized
        String name;
    }
}

