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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.StubBroker;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.jaas.UserPrincipal;


public class JaasCertificateAuthenticationBrokerTest extends TestCase {
    StubBroker receiveBroker;

    JaasCertificateAuthenticationBroker authBroker;

    ConnectionContext connectionContext;

    ConnectionInfo connectionInfo;

    public void testAddConnectionSuccess() {
        String dnUserName = "dnUserName";
        HashSet<String> userNames = new HashSet<String>();
        userNames.add(dnUserName);
        HashSet<String> groupNames = new HashSet<String>();
        groupNames.add("testGroup1");
        groupNames.add("testGroup2");
        groupNames.add("tesetGroup3");
        setConfiguration(userNames, groupNames, true);
        try {
            authBroker.addConnection(connectionContext, connectionInfo);
        } catch (Exception e) {
            TestCase.fail(("Call to addConnection failed: " + (e.getMessage())));
        }
        TestCase.assertEquals(("Number of addConnection calls to underlying Broker must match number of calls made to " + "AuthenticationBroker."), 1, receiveBroker.addConnectionData.size());
        ConnectionContext receivedContext = receiveBroker.addConnectionData.getFirst().connectionContext;
        TestCase.assertEquals("The SecurityContext's userName must be set to that of the UserPrincipal.", dnUserName, receivedContext.getSecurityContext().getUserName());
        Set<Principal> receivedPrincipals = receivedContext.getSecurityContext().getPrincipals();
        for (Iterator<Principal> iter = receivedPrincipals.iterator(); iter.hasNext();) {
            Principal currentPrincipal = iter.next();
            if (currentPrincipal instanceof UserPrincipal) {
                if (userNames.remove(currentPrincipal.getName())) {
                    // Nothing, we did good.
                } else {
                    // Found an unknown userName.
                    TestCase.fail("Unknown UserPrincipal found");
                }
            } else
                if (currentPrincipal instanceof GroupPrincipal) {
                    if (groupNames.remove(currentPrincipal.getName())) {
                        // Nothing, we did good.
                    } else {
                        TestCase.fail("Unknown GroupPrincipal found.");
                    }
                } else {
                    TestCase.fail("Unexpected Principal subclass found.");
                }

        }
        if (!(userNames.isEmpty())) {
            TestCase.fail("Some usernames were not added as UserPrincipals");
        }
        if (!(groupNames.isEmpty())) {
            TestCase.fail("Some group names were not added as GroupPrincipals");
        }
    }

    public void testAddConnectionFailure() {
        HashSet<String> userNames = new HashSet<String>();
        HashSet<String> groupNames = new HashSet<String>();
        groupNames.add("testGroup1");
        groupNames.add("testGroup2");
        groupNames.add("tesetGroup3");
        setConfiguration(userNames, groupNames, false);
        boolean connectFailed = false;
        try {
            authBroker.addConnection(connectionContext, connectionInfo);
        } catch (SecurityException e) {
            connectFailed = true;
        } catch (Exception e) {
            TestCase.fail(("Failed to connect for unexpected reason: " + (e.getMessage())));
        }
        if (!connectFailed) {
            TestCase.fail("Unauthenticated connection allowed.");
        }
        TestCase.assertEquals("Unauthenticated connection allowed.", true, receiveBroker.addConnectionData.isEmpty());
    }

    public void testRemoveConnection() throws Exception {
        connectionContext.setSecurityContext(new StubSecurityContext());
        authBroker.removeConnection(connectionContext, connectionInfo, new Throwable());
        TestCase.assertEquals("removeConnection should clear ConnectionContext.", null, connectionContext.getSecurityContext());
        TestCase.assertEquals("Incorrect number of calls to underlying broker were made.", 1, receiveBroker.removeConnectionData.size());
    }
}

