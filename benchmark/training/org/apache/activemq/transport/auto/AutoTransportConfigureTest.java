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
package org.apache.activemq.transport.auto;


import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AutoTransportConfigureTest {
    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private BrokerService brokerService;

    private String url;

    private String transportType;

    public AutoTransportConfigureTest(String transportType) {
        super();
        this.transportType = transportType;
    }

    @Test(expected = JMSException.class)
    public void testUrlConfiguration() throws Exception {
        createBroker(((transportType) + "://localhost:0?wireFormat.maxFrameSize=10"));
        ConnectionFactory factory = new ActiveMQConnectionFactory(url);
        sendMessage(factory.createConnection());
    }

    @Test(expected = JMSException.class)
    public void testUrlConfigurationOpenWireFail() throws Exception {
        createBroker(((transportType) + "://localhost:0?wireFormat.default.maxFrameSize=10"));
        ConnectionFactory factory = new ActiveMQConnectionFactory(url);
        sendMessage(factory.createConnection());
    }

    @Test
    public void testUrlConfigurationOpenWireSuccess() throws Exception {
        // Will work because max frame size only applies to stomp
        createBroker(((transportType) + "://localhost:0?wireFormat.stomp.maxFrameSize=10"));
        ConnectionFactory factory = new ActiveMQConnectionFactory(url);
        sendMessage(factory.createConnection());
    }

    @Test(expected = JMSException.class)
    public void testUrlConfigurationOpenWireNotAvailable() throws Exception {
        // only stomp is available so should fail
        createBroker(((transportType) + "://localhost:0?auto.protocols=stomp"));
        ConnectionFactory factory = new ActiveMQConnectionFactory(url);
        sendMessage(factory.createConnection());
    }

    @Test
    public void testUrlConfigurationOpenWireAvailable() throws Exception {
        // only open wire is available
        createBroker(((transportType) + "://localhost:0?auto.protocols=default"));
        ConnectionFactory factory = new ActiveMQConnectionFactory(url);
        sendMessage(factory.createConnection());
    }

    @Test
    public void testUrlConfigurationOpenWireAndAmqpAvailable() throws Exception {
        createBroker(((transportType) + "://localhost:0?auto.protocols=default,stomp"));
        ConnectionFactory factory = new ActiveMQConnectionFactory(url);
        sendMessage(factory.createConnection());
    }
}

