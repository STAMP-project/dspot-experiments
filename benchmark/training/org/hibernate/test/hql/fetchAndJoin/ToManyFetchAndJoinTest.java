/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.hql.fetchAndJoin;


import java.util.Iterator;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class ToManyFetchAndJoinTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testExplicitJoinBeforeFetchJoins() {
        Session s = openSession();
        s.getTransaction().begin();
        Parent p = ((Parent) (s.createQuery("select p from Parent p inner join p.children cRestrict inner join fetch p.children c inner join fetch c.grandChildren where cRestrict.value = 'c1'").uniqueResult()));
        Assert.assertEquals("p", p.getValue());
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(2, p.getChildren().size());
        Iterator<Child> iterator = p.getChildren().iterator();
        Child cA = iterator.next();
        Assert.assertTrue(Hibernate.isInitialized(cA.getGrandChildren()));
        if (cA.getValue().equals("c1")) {
            Assert.assertEquals(2, cA.getGrandChildren().size());
            Child cB = iterator.next();
            Assert.assertTrue(Hibernate.isInitialized(cB.getGrandChildren()));
            Assert.assertEquals(3, cB.getGrandChildren().size());
        } else
            if (cA.getValue().equals("c2")) {
                Assert.assertEquals(3, cA.getGrandChildren().size());
                Child cB = iterator.next();
                Assert.assertTrue(Hibernate.isInitialized(cB.getGrandChildren()));
                Assert.assertEquals(2, cB.getGrandChildren().size());
            } else {
                Assert.fail("unexpected value");
            }

        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testExplicitJoinBetweenFetchJoins() {
        Session s = openSession();
        s.getTransaction().begin();
        Parent p = ((Parent) (s.createQuery("select p from Parent p inner join fetch p.children c inner join p.children cRestrict inner join fetch c.grandChildren where cRestrict.value = 'c1'").uniqueResult()));
        Assert.assertEquals("p", p.getValue());
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(2, p.getChildren().size());
        Iterator<Child> iterator = p.getChildren().iterator();
        Child cA = iterator.next();
        Assert.assertTrue(Hibernate.isInitialized(cA.getGrandChildren()));
        if (cA.getValue().equals("c1")) {
            Assert.assertEquals(2, cA.getGrandChildren().size());
            Child cB = iterator.next();
            Assert.assertTrue(Hibernate.isInitialized(cB.getGrandChildren()));
            Assert.assertEquals(3, cB.getGrandChildren().size());
        } else
            if (cA.getValue().equals("c2")) {
                Assert.assertEquals(3, cA.getGrandChildren().size());
                Child cB = iterator.next();
                Assert.assertTrue(Hibernate.isInitialized(cB.getGrandChildren()));
                Assert.assertEquals(2, cB.getGrandChildren().size());
            } else {
                Assert.fail("unexpected value");
            }

        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testExplicitJoinAfterFetchJoins() {
        Session s = openSession();
        s.getTransaction().begin();
        Parent p = ((Parent) (s.createQuery("select p from Parent p inner join fetch p.children c inner join fetch c.grandChildren inner join p.children cRestrict where cRestrict.value = 'c1'").uniqueResult()));
        Assert.assertEquals("p", p.getValue());
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(2, p.getChildren().size());
        Iterator<Child> iterator = p.getChildren().iterator();
        Child cA = iterator.next();
        Assert.assertTrue(Hibernate.isInitialized(cA.getGrandChildren()));
        if (cA.getValue().equals("c1")) {
            Assert.assertEquals(2, cA.getGrandChildren().size());
            Child cB = iterator.next();
            Assert.assertTrue(Hibernate.isInitialized(cB.getGrandChildren()));
            Assert.assertEquals(3, cB.getGrandChildren().size());
        } else
            if (cA.getValue().equals("c2")) {
                Assert.assertEquals(3, cA.getGrandChildren().size());
                Child cB = iterator.next();
                Assert.assertTrue(Hibernate.isInitialized(cB.getGrandChildren()));
                Assert.assertEquals(2, cB.getGrandChildren().size());
            } else {
                Assert.fail("unexpected value");
            }

        s.getTransaction().commit();
        s.close();
    }
}

