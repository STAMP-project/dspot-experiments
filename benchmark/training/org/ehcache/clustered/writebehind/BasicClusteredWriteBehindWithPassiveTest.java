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
package org.ehcache.clustered.writebehind;


import java.io.File;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class BasicClusteredWriteBehindWithPassiveTest extends WriteBehindTestBase {
    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(WriteBehindTestBase.RESOURCE_CONFIG).build();

    private PersistentCacheManager cacheManager;

    private Cache<Long, String> cache;

    @Test
    public void testBasicClusteredWriteBehind() throws Exception {
        for (int i = 0; i < 10; i++) {
            cache.put(WriteBehindTestBase.KEY, String.valueOf(i));
        }
        assertValue(cache, "9");
        BasicClusteredWriteBehindWithPassiveTest.CLUSTER.getClusterControl().terminateActive();
        BasicClusteredWriteBehindWithPassiveTest.CLUSTER.getClusterControl().waitForActive();
        assertValue(cache, "9");
        checkValueFromLoaderWriter(cache, String.valueOf(9));
    }

    @Test
    public void testClusteredWriteBehindCAS() throws Exception {
        cache.putIfAbsent(WriteBehindTestBase.KEY, "First value");
        assertValue(cache, "First value");
        cache.putIfAbsent(WriteBehindTestBase.KEY, "Second value");
        assertValue(cache, "First value");
        cache.put(WriteBehindTestBase.KEY, "First value again");
        assertValue(cache, "First value again");
        cache.replace(WriteBehindTestBase.KEY, "Replaced First value");
        assertValue(cache, "Replaced First value");
        cache.replace(WriteBehindTestBase.KEY, "Replaced First value", "Replaced First value again");
        assertValue(cache, "Replaced First value again");
        cache.replace(WriteBehindTestBase.KEY, "Replaced First", "Tried Replacing First value again");
        assertValue(cache, "Replaced First value again");
        cache.remove(WriteBehindTestBase.KEY, "Replaced First value again");
        assertValue(cache, null);
        cache.replace(WriteBehindTestBase.KEY, "Trying to replace value");
        assertValue(cache, null);
        cache.put(WriteBehindTestBase.KEY, "new value");
        assertValue(cache, "new value");
        BasicClusteredWriteBehindWithPassiveTest.CLUSTER.getClusterControl().terminateActive();
        BasicClusteredWriteBehindWithPassiveTest.CLUSTER.getClusterControl().waitForActive();
        assertValue(cache, "new value");
        checkValueFromLoaderWriter(cache, "new value");
    }
}

