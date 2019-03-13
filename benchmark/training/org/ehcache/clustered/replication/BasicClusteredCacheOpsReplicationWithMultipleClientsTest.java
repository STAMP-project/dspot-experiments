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
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
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
import org.terracotta.testing.rules.Cluster;


/**
 * The point of this test is to assert proper data read after fail-over handling.
 */
@RunWith(Parameterized.class)
public class BasicClusteredCacheOpsReplicationWithMultipleClientsTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">16</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    private static PersistentCacheManager CACHE_MANAGER1;

    private static PersistentCacheManager CACHE_MANAGER2;

    private static Cache<Long, BasicClusteredCacheOpsReplicationWithMultipleClientsTest.BlobValue> CACHE1;

    private static Cache<Long, BasicClusteredCacheOpsReplicationWithMultipleClientsTest.BlobValue> CACHE2;

    @Parameterized.Parameter
    public Consistency cacheConsistency;

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(BasicClusteredCacheOpsReplicationWithMultipleClientsTest.RESOURCE_CONFIG).build();

    @Test(timeout = 180000)
    public void testCRUD() throws Exception {
        Random random = new Random();
        LongStream longStream = random.longs(1000);
        Set<Long> added = new HashSet<>();
        longStream.forEach(( x) -> {
            BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE1.put(x, new BasicClusteredCacheOpsReplicationWithMultipleClientsTest.BlobValue());
            added.add(x);
        });
        Set<Long> readKeysByCache2BeforeFailOver = new HashSet<>();
        added.forEach(( x) -> {
            if ((BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE2.get(x)) != null) {
                readKeysByCache2BeforeFailOver.add(x);
            }
        });
        BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CLUSTER.getClusterControl().terminateActive();
        Set<Long> readKeysByCache1AfterFailOver = new HashSet<>();
        added.forEach(( x) -> {
            if ((BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE1.get(x)) != null) {
                readKeysByCache1AfterFailOver.add(x);
            }
        });
        Assert.assertThat(readKeysByCache2BeforeFailOver.size(), Matchers.greaterThanOrEqualTo(readKeysByCache1AfterFailOver.size()));
        readKeysByCache1AfterFailOver.stream().filter(readKeysByCache2BeforeFailOver::contains).forEach(( y) -> Assert.assertThat(BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE2.get(y), Matchers.notNullValue()));
    }

    @Test(timeout = 180000)
    public void testClear() throws Exception {
        List<Cache<Long, BasicClusteredCacheOpsReplicationWithMultipleClientsTest.BlobValue>> caches = new ArrayList<>();
        caches.add(BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE1);
        caches.add(BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE2);
        Map<Long, BasicClusteredCacheOpsReplicationWithMultipleClientsTest.BlobValue> entriesMap = new HashMap<>();
        Random random = new Random();
        LongStream longStream = random.longs(1000);
        longStream.forEach(( x) -> entriesMap.put(x, new BasicClusteredCacheOpsReplicationWithMultipleClientsTest.BlobValue()));
        caches.forEach(( cache) -> cache.putAll(entriesMap));
        Set<Long> keySet = entriesMap.keySet();
        Set<Long> readKeysByCache2BeforeFailOver = new HashSet<>();
        keySet.forEach(( x) -> {
            if ((BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE2.get(x)) != null) {
                readKeysByCache2BeforeFailOver.add(x);
            }
        });
        BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE1.clear();
        BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CLUSTER.getClusterControl().terminateActive();
        if ((cacheConsistency) == (Consistency.STRONG)) {
            readKeysByCache2BeforeFailOver.forEach(( x) -> Assert.assertThat(BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE2.get(x), Matchers.nullValue()));
        } else {
            readKeysByCache2BeforeFailOver.forEach(( x) -> Assert.assertThat(BasicClusteredCacheOpsReplicationWithMultipleClientsTest.CACHE1.get(x), Matchers.nullValue()));
        }
    }

    private static class BlobValue implements Serializable {
        private static final long serialVersionUID = 1L;

        private final byte[] data = new byte[10 * 1024];
    }
}

