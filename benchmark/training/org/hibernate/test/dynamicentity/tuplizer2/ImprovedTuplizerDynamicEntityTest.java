/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dynamicentity.tuplizer2;


import java.util.HashSet;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.test.dynamicentity.Address;
import org.hibernate.test.dynamicentity.Company;
import org.hibernate.test.dynamicentity.Customer;
import org.hibernate.test.dynamicentity.Person;
import org.hibernate.test.dynamicentity.ProxyHelper;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Demonstrates use of Tuplizers to allow the use of JDK
 * {@link java.lang.reflect.Proxy dynamic proxies} as our
 * domain model.
 * <p/>
 * Here we plug a custom Interceptor into the session simply to
 * allow us to not have to explicitly supply the appropriate entity
 * name to the Session calls.
 *
 * @author Steve Ebersole
 */
public class ImprovedTuplizerDynamicEntityTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testIt() {
        // Test saving these dyna-proxies
        Session session = openSession();
        session.beginTransaction();
        Company company = ProxyHelper.newCompanyProxy();
        company.setName("acme");
        session.save(company);
        Customer customer = ProxyHelper.newCustomerProxy();
        customer.setName("Steve");
        customer.setCompany(company);
        Address address = ProxyHelper.newAddressProxy();
        address.setStreet("somewhere over the rainbow");
        address.setCity("lawerence, kansas");
        address.setPostalCode("toto");
        customer.setAddress(address);
        customer.setFamily(new HashSet());
        Person son = ProxyHelper.newPersonProxy();
        son.setName("son");
        customer.getFamily().add(son);
        Person wife = ProxyHelper.newPersonProxy();
        wife.setName("wife");
        customer.getFamily().add(wife);
        session.save(customer);
        session.getTransaction().commit();
        session.close();
        Assert.assertNotNull("company id not assigned", company.getId());
        Assert.assertNotNull("customer id not assigned", customer.getId());
        Assert.assertNotNull("address id not assigned", address.getId());
        Assert.assertNotNull("son:Person id not assigned", son.getId());
        Assert.assertNotNull("wife:Person id not assigned", wife.getId());
        // Test loading these dyna-proxies, along with flush processing
        session = openSession();
        session.beginTransaction();
        customer = ((Customer) (session.load(Customer.class, customer.getId())));
        Assert.assertFalse("should-be-proxy was initialized", Hibernate.isInitialized(customer));
        customer.setName("other");
        session.flush();
        Assert.assertFalse("should-be-proxy was initialized", Hibernate.isInitialized(customer.getCompany()));
        session.refresh(customer);
        Assert.assertEquals("name not updated", "other", customer.getName());
        Assert.assertEquals("company association not correct", "acme", customer.getCompany().getName());
        session.getTransaction().commit();
        session.close();
        // Test detached entity re-attachment with these dyna-proxies
        customer.setName("Steve");
        session = openSession();
        session.beginTransaction();
        session.update(customer);
        session.flush();
        session.refresh(customer);
        Assert.assertEquals("name not updated", "Steve", customer.getName());
        session.getTransaction().commit();
        session.close();
        // Test querying
        session = openSession();
        session.beginTransaction();
        int count = session.createQuery("from Customer").list().size();
        Assert.assertEquals("querying dynamic entity", 1, count);
        session.clear();
        count = session.createQuery("from Person").list().size();
        Assert.assertEquals("querying dynamic entity", 3, count);
        session.getTransaction().commit();
        session.close();
        // test deleteing
        session = openSession();
        session.beginTransaction();
        session.delete(company);
        session.delete(customer);
        session.getTransaction().commit();
        session.close();
    }
}

