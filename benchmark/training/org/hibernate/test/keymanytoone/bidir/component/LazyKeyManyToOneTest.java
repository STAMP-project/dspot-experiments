/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.keymanytoone.bidir.component;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class LazyKeyManyToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testQueryingOnMany2One() {
        Session s = openSession();
        s.beginTransaction();
        Customer cust = new Customer("Acme, Inc.");
        Order order = new Order(new Order.Id(cust, 1));
        cust.getOrders().add(order);
        s.save(cust);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List results = s.createQuery("from Order o where o.id.customer.name = :name").setParameter("name", cust.getName()).list();
        Assert.assertEquals(1, results.size());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(cust);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSaveCascadedToKeyManyToOne() {
        // test cascading a save to an association with a key-many-to-one which refers to a
        // just saved entity
        Session s = openSession();
        s.beginTransaction();
        Customer cust = new Customer("Acme, Inc.");
        Order order = new Order(new Order.Id(cust, 1));
        cust.getOrders().add(order);
        sessionFactory().getStatistics().clear();
        s.save(cust);
        s.flush();
        Assert.assertEquals(2, sessionFactory().getStatistics().getEntityInsertCount());
        s.delete(cust);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLoadingStrategies() {
        Session s = openSession();
        s.beginTransaction();
        Customer cust = new Customer("Acme, Inc.");
        Order order = new Order(new Order.Id(cust, 1));
        cust.getOrders().add(order);
        s.save(cust);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        cust = ((Customer) (s.get(Customer.class, cust.getId())));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        cust = ((Customer) (s.createQuery("from Customer").uniqueResult()));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        cust = ((Customer) (s.createQuery("from Customer c join fetch c.orders").uniqueResult()));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        s.delete(cust);
        s.getTransaction().commit();
        s.close();
    }
}

