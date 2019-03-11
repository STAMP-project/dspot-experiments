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
import Event.EventType;
import Event.KeeperState;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Watcher.Event.EventType.NodeDataChanged;
import static Watcher.Event.KeeperState.SyncConnected;


public class SessionTimeoutTest extends ClientBase {
    protected static final Logger LOG = LoggerFactory.getLogger(SessionTimeoutTest.class);

    private TestableZooKeeper zk;

    @Test
    public void testSessionExpiration() throws InterruptedException, KeeperException {
        final CountDownLatch expirationLatch = new CountDownLatch(1);
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if ((event.getState()) == (KeeperState.Expired)) {
                    expirationLatch.countDown();
                }
            }
        };
        zk.exists("/foo", watcher);
        getTestable().injectSessionExpiration();
        Assert.assertTrue(expirationLatch.await(5, TimeUnit.SECONDS));
        boolean gotException = false;
        try {
            zk.exists("/foo", false);
            Assert.fail("Should have thrown a SessionExpiredException");
        } catch (KeeperException e) {
            // correct
            gotException = true;
        }
        Assert.assertTrue(gotException);
    }

    @Test
    public void testQueueEvent() throws InterruptedException, KeeperException {
        final CountDownLatch eventLatch = new CountDownLatch(1);
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if ((event.getType()) == (EventType.NodeDataChanged)) {
                    if (event.getPath().equals("/foo/bar")) {
                        eventLatch.countDown();
                    }
                }
            }
        };
        zk.exists("/foo/bar", watcher);
        WatchedEvent event = new WatchedEvent(NodeDataChanged, SyncConnected, "/foo/bar");
        getTestable().queueEvent(event);
        Assert.assertTrue(eventLatch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Make sure ephemerals get cleaned up when session disconnects.
     */
    @Test
    public void testSessionDisconnect() throws IOException, InterruptedException, KeeperException {
        zk.create("/sdisconnect", new byte[0], OPEN_ACL_UNSAFE, EPHEMERAL);
        Assert.assertNotNull("Ephemeral node has not been created", exists("/sdisconnect", null));
        close();
        zk = createClient();
        Assert.assertNull("Ephemeral node shouldn't exist after client disconnect", exists("/sdisconnect", null));
    }

    /**
     * Make sure ephemerals are kept when session restores.
     */
    @Test
    public void testSessionRestore() throws IOException, InterruptedException, KeeperException {
        zk.create("/srestore", new byte[0], OPEN_ACL_UNSAFE, EPHEMERAL);
        Assert.assertNotNull("Ephemeral node has not been created", exists("/srestore", null));
        zk.disconnect();
        close();
        zk = createClient();
        Assert.assertNotNull("Ephemeral node should be present when session is restored", exists("/srestore", null));
    }

    /**
     * Make sure ephemerals are kept when server restarts.
     */
    @Test
    public void testSessionSurviveServerRestart() throws Exception {
        zk.create("/sdeath", new byte[0], OPEN_ACL_UNSAFE, EPHEMERAL);
        Assert.assertNotNull("Ephemeral node has not been created", exists("/sdeath", null));
        zk.disconnect();
        stopServer();
        startServer();
        zk = createClient();
        Assert.assertNotNull("Ephemeral node should be present when server restarted", exists("/sdeath", null));
    }
}

