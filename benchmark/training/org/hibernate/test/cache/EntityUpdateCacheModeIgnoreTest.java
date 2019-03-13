/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import CacheMode.GET;
import CacheMode.IGNORE;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that updating an entity does not add an entity to the cache with CacheMode.IGNORE
 */
public class EntityUpdateCacheModeIgnoreTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH9739")
    public void testCachModeIgnore() {
        // Test that there is no interaction with cache except for invalidation when using CacheMode.IGNORE
        // From API Doc : CacheMode.IGNORE -> The session will never interact with the cache, except to invalidate cache items when updates occur.
        Session s;
        Transaction t;
        SessionFactory sessionFactory;
        Statistics statistics;
        // ----------------------------------------------------------------------------------------------
        // insert
        s = openSession();
        s.setCacheMode(IGNORE);
        sessionFactory = s.getSessionFactory();
        sessionFactory.getCache().evictAllRegions();
        statistics = sessionFactory.getStatistics();
        statistics.clear();
        t = s.beginTransaction();
        EntityUpdateCacheModeIgnoreTest.PurchaseOrder purchaseOrder = new EntityUpdateCacheModeIgnoreTest.PurchaseOrder(1L, 2L, 1000L);
        s.persist(purchaseOrder);
        t.commit();
        s.close();
        Assert.assertEquals(0L, statistics.getSecondLevelCacheHitCount());
        Assert.assertEquals(0L, statistics.getSecondLevelCacheMissCount());
        Assert.assertEquals(0L, statistics.getSecondLevelCachePutCount());
        Assert.assertFalse(sessionFactory.getCache().containsEntity(EntityUpdateCacheModeIgnoreTest.PurchaseOrder.class, 1L));
        // ----------------------------------------------------------------------------------------------
        // update
        s = openSession();
        s.setCacheMode(IGNORE);
        sessionFactory = s.getSessionFactory();
        sessionFactory.getCache().evictAllRegions();
        statistics = sessionFactory.getStatistics();
        statistics.clear();
        t = s.beginTransaction();
        EntityUpdateCacheModeIgnoreTest.PurchaseOrder result = ((EntityUpdateCacheModeIgnoreTest.PurchaseOrder) (s.get(EntityUpdateCacheModeIgnoreTest.PurchaseOrder.class, 1L)));
        result.setTotalAmount(2000L);
        t.commit();
        s.close();
        Assert.assertEquals(0, statistics.getSecondLevelCacheHitCount());
        Assert.assertEquals(0, statistics.getSecondLevelCacheMissCount());
        Assert.assertEquals(0, statistics.getSecondLevelCachePutCount());
        // the following fails because the cache contains a lock for that entity
        // assertFalse(sessionFactory.getCache().containsEntity(PurchaseOrder.class, 1L));
        // make sure the updated entity is not found in the cache
        s = openSession();
        s.setCacheMode(GET);
        sessionFactory = s.getSessionFactory();
        // sessionFactory.getCache().evictAllRegions();
        t = s.beginTransaction();
        result = s.get(EntityUpdateCacheModeIgnoreTest.PurchaseOrder.class, 1L);
        Assert.assertEquals(2000, result.getTotalAmount().longValue());
        t.commit();
        s.close();
        Assert.assertEquals(0, statistics.getSecondLevelCacheHitCount());
        Assert.assertEquals(1, statistics.getSecondLevelCacheMissCount());
        Assert.assertEquals(0, statistics.getSecondLevelCachePutCount());
    }

    @Entity
    @Table(name = "PurchaseOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class PurchaseOrder implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
        private Long purchaseOrderId;

        private Long customerId;

        private Long totalAmount;

        public PurchaseOrder() {
        }

        public PurchaseOrder(Long purchaseOrderId, Long customerId, Long totalAmount) {
            this.purchaseOrderId = purchaseOrderId;
            this.customerId = customerId;
            this.totalAmount = totalAmount;
        }

        public Long getPurchaseOrderId() {
            return purchaseOrderId;
        }

        public void setPurchaseOrderId(Long purchaseOrderId) {
            this.purchaseOrderId = purchaseOrderId;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public Long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}

