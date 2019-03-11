/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class ElementCollectionConverterTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12581")
    public void testCriteriaQueryWithElementCollectionUsingConverter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Item item1 = new Item("P1");
            item1.getRoles().add(new Color());
            Item item2 = new Item("P2");
            item2.getRoles().add(new Industry());
            Item item3 = new Item("P3");
            item3.getRoles().add(new Color());
            item3.getRoles().add(new Industry());
            entityManager.persist(item1);
            entityManager.persist(item2);
            entityManager.persist(item3);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Item> query = cb.createQuery(.class);
            Root<Item> root = query.from(.class);
            // HHH-12338 effectively caused Item_.roles to be null.
            // Therefore this caused a NPE with the commit originally applied for HHH-12338.
            // Reverting that fix avoids the regression and this proceeds as expected.
            root.fetch(Item_.roles);
            // Just running the query here.
            // the outcome is less important than the above for context of this test case.
            query = query.select(root).distinct(true);
            List<Item> items = entityManager.createQuery(query).getResultList();
            assertEquals(3, items.size());
        });
    }
}

