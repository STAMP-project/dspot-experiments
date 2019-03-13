/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertyref.component.complete;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class CompleteComponentPropertyRefTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testComponentPropertyRef() {
        Session s = openSession();
        s.beginTransaction();
        Person p = new Person();
        p.setIdentity(new Identity());
        Account a = new Account();
        a.setNumber("123-12345-1236");
        a.setOwner(p);
        p.getIdentity().setName("Gavin");
        p.getIdentity().setSsn("123-12-1234");
        s.persist(p);
        s.persist(a);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        a = ((Account) (s.createQuery("from Account a left join fetch a.owner").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(a.getOwner()));
        Assert.assertNotNull(a.getOwner());
        Assert.assertEquals("Gavin", a.getOwner().getIdentity().getName());
        s.clear();
        a = ((Account) (s.get(Account.class, "123-12345-1236")));
        Assert.assertFalse(Hibernate.isInitialized(a.getOwner()));
        Assert.assertNotNull(a.getOwner());
        Assert.assertEquals("Gavin", a.getOwner().getIdentity().getName());
        Assert.assertTrue(Hibernate.isInitialized(a.getOwner()));
        s.clear();
        sessionFactory().getCache().evictEntityRegion(Account.class);
        sessionFactory().getCache().evictEntityRegion(Person.class);
        a = ((Account) (s.get(Account.class, "123-12345-1236")));
        Assert.assertTrue(Hibernate.isInitialized(a.getOwner()));
        Assert.assertNotNull(a.getOwner());
        Assert.assertEquals("Gavin", a.getOwner().getIdentity().getName());
        Assert.assertTrue(Hibernate.isInitialized(a.getOwner()));
        s.delete(a);
        s.delete(a.getOwner());
        s.getTransaction().commit();
        s.close();
    }
}

