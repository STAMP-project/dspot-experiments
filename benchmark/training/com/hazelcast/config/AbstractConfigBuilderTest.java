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


import EndpointQualifier.CLIENT;
import EndpointQualifier.MEMBER;
import EndpointQualifier.MEMCACHE;
import RestEndpointGroup.WAN;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Abstract class defining the common test cases for XML and YAML
 * based configuration tests.
 * <p/>
 * All common test cases should be defined in this class to guarantee
 * compilation error if either YAML or XML configuration misses to cover
 * a common case.
 * <p/>
 * For specific test cases, see {@link XmlOnlyConfigBuilderTest} and
 * {@link YamlOnlyConfigBuilderTest}.
 *
 * @see XMLConfigBuilderTest
 * @see YamlConfigBuilderTest
 */
public abstract class AbstractConfigBuilderTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testCompleteAdvancedNetworkConfig() {
        Config config = buildCompleteAdvancedNetworkConfig();
        AdvancedNetworkConfig advancedNetworkConfig = config.getAdvancedNetworkConfig();
        JoinConfig joinConfig = advancedNetworkConfig.getJoin();
        IcmpFailureDetectorConfig fdConfig = advancedNetworkConfig.getIcmpFailureDetectorConfig();
        MemberAddressProviderConfig providerConfig = advancedNetworkConfig.getMemberAddressProviderConfig();
        Assert.assertTrue(advancedNetworkConfig.isEnabled());
        // join config
        Assert.assertFalse(joinConfig.getMulticastConfig().isEnabled());
        Assert.assertTrue(joinConfig.getTcpIpConfig().isEnabled());
        Assert.assertEquals("10.10.1.10", joinConfig.getTcpIpConfig().getRequiredMember());
        HazelcastTestSupport.assertContains(joinConfig.getTcpIpConfig().getMembers(), "10.10.1.11");
        HazelcastTestSupport.assertContains(joinConfig.getTcpIpConfig().getMembers(), "10.10.1.12");
        // failure detector config
        Assert.assertTrue(fdConfig.isEnabled());
        Assert.assertTrue(fdConfig.isParallelMode());
        Assert.assertTrue(fdConfig.isFailFastOnStartup());
        Assert.assertEquals(42, fdConfig.getTimeoutMilliseconds());
        Assert.assertEquals(42, fdConfig.getMaxAttempts());
        Assert.assertEquals(4200, fdConfig.getIntervalMilliseconds());
        Assert.assertEquals(255, fdConfig.getTtl());
        // member address provider config
        Assert.assertEquals("com.hazelcast.test.Provider", providerConfig.getClassName());
        // endpoint config
        ServerSocketEndpointConfig memberEndpointConfig = ((ServerSocketEndpointConfig) (advancedNetworkConfig.getEndpointConfigs().get(MEMBER)));
        Assert.assertEquals("member-server-socket", memberEndpointConfig.getName());
        Assert.assertEquals(ProtocolType.MEMBER, memberEndpointConfig.getProtocolType());
        // port
        Assert.assertEquals(93, memberEndpointConfig.getPortCount());
        Assert.assertEquals(9191, memberEndpointConfig.getPort());
        Assert.assertFalse(memberEndpointConfig.isPortAutoIncrement());
        // reuse address
        Assert.assertTrue(memberEndpointConfig.isReuseAddress());
        // outbound ports
        Assert.assertEquals("33000-33100", memberEndpointConfig.getOutboundPortDefinitions().iterator().next());
        // interfaces
        Assert.assertTrue(memberEndpointConfig.getInterfaces().isEnabled());
        Assert.assertEquals("10.10.0.1", memberEndpointConfig.getInterfaces().getInterfaces().iterator().next());
        // ssl
        Assert.assertTrue(memberEndpointConfig.getSSLConfig().isEnabled());
        Assert.assertEquals("com.hazelcast.examples.MySSLContextFactory", memberEndpointConfig.getSSLConfig().getFactoryClassName());
        Assert.assertEquals("bar", memberEndpointConfig.getSSLConfig().getProperty("foo"));
        // socket interceptor
        Assert.assertTrue(memberEndpointConfig.getSocketInterceptorConfig().isEnabled());
        Assert.assertEquals("com.hazelcast.examples.MySocketInterceptor", memberEndpointConfig.getSocketInterceptorConfig().getClassName());
        Assert.assertEquals("baz", memberEndpointConfig.getSocketInterceptorConfig().getProperty("foo"));
        // symmetric encryption config
        Assert.assertTrue(memberEndpointConfig.getSymmetricEncryptionConfig().isEnabled());
        Assert.assertEquals("Algorithm", memberEndpointConfig.getSymmetricEncryptionConfig().getAlgorithm());
        Assert.assertEquals("thesalt", memberEndpointConfig.getSymmetricEncryptionConfig().getSalt());
        Assert.assertEquals("thepassword", memberEndpointConfig.getSymmetricEncryptionConfig().getPassword());
        Assert.assertEquals(1000, memberEndpointConfig.getSymmetricEncryptionConfig().getIterationCount());
        // socket options
        Assert.assertTrue(memberEndpointConfig.isSocketBufferDirect());
        Assert.assertTrue(memberEndpointConfig.isSocketTcpNoDelay());
        Assert.assertTrue(memberEndpointConfig.isSocketKeepAlive());
        Assert.assertEquals(33, memberEndpointConfig.getSocketConnectTimeoutSeconds());
        Assert.assertEquals(34, memberEndpointConfig.getSocketSendBufferSizeKb());
        Assert.assertEquals(67, memberEndpointConfig.getSocketRcvBufferSizeKb());
        Assert.assertEquals(11, memberEndpointConfig.getSocketLingerSeconds());
        RestServerEndpointConfig restServerEndpointConfig = advancedNetworkConfig.getRestEndpointConfig();
        Assert.assertEquals(8080, restServerEndpointConfig.getPort());
        HazelcastTestSupport.assertContainsAll(restServerEndpointConfig.getEnabledGroups(), Arrays.asList(RestEndpointGroup.CLUSTER_READ, WAN, RestEndpointGroup.HEALTH_CHECK));
        // memcache config
        EndpointConfig memcacheEndpointConfig = advancedNetworkConfig.getEndpointConfigs().get(MEMCACHE);
        Assert.assertEquals(ProtocolType.MEMCACHE, memcacheEndpointConfig.getProtocolType());
        Assert.assertEquals("42000-42100", memcacheEndpointConfig.getOutboundPortDefinitions().iterator().next());
        // WAN server socket config
        EndpointConfig wanServerSockerEndpointConfig = advancedNetworkConfig.getEndpointConfigs().get(EndpointQualifier.resolve(ProtocolType.WAN, "WAN_SERVER"));
        Assert.assertEquals(ProtocolType.WAN, wanServerSockerEndpointConfig.getProtocolType());
        Assert.assertEquals("52000-52100", wanServerSockerEndpointConfig.getOutboundPortDefinitions().iterator().next());
        // WAN endpoint config
        EndpointConfig wanEndpointConfig = advancedNetworkConfig.getEndpointConfigs().get(EndpointQualifier.resolve(ProtocolType.WAN, "WAN_ENDPOINT"));
        Assert.assertEquals(ProtocolType.WAN, wanEndpointConfig.getProtocolType());
        Assert.assertEquals("62000-62100", wanEndpointConfig.getOutboundPortDefinitions().iterator().next());
        // client server socket config
        EndpointConfig clientServerSocketConfig = advancedNetworkConfig.getEndpointConfigs().get(CLIENT);
        Assert.assertEquals(ProtocolType.CLIENT, clientServerSocketConfig.getProtocolType());
        Assert.assertEquals("72000-72100", clientServerSocketConfig.getOutboundPortDefinitions().iterator().next());
    }
}

