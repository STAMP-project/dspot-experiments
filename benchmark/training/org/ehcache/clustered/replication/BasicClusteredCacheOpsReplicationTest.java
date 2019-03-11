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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
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


@RunWith(Parameterized.class)
public class BasicClusteredCacheOpsReplicationTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">16</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    private static PersistentCacheManager CACHE_MANAGER;

    private static Cache<Long, String> CACHE1;

    private static Cache<Long, String> CACHE2;

    @Parameterized.Parameter
    public Consistency cacheConsistency;

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(BasicClusteredCacheOpsReplicationTest.RESOURCE_CONFIG).build();

    @Test
    public void testCRUD() throws Exception {
        List<Cache<Long, String>> caches = new ArrayList<>();
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE1);
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE2);
        caches.forEach(( x) -> {
            x.put(1L, "The one");
            x.put(2L, "The two");
            x.put(1L, "Another one");
            x.put(3L, "The three");
            x.put(4L, "The four");
            Assert.assertThat(x.get(1L), Matchers.equalTo("Another one"));
            Assert.assertThat(x.get(2L), Matchers.equalTo("The two"));
            Assert.assertThat(x.get(3L), Matchers.equalTo("The three"));
            x.remove(4L);
        });
        BasicClusteredCacheOpsReplicationTest.CLUSTER.getClusterControl().terminateActive();
        caches.forEach(( x) -> {
            Assert.assertThat(x.get(1L), Matchers.equalTo("Another one"));
            Assert.assertThat(x.get(2L), Matchers.equalTo("The two"));
            Assert.assertThat(x.get(3L), Matchers.equalTo("The three"));
            Assert.assertThat(x.get(4L), Matchers.nullValue());
        });
    }

    @Test
    public void testBulkOps() throws Exception {
        List<Cache<Long, String>> caches = new ArrayList<>();
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE1);
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE2);
        Map<Long, String> entriesMap = new HashMap<>();
        entriesMap.put(1L, "one");
        entriesMap.put(2L, "two");
        entriesMap.put(3L, "three");
        entriesMap.put(4L, "four");
        entriesMap.put(5L, "five");
        entriesMap.put(6L, "six");
        caches.forEach(( cache) -> cache.putAll(entriesMap));
        BasicClusteredCacheOpsReplicationTest.CLUSTER.getClusterControl().terminateActive();
        Set<Long> keySet = entriesMap.keySet();
        caches.forEach(( cache) -> {
            Map<Long, String> all = cache.getAll(keySet);
            Assert.assertThat(all.get(1L), Matchers.is("one"));
            Assert.assertThat(all.get(2L), Matchers.is("two"));
            Assert.assertThat(all.get(3L), Matchers.is("three"));
            Assert.assertThat(all.get(4L), Matchers.is("four"));
            Assert.assertThat(all.get(5L), Matchers.is("five"));
            Assert.assertThat(all.get(6L), Matchers.is("six"));
        });
    }

    @Test
    public void testCAS() throws Exception {
        List<Cache<Long, String>> caches = new ArrayList<>();
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE1);
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE2);
        caches.forEach(( cache) -> {
            Assert.assertThat(cache.putIfAbsent(1L, "one"), Matchers.nullValue());
            Assert.assertThat(cache.putIfAbsent(2L, "two"), Matchers.nullValue());
            Assert.assertThat(cache.putIfAbsent(3L, "three"), Matchers.nullValue());
            Assert.assertThat(cache.replace(3L, "another one", "yet another one"), Matchers.is(false));
        });
        BasicClusteredCacheOpsReplicationTest.CLUSTER.getClusterControl().terminateActive();
        caches.forEach(( cache) -> {
            Assert.assertThat(cache.putIfAbsent(1L, "another one"), Matchers.is("one"));
            Assert.assertThat(cache.remove(2L, "not two"), Matchers.is(false));
            Assert.assertThat(cache.replace(3L, "three", "another three"), Matchers.is(true));
            Assert.assertThat(cache.replace(2L, "new two"), Matchers.is("two"));
        });
    }

    @Test
    public void testClear() throws Exception {
        List<Cache<Long, String>> caches = new ArrayList<>();
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE1);
        caches.add(BasicClusteredCacheOpsReplicationTest.CACHE2);
        Map<Long, String> entriesMap = new HashMap<>();
        entriesMap.put(1L, "one");
        entriesMap.put(2L, "two");
        entriesMap.put(3L, "three");
        entriesMap.put(4L, "four");
        entriesMap.put(5L, "five");
        entriesMap.put(6L, "six");
        caches.forEach(( cache) -> cache.putAll(entriesMap));
        Set<Long> keySet = entriesMap.keySet();
        caches.forEach(( cache) -> {
            Map<Long, String> all = cache.getAll(keySet);
            Assert.assertThat(all.get(1L), Matchers.is("one"));
            Assert.assertThat(all.get(2L), Matchers.is("two"));
            Assert.assertThat(all.get(3L), Matchers.is("three"));
            Assert.assertThat(all.get(4L), Matchers.is("four"));
            Assert.assertThat(all.get(5L), Matchers.is("five"));
            Assert.assertThat(all.get(6L), Matchers.is("six"));
        });
        BasicClusteredCacheOpsReplicationTest.CACHE1.clear();
        BasicClusteredCacheOpsReplicationTest.CACHE2.clear();
        BasicClusteredCacheOpsReplicationTest.CLUSTER.getClusterControl().terminateActive();
        keySet.forEach(( x) -> Assert.assertThat(BasicClusteredCacheOpsReplicationTest.CACHE1.get(x), Matchers.nullValue()));
        keySet.forEach(( x) -> Assert.assertThat(BasicClusteredCacheOpsReplicationTest.CACHE2.get(x), Matchers.nullValue()));
    }
}

