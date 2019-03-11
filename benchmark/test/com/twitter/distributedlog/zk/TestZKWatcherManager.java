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
package com.twitter.distributedlog.zk;


import NullStatsLogger.INSTANCE;
import Watcher.Event.EventType;
import Watcher.Event.KeeperState;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Assert;
import org.junit.Test;


public class TestZKWatcherManager {
    @Test(timeout = 60000)
    public void testRegisterUnregisterWatcher() throws Exception {
        ZKWatcherManager watcherManager = ZKWatcherManager.newBuilder().name("test-register-unregister-watcher").statsLogger(INSTANCE).build();
        String path = "/test-register-unregister-watcher";
        final List<WatchedEvent> events = new LinkedList<WatchedEvent>();
        final CountDownLatch latch = new CountDownLatch(2);
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                events.add(event);
                latch.countDown();
            }
        };
        watcherManager.registerChildWatcher(path, watcher);
        // fire the event
        WatchedEvent event0 = new WatchedEvent(EventType.NodeCreated, KeeperState.SyncConnected, path);
        WatchedEvent event1 = new WatchedEvent(EventType.None, KeeperState.SyncConnected, path);
        WatchedEvent event2 = new WatchedEvent(EventType.NodeChildrenChanged, KeeperState.SyncConnected, path);
        watcher.process(event1);
        watcher.process(event2);
        latch.await();
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(event1, events.get(0));
        Assert.assertEquals(event2, events.get(1));
        // unregister watcher
        watcherManager.unregisterChildWatcher(path, watcher);
        Assert.assertEquals(0, watcherManager.childWatches.size());
    }
}

