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


import CacheDeserializedValues.ALWAYS;
import CacheDeserializedValues.INDEX_ONLY;
import CacheDeserializedValues.NEVER;
import ConsistencyCheckStrategy.MERKLE_TREES;
import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import EvictionPolicy.NONE;
import EvictionPolicy.RANDOM;
import InMemoryFormat.OBJECT;
import MapStoreConfig.InitialLoadMode.EAGER;
import MapStoreConfig.InitialLoadMode.LAZY;
import MaxSizeConfig.MaxSizePolicy.PER_NODE;
import MaxSizeConfig.MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE;
import MemoryUnit.MEGABYTES;
import MetadataPolicy.OFF;
import MultiMapConfig.ValueCollectionType.LIST;
import NativeMemoryConfig.MemoryAllocatorType.POOLED;
import OnJoinPermissionOperationName.SEND;
import PartitionGroupConfig.MemberGroupType.CUSTOM;
import QuorumType.READ;
import QuorumType.READ_WRITE;
import TopicOverloadPolicy.BLOCK;
import WANQueueFullBehavior.THROW_EXCEPTION;
import WANQueueFullBehavior.THROW_EXCEPTION_ONLY_IF_REPLICATION_ACTIVE;
import WanAcknowledgeType.ACK_ON_OPERATION_COMPLETE;
import WanPublisherState.STOPPED;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CRDTReplicationConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.Config;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.HotRestartPersistenceConfig;
import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.PermissionConfig.PermissionType;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.MapStoreFactory;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.QueueStore;
import com.hazelcast.core.QueueStoreFactory;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.nio.SocketInterceptor;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.hazelcast.nio.ssl.SSLContextFactory;
import com.hazelcast.quorum.impl.ProbabilisticQuorumFunction;
import com.hazelcast.quorum.impl.RecentlyActiveQuorumFunction;
import com.hazelcast.spring.serialization.DummyDataSerializableFactory;
import com.hazelcast.spring.serialization.DummyPortableFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.wan.WanReplicationEndpoint;
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
@ContextConfiguration(locations = { "fullConfig-applicationContext-hazelcast.xml" })
@Category(QuickTest.class)
@SuppressWarnings("unused")
public class TestFullApplicationContext extends HazelcastTestSupport {
    private Config config;

    @Resource(name = "instance")
    private HazelcastInstance instance;

    @Resource(name = "map1")
    private IMap<Object, Object> map1;

    @Resource(name = "map2")
    private IMap<Object, Object> map2;

    @Resource(name = "multiMap")
    private MultiMap multiMap;

    @Resource(name = "replicatedMap")
    private ReplicatedMap replicatedMap;

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

    @Resource(name = "flakeIdGenerator")
    private FlakeIdGenerator flakeIdGenerator;

    @Resource(name = "atomicLong")
    private IAtomicLong atomicLong;

    @Resource(name = "atomicReference")
    private IAtomicReference atomicReference;

    @Resource(name = "countDownLatch")
    private ICountDownLatch countDownLatch;

    @Resource(name = "semaphore")
    private ISemaphore semaphore;

    @Resource(name = "lock")
    private ILock lock;

    @Resource(name = "dummyMapStore")
    private MapStore dummyMapStore;

    @Autowired
    private MapStoreFactory dummyMapStoreFactory;

    @Resource(name = "dummyQueueStore")
    private QueueStore dummyQueueStore;

    @Autowired
    private QueueStoreFactory dummyQueueStoreFactory;

    @Resource(name = "dummyRingbufferStore")
    private RingbufferStore dummyRingbufferStore;

    @Autowired
    private RingbufferStoreFactory dummyRingbufferStoreFactory;

    @Autowired
    private WanReplicationEndpoint wanReplication;

    @Autowired
    private MembershipListener membershipListener;

    @Autowired
    private EntryListener entryListener;

    @Resource
    private SSLContextFactory sslContextFactory;

    @Resource
    private SocketInterceptor socketInterceptor;

    @Resource
    private StreamSerializer dummySerializer;

    @Resource(name = "pnCounter")
    private PNCounter pnCounter;

    @Test
    public void testCacheConfig() {
        Assert.assertNotNull(config);
        Assert.assertEquals(1, config.getCacheConfigs().size());
        CacheSimpleConfig cacheConfig = config.getCacheConfig("testCache");
        Assert.assertEquals("testCache", cacheConfig.getName());
        Assert.assertTrue(cacheConfig.isDisablePerEntryInvalidationEvents());
        Assert.assertTrue(cacheConfig.getHotRestartConfig().isEnabled());
        Assert.assertTrue(cacheConfig.getHotRestartConfig().isFsync());
        WanReplicationRef wanRef = cacheConfig.getWanReplicationRef();
        Assert.assertEquals("testWan", wanRef.getName());
        Assert.assertEquals("PUT_IF_ABSENT", wanRef.getMergePolicy());
        Assert.assertEquals(1, wanRef.getFilters().size());
        Assert.assertEquals("com.example.SampleFilter", wanRef.getFilters().get(0));
    }

    @Test
    public void testMapConfig() {
        Assert.assertNotNull(config);
        Assert.assertEquals(27, config.getMapConfigs().size());
        MapConfig testMapConfig = config.getMapConfig("testMap");
        Assert.assertNotNull(testMapConfig);
        Assert.assertEquals("testMap", testMapConfig.getName());
        Assert.assertEquals(2, testMapConfig.getBackupCount());
        Assert.assertEquals(NONE, testMapConfig.getEvictionPolicy());
        Assert.assertEquals(Integer.MAX_VALUE, testMapConfig.getMaxSizeConfig().getSize());
        Assert.assertEquals(30, testMapConfig.getEvictionPercentage());
        Assert.assertEquals(0, testMapConfig.getTimeToLiveSeconds());
        Assert.assertTrue(testMapConfig.getHotRestartConfig().isEnabled());
        Assert.assertTrue(testMapConfig.getHotRestartConfig().isFsync());
        Assert.assertEquals(OFF, testMapConfig.getMetadataPolicy());
        Assert.assertEquals(1000, testMapConfig.getMinEvictionCheckMillis());
        Assert.assertTrue(testMapConfig.isReadBackupData());
        Assert.assertEquals(2, testMapConfig.getMapIndexConfigs().size());
        for (MapIndexConfig index : testMapConfig.getMapIndexConfigs()) {
            if ("name".equals(index.getAttribute())) {
                Assert.assertFalse(index.isOrdered());
            } else
                if ("age".equals(index.getAttribute())) {
                    Assert.assertTrue(index.isOrdered());
                } else {
                    Assert.fail("unknown index!");
                }

        }
        Assert.assertEquals(2, testMapConfig.getMapAttributeConfigs().size());
        for (MapAttributeConfig attribute : testMapConfig.getMapAttributeConfigs()) {
            if ("power".equals(attribute.getName())) {
                Assert.assertEquals("com.car.PowerExtractor", attribute.getExtractor());
            } else
                if ("weight".equals(attribute.getName())) {
                    Assert.assertEquals("com.car.WeightExtractor", attribute.getExtractor());
                } else {
                    Assert.fail("unknown attribute!");
                }

        }
        Assert.assertEquals("my-quorum", testMapConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = testMapConfig.getMergePolicyConfig();
        Assert.assertNotNull(mergePolicyConfig);
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
        // test that the testMapConfig has a mapStoreConfig and it is correct
        MapStoreConfig testMapStoreConfig = testMapConfig.getMapStoreConfig();
        Assert.assertNotNull(testMapStoreConfig);
        Assert.assertEquals("com.hazelcast.spring.DummyStore", testMapStoreConfig.getClassName());
        Assert.assertTrue(testMapStoreConfig.isEnabled());
        Assert.assertEquals(0, testMapStoreConfig.getWriteDelaySeconds());
        Assert.assertEquals(10, testMapStoreConfig.getWriteBatchSize());
        Assert.assertTrue(testMapStoreConfig.isWriteCoalescing());
        Assert.assertEquals(EAGER, testMapStoreConfig.getInitialLoadMode());
        // test that the testMapConfig has a nearCacheConfig and it is correct
        NearCacheConfig testNearCacheConfig = testMapConfig.getNearCacheConfig();
        Assert.assertNotNull(testNearCacheConfig);
        Assert.assertEquals(0, testNearCacheConfig.getTimeToLiveSeconds());
        Assert.assertEquals(60, testNearCacheConfig.getMaxIdleSeconds());
        Assert.assertEquals(LRU, testNearCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(5000, testNearCacheConfig.getEvictionConfig().getSize());
        Assert.assertTrue(testNearCacheConfig.isInvalidateOnChange());
        Assert.assertFalse(testNearCacheConfig.isSerializeKeys());
        // test that the testMapConfig2's mapStoreConfig implementation
        MapConfig testMapConfig2 = config.getMapConfig("testMap2");
        Assert.assertNotNull(testMapConfig2.getMapStoreConfig().getImplementation());
        Assert.assertEquals(dummyMapStore, testMapConfig2.getMapStoreConfig().getImplementation());
        Assert.assertEquals(LAZY, testMapConfig2.getMapStoreConfig().getInitialLoadMode());
        // test testMapConfig2's WanReplicationConfig
        WanReplicationRef wanReplicationRef = testMapConfig2.getWanReplicationRef();
        Assert.assertEquals("testWan", wanReplicationRef.getName());
        Assert.assertEquals("PUT_IF_ABSENT", wanReplicationRef.getMergePolicy());
        Assert.assertTrue(wanReplicationRef.isRepublishingEnabled());
        Assert.assertEquals(1000, testMapConfig2.getMaxSizeConfig().getSize());
        Assert.assertEquals(PER_NODE, testMapConfig2.getMaxSizeConfig().getMaxSizePolicy());
        Assert.assertEquals(2, testMapConfig2.getEntryListenerConfigs().size());
        for (EntryListenerConfig listener : testMapConfig2.getEntryListenerConfigs()) {
            if ((listener.getClassName()) != null) {
                Assert.assertNull(listener.getImplementation());
                Assert.assertTrue(listener.isIncludeValue());
                Assert.assertFalse(listener.isLocal());
            } else {
                Assert.assertNotNull(listener.getImplementation());
                Assert.assertEquals(entryListener, listener.getImplementation());
                Assert.assertTrue(listener.isLocal());
                Assert.assertTrue(listener.isIncludeValue());
            }
        }
        MapConfig simpleMapConfig = config.getMapConfig("simpleMap");
        Assert.assertNotNull(simpleMapConfig);
        Assert.assertEquals("simpleMap", simpleMapConfig.getName());
        Assert.assertEquals(3, simpleMapConfig.getBackupCount());
        Assert.assertEquals(1, simpleMapConfig.getAsyncBackupCount());
        Assert.assertEquals(LRU, simpleMapConfig.getEvictionPolicy());
        Assert.assertEquals(10, simpleMapConfig.getMaxSizeConfig().getSize());
        Assert.assertEquals(50, simpleMapConfig.getEvictionPercentage());
        Assert.assertEquals(1, simpleMapConfig.getTimeToLiveSeconds());
        // test that the simpleMapConfig does NOT have a nearCacheConfig
        Assert.assertNull(simpleMapConfig.getNearCacheConfig());
        MapConfig testMapConfig3 = config.getMapConfig("testMap3");
        Assert.assertEquals("com.hazelcast.spring.DummyStoreFactory", testMapConfig3.getMapStoreConfig().getFactoryClassName());
        Assert.assertFalse(testMapConfig3.getMapStoreConfig().getProperties().isEmpty());
        Assert.assertEquals(testMapConfig3.getMapStoreConfig().getProperty("dummy.property"), "value");
        MapConfig testMapConfig4 = config.getMapConfig("testMap4");
        Assert.assertEquals(dummyMapStoreFactory, testMapConfig4.getMapStoreConfig().getFactoryImplementation());
        MapConfig mapWithOptimizedQueriesConfig = config.getMapConfig("mapWithOptimizedQueries");
        Assert.assertEquals(ALWAYS, mapWithOptimizedQueriesConfig.getCacheDeserializedValues());
        MapConfig mapWithValueCachingSetToNever = config.getMapConfig("mapWithValueCachingSetToNever");
        Assert.assertEquals(NEVER, mapWithValueCachingSetToNever.getCacheDeserializedValues());
        MapConfig mapWithValueCachingSetToAlways = config.getMapConfig("mapWithValueCachingSetToAlways");
        Assert.assertEquals(ALWAYS, mapWithValueCachingSetToAlways.getCacheDeserializedValues());
        MapConfig mapWithNotOptimizedQueriesConfig = config.getMapConfig("mapWithNotOptimizedQueries");
        Assert.assertEquals(INDEX_ONLY, mapWithNotOptimizedQueriesConfig.getCacheDeserializedValues());
        MapConfig mapWithDefaultOptimizedQueriesConfig = config.getMapConfig("mapWithDefaultOptimizedQueries");
        Assert.assertEquals(INDEX_ONLY, mapWithDefaultOptimizedQueriesConfig.getCacheDeserializedValues());
        MapConfig testMapWithPartitionLostListenerConfig = config.getMapConfig("mapWithPartitionLostListener");
        List<MapPartitionLostListenerConfig> partitionLostListenerConfigs = testMapWithPartitionLostListenerConfig.getPartitionLostListenerConfigs();
        Assert.assertEquals(1, partitionLostListenerConfigs.size());
        Assert.assertEquals("DummyMapPartitionLostListenerImpl", partitionLostListenerConfigs.get(0).getClassName());
        MapConfig testMapWithPartitionStrategyConfig = config.getMapConfig("mapWithPartitionStrategy");
        Assert.assertEquals("com.hazelcast.spring.DummyPartitionStrategy", testMapWithPartitionStrategyConfig.getPartitioningStrategyConfig().getPartitioningStrategyClass());
    }

    @Test
    public void testMapNoWanMergePolicy() {
        MapConfig testMapConfig2 = config.getMapConfig("testMap2");
        // test testMapConfig2's WanReplicationConfig
        WanReplicationRef wanReplicationRef = testMapConfig2.getWanReplicationRef();
        Assert.assertEquals("testWan", wanReplicationRef.getName());
        Assert.assertEquals("PUT_IF_ABSENT", wanReplicationRef.getMergePolicy());
    }

    @Test
    public void testMemberFlakeIdGeneratorConfig() {
        FlakeIdGeneratorConfig c = instance.getConfig().findFlakeIdGeneratorConfig("flakeIdGenerator");
        Assert.assertEquals(3, c.getPrefetchCount());
        Assert.assertEquals(10L, c.getPrefetchValidityMillis());
        Assert.assertEquals(20L, c.getIdOffset());
        Assert.assertEquals(30L, c.getNodeIdOffset());
        Assert.assertEquals("flakeIdGenerator*", c.getName());
        Assert.assertFalse(c.isStatisticsEnabled());
    }

    @Test
    public void testQueueConfig() {
        QueueConfig testQConfig = config.getQueueConfig("testQ");
        Assert.assertNotNull(testQConfig);
        Assert.assertEquals("testQ", testQConfig.getName());
        Assert.assertEquals(1000, testQConfig.getMaxSize());
        Assert.assertEquals(1, testQConfig.getItemListenerConfigs().size());
        Assert.assertTrue(testQConfig.isStatisticsEnabled());
        ItemListenerConfig listenerConfig = testQConfig.getItemListenerConfigs().get(0);
        Assert.assertEquals("com.hazelcast.spring.DummyItemListener", listenerConfig.getClassName());
        Assert.assertTrue(listenerConfig.isIncludeValue());
        QueueConfig qConfig = config.getQueueConfig("queueWithSplitBrainConfig");
        Assert.assertNotNull(qConfig);
        Assert.assertEquals("queueWithSplitBrainConfig", qConfig.getName());
        Assert.assertEquals(2500, qConfig.getMaxSize());
        Assert.assertFalse(qConfig.isStatisticsEnabled());
        Assert.assertEquals(100, qConfig.getEmptyQueueTtl());
        Assert.assertEquals("my-quorum", qConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = qConfig.getMergePolicyConfig();
        Assert.assertEquals("DiscardMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
        QueueConfig queueWithStore1 = config.getQueueConfig("queueWithStore1");
        Assert.assertNotNull(queueWithStore1);
        QueueStoreConfig storeConfig1 = queueWithStore1.getQueueStoreConfig();
        Assert.assertNotNull(storeConfig1);
        Assert.assertEquals(DummyQueueStore.class.getName(), storeConfig1.getClassName());
        QueueConfig queueWithStore2 = config.getQueueConfig("queueWithStore2");
        Assert.assertNotNull(queueWithStore2);
        QueueStoreConfig storeConfig2 = queueWithStore2.getQueueStoreConfig();
        Assert.assertNotNull(storeConfig2);
        Assert.assertEquals(DummyQueueStoreFactory.class.getName(), storeConfig2.getFactoryClassName());
        QueueConfig queueWithStore3 = config.getQueueConfig("queueWithStore3");
        Assert.assertNotNull(queueWithStore3);
        QueueStoreConfig storeConfig3 = queueWithStore3.getQueueStoreConfig();
        Assert.assertNotNull(storeConfig3);
        Assert.assertEquals(dummyQueueStore, storeConfig3.getStoreImplementation());
        QueueConfig queueWithStore4 = config.getQueueConfig("queueWithStore4");
        Assert.assertNotNull(queueWithStore4);
        QueueStoreConfig storeConfig4 = queueWithStore4.getQueueStoreConfig();
        Assert.assertNotNull(storeConfig4);
        Assert.assertEquals(dummyQueueStoreFactory, storeConfig4.getFactoryImplementation());
    }

    @Test
    public void testLockConfig() {
        LockConfig lockConfig = config.getLockConfig("lock");
        Assert.assertNotNull(lockConfig);
        Assert.assertEquals("lock", lockConfig.getName());
        Assert.assertEquals("my-quorum", lockConfig.getQuorumName());
    }

    @Test
    public void testRingbufferConfig() {
        RingbufferConfig testRingbuffer = config.getRingbufferConfig("testRingbuffer");
        Assert.assertNotNull(testRingbuffer);
        Assert.assertEquals("testRingbuffer", testRingbuffer.getName());
        Assert.assertEquals(OBJECT, testRingbuffer.getInMemoryFormat());
        Assert.assertEquals(100, testRingbuffer.getCapacity());
        Assert.assertEquals(1, testRingbuffer.getBackupCount());
        Assert.assertEquals(1, testRingbuffer.getAsyncBackupCount());
        Assert.assertEquals(20, testRingbuffer.getTimeToLiveSeconds());
        RingbufferStoreConfig store1 = testRingbuffer.getRingbufferStoreConfig();
        Assert.assertNotNull(store1);
        Assert.assertEquals(DummyRingbufferStore.class.getName(), store1.getClassName());
        MergePolicyConfig mergePolicyConfig = testRingbuffer.getMergePolicyConfig();
        Assert.assertNotNull(mergePolicyConfig);
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
        RingbufferConfig testRingbuffer2 = config.getRingbufferConfig("testRingbuffer2");
        Assert.assertNotNull(testRingbuffer2);
        RingbufferStoreConfig store2 = testRingbuffer2.getRingbufferStoreConfig();
        Assert.assertNotNull(store2);
        Assert.assertEquals(DummyRingbufferStoreFactory.class.getName(), store2.getFactoryClassName());
        Assert.assertFalse(store2.getProperties().isEmpty());
        Assert.assertEquals("value", store2.getProperty("dummy.property"));
        Assert.assertEquals("value2", store2.getProperty("dummy.property.2"));
        RingbufferConfig testRingbuffer3 = config.getRingbufferConfig("testRingbuffer3");
        Assert.assertNotNull(testRingbuffer3);
        RingbufferStoreConfig store3 = testRingbuffer3.getRingbufferStoreConfig();
        Assert.assertNotNull(store3);
        Assert.assertEquals(dummyRingbufferStore, store3.getStoreImplementation());
        RingbufferConfig testRingbuffer4 = config.getRingbufferConfig("testRingbuffer4");
        Assert.assertNotNull(testRingbuffer4);
        RingbufferStoreConfig store4 = testRingbuffer4.getRingbufferStoreConfig();
        Assert.assertNotNull(store4);
        Assert.assertEquals(dummyRingbufferStoreFactory, store4.getFactoryImplementation());
    }

    @Test
    public void testPNCounterConfig() {
        PNCounterConfig testPNCounter = config.getPNCounterConfig("testPNCounter");
        Assert.assertNotNull(testPNCounter);
        Assert.assertEquals("testPNCounter", testPNCounter.getName());
        Assert.assertEquals(100, testPNCounter.getReplicaCount());
        Assert.assertEquals("my-quorum", testPNCounter.getQuorumName());
        Assert.assertFalse(testPNCounter.isStatisticsEnabled());
    }

    @Test
    public void testSecurity() {
        SecurityConfig securityConfig = config.getSecurityConfig();
        Assert.assertEquals(SEND, securityConfig.getOnJoinPermissionOperation());
        final Set<PermissionConfig> clientPermissionConfigs = securityConfig.getClientPermissionConfigs();
        Assert.assertFalse(securityConfig.getClientBlockUnmappedActions());
        Assert.assertTrue(isNotEmpty(clientPermissionConfigs));
        Assert.assertEquals(22, clientPermissionConfigs.size());
        final PermissionConfig pnCounterPermission = addAction("create").setEndpoints(Collections.<String>emptySet());
        assertContains(clientPermissionConfigs, pnCounterPermission);
        Set<PermissionType> permTypes = new HashSet<PermissionType>(Arrays.asList(PermissionType.values()));
        for (PermissionConfig pc : clientPermissionConfigs) {
            permTypes.remove(pc.getType());
        }
        Assert.assertTrue(("All permission types should be listed in fullConfig. Not found ones: " + permTypes), permTypes.isEmpty());
    }

    @Test
    public void testAtomicLongConfig() {
        AtomicLongConfig testAtomicLong = config.getAtomicLongConfig("testAtomicLong");
        Assert.assertNotNull(testAtomicLong);
        Assert.assertEquals("testAtomicLong", testAtomicLong.getName());
        MergePolicyConfig mergePolicyConfig = testAtomicLong.getMergePolicyConfig();
        Assert.assertEquals("DiscardMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
    }

    @Test
    public void testAtomicReferenceConfig() {
        AtomicReferenceConfig testAtomicReference = config.getAtomicReferenceConfig("testAtomicReference");
        Assert.assertNotNull(testAtomicReference);
        Assert.assertEquals("testAtomicReference", testAtomicReference.getName());
        MergePolicyConfig mergePolicyConfig = testAtomicReference.getMergePolicyConfig();
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(4223, mergePolicyConfig.getBatchSize());
    }

    @Test
    public void testCountDownLatchConfig() {
        CountDownLatchConfig testCountDownLatch = config.getCountDownLatchConfig("testCountDownLatch");
        Assert.assertNotNull(testCountDownLatch);
        Assert.assertEquals("testCountDownLatch", testCountDownLatch.getName());
        Assert.assertEquals("my-quorum", testCountDownLatch.getQuorumName());
    }

    @Test
    public void testSemaphoreConfig() {
        SemaphoreConfig testSemaphore = config.getSemaphoreConfig("testSemaphore");
        Assert.assertNotNull(testSemaphore);
        Assert.assertEquals("testSemaphore", testSemaphore.getName());
        Assert.assertEquals(1, testSemaphore.getBackupCount());
        Assert.assertEquals(1, testSemaphore.getAsyncBackupCount());
        Assert.assertEquals(10, testSemaphore.getInitialPermits());
    }

    @Test
    public void testReliableTopicConfig() {
        ReliableTopicConfig testReliableTopic = config.getReliableTopicConfig("testReliableTopic");
        Assert.assertNotNull(testReliableTopic);
        Assert.assertEquals("testReliableTopic", testReliableTopic.getName());
        Assert.assertEquals(1, testReliableTopic.getMessageListenerConfigs().size());
        Assert.assertFalse(testReliableTopic.isStatisticsEnabled());
        ListenerConfig listenerConfig = testReliableTopic.getMessageListenerConfigs().get(0);
        Assert.assertEquals("com.hazelcast.spring.DummyMessageListener", listenerConfig.getClassName());
        Assert.assertEquals(10, testReliableTopic.getReadBatchSize());
        Assert.assertEquals(BLOCK, testReliableTopic.getTopicOverloadPolicy());
    }

    @Test
    public void testMultimapConfig() {
        MultiMapConfig testMultiMapConfig = config.getMultiMapConfig("testMultimap");
        Assert.assertEquals(LIST, testMultiMapConfig.getValueCollectionType());
        Assert.assertEquals(2, testMultiMapConfig.getEntryListenerConfigs().size());
        Assert.assertFalse(testMultiMapConfig.isBinary());
        Assert.assertFalse(testMultiMapConfig.isStatisticsEnabled());
        for (EntryListenerConfig listener : testMultiMapConfig.getEntryListenerConfigs()) {
            if ((listener.getClassName()) != null) {
                Assert.assertNull(listener.getImplementation());
                Assert.assertTrue(listener.isIncludeValue());
                Assert.assertFalse(listener.isLocal());
            } else {
                Assert.assertNotNull(listener.getImplementation());
                Assert.assertEquals(entryListener, listener.getImplementation());
                Assert.assertTrue(listener.isLocal());
                Assert.assertTrue(listener.isIncludeValue());
            }
        }
        MergePolicyConfig mergePolicyConfig = testMultiMapConfig.getMergePolicyConfig();
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(1234, mergePolicyConfig.getBatchSize());
    }

    @Test
    public void testListConfig() {
        ListConfig testListConfig = config.getListConfig("testList");
        Assert.assertNotNull(testListConfig);
        Assert.assertEquals("testList", testListConfig.getName());
        Assert.assertEquals(9999, testListConfig.getMaxSize());
        Assert.assertEquals(1, testListConfig.getBackupCount());
        Assert.assertEquals(1, testListConfig.getAsyncBackupCount());
        Assert.assertFalse(testListConfig.isStatisticsEnabled());
        MergePolicyConfig mergePolicyConfig = testListConfig.getMergePolicyConfig();
        Assert.assertEquals("DiscardMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
    }

    @Test
    public void testSetConfig() {
        SetConfig testSetConfig = config.getSetConfig("testSet");
        Assert.assertNotNull(testSetConfig);
        Assert.assertEquals("testSet", testSetConfig.getName());
        Assert.assertEquals(7777, testSetConfig.getMaxSize());
        Assert.assertEquals(0, testSetConfig.getBackupCount());
        Assert.assertEquals(0, testSetConfig.getAsyncBackupCount());
        Assert.assertFalse(testSetConfig.isStatisticsEnabled());
        MergePolicyConfig mergePolicyConfig = testSetConfig.getMergePolicyConfig();
        Assert.assertEquals("DiscardMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
    }

    @Test
    public void testTopicConfig() {
        TopicConfig testTopicConfig = config.getTopicConfig("testTopic");
        Assert.assertNotNull(testTopicConfig);
        Assert.assertEquals("testTopic", testTopicConfig.getName());
        Assert.assertEquals(1, testTopicConfig.getMessageListenerConfigs().size());
        Assert.assertTrue(testTopicConfig.isGlobalOrderingEnabled());
        Assert.assertFalse(testTopicConfig.isStatisticsEnabled());
        ListenerConfig listenerConfig = testTopicConfig.getMessageListenerConfigs().get(0);
        Assert.assertEquals("com.hazelcast.spring.DummyMessageListener", listenerConfig.getClassName());
    }

    @Test
    public void testServiceConfig() {
        ServiceConfig serviceConfig = config.getServicesConfig().getServiceConfig("my-service");
        Assert.assertEquals("com.hazelcast.spring.MyService", serviceConfig.getClassName());
        Assert.assertEquals("prop1-value", serviceConfig.getProperties().getProperty("prop1"));
        Assert.assertEquals("prop2-value", serviceConfig.getProperties().getProperty("prop2"));
        MyServiceConfig configObject = ((MyServiceConfig) (serviceConfig.getConfigObject()));
        Assert.assertNotNull(configObject);
        Assert.assertEquals("prop1", configObject.stringProp);
        Assert.assertEquals(123, configObject.intProp);
        Assert.assertTrue(configObject.boolProp);
        Object impl = serviceConfig.getImplementation();
        Assert.assertNotNull(impl);
        Assert.assertTrue(("expected service of class com.hazelcast.spring.MyService but it is " + (impl.getClass().getName())), (impl instanceof MyService));
    }

    @Test
    public void testGroupConfig() {
        GroupConfig groupConfig = config.getGroupConfig();
        Assert.assertNotNull(groupConfig);
        Assert.assertEquals("spring-group", groupConfig.getName());
        Assert.assertEquals("spring-group-pass", groupConfig.getPassword());
    }

    @Test
    public void testExecutorConfig() {
        ExecutorConfig testExecConfig = config.getExecutorConfig("testExec");
        Assert.assertNotNull(testExecConfig);
        Assert.assertEquals("testExec", testExecConfig.getName());
        Assert.assertEquals(2, testExecConfig.getPoolSize());
        Assert.assertEquals(100, testExecConfig.getQueueCapacity());
        Assert.assertTrue(testExecConfig.isStatisticsEnabled());
        ExecutorConfig testExec2Config = config.getExecutorConfig("testExec2");
        Assert.assertNotNull(testExec2Config);
        Assert.assertEquals("testExec2", testExec2Config.getName());
        Assert.assertEquals(5, testExec2Config.getPoolSize());
        Assert.assertEquals(300, testExec2Config.getQueueCapacity());
        Assert.assertFalse(testExec2Config.isStatisticsEnabled());
    }

    @Test
    public void testDurableExecutorConfig() {
        DurableExecutorConfig testExecConfig = config.getDurableExecutorConfig("durableExec");
        Assert.assertNotNull(testExecConfig);
        Assert.assertEquals("durableExec", testExecConfig.getName());
        Assert.assertEquals(10, testExecConfig.getPoolSize());
        Assert.assertEquals(5, testExecConfig.getDurability());
        Assert.assertEquals(200, testExecConfig.getCapacity());
    }

    @Test
    public void testScheduledExecutorConfig() {
        ScheduledExecutorConfig testExecConfig = config.getScheduledExecutorConfig("scheduledExec");
        Assert.assertNotNull(testExecConfig);
        Assert.assertEquals("scheduledExec", testExecConfig.getName());
        Assert.assertEquals(10, testExecConfig.getPoolSize());
        Assert.assertEquals(5, testExecConfig.getDurability());
        MergePolicyConfig mergePolicyConfig = testExecConfig.getMergePolicyConfig();
        Assert.assertNotNull(mergePolicyConfig);
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(101, mergePolicyConfig.getBatchSize());
    }

    @Test
    public void testCardinalityEstimatorConfig() {
        CardinalityEstimatorConfig estimatorConfig = config.getCardinalityEstimatorConfig("estimator");
        Assert.assertNotNull(estimatorConfig);
        Assert.assertEquals("estimator", estimatorConfig.getName());
        Assert.assertEquals(4, estimatorConfig.getBackupCount());
        Assert.assertEquals("DiscardMergePolicy", estimatorConfig.getMergePolicyConfig().getPolicy());
        Assert.assertEquals(44, estimatorConfig.getMergePolicyConfig().getBatchSize());
    }

    @Test
    public void testNetworkConfig() {
        NetworkConfig networkConfig = config.getNetworkConfig();
        Assert.assertNotNull(networkConfig);
        Assert.assertEquals(5700, networkConfig.getPort());
        Assert.assertFalse(networkConfig.isPortAutoIncrement());
        Collection<String> allowedPorts = networkConfig.getOutboundPortDefinitions();
        Assert.assertEquals(2, allowedPorts.size());
        Iterator portIter = allowedPorts.iterator();
        Assert.assertEquals("35000-35100", portIter.next());
        Assert.assertEquals("36000,36100", portIter.next());
        Assert.assertFalse(networkConfig.getJoin().getMulticastConfig().isEnabled());
        Assert.assertEquals(networkConfig.getJoin().getMulticastConfig().getMulticastTimeoutSeconds(), 8);
        Assert.assertEquals(networkConfig.getJoin().getMulticastConfig().getMulticastTimeToLive(), 16);
        Assert.assertFalse(networkConfig.getJoin().getMulticastConfig().isLoopbackModeEnabled());
        Set<String> tis = networkConfig.getJoin().getMulticastConfig().getTrustedInterfaces();
        Assert.assertEquals(1, tis.size());
        Assert.assertEquals("10.10.10.*", tis.iterator().next());
        Assert.assertFalse(networkConfig.getInterfaces().isEnabled());
        Assert.assertEquals(1, networkConfig.getInterfaces().getInterfaces().size());
        Assert.assertEquals("10.10.1.*", networkConfig.getInterfaces().getInterfaces().iterator().next());
        TcpIpConfig tcp = networkConfig.getJoin().getTcpIpConfig();
        Assert.assertNotNull(tcp);
        Assert.assertTrue(tcp.isEnabled());
        SymmetricEncryptionConfig symmetricEncryptionConfig = networkConfig.getSymmetricEncryptionConfig();
        Assert.assertFalse(symmetricEncryptionConfig.isEnabled());
        Assert.assertEquals("PBEWithMD5AndDES", symmetricEncryptionConfig.getAlgorithm());
        Assert.assertEquals("thesalt", symmetricEncryptionConfig.getSalt());
        Assert.assertEquals("thepass", symmetricEncryptionConfig.getPassword());
        Assert.assertEquals(19, symmetricEncryptionConfig.getIterationCount());
        List<String> members = tcp.getMembers();
        Assert.assertEquals(members.toString(), 2, members.size());
        Assert.assertEquals("127.0.0.1:5700", members.get(0));
        Assert.assertEquals("127.0.0.1:5701", members.get(1));
        Assert.assertEquals("127.0.0.1:5700", tcp.getRequiredMember());
        assertAwsConfig(networkConfig.getJoin().getAwsConfig());
        assertGcpConfig(networkConfig.getJoin().getGcpConfig());
        assertAzureConfig(networkConfig.getJoin().getAzureConfig());
        assertKubernetesConfig(networkConfig.getJoin().getKubernetesConfig());
        assertEurekaConfig(networkConfig.getJoin().getEurekaConfig());
        Assert.assertTrue("reuse-address", networkConfig.isReuseAddress());
        assertDiscoveryConfig(networkConfig.getJoin().getDiscoveryConfig());
        MemberAddressProviderConfig memberAddressProviderConfig = networkConfig.getMemberAddressProviderConfig();
        Assert.assertFalse(memberAddressProviderConfig.isEnabled());
        Assert.assertEquals("com.hazelcast.spring.DummyMemberAddressProvider", memberAddressProviderConfig.getClassName());
        Assert.assertFalse(memberAddressProviderConfig.getProperties().isEmpty());
        Assert.assertEquals("value", memberAddressProviderConfig.getProperties().getProperty("dummy.property"));
        Assert.assertEquals("value2", memberAddressProviderConfig.getProperties().getProperty("dummy.property.2"));
        IcmpFailureDetectorConfig icmpFailureDetectorConfig = networkConfig.getIcmpFailureDetectorConfig();
        Assert.assertFalse(icmpFailureDetectorConfig.isEnabled());
        Assert.assertTrue(icmpFailureDetectorConfig.isParallelMode());
        Assert.assertTrue(icmpFailureDetectorConfig.isFailFastOnStartup());
        Assert.assertEquals(500, icmpFailureDetectorConfig.getTimeoutMilliseconds());
        Assert.assertEquals(1002, icmpFailureDetectorConfig.getIntervalMilliseconds());
        Assert.assertEquals(2, icmpFailureDetectorConfig.getMaxAttempts());
        Assert.assertEquals(1, icmpFailureDetectorConfig.getTtl());
    }

    @Test
    public void testProperties() {
        Properties properties = config.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals("5", properties.get(MERGE_FIRST_RUN_DELAY_SECONDS.getName()));
        Assert.assertEquals("5", properties.get(MERGE_NEXT_RUN_DELAY_SECONDS.getName()));
        Assert.assertEquals("277", properties.get(PARTITION_COUNT.getName()));
        Config config2 = instance.getConfig();
        Properties properties2 = config2.getProperties();
        Assert.assertNotNull(properties2);
        Assert.assertEquals("5", properties2.get(MERGE_FIRST_RUN_DELAY_SECONDS.getName()));
        Assert.assertEquals("5", properties2.get(MERGE_NEXT_RUN_DELAY_SECONDS.getName()));
        Assert.assertEquals("277", properties2.get(PARTITION_COUNT.getName()));
    }

    @Test
    public void testInstance() {
        Assert.assertNotNull(instance);
        Set<Member> members = instance.getCluster().getMembers();
        Assert.assertEquals(1, members.size());
        Member member = members.iterator().next();
        InetSocketAddress inetSocketAddress = member.getSocketAddress();
        Assert.assertEquals(5700, inetSocketAddress.getPort());
        Assert.assertEquals("test-instance", config.getInstanceName());
        Assert.assertEquals("HAZELCAST_ENTERPRISE_LICENSE_KEY", config.getLicenseKey());
        Assert.assertEquals(277, instance.getPartitionService().getPartitions().size());
    }

    @Test
    public void testHazelcastInstances() {
        Assert.assertNotNull(map1);
        Assert.assertNotNull(map2);
        Assert.assertNotNull(multiMap);
        Assert.assertNotNull(replicatedMap);
        Assert.assertNotNull(queue);
        Assert.assertNotNull(topic);
        Assert.assertNotNull(set);
        Assert.assertNotNull(list);
        Assert.assertNotNull(executorService);
        Assert.assertNotNull(idGenerator);
        Assert.assertNotNull(flakeIdGenerator);
        Assert.assertNotNull(atomicLong);
        Assert.assertNotNull(atomicReference);
        Assert.assertNotNull(countDownLatch);
        Assert.assertNotNull(semaphore);
        Assert.assertNotNull(lock);
        Assert.assertNotNull(pnCounter);
        Assert.assertEquals("map1", map1.getName());
        Assert.assertEquals("map2", map2.getName());
        Assert.assertEquals("testMultimap", multiMap.getName());
        Assert.assertEquals("replicatedMap", replicatedMap.getName());
        Assert.assertEquals("testQ", queue.getName());
        Assert.assertEquals("testTopic", topic.getName());
        Assert.assertEquals("set", set.getName());
        Assert.assertEquals("list", list.getName());
        Assert.assertEquals("idGenerator", idGenerator.getName());
        Assert.assertEquals("flakeIdGenerator", flakeIdGenerator.getName());
        Assert.assertEquals("testAtomicLong", atomicLong.getName());
        Assert.assertEquals("testAtomicReference", atomicReference.getName());
        Assert.assertEquals("countDownLatch", countDownLatch.getName());
        Assert.assertEquals("semaphore", semaphore.getName());
    }

    @Test
    public void testWanReplicationConfig() {
        WanReplicationConfig wcfg = config.getWanReplicationConfig("testWan");
        Assert.assertNotNull(wcfg);
        WanPublisherConfig publisherConfig = wcfg.getWanPublisherConfigs().get(0);
        Assert.assertEquals("tokyo", publisherConfig.getGroupName());
        Assert.assertEquals("tokyoPublisherId", publisherConfig.getPublisherId());
        Assert.assertEquals("com.hazelcast.enterprise.wan.replication.WanBatchReplication", publisherConfig.getClassName());
        Assert.assertEquals(THROW_EXCEPTION, publisherConfig.getQueueFullBehavior());
        Assert.assertEquals(STOPPED, publisherConfig.getInitialPublisherState());
        Assert.assertEquals(1000, publisherConfig.getQueueCapacity());
        Map<String, Comparable> publisherProps = publisherConfig.getProperties();
        Assert.assertEquals("50", publisherProps.get("batch.size"));
        Assert.assertEquals("3000", publisherProps.get("batch.max.delay.millis"));
        Assert.assertEquals("false", publisherProps.get("snapshot.enabled"));
        Assert.assertEquals("5000", publisherProps.get("response.timeout.millis"));
        Assert.assertEquals(ACK_ON_OPERATION_COMPLETE.name(), publisherProps.get("ack.type"));
        Assert.assertEquals("pass", publisherProps.get("group.password"));
        WanPublisherConfig customPublisher = wcfg.getWanPublisherConfigs().get(1);
        Assert.assertEquals("istanbul", customPublisher.getGroupName());
        Assert.assertEquals("istanbulPublisherId", customPublisher.getPublisherId());
        Assert.assertEquals("com.hazelcast.wan.custom.CustomPublisher", customPublisher.getClassName());
        Assert.assertEquals(THROW_EXCEPTION_ONLY_IF_REPLICATION_ACTIVE, customPublisher.getQueueFullBehavior());
        Map<String, Comparable> customPublisherProps = customPublisher.getProperties();
        Assert.assertEquals("prop.publisher", customPublisherProps.get("custom.prop.publisher"));
        Assert.assertEquals("5", customPublisherProps.get("discovery.period"));
        Assert.assertEquals("2", customPublisherProps.get("maxEndpoints"));
        assertAwsConfig(customPublisher.getAwsConfig());
        assertGcpConfig(customPublisher.getGcpConfig());
        assertAzureConfig(customPublisher.getAzureConfig());
        assertKubernetesConfig(customPublisher.getKubernetesConfig());
        assertEurekaConfig(customPublisher.getEurekaConfig());
        assertDiscoveryConfig(customPublisher.getDiscoveryConfig());
        WanPublisherConfig publisherPlaceHolderConfig = wcfg.getWanPublisherConfigs().get(2);
        Assert.assertEquals(5000, publisherPlaceHolderConfig.getQueueCapacity());
        WanConsumerConfig consumerConfig = wcfg.getWanConsumerConfig();
        Assert.assertEquals("com.hazelcast.wan.custom.WanConsumer", consumerConfig.getClassName());
        Map<String, Comparable> consumerProps = consumerConfig.getProperties();
        Assert.assertEquals("prop.consumer", consumerProps.get("custom.prop.consumer"));
        Assert.assertTrue(consumerConfig.isPersistWanReplicatedData());
    }

    @Test
    public void testWanConsumerWithPersistDataFalse() {
        WanReplicationConfig config2 = config.getWanReplicationConfig("testWan2");
        WanConsumerConfig consumerConfig2 = config2.getWanConsumerConfig();
        assertInstanceOf(DummyWanConsumer.class, consumerConfig2.getImplementation());
        Assert.assertFalse(consumerConfig2.isPersistWanReplicatedData());
    }

    @Test
    public void testNoWanConsumerClass() {
        WanReplicationConfig config2 = config.getWanReplicationConfig("testWan3");
        WanConsumerConfig consumerConfig2 = config2.getWanConsumerConfig();
        Assert.assertFalse(consumerConfig2.isPersistWanReplicatedData());
    }

    @Test
    public void testWanReplicationSyncConfig() {
        final WanReplicationConfig wcfg = config.getWanReplicationConfig("testWan2");
        final WanConsumerConfig consumerConfig = wcfg.getWanConsumerConfig();
        final Map<String, Comparable> consumerProps = new HashMap<String, Comparable>();
        consumerProps.put("custom.prop.consumer", "prop.consumer");
        consumerConfig.setProperties(consumerProps);
        assertInstanceOf(DummyWanConsumer.class, consumerConfig.getImplementation());
        Assert.assertEquals("prop.consumer", consumerConfig.getProperties().get("custom.prop.consumer"));
        Assert.assertFalse(consumerConfig.isPersistWanReplicatedData());
        final List<WanPublisherConfig> publisherConfigs = wcfg.getWanPublisherConfigs();
        Assert.assertNotNull(publisherConfigs);
        Assert.assertEquals(1, publisherConfigs.size());
        final WanPublisherConfig publisherConfig = publisherConfigs.get(0);
        Assert.assertEquals("tokyo", publisherConfig.getGroupName());
        Assert.assertEquals("PublisherClassName", publisherConfig.getClassName());
        final WanSyncConfig wanSyncConfig = publisherConfig.getWanSyncConfig();
        Assert.assertNotNull(wanSyncConfig);
        Assert.assertEquals(MERKLE_TREES, wanSyncConfig.getConsistencyCheckStrategy());
    }

    @Test
    public void testConfigListeners() {
        Assert.assertNotNull(membershipListener);
        List<ListenerConfig> list = config.getListenerConfigs();
        Assert.assertEquals(2, list.size());
        for (ListenerConfig lc : list) {
            if ((lc.getClassName()) != null) {
                Assert.assertNull(lc.getImplementation());
                Assert.assertEquals(DummyMembershipListener.class.getName(), lc.getClassName());
            } else {
                Assert.assertNotNull(lc.getImplementation());
                Assert.assertEquals(membershipListener, lc.getImplementation());
            }
        }
    }

    @Test
    public void testPartitionGroupConfig() {
        PartitionGroupConfig pgc = config.getPartitionGroupConfig();
        Assert.assertTrue(pgc.isEnabled());
        Assert.assertEquals(CUSTOM, pgc.getGroupType());
        Assert.assertEquals(2, pgc.getMemberGroupConfigs().size());
        for (MemberGroupConfig mgc : pgc.getMemberGroupConfigs()) {
            Assert.assertEquals(2, mgc.getInterfaces().size());
        }
    }

    @Test
    public void testCRDTReplicationConfig() {
        CRDTReplicationConfig replicationConfig = config.getCRDTReplicationConfig();
        Assert.assertEquals(10, replicationConfig.getMaxConcurrentReplicationTargets());
        Assert.assertEquals(2000, replicationConfig.getReplicationPeriodMillis());
    }

    @Test
    public void testSSLConfig() {
        SSLConfig sslConfig = config.getNetworkConfig().getSSLConfig();
        Assert.assertNotNull(sslConfig);
        Assert.assertFalse(sslConfig.isEnabled());
        Assert.assertEquals(DummySSLContextFactory.class.getName(), sslConfig.getFactoryClassName());
        Assert.assertEquals(sslContextFactory, sslConfig.getFactoryImplementation());
    }

    @Test
    public void testSocketInterceptorConfig() {
        SocketInterceptorConfig socketInterceptorConfig = config.getNetworkConfig().getSocketInterceptorConfig();
        Assert.assertNotNull(socketInterceptorConfig);
        Assert.assertFalse(socketInterceptorConfig.isEnabled());
        Assert.assertEquals(DummySocketInterceptor.class.getName(), socketInterceptorConfig.getClassName());
        Assert.assertEquals(socketInterceptor, socketInterceptorConfig.getImplementation());
    }

    @Test
    public void testManagementCenterConfig() {
        ManagementCenterConfig managementCenterConfig = config.getManagementCenterConfig();
        Assert.assertNotNull(managementCenterConfig);
        Assert.assertTrue(managementCenterConfig.isEnabled());
        Assert.assertFalse(managementCenterConfig.isScriptingEnabled());
        Assert.assertEquals("myserver:80", managementCenterConfig.getUrl());
        Assert.assertEquals(2, managementCenterConfig.getUpdateInterval());
        Assert.assertTrue(managementCenterConfig.getMutualAuthConfig().isEnabled());
        Assert.assertEquals(1, managementCenterConfig.getMutualAuthConfig().getProperties().size());
        Assert.assertEquals("who.let.the.cat.out.class", managementCenterConfig.getMutualAuthConfig().getFactoryClassName());
    }

    @Test
    public void testMemberAttributesConfig() {
        MemberAttributeConfig memberAttributeConfig = config.getMemberAttributeConfig();
        Assert.assertNotNull(memberAttributeConfig);
        Assert.assertEquals("spring-group", memberAttributeConfig.getStringAttribute("cluster.group.name"));
        Assert.assertEquals(new Integer(5700), memberAttributeConfig.getIntAttribute("cluster.port.int"));
        Assert.assertEquals(new Long(5700), memberAttributeConfig.getLongAttribute("cluster.port.long"));
        Assert.assertEquals(new Short("5700"), memberAttributeConfig.getShortAttribute("cluster.port.short"));
        Assert.assertEquals(new Byte("111"), memberAttributeConfig.getByteAttribute("attribute.byte"));
        Assert.assertTrue(memberAttributeConfig.getBooleanAttribute("attribute.boolean"));
        Assert.assertEquals(0.0, memberAttributeConfig.getDoubleAttribute("attribute.double"), 1.0E-4);
        Assert.assertEquals(1234.5678, memberAttributeConfig.getFloatAttribute("attribute.float"), 1.0E-4);
    }

    @Test
    public void testSerializationConfig() {
        SerializationConfig serializationConfig = config.getSerializationConfig();
        Assert.assertEquals(ByteOrder.BIG_ENDIAN, serializationConfig.getByteOrder());
        Assert.assertFalse(serializationConfig.isCheckClassDefErrors());
        Assert.assertEquals(13, serializationConfig.getPortableVersion());
        Map<Integer, String> dataSerializableFactoryClasses = serializationConfig.getDataSerializableFactoryClasses();
        Assert.assertFalse(dataSerializableFactoryClasses.isEmpty());
        Assert.assertEquals(DummyDataSerializableFactory.class.getName(), dataSerializableFactoryClasses.get(1));
        Map<Integer, DataSerializableFactory> dataSerializableFactories = serializationConfig.getDataSerializableFactories();
        Assert.assertFalse(dataSerializableFactories.isEmpty());
        Assert.assertEquals(DummyDataSerializableFactory.class, dataSerializableFactories.get(2).getClass());
        Map<Integer, String> portableFactoryClasses = serializationConfig.getPortableFactoryClasses();
        Assert.assertFalse(portableFactoryClasses.isEmpty());
        Assert.assertEquals(DummyPortableFactory.class.getName(), portableFactoryClasses.get(1));
        Map<Integer, PortableFactory> portableFactories = serializationConfig.getPortableFactories();
        Assert.assertFalse(portableFactories.isEmpty());
        Assert.assertEquals(DummyPortableFactory.class, portableFactories.get(2).getClass());
        Collection<SerializerConfig> serializerConfigs = serializationConfig.getSerializerConfigs();
        Assert.assertFalse(serializerConfigs.isEmpty());
        GlobalSerializerConfig globalSerializerConfig = serializationConfig.getGlobalSerializerConfig();
        Assert.assertNotNull(globalSerializerConfig);
        Assert.assertEquals(dummySerializer, globalSerializerConfig.getImplementation());
    }

    @Test
    public void testNativeMemoryConfig() {
        NativeMemoryConfig nativeMemoryConfig = config.getNativeMemoryConfig();
        Assert.assertFalse(nativeMemoryConfig.isEnabled());
        Assert.assertEquals(MEGABYTES, nativeMemoryConfig.getSize().getUnit());
        Assert.assertEquals(256, nativeMemoryConfig.getSize().getValue());
        Assert.assertEquals(20, nativeMemoryConfig.getPageSize());
        Assert.assertEquals(POOLED, nativeMemoryConfig.getAllocatorType());
        Assert.assertEquals(10.2, nativeMemoryConfig.getMetadataSpacePercentage(), 0.1);
        Assert.assertEquals(10, nativeMemoryConfig.getMinBlockSize());
    }

    @Test
    public void testReplicatedMapConfig() {
        Assert.assertNotNull(config);
        Assert.assertEquals(1, config.getReplicatedMapConfigs().size());
        ReplicatedMapConfig replicatedMapConfig = config.getReplicatedMapConfig("replicatedMap");
        Assert.assertNotNull(replicatedMapConfig);
        Assert.assertEquals("replicatedMap", replicatedMapConfig.getName());
        Assert.assertEquals(OBJECT, replicatedMapConfig.getInMemoryFormat());
        Assert.assertEquals(200, replicatedMapConfig.getReplicationDelayMillis());
        Assert.assertEquals(16, replicatedMapConfig.getConcurrencyLevel());
        Assert.assertFalse(replicatedMapConfig.isAsyncFillup());
        Assert.assertFalse(replicatedMapConfig.isStatisticsEnabled());
        Assert.assertEquals("my-quorum", replicatedMapConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = replicatedMapConfig.getMergePolicyConfig();
        Assert.assertNotNull(mergePolicyConfig);
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
        replicatedMapConfig.getListenerConfigs();
        for (ListenerConfig listener : replicatedMapConfig.getListenerConfigs()) {
            if ((listener.getClassName()) != null) {
                Assert.assertNull(listener.getImplementation());
                Assert.assertTrue(listener.isIncludeValue());
                Assert.assertFalse(listener.isLocal());
            } else {
                Assert.assertNotNull(listener.getImplementation());
                Assert.assertEquals(entryListener, listener.getImplementation());
                Assert.assertTrue(listener.isLocal());
                Assert.assertTrue(listener.isIncludeValue());
            }
        }
    }

    @Test
    public void testQuorumConfig() {
        Assert.assertNotNull(config);
        Assert.assertEquals(3, config.getQuorumConfigs().size());
        QuorumConfig quorumConfig = config.getQuorumConfig("my-quorum");
        Assert.assertNotNull(quorumConfig);
        Assert.assertEquals("my-quorum", quorumConfig.getName());
        Assert.assertEquals("com.hazelcast.spring.DummyQuorumFunction", quorumConfig.getQuorumFunctionClassName());
        Assert.assertTrue(quorumConfig.isEnabled());
        Assert.assertEquals(2, quorumConfig.getSize());
        Assert.assertEquals(2, quorumConfig.getListenerConfigs().size());
        Assert.assertEquals(READ, quorumConfig.getType());
        Assert.assertEquals("com.hazelcast.spring.DummyQuorumListener", quorumConfig.getListenerConfigs().get(0).getClassName());
        Assert.assertNotNull(quorumConfig.getListenerConfigs().get(1).getImplementation());
    }

    @Test
    public void testProbabilisticQuorumConfig() {
        QuorumConfig probabilisticQuorumConfig = config.getQuorumConfig("probabilistic-quorum");
        Assert.assertNotNull(probabilisticQuorumConfig);
        Assert.assertEquals("probabilistic-quorum", probabilisticQuorumConfig.getName());
        Assert.assertNotNull(probabilisticQuorumConfig.getQuorumFunctionImplementation());
        assertInstanceOf(ProbabilisticQuorumFunction.class, probabilisticQuorumConfig.getQuorumFunctionImplementation());
        Assert.assertTrue(probabilisticQuorumConfig.isEnabled());
        Assert.assertEquals(3, probabilisticQuorumConfig.getSize());
        Assert.assertEquals(2, probabilisticQuorumConfig.getListenerConfigs().size());
        Assert.assertEquals(READ_WRITE, probabilisticQuorumConfig.getType());
        Assert.assertEquals("com.hazelcast.spring.DummyQuorumListener", probabilisticQuorumConfig.getListenerConfigs().get(0).getClassName());
        Assert.assertNotNull(probabilisticQuorumConfig.getListenerConfigs().get(1).getImplementation());
        ProbabilisticQuorumFunction quorumFunction = ((ProbabilisticQuorumFunction) (probabilisticQuorumConfig.getQuorumFunctionImplementation()));
        Assert.assertEquals(11, quorumFunction.getSuspicionThreshold(), 0.001);
        Assert.assertEquals(31415, quorumFunction.getAcceptableHeartbeatPauseMillis());
        Assert.assertEquals(42, quorumFunction.getMaxSampleSize());
        Assert.assertEquals(77123, quorumFunction.getHeartbeatIntervalMillis());
        Assert.assertEquals(1000, quorumFunction.getMinStdDeviationMillis());
    }

    @Test
    public void testRecentlyActiveQuorumConfig() {
        QuorumConfig recentlyActiveQuorumConfig = config.getQuorumConfig("recently-active-quorum");
        Assert.assertNotNull(recentlyActiveQuorumConfig);
        Assert.assertEquals("recently-active-quorum", recentlyActiveQuorumConfig.getName());
        Assert.assertNotNull(recentlyActiveQuorumConfig.getQuorumFunctionImplementation());
        assertInstanceOf(RecentlyActiveQuorumFunction.class, recentlyActiveQuorumConfig.getQuorumFunctionImplementation());
        Assert.assertTrue(recentlyActiveQuorumConfig.isEnabled());
        Assert.assertEquals(5, recentlyActiveQuorumConfig.getSize());
        Assert.assertEquals(READ_WRITE, recentlyActiveQuorumConfig.getType());
        RecentlyActiveQuorumFunction quorumFunction = ((RecentlyActiveQuorumFunction) (recentlyActiveQuorumConfig.getQuorumFunctionImplementation()));
        Assert.assertEquals(5123, quorumFunction.getHeartbeatToleranceMillis());
    }

    @Test
    public void testFullQueryCacheConfig() {
        MapConfig mapConfig = config.getMapConfig("map-with-query-cache");
        QueryCacheConfig queryCacheConfig = mapConfig.getQueryCacheConfigs().get(0);
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
        assertIndexesEqual(queryCacheConfig);
        Assert.assertEquals("__key > 12", queryCacheConfig.getPredicateConfig().getSql());
        Assert.assertEquals(LRU, queryCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(ENTRY_COUNT, queryCacheConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(111, queryCacheConfig.getEvictionConfig().getSize());
    }

    @Test
    public void testMapNativeMaxSizePolicy() {
        MapConfig mapConfig = config.getMapConfig("map-with-native-max-size-policy");
        MaxSizeConfig maxSizeConfig = mapConfig.getMaxSizeConfig();
        Assert.assertEquals(USED_NATIVE_MEMORY_PERCENTAGE, maxSizeConfig.getMaxSizePolicy());
    }

    @Test
    public void testHotRestart() {
        File dir = new File("/mnt/hot-restart/");
        File hotBackupDir = new File("/mnt/hot-backup/");
        HotRestartPersistenceConfig hotRestartPersistenceConfig = config.getHotRestartPersistenceConfig();
        Assert.assertTrue(hotRestartPersistenceConfig.isEnabled());
        Assert.assertEquals(dir.getAbsolutePath(), hotRestartPersistenceConfig.getBaseDir().getAbsolutePath());
        Assert.assertEquals(hotBackupDir.getAbsolutePath(), hotRestartPersistenceConfig.getBackupDir().getAbsolutePath());
        Assert.assertEquals(1111, hotRestartPersistenceConfig.getValidationTimeoutSeconds());
        Assert.assertEquals(2222, hotRestartPersistenceConfig.getDataLoadTimeoutSeconds());
        Assert.assertEquals(PARTIAL_RECOVERY_MOST_COMPLETE, hotRestartPersistenceConfig.getClusterDataRecoveryPolicy());
        Assert.assertFalse(hotRestartPersistenceConfig.isAutoRemoveStaleData());
    }

    @Test
    public void testMapEvictionPolicies() {
        Assert.assertEquals(LFU, config.getMapConfig("lfuEvictionMap").getEvictionPolicy());
        Assert.assertEquals(LRU, config.getMapConfig("lruEvictionMap").getEvictionPolicy());
        Assert.assertEquals(NONE, config.getMapConfig("noneEvictionMap").getEvictionPolicy());
        Assert.assertEquals(RANDOM, config.getMapConfig("randomEvictionMap").getEvictionPolicy());
    }

    @Test
    public void testMemberNearCacheEvictionPolicies() {
        Assert.assertEquals(LFU, getNearCacheEvictionPolicy("lfuNearCacheEvictionMap", config));
        Assert.assertEquals(LRU, getNearCacheEvictionPolicy("lruNearCacheEvictionMap", config));
        Assert.assertEquals(NONE, getNearCacheEvictionPolicy("noneNearCacheEvictionMap", config));
        Assert.assertEquals(RANDOM, getNearCacheEvictionPolicy("randomNearCacheEvictionMap", config));
    }

    @Test
    public void testMapEvictionPolicyClassName() {
        MapConfig mapConfig = config.getMapConfig("mapWithMapEvictionPolicyClassName");
        String expectedComparatorClassName = "com.hazelcast.map.eviction.LRUEvictionPolicy";
        Assert.assertEquals(expectedComparatorClassName, mapConfig.getMapEvictionPolicy().getClass().getName());
    }

    @Test
    public void testMapEvictionPolicyImpl() {
        MapConfig mapConfig = config.getMapConfig("mapWithMapEvictionPolicyImpl");
        Assert.assertEquals(DummyMapEvictionPolicy.class, mapConfig.getMapEvictionPolicy().getClass());
    }

    @Test
    public void testWhenBothMapEvictionPolicyClassNameAndEvictionPolicySet() {
        MapConfig mapConfig = config.getMapConfig("mapBothMapEvictionPolicyClassNameAndEvictionPolicy");
        String expectedComparatorClassName = "com.hazelcast.map.eviction.LRUEvictionPolicy";
        Assert.assertEquals(expectedComparatorClassName, mapConfig.getMapEvictionPolicy().getClass().getName());
    }

    @Test
    public void testMapEventJournalConfigIsWellParsed() {
        EventJournalConfig journalConfig = config.getMapEventJournalConfig("mapName");
        Assert.assertTrue(journalConfig.isEnabled());
        Assert.assertEquals(123, journalConfig.getCapacity());
        Assert.assertEquals(321, journalConfig.getTimeToLiveSeconds());
    }

    @Test
    public void testCacheEventJournalConfigIsWellParsed() {
        EventJournalConfig journalConfig = config.getCacheEventJournalConfig("cacheName");
        Assert.assertTrue(journalConfig.isEnabled());
        Assert.assertEquals(123, journalConfig.getCapacity());
        Assert.assertEquals(321, journalConfig.getTimeToLiveSeconds());
    }

    @Test
    public void testMapMerkleTreeConfigIsWellParsed() {
        MerkleTreeConfig treeConfig = config.getMapMerkleTreeConfig("mapName");
        Assert.assertTrue(treeConfig.isEnabled());
        Assert.assertEquals(15, treeConfig.getDepth());
    }

    @Test
    public void testExplicitPortCountConfiguration() {
        int portCount = instance.getConfig().getNetworkConfig().getPortCount();
        Assert.assertEquals(42, portCount);
    }

    @Test
    public void testJavaSerializationFilterConfig() {
        JavaSerializationFilterConfig filterConfig = config.getSerializationConfig().getJavaSerializationFilterConfig();
        Assert.assertNotNull(filterConfig);
        Assert.assertTrue(filterConfig.isDefaultsDisabled());
        ClassFilter blacklist = filterConfig.getBlacklist();
        Assert.assertNotNull(blacklist);
        Assert.assertEquals(1, blacklist.getClasses().size());
        Assert.assertTrue(blacklist.getClasses().contains("com.acme.app.BeanComparator"));
        Assert.assertEquals(0, blacklist.getPackages().size());
        Set<String> prefixes = blacklist.getPrefixes();
        Assert.assertTrue(prefixes.contains("a.dangerous.package."));
        Assert.assertTrue(prefixes.contains("justaprefix"));
        Assert.assertEquals(2, prefixes.size());
        ClassFilter whitelist = filterConfig.getWhitelist();
        Assert.assertNotNull(whitelist);
        Assert.assertEquals(2, whitelist.getClasses().size());
        Assert.assertTrue(whitelist.getClasses().contains("java.lang.String"));
        Assert.assertTrue(whitelist.getClasses().contains("example.Foo"));
        Assert.assertEquals(2, whitelist.getPackages().size());
        Assert.assertTrue(whitelist.getPackages().contains("com.acme.app"));
        Assert.assertTrue(whitelist.getPackages().contains("com.acme.app.subpkg"));
    }

    @Test
    public void testRestApiConfig() {
        RestApiConfig restApiConfig = config.getNetworkConfig().getRestApiConfig();
        Assert.assertNotNull(restApiConfig);
        Assert.assertFalse(restApiConfig.isEnabled());
        for (RestEndpointGroup group : RestEndpointGroup.values()) {
            Assert.assertTrue(("Unexpected status of REST Endpoint group" + group), restApiConfig.isGroupEnabled(group));
        }
    }

    @Test
    public void testMemcacheProtocolConfig() {
        MemcacheProtocolConfig memcacheProtocolConfig = config.getNetworkConfig().getMemcacheProtocolConfig();
        Assert.assertNotNull(memcacheProtocolConfig);
        Assert.assertTrue(memcacheProtocolConfig.isEnabled());
    }
}

