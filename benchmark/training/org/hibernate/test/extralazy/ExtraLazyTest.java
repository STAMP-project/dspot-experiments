/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.extralazy;


import DialectChecks.DoubleQuoteQuoting;
import java.util.List;
import java.util.Map;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class ExtraLazyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOrphanDelete() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User gavin = new User("gavin", "secret");
        Document hia = new Document("HiA", "blah blah blah", gavin);
        Document hia2 = new Document("HiA2", "blah blah blah blah", gavin);
        s.persist(gavin);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(2, gavin.getDocuments().size());
        gavin.getDocuments().remove(hia2);
        Assert.assertFalse(gavin.getDocuments().contains(hia2));
        Assert.assertTrue(gavin.getDocuments().contains(hia));
        Assert.assertEquals(1, gavin.getDocuments().size());
        Assert.assertFalse(Hibernate.isInitialized(gavin.getDocuments()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        gavin = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(1, gavin.getDocuments().size());
        Assert.assertFalse(gavin.getDocuments().contains(hia2));
        Assert.assertTrue(gavin.getDocuments().contains(hia));
        Assert.assertFalse(Hibernate.isInitialized(gavin.getDocuments()));
        Assert.assertNull(s.get(Document.class, "HiA2"));
        gavin.getDocuments().clear();
        Assert.assertTrue(Hibernate.isInitialized(gavin.getDocuments()));
        s.delete(gavin);
        t.commit();
        s.close();
    }

    @Test
    public void testGet() {
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
        gavin = ((User) (g.getUsers().get("gavin")));
        turin = ((User) (g.getUsers().get("turin")));
        Assert.assertNotNull(gavin);
        Assert.assertNotNull(turin);
        Assert.assertNull(g.getUsers().get("emmanuel"));
        Assert.assertFalse(Hibernate.isInitialized(g.getUsers()));
        Assert.assertNotNull(gavin.getSession().get("foo"));
        Assert.assertNull(turin.getSession().get("foo"));
        Assert.assertFalse(Hibernate.isInitialized(gavin.getSession()));
        Assert.assertFalse(Hibernate.isInitialized(turin.getSession()));
        s.delete(gavin);
        s.delete(turin);
        s.delete(g);
        t.commit();
        s.close();
    }

    @Test
    public void testRemoveClear() {
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
        gavin = ((User) (g.getUsers().get("gavin")));
        turin = ((User) (g.getUsers().get("turin")));
        Assert.assertFalse(Hibernate.isInitialized(g.getUsers()));
        g.getUsers().clear();
        gavin.getSession().remove("foo");
        Assert.assertTrue(Hibernate.isInitialized(g.getUsers()));
        Assert.assertTrue(Hibernate.isInitialized(gavin.getSession()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        g = ((Group) (s.get(Group.class, "developers")));
        Assert.assertTrue(g.getUsers().isEmpty());
        Assert.assertFalse(Hibernate.isInitialized(g.getUsers()));
        gavin = ((User) (s.get(User.class, "gavin")));
        Assert.assertFalse(gavin.getSession().containsKey("foo"));
        Assert.assertFalse(Hibernate.isInitialized(gavin.getSession()));
        s.delete(gavin);
        s.delete(turin);
        s.delete(g);
        t.commit();
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
        Assert.assertEquals(s.createQuery("select count(*) from User").uniqueResult(), new Long(1));
        s.delete(g);
        s.delete(turin);
        Assert.assertEquals(s.createQuery("select count(*) from User").uniqueResult(), new Long(0));
        t.commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(DoubleQuoteQuoting.class)
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

    @Test
    @TestForIssue(jiraKey = "HHH-4294")
    public void testMap() {
        Session session1 = openSession();
        Transaction tx1 = session1.beginTransaction();
        Parent parent = new Parent();
        Child child = new Child();
        child.setFirstName("Ben");
        parent.getChildren().put(child.getFirstName(), child);
        child.setParent(parent);
        session1.save(parent);
        tx1.commit();
        session1.close();
        // END PREPARE SECTION
        Session session2 = openSession();
        Parent parent2 = ((Parent) (session2.get(Parent.class, parent.getId())));
        Child child2 = parent2.getChildren().get(child.getFirstName());// causes SQLGrammarException because of wrong condition: 	where child0_.PARENT_ID=? and child0_.null=?

        Assert.assertNotNull(child2);
        session2.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10874")
    public void testWhereClauseOnBidirectionalCollection() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        School school = new School(1);
        s.persist(school);
        Student gavin = new Student("gavin", 4);
        Student turin = new Student("turin", 3);
        Student mike = new Student("mike", 5);
        Student fred = new Student("fred", 2);
        gavin.setSchool(school);
        turin.setSchool(school);
        mike.setSchool(school);
        fred.setSchool(school);
        s.persist(gavin);
        s.persist(turin);
        s.persist(mike);
        s.persist(fred);
        t.commit();
        s.close();
        s = openSession();
        School school2 = s.get(School.class, 1);
        Assert.assertEquals(4, school2.getStudents().size());
        Assert.assertEquals(2, school2.getTopStudents().size());
        Assert.assertTrue(school2.getTopStudents().contains(gavin));
        Assert.assertTrue(school2.getTopStudents().contains(mike));
        Assert.assertEquals(2, school2.getStudentsMap().size());
        Assert.assertTrue(school2.getStudentsMap().containsKey(gavin.getId()));
        Assert.assertTrue(school2.getStudentsMap().containsKey(mike.getId()));
        s.close();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-3319")
    public void testWhereClauseOnUnidirectionalCollection() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Championship championship = new Championship(1);
        s.persist(championship);
        Student gavin = new Student("gavin", 4);
        Student turin = new Student("turin", 3);
        Student mike = new Student("mike", 5);
        Student fred = new Student("fred", 2);
        championship.getStudents().add(gavin);
        championship.getStudents().add(turin);
        championship.getStudents().add(mike);
        championship.getStudents().add(fred);
        s.persist(gavin);
        s.persist(turin);
        s.persist(mike);
        s.persist(fred);
        t.commit();
        s.close();
        s = openSession();
        Championship championship2 = s.get(Championship.class, 1);
        Assert.assertEquals(2, championship2.getStudents().size());
        Assert.assertTrue(championship2.getStudents().contains(gavin));
        Assert.assertTrue(championship2.getStudents().contains(mike));
        s.close();
    }
}

