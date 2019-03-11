/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.naturalid;


import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for NaturalId annotation
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
@SuppressWarnings("unchecked")
public class NaturalIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMappingProperties() {
        ClassMetadata metaData = sessionFactory().getClassMetadata(Citizen.class);
        Assert.assertTrue("Class should have a natural key", metaData.hasNaturalIdentifier());
        int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
        Assert.assertTrue("Wrong number of elements", ((propertiesIndex.length) == 2));
    }

    @Test
    public void testNaturalIdCached() {
        saveSomeCitizens();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        State france = this.getState(s, "Ile de France");
        Criteria criteria = s.createCriteria(Citizen.class);
        criteria.add(Restrictions.naturalId().set("ssn", "1234").set("state", france));
        criteria.setCacheable(true);
        cleanupCache();
        Statistics stats = sessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        Assert.assertEquals("Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("Cache puts should be empty", 0, stats.getNaturalIdCachePutCount());
        // first query
        List results = criteria.list();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount());
        // query a second time - result should be cached in session
        criteria.list();
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
    }

    @Test
    public void testNaturalIdLoaderNotCached() {
        saveSomeCitizens();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        State france = this.getState(s, "Ile de France");
        final NaturalIdLoadAccess naturalIdLoader = s.byNaturalId(Citizen.class);
        naturalIdLoader.using("ssn", "1234").using("state", france);
        // NaturalId cache gets populated during entity loading, need to clear it out
        cleanupCache();
        Statistics stats = sessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount());
        // first query
        Citizen citizen = ((Citizen) (naturalIdLoader.load()));
        Assert.assertNotNull(citizen);
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
    }

    @Test
    public void testNaturalIdLoaderCached() {
        Statistics stats = sessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount());
        saveSomeCitizens();
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 2, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount());
        // Try NaturalIdLoadAccess after insert
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        State france = this.getState(s, "Ile de France");
        NaturalIdLoadAccess naturalIdLoader = s.byNaturalId(Citizen.class);
        naturalIdLoader.using("ssn", "1234").using("state", france);
        // Not clearing naturalId caches, should be warm from entity loading
        stats.clear();
        // first query
        Citizen citizen = ((Citizen) (naturalIdLoader.load()));
        Assert.assertNotNull(citizen);
        Assert.assertEquals("NaturalId Cache Hits", 1, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
        // Try NaturalIdLoadAccess
        s = openSession();
        tx = s.beginTransaction();
        cleanupCache();
        stats.setStatisticsEnabled(true);
        stats.clear();
        // first query
        citizen = ((Citizen) (s.get(Citizen.class, citizen.getId())));
        Assert.assertNotNull(citizen);
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
        // Try NaturalIdLoadAccess after load
        s = openSession();
        tx = s.beginTransaction();
        france = this.getState(s, "Ile de France");
        naturalIdLoader = s.byNaturalId(Citizen.class);
        naturalIdLoader.using("ssn", "1234").using("state", france);
        // Not clearing naturalId caches, should be warm from entity loading
        stats.setStatisticsEnabled(true);
        stats.clear();
        // first query
        citizen = ((Citizen) (naturalIdLoader.load()));
        Assert.assertNotNull(citizen);
        Assert.assertEquals("NaturalId Cache Hits", 1, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
    }

    @Test
    public void testNaturalIdUncached() {
        saveSomeCitizens();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        State france = this.getState(s, "Ile de France");
        Criteria criteria = s.createCriteria(Citizen.class);
        criteria.add(Restrictions.naturalId().set("ssn", "1234").set("state", france));
        criteria.setCacheable(false);
        cleanupCache();
        Statistics stats = sessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        Assert.assertEquals("Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount());
        // first query
        List results = criteria.list();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("Query execution count should be one", 1, stats.getNaturalIdQueryExecutionCount());
        // query a second time - result should be cached in session
        criteria.list();
        Assert.assertEquals("Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("Second query should not be a miss", 1, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("Query execution count should be one", 1, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
    }
}

