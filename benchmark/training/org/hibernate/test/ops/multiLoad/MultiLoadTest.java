/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops.multiLoad;


import CacheMode.IGNORE;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.BatchSize;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class MultiLoadTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testBasicMultiLoad() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            sqlStatementInterceptor.getSqlQueries().clear();
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> list = session.byMultipleIds(.class).multiLoad(ids(5));
            assertEquals(5, list.size());
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?,?,?,?)"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10984")
    public void testUnflushedDeleteAndThenMultiLoad() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // delete one of them (but do not flush)...
            session.delete(session.load(.class, 5));
            // as a baseline, assert based on how load() handles it
            org.hibernate.test.ops.multiLoad.SimpleEntity s5 = session.load(.class, 5);
            assertNotNull(s5);
            // and then, assert how get() handles it
            s5 = session.get(.class, 5);
            assertNull(s5);
            // finally assert how multiLoad handles it
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> list = session.byMultipleIds(.class).multiLoad(ids(56));
            assertEquals(56, list.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10617")
    public void testDuplicatedRequestedIds() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // ordered multiLoad
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> list = session.byMultipleIds(.class).multiLoad(1, 2, 3, 2, 2);
            assertEquals(5, list.size());
            assertSame(list.get(1), list.get(3));
            assertSame(list.get(1), list.get(4));
            // un-ordered multiLoad
            list = session.byMultipleIds(.class).enableOrderedReturn(false).multiLoad(1, 2, 3, 2, 2);
            assertEquals(3, list.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10617")
    public void testNonExistentIdRequest() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // ordered multiLoad
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> list = session.byMultipleIds(.class).multiLoad(1, 699, 2);
            assertEquals(3, list.size());
            assertNull(list.get(1));
            // un-ordered multiLoad
            list = session.byMultipleIds(.class).enableOrderedReturn(false).multiLoad(1, 699, 2);
            assertEquals(2, list.size());
        });
    }

    @Test
    public void testBasicMultiLoadWithManagedAndNoChecking() {
        Session session = openSession();
        session.getTransaction().begin();
        MultiLoadTest.SimpleEntity first = session.byId(MultiLoadTest.SimpleEntity.class).load(1);
        java.util.List<MultiLoadTest.SimpleEntity> list = session.byMultipleIds(MultiLoadTest.SimpleEntity.class).multiLoad(ids(56));
        Assert.assertEquals(56, list.size());
        // this check is HIGHLY specific to implementation in the batch loader
        // which puts existing managed entities first...
        Assert.assertSame(first, list.get(0));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testBasicMultiLoadWithManagedAndChecking() {
        Session session = openSession();
        session.getTransaction().begin();
        MultiLoadTest.SimpleEntity first = session.byId(MultiLoadTest.SimpleEntity.class).load(1);
        java.util.List<MultiLoadTest.SimpleEntity> list = session.byMultipleIds(MultiLoadTest.SimpleEntity.class).enableSessionCheck(true).multiLoad(ids(56));
        Assert.assertEquals(56, list.size());
        // this check is HIGHLY specific to implementation in the batch loader
        // which puts existing managed entities first...
        Assert.assertSame(first, list.get(0));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12944")
    public void testMultiLoadFrom2ndLevelCache() {
        Statistics statistics = sessionFactory().getStatistics();
        sessionFactory().getCache().evictAll();
        statistics.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Load 1 of the items directly
            org.hibernate.test.ops.multiLoad.SimpleEntity entity = session.get(.class, 2);
            assertNotNull(entity);
            assertEquals(1, statistics.getSecondLevelCacheMissCount());
            assertEquals(0, statistics.getSecondLevelCacheHitCount());
            assertEquals(1, statistics.getSecondLevelCachePutCount());
            assertTrue(session.getSessionFactory().getCache().containsEntity(.class, 2));
        });
        statistics.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Validate that the entity is still in the Level 2 cache
            assertTrue(session.getSessionFactory().getCache().containsEntity(.class, 2));
            sqlStatementInterceptor.getSqlQueries().clear();
            // Multiload 3 items and ensure that multiload pulls 2 from the database & 1 from the cache.
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> entities = session.byMultipleIds(.class).with(CacheMode.NORMAL).enableSessionCheck(true).multiLoad(ids(3));
            assertEquals(3, entities.size());
            assertEquals(1, statistics.getSecondLevelCacheHitCount());
            for (org.hibernate.test.ops.multiLoad.SimpleEntity entity : entities) {
                assertTrue(session.contains(entity));
            }
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?)"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12944")
    public void testUnorderedMultiLoadFrom2ndLevelCache() {
        Statistics statistics = sessionFactory().getStatistics();
        sessionFactory().getCache().evictAll();
        statistics.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Load 1 of the items directly
            org.hibernate.test.ops.multiLoad.SimpleEntity entity = session.get(.class, 2);
            assertNotNull(entity);
            assertEquals(1, statistics.getSecondLevelCacheMissCount());
            assertEquals(0, statistics.getSecondLevelCacheHitCount());
            assertEquals(1, statistics.getSecondLevelCachePutCount());
            assertTrue(session.getSessionFactory().getCache().containsEntity(.class, 2));
        });
        statistics.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Validate that the entity is still in the Level 2 cache
            assertTrue(session.getSessionFactory().getCache().containsEntity(.class, 2));
            sqlStatementInterceptor.getSqlQueries().clear();
            // Multiload 3 items and ensure that multiload pulls 2 from the database & 1 from the cache.
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> entities = session.byMultipleIds(.class).with(CacheMode.NORMAL).enableSessionCheck(true).enableOrderedReturn(false).multiLoad(ids(3));
            assertEquals(3, entities.size());
            assertEquals(1, statistics.getSecondLevelCacheHitCount());
            for (org.hibernate.test.ops.multiLoad.SimpleEntity entity : entities) {
                assertTrue(session.contains(entity));
            }
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?)"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12944")
    public void testOrderedMultiLoadFrom2ndLevelCachePendingDelete() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.remove(session.find(.class, 2));
            sqlStatementInterceptor.getSqlQueries().clear();
            // Multiload 3 items and ensure that multiload pulls 2 from the database & 1 from the cache.
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> entities = session.byMultipleIds(.class).with(CacheMode.NORMAL).enableSessionCheck(true).enableOrderedReturn(true).multiLoad(ids(3));
            assertEquals(3, entities.size());
            assertNull(entities.get(1));
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?)"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12944")
    public void testOrderedMultiLoadFrom2ndLevelCachePendingDeleteReturnRemoved() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.remove(session.find(.class, 2));
            sqlStatementInterceptor.getSqlQueries().clear();
            // Multiload 3 items and ensure that multiload pulls 2 from the database & 1 from the cache.
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> entities = session.byMultipleIds(.class).with(CacheMode.NORMAL).enableSessionCheck(true).enableOrderedReturn(true).enableReturnOfDeletedEntities(true).multiLoad(ids(3));
            assertEquals(3, entities.size());
            org.hibernate.test.ops.multiLoad.SimpleEntity deletedEntity = entities.get(1);
            assertNotNull(deletedEntity);
            final EntityEntry entry = getPersistenceContext().getEntry(deletedEntity);
            assertTrue((((entry.getStatus()) == Status.DELETED) || ((entry.getStatus()) == Status.GONE)));
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?)"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12944")
    public void testUnorderedMultiLoadFrom2ndLevelCachePendingDelete() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.remove(session.find(.class, 2));
            sqlStatementInterceptor.getSqlQueries().clear();
            // Multiload 3 items and ensure that multiload pulls 2 from the database & 1 from the cache.
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> entities = session.byMultipleIds(.class).with(CacheMode.NORMAL).enableSessionCheck(true).enableOrderedReturn(false).multiLoad(ids(3));
            assertEquals(3, entities.size());
            assertTrue(entities.stream().anyMatch(Objects::isNull));
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?)"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12944")
    public void testUnorderedMultiLoadFrom2ndLevelCachePendingDeleteReturnRemoved() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.remove(session.find(.class, 2));
            sqlStatementInterceptor.getSqlQueries().clear();
            // Multiload 3 items and ensure that multiload pulls 2 from the database & 1 from the cache.
            List<org.hibernate.test.ops.multiLoad.SimpleEntity> entities = session.byMultipleIds(.class).with(CacheMode.NORMAL).enableSessionCheck(true).enableOrderedReturn(false).enableReturnOfDeletedEntities(true).multiLoad(ids(3));
            assertEquals(3, entities.size());
            org.hibernate.test.ops.multiLoad.SimpleEntity deletedEntity = entities.stream().filter(( simpleEntity) -> simpleEntity.getId().equals(2)).findAny().orElse(null);
            assertNotNull(deletedEntity);
            final EntityEntry entry = getPersistenceContext().getEntry(deletedEntity);
            assertTrue((((entry.getStatus()) == Status.DELETED) || ((entry.getStatus()) == Status.GONE)));
            assertTrue(sqlStatementInterceptor.getSqlQueries().getFirst().endsWith("id in (?,?)"));
        });
    }

    @Test
    public void testMultiLoadWithCacheModeIgnore() {
        // do the multi-load, telling Hibernate to IGNORE the L2 cache -
        // the end result should be that the cache is (still) empty afterwards
        Session session = openSession();
        session.getTransaction().begin();
        java.util.List<MultiLoadTest.SimpleEntity> list = session.byMultipleIds(MultiLoadTest.SimpleEntity.class).with(IGNORE).multiLoad(ids(56));
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(56, list.size());
        for (MultiLoadTest.SimpleEntity entity : list) {
            Assert.assertFalse(sessionFactory().getCache().containsEntity(MultiLoadTest.SimpleEntity.class, entity.getId()));
        }
    }

    @Test
    public void testMultiLoadClearsBatchFetchQueue() {
        final EntityKey entityKey = new EntityKey(1, sessionFactory().getEntityPersister(MultiLoadTest.SimpleEntity.class.getName()));
        Session session = openSession();
        session.getTransaction().begin();
        // create a proxy, which should add an entry to the BatchFetchQueue
        MultiLoadTest.SimpleEntity first = session.byId(MultiLoadTest.SimpleEntity.class).getReference(1);
        Assert.assertTrue(getPersistenceContext().getBatchFetchQueue().containsEntityKey(entityKey));
        // now bulk load, which should clean up the BatchFetchQueue entry
        java.util.List<MultiLoadTest.SimpleEntity> list = session.byMultipleIds(MultiLoadTest.SimpleEntity.class).enableSessionCheck(true).multiLoad(ids(56));
        Assert.assertEquals(56, list.size());
        Assert.assertFalse(getPersistenceContext().getBatchFetchQueue().containsEntityKey(entityKey));
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "SimpleEntity")
    @Table(name = "SimpleEntity")
    @Cacheable
    @BatchSize(size = 15)
    public static class SimpleEntity {
        Integer id;

        String text;

        public SimpleEntity() {
        }

        public SimpleEntity(Integer id, String text) {
            this.id = id;
            this.text = text;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

