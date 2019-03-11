/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.mapjoin;


import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class MapJoinEntryTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12945")
    public void testMapJoinEntryCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Map.Entry> query = criteriaBuilder.createQuery(.class);
            Root<Customer> customer = query.from(.class);
            MapJoin<Customer, String, CustomerOrder> orderMap = customer.join(Customer_.orderMap);
            query.select(orderMap.entry());
            TypedQuery<Map.Entry> typedQuery = em.createQuery(query);
            List<Map.Entry> resultList = typedQuery.getResultList();
            assertEquals(1, resultList.size());
            assertEquals("online", resultList.get(0).getKey());
            assertEquals("AA Glass Cleaner", ((CustomerOrder) (resultList.get(0).getValue())).getItem());
        });
    }

    @Test
    public void testMapJoinEntryJPQL() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            TypedQuery<Map.Entry> query = em.createQuery("SELECT ENTRY(mp) FROM Customer c JOIN c.orderMap mp", .class);
            List<Map.Entry> resultList = query.getResultList();
            assertEquals(1, resultList.size());
            assertEquals("online", resultList.get(0).getKey());
            assertEquals("AA Glass Cleaner", ((CustomerOrder) (resultList.get(0).getValue())).getItem());
        });
    }
}

