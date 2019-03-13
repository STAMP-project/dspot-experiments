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


import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMXRemoveQueueThenSendIgnoredTest {
    private static final Logger LOG = LoggerFactory.getLogger(JMXRemoveQueueThenSendIgnoredTest.class);

    private static final String domain = "org.apache.activemq";

    private BrokerService brokerService;

    private MessageProducer producer;

    private QueueSession session;

    private QueueConnection connection;

    private Queue queue;

    private int count = 1;

    @Test
    public void testRemoveQueueAndProduceAfterNewConsumerAdded() throws Exception {
        MessageConsumer firstConsumer = registerConsumer();
        produceMessage();
        Message message = firstConsumer.receive(5000);
        JMXRemoveQueueThenSendIgnoredTest.LOG.info(("Received message " + message));
        Assert.assertEquals(1, numberOfMessages());
        firstConsumer.close();
        session.commit();
        Thread.sleep(1000);
        removeQueue();
        Thread.sleep(1000);
        MessageConsumer secondConsumer = registerConsumer();
        produceMessage();
        message = secondConsumer.receive(5000);
        JMXRemoveQueueThenSendIgnoredTest.LOG.debug(("Received message " + message));
        Assert.assertEquals(1, numberOfMessages());
        secondConsumer.close();
    }

    @Test
    public void testRemoveQueueAndProduceBeforeNewConsumerAdded() throws Exception {
        MessageConsumer firstConsumer = registerConsumer();
        produceMessage();
        Message message = firstConsumer.receive(5000);
        JMXRemoveQueueThenSendIgnoredTest.LOG.info(("Received message " + message));
        Assert.assertEquals(1, numberOfMessages());
        firstConsumer.close();
        session.commit();
        Thread.sleep(1000);
        removeQueue();
        Thread.sleep(1000);
        produceMessage();
        MessageConsumer secondConsumer = registerConsumer();
        message = secondConsumer.receive(5000);
        JMXRemoveQueueThenSendIgnoredTest.LOG.debug(("Received message " + message));
        Assert.assertEquals(1, numberOfMessages());
        secondConsumer.close();
    }
}

