/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.subselectfetch;


import FetchMode.JOIN;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class SubselectFetchTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSubselectFetchHql() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("foo");
        p.getChildren().add(new Child("foo1"));
        p.getChildren().add(new Child("foo2"));
        Parent q = new Parent("bar");
        q.getChildren().add(new Child("bar1"));
        q.getChildren().add(new Child("bar2"));
        q.getMoreChildren().addAll(p.getChildren());
        s.persist(p);
        s.persist(q);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        sessionFactory().getStatistics().clear();
        List parents = s.createQuery("from Parent where name between 'bar' and 'foo' order by name desc").list();
        p = ((Parent) (parents.get(0)));
        q = ((Parent) (parents.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(p.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(q.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren().iterator().next()));
        Assert.assertFalse(Hibernate.isInitialized(p.getMoreChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(p.getMoreChildren().size(), 0);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(q.getMoreChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren().iterator().next()));
        Assert.assertEquals(3, sessionFactory().getStatistics().getPrepareStatementCount());
        Child c = ((Child) (p.getChildren().get(0)));
        c.getFriends().size();
        s.delete(p);
        s.delete(q);
        t.commit();
        s.close();
    }

    @Test
    public void testSubselectFetchNamedParam() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("foo");
        p.getChildren().add(new Child("foo1"));
        p.getChildren().add(new Child("foo2"));
        Parent q = new Parent("bar");
        q.getChildren().add(new Child("bar1"));
        q.getChildren().add(new Child("bar2"));
        q.getMoreChildren().addAll(p.getChildren());
        s.persist(p);
        s.persist(q);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        sessionFactory().getStatistics().clear();
        List parents = s.createQuery("from Parent where name between :bar and :foo order by name desc").setParameter("bar", "bar").setParameter("foo", "foo").list();
        p = ((Parent) (parents.get(0)));
        q = ((Parent) (parents.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(p.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(q.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren().iterator().next()));
        Assert.assertFalse(Hibernate.isInitialized(p.getMoreChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(p.getMoreChildren().size(), 0);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(q.getMoreChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren().iterator().next()));
        Assert.assertEquals(3, sessionFactory().getStatistics().getPrepareStatementCount());
        Child c = ((Child) (p.getChildren().get(0)));
        c.getFriends().size();
        s.delete(p);
        s.delete(q);
        t.commit();
        s.close();
    }

    @Test
    public void testSubselectFetchPosParam() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("foo");
        p.getChildren().add(new Child("foo1"));
        p.getChildren().add(new Child("foo2"));
        Parent q = new Parent("bar");
        q.getChildren().add(new Child("bar1"));
        q.getChildren().add(new Child("bar2"));
        q.getMoreChildren().addAll(p.getChildren());
        s.persist(p);
        s.persist(q);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        sessionFactory().getStatistics().clear();
        List parents = s.createQuery("from Parent where name between ?1 and ?2 order by name desc").setParameter(1, "bar").setParameter(2, "foo").list();
        p = ((Parent) (parents.get(0)));
        q = ((Parent) (parents.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(p.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(q.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren().iterator().next()));
        Assert.assertFalse(Hibernate.isInitialized(p.getMoreChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(p.getMoreChildren().size(), 0);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(q.getMoreChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren().iterator().next()));
        Assert.assertEquals(3, sessionFactory().getStatistics().getPrepareStatementCount());
        Child c = ((Child) (p.getChildren().get(0)));
        c.getFriends().size();
        s.delete(p);
        s.delete(q);
        t.commit();
        s.close();
    }

    @Test
    public void testSubselectFetchWithLimit() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("foo");
        p.getChildren().add(new Child("foo1"));
        p.getChildren().add(new Child("foo2"));
        Parent q = new Parent("bar");
        q.getChildren().add(new Child("bar1"));
        q.getChildren().add(new Child("bar2"));
        Parent r = new Parent("aaa");
        r.getChildren().add(new Child("aaa1"));
        s.persist(p);
        s.persist(q);
        s.persist(r);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        sessionFactory().getStatistics().clear();
        List parents = s.createQuery("from Parent order by name desc").setMaxResults(2).list();
        p = ((Parent) (parents.get(0)));
        q = ((Parent) (parents.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(p.getMoreChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(p.getMoreChildren().size(), 0);
        Assert.assertEquals(p.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren()));
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(3, sessionFactory().getStatistics().getPrepareStatementCount());
        r = ((Parent) (s.get(Parent.class, r.getName())));
        Assert.assertTrue(Hibernate.isInitialized(r.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(r.getMoreChildren()));
        Assert.assertEquals(r.getChildren().size(), 1);
        Assert.assertEquals(r.getMoreChildren().size(), 0);
        s.delete(p);
        s.delete(q);
        s.delete(r);
        t.commit();
        s.close();
    }

    @Test
    public void testManyToManyCriteriaJoin() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("foo");
        p.getChildren().add(new Child("foo1"));
        p.getChildren().add(new Child("foo2"));
        Parent q = new Parent("bar");
        q.getChildren().add(new Child("bar1"));
        q.getChildren().add(new Child("bar2"));
        q.getMoreChildren().addAll(p.getChildren());
        s.persist(p);
        s.persist(q);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List parents = s.createCriteria(Parent.class).createCriteria("moreChildren").createCriteria("friends").addOrder(Order.desc("name")).list();
        parents = s.createCriteria(Parent.class).setFetchMode("moreChildren", JOIN).setFetchMode("moreChildren.friends", JOIN).addOrder(Order.desc("name")).list();
        s.delete(parents.get(0));
        s.delete(parents.get(1));
        t.commit();
        s.close();
    }

    @Test
    public void testSubselectFetchCriteria() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent("foo");
        p.getChildren().add(new Child("foo1"));
        p.getChildren().add(new Child("foo2"));
        Parent q = new Parent("bar");
        q.getChildren().add(new Child("bar1"));
        q.getChildren().add(new Child("bar2"));
        q.getMoreChildren().addAll(p.getChildren());
        s.persist(p);
        s.persist(q);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        sessionFactory().getStatistics().clear();
        List parents = s.createCriteria(Parent.class).add(Property.forName("name").between("bar", "foo")).addOrder(Order.desc("name")).list();
        p = ((Parent) (parents.get(0)));
        q = ((Parent) (parents.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(p.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren()));
        Assert.assertEquals(q.getChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getChildren().iterator().next()));
        Assert.assertFalse(Hibernate.isInitialized(p.getMoreChildren()));
        Assert.assertFalse(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(p.getMoreChildren().size(), 0);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren()));
        Assert.assertEquals(q.getMoreChildren().size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(q.getMoreChildren().iterator().next()));
        Assert.assertEquals(3, sessionFactory().getStatistics().getPrepareStatementCount());
        Child c = ((Child) (p.getChildren().get(0)));
        c.getFriends().size();
        s.delete(p);
        s.delete(q);
        t.commit();
        s.close();
    }
}

