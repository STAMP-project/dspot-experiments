/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetoone.joined;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class JoinedSubclassOneToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneOnSubclass() {
        Person p = new Person();
        p.name = "Gavin";
        Address a = new Address();
        a.entityName = "Gavin";
        a.zip = "3181";
        a.state = "VIC";
        a.street = "Karbarook Ave";
        p.address = a;
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        EntityStatistics addressStats = sessionFactory().getStatistics().getEntityStatistics(Address.class.getName());
        EntityStatistics mailingAddressStats = sessionFactory().getStatistics().getEntityStatistics("MailingAddress");
        p = ((Person) (s.createQuery("from Person p join fetch p.address left join fetch p.mailingAddress").uniqueResult()));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        p = ((Person) (s.createQuery("select p from Person p join fetch p.address left join fetch p.mailingAddress").uniqueResult()));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Object[] stuff = ((Object[]) (s.createQuery("select p.name, p from Person p join fetch p.address left join fetch p.mailingAddress").uniqueResult()));
        Assert.assertEquals(stuff.length, 2);
        p = ((Person) (stuff[1]));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
        Assert.assertEquals(mailingAddressStats.getFetchCount(), 0);
        p = ((Person) (s.createQuery("from Person p join fetch p.address").uniqueResult()));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
        Assert.assertEquals(mailingAddressStats.getFetchCount(), 1);
        p = ((Person) (s.createQuery("from Person").uniqueResult()));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
        Assert.assertEquals(mailingAddressStats.getFetchCount(), 2);
        p = ((Person) (s.createQuery("from Entity").uniqueResult()));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
        Assert.assertEquals(mailingAddressStats.getFetchCount(), 3);
        // note that in here join fetch is used for the nullable
        // one-to-one, due to a very special case of default
        p = ((Person) (s.get(Person.class, "Gavin")));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
        Assert.assertEquals(mailingAddressStats.getFetchCount(), 3);
        p = ((Person) (s.get(Entity.class, "Gavin")));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
        Assert.assertEquals(mailingAddressStats.getFetchCount(), 3);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Org org = new Org();
        org.name = "IFA";
        Address a2 = new Address();
        a2.entityName = "IFA";
        a2.zip = "3181";
        a2.state = "VIC";
        a2.street = "Orrong Rd";
        s.persist(org);
        s.persist(a2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.get(Entity.class, "IFA");
        s.clear();
        List list = s.createQuery("from Entity e order by e.name").list();
        p = ((Person) (list.get(0)));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        list.get(1);
        s.clear();
        list = s.createQuery("from Entity e left join fetch e.address left join fetch e.mailingAddress order by e.name").list();
        p = ((Person) (list.get(0)));
        org = ((Org) (list.get(1)));
        Assert.assertNotNull(p.address);
        Assert.assertNull(p.mailingAddress);
        s.clear();
        s.delete(p);
        s.delete(p.address);
        s.delete(org);
        s.delete(a2);
        s.flush();
        t.commit();
        s.close();
    }
}

