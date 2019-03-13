/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.zookeeper.cluster;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.camel.component.zookeeper.ZooKeeperTestSupport;
import org.apache.camel.test.AvailablePortFinder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ZooKeeperClusteredRoutePolicyTest {
    private static final int PORT = AvailablePortFinder.getNextAvailable();

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperClusteredRoutePolicyTest.class);

    private static final List<String> CLIENTS = IntStream.range(0, 3).mapToObj(Integer::toString).collect(Collectors.toList());

    private static final List<String> RESULTS = new ArrayList<>();

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(((ZooKeeperClusteredRoutePolicyTest.CLIENTS.size()) * 2));

    private static final CountDownLatch LATCH = new CountDownLatch(ZooKeeperClusteredRoutePolicyTest.CLIENTS.size());

    // ************************************
    // Test
    // ************************************
    @Test
    public void test() throws Exception {
        ZooKeeperTestSupport.TestZookeeperServer server = null;
        try {
            server = new ZooKeeperTestSupport.TestZookeeperServer(ZooKeeperClusteredRoutePolicyTest.PORT, true);
            ZooKeeperTestSupport.waitForServerUp(("localhost:" + (ZooKeeperClusteredRoutePolicyTest.PORT)), 1000);
            for (String id : ZooKeeperClusteredRoutePolicyTest.CLIENTS) {
                ZooKeeperClusteredRoutePolicyTest.SCHEDULER.submit(() -> ZooKeeperClusteredRoutePolicyTest.run(id));
            }
            ZooKeeperClusteredRoutePolicyTest.LATCH.await(1, TimeUnit.MINUTES);
            ZooKeeperClusteredRoutePolicyTest.SCHEDULER.shutdownNow();
            Assert.assertEquals(ZooKeeperClusteredRoutePolicyTest.CLIENTS.size(), ZooKeeperClusteredRoutePolicyTest.RESULTS.size());
            Assert.assertTrue(ZooKeeperClusteredRoutePolicyTest.RESULTS.containsAll(ZooKeeperClusteredRoutePolicyTest.CLIENTS));
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }
    }
}

