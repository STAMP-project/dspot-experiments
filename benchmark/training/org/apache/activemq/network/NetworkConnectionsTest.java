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


import Session.AUTO_ACKNOWLEDGE;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkConnectionsTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkConnectionsTest.class);

    private static final String LOCAL_BROKER_TRANSPORT_URI = "tcp://localhost:61616";

    private static final String REMOTE_BROKER_TRANSPORT_URI = "tcp://localhost:61617";

    private static final String DESTINATION_NAME = "TEST.RECONNECT";

    private BrokerService localBroker;

    private BrokerService remoteBroker;

    @Test
    public void testIsStarted() throws Exception {
        NetworkConnectionsTest.LOG.info("testIsStarted is starting...");
        NetworkConnectionsTest.LOG.info("Adding network connector...");
        NetworkConnector nc = localBroker.addNetworkConnector((("static:(" + (NetworkConnectionsTest.REMOTE_BROKER_TRANSPORT_URI)) + ")"));
        nc.setName("NC1");
        NetworkConnectionsTest.LOG.info("Starting network connector...");
        nc.start();
        TestCase.assertTrue(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Stopping network connector...");
        nc.stop();
        while (nc.isStopping()) {
            NetworkConnectionsTest.LOG.info("... still stopping ...");
            Thread.sleep(100);
        } 
        TestCase.assertTrue(nc.isStopped());
        TestCase.assertFalse(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Starting network connector...");
        nc.start();
        TestCase.assertTrue(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Stopping network connector...");
        nc.stop();
        while (nc.isStopping()) {
            NetworkConnectionsTest.LOG.info("... still stopping ...");
            Thread.sleep(100);
        } 
        TestCase.assertTrue(nc.isStopped());
        TestCase.assertFalse(nc.isStarted());
    }

    @Test
    public void testNetworkConnectionRestart() throws Exception {
        NetworkConnectionsTest.LOG.info("testNetworkConnectionRestart is starting...");
        NetworkConnectionsTest.LOG.info("Adding network connector...");
        NetworkConnector nc = localBroker.addNetworkConnector((("static:(" + (NetworkConnectionsTest.REMOTE_BROKER_TRANSPORT_URI)) + ")"));
        nc.setName("NC1");
        nc.start();
        TestCase.assertTrue(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Setting up Message Producer and Consumer");
        ActiveMQQueue destination = new ActiveMQQueue(NetworkConnectionsTest.DESTINATION_NAME);
        ActiveMQConnectionFactory localFactory = new ActiveMQConnectionFactory(NetworkConnectionsTest.LOCAL_BROKER_TRANSPORT_URI);
        Connection localConnection = localFactory.createConnection();
        localConnection.start();
        Session localSession = localConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer localProducer = localSession.createProducer(destination);
        ActiveMQConnectionFactory remoteFactory = new ActiveMQConnectionFactory(NetworkConnectionsTest.REMOTE_BROKER_TRANSPORT_URI);
        Connection remoteConnection = remoteFactory.createConnection();
        remoteConnection.start();
        Session remoteSession = remoteConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer remoteConsumer = remoteSession.createConsumer(destination);
        Message message = localSession.createTextMessage("test");
        localProducer.send(message);
        NetworkConnectionsTest.LOG.info("Testing initial network connection...");
        message = remoteConsumer.receive(10000);
        TestCase.assertNotNull(message);
        NetworkConnectionsTest.LOG.info("Stopping network connection...");
        nc.stop();
        TestCase.assertFalse(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Sending 2nd message...");
        message = localSession.createTextMessage("test stop");
        localProducer.send(message);
        message = remoteConsumer.receive(1000);
        TestCase.assertNull("Message should not have been delivered since NetworkConnector was stopped", message);
        NetworkConnectionsTest.LOG.info("(Re)starting network connection...");
        nc.start();
        TestCase.assertTrue(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Wait for 2nd message to get forwarded and received...");
        message = remoteConsumer.receive(10000);
        TestCase.assertNotNull("Should have received 2nd message", message);
    }

    @Test
    public void testNetworkConnectionReAddURI() throws Exception {
        NetworkConnectionsTest.LOG.info("testNetworkConnectionReAddURI is starting...");
        NetworkConnectionsTest.LOG.info("Adding network connector 'NC1'...");
        NetworkConnector nc = localBroker.addNetworkConnector((("static:(" + (NetworkConnectionsTest.REMOTE_BROKER_TRANSPORT_URI)) + ")"));
        nc.setName("NC1");
        nc.start();
        TestCase.assertTrue(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Looking up network connector by name...");
        NetworkConnector nc1 = localBroker.getNetworkConnectorByName("NC1");
        TestCase.assertNotNull("Should find network connector 'NC1'", nc1);
        TestCase.assertTrue(nc1.isStarted());
        TestCase.assertEquals(nc, nc1);
        NetworkConnectionsTest.LOG.info("Setting up producer and consumer...");
        ActiveMQQueue destination = new ActiveMQQueue(NetworkConnectionsTest.DESTINATION_NAME);
        ActiveMQConnectionFactory localFactory = new ActiveMQConnectionFactory(NetworkConnectionsTest.LOCAL_BROKER_TRANSPORT_URI);
        Connection localConnection = localFactory.createConnection();
        localConnection.start();
        Session localSession = localConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer localProducer = localSession.createProducer(destination);
        ActiveMQConnectionFactory remoteFactory = new ActiveMQConnectionFactory(NetworkConnectionsTest.REMOTE_BROKER_TRANSPORT_URI);
        Connection remoteConnection = remoteFactory.createConnection();
        remoteConnection.start();
        Session remoteSession = remoteConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer remoteConsumer = remoteSession.createConsumer(destination);
        Message message = localSession.createTextMessage("test");
        localProducer.send(message);
        NetworkConnectionsTest.LOG.info("Testing initial network connection...");
        message = remoteConsumer.receive(10000);
        TestCase.assertNotNull(message);
        NetworkConnectionsTest.LOG.info("Stopping network connector 'NC1'...");
        nc.stop();
        TestCase.assertFalse(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Removing network connector...");
        TestCase.assertTrue(localBroker.removeNetworkConnector(nc));
        nc1 = localBroker.getNetworkConnectorByName("NC1");
        TestCase.assertNull("Should not find network connector 'NC1'", nc1);
        NetworkConnectionsTest.LOG.info("Re-adding network connector 'NC2'...");
        nc = localBroker.addNetworkConnector((("static:(" + (NetworkConnectionsTest.REMOTE_BROKER_TRANSPORT_URI)) + ")"));
        nc.setName("NC2");
        nc.start();
        TestCase.assertTrue(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Looking up network connector by name...");
        NetworkConnector nc2 = localBroker.getNetworkConnectorByName("NC2");
        TestCase.assertNotNull(nc2);
        TestCase.assertTrue(nc2.isStarted());
        TestCase.assertEquals(nc, nc2);
        NetworkConnectionsTest.LOG.info("Testing re-added network connection...");
        message = localSession.createTextMessage("test");
        localProducer.send(message);
        message = remoteConsumer.receive(10000);
        TestCase.assertNotNull(message);
        NetworkConnectionsTest.LOG.info("Stopping network connector...");
        nc.stop();
        TestCase.assertFalse(nc.isStarted());
        NetworkConnectionsTest.LOG.info("Removing network connection 'NC2'");
        TestCase.assertTrue(localBroker.removeNetworkConnector(nc));
        nc2 = localBroker.getNetworkConnectorByName("NC2");
        TestCase.assertNull("Should not find network connector 'NC2'", nc2);
    }
}

