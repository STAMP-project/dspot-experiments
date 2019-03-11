/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-12729")
public class HibernateFirstResultMaxResultsTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testFirstResult() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("from Employee");
            // not initialized yet
            assertNull(query.getHibernateFirstResult());
            // the following is special case; when initialized to -1, getHibernateFirstResult returns 0
            assertEquals(Integer.valueOf(0), query.setHibernateFirstResult((-1)).getHibernateFirstResult());
            assertEquals(Integer.valueOf(0), query.setHibernateFirstResult(0).getHibernateFirstResult());
            assertEquals(Integer.valueOf(1), query.setHibernateFirstResult(1).getHibernateFirstResult());
            assertEquals(Integer.valueOf(10), query.setFirstResult(10).getHibernateFirstResult());
        });
    }

    @Test
    public void testMaxResults() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("from Employee");
            // not initialized yet
            assertNull(query.getHibernateMaxResults());
            // values <= 0 are considered uninitialized;
            assertNull(query.setHibernateMaxResults((-1)).getHibernateMaxResults());
            assertNull(query.setHibernateMaxResults(0).getHibernateMaxResults());
            assertEquals(Integer.valueOf(1), query.setHibernateMaxResults(1).getHibernateMaxResults());
            assertEquals(Integer.valueOf(0), query.setMaxResults(0).getHibernateMaxResults());
            assertEquals(Integer.valueOf(2), query.setMaxResults(2).getHibernateMaxResults());
        });
    }

    @Test
    public void testPagination() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            for (int i = 0; i < 5; i++) {
                session.persist(new org.hibernate.test.hql.Employee(i));
            }
        });
        final String query = "from Employee order by id";
        checkResults(executeQuery(query, null, null), 0, 4);
        checkResults(executeQuery(query, 0, null), 0, 4);
        checkResults(executeQuery(query, (-1), null), 0, 4);
        checkResults(executeQuery(query, null, 0), 0, 4);
        checkResults(executeQuery(query, null, (-1)), 0, 4);
        checkResults(executeQuery(query, null, 2), 0, 1);
        checkResults(executeQuery(query, (-1), 0), 0, 4);
        checkResults(executeQuery(query, (-1), 3), 0, 2);
        checkResults(executeQuery(query, 1, null), 1, 4);
        checkResults(executeQuery(query, 1, 0), 1, 4);
        checkResults(executeQuery(query, 1, (-1)), 1, 4);
        checkResults(executeQuery(query, 1, 1), 1, 1);
    }

    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private long id;

        private String name;

        public Employee() {
        }

        public Employee(long id) {
            this.id = id;
        }
    }
}

