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
package org.apache.activemq.network;


import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import org.junit.Test;


/**
 * This test is to show that if a durable subscription over a network bridge is deleted and
 * re-created, messages will flow properly again for dynamic subscriptions.
 *
 * AMQ-6050
 */
public class NetworkDurableRecreationTest extends DynamicNetworkTestSupport {
    /**
     * Test publisher on localBroker and durable on remoteBroker
     * after durable deletion, recreate durable
     */
    @Test(timeout = 30 * 1000)
    public void testDurableConsumer() throws Exception {
        testReceive(remoteBroker, remoteSession, localBroker, localSession, new DynamicNetworkTestSupport.ConsumerCreator() {
            @Override
            public MessageConsumer createConsumer() throws JMSException {
                return remoteSession.createDurableSubscriber(included, subName);
            }
        });
    }

    /**
     * Reverse and test publisher on remoteBroker and durable on localBroker
     * after durable deletion, recreate durable
     */
    @Test(timeout = 30 * 1000)
    public void testDurableConsumerReverse() throws Exception {
        testReceive(localBroker, localSession, remoteBroker, remoteSession, new DynamicNetworkTestSupport.ConsumerCreator() {
            @Override
            public MessageConsumer createConsumer() throws JMSException {
                return localSession.createDurableSubscriber(included, subName);
            }
        });
    }

    /**
     * Test publisher on localBroker and durable on remoteBroker
     * after durable deletion, recreate with a non-durable consumer
     */
    @Test(timeout = 30 * 1000)
    public void testDurableAndTopicConsumer() throws Exception {
        testReceive(remoteBroker, remoteSession, localBroker, localSession, new DynamicNetworkTestSupport.ConsumerCreator() {
            @Override
            public MessageConsumer createConsumer() throws JMSException {
                return remoteSession.createConsumer(included);
            }
        });
    }

    /**
     * Reverse and test publisher on remoteBroker and durable on localBroker
     * after durable deletion, recreate with a non-durable consumer
     */
    @Test(timeout = 30 * 1000)
    public void testDurableAndTopicConsumerReverse() throws Exception {
        testReceive(localBroker, localSession, remoteBroker, remoteSession, new DynamicNetworkTestSupport.ConsumerCreator() {
            @Override
            public MessageConsumer createConsumer() throws JMSException {
                return localSession.createConsumer(included);
            }
        });
    }

    protected NetworkConnector connector;
}

