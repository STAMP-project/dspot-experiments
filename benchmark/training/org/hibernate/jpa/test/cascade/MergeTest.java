/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cascade;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class MergeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMergeDetachedEntityWithNewOneToManyElements() {
        MergeTest.Order order = new MergeTest.Order();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(order);
        em.getTransaction().commit();
        em.close();
        MergeTest.Item item1 = new MergeTest.Item();
        item1.name = "i1";
        MergeTest.Item item2 = new MergeTest.Item();
        item2.name = "i2";
        order.addItem(item1);
        order.addItem(item2);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        order = em.merge(order);
        em.flush();
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        order = em.find(MergeTest.Order.class, order.id);
        Assert.assertEquals(2, order.items.size());
        em.remove(order);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testMergeLoadedEntityWithNewOneToManyElements() {
        MergeTest.Order order = new MergeTest.Order();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(order);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        order = em.find(MergeTest.Order.class, order.id);
        MergeTest.Item item1 = new MergeTest.Item();
        item1.name = "i1";
        MergeTest.Item item2 = new MergeTest.Item();
        item2.name = "i2";
        order.addItem(item1);
        order.addItem(item2);
        order = em.merge(order);
        em.flush();
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        order = em.find(MergeTest.Order.class, order.id);
        Assert.assertEquals(2, order.items.size());
        em.remove(order);
        em.getTransaction().commit();
        em.close();
    }

    @Entity
    private static class Order {
        @Id
        @GeneratedValue
        private Long id;

        @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
        private List<MergeTest.Item> items = new ArrayList<MergeTest.Item>();

        public Order() {
        }

        public void addItem(MergeTest.Item item) {
            items.add(item);
            item.order = this;
        }
    }

    @Entity
    private static class Item {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @ManyToOne
        private MergeTest.Order order;
    }
}

