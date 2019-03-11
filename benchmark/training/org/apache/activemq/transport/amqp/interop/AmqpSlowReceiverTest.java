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


import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import org.apache.activemq.broker.jmx.AbortSlowConsumerStrategyViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.region.policy.AbortSlowAckConsumerStrategy;
import org.apache.activemq.transport.amqp.AmqpTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpMessage;
import org.apache.activemq.transport.amqp.client.AmqpReceiver;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the handling of consumer abort when the AbortSlowAckConsumerStrategy is used.
 */
public class AmqpSlowReceiverTest extends AmqpClientTestSupport {
    private final long DEFAULT_CHECK_PERIOD = 1000;

    private final long DEFAULT_MAX_SLOW_DURATION = 3000;

    private AbortSlowAckConsumerStrategy strategy;

    @Test(timeout = 60 * 1000)
    public void testSlowConsumerIsAborted() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        final AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        receiver.flow(100);
        Assert.assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);
        sendMessages(getTestName(), 100, false);
        AmqpMessage message = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        Assert.assertTrue("Receiver should be closed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return receiver.isClosed();
            }
        }));
        Assert.assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
    }

    @Test
    public void testSlowConsumerIsAbortedViaJmx() throws Exception {
        strategy.setMaxSlowDuration((60 * 1000));// so jmx does the abort

        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        final AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        receiver.flow(100);
        sendMessages(getTestName(), 100, false);
        AmqpMessage message = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        QueueViewMBean queue = getProxyToQueue(getTestName());
        ObjectName slowConsumerPolicyMBeanName = queue.getSlowConsumerStrategy();
        Assert.assertNotNull(slowConsumerPolicyMBeanName);
        AbortSlowConsumerStrategyViewMBean abortPolicy = ((AbortSlowConsumerStrategyViewMBean) (brokerService.getManagementContext().newProxyInstance(slowConsumerPolicyMBeanName, AbortSlowConsumerStrategyViewMBean.class, true)));
        TimeUnit.SECONDS.sleep(6);
        TabularData slowOnes = abortPolicy.getSlowConsumers();
        Assert.assertEquals("one slow consumers", 1, slowOnes.size());
        AmqpTestSupport.LOG.info(("slow ones:" + slowOnes));
        CompositeData slowOne = ((CompositeData) (slowOnes.values().iterator().next()));
        AmqpTestSupport.LOG.info(("Slow one: " + slowOne));
        Assert.assertTrue("we have an object name", ((slowOne.get("subscription")) instanceof ObjectName));
        abortPolicy.abortConsumer(((ObjectName) (slowOne.get("subscription"))));
        Assert.assertTrue("Receiver should be closed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return receiver.isClosed();
            }
        }));
        slowOnes = abortPolicy.getSlowConsumers();
        Assert.assertEquals("no slow consumers left", 0, slowOnes.size());
        // verify mbean gone with destination
        brokerService.getAdminView().removeQueue(getTestName());
        try {
            abortPolicy.getSlowConsumers();
            Assert.fail("expect not found post destination removal");
        } catch (UndeclaredThrowableException expected) {
            Assert.assertTrue(("correct exception: " + (expected.getCause())), ((expected.getCause()) instanceof InstanceNotFoundException));
        }
    }
}

