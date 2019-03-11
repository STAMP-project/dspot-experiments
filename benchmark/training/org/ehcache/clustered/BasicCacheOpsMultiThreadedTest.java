/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


/**
 * Simulate multiple clients starting up the same cache manager simultaneously and ensure that puts and gets works just
 * fine and nothing get lost or hung, just because multiple cache manager instances of the same cache manager are coming up
 * simultaneously.
 */
public class BasicCacheOpsMultiThreadedTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(BasicCacheOpsMultiThreadedTest.RESOURCE_CONFIG).build();

    private static final String CLUSTERED_CACHE_NAME = "clustered-cache";

    private static final String SYN_CACHE_NAME = "syn-cache";

    private static final String PRIMARY_SERVER_RESOURCE_NAME = "primary-server-resource";

    private static final String CACHE_MANAGER_NAME = "/crud-cm";

    private static final int PRIMARY_SERVER_RESOURCE_SIZE = 4;// MB


    private static final int NUM_THREADS = 8;

    private static final int MAX_WAIT_TIME_SECONDS = 30;

    private final AtomicReference<Throwable> exception = new AtomicReference<>();

    private final AtomicLong idGenerator = new AtomicLong(2L);

    @Test
    public void testMulipleClients() throws Throwable {
        CountDownLatch latch = new CountDownLatch(((BasicCacheOpsMultiThreadedTest.NUM_THREADS) + 1));
        List<Thread> threads = new ArrayList<>(BasicCacheOpsMultiThreadedTest.NUM_THREADS);
        for (int i = 0; i < (BasicCacheOpsMultiThreadedTest.NUM_THREADS); i++) {
            Thread t1 = new Thread(content(latch));
            t1.start();
            threads.add(t1);
        }
        latch.countDown();
        Assert.assertTrue(latch.await(BasicCacheOpsMultiThreadedTest.MAX_WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        for (Thread t : threads) {
            t.join();
        }
        Throwable throwable = exception.get();
        if (throwable != null) {
            throw throwable;
        }
    }
}

