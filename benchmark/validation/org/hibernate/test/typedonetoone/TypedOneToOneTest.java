/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.typedonetoone;


import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class TypedOneToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCreateQuery() {
        Customer cust = new Customer();
        cust.setCustomerId("abc123");
        cust.setName("Matt");
        Address ship = new Address();
        ship.setStreet("peachtree rd");
        ship.setState("GA");
        ship.setCity("ATL");
        ship.setZip("30326");
        ship.setAddressId(new AddressId("SHIPPING", "abc123"));
        ship.setCustomer(cust);
        Address bill = new Address();
        bill.setStreet("peachtree rd");
        bill.setState("GA");
        bill.setCity("ATL");
        bill.setZip("30326");
        bill.setAddressId(new AddressId("BILLING", "abc123"));
        bill.setCustomer(cust);
        cust.setBillingAddress(bill);
        cust.setShippingAddress(ship);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(cust);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List results = s.createQuery("from Customer cust left join fetch cust.billingAddress where cust.customerId='abc123'").list();
        // List results = s.createQuery("from Customer cust left join fetch cust.billingAddress left join fetch cust.shippingAddress").list();
        cust = ((Customer) (results.get(0)));
        Assert.assertTrue(Hibernate.isInitialized(cust.getShippingAddress()));
        Assert.assertTrue(Hibernate.isInitialized(cust.getBillingAddress()));
        Assert.assertEquals("30326", cust.getBillingAddress().getZip());
        Assert.assertEquals("30326", cust.getShippingAddress().getZip());
        Assert.assertEquals("BILLING", cust.getBillingAddress().getAddressId().getType());
        Assert.assertEquals("SHIPPING", cust.getShippingAddress().getAddressId().getType());
        s.delete(cust);
        t.commit();
        s.close();
    }

    @Test
    public void testCreateQueryNull() {
        Customer cust = new Customer();
        cust.setCustomerId("xyz123");
        cust.setName("Matt");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(cust);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List results = s.createQuery("from Customer cust left join fetch cust.billingAddress where cust.customerId='xyz123'").list();
        // List results = s.createQuery("from Customer cust left join fetch cust.billingAddress left join fetch cust.shippingAddress").list();
        cust = ((Customer) (results.get(0)));
        Assert.assertNull(cust.getShippingAddress());
        Assert.assertNull(cust.getBillingAddress());
        s.delete(cust);
        t.commit();
        s.close();
    }
}

