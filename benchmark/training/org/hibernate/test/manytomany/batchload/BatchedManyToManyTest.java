/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.manytomany.batchload;


import java.util.List;
import java.util.Locale;
import junit.framework.Assert;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Hibernate;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Tests loading of many-to-many collection which should trigger
 * a batch load.
 *
 * @author Steve Ebersole
 */
public class BatchedManyToManyTest extends BaseCoreFunctionalTestCase {
    public static class TestingBatchBuilder extends BatchBuilderImpl {
        @Override
        public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
            return new BatchedManyToManyTest.TestingBatch(key, jdbcCoordinator);
        }
    }

    public static class TestingBatch extends NonBatchingBatch {
        public TestingBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
            super(key, jdbcCoordinator);
        }
    }

    @Test
    public void testLoadingNonInverseSide() {
        prepareTestData();
        sessionFactory().getStatistics().clear();
        CollectionStatistics userGroupStats = sessionFactory().getStatistics().getCollectionStatistics(((User.class.getName()) + ".groups"));
        CollectionStatistics groupUserStats = sessionFactory().getStatistics().getCollectionStatistics(((Group.class.getName()) + ".users"));
        Interceptor testingInterceptor = new EmptyInterceptor() {
            @Override
            public String onPrepareStatement(String sql) {
                // ugh, this is the best way I could come up with to assert this.
                // unfortunately, this is highly dependent on the dialect and its
                // outer join fragment.  But at least this wil fail on the majority
                // of dialects...
                Assert.assertFalse("batch load of many-to-many should use inner join", sql.toLowerCase(Locale.ROOT).contains("left outer join"));
                return super.onPrepareStatement(sql);
            }
        };
        Session s = openSession(testingInterceptor);
        s.beginTransaction();
        List users = s.createQuery("from User u").list();
        User user = ((User) (users.get(0)));
        assertTrue(Hibernate.isInitialized(user));
        assertTrue(Hibernate.isInitialized(user.getGroups()));
        user = ((User) (users.get(1)));
        assertTrue(Hibernate.isInitialized(user));
        assertTrue(Hibernate.isInitialized(user.getGroups()));
        assertEquals(1, userGroupStats.getFetchCount());// should have been just one fetch (the batch fetch)

        assertEquals(1, groupUserStats.getFetchCount());// should have been just one fetch (the batch fetch)

        s.getTransaction().commit();
        s.close();
    }
}

