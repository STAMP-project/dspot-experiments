/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.beans;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.geode.internal.cache.CacheServerImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.tier.sockets.AcceptorImpl;
import org.apache.geode.management.internal.beans.stats.MBeanStatsMonitor;
import org.junit.Test;


/**
 * JMX and membership should not deadlock on CacheFactory.getAnyInstance.
 *
 * <p>
 * GEODE-3407: JMX and membership may deadlock on CacheFactory.getAnyInstance
 */
public class CacheServerBridgeClientMembershipRegressionTest {
    private final AtomicBoolean after = new AtomicBoolean();

    private final AtomicBoolean before = new AtomicBoolean();

    private ExecutorService synchronizing;

    private ExecutorService blocking;

    private CountDownLatch latch;

    private InternalCache cache;

    private CacheServerImpl cacheServer;

    private AcceptorImpl acceptor;

    private MBeanStatsMonitor monitor;

    private CacheServerBridge cacheServerBridge;

    @Test
    public void getNumSubscriptionsDeadlocksOnCacheFactory() throws Exception {
        givenCacheFactoryIsSynchronized();
        givenCacheServerBridge();
        blocking.execute(() -> {
            try {
                before.set(true);
                // getNumSubscriptions -> getClientQueueSizes -> synchronizes on CacheFactory
                cacheServerBridge.getNumSubscriptions();
            } finally {
                after.set(true);
            }
        });
        await().until(() -> before.get());
        // if deadlocked, then this line will throw ConditionTimeoutException
        await().untilAsserted(() -> assertThat(after.get()).isTrue());
    }
}

