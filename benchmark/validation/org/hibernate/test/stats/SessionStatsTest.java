/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.stats;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class SessionStatsTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSessionStatistics() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Statistics stats = sessionFactory().getStatistics();
        stats.clear();
        boolean isStats = stats.isStatisticsEnabled();
        stats.setStatisticsEnabled(true);
        Continent europe = fillDb(s);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        SessionStatistics sessionStats = s.getStatistics();
        Assert.assertEquals(0, sessionStats.getEntityKeys().size());
        Assert.assertEquals(0, sessionStats.getEntityCount());
        Assert.assertEquals(0, sessionStats.getCollectionKeys().size());
        Assert.assertEquals(0, sessionStats.getCollectionCount());
        europe = ((Continent) (s.get(Continent.class, europe.getId())));
        Hibernate.initialize(europe.getCountries());
        Hibernate.initialize(europe.getCountries().iterator().next());
        Assert.assertEquals(2, sessionStats.getEntityKeys().size());
        Assert.assertEquals(2, sessionStats.getEntityCount());
        Assert.assertEquals(1, sessionStats.getCollectionKeys().size());
        Assert.assertEquals(1, sessionStats.getCollectionCount());
        tx.commit();
        s.close();
        stats.setStatisticsEnabled(isStats);
    }
}

