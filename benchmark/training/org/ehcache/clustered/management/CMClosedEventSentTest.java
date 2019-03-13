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
package org.ehcache.clustered.management;


import java.io.File;
import org.ehcache.CacheManager;
import org.ehcache.Status;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.management.registry.DefaultManagementRegistryConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class CMClosedEventSentTest {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((((((((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "<ohr:resource name=\"secondary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n") + "<service xmlns:lease='http://www.terracotta.org/service/lease'>") + "<lease:connection-leasing>") + "<lease:lease-length unit='seconds'>5</lease:lease-length>") + "</lease:connection-leasing>") + "</service>");

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(CMClosedEventSentTest.RESOURCE_CONFIG).build();

    @Test(timeout = 60000)
    public void test_CACHE_MANAGER_CLOSED() throws Exception {
        AbstractClusteringManagementTest.createNmsService(CMClosedEventSentTest.CLUSTER);
        try (CacheManager cacheManager = // cache config
        // will take from primary-server-resource
        // management config
        CacheManagerBuilder.newCacheManagerBuilder().with(// <2>
        cluster(CMClosedEventSentTest.CLUSTER.getConnectionURI().resolve("/my-server-entity-1")).autoCreate().defaultServerResource("primary-server-resource").resourcePool("resource-pool-a", 10, MemoryUnit.MB, "secondary-server-resource").resourcePool("resource-pool-b", 10, MemoryUnit.MB)).using(new DefaultManagementRegistryConfiguration().addTags("webapp-1", "server-node-1").setCacheManagerAlias("my-super-cache-manager")).withCache("dedicated-cache-1", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(1, MemoryUnit.MB).with(clusteredDedicated("primary-server-resource", 4, MemoryUnit.MB))).build()).build(true)) {
            Assert.assertThat(cacheManager.getStatus(), CoreMatchers.equalTo(Status.AVAILABLE));
            waitFor("CACHE_MANAGER_AVAILABLE");
        }
        waitFor("CACHE_MANAGER_CLOSED");
    }
}

