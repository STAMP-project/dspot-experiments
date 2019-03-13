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
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.DedicatedClusteredResourcePool;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class ResourcePoolAllocationFailureTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(ResourcePoolAllocationFailureTest.RESOURCE_CONFIG).build();

    @Test
    public void testTooLowResourceException() throws InterruptedException {
        DedicatedClusteredResourcePool resourcePool = ClusteredResourcePoolBuilder.clusteredDedicated(10, MemoryUnit.KB);
        CacheManagerBuilder<PersistentCacheManager> cacheManagerBuilder = getPersistentCacheManagerCacheManagerBuilder(resourcePool);
        try {
            cacheManagerBuilder.build(true);
            Assert.fail("InvalidServerStoreConfigurationException expected");
        } catch (Exception e) {
            Throwable cause = ResourcePoolAllocationFailureTest.getCause(e, CachePersistenceException.class);
            Assert.assertThat(cause, Matchers.notNullValue());
            Assert.assertThat(cause.getMessage(), Matchers.startsWith("Unable to create"));
        }
        resourcePool = ClusteredResourcePoolBuilder.clusteredDedicated(100, MemoryUnit.KB);
        cacheManagerBuilder = getPersistentCacheManagerCacheManagerBuilder(resourcePool);
        PersistentCacheManager persistentCacheManager = cacheManagerBuilder.build(true);
        Assert.assertThat(persistentCacheManager, Matchers.notNullValue());
        persistentCacheManager.close();
    }
}

