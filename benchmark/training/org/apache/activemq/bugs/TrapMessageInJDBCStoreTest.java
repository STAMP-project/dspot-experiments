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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.jdbc.TransactionContext;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


/**
 * Test to demostrate a message trapped in the JDBC store and not
 * delivered to consumer
 *
 * The test throws issues the commit to the DB but throws
 * an exception back to the broker. This scenario could happen when a network
 * cable is disconnected - message is committed to DB but broker does not know.
 */
public class TrapMessageInJDBCStoreTest extends TestCase {
    private static final String MY_TEST_Q = "MY_TEST_Q";

    private static final Logger LOG = LoggerFactory.getLogger(TrapMessageInJDBCStoreTest.class);

    private String transportUrl = "tcp://127.0.0.1:0";

    private BrokerService broker;

    private TrapMessageInJDBCStoreTest.TestTransactionContext testTransactionContext;

    private TrapMessageInJDBCStoreTest.TestJDBCPersistenceAdapter jdbc;

    private Connection checkOnStoreConnection;

    /**
     * sends 3 messages to the queue. When the second message is being committed to the JDBCStore, $
     * it throws a dummy SQL exception - the message has been committed to the embedded DB before the exception
     * is thrown
     *
     * Excepted correct outcome: receive 3 messages and the DB should contain no messages
     *
     * @throws Exception
     * 		
     */
    public void testDBCommitException() throws Exception {
        org.apache.log4j.Logger serviceLogger = getLogger(((TransportConnection.class.getName()) + ".Service"));
        serviceLogger.setLevel(Level.TRACE);
        broker = this.createBroker(false);
        broker.deleteAllMessages();
        broker.start();
        broker.waitUntilStarted();
        try {
            TrapMessageInJDBCStoreTest.LOG.info("***Broker started...");
            // failover but timeout in 5 seconds so the test does not hang
            String failoverTransportURL = ("failover:(" + (transportUrl)) + ")?timeout=5000";
            sendMessage(TrapMessageInJDBCStoreTest.MY_TEST_Q, failoverTransportURL);
            // check db contents
            ArrayList<Long> dbSeq = dbMessageCount(checkOnStoreConnection);
            TrapMessageInJDBCStoreTest.LOG.info(("*** after send: db contains message seq " + dbSeq));
            List<TextMessage> consumedMessages = consumeMessages(TrapMessageInJDBCStoreTest.MY_TEST_Q, failoverTransportURL);
            TestCase.assertEquals("number of consumed messages", 3, consumedMessages.size());
            // check db contents
            dbSeq = dbMessageCount(checkOnStoreConnection);
            TrapMessageInJDBCStoreTest.LOG.info(("*** after consume - db contains message seq " + dbSeq));
            TestCase.assertEquals("number of messages in DB after test", 0, dbSeq.size());
        } finally {
            try {
                checkOnStoreConnection.close();
            } catch (Exception ignored) {
            }
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    /* Mock classes used for testing */
    public class TestJDBCPersistenceAdapter extends JDBCPersistenceAdapter {
        public TransactionContext getTransactionContext() throws IOException {
            return testTransactionContext;
        }
    }

    public class TestTransactionContext extends TransactionContext {
        private int count;

        public TestTransactionContext(JDBCPersistenceAdapter jdbcPersistenceAdapter) throws IOException {
            super(jdbcPersistenceAdapter);
        }

        public void executeBatch() throws SQLException {
            super.executeBatch();
            (count)++;
            TrapMessageInJDBCStoreTest.LOG.debug(("ExecuteBatchOverride: count:" + (count)), new RuntimeException("executeBatch"));
            // throw on second add message
            if ((count) == 16) {
                throw new SQLException(("TEST SQL EXCEPTION from executeBatch after super.execution: count:" + (count)));
            }
        }
    }
}

