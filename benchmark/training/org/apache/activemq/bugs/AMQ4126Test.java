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


import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.stomp.StompConnection;
import org.junit.Test;


/**
 *
 */
public class AMQ4126Test {
    protected BrokerService broker;

    protected String java_security_auth_login_config = "java.security.auth.login.config";

    protected String xbean = "xbean:";

    protected String confBase = "src/test/resources/org/apache/activemq/bugs/amq4126";

    protected String certBase = "src/test/resources/org/apache/activemq/security";

    protected String JaasStompSSLBroker_xml = "JaasStompSSLBroker.xml";

    protected StompConnection stompConnection = new StompConnection();

    private static final String destinationName = "TEST.QUEUE";

    protected String oldLoginConf = null;

    @Test
    public void testStompSSLWithUsernameAndPassword() throws Exception {
        stompConnectTo("stomp+ssl", ("login:system\n" + "passcode:manager\n"));
    }

    @Test
    public void testStompSSLWithCertificate() throws Exception {
        stompConnectTo("stomp+ssl", null);
    }

    @Test
    public void testStompNIOSSLWithUsernameAndPassword() throws Exception {
        stompConnectTo("stomp+nio+ssl", ("login:system\n" + "passcode:manager\n"));
    }

    @Test
    public void testStompNIOSSLWithCertificate() throws Exception {
        stompConnectTo("stomp+nio+ssl", null);
    }

    @Test
    public void testOpenwireSSLWithUsernameAndPassword() throws Exception {
        openwireConnectTo("openwire+ssl", "system", "manager");
    }

    @Test
    public void testOpenwireSSLWithCertificate() throws Exception {
        openwireConnectTo("openwire+ssl", null, null);
    }

    @Test
    public void testOpenwireNIOSSLWithUsernameAndPassword() throws Exception {
        openwireConnectTo("openwire+nio+ssl", "system", "mmanager");
    }

    @Test
    public void testOpenwireNIOSSLWithCertificate() throws Exception {
        openwireConnectTo("openwire+nio+ssl", null, null);
    }

    @Test
    public void testJmx() throws Exception {
        TestCase.assertFalse(findDestination(AMQ4126Test.destinationName));
        broker.getAdminView().addQueue(AMQ4126Test.destinationName);
        TestCase.assertTrue(findDestination(AMQ4126Test.destinationName));
        broker.getAdminView().removeQueue(AMQ4126Test.destinationName);
        TestCase.assertFalse(findDestination(AMQ4126Test.destinationName));
    }
}

