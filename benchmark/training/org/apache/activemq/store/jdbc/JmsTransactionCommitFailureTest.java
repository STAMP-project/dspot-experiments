/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.jdbc;


import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;


public class JmsTransactionCommitFailureTest {
    private static final Log LOGGER = LogFactory.getLog(JmsTransactionCommitFailureTest.class);

    private static final String OUTPUT_DIR = "target/" + (JmsTransactionCommitFailureTest.class.getSimpleName());

    private Properties originalSystemProps;

    private DataSource dataSource;

    private JmsTransactionCommitFailureTest.CommitFailurePersistenceAdapter persistenceAdapter;

    private BrokerService broker;

    private ConnectionFactory connectionFactory;

    private int messageCounter = 1;

    @Test
    public void testJmsTransactionCommitFailure() throws Exception {
        String queueName = "testJmsTransactionCommitFailure";
        // Send 1.message
        sendMessage(queueName, 1);
        // Check message count directly in database
        Assert.assertEquals(1L, getMessageCount());
        // Set failure flag on persistence adapter
        persistenceAdapter.setCommitFailureEnabled(true);
        // Send 2.message and 3.message in one JMS transaction
        try {
            JmsTransactionCommitFailureTest.LOGGER.warn("Attempt to send Message-2/Message-3 (first time)...");
            sendMessage(queueName, 2);
            JmsTransactionCommitFailureTest.LOGGER.warn("Message-2/Message-3 successfuly sent (first time)");
            Assert.fail();
        } catch (JMSException jmse) {
            // Expected - decrease message counter (I want to repeat message send)
            JmsTransactionCommitFailureTest.LOGGER.warn("Attempt to send Message-2/Message-3 failed", jmse);
            messageCounter -= 2;
            Assert.assertEquals(1L, getMessageCount());
        }
        // Reset failure flag on persistence adapter
        persistenceAdapter.setCommitFailureEnabled(false);
        // Send 2.message again
        JmsTransactionCommitFailureTest.LOGGER.warn("Attempt to send Message-2/Message-3 (second time)...");
        sendMessage(queueName, 2);
        JmsTransactionCommitFailureTest.LOGGER.warn("Message-2/Message-3 successfuly sent (second time)");
        int expectedMessageCount = 3;
        // Check message count directly in database
        Assert.assertEquals(3L, getMessageCount());
        // Attempt to receive 3 (expected) messages
        for (int i = 1; i <= expectedMessageCount; i++) {
            Message message = receiveMessage(queueName, 10000);
            JmsTransactionCommitFailureTest.LOGGER.warn((((i + ". Message received (") + message) + ")"));
            Assert.assertNotNull(message);
            Assert.assertTrue((message instanceof TextMessage));
            Assert.assertEquals(i, message.getIntProperty("MessageId"));
            Assert.assertEquals(("Message-" + i), getText());
            // Check message count directly in database
            // Assert.assertEquals(expectedMessageCount - i, getMessageCount());
        }
        // Check message count directly in database
        Assert.assertEquals(0, getMessageCount());
        // No next message is expected
        Assert.assertNull(receiveMessage(queueName, 4000));
    }

    @Test
    public void testQueueMemoryLeak() throws Exception {
        String queueName = "testMemoryLeak";
        sendMessage(queueName, 1);
        // Set failure flag on persistence adapter
        persistenceAdapter.setCommitFailureEnabled(true);
        try {
            for (int i = 0; i < 10; i++) {
                try {
                    sendMessage(queueName, 2);
                } catch (JMSException jmse) {
                    // Expected
                }
            }
        } finally {
            persistenceAdapter.setCommitFailureEnabled(false);
        }
        Destination destination = broker.getDestination(new ActiveMQQueue(queueName));
        if (destination instanceof org.apache.activemq.broker.region.Queue) {
            org.apache.activemq.broker.region.Queue queue = ((org.apache.activemq.broker.region.Queue) (destination));
            Field listField = Queue.class.getDeclaredField("indexOrderedCursorUpdates");
            listField.setAccessible(true);
            List<?> list = ((List<?>) (listField.get(queue)));
            Assert.assertEquals(0, list.size());
        }
    }

    @Test
    public void testQueueMemoryLeakNoTx() throws Exception {
        String queueName = "testMemoryLeak";
        sendMessage(queueName, 1);
        // Set failure flag on persistence adapter
        persistenceAdapter.setCommitFailureEnabled(true);
        try {
            for (int i = 0; i < 10; i++) {
                try {
                    sendMessage(queueName, 2, false);
                } catch (JMSException jmse) {
                    // Expected
                }
            }
        } finally {
            persistenceAdapter.setCommitFailureEnabled(false);
        }
        Destination destination = broker.getDestination(new ActiveMQQueue(queueName));
        if (destination instanceof org.apache.activemq.broker.region.Queue) {
            org.apache.activemq.broker.region.Queue queue = ((org.apache.activemq.broker.region.Queue) (destination));
            Field listField = Queue.class.getDeclaredField("indexOrderedCursorUpdates");
            listField.setAccessible(true);
            List<?> list = ((List<?>) (listField.get(queue)));
            Assert.assertEquals(0, list.size());
        }
    }

    private static class CommitFailurePersistenceAdapter extends JDBCPersistenceAdapter {
        private boolean isCommitFailureEnabled;

        private int transactionIsolation;

        public CommitFailurePersistenceAdapter(DataSource dataSource) {
            setDataSource(dataSource);
        }

        public void setCommitFailureEnabled(boolean isCommitFailureEnabled) {
            this.isCommitFailureEnabled = isCommitFailureEnabled;
        }

        @Override
        public void setTransactionIsolation(int transactionIsolation) {
            super.setTransactionIsolation(transactionIsolation);
            this.transactionIsolation = transactionIsolation;
        }

        @Override
        public TransactionContext getTransactionContext() throws IOException {
            TransactionContext answer = new TransactionContext(this) {
                @Override
                public void executeBatch() throws SQLException {
                    if (isCommitFailureEnabled) {
                        throw new SQLException("Test commit failure exception");
                    }
                    super.executeBatch();
                }
            };
            if ((transactionIsolation) > 0) {
                answer.setTransactionIsolation(transactionIsolation);
            }
            return answer;
        }
    }
}

