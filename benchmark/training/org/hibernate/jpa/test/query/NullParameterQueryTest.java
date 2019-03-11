/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 * @see <a href="https://hibernate.atlassian.net/browse/JPA-31">JPA-31</a>
 */
@TestForIssue(jiraKey = "JPA-31")
public class NullParameterQueryTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.query.Event event = new org.hibernate.jpa.test.query.Event();
            entityManager.persist(event);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.query.Event event = entityManager.createQuery(("select e " + ("from Event e " + "where (:name is null or e.name = :name)")), .class).setParameter("name", null).getSingleResult();
            assertNotNull(event);
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        @GeneratedValue
        private Long id;

        private String name;
    }
}

