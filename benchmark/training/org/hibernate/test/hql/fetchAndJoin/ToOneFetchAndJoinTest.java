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
public class ToOneFetchAndJoinTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testFetchJoinsWithImplicitJoinInRestriction() {
        Session s = openSession();
        s.getTransaction().begin();
        Entity1 e1Queryied = ((Entity1) (s.createQuery("select e1 from Entity1 e1 inner join fetch e1.entity2 e2 inner join fetch e2.entity3 where e1.entity2.value = 'entity2'").uniqueResult()));
        Assert.assertEquals("entity1", e1Queryied.getValue());
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2()));
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2().getEntity3()));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testExplicitJoinBeforeFetchJoins() {
        Session s = openSession();
        s.getTransaction().begin();
        Entity1 e1Queryied = ((Entity1) (s.createQuery("select e1 from Entity1 e1 inner join e1.entity2 e1Restrict inner join fetch e1.entity2 e2 inner join fetch e2.entity3 where e1Restrict.value = 'entity2'").uniqueResult()));
        Assert.assertEquals("entity1", e1Queryied.getValue());
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2()));
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2().getEntity3()));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testExplicitJoinBetweenFetchJoins() {
        Session s = openSession();
        s.getTransaction().begin();
        Entity1 e1Queryied = ((Entity1) (s.createQuery("select e1 from Entity1 e1 inner join fetch e1.entity2 e2 inner join e1.entity2 e1Restrict inner join fetch e2.entity3 where e1Restrict.value = 'entity2'").uniqueResult()));
        Assert.assertEquals("entity1", e1Queryied.getValue());
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2()));
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2().getEntity3()));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9637")
    public void testExplicitJoinAfterFetchJoins() {
        Session s = openSession();
        s.getTransaction().begin();
        Entity1 e1Queryied = ((Entity1) (s.createQuery("select e1 from Entity1 e1 inner join fetch e1.entity2 e2 inner join fetch e2.entity3 inner join e1.entity2 e1Restrict where e1Restrict.value = 'entity2'").uniqueResult()));
        Assert.assertEquals("entity1", e1Queryied.getValue());
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2()));
        Assert.assertTrue(Hibernate.isInitialized(e1Queryied.getEntity2().getEntity3()));
        s.getTransaction().commit();
        s.close();
    }
}

