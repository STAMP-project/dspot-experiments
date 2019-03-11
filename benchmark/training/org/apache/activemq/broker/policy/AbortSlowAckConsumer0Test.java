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
package org.apache.activemq.broker.policy;


import Session.CLIENT_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.JmsMultipleClientsTestSupport;
import org.apache.activemq.broker.region.policy.AbortSlowAckConsumerStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AbortSlowAckConsumer0Test extends AbortSlowConsumer0Test {
    protected long maxTimeSinceLastAck = 5 * 1000;

    protected AbortSlowAckConsumerStrategy strategy;

    public AbortSlowAckConsumer0Test(Boolean isTopic) {
        super();
        this.topic = isTopic;
    }

    @Override
    @Test
    public void testSlowConsumerIsAbortedViaJmx() throws Exception {
        strategy.setMaxTimeSinceLastAck(500);// so jmx does the abort

        super.testSlowConsumerIsAbortedViaJmx();
    }

    @Test
    public void testZeroPrefetchConsumerIsAborted() throws Exception {
        strategy.setMaxTimeSinceLastAck(2000);// Make it shorter

        ActiveMQConnection conn = ((ActiveMQConnection) (createConnectionFactory().createConnection()));
        conn.setExceptionListener(this);
        connections.add(conn);
        Session sess = conn.createSession(false, CLIENT_ACKNOWLEDGE);
        final MessageConsumer consumer = sess.createConsumer(destination);
        Assert.assertNotNull(consumer);
        conn.start();
        startProducers(destination, 20);
        Message message = consumer.receive(5000);
        Assert.assertNotNull(message);
        TimeUnit.SECONDS.sleep(15);
        try {
            consumer.receive(5000);
            Assert.fail("Slow consumer not aborted.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testIdleConsumerCanBeAbortedNoMessages() throws Exception {
        strategy.setIgnoreIdleConsumers(false);
        strategy.setMaxTimeSinceLastAck(2000);// Make it shorter

        ActiveMQConnection conn = ((ActiveMQConnection) (createConnectionFactory().createConnection()));
        conn.setExceptionListener(this);
        connections.add(conn);
        Session sess = conn.createSession(false, CLIENT_ACKNOWLEDGE);
        final MessageConsumer consumer = sess.createConsumer(destination);
        Assert.assertNotNull(consumer);
        conn.start();
        startProducers(destination, 1);
        Message message = consumer.receive(5000);
        Assert.assertNotNull(message);
        // Consumer needs to be closed before the reeive call.
        TimeUnit.SECONDS.sleep(15);
        try {
            consumer.receive(5000);
            Assert.fail("Idle consumer not aborted.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testIdleConsumerCanBeAborted() throws Exception {
        strategy.setIgnoreIdleConsumers(false);
        strategy.setMaxTimeSinceLastAck(2000);// Make it shorter

        ActiveMQConnection conn = ((ActiveMQConnection) (createConnectionFactory().createConnection()));
        conn.setExceptionListener(this);
        connections.add(conn);
        Session sess = conn.createSession(false, CLIENT_ACKNOWLEDGE);
        final MessageConsumer consumer = sess.createConsumer(destination);
        Assert.assertNotNull(consumer);
        conn.start();
        startProducers(destination, 1);
        Message message = consumer.receive(5000);
        Assert.assertNotNull(message);
        message.acknowledge();
        // Consumer needs to be closed before the reeive call.
        TimeUnit.SECONDS.sleep(15);
        try {
            consumer.receive(5000);
            Assert.fail("Idle consumer not aborted.");
        } catch (Exception ex) {
        }
    }
}

