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
package org.apache.activemq.security;


import java.security.Principal;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Connector;
import org.apache.activemq.broker.StubBroker;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.jaas.UserPrincipal;
import org.apache.activemq.transport.tcp.SslTransportServer;
import org.apache.activemq.transport.tcp.StubX509Certificate;
import org.apache.activemq.transport.tcp.TcpTransportServer;


/**
 *
 */
public class JaasDualAuthenticationBrokerTest extends TestCase {
    private static final String INSECURE_GROUP = "insecureGroup";

    private static final String INSECURE_USERNAME = "insecureUserName";

    private static final String DN_GROUP = "dnGroup";

    private static final String DN_USERNAME = "dnUserName";

    StubBroker receiveBroker;

    JaasDualAuthenticationBroker authBroker;

    ConnectionContext connectionContext;

    ConnectionInfo connectionInfo;

    SslTransportServer sslTransportServer;

    TcpTransportServer nonSslTransportServer;

    public void testSecureConnector() {
        Connector connector = new org.apache.activemq.broker.TransportConnector(sslTransportServer);
        connectionContext.setConnector(connector);
        connectionInfo.setTransportContext(new StubX509Certificate[]{  });
        try {
            authBroker.addConnection(connectionContext, connectionInfo);
        } catch (Exception e) {
            TestCase.fail(("Call to addConnection failed: " + (e.getMessage())));
        }
        TestCase.assertEquals(("Number of addConnection calls to underlying Broker must match number of calls made to " + "AuthenticationBroker."), 1, receiveBroker.addConnectionData.size());
        ConnectionContext receivedContext = receiveBroker.addConnectionData.getFirst().connectionContext;
        TestCase.assertEquals("The SecurityContext's userName must be set to that of the UserPrincipal.", JaasDualAuthenticationBrokerTest.DN_USERNAME, receivedContext.getSecurityContext().getUserName());
        Set<Principal> receivedPrincipals = receivedContext.getSecurityContext().getPrincipals();
        TestCase.assertEquals("2 Principals received", 2, receivedPrincipals.size());
        for (Iterator<Principal> iter = receivedPrincipals.iterator(); iter.hasNext();) {
            Principal currentPrincipal = iter.next();
            if (currentPrincipal instanceof UserPrincipal) {
                TestCase.assertEquals((("UserPrincipal is '" + (JaasDualAuthenticationBrokerTest.DN_USERNAME)) + "'"), JaasDualAuthenticationBrokerTest.DN_USERNAME, currentPrincipal.getName());
            } else
                if (currentPrincipal instanceof GroupPrincipal) {
                    TestCase.assertEquals((("GroupPrincipal is '" + (JaasDualAuthenticationBrokerTest.DN_GROUP)) + "'"), JaasDualAuthenticationBrokerTest.DN_GROUP, currentPrincipal.getName());
                } else {
                    TestCase.fail("Unexpected Principal subclass found.");
                }

        }
        try {
            authBroker.removeConnection(connectionContext, connectionInfo, null);
        } catch (Exception e) {
            TestCase.fail(("Call to removeConnection failed: " + (e.getMessage())));
        }
        TestCase.assertEquals(("Number of removeConnection calls to underlying Broker must match number of calls made to " + "AuthenticationBroker."), 1, receiveBroker.removeConnectionData.size());
    }

    public void testInsecureConnector() {
        Connector connector = new org.apache.activemq.broker.TransportConnector(nonSslTransportServer);
        connectionContext.setConnector(connector);
        connectionInfo.setUserName(JaasDualAuthenticationBrokerTest.INSECURE_USERNAME);
        try {
            authBroker.addConnection(connectionContext, connectionInfo);
        } catch (Exception e) {
            TestCase.fail(("Call to addConnection failed: " + (e.getMessage())));
        }
        TestCase.assertEquals(("Number of addConnection calls to underlying Broker must match number of calls made to " + "AuthenticationBroker."), 1, receiveBroker.addConnectionData.size());
        ConnectionContext receivedContext = receiveBroker.addConnectionData.getFirst().connectionContext;
        TestCase.assertEquals("The SecurityContext's userName must be set to that of the UserPrincipal.", JaasDualAuthenticationBrokerTest.INSECURE_USERNAME, receivedContext.getSecurityContext().getUserName());
        Set<Principal> receivedPrincipals = receivedContext.getSecurityContext().getPrincipals();
        TestCase.assertEquals("2 Principals received", 2, receivedPrincipals.size());
        for (Iterator<Principal> iter = receivedPrincipals.iterator(); iter.hasNext();) {
            Principal currentPrincipal = iter.next();
            if (currentPrincipal instanceof UserPrincipal) {
                TestCase.assertEquals((("UserPrincipal is '" + (JaasDualAuthenticationBrokerTest.INSECURE_USERNAME)) + "'"), JaasDualAuthenticationBrokerTest.INSECURE_USERNAME, currentPrincipal.getName());
            } else
                if (currentPrincipal instanceof GroupPrincipal) {
                    TestCase.assertEquals((("GroupPrincipal is '" + (JaasDualAuthenticationBrokerTest.INSECURE_GROUP)) + "'"), JaasDualAuthenticationBrokerTest.INSECURE_GROUP, currentPrincipal.getName());
                } else {
                    TestCase.fail("Unexpected Principal subclass found.");
                }

        }
        try {
            authBroker.removeConnection(connectionContext, connectionInfo, null);
        } catch (Exception e) {
            TestCase.fail(("Call to removeConnection failed: " + (e.getMessage())));
        }
        TestCase.assertEquals(("Number of removeConnection calls to underlying Broker must match number of calls made to " + "AuthenticationBroker."), 1, receiveBroker.removeConnectionData.size());
    }
}

