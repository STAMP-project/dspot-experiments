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


import DeliveryMode.NON_PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class AMQ4517Test {
    private BrokerService brokerService;

    private String connectionUri;

    @Test(timeout = 360000)
    public void test() throws Exception {
        final ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(connectionUri);
        final AtomicBoolean advised = new AtomicBoolean(false);
        Connection connection = cf.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination dlqDestination = session.createTopic(((AdvisorySupport.MESSAGE_DLQ_TOPIC_PREFIX) + ">"));
        MessageConsumer consumer = session.createConsumer(dlqDestination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                advised.set(true);
            }
        });
        connection.start();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ActiveMQConnection connection = ((ActiveMQConnection) (cf.createConnection()));
                    Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                    Destination destination = session.createTemporaryQueue();
                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(NON_PERSISTENT);
                    producer.setTimeToLive(400);
                    producer.send(session.createTextMessage());
                    producer.send(session.createTextMessage());
                    TimeUnit.MILLISECONDS.sleep(500);
                    connection.close();
                } catch (Exception e) {
                }
            }
        });
        service.shutdown();
        Assert.assertTrue(service.awaitTermination(1, TimeUnit.MINUTES));
        Assert.assertFalse("Should not get any Advisories for DLQ'd Messages", advised.get());
    }
}

