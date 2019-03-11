/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertyref.inheritence.union;


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
public class UnionSubclassPropertyRefTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOnePropertyRef() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Customer c = new Customer();
        c.setName("Emmanuel");
        c.setCustomerId("C123-456");
        c.setPersonId("P123-456");
        Account a = new Account();
        a.setCustomer(c);
        a.setPerson(c);
        a.setType('X');
        s.persist(c);
        s.persist(a);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        a = ((Account) (s.createQuery("from Account acc join fetch acc.customer join fetch acc.person").uniqueResult()));
        Assert.assertNotNull(a.getCustomer());
        Assert.assertTrue(Hibernate.isInitialized(a.getCustomer()));
        Assert.assertNotNull(a.getPerson());
        Assert.assertTrue(Hibernate.isInitialized(a.getPerson()));
        c = ((Customer) (s.createQuery("from Customer").uniqueResult()));
        Assert.assertSame(c, a.getCustomer());
        Assert.assertSame(c, a.getPerson());
        s.delete(a);
        s.delete(a.getCustomer());
        s.delete(a.getPerson());
        t.commit();
        s.close();
    }
}

