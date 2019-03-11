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
package org.apache.activemq;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class JmsCreateConsumerInOnMessageTest {
    private Connection connection;

    private ActiveMQConnectionFactory factory;

    @Rule
    public final TestName name = new TestName();

    /**
     * Tests if a consumer can be created asynchronusly
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testCreateConsumer() throws Exception {
        final CountDownLatch done = new CountDownLatch(1);
        final Session publisherSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final Session consumerSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final Topic topic = publisherSession.createTopic("Test.Topic");
        MessageConsumer consumer = consumerSession.createConsumer(topic);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    consumerSession.createConsumer(topic);
                    consumerSession.createProducer(topic);
                    done.countDown();
                } catch (Exception ex) {
                    Assert.assertTrue(false);
                }
            }
        });
        MessageProducer producer = publisherSession.createProducer(topic);
        connection.start();
        producer.send(publisherSession.createTextMessage("test"));
        Assert.assertTrue("Should have finished onMessage", done.await(5, TimeUnit.SECONDS));
    }
}

