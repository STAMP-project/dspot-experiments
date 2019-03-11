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


import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.ConsumerThread;
import org.apache.activemq.util.ProducerThread;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3120Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ3120Test.class);

    BrokerService broker = null;

    File kahaDbDir = null;

    private final Destination destination = new ActiveMQQueue("AMQ3120Test");

    final String payload = new String(new byte[1024]);

    @Test
    public void testCleanupOfFiles() throws Exception {
        final int messageCount = 500;
        startBroker(true);
        int fileCount = getFileCount(kahaDbDir);
        Assert.assertEquals(4, fileCount);
        Connection connection = createConnection();
        connection.start();
        Session producerSess = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Session consumerSess = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ProducerThread producer = new ProducerThread(producerSess, destination) {
            @Override
            protected Message createMessage(int i) throws Exception {
                return session.createTextMessage((((payload) + "::") + i));
            }
        };
        producer.setSleep(650);
        producer.setMessageCount(messageCount);
        ConsumerThread consumer = new ConsumerThread(consumerSess, destination);
        consumer.setBreakOnNull(false);
        consumer.setMessageCount(messageCount);
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        Assert.assertEquals("consumer got all produced messages", producer.getMessageCount(), consumer.getReceived());
        broker.stop();
        broker.waitUntilStopped();
    }
}

