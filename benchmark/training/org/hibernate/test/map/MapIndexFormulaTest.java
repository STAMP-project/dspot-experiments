/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.map;


import java.util.List;
import java.util.Map;
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
public class MapIndexFormulaTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIndexFunctionOnManyToManyMap() {
        Session s = openSession();
        s.beginTransaction();
        s.createQuery("from Group g join g.users u where g.name = 'something' and index(u) = 'nada'").list();
        s.createQuery("from Group g join g.users u where g.name = 'something' and minindex(u) = 'nada'").list();
        s.createQuery("from Group g join g.users u where g.name = 'something' and maxindex(u) = 'nada'").list();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testIndexFormulaMap() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin", "secret");
        User turin = new User("turin", "tiger");
        Group g = new Group("developers");
        g.getUsers().put("gavin", gavin);
        g.getUsers().put("turin", turin);
        s.persist(g);
        gavin.getSession().put("foo", new SessionAttribute("foo", "foo bar baz"));
        gavin.getSession().put("bar", new SessionAttribute("bar", "foo bar baz 2"));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        g = ((Group) (s.get(Group.class, "developers")));
        Assert.assertEquals(g.getUsers().size(), 2);
        g.getUsers().remove("turin");
        Map smap = ((User) (g.getUsers().get("gavin"))).getSession();
        Assert.assertEquals(smap.size(), 2);
        smap.remove("bar");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        g = ((Group) (s.get(Group.class, "developers")));
        Assert.assertEquals(g.getUsers().size(), 1);
        smap = ((User) (g.getUsers().get("gavin"))).getSession();
        Assert.assertEquals(smap.size(), 1);
        gavin = ((User) (g.getUsers().put("gavin", turin)));
        s.delete(gavin);
        Assert.assertEquals(s.createQuery("select count(*) from SessionAttribute").uniqueResult(), new Long(0));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        g = ((Group) (s.get(Group.class, "developers")));
        Assert.assertEquals(g.getUsers().size(), 1);
        turin = ((User) (g.getUsers().get("turin")));
        smap = turin.getSession();
        Assert.assertEquals(smap.size(), 0);
        Assert.assertEquals(s.createQuery("select count(*) from User").uniqueResult(), Long.valueOf(1));
        s.delete(g);
        s.delete(turin);
        Assert.assertEquals(s.createQuery("select count(*) from User").uniqueResult(), Long.valueOf(0));
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testSQLQuery() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin", "secret");
        User turin = new User("turin", "tiger");
        gavin.getSession().put("foo", new SessionAttribute("foo", "foo bar baz"));
        gavin.getSession().put("bar", new SessionAttribute("bar", "foo bar baz 2"));
        s.persist(gavin);
        s.persist(turin);
        s.flush();
        s.clear();
        List results = s.getNamedQuery("userSessionData").setParameter("uname", "%in").list();
        Assert.assertEquals(results.size(), 2);
        gavin = ((User) (((Object[]) (results.get(0)))[0]));
        Assert.assertEquals(gavin.getName(), "gavin");
        Assert.assertEquals(gavin.getSession().size(), 2);
        s.createQuery("delete SessionAttribute").executeUpdate();
        s.createQuery("delete User").executeUpdate();
        t.commit();
        s.close();
    }
}

