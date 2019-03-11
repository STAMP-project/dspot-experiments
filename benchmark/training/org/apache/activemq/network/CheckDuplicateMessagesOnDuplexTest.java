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


import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.net.ServerSocketFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.Response;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportFilter;
import org.apache.activemq.transport.nio.NIOTransportFactory;
import org.apache.activemq.transport.tcp.TcpTransportFactory;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import org.apache.activemq.wireformat.WireFormat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author x22koe
 */
public class CheckDuplicateMessagesOnDuplexTest {
    private static final Logger log = LoggerFactory.getLogger(CheckDuplicateMessagesOnDuplexTest.class);

    private BrokerService localBroker;

    private BrokerService remoteBroker;

    private ActiveMQConnectionFactory localFactory;

    private ActiveMQConnectionFactory remoteFactory;

    private Session localSession;

    private MessageConsumer consumer;

    private Session remoteSession;

    private MessageProducer producer;

    private Connection remoteConnection;

    private Connection localConnection;

    private CheckDuplicateMessagesOnDuplexTest.DebugTransportFilter debugTransportFilter;

    private boolean useLevelDB = false;

    public CheckDuplicateMessagesOnDuplexTest() {
    }

    @Test
    public void testConnectionLossBehaviorBeforeAckIsSent() throws Exception {
        createBrokers();
        localBroker.deleteAllMessages();
        remoteBroker.deleteAllMessages();
        startBrokers();
        openConnections();
        Thread.sleep(1000);
        CheckDuplicateMessagesOnDuplexTest.log.info("\n\n==============================================\nsend hello1\n");
        // simulate network failure between REMOTE and LOCAL just before the reception response is sent back to REMOTE
        debugTransportFilter.closeOnResponse = true;
        producer.send(remoteSession.createTextMessage("hello1"));
        Message msg = consumer.receive(30000);
        Assert.assertNotNull("expected hello1", msg);
        Assert.assertEquals("hello1", getText());
        Thread.sleep(1000);
        CheckDuplicateMessagesOnDuplexTest.log.info("\n\n------------------------------------------\nsend hello2\n");
        producer.send(remoteSession.createTextMessage("hello2"));
        msg = consumer.receive(30000);
        Assert.assertNotNull("expected hello2", msg);
        Assert.assertEquals("hello2", getText());
        closeLocalConnection();
        Thread.sleep(1000);
        CheckDuplicateMessagesOnDuplexTest.log.info("\n\n------------------------------------------\nsend hello3\n");
        openLocalConnection();
        Thread.sleep(1000);
        producer.send(remoteSession.createTextMessage("hello3"));
        msg = consumer.receive(30000);
        Assert.assertNotNull("expected hello3", msg);
        Assert.assertEquals("hello3", getText());
        Thread.sleep(1000);
        CheckDuplicateMessagesOnDuplexTest.log.info("\n\n==============================================\n\n");
        closeConnections();
        stopBrokers();
        // restart the local broker, which should be empty
        Thread.sleep(1000);
        CheckDuplicateMessagesOnDuplexTest.log.info("\n\n##############################################\n\n");
        createLocalBroker();
        startLocalBroker();
        openLocalConnection();
        // this should not return the "hello1" message
        msg = consumer.receive(1000);
        closeLocalConnection();
        stopLocalBroker();
        Assert.assertNull(msg);
    }

    private class DebugTransportFactory extends NIOTransportFactory {
        @Override
        protected TcpTransportServer createTcpTransportServer(URI location, ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
            return new CheckDuplicateMessagesOnDuplexTest.DebugTransportServer(this, location, serverSocketFactory);
        }
    }

    private class DebugTransportServer extends TcpTransportServer {
        public DebugTransportServer(TcpTransportFactory transportFactory, URI location, ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
            super(transportFactory, location, serverSocketFactory);
        }

        @Override
        protected Transport createTransport(Socket socket, WireFormat format) throws IOException {
            Transport transport;
            transport = new org.apache.activemq.transport.nio.NIOTransport(format, socket);
            debugTransportFilter = new CheckDuplicateMessagesOnDuplexTest.DebugTransportFilter(transport);
            return debugTransportFilter;
        }
    }

    private class DebugTransportFilter extends TransportFilter {
        boolean closeOnResponse = false;

        public DebugTransportFilter(Transport next) {
            super(next);
        }

        @Override
        public void oneway(Object command) throws IOException {
            if ((closeOnResponse) && (command instanceof Response)) {
                closeOnResponse = false;
                CheckDuplicateMessagesOnDuplexTest.log.warn("\n\nclosing connection before response is sent\n\n");
                try {
                    stop();
                } catch (Exception ex) {
                    CheckDuplicateMessagesOnDuplexTest.log.error("couldn't stop niotransport", ex);
                }
                // don't send response
                return;
            }
            super.oneway(command);
        }
    }
}

