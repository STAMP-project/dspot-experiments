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


import CreateMode.EPHEMERAL;
import CreateMode.PERSISTENT;
import KeeperException.Code.OK;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.Leader;
import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QuorumTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(QuorumTest.class);

    public static final long CONNECTION_TIMEOUT = ClientTest.CONNECTION_TIMEOUT;

    private final QuorumBase qb = new QuorumBase();

    private final ClientTest ct = new ClientTest();

    private QuorumUtil qu;

    @Test
    public void testDeleteWithChildren() throws Exception {
        ct.testDeleteWithChildren();
    }

    @Test
    public void testPing() throws Exception {
        ct.testPing();
    }

    @Test
    public void testSequentialNodeNames() throws IOException, InterruptedException, KeeperException {
        ct.testSequentialNodeNames();
    }

    @Test
    public void testACLs() throws Exception {
        ct.testACLs();
    }

    @Test
    public void testClientwithoutWatcherObj() throws IOException, InterruptedException, KeeperException {
        ct.testClientwithoutWatcherObj();
    }

    @Test
    public void testClientWithWatcherObj() throws IOException, InterruptedException, KeeperException {
        ct.testClientWithWatcherObj();
    }

    @Test
    public void testGetView() {
        Assert.assertEquals(5, qb.s1.getView().size());
        Assert.assertEquals(5, qb.s2.getView().size());
        Assert.assertEquals(5, qb.s3.getView().size());
        Assert.assertEquals(5, qb.s4.getView().size());
        Assert.assertEquals(5, qb.s5.getView().size());
    }

    @Test
    public void testViewContains() {
        // Test view contains self
        Assert.assertTrue(qb.s1.viewContains(qb.s1.getId()));
        // Test view contains other servers
        Assert.assertTrue(qb.s1.viewContains(qb.s2.getId()));
        // Test view does not contain non-existant servers
        Assert.assertFalse(qb.s1.viewContains((-1L)));
    }

    volatile int counter = 0;

    volatile int errors = 0;

    @Test
    public void testLeaderShutdown() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = new DisconnectableZooKeeper(qb.hostPort, ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zk.create("/blah", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.create("/blah/blah", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Leader leader = qb.s1.leader;
        if (leader == null)
            leader = qb.s2.leader;

        if (leader == null)
            leader = qb.s3.leader;

        if (leader == null)
            leader = qb.s4.leader;

        if (leader == null)
            leader = qb.s5.leader;

        Assert.assertNotNull(leader);
        for (int i = 0; i < 5000; i++) {
            zk.setData("/blah/blah", new byte[0], (-1), new AsyncCallback.StatCallback() {
                public void processResult(int rc, String path, Object ctx, Stat stat) {
                    (counter)++;
                    if (rc != 0) {
                        (errors)++;
                    }
                }
            }, null);
        }
        for (LearnerHandler f : leader.getForwardingFollowers()) {
            f.getSocket().shutdownInput();
        }
        for (int i = 0; i < 5000; i++) {
            zk.setData("/blah/blah", new byte[0], (-1), new AsyncCallback.StatCallback() {
                public void processResult(int rc, String path, Object ctx, Stat stat) {
                    (counter)++;
                    if (rc != 0) {
                        (errors)++;
                    }
                }
            }, null);
        }
        // check if all the followers are alive
        Assert.assertTrue(qb.s1.isAlive());
        Assert.assertTrue(qb.s2.isAlive());
        Assert.assertTrue(qb.s3.isAlive());
        Assert.assertTrue(qb.s4.isAlive());
        Assert.assertTrue(qb.s5.isAlive());
        zk.close();
    }

    @Test
    public void testMultipleWatcherObjs() throws IOException, InterruptedException, KeeperException {
        ct.testMutipleWatcherObjs();
    }

    /**
     * Make sure that we can change sessions
     *  from follower to leader.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     */
    @Test
    public void testSessionMoved() throws Exception {
        String[] hostPorts = qb.hostPort.split(",");
        DisconnectableZooKeeper zk = new DisconnectableZooKeeper(hostPorts[0], ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zk.create("/sessionMoveTest", new byte[0], Ids.OPEN_ACL_UNSAFE, EPHEMERAL);
        // we want to loop through the list twice
        for (int i = 0; i < ((hostPorts.length) * 2); i++) {
            zk.dontReconnect();
            // This should stomp the zk handle
            DisconnectableZooKeeper zknew = new DisconnectableZooKeeper(hostPorts[((i + 1) % (hostPorts.length))], ClientBase.CONNECTION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                }
            }, getSessionId(), getSessionPasswd());
            setData("/", new byte[1], (-1));
            final int[] result = new int[1];
            result[0] = Integer.MAX_VALUE;
            sync("/", new AsyncCallback.VoidCallback() {
                public void processResult(int rc, String path, Object ctx) {
                    synchronized(result) {
                        result[0] = rc;
                        result.notify();
                    }
                }
            }, null);
            synchronized(result) {
                if ((result[0]) == (Integer.MAX_VALUE)) {
                    result.wait(5000);
                }
            }
            QuorumTest.LOG.info((((hostPorts[((i + 1) % (hostPorts.length))]) + " Sync returned ") + (result[0])));
            Assert.assertTrue(((result[0]) == (OK.intValue())));
            try {
                setData("/", new byte[1], (-1));
                Assert.fail("Should have lost the connection");
            } catch (KeeperException e) {
            }
            zk = zknew;
        }
        close();
    }

    private static class DiscoWatcher implements Watcher {
        volatile boolean zkDisco = false;

        public void process(WatchedEvent event) {
            if ((event.getState()) == (KeeperState.Disconnected)) {
                zkDisco = true;
            }
        }
    }

    /**
     * Make sure the previous connection closed after session move within
     * multiop.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     */
    @Test
    public void testSessionMovedWithMultiOp() throws Exception {
        String[] hostPorts = qb.hostPort.split(",");
        DisconnectableZooKeeper zk = new DisconnectableZooKeeper(hostPorts[0], ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        zk.multi(Arrays.asList(Op.create("/testSessionMovedWithMultiOp", new byte[0], Ids.OPEN_ACL_UNSAFE, EPHEMERAL)));
        // session moved to the next server
        ZooKeeper zknew = new ZooKeeper(hostPorts[1], ClientBase.CONNECTION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
            }
        }, getSessionId(), getSessionPasswd());
        zknew.multi(Arrays.asList(Op.create("/testSessionMovedWithMultiOp-1", new byte[0], Ids.OPEN_ACL_UNSAFE, EPHEMERAL)));
        // try to issue the multi op again from the old connection
        // expect to have ConnectionLossException instead of keep
        // getting SessionMovedException
        try {
            zk.multi(Arrays.asList(Op.create("/testSessionMovedWithMultiOp-Failed", new byte[0], Ids.OPEN_ACL_UNSAFE, EPHEMERAL)));
            Assert.fail("Should have lost the connection");
        } catch (KeeperException e) {
        }
        close();
        zknew.close();
    }

    /**
     * See ZOOKEEPER-790 for details
     */
    @Test
    public void testFollowersStartAfterLeader() throws Exception {
        qu = new QuorumUtil(1);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        qu.startQuorum();
        int index = 1;
        while ((qu.getPeer(index).peer.leader) == null)
            index++;

        // break the quorum
        qu.shutdown(index);
        // try to reestablish the quorum
        qu.start(index);
        // Connect the client after services are restarted (otherwise we would get
        // SessionExpiredException as the previous local session was not persisted).
        ZooKeeper zk = new ZooKeeper(("127.0.0.1:" + (qu.getPeer((index == 1 ? 2 : 1)).peer.getClientPort())), ClientBase.CONNECTION_TIMEOUT, watcher);
        try {
            watcher.waitForConnected(QuorumTest.CONNECTION_TIMEOUT);
        } catch (TimeoutException e) {
            Assert.fail("client could not connect to reestablished quorum: giving up after 30+ seconds.");
        }
        zk.close();
    }

    // skip superhammer and clientcleanup as they are too expensive for quorum
    /**
     * Tests if a multiop submitted to a non-leader propagates to the leader properly
     * (see ZOOKEEPER-1124).
     *
     * The test works as follows. It has a client connect to a follower and submit a multiop
     * to the follower. It then verifies that the multiop successfully gets committed by the leader.
     *
     * Without the fix in ZOOKEEPER-1124, this fails with a ConnectionLoss KeeperException.
     */
    @Test
    public void testMultiToFollower() throws Exception {
        qu = new QuorumUtil(1);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        qu.startQuorum();
        int index = 1;
        while ((qu.getPeer(index).peer.leader) == null)
            index++;

        ZooKeeper zk = new ZooKeeper(("127.0.0.1:" + (qu.getPeer((index == 1 ? 2 : 1)).peer.getClientPort())), ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(QuorumTest.CONNECTION_TIMEOUT);
        zk.multi(Arrays.asList(Op.create("/multi0", new byte[0], Ids.OPEN_ACL_UNSAFE, PERSISTENT), Op.create("/multi1", new byte[0], Ids.OPEN_ACL_UNSAFE, PERSISTENT), Op.create("/multi2", new byte[0], Ids.OPEN_ACL_UNSAFE, PERSISTENT)));
        zk.getData("/multi0", false, null);
        zk.getData("/multi1", false, null);
        zk.getData("/multi2", false, null);
        zk.close();
    }
}

