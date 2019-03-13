/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.networking.nio;


import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.ascii.HTTPCommunicator;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.SlowTest;
import java.io.IOException;
import net.spy.memcached.MemcachedClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class AdvancedNetworkingCommunicationIntegrationTest extends AbstractAdvancedNetworkIntegrationTest {
    @Test
    public void testMemberConnectionToEndpoints() {
        Config config = createCompleteMultiSocketConfig();
        configureTcpIpConfig(config);
        newHazelcastInstance(config);
        startMemberAndTryToJoinToPort(AbstractAdvancedNetworkIntegrationTest.MEMBER_PORT, 2);
        startMemberAndTryToJoinToPort(AbstractAdvancedNetworkIntegrationTest.NOT_OPENED_PORT, 1);
        testMemberJoinFailsOnPort(AbstractAdvancedNetworkIntegrationTest.CLIENT_PORT);
    }

    @Test
    public void testRestConnectionToEndpoints() throws IOException {
        Config config = createCompleteMultiSocketConfig();
        HazelcastInstance hz = newHazelcastInstance(config);
        HTTPCommunicator communicator = new HTTPCommunicator(hz, ("/127.0.0.1:" + (AbstractAdvancedNetworkIntegrationTest.REST_PORT)));
        final String expected = (("{\"status\":\"success\"," + "\"version\":\"") + (hz.getCluster().getClusterVersion().toString())) + "\"}";
        Assert.assertEquals(expected, communicator.getClusterVersion());
        testRestCallFailsOnPort(hz, AbstractAdvancedNetworkIntegrationTest.MEMBER_PORT);
        testRestCallFailsOnPort(hz, AbstractAdvancedNetworkIntegrationTest.CLIENT_PORT);
        testRestCallFailsOnPort(hz, AbstractAdvancedNetworkIntegrationTest.WAN1_PORT);
    }

    @Test
    public void testMemcacheConnectionToEndpoints() throws Exception {
        Config config = createCompleteMultiSocketConfig();
        JoinConfig join = config.getAdvancedNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(false);
        HazelcastInstance hz = newHazelcastInstance(config);
        MemcachedClient client = null;
        try {
            client = getMemcachedClient(hz, AbstractAdvancedNetworkIntegrationTest.MEMCACHE_PORT);
            client.get("whatever");
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
        testMemcacheCallFailsOnPort(hz, AbstractAdvancedNetworkIntegrationTest.MEMBER_PORT);
        testMemcacheCallFailsOnPort(hz, AbstractAdvancedNetworkIntegrationTest.CLIENT_PORT);
        testMemcacheCallFailsOnPort(hz, AbstractAdvancedNetworkIntegrationTest.WAN1_PORT);
    }
}

