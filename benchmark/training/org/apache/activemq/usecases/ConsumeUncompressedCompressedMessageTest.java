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


import java.net.URI;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumeUncompressedCompressedMessageTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumeUncompressedCompressedMessageTest.class);

    private BrokerService broker;

    private URI tcpUri;

    ActiveMQConnectionFactory factory;

    ActiveMQConnection connection;

    Session session;

    Queue queue;

    @Test
    public void testBrowseAndReceiveCompressedMessages() throws Exception {
        Assert.assertTrue(connection.isUseCompression());
        createProducerAndSendMessages(1);
        QueueViewMBean queueView = getProxyToQueueViewMBean();
        Assert.assertNotNull(queueView);
        CompositeData[] compdatalist = queueView.browse();
        if ((compdatalist.length) == 0) {
            Assert.fail("There is no message in the queue:");
        }
        CompositeData cdata = compdatalist[0];
        assertComplexData(0, cdata, "Text", ("Test Text Message: " + 0));
        assertMessageAreCorrect(1);
    }

    @Test
    public void testReceiveAndResendWithCompressionOff() throws Exception {
        Assert.assertTrue(connection.isUseCompression());
        createProducerAndSendMessages(1);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = ((TextMessage) (consumer.receive(5000)));
        Assert.assertTrue(isCompressed());
        ConsumeUncompressedCompressedMessageTest.LOG.debug(("Received Message with Text = " + (message.getText())));
        connection.setUseCompression(false);
        MessageProducer producer = session.createProducer(queue);
        producer.send(message);
        producer.close();
        message = ((TextMessage) (consumer.receive(5000)));
        ConsumeUncompressedCompressedMessageTest.LOG.debug(("Received Message with Text = " + (message.getText())));
    }
}

