/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.manytomany.ordered;


import FetchMode.JOIN;
import FetchMode.SELECT;
import org.hibernate.Criteria;
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
public class OrderedManyToManyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToManyOrdering() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin", "jboss");
        User steve = new User("steve", "jboss");
        User max = new User("max", "jboss");
        User emmanuel = new User("emmanuel", "jboss");
        s.persist(gavin);
        s.persist(steve);
        s.persist(max);
        s.persist(emmanuel);
        Group hibernate = new Group("hibernate", "jboss");
        hibernate.addUser(gavin);
        hibernate.addUser(steve);
        hibernate.addUser(max);
        hibernate.addUser(emmanuel);
        s.persist(hibernate);
        t.commit();
        s.close();
        // delayed collection load...
        s = openSession();
        t = s.beginTransaction();
        hibernate = ((Group) (s.get(Group.class, hibernate.getId())));
        Assert.assertFalse(Hibernate.isInitialized(hibernate.getUsers()));
        Assert.assertEquals(4, hibernate.getUsers().size());
        assertOrdering(hibernate.getUsers());
        t.commit();
        s.close();
        // HQL (non eager)
        s = openSession();
        t = s.beginTransaction();
        hibernate = ((Group) (s.createQuery("from Group").uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(hibernate.getUsers()));
        Assert.assertEquals(4, hibernate.getUsers().size());
        assertOrdering(hibernate.getUsers());
        t.commit();
        s.close();
        // HQL (eager)
        s = openSession();
        t = s.beginTransaction();
        hibernate = ((Group) (s.createQuery("from Group g inner join fetch g.users").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(hibernate.getUsers()));
        Assert.assertEquals(4, hibernate.getUsers().size());
        assertOrdering(hibernate.getUsers());
        t.commit();
        s.close();
        // criteria load (forced eager fetch)
        s = openSession();
        t = s.beginTransaction();
        Criteria criteria = s.createCriteria(Group.class);
        criteria.setFetchMode("users", JOIN);
        hibernate = ((Group) (criteria.uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(hibernate.getUsers()));
        Assert.assertEquals(4, hibernate.getUsers().size());
        assertOrdering(hibernate.getUsers());
        t.commit();
        s.close();
        // criteria load (forced non eager fetch)
        s = openSession();
        t = s.beginTransaction();
        criteria = s.createCriteria(Group.class);
        criteria.setFetchMode("users", SELECT);
        hibernate = ((Group) (criteria.uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(hibernate.getUsers()));
        Assert.assertEquals(4, hibernate.getUsers().size());
        assertOrdering(hibernate.getUsers());
        t.commit();
        s.close();
        // clean up
        s = openSession();
        t = s.beginTransaction();
        s.delete(gavin);
        s.delete(steve);
        s.delete(max);
        s.delete(emmanuel);
        s.delete(hibernate);
        t.commit();
        s.close();
    }
}

