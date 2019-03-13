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


import java.util.ArrayList;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.admin.ZooKeeperAdmin;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.test.ClientBase;
import org.apache.zookeeper.test.ReconfigTest;
import org.junit.Assert;
import org.junit.Test;


public class StandaloneDisabledTest extends QuorumPeerTestBase {
    private final int NUM_SERVERS = 5;

    private QuorumPeerTestBase.MainThread[] peers;

    private ZooKeeper[] zkHandles;

    private ZooKeeperAdmin[] zkAdminHandles;

    private int[] clientPorts;

    private final int leaderId = 0;

    private final int follower1 = 1;

    private final int follower2 = 2;

    private final int observer1 = 3;

    private final int observer2 = 4;

    private ArrayList<String> serverStrings;

    private ArrayList<String> reconfigServers;

    /**
     * Test normal quorum operations work cleanly
     * with just a single server.
     */
    @Test(timeout = 600000)
    public void startSingleServerTest() throws Exception {
        setUpData();
        // start one server
        startServer(leaderId, ((serverStrings.get(leaderId)) + "\n"));
        ReconfigTest.testServerHasConfig(zkHandles[leaderId], null, null);
        QuorumPeerTestBase.LOG.info(("Initial Configuration:\n" + (new String(zkHandles[leaderId].getConfig(this, new Stat())))));
        // start and add 2 followers
        startFollowers();
        testReconfig(leaderId, true, reconfigServers);
        QuorumPeerTestBase.LOG.info(("Configuration after adding 2 followers:\n" + (new String(zkHandles[leaderId].getConfig(this, new Stat())))));
        // shutdown leader- quorum should still exist
        shutDownServer(leaderId);
        ReconfigTest.testNormalOperation(zkHandles[follower1], zkHandles[follower2]);
        // should not be able to remove follower 2
        // No quorum in new config (1/2)
        reconfigServers.clear();
        reconfigServers.add(Integer.toString(follower2));
        try {
            ReconfigTest.reconfig(zkAdminHandles[follower1], null, reconfigServers, null, (-1));
            Assert.fail("reconfig completed successfully even though there is no quorum up in new config!");
        } catch (KeeperException e) {
        }
        // reconfigure out leader and follower 1. Remaining follower
        // 2 should elect itself as leader and run by itself
        reconfigServers.clear();
        reconfigServers.add(Integer.toString(leaderId));
        reconfigServers.add(Integer.toString(follower1));
        testReconfig(follower2, false, reconfigServers);
        QuorumPeerTestBase.LOG.info(("Configuration after removing leader and follower 1:\n" + (new String(zkHandles[follower2].getConfig(this, new Stat())))));
        // Kill server 1 to avoid it interferences with FLE of the quorum {2, 3, 4}.
        shutDownServer(follower1);
        // Try to remove follower2, which is the only remaining server. This should fail.
        reconfigServers.clear();
        reconfigServers.add(Integer.toString(follower2));
        try {
            zkAdminHandles[follower2].reconfigure(null, reconfigServers, null, (-1), new Stat());
            Assert.fail("reconfig completed successfully even though there is no quorum up in new config!");
        } catch (KeeperException e) {
            // This is expected.
        } catch (Exception e) {
            Assert.fail("Should have been BadArgumentsException!");
        }
        // Add two participants and change them to observers to check
        // that we can reconfigure down to one participant with observers.
        ArrayList<String> observerStrings = new ArrayList<String>();
        startObservers(observerStrings);
        testReconfig(follower2, true, reconfigServers);// add partcipants

        testReconfig(follower2, true, observerStrings);// change to observers

        QuorumPeerTestBase.LOG.info(("Configuration after adding two observers:\n" + (new String(zkHandles[follower2].getConfig(this, new Stat())))));
        shutDownData();
    }

    /**
     * Ensure observer cannot start by itself
     */
    @Test
    public void startObserver() throws Exception {
        int clientPort = PortAssignment.unique();
        String config = ((((((("server." + (observer1)) + "=localhost:") + (PortAssignment.unique())) + ":") + clientPort) + ":observer;") + "localhost:") + (PortAssignment.unique());
        QuorumPeerTestBase.MainThread observer = new QuorumPeerTestBase.MainThread(observer1, clientPort, config);
        observer.start();
        Assert.assertFalse("Observer was able to start by itself!", ClientBase.waitForServerUp(("127.0.0.1:" + clientPort), ClientBase.CONNECTION_TIMEOUT));
    }
}

