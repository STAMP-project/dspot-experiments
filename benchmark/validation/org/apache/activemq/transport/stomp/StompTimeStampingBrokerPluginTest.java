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
package org.apache.activemq.transport.stomp;


import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


public class StompTimeStampingBrokerPluginTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompTimeStampingBrokerPluginTest.class);

    private Connection connection;

    private Session session;

    @Test(timeout = 60000)
    public void testSendMessage() throws Exception {
        Destination destination = session.createQueue(getQueueName());
        MessageConsumer consumer = session.createConsumer(destination);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        long timestamp = (System.currentTimeMillis()) - (TimeUnit.DAYS.toMillis(1));
        long expires = timestamp + (TimeUnit.SECONDS.toMillis(5));
        StompTimeStampingBrokerPluginTest.LOG.info("Message timestamp = {}, expires = {}", timestamp, expires);
        frame = (((((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "timestamp:") + timestamp) + "\n") + "expires:") + expires) + "\n\n") + "Hello World 1") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "timestamp:") + timestamp) + "\n") + "expires:") + expires) + "\n\n") + "Hello World 2") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        TimeUnit.SECONDS.sleep(10);
        message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNull(message);
    }
}

