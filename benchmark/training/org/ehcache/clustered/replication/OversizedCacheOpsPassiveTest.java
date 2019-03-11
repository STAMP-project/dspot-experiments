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


import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.ClusteredTests;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


/**
 * Test the effect of cache eviction during passive sync.
 */
public class OversizedCacheOpsPassiveTest extends ClusteredTests {
    private static final int MAX_PUTS = 3000;

    private static final int MAX_SWITCH_OVER = 3;

    private static final int PER_ELEMENT_SIZE = 256 * 1024;

    private static final int CACHE_SIZE_IN_MB = 2;

    private static final String LARGE_VALUE = OversizedCacheOpsPassiveTest.buildLargeString();

    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">2</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(Paths.get("build", "cluster").toFile()).withSystemProperty("ehcache.sync.data.gets.threshold", "2").withServiceFragment(OversizedCacheOpsPassiveTest.RESOURCE_CONFIG).build();

    @Test
    public void oversizedPuts() throws Exception {
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(OversizedCacheOpsPassiveTest.CLUSTER.getConnectionURI().resolve("/crud-cm")).autoCreate().defaultServerResource("primary-server-resource"));
        CountDownLatch syncLatch = new CountDownLatch(2);
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> doPuts(clusteredCacheManagerBuilder, syncLatch));
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> doPuts(clusteredCacheManagerBuilder, syncLatch));
        syncLatch.await();
        for (int i = 0; i < (OversizedCacheOpsPassiveTest.MAX_SWITCH_OVER); i++) {
            OversizedCacheOpsPassiveTest.CLUSTER.getClusterControl().terminateActive();
            OversizedCacheOpsPassiveTest.CLUSTER.getClusterControl().waitForActive();
            OversizedCacheOpsPassiveTest.CLUSTER.getClusterControl().startOneServer();
            OversizedCacheOpsPassiveTest.CLUSTER.getClusterControl().waitForRunningPassivesInStandby();
            Thread.sleep(2000);
        }
        f1.get();
        f2.get();
    }
}

