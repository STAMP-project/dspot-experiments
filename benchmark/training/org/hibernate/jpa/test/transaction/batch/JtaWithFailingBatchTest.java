/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.jpa.test.transaction.batch;


import DialectChecks.SupportsIdentityColumns;
import FlushModeType.AUTO;
import TestingJtaPlatformImpl.INSTANCE;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.TransactionManager;
import org.hamcrest.core.Is;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-13050")
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class JtaWithFailingBatchTest extends AbstractJtaBatchTest {
    private static JtaWithFailingBatchTest.TestBatch testBatch;

    @Test
    public void testAllStatementsAreClosedInCaseOfBatchExecutionFailure() throws Exception {
        TransactionManager transactionManager = INSTANCE.getTransactionManager();
        EntityManager em = createEntityManager();
        try {
            transactionManager.begin();
            em.setFlushMode(AUTO);
            // Persist entity with non-generated id
            AbstractJtaBatchTest.EventLog eventLog1 = new AbstractJtaBatchTest.EventLog();
            eventLog1.setMessage("Foo1");
            em.persist(eventLog1);
            // Persist entity with non-generated id
            AbstractJtaBatchTest.EventLog eventLog2 = new AbstractJtaBatchTest.EventLog();
            eventLog2.setMessage("Foo2");
            em.persist(eventLog2);
            AbstractJtaBatchTest.Comment comment = new AbstractJtaBatchTest.Comment();
            comment.setMessage("Bar");
            try {
                em.persist(comment);
                transactionManager.commit();
            } catch (Exception expected) {
                // expected
                if ((transactionManager.getStatus()) == (Status.STATUS_ACTIVE)) {
                    transactionManager.rollback();
                }
            }
            Assert.assertThat("AbstractBatchImpl#releaseStatements() has not been callled", JtaWithFailingBatchTest.testBatch.calledReleaseStatements, Is.is(true));
            assertAllStatementsAreClosed(JtaWithFailingBatchTest.testBatch.createdStatements);
            assertStatementsListIsCleared();
        } finally {
            em.close();
        }
        Assert.assertFalse("HHH000352: Unable to release batch statement... has been thrown", triggerable.wasTriggered());
    }

    public static class TestBatch extends BatchingBatch {
        private int numberOfStatementsAfterReleasing;

        private List<PreparedStatement> createdStatements = new ArrayList<>();

        private boolean calledReleaseStatements;

        private String currentStatementSql;

        public TestBatch(BatchKey key, JdbcCoordinator jdbcCoordinator, int batchSize) {
            super(key, jdbcCoordinator, batchSize);
        }

        @Override
        public PreparedStatement getBatchStatement(String sql, boolean callable) {
            currentStatementSql = sql;
            PreparedStatement batchStatement = super.getBatchStatement(sql, callable);
            createdStatements.add(batchStatement);
            return batchStatement;
        }

        @Override
        public void addToBatch() {
            // Implementations really should call abortBatch() before throwing an exception.
            // Purposely skipping the call to abortBatch() to ensure that Hibernate works properly when
            // a legacy implementation does not call abortBatch().
            throw sqlExceptionHelper().convert(new SQLException("fake SQLException"), "could not perform addBatch", currentStatementSql);
        }

        @Override
        protected void releaseStatements() {
            super.releaseStatements();
            calledReleaseStatements = true;
            numberOfStatementsAfterReleasing += getStatements().size();
        }
    }

    public static class TestBatchBuilder extends BatchBuilderImpl {
        private int jdbcBatchSize;

        @Override
        public void setJdbcBatchSize(int jdbcBatchSize) {
            this.jdbcBatchSize = jdbcBatchSize;
        }

        @Override
        public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
            return buildBatchTest(key, jdbcCoordinator, jdbcBatchSize);
        }

        protected BatchingBatch buildBatchTest(BatchKey key, JdbcCoordinator jdbcCoordinator, int jdbcBatchSize) {
            JtaWithFailingBatchTest.testBatch = new JtaWithFailingBatchTest.TestBatch(key, jdbcCoordinator, jdbcBatchSize);
            return JtaWithFailingBatchTest.testBatch;
        }
    }
}

