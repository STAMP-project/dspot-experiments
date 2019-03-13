/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.server.cluster.zookeeper;


import CreateMode.EPHEMERAL;
import Watcher.Event.EventType.NodeChildrenChanged;
import ZKPaths.PathAndNode;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.navercorp.pinpoint.common.server.cluster.zookeeper.exception.BadOperationException;
import com.navercorp.pinpoint.common.server.util.TestAwaitTaskUtils;
import com.navercorp.pinpoint.common.server.util.TestAwaitUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Taejin Koo
 */
public class CuratorZookeeperClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorZookeeperClientTest.class);

    private static TestAwaitUtils AWAIT_UTILS = new TestAwaitUtils(100, 3000);

    private static TestingServer ts = null;

    private static CuratorZookeeperClientTest.EventHoldingZookeeperEventWatcher eventHoldingZookeeperEventWatcher;

    private static CuratorZookeeperClient curatorZookeeperClient;

    private static final String PARENT_PATH = "/a/b/c/";

    private static final AtomicInteger TEST_NODE_ID = new AtomicInteger();

    @Test
    public void createAndDeleteTest() throws Exception {
        ZooKeeper zooKeeper = createZookeeper();
        try {
            String message = CuratorZookeeperClientTest.createTestMessage();
            String testNodePath = CuratorZookeeperClientTest.createTestNodePath();
            CuratorZookeeperClientTest.curatorZookeeperClient.createPath(testNodePath);
            CuratorZookeeperClientTest.curatorZookeeperClient.createNode(testNodePath, message.getBytes());
            byte[] result = CuratorZookeeperClientTest.curatorZookeeperClient.getData(testNodePath);
            Assert.assertEquals(message, new String(result));
            CuratorZookeeperClientTest.curatorZookeeperClient.delete(testNodePath);
            Assert.assertFalse(isExistNode(zooKeeper, testNodePath));
        } finally {
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        }
    }

    @Test(expected = BadOperationException.class)
    public void alreadyExistNodeCreateTest() throws Exception {
        ZooKeeper zooKeeper = createZookeeper();
        try {
            String message = CuratorZookeeperClientTest.createTestMessage();
            String testNodePath = CuratorZookeeperClientTest.createTestNodePath();
            CuratorZookeeperClientTest.curatorZookeeperClient.createNode(testNodePath, message.getBytes());
            Assert.assertTrue(isExistNode(zooKeeper, testNodePath));
            CuratorZookeeperClientTest.curatorZookeeperClient.createNode(testNodePath, "test".getBytes());
        } finally {
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        }
    }

    @Test
    public void getTest() throws Exception {
        ZooKeeper zooKeeper = createZookeeper();
        try {
            String testNodePath = CuratorZookeeperClientTest.createTestNodePath();
            CuratorZookeeperClientTest.curatorZookeeperClient.createNode(testNodePath, "".getBytes());
            Assert.assertTrue(isExistNode(zooKeeper, testNodePath));
            CuratorZookeeperClientTest.curatorZookeeperClient.getData(testNodePath, true);
            String message = CuratorZookeeperClientTest.createTestMessage();
            zooKeeper.setData(testNodePath, message.getBytes(), (-1));
            assertGetWatchedEvent(testNodePath, message);
            message = CuratorZookeeperClientTest.createTestMessage();
            CuratorZookeeperClientTest.curatorZookeeperClient.createOrSetNode(testNodePath, message.getBytes());
            assertGetWatchedEvent(testNodePath, message);
        } finally {
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        }
    }

    @Test
    public void getChildrenTest() throws Exception {
        ZooKeeper zooKeeper = createZookeeper();
        try {
            String message = CuratorZookeeperClientTest.createTestMessage();
            String testNodePath = CuratorZookeeperClientTest.createTestNodePath();
            ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode(testNodePath);
            List<String> childrenNode = CuratorZookeeperClientTest.curatorZookeeperClient.getChildNodeList(pathAndNode.getPath(), true);
            Assert.assertTrue(childrenNode.isEmpty());
            zooKeeper.create(testNodePath, new byte[0], OPEN_ACL_UNSAFE, EPHEMERAL);
            boolean await = CuratorZookeeperClientTest.AWAIT_UTILS.await(new TestAwaitTaskUtils() {
                @Override
                public boolean checkCompleted() {
                    return (CuratorZookeeperClientTest.eventHoldingZookeeperEventWatcher.getLastWatchedEvent()) != null;
                }
            });
            Assert.assertTrue(await);
            WatchedEvent lastWatchedEvent = CuratorZookeeperClientTest.eventHoldingZookeeperEventWatcher.getLastWatchedEvent();
            Assert.assertEquals(NodeChildrenChanged, lastWatchedEvent.getType());
            childrenNode = CuratorZookeeperClientTest.curatorZookeeperClient.getChildNodeList(pathAndNode.getPath(), false);
            Assert.assertTrue((!(childrenNode.isEmpty())));
        } finally {
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        }
    }

    private static class EventHoldingZookeeperEventWatcher implements ZookeeperEventWatcher {
        private WatchedEvent watchedEvent;

        @Override
        public boolean handleConnected() {
            return true;
        }

        @Override
        public boolean handleDisconnected() {
            return true;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            synchronized(this) {
                CuratorZookeeperClientTest.LOGGER.info("process() event:{}", watchedEvent);
                this.watchedEvent = watchedEvent;
            }
        }

        synchronized WatchedEvent getLastWatchedEvent() {
            return watchedEvent;
        }

        void eventClear() {
            synchronized(this) {
                watchedEvent = null;
            }
        }
    }
}

