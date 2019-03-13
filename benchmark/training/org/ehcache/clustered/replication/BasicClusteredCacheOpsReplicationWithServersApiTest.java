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
import java.util.List;
import java.util.function.Consumer;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.ClusteredTests;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class BasicClusteredCacheOpsReplicationWithServersApiTest extends ClusteredTests {
    private static final String CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">16</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    private static PersistentCacheManager CACHE_MANAGER;

    private static Cache<Long, String> CACHE1;

    private static Cache<Long, String> CACHE2;

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(BasicClusteredCacheOpsReplicationWithServersApiTest.CONFIG).build();

    @Test
    public void testCRUD() throws Exception {
        List<Cache<Long, String>> caches = new ArrayList<>();
        caches.add(BasicClusteredCacheOpsReplicationWithServersApiTest.CACHE1);
        caches.add(BasicClusteredCacheOpsReplicationWithServersApiTest.CACHE2);
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
        BasicClusteredCacheOpsReplicationWithServersApiTest.CLUSTER.getClusterControl().terminateActive();
        caches.forEach(( x) -> {
            Assert.assertThat(x.get(1L), Matchers.equalTo("Another one"));
            Assert.assertThat(x.get(2L), Matchers.equalTo("The two"));
            Assert.assertThat(x.get(3L), Matchers.equalTo("The three"));
            Assert.assertThat(x.get(4L), Matchers.nullValue());
        });
    }
}

