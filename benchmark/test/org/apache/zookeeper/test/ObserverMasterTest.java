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
import Ids.OPEN_ACL_UNSAFE;
import KeeperState.SyncConnected;
import States.CONNECTED;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.jmx.CommonNames;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.server.admin.Commands;
import org.apache.zookeeper.server.quorum.DelayRequestProcessor;
import org.apache.zookeeper.server.quorum.FollowerZooKeeperServer;
import org.apache.zookeeper.server.quorum.QuorumPeerTestBase;
import org.apache.zookeeper.server.util.PortForwarder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class ObserverMasterTest extends QuorumPeerTestBase implements Watcher {
    protected static final Logger LOG = LoggerFactory.getLogger(ObserverMasterTest.class);

    public ObserverMasterTest(Boolean testObserverMaster) {
        this.testObserverMaster = testObserverMaster;
    }

    private Boolean testObserverMaster;

    private CountDownLatch latch;

    ZooKeeper zk;

    private WatchedEvent lastEvent = null;

    private int CLIENT_PORT_QP1;

    private int CLIENT_PORT_QP2;

    private int CLIENT_PORT_OBS;

    private int OM_PORT;

    private QuorumPeerTestBase.MainThread q1;

    private QuorumPeerTestBase.MainThread q2;

    private QuorumPeerTestBase.MainThread q3;

    @Test
    public void testLaggingObserverMaster() throws Exception {
        final int OM_PROXY_PORT = PortAssignment.unique();
        PortForwarder forwarder = setUp(OM_PROXY_PORT);
        // find the leader and observer master
        int leaderPort;
        QuorumPeerTestBase.MainThread leader;
        QuorumPeerTestBase.MainThread follower;
        if ((q1.getQuorumPeer().leader) != null) {
            leaderPort = CLIENT_PORT_QP1;
            leader = q1;
            follower = q2;
        } else
            if ((q2.getQuorumPeer().leader) != null) {
                leaderPort = CLIENT_PORT_QP2;
                leader = q2;
                follower = q1;
            } else {
                throw new RuntimeException("No leader");
            }

        // ensure the observer master has commits in the queue before observer sync
        zk = new ZooKeeper(("127.0.0.1:" + leaderPort), ClientBase.CONNECTION_TIMEOUT, this);
        for (int i = 0; i < 10; i++) {
            zk.create(("/bulk" + i), "initial data of some size".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        }
        zk.close();
        q3.start();
        Assert.assertTrue("waiting for server 3 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        latch = new CountDownLatch(1);
        zk = new ZooKeeper(("127.0.0.1:" + leaderPort), ClientBase.CONNECTION_TIMEOUT, this);
        latch.await();
        Assert.assertEquals(zk.getState(), CONNECTED);
        zk.create("/init", "first".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        final long lastLoggedZxid = leader.getQuorumPeer().getLastLoggedZxid();
        // wait for change to propagate
        waitFor("Timeout waiting for observer sync", new ZKTestCase.WaitForCondition() {
            public boolean evaluate() {
                return lastLoggedZxid == (q3.getQuorumPeer().getLastLoggedZxid());
            }
        }, 30);
        // simulate network fault
        if (forwarder != null) {
            forwarder.shutdown();
        }
        for (int i = 0; i < 10; i++) {
            zk.create(("/basic" + i), "second".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        }
        DelayRequestProcessor delayRequestProcessor = null;
        if (testObserverMaster) {
            FollowerZooKeeperServer followerZooKeeperServer = ((FollowerZooKeeperServer) (follower.getQuorumPeer().getActiveServer()));
            delayRequestProcessor = DelayRequestProcessor.injectDelayRequestProcessor(followerZooKeeperServer);
        }
        zk.create("/target1", "third".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        zk.create("/target2", "third".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        ObserverMasterTest.LOG.info((((("observer zxid " + (Long.toHexString(q3.getQuorumPeer().getLastLoggedZxid()))) + (testObserverMaster ? "" : " observer master zxid " + (Long.toHexString(follower.getQuorumPeer().getLastLoggedZxid())))) + " leader zxid ") + (Long.toHexString(leader.getQuorumPeer().getLastLoggedZxid()))));
        // restore network
        forwarder = (testObserverMaster) ? new PortForwarder(OM_PROXY_PORT, OM_PORT) : null;
        Assert.assertTrue("waiting for server 3 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertNotNull("Leader switched", leader.getQuorumPeer().leader);
        if (delayRequestProcessor != null) {
            delayRequestProcessor.unblockQueue();
        }
        latch = new CountDownLatch(1);
        ZooKeeper obsZk = new ZooKeeper(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT, this);
        latch.await();
        zk.create("/finalop", "fourth".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertEquals("first", new String(obsZk.getData("/init", null, null)));
        Assert.assertEquals("third", new String(obsZk.getData("/target1", null, null)));
        obsZk.close();
        shutdown();
        try {
            if (forwarder != null) {
                forwarder.shutdown();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * This test ensures two things:
     * 1. That Observers can successfully proxy requests to the ensemble.
     * 2. That Observers don't participate in leader elections.
     * The second is tested by constructing an ensemble where a leader would
     * be elected if and only if an Observer voted.
     */
    @Test
    public void testObserver() throws Exception {
        // We expect two notifications before we want to continue
        latch = new CountDownLatch(2);
        setUp((-1));
        q3.start();
        Assert.assertTrue("waiting for server 3 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        if (testObserverMaster) {
            int masterPort = q3.getQuorumPeer().observer.getSocket().getPort();
            ObserverMasterTest.LOG.info(((("port " + masterPort) + " ") + (OM_PORT)));
            Assert.assertEquals("observer failed to connect to observer master", masterPort, OM_PORT);
        }
        zk = new ZooKeeper(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT, this);
        zk.create("/obstest", "test".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        // Assert that commands are getting forwarded correctly
        Assert.assertEquals(new String(zk.getData("/obstest", null, null)), "test");
        // Now check that other commands don't blow everything up
        zk.sync("/", null, null);
        zk.setData("/obstest", "test2".getBytes(), (-1));
        zk.getChildren("/", false);
        Assert.assertEquals(zk.getState(), CONNECTED);
        ObserverMasterTest.LOG.info("Shutting down server 2");
        // Now kill one of the other real servers
        q2.shutdown();
        Assert.assertTrue("Waiting for server 2 to shut down", ClientBase.waitForServerDown(("127.0.0.1:" + (CLIENT_PORT_QP2)), ClientBase.CONNECTION_TIMEOUT));
        ObserverMasterTest.LOG.info("Server 2 down");
        // Now the resulting ensemble shouldn't be quorate
        latch.await();
        Assert.assertNotSame("Client is still connected to non-quorate cluster", SyncConnected, lastEvent.getState());
        ObserverMasterTest.LOG.info("Latch returned");
        try {
            Assert.assertNotEquals("Shouldn't get a response when cluster not quorate!", "test", new String(zk.getData("/obstest", null, null)));
        } catch (ConnectionLossException c) {
            ObserverMasterTest.LOG.info("Connection loss exception caught - ensemble not quorate (this is expected)");
        }
        latch = new CountDownLatch(1);
        ObserverMasterTest.LOG.info("Restarting server 2");
        // Bring it back
        // q2 = new MainThread(2, CLIENT_PORT_QP2, quorumCfgSection, extraCfgs);
        q2.start();
        ObserverMasterTest.LOG.info("Waiting for server 2 to come up");
        Assert.assertTrue("waiting for server 2 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_QP2)), ClientBase.CONNECTION_TIMEOUT));
        ObserverMasterTest.LOG.info("Server 2 started, waiting for latch");
        latch.await();
        // It's possible our session expired - but this is ok, shows we
        // were able to talk to the ensemble
        Assert.assertTrue((("Client didn't reconnect to quorate ensemble (state was" + (lastEvent.getState())) + ")"), (((KeeperState.SyncConnected) == (lastEvent.getState())) || ((KeeperState.Expired) == (lastEvent.getState()))));
        ObserverMasterTest.LOG.info("perform a revalidation test");
        int leaderProxyPort = PortAssignment.unique();
        int obsProxyPort = PortAssignment.unique();
        int leaderPort = ((q1.getQuorumPeer().leader) == null) ? CLIENT_PORT_QP2 : CLIENT_PORT_QP1;
        PortForwarder leaderPF = new PortForwarder(leaderProxyPort, leaderPort);
        latch = new CountDownLatch(1);
        ZooKeeper client = new ZooKeeper(String.format("127.0.0.1:%d,127.0.0.1:%d", leaderProxyPort, obsProxyPort), ClientBase.CONNECTION_TIMEOUT, this);
        latch.await();
        client.create("/revalidtest", "test".getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL);
        Assert.assertNotNull("Read-after write failed", client.exists("/revalidtest", null));
        latch = new CountDownLatch(2);
        PortForwarder obsPF = new PortForwarder(obsProxyPort, CLIENT_PORT_OBS);
        try {
            leaderPF.shutdown();
        } catch (Exception e) {
            // ignore?
        }
        latch.await();
        Assert.assertEquals(new String(client.getData("/revalidtest", null, null)), "test");
        client.close();
        obsPF.shutdown();
        shutdown();
    }

    @Test
    public void testRevalidation() throws Exception {
        setUp((-1));
        q3.start();
        Assert.assertTrue("waiting for server 3 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        final int leaderProxyPort = PortAssignment.unique();
        final int obsProxyPort = PortAssignment.unique();
        int leaderPort = ((q1.getQuorumPeer().leader) == null) ? CLIENT_PORT_QP2 : CLIENT_PORT_QP1;
        PortForwarder leaderPF = new PortForwarder(leaderProxyPort, leaderPort);
        latch = new CountDownLatch(1);
        zk = new ZooKeeper(String.format("127.0.0.1:%d,127.0.0.1:%d", leaderProxyPort, obsProxyPort), ClientBase.CONNECTION_TIMEOUT, this);
        latch.await();
        zk.create("/revalidtest", "test".getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL);
        Assert.assertNotNull("Read-after write failed", zk.exists("/revalidtest", null));
        latch = new CountDownLatch(2);
        PortForwarder obsPF = new PortForwarder(obsProxyPort, CLIENT_PORT_OBS);
        try {
            leaderPF.shutdown();
        } catch (Exception e) {
            // ignore?
        }
        latch.await();
        Assert.assertEquals(new String(zk.getData("/revalidtest", null, null)), "test");
        obsPF.shutdown();
        shutdown();
    }

    @Test
    public void testInOrderCommits() throws Exception {
        setUp((-1));
        zk = new ZooKeeper(("127.0.0.1:" + (CLIENT_PORT_QP1)), ClientBase.CONNECTION_TIMEOUT, null);
        for (int i = 0; i < 10; i++) {
            zk.create(("/bulk" + i), "Initial data of some size".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        }
        zk.close();
        q3.start();
        Assert.assertTrue("waiting for observer to be up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        latch = new CountDownLatch(1);
        zk = new ZooKeeper(("127.0.0.1:" + (CLIENT_PORT_QP1)), ClientBase.CONNECTION_TIMEOUT, this);
        latch.await();
        Assert.assertEquals(zk.getState(), CONNECTED);
        zk.create("/init", "first".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        final long zxid = q1.getQuorumPeer().getLastLoggedZxid();
        // wait for change to propagate
        waitFor("Timeout waiting for observer sync", new ZKTestCase.WaitForCondition() {
            public boolean evaluate() {
                return zxid == (q3.getQuorumPeer().getLastLoggedZxid());
            }
        }, 30);
        ZooKeeper obsZk = new ZooKeeper(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT, this);
        int followerPort = ((q1.getQuorumPeer().leader) == null) ? CLIENT_PORT_QP1 : CLIENT_PORT_QP2;
        ZooKeeper fZk = new ZooKeeper(("127.0.0.1:" + followerPort), ClientBase.CONNECTION_TIMEOUT, this);
        final int numTransactions = 10001;
        CountDownLatch gate = new CountDownLatch(1);
        CountDownLatch oAsyncLatch = new CountDownLatch(numTransactions);
        Thread oAsyncWriteThread = new Thread(new ObserverMasterTest.AsyncWriter(obsZk, numTransactions, true, oAsyncLatch, "/obs", gate));
        CountDownLatch fAsyncLatch = new CountDownLatch(numTransactions);
        Thread fAsyncWriteThread = new Thread(new ObserverMasterTest.AsyncWriter(fZk, numTransactions, true, fAsyncLatch, "/follower", gate));
        ObserverMasterTest.LOG.info("ASYNC WRITES");
        oAsyncWriteThread.start();
        fAsyncWriteThread.start();
        gate.countDown();
        oAsyncLatch.await();
        fAsyncLatch.await();
        oAsyncWriteThread.join(ClientBase.CONNECTION_TIMEOUT);
        if (oAsyncWriteThread.isAlive()) {
            ObserverMasterTest.LOG.error("asyncWriteThread is still alive");
        }
        fAsyncWriteThread.join(ClientBase.CONNECTION_TIMEOUT);
        if (fAsyncWriteThread.isAlive()) {
            ObserverMasterTest.LOG.error("asyncWriteThread is still alive");
        }
        obsZk.close();
        fZk.close();
        shutdown();
    }

    @Test
    public void testAdminCommands() throws IOException, InterruptedException, AttributeNotFoundException, InstanceNotFoundException, InvalidAttributeValueException, MBeanException, MalformedObjectNameException, ReflectionException, KeeperException {
        // flush all beans, then start
        for (ZKMBeanInfo beanInfo : MBeanRegistry.getInstance().getRegisteredBeans()) {
            MBeanRegistry.getInstance().unregister(beanInfo);
        }
        JMXEnv.setUp();
        setUp((-1));
        q3.start();
        Assert.assertTrue("waiting for observer to be up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        // Assert that commands are getting forwarded correctly
        zk = new ZooKeeper(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT, this);
        zk.create("/obstest", "test".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertEquals(new String(zk.getData("/obstest", null, null)), "test");
        // test stats collection
        final Map<String, String> emptyMap = Collections.emptyMap();
        Map<String, Object> stats = Commands.runCommand("mntr", q3.getQuorumPeer().getActiveServer(), emptyMap).toMap();
        Assert.assertTrue("observer not emitting observer_master_id", stats.containsKey("observer_master_id"));
        // check the stats for the first peer
        stats = Commands.runCommand("mntr", q1.getQuorumPeer().getActiveServer(), emptyMap).toMap();
        if (testObserverMaster) {
            if ((q1.getQuorumPeer().leader) == null) {
                Assert.assertEquals(1, stats.get("synced_observers"));
            } else {
                Assert.assertEquals(0, stats.get("synced_observers"));
            }
        } else {
            if ((q1.getQuorumPeer().leader) == null) {
                Assert.assertNull(stats.get("synced_observers"));
            } else {
                Assert.assertEquals(1, stats.get("synced_observers"));
            }
        }
        // check the stats for the second peer
        stats = Commands.runCommand("mntr", q2.getQuorumPeer().getActiveServer(), emptyMap).toMap();
        if (testObserverMaster) {
            if ((q2.getQuorumPeer().leader) == null) {
                Assert.assertEquals(1, stats.get("synced_observers"));
            } else {
                Assert.assertEquals(0, stats.get("synced_observers"));
            }
        } else {
            if ((q2.getQuorumPeer().leader) == null) {
                Assert.assertNull(stats.get("synced_observers"));
            } else {
                Assert.assertEquals(1, stats.get("synced_observers"));
            }
        }
        // test admin commands for disconnection
        ObjectName connBean = null;
        for (ObjectName bean : JMXEnv.conn().queryNames(new ObjectName(((CommonNames.DOMAIN) + ":*")), null)) {
            if ((bean.getCanonicalName().contains("Learner_Connections")) && (bean.getCanonicalName().contains(("id:" + (q3.getQuorumPeer().getId()))))) {
                connBean = bean;
                break;
            }
        }
        Assert.assertNotNull("could not find connection bean", connBean);
        latch = new CountDownLatch(1);
        JMXEnv.conn().invoke(connBean, "terminateConnection", new Object[0], null);
        Assert.assertTrue("server failed to disconnect on terminate", latch.await(((ClientBase.CONNECTION_TIMEOUT) / 2), TimeUnit.MILLISECONDS));
        Assert.assertTrue("waiting for server 3 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        final String obsBeanName = String.format("org.apache.ZooKeeperService:name0=ReplicatedServer_id%d,name1=replica.%d,name2=Observer", q3.getQuorumPeer().getId(), q3.getQuorumPeer().getId());
        Set<ObjectName> names = JMXEnv.conn().queryNames(new ObjectName(obsBeanName), null);
        Assert.assertEquals("expecting singular observer bean", 1, names.size());
        ObjectName obsBean = names.iterator().next();
        if (testObserverMaster) {
            // show we can move the observer using the id
            long observerMasterId = q3.getQuorumPeer().observer.getLearnerMasterId();
            latch = new CountDownLatch(1);
            JMXEnv.conn().setAttribute(obsBean, new Attribute("LearnerMaster", Long.toString((3 - observerMasterId))));
            Assert.assertTrue("server failed to disconnect on terminate", latch.await(ClientBase.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS));
            Assert.assertTrue("waiting for server 3 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (CLIENT_PORT_OBS)), ClientBase.CONNECTION_TIMEOUT));
        } else {
            // show we get an error
            final long leaderId = ((q1.getQuorumPeer().leader) == null) ? 2 : 1;
            try {
                JMXEnv.conn().setAttribute(obsBean, new Attribute("LearnerMaster", Long.toString((3 - leaderId))));
                Assert.fail("should have seen an exception on previous command");
            } catch (RuntimeMBeanException e) {
                Assert.assertEquals("mbean failed for the wrong reason", IllegalArgumentException.class, e.getCause().getClass());
            }
        }
        shutdown();
        JMXEnv.tearDown();
    }

    class AsyncWriter implements Runnable {
        private final ZooKeeper client;

        private final int numTransactions;

        private final boolean issueSync;

        private final CountDownLatch writerLatch;

        private final String root;

        private final CountDownLatch gate;

        AsyncWriter(ZooKeeper client, int numTransactions, boolean issueSync, CountDownLatch writerLatch, String root, CountDownLatch gate) {
            this.client = client;
            this.numTransactions = numTransactions;
            this.issueSync = issueSync;
            this.writerLatch = writerLatch;
            this.root = root;
            this.gate = gate;
        }

        @Override
        public void run() {
            if ((gate) != null) {
                try {
                    gate.await();
                } catch (InterruptedException e) {
                    ObserverMasterTest.LOG.error("Gate interrupted");
                    return;
                }
            }
            for (int i = 0; i < (numTransactions); i++) {
                final boolean pleaseLog = (i % 100) == 0;
                client.create(((root) + i), "inner thread".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT, new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        writerLatch.countDown();
                        if (pleaseLog) {
                            ObserverMasterTest.LOG.info("wrote {}", path);
                        }
                    }
                }, null);
                if (pleaseLog) {
                    ObserverMasterTest.LOG.info("async wrote {}{}", root, i);
                    if (issueSync) {
                        client.sync(((root) + "0"), null, null);
                    }
                }
            }
        }
    }
}

