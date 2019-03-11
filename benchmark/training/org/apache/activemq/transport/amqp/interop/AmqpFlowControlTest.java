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
package org.apache.activemq.transport.amqp.interop;


import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.transport.amqp.AmqpTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpMessage;
import org.apache.activemq.transport.amqp.client.AmqpReceiver;
import org.apache.activemq.transport.amqp.client.AmqpSender;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class AmqpFlowControlTest extends AmqpClientTestSupport {
    @Test(timeout = 60000)
    public void testCreditNotGrantedUntilBacklogClears() throws Exception {
        final int MSG_COUNT = 1000;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        AmqpSender sender = session.createSender(("queue://" + (getTestName())), true);
        for (int i = 1; i <= MSG_COUNT; ++i) {
            AmqpMessage message = new AmqpMessage();
            message.setText(("Test-Message: " + i));
            sender.send(message);
            if ((i % 1000) == 0) {
                AmqpTestSupport.LOG.info("Sent message: {}", i);
            }
        }
        // Should only accept one message
        final QueueViewMBean queue = getProxyToQueue(getTestName());
        Assert.assertTrue("All messages should arrive", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queue.getQueueSize()) == 1;
            }
        }));
        Assert.assertEquals(0, sender.getEndpoint().getRemoteCredit());
        receiver.flow(1);
        AmqpMessage received = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(received);
        received.accept();
        // Should not grant any credit until backlog starts to clear
        Assert.assertEquals(0, sender.getEndpoint().getRemoteCredit());
        receiver.flow((MSG_COUNT - 1));
        for (int i = 0; i < (MSG_COUNT - 1); ++i) {
            received = receiver.receive(5, TimeUnit.SECONDS);
            Assert.assertNotNull(received);
            received.accept();
        }
        // Should have been granted credit once backlog was cleared.
        Assert.assertTrue(((sender.getEndpoint().getRemoteCredit()) > 0));
        sender.close();
        connection.close();
    }
}

