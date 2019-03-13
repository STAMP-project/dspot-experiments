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


import java.io.File;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ7118Test {
    protected static final Logger LOG = LoggerFactory.getLogger(AMQ7118Test.class);

    protected static Random r = new Random();

    static final String WIRE_LEVEL_ENDPOINT = "tcp://localhost:61616";

    protected BrokerService broker;

    protected Connection producerConnection;

    protected Session pSession;

    protected Connection cConnection;

    protected Session cSession;

    private final String xbean = "xbean:";

    private final String confBase = "src/test/resources/org/apache/activemq/bugs/amq7118";

    int checkpointIndex = 0;

    protected long idGenerator;

    private static final ActiveMQConnectionFactory ACTIVE_MQ_CONNECTION_FACTORY = new ActiveMQConnectionFactory(AMQ7118Test.WIRE_LEVEL_ENDPOINT);

    @Test
    public void testCompaction() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        setupProducerConnection();
        setupConsumerConnection();
        Topic topic = pSession.createTopic("test");
        MessageConsumer consumer = cSession.createDurableSubscriber(topic, "clientId1");
        AMQ7118Test.LOG.info("Produce message to test topic");
        AMQ7118Test.produce(pSession, topic, 1, 512);// just one message

        AMQ7118Test.LOG.info("Consume message from test topic");
        Message msg = consumer.receive(5000);
        Assert.assertNotNull(msg);
        AMQ7118Test.LOG.info("Produce more messages to test topic and get into PFC");
        boolean sent = AMQ7118Test.produce(cSession, topic, 20, (512 * 1024));// Fill the store

        Assert.assertFalse("Never got to PFC condition", sent);
        AMQ7118Test.LOG.info("PFC hit");
        // We hit PFC, so shut down the producer
        producerConnection.close();
        // Lets check the db-*.log file count before checkpointUpdate
        checkFiles(false, 21, "db-21.log");
        // Force checkFiles update
        checkFiles(true, 23, "db-23.log");
        // The ackMessageFileMap should be clean, so no more writing
        checkFiles(true, 23, "db-23.log");
        // One more time just to be sure - The ackMessageFileMap should be clean, so no more writing
        checkFiles(true, 23, "db-23.log");
        // Read out the rest of the messages
        AMQ7118Test.LOG.info("Consuming the rest of the files...");
        for (int i = 0; i < 20; i++) {
            msg = consumer.receive(5000);
        }
        AMQ7118Test.LOG.info("All messages Consumed.");
        // Clean up the log files and be sure its stable
        checkFiles(true, 2, "db-33.log");
        checkFiles(true, 3, "db-34.log");
        checkFiles(true, 2, "db-34.log");
        checkFiles(true, 2, "db-34.log");
        checkFiles(true, 2, "db-34.log");
        broker.stop();
        broker.waitUntilStopped();
    }

    private class DBFileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            int n1 = extractNumber(o1.getName());
            int n2 = extractNumber(o2.getName());
            return n1 - n2;
        }

        private int extractNumber(String name) {
            int i = 0;
            try {
                int s = (name.indexOf('-')) + 1;
                int e = name.lastIndexOf('.');
                String number = name.substring(s, e);
                i = Integer.parseInt(number);
            } catch (Exception e) {
                i = 0;// if filename does not match the format

                // then default to 0
            }
            return i;
        }
    }
}

