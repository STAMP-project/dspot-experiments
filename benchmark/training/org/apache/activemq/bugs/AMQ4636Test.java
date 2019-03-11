/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import javax.jms.TransactionRolledBackException;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.jdbc.TransactionContext;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing how the broker reacts when a SQL Exception is thrown from
 * org.apache.activemq.store.jdbc.TransactionContext.executeBatch().
 * <p/>
 * see https://issues.apache.org/jira/browse/AMQ-4636
 */
public class AMQ4636Test {
    private static final String MY_TEST_TOPIC = "MY_TEST_TOPIC";

    private static final Logger LOG = LoggerFactory.getLogger(AMQ4636Test.class);

    private String transportUrl = "tcp://0.0.0.0:0";

    private BrokerService broker;

    EmbeddedDataSource embeddedDataSource;

    CountDownLatch throwSQLException = new CountDownLatch(0);

    /**
     * adding a TestTransactionContext (wrapper to TransactionContext) so an SQLException is triggered
     * during TransactionContext.executeBatch() when called in the broker.
     * <p/>
     * Expectation: SQLException triggers a connection shutdown and failover should kick and try to redeliver the
     * message. SQLException should NOT be returned to client
     */
    @Test
    public void testProducerWithDBShutdown() throws Exception {
        // failover but timeout in 1 seconds so the test does not hang
        String failoverTransportURL = ("failover:(" + (transportUrl)) + ")?timeout=1000";
        this.createDurableConsumer(AMQ4636Test.MY_TEST_TOPIC, failoverTransportURL);
        this.sendMessage(AMQ4636Test.MY_TEST_TOPIC, failoverTransportURL, false, false);
    }

    @Test
    public void testTransactedProducerCommitWithDBShutdown() throws Exception {
        // failover but timeout in 1 seconds so the test does not hang
        String failoverTransportURL = ("failover:(" + (transportUrl)) + ")?timeout=1000";
        this.createDurableConsumer(AMQ4636Test.MY_TEST_TOPIC, failoverTransportURL);
        try {
            this.sendMessage(AMQ4636Test.MY_TEST_TOPIC, failoverTransportURL, true, true);
            Assert.fail("Expect rollback after failover - inddoubt commit");
        } catch (javax.jms expectedInDoubt) {
            AMQ4636Test.LOG.info("Got rollback after failover failed commit", expectedInDoubt);
        }
    }

    @Test
    public void testTransactedProducerRollbackWithDBShutdown() throws Exception {
        // failover but timeout in 1 seconds so the test does not hang
        String failoverTransportURL = ("failover:(" + (transportUrl)) + ")?timeout=1000";
        this.createDurableConsumer(AMQ4636Test.MY_TEST_TOPIC, failoverTransportURL);
        this.sendMessage(AMQ4636Test.MY_TEST_TOPIC, failoverTransportURL, true, false);
    }

    /* Mock classes used for testing */
    public class TestJDBCPersistenceAdapter extends JDBCPersistenceAdapter {
        public TransactionContext getTransactionContext() throws IOException {
            return new AMQ4636Test.TestTransactionContext(this);
        }
    }

    public class TestTransactionContext extends TransactionContext {
        public TestTransactionContext(JDBCPersistenceAdapter jdbcPersistenceAdapter) throws IOException {
            super(jdbcPersistenceAdapter);
        }

        @Override
        public void executeBatch() throws SQLException {
            if ((throwSQLException.getCount()) > 0) {
                // only throw exception once
                throwSQLException.countDown();
                throw new SQLException("TEST SQL EXCEPTION");
            }
            super.executeBatch();
        }
    }
}

