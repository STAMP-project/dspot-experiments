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


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.leveldb.LevelDBStoreViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4677Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ4677Test.class);

    private static BrokerService brokerService;

    @Rule
    public TestName name = new TestName();

    private File dataDirFile;

    @Test
    public void testSendAndReceiveAllMessages() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://LevelDBBroker");
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(getClass().getName());
        connection.start();
        final Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.toString());
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(PERSISTENT);
        final LevelDBStoreViewMBean levelDBView = getLevelDBStoreMBean();
        Assert.assertNotNull(levelDBView);
        levelDBView.compact();
        final int SIZE = 10 * 1024;
        final int MSG_COUNT = 30000;// very slow consuming 60k messages of size 30k

        final CountDownLatch done = new CountDownLatch(MSG_COUNT);
        byte[] buffer = new byte[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            buffer[i] = ((byte) (128));
        }
        for (int i = 0; i < MSG_COUNT; ++i) {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(buffer);
            producer.send(message);
            if ((i % 1000) == 0) {
                AMQ4677Test.LOG.info("Sent message #{}", i);
                session.commit();
            }
        }
        session.commit();
        AMQ4677Test.LOG.info("Finished sending all messages.");
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (((done.getCount()) % 1000) == 0) {
                    try {
                        AMQ4677Test.LOG.info("Received message #{}", (MSG_COUNT - (done.getCount())));
                        session.commit();
                    } catch (JMSException e) {
                    }
                }
                done.countDown();
            }
        });
        done.await(15, TimeUnit.MINUTES);
        session.commit();
        AMQ4677Test.LOG.info("Finished receiving all messages.");
        Assert.assertTrue("Should < 3 logfiles left.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                levelDBView.compact();
                return (countLogFiles()) < 3;
            }
        }, TimeUnit.MINUTES.toMillis(5), ((int) (TimeUnit.SECONDS.toMillis(30)))));
        levelDBView.compact();
        AMQ4677Test.LOG.info("Current number of logs {}", countLogFiles());
    }
}

