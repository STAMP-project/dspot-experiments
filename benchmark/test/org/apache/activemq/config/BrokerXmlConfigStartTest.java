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
package org.apache.activemq.config;


import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.util.URISupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class BrokerXmlConfigStartTest {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerXmlConfigStartTest.class);

    Properties secProps;

    private String configUrl;

    private String shortName;

    public BrokerXmlConfigStartTest(String config, String configFileShortName) {
        this.configUrl = config;
        this.shortName = configFileShortName;
    }

    @Test
    public void testStartBrokerUsingXmlConfig1() throws Exception {
        BrokerService broker = null;
        BrokerXmlConfigStartTest.LOG.info(("Broker config: " + (configUrl)));
        System.err.println(("Broker config: " + (configUrl)));
        broker = BrokerFactory.createBroker(configUrl);
        if ("activemq-leveldb-replicating.xml".equals(shortName)) {
            try {
                broker.start();
            } catch (TimeoutException expectedWithNoZk) {
                return;
            }
        } else {
            broker.start();
        }
        // alive, now try connect to connect
        try {
            for (TransportConnector transport : broker.getTransportConnectors()) {
                final URI UriToConnectTo = URISupport.removeQuery(transport.getConnectUri());
                if (UriToConnectTo.getScheme().startsWith("stomp")) {
                    BrokerXmlConfigStartTest.LOG.info(("validating alive with connection to: " + UriToConnectTo));
                    StompConnection connection = new StompConnection();
                    connection.open(UriToConnectTo.getHost(), UriToConnectTo.getPort());
                    connection.close();
                    break;
                } else
                    if (UriToConnectTo.getScheme().startsWith("tcp")) {
                        BrokerXmlConfigStartTest.LOG.info(("validating alive with connection to: " + UriToConnectTo));
                        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(UriToConnectTo);
                        Connection connection = connectionFactory.createConnection(secProps.getProperty("activemq.username"), secProps.getProperty("activemq.password"));
                        connection.start();
                        connection.close();
                        break;
                    } else {
                        BrokerXmlConfigStartTest.LOG.info(("not validating connection to: " + UriToConnectTo));
                    }

            }
        } finally {
            if (broker != null) {
                broker.stop();
                broker.waitUntilStopped();
                broker = null;
            }
        }
    }
}

