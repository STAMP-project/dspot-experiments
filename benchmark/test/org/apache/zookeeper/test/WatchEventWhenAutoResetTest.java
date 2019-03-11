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
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WatchEventWhenAutoResetTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(WatchEventWhenAutoResetTest.class);

    // waiting time for expected condition
    private static final int TIMEOUT = 30000;

    private QuorumUtil qu;

    private WatchEventWhenAutoResetTest.EventsWatcher watcher;

    private ZooKeeper zk1;

    private ZooKeeper zk2;

    public static class EventsWatcher extends ClientBase.CountdownWatcher {
        private LinkedBlockingQueue<WatchedEvent> dataEvents = new LinkedBlockingQueue<WatchedEvent>();

        @Override
        public void process(WatchedEvent event) {
            super.process(event);
            try {
                if ((event.getType()) != (EventType.None)) {
                    dataEvents.put(event);
                }
            } catch (InterruptedException e) {
                WatchEventWhenAutoResetTest.LOG.warn("ignoring interrupt during EventsWatcher process");
            }
        }

        public void assertEvent(long timeout, EventType eventType) {
            try {
                WatchedEvent event = dataEvents.poll(timeout, TimeUnit.MILLISECONDS);
                Assert.assertNotNull(("do not receive a " + eventType), event);
                Assert.assertEquals(eventType, event.getType());
            } catch (InterruptedException e) {
                WatchEventWhenAutoResetTest.LOG.warn("ignoring interrupt during EventsWatcher assertEvent");
            }
        }
    }

    @Test
    public void testNodeDataChanged() throws Exception {
        String path = "/test-changed";
        zk1.create(path, new byte[1], OPEN_ACL_UNSAFE, PERSISTENT);
        Stat stat1 = zk1.exists(path, watcher);
        qu.shutdown(1);
        zk2.setData(path, new byte[2], stat1.getVersion());
        qu.start(1);
        watcher.waitForConnected(WatchEventWhenAutoResetTest.TIMEOUT);
        watcher.assertEvent(WatchEventWhenAutoResetTest.TIMEOUT, NodeDataChanged);
    }

    @Test
    public void testNodeCreated() throws Exception {
        String path = "/test1-created";
        zk1.exists(path, watcher);
        qu.shutdown(1);
        zk2.create(path, new byte[2], OPEN_ACL_UNSAFE, PERSISTENT);
        qu.start(1);
        watcher.waitForConnected(((WatchEventWhenAutoResetTest.TIMEOUT) * 1000L));
        watcher.assertEvent(WatchEventWhenAutoResetTest.TIMEOUT, NodeCreated);
    }

    @Test
    public void testNodeDeleted() throws Exception {
        String path = "/test-deleted";
        zk1.create(path, new byte[1], OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.getData(path, watcher, null);
        qu.shutdown(1);
        zk2.delete(path, (-1));
        qu.start(1);
        watcher.waitForConnected(((WatchEventWhenAutoResetTest.TIMEOUT) * 1000L));
        watcher.assertEvent(WatchEventWhenAutoResetTest.TIMEOUT, NodeDeleted);
        zk1.create(path, new byte[1], OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.exists(path, watcher);
        qu.shutdown(1);
        zk2.delete(path, (-1));
        qu.start(1);
        watcher.waitForConnected(((WatchEventWhenAutoResetTest.TIMEOUT) * 1000L));
        watcher.assertEvent(WatchEventWhenAutoResetTest.TIMEOUT, NodeDeleted);
        zk1.create(path, new byte[1], OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.getChildren(path, watcher);
        qu.shutdown(1);
        zk2.delete(path, (-1));
        qu.start(1);
        watcher.waitForConnected(((WatchEventWhenAutoResetTest.TIMEOUT) * 1000L));
        watcher.assertEvent(WatchEventWhenAutoResetTest.TIMEOUT, NodeDeleted);
    }

    @Test
    public void testNodeChildrenChanged() throws Exception {
        String path = "/test-children-changed";
        zk1.create(path, new byte[1], OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.getChildren(path, watcher);
        qu.shutdown(1);
        zk2.create((path + "/children-1"), new byte[2], OPEN_ACL_UNSAFE, PERSISTENT);
        qu.start(1);
        watcher.waitForConnected(((WatchEventWhenAutoResetTest.TIMEOUT) * 1000L));
        watcher.assertEvent(WatchEventWhenAutoResetTest.TIMEOUT, NodeChildrenChanged);
    }
}

