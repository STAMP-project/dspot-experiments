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
package org.ehcache.clustered.replication;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.ClusteredTests;
import org.ehcache.clustered.common.Consistency;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.testing.rules.Cluster;


/**
 * This test asserts Active-Passive fail-over with
 * multi-threaded/multi-client scenarios.
 * Note that fail-over is happening while client threads are still writing
 * Finally the same key set correctness is asserted.
 */
@RunWith(Parameterized.class)
public class BasicClusteredCacheOpsReplicationMultiThreadedTest extends ClusteredTests {
    private static final int NUM_OF_THREADS = 10;

    private static final int JOB_SIZE = 100;

    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">16</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    private static PersistentCacheManager CACHE_MANAGER1;

    private static PersistentCacheManager CACHE_MANAGER2;

    private static Cache<Long, BasicClusteredCacheOpsReplicationMultiThreadedTest.BlobValue> CACHE1;

    private static Cache<Long, BasicClusteredCacheOpsReplicationMultiThreadedTest.BlobValue> CACHE2;

    @Parameterized.Parameter
    public Consistency cacheConsistency;

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(BasicClusteredCacheOpsReplicationMultiThreadedTest.RESOURCE_CONFIG).build();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<Cache<Long, BasicClusteredCacheOpsReplicationMultiThreadedTest.BlobValue>> caches;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final ExecutorService executorService = Executors.newWorkStealingPool(BasicClusteredCacheOpsReplicationMultiThreadedTest.NUM_OF_THREADS);

    @Test(timeout = 180000)
    public void testCRUD() throws Exception {
        Set<Long> universalSet = ConcurrentHashMap.newKeySet();
        List<Future<?>> futures = new ArrayList<>();
        caches.forEach(( cache) -> {
            for (int i = 0; i < (BasicClusteredCacheOpsReplicationMultiThreadedTest.NUM_OF_THREADS); i++) {
                futures.add(executorService.submit(() -> random.longs().limit(BasicClusteredCacheOpsReplicationMultiThreadedTest.JOB_SIZE).forEach(( x) -> {
                    cache.put(x, new BasicClusteredCacheOpsReplicationMultiThreadedTest.BlobValue());
                    universalSet.add(x);
                })));
            }
        });
        // This step is to add values in local tier randomly to test invalidations happen correctly
        futures.add(executorService.submit(() -> universalSet.forEach(( x) -> {
            BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE1.get(x);
            BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE2.get(x);
        })));
        BasicClusteredCacheOpsReplicationMultiThreadedTest.CLUSTER.getClusterControl().terminateActive();
        drainTasks(futures);
        Set<Long> readKeysByCache1AfterFailOver = new HashSet<>();
        Set<Long> readKeysByCache2AfterFailOver = new HashSet<>();
        universalSet.forEach(( x) -> {
            if ((BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE1.get(x)) != null) {
                readKeysByCache1AfterFailOver.add(x);
            }
            if ((BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE2.get(x)) != null) {
                readKeysByCache2AfterFailOver.add(x);
            }
        });
        Assert.assertThat(readKeysByCache2AfterFailOver.size(), Matchers.equalTo(readKeysByCache1AfterFailOver.size()));
        readKeysByCache2AfterFailOver.stream().forEach(( y) -> Assert.assertThat(readKeysByCache1AfterFailOver.contains(y), Matchers.is(true)));
    }

    @Test(timeout = 180000)
    public void testBulkOps() throws Exception {
        Set<Long> universalSet = ConcurrentHashMap.newKeySet();
        List<Future<?>> futures = new ArrayList<>();
        caches.forEach(( cache) -> {
            for (int i = 0; i < (BasicClusteredCacheOpsReplicationMultiThreadedTest.NUM_OF_THREADS); i++) {
                Map<Long, BasicClusteredCacheOpsReplicationMultiThreadedTest.BlobValue> map = random.longs().limit(BasicClusteredCacheOpsReplicationMultiThreadedTest.JOB_SIZE).collect(HashMap::new, ( hashMap, x) -> hashMap.put(x, new BasicClusteredCacheOpsReplicationMultiThreadedTest.BlobValue()), HashMap::putAll);
                futures.add(executorService.submit(() -> {
                    cache.putAll(map);
                    universalSet.addAll(map.keySet());
                }));
            }
        });
        // This step is to add values in local tier randomly to test invalidations happen correctly
        futures.add(executorService.submit(() -> {
            universalSet.forEach(( x) -> {
                BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE1.get(x);
                BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE2.get(x);
            });
        }));
        BasicClusteredCacheOpsReplicationMultiThreadedTest.CLUSTER.getClusterControl().terminateActive();
        drainTasks(futures);
        Set<Long> readKeysByCache1AfterFailOver = new HashSet<>();
        Set<Long> readKeysByCache2AfterFailOver = new HashSet<>();
        universalSet.forEach(( x) -> {
            if ((BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE1.get(x)) != null) {
                readKeysByCache1AfterFailOver.add(x);
            }
            if ((BasicClusteredCacheOpsReplicationMultiThreadedTest.CACHE2.get(x)) != null) {
                readKeysByCache2AfterFailOver.add(x);
            }
        });
        Assert.assertThat(readKeysByCache2AfterFailOver.size(), Matchers.equalTo(readKeysByCache1AfterFailOver.size()));
        readKeysByCache2AfterFailOver.stream().forEach(( y) -> Assert.assertThat(readKeysByCache1AfterFailOver.contains(y), Matchers.is(true)));
    }

    private static class BlobValue implements Serializable {
        private static final long serialVersionUID = 1L;

        private final byte[] data = new byte[10 * 1024];
    }
}

