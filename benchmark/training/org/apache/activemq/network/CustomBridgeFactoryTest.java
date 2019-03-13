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


import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.Message;
import org.apache.activemq.transport.Transport;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Basic test which verify if custom bridge factory receives any interactions when configured.
 */
public class CustomBridgeFactoryTest extends BaseNetworkTest {
    private ActiveMQQueue outgoing = new ActiveMQQueue("outgoing");

    /**
     * Verification of outgoing communication - from local broker (with customized bridge configured) to remote one.
     */
    @Test
    public void verifyOutgoingCommunication() throws JMSException {
        CustomBridgeFactoryTest.CustomNetworkBridgeFactory bridgeFactory = getCustomNetworkBridgeFactory();
        NetworkBridgeListener listener = bridgeFactory.getListener();
        Mockito.verify(listener).onStart(ArgumentMatchers.any(NetworkBridge.class));
        Mockito.verifyNoMoreInteractions(listener);
        send(localSession, outgoing, localSession.createTextMessage("test message"));
        Assert.assertNotNull("Message didn't arrive", receive(remoteSession, outgoing));
        Mockito.verify(listener).onOutboundMessage(ArgumentMatchers.any(NetworkBridge.class), ArgumentMatchers.any(Message.class));
        Mockito.verifyNoMoreInteractions(listener);
    }

    /**
     * Additional test which makes sure that custom bridge receives notification about broker shutdown.
     */
    @Test
    public void verifyBrokerShutdown() {
        shutdownTest(() -> {
            try {
                localBroker.stop();
            } catch (Exception e) {
                return e;
            }
            return null;
        });
    }

    /**
     * Verification of network connector shutdown.
     */
    @Test
    public void verifyConnectorShutdown() {
        shutdownTest(() -> {
            try {
                getLocalConnector(0).stop();
            } catch (Exception e) {
                return e;
            }
            return null;
        });
    }

    // test classes
    static class CustomNetworkBridgeFactory implements BridgeFactory {
        private final NetworkBridgeListener listener;

        CustomNetworkBridgeFactory() {
            this(Mockito.mock(NetworkBridgeListener.class));
        }

        CustomNetworkBridgeFactory(NetworkBridgeListener listener) {
            this.listener = listener;
        }

        public NetworkBridgeListener getListener() {
            return listener;
        }

        @Override
        public DemandForwardingBridge createNetworkBridge(NetworkBridgeConfiguration configuration, Transport localTransport, Transport remoteTransport, NetworkBridgeListener listener) {
            DemandForwardingBridge bridge = new DemandForwardingBridge(configuration, localTransport, remoteTransport);
            bridge.setNetworkBridgeListener(new CustomBridgeFactoryTest.CompositeNetworkBridgeListener(this.listener, listener));
            return bridge;
        }
    }

    static class CompositeNetworkBridgeListener implements NetworkBridgeListener {
        private final List<NetworkBridgeListener> listeners;

        public CompositeNetworkBridgeListener(NetworkBridgeListener... wrapped) {
            this.listeners = Arrays.asList(wrapped);
        }

        @Override
        public void bridgeFailed() {
            for (NetworkBridgeListener listener : listeners) {
                listener.bridgeFailed();
            }
        }

        @Override
        public void onStart(NetworkBridge bridge) {
            for (NetworkBridgeListener listener : listeners) {
                listener.onStart(bridge);
            }
        }

        @Override
        public void onStop(NetworkBridge bridge) {
            for (NetworkBridgeListener listener : listeners) {
                listener.onStop(bridge);
            }
        }

        @Override
        public void onOutboundMessage(NetworkBridge bridge, Message message) {
            for (NetworkBridgeListener listener : listeners) {
                listener.onOutboundMessage(bridge, message);
            }
        }

        @Override
        public void onInboundMessage(NetworkBridge bridge, Message message) {
            for (NetworkBridgeListener listener : listeners) {
                listener.onInboundMessage(bridge, message);
            }
        }
    }
}

