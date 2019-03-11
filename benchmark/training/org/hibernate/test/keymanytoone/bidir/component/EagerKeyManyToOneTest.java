/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.keymanytoone.bidir.component;


import LoadEventListener.INTERNAL_LOAD_EAGER;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.event.internal.DefaultLoadEventListener;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "unchecked" })
public class EagerKeyManyToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSaveCascadedToKeyManyToOne() {
        sessionFactory().getStatistics().clear();
        // test cascading a save to an association with a key-many-to-one which refers to a
        // just saved entity
        Session s = openSession();
        s.beginTransaction();
        Customer cust = new Customer("Acme, Inc.");
        Order order = new Order(new Order.Id(cust, 1));
        cust.getOrders().add(order);
        s.save(cust);
        s.flush();
        Assert.assertEquals(2, sessionFactory().getStatistics().getEntityInsertCount());
        s.delete(cust);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLoadingStrategies() {
        sessionFactory().getStatistics().clear();
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
        cust = ((Customer) (s.createQuery("from Customer").uniqueResult()));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        cust = ((Customer) (s.createQuery("from Customer c join fetch c.orders").uniqueResult()));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        cust = ((Customer) (s.createQuery("from Customer c join fetch c.orders as o join fetch o.id.customer").uniqueResult()));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        cust = ((Customer) (s.createCriteria(Customer.class).uniqueResult()));
        Assert.assertEquals(1, cust.getOrders().size());
        s.clear();
        s.delete(cust);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-2277")
    public void testLoadEntityWithEagerFetchingToKeyManyToOneReferenceBackToSelf() {
        sessionFactory().getStatistics().clear();
        // long winded method name to say that this is a test specifically for HHH-2277 ;)
        // essentially we have a bidirectional association where one side of the
        // association is actually part of a composite PK.
        // 
        // The way these are mapped causes the problem because both sides
        // are defined as eager which leads to the infinite loop; if only
        // one side is marked as eager, then all is ok.  In other words the
        // problem arises when both pieces of instance data are coming from
        // the same result set.  This is because no "entry" can be placed
        // into the persistence context for the association with the
        // composite key because we are in the process of trying to build
        // the composite-id instance
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
        try {
            cust = ((Customer) (s.get(Customer.class, cust.getId())));
        } catch (EagerKeyManyToOneTest.OverflowCondition overflow) {
            Assert.fail("get()/load() caused overflow condition");
        }
        s.delete(cust);
        s.getTransaction().commit();
        s.close();
    }

    private static class OverflowCondition extends RuntimeException {}

    private static class CustomLoadListener extends DefaultLoadEventListener {
        private int internalLoadCount = 0;

        @Override
        public void onLoad(LoadEvent event, LoadType loadType) throws HibernateException {
            if (INTERNAL_LOAD_EAGER.getName().equals(loadType.getName())) {
                (internalLoadCount)++;
                if ((internalLoadCount) > 10) {
                    throw new EagerKeyManyToOneTest.OverflowCondition();
                }
            }
            super.onLoad(event, loadType);
            (internalLoadCount)--;
        }
    }
}

