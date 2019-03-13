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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.CacheConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.terracotta.testing.rules.Cluster;


@RunWith(Parameterized.class)
public class ClusteredLoaderWriterTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @Parameterized.Parameter
    public Consistency cacheConsistency;

    private static CacheManager cacheManager;

    private Cache<Long, String> client1;

    private CacheConfiguration<Long, String> configuration;

    private ConcurrentMap<Long, String> sor;

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(ClusteredLoaderWriterTest.RESOURCE_CONFIG).build();

    @Test
    public void testBasicOps() {
        client1 = ClusteredLoaderWriterTest.cacheManager.createCache(("basicops" + (cacheConsistency.name())), configuration);
        Assert.assertThat(sor.isEmpty(), Matchers.is(true));
        Set<Long> keys = new HashSet<>();
        ThreadLocalRandom.current().longs(10).forEach(( x) -> {
            keys.add(x);
            client1.put(x, Long.toString(x));
        });
        Assert.assertThat(sor.size(), Matchers.is(10));
        CacheManager anotherCacheManager = ClusteredLoaderWriterTest.newCacheManager();
        Cache<Long, String> client2 = anotherCacheManager.createCache(("basicops" + (cacheConsistency.name())), getCacheConfig());
        Map<Long, String> all = client2.getAll(keys);
        Assert.assertThat(all.keySet(), Matchers.containsInAnyOrder(keys.toArray()));
        keys.stream().limit(3).forEach(client2::remove);
        Assert.assertThat(sor.size(), Matchers.is(7));
    }

    @Test
    public void testCASOps() {
        client1 = ClusteredLoaderWriterTest.cacheManager.createCache(("casops" + (cacheConsistency.name())), configuration);
        Assert.assertThat(sor.isEmpty(), Matchers.is(true));
        Set<Long> keys = new HashSet<>();
        ThreadLocalRandom.current().longs(10).forEach(( x) -> {
            keys.add(x);
            client1.put(x, Long.toString(x));
        });
        Assert.assertThat(sor.size(), Matchers.is(10));
        CacheManager anotherCacheManager = ClusteredLoaderWriterTest.newCacheManager();
        Cache<Long, String> client2 = anotherCacheManager.createCache(("casops" + (cacheConsistency.name())), getCacheConfig());
        keys.forEach(( x) -> Assert.assertThat(client2.putIfAbsent(x, ("Again" + x)), Matchers.is(Long.toString(x))));
        Assert.assertThat(sor.size(), Matchers.is(10));
        keys.stream().limit(5).forEach(( x) -> Assert.assertThat(client2.replace(x, ("Replaced" + x)), Matchers.is(Long.toString(x))));
        Assert.assertThat(sor.size(), Matchers.is(10));
        keys.forEach(( x) -> client1.remove(x, Long.toString(x)));
        Assert.assertThat(sor.size(), Matchers.is(5));
        AtomicInteger success = new AtomicInteger(0);
        keys.forEach(( x) -> {
            if (client2.replace(x, ("Replaced" + x), "Again")) {
                success.incrementAndGet();
            }
        });
        Assert.assertThat(success.get(), Matchers.is(5));
    }
}

