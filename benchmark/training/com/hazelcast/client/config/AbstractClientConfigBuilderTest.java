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


import ClientConnectionStrategyConfig.ReconnectMode.ASYNC;
import ClientConnectionStrategyConfig.ReconnectMode.ON;
import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import InMemoryFormat.BINARY;
import InMemoryFormat.OBJECT;
import TopicOverloadPolicy.DISCARD_NEWEST;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.XMLConfigBuilderTest;
import com.hazelcast.test.HazelcastTestSupport;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractClientConfigBuilderTest extends HazelcastTestSupport {
    protected ClientConfig fullClientConfig;

    protected ClientConfig defaultClientConfig;

    @Test
    public void testNetworkConfig() {
        final ClientNetworkConfig networkConfig = fullClientConfig.getNetworkConfig();
        Assert.assertEquals(2, networkConfig.getConnectionAttemptLimit());
        Assert.assertEquals(2, networkConfig.getAddresses().size());
        assertContains(networkConfig.getAddresses(), "127.0.0.1");
        assertContains(networkConfig.getAddresses(), "127.0.0.2");
        Collection<String> allowedPorts = networkConfig.getOutboundPortDefinitions();
        Assert.assertEquals(2, allowedPorts.size());
        Assert.assertTrue(allowedPorts.contains("34600"));
        Assert.assertTrue(allowedPorts.contains("34700-34710"));
        Assert.assertTrue(networkConfig.isSmartRouting());
        Assert.assertTrue(networkConfig.isRedoOperation());
        final SocketInterceptorConfig socketInterceptorConfig = networkConfig.getSocketInterceptorConfig();
        Assert.assertTrue(socketInterceptorConfig.isEnabled());
        Assert.assertEquals("com.hazelcast.examples.MySocketInterceptor", socketInterceptorConfig.getClassName());
        Assert.assertEquals("bar", socketInterceptorConfig.getProperty("foo"));
        AwsConfig awsConfig = networkConfig.getAwsConfig();
        Assert.assertTrue(awsConfig.isEnabled());
        Assert.assertEquals("TEST_ACCESS_KEY", awsConfig.getProperty("access-key"));
        Assert.assertEquals("TEST_SECRET_KEY", awsConfig.getProperty("secret-key"));
        Assert.assertEquals("us-east-1", awsConfig.getProperty("region"));
        Assert.assertEquals("ec2.amazonaws.com", awsConfig.getProperty("host-header"));
        Assert.assertEquals("type", awsConfig.getProperty("tag-key"));
        Assert.assertEquals("hz-nodes", awsConfig.getProperty("tag-value"));
        Assert.assertEquals("11", awsConfig.getProperty("connection-timeout-seconds"));
        Assert.assertFalse(networkConfig.getGcpConfig().isEnabled());
        Assert.assertFalse(networkConfig.getAzureConfig().isEnabled());
        Assert.assertFalse(networkConfig.getKubernetesConfig().isEnabled());
        Assert.assertFalse(networkConfig.getEurekaConfig().isEnabled());
    }

    @Test
    public void testSerializationConfig() {
        final SerializationConfig serializationConfig = fullClientConfig.getSerializationConfig();
        Assert.assertEquals(3, serializationConfig.getPortableVersion());
        final Map<Integer, String> dsClasses = serializationConfig.getDataSerializableFactoryClasses();
        Assert.assertEquals(1, dsClasses.size());
        Assert.assertEquals("com.hazelcast.examples.DataSerializableFactory", dsClasses.get(1));
        final Map<Integer, String> pfClasses = serializationConfig.getPortableFactoryClasses();
        Assert.assertEquals(1, pfClasses.size());
        Assert.assertEquals("com.hazelcast.examples.PortableFactory", pfClasses.get(2));
        final Collection<SerializerConfig> serializerConfigs = serializationConfig.getSerializerConfigs();
        Assert.assertEquals(1, serializerConfigs.size());
        final SerializerConfig serializerConfig = serializerConfigs.iterator().next();
        Assert.assertEquals("com.hazelcast.examples.DummyType", serializerConfig.getTypeClassName());
        Assert.assertEquals("com.hazelcast.examples.SerializerFactory", serializerConfig.getClassName());
        final GlobalSerializerConfig globalSerializerConfig = serializationConfig.getGlobalSerializerConfig();
        Assert.assertEquals("com.hazelcast.examples.GlobalSerializerFactory", globalSerializerConfig.getClassName());
        Assert.assertEquals(ByteOrder.BIG_ENDIAN, serializationConfig.getByteOrder());
        Assert.assertTrue(serializationConfig.isCheckClassDefErrors());
        Assert.assertFalse(serializationConfig.isAllowUnsafe());
        Assert.assertFalse(serializationConfig.isEnableCompression());
        Assert.assertTrue(serializationConfig.isEnableSharedObject());
        Assert.assertTrue(serializationConfig.isUseNativeByteOrder());
        JavaSerializationFilterConfig javaSerializationFilterConfig = serializationConfig.getJavaSerializationFilterConfig();
        ClassFilter blacklist = javaSerializationFilterConfig.getBlacklist();
        Assert.assertEquals(1, blacklist.getClasses().size());
        Assert.assertTrue(blacklist.getClasses().contains("com.acme.app.BeanComparator"));
        ClassFilter whitelist = javaSerializationFilterConfig.getWhitelist();
        Assert.assertEquals(2, whitelist.getClasses().size());
        Assert.assertTrue(whitelist.getClasses().contains("java.lang.String"));
        Assert.assertTrue(whitelist.getClasses().contains("example.Foo"));
        Assert.assertEquals(2, whitelist.getPackages().size());
        Assert.assertTrue(whitelist.getPackages().contains("com.acme.app"));
        Assert.assertTrue(whitelist.getPackages().contains("com.acme.app.subpkg"));
        Assert.assertEquals(3, whitelist.getPrefixes().size());
        Assert.assertTrue(whitelist.getPrefixes().contains("java"));
        Assert.assertTrue(whitelist.getPrefixes().contains("["));
        Assert.assertTrue(whitelist.getPrefixes().contains("com."));
    }

    @Test
    public void testProxyFactories() {
        final List<ProxyFactoryConfig> pfc = fullClientConfig.getProxyFactoryConfigs();
        Assert.assertEquals(3, pfc.size());
        assertContains(pfc, new ProxyFactoryConfig("com.hazelcast.examples.ProxyXYZ1", "sampleService1"));
        assertContains(pfc, new ProxyFactoryConfig("com.hazelcast.examples.ProxyXYZ2", "sampleService1"));
        assertContains(pfc, new ProxyFactoryConfig("com.hazelcast.examples.ProxyXYZ3", "sampleService3"));
    }

    @Test
    public void testNearCacheConfigs() {
        Assert.assertEquals(1, fullClientConfig.getNearCacheConfigMap().size());
        final NearCacheConfig nearCacheConfig = fullClientConfig.getNearCacheConfig("asd");
        Assert.assertEquals(2000, nearCacheConfig.getMaxSize());
        Assert.assertEquals(2000, nearCacheConfig.getEvictionConfig().getSize());
        Assert.assertEquals(90, nearCacheConfig.getTimeToLiveSeconds());
        Assert.assertEquals(100, nearCacheConfig.getMaxIdleSeconds());
        Assert.assertEquals("LFU", nearCacheConfig.getEvictionPolicy());
        Assert.assertEquals(LFU, nearCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertTrue(nearCacheConfig.isInvalidateOnChange());
        Assert.assertTrue(nearCacheConfig.isSerializeKeys());
        Assert.assertEquals(OBJECT, nearCacheConfig.getInMemoryFormat());
    }

    @Test
    public void testSSLConfigs() {
        SSLConfig sslConfig = fullClientConfig.getNetworkConfig().getSSLConfig();
        Assert.assertNotNull(sslConfig);
        Assert.assertFalse(sslConfig.isEnabled());
        Assert.assertEquals("com.hazelcast.nio.ssl.BasicSSLContextFactory", sslConfig.getFactoryClassName());
        Assert.assertEquals(7, sslConfig.getProperties().size());
        Assert.assertEquals("TLS", sslConfig.getProperty("protocol"));
        Assert.assertEquals("/opt/hazelcast-client.truststore", sslConfig.getProperty("trustStore"));
        Assert.assertEquals("secret.123456", sslConfig.getProperty("trustStorePassword"));
        Assert.assertEquals("JKS", sslConfig.getProperty("trustStoreType"));
        Assert.assertEquals("/opt/hazelcast-client.keystore", sslConfig.getProperty("keyStore"));
        Assert.assertEquals("keystorePassword123", sslConfig.getProperty("keyStorePassword"));
        Assert.assertEquals("JKS", sslConfig.getProperty("keyStoreType"));
    }

    @Test
    public void testNearCacheConfig_withEvictionConfig_withPreloaderConfig() throws IOException {
        URL schemaResource = XMLConfigBuilderTest.class.getClassLoader().getResource("hazelcast-client-test.xml");
        ClientConfig clientConfig = new XmlClientConfigBuilder(schemaResource).build();
        Assert.assertEquals("MyInstanceName", clientConfig.getInstanceName());
        NearCacheConfig nearCacheConfig = clientConfig.getNearCacheConfig("nearCacheWithEvictionAndPreloader");
        Assert.assertEquals(10000, nearCacheConfig.getTimeToLiveSeconds());
        Assert.assertEquals(5000, nearCacheConfig.getMaxIdleSeconds());
        Assert.assertFalse(nearCacheConfig.isInvalidateOnChange());
        Assert.assertEquals(OBJECT, nearCacheConfig.getInMemoryFormat());
        Assert.assertTrue(nearCacheConfig.isCacheLocalEntries());
        Assert.assertNotNull(nearCacheConfig.getEvictionConfig());
        Assert.assertEquals(100, nearCacheConfig.getEvictionConfig().getSize());
        Assert.assertEquals(ENTRY_COUNT, nearCacheConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(LFU, nearCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertNotNull(nearCacheConfig.getPreloaderConfig());
        Assert.assertTrue(nearCacheConfig.getPreloaderConfig().isEnabled());
        Assert.assertEquals("/tmp/myNearCache", nearCacheConfig.getPreloaderConfig().getDirectory());
        Assert.assertEquals(2342, nearCacheConfig.getPreloaderConfig().getStoreInitialDelaySeconds());
        Assert.assertEquals(4223, nearCacheConfig.getPreloaderConfig().getStoreIntervalSeconds());
    }

    @Test
    public void testQueryCacheFullConfig() throws Exception {
        QueryCacheConfig queryCacheClassPredicateConfig = fullClientConfig.getQueryCacheConfigs().get("map-name").get("query-cache-class-name-predicate");
        QueryCacheConfig queryCacheSqlPredicateConfig = fullClientConfig.getQueryCacheConfigs().get("map-name").get("query-cache-sql-predicate");
        EntryListenerConfig entryListenerConfig = queryCacheClassPredicateConfig.getEntryListenerConfigs().get(0);
        Assert.assertEquals("query-cache-class-name-predicate", queryCacheClassPredicateConfig.getName());
        Assert.assertTrue(entryListenerConfig.isIncludeValue());
        Assert.assertFalse(entryListenerConfig.isLocal());
        Assert.assertEquals("com.hazelcast.examples.EntryListener", entryListenerConfig.getClassName());
        Assert.assertTrue(queryCacheClassPredicateConfig.isIncludeValue());
        Assert.assertEquals(1, queryCacheClassPredicateConfig.getBatchSize());
        Assert.assertEquals(16, queryCacheClassPredicateConfig.getBufferSize());
        Assert.assertEquals(0, queryCacheClassPredicateConfig.getDelaySeconds());
        Assert.assertEquals(LRU, queryCacheClassPredicateConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(ENTRY_COUNT, queryCacheClassPredicateConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(10000, queryCacheClassPredicateConfig.getEvictionConfig().getSize());
        Assert.assertEquals(BINARY, queryCacheClassPredicateConfig.getInMemoryFormat());
        Assert.assertFalse(queryCacheClassPredicateConfig.isCoalesce());
        Assert.assertTrue(queryCacheClassPredicateConfig.isPopulate());
        for (MapIndexConfig mapIndexConfig : queryCacheClassPredicateConfig.getIndexConfigs()) {
            Assert.assertEquals("name", mapIndexConfig.getAttribute());
            Assert.assertFalse(mapIndexConfig.isOrdered());
        }
        Assert.assertEquals("com.hazelcast.examples.ExamplePredicate", queryCacheClassPredicateConfig.getPredicateConfig().getClassName());
        Assert.assertEquals("query-cache-sql-predicate", queryCacheSqlPredicateConfig.getName());
        Assert.assertEquals("%age=40", queryCacheSqlPredicateConfig.getPredicateConfig().getSql());
    }

    @Test
    public void testConnectionStrategyConfig() {
        ClientConnectionStrategyConfig connectionStrategyConfig = fullClientConfig.getConnectionStrategyConfig();
        Assert.assertTrue(connectionStrategyConfig.isAsyncStart());
        Assert.assertEquals(ASYNC, connectionStrategyConfig.getReconnectMode());
    }

    @Test
    public void testConnectionStrategyConfig_defaults() {
        ClientConnectionStrategyConfig connectionStrategyConfig = defaultClientConfig.getConnectionStrategyConfig();
        Assert.assertFalse(connectionStrategyConfig.isAsyncStart());
        Assert.assertEquals(ON, connectionStrategyConfig.getReconnectMode());
    }

    @Test
    public void testExponentialConnectionRetryConfig() {
        ClientConnectionStrategyConfig connectionStrategyConfig = fullClientConfig.getConnectionStrategyConfig();
        ConnectionRetryConfig exponentialRetryConfig = connectionStrategyConfig.getConnectionRetryConfig();
        Assert.assertTrue(exponentialRetryConfig.isEnabled());
        Assert.assertTrue(exponentialRetryConfig.isFailOnMaxBackoff());
        Assert.assertEquals(0.5, exponentialRetryConfig.getJitter(), 0);
        Assert.assertEquals(2000, exponentialRetryConfig.getInitialBackoffMillis());
        Assert.assertEquals(60000, exponentialRetryConfig.getMaxBackoffMillis());
        Assert.assertEquals(3, exponentialRetryConfig.getMultiplier(), 0);
    }

    @Test
    public void testExponentialConnectionRetryConfig_defaults() {
        ClientConnectionStrategyConfig connectionStrategyConfig = defaultClientConfig.getConnectionStrategyConfig();
        ConnectionRetryConfig exponentialRetryConfig = connectionStrategyConfig.getConnectionRetryConfig();
        Assert.assertFalse(exponentialRetryConfig.isEnabled());
        Assert.assertFalse(exponentialRetryConfig.isFailOnMaxBackoff());
        Assert.assertEquals(0.2, exponentialRetryConfig.getJitter(), 0);
        Assert.assertEquals(1000, exponentialRetryConfig.getInitialBackoffMillis());
        Assert.assertEquals(30000, exponentialRetryConfig.getMaxBackoffMillis());
        Assert.assertEquals(2, exponentialRetryConfig.getMultiplier(), 0);
    }

    @Test
    public void testLeftovers() {
        Assert.assertEquals(40, fullClientConfig.getExecutorPoolSize());
        Assert.assertEquals("com.hazelcast.client.util.RandomLB", fullClientConfig.getLoadBalancer().getClass().getName());
        final List<ListenerConfig> listenerConfigs = fullClientConfig.getListenerConfigs();
        Assert.assertEquals(3, listenerConfigs.size());
        assertContains(listenerConfigs, new ListenerConfig("com.hazelcast.examples.MembershipListener"));
        assertContains(listenerConfigs, new ListenerConfig("com.hazelcast.examples.InstanceListener"));
        assertContains(listenerConfigs, new ListenerConfig("com.hazelcast.examples.MigrationListener"));
    }

    @Test
    public void testClientIcmpPingConfig() {
        ClientIcmpPingConfig icmpPingConfig = fullClientConfig.getNetworkConfig().getClientIcmpPingConfig();
        Assert.assertEquals(false, icmpPingConfig.isEnabled());
        Assert.assertEquals(2000, icmpPingConfig.getTimeoutMilliseconds());
        Assert.assertEquals(3000, icmpPingConfig.getIntervalMilliseconds());
        Assert.assertEquals(100, icmpPingConfig.getTtl());
        Assert.assertEquals(5, icmpPingConfig.getMaxAttempts());
        Assert.assertEquals(false, icmpPingConfig.isEchoFailFastOnStartup());
    }

    @Test
    public void testClientIcmpPingConfig_defaults() {
        ClientIcmpPingConfig icmpPingConfig = defaultClientConfig.getNetworkConfig().getClientIcmpPingConfig();
        Assert.assertEquals(false, icmpPingConfig.isEnabled());
        Assert.assertEquals(1000, icmpPingConfig.getTimeoutMilliseconds());
        Assert.assertEquals(1000, icmpPingConfig.getIntervalMilliseconds());
        Assert.assertEquals(255, icmpPingConfig.getTtl());
        Assert.assertEquals(2, icmpPingConfig.getMaxAttempts());
        Assert.assertEquals(true, icmpPingConfig.isEchoFailFastOnStartup());
    }

    @Test
    public void testReliableTopic() {
        ClientReliableTopicConfig reliableTopicConfig = fullClientConfig.getReliableTopicConfig("rel-topic");
        Assert.assertEquals(100, reliableTopicConfig.getReadBatchSize());
        Assert.assertEquals(DISCARD_NEWEST, reliableTopicConfig.getTopicOverloadPolicy());
    }

    @Test
    public void testCloudConfig() {
        ClientCloudConfig cloudConfig = fullClientConfig.getNetworkConfig().getCloudConfig();
        Assert.assertEquals(false, cloudConfig.isEnabled());
        Assert.assertEquals("EXAMPLE_TOKEN", cloudConfig.getDiscoveryToken());
    }

    @Test
    public void testCloudConfig_defaults() {
        ClientCloudConfig cloudConfig = defaultClientConfig.getNetworkConfig().getCloudConfig();
        Assert.assertEquals(false, cloudConfig.isEnabled());
        Assert.assertEquals(null, cloudConfig.getDiscoveryToken());
    }

    @Test
    public void testDiscoveryStrategyConfig() {
        DiscoveryConfig discoveryConfig = fullClientConfig.getNetworkConfig().getDiscoveryConfig();
        Assert.assertEquals("DummyFilterClass", discoveryConfig.getNodeFilterClass());
        Collection<DiscoveryStrategyConfig> discoveryStrategyConfigs = discoveryConfig.getDiscoveryStrategyConfigs();
        Assert.assertEquals(1, discoveryStrategyConfigs.size());
        DiscoveryStrategyConfig discoveryStrategyConfig = discoveryStrategyConfigs.iterator().next();
        Assert.assertEquals("DummyDiscoveryStrategy1", discoveryStrategyConfig.getClassName());
        Map<String, Comparable> properties = discoveryStrategyConfig.getProperties();
        Assert.assertEquals(3, properties.size());
        Assert.assertEquals("foo", properties.get("key-string"));
        Assert.assertEquals("123", properties.get("key-int"));
        Assert.assertEquals("true", properties.get("key-boolean"));
    }

    @Test
    public void testGroupConfig() {
        final GroupConfig groupConfig = fullClientConfig.getGroupConfig();
        Assert.assertEquals("dev", groupConfig.getName());
        Assert.assertEquals("dev-pass", groupConfig.getPassword());
    }

    @Test
    public void testProperties() {
        Assert.assertEquals(6, fullClientConfig.getProperties().size());
        Assert.assertEquals("60000", fullClientConfig.getProperty("hazelcast.client.heartbeat.timeout"));
    }

    @Test
    public void testLabels() {
        Set<String> labels = fullClientConfig.getLabels();
        Assert.assertEquals(2, labels.size());
        assertContains(labels, "admin");
        assertContains(labels, "foo");
    }

    @Test
    public void testInstanceName() {
        Assert.assertEquals("CLIENT_NAME", fullClientConfig.getInstanceName());
    }

    @Test
    public void testSecurityConfig() {
        ClientSecurityConfig securityConfig = fullClientConfig.getSecurityConfig();
        Assert.assertEquals("com.hazelcast.security.UsernamePasswordCredentials", securityConfig.getCredentialsClassname());
        CredentialsFactoryConfig credentialsFactoryConfig = securityConfig.getCredentialsFactoryConfig();
        Assert.assertEquals("com.hazelcast.examples.MyCredentialsFactory", credentialsFactoryConfig.getClassName());
        Properties properties = credentialsFactoryConfig.getProperties();
        Assert.assertEquals("value", properties.getProperty("property"));
    }
}

