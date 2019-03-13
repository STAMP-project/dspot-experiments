/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.subselect.join;


import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class SubselectInJoinedTableTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10998")
    public void testSubselectInJoinedTable() {
        SubselectInJoinedTableTest.OrderEntry orderEntry1 = new SubselectInJoinedTableTest.OrderEntry();
        orderEntry1.setOrderEntryId(1L);
        SubselectInJoinedTableTest.OrderEntry orderEntry2 = new SubselectInJoinedTableTest.OrderEntry();
        orderEntry2.setOrderEntryId(2L);
        SubselectInJoinedTableTest.Order order = new SubselectInJoinedTableTest.Order();
        order.setOrderId(3L);
        order.getOrderEntries().add(orderEntry1);
        order.getOrderEntries().add(orderEntry2);
        order.setFirstOrderEntry(orderEntry1);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(orderEntry1);
        s.persist(orderEntry2);
        s.persist(order);
        tx.commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        order = ((SubselectInJoinedTableTest.Order) (s.get(SubselectInJoinedTableTest.Order.class, order.getOrderId())));
        Assert.assertEquals(orderEntry1.getOrderEntryId(), order.getFirstOrderEntry().getOrderEntryId());
        Assert.assertEquals(2, order.getOrderEntries().size());
        Assert.assertEquals(orderEntry1.getOrderEntryId(), order.getOrderEntries().get(0).getOrderEntryId());
        Assert.assertEquals(orderEntry2.getOrderEntryId(), order.getOrderEntries().get(1).getOrderEntryId());
        s.getTransaction().commit();
        s.close();
    }

    public static class Order {
        private Long orderId;

        private SubselectInJoinedTableTest.OrderEntry firstOrderEntry;

        private List<SubselectInJoinedTableTest.OrderEntry> orderEntries = new ArrayList<SubselectInJoinedTableTest.OrderEntry>();

        public SubselectInJoinedTableTest.OrderEntry getFirstOrderEntry() {
            return firstOrderEntry;
        }

        public void setFirstOrderEntry(SubselectInJoinedTableTest.OrderEntry firstOrderEntry) {
            this.firstOrderEntry = firstOrderEntry;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public List<SubselectInJoinedTableTest.OrderEntry> getOrderEntries() {
            return orderEntries;
        }

        public void setOrderEntries(List<SubselectInJoinedTableTest.OrderEntry> orderEntries) {
            this.orderEntries = orderEntries;
        }
    }

    public static class OrderEntry {
        private Long orderEntryId;

        public Long getOrderEntryId() {
            return orderEntryId;
        }

        public void setOrderEntryId(Long orderEntryId) {
            this.orderEntryId = orderEntryId;
        }
    }
}

