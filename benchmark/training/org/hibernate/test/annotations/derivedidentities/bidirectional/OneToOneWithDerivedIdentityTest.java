/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.bidirectional;


import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class OneToOneWithDerivedIdentityTest extends BaseCoreFunctionalTestCase {
    // @TestForIssue(jiraKey = "HHH-5695")
    @Test
    @TestForIssue(jiraKey = "HHH-11903")
    @FailureExpected(jiraKey = "HHH-11903")
    public void testInsertFooAndBarWithDerivedId() {
        Session s = openSession();
        s.beginTransaction();
        Bar bar = new Bar();
        bar.setDetails("Some details");
        Foo foo = new Foo();
        foo.setBar(bar);
        bar.setFoo(foo);
        s.persist(foo);
        s.flush();
        Assert.assertNotNull(foo.getId());
        Assert.assertEquals(foo.getId(), bar.getFoo().getId());
        s.clear();
        Bar newBar = ((Bar) (s.createQuery("SELECT b FROM Bar b WHERE b.foo.id = :id").setParameter("id", foo.getId()).uniqueResult()));
        Assert.assertNotNull(newBar);
        Assert.assertNotNull(newBar.getFoo());
        Assert.assertEquals(foo.getId(), newBar.getFoo().getId());
        Assert.assertEquals("Some details", newBar.getDetails());
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10476")
    public void testInsertFooAndBarWithDerivedIdPC() {
        Session s = openSession();
        s.beginTransaction();
        Bar bar = new Bar();
        bar.setDetails("Some details");
        Foo foo = new Foo();
        foo.setBar(bar);
        bar.setFoo(foo);
        s.persist(foo);
        s.flush();
        Assert.assertNotNull(foo.getId());
        Assert.assertEquals(foo.getId(), bar.getFoo().getId());
        s.clear();
        Bar barWithFoo = new Bar();
        barWithFoo.setFoo(foo);
        barWithFoo.setDetails("wrong details");
        bar = ((Bar) (s.get(Bar.class, barWithFoo)));
        Assert.assertSame(bar, barWithFoo);
        Assert.assertEquals("Some details", bar.getDetails());
        SessionImplementor si = ((SessionImplementor) (s));
        Assert.assertTrue(si.getPersistenceContext().isEntryFor(bar));
        Assert.assertFalse(si.getPersistenceContext().isEntryFor(bar.getFoo()));
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6813")
    public void testSelectWithDerivedId() {
        Session s = openSession();
        s.beginTransaction();
        Bar bar = new Bar();
        bar.setDetails("Some details");
        Foo foo = new Foo();
        foo.setBar(bar);
        bar.setFoo(foo);
        s.persist(foo);
        s.flush();
        Assert.assertNotNull(foo.getId());
        Assert.assertEquals(foo.getId(), bar.getFoo().getId());
        s.clear();
        Foo newFoo = ((Foo) (s.createQuery("SELECT f FROM Foo f").uniqueResult()));
        Assert.assertNotNull(newFoo);
        Assert.assertNotNull(newFoo.getBar());
        Assert.assertSame(newFoo, newFoo.getBar().getFoo());
        Assert.assertEquals("Some details", newFoo.getBar().getDetails());
        s.getTransaction().rollback();
        s.close();
    }

    // Regression test utilizing multiple types of queries.
    @Test
    @TestForIssue(jiraKey = "HHH-6813")
    public void testCase() {
        Session s = openSession();
        s.getTransaction().begin();
        Person p = new Person();
        p.setName("Alfio");
        PersonInfo pi = new PersonInfo();
        pi.setId(p);
        pi.setInfo("Some information");
        s.persist(p);
        s.persist(pi);
        s.getTransaction().commit();
        s.clear();
        s.getTransaction().begin();
        Query q = s.getNamedQuery("PersonQuery");
        List<Person> persons = q.list();
        Assert.assertEquals(persons.size(), 1);
        Assert.assertEquals(persons.get(0).getName(), "Alfio");
        s.getTransaction().commit();
        s.clear();
        s.getTransaction().begin();
        p = ((Person) (s.get(Person.class, persons.get(0).getId())));
        Assert.assertEquals(p.getName(), "Alfio");
        s.getTransaction().commit();
        s.close();
    }
}

