/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.timestamp;


import java.util.Date;
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
public class TimestampTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUpdateFalse() {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("gavin", "secret", new Person("Gavin King", new Date(), "Karbarook Ave"));
        s.persist(u);
        s.flush();
        u.getPerson().setName("XXXXYYYYY");
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getEntityInsertCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityUpdateCount());
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(u.getPerson().getName(), "Gavin King");
        s.delete(u);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getEntityDeleteCount());
    }

    @Test
    public void testComponent() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("gavin", "secret", new Person("Gavin King", new Date(), "Karbarook Ave"));
        s.persist(u);
        s.flush();
        u.getPerson().setCurrentAddress("Peachtree Rd");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.get(User.class, "gavin")));
        u.setPassword("$ecret");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(u.getPassword(), "$ecret");
        s.delete(u);
        t.commit();
        s.close();
    }
}

