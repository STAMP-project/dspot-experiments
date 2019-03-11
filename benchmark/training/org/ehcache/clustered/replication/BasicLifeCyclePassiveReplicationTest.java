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


import VoltronReadWriteLock.Hold;
import java.io.File;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.ClusteredTests;
import org.ehcache.clustered.client.internal.lock.VoltronReadWriteLock;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class BasicLifeCyclePassiveReplicationTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">16</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(BasicLifeCyclePassiveReplicationTest.RESOURCE_CONFIG).build();

    @Test
    public void testDestroyCacheManager() throws Exception {
        CacheManagerBuilder<PersistentCacheManager> configBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(cluster(BasicLifeCyclePassiveReplicationTest.CLUSTER.getConnectionURI().resolve("/destroy-CM")).autoCreate().defaultServerResource("primary-server-resource"));
        PersistentCacheManager cacheManager1 = configBuilder.build(true);
        PersistentCacheManager cacheManager2 = configBuilder.build(true);
        cacheManager2.close();
        try {
            cacheManager2.destroy();
            Assert.fail("Exception expected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        BasicLifeCyclePassiveReplicationTest.CLUSTER.getClusterControl().terminateActive();
        BasicLifeCyclePassiveReplicationTest.CLUSTER.getClusterControl().waitForActive();
        cacheManager1.createCache("test", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10).with(clusteredDedicated(10, MemoryUnit.MB))));
    }

    @Test
    public void testDestroyLockEntity() throws Exception {
        VoltronReadWriteLock lock1 = new VoltronReadWriteLock(BasicLifeCyclePassiveReplicationTest.CLUSTER.newConnection(), "my-lock");
        VoltronReadWriteLock.Hold hold1 = lock1.tryReadLock();
        VoltronReadWriteLock lock2 = new VoltronReadWriteLock(BasicLifeCyclePassiveReplicationTest.CLUSTER.newConnection(), "my-lock");
        Assert.assertThat(lock2.tryWriteLock(), Matchers.nullValue());
        BasicLifeCyclePassiveReplicationTest.CLUSTER.getClusterControl().terminateActive();
        BasicLifeCyclePassiveReplicationTest.CLUSTER.getClusterControl().waitForActive();
        hold1.unlock();
    }
}

