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
package com.hazelcast.config;


import GroupProperty.REST_ENABLED;
import GroupProperty.SOCKET_RECEIVE_BUFFER_SIZE;
import GroupProperty.SOCKET_SEND_BUFFER_SIZE;
import ProtocolType.CLIENT;
import ProtocolType.MEMBER;
import ServerSocketEndpointConfig.DEFAULT_PORT;
import ServerSocketEndpointConfig.PORT_AUTO_INCREMENT;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.IdentityHashMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AdvancedNetworkConfigTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testDefault() {
        AdvancedNetworkConfig config = new AdvancedNetworkConfig();
        ServerSocketEndpointConfig defaultEndpointConfig = ((ServerSocketEndpointConfig) (config.getEndpointConfigs().get(EndpointQualifier.MEMBER)));
        Assert.assertNotNull(defaultEndpointConfig);
        Assert.assertEquals(MEMBER, defaultEndpointConfig.getProtocolType());
        Assert.assertEquals(DEFAULT_PORT, defaultEndpointConfig.getPort());
        Assert.assertEquals(PORT_AUTO_INCREMENT, defaultEndpointConfig.getPortCount());
        Assert.assertTrue(defaultEndpointConfig.isPortAutoIncrement());
        Assert.assertEquals(Integer.parseInt(SOCKET_RECEIVE_BUFFER_SIZE.getDefaultValue()), defaultEndpointConfig.getSocketRcvBufferSizeKb());
        Assert.assertEquals(Integer.parseInt(SOCKET_SEND_BUFFER_SIZE.getDefaultValue()), defaultEndpointConfig.getSocketSendBufferSizeKb());
    }

    @Test
    public void testFailFast_whenRestEnabledWithoutTextEndpoint() {
        Config config = new Config();
        config.getAdvancedNetworkConfig().setEnabled(true);
        config.setProperty(REST_ENABLED.getName(), "true");
        expected.expect(InvalidConfigurationException.class);
        createHazelcastInstance(config);
    }

    @Test
    public void testFailFast_whenWanPublisherRequiresUndefinedEndpointConfig() {
        Config config = new Config();
        config.getAdvancedNetworkConfig().setEnabled(true);
        config.addWanReplicationConfig(new WanReplicationConfig().setName("seattle-tokyo").addWanPublisherConfig(new WanPublisherConfig().setGroupName("target-cluster").setEndpoint("does-not-exist")));
        expected.expect(InvalidConfigurationException.class);
        createHazelcastInstance(config);
    }

    @Test
    public void testFailFast_whenNoMemberServerSocketDefined() {
        Config config = new Config();
        config.getAdvancedNetworkConfig().setEnabled(true).getEndpointConfigs().remove(EndpointQualifier.MEMBER);
        expected.expect(InvalidConfigurationException.class);
        createHazelcastInstance(config);
    }

    @Test
    public void test_setMemberServerSocketConfig_replacesPreviousConfig() {
        AdvancedNetworkConfig config = new AdvancedNetworkConfig();
        config.setMemberEndpointConfig(new ServerSocketEndpointConfig().setProtocolType(MEMBER).setPort(19999));
        config.setMemberEndpointConfig(new ServerSocketEndpointConfig().setProtocolType(MEMBER).setPort(11000));
        Assert.assertEquals(11000, getPort());
    }

    @Test
    public void test_setClientServerSocketConfig_replacesPreviousConfig() {
        AdvancedNetworkConfig config = new AdvancedNetworkConfig();
        config.setClientEndpointConfig(new ServerSocketEndpointConfig().setProtocolType(CLIENT).setPort(19999));
        config.setClientEndpointConfig(new ServerSocketEndpointConfig().setProtocolType(CLIENT).setPort(11000));
        Assert.assertEquals(11000, getPort());
    }

    @Test
    public void test_setTextServerSocketConfig_replacesPreviousConfig() {
        AdvancedNetworkConfig config = new AdvancedNetworkConfig();
        config.setRestEndpointConfig(new RestServerEndpointConfig().setPort(19999));
        config.setRestEndpointConfig(new RestServerEndpointConfig().setPort(11000));
        Assert.assertEquals(11000, getPort());
    }

    @Test
    public void test_setMemcacheEndpointConfig_replacesPreviousConfig() {
        AdvancedNetworkConfig config = new AdvancedNetworkConfig();
        config.setMemcacheEndpointConfig(new ServerSocketEndpointConfig().setPort(19999));
        config.setMemcacheEndpointConfig(new ServerSocketEndpointConfig().setPort(11000));
        Assert.assertEquals(11000, getPort());
    }

    @Test
    public void test_setEndpointConfigs_throwsException_whenServerSocketCardinalityBroken() throws Exception {
        IdentityHashMap<EndpointQualifier, EndpointConfig> offendingEndpointConfigs = new IdentityHashMap<EndpointQualifier, EndpointConfig>();
        EndpointQualifier one = createReflectively(MEMBER, "1");
        EndpointQualifier two = createReflectively(MEMBER, "2");
        offendingEndpointConfigs.put(one, new ServerSocketEndpointConfig());
        offendingEndpointConfigs.put(two, new ServerSocketEndpointConfig());
        AdvancedNetworkConfig config = new AdvancedNetworkConfig();
        expected.expect(InvalidConfigurationException.class);
        config.setEndpointConfigs(offendingEndpointConfigs);
    }
}

