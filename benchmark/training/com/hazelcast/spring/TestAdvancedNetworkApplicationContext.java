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
package com.hazelcast.spring;


import EndpointQualifier.CLIENT;
import EndpointQualifier.MEMBER;
import ProtocolType.WAN;
import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.config.RestServerEndpointConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "advancedNetworkConfig-applicationContext-hazelcast.xml" })
@Category(QuickTest.class)
public class TestAdvancedNetworkApplicationContext {
    @Resource(name = "instance")
    private HazelcastInstance instance;

    @Test
    public void testAdvancedNetworkConfig() {
        Config config = instance.getConfig();
        AdvancedNetworkConfig advancedNetworkConfig = config.getAdvancedNetworkConfig();
        Assert.assertTrue(advancedNetworkConfig.isEnabled());
        TcpIpConfig tcpIpConfig = advancedNetworkConfig.getJoin().getTcpIpConfig();
        Assert.assertTrue(tcpIpConfig.isEnabled());
        Assert.assertEquals("127.0.0.1:5700", tcpIpConfig.getRequiredMember());
        Assert.assertFalse(advancedNetworkConfig.getJoin().getMulticastConfig().isEnabled());
        MemberAddressProviderConfig addressProviderConfig = advancedNetworkConfig.getMemberAddressProviderConfig();
        Assert.assertFalse(addressProviderConfig.isEnabled());
        ServerSocketEndpointConfig memberEndpointConfig = ((ServerSocketEndpointConfig) (advancedNetworkConfig.getEndpointConfigs().get(MEMBER)));
        Assert.assertEquals(5700, memberEndpointConfig.getPort());
        Assert.assertEquals(99, memberEndpointConfig.getPortCount());
        Assert.assertFalse(memberEndpointConfig.isPortAutoIncrement());
        Assert.assertTrue(memberEndpointConfig.getInterfaces().isEnabled());
        assertContains(memberEndpointConfig.getInterfaces().getInterfaces(), "127.0.0.1");
        Assert.assertTrue(memberEndpointConfig.isReuseAddress());
        Assert.assertTrue(memberEndpointConfig.getSocketInterceptorConfig().isEnabled());
        Assert.assertEquals("com.hazelcast.SocketInterceptor", memberEndpointConfig.getSocketInterceptorConfig().getClassName());
        Assert.assertTrue(memberEndpointConfig.isSocketBufferDirect());
        Assert.assertTrue(memberEndpointConfig.isSocketKeepAlive());
        Assert.assertFalse(memberEndpointConfig.isSocketTcpNoDelay());
        EndpointConfig wanConfig = advancedNetworkConfig.getEndpointConfigs().get(EndpointQualifier.resolve(WAN, "wan-tokyo"));
        Assert.assertFalse(wanConfig.getInterfaces().isEnabled());
        Assert.assertTrue(wanConfig.getSymmetricEncryptionConfig().isEnabled());
        Assert.assertEquals("PBEWithMD5AndDES", wanConfig.getSymmetricEncryptionConfig().getAlgorithm());
        Assert.assertEquals("thesalt", wanConfig.getSymmetricEncryptionConfig().getSalt());
        Assert.assertEquals("thepass", wanConfig.getSymmetricEncryptionConfig().getPassword());
        Assert.assertEquals(19, wanConfig.getSymmetricEncryptionConfig().getIterationCount());
        ServerSocketEndpointConfig clientEndpointConfig = ((ServerSocketEndpointConfig) (advancedNetworkConfig.getEndpointConfigs().get(CLIENT)));
        Assert.assertEquals(9919, clientEndpointConfig.getPort());
        Assert.assertEquals(10, clientEndpointConfig.getPortCount());
        Assert.assertFalse(clientEndpointConfig.isPortAutoIncrement());
        Assert.assertTrue(clientEndpointConfig.isReuseAddress());
        RestServerEndpointConfig restServerEndpointConfig = advancedNetworkConfig.getRestEndpointConfig();
        Assert.assertEquals(9999, restServerEndpointConfig.getPort());
        Assert.assertTrue(restServerEndpointConfig.isPortAutoIncrement());
        assertContainsAll(restServerEndpointConfig.getEnabledGroups(), Arrays.asList(HEALTH_CHECK, CLUSTER_READ));
        WanReplicationConfig testWan = config.getWanReplicationConfig("testWan");
        WanPublisherConfig tokyoWanPublisherConfig = null;
        for (WanPublisherConfig wanPublisherConfig : testWan.getWanPublisherConfigs()) {
            if (wanPublisherConfig.getPublisherId().equals("tokyoPublisherId")) {
                tokyoWanPublisherConfig = wanPublisherConfig;
                break;
            }
        }
        Assert.assertNotNull(tokyoWanPublisherConfig);
        Assert.assertEquals("wan-tokyo", tokyoWanPublisherConfig.getEndpoint());
    }
}

