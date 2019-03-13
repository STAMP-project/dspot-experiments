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
package org.apache.activemq.broker.virtual;


import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.virtual.CompositeQueue;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;


/**
 * https://issues.apache.org/jira/browse/AMQ-5898
 */
public class MultipleCompositeToPhysicalQueueTest {
    private final ActiveMQQueue SUB1 = new ActiveMQQueue("SUB1");

    private final CompositeQueue PUB_BROADCAST = newCompositeQueue("PUB.ALL", SUB1);

    private final CompositeQueue PUB_INDIVIDUAL = newCompositeQueue("PUB.SUB1", SUB1);

    private String url;

    private BrokerService broker;

    @Test(timeout = 60000)
    public void testManyToOne() throws Exception {
        Session consumerSession = buildSession("Consumer", url);
        MessageConsumer consumer = createSubscriber(consumerSession, SUB1, null);
        // Producer
        Session publisherSession = buildSession("Producer", url);
        createPublisher(publisherSession, PUB_BROADCAST.getVirtualDestination()).send(publisherSession.createTextMessage("BROADCAST"));
        ActiveMQMessage broadcastMessage = ((ActiveMQMessage) (consumer.receive()));
        ActiveMQDestination originalDestination = broadcastMessage.getOriginalDestination();
        Assert.assertEquals("BROADCAST", getText());
        Assert.assertEquals(PUB_BROADCAST.getName(), broadcastMessage.getOriginalDestination().getPhysicalName());
        createPublisher(publisherSession, PUB_INDIVIDUAL.getVirtualDestination()).send(publisherSession.createTextMessage("INDIVIDUAL"));
        ActiveMQMessage individualMessage = ((ActiveMQMessage) (consumer.receive()));
        Assert.assertEquals("INDIVIDUAL", getText());
        Assert.assertEquals(PUB_INDIVIDUAL.getName(), individualMessage.getOriginalDestination().getPhysicalName());
    }
}

