/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.bidirectional;


import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class CompositeDerivedIdentityTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCreateProject() {
        Product product = new Product();
        product.setName("Product 1");
        Session session = openSession();
        session.beginTransaction();
        session.save(product);
        session.getTransaction().commit();
        session.close();
        Order order = new Order();
        order.setName("Order 1");
        order.addLineItem(product, 2);
        session = openSession();
        session.beginTransaction();
        session.save(order);
        session.getTransaction().commit();
        session.close();
        Long orderId = order.getId();
        session = openSession();
        session.beginTransaction();
        order = ((Order) (session.get(Order.class, orderId)));
        Assert.assertEquals(1, order.getLineItems().size());
        session.delete(order);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10476")
    public void testBidirectonalKeyManyToOneId() {
        Product product = new Product();
        product.setName("Product 1");
        Session session = openSession();
        session.beginTransaction();
        session.save(product);
        session.getTransaction().commit();
        session.close();
        Order order = new Order();
        order.setName("Order 1");
        order.addLineItem(product, 2);
        session = openSession();
        session.beginTransaction();
        session.save(order);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        OrderLine orderLine = order.getLineItems().iterator().next();
        orderLine.setAmount(5);
        OrderLine orderLineGotten = session.get(OrderLine.class, orderLine);
        Assert.assertSame(orderLineGotten, orderLine);
        Assert.assertEquals(Integer.valueOf(2), orderLineGotten.getAmount());
        SessionImplementor si = ((SessionImplementor) (session));
        Assert.assertTrue(si.getPersistenceContext().isEntryFor(orderLineGotten));
        Assert.assertFalse(si.getPersistenceContext().isEntryFor(orderLineGotten.getOrder()));
        Assert.assertFalse(si.getPersistenceContext().isEntryFor(orderLineGotten.getProduct()));
        session.getTransaction().commit();
        session.close();
    }
}

