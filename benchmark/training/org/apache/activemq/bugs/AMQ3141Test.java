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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class AMQ3141Test {
    private static final int MAX_MESSAGES = 100;

    private static final long DELAY_IN_MS = 100;

    private static final String QUEUE_NAME = "target.queue";

    private BrokerService broker;

    private final CountDownLatch messageCountDown = new CountDownLatch(AMQ3141Test.MAX_MESSAGES);

    private ConnectionFactory factory;

    @Test
    public void testNoMissingMessagesOnShortScheduleDelay() throws Exception {
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(AMQ3141Test.QUEUE_NAME));
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                messageCountDown.countDown();
            }
        });
        sendMessages();
        boolean receiveComplete = messageCountDown.await(5, TimeUnit.SECONDS);
        connection.close();
        Assert.assertTrue((("expect all messages received but " + (messageCountDown.getCount())) + " are missing"), receiveComplete);
    }
}

