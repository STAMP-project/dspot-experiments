/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum.auth;


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import QuorumAuth.QUORUM_KERBEROS_SERVICE_PRINCIPAL;
import QuorumAuth.QUORUM_LEARNER_SASL_AUTH_REQUIRED;
import QuorumAuth.QUORUM_LEARNER_SASL_LOGIN_CONTEXT;
import QuorumAuth.QUORUM_SASL_AUTH_ENABLED;
import QuorumAuth.QUORUM_SERVER_SASL_AUTH_REQUIRED;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import junit.framework.Assert;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.quorum.QuorumPeerTestBase;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Test;


public class QuorumKerberosHostBasedAuthTest extends KerberosSecurityTestcase {
    private static File keytabFile;

    private static String hostServerPrincipal = KerberosTestUtils.getHostServerPrincipal();

    private static String hostLearnerPrincipal = KerberosTestUtils.getHostLearnerPrincipal();

    private static String hostNamedLearnerPrincipal = KerberosTestUtils.getHostNamedLearnerPrincipal("myHost");

    static {
        QuorumKerberosHostBasedAuthTest.setupJaasConfigEntries(QuorumKerberosHostBasedAuthTest.hostServerPrincipal, QuorumKerberosHostBasedAuthTest.hostLearnerPrincipal, QuorumKerberosHostBasedAuthTest.hostNamedLearnerPrincipal);
    }

    /**
     * Test to verify that server is able to start with valid credentials
     */
    @Test(timeout = 120000)
    public void testValidCredentials() throws Exception {
        String serverPrincipal = QuorumKerberosHostBasedAuthTest.hostServerPrincipal.substring(0, QuorumKerberosHostBasedAuthTest.hostServerPrincipal.lastIndexOf("@"));
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        authConfigs.put(QUORUM_SERVER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_LEARNER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_KERBEROS_SERVICE_PRINCIPAL, serverPrincipal);
        String connectStr = startQuorum(3, authConfigs, 3);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        for (int i = 0; i < 10; i++) {
            zk.create(("/" + i), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        }
        zk.close();
    }

    /**
     * Test to verify that the bad server connection to the quorum should be rejected.
     */
    @Test(timeout = 120000)
    public void testConnectBadServer() throws Exception {
        String serverPrincipal = QuorumKerberosHostBasedAuthTest.hostServerPrincipal.substring(0, QuorumKerberosHostBasedAuthTest.hostServerPrincipal.lastIndexOf("@"));
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        authConfigs.put(QUORUM_SERVER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_LEARNER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_KERBEROS_SERVICE_PRINCIPAL, serverPrincipal);
        String connectStr = startQuorum(3, authConfigs, 3);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        for (int i = 0; i < 10; i++) {
            zk.create(("/" + i), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        }
        zk.close();
        String quorumCfgSection = mt.get(0).getQuorumCfgSection();
        StringBuilder sb = new StringBuilder();
        sb.append(quorumCfgSection);
        int myid = (mt.size()) + 1;
        final int clientPort = PortAssignment.unique();
        String server = String.format("server.%d=localhost:%d:%d:participant", myid, PortAssignment.unique(), PortAssignment.unique());
        sb.append((server + "\n"));
        quorumCfgSection = sb.toString();
        authConfigs.put(QUORUM_LEARNER_SASL_LOGIN_CONTEXT, "QuorumLearnerMyHost");
        QuorumPeerTestBase.MainThread badServer = new QuorumPeerTestBase.MainThread(myid, clientPort, quorumCfgSection, authConfigs);
        badServer.start();
        watcher = new ClientBase.CountdownWatcher();
        connectStr = "127.0.0.1:" + clientPort;
        zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        try {
            watcher.waitForConnected(((ClientBase.CONNECTION_TIMEOUT) / 3));
            Assert.fail("Must throw exception as the myHost is not an authorized one!");
        } catch (TimeoutException e) {
            // expected
        } finally {
            zk.close();
            badServer.shutdown();
            badServer.deleteBaseDir();
        }
    }
}

