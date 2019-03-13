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
package org.apache.zookeeper.test;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.admin.ZooKeeperAdmin;
import org.apache.zookeeper.jmx.CommonNames;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.QuorumPeer.ServerState;
import org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical;
import org.apache.zookeeper.server.quorum.flexible.QuorumMaj;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReconfigTest extends ZKTestCase implements DataCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ReconfigTest.class);

    private QuorumUtil qu;

    private ZooKeeper[] zkArr;

    private ZooKeeperAdmin[] zkAdminArr;

    @Test
    public void testRemoveAddOne() throws Exception {
        qu = new QuorumUtil(1);// create 3 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        List<String> leavingServers = new ArrayList<String>();
        List<String> joiningServers = new ArrayList<String>();
        int leaderIndex = getLeaderId(qu);
        // during first iteration, leavingIndex will correspond to a follower
        // during second iteration leavingIndex will be the index of the leader
        int leavingIndex = (leaderIndex == 1) ? 2 : 1;
        for (int i = 0; i < 2; i++) {
            // some of the operations will be executed by a client connected to
            // the removed server
            // while others are invoked by a client connected to some other
            // server.
            // when we're removing the leader, zk1 will be the client connected
            // to removed server
            ZooKeeper zk1 = (leavingIndex == leaderIndex) ? zkArr[leaderIndex] : zkArr[((leaderIndex % (qu.ALL)) + 1)];
            ZooKeeper zk2 = (leavingIndex == leaderIndex) ? zkArr[((leaderIndex % (qu.ALL)) + 1)] : zkArr[leaderIndex];
            ZooKeeperAdmin zkAdmin1 = (leavingIndex == leaderIndex) ? zkAdminArr[leaderIndex] : zkAdminArr[((leaderIndex % (qu.ALL)) + 1)];
            ZooKeeperAdmin zkAdmin2 = (leavingIndex == leaderIndex) ? zkAdminArr[((leaderIndex % (qu.ALL)) + 1)] : zkAdminArr[leaderIndex];
            leavingServers.add(Integer.toString(leavingIndex));
            // remember this server so we can add it back later
            joiningServers.add(((((((("server." + leavingIndex) + "=localhost:") + (qu.getPeer(leavingIndex).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(leavingIndex).peer.getElectionAddress().getPort())) + ":participant;localhost:") + (qu.getPeer(leavingIndex).peer.getClientPort())));
            String configStr = ReconfigTest.reconfig(zkAdmin1, null, leavingServers, null, (-1));
            ReconfigTest.testServerHasConfig(zk2, null, leavingServers);
            ReconfigTest.testNormalOperation(zk2, zk1);
            QuorumVerifier qv = qu.getPeer(1).peer.configFromString(configStr);
            long version = qv.getVersion();
            // checks that conditioning on version works properly
            try {
                ReconfigTest.reconfig(zkAdmin2, joiningServers, null, null, (version + 1));
                Assert.fail("reconfig succeeded even though version condition was incorrect!");
            } catch (KeeperException e) {
            }
            ReconfigTest.reconfig(zkAdmin2, joiningServers, null, null, version);
            ReconfigTest.testNormalOperation(zk1, zk2);
            ReconfigTest.testServerHasConfig(zk1, joiningServers, null);
            // second iteration of the loop will remove the leader
            // and add it back (as follower)
            leavingIndex = leaderIndex = getLeaderId(qu);
            leavingServers.clear();
            joiningServers.clear();
        }
    }

    /**
     * 1. removes and adds back two servers (incl leader). One of the servers is added back as observer
     * 2. tests that reconfig fails if quorum of new config is not up
     * 3. tests that a server that's not up during reconfig learns the new config when it comes up
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveAddTwo() throws Exception {
        qu = new QuorumUtil(2);// create 5 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        List<String> leavingServers = new ArrayList<String>();
        List<String> joiningServers = new ArrayList<String>();
        int leaderIndex = getLeaderId(qu);
        // lets remove the leader and some other server
        int leavingIndex1 = leaderIndex;
        int leavingIndex2 = (leaderIndex == 1) ? 2 : 1;
        // find some server that's staying
        int stayingIndex1 = 1;
        int stayingIndex2 = 1;
        int stayingIndex3 = 1;
        while ((stayingIndex1 == leavingIndex1) || (stayingIndex1 == leavingIndex2))
            stayingIndex1++;

        while (((stayingIndex2 == leavingIndex1) || (stayingIndex2 == leavingIndex2)) || (stayingIndex2 == stayingIndex1))
            stayingIndex2++;

        while ((((stayingIndex3 == leavingIndex1) || (stayingIndex3 == leavingIndex2)) || (stayingIndex3 == stayingIndex1)) || (stayingIndex3 == stayingIndex2))
            stayingIndex3++;

        leavingServers.add(Integer.toString(leavingIndex1));
        leavingServers.add(Integer.toString(leavingIndex2));
        // remember these servers so we can add them back later
        joiningServers.add(((((((("server." + leavingIndex1) + "=localhost:") + (qu.getPeer(leavingIndex1).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(leavingIndex1).peer.getElectionAddress().getPort())) + ":participant;localhost:") + (qu.getPeer(leavingIndex1).peer.getClientPort())));
        // this server will be added back as an observer
        joiningServers.add(((((((("server." + leavingIndex2) + "=localhost:") + (qu.getPeer(leavingIndex2).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(leavingIndex2).peer.getElectionAddress().getPort())) + ":observer;localhost:") + (qu.getPeer(leavingIndex2).peer.getClientPort())));
        qu.shutdown(leavingIndex1);
        qu.shutdown(leavingIndex2);
        // 3 servers still up so this should work
        ReconfigTest.reconfig(zkAdminArr[stayingIndex2], null, leavingServers, null, (-1));
        qu.shutdown(stayingIndex2);
        // the following commands would not work in the original
        // cluster of 5, but now that we've removed 2 servers
        // we have a cluster of 3 servers and one of them is allowed to fail
        ReconfigTest.testServerHasConfig(zkArr[stayingIndex1], null, leavingServers);
        ReconfigTest.testServerHasConfig(zkArr[stayingIndex3], null, leavingServers);
        ReconfigTest.testNormalOperation(zkArr[stayingIndex1], zkArr[stayingIndex3]);
        // this is a test that a reconfig will only succeed
        // if there is a quorum up in new config. Below there is no
        // quorum so it should fail
        // the sleep is necessary so that the leader figures out
        // that the switched off servers are down
        Thread.sleep(10000);
        try {
            ReconfigTest.reconfig(zkAdminArr[stayingIndex1], joiningServers, null, null, (-1));
            Assert.fail("reconfig completed successfully even though there is no quorum up in new config!");
        } catch (KeeperException e) {
        }
        // now start the third server so that new config has quorum
        qu.restart(stayingIndex2);
        ReconfigTest.reconfig(zkAdminArr[stayingIndex1], joiningServers, null, null, (-1));
        ReconfigTest.testNormalOperation(zkArr[stayingIndex2], zkArr[stayingIndex3]);
        ReconfigTest.testServerHasConfig(zkArr[stayingIndex2], joiningServers, null);
        // this server wasn't around during the configuration change
        // we should check that it is able to connect, finds out
        // about the change and becomes an observer.
        qu.restart(leavingIndex2);
        Assert.assertTrue(((qu.getPeer(leavingIndex2).peer.getPeerState()) == (ServerState.OBSERVING)));
        ReconfigTest.testNormalOperation(zkArr[stayingIndex2], zkArr[leavingIndex2]);
        ReconfigTest.testServerHasConfig(zkArr[leavingIndex2], joiningServers, null);
    }

    @Test
    public void testBulkReconfig() throws Exception {
        qu = new QuorumUtil(3);// create 7 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        // new config will have three of the servers as followers
        // two of the servers as observers, and all ports different
        ArrayList<String> newServers = new ArrayList<String>();
        for (int i = 1; i <= 5; i++) {
            String server = (((((((("server." + i) + "=localhost:") + (PortAssignment.unique())) + ":") + (PortAssignment.unique())) + ":") + ((i == 4) || (i == 5) ? "observer" : "participant")) + ";localhost:") + (qu.getPeer(i).peer.getClientPort());
            newServers.add(server);
        }
        qu.shutdown(3);
        qu.shutdown(6);
        qu.shutdown(7);
        ReconfigTest.reconfig(zkAdminArr[1], null, null, newServers, (-1));
        ReconfigTest.testNormalOperation(zkArr[1], zkArr[2]);
        ReconfigTest.testServerHasConfig(zkArr[1], newServers, null);
        ReconfigTest.testServerHasConfig(zkArr[2], newServers, null);
        ReconfigTest.testServerHasConfig(zkArr[4], newServers, null);
        ReconfigTest.testServerHasConfig(zkArr[5], newServers, null);
        qu.shutdown(5);
        qu.shutdown(4);
        ReconfigTest.testNormalOperation(zkArr[1], zkArr[2]);
    }

    @Test
    public void testRemoveOneAsynchronous() throws Exception {
        qu = new QuorumUtil(2);
        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        List<String> leavingServers = new ArrayList<String>();
        // lets remove someone who's not the leader
        leavingServers.add(((getLeaderId(qu)) == 5 ? "4" : "5"));
        List<Integer> results = new LinkedList<Integer>();
        zkAdminArr[1].reconfigure(null, leavingServers, null, (-1), this, results);
        synchronized(results) {
            while ((results.size()) < 1) {
                results.wait();
            } 
        }
        Assert.assertEquals(0, ((int) (results.get(0))));
        ReconfigTest.testNormalOperation(zkArr[1], zkArr[2]);
        for (int i = 1; i <= 5; i++)
            ReconfigTest.testServerHasConfig(zkArr[i], null, leavingServers);

    }

    @Test
    public void testRoleChange() throws Exception {
        qu = new QuorumUtil(1);// create 3 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        // changing a server's role / port is done by "adding" it with the same
        // id but different role / port
        List<String> joiningServers = new ArrayList<String>();
        int leaderIndex = getLeaderId(qu);
        // during first and second iteration, leavingIndex will correspond to a
        // follower
        // during third and fouth iteration leavingIndex will be the index of
        // the leader
        int changingIndex = (leaderIndex == 1) ? 2 : 1;
        // first convert participant to observer, then observer to participant,
        // and so on
        String newRole = "observer";
        for (int i = 0; i < 4; i++) {
            // some of the operations will be executed by a client connected to
            // the removed server
            // while others are invoked by a client connected to some other
            // server.
            // when we're removing the leader, zk1 will be the client connected
            // to removed server
            ZooKeeper zk1 = (changingIndex == leaderIndex) ? zkArr[leaderIndex] : zkArr[((leaderIndex % (qu.ALL)) + 1)];
            ZooKeeperAdmin zkAdmin1 = (changingIndex == leaderIndex) ? zkAdminArr[leaderIndex] : zkAdminArr[((leaderIndex % (qu.ALL)) + 1)];
            // exactly as it is now, except for role change
            joiningServers.add(((((((((("server." + changingIndex) + "=localhost:") + (qu.getPeer(changingIndex).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(changingIndex).peer.getElectionAddress().getPort())) + ":") + newRole) + ";localhost:") + (qu.getPeer(changingIndex).peer.getClientPort())));
            ReconfigTest.reconfig(zkAdmin1, joiningServers, null, null, (-1));
            ReconfigTest.testNormalOperation(zkArr[changingIndex], zk1);
            if (newRole.equals("observer")) {
                Assert.assertTrue(((((qu.getPeer(changingIndex).peer.observer) != null) && ((qu.getPeer(changingIndex).peer.follower) == null)) && ((qu.getPeer(changingIndex).peer.leader) == null)));
                Assert.assertTrue(((qu.getPeer(changingIndex).peer.getPeerState()) == (ServerState.OBSERVING)));
            } else {
                Assert.assertTrue((((qu.getPeer(changingIndex).peer.observer) == null) && (((qu.getPeer(changingIndex).peer.follower) != null) || ((qu.getPeer(changingIndex).peer.leader) != null))));
                Assert.assertTrue((((qu.getPeer(changingIndex).peer.getPeerState()) == (ServerState.FOLLOWING)) || ((qu.getPeer(changingIndex).peer.getPeerState()) == (ServerState.LEADING))));
            }
            joiningServers.clear();
            if (newRole.equals("observer")) {
                newRole = "participant";
            } else {
                // lets change leader to observer
                newRole = "observer";
                leaderIndex = getLeaderId(qu);
                changingIndex = leaderIndex;
            }
        }
    }

    @Test
    public void testPortChange() throws Exception {
        qu = new QuorumUtil(1);// create 3 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        List<String> joiningServers = new ArrayList<String>();
        int leaderIndex = getLeaderId(qu);
        int followerIndex = (leaderIndex == 1) ? 2 : 1;
        // modify follower's client port
        int quorumPort = qu.getPeer(followerIndex).peer.getQuorumAddress().getPort();
        int electionPort = qu.getPeer(followerIndex).peer.getElectionAddress().getPort();
        int oldClientPort = qu.getPeer(followerIndex).peer.getClientPort();
        int newClientPort = PortAssignment.unique();
        joiningServers.add(((((((("server." + followerIndex) + "=localhost:") + quorumPort) + ":") + electionPort) + ":participant;localhost:") + newClientPort));
        // create a /test znode and check that read/write works before
        // any reconfig is invoked
        ReconfigTest.testNormalOperation(zkArr[followerIndex], zkArr[leaderIndex]);
        ReconfigTest.reconfig(zkAdminArr[followerIndex], joiningServers, null, null, (-1));
        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(1000);
                zkArr[followerIndex].setData("/test", "teststr".getBytes(), (-1));
            }
        } catch (KeeperException e) {
            Assert.fail("Existing client disconnected when client port changed!");
        }
        zkArr[followerIndex].close();
        zkArr[followerIndex] = new ZooKeeper(("127.0.0.1:" + oldClientPort), ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zkAdminArr[followerIndex].close();
        zkAdminArr[followerIndex] = new ZooKeeperAdmin(("127.0.0.1:" + oldClientPort), ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zkAdminArr[followerIndex].addAuthInfo("digest", "super:test".getBytes());
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                zkArr[followerIndex].setData("/test", "teststr".getBytes(), (-1));
                Assert.fail("New client connected to old client port!");
            } catch (KeeperException e) {
            }
        }
        zkArr[followerIndex].close();
        zkArr[followerIndex] = new ZooKeeper(("127.0.0.1:" + newClientPort), ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zkAdminArr[followerIndex].close();
        zkAdminArr[followerIndex] = new ZooKeeperAdmin(("127.0.0.1:" + newClientPort), ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zkAdminArr[followerIndex].addAuthInfo("digest", "super:test".getBytes());
        ReconfigTest.testNormalOperation(zkArr[followerIndex], zkArr[leaderIndex]);
        ReconfigTest.testServerHasConfig(zkArr[followerIndex], joiningServers, null);
        Assert.assertEquals(newClientPort, qu.getPeer(followerIndex).peer.getClientPort());
        joiningServers.clear();
        // change leader's leading port - should renounce leadership
        int newQuorumPort = PortAssignment.unique();
        joiningServers.add(((((((("server." + leaderIndex) + "=localhost:") + newQuorumPort) + ":") + (qu.getPeer(leaderIndex).peer.getElectionAddress().getPort())) + ":participant;localhost:") + (qu.getPeer(leaderIndex).peer.getClientPort())));
        ReconfigTest.reconfig(zkAdminArr[leaderIndex], joiningServers, null, null, (-1));
        ReconfigTest.testNormalOperation(zkArr[followerIndex], zkArr[leaderIndex]);
        Assert.assertTrue(((qu.getPeer(leaderIndex).peer.getQuorumAddress().getPort()) == newQuorumPort));
        joiningServers.clear();
        // change everyone's leader election port
        for (int i = 1; i <= 3; i++) {
            joiningServers.add(((((((("server." + i) + "=localhost:") + (qu.getPeer(i).peer.getQuorumAddress().getPort())) + ":") + (PortAssignment.unique())) + ":participant;localhost:") + (qu.getPeer(i).peer.getClientPort())));
        }
        ReconfigTest.reconfig(zkAdminArr[1], joiningServers, null, null, (-1));
        leaderIndex = getLeaderId(qu);
        int follower1 = (leaderIndex == 1) ? 2 : 1;
        int follower2 = 1;
        while ((follower2 == leaderIndex) || (follower2 == follower1))
            follower2++;

        // lets kill the leader and see if a new one is elected
        qu.shutdown(getLeaderId(qu));
        ReconfigTest.testNormalOperation(zkArr[follower2], zkArr[follower1]);
        ReconfigTest.testServerHasConfig(zkArr[follower1], joiningServers, null);
        ReconfigTest.testServerHasConfig(zkArr[follower2], joiningServers, null);
    }

    @Test
    public void testPortChangeToBlockedPortFollower() throws Exception {
        testPortChangeToBlockedPort(false);
    }

    @Test
    public void testPortChangeToBlockedPortLeader() throws Exception {
        testPortChangeToBlockedPort(true);
    }

    @Test
    public void testUnspecifiedClientAddress() throws Exception {
        int[] ports = new int[]{ PortAssignment.unique(), PortAssignment.unique(), PortAssignment.unique() };
        String server = (((("server.0=localhost:" + (ports[0])) + ":") + (ports[1])) + ";") + (ports[2]);
        QuorumServer qs = new QuorumServer(0, server);
        Assert.assertEquals(qs.clientAddr.getHostString(), "0.0.0.0");
        Assert.assertEquals(qs.clientAddr.getPort(), ports[2]);
    }

    @Test
    public void testQuorumSystemChange() throws Exception {
        qu = new QuorumUtil(3);// create 7 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        ArrayList<String> members = new ArrayList<String>();
        members.add("group.1=3:4:5");
        members.add("group.2=1:2");
        members.add("weight.1=0");
        members.add("weight.2=0");
        members.add("weight.3=1");
        members.add("weight.4=1");
        members.add("weight.5=1");
        for (int i = 1; i <= 5; i++) {
            members.add((((((((("server." + i) + "=127.0.0.1:") + (qu.getPeer(i).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(i).peer.getElectionAddress().getPort())) + ";") + "127.0.0.1:") + (qu.getPeer(i).peer.getClientPort())));
        }
        ReconfigTest.reconfig(zkAdminArr[1], null, null, members, (-1));
        // this should flush the config to servers 2, 3, 4 and 5
        ReconfigTest.testNormalOperation(zkArr[2], zkArr[3]);
        ReconfigTest.testNormalOperation(zkArr[4], zkArr[5]);
        for (int i = 1; i <= 5; i++) {
            if (!((qu.getPeer(i).peer.getQuorumVerifier()) instanceof QuorumHierarchical))
                Assert.fail((("peer " + i) + " doesn't think the quorum system is Hieararchical!"));

        }
        qu.shutdown(1);
        qu.shutdown(2);
        qu.shutdown(3);
        qu.shutdown(7);
        qu.shutdown(6);
        // servers 4 and 5 should be able to work independently
        ReconfigTest.testNormalOperation(zkArr[4], zkArr[5]);
        qu.restart(1);
        qu.restart(2);
        members.clear();
        for (int i = 1; i <= 3; i++) {
            members.add((((((((("server." + i) + "=127.0.0.1:") + (qu.getPeer(i).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(i).peer.getElectionAddress().getPort())) + ";") + "127.0.0.1:") + (qu.getPeer(i).peer.getClientPort())));
        }
        ReconfigTest.reconfig(zkAdminArr[1], null, null, members, (-1));
        // flush the config to server 2
        ReconfigTest.testNormalOperation(zkArr[1], zkArr[2]);
        qu.shutdown(4);
        qu.shutdown(5);
        // servers 1 and 2 should be able to work independently
        ReconfigTest.testNormalOperation(zkArr[1], zkArr[2]);
        for (int i = 1; i <= 2; i++) {
            if (!((qu.getPeer(i).peer.getQuorumVerifier()) instanceof QuorumMaj))
                Assert.fail((("peer " + i) + " doesn't think the quorum system is a majority quorum system!"));

        }
    }

    @Test
    public void testInitialConfigHasPositiveVersion() throws Exception {
        qu = new QuorumUtil(1);// create 3 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        ReconfigTest.testNormalOperation(zkArr[1], zkArr[2]);
        for (int i = 1; i < 4; i++) {
            String configStr = ReconfigTest.testServerHasConfig(zkArr[i], null, null);
            QuorumVerifier qv = qu.getPeer(i).peer.configFromString(configStr);
            long version = qv.getVersion();
            Assert.assertTrue((version == 4294967296L));
        }
    }

    /**
     * Tests verifies the jmx attributes of local and remote peer bean - remove
     * one quorum peer and again adding it back
     */
    @Test
    public void testJMXBeanAfterRemoveAddOne() throws Exception {
        qu = new QuorumUtil(1);// create 3 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        List<String> leavingServers = new ArrayList<String>();
        List<String> joiningServers = new ArrayList<String>();
        // assert remotePeerBean.1 of ReplicatedServer_2
        int leavingIndex = 1;
        int replica2 = 2;
        QuorumPeer peer2 = qu.getPeer(replica2).peer;
        QuorumServer leavingQS2 = peer2.getView().get(new Long(leavingIndex));
        String remotePeerBean2 = ((((CommonNames.DOMAIN) + ":name0=ReplicatedServer_id") + replica2) + ",name1=replica.") + leavingIndex;
        assertRemotePeerMXBeanAttributes(leavingQS2, remotePeerBean2);
        // assert remotePeerBean.1 of ReplicatedServer_3
        int replica3 = 3;
        QuorumPeer peer3 = qu.getPeer(replica3).peer;
        QuorumServer leavingQS3 = peer3.getView().get(new Long(leavingIndex));
        String remotePeerBean3 = ((((CommonNames.DOMAIN) + ":name0=ReplicatedServer_id") + replica3) + ",name1=replica.") + leavingIndex;
        assertRemotePeerMXBeanAttributes(leavingQS3, remotePeerBean3);
        ZooKeeper zk = zkArr[leavingIndex];
        ZooKeeperAdmin zkAdmin = zkAdminArr[leavingIndex];
        leavingServers.add(Integer.toString(leavingIndex));
        // remember this server so we can add it back later
        joiningServers.add(((((((("server." + leavingIndex) + "=127.0.0.1:") + (qu.getPeer(leavingIndex).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(leavingIndex).peer.getElectionAddress().getPort())) + ":participant;127.0.0.1:") + (qu.getPeer(leavingIndex).peer.getClientPort())));
        // Remove ReplicatedServer_1 from the ensemble
        ReconfigTest.reconfig(zkAdmin, null, leavingServers, null, (-1));
        // localPeerBean.1 of ReplicatedServer_1
        QuorumPeer removedPeer = qu.getPeer(leavingIndex).peer;
        String localPeerBean = ((((CommonNames.DOMAIN) + ":name0=ReplicatedServer_id") + leavingIndex) + ",name1=replica.") + leavingIndex;
        assertLocalPeerMXBeanAttributes(removedPeer, localPeerBean, false);
        // remotePeerBean.1 shouldn't exists in ReplicatedServer_2
        JMXEnv.ensureNone(remotePeerBean2);
        // remotePeerBean.1 shouldn't exists in ReplicatedServer_3
        JMXEnv.ensureNone(remotePeerBean3);
        // Add ReplicatedServer_1 back to the ensemble
        ReconfigTest.reconfig(zkAdmin, joiningServers, null, null, (-1));
        // localPeerBean.1 of ReplicatedServer_1
        assertLocalPeerMXBeanAttributes(removedPeer, localPeerBean, true);
        // assert remotePeerBean.1 of ReplicatedServer_2
        leavingQS2 = peer2.getView().get(new Long(leavingIndex));
        assertRemotePeerMXBeanAttributes(leavingQS2, remotePeerBean2);
        // assert remotePeerBean.1 of ReplicatedServer_3
        leavingQS3 = peer3.getView().get(new Long(leavingIndex));
        assertRemotePeerMXBeanAttributes(leavingQS3, remotePeerBean3);
    }

    /**
     * Tests verifies the jmx attributes of local and remote peer bean - change
     * participant to observer role
     */
    @Test
    public void testJMXBeanAfterRoleChange() throws Exception {
        qu = new QuorumUtil(1);// create 3 servers

        qu.disableJMXTest = true;
        qu.startAll();
        zkArr = ReconfigTest.createHandles(qu);
        zkAdminArr = ReconfigTest.createAdminHandles(qu);
        // changing a server's role / port is done by "adding" it with the same
        // id but different role / port
        List<String> joiningServers = new ArrayList<String>();
        // assert remotePeerBean.1 of ReplicatedServer_2
        int changingIndex = 1;
        int replica2 = 2;
        QuorumPeer peer2 = qu.getPeer(replica2).peer;
        QuorumServer changingQS2 = peer2.getView().get(new Long(changingIndex));
        String remotePeerBean2 = ((((CommonNames.DOMAIN) + ":name0=ReplicatedServer_id") + replica2) + ",name1=replica.") + changingIndex;
        assertRemotePeerMXBeanAttributes(changingQS2, remotePeerBean2);
        // assert remotePeerBean.1 of ReplicatedServer_3
        int replica3 = 3;
        QuorumPeer peer3 = qu.getPeer(replica3).peer;
        QuorumServer changingQS3 = peer3.getView().get(new Long(changingIndex));
        String remotePeerBean3 = ((((CommonNames.DOMAIN) + ":name0=ReplicatedServer_id") + replica3) + ",name1=replica.") + changingIndex;
        assertRemotePeerMXBeanAttributes(changingQS3, remotePeerBean3);
        String newRole = "observer";
        ZooKeeper zk = zkArr[changingIndex];
        ZooKeeperAdmin zkAdmin = zkAdminArr[changingIndex];
        // exactly as it is now, except for role change
        joiningServers.add(((((((((("server." + changingIndex) + "=127.0.0.1:") + (qu.getPeer(changingIndex).peer.getQuorumAddress().getPort())) + ":") + (qu.getPeer(changingIndex).peer.getElectionAddress().getPort())) + ":") + newRole) + ";127.0.0.1:") + (qu.getPeer(changingIndex).peer.getClientPort())));
        ReconfigTest.reconfig(zkAdmin, joiningServers, null, null, (-1));
        ReconfigTest.testNormalOperation(zkArr[changingIndex], zk);
        Assert.assertTrue(((((qu.getPeer(changingIndex).peer.observer) != null) && ((qu.getPeer(changingIndex).peer.follower) == null)) && ((qu.getPeer(changingIndex).peer.leader) == null)));
        Assert.assertTrue(((qu.getPeer(changingIndex).peer.getPeerState()) == (ServerState.OBSERVING)));
        QuorumPeer qp = qu.getPeer(changingIndex).peer;
        String localPeerBeanName = ((((CommonNames.DOMAIN) + ":name0=ReplicatedServer_id") + changingIndex) + ",name1=replica.") + changingIndex;
        // localPeerBean.1 of ReplicatedServer_1
        assertLocalPeerMXBeanAttributes(qp, localPeerBeanName, true);
        // assert remotePeerBean.1 of ReplicatedServer_2
        changingQS2 = peer2.getView().get(new Long(changingIndex));
        assertRemotePeerMXBeanAttributes(changingQS2, remotePeerBean2);
        // assert remotePeerBean.1 of ReplicatedServer_3
        changingQS3 = peer3.getView().get(new Long(changingIndex));
        assertRemotePeerMXBeanAttributes(changingQS3, remotePeerBean3);
    }
}

