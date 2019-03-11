/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.bag;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * Tests related to contains operations on a PersistentBag.
 *
 * @author Vlad Mihalcea
 */
public class PersistentBagContainsTest extends BaseCoreFunctionalTestCase {
    /**
     * This test does not verify how equals is implemented for Bags,
     * but rather if the child entity equals and hashCode are used properly for both
     * managed and detached entities.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-5409")
    public void testContains() {
        PersistentBagContainsTest.Order _order = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.Order order = new org.hibernate.test.collection.bag.Order();
            session.persist(order);
            org.hibernate.test.collection.bag.Item item1 = new org.hibernate.test.collection.bag.Item();
            item1.setName("i1");
            org.hibernate.test.collection.bag.Item item2 = new org.hibernate.test.collection.bag.Item();
            item2.setName("i2");
            order.addItem(item1);
            order.addItem(item2);
            return order;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.Item item1 = new org.hibernate.test.collection.bag.Item();
            item1.setName("i1");
            org.hibernate.test.collection.bag.Item item2 = new org.hibernate.test.collection.bag.Item();
            item2.setName("i2");
            assertTrue(_order.getItems().contains(item1));
            assertTrue(_order.getItems().contains(item2));
            org.hibernate.test.collection.bag.Order order = session.find(.class, _order.getId());
            assertTrue(order.getItems().contains(item1));
            assertTrue(order.getItems().contains(item2));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.bag.Order order = session.find(.class, _order.getId());
            session.delete(order);
        });
    }

    @Entity(name = "`Order`")
    public static class Order {
        @Id
        @GeneratedValue
        private Long id;

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<PersistentBagContainsTest.Item> items = new ArrayList<PersistentBagContainsTest.Item>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<PersistentBagContainsTest.Item> getItems() {
            return items;
        }

        public void addItem(PersistentBagContainsTest.Item item) {
            items.add(item);
            item.setOrder(this);
        }
    }

    @Entity(name = "Item")
    public static class Item {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @ManyToOne
        private PersistentBagContainsTest.Order order;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PersistentBagContainsTest.Order getOrder() {
            return order;
        }

        public void setOrder(PersistentBagContainsTest.Order order) {
            this.order = order;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof PersistentBagContainsTest.Item)) {
                return false;
            }
            PersistentBagContainsTest.Item item = ((PersistentBagContainsTest.Item) (o));
            return Objects.equals(getName(), item.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName());
        }
    }
}

