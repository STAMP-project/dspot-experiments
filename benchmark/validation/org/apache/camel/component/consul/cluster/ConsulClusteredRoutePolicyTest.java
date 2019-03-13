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
package org.apache.camel.component.consul.cluster;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.camel.component.consul.ConsulTestSupport;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;


public class ConsulClusteredRoutePolicyTest {
    @ClassRule
    public static GenericContainer container = ConsulTestSupport.consulContainer();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulClusteredRoutePolicyTest.class);

    private static final List<String> CLIENTS = IntStream.range(0, 3).mapToObj(Integer::toString).collect(Collectors.toList());

    private static final List<String> RESULTS = new ArrayList<>();

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(((ConsulClusteredRoutePolicyTest.CLIENTS.size()) * 2));

    private static final CountDownLatch LATCH = new CountDownLatch(ConsulClusteredRoutePolicyTest.CLIENTS.size());

    // ************************************
    // Test
    // ************************************
    @Test
    public void test() throws Exception {
        for (String id : ConsulClusteredRoutePolicyTest.CLIENTS) {
            ConsulClusteredRoutePolicyTest.SCHEDULER.submit(() -> ConsulClusteredRoutePolicyTest.run(id));
        }
        ConsulClusteredRoutePolicyTest.LATCH.await(1, TimeUnit.MINUTES);
        ConsulClusteredRoutePolicyTest.SCHEDULER.shutdownNow();
        Assert.assertEquals(ConsulClusteredRoutePolicyTest.CLIENTS.size(), ConsulClusteredRoutePolicyTest.RESULTS.size());
        Assert.assertTrue(ConsulClusteredRoutePolicyTest.RESULTS.containsAll(ConsulClusteredRoutePolicyTest.CLIENTS));
    }
}

