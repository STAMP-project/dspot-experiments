/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idbag;


import java.sql.SQLException;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
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
public class IdBagTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUpdateIdBag() throws SQLException, HibernateException {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin");
        Group admins = new Group("admins");
        Group plebs = new Group("plebs");
        Group moderators = new Group("moderators");
        Group banned = new Group("banned");
        gavin.getGroups().add(plebs);
        // gavin.getGroups().add(moderators);
        s.persist(gavin);
        s.persist(plebs);
        s.persist(admins);
        s.persist(moderators);
        s.persist(banned);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.createCriteria(User.class).uniqueResult()));
        admins = ((Group) (s.load(Group.class, "admins")));
        plebs = ((Group) (s.load(Group.class, "plebs")));
        banned = ((Group) (s.load(Group.class, "banned")));
        gavin.getGroups().add(admins);
        gavin.getGroups().remove(plebs);
        // gavin.getGroups().add(banned);
        s.delete(plebs);
        s.delete(banned);
        s.delete(moderators);
        s.delete(admins);
        s.delete(gavin);
        t.commit();
        s.close();
    }

    @Test
    public void testJoin() throws SQLException, HibernateException {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin");
        Group admins = new Group("admins");
        Group plebs = new Group("plebs");
        gavin.getGroups().add(plebs);
        gavin.getGroups().add(admins);
        s.persist(gavin);
        s.persist(plebs);
        s.persist(admins);
        List l = s.createQuery("from User u join u.groups g").list();
        Assert.assertEquals(l.size(), 2);
        s.clear();
        gavin = ((User) (s.createQuery("from User u join fetch u.groups").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(gavin.getGroups()));
        Assert.assertEquals(gavin.getGroups().size(), 2);
        Assert.assertEquals(((Group) (gavin.getGroups().get(0))).getName(), "admins");
        s.delete(gavin.getGroups().get(0));
        s.delete(gavin.getGroups().get(1));
        s.delete(gavin);
        t.commit();
        s.close();
    }
}

