/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.iterate;


import java.util.Iterator;
import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
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
public class IterateTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIterate() throws Exception {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Item i1 = new Item("foo");
        Item i2 = new Item("bar");
        s.persist("Item", i1);
        s.persist("Item", i2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Iterator iter = s.getNamedQuery("Item.nameDesc").iterate();
        i1 = ((Item) (iter.next()));
        i2 = ((Item) (iter.next()));
        Assert.assertFalse(Hibernate.isInitialized(i1));
        Assert.assertFalse(Hibernate.isInitialized(i2));
        i1.getName();
        i2.getName();
        Assert.assertFalse(Hibernate.isInitialized(i1));
        Assert.assertFalse(Hibernate.isInitialized(i2));
        Assert.assertEquals(i1.getName(), "foo");
        Assert.assertEquals(i2.getName(), "bar");
        Hibernate.initialize(i1);
        Assert.assertFalse(iter.hasNext());
        s.delete(i1);
        s.delete(i2);
        t.commit();
        s.close();
        Assert.assertEquals(sessionFactory().getStatistics().getEntityFetchCount(), 2);
    }

    @Test
    public void testScroll() throws Exception {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Item i1 = new Item("foo");
        Item i2 = new Item("bar");
        s.persist("Item", i1);
        s.persist("Item", i2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        ScrollableResults sr = s.getNamedQuery("Item.nameDesc").scroll();
        Assert.assertTrue(sr.next());
        i1 = ((Item) (sr.get(0)));
        Assert.assertTrue(sr.next());
        i2 = ((Item) (sr.get(0)));
        Assert.assertTrue(Hibernate.isInitialized(i1));
        Assert.assertTrue(Hibernate.isInitialized(i2));
        Assert.assertEquals(i1.getName(), "foo");
        Assert.assertEquals(i2.getName(), "bar");
        Assert.assertFalse(sr.next());
        s.delete(i1);
        s.delete(i2);
        t.commit();
        s.close();
        Assert.assertEquals(sessionFactory().getStatistics().getEntityFetchCount(), 0);
    }
}

