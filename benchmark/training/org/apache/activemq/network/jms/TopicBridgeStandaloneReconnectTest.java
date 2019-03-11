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
package org.apache.activemq.network.jms;


import java.util.ArrayList;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class TopicBridgeStandaloneReconnectTest {
    private SimpleJmsTopicConnector jmsTopicConnector;

    private BrokerService localBroker;

    private BrokerService foreignBroker;

    private ActiveMQConnectionFactory localConnectionFactory;

    private ActiveMQConnectionFactory foreignConnectionFactory;

    private Destination outbound;

    private Destination inbound;

    private final ArrayList<Connection> connections = new ArrayList<Connection>();

    @Test
    public void testSendAndReceiveOverConnectedBridges() throws Exception {
        startLocalBroker();
        startForeignBroker();
        jmsTopicConnector.start();
        final MessageConsumer local = createConsumerForLocalBroker();
        final MessageConsumer foreign = createConsumerForForeignBroker();
        sendMessageToForeignBroker("to.foreign.broker");
        sendMessageToLocalBroker("to.local.broker");
        Assert.assertTrue("Should have received a Message.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = local.receive(100);
                if ((message != null) && (getText().equals("to.local.broker"))) {
                    return true;
                }
                return false;
            }
        }));
        Assert.assertTrue("Should have received a Message.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = foreign.receive(100);
                if ((message != null) && (getText().equals("to.foreign.broker"))) {
                    return true;
                }
                return false;
            }
        }));
    }

    @Test
    public void testSendAndReceiveOverBridgeWhenStartedBeforeBrokers() throws Exception {
        jmsTopicConnector.start();
        startLocalBroker();
        startForeignBroker();
        Assert.assertTrue("Should have Connected.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return jmsTopicConnector.isConnected();
            }
        }));
        final MessageConsumer local = createConsumerForLocalBroker();
        final MessageConsumer foreign = createConsumerForForeignBroker();
        sendMessageToForeignBroker("to.foreign.broker");
        sendMessageToLocalBroker("to.local.broker");
        Assert.assertTrue("Should have received a Message.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = local.receive(100);
                if ((message != null) && (getText().equals("to.local.broker"))) {
                    return true;
                }
                return false;
            }
        }));
        Assert.assertTrue("Should have received a Message.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = foreign.receive(100);
                if ((message != null) && (getText().equals("to.foreign.broker"))) {
                    return true;
                }
                return false;
            }
        }));
    }

    @Test
    public void testSendAndReceiveOverBridgeWithRestart() throws Exception {
        startLocalBroker();
        startForeignBroker();
        jmsTopicConnector.start();
        Assert.assertTrue("Should have Connected.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return jmsTopicConnector.isConnected();
            }
        }));
        stopLocalBroker();
        stopForeignBroker();
        Assert.assertTrue("Should have detected connection drop.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(jmsTopicConnector.isConnected());
            }
        }));
        startLocalBroker();
        startForeignBroker();
        Assert.assertTrue("Should have Re-Connected.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return jmsTopicConnector.isConnected();
            }
        }));
        final MessageConsumer local = createConsumerForLocalBroker();
        final MessageConsumer foreign = createConsumerForForeignBroker();
        sendMessageToForeignBroker("to.foreign.broker");
        sendMessageToLocalBroker("to.local.broker");
        Assert.assertTrue("Should have received a Message.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = local.receive(100);
                if ((message != null) && (getText().equals("to.local.broker"))) {
                    return true;
                }
                return false;
            }
        }));
        Assert.assertTrue("Should have received a Message.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = foreign.receive(100);
                if ((message != null) && (getText().equals("to.foreign.broker"))) {
                    return true;
                }
                return false;
            }
        }));
    }
}

