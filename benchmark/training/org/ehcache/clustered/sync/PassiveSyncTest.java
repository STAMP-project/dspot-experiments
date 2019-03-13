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
package org.ehcache.clustered.sync;


import java.io.File;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.ClusteredTests;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class PassiveSyncTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">16</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(PassiveSyncTest.RESOURCE_CONFIG).build();

    @Test(timeout = 150000)
    public void testSync() throws Exception {
        PassiveSyncTest.CLUSTER.getClusterControl().terminateOnePassive();
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(PassiveSyncTest.CLUSTER.getConnectionURI().resolve("/op-sync")).autoCreate().defaultServerResource("primary-server-resource"));
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            CacheConfiguration<Long, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 1, MemoryUnit.MB))).build();
            Cache<Long, String> cache = cacheManager.createCache("clustered-cache", config);
            for (long i = -5; i < 5; i++) {
                cache.put(i, ("value" + i));
            }
            PassiveSyncTest.CLUSTER.getClusterControl().startOneServer();
            PassiveSyncTest.CLUSTER.getClusterControl().waitForRunningPassivesInStandby();
            PassiveSyncTest.CLUSTER.getClusterControl().terminateActive();
            PassiveSyncTest.CLUSTER.getClusterControl().waitForActive();
            // Sometimes the new passive believes there is a second connection and we have to wait for the full reconnect window before getting a result
            waitOrTimeout(() -> "value-5".equals(cache.get((-5L))), timeout(seconds(130)));
            for (long i = -4; i < 5; i++) {
                Assert.assertThat(cache.get(i), Matchers.equalTo(("value" + i)));
            }
        } finally {
            cacheManager.close();
        }
    }
}

