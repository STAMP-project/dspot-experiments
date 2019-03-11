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
package org.apache.activemq.camel.camelplugin;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;


public class CamelPluginConfigTest {
    protected static final String CONF_ROOT = "src/test/resources/org/apache/activemq/camel/camelplugin/";

    protected static final String TOPIC_NAME = "test.topic";

    protected static final String QUEUE_NAME = "test.queue";

    protected BrokerService brokerService;

    protected ActiveMQConnectionFactory factory;

    protected Connection producerConnection;

    protected Connection consumerConnection;

    protected Session consumerSession;

    protected Session producerSession;

    protected int messageCount = 1000;

    protected int timeOutInSeconds = 10;

    @Test
    public void testReRouteAll() throws Exception {
        Thread.sleep(2000);
        final ActiveMQQueue queue = new ActiveMQQueue(CamelPluginConfigTest.QUEUE_NAME);
        Topic topic = consumerSession.createTopic(CamelPluginConfigTest.TOPIC_NAME);
        final CountDownLatch latch = new CountDownLatch(messageCount);
        MessageConsumer consumer = consumerSession.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    latch.countDown();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        MessageProducer producer = producerSession.createProducer(topic);
        for (int i = 0; i < (messageCount); i++) {
            Message message = producerSession.createTextMessage(("test: " + i));
            producer.send(message);
        }
        latch.await(timeOutInSeconds, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }
}

