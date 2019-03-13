/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10893")
public class ParameterCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testReusingQueryWithNewParameterValues() throws Exception {
        try (Session session = openSession()) {
            Collection<Long> ids = new ArrayList<>();
            Query q = session.createQuery("select id from Person where id in (:ids) order by id");
            for (int i = 0; i < 10; i++) {
                ids.add(Long.valueOf(i));
            }
            q.setParameterList("ids", ids);
            q.list();
            ids.clear();
            for (int i = 10; i < 20; i++) {
                ids.add(Long.valueOf(i));
            }
            // reuse the same query, but set new collection parameter
            q.setParameterList("ids", ids);
            List<Long> foundIds = q.list();
            Assert.assertThat("Wrong number of results", foundIds.size(), CoreMatchers.is(ids.size()));
            Assert.assertThat(foundIds, CoreMatchers.is(ids));
        }
    }

    @Entity(name = "Person")
    @Table(name = "PERSON")
    public static class Person {
        @Id
        private long id;

        private String name;

        public Person() {
        }

        public Person(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

