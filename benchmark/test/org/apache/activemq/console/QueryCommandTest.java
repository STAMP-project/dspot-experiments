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
package org.apache.activemq.console;


import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryCommandTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueryCommandTest.class);

    final String CONNECTOR_NAME = "tcp-openWire";

    final String CLIENT_ID = "some-id";

    BrokerService brokerService;

    @Test
    public void tryQuery() throws Exception {
        String result = executeQuery("-QQueue=* --view destinationName,EnqueueCount,DequeueCount");
        Assert.assertTrue("Output valid", result.contains("Q1"));
        Assert.assertTrue("Output valid", result.contains("Q2"));
        Assert.assertFalse("Output valid", result.contains("T1"));
        result = executeQuery("-QQueue=Q2 --view destinationName,QueueSize");
        Assert.assertTrue("size present", result.contains("QueueSize"));
        Assert.assertTrue("Output valid", result.contains("Q2"));
        Assert.assertFalse("Output valid", result.contains("Q1"));
        Assert.assertFalse("Output valid", result.contains("T1"));
        result = executeQuery("-QQueue=* -xQQueue=Q1 --view destinationName,QueueSize");
        Assert.assertTrue("size present", result.contains("QueueSize"));
        Assert.assertTrue("q2", result.contains("Q2"));
        Assert.assertFalse(("!q1: " + result), result.contains("Q1"));
        Assert.assertFalse("!t1", result.contains("T1"));
        result = executeQuery("-QTopic=* -QQueue=* --view destinationName");
        Assert.assertTrue("got Q1", result.contains("Q1"));
        Assert.assertTrue("got Q2", result.contains("Q2"));
        Assert.assertTrue("got T1", result.contains("T1"));
        result = executeQuery("-QQueue=*");
        Assert.assertTrue("got Q1", result.contains("Q1"));
        Assert.assertTrue("got Q2", result.contains("Q2"));
        Assert.assertFalse("!T1", result.contains("T1"));
        result = executeQuery("-QBroker=*");
        Assert.assertTrue("got localhost", result.contains("localhost"));
        result = executeQuery("--view destinationName");
        // all mbeans with a destinationName attribute
        Assert.assertTrue("got Q1", result.contains("Q1"));
        Assert.assertTrue("got Q2", result.contains("Q2"));
        Assert.assertTrue("got T1", result.contains("T1"));
        result = executeQuery("--objname type=Broker,brokerName=*,destinationType=Queue,destinationName=*");
        Assert.assertTrue("got Q1", result.contains("Q1"));
        Assert.assertTrue("got Q2", result.contains("Q2"));
        Assert.assertFalse("!T1", result.contains("T1"));
        result = executeQuery("--objname type=Broker,brokerName=*,destinationType=*,destinationName=* --xobjname type=Broker,brokerName=*,destinationType=Queue,destinationName=Q1");
        Assert.assertFalse("!Q1", result.contains("Q1"));
        Assert.assertTrue("got Q2", result.contains("Q2"));
        Assert.assertTrue("T1", result.contains("T1"));
    }

    @Test
    public void testConnection() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerService.getTransportConnectors().get(0).getPublishableConnectURI());
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(CLIENT_ID);
        connection.start();
        String result = executeQuery("-QConnection=* --view ClientId");
        Assert.assertTrue("got client id", result.contains(CLIENT_ID));
        result = executeQuery("--objname type=Broker,brokerName=*,connector=clientConnectors,connectorName=* -xQNetworkConnector=*");
        Assert.assertTrue("got named", result.contains(CONNECTOR_NAME));
        result = executeQuery("-QConnector=*");
        Assert.assertTrue("got named", result.contains(CONNECTOR_NAME));
    }

    @Test
    public void testInvoke() throws Exception {
        String result = executeQuery("-QQueue=Q* --view Paused");
        Assert.assertTrue("got pause status", result.contains("Paused = false"));
        result = executeQuery("-QQueue=* --invoke pause");
        QueryCommandTest.LOG.info(("result of invoke: " + result));
        Assert.assertTrue("invoked", result.contains("Q1"));
        Assert.assertTrue("invoked", result.contains("Q2"));
        result = executeQuery("-QQueue=Q2 --view Paused");
        Assert.assertTrue("got pause status", result.contains("Paused = true"));
        result = executeQuery("-QQueue=Q2 --invoke resume");
        QueryCommandTest.LOG.info(("result of invoke: " + result));
        Assert.assertTrue("invoked", result.contains("Q2"));
        result = executeQuery("-QQueue=Q2 --view Paused");
        Assert.assertTrue("pause status", result.contains("Paused = false"));
        result = executeQuery("-QQueue=Q1 --view Paused");
        Assert.assertTrue("pause status", result.contains("Paused = true"));
        // op with string param
        result = executeQuery("-QQueue=Q2 --invoke sendTextMessage,hi");
        QueryCommandTest.LOG.info(("result of invoke: " + result));
        Assert.assertTrue("invoked", result.contains("Q2"));
        result = executeQuery("-QQueue=Q2 --view EnqueueCount");
        Assert.assertTrue("enqueueCount", result.contains("EnqueueCount = 1"));
    }
}

