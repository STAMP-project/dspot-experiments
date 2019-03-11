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
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to ensure DLQ expiring message is not in recursive loop.
 */
public class AMQ6121Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6121Test.class);

    private BrokerService broker;

    @Test(timeout = 30000)
    public void sendToDLQ() throws Exception {
        final int MSG_COUNT = 50;
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(broker.getVmConnectorURI());
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("ActiveMQ.DLQ");
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(PERSISTENT);
        TextMessage txtMessage = session.createTextMessage();
        txtMessage.setText("Test_Message");
        // Exceed audit so that the entries beyond audit aren't detected as duplicate
        for (int i = 0; i < MSG_COUNT; ++i) {
            producer.send(txtMessage, PERSISTENT, 4, 1000L);
        }
        final QueueViewMBean view = getProxyToQueue("ActiveMQ.DLQ");
        AMQ6121Test.LOG.info("WAITING for expiry...");
        Assert.assertTrue("Queue drained of expired", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getQueueSize()) == 0;
            }
        }));
        AMQ6121Test.LOG.info("FINISHED WAITING for expiry.");
        // check the enqueue counter
        AMQ6121Test.LOG.info(("Queue enqueue counter ==>>>" + (view.getEnqueueCount())));
        Assert.assertEquals("Enqueue size ", MSG_COUNT, view.getEnqueueCount());
        connection.close();
    }
}

