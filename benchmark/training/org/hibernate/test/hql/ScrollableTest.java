/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import ScrollMode.FORWARD_ONLY;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hibernate.ScrollableResults;
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
public class ScrollableTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10860")
    public void testScrollableResults() {
        final List params = new ArrayList();
        params.add(1L);
        params.add(2L);
        try (Session s = openSession()) {
            final Query query = s.createQuery("from MyEntity e where e.id in (:ids)").setParameter("ids", params).setFetchSize(10);
            try (ScrollableResults scroll = query.scroll(FORWARD_ONLY)) {
                int i = 0;
                while (scroll.next()) {
                    if (i == 0) {
                        Assert.assertThat(((ScrollableTest.MyEntity) (scroll.get()[0])).getDescription(), CoreMatchers.is("entity_1"));
                    } else {
                        Assert.assertThat(((ScrollableTest.MyEntity) (scroll.get()[0])).getDescription(), CoreMatchers.is("entity_2"));
                    }
                    i++;
                } 
            }
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10860")
    public void testScrollableResults2() {
        final List params = new ArrayList();
        params.add(1L);
        params.add(2L);
        try (Session s = openSession()) {
            final Query query = s.createQuery("from MyEntity e where e.id in (:ids)").setParameter("ids", params).setFetchSize(10);
            try (ScrollableResults scroll = query.scroll()) {
                int i = 0;
                while (scroll.next()) {
                    if (i == 0) {
                        Assert.assertThat(((ScrollableTest.MyEntity) (scroll.get()[0])).getDescription(), CoreMatchers.is("entity_1"));
                    } else {
                        Assert.assertThat(((ScrollableTest.MyEntity) (scroll.get()[0])).getDescription(), CoreMatchers.is("entity_2"));
                    }
                    i++;
                } 
            }
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "MY_ENTITY")
    public static class MyEntity {
        @Id
        private Long id;

        private String description;

        public MyEntity() {
        }

        public MyEntity(Long id, String description) {
            this.id = id;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}

