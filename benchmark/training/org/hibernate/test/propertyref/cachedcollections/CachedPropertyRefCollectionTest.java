/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertyref.cachedcollections;


import FetchMode.JOIN;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Set of tests originally developed to verify and fix HHH-5853
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-5853")
public class CachedPropertyRefCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testRetrievalOfCachedCollectionWithPropertyRefKey() {
        // create the test data...
        Session session = openSession();
        session.beginTransaction();
        ManagedObject mo = new ManagedObject("test", "test");
        mo.getMembers().add("members");
        session.save(mo);
        session.getTransaction().commit();
        session.close();
        // First attempt to load it via PK lookup
        session = openSession();
        session.beginTransaction();
        ManagedObject obj = ((ManagedObject) (session.get(ManagedObject.class, 1L)));
        Assert.assertNotNull(obj);
        Assert.assertTrue(Hibernate.isInitialized(obj));
        obj.getMembers().size();
        Assert.assertTrue(Hibernate.isInitialized(obj.getMembers()));
        session.getTransaction().commit();
        session.close();
        // Now try to access it via natural key
        session = openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ManagedObject.class).add(Restrictions.naturalId().set("name", "test")).setCacheable(true).setFetchMode("members", JOIN);
        obj = ((ManagedObject) (criteria.uniqueResult()));
        Assert.assertNotNull(obj);
        Assert.assertTrue(Hibernate.isInitialized(obj));
        obj.getMembers().size();
        Assert.assertTrue(Hibernate.isInitialized(obj.getMembers()));
        session.getTransaction().commit();
        session.close();
        // Clean up
        session = openSession();
        session.beginTransaction();
        session.delete(obj);
        session.getTransaction().commit();
        session.close();
    }
}

