/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.collectionelement;


import java.util.HashSet;
import java.util.Iterator;
import junit.framework.Assert;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.TeradataDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


public class OrderByTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOrderByName() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Products p = new Products();
        HashSet<Widgets> set = new HashSet<Widgets>();
        Widgets widget = new Widgets();
        widget.setName("hammer");
        set.add(widget);
        s.persist(widget);
        widget = new Widgets();
        widget.setName("axel");
        set.add(widget);
        s.persist(widget);
        widget = new Widgets();
        widget.setName("screwdriver");
        set.add(widget);
        s.persist(widget);
        p.setWidgets(set);
        s.persist(p);
        tx.commit();
        tx = s.beginTransaction();
        s.clear();
        p = ((Products) (s.get(Products.class, p.getId())));
        Assert.assertTrue("has three Widgets", ((p.getWidgets().size()) == 3));
        Iterator iter = p.getWidgets().iterator();
        Assert.assertEquals("axel", ((Widgets) (iter.next())).getName());
        Assert.assertEquals("hammer", ((Widgets) (iter.next())).getName());
        Assert.assertEquals("screwdriver", ((Widgets) (iter.next())).getName());
        tx.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = TeradataDialect.class, jiraKey = "HHH-8190", comment = "uses Teradata reserved word - summary")
    public void testOrderByWithDottedNotation() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        BugSystem bs = new BugSystem();
        HashSet<Bug> set = new HashSet<Bug>();
        Bug bug = new Bug();
        bug.setDescription("JPA-2 locking");
        bug.setSummary("JPA-2 impl locking");
        Person p = new Person();
        p.setFirstName("Scott");
        p.setLastName("Marlow");
        bug.setReportedBy(p);
        set.add(bug);
        bug = new Bug();
        bug.setDescription("JPA-2 annotations");
        bug.setSummary("JPA-2 impl annotations");
        p = new Person();
        p.setFirstName("Emmanuel");
        p.setLastName("Bernard");
        bug.setReportedBy(p);
        set.add(bug);
        bug = new Bug();
        bug.setDescription("Implement JPA-2 criteria");
        bug.setSummary("JPA-2 impl criteria");
        p = new Person();
        p.setFirstName("Steve");
        p.setLastName("Ebersole");
        bug.setReportedBy(p);
        set.add(bug);
        bs.setBugs(set);
        s.persist(bs);
        tx.commit();
        tx = s.beginTransaction();
        s.clear();
        bs = ((BugSystem) (s.get(BugSystem.class, bs.getId())));
        Assert.assertTrue("has three bugs", ((bs.getBugs().size()) == 3));
        Iterator iter = bs.getBugs().iterator();
        Assert.assertEquals("Emmanuel", ((Bug) (iter.next())).getReportedBy().getFirstName());
        Assert.assertEquals("Steve", ((Bug) (iter.next())).getReportedBy().getFirstName());
        Assert.assertEquals("Scott", ((Bug) (iter.next())).getReportedBy().getFirstName());
        tx.commit();
        s.close();
    }
}

