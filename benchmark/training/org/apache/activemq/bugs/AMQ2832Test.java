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


import ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.leveldb.LevelDBStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2832Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2832Test.class);

    BrokerService broker = null;

    private ActiveMQConnectionFactory cf;

    private final Destination destination = new ActiveMQQueue("AMQ2832Test");

    private String connectionUri;

    /**
     * Scenario:
     * db-1.log has an unacknowledged message,
     * db-2.log contains acks for the messages from db-1.log,
     * db-3.log contains acks for the messages from db-2.log
     *
     * Expected behavior: since db-1.log is blocked, db-2.log and db-3.log should not be removed during the cleanup.
     * Current situation on 5.10.0, 5.10.1 is that db-3.log is removed causing all messages from db-2.log, whose acks were in db-3.log, to be replayed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAckChain() throws Exception {
        startBroker();
        makeAckChain();
        broker.stop();
        broker.waitUntilStopped();
        recoverBroker();
        AMQ2832Test.StagedConsumer consumer = new AMQ2832Test.StagedConsumer();
        Message message = consumer.receive(1);
        Assert.assertNotNull("One message stays unacked from db-1.log", message);
        message.acknowledge();
        message = consumer.receive(1);
        Assert.assertNull("There should not be any unconsumed messages any more", message);
        consumer.close();
    }

    @Test
    public void testNoRestartOnMissingAckDataFile() throws Exception {
        startBroker();
        // reuse scenario from previous test
        makeAckChain();
        File dataDir = broker.getPersistenceAdapter().getDirectory();
        broker.stop();
        broker.waitUntilStopped();
        File secondLastDataFile = new File(dataDir, "db-3.log");
        AMQ2832Test.LOG.info(("Whacking data file with acks: " + secondLastDataFile));
        secondLastDataFile.delete();
        try {
            doStartBroker(false, false);
            Assert.fail("Expect failure to start with corrupt journal");
        } catch (IOException expected) {
        }
    }

    @Test
    public void testAckRemovedMessageReplayedAfterRecovery() throws Exception {
        startBroker();
        AMQ2832Test.StagedConsumer consumer = new AMQ2832Test.StagedConsumer();
        int numMessagesAvailable = produceMessagesToConsumeMultipleDataFiles(20);
        // this will block the reclaiming of one data file
        Message firstUnacked = consumer.receive(10);
        AMQ2832Test.LOG.info(("first unacked: " + (firstUnacked.getJMSMessageID())));
        Message secondUnacked = consumer.receive(1);
        AMQ2832Test.LOG.info(("second unacked: " + (secondUnacked.getJMSMessageID())));
        numMessagesAvailable -= 11;
        numMessagesAvailable += produceMessagesToConsumeMultipleDataFiles(10);
        // ensure ack is another data file
        AMQ2832Test.LOG.info(("Acking firstUnacked: " + (firstUnacked.getJMSMessageID())));
        firstUnacked.acknowledge();
        numMessagesAvailable += produceMessagesToConsumeMultipleDataFiles(10);
        consumer.receive(numMessagesAvailable).acknowledge();
        // second unacked should keep first data file available but journal with the first ack
        // may get whacked
        consumer.close();
        broker.stop();
        broker.waitUntilStopped();
        recoverBroker();
        consumer = new AMQ2832Test.StagedConsumer();
        // need to force recovery?
        Message msg = consumer.receive(1, 5);
        Assert.assertNotNull("One messages left after recovery", msg);
        msg.acknowledge();
        // should be no more messages
        msg = consumer.receive(1, 5);
        Assert.assertEquals(("Only one messages left after recovery: " + msg), null, msg);
        consumer.close();
    }

    @Test
    public void testAlternateLossScenario() throws Exception {
        startBroker();
        PersistenceAdapter pa = broker.getPersistenceAdapter();
        if (pa instanceof LevelDBStore) {
            return;
        }
        ActiveMQQueue queue = new ActiveMQQueue("MyQueue");
        ActiveMQQueue disposable = new ActiveMQQueue("MyDisposableQueue");
        ActiveMQTopic topic = new ActiveMQTopic("MyDurableTopic");
        // This ensure that data file 1 never goes away.
        createInactiveDurableSub(topic);
        Assert.assertEquals(1, getNumberOfJournalFiles());
        // One Queue Message that will be acked in another data file.
        produceMessages(queue, 1);
        Assert.assertEquals(1, getNumberOfJournalFiles());
        // Add some messages to consume space
        produceMessages(disposable, 50);
        int dataFilesCount = getNumberOfJournalFiles();
        Assert.assertTrue((dataFilesCount > 1));
        // Create an ack for the single message on this queue
        drainQueue(queue);
        // Add some more messages to consume space beyond tha data file with the ack
        produceMessages(disposable, 50);
        Assert.assertTrue((dataFilesCount < (getNumberOfJournalFiles())));
        dataFilesCount = getNumberOfJournalFiles();
        restartBroker();
        // Clear out all queue data
        broker.getAdminView().removeQueue(disposable.getQueueName());
        // Once this becomes true our ack could be lost.
        Assert.assertTrue(("Less than three journal file expected, was " + (getNumberOfJournalFiles())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumberOfJournalFiles()) <= 4;
            }
        }, TimeUnit.MINUTES.toMillis(3)));
        // Recover and the Message should not be replayed but if the old MessageAck is lost
        // then it could be.
        recoverBroker();
        Assert.assertTrue(((drainQueue(queue)) == 0));
    }

    final String payload = new String(new byte[1024]);

    private class StagedConsumer {
        Connection connection;

        MessageConsumer consumer;

        StagedConsumer() throws Exception {
            connection = createConnection();
            connection.start();
            consumer = connection.createSession(false, INDIVIDUAL_ACKNOWLEDGE).createConsumer(destination);
        }

        public Message receive(int numToReceive) throws Exception {
            return receive(numToReceive, 2);
        }

        public Message receive(int numToReceive, int timeoutInSeconds) throws Exception {
            Message msg = null;
            for (; numToReceive > 0; numToReceive--) {
                do {
                    msg = consumer.receive((1 * 1000));
                } while ((msg == null) && ((--timeoutInSeconds) > 0) );
                if (numToReceive > 1) {
                    msg.acknowledge();
                }
                if (msg != null) {
                    AMQ2832Test.LOG.debug(("received: " + (msg.getJMSMessageID())));
                }
            }
            // last message, unacked
            return msg;
        }

        void close() throws JMSException {
            consumer.close();
            connection.close();
        }
    }
}

