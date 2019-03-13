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
package com.hazelcast.client.config;


import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import MemoryAllocatorType.STANDARD;
import com.hazelcast.client.LoadBalancer;
import com.hazelcast.client.util.RandomLB;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionConfig.MaxSizePolicy;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
public class ClientConfigXmlGeneratorTest extends HazelcastTestSupport {
    private static final boolean DEBUG = false;

    private static final Random RANDOM = new Random();

    private ClientConfig clientConfig = new ClientConfig();

    @Test
    public void escape() {
        String toEscape = "<>&\"\'";
        // escape xml value
        GroupConfig groupConfig = new GroupConfig(toEscape, "pass");
        // escape xml attribute
        NearCacheConfig nearCacheConfig = new NearCacheConfig(toEscape);
        clientConfig.setGroupConfig(groupConfig).addNearCacheConfig(nearCacheConfig);
        ClientConfig actual = newConfigViaGenerator();
        Assert.assertEquals(groupConfig.getName(), actual.getGroupConfig().getName());
        Assert.assertEquals(toEscape, actual.getNearCacheConfig(toEscape).getName());
    }

    @Test
    public void instanceName() {
        String instanceName = randomString();
        clientConfig.setInstanceName(instanceName);
        ClientConfig actual = newConfigViaGenerator();
        Assert.assertEquals(instanceName, actual.getInstanceName());
    }

    @Test
    public void labels() {
        clientConfig.addLabel("foo");
        ClientConfig actual = newConfigViaGenerator();
        Set<String> labels = actual.getLabels();
        Assert.assertEquals(1, labels.size());
        assertContains(labels, "foo");
    }

    @Test
    public void group() {
        String name = randomString();
        String pass = randomString();
        GroupConfig expected = new GroupConfig(name, pass);
        clientConfig.setGroupConfig(expected);
        ClientConfig actual = newConfigViaGenerator();
        Assert.assertEquals(name, actual.getGroupConfig().getName());
        Assert.assertEquals(pass, actual.getGroupConfig().getPassword());
    }

    @Test
    public void nameAndProperties() {
        String name = randomString();
        String property = randomString();
        clientConfig.setInstanceName(name);
        clientConfig.setProperty("prop", property);
        ClientConfig actual = newConfigViaGenerator();
        Assert.assertEquals(name, actual.getInstanceName());
        ClientConfigXmlGeneratorTest.assertProperties(clientConfig.getProperties(), actual.getProperties());
    }

    @Test
    public void network() {
        ClientNetworkConfig expected = new ClientNetworkConfig();
        expected.setSmartRouting(false).setRedoOperation(true).setConnectionTimeout(ClientConfigXmlGeneratorTest.randomInt()).setConnectionAttemptPeriod(ClientConfigXmlGeneratorTest.randomInt()).setConnectionAttemptLimit(ClientConfigXmlGeneratorTest.randomInt()).addAddress(randomString()).setOutboundPortDefinitions(Collections.singleton(randomString()));
        clientConfig.setNetworkConfig(expected);
        ClientNetworkConfig actual = newConfigViaGenerator().getNetworkConfig();
        Assert.assertFalse(actual.isSmartRouting());
        Assert.assertTrue(actual.isRedoOperation());
        Assert.assertEquals(expected.getConnectionTimeout(), actual.getConnectionTimeout());
        Assert.assertEquals(expected.getConnectionAttemptPeriod(), actual.getConnectionAttemptPeriod());
        Assert.assertEquals(expected.getConnectionAttemptLimit(), actual.getConnectionAttemptLimit());
        ClientConfigXmlGeneratorTest.assertCollection(expected.getAddresses(), actual.getAddresses());
        ClientConfigXmlGeneratorTest.assertCollection(expected.getOutboundPortDefinitions(), actual.getOutboundPortDefinitions());
    }

    @Test
    public void networkSocketOptions() {
        SocketOptions expected = new SocketOptions();
        expected.setBufferSize(ClientConfigXmlGeneratorTest.randomInt()).setLingerSeconds(ClientConfigXmlGeneratorTest.randomInt()).setReuseAddress(false).setKeepAlive(false).setTcpNoDelay(false);
        clientConfig.getNetworkConfig().setSocketOptions(expected);
        SocketOptions actual = newConfigViaGenerator().getNetworkConfig().getSocketOptions();
        Assert.assertEquals(expected.getBufferSize(), actual.getBufferSize());
        Assert.assertEquals(expected.getLingerSeconds(), actual.getLingerSeconds());
        Assert.assertEquals(expected.isReuseAddress(), actual.isReuseAddress());
        Assert.assertEquals(expected.isKeepAlive(), actual.isKeepAlive());
        Assert.assertEquals(expected.isTcpNoDelay(), actual.isTcpNoDelay());
    }

    @Test
    public void networkSocketInterceptor() {
        SocketInterceptorConfig expected = new SocketInterceptorConfig();
        expected.setEnabled(true).setClassName(randomString()).setProperty("prop", randomString());
        clientConfig.getNetworkConfig().setSocketInterceptorConfig(expected);
        SocketInterceptorConfig actual = newConfigViaGenerator().getNetworkConfig().getSocketInterceptorConfig();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void networkSsl() {
        SSLConfig expected = new SSLConfig();
        expected.setFactoryClassName(randomString()).setEnabled(true).setProperty("prop", randomString());
        clientConfig.getNetworkConfig().setSSLConfig(expected);
        SSLConfig actual = newConfigViaGenerator().getNetworkConfig().getSSLConfig();
        Assert.assertEquals(expected.isEnabled(), actual.isEnabled());
        Assert.assertEquals(expected.getFactoryClassName(), actual.getFactoryClassName());
        ClientConfigXmlGeneratorTest.assertProperties(expected.getProperties(), actual.getProperties());
    }

    @Test
    public void networkAws() {
        ClientAwsConfig expected = new ClientAwsConfig();
        expected.setInsideAws(true).setEnabled(true).setTagValue(randomString()).setTagKey(randomString()).setSecurityGroupName(randomString()).setHostHeader(randomString()).setRegion(randomString()).setIamRole(randomString()).setSecretKey(randomString()).setAccessKey(randomString()).setConnectionTimeoutSeconds(ClientConfigXmlGeneratorTest.randomInt());
        clientConfig.getNetworkConfig().setAwsConfig(expected);
        ClientAwsConfig actual = newConfigViaGenerator().getNetworkConfig().getAwsConfig();
        Assert.assertEquals(expected.isInsideAws(), actual.isInsideAws());
        Assert.assertEquals(expected.isEnabled(), actual.isEnabled());
        Assert.assertEquals(expected.getTagValue(), actual.getTagValue());
        Assert.assertEquals(expected.getTagKey(), actual.getTagKey());
        Assert.assertEquals(expected.getSecurityGroupName(), actual.getSecurityGroupName());
        Assert.assertEquals(expected.getHostHeader(), actual.getHostHeader());
        Assert.assertEquals(expected.getRegion(), actual.getRegion());
        Assert.assertEquals(expected.getIamRole(), actual.getIamRole());
        Assert.assertEquals(expected.getSecretKey(), actual.getSecretKey());
        Assert.assertEquals(expected.getAccessKey(), actual.getAccessKey());
        Assert.assertEquals(expected.getConnectionTimeoutSeconds(), actual.getConnectionTimeoutSeconds());
    }

    @Test
    public void networkIcmp() {
        ClientIcmpPingConfig expected = new ClientIcmpPingConfig();
        expected.setEnabled(true).setEchoFailFastOnStartup(false).setIntervalMilliseconds(ClientConfigXmlGeneratorTest.randomInt()).setMaxAttempts(ClientConfigXmlGeneratorTest.randomInt()).setTimeoutMilliseconds(ClientConfigXmlGeneratorTest.randomInt()).setTtl(ClientConfigXmlGeneratorTest.randomInt());
        clientConfig.getNetworkConfig().setClientIcmpPingConfig(expected);
        ClientIcmpPingConfig actual = newConfigViaGenerator().getNetworkConfig().getClientIcmpPingConfig();
        Assert.assertEquals(expected.isEnabled(), actual.isEnabled());
        Assert.assertEquals(expected.isEchoFailFastOnStartup(), actual.isEchoFailFastOnStartup());
        Assert.assertEquals(expected.getIntervalMilliseconds(), actual.getIntervalMilliseconds());
        Assert.assertEquals(expected.getMaxAttempts(), actual.getMaxAttempts());
        Assert.assertEquals(expected.getTimeoutMilliseconds(), actual.getTimeoutMilliseconds());
        Assert.assertEquals(expected.getTtl(), actual.getTtl());
    }

    @Test
    public void discovery() {
        DiscoveryConfig expected = new DiscoveryConfig();
        expected.setNodeFilterClass(randomString());
        DiscoveryStrategyConfig discoveryStrategy = new DiscoveryStrategyConfig(randomString());
        discoveryStrategy.addProperty("prop", randomString());
        expected.addDiscoveryStrategyConfig(discoveryStrategy);
        clientConfig.getNetworkConfig().setDiscoveryConfig(expected);
        DiscoveryConfig actual = newConfigViaGenerator().getNetworkConfig().getDiscoveryConfig();
        Assert.assertEquals(expected.getNodeFilterClass(), actual.getNodeFilterClass());
        ClientConfigXmlGeneratorTest.assertCollection(expected.getDiscoveryStrategyConfigs(), actual.getDiscoveryStrategyConfigs(), new Comparator<DiscoveryStrategyConfig>() {
            @Override
            public int compare(DiscoveryStrategyConfig o1, DiscoveryStrategyConfig o2) {
                ClientConfigXmlGeneratorTest.assertMap(o1.getProperties(), o2.getProperties());
                return o1.getClassName().equals(o2.getClassName()) ? 0 : -1;
            }
        });
    }

    @Test
    public void executorPoolSize() {
        clientConfig.setExecutorPoolSize(ClientConfigXmlGeneratorTest.randomInt());
        ClientConfig actual = newConfigViaGenerator();
        Assert.assertEquals(clientConfig.getExecutorPoolSize(), actual.getExecutorPoolSize());
    }

    @Test
    public void security() {
        clientConfig.getSecurityConfig().setCredentialsClassname(randomString());
        ClientConfig actual = newConfigViaGenerator();
        Assert.assertEquals(clientConfig.getSecurityConfig().getCredentialsClassname(), actual.getSecurityConfig().getCredentialsClassname());
    }

    @Test
    public void listener() {
        ListenerConfig expected = new ListenerConfig(randomString());
        clientConfig.addListenerConfig(expected);
        ClientConfig actual = newConfigViaGenerator();
        ClientConfigXmlGeneratorTest.assertCollection(clientConfig.getListenerConfigs(), actual.getListenerConfigs());
    }

    @Test
    public void serialization() {
        SerializationConfig expected = new SerializationConfig();
        expected.setPortableVersion(ClientConfigXmlGeneratorTest.randomInt()).setUseNativeByteOrder(true).setByteOrder(ByteOrder.LITTLE_ENDIAN).setEnableCompression(true).setEnableSharedObject(false).setAllowUnsafe(true).setCheckClassDefErrors(false).addDataSerializableFactoryClass(ClientConfigXmlGeneratorTest.randomInt(), randomString()).addPortableFactoryClass(ClientConfigXmlGeneratorTest.randomInt(), randomString()).setGlobalSerializerConfig(new GlobalSerializerConfig().setClassName(randomString()).setOverrideJavaSerialization(true)).addSerializerConfig(new SerializerConfig().setClassName(randomString()).setTypeClassName(randomString()));
        clientConfig.setSerializationConfig(expected);
        SerializationConfig actual = newConfigViaGenerator().getSerializationConfig();
        Assert.assertEquals(expected.getPortableVersion(), actual.getPortableVersion());
        Assert.assertEquals(expected.isUseNativeByteOrder(), actual.isUseNativeByteOrder());
        Assert.assertEquals(expected.getByteOrder(), actual.getByteOrder());
        Assert.assertEquals(expected.isEnableCompression(), actual.isEnableCompression());
        Assert.assertEquals(expected.isEnableSharedObject(), actual.isEnableSharedObject());
        Assert.assertEquals(expected.isAllowUnsafe(), actual.isAllowUnsafe());
        Assert.assertEquals(expected.isCheckClassDefErrors(), actual.isCheckClassDefErrors());
        Assert.assertEquals(expected.getGlobalSerializerConfig(), actual.getGlobalSerializerConfig());
        ClientConfigXmlGeneratorTest.assertCollection(expected.getSerializerConfigs(), actual.getSerializerConfigs());
        ClientConfigXmlGeneratorTest.assertMap(expected.getDataSerializableFactoryClasses(), actual.getDataSerializableFactoryClasses());
        ClientConfigXmlGeneratorTest.assertMap(expected.getPortableFactoryClasses(), actual.getPortableFactoryClasses());
    }

    @Test
    public void nativeMemory() {
        NativeMemoryConfig expected = new NativeMemoryConfig();
        expected.setEnabled(true).setAllocatorType(STANDARD).setMetadataSpacePercentage(70).setMinBlockSize(ClientConfigXmlGeneratorTest.randomInt()).setPageSize(ClientConfigXmlGeneratorTest.randomInt()).setSize(new com.hazelcast.memory.MemorySize(ClientConfigXmlGeneratorTest.randomInt(), MemoryUnit.BYTES));
        clientConfig.setNativeMemoryConfig(expected);
        NativeMemoryConfig actual = newConfigViaGenerator().getNativeMemoryConfig();
        Assert.assertEquals(clientConfig.getNativeMemoryConfig(), actual);
    }

    @Test
    public void proxyFactory() {
        ProxyFactoryConfig expected = new ProxyFactoryConfig();
        expected.setClassName(randomString()).setService(randomString());
        clientConfig.addProxyFactoryConfig(expected);
        List<ProxyFactoryConfig> actual = newConfigViaGenerator().getProxyFactoryConfigs();
        ClientConfigXmlGeneratorTest.assertCollection(clientConfig.getProxyFactoryConfigs(), actual);
    }

    @Test
    public void loadBalancer() {
        clientConfig.setLoadBalancer(new RandomLB());
        LoadBalancer actual = newConfigViaGenerator().getLoadBalancer();
        Assert.assertTrue((actual instanceof RandomLB));
    }

    @Test
    public void nearCache() {
        NearCacheConfig expected = new NearCacheConfig();
        expected.setInMemoryFormat(NATIVE).setSerializeKeys(true).setInvalidateOnChange(false).setCacheLocalEntries(true).setTimeToLiveSeconds(ClientConfigXmlGeneratorTest.randomInt()).setMaxIdleSeconds(ClientConfigXmlGeneratorTest.randomInt()).setLocalUpdatePolicy(LocalUpdatePolicy.CACHE_ON_UPDATE).setName(randomString()).setPreloaderConfig(new NearCachePreloaderConfig().setEnabled(true).setDirectory(randomString()).setStoreInitialDelaySeconds(ClientConfigXmlGeneratorTest.randomInt()).setStoreIntervalSeconds(ClientConfigXmlGeneratorTest.randomInt())).setEvictionConfig(// Comparator class name cannot set via xml
        // see https://github.com/hazelcast/hazelcast/issues/14093
        // .setComparatorClassName(randomString())
        new EvictionConfig().setEvictionPolicy(EvictionPolicy.LFU).setMaximumSizePolicy(MaxSizePolicy.USED_NATIVE_MEMORY_SIZE).setSize(ClientConfigXmlGeneratorTest.randomInt()));
        clientConfig.addNearCacheConfig(expected);
        Map<String, NearCacheConfig> actual = newConfigViaGenerator().getNearCacheConfigMap();
        ClientConfigXmlGeneratorTest.assertMap(clientConfig.getNearCacheConfigMap(), actual);
    }

    @Test
    public void queryCache() {
        QueryCacheConfig expected = new QueryCacheConfig();
        expected.setBufferSize(ClientConfigXmlGeneratorTest.randomInt()).setInMemoryFormat(OBJECT).setName(randomString()).setBatchSize(ClientConfigXmlGeneratorTest.randomInt()).setCoalesce(true).setDelaySeconds(ClientConfigXmlGeneratorTest.randomInt()).setIncludeValue(false).setPopulate(false).setPredicateConfig(new com.hazelcast.config.PredicateConfig(randomString())).setEvictionConfig(// Comparator class name cannot set via xml
        // see https://github.com/hazelcast/hazelcast/issues/14093
        // .setComparatorClassName(randomString())
        new EvictionConfig().setEvictionPolicy(EvictionPolicy.LFU).setMaximumSizePolicy(MaxSizePolicy.USED_NATIVE_MEMORY_SIZE).setSize(ClientConfigXmlGeneratorTest.randomInt())).addIndexConfig(new MapIndexConfig().setOrdered(true).setAttribute(randomString())).addEntryListenerConfig(((EntryListenerConfig) (new EntryListenerConfig().setIncludeValue(true).setLocal(true).setClassName(randomString()))));
        clientConfig.addQueryCacheConfig(randomString(), expected);
        Map<String, Map<String, QueryCacheConfig>> actual = newConfigViaGenerator().getQueryCacheConfigs();
        ClientConfigXmlGeneratorTest.assertMap(clientConfig.getQueryCacheConfigs(), actual);
    }

    @Test
    public void connectionStrategy() {
        ClientConnectionStrategyConfig expected = new ClientConnectionStrategyConfig();
        expected.setAsyncStart(true).setReconnectMode(ReconnectMode.ASYNC);
        clientConfig.setConnectionStrategyConfig(expected);
        ClientConnectionStrategyConfig actual = newConfigViaGenerator().getConnectionStrategyConfig();
        Assert.assertEquals(expected.isAsyncStart(), actual.isAsyncStart());
        Assert.assertEquals(expected.getReconnectMode(), actual.getReconnectMode());
    }

    @Test
    public void userCodeDeployment() {
        ClientUserCodeDeploymentConfig expected = new ClientUserCodeDeploymentConfig();
        expected.setEnabled(true).addClass(randomString()).addJar(randomString());
        clientConfig.setUserCodeDeploymentConfig(expected);
        ClientUserCodeDeploymentConfig actual = newConfigViaGenerator().getUserCodeDeploymentConfig();
        Assert.assertEquals(expected.isEnabled(), actual.isEnabled());
        ClientConfigXmlGeneratorTest.assertCollection(expected.getClassNames(), actual.getClassNames());
        ClientConfigXmlGeneratorTest.assertCollection(expected.getJarPaths(), actual.getJarPaths());
    }

    @Test
    public void flakeIdGenerator() {
        ClientFlakeIdGeneratorConfig expected = new ClientFlakeIdGeneratorConfig(randomString());
        expected.setPrefetchCount(ClientConfigXmlGeneratorTest.randomInt()).setPrefetchValidityMillis(ClientConfigXmlGeneratorTest.randomInt());
        clientConfig.addFlakeIdGeneratorConfig(expected);
        Map<String, ClientFlakeIdGeneratorConfig> actual = newConfigViaGenerator().getFlakeIdGeneratorConfigMap();
        ClientConfigXmlGeneratorTest.assertMap(clientConfig.getFlakeIdGeneratorConfigMap(), actual);
    }
}

