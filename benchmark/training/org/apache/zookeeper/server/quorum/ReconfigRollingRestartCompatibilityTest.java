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
package org.apache.zookeeper.server.quorum;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * ReconfigRollingRestartCompatibilityTest - we want to make sure that users
 * can continue using the rolling restart approach when reconfig feature is disabled.
 * It is important to stay compatible with rolling restart because dynamic reconfig
 * has its limitation: it requires a quorum of server to work. When no quorum can be formed,
 * rolling restart is the only approach to reconfigure the ensemble (e.g. removing bad nodes
 * such that a new quorum with smaller number of nodes can be formed.).
 *
 * See ZOOKEEPER-2819 for more details.
 */
public class ReconfigRollingRestartCompatibilityTest extends QuorumPeerTestBase {
    private static final String ZOO_CFG_BAK_FILE = "zoo.cfg.bak";

    Map<Integer, Integer> clientPorts = new HashMap<>(5);

    Map<Integer, String> serverAddress = new HashMap<>(5);

    // Verify no zoo.cfg.dynamic and zoo.cfg.bak files existing locally
    // when reconfig feature flag is off by default.
    @Test(timeout = 60000)
    public void testNoLocalDynamicConfigAndBackupFiles() throws IOException, InterruptedException {
        int serverCount = 3;
        String config = generateNewQuorumConfig(serverCount);
        QuorumPeerTestBase.MainThread[] mt = new QuorumPeerTestBase.MainThread[serverCount];
        String[] staticFileContent = new String[serverCount];
        for (int i = 0; i < serverCount; i++) {
            mt[i] = new QuorumPeerTestBase.MainThread(i, clientPorts.get(i), config, false);
            mt[i].start();
        }
        for (int i = 0; i < serverCount; i++) {
            Assert.assertTrue((("waiting for server " + i) + " being up"), ClientBase.waitForServerUp(("127.0.0.1:" + (clientPorts.get(i))), ClientBase.CONNECTION_TIMEOUT));
            Assert.assertNull("static file backup (zoo.cfg.bak) shouldn't exist!", mt[i].getFileByName(ReconfigRollingRestartCompatibilityTest.ZOO_CFG_BAK_FILE));
            Assert.assertNull("dynamic configuration file (zoo.cfg.dynamic.*) shouldn't exist!", mt[i].getFileByName(mt[i].getQuorumPeer().getNextDynamicConfigFilename()));
            staticFileContent[i] = Files.readAllLines(mt[i].confFile.toPath(), StandardCharsets.UTF_8).toString();
            Assert.assertTrue(("static config file should contain server entry " + (serverAddress.get(i))), staticFileContent[i].contains(serverAddress.get(i)));
        }
        for (int i = 0; i < serverCount; i++) {
            mt[i].shutdown();
        }
    }

    // This test simulate the usual rolling restart with no membership change:
    // 1. A node is shutdown first (e.g. to upgrade software, or hardware, or cleanup local data.).
    // 2. After upgrade, start the node.
    // 3. Do this for every node, one at a time.
    @Test(timeout = 60000)
    public void testRollingRestartWithoutMembershipChange() throws Exception {
        int serverCount = 3;
        String config = generateNewQuorumConfig(serverCount);
        List<String> joiningServers = new ArrayList<>();
        QuorumPeerTestBase.MainThread[] mt = new QuorumPeerTestBase.MainThread[serverCount];
        for (int i = 0; i < serverCount; ++i) {
            mt[i] = new QuorumPeerTestBase.MainThread(i, clientPorts.get(i), config, false);
            mt[i].start();
            joiningServers.add(serverAddress.get(i));
        }
        for (int i = 0; i < serverCount; ++i) {
            Assert.assertTrue((("waiting for server " + i) + " being up"), ClientBase.waitForServerUp(("127.0.0.1:" + (clientPorts.get(i))), ClientBase.CONNECTION_TIMEOUT));
        }
        for (int i = 0; i < serverCount; ++i) {
            mt[i].shutdown();
            mt[i].start();
            verifyQuorumConfig(i, joiningServers, null);
            verifyQuorumMembers(mt[i]);
        }
        for (int i = 0; i < serverCount; i++) {
            mt[i].shutdown();
        }
    }

    // This test simulate the use case of change of membership through rolling
    // restart. For a 3 node ensemble we expand it to a 5 node ensemble, verify
    // during the process each node has the expected configuration setting pushed
    // via updating local zoo.cfg file.
    @Test(timeout = 90000)
    public void testRollingRestartWithMembershipChange() throws Exception {
        int serverCount = 3;
        String config = generateNewQuorumConfig(serverCount);
        QuorumPeerTestBase.MainThread[] mt = new QuorumPeerTestBase.MainThread[serverCount];
        List<String> joiningServers = new ArrayList<>();
        for (int i = 0; i < serverCount; ++i) {
            mt[i] = new QuorumPeerTestBase.MainThread(i, clientPorts.get(i), config, false);
            mt[i].start();
            joiningServers.add(serverAddress.get(i));
        }
        for (int i = 0; i < serverCount; ++i) {
            Assert.assertTrue((("waiting for server " + i) + " being up"), ClientBase.waitForServerUp(("127.0.0.1:" + (clientPorts.get(i))), ClientBase.CONNECTION_TIMEOUT));
        }
        for (int i = 0; i < serverCount; ++i) {
            verifyQuorumConfig(i, joiningServers, null);
            verifyQuorumMembers(mt[i]);
        }
        Map<Integer, String> oldServerAddress = new HashMap<>(serverAddress);
        List<String> newServers = new ArrayList<>(joiningServers);
        config = updateExistingQuorumConfig(Arrays.asList(3, 4), new ArrayList<Integer>());
        newServers.add(serverAddress.get(3));
        newServers.add(serverAddress.get(4));
        serverCount = serverAddress.size();
        Assert.assertEquals("Server count should be 5 after config update.", serverCount, 5);
        // We are adding two new servers to the ensemble. These two servers should have the config which includes
        // all five servers (the old three servers, plus the two servers added). The old three servers should only
        // have the old three server config, because disabling reconfig will prevent synchronizing configs between
        // peers.
        mt = Arrays.copyOf(mt, ((mt.length) + 2));
        for (int i = 3; i < 5; ++i) {
            mt[i] = new QuorumPeerTestBase.MainThread(i, clientPorts.get(i), config, false);
            mt[i].start();
            Assert.assertTrue((("waiting for server " + i) + " being up"), ClientBase.waitForServerUp(("127.0.0.1:" + (clientPorts.get(i))), ClientBase.CONNECTION_TIMEOUT));
            verifyQuorumConfig(i, newServers, null);
            verifyQuorumMembers(mt[i]);
        }
        Set<String> expectedConfigs = new HashSet<>();
        for (String conf : oldServerAddress.values()) {
            // Remove "server.x=" prefix which quorum peer does not include.
            expectedConfigs.add(conf.substring(((conf.indexOf('=')) + 1)));
        }
        for (int i = 0; i < 3; ++i) {
            verifyQuorumConfig(i, joiningServers, null);
            verifyQuorumMembers(mt[i], expectedConfigs);
        }
        for (int i = 0; i < serverCount; ++i) {
            mt[i].shutdown();
        }
    }
}

