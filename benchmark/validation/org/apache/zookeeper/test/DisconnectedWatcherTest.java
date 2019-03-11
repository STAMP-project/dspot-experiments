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
import CreateMode.PERSISTENT_SEQUENTIAL;
import EventType.NodeChildrenChanged;
import EventType.NodeCreated;
import EventType.NodeDataChanged;
import Ids.OPEN_ACL_UNSAFE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DisconnectedWatcherTest extends ClientBase {
    protected static final Logger LOG = LoggerFactory.getLogger(DisconnectedWatcherTest.class);

    final int TIMEOUT = 5000;

    private class MyWatcher extends ClientBase.CountdownWatcher {
        LinkedBlockingQueue<WatchedEvent> events = new LinkedBlockingQueue<WatchedEvent>();

        public void process(WatchedEvent event) {
            super.process(event);
            if ((event.getType()) != (EventType.None)) {
                try {
                    events.put(event);
                } catch (InterruptedException e) {
                    DisconnectedWatcherTest.LOG.warn("ignoring interrupt during event.put");
                }
            }
        }
    }

    // @see jira issue ZOOKEEPER-961
    @Test
    public void testChildWatcherAutoResetWithChroot() throws Exception {
        ZooKeeper zk1 = createClient();
        zk1.create("/ch1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        DisconnectedWatcherTest.MyWatcher watcher = new DisconnectedWatcherTest.MyWatcher();
        ZooKeeper zk2 = createClient(watcher, ((hostPort) + "/ch1"));
        zk2.getChildren("/", true);
        // this call shouldn't trigger any error or watch
        zk1.create("/youdontmatter1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        // this should trigger the watch
        zk1.create("/ch1/youshouldmatter1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        WatchedEvent e = watcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(e);
        Assert.assertEquals(NodeChildrenChanged, e.getType());
        Assert.assertEquals("/", e.getPath());
        DisconnectedWatcherTest.MyWatcher childWatcher = new DisconnectedWatcherTest.MyWatcher();
        zk2.getChildren("/", childWatcher);
        stopServer();
        watcher.waitForDisconnected(3000);
        startServer();
        watcher.waitForConnected(3000);
        // this should trigger the watch
        zk1.create("/ch1/youshouldmatter2", null, OPEN_ACL_UNSAFE, PERSISTENT);
        e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(e);
        Assert.assertEquals(NodeChildrenChanged, e.getType());
        Assert.assertEquals("/", e.getPath());
    }

    @Test
    public void testDefaultWatcherAutoResetWithChroot() throws Exception {
        ZooKeeper zk1 = createClient();
        zk1.create("/ch1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        DisconnectedWatcherTest.MyWatcher watcher = new DisconnectedWatcherTest.MyWatcher();
        ZooKeeper zk2 = createClient(watcher, ((hostPort) + "/ch1"));
        zk2.getChildren("/", true);
        // this call shouldn't trigger any error or watch
        zk1.create("/youdontmatter1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        // this should trigger the watch
        zk1.create("/ch1/youshouldmatter1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        WatchedEvent e = watcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(e);
        Assert.assertEquals(NodeChildrenChanged, e.getType());
        Assert.assertEquals("/", e.getPath());
        zk2.getChildren("/", true);
        stopServer();
        watcher.waitForDisconnected(3000);
        startServer();
        watcher.waitForConnected(3000);
        // this should trigger the watch
        zk1.create("/ch1/youshouldmatter2", null, OPEN_ACL_UNSAFE, PERSISTENT);
        e = watcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(e);
        Assert.assertEquals(NodeChildrenChanged, e.getType());
        Assert.assertEquals("/", e.getPath());
    }

    @Test
    public void testDeepChildWatcherAutoResetWithChroot() throws Exception {
        ZooKeeper zk1 = createClient();
        zk1.create("/ch1", null, OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.create("/ch1/here", null, OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.create("/ch1/here/we", null, OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.create("/ch1/here/we/are", null, OPEN_ACL_UNSAFE, PERSISTENT);
        DisconnectedWatcherTest.MyWatcher watcher = new DisconnectedWatcherTest.MyWatcher();
        ZooKeeper zk2 = createClient(watcher, ((hostPort) + "/ch1/here/we"));
        zk2.getChildren("/are", true);
        // this should trigger the watch
        zk1.create("/ch1/here/we/are/now", null, OPEN_ACL_UNSAFE, PERSISTENT);
        WatchedEvent e = watcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(e);
        Assert.assertEquals(NodeChildrenChanged, e.getType());
        Assert.assertEquals("/are", e.getPath());
        DisconnectedWatcherTest.MyWatcher childWatcher = new DisconnectedWatcherTest.MyWatcher();
        zk2.getChildren("/are", childWatcher);
        stopServer();
        watcher.waitForDisconnected(3000);
        startServer();
        watcher.waitForConnected(3000);
        // this should trigger the watch
        zk1.create("/ch1/here/we/are/again", null, OPEN_ACL_UNSAFE, PERSISTENT);
        e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(e);
        Assert.assertEquals(NodeChildrenChanged, e.getType());
        Assert.assertEquals("/are", e.getPath());
    }

    // @see jira issue ZOOKEEPER-706. Test auto reset of a large number of
    // watches which require multiple SetWatches calls.
    @Test(timeout = 840000)
    public void testManyChildWatchersAutoReset() throws Exception {
        ZooKeeper zk1 = createClient();
        DisconnectedWatcherTest.MyWatcher watcher = new DisconnectedWatcherTest.MyWatcher();
        ZooKeeper zk2 = createClient(watcher);
        // 110 character base path
        String pathBase = "/long-path-000000000-111111111-222222222-333333333-444444444-" + "555555555-666666666-777777777-888888888-999999999";
        zk1.create(pathBase, null, OPEN_ACL_UNSAFE, PERSISTENT);
        // Create 10,000 nodes. This should ensure the length of our
        // watches set below exceeds 1MB.
        List<String> paths = new ArrayList<String>();
        for (int i = 0; i < 10000; i++) {
            String path = zk1.create((pathBase + "/ch-"), null, OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL);
            paths.add(path);
        }
        DisconnectedWatcherTest.LOG.info("Created 10,000 nodes.");
        DisconnectedWatcherTest.MyWatcher childWatcher = new DisconnectedWatcherTest.MyWatcher();
        // Set a combination of child/exists/data watches
        int i = 0;
        for (String path : paths) {
            if ((i % 3) == 0) {
                zk2.getChildren(path, childWatcher);
            } else
                if ((i % 3) == 1) {
                    zk2.exists((path + "/foo"), childWatcher);
                } else
                    if ((i % 3) == 2) {
                        zk2.getData(path, childWatcher, null);
                    }


            i++;
        }
        stopServer();
        watcher.waitForDisconnected(30000);
        startServer();
        watcher.waitForConnected(30000);
        // Trigger the watches and ensure they properly propagate to the client
        i = 0;
        for (String path : paths) {
            if ((i % 3) == 0) {
                zk1.create((path + "/ch"), null, OPEN_ACL_UNSAFE, PERSISTENT);
                WatchedEvent e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                Assert.assertNotNull(e);
                Assert.assertEquals(NodeChildrenChanged, e.getType());
                Assert.assertEquals(path, e.getPath());
            } else
                if ((i % 3) == 1) {
                    zk1.create((path + "/foo"), null, OPEN_ACL_UNSAFE, PERSISTENT);
                    WatchedEvent e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                    Assert.assertNotNull(e);
                    Assert.assertEquals(NodeCreated, e.getType());
                    Assert.assertEquals((path + "/foo"), e.getPath());
                } else
                    if ((i % 3) == 2) {
                        zk1.setData(path, new byte[]{ 1, 2, 3 }, (-1));
                        WatchedEvent e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                        Assert.assertNotNull(e);
                        Assert.assertEquals(NodeDataChanged, e.getType());
                        Assert.assertEquals(path, e.getPath());
                    }


            i++;
        }
    }
}

