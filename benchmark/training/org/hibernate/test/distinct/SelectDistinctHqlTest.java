/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.distinct;


import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-10965")
public class SelectDistinctHqlTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            List<org.hibernate.test.distinct.Person> persons = session.createQuery("select distinct p from Person p").getResultList();
            String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            assertTrue(sqlQuery.contains(" distinct "));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            List<org.hibernate.test.distinct.Person> persons = session.createQuery("select distinct p from Person p").setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false).getResultList();
            String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            assertFalse(sqlQuery.contains(" distinct "));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.distinct.Person> persons = session.createQuery("select p from Person p left join fetch p.phones ").getResultList();
            assertEquals(2, persons.size());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            List<org.hibernate.test.distinct.Person> persons = session.createQuery("select distinct p from Person p left join fetch p.phones ").getResultList();
            assertEquals(1, persons.size());
            String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            assertTrue(sqlQuery.contains(" distinct "));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11726")
    public void testDistinctPassThroughFalse() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            List<org.hibernate.test.distinct.Person> persons = session.createQuery("select distinct p from Person p left join fetch p.phones ").setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false).setMaxResults(5).getResultList();
            assertEquals(1, persons.size());
            String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            assertFalse(sqlQuery.contains(" distinct "));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11726")
    public void testDistinctPassThroughTrue() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            List<org.hibernate.test.distinct.Person> persons = session.createQuery("select distinct p from Person p left join fetch p.phones ").setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, true).setMaxResults(5).getResultList();
            assertEquals(1, persons.size());
            String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            assertTrue(sqlQuery.contains(" distinct "));
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
        private java.util.List<SelectDistinctHqlTest.Phone> phones = new ArrayList<>();

        public void addPhone(SelectDistinctHqlTest.Phone phone) {
            phones.add(phone);
            phone.person = this;
        }
    }

    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @Column(name = "`number`")
        private String number;

        @ManyToOne
        private SelectDistinctHqlTest.Person person;

        public Phone() {
        }

        public Phone(String number) {
            this.number = number;
        }
    }
}

