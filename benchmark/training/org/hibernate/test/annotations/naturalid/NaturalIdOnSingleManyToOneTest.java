/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.naturalid;


import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for NaturalId annotation. See ANN-750.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
@SuppressWarnings("unchecked")
@TestForIssue(jiraKey = "ANN-750")
public class NaturalIdOnSingleManyToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMappingProperties() {
        log.warn("Commented out test");
        ClassMetadata metaData = sessionFactory().getClassMetadata(NaturalIdOnManyToOne.class);
        Assert.assertTrue("Class should have a natural key", metaData.hasNaturalIdentifier());
        int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
        Assert.assertTrue("Wrong number of elements", ((propertiesIndex.length) == 1));
    }

    @Test
    public void testManyToOneNaturalIdCached() {
        NaturalIdOnManyToOne singleManyToOne = new NaturalIdOnManyToOne();
        Citizen c1 = new Citizen();
        c1.setFirstname("Emmanuel");
        c1.setLastname("Bernard");
        c1.setSsn("1234");
        State france = new State();
        france.setName("Ile de France");
        c1.setState(france);
        singleManyToOne.setCitizen(c1);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(france);
        s.persist(c1);
        s.persist(singleManyToOne);
        tx.commit();
        s.close();
        s.getSessionFactory().getCache().evictNaturalIdRegions();
        Statistics stats = sessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        Assert.assertEquals("NaturalId cache puts should be zero", 0, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId cache hits should be zero", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId cache misses should be zero", 0, stats.getNaturalIdCacheMissCount());
        s = openSession();
        tx = s.beginTransaction();
        Criteria criteria = s.createCriteria(NaturalIdOnManyToOne.class);
        criteria.add(Restrictions.naturalId().set("citizen", c1));
        criteria.setCacheable(true);
        // first query
        List results = criteria.list();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 2, stats.getNaturalIdCachePutCount());// one for Citizen, one for NaturalIdOnManyToOne

        Assert.assertEquals("NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount());
        // query a second time - result should be in session cache
        criteria.list();
        Assert.assertEquals("NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount());
        Assert.assertEquals("NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount());
        Assert.assertEquals("NaturalId Cache Puts", 2, stats.getNaturalIdCachePutCount());
        Assert.assertEquals("NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount());
        // cleanup
        tx.rollback();
        s.close();
    }
}

