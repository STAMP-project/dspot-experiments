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
package com.twitter.distributedlog;


import CreateMode.PERSISTENT;
import Credentials.NONE;
import DistributedLogConstants.EVERYONE_READ_CREATOR_ALL;
import KeeperException.InvalidACLException;
import KeeperException.NoAuthException;
import Watcher.Event.EventType.NodeDataChanged;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import ZooKeeper.States.CONNECTED;
import com.twitter.distributedlog.ZooKeeperClient.Credentials;
import com.twitter.distributedlog.ZooKeeperClient.DigestCredentials;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Cases for {@link com.twitter.distributedlog.ZooKeeperClient}
 */
public class TestZooKeeperClient extends ZooKeeperClusterTestCase {
    static final Logger LOG = LoggerFactory.getLogger(TestZooKeeperClient.class);

    private static final int sessionTimeoutMs = 2000;

    private ZooKeeperClient zkc;

    @Test(timeout = 60000)
    public void testAclCreatePerms() throws Exception {
        ZooKeeperClient zkcAuth = buildAuthdClient("test");
        zkcAuth.get().create("/test", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zkcAuth.get().create("/test/key1", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zkcAuth.get().create("/test/key2", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
        ZooKeeperClient zkcNoAuth = buildClient();
        zkcNoAuth.get().create("/test/key1/key1", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        try {
            zkcNoAuth.get().create("/test/key2/key1", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
            Assert.fail("create should fail on acl protected key");
        } catch (KeeperException ex) {
            TestZooKeeperClient.LOG.info("caught exception writing to protected key", ex);
        }
        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclNullIdDisablesAuth() throws Exception {
        ZooKeeperClient zkcAuth = buildAuthdClient(null);
        zkcAuth.get().create("/test", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zkcAuth.get().create("/test/key1", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        try {
            zkcAuth.get().create("/test/key2", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
            Assert.fail("create should fail because we're not authenticated");
        } catch (KeeperException ex) {
            TestZooKeeperClient.LOG.info("caught exception writing to protected key", ex);
        }
        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclAllowsReadsForNoAuth() throws Exception {
        ZooKeeperClient zkcAuth = buildAuthdClient("test");
        zkcAuth.get().create("/test", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
        zkcAuth.get().create("/test/key1", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
        zkcAuth.get().create("/test/key1/key2", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
        ZooKeeperClient zkcNoAuth = buildClient();
        List<String> nodes = null;
        String path = "/test";
        nodes = zkcNoAuth.get().getChildren(path, false);
        path = (path + "/") + (nodes.get(0));
        nodes = zkcNoAuth.get().getChildren(path, false);
        Assert.assertEquals("key2", nodes.get(0));
        ZooKeeperClient zkcAuth2 = buildAuthdClient("test2");
        path = "/test";
        nodes = zkcNoAuth.get().getChildren(path, false);
        path = (path + "/") + (nodes.get(0));
        nodes = zkcNoAuth.get().getChildren(path, false);
        Assert.assertEquals("key2", nodes.get(0));
        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclDigestCredentialsBasics() throws Exception {
        ZooKeeperClient zkcAuth = buildClient();
        zkcAuth.get().create("/test", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        try {
            zkcAuth.get().create("/test/key1", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
            Assert.fail("should have failed");
        } catch (Exception ex) {
        }
        Credentials credentials = new DigestCredentials("test", "test");
        credentials.authenticate(zkcAuth.get());
        // Should not throw now that we're authenticated.
        zkcAuth.get().create("/test/key1", new byte[0], EVERYONE_READ_CREATOR_ALL, PERSISTENT);
        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclNoopCredentialsDoesNothing() throws Exception {
        NONE.authenticate(null);
    }

    class FailingCredentials implements Credentials {
        boolean shouldFail = true;

        @Override
        public void authenticate(ZooKeeper zooKeeper) {
            if (shouldFail) {
                throw new RuntimeException("authfailed");
            }
        }

        public void setShouldFail(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }
    }

    @Test(timeout = 60000)
    public void testAclFailedAuthenticationCanBeRecovered() throws Exception {
        TestZooKeeperClient.FailingCredentials credentials = new TestZooKeeperClient.FailingCredentials();
        ZooKeeperClient zkc = new ZooKeeperClient("test", 2000, 2000, ZooKeeperClusterTestCase.zkServers, null, NullStatsLogger.INSTANCE, 1, 10000, credentials);
        try {
            zkc.get();
            Assert.fail("should have failed on auth");
        } catch (Exception ex) {
            Assert.assertEquals("authfailed", ex.getMessage());
        }
        // Should recover fine
        credentials.setShouldFail(false);
        zkc.get().create("/test", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        rmAll(zkc, "/test");
    }

    static class TestWatcher implements Watcher {
        final List<WatchedEvent> receivedEvents = new ArrayList<WatchedEvent>();

        CountDownLatch latch = new CountDownLatch(0);

        public TestZooKeeperClient.TestWatcher setLatch(CountDownLatch latch) {
            this.latch = latch;
            return this;
        }

        @Override
        public void process(WatchedEvent event) {
            if ((event.getType()) == (EventType.NodeDataChanged)) {
                synchronized(receivedEvents) {
                    receivedEvents.add(event);
                }
                latch.countDown();
            }
        }
    }

    @Test(timeout = 60000)
    public void testRegisterUnregisterWatchers() throws Exception {
        TestZooKeeperClient.TestWatcher w1 = new TestZooKeeperClient.TestWatcher();
        TestZooKeeperClient.TestWatcher w2 = new TestZooKeeperClient.TestWatcher();
        final CountDownLatch latch = new CountDownLatch(2);
        w1.setLatch(latch);
        w2.setLatch(latch);
        zkc.register(w1);
        zkc.register(w2);
        Assert.assertEquals(2, zkc.watchers.size());
        final String zkPath = "/test-register-unregister-watchers";
        zkc.get().create(zkPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zkc.get().getData(zkPath, true, new Stat());
        zkc.get().setData(zkPath, "first-set".getBytes(), (-1));
        latch.await();
        Assert.assertEquals(1, w1.receivedEvents.size());
        Assert.assertEquals(zkPath, w1.receivedEvents.get(0).getPath());
        Assert.assertEquals(NodeDataChanged, w1.receivedEvents.get(0).getType());
        Assert.assertEquals(1, w2.receivedEvents.size());
        Assert.assertEquals(zkPath, w2.receivedEvents.get(0).getPath());
        Assert.assertEquals(NodeDataChanged, w2.receivedEvents.get(0).getType());
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        w1.setLatch(latch1);
        w2.setLatch(latch2);
        zkc.unregister(w2);
        Assert.assertEquals(1, zkc.watchers.size());
        zkc.get().getData(zkPath, true, new Stat());
        zkc.get().setData(zkPath, "second-set".getBytes(), (-1));
        latch1.await();
        Assert.assertEquals(2, w1.receivedEvents.size());
        Assert.assertEquals(zkPath, w1.receivedEvents.get(1).getPath());
        Assert.assertEquals(NodeDataChanged, w1.receivedEvents.get(1).getType());
        Assert.assertFalse(latch2.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(1, w2.receivedEvents.size());
    }

    @Test(timeout = 60000)
    public void testExceptionOnWatchers() throws Exception {
        TestZooKeeperClient.TestWatcher w1 = new TestZooKeeperClient.TestWatcher();
        TestZooKeeperClient.TestWatcher w2 = new TestZooKeeperClient.TestWatcher();
        final CountDownLatch latch = new CountDownLatch(2);
        w1.setLatch(latch);
        w2.setLatch(latch);
        zkc.register(w1);
        zkc.register(w2);
        // register bad watcher
        zkc.register(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                throw new NullPointerException("bad watcher returning null");
            }
        });
        Assert.assertEquals(3, zkc.watchers.size());
        final String zkPath = "/test-exception-on-watchers";
        zkc.get().create(zkPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zkc.get().getData(zkPath, true, new Stat());
        zkc.get().setData(zkPath, "first-set".getBytes(), (-1));
        latch.await();
        Assert.assertEquals(1, w1.receivedEvents.size());
        Assert.assertEquals(zkPath, w1.receivedEvents.get(0).getPath());
        Assert.assertEquals(NodeDataChanged, w1.receivedEvents.get(0).getType());
        Assert.assertEquals(1, w2.receivedEvents.size());
        Assert.assertEquals(zkPath, w2.receivedEvents.get(0).getPath());
        Assert.assertEquals(NodeDataChanged, w2.receivedEvents.get(0).getType());
    }

    @Test(timeout = 60000)
    public void testZooKeeperReconnection() throws Exception {
        int sessionTimeoutMs = 100;
        ZooKeeperClient zkc = clientBuilder(sessionTimeoutMs).zkAclId(null).build();
        ZooKeeper zk = zkc.get();
        long sessionId = zk.getSessionId();
        ZooKeeperClientUtils.expireSession(zkc, ZooKeeperClusterTestCase.zkServers, (2 * sessionTimeoutMs));
        ZooKeeper newZk = zkc.get();
        while (!(CONNECTED.equals(newZk.getState()))) {
            TimeUnit.MILLISECONDS.sleep((sessionTimeoutMs / 2));
        } 
        long newSessionId = newZk.getSessionId();
        Assert.assertTrue((newZk == zk));
        Assert.assertFalse((sessionId == newSessionId));
    }

    @Test(timeout = 60000)
    public void testZooKeeperReconnectionBlockingRetryThread() throws Exception {
        int sessionTimeoutMs = 100;
        ZooKeeperClient zkc = clientBuilder(sessionTimeoutMs).zkAclId(null).build();
        ZooKeeper zk = zkc.get();
        Assert.assertTrue((zk instanceof org.apache.bookkeeper.zookeeper.ZooKeeperClient));
        org.apache.bookkeeper.zookeeper.ZooKeeperClient bkZkc = ((org.apache.bookkeeper.zookeeper.ZooKeeperClient) (zk));
        // get the connect executor
        Field connectExecutorField = bkZkc.getClass().getDeclaredField("connectExecutor");
        connectExecutorField.setAccessible(true);
        ExecutorService connectExecutor = ((ExecutorService) (connectExecutorField.get(bkZkc)));
        final CountDownLatch latch = new CountDownLatch(1);
        // block retry thread in the zookeeper client
        connectExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
            }
        });
        ZooKeeperClientUtils.expireSession(zkc, ZooKeeperClusterTestCase.zkServers, (2 * sessionTimeoutMs));
        ZooKeeper newZk;
        while ((newZk = zkc.get()) == zk) {
            TimeUnit.MILLISECONDS.sleep((sessionTimeoutMs / 2));
        } 
        Assert.assertEquals(CONNECTED, newZk.getState());
    }
}

