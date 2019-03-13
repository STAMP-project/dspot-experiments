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
package com.hazelcast.internal.dynamicconfig;


import InMemoryFormat.NATIVE;
import TimedExpiryPolicyFactoryConfig.ExpiryPolicyType;
import TopicOverloadPolicy.DISCARD_OLDEST;
import com.hazelcast.cache.impl.event.CachePartitionLostEvent;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.internal.eviction.EvictableEntryView;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.map.MapPartitionLostEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DynamicConfigTest extends HazelcastTestSupport {
    protected static final int INSTANCE_COUNT = 2;

    protected static final String NON_DEFAULT_MERGE_POLICY = "AnotherMergePolicy";

    protected static final int NON_DEFAULT_MERGE_BATCH_SIZE = 31415;

    private String name = HazelcastTestSupport.randomString();

    private HazelcastInstance[] members;

    // add***Config is invoked on driver instance
    private HazelcastInstance driver;

    @Test
    public void testMultiMapConfig() {
        MultiMapConfig multiMapConfig = new MultiMapConfig(name).setBackupCount(4).setAsyncBackupCount(2).setStatisticsEnabled(true).setBinary(true).setValueCollectionType(ValueCollectionType.LIST).addEntryListenerConfig(new EntryListenerConfig("com.hazelcast.Listener", true, false));
        driver.getConfig().addMultiMapConfig(multiMapConfig);
        assertConfigurationsEqualsOnAllMembers(multiMapConfig);
    }

    @Test
    public void testMultiMapConfig_whenEntryListenerConfigHasImplementation() {
        MultiMapConfig multiMapConfig = new MultiMapConfig(name).setBackupCount(4).setAsyncBackupCount(2).setStatisticsEnabled(true).setBinary(true).setValueCollectionType(ValueCollectionType.LIST).setMergePolicyConfig(new MergePolicyConfig(DynamicConfigTest.NON_DEFAULT_MERGE_POLICY, DynamicConfigTest.NON_DEFAULT_MERGE_BATCH_SIZE)).addEntryListenerConfig(new EntryListenerConfig(new DynamicConfigTest.SampleEntryListener(), true, false));
        driver.getConfig().addMultiMapConfig(multiMapConfig);
        assertConfigurationsEqualsOnAllMembers(multiMapConfig);
    }

    @Test
    public void testCardinalityEstimatorConfig() {
        CardinalityEstimatorConfig config = new CardinalityEstimatorConfig(name, 4, 2).setMergePolicyConfig(new MergePolicyConfig("com.hazelcast.spi.merge.DiscardMergePolicy", 20));
        driver.getConfig().addCardinalityEstimatorConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testLockConfig() {
        LockConfig config = new LockConfig(name);
        config.setQuorumName(HazelcastTestSupport.randomString());
        driver.getConfig().addLockConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testListConfig() {
        ListConfig config = getListConfig();
        driver.getConfig().addListConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testListConfig_withItemListenerConfigs() {
        ListConfig config = getListConfig();
        config.addItemListenerConfig(getItemListenerConfig_byClassName());
        config.addItemListenerConfig(getItemListenerConfig_byImplementation());
        driver.getConfig().addListConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testExecutorConfig() {
        ExecutorConfig config = new ExecutorConfig(name, 7).setStatisticsEnabled(true).setQueueCapacity(13);
        driver.getConfig().addExecutorConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testDurableExecutorConfig() {
        DurableExecutorConfig config = new DurableExecutorConfig(name, 7, 3, 10);
        driver.getConfig().addDurableExecutorConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testScheduledExecutorConfig() {
        ScheduledExecutorConfig config = new ScheduledExecutorConfig(name, 2, 3, 10, null, new MergePolicyConfig(DynamicConfigTest.NON_DEFAULT_MERGE_POLICY, DynamicConfigTest.NON_DEFAULT_MERGE_BATCH_SIZE));
        driver.getConfig().addScheduledExecutorConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testRingbufferConfig() {
        RingbufferConfig config = getRingbufferConfig();
        driver.getConfig().addRingBufferConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testRingbufferConfig_whenConfiguredWithRingbufferStore_byClassName() {
        RingbufferConfig config = getRingbufferConfig();
        config.getRingbufferStoreConfig().setEnabled(true).setClassName("com.hazelcast.Foo");
        driver.getConfig().addRingBufferConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testRingbufferConfig_whenConfiguredWithRingbufferStore_byFactoryClassName() {
        RingbufferConfig config = getRingbufferConfig();
        config.getRingbufferStoreConfig().setEnabled(true).setFactoryClassName("com.hazelcast.FactoryFoo");
        driver.getConfig().addRingBufferConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testRingbufferConfig_whenConfiguredWithRingbufferStore_byStoreImplementation() {
        RingbufferConfig config = getRingbufferConfig();
        config.getRingbufferStoreConfig().setEnabled(true).setStoreImplementation(new DynamicConfigTest.SampleRingbufferStore());
        driver.getConfig().addRingBufferConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testRingbufferConfig_whenConfiguredWithRingbufferStore_byFactoryImplementation() {
        RingbufferConfig config = getRingbufferConfig();
        config.getRingbufferStoreConfig().setEnabled(true).setFactoryImplementation(new DynamicConfigTest.SampleRingbufferStoreFactory());
        driver.getConfig().addRingBufferConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testQueueConfig() {
        QueueConfig config = getQueueConfig();
        driver.getConfig().addQueueConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testQueueConfig_withListeners() {
        QueueConfig config = getQueueConfig_withListeners();
        driver.getConfig().addQueueConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testReplicatedMapDefaultConfig() {
        ReplicatedMapConfig config = new ReplicatedMapConfig(name);
        driver.getConfig().addReplicatedMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testReplicatedMapConfig_withNonDefaultMergePolicy() {
        ReplicatedMapConfig config = new ReplicatedMapConfig(name).setMergePolicyConfig(new MergePolicyConfig(DynamicConfigTest.NON_DEFAULT_MERGE_POLICY, DynamicConfigTest.NON_DEFAULT_MERGE_BATCH_SIZE));
        driver.getConfig().addReplicatedMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testSameReplicatedMapConfig_canBeAddedTwice() {
        ReplicatedMapConfig config = new ReplicatedMapConfig(name);
        driver.getConfig().addReplicatedMapConfig(config);
        ReplicatedMap map = driver.getReplicatedMap(name);
        driver.getConfig().addReplicatedMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testReplicatedMapConfig_withListenerByClassName() {
        ReplicatedMapConfig config = new ReplicatedMapConfig(name).setStatisticsEnabled(true).setMergePolicy("com.hazelcast.SomeMergePolicy").setInMemoryFormat(NATIVE).addEntryListenerConfig(new EntryListenerConfig(HazelcastTestSupport.randomString(), true, false));
        config.setAsyncFillup(true);
        driver.getConfig().addReplicatedMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testReplicatedMapConfig_withListenerByImplementation() {
        ReplicatedMapConfig config = new ReplicatedMapConfig(name).setStatisticsEnabled(true).setMergePolicy("com.hazelcast.SomeMergePolicy").setInMemoryFormat(NATIVE).addEntryListenerConfig(new EntryListenerConfig(new DynamicConfigTest.SampleEntryListener(), false, true));
        config.setAsyncFillup(true);
        driver.getConfig().addReplicatedMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testSetConfig() {
        SetConfig setConfig = getSetConfig(name);
        driver.getConfig().addSetConfig(setConfig);
        assertConfigurationsEqualsOnAllMembers(setConfig);
    }

    @Test
    public void testMapConfig() {
        MapConfig config = getMapConfig();
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testDefaultMapConfig() {
        MapConfig config = new MapConfig(name);
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withEntryListenerImplementation() {
        MapConfig config = getMapConfig_withEntryListenerImplementation();
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withEntryListenerClassName() {
        MapConfig config = getMapConfig_withEntryListenerClassName();
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withQueryCacheConfig() {
        MapConfig config = getMapConfig_withQueryCacheConfig();
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withQueryCacheConfig_andEntryListenerConfigByClassName() {
        MapConfig config = getMapConfig_withQueryCacheConfig();
        config.getQueryCacheConfigs().get(0).addEntryListenerConfig(entryListenerConfigWithClassName());
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withQueryCacheConfig_andEntryListenerConfigByImplementation() {
        MapConfig config = getMapConfig_withQueryCacheConfig();
        config.getQueryCacheConfigs().get(0).addEntryListenerConfig(entryListenerConfigWithImplementation());
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withMapPartitionLostListener_byClassName() {
        MapConfig config = getMapConfig();
        config.addMapPartitionLostListenerConfig(getMapPartitionLostListenerConfig_byClassName());
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testMapConfig_withMapPartitionLostListener_byImplementation() {
        MapConfig config = getMapConfig().addMapPartitionLostListenerConfig(getMapPartitionLostListenerConfig_byImplementation());
        driver.getConfig().addMapConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testSetConfig_whenItemListenersConfigured() {
        SetConfig setConfig = getSetConfig(name);
        setConfig.addItemListenerConfig(getItemListenerConfig_byImplementation());
        setConfig.addItemListenerConfig(getItemListenerConfig_byClassName());
        driver.getConfig().addSetConfig(setConfig);
        assertConfigurationsEqualsOnAllMembers(setConfig);
    }

    @Test
    public void testDefaultCacheConfig() {
        CacheSimpleConfig config = new CacheSimpleConfig().setName(name);
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testCacheConfig() {
        CacheSimpleConfig config = getCacheConfig().setExpiryPolicyFactoryConfig(new ExpiryPolicyFactoryConfig("expiryPolicyFactory"));
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testCacheConfig_withEvictionPolicy_cacheLoaderAndWriter() {
        CacheSimpleConfig config = // also exercise alternative method to set expiry policy factory config
        getCacheConfig().setEvictionConfig(getEvictionConfigByPolicy()).setCacheLoader("com.hazelcast.CacheLoader").setCacheWriter("com.hazelcast.CacheWriter").setExpiryPolicyFactory("expiryPolicyFactory");
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testCacheConfig_withEvictionPolicyImplementation_cacheLoaderAndWriterFactory() {
        CacheSimpleConfig config = getCacheConfig().setEvictionConfig(getEvictionConfigByImplementation()).setCacheLoaderFactory("com.hazelcast.CacheLoaderFactory").setCacheWriterFactory("com.hazelcast.CacheWriterFactory");
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testCacheConfig_withTimedExpiryPolicyFactory() {
        CacheSimpleConfig config = getCacheConfig().setExpiryPolicyFactoryConfig(new ExpiryPolicyFactoryConfig(new com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig(ExpiryPolicyType.TOUCHED, new ExpiryPolicyFactoryConfig.DurationConfig(130, TimeUnit.SECONDS))));
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testCacheConfig_withPartitionLostListenerByClassName() {
        CacheSimpleConfig config = getCacheConfig().addCachePartitionLostListenerConfig(new CachePartitionLostListenerConfig("partitionLostListener"));
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testCacheConfig_withPartitionLostListenerByImplementation() {
        CacheSimpleConfig config = getCacheConfig().addCachePartitionLostListenerConfig(new CachePartitionLostListenerConfig(new DynamicConfigTest.SampleCachePartitionLostListener()));
        driver.getConfig().addCacheConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testEventJournalConfig() {
        EventJournalConfig cacheJournalConfig = new EventJournalConfig().setEnabled(true).setCacheName(HazelcastTestSupport.randomName()).setCapacity(39).setTimeToLiveSeconds(98);
        EventJournalConfig mapJournalConfig = new EventJournalConfig().setEnabled(true).setMapName(HazelcastTestSupport.randomName()).setCapacity(42).setTimeToLiveSeconds(52);
        driver.getConfig().addEventJournalConfig(cacheJournalConfig);
        driver.getConfig().addEventJournalConfig(mapJournalConfig);
        assertConfigurationsEqualsOnAllMembers(mapJournalConfig, cacheJournalConfig);
    }

    @Test
    public void testFlakeIdGeneratorConfig() {
        FlakeIdGeneratorConfig config = new FlakeIdGeneratorConfig(HazelcastTestSupport.randomName()).setPrefetchCount(123).setPrefetchValidityMillis(456).setIdOffset(789).setNodeIdOffset(890).setStatisticsEnabled(false);
        driver.getConfig().addFlakeIdGeneratorConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testSemaphoreConfig() {
        SemaphoreConfig semaphoreConfig = new SemaphoreConfig().setName(name).setInitialPermits(33).setAsyncBackupCount(4).setBackupCount(2);
        driver.getConfig().addSemaphoreConfig(semaphoreConfig);
        assertConfigurationsEqualsOnAllMembers(semaphoreConfig);
    }

    @Test
    public void testTopicConfig() {
        TopicConfig topicConfig = new TopicConfig(name).setGlobalOrderingEnabled(true).setMultiThreadingEnabled(false).setStatisticsEnabled(true);
        driver.getConfig().addTopicConfig(topicConfig);
        assertConfigurationsEqualsOnAllMembers(topicConfig);
    }

    @Test
    public void testTopicConfig_whenListenerConfigByClassName() {
        TopicConfig topicConfig = new TopicConfig(name).setGlobalOrderingEnabled(false).setMultiThreadingEnabled(true).setStatisticsEnabled(true).addMessageListenerConfig(getListenerConfig_byClassName());
        driver.getConfig().addTopicConfig(topicConfig);
        assertConfigurationsEqualsOnAllMembers(topicConfig);
    }

    @Test
    public void testTopicConfig_whenListenerConfigByImplementation() {
        TopicConfig topicConfig = new TopicConfig(name).setGlobalOrderingEnabled(false).setMultiThreadingEnabled(true).setStatisticsEnabled(true).addMessageListenerConfig(getMessageListenerConfig_byImplementation());
        driver.getConfig().addTopicConfig(topicConfig);
        assertConfigurationsEqualsOnAllMembers(topicConfig);
    }

    @Test
    public void testReliableTopicConfig() {
        ReliableTopicConfig reliableTopicConfig = new ReliableTopicConfig(name).setTopicOverloadPolicy(DISCARD_OLDEST).setReadBatchSize(42).setStatisticsEnabled(true);
        driver.getConfig().addReliableTopicConfig(reliableTopicConfig);
        assertConfigurationsEqualsOnAllMembers(reliableTopicConfig);
    }

    @Test
    public void testReliableTopicConfig_whenListenerConfigByClassName() {
        ReliableTopicConfig reliableTopicConfig = new ReliableTopicConfig(name).setTopicOverloadPolicy(DISCARD_OLDEST).setReadBatchSize(42).setStatisticsEnabled(true).addMessageListenerConfig(getListenerConfig_byClassName());
        driver.getConfig().addReliableTopicConfig(reliableTopicConfig);
        assertConfigurationsEqualsOnAllMembers(reliableTopicConfig);
    }

    @Test
    public void testReliableTopicConfig_whenListenerConfigByImplementation() {
        ReliableTopicConfig reliableTopicConfig = new ReliableTopicConfig(name).setTopicOverloadPolicy(DISCARD_OLDEST).setReadBatchSize(42).setStatisticsEnabled(true).addMessageListenerConfig(getMessageListenerConfig_byImplementation());
        driver.getConfig().addReliableTopicConfig(reliableTopicConfig);
        assertConfigurationsEqualsOnAllMembers(reliableTopicConfig);
    }

    @Test
    public void testMerkleTreeConfig() {
        MerkleTreeConfig config = new MerkleTreeConfig().setEnabled(true).setMapName(HazelcastTestSupport.randomName()).setDepth(10);
        driver.getConfig().addMerkleTreeConfig(config);
        assertConfigurationsEqualsOnAllMembers(config);
    }

    @Test
    public void testReliableTopicConfig_whenHasExecutor() {
        ReliableTopicConfig reliableTopicConfig = new ReliableTopicConfig(name).setTopicOverloadPolicy(DISCARD_OLDEST).setReadBatchSize(42).setStatisticsEnabled(true).setExecutor(new DynamicConfigTest.SampleExecutor());
        driver.getConfig().addReliableTopicConfig(reliableTopicConfig);
        assertConfigurationsEqualsOnAllMembers(reliableTopicConfig);
    }

    public static class SampleEntryListener implements EntryAddedListener , Serializable {
        @Override
        public void entryAdded(EntryEvent event) {
        }

        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleEntryListener;
        }
    }

    public static class SampleItemListener implements ItemListener , Serializable {
        @Override
        public void itemAdded(ItemEvent item) {
        }

        @Override
        public void itemRemoved(ItemEvent item) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleItemListener;
        }

        @Override
        public int hashCode() {
            return 33;
        }
    }

    public static class SampleRingbufferStore implements RingbufferStore , Serializable {
        @Override
        public void store(long sequence, Object data) {
        }

        @Override
        public void storeAll(long firstItemSequence, Object[] items) {
        }

        @Override
        public Object load(long sequence) {
            return null;
        }

        @Override
        public long getLargestSequence() {
            return 0;
        }

        @Override
        public int hashCode() {
            return 33;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleRingbufferStore;
        }
    }

    public static class SampleRingbufferStoreFactory implements RingbufferStoreFactory , Serializable {
        @Override
        public RingbufferStore newRingbufferStore(String name, Properties properties) {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleRingbufferStoreFactory;
        }
    }

    public static class SamplePartitionLostListener implements MapPartitionLostListener , Serializable {
        @Override
        public void partitionLost(MapPartitionLostEvent event) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SamplePartitionLostListener;
        }
    }

    public static class SampleEvictionPolicyComparator extends EvictionPolicyComparator {
        @Override
        public int compare(EvictableEntryView e1, EvictableEntryView e2) {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleEvictionPolicyComparator;
        }
    }

    public static class SampleCachePartitionLostListener implements CachePartitionLostListener , Serializable {
        @Override
        public void partitionLost(CachePartitionLostEvent event) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleCachePartitionLostListener;
        }
    }

    public static class SampleMessageListener implements MessageListener , Serializable {
        @Override
        public void onMessage(Message message) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleMessageListener;
        }
    }

    public static class SampleExecutor implements Serializable , Executor {
        @Override
        public void execute(Runnable command) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DynamicConfigTest.SampleExecutor;
        }
    }
}

