/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cuk;


import java.util.List;
import java.util.Set;
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
public class CompositePropertyRefTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked", "UnusedAssignment" })
    public void testOneToOnePropertyRef() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Person p = new Person();
        p.setName("Steve");
        p.setUserId("steve");
        Address a = new Address();
        a.setAddress("Texas");
        a.setCountry("USA");
        p.setAddress(a);
        a.setPerson(p);
        s.save(p);
        Person p2 = new Person();
        p2.setName("Max");
        p2.setUserId("max");
        s.save(p2);
        Account act = new Account();
        act.setType('c');
        act.setUser(p2);
        p2.getAccounts().add(act);
        s.save(act);
        s.flush();
        s.clear();
        p = s.get(Person.class, p.getId());// get address reference by outer join

        p2 = s.get(Person.class, p2.getId());// get null address reference by outer join

        Assert.assertNull(p2.getAddress());
        Assert.assertNotNull(p.getAddress());
        List l = s.createQuery("from Person").list();// pull address references for cache

        Assert.assertEquals(l.size(), 2);
        Assert.assertTrue(((l.contains(p)) && (l.contains(p2))));
        s.clear();
        l = s.createQuery("from Person p order by p.name").list();// get address references by sequential selects

        Assert.assertEquals(l.size(), 2);
        Assert.assertNull(((Person) (l.get(0))).getAddress());
        Assert.assertNotNull(((Person) (l.get(1))).getAddress());
        s.clear();
        l = s.createQuery("from Person p left join fetch p.address a order by a.country").list();// get em by outer join

        Assert.assertEquals(l.size(), 2);
        if (((Person) (l.get(0))).getName().equals("Max")) {
            Assert.assertNull(((Person) (l.get(0))).getAddress());
            Assert.assertNotNull(((Person) (l.get(1))).getAddress());
        } else {
            Assert.assertNull(((Person) (l.get(1))).getAddress());
            Assert.assertNotNull(((Person) (l.get(0))).getAddress());
        }
        s.clear();
        l = s.createQuery("from Person p left join p.accounts").list();
        for (int i = 0; i < 2; i++) {
            Object[] row = ((Object[]) (l.get(i)));
            Person px = ((Person) (row[0]));
            Set accounts = px.getAccounts();
            Assert.assertFalse(Hibernate.isInitialized(accounts));
            Assert.assertTrue((((px.getAccounts().size()) > 0) || ((row[1]) == null)));
        }
        s.clear();
        l = s.createQuery("from Person p left join fetch p.accounts a order by p.name").list();
        Person p0 = ((Person) (l.get(0)));
        Assert.assertTrue(Hibernate.isInitialized(p0.getAccounts()));
        Assert.assertEquals(p0.getAccounts().size(), 1);
        Assert.assertSame(((Account) (p0.getAccounts().iterator().next())).getUser(), p0);
        Person p1 = ((Person) (l.get(1)));
        Assert.assertTrue(Hibernate.isInitialized(p1.getAccounts()));
        Assert.assertEquals(p1.getAccounts().size(), 0);
        s.clear();
        l = s.createQuery("from Account a join fetch a.user").list();
        s.clear();
        l = s.createQuery("from Person p left join fetch p.address").list();
        s.clear();
        s.createQuery("delete Address").executeUpdate();
        s.createQuery("delete Account").executeUpdate();
        s.createQuery("delete Person").executeUpdate();
        t.commit();
        s.close();
    }
}

