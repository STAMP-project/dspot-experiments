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
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-13050")
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class JtaWithStatementsBatchTest extends AbstractJtaBatchTest {
    private static JtaWithStatementsBatchTest.TestBatch testBatch;

    @Test
    public void testUnableToReleaseStatementMessageIsNotLogged() throws Exception {
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
            em.persist(comment);
            transactionManager.commit();
            assertStatementsListIsCleared();
            assertAllStatementsAreClosed(JtaWithStatementsBatchTest.testBatch.createtdStatements);
        } finally {
            if ((transactionManager.getStatus()) == (Status.STATUS_ACTIVE)) {
                transactionManager.rollback();
            }
            em.close();
        }
        Assert.assertFalse("HHH000352: Unable to release batch statement... has been thrown", triggerable.wasTriggered());
        em = createEntityManager();
        try {
            transactionManager.begin();
            Integer savedComments = em.createQuery("from Comment").getResultList().size();
            Assert.assertThat(savedComments, Is.is(1));
            Integer savedEventLogs = em.createQuery("from EventLog").getResultList().size();
            Assert.assertThat(savedEventLogs, Is.is(2));
        } finally {
            if ((transactionManager.getStatus()) == (Status.STATUS_ACTIVE)) {
                transactionManager.rollback();
            }
            em.close();
        }
    }

    public static class TestBatch extends BatchingBatch {
        private int numberOfStatementsAfterReleasing;

        private List<PreparedStatement> createtdStatements = new ArrayList<>();

        public TestBatch(BatchKey key, JdbcCoordinator jdbcCoordinator, int batchSize) {
            super(key, jdbcCoordinator, batchSize);
        }

        protected void releaseStatements() {
            createtdStatements.addAll(getStatements().values());
            super.releaseStatements();
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
            JtaWithStatementsBatchTest.testBatch = new JtaWithStatementsBatchTest.TestBatch(key, jdbcCoordinator, jdbcBatchSize);
            return JtaWithStatementsBatchTest.testBatch;
        }
    }
}

