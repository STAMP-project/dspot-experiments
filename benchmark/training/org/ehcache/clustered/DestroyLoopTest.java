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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.ehcache.PersistentCacheManager;
import org.ehcache.StateTransitionException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class DestroyLoopTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    private static final String CACHE_MANAGER_NAME = "/destroy-cm";

    private static final String CACHE_NAME = "clustered-cache";

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(DestroyLoopTest.RESOURCE_CONFIG).build();

    @Test
    public void testDestroyLoop() throws Exception {
        for (int i = 0; i < 10; i++) {
            try (DestroyLoopTest.CacheManagerContainer cmc = new DestroyLoopTest.CacheManagerContainer(10, this::createCacheManager)) {
                // just put in one and get from another
                cmc.cacheManagerList.get(0).getCache(DestroyLoopTest.CACHE_NAME, Long.class, String.class).put(1L, "value");
                Assert.assertThat(cmc.cacheManagerList.get(5).getCache(DestroyLoopTest.CACHE_NAME, Long.class, String.class).get(1L), CoreMatchers.is("value"));
            }
            destroyCacheManager();
        }
    }

    private static class CacheManagerContainer implements AutoCloseable {
        private final List<PersistentCacheManager> cacheManagerList;

        private CacheManagerContainer(int numCacheManagers, Supplier<PersistentCacheManager> cmSupplier) {
            List<PersistentCacheManager> cm = new ArrayList<>();
            for (int i = 0; i < numCacheManagers; i++) {
                cm.add(cmSupplier.get());
            }
            cacheManagerList = Collections.unmodifiableList(cm);
        }

        @Override
        public void close() throws StateTransitionException {
            cacheManagerList.forEach(PersistentCacheManager::close);
        }
    }
}

