/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.manytomany;


import FetchMode.JOIN;
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
public class ManyToManyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToManyWithFormula() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin", "jboss");
        Group seam = new Group("seam", "jboss");
        Group hb = new Group("hibernate", "jboss");
        gavin.getGroups().add(seam);
        gavin.getGroups().add(hb);
        seam.getUsers().add(gavin);
        hb.getUsers().add(gavin);
        s.persist(gavin);
        s.persist(seam);
        s.persist(hb);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.get(User.class, gavin)));
        Assert.assertFalse(Hibernate.isInitialized(gavin.getGroups()));
        Assert.assertEquals(2, gavin.getGroups().size());
        hb = ((Group) (s.get(Group.class, hb)));
        Assert.assertFalse(Hibernate.isInitialized(hb.getUsers()));
        Assert.assertEquals(1, hb.getUsers().size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.createCriteria(User.class).setFetchMode("groups", JOIN).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(gavin.getGroups()));
        Assert.assertEquals(2, gavin.getGroups().size());
        Group group = ((Group) (gavin.getGroups().iterator().next()));
        Assert.assertFalse(Hibernate.isInitialized(group.getUsers()));
        Assert.assertEquals(1, group.getUsers().size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.createCriteria(User.class).setFetchMode("groups", JOIN).setFetchMode("groups.users", JOIN).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(gavin.getGroups()));
        Assert.assertEquals(2, gavin.getGroups().size());
        group = ((Group) (gavin.getGroups().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(group.getUsers()));
        Assert.assertEquals(1, group.getUsers().size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.createQuery("from User u join fetch u.groups g join fetch g.users").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(gavin.getGroups()));
        Assert.assertEquals(2, gavin.getGroups().size());
        group = ((Group) (gavin.getGroups().iterator().next()));
        Assert.assertTrue(Hibernate.isInitialized(group.getUsers()));
        Assert.assertEquals(1, group.getUsers().size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.get(User.class, gavin)));
        hb = ((Group) (s.get(Group.class, hb)));
        gavin.getGroups().remove(hb);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.get(User.class, gavin)));
        Assert.assertEquals(gavin.getGroups().size(), 1);
        hb = ((Group) (s.get(Group.class, hb)));
        Assert.assertEquals(hb.getUsers().size(), 0);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(gavin);
        s.flush();
        s.createQuery("delete from Group").executeUpdate();
        t.commit();
        s.close();
    }
}

