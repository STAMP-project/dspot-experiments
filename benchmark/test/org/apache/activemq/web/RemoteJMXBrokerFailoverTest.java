/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.web;


import java.util.LinkedList;
import javax.management.remote.JMXConnectorServer;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.web.config.SystemPropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class RemoteJMXBrokerFailoverTest {
    private BrokerService master;

    private BrokerService slave;

    private LinkedList<JMXConnectorServer> serverList = new LinkedList<JMXConnectorServer>();

    @Test
    public void testConnectToMasterFailover() throws Exception {
        String jmxUri = "";
        for (JMXConnectorServer jmxConnectorServer : serverList) {
            if (!(jmxUri.isEmpty())) {
                jmxUri += ',';
            }
            jmxUri += jmxConnectorServer.getAddress().toString();
        }
        System.out.println(("jmx url: " + jmxUri));
        System.setProperty("webconsole.jmx.url", jmxUri);
        RemoteJMXBrokerFacade brokerFacade = new RemoteJMXBrokerFacade();
        SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
        brokerFacade.setConfiguration(configuration);
        Assert.assertEquals("connected to master", master.getBrokerName(), brokerFacade.getBrokerName());
        stopAndRestartMaster();
        Assert.assertEquals("connected to slave", slave.getBrokerName(), brokerFacade.getBrokerName());
    }
}

