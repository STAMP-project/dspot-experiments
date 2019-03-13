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
package org.apache.activemq.usecases;


import java.util.List;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class PublishOnQueueConsumedMessageInTransactionTest extends TestCase implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(PublishOnQueueConsumedMessageInTransactionTest.class);

    private Session producerSession;

    private Session consumerSession;

    private Destination queue;

    private ActiveMQConnectionFactory factory;

    private MessageProducer producer;

    private MessageConsumer consumer;

    private Connection connection;

    private ObjectMessage objectMessage;

    private List<Message> messages = createConcurrentList();

    private final Object lock = new Object();

    private String[] data;

    private String dataFileRoot = IOHelper.getDefaultDataDirectory();

    private int messageCount = 3;

    private String url = "vm://localhost";

    public void testSendReceive() throws Exception {
        sendMessage();
        connection.start();
        consumer = consumerSession.createConsumer(queue);
        consumer.setMessageListener(this);
        waitForMessagesToBeDelivered();
        TestCase.assertEquals("Messages received doesn't equal messages sent", messages.size(), data.length);
    }
}

