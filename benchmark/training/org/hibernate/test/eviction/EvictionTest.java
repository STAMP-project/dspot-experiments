/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.eviction;


import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class EvictionTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-7912")
    public void testNormalUsage() {
        Session session = openSession();
        session.beginTransaction();
        session.save(new IsolatedEvictableEntity(1));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        IsolatedEvictableEntity entity = ((IsolatedEvictableEntity) (session.get(IsolatedEvictableEntity.class, 1)));
        Assert.assertTrue(session.contains(entity));
        session.evict(entity);
        Assert.assertFalse(session.contains(entity));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(entity);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7912")
    public void testEvictingNull() {
        Session session = openSession();
        session.beginTransaction();
        try {
            session.evict(null);
            Assert.fail("Expecting evict(null) to throw NPE");
        } catch (NullPointerException expected) {
        }
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7912")
    public void testEvictingTransientEntity() {
        Session session = openSession();
        session.beginTransaction();
        session.evict(new IsolatedEvictableEntity(1));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7912")
    public void testEvictingDetachedEntity() {
        Session session = openSession();
        session.beginTransaction();
        session.save(new IsolatedEvictableEntity(1));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        IsolatedEvictableEntity entity = ((IsolatedEvictableEntity) (session.get(IsolatedEvictableEntity.class, 1)));
        Assert.assertTrue(session.contains(entity));
        // detach the entity
        session.evict(entity);
        Assert.assertFalse(session.contains(entity));
        // evict it again the entity
        session.evict(entity);
        Assert.assertFalse(session.contains(entity));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(entity);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7912")
    public void testEvictingNonEntity() {
        Session session = openSession();
        session.beginTransaction();
        try {
            session.evict(new EvictionTest());
            Assert.fail("Expecting evict(non-entity) to throw IAE");
        } catch (IllegalArgumentException expected) {
        }
        session.getTransaction().commit();
        session.close();
    }
}

