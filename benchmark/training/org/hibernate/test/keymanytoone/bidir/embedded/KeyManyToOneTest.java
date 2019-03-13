/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.keymanytoone.bidir.embedded;


import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class KeyManyToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCriteriaRestrictionOnKeyManyToOne() {
        Session s = openSession();
        s.beginTransaction();
        s.createQuery("from Order o where o.customer.name = 'Acme'").list();
        Criteria criteria = s.createCriteria(Order.class);
        criteria.createCriteria("customer").add(Restrictions.eq("name", "Acme"));
        criteria.list();
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
        Order order = new Order(cust, 1);
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
    public void testQueryingOnMany2One() {
        Session s = openSession();
        s.beginTransaction();
        Customer cust = new Customer("Acme, Inc.");
        Order order = new Order(cust, 1);
        cust.getOrders().add(order);
        s.save(cust);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List results = s.createQuery("from Order o where o.customer.name = :name").setParameter("name", cust.getName()).list();
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
    public void testLoadingStrategies() {
        Session s = openSession();
        s.beginTransaction();
        Customer cust = new Customer("Acme, Inc.");
        Order order = new Order(cust, 1);
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

