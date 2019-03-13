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


import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import EvictionPolicy.NONE;
import EvictionPolicy.RANDOM;
import InMemoryFormat.OBJECT;
import ReconnectMode.ASYNC;
import TopicOverloadPolicy.DISCARD_NEWEST;
import com.hazelcast.client.LoadBalancer;
import com.hazelcast.client.config.ClientAwsConfig;
import com.hazelcast.client.config.ClientCloudConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.client.config.ClientFlakeIdGeneratorConfig;
import com.hazelcast.client.config.ClientIcmpPingConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.ClientReliableTopicConfig;
import com.hazelcast.client.config.ClientSecurityConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.client.config.ConnectionRetryConfig;
import com.hazelcast.client.config.ProxyFactoryConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.client.util.RoundRobinLB;
import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.MultiMap;
import com.hazelcast.security.Credentials;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "node-client-applicationContext-hazelcast.xml" })
@Category(QuickTest.class)
public class TestClientApplicationContext {
    @Resource(name = "client")
    private HazelcastClientProxy client;

    @Resource(name = "client2")
    private HazelcastClientProxy client2;

    @Resource(name = "client3")
    private HazelcastClientProxy client3;

    @Resource(name = "client4")
    private HazelcastClientProxy client4;

    @Resource(name = "client5")
    private HazelcastClientProxy client5;

    @Resource(name = "client6")
    private HazelcastClientProxy client6;

    @Resource(name = "client7-empty-serialization-config")
    private HazelcastClientProxy client7;

    @Resource(name = "client8")
    private HazelcastClientProxy client8;

    @Resource(name = "client9-user-code-deployment-test")
    private HazelcastClientProxy userCodeDeploymentTestClient;

    @Resource(name = "client10-flakeIdGenerator")
    private HazelcastClientProxy client10;

    @Resource(name = "client11-icmp-ping")
    private HazelcastClientProxy icmpPingTestClient;

    @Resource(name = "client12-hazelcast-cloud")
    private HazelcastClientProxy hazelcastCloudClient;

    @Resource(name = "client13-exponential-connection-retry")
    private HazelcastClientProxy connectionRetryClient;

    @Resource(name = "client14-reliable-topic")
    private HazelcastClientProxy hazelcastReliableTopic;

    @Resource(name = "client15-credentials-factory")
    private HazelcastClientProxy credentialsFactory;

    @Resource(name = "client16-name-and-labels")
    private HazelcastClientProxy namedClient;

    @Resource(name = "instance")
    private HazelcastInstance instance;

    @Resource(name = "map1")
    private IMap<Object, Object> map1;

    @Resource(name = "map2")
    private IMap<Object, Object> map2;

    @Resource(name = "multiMap")
    private MultiMap multiMap;

    @Resource(name = "queue")
    private IQueue queue;

    @Resource(name = "topic")
    private ITopic topic;

    @Resource(name = "set")
    private ISet set;

    @Resource(name = "list")
    private IList list;

    @Resource(name = "executorService")
    private ExecutorService executorService;

    @Resource(name = "idGenerator")
    private IdGenerator idGenerator;

    @Resource(name = "atomicLong")
    private IAtomicLong atomicLong;

    @Resource(name = "atomicReference")
    private IAtomicReference atomicReference;

    @Resource(name = "countDownLatch")
    private ICountDownLatch countDownLatch;

    @Resource(name = "semaphore")
    private ISemaphore semaphore;

    @Resource(name = "reliableTopic")
    private ITopic reliableTopic;

    @Autowired
    private Credentials credentials;

    @Test
    public void testClient() {
        Assert.assertNotNull(client);
        Assert.assertNotNull(client2);
        Assert.assertNotNull(client3);
        ClientConfig config = client.getClientConfig();
        Assert.assertEquals("13", config.getProperty("hazelcast.client.retry.count"));
        Assert.assertEquals(3, config.getNetworkConfig().getConnectionAttemptLimit());
        Assert.assertEquals(1000, config.getNetworkConfig().getConnectionTimeout());
        Assert.assertEquals(3000, config.getNetworkConfig().getConnectionAttemptPeriod());
        ClientConfig config2 = client2.getClientConfig();
        Assert.assertEquals(credentials, config2.getSecurityConfig().getCredentials());
        client.getMap("default").put("Q", "q");
        client2.getMap("default").put("X", "x");
        IMap<Object, Object> map = instance.getMap("default");
        Assert.assertEquals("q", map.get("Q"));
        Assert.assertEquals("x", map.get("X"));
        ClientConfig config3 = client3.getClientConfig();
        SerializationConfig serConf = config3.getSerializationConfig();
        Assert.assertEquals(ByteOrder.BIG_ENDIAN, serConf.getByteOrder());
        Assert.assertFalse(serConf.isAllowUnsafe());
        Assert.assertFalse(serConf.isCheckClassDefErrors());
        Assert.assertFalse(serConf.isEnableCompression());
        Assert.assertFalse(serConf.isEnableSharedObject());
        Assert.assertFalse(serConf.isUseNativeByteOrder());
        Assert.assertEquals(10, serConf.getPortableVersion());
        Map<Integer, String> map1 = serConf.getDataSerializableFactoryClasses();
        Assert.assertNotNull(map1);
        Assert.assertTrue(map1.containsKey(1));
        Assert.assertEquals("com.hazelcast.spring.serialization.DummyDataSerializableFactory", map1.get(1));
        Map<Integer, String> portableFactoryClasses = serConf.getPortableFactoryClasses();
        Assert.assertNotNull(portableFactoryClasses);
        Assert.assertTrue(portableFactoryClasses.containsKey(2));
        Assert.assertEquals("com.hazelcast.spring.serialization.DummyPortableFactory", portableFactoryClasses.get(2));
        Collection<SerializerConfig> serializerConfigs = serConf.getSerializerConfigs();
        Assert.assertNotNull(serializerConfigs);
        SerializerConfig serializerConfig = serializerConfigs.iterator().next();
        Assert.assertNotNull(serializerConfig);
        Assert.assertEquals("com.hazelcast.nio.serialization.CustomSerializationTest$FooXmlSerializer", serializerConfig.getClassName());
        Assert.assertEquals("com.hazelcast.nio.serialization.CustomSerializationTest$Foo", serializerConfig.getTypeClassName());
        List<ProxyFactoryConfig> proxyFactoryConfigs = config3.getProxyFactoryConfigs();
        Assert.assertNotNull(proxyFactoryConfigs);
        ProxyFactoryConfig proxyFactoryConfig = proxyFactoryConfigs.get(0);
        Assert.assertNotNull(proxyFactoryConfig);
        Assert.assertEquals("com.hazelcast.spring.DummyProxyFactory", proxyFactoryConfig.getClassName());
        Assert.assertEquals("MyService", proxyFactoryConfig.getService());
        LoadBalancer loadBalancer = config3.getLoadBalancer();
        Assert.assertNotNull(loadBalancer);
        Assert.assertTrue((loadBalancer instanceof RoundRobinLB));
        NearCacheConfig nearCacheConfig = config3.getNearCacheConfig("default");
        Assert.assertNotNull(nearCacheConfig);
        Assert.assertEquals(1, nearCacheConfig.getTimeToLiveSeconds());
        Assert.assertEquals(70, nearCacheConfig.getMaxIdleSeconds());
        Assert.assertEquals(LRU, nearCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(4000, nearCacheConfig.getEvictionConfig().getSize());
        Assert.assertTrue(nearCacheConfig.isInvalidateOnChange());
        Assert.assertFalse(nearCacheConfig.isSerializeKeys());
        Assert.assertEquals(CACHE_ON_UPDATE, nearCacheConfig.getLocalUpdatePolicy());
    }

    @Test
    public void testAwsClientConfig() {
        Assert.assertNotNull(client4);
        ClientConfig config = client4.getClientConfig();
        ClientNetworkConfig networkConfig = config.getNetworkConfig();
        ClientAwsConfig awsConfig = networkConfig.getAwsConfig();
        Assert.assertFalse(awsConfig.isEnabled());
        Assert.assertTrue(awsConfig.isInsideAws());
        Assert.assertEquals("sample-access-key", awsConfig.getAccessKey());
        Assert.assertEquals("sample-secret-key", awsConfig.getSecretKey());
        Assert.assertEquals("sample-region", awsConfig.getRegion());
        Assert.assertEquals("sample-group", awsConfig.getSecurityGroupName());
        Assert.assertEquals("sample-tag-key", awsConfig.getTagKey());
        Assert.assertEquals("sample-tag-value", awsConfig.getTagValue());
    }

    @Test
    public void testUnlimitedConnectionAttempt() {
        Assert.assertNotNull(client5);
        ClientConfig config = client5.getClientConfig();
        Assert.assertEquals(0, config.getNetworkConfig().getConnectionAttemptLimit());
    }

    @Test
    public void testHazelcastInstances() {
        Assert.assertNotNull(map1);
        Assert.assertNotNull(map2);
        Assert.assertNotNull(multiMap);
        Assert.assertNotNull(queue);
        Assert.assertNotNull(topic);
        Assert.assertNotNull(set);
        Assert.assertNotNull(list);
        Assert.assertNotNull(executorService);
        Assert.assertNotNull(idGenerator);
        Assert.assertNotNull(atomicLong);
        Assert.assertNotNull(atomicReference);
        Assert.assertNotNull(countDownLatch);
        Assert.assertNotNull(semaphore);
        Assert.assertNotNull(reliableTopic);
        Assert.assertEquals("map1", map1.getName());
        Assert.assertEquals("map2", map2.getName());
        Assert.assertEquals("multiMap", multiMap.getName());
        Assert.assertEquals("queue", queue.getName());
        Assert.assertEquals("topic", topic.getName());
        Assert.assertEquals("set", set.getName());
        Assert.assertEquals("list", list.getName());
        Assert.assertEquals("idGenerator", idGenerator.getName());
        Assert.assertEquals("atomicLong", atomicLong.getName());
        Assert.assertEquals("atomicReference", atomicReference.getName());
        Assert.assertEquals("countDownLatch", countDownLatch.getName());
        Assert.assertEquals("semaphore", semaphore.getName());
        Assert.assertEquals("reliableTopic", reliableTopic.getName());
    }

    @Test
    public void testDefaultSerializationConfig() {
        ClientConfig config7 = client7.getClientConfig();
        SerializationConfig serConf = config7.getSerializationConfig();
        Assert.assertEquals(ByteOrder.BIG_ENDIAN, serConf.getByteOrder());
        Assert.assertFalse(serConf.isAllowUnsafe());
        Assert.assertTrue(serConf.isCheckClassDefErrors());
        Assert.assertFalse(serConf.isEnableCompression());
        Assert.assertTrue(serConf.isEnableSharedObject());
        Assert.assertFalse(serConf.isUseNativeByteOrder());
        Assert.assertEquals(0, serConf.getPortableVersion());
    }

    @Test
    public void testClientNearCacheEvictionPolicies() {
        ClientConfig config = client3.getClientConfig();
        Assert.assertEquals(LFU, getNearCacheEvictionPolicy("lfuNearCacheEviction", config));
        Assert.assertEquals(LRU, getNearCacheEvictionPolicy("lruNearCacheEviction", config));
        Assert.assertEquals(NONE, getNearCacheEvictionPolicy("noneNearCacheEviction", config));
        Assert.assertEquals(RANDOM, getNearCacheEvictionPolicy("randomNearCacheEviction", config));
    }

    @Test
    public void testNearCachePreloader() {
        NearCachePreloaderConfig preloaderConfig = client3.getClientConfig().getNearCacheConfig("preloader").getPreloaderConfig();
        Assert.assertTrue(preloaderConfig.isEnabled());
        Assert.assertEquals("/tmp/preloader", preloaderConfig.getDirectory());
        Assert.assertEquals(23, preloaderConfig.getStoreInitialDelaySeconds());
        Assert.assertEquals(42, preloaderConfig.getStoreIntervalSeconds());
    }

    @Test
    public void testUserCodeDeploymentConfig() {
        ClientConfig config = userCodeDeploymentTestClient.getClientConfig();
        ClientUserCodeDeploymentConfig userCodeDeploymentConfig = config.getUserCodeDeploymentConfig();
        List<String> classNames = userCodeDeploymentConfig.getClassNames();
        Assert.assertFalse(userCodeDeploymentConfig.isEnabled());
        Assert.assertEquals(2, classNames.size());
        Assert.assertTrue(classNames.contains("SampleClassName1"));
        Assert.assertTrue(classNames.contains("SampleClassName2"));
        List<String> jarPaths = userCodeDeploymentConfig.getJarPaths();
        Assert.assertEquals(1, jarPaths.size());
        Assert.assertTrue(jarPaths.contains("/User/jar/path/test.jar"));
    }

    @Test
    public void testFullQueryCacheConfig() throws Exception {
        ClientConfig config = client6.getClientConfig();
        QueryCacheConfig queryCacheConfig = TestClientApplicationContext.getQueryCacheConfig(config);
        EntryListenerConfig entryListenerConfig = queryCacheConfig.getEntryListenerConfigs().get(0);
        Assert.assertTrue(entryListenerConfig.isIncludeValue());
        Assert.assertFalse(entryListenerConfig.isLocal());
        Assert.assertEquals("com.hazelcast.spring.DummyEntryListener", entryListenerConfig.getClassName());
        Assert.assertFalse(queryCacheConfig.isIncludeValue());
        Assert.assertEquals("my-query-cache-1", queryCacheConfig.getName());
        Assert.assertEquals(12, queryCacheConfig.getBatchSize());
        Assert.assertEquals(33, queryCacheConfig.getBufferSize());
        Assert.assertEquals(12, queryCacheConfig.getDelaySeconds());
        Assert.assertEquals(OBJECT, queryCacheConfig.getInMemoryFormat());
        Assert.assertTrue(queryCacheConfig.isCoalesce());
        Assert.assertFalse(queryCacheConfig.isPopulate());
        Assert.assertEquals("__key > 12", queryCacheConfig.getPredicateConfig().getSql());
        Assert.assertEquals(LRU, queryCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(ENTRY_COUNT, queryCacheConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(111, queryCacheConfig.getEvictionConfig().getSize());
    }

    @Test
    public void testClientConnectionStrategyConfig() {
        ClientConnectionStrategyConfig connectionStrategyConfig = client8.getClientConfig().getConnectionStrategyConfig();
        Assert.assertTrue(connectionStrategyConfig.isAsyncStart());
        Assert.assertEquals(ASYNC, connectionStrategyConfig.getReconnectMode());
    }

    @Test
    public void testFlakeIdGeneratorConfig() {
        Map<String, ClientFlakeIdGeneratorConfig> configMap = client10.getClientConfig().getFlakeIdGeneratorConfigMap();
        Assert.assertEquals(1, configMap.size());
        ClientFlakeIdGeneratorConfig config = configMap.values().iterator().next();
        Assert.assertEquals("gen1", config.getName());
        Assert.assertEquals(3, config.getPrefetchCount());
        Assert.assertEquals(3000L, config.getPrefetchValidityMillis());
    }

    @Test
    public void testCredentialsFactory() {
        ClientSecurityConfig securityConfig = credentialsFactory.getClientConfig().getSecurityConfig();
        CredentialsFactoryConfig credentialsFactoryConfig = securityConfig.getCredentialsFactoryConfig();
        Assert.assertEquals("com.hazelcast.examples.MyCredentialsFactory", credentialsFactoryConfig.getClassName());
        Assert.assertEquals("value", credentialsFactoryConfig.getProperties().getProperty("property"));
        Assert.assertNotNull(credentialsFactoryConfig.getImplementation());
    }

    @Test
    public void testClientIcmpConfig() {
        ClientIcmpPingConfig icmpPingConfig = icmpPingTestClient.getClientConfig().getNetworkConfig().getClientIcmpPingConfig();
        Assert.assertEquals(false, icmpPingConfig.isEnabled());
        Assert.assertEquals(2000, icmpPingConfig.getTimeoutMilliseconds());
        Assert.assertEquals(3000, icmpPingConfig.getIntervalMilliseconds());
        Assert.assertEquals(50, icmpPingConfig.getTtl());
        Assert.assertEquals(5, icmpPingConfig.getMaxAttempts());
        Assert.assertEquals(false, icmpPingConfig.isEchoFailFastOnStartup());
    }

    @Test
    public void testCloudConfig() {
        ClientCloudConfig cloudConfig = hazelcastCloudClient.getClientConfig().getNetworkConfig().getCloudConfig();
        Assert.assertEquals(false, cloudConfig.isEnabled());
        Assert.assertEquals("EXAMPLE_TOKEN", cloudConfig.getDiscoveryToken());
    }

    @Test
    public void testConnectionRetry() {
        ConnectionRetryConfig connectionRetryConfig = connectionRetryClient.getClientConfig().getConnectionStrategyConfig().getConnectionRetryConfig();
        Assert.assertTrue(connectionRetryConfig.isEnabled());
        Assert.assertTrue(connectionRetryConfig.isFailOnMaxBackoff());
        Assert.assertEquals(0.5, connectionRetryConfig.getJitter(), 0);
        Assert.assertEquals(2000, connectionRetryConfig.getInitialBackoffMillis());
        Assert.assertEquals(60000, connectionRetryConfig.getMaxBackoffMillis());
        Assert.assertEquals(3, connectionRetryConfig.getMultiplier(), 0);
    }

    @Test
    public void testReliableTopicConfig() {
        ClientConfig clientConfig = hazelcastReliableTopic.getClientConfig();
        ClientReliableTopicConfig topicConfig = clientConfig.getReliableTopicConfig("rel-topic");
        Assert.assertEquals(100, topicConfig.getReadBatchSize());
        Assert.assertEquals(DISCARD_NEWEST, topicConfig.getTopicOverloadPolicy());
    }

    @Test
    public void testInstanceNameConfig() {
        Assert.assertEquals("clientName", namedClient.getName());
    }

    @Test
    public void testLabelsConfig() {
        Set<String> labels = namedClient.getClientConfig().getLabels();
        Assert.assertEquals(1, labels.size());
        assertContains(labels, "foo");
    }
}

