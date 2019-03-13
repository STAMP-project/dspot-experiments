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


import ConsistencyCheckStrategy.MERKLE_TREES;
import HotRestartClusterDataRecoveryPolicy.FULL_RECOVERY_ONLY;
import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import LoginModuleConfig.LoginModuleUsage.OPTIONAL;
import LoginModuleConfig.LoginModuleUsage.REQUIRED;
import LoginModuleConfig.LoginModuleUsage.SUFFICIENT;
import MapStoreConfig.InitialLoadMode.EAGER;
import MultiMapConfig.ValueCollectionType.LIST;
import NativeMemoryConfig.MemoryAllocatorType.STANDARD;
import NearCacheConfig.LocalUpdatePolicy.INVALIDATE;
import OnJoinPermissionOperationName.NONE;
import PartitionGroupConfig.MemberGroupType.PER_MEMBER;
import PermissionConfig.PermissionType.ATOMIC_LONG;
import QuorumType.READ_WRITE;
import RestEndpointGroup.CLUSTER_READ;
import RestEndpointGroup.CLUSTER_WRITE;
import RestEndpointGroup.DATA;
import RestEndpointGroup.HEALTH_CHECK;
import RestEndpointGroup.HOT_RESTART;
import RestEndpointGroup.WAN;
import TopicOverloadPolicy.BLOCK;
import WanPublisherState.STOPPED;
import com.google.common.collect.Sets;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.spi.merge.HigherHitsMergePolicy;
import com.hazelcast.spi.merge.LatestUpdateMergePolicy;
import com.hazelcast.spi.merge.PassThroughMergePolicy;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static ExpiryPolicyType.ACCESSED;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigXmlGeneratorTest {
    @Test
    public void testIfSensitiveDataIsMasked_whenMaskingEnabled() {
        Config cfg = new Config();
        SSLConfig sslConfig = new SSLConfig();
        sslConfig.setProperty("keyStorePassword", "Hazelcast").setProperty("trustStorePassword", "Hazelcast");
        cfg.getNetworkConfig().setSSLConfig(sslConfig);
        SymmetricEncryptionConfig symmetricEncryptionConfig = new SymmetricEncryptionConfig();
        symmetricEncryptionConfig.setPassword("Hazelcast");
        symmetricEncryptionConfig.setSalt("theSalt");
        cfg.getNetworkConfig().setSymmetricEncryptionConfig(symmetricEncryptionConfig);
        cfg.setLicenseKey("HazelcastLicenseKey");
        Config newConfigViaXMLGenerator = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg);
        SSLConfig generatedSSLConfig = newConfigViaXMLGenerator.getNetworkConfig().getSSLConfig();
        Assert.assertEquals(generatedSSLConfig.getProperty("keyStorePassword"), ConfigXmlGenerator.MASK_FOR_SENSITIVE_DATA);
        Assert.assertEquals(generatedSSLConfig.getProperty("trustStorePassword"), ConfigXmlGenerator.MASK_FOR_SENSITIVE_DATA);
        String secPassword = newConfigViaXMLGenerator.getNetworkConfig().getSymmetricEncryptionConfig().getPassword();
        String theSalt = newConfigViaXMLGenerator.getNetworkConfig().getSymmetricEncryptionConfig().getSalt();
        Assert.assertEquals(secPassword, ConfigXmlGenerator.MASK_FOR_SENSITIVE_DATA);
        Assert.assertEquals(theSalt, ConfigXmlGenerator.MASK_FOR_SENSITIVE_DATA);
        Assert.assertEquals(newConfigViaXMLGenerator.getLicenseKey(), ConfigXmlGenerator.MASK_FOR_SENSITIVE_DATA);
        Assert.assertEquals(newConfigViaXMLGenerator.getGroupConfig().getPassword(), ConfigXmlGenerator.MASK_FOR_SENSITIVE_DATA);
    }

    @Test
    public void testIfSensitiveDataIsNotMasked_whenMaskingDisabled() {
        String password = "Hazelcast";
        String salt = "theSalt";
        String licenseKey = "HazelcastLicenseKey";
        Config cfg = new Config();
        cfg.getGroupConfig().setPassword(password);
        SSLConfig sslConfig = new SSLConfig();
        sslConfig.setProperty("keyStorePassword", password).setProperty("trustStorePassword", password);
        cfg.getNetworkConfig().setSSLConfig(sslConfig);
        SymmetricEncryptionConfig symmetricEncryptionConfig = new SymmetricEncryptionConfig();
        symmetricEncryptionConfig.setPassword(password);
        symmetricEncryptionConfig.setSalt(salt);
        cfg.getNetworkConfig().setSymmetricEncryptionConfig(symmetricEncryptionConfig);
        cfg.setLicenseKey(licenseKey);
        Config newConfigViaXMLGenerator = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg, false);
        SSLConfig generatedSSLConfig = newConfigViaXMLGenerator.getNetworkConfig().getSSLConfig();
        Assert.assertEquals(generatedSSLConfig.getProperty("keyStorePassword"), password);
        Assert.assertEquals(generatedSSLConfig.getProperty("trustStorePassword"), password);
        String secPassword = newConfigViaXMLGenerator.getNetworkConfig().getSymmetricEncryptionConfig().getPassword();
        String theSalt = newConfigViaXMLGenerator.getNetworkConfig().getSymmetricEncryptionConfig().getSalt();
        Assert.assertEquals(secPassword, password);
        Assert.assertEquals(theSalt, salt);
        Assert.assertEquals(newConfigViaXMLGenerator.getLicenseKey(), licenseKey);
        Assert.assertEquals(newConfigViaXMLGenerator.getGroupConfig().getPassword(), password);
    }

    @Test
    public void testMemberAddressProvider() {
        Config cfg = new Config();
        MemberAddressProviderConfig expected = cfg.getNetworkConfig().getMemberAddressProviderConfig();
        expected.setEnabled(true).setEnabled(true).setClassName("ClassName");
        Properties props = expected.getProperties();
        props.setProperty("p1", "v1");
        props.setProperty("p2", "v2");
        props.setProperty("p3", "v3");
        Config newConfigViaXMLGenerator = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg);
        MemberAddressProviderConfig actual = newConfigViaXMLGenerator.getNetworkConfig().getMemberAddressProviderConfig();
        Assert.assertEquals(expected.isEnabled(), actual.isEnabled());
        Assert.assertEquals(expected.getClassName(), actual.getClassName());
        Assert.assertEquals(expected.getProperties(), actual.getProperties());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFailureDetectorConfigGenerator() {
        Config cfg = new Config();
        IcmpFailureDetectorConfig expected = new IcmpFailureDetectorConfig();
        // Defaults to false
        expected.setEnabled(true).setIntervalMilliseconds(1001).setTimeoutMilliseconds(1002).setMaxAttempts(4).setTtl(300).setParallelMode(false).setFailFastOnStartup(false);// Defaults to false

        cfg.getNetworkConfig().setIcmpFailureDetectorConfig(expected);
        Config newConfigViaXMLGenerator = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg);
        IcmpFailureDetectorConfig actual = newConfigViaXMLGenerator.getNetworkConfig().getIcmpFailureDetectorConfig();
        ConfigXmlGeneratorTest.assertFailureDetectorConfigEquals(expected, actual);
    }

    @Test
    public void testNetworkMulticastJoinConfig() {
        Config cfg = new Config();
        MulticastConfig expectedConfig = ConfigXmlGeneratorTest.multicastConfig();
        cfg.getNetworkConfig().getJoin().setMulticastConfig(expectedConfig);
        MulticastConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getNetworkConfig().getJoin().getMulticastConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testNetworkTcpJoinConfig() {
        Config cfg = new Config();
        TcpIpConfig expectedConfig = ConfigXmlGeneratorTest.tcpIpConfig();
        cfg.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        cfg.getNetworkConfig().getJoin().setTcpIpConfig(expectedConfig);
        TcpIpConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getNetworkConfig().getJoin().getTcpIpConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testNetworkConfigOutboundPorts() {
        Config cfg = new Config();
        NetworkConfig expectedNetworkConfig = cfg.getNetworkConfig();
        expectedNetworkConfig.addOutboundPortDefinition("4242-4244").addOutboundPortDefinition("5252;5254");
        NetworkConfig actualNetworkConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getNetworkConfig();
        Assert.assertEquals(expectedNetworkConfig.getOutboundPortDefinitions(), actualNetworkConfig.getOutboundPortDefinitions());
        Assert.assertEquals(expectedNetworkConfig.getOutboundPorts(), actualNetworkConfig.getOutboundPorts());
    }

    @Test
    public void testNetworkConfigInterfaces() {
        Config cfg = new Config();
        NetworkConfig expectedNetworkConfig = cfg.getNetworkConfig();
        expectedNetworkConfig.getInterfaces().addInterface("127.0.0.*").setEnabled(true);
        NetworkConfig actualNetworkConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getNetworkConfig();
        Assert.assertEquals(expectedNetworkConfig.getInterfaces(), actualNetworkConfig.getInterfaces());
    }

    @Test
    public void testNetworkConfigSocketInterceptor() {
        Config cfg = new Config();
        SocketInterceptorConfig expectedConfig = new SocketInterceptorConfig().setEnabled(true).setClassName("socketInterceptor").setProperty("key", "value");
        cfg.getNetworkConfig().setSocketInterceptorConfig(expectedConfig);
        SocketInterceptorConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getNetworkConfig().getSocketInterceptorConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testListenerConfig() {
        Config expectedConfig = new Config();
        expectedConfig.setListenerConfigs(Collections.singletonList(new ListenerConfig("Listener")));
        Config actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(expectedConfig);
        Assert.assertEquals(expectedConfig.getListenerConfigs(), actualConfig.getListenerConfigs());
    }

    @Test
    public void testHotRestartPersistenceConfig() {
        Config cfg = new Config();
        HotRestartPersistenceConfig expectedConfig = cfg.getHotRestartPersistenceConfig();
        expectedConfig.setEnabled(true).setClusterDataRecoveryPolicy(FULL_RECOVERY_ONLY).setValidationTimeoutSeconds(100).setDataLoadTimeoutSeconds(130).setBaseDir(new File("nonExisting-base").getAbsoluteFile()).setBackupDir(new File("nonExisting-backup").getAbsoluteFile()).setParallelism(5).setAutoRemoveStaleData(false);
        HotRestartPersistenceConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getHotRestartPersistenceConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testServicesConfig() {
        Config cfg = new Config();
        Properties properties = new Properties();
        properties.setProperty("key", "value");
        ServiceConfig serviceConfig = new ServiceConfig().setName("ServiceConfig").setEnabled(true).setClassName("ServiceClass").setProperties(properties);
        ServicesConfig expectedConfig = cfg.getServicesConfig().setEnableDefaults(true).setServiceConfigs(Collections.singletonList(serviceConfig));
        ServicesConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getServicesConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testSecurityConfig() {
        Config cfg = new Config();
        Properties dummyprops = new Properties();
        dummyprops.put("a", "b");
        SecurityConfig expectedConfig = new SecurityConfig();
        expectedConfig.setEnabled(true).setOnJoinPermissionOperation(NONE).setClientBlockUnmappedActions(false).setClientLoginModuleConfigs(Arrays.asList(new LoginModuleConfig().setClassName("f.o.o").setUsage(OPTIONAL), new LoginModuleConfig().setClassName("b.a.r").setUsage(SUFFICIENT), new LoginModuleConfig().setClassName("l.o.l").setUsage(REQUIRED))).setMemberLoginModuleConfigs(Arrays.asList(new LoginModuleConfig().setClassName("member.f.o.o").setUsage(OPTIONAL), new LoginModuleConfig().setClassName("member.b.a.r").setUsage(SUFFICIENT), new LoginModuleConfig().setClassName("member.l.o.l").setUsage(REQUIRED))).setMemberCredentialsConfig(new CredentialsFactoryConfig().setClassName("foo.bar").setProperties(dummyprops)).setClientPermissionConfigs(new HashSet<PermissionConfig>(Collections.singletonList(new PermissionConfig().setActions(Sets.newHashSet("read", "remove")).setEndpoints(Sets.newHashSet("127.0.0.1", "127.0.0.2")).setType(ATOMIC_LONG).setName("mycounter").setPrincipal("devos"))));
        cfg.setSecurityConfig(expectedConfig);
        SecurityConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getSecurityConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testSerializationConfig() {
        Config cfg = new Config();
        GlobalSerializerConfig globalSerializerConfig = new GlobalSerializerConfig().setClassName("GlobalSerializer").setOverrideJavaSerialization(true);
        SerializerConfig serializerConfig = new SerializerConfig().setClassName("SerializerClass").setTypeClassName("TypeClass");
        JavaSerializationFilterConfig filterConfig = new JavaSerializationFilterConfig();
        filterConfig.getBlacklist().addClasses("example.Class1", "acme.Test").addPackages("org.infinitban").addPrefixes("dangerous.", "bang");
        filterConfig.getWhitelist().addClasses("WhiteOne", "WhiteTwo").addPackages("com.hazelcast", "test.package").addPrefixes("java");
        SerializationConfig expectedConfig = new SerializationConfig().setAllowUnsafe(true).setPortableVersion(2).setByteOrder(ByteOrder.BIG_ENDIAN).setUseNativeByteOrder(true).setCheckClassDefErrors(true).setEnableCompression(true).setEnableSharedObject(true).setGlobalSerializerConfig(globalSerializerConfig).setJavaSerializationFilterConfig(filterConfig).addDataSerializableFactoryClass(10, "SerializableFactory").addPortableFactoryClass(10, "PortableFactory").addSerializerConfig(serializerConfig);
        cfg.setSerializationConfig(expectedConfig);
        SerializationConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getSerializationConfig();
        Assert.assertEquals(expectedConfig.isAllowUnsafe(), actualConfig.isAllowUnsafe());
        Assert.assertEquals(expectedConfig.getPortableVersion(), actualConfig.getPortableVersion());
        Assert.assertEquals(expectedConfig.getByteOrder(), actualConfig.getByteOrder());
        Assert.assertEquals(expectedConfig.isUseNativeByteOrder(), actualConfig.isUseNativeByteOrder());
        Assert.assertEquals(expectedConfig.isCheckClassDefErrors(), actualConfig.isCheckClassDefErrors());
        Assert.assertEquals(expectedConfig.isEnableCompression(), actualConfig.isEnableCompression());
        Assert.assertEquals(expectedConfig.isEnableSharedObject(), actualConfig.isEnableSharedObject());
        Assert.assertEquals(expectedConfig.getGlobalSerializerConfig(), actualConfig.getGlobalSerializerConfig());
        Assert.assertEquals(expectedConfig.getDataSerializableFactoryClasses(), actualConfig.getDataSerializableFactoryClasses());
        Assert.assertEquals(expectedConfig.getPortableFactoryClasses(), actualConfig.getPortableFactoryClasses());
        Assert.assertEquals(expectedConfig.getSerializerConfigs(), actualConfig.getSerializerConfigs());
        Assert.assertEquals(expectedConfig.getJavaSerializationFilterConfig(), actualConfig.getJavaSerializationFilterConfig());
    }

    @Test
    public void testPartitionGroupConfig() {
        Config cfg = new Config();
        PartitionGroupConfig expectedConfig = new PartitionGroupConfig().setEnabled(true).setGroupType(PER_MEMBER).setMemberGroupConfigs(Collections.singletonList(new MemberGroupConfig().addInterface("hostname")));
        cfg.setPartitionGroupConfig(expectedConfig);
        PartitionGroupConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getPartitionGroupConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testManagementCenterConfigGenerator() {
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig().setEnabled(true).setScriptingEnabled(false).setUpdateInterval(8).setUrl("http://foomybar.ber").setMutualAuthConfig(new MCMutualAuthConfig().setEnabled(true).setProperty("keyStore", "/tmp/foo_keystore").setProperty("keyStorePassword", "myp@ss1").setProperty("trustStore", "/tmp/foo_truststore").setProperty("trustStorePassword", "myp@ss2"));
        Config config = new Config().setManagementCenterConfig(managementCenterConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        ManagementCenterConfig xmlManCenterConfig = xmlConfig.getManagementCenterConfig();
        Assert.assertEquals(managementCenterConfig.isEnabled(), xmlManCenterConfig.isEnabled());
        Assert.assertEquals(managementCenterConfig.isScriptingEnabled(), xmlManCenterConfig.isScriptingEnabled());
        Assert.assertEquals(managementCenterConfig.getUpdateInterval(), xmlManCenterConfig.getUpdateInterval());
        Assert.assertEquals(managementCenterConfig.getUrl(), xmlManCenterConfig.getUrl());
        Assert.assertEquals(managementCenterConfig.getMutualAuthConfig().isEnabled(), xmlManCenterConfig.getMutualAuthConfig().isEnabled());
        Assert.assertEquals(managementCenterConfig.getMutualAuthConfig().getFactoryClassName(), xmlManCenterConfig.getMutualAuthConfig().getFactoryClassName());
        Assert.assertEquals(managementCenterConfig.getMutualAuthConfig().getProperty("keyStore"), xmlManCenterConfig.getMutualAuthConfig().getProperty("keyStore"));
        Assert.assertEquals(managementCenterConfig.getMutualAuthConfig().getProperty("trustStore"), xmlManCenterConfig.getMutualAuthConfig().getProperty("trustStore"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testReplicatedMapConfigGenerator() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy("PassThroughMergePolicy").setBatchSize(1234);
        ReplicatedMapConfig replicatedMapConfig = new ReplicatedMapConfig().setName("replicated-map-name").setStatisticsEnabled(false).setConcurrencyLevel(128).setQuorumName("quorum").setMergePolicyConfig(mergePolicyConfig).setInMemoryFormat(NATIVE).addEntryListenerConfig(new EntryListenerConfig("com.hazelcast.entrylistener", false, false));
        replicatedMapConfig.setAsyncFillup(true);
        Config config = new Config().addReplicatedMapConfig(replicatedMapConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        ReplicatedMapConfig xmlReplicatedMapConfig = xmlConfig.getReplicatedMapConfig("replicated-map-name");
        MergePolicyConfig actualMergePolicyConfig = xmlReplicatedMapConfig.getMergePolicyConfig();
        Assert.assertEquals("replicated-map-name", xmlReplicatedMapConfig.getName());
        Assert.assertFalse(xmlReplicatedMapConfig.isStatisticsEnabled());
        Assert.assertEquals(128, xmlReplicatedMapConfig.getConcurrencyLevel());
        Assert.assertEquals("com.hazelcast.entrylistener", xmlReplicatedMapConfig.getListenerConfigs().get(0).getClassName());
        Assert.assertEquals("quorum", xmlReplicatedMapConfig.getQuorumName());
        Assert.assertEquals(NATIVE, xmlReplicatedMapConfig.getInMemoryFormat());
        Assert.assertTrue(xmlReplicatedMapConfig.isAsyncFillup());
        Assert.assertEquals("PassThroughMergePolicy", actualMergePolicyConfig.getPolicy());
        Assert.assertEquals(1234, actualMergePolicyConfig.getBatchSize());
        Assert.assertEquals(replicatedMapConfig, xmlReplicatedMapConfig);
    }

    @Test
    public void testFlakeIdGeneratorConfigGenerator() {
        FlakeIdGeneratorConfig figConfig = new FlakeIdGeneratorConfig("flake-id-gen1").setPrefetchCount(3).setPrefetchValidityMillis(10L).setIdOffset(20L).setNodeIdOffset(30L).setStatisticsEnabled(false);
        Config config = new Config().addFlakeIdGeneratorConfig(figConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        FlakeIdGeneratorConfig xmlReplicatedConfig = xmlConfig.getFlakeIdGeneratorConfig("flake-id-gen1");
        Assert.assertEquals(figConfig, xmlReplicatedConfig);
    }

    @Test
    public void testCacheAttributes() {
        CacheSimpleConfig expectedConfig = new CacheSimpleConfig().setName("testCache").setEvictionConfig(ConfigXmlGeneratorTest.evictionConfig()).setInMemoryFormat(OBJECT).setBackupCount(2).setAsyncBackupCount(3).setCacheLoader("cacheLoader").setCacheWriter("cacheWriter").setExpiryPolicyFactoryConfig(new ExpiryPolicyFactoryConfig("expiryPolicyFactory")).setManagementEnabled(true).setStatisticsEnabled(true).setKeyType("keyType").setValueType("valueType").setReadThrough(true).setHotRestartConfig(ConfigXmlGeneratorTest.hotRestartConfig()).setCacheEntryListeners(Collections.singletonList(ConfigXmlGeneratorTest.cacheSimpleEntryListenerConfig())).setWriteThrough(true).setPartitionLostListenerConfigs(Collections.singletonList(new CachePartitionLostListenerConfig("partitionLostListener"))).setQuorumName("testQuorum");
        expectedConfig.setMergePolicy("mergePolicy");
        expectedConfig.setDisablePerEntryInvalidationEvents(true);
        expectedConfig.setWanReplicationRef(ConfigXmlGeneratorTest.wanReplicationRef());
        Config config = new Config().addCacheConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        CacheSimpleConfig actualConfig = xmlConfig.getCacheConfig("testCache");
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testCacheFactoryAttributes() {
        TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = new TimedExpiryPolicyFactoryConfig(ACCESSED, new DurationConfig(10, TimeUnit.SECONDS));
        CacheSimpleConfig expectedConfig = new CacheSimpleConfig().setName("testCache").setCacheLoaderFactory("cacheLoaderFactory").setCacheWriterFactory("cacheWriterFactory").setExpiryPolicyFactory("expiryPolicyFactory").setCacheEntryListeners(Collections.singletonList(ConfigXmlGeneratorTest.cacheSimpleEntryListenerConfig())).setExpiryPolicyFactoryConfig(new ExpiryPolicyFactoryConfig(timedExpiryPolicyFactoryConfig)).setPartitionLostListenerConfigs(Collections.singletonList(new CachePartitionLostListenerConfig("partitionLostListener")));
        expectedConfig.setMergePolicy("mergePolicy");
        expectedConfig.setDisablePerEntryInvalidationEvents(true);
        Config config = new Config().addCacheConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        CacheSimpleConfig actualConfig = xmlConfig.getCacheConfig("testCache");
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testCacheQuorumRef() {
        CacheSimpleConfig expectedConfig = new CacheSimpleConfig().setName("testCache").setQuorumName("testQuorum");
        Config config = new Config().addCacheConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        CacheSimpleConfig actualConfig = xmlConfig.getCacheConfig("testCache");
        Assert.assertEquals("testQuorum", actualConfig.getQuorumName());
    }

    @Test
    public void testRingbufferWithStoreClass() {
        RingbufferStoreConfig ringbufferStoreConfig = new RingbufferStoreConfig().setEnabled(true).setClassName("ClassName").setProperty("p1", "v1").setProperty("p2", "v2").setProperty("p3", "v3");
        testRingbuffer(ringbufferStoreConfig);
    }

    @Test
    public void testRingbufferWithStoreFactory() {
        RingbufferStoreConfig ringbufferStoreConfig = new RingbufferStoreConfig().setEnabled(true).setFactoryClassName("FactoryClassName").setProperty("p1", "v1").setProperty("p2", "v2").setProperty("p3", "v3");
        testRingbuffer(ringbufferStoreConfig);
    }

    @Test
    public void testSemaphore() {
        SemaphoreConfig expectedConfig = new SemaphoreConfig().setName("testSemaphore").setQuorumName("quorum").setInitialPermits(3).setBackupCount(1).setAsyncBackupCount(2);
        Config config = new Config().addSemaphoreConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        SemaphoreConfig actualConfig = xmlConfig.getSemaphoreConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testExecutor() {
        ExecutorConfig expectedConfig = new ExecutorConfig().setName("testExecutor").setStatisticsEnabled(true).setPoolSize(10).setQueueCapacity(100).setQuorumName("quorum");
        Config config = new Config().addExecutorConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        ExecutorConfig actualConfig = xmlConfig.getExecutorConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testDurableExecutor() {
        DurableExecutorConfig expectedConfig = new DurableExecutorConfig().setName("testDurableExecutor").setPoolSize(10).setCapacity(100).setDurability(2).setQuorumName("quorum");
        Config config = new Config().addDurableExecutorConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        DurableExecutorConfig actualConfig = xmlConfig.getDurableExecutorConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testPNCounter() {
        PNCounterConfig expectedConfig = new PNCounterConfig().setName("testPNCounter").setReplicaCount(100).setQuorumName("quorum");
        Config config = new Config().addPNCounterConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        PNCounterConfig actualConfig = xmlConfig.getPNCounterConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testMultiMap() {
        MultiMapConfig expectedConfig = new MultiMapConfig().setName("testMultiMap").setBackupCount(2).setAsyncBackupCount(3).setValueCollectionType(LIST).setBinary(true).setStatisticsEnabled(true).setQuorumName("quorum").setEntryListenerConfigs(Collections.singletonList(new EntryListenerConfig("java.Listener", true, true)));
        Config config = new Config().addMultiMapConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        MultiMapConfig actualConfig = xmlConfig.getMultiMapConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testAtomicLong() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(DiscardMergePolicy.class.getSimpleName()).setBatchSize(1234);
        AtomicLongConfig expectedConfig = new AtomicLongConfig("testAtomicLongConfig").setMergePolicyConfig(mergePolicyConfig).setQuorumName("quorum");
        Config config = new Config().addAtomicLongConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        AtomicLongConfig actualConfig = xmlConfig.getAtomicLongConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
        MergePolicyConfig xmlMergePolicyConfig = actualConfig.getMergePolicyConfig();
        Assert.assertEquals(DiscardMergePolicy.class.getSimpleName(), xmlMergePolicyConfig.getPolicy());
        Assert.assertEquals(1234, xmlMergePolicyConfig.getBatchSize());
    }

    @Test
    public void testAtomicReference() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(PassThroughMergePolicy.class.getSimpleName()).setBatchSize(4321);
        AtomicReferenceConfig expectedConfig = new AtomicReferenceConfig("testAtomicReferenceConfig").setMergePolicyConfig(mergePolicyConfig).setQuorumName("quorum");
        Config config = new Config().addAtomicReferenceConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        AtomicReferenceConfig actualConfig = xmlConfig.getAtomicReferenceConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
        MergePolicyConfig xmlMergePolicyConfig = actualConfig.getMergePolicyConfig();
        Assert.assertEquals(PassThroughMergePolicy.class.getSimpleName(), xmlMergePolicyConfig.getPolicy());
        Assert.assertEquals(4321, xmlMergePolicyConfig.getBatchSize());
    }

    @Test
    public void testCountDownLatch() {
        CountDownLatchConfig expectedConfig = new CountDownLatchConfig("testCountDownLatchConfig").setQuorumName("quorum");
        Config config = new Config().addCountDownLatchConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        CountDownLatchConfig actualConfig = xmlConfig.getCountDownLatchConfig(expectedConfig.getName());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testList() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(HigherHitsMergePolicy.class.getName()).setBatchSize(1234);
        ListConfig expectedConfig = new ListConfig("testList").setMaxSize(10).setStatisticsEnabled(true).setBackupCount(2).setAsyncBackupCount(3).setQuorumName("quorum").setMergePolicyConfig(mergePolicyConfig).setItemListenerConfigs(Collections.singletonList(new ItemListenerConfig("java.Listener", true)));
        Config config = new Config().addListConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        ListConfig actualConfig = xmlConfig.getListConfig("testList");
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testSet() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(LatestUpdateMergePolicy.class.getName()).setBatchSize(1234);
        SetConfig expectedConfig = new SetConfig("testSet").setMaxSize(10).setStatisticsEnabled(true).setBackupCount(2).setAsyncBackupCount(3).setQuorumName("quorum").setMergePolicyConfig(mergePolicyConfig).setItemListenerConfigs(Collections.singletonList(new ItemListenerConfig("java.Listener", true)));
        Config config = new Config().addSetConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        SetConfig actualConfig = xmlConfig.getSetConfig("testSet");
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testQueueWithStoreClass() {
        QueueStoreConfig queueStoreConfig = new QueueStoreConfig().setClassName("className").setEnabled(true).setProperty("key", "value");
        testQueue(queueStoreConfig);
    }

    @Test
    public void testQueueWithStoreFactory() {
        QueueStoreConfig queueStoreConfig = new QueueStoreConfig().setFactoryClassName("factoryClassName").setEnabled(true).setProperty("key", "value");
        testQueue(queueStoreConfig);
    }

    @Test
    public void testNativeMemory() {
        NativeMemoryConfig expectedConfig = new NativeMemoryConfig();
        expectedConfig.setEnabled(true);
        expectedConfig.setAllocatorType(STANDARD);
        expectedConfig.setMetadataSpacePercentage(12.5F);
        expectedConfig.setMinBlockSize(50);
        expectedConfig.setPageSize(100);
        expectedConfig.setSize(new com.hazelcast.memory.MemorySize(20, MemoryUnit.MEGABYTES));
        Config config = new Config().setNativeMemoryConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        NativeMemoryConfig actualConfig = xmlConfig.getNativeMemoryConfig();
        Assert.assertTrue(actualConfig.isEnabled());
        Assert.assertEquals(STANDARD, actualConfig.getAllocatorType());
        Assert.assertEquals(12.5, actualConfig.getMetadataSpacePercentage(), 1.0E-4);
        Assert.assertEquals(50, actualConfig.getMinBlockSize());
        Assert.assertEquals(100, actualConfig.getPageSize());
        Assert.assertEquals(getUnit(), getUnit());
        Assert.assertEquals(getValue(), getValue());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testMapAttributesConfigWithStoreClass() {
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setWriteDelaySeconds(10).setClassName("className").setWriteCoalescing(true).setWriteBatchSize(500).setProperty("key", "value");
        testMap(mapStoreConfig);
    }

    @Test
    public void testCRDTReplication() {
        final CRDTReplicationConfig replicationConfig = new CRDTReplicationConfig().setMaxConcurrentReplicationTargets(10).setReplicationPeriodMillis(2000);
        final Config config = new Config().setCRDTReplicationConfig(replicationConfig);
        final Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        final CRDTReplicationConfig xmlReplicationConfig = xmlConfig.getCRDTReplicationConfig();
        Assert.assertNotNull(xmlReplicationConfig);
        Assert.assertEquals(10, xmlReplicationConfig.getMaxConcurrentReplicationTargets());
        Assert.assertEquals(2000, xmlReplicationConfig.getReplicationPeriodMillis());
    }

    @Test
    public void testMapAttributesConfigWithStoreFactory() {
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setWriteDelaySeconds(10).setWriteCoalescing(true).setWriteBatchSize(500).setFactoryClassName("factoryClassName").setProperty("key", "value");
        testMap(mapStoreConfig);
    }

    @Test
    public void testMapNearCacheConfig() {
        NearCacheConfig expectedConfig = new NearCacheConfig().setName("nearCache").setInMemoryFormat(NATIVE).setMaxIdleSeconds(42).setCacheLocalEntries(true).setInvalidateOnChange(true).setLocalUpdatePolicy(INVALIDATE).setTimeToLiveSeconds(10).setEvictionConfig(ConfigXmlGeneratorTest.evictionConfig()).setSerializeKeys(true);
        MapConfig mapConfig = new MapConfig().setName("nearCacheTest").setNearCacheConfig(expectedConfig);
        Config config = new Config().addMapConfig(mapConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        NearCacheConfig actualConfig = xmlConfig.getMapConfig("nearCacheTest").getNearCacheConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testMapNearCacheEvictionConfig() {
        NearCacheConfig expectedConfig = new NearCacheConfig().setName("nearCache").setMaxSize(23).setEvictionPolicy("LRU");
        MapConfig mapConfig = new MapConfig().setName("nearCacheTest").setNearCacheConfig(expectedConfig);
        Config config = new Config().addMapConfig(mapConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        NearCacheConfig actualConfig = xmlConfig.getMapConfig("nearCacheTest").getNearCacheConfig();
        Assert.assertEquals(23, actualConfig.getMaxSize());
        Assert.assertEquals("LRU", actualConfig.getEvictionPolicy());
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testMultiMapConfig() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(DiscardMergePolicy.class.getSimpleName()).setBatchSize(2342);
        MultiMapConfig multiMapConfig = new MultiMapConfig().setName("myMultiMap").setBackupCount(2).setAsyncBackupCount(3).setBinary(false).setMergePolicyConfig(mergePolicyConfig);
        Config config = new Config().addMultiMapConfig(multiMapConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        Assert.assertEquals(multiMapConfig, xmlConfig.getMultiMapConfig("myMultiMap"));
    }

    @Test
    public void testWanConfig() {
        HashMap<String, Comparable> props = new HashMap<String, Comparable>();
        props.put("prop1", "val1");
        props.put("prop2", "val2");
        props.put("prop3", "val3");
        WanReplicationConfig wanConfig = new WanReplicationConfig().setName("testName").setWanConsumerConfig(new WanConsumerConfig().setClassName("dummyClass").setProperties(props));
        WanPublisherConfig publisherConfig = new WanPublisherConfig().setGroupName("dummyGroup").setPublisherId("dummyPublisherId").setClassName("dummyClass").setAwsConfig(getDummyAwsConfig()).setInitialPublisherState(STOPPED).setDiscoveryConfig(getDummyDiscoveryConfig());
        publisherConfig.getWanSyncConfig().setConsistencyCheckStrategy(MERKLE_TREES);
        WanConsumerConfig wanConsumerConfig = new WanConsumerConfig().setClassName("dummyClass").setProperties(props).setPersistWanReplicatedData(false);
        wanConfig.setWanConsumerConfig(wanConsumerConfig).setWanPublisherConfigs(Collections.singletonList(publisherConfig));
        Config config = new Config().addWanReplicationConfig(wanConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        ConfigCompatibilityChecker.checkWanConfigs(config.getWanReplicationConfigs(), xmlConfig.getWanReplicationConfigs());
    }

    @Test
    public void testMapMerkleTree() {
        String mapName = "mapName";
        MerkleTreeConfig expectedConfig = new MerkleTreeConfig().setMapName(mapName).setEnabled(true).setDepth(10);
        Config config = new Config().addMerkleTreeConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        MerkleTreeConfig actualConfig = xmlConfig.getMapMerkleTreeConfig(mapName);
        Assert.assertTrue(new ConfigCompatibilityChecker.MapMerkleTreeConfigChecker().check(expectedConfig, actualConfig));
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testMapEventJournal() {
        String mapName = "mapName";
        EventJournalConfig expectedConfig = new EventJournalConfig().setMapName(mapName).setEnabled(true).setCapacity(123).setTimeToLiveSeconds(321);
        Config config = new Config().addEventJournalConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        EventJournalConfig actualConfig = xmlConfig.getMapEventJournalConfig(mapName);
        Assert.assertTrue(new ConfigCompatibilityChecker.EventJournalConfigChecker().check(expectedConfig, actualConfig));
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testCacheEventJournal() {
        String cacheName = "cacheName";
        EventJournalConfig expectedConfig = new EventJournalConfig().setCacheName(cacheName).setEnabled(true).setCapacity(123).setTimeToLiveSeconds(321);
        Config config = new Config().addEventJournalConfig(expectedConfig);
        Config xmlConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config);
        EventJournalConfig actualConfig = xmlConfig.getCacheEventJournalConfig(cacheName);
        Assert.assertTrue(new ConfigCompatibilityChecker.EventJournalConfigChecker().check(expectedConfig, actualConfig));
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testCardinalityEstimator() {
        Config cfg = new Config();
        CardinalityEstimatorConfig estimatorConfig = new CardinalityEstimatorConfig().setBackupCount(2).setAsyncBackupCount(3).setName("Existing").setQuorumName("quorum").setMergePolicyConfig(new MergePolicyConfig("DiscardMergePolicy", 14));
        cfg.addCardinalityEstimatorConfig(estimatorConfig);
        CardinalityEstimatorConfig defaultCardinalityEstConfig = new CardinalityEstimatorConfig();
        cfg.addCardinalityEstimatorConfig(defaultCardinalityEstConfig);
        CardinalityEstimatorConfig existing = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getCardinalityEstimatorConfig("Existing");
        Assert.assertEquals(estimatorConfig, existing);
        CardinalityEstimatorConfig fallsbackToDefault = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getCardinalityEstimatorConfig("NotExisting/Default");
        Assert.assertEquals(defaultCardinalityEstConfig.getMergePolicyConfig(), fallsbackToDefault.getMergePolicyConfig());
        Assert.assertEquals(defaultCardinalityEstConfig.getBackupCount(), fallsbackToDefault.getBackupCount());
        Assert.assertEquals(defaultCardinalityEstConfig.getAsyncBackupCount(), fallsbackToDefault.getAsyncBackupCount());
        Assert.assertEquals(defaultCardinalityEstConfig.getQuorumName(), fallsbackToDefault.getQuorumName());
    }

    @Test
    public void testTopicGlobalOrdered() {
        Config cfg = new Config();
        TopicConfig expectedConfig = new TopicConfig().setName("TestTopic").setGlobalOrderingEnabled(true).setStatisticsEnabled(true).setMessageListenerConfigs(Collections.singletonList(new ListenerConfig("foo.bar.Listener")));
        cfg.addTopicConfig(expectedConfig);
        TopicConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getTopicConfig("TestTopic");
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testTopicMultiThreaded() {
        String testTopic = "TestTopic";
        Config cfg = new Config();
        TopicConfig expectedConfig = new TopicConfig().setName(testTopic).setMultiThreadingEnabled(true).setStatisticsEnabled(true).setMessageListenerConfigs(Collections.singletonList(new ListenerConfig("foo.bar.Listener")));
        cfg.addTopicConfig(expectedConfig);
        TopicConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getTopicConfig(testTopic);
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testReliableTopic() {
        String testTopic = "TestTopic";
        Config cfg = new Config();
        ReliableTopicConfig expectedConfig = new ReliableTopicConfig().setName(testTopic).setReadBatchSize(10).setTopicOverloadPolicy(BLOCK).setStatisticsEnabled(true).setMessageListenerConfigs(Collections.singletonList(new ListenerConfig("foo.bar.Listener")));
        cfg.addReliableTopicConfig(expectedConfig);
        ReliableTopicConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getReliableTopicConfig(testTopic);
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testLock() {
        String testLock = "TestLock";
        Config cfg = new Config();
        LockConfig expectedConfig = new LockConfig().setName(testLock).setQuorumName("quorum");
        cfg.addLockConfig(expectedConfig);
        LockConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getLockConfig(testLock);
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testScheduledExecutor() {
        Config cfg = new Config();
        ScheduledExecutorConfig scheduledExecutorConfig = new ScheduledExecutorConfig().setCapacity(1).setDurability(2).setName("Existing").setPoolSize(3).setQuorumName("quorum").setMergePolicyConfig(new MergePolicyConfig("JediPolicy", 23));
        cfg.addScheduledExecutorConfig(scheduledExecutorConfig);
        ScheduledExecutorConfig defaultSchedExecConfig = new ScheduledExecutorConfig();
        cfg.addScheduledExecutorConfig(defaultSchedExecConfig);
        ScheduledExecutorConfig existing = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getScheduledExecutorConfig("Existing");
        Assert.assertEquals(scheduledExecutorConfig, existing);
        ScheduledExecutorConfig fallsbackToDefault = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getScheduledExecutorConfig("NotExisting/Default");
        Assert.assertEquals(defaultSchedExecConfig.getMergePolicyConfig(), fallsbackToDefault.getMergePolicyConfig());
        Assert.assertEquals(defaultSchedExecConfig.getCapacity(), fallsbackToDefault.getCapacity());
        Assert.assertEquals(defaultSchedExecConfig.getPoolSize(), fallsbackToDefault.getPoolSize());
        Assert.assertEquals(defaultSchedExecConfig.getDurability(), fallsbackToDefault.getDurability());
    }

    @Test
    public void testQuorumConfig_configByClassName() {
        Config config = new Config();
        QuorumConfig quorumConfig = new QuorumConfig("test-quorum", true, 3);
        quorumConfig.setType(READ_WRITE).setQuorumFunctionClassName("com.hazelcast.QuorumFunction");
        config.addQuorumConfig(quorumConfig);
        QuorumConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getQuorumConfig("test-quorum");
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (quorumConfig.toString())), new ConfigCompatibilityChecker.QuorumConfigChecker().check(quorumConfig, generatedConfig));
    }

    @Test
    public void testQuorumConfig_configuredByRecentlyActiveQuorumConfigBuilder() {
        Config config = new Config();
        QuorumConfig quorumConfig = QuorumConfig.newRecentlyActiveQuorumConfigBuilder("recently-active", 3, 3141592).build();
        quorumConfig.setType(READ_WRITE).addListenerConfig(new QuorumListenerConfig("com.hazelcast.QuorumListener"));
        config.addQuorumConfig(quorumConfig);
        QuorumConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getQuorumConfig("recently-active");
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (quorumConfig.toString())), new ConfigCompatibilityChecker.QuorumConfigChecker().check(quorumConfig, generatedConfig));
    }

    @Test
    public void testQuorumConfig_configuredByProbabilisticQuorumConfigBuilder() {
        Config config = new Config();
        QuorumConfig quorumConfig = QuorumConfig.newProbabilisticQuorumConfigBuilder("probabilistic-quorum", 3).withHeartbeatIntervalMillis(1).withAcceptableHeartbeatPauseMillis(2).withMaxSampleSize(3).withMinStdDeviationMillis(4).withSuspicionThreshold(5).build();
        quorumConfig.setType(READ_WRITE).addListenerConfig(new QuorumListenerConfig("com.hazelcast.QuorumListener"));
        config.addQuorumConfig(quorumConfig);
        QuorumConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getQuorumConfig("probabilistic-quorum");
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (quorumConfig.toString())), new ConfigCompatibilityChecker.QuorumConfigChecker().check(quorumConfig, generatedConfig));
    }

    @Test
    public void testCPSubsystemConfig() {
        Config config = new Config();
        config.getCPSubsystemConfig().setCPMemberCount(10).setGroupSize(5).setSessionTimeToLiveSeconds(15).setSessionHeartbeatIntervalSeconds(3).setMissingCPMemberAutoRemovalSeconds(120).setFailOnIndeterminateOperationState(true);
        config.getCPSubsystemConfig().getRaftAlgorithmConfig().setLeaderElectionTimeoutInMillis(500).setLeaderHeartbeatPeriodInMillis(100).setMaxMissedLeaderHeartbeatCount(10).setAppendRequestMaxEntryCount(25).setAppendRequestMaxEntryCount(250).setUncommittedEntryCountToRejectNewAppends(75).setAppendRequestBackoffTimeoutInMillis(50);
        config.getCPSubsystemConfig().addSemaphoreConfig(new CPSemaphoreConfig("sem1", true)).addSemaphoreConfig(new CPSemaphoreConfig("sem2", false));
        config.getCPSubsystemConfig().addLockConfig(new FencedLockConfig("lock1", 1)).addLockConfig(new FencedLockConfig("lock1", 2));
        CPSubsystemConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getCPSubsystemConfig();
        Assert.assertTrue(((generatedConfig + " should be compatible with ") + (config.getCPSubsystemConfig())), new ConfigCompatibilityChecker.CPSubsystemConfigChecker().check(config.getCPSubsystemConfig(), generatedConfig));
    }

    @Test
    public void testMemcacheProtocolConfig() {
        MemcacheProtocolConfig memcacheProtocolConfig = new MemcacheProtocolConfig().setEnabled(true);
        Config config = new Config();
        config.getNetworkConfig().setMemcacheProtocolConfig(memcacheProtocolConfig);
        MemcacheProtocolConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getNetworkConfig().getMemcacheProtocolConfig();
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (memcacheProtocolConfig.toString())), new ConfigCompatibilityChecker.MemcacheProtocolConfigChecker().check(memcacheProtocolConfig, generatedConfig));
    }

    @Test
    public void testEmptyRestApiConfig() {
        RestApiConfig restApiConfig = new RestApiConfig();
        Config config = new Config();
        config.getNetworkConfig().setRestApiConfig(restApiConfig);
        RestApiConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getNetworkConfig().getRestApiConfig();
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (restApiConfig.toString())), new ConfigCompatibilityChecker.RestApiConfigChecker().check(restApiConfig, generatedConfig));
    }

    @Test
    public void testAllEnabledRestApiConfig() {
        RestApiConfig restApiConfig = new RestApiConfig();
        restApiConfig.setEnabled(true).enableAllGroups();
        Config config = new Config();
        config.getNetworkConfig().setRestApiConfig(restApiConfig);
        RestApiConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getNetworkConfig().getRestApiConfig();
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (restApiConfig.toString())), new ConfigCompatibilityChecker.RestApiConfigChecker().check(restApiConfig, generatedConfig));
    }

    @Test
    public void testExplicitlyAssignedGroupsRestApiConfig() {
        RestApiConfig restApiConfig = new RestApiConfig();
        restApiConfig.setEnabled(true);
        restApiConfig.enableGroups(CLUSTER_READ, HEALTH_CHECK, HOT_RESTART, WAN);
        restApiConfig.disableGroups(CLUSTER_WRITE, DATA);
        Config config = new Config();
        config.getNetworkConfig().setRestApiConfig(restApiConfig);
        RestApiConfig generatedConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(config).getNetworkConfig().getRestApiConfig();
        Assert.assertTrue((((generatedConfig.toString()) + " should be compatible with ") + (restApiConfig.toString())), new ConfigCompatibilityChecker.RestApiConfigChecker().check(restApiConfig, generatedConfig));
    }

    @Test
    public void testAdvancedNetworkMulticastJoinConfig() {
        Config cfg = new Config();
        cfg.getAdvancedNetworkConfig().setEnabled(true);
        MulticastConfig expectedConfig = ConfigXmlGeneratorTest.multicastConfig();
        cfg.getAdvancedNetworkConfig().getJoin().setMulticastConfig(expectedConfig);
        cfg.getAdvancedNetworkConfig().setEnabled(true);
        MulticastConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getAdvancedNetworkConfig().getJoin().getMulticastConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testAdvancedNetworkTcpJoinConfig() {
        Config cfg = new Config();
        cfg.getAdvancedNetworkConfig().setEnabled(true);
        TcpIpConfig expectedConfig = ConfigXmlGeneratorTest.tcpIpConfig();
        cfg.getAdvancedNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        cfg.getAdvancedNetworkConfig().getJoin().setTcpIpConfig(expectedConfig);
        TcpIpConfig actualConfig = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getAdvancedNetworkConfig().getJoin().getTcpIpConfig();
        Assert.assertEquals(expectedConfig, actualConfig);
    }

    @Test
    public void testAdvancedNetworkFailureDetectorConfigGenerator() {
        Config cfg = new Config();
        IcmpFailureDetectorConfig expected = new IcmpFailureDetectorConfig();
        // Defaults to false
        expected.setEnabled(true).setIntervalMilliseconds(1001).setTimeoutMilliseconds(1002).setMaxAttempts(4).setTtl(300).setParallelMode(false).setFailFastOnStartup(false);// Defaults to false

        cfg.getAdvancedNetworkConfig().setEnabled(true);
        cfg.getAdvancedNetworkConfig().setIcmpFailureDetectorConfig(expected);
        Config newConfigViaXMLGenerator = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg);
        IcmpFailureDetectorConfig actual = newConfigViaXMLGenerator.getAdvancedNetworkConfig().getIcmpFailureDetectorConfig();
        ConfigXmlGeneratorTest.assertFailureDetectorConfigEquals(expected, actual);
    }

    @Test
    public void testAdvancedNetworkMemberAddressProvider() {
        Config cfg = new Config();
        cfg.getAdvancedNetworkConfig().setEnabled(true);
        MemberAddressProviderConfig expected = cfg.getAdvancedNetworkConfig().getMemberAddressProviderConfig();
        expected.setEnabled(true).setEnabled(true).setClassName("ClassName");
        expected.getProperties().setProperty("p1", "v1");
        Config newConfigViaXMLGenerator = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg);
        MemberAddressProviderConfig actual = newConfigViaXMLGenerator.getAdvancedNetworkConfig().getMemberAddressProviderConfig();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEndpointConfig_completeConfiguration() {
        Config cfg = new Config();
        ServerSocketEndpointConfig expected = new ServerSocketEndpointConfig();
        expected.setName(HazelcastTestSupport.randomName());
        expected.setPort(9393);
        expected.setPortCount(22);
        expected.setPortAutoIncrement(false);
        expected.setPublicAddress("194.143.14.17");
        expected.setReuseAddress(true);
        expected.addOutboundPortDefinition("4242-4244").addOutboundPortDefinition("5252;5254");
        SocketInterceptorConfig socketInterceptorConfig = new SocketInterceptorConfig().setEnabled(true).setClassName("socketInterceptor").setProperty("key", "value");
        expected.setSocketInterceptorConfig(socketInterceptorConfig);
        expected.getInterfaces().addInterface("127.0.0.*").setEnabled(true);
        expected.setSocketConnectTimeoutSeconds(67);
        expected.setSocketRcvBufferSizeKb(192);
        expected.setSocketSendBufferSizeKb(384);
        expected.setSocketLingerSeconds(3);
        expected.setSocketKeepAlive(true);
        expected.setSocketTcpNoDelay(true);
        expected.setSocketBufferDirect(true);
        cfg.getAdvancedNetworkConfig().setEnabled(true);
        cfg.getAdvancedNetworkConfig().addWanEndpointConfig(expected);
        EndpointConfig actual = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getAdvancedNetworkConfig().getEndpointConfigs().get(expected.getQualifier());
        ConfigCompatibilityChecker.checkEndpointConfigCompatible(expected, actual);
    }

    @Test
    public void testEndpointConfig_defaultConfiguration() {
        Config cfg = new Config();
        ServerSocketEndpointConfig expected = new ServerSocketEndpointConfig();
        expected.setProtocolType(ProtocolType.MEMBER);
        cfg.getAdvancedNetworkConfig().setEnabled(true);
        cfg.getAdvancedNetworkConfig().setMemberEndpointConfig(expected);
        EndpointConfig actual = ConfigXmlGeneratorTest.getNewConfigViaXMLGenerator(cfg).getAdvancedNetworkConfig().getEndpointConfigs().get(expected.getQualifier());
        ConfigCompatibilityChecker.checkEndpointConfigCompatible(expected, actual);
    }
}

