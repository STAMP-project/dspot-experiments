/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.joinfetch;


import FetchMode.JOIN;
import FetchMode.SELECT;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class JoinFetchTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testProjection() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createCriteria(Item.class).setProjection(Projections.rowCount()).uniqueResult();
        s.createCriteria(Item.class).uniqueResult();
        t.commit();
        s.close();
    }

    @Test
    public void testJoinFetch() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("delete from Bid").executeUpdate();
        s.createQuery("delete from Comment").executeUpdate();
        s.createQuery("delete from Item").executeUpdate();
        t.commit();
        s.close();
        Category cat = new Category("Photography");
        Item i = new Item(cat, "Camera");
        Bid b = new Bid(i, 100.0F);
        new Bid(i, 105.0F);
        new Comment(i, "This looks like a really good deal");
        new Comment(i, "Is it the latest version?");
        new Comment(i, "<comment deleted>");
        System.out.println(b.getTimestamp());
        s = openSession();
        t = s.beginTransaction();
        s.persist(cat);
        s.persist(i);
        t.commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Item.class);
        s = openSession();
        t = s.beginTransaction();
        i = s.get(Item.class, i.getId());
        Assert.assertTrue(Hibernate.isInitialized(i.getBids()));
        Assert.assertEquals(i.getBids().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(i.getComments()));
        Assert.assertEquals(i.getComments().size(), 3);
        t.commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Bid.class);
        s = openSession();
        t = s.beginTransaction();
        b = s.get(Bid.class, b.getId());
        Assert.assertTrue(Hibernate.isInitialized(b.getItem()));
        Assert.assertTrue(Hibernate.isInitialized(b.getItem().getComments()));
        Assert.assertEquals(b.getItem().getComments().size(), 3);
        System.out.println(b.getTimestamp());
        t.commit();
        s.close();
        sessionFactory().getCache().evictCollectionRegion(((Item.class.getName()) + ".bids"));
        s = openSession();
        t = s.beginTransaction();
        i = ((Item) (s.createCriteria(Item.class).setFetchMode("bids", SELECT).setFetchMode("comments", SELECT).uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(i.getBids()));
        Assert.assertFalse(Hibernate.isInitialized(i.getComments()));
        b = ((Bid) (i.getBids().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(b.getItem()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        i = ((Item) (s.createQuery("from Item i left join fetch i.bids left join fetch i.comments").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(i.getBids()));
        Assert.assertTrue(Hibernate.isInitialized(i.getComments()));
        Assert.assertEquals(i.getComments().size(), 3);
        Assert.assertEquals(i.getBids().size(), 2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Object[] row = ((Object[]) (s.getNamedQuery(((Item.class.getName()) + ".all")).list().get(0)));
        i = ((Item) (row[0]));
        Assert.assertTrue(Hibernate.isInitialized(i.getBids()));
        Assert.assertTrue(Hibernate.isInitialized(i.getComments()));
        Assert.assertEquals(i.getComments().size(), 3);
        Assert.assertEquals(i.getBids().size(), 2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        i = ((Item) (s.createCriteria(Item.class).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(i.getBids()));
        Assert.assertTrue(Hibernate.isInitialized(i.getComments()));
        Assert.assertEquals(i.getComments().size(), 3);
        Assert.assertEquals(i.getBids().size(), 2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List bids = s.createQuery("from Bid b left join fetch b.item i left join fetch i.category").list();
        Bid bid = ((Bid) (bids.get(0)));
        Assert.assertTrue(Hibernate.isInitialized(bid.getItem()));
        Assert.assertTrue(Hibernate.isInitialized(bid.getItem().getCategory()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List pairs = s.createQuery("from Item i left join i.bids b left join fetch i.category").list();
        Item item = ((Item) (((Object[]) (pairs.get(0)))[0]));
        Assert.assertFalse(Hibernate.isInitialized(item.getBids()));
        Assert.assertTrue(Hibernate.isInitialized(item.getCategory()));
        s.clear();
        pairs = s.createQuery("from Item i left join i.bids b left join i.category").list();
        item = ((Item) (((Object[]) (pairs.get(0)))[0]));
        Assert.assertFalse(Hibernate.isInitialized(item.getBids()));
        Assert.assertTrue(Hibernate.isInitialized(item.getCategory()));
        s.clear();
        pairs = s.createQuery("from Bid b left join b.item i left join fetch i.category").list();
        bid = ((Bid) (((Object[]) (pairs.get(0)))[0]));
        Assert.assertTrue(Hibernate.isInitialized(bid.getItem()));
        Assert.assertTrue(Hibernate.isInitialized(bid.getItem().getCategory()));
        s.clear();
        pairs = s.createQuery("from Bid b left join b.item i left join i.category").list();
        bid = ((Bid) (((Object[]) (pairs.get(0)))[0]));
        Assert.assertTrue(Hibernate.isInitialized(bid.getItem()));
        Assert.assertTrue(Hibernate.isInitialized(bid.getItem().getCategory()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from Bid").executeUpdate();
        s.createQuery("delete from Comment").executeUpdate();
        s.createQuery("delete from Item").executeUpdate();
        s.createQuery("delete from Category").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    public void testCollectionFilter() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Group hb = new Group("hibernate");
        User gavin = new User("gavin");
        User max = new User("max");
        hb.getUsers().put("gavin", gavin);
        hb.getUsers().put("max", max);
        gavin.getGroups().put("hibernate", hb);
        max.getGroups().put("hibernate", hb);
        s.persist(hb);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        hb = ((Group) (s.createCriteria(Group.class).setFetchMode("users", SELECT).add(Restrictions.idEq("hibernate")).uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(hb.getUsers()));
        // gavin = (User) s.createFilter( hb.getUsers(), "where index(this) = 'gavin'" ).uniqueResult();
        Long size = ((Long) (s.createFilter(hb.getUsers(), "select count(*)").uniqueResult()));
        Assert.assertEquals(new Long(2), size);
        Assert.assertFalse(Hibernate.isInitialized(hb.getUsers()));
        s.delete(hb);
        t.commit();
        s.close();
    }

    @Test
    public void testJoinFetchManyToMany() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Group hb = new Group("hibernate");
        User gavin = new User("gavin");
        User max = new User("max");
        hb.getUsers().put("gavin", gavin);
        hb.getUsers().put("max", max);
        gavin.getGroups().put("hibernate", hb);
        max.getGroups().put("hibernate", hb);
        s.persist(hb);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        hb = s.get(Group.class, "hibernate");
        Assert.assertTrue(Hibernate.isInitialized(hb.getUsers()));
        gavin = ((User) (hb.getUsers().get("gavin")));
        Assert.assertFalse(Hibernate.isInitialized(gavin.getGroups()));
        max = s.get(User.class, "max");
        Assert.assertFalse(Hibernate.isInitialized(max.getGroups()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        hb = ((Group) (s.createCriteria(Group.class).setFetchMode("users", JOIN).setFetchMode("users.groups", JOIN).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(hb.getUsers()));
        gavin = ((User) (hb.getUsers().get("gavin")));
        Assert.assertTrue(Hibernate.isInitialized(gavin.getGroups()));
        max = s.get(User.class, "max");
        Assert.assertTrue(Hibernate.isInitialized(max.getGroups()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(hb);
        t.commit();
        s.close();
    }
}

