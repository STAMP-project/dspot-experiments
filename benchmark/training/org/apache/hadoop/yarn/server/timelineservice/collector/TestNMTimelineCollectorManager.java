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
package org.apache.hadoop.yarn.server.timelineservice.collector;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.junit.Assert;
import org.junit.Test;


public class TestNMTimelineCollectorManager {
    private NodeTimelineCollectorManager collectorManager;

    @Test
    public void testStartingWriterFlusher() throws Exception {
        Assert.assertTrue(collectorManager.writerFlusherRunning());
    }

    @Test
    public void testStartWebApp() throws Exception {
        Assert.assertNotNull(collectorManager.getRestServerBindAddress());
        String address = collectorManager.getRestServerBindAddress();
        String[] parts = address.split(":");
        Assert.assertEquals(2, parts.length);
        Assert.assertNotNull(parts[0]);
        Assert.assertTrue((((Integer.valueOf(parts[1])) >= 30000) && ((Integer.valueOf(parts[1])) <= 30100)));
    }

    @Test(timeout = 60000)
    public void testMultithreadedAdd() throws Exception {
        final int numApps = 5;
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        for (int i = 0; i < numApps; i++) {
            final ApplicationId appId = ApplicationId.newInstance(0L, i);
            Callable<Boolean> task = new Callable<Boolean>() {
                public Boolean call() {
                    AppLevelTimelineCollector collector = new AppLevelTimelineCollectorWithAgg(appId, "user");
                    return (collectorManager.putIfAbsent(appId, collector)) == collector;
                }
            };
            tasks.add(task);
        }
        ExecutorService executor = Executors.newFixedThreadPool(numApps);
        try {
            List<Future<Boolean>> futures = executor.invokeAll(tasks);
            for (Future<Boolean> future : futures) {
                Assert.assertTrue(future.get());
            }
        } finally {
            executor.shutdownNow();
        }
        // check the keys
        for (int i = 0; i < numApps; i++) {
            final ApplicationId appId = ApplicationId.newInstance(0L, i);
            Assert.assertTrue(collectorManager.containsTimelineCollector(appId));
        }
    }

    @Test
    public void testMultithreadedAddAndRemove() throws Exception {
        final int numApps = 5;
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        for (int i = 0; i < numApps; i++) {
            final ApplicationId appId = ApplicationId.newInstance(0L, i);
            Callable<Boolean> task = new Callable<Boolean>() {
                public Boolean call() {
                    AppLevelTimelineCollector collector = new AppLevelTimelineCollectorWithAgg(appId, "user");
                    boolean successPut = (collectorManager.putIfAbsent(appId, collector)) == collector;
                    return successPut && (collectorManager.remove(appId));
                }
            };
            tasks.add(task);
        }
        ExecutorService executor = Executors.newFixedThreadPool(numApps);
        try {
            List<Future<Boolean>> futures = executor.invokeAll(tasks);
            for (Future<Boolean> future : futures) {
                Assert.assertTrue(future.get());
            }
        } finally {
            executor.shutdownNow();
        }
        // check the keys
        for (int i = 0; i < numApps; i++) {
            final ApplicationId appId = ApplicationId.newInstance(0L, i);
            Assert.assertFalse(collectorManager.containsTimelineCollector(appId));
        }
    }
}

