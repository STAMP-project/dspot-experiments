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
import CreateMode.PERSISTENT_SEQUENTIAL;
import Event.EventType.NodeDataChanged;
import Event.EventType.NodeDeleted;
import Event.EventType.None;
import Event.KeeperState.Closed;
import Event.KeeperState.SyncConnected;
import Ids.OPEN_ACL_UNSAFE;
import ZKClientConfig.DISABLE_AUTO_WATCH_RESET;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WatcherTest extends ClientBase {
    protected static final Logger LOG = LoggerFactory.getLogger(WatcherTest.class);

    private long timeOfLastWatcherInvocation;

    private static final class MyStatCallback implements StatCallback {
        int rc;

        public void processResult(int rc, String path, Object ctx, Stat stat) {
            (((int[]) (ctx))[0])++;
            this.rc = rc;
        }
    }

    private class MyWatcher extends ClientBase.CountdownWatcher {
        LinkedBlockingQueue<WatchedEvent> events = new LinkedBlockingQueue<WatchedEvent>();

        public void process(WatchedEvent event) {
            super.process(event);
            if ((event.getType()) != (EventType.None)) {
                timeOfLastWatcherInvocation = System.currentTimeMillis();
                try {
                    events.put(event);
                } catch (InterruptedException e) {
                    WatcherTest.LOG.warn("ignoring interrupt during event.put");
                }
            }
        }
    }

    /**
     * Verify that we get all of the events we expect to get. This particular
     * case verifies that we see all of the data events on a particular node.
     * There was a bug (ZOOKEEPER-137) that resulted in events being dropped
     * in some cases (timing).
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     */
    @Test
    public void testWatcherCorrectness() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = null;
        try {
            WatcherTest.MyWatcher watcher = new WatcherTest.MyWatcher();
            zk = createClient(watcher, hostPort);
            StatCallback scb = new StatCallback() {
                public void processResult(int rc, String path, Object ctx, Stat stat) {
                    // don't do anything
                }
            };
            VoidCallback vcb = new VoidCallback() {
                public void processResult(int rc, String path, Object ctx) {
                    // don't do anything
                }
            };
            String[] names = new String[10];
            for (int i = 0; i < (names.length); i++) {
                String name = zk.create("/tc-", "initialvalue".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL);
                names[i] = name;
                Stat stat = new Stat();
                zk.getData(name, watcher, stat);
                zk.setData(name, "new".getBytes(), stat.getVersion(), scb, null);
                stat = zk.exists(name, watcher);
                zk.delete(name, stat.getVersion(), vcb, null);
            }
            for (int i = 0; i < (names.length); i++) {
                String name = names[i];
                WatchedEvent event = watcher.events.poll(10, TimeUnit.SECONDS);
                Assert.assertEquals(name, event.getPath());
                Assert.assertEquals(NodeDataChanged, event.getType());
                Assert.assertEquals(SyncConnected, event.getState());
                event = watcher.events.poll(10, TimeUnit.SECONDS);
                Assert.assertEquals(name, event.getPath());
                Assert.assertEquals(NodeDeleted, event.getType());
                Assert.assertEquals(SyncConnected, event.getState());
            }
        } finally {
            if (zk != null) {
                zk.close();
            }
        }
    }

    @Test
    public void testWatcherDisconnectOnClose() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = null;
        try {
            final BlockingQueue<WatchedEvent> queue = new LinkedBlockingQueue<>();
            WatcherTest.MyWatcher connWatcher = new WatcherTest.MyWatcher();
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    try {
                        queue.put(event);
                    } catch (InterruptedException e) {
                        // Oh well, never mind
                    }
                }
            };
            zk = createClient(connWatcher, hostPort);
            StatCallback scb = new StatCallback() {
                public void processResult(int rc, String path, Object ctx, Stat stat) {
                    // don't do anything
                }
            };
            // Register a watch on the node
            zk.exists("/missing", watcher, scb, null);
            // Close the client without changing the node
            zk.close();
            WatchedEvent event = queue.poll(10, TimeUnit.SECONDS);
            Assert.assertNotNull("No watch event was received after closing the Zookeeper client. A 'Closed' event should have occurred", event);
            Assert.assertEquals("Closed events are not generated by the server, and so should have a type of 'None'", None, event.getType());
            Assert.assertEquals("A 'Closed' event was expected as the Zookeeper client was closed without altering the node it was watching", Closed, event.getState());
        } finally {
            if (zk != null) {
                zk.close();
            }
        }
    }

    @Test
    public void testWatcherCount() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk1 = null;
        ZooKeeper zk2 = null;
        try {
            WatcherTest.MyWatcher w1 = new WatcherTest.MyWatcher();
            zk1 = createClient(w1, hostPort);
            WatcherTest.MyWatcher w2 = new WatcherTest.MyWatcher();
            zk2 = createClient(w2, hostPort);
            Stat stat = new Stat();
            zk1.create("/watch-count-test", "value".getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL);
            zk1.create("/watch-count-test-2", "value".getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL);
            zk1.getData("/watch-count-test", w1, stat);
            zk1.getData("/watch-count-test-2", w1, stat);
            zk2.getData("/watch-count-test", w2, stat);
            Assert.assertEquals(ClientBase.getServer(serverFactory).getZKDatabase().getDataTree().getWatchCount(), 3);
        } finally {
            if (zk1 != null) {
                zk1.close();
            }
            if (zk2 != null) {
                zk2.close();
            }
        }
    }

    static final int COUNT = 100;

    /**
     * This test checks that watches for pending requests do not get triggered,
     * but watches set by previous requests do.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWatchAutoResetWithPending() throws Exception {
        WatcherTest.MyWatcher[] watches = new WatcherTest.MyWatcher[WatcherTest.COUNT];
        WatcherTest.MyStatCallback[] cbs = new WatcherTest.MyStatCallback[WatcherTest.COUNT];
        WatcherTest.MyWatcher watcher = new WatcherTest.MyWatcher();
        int[] count = new int[1];
        TestableZooKeeper zk = createClient(watcher, hostPort, 6000);
        ZooKeeper zk2 = createClient(watcher, hostPort, 5000);
        zk2.create("/test", new byte[0], OPEN_ACL_UNSAFE, EPHEMERAL);
        for (int i = 0; i < ((WatcherTest.COUNT) / 2); i++) {
            watches[i] = new WatcherTest.MyWatcher();
            cbs[i] = new WatcherTest.MyStatCallback();
            zk.exists("/test", watches[i], cbs[i], count);
        }
        exists("/test", false);
        Assert.assertTrue("Failed to pause the connection!", zk.pauseCnxn(3000));
        zk2.close();
        stopServer();
        watches[0].waitForDisconnected(60000);
        for (int i = (WatcherTest.COUNT) / 2; i < (WatcherTest.COUNT); i++) {
            watches[i] = new WatcherTest.MyWatcher();
            cbs[i] = new WatcherTest.MyStatCallback();
            zk.exists("/test", watches[i], cbs[i], count);
        }
        startServer();
        watches[(((WatcherTest.COUNT) / 2) - 1)].waitForConnected(60000);
        Assert.assertEquals(null, exists("/test", false));
        waitForAllWatchers();
        for (int i = 0; i < ((WatcherTest.COUNT) / 2); i++) {
            Assert.assertEquals(("For " + i), 1, watches[i].events.size());
        }
        for (int i = (WatcherTest.COUNT) / 2; i < (WatcherTest.COUNT); i++) {
            if ((cbs[i].rc) == 0) {
                Assert.assertEquals(("For " + i), 1, watches[i].events.size());
            } else {
                Assert.assertEquals(("For " + i), 0, watches[i].events.size());
            }
        }
        Assert.assertEquals(WatcherTest.COUNT, count[0]);
        close();
    }

    final int TIMEOUT = 5000;

    @Test
    public void testWatcherAutoResetWithGlobal() throws Exception {
        ZooKeeper zk = null;
        WatcherTest.MyWatcher watcher = new WatcherTest.MyWatcher();
        zk = createClient(watcher, hostPort, TIMEOUT);
        testWatcherAutoReset(zk, watcher, watcher);
        zk.close();
    }

    @Test
    public void testWatcherAutoResetWithLocal() throws Exception {
        ZooKeeper zk = null;
        WatcherTest.MyWatcher watcher = new WatcherTest.MyWatcher();
        zk = createClient(watcher, hostPort, TIMEOUT);
        testWatcherAutoReset(zk, watcher, new WatcherTest.MyWatcher());
        zk.close();
    }

    @Test
    public void testWatcherAutoResetDisabledWithGlobal() throws Exception {
        /**
         * When ZooKeeper is created this property will get used.
         */
        System.setProperty(DISABLE_AUTO_WATCH_RESET, "true");
        testWatcherAutoResetWithGlobal();
    }

    @Test
    public void testWatcherAutoResetDisabledWithLocal() throws Exception {
        System.setProperty(DISABLE_AUTO_WATCH_RESET, "true");
        testWatcherAutoResetWithLocal();
    }
}

