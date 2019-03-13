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
package com.twitter.distributedlog.impl;


import CreateMode.PERSISTENT;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.google.common.collect.Sets;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClientUtils;
import com.twitter.distributedlog.callback.NamespaceListener;
import com.twitter.distributedlog.util.DLUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test ZK Namespace Watcher.
 */
public class TestZKNamespaceWatcher extends TestDistributedLogBase {
    private static final int zkSessionTimeoutMs = 2000;

    @Rule
    public TestName runtime = new TestName();

    protected final DistributedLogConfiguration baseConf = new DistributedLogConfiguration();

    protected ZooKeeperClient zkc;

    protected OrderedScheduler scheduler;

    @Test(timeout = 60000)
    public void testNamespaceListener() throws Exception {
        URI uri = createDLMURI(("/" + (runtime.getMethodName())));
        zkc.get().create(uri.getPath(), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.addConfiguration(baseConf);
        ZKNamespaceWatcher watcher = new ZKNamespaceWatcher(conf, uri, zkc, scheduler);
        final CountDownLatch[] latches = new CountDownLatch[10];
        for (int i = 0; i < 10; i++) {
            latches[i] = new CountDownLatch(1);
        }
        final AtomicInteger numUpdates = new AtomicInteger(0);
        final AtomicReference<Set<String>> receivedLogs = new AtomicReference<Set<String>>(null);
        watcher.registerListener(new NamespaceListener() {
            @Override
            public void onStreamsChanged(Iterator<String> streams) {
                Set<String> streamSet = Sets.newHashSet(streams);
                int updates = numUpdates.incrementAndGet();
                receivedLogs.set(streamSet);
                latches[(updates - 1)].countDown();
            }
        });
        // first update
        final Set<String> expectedLogs = Sets.newHashSet();
        latches[0].await();
        validateReceivedLogs(expectedLogs, receivedLogs.get());
        // create test1
        expectedLogs.add("test1");
        createLogInNamespace(uri, "test1");
        latches[1].await();
        validateReceivedLogs(expectedLogs, receivedLogs.get());
        // create invalid log
        createLogInNamespace(uri, ".test1");
        latches[2].await();
        validateReceivedLogs(expectedLogs, receivedLogs.get());
        // create test2
        expectedLogs.add("test2");
        createLogInNamespace(uri, "test2");
        latches[3].await();
        validateReceivedLogs(expectedLogs, receivedLogs.get());
        // delete test1
        expectedLogs.remove("test1");
        deleteLogInNamespace(uri, "test1");
        latches[4].await();
        validateReceivedLogs(expectedLogs, receivedLogs.get());
    }

    @Test(timeout = 60000)
    public void testSessionExpired() throws Exception {
        URI uri = createDLMURI(("/" + (runtime.getMethodName())));
        zkc.get().create(uri.getPath(), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.addConfiguration(baseConf);
        ZKNamespaceWatcher watcher = new ZKNamespaceWatcher(conf, uri, zkc, scheduler);
        final CountDownLatch[] latches = new CountDownLatch[10];
        for (int i = 0; i < 10; i++) {
            latches[i] = new CountDownLatch(1);
        }
        final AtomicInteger numUpdates = new AtomicInteger(0);
        final AtomicReference<Set<String>> receivedLogs = new AtomicReference<Set<String>>(null);
        watcher.registerListener(new NamespaceListener() {
            @Override
            public void onStreamsChanged(Iterator<String> streams) {
                Set<String> streamSet = Sets.newHashSet(streams);
                int updates = numUpdates.incrementAndGet();
                receivedLogs.set(streamSet);
                latches[(updates - 1)].countDown();
            }
        });
        latches[0].await();
        createLogInNamespace(uri, "test1");
        latches[1].await();
        createLogInNamespace(uri, "test2");
        latches[2].await();
        Assert.assertEquals(2, receivedLogs.get().size());
        ZooKeeperClientUtils.expireSession(zkc, DLUtils.getZKServersFromDLUri(uri), TestZKNamespaceWatcher.zkSessionTimeoutMs);
        latches[3].await();
        Assert.assertEquals(2, receivedLogs.get().size());
        createLogInNamespace(uri, "test3");
        latches[4].await();
        Assert.assertEquals(3, receivedLogs.get().size());
    }
}

