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
package org.apache.activemq.bugs;


import org.apache.activemq.broker.BrokerService;
import org.junit.Test;


public class AMQ4133Test {
    protected String java_security_auth_login_config = "java.security.auth.login.config";

    protected String xbean = "xbean:";

    protected String confBase = "src/test/resources/org/apache/activemq/bugs/amq4126";

    protected String certBase = "src/test/resources/org/apache/activemq/security";

    protected String activemqXml = "InconsistentConnectorPropertiesBehaviour.xml";

    protected BrokerService broker;

    protected String oldLoginConf = null;

    @Test
    public void stompSSLTransportNeedClientAuthTrue() throws Exception {
        stompConnectTo("localhost", broker.getConnectorByName("stomp+ssl").getConnectUri().getPort());
    }

    @Test
    public void stompSSLNeedClientAuthTrue() throws Exception {
        stompConnectTo("localhost", broker.getConnectorByName("stomp+ssl+special").getConnectUri().getPort());
    }

    @Test
    public void stompNIOSSLTransportNeedClientAuthTrue() throws Exception {
        stompConnectTo("localhost", broker.getConnectorByName("stomp+nio+ssl").getConnectUri().getPort());
    }

    @Test
    public void stompNIOSSLNeedClientAuthTrue() throws Exception {
        stompConnectTo("localhost", broker.getConnectorByName("stomp+nio+ssl+special").getConnectUri().getPort());
    }

    @Test
    public void mqttSSLNeedClientAuthTrue() throws Exception {
        mqttConnectTo("localhost", broker.getConnectorByName("mqtt+ssl").getConnectUri().getPort());
    }

    @Test
    public void mqttNIOSSLNeedClientAuthTrue() throws Exception {
        mqttConnectTo("localhost", broker.getConnectorByName("mqtt+nio+ssl").getConnectUri().getPort());
    }
}

