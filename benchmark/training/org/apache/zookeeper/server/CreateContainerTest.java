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
package org.apache.zookeeper.server;


import AsyncCallback.Create2Callback;
import CreateMode.CONTAINER;
import CreateMode.PERSISTENT;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


public class CreateContainerTest extends ClientBase {
    private ZooKeeper zk;

    @Test(timeout = 30000)
    public void testCreate() throws InterruptedException, KeeperException {
        createNoStatVerifyResult("/foo");
        createNoStatVerifyResult("/foo/child");
    }

    @Test(timeout = 30000)
    public void testCreateWithStat() throws InterruptedException, KeeperException {
        Stat stat = createWithStatVerifyResult("/foo");
        Stat childStat = createWithStatVerifyResult("/foo/child");
        // Don't expect to get the same stats for different creates.
        Assert.assertFalse(stat.equals(childStat));
    }

    @SuppressWarnings("ConstantConditions")
    @Test(timeout = 30000)
    public void testCreateWithNullStat() throws InterruptedException, KeeperException {
        final String name = "/foo";
        Assert.assertNull(zk.exists(name, false));
        Stat stat = null;
        // If a null Stat object is passed the create should still
        // succeed, but no Stat info will be returned.
        zk.create(name, name.getBytes(), OPEN_ACL_UNSAFE, CONTAINER, stat);
        Assert.assertNull(stat);
        Assert.assertNotNull(zk.exists(name, false));
    }

    @Test(timeout = 30000)
    public void testSimpleDeletion() throws InterruptedException, KeeperException {
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        zk.create("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.delete("/foo/bar", (-1));// should cause "/foo" to get deleted when checkContainers() is called

        ContainerManager containerManager = new ContainerManager(serverFactory.getZooKeeperServer().getZKDatabase(), serverFactory.getZooKeeperServer().firstProcessor, 1, 100);
        containerManager.checkContainers();
        Thread.sleep(1000);
        Assert.assertNull("Container should have been deleted", zk.exists("/foo", false));
    }

    @Test(timeout = 30000)
    public void testMultiWithContainerSimple() throws InterruptedException, KeeperException {
        Op createContainer = Op.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        zk.multi(Collections.singletonList(createContainer));
        DataTree dataTree = serverFactory.getZooKeeperServer().getZKDatabase().getDataTree();
        Assert.assertEquals(dataTree.getContainers().size(), 1);
    }

    @Test(timeout = 30000)
    public void testMultiWithContainer() throws InterruptedException, KeeperException {
        Op createContainer = Op.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        Op createChild = Op.create("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.multi(Arrays.asList(createContainer, createChild));
        DataTree dataTree = serverFactory.getZooKeeperServer().getZKDatabase().getDataTree();
        Assert.assertEquals(dataTree.getContainers().size(), 1);
        zk.delete("/foo/bar", (-1));// should cause "/foo" to get deleted when checkContainers() is called

        ContainerManager containerManager = new ContainerManager(serverFactory.getZooKeeperServer().getZKDatabase(), serverFactory.getZooKeeperServer().firstProcessor, 1, 100);
        containerManager.checkContainers();
        Thread.sleep(1000);
        Assert.assertNull("Container should have been deleted", zk.exists("/foo", false));
        createContainer = Op.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        createChild = Op.create("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Op deleteChild = Op.delete("/foo/bar", (-1));
        zk.multi(Arrays.asList(createContainer, createChild, deleteChild));
        containerManager.checkContainers();
        Thread.sleep(1000);
        Assert.assertNull("Container should have been deleted", zk.exists("/foo", false));
    }

    @Test(timeout = 30000)
    public void testSimpleDeletionAsync() throws InterruptedException, KeeperException {
        final CountDownLatch latch = new CountDownLatch(1);
        AsyncCallback.Create2Callback cb = new AsyncCallback.Create2Callback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
                Assert.assertEquals(ctx, "context");
                latch.countDown();
            }
        };
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER, cb, "context");
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        zk.create("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.delete("/foo/bar", (-1));// should cause "/foo" to get deleted when checkContainers() is called

        ContainerManager containerManager = new ContainerManager(serverFactory.getZooKeeperServer().getZKDatabase(), serverFactory.getZooKeeperServer().firstProcessor, 1, 100);
        containerManager.checkContainers();
        Thread.sleep(1000);
        Assert.assertNull("Container should have been deleted", zk.exists("/foo", false));
    }

    @Test(timeout = 30000)
    public void testCascadingDeletion() throws InterruptedException, KeeperException {
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        zk.create("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        zk.create("/foo/bar/one", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.delete("/foo/bar/one", (-1));// should cause "/foo/bar" and "/foo" to get deleted when checkContainers() is called

        ContainerManager containerManager = new ContainerManager(serverFactory.getZooKeeperServer().getZKDatabase(), serverFactory.getZooKeeperServer().firstProcessor, 1, 100);
        containerManager.checkContainers();
        Thread.sleep(1000);
        containerManager.checkContainers();
        Thread.sleep(1000);
        Assert.assertNull("Container should have been deleted", zk.exists("/foo/bar", false));
        Assert.assertNull("Container should have been deleted", zk.exists("/foo", false));
    }

    @Test(timeout = 30000)
    public void testFalseEmpty() throws InterruptedException, KeeperException {
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, CONTAINER);
        zk.create("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        ContainerManager containerManager = new ContainerManager(serverFactory.getZooKeeperServer().getZKDatabase(), serverFactory.getZooKeeperServer().firstProcessor, 1, 100) {
            @Override
            protected Collection<String> getCandidates() {
                return Collections.singletonList("/foo");
            }
        };
        containerManager.checkContainers();
        Thread.sleep(1000);
        Assert.assertNotNull("Container should have not been deleted", zk.exists("/foo", false));
    }

    @Test(timeout = 30000)
    public void testMaxPerMinute() throws InterruptedException {
        final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        RequestProcessor processor = new RequestProcessor() {
            @Override
            public void processRequest(Request request) {
                queue.add(new String(request.request.array()));
            }

            @Override
            public void shutdown() {
            }
        };
        final ContainerManager containerManager = new ContainerManager(serverFactory.getZooKeeperServer().getZKDatabase(), processor, 1, 2) {
            @Override
            protected long getMinIntervalMs() {
                return 1000;
            }

            @Override
            protected Collection<String> getCandidates() {
                return Arrays.asList("/one", "/two", "/three", "/four");
            }
        };
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                containerManager.checkContainers();
                return null;
            }
        });
        Assert.assertEquals(queue.poll(5, TimeUnit.SECONDS), "/one");
        Assert.assertEquals(queue.poll(5, TimeUnit.SECONDS), "/two");
        Assert.assertEquals(queue.size(), 0);
        Thread.sleep(500);
        Assert.assertEquals(queue.size(), 0);
        Assert.assertEquals(queue.poll(5, TimeUnit.SECONDS), "/three");
        Assert.assertEquals(queue.poll(5, TimeUnit.SECONDS), "/four");
    }
}

