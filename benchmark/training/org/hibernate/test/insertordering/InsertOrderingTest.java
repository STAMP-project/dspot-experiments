/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class InsertOrderingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBatchOrdering() {
        Session s = openSession();
        s.beginTransaction();
        int iterations = 12;
        for (int i = 0; i < iterations; i++) {
            User user = new User(("user-" + i));
            Group group = new Group(("group-" + i));
            s.save(user);
            s.save(group);
            user.addMembership(group);
        }
        InsertOrderingTest.StatsBatch.reset();
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(3, InsertOrderingTest.StatsBatch.batchSizes.size());
        s = openSession();
        s.beginTransaction();
        Iterator users = s.createQuery("from User u left join fetch u.memberships m left join fetch m.group").list().iterator();
        while (users.hasNext()) {
            s.delete(users.next());
        } 
        s.getTransaction().commit();
        s.close();
    }

    public static class Counter {
        public int count = 0;
    }

    public static class StatsBatch extends BatchingBatch {
        private static String batchSQL;

        private static List batchSizes = new ArrayList();

        private static int currentBatch = -1;

        public StatsBatch(BatchKey key, JdbcCoordinator jdbcCoordinator, int jdbcBatchSize) {
            super(key, jdbcCoordinator, jdbcBatchSize);
        }

        static void reset() {
            InsertOrderingTest.StatsBatch.batchSizes = new ArrayList();
            InsertOrderingTest.StatsBatch.currentBatch = -1;
            InsertOrderingTest.StatsBatch.batchSQL = null;
        }

        @Override
        public PreparedStatement getBatchStatement(String sql, boolean callable) {
            if (((InsertOrderingTest.StatsBatch.batchSQL) == null) || (!(InsertOrderingTest.StatsBatch.batchSQL.equals(sql)))) {
                (InsertOrderingTest.StatsBatch.currentBatch)++;
                InsertOrderingTest.StatsBatch.batchSQL = sql;
                InsertOrderingTest.StatsBatch.batchSizes.add(InsertOrderingTest.StatsBatch.currentBatch, new InsertOrderingTest.Counter());
            }
            return super.getBatchStatement(sql, callable);
        }

        @Override
        public void addToBatch() {
            InsertOrderingTest.Counter counter = ((InsertOrderingTest.Counter) (InsertOrderingTest.StatsBatch.batchSizes.get(InsertOrderingTest.StatsBatch.currentBatch)));
            (counter.count)++;
            super.addToBatch();
        }
    }

    public static class StatsBatchBuilder extends BatchBuilderImpl {
        private int jdbcBatchSize;

        @Override
        public void setJdbcBatchSize(int jdbcBatchSize) {
            this.jdbcBatchSize = jdbcBatchSize;
        }

        @Override
        public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
            return new InsertOrderingTest.StatsBatch(key, jdbcCoordinator, jdbcBatchSize);
        }
    }
}

