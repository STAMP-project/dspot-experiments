/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.mutable;


import LockOptions.NONE;
import java.lang.reflect.Field;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class MutableNaturalIdTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10360")
    public void testNaturalIdNullability() {
        final EntityPersister persister = sessionFactory().getEntityPersister(User.class.getName());
        final EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
        // nullability is not specified, so it should be non-nullable by hbm-specific default
        Assert.assertFalse(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("name")]);
        Assert.assertFalse(persister.getPropertyNullability()[entityMetamodel.getPropertyIndex("org")]);
    }

    @Test
    public void testCacheSynchronizationOnMutation() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("gavin", "hb", "secret");
        s.persist(u);
        t.commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        u = ((User) (s.byId(User.class).getReference(u.getId())));
        u.setOrg("ceylon");
        User oldNaturalId = ((User) (s.byNaturalId(User.class).using("name", "gavin").using("org", "hb").load()));
        Assert.assertNull(oldNaturalId);
        Assert.assertNotSame(u, oldNaturalId);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReattachmentNaturalIdCheck() throws Throwable {
        Session s = openSession();
        s.beginTransaction();
        User u = new User("gavin", "hb", "secret");
        s.persist(u);
        s.getTransaction().commit();
        s.close();
        Field name = u.getClass().getDeclaredField("name");
        name.setAccessible(true);
        name.set(u, "Gavin");
        s = openSession();
        s.beginTransaction();
        try {
            s.update(u);
            Assert.assertNotNull(s.byNaturalId(User.class).using("name", "Gavin").using("org", "hb").load());
            s.getTransaction().commit();
        } catch (HibernateException expected) {
            s.getTransaction().rollback();
        } catch (Throwable t) {
            try {
                s.getTransaction().rollback();
            } catch (Throwable ignore) {
            }
            throw t;
        } finally {
            s.close();
        }
        s = openSession();
        s.beginTransaction();
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReattachmentUnmodifiedNaturalIdCheck() throws Throwable {
        Session s = openSession();
        s.beginTransaction();
        User u = new User("gavin", "hb", "secret");
        s.persist(u);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        try {
            s.buildLockRequest(NONE).lock(u);
            Field name = u.getClass().getDeclaredField("name");
            name.setAccessible(true);
            name.set(u, "Gavin");
            Assert.assertNotNull(s.byNaturalId(User.class).using("name", "Gavin").using("org", "hb").load());
            s.getTransaction().commit();
        } catch (HibernateException expected) {
            s.getTransaction().rollback();
        } catch (Throwable t) {
            try {
                s.getTransaction().rollback();
            } catch (Throwable ignore) {
            }
            throw t;
        } finally {
            s.close();
        }
        s = openSession();
        s.beginTransaction();
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNonexistentNaturalIdCache() {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Object nullUser = s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult();
        Assert.assertNull(nullUser);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount(), 1);
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount(), 0);
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCachePutCount(), 0);
        s = openSession();
        t = s.beginTransaction();
        User u = new User("gavin", "hb", "secret");
        s.persist(u);
        t.commit();
        s.close();
        sessionFactory().getStatistics().clear();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCachePutCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        sessionFactory().getStatistics().clear();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult()));
        s.delete(u);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());// 0: incorrect stats since hbm.xml can't enable NaturalId caching

        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        sessionFactory().getStatistics().clear();
        s = openSession();
        t = s.beginTransaction();
        nullUser = s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult();
        Assert.assertNull(nullUser);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCachePutCount());
    }

    @Test
    public void testNaturalIdCache() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("gavin", "hb", "secret");
        s.persist(u);
        t.commit();
        s.close();
        sessionFactory().getStatistics().clear();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCachePutCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        s = openSession();
        t = s.beginTransaction();
        User v = new User("xam", "hb", "foobar");
        s.persist(v);
        t.commit();
        s.close();
        sessionFactory().getStatistics().clear();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "gavin").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete User").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    public void testNaturalIdDeleteUsingCache() {
        Session s = openSession();
        s.beginTransaction();
        User u = new User("steve", "hb", "superSecret");
        s.persist(u);
        s.getTransaction().commit();
        s.close();
        sessionFactory().getStatistics().clear();
        s = openSession();
        s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "steve").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCachePutCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        sessionFactory().getStatistics().clear();
        s = openSession();
        s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "steve").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());// 0: incorrect stats since hbm.xml can't enable NaturalId caching

        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        s.delete(u);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "steve").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNull(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNaturalIdRecreateUsingCache() {
        testNaturalIdDeleteUsingCache();
        Session s = openSession();
        s.beginTransaction();
        User u = new User("steve", "hb", "superSecret");
        s.persist(u);
        s.getTransaction().commit();
        s.close();
        sessionFactory().getStatistics().clear();
        s = openSession();
        s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "steve").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCachePutCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        sessionFactory().getStatistics().clear();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        u = ((User) (s.createCriteria(User.class).add(Restrictions.naturalId().set("name", "steve").set("org", "hb")).setCacheable(true).uniqueResult()));
        Assert.assertNotNull(u);
        Assert.assertEquals(1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount());// 0: incorrect stats since hbm.xml can't enable NaturalId caching

        Assert.assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());// 1: no stats since hbm.xml can't enable NaturalId caching

        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testQuerying() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("emmanuel", "hb", "bh");
        s.persist(u);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.createQuery("from User u where u.name = :name").setParameter("name", "emmanuel").uniqueResult()));
        Assert.assertEquals("emmanuel", u.getName());
        s.delete(u);
        t.commit();
        s.close();
    }

    @Test
    public void testClear() {
        Session s = openSession();
        s.beginTransaction();
        User u = new User("steve", "hb", "superSecret");
        s.persist(u);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        u = ((User) (session.byNaturalId(User.class).using("name", "steve").using("org", "hb").load()));
        Assert.assertNotNull(u);
        s.clear();
        u = ((User) (session.byNaturalId(User.class).using("name", "steve").using("org", "hb").load()));
        Assert.assertNotNull(u);
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testEviction() {
        Session s = openSession();
        s.beginTransaction();
        User u = new User("steve", "hb", "superSecret");
        s.persist(u);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        u = ((User) (session.byNaturalId(User.class).using("name", "steve").using("org", "hb").load()));
        Assert.assertNotNull(u);
        s.evict(u);
        u = ((User) (session.byNaturalId(User.class).using("name", "steve").using("org", "hb").load()));
        Assert.assertNotNull(u);
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }
}

