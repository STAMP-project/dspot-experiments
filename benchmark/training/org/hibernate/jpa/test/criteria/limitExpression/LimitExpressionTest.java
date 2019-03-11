/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.limitExpression;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class LimitExpressionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11278")
    public void testAnEmptyListIsReturnedWhenSetMaxResultsToZero() {
        TransactionUtil.doInJPA(this::entityManagerFactory, (EntityManager entityManager) -> {
            final CriteriaQuery<org.hibernate.jpa.test.criteria.limitExpression.Person> query = entityManager.getCriteriaBuilder().createQuery(.class);
            query.from(.class);
            final List list = entityManager.createQuery(query).setMaxResults(0).getResultList();
            assertTrue("The list should be empty with setMaxResults 0", list.isEmpty());
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;
    }
}

