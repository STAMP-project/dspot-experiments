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
package org.ehcache.clustered.reconnect;


import com.tc.net.proxy.TCPProxy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.ClusteredTests;
import org.ehcache.clustered.util.TCPProxyUtil;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class CacheManagerDestroyReconnectTest extends ClusteredTests {
    private static PersistentCacheManager cacheManager;

    private static final List<TCPProxy> proxies = new ArrayList<>();

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(BasicCacheReconnectTest.RESOURCE_CONFIG).build();

    @Test
    public void testDestroyCacheManagerReconnects() throws Exception {
        TCPProxyUtil.setDelay(6000, CacheManagerDestroyReconnectTest.proxies);
        Thread.sleep(6000);
        TCPProxyUtil.setDelay(0L, CacheManagerDestroyReconnectTest.proxies);
        CacheManagerDestroyReconnectTest.cacheManager.close();
        CacheManagerDestroyReconnectTest.cacheManager.destroy();
        System.out.println(CacheManagerDestroyReconnectTest.cacheManager.getStatus());
    }
}

