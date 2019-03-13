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
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.ehcache.PersistentCacheManager;
import org.ehcache.StateTransitionException;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.ClusterTierManagerClientEntity;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.connection.Connection;
import org.terracotta.testing.rules.Cluster;


public class CacheManagerLifecycleEhcacheIntegrationTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(CacheManagerLifecycleEhcacheIntegrationTest.RESOURCE_CONFIG).build();

    private static Connection ASSERTION_CONNECTION;

    @Test
    public void testAutoCreatedCacheManager() throws Exception {
        CacheManagerLifecycleEhcacheIntegrationTest.assertEntityNotExists(ClusterTierManagerClientEntity.class, "testAutoCreatedCacheManager");
        PersistentCacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(CacheManagerLifecycleEhcacheIntegrationTest.CLUSTER.getConnectionURI().resolve("/testAutoCreatedCacheManager")).autoCreate().build()).build();
        CacheManagerLifecycleEhcacheIntegrationTest.assertEntityNotExists(ClusterTierManagerClientEntity.class, "testAutoCreatedCacheManager");
        manager.init();
        try {
            CacheManagerLifecycleEhcacheIntegrationTest.assertEntityExists(ClusterTierManagerClientEntity.class, "testAutoCreatedCacheManager");
        } finally {
            manager.close();
        }
    }

    @Test
    public void testAutoCreatedCacheManagerUsingXml() throws Exception {
        URL xml = CacheManagerLifecycleEhcacheIntegrationTest.class.getResource("/configs/clustered.xml");
        URL substitutedXml = CacheManagerLifecycleEhcacheIntegrationTest.substitute(xml, "cluster-uri", CacheManagerLifecycleEhcacheIntegrationTest.CLUSTER.getConnectionURI().toString());
        PersistentCacheManager manager = ((PersistentCacheManager) (CacheManagerBuilder.newCacheManager(new XmlConfiguration(substitutedXml))));
        CacheManagerLifecycleEhcacheIntegrationTest.assertEntityNotExists(ClusterTierManagerClientEntity.class, "testAutoCreatedCacheManagerUsingXml");
        manager.init();
        try {
            CacheManagerLifecycleEhcacheIntegrationTest.assertEntityExists(ClusterTierManagerClientEntity.class, "testAutoCreatedCacheManagerUsingXml");
        } finally {
            manager.close();
        }
    }

    @Test
    public void testMultipleClientsAutoCreatingCacheManager() throws Exception {
        CacheManagerLifecycleEhcacheIntegrationTest.assertEntityNotExists(ClusterTierManagerClientEntity.class, "testMultipleClientsAutoCreatingCacheManager");
        final CacheManagerBuilder<PersistentCacheManager> managerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(CacheManagerLifecycleEhcacheIntegrationTest.CLUSTER.getConnectionURI().resolve("/testMultipleClientsAutoCreatingCacheManager")).autoCreate().build());
        Callable<PersistentCacheManager> task = () -> {
            PersistentCacheManager manager = managerBuilder.build();
            manager.init();
            return manager;
        };
        CacheManagerLifecycleEhcacheIntegrationTest.assertEntityNotExists(ClusterTierManagerClientEntity.class, "testMultipleClientsAutoCreatingCacheManager");
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            List<Future<PersistentCacheManager>> results = executor.invokeAll(Collections.nCopies(4, task), 30, TimeUnit.SECONDS);
            for (Future<PersistentCacheManager> result : results) {
                Assert.assertThat(result.isDone(), Matchers.is(true));
            }
            for (Future<PersistentCacheManager> result : results) {
                result.get().close();
            }
            CacheManagerLifecycleEhcacheIntegrationTest.assertEntityExists(ClusterTierManagerClientEntity.class, "testMultipleClientsAutoCreatingCacheManager");
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testCacheManagerNotExistingFailsOnInit() throws Exception {
        try {
            CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(CacheManagerLifecycleEhcacheIntegrationTest.CLUSTER.getConnectionURI().resolve("/testCacheManagerNotExistingFailsOnInit")).build()).build(true);
            Assert.fail("Expected StateTransitionException");
        } catch (StateTransitionException e) {
            // expected
        }
    }
}

