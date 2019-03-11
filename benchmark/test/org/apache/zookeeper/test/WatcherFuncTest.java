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


import CreateMode.PERSISTENT;
import EventType.NodeChildrenChanged;
import EventType.NodeCreated;
import EventType.NodeDataChanged;
import EventType.NodeDeleted;
import Ids.OPEN_ACL_UNSAFE;
import KeeperException.Code.NONODE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Test;


public class WatcherFuncTest extends ClientBase {
    private static class SimpleWatcher implements Watcher {
        private LinkedBlockingQueue<WatchedEvent> events = new LinkedBlockingQueue<WatchedEvent>();

        private CountDownLatch latch;

        public SimpleWatcher(CountDownLatch latch) {
            this.latch = latch;
        }

        public void process(WatchedEvent event) {
            if ((event.getState()) == (KeeperState.SyncConnected)) {
                if ((latch) != null) {
                    latch.countDown();
                }
            }
            if ((event.getType()) == (EventType.None)) {
                return;
            }
            try {
                events.put(event);
            } catch (InterruptedException e) {
                Assert.assertTrue("interruption unexpected", false);
            }
        }

        public void verify(List<EventType> expected) throws InterruptedException {
            WatchedEvent event;
            int count = 0;
            while ((count < (expected.size())) && ((event = events.poll(30, TimeUnit.SECONDS)) != null)) {
                Assert.assertEquals(expected.get(count), event.getType());
                count++;
            } 
            Assert.assertEquals(expected.size(), count);
            events.clear();
        }
    }

    private WatcherFuncTest.SimpleWatcher client_dwatch;

    private volatile CountDownLatch client_latch;

    private ZooKeeper client;

    private WatcherFuncTest.SimpleWatcher lsnr_dwatch;

    private volatile CountDownLatch lsnr_latch;

    private ZooKeeper lsnr;

    private List<EventType> expected;

    @Test
    public void testExistsSync() throws IOException, InterruptedException, KeeperException {
        Assert.assertNull(lsnr.exists("/foo", true));
        Assert.assertNull(lsnr.exists("/foo/bar", true));
        client.create("/foo", "parent".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        expected.add(NodeCreated);
        client.create("/foo/bar", "child".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        expected.add(NodeCreated);
        verify();
        Assert.assertNotNull(lsnr.exists("/foo", true));
        Assert.assertNotNull(lsnr.exists("/foo/bar", true));
        try {
            Assert.assertNull(lsnr.exists("/car", true));
            client.setData("/car", "missing".getBytes(), (-1));
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/car", e.getPath());
        }
        try {
            Assert.assertNull(lsnr.exists("/foo/car", true));
            client.setData("/foo/car", "missing".getBytes(), (-1));
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo/car", e.getPath());
        }
        client.setData("/foo", "parent".getBytes(), (-1));
        expected.add(NodeDataChanged);
        client.setData("/foo/bar", "child".getBytes(), (-1));
        expected.add(NodeDataChanged);
        verify();
        Assert.assertNotNull(lsnr.exists("/foo", true));
        Assert.assertNotNull(lsnr.exists("/foo/bar", true));
        client.delete("/foo/bar", (-1));
        expected.add(NodeDeleted);
        client.delete("/foo", (-1));
        expected.add(NodeDeleted);
        verify();
    }

    @Test
    public void testGetDataSync() throws IOException, InterruptedException, KeeperException {
        try {
            lsnr.getData("/foo", true, null);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo", e.getPath());
        }
        try {
            lsnr.getData("/foo/bar", true, null);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo/bar", e.getPath());
        }
        client.create("/foo", "parent".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertNotNull(lsnr.getData("/foo", true, null));
        client.create("/foo/bar", "child".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertNotNull(lsnr.getData("/foo/bar", true, null));
        client.setData("/foo", "parent".getBytes(), (-1));
        expected.add(NodeDataChanged);
        client.setData("/foo/bar", "child".getBytes(), (-1));
        expected.add(NodeDataChanged);
        verify();
        Assert.assertNotNull(lsnr.getData("/foo", true, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", true, null));
        client.delete("/foo/bar", (-1));
        expected.add(NodeDeleted);
        client.delete("/foo", (-1));
        expected.add(NodeDeleted);
        verify();
    }

    @Test
    public void testGetChildrenSync() throws IOException, InterruptedException, KeeperException {
        try {
            lsnr.getChildren("/foo", true);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo", e.getPath());
        }
        try {
            lsnr.getChildren("/foo/bar", true);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo/bar", e.getPath());
        }
        client.create("/foo", "parent".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertNotNull(lsnr.getChildren("/foo", true));
        client.create("/foo/bar", "child".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        expected.add(NodeChildrenChanged);// /foo

        Assert.assertNotNull(lsnr.getChildren("/foo/bar", true));
        client.setData("/foo", "parent".getBytes(), (-1));
        client.setData("/foo/bar", "child".getBytes(), (-1));
        Assert.assertNotNull(lsnr.exists("/foo", true));
        Assert.assertNotNull(lsnr.getChildren("/foo", true));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", true));
        client.delete("/foo/bar", (-1));
        expected.add(NodeDeleted);// /foo/bar childwatch

        expected.add(NodeChildrenChanged);// /foo

        client.delete("/foo", (-1));
        expected.add(NodeDeleted);
        verify();
    }

    @Test
    public void testExistsSyncWObj() throws IOException, InterruptedException, KeeperException {
        WatcherFuncTest.SimpleWatcher w1 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w2 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w3 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w4 = new WatcherFuncTest.SimpleWatcher(null);
        List<EventType> e2 = new ArrayList<EventType>();
        Assert.assertNull(lsnr.exists("/foo", true));
        Assert.assertNull(lsnr.exists("/foo", w1));
        Assert.assertNull(lsnr.exists("/foo/bar", w2));
        Assert.assertNull(lsnr.exists("/foo/bar", w3));
        Assert.assertNull(lsnr.exists("/foo/bar", w3));
        Assert.assertNull(lsnr.exists("/foo/bar", w4));
        client.create("/foo", "parent".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        expected.add(NodeCreated);
        client.create("/foo/bar", "child".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        e2.add(NodeCreated);
        lsnr_dwatch.verify(expected);
        w1.verify(expected);
        w2.verify(e2);
        w3.verify(e2);
        w4.verify(e2);
        expected.clear();
        e2.clear();
        // default not registered
        Assert.assertNotNull(lsnr.exists("/foo", w1));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w2));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w3));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w4));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w4));
        client.setData("/foo", "parent".getBytes(), (-1));
        expected.add(NodeDataChanged);
        client.setData("/foo/bar", "child".getBytes(), (-1));
        e2.add(NodeDataChanged);
        lsnr_dwatch.verify(new ArrayList<EventType>());// not reg so should = 0

        w1.verify(expected);
        w2.verify(e2);
        w3.verify(e2);
        w4.verify(e2);
        expected.clear();
        e2.clear();
        Assert.assertNotNull(lsnr.exists("/foo", true));
        Assert.assertNotNull(lsnr.exists("/foo", w1));
        Assert.assertNotNull(lsnr.exists("/foo", w1));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w2));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w2));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w3));
        Assert.assertNotNull(lsnr.exists("/foo/bar", w4));
        client.delete("/foo/bar", (-1));
        expected.add(NodeDeleted);
        client.delete("/foo", (-1));
        e2.add(NodeDeleted);
        lsnr_dwatch.verify(expected);
        w1.verify(expected);
        w2.verify(e2);
        w3.verify(e2);
        w4.verify(e2);
        expected.clear();
        e2.clear();
    }

    @Test
    public void testGetDataSyncWObj() throws IOException, InterruptedException, KeeperException {
        WatcherFuncTest.SimpleWatcher w1 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w2 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w3 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w4 = new WatcherFuncTest.SimpleWatcher(null);
        List<EventType> e2 = new ArrayList<EventType>();
        try {
            lsnr.getData("/foo", w1, null);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo", e.getPath());
        }
        try {
            lsnr.getData("/foo/bar", w2, null);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo/bar", e.getPath());
        }
        client.create("/foo", "parent".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertNotNull(lsnr.getData("/foo", true, null));
        Assert.assertNotNull(lsnr.getData("/foo", w1, null));
        client.create("/foo/bar", "child".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertNotNull(lsnr.getData("/foo/bar", w2, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w3, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w4, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w4, null));
        client.setData("/foo", "parent".getBytes(), (-1));
        expected.add(NodeDataChanged);
        client.setData("/foo/bar", "child".getBytes(), (-1));
        e2.add(NodeDataChanged);
        lsnr_dwatch.verify(expected);
        w1.verify(expected);
        w2.verify(e2);
        w3.verify(e2);
        w4.verify(e2);
        expected.clear();
        e2.clear();
        Assert.assertNotNull(lsnr.getData("/foo", true, null));
        Assert.assertNotNull(lsnr.getData("/foo", w1, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w2, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w3, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w3, null));
        Assert.assertNotNull(lsnr.getData("/foo/bar", w4, null));
        client.delete("/foo/bar", (-1));
        expected.add(NodeDeleted);
        client.delete("/foo", (-1));
        e2.add(NodeDeleted);
        lsnr_dwatch.verify(expected);
        w1.verify(expected);
        w2.verify(e2);
        w3.verify(e2);
        w4.verify(e2);
        expected.clear();
        e2.clear();
    }

    @Test
    public void testGetChildrenSyncWObj() throws IOException, InterruptedException, KeeperException {
        WatcherFuncTest.SimpleWatcher w1 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w2 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w3 = new WatcherFuncTest.SimpleWatcher(null);
        WatcherFuncTest.SimpleWatcher w4 = new WatcherFuncTest.SimpleWatcher(null);
        List<EventType> e2 = new ArrayList<EventType>();
        try {
            lsnr.getChildren("/foo", true);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo", e.getPath());
        }
        try {
            lsnr.getChildren("/foo/bar", true);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(NONODE, e.code());
            Assert.assertEquals("/foo/bar", e.getPath());
        }
        client.create("/foo", "parent".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertNotNull(lsnr.getChildren("/foo", true));
        Assert.assertNotNull(lsnr.getChildren("/foo", w1));
        client.create("/foo/bar", "child".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        expected.add(NodeChildrenChanged);// /foo

        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w2));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w2));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w3));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w4));
        client.setData("/foo", "parent".getBytes(), (-1));
        client.setData("/foo/bar", "child".getBytes(), (-1));
        Assert.assertNotNull(lsnr.exists("/foo", true));
        Assert.assertNotNull(lsnr.exists("/foo", w1));
        Assert.assertNotNull(lsnr.exists("/foo", true));
        Assert.assertNotNull(lsnr.exists("/foo", w1));
        Assert.assertNotNull(lsnr.getChildren("/foo", true));
        Assert.assertNotNull(lsnr.getChildren("/foo", w1));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w2));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w3));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w4));
        Assert.assertNotNull(lsnr.getChildren("/foo/bar", w4));
        client.delete("/foo/bar", (-1));
        e2.add(NodeDeleted);// /foo/bar childwatch

        expected.add(NodeChildrenChanged);// /foo

        client.delete("/foo", (-1));
        expected.add(NodeDeleted);
        lsnr_dwatch.verify(expected);
        w1.verify(expected);
        w2.verify(e2);
        w3.verify(e2);
        w4.verify(e2);
        expected.clear();
        e2.clear();
    }
}

