/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.list;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import junit.framework.Assert;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Test initially developed for HHH-9195
 *
 * @author Steve Ebersole
 */
public class ListIndexReferenceFromListElementTest extends BaseNonConfigCoreFunctionalTestCase {
    @Entity(name = "LocalOrder")
    @Table(name = "LocalOrder")
    public static class LocalOrder {
        @Id
        @GeneratedValue
        public Integer id;

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @OrderColumn(name = "position")
        public List<ListIndexReferenceFromListElementTest.LocalLineItem> lineItems = new ArrayList<ListIndexReferenceFromListElementTest.LocalLineItem>();

        public LocalOrder() {
        }

        public ListIndexReferenceFromListElementTest.LocalLineItem makeLineItem(String name) {
            ListIndexReferenceFromListElementTest.LocalLineItem lineItem = new ListIndexReferenceFromListElementTest.LocalLineItem(name, this);
            lineItems.add(lineItem);
            return lineItem;
        }
    }

    @Entity(name = "LocalLineItem")
    @Table(name = "LocalLineItem")
    public static class LocalLineItem {
        @Id
        @GeneratedValue
        public Integer id;

        public String name;

        @ManyToOne
        @JoinColumn
        public ListIndexReferenceFromListElementTest.LocalOrder order;

        @Column(insertable = false, updatable = false)
        public int position;

        public LocalLineItem() {
        }

        public LocalLineItem(String name, ListIndexReferenceFromListElementTest.LocalOrder order) {
            this.name = name;
            this.order = order;
        }
    }

    @Test
    public void testIt() {
        {
            Session s = openSession();
            s.beginTransaction();
            ListIndexReferenceFromListElementTest.LocalOrder order = s.byId(ListIndexReferenceFromListElementTest.LocalOrder.class).load(1);
            Assert.assertEquals(2, order.lineItems.size());
            ListIndexReferenceFromListElementTest.LocalLineItem shoes = order.lineItems.get(0);
            ListIndexReferenceFromListElementTest.LocalLineItem socks = order.lineItems.get(1);
            Assert.assertEquals("Shoes", shoes.name);
            Assert.assertEquals(0, shoes.position);
            Assert.assertEquals(1, socks.position);
            order.lineItems.remove(socks);
            order.lineItems.add(0, socks);
            s.getTransaction().commit();
            s.close();
        }
        {
            Session s = openSession();
            s.beginTransaction();
            ListIndexReferenceFromListElementTest.LocalOrder order = s.byId(ListIndexReferenceFromListElementTest.LocalOrder.class).load(1);
            Assert.assertEquals(2, order.lineItems.size());
            ListIndexReferenceFromListElementTest.LocalLineItem socks = order.lineItems.get(0);
            ListIndexReferenceFromListElementTest.LocalLineItem shoes = order.lineItems.get(1);
            Assert.assertEquals("Shoes", shoes.name);
            Assert.assertEquals(0, socks.position);
            Assert.assertEquals(1, shoes.position);
            s.getTransaction().commit();
            s.close();
        }
    }
}

