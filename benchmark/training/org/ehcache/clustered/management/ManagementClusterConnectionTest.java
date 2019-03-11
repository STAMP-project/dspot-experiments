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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tc.net.proxy.TCPProxy;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.clustered.util.TCPProxyUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class ManagementClusterConnectionTest {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((((((((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "<ohr:resource name=\"secondary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n") + "<service xmlns:lease='http://www.terracotta.org/service/lease'>") + "<lease:connection-leasing>") + "<lease:lease-length unit='seconds'>5</lease:lease-length>") + "</lease:connection-leasing>") + "</service>");

    protected static CacheManager cacheManager;

    protected static ObjectMapper mapper = new ObjectMapper();

    private static final List<TCPProxy> proxies = new ArrayList<>();

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(ManagementClusterConnectionTest.RESOURCE_CONFIG).build();

    @Test
    public void test_reconnection() throws Exception {
        long count = AbstractClusteringManagementTest.readTopology().clientStream().filter(( client) -> ((client.getName().startsWith("Ehcache:")) && (client.isManageable())) && (client.getTags().containsAll(Arrays.asList("webapp-1", "server-node-1")))).count();
        Assert.assertThat(count, Matchers.equalTo(1L));
        String instanceId = getInstanceId();
        TCPProxyUtil.setDelay(6000, ManagementClusterConnectionTest.proxies);
        Thread.sleep(6000);
        TCPProxyUtil.setDelay(0L, ManagementClusterConnectionTest.proxies);
        Cache<String, String> cache = ManagementClusterConnectionTest.cacheManager.getCache("dedicated-cache-1", String.class, String.class);
        String initiate_reconnect = cache.get("initiate reconnect");
        Assert.assertThat(initiate_reconnect, Matchers.nullValue());
        while (!(Thread.currentThread().isInterrupted())) {
            // System.out.println(mapper.writeValueAsString(readTopology().toMap()));
            count = AbstractClusteringManagementTest.readTopology().clientStream().filter(( client) -> ((client.getName().startsWith("Ehcache:")) && (client.isManageable())) && (client.getTags().containsAll(Arrays.asList("webapp-1", "server-node-1")))).count();
            if (count == 1) {
                break;
            } else {
                Thread.sleep(1000);
            }
        } 
        Assert.assertThat(Thread.currentThread().isInterrupted(), CoreMatchers.is(false));
        Assert.assertThat(getInstanceId(), CoreMatchers.equalTo(instanceId));
    }
}

