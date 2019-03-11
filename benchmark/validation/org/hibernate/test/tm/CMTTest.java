/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.tm;


import DialectChecks.DoesReadCommittedNotCauseWritersToBlockReadersCheck;
import TestingJtaPlatformImpl.INSTANCE;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.transaction.Transaction;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
@SkipForDialect(SQLServerDialect.class)
public class CMTTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testConcurrent() throws Exception {
        sessionFactory().getStatistics().clear();
        Assert.assertEquals(0, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getUpdateTimestampsCachePutCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getUpdateTimestampsCacheMissCount());
        Assert.assertNotNull(sessionFactory().getEntityPersister("Item").getCacheAccessStrategy());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityLoadCount());
        INSTANCE.getTransactionManager().begin();
        Session s = openSession();
        Map foo = new HashMap();
        foo.put("name", "Foo");
        foo.put("description", "a big foo");
        s.persist("Item", foo);
        Map bar = new HashMap();
        bar.put("name", "Bar");
        bar.put("description", "a small bar");
        s.persist("Item", bar);
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(0, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getUpdateTimestampsCachePutCount());// One preinvalidate & one invalidate

        Assert.assertEquals(0, sessionFactory().getStatistics().getUpdateTimestampsCacheMissCount());
        sessionFactory().getCache().evictEntityRegion("Item");
        INSTANCE.getTransactionManager().begin();
        Session s1 = openSession();
        foo = ((Map) (s1.get("Item", "Foo")));
        // foo.put("description", "a big red foo");
        // s1.flush();
        Transaction tx = INSTANCE.getTransactionManager().suspend();
        INSTANCE.getTransactionManager().begin();
        Session s2 = openSession();
        foo = ((Map) (s2.get("Item", "Foo")));
        INSTANCE.getTransactionManager().commit();
        INSTANCE.getTransactionManager().resume(tx);
        INSTANCE.getTransactionManager().commit();
        sessionFactory().getCache().evictEntityRegion("Item");
        INSTANCE.getTransactionManager().begin();
        s1 = openSession();
        s1.createCriteria("Item").list();
        // foo.put("description", "a big red foo");
        // s1.flush();
        tx = INSTANCE.getTransactionManager().suspend();
        INSTANCE.getTransactionManager().begin();
        s2 = openSession();
        s2.createCriteria("Item").list();
        INSTANCE.getTransactionManager().commit();
        INSTANCE.getTransactionManager().resume(tx);
        INSTANCE.getTransactionManager().commit();
        INSTANCE.getTransactionManager().begin();
        s2 = openSession();
        s2.createCriteria("Item").list();
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(7, sessionFactory().getStatistics().getEntityLoadCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryExecutionCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getQueryCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getQueryCacheMissCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getUpdateTimestampsCachePutCount());
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        s.createQuery("delete from Item").executeUpdate();
        INSTANCE.getTransactionManager().commit();
    }

    @Test
    public void testConcurrentCachedQueries() throws Exception {
        sessionFactory().getStatistics().clear();
        cleanupCache();
        INSTANCE.getTransactionManager().begin();
        Session s = openSession();
        Map foo = new HashMap();
        foo.put("name", "Foo");
        foo.put("description", "a big foo");
        s.persist("Item", foo);
        Map bar = new HashMap();
        bar.put("name", "Bar");
        bar.put("description", "a small bar");
        s.persist("Item", bar);
        INSTANCE.getTransactionManager().commit();
        synchronized(this) {
            wait(1000);
        }
        sessionFactory().getStatistics().clear();
        sessionFactory().getCache().evictEntityRegion("Item");
        INSTANCE.getTransactionManager().begin();
        Session s4 = openSession();
        Transaction tx4 = INSTANCE.getTransactionManager().suspend();
        INSTANCE.getTransactionManager().begin();
        Session s1 = openSession();
        List r1 = s1.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        Assert.assertEquals(r1.size(), 2);
        Transaction tx1 = INSTANCE.getTransactionManager().suspend();
        INSTANCE.getTransactionManager().begin();
        Session s2 = openSession();
        List r2 = s2.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        Assert.assertEquals(r2.size(), 2);
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(sessionFactory().getStatistics().getSecondLevelCacheHitCount(), 2);
        Assert.assertEquals(sessionFactory().getStatistics().getSecondLevelCacheMissCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityLoadCount(), 2);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityFetchCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryExecutionCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCachePutCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCacheHitCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCacheMissCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCachePutCount(), 0);
        INSTANCE.getTransactionManager().resume(tx1);
        INSTANCE.getTransactionManager().commit();
        INSTANCE.getTransactionManager().begin();
        Session s3 = openSession();
        s3.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(sessionFactory().getStatistics().getSecondLevelCacheHitCount(), 4);
        Assert.assertEquals(sessionFactory().getStatistics().getSecondLevelCacheMissCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityLoadCount(), 2);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityFetchCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryExecutionCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCachePutCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCacheHitCount(), 2);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCacheMissCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount(), 2);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCachePutCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCacheMissCount(), 0);
        INSTANCE.getTransactionManager().resume(tx4);
        List r4 = s4.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        Assert.assertEquals(r4.size(), 2);
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(sessionFactory().getStatistics().getSecondLevelCacheHitCount(), 6);
        Assert.assertEquals(sessionFactory().getStatistics().getSecondLevelCacheMissCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityLoadCount(), 2);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityFetchCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryExecutionCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCachePutCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCacheHitCount(), 3);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryCacheMissCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount(), 3);
        Assert.assertEquals(sessionFactory().getStatistics().getUpdateTimestampsCachePutCount(), 0);
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        s.createQuery("delete from Item").executeUpdate();
        INSTANCE.getTransactionManager().commit();
    }

    @Test
    @RequiresDialectFeature(value = DoesReadCommittedNotCauseWritersToBlockReadersCheck.class, comment = "write locks block readers")
    public void testConcurrentCachedDirtyQueries() throws Exception {
        INSTANCE.getTransactionManager().begin();
        Session s = openSession();
        Map foo = new HashMap();
        foo.put("name", "Foo");
        foo.put("description", "a big foo");
        s.persist("Item", foo);
        Map bar = new HashMap();
        bar.put("name", "Bar");
        bar.put("description", "a small bar");
        s.persist("Item", bar);
        INSTANCE.getTransactionManager().commit();
        synchronized(this) {
            wait(1000);
        }
        sessionFactory().getStatistics().clear();
        cleanupCache();// we need a clean 2L cache here.

        // open a TX and suspend it
        INSTANCE.getTransactionManager().begin();
        Session s4 = openSession();
        Transaction tx4 = INSTANCE.getTransactionManager().suspend();
        // open a new TX and execute a query, this would fill the query cache.
        INSTANCE.getTransactionManager().begin();
        Session s1 = openSession();
        List r1 = s1.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        Assert.assertEquals(r1.size(), 2);
        foo = ((Map) (r1.get(0)));
        // update data and make query cache stale, but TX is suspended
        foo.put("description", "a big red foo");
        s1.flush();
        Transaction tx1 = INSTANCE.getTransactionManager().suspend();
        // open a new TX and run query again
        // this TX is committed after query
        INSTANCE.getTransactionManager().begin();
        Session s2 = openSession();
        List r2 = s2.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        Assert.assertEquals(r2.size(), 2);
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheMissCount());
        Assert.assertEquals(4, sessionFactory().getStatistics().getEntityLoadCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getQueryExecutionCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getQueryCachePutCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getQueryCacheHitCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getQueryCacheMissCount());
        // updateTimestampsCache put happens at two places
        // 1. {@link org.hibernate.engine.spi.ActionQueue#registerCleanupActions} calls preinvalidate
        // 2. {@link org.hibernate.engine.spi.ActionQueue.AfterTransactionCompletionProcessQueue#afterTransactionCompletion} calls invalidate
        // but since the TX which the update action happened is not committed yet, so there should be only 1 updateTimestamps put.
        Assert.assertEquals(1, sessionFactory().getStatistics().getUpdateTimestampsCachePutCount());
        // updateTimestampsCache hit only happens when the query cache data's timestamp is newer
        // than the timestamp of when update happens
        // since there is only 1 update action
        Assert.assertEquals(1, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        INSTANCE.getTransactionManager().resume(tx1);
        INSTANCE.getTransactionManager().commit();
        // update action's TX committed, so, invalidate is called, put new timestamp into UpdateTimestampsCache
        Assert.assertEquals(2, sessionFactory().getStatistics().getUpdateTimestampsCachePutCount());
        // but no more query cache lookup here, so it should still 1
        Assert.assertEquals(1, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        INSTANCE.getTransactionManager().begin();
        Session s3 = openSession();
        s3.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheMissCount());
        Assert.assertEquals(6, sessionFactory().getStatistics().getEntityLoadCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryExecutionCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryCachePutCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getQueryCacheHitCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryCacheMissCount());
        // a new query cache hit and one more update timestamps cache hit, so should be 2
        Assert.assertEquals(2, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        INSTANCE.getTransactionManager().resume(tx4);
        List r4 = s4.createCriteria("Item").addOrder(Order.asc("description")).setCacheable(true).list();
        Assert.assertEquals(r4.size(), 2);
        INSTANCE.getTransactionManager().commit();
        Assert.assertEquals(2, sessionFactory().getStatistics().getSecondLevelCacheHitCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheMissCount());
        Assert.assertEquals(6, sessionFactory().getStatistics().getEntityLoadCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryExecutionCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryCachePutCount());
        Assert.assertEquals(1, sessionFactory().getStatistics().getQueryCacheHitCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getQueryCacheMissCount());
        Assert.assertEquals(3, sessionFactory().getStatistics().getUpdateTimestampsCacheHitCount());
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        s.createQuery("delete from Item").executeUpdate();
        INSTANCE.getTransactionManager().commit();
    }

    @Test
    public void testCMT() throws Exception {
        sessionFactory().getStatistics().clear();
        INSTANCE.getTransactionManager().begin();
        Session s = openSession();
        INSTANCE.getTransactionManager().commit();
        Assert.assertFalse(s.isOpen());
        Assert.assertEquals(sessionFactory().getStatistics().getFlushCount(), 0);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityInsertCount(), 0);
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        INSTANCE.getTransactionManager().rollback();
        Assert.assertFalse(s.isOpen());
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        Map item = new HashMap();
        item.put("name", "The Item");
        item.put("description", "The only item we have");
        s.persist("Item", item);
        INSTANCE.getTransactionManager().commit();
        Assert.assertFalse(s.isOpen());
        Assert.assertEquals(sessionFactory().getStatistics().getFlushCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityInsertCount(), 1);
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        item = ((Map) (s.createQuery("from Item").uniqueResult()));
        Assert.assertNotNull(item);
        s.delete(item);
        INSTANCE.getTransactionManager().commit();
        Assert.assertFalse(s.isOpen());
        Assert.assertEquals(sessionFactory().getStatistics().getTransactionCount(), 4);
        Assert.assertEquals(sessionFactory().getStatistics().getSuccessfulTransactionCount(), 3);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityDeleteCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getEntityInsertCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getSessionOpenCount(), 4);
        Assert.assertEquals(sessionFactory().getStatistics().getSessionCloseCount(), 4);
        Assert.assertEquals(sessionFactory().getStatistics().getQueryExecutionCount(), 1);
        Assert.assertEquals(sessionFactory().getStatistics().getFlushCount(), 2);
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        s.createQuery("delete from Item").executeUpdate();
        INSTANCE.getTransactionManager().commit();
    }

    @Test
    public void testCurrentSession() throws Exception {
        INSTANCE.getTransactionManager().begin();
        Session s = sessionFactory().getCurrentSession();
        Session s2 = sessionFactory().getCurrentSession();
        Assert.assertSame(s, s2);
        INSTANCE.getTransactionManager().commit();
        Assert.assertFalse(s.isOpen());
        // TODO : would be nice to automate-test that the SF internal map actually gets cleaned up
        // i verified that is does currently in my debugger...
    }

    @Test
    public void testCurrentSessionWithIterate() throws Exception {
        INSTANCE.getTransactionManager().begin();
        Session s = openSession();
        Map item1 = new HashMap();
        item1.put("name", "Item - 1");
        item1.put("description", "The first item");
        s.persist("Item", item1);
        Map item2 = new HashMap();
        item2.put("name", "Item - 2");
        item2.put("description", "The second item");
        s.persist("Item", item2);
        INSTANCE.getTransactionManager().commit();
        // First, test iterating the partial iterator; iterate to past
        // the first, but not the second, item
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        Iterator itr = s.createQuery("from Item").iterate();
        if (!(itr.hasNext())) {
            Assert.fail("No results in iterator");
        }
        itr.next();
        if (!(itr.hasNext())) {
            Assert.fail("Only one result in iterator");
        }
        INSTANCE.getTransactionManager().commit();
        // Next, iterate the entire result
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        itr = s.createQuery("from Item").iterate();
        if (!(itr.hasNext())) {
            Assert.fail("No results in iterator");
        }
        while (itr.hasNext()) {
            itr.next();
        } 
        INSTANCE.getTransactionManager().commit();
        INSTANCE.getTransactionManager().begin();
        s = openSession();
        s.createQuery("delete from Item").executeUpdate();
        INSTANCE.getTransactionManager().commit();
    }

    @Test
    public void testCurrentSessionWithScroll() throws Exception {
        INSTANCE.getTransactionManager().begin();
        Session s = sessionFactory().getCurrentSession();
        Map item1 = new HashMap();
        item1.put("name", "Item - 1");
        item1.put("description", "The first item");
        s.persist("Item", item1);
        Map item2 = new HashMap();
        item2.put("name", "Item - 2");
        item2.put("description", "The second item");
        s.persist("Item", item2);
        INSTANCE.getTransactionManager().commit();
        // First, test partially scrolling the result with out closing
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        ScrollableResults results = s.createQuery("from Item").scroll();
        results.next();
        INSTANCE.getTransactionManager().commit();
        // Next, test partially scrolling the result with closing
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        results = s.createQuery("from Item").scroll();
        results.next();
        results.close();
        INSTANCE.getTransactionManager().commit();
        // Next, scroll the entire result (w/o closing)
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        results = s.createQuery("from Item").scroll();
        while (results.next()) {
            // do nothing
        } 
        INSTANCE.getTransactionManager().commit();
        // Next, scroll the entire result (closing)
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        results = s.createQuery("from Item").scroll();
        while (results.next()) {
            // do nothing
        } 
        results.close();
        INSTANCE.getTransactionManager().commit();
        INSTANCE.getTransactionManager().begin();
        s = sessionFactory().getCurrentSession();
        s.createQuery("delete from Item").executeUpdate();
        INSTANCE.getTransactionManager().commit();
    }
}

