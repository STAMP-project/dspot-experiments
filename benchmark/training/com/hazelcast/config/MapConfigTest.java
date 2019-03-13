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


import CacheDeserializedValues.ALWAYS;
import CacheDeserializedValues.INDEX_ONLY;
import CacheDeserializedValues.NEVER;
import EvictionPolicy.LRU;
import MapConfig.DEFAULT_BACKUP_COUNT;
import MapConfig.DEFAULT_EVICTION_PERCENTAGE;
import MapConfig.DEFAULT_EVICTION_POLICY;
import MapConfig.DEFAULT_MAX_IDLE_SECONDS;
import MapConfig.DEFAULT_MIN_EVICTION_CHECK_MILLIS;
import MapConfig.DEFAULT_TTL_SECONDS;
import MapStoreConfig.InitialLoadMode.EAGER;
import MaxSizeConfig.DEFAULT_MAX_SIZE;
import MaxSizeConfig.MaxSizePolicy;
import Warning.NONFINAL_FIELDS;
import Warning.NULL_FIELDS;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.spi.merge.PassThroughMergePolicy;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.EventListener;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static InMemoryFormat.BINARY;
import static InMemoryFormat.OBJECT;
import static MapConfig.MAX_EVICTION_PERCENTAGE;
import static MapConfig.MIN_BACKUP_COUNT;
import static MapConfig.MIN_EVICTION_PERCENTAGE;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapConfigTest {
    @Test
    public void testGetName() {
        Assert.assertNull(new MapConfig().getName());
    }

    @Test
    public void testSetName() {
        Assert.assertEquals("map-test-name", new MapConfig().setName("map-test-name").getName());
    }

    @Test
    public void testGetBackupCount() {
        Assert.assertEquals(DEFAULT_BACKUP_COUNT, new MapConfig().getBackupCount());
    }

    @Test
    public void testSetBackupCount() {
        Assert.assertEquals(0, new MapConfig().setBackupCount(0).getBackupCount());
        Assert.assertEquals(1, new MapConfig().setBackupCount(1).getBackupCount());
        Assert.assertEquals(2, new MapConfig().setBackupCount(2).getBackupCount());
        Assert.assertEquals(3, new MapConfig().setBackupCount(3).getBackupCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBackupCountLowerLimit() {
        new MapConfig().setBackupCount(((MIN_BACKUP_COUNT) - 1));
    }

    @Test
    public void testGetEvictionPercentage() {
        Assert.assertEquals(DEFAULT_EVICTION_PERCENTAGE, new MapConfig().getEvictionPercentage());
    }

    @Test
    public void testMinEvictionCheckMillis() {
        Assert.assertEquals(DEFAULT_MIN_EVICTION_CHECK_MILLIS, new MapConfig().getMinEvictionCheckMillis());
    }

    @Test
    public void testSetEvictionPercentage() {
        Assert.assertEquals(50, new MapConfig().setEvictionPercentage(50).getEvictionPercentage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEvictionPercentageLowerLimit() {
        new MapConfig().setEvictionPercentage(((MIN_EVICTION_PERCENTAGE) - 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEvictionPercentageUpperLimit() {
        new MapConfig().setEvictionPercentage(((MAX_EVICTION_PERCENTAGE) + 1));
    }

    @Test
    public void testGetTimeToLiveSeconds() {
        Assert.assertEquals(DEFAULT_TTL_SECONDS, new MapConfig().getTimeToLiveSeconds());
    }

    @Test
    public void testSetTimeToLiveSeconds() {
        Assert.assertEquals(1234, new MapConfig().setTimeToLiveSeconds(1234).getTimeToLiveSeconds());
    }

    @Test
    public void testGetMaxIdleSeconds() {
        Assert.assertEquals(DEFAULT_MAX_IDLE_SECONDS, new MapConfig().getMaxIdleSeconds());
    }

    @Test
    public void testSetMaxIdleSeconds() {
        Assert.assertEquals(1234, new MapConfig().setMaxIdleSeconds(1234).getMaxIdleSeconds());
    }

    @Test
    public void testGetMaxSize() {
        Assert.assertEquals(DEFAULT_MAX_SIZE, new MapConfig().getMaxSizeConfig().getSize());
    }

    @Test
    public void testSetMaxSize() {
        Assert.assertEquals(1234, new MapConfig().getMaxSizeConfig().setSize(1234).getSize());
    }

    @Test
    public void testSetMaxSizeMustBePositive() {
        Assert.assertTrue(((new MapConfig().getMaxSizeConfig().setSize((-1)).getSize()) > 0));
    }

    @Test
    public void testGetEvictionPolicy() {
        Assert.assertEquals(DEFAULT_EVICTION_POLICY, new MapConfig().getEvictionPolicy());
    }

    @Test
    public void testSetEvictionPolicy() {
        Assert.assertEquals(LRU, new MapConfig().setEvictionPolicy(LRU).getEvictionPolicy());
    }

    @Test
    public void testGetMapStoreConfig() {
        MapStoreConfig mapStoreConfig = new MapConfig().getMapStoreConfig();
        Assert.assertNotNull(mapStoreConfig);
        Assert.assertFalse(mapStoreConfig.isEnabled());
    }

    @Test
    public void testSetMapStoreConfig() {
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        Assert.assertEquals(mapStoreConfig, new MapConfig().setMapStoreConfig(mapStoreConfig).getMapStoreConfig());
    }

    @Test
    public void testGetNearCacheConfig() {
        Assert.assertNull(new MapConfig().getNearCacheConfig());
    }

    @Test
    public void testSetNearCacheConfig() {
        NearCacheConfig nearCacheConfig = new NearCacheConfig();
        Assert.assertEquals(nearCacheConfig, new MapConfig().setNearCacheConfig(nearCacheConfig).getNearCacheConfig());
    }

    @Test
    public void configSetsForDefaultAlwaysIssue466() {
        Config config = new XmlConfigBuilder().build();
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setWriteDelaySeconds(0);
        mapStoreConfig.setClassName("com.hazelcast.examples.DummyStore");
        config.getMapConfig("test").setMapStoreConfig(mapStoreConfig);
        config.getMapConfig("default").setMapStoreConfig(null);
        Assert.assertNotNull(config.getMapConfig("test").getMapStoreConfig());
        Assert.assertNull(config.getMapConfig("default").getMapStoreConfig());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAsyncBackupCount_whenItsNegative() {
        MapConfig config = new MapConfig();
        config.setAsyncBackupCount((-1));
    }

    @Test
    public void setAsyncBackupCount_whenItsZero() {
        MapConfig config = new MapConfig();
        config.setAsyncBackupCount(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAsyncBackupCount_whenTooLarge() {
        MapConfig config = new MapConfig();
        // max allowed is 6
        config.setAsyncBackupCount(200);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBackupCount_whenItsNegative() {
        MapConfig config = new MapConfig();
        config.setBackupCount((-1));
    }

    @Test
    public void setBackupCount_whenItsZero() {
        MapConfig config = new MapConfig();
        config.setBackupCount(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBackupCount_tooLarge() {
        MapConfig config = new MapConfig();
        // max allowed is 6
        config.setBackupCount(200);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyMapStoreConfigSetWriteBatchSize() {
        setWriteBatchSize(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyMapStoreConfigSetInitialLoadMode() {
        new MapStoreConfigReadOnly(new MapStoreConfig()).setInitialLoadMode(EAGER);
    }

    @Test
    public void testMapPartitionLostListenerConfig() {
        MapConfig mapConfig = new MapConfig();
        MapPartitionLostListener listener = Mockito.mock(MapPartitionLostListener.class);
        mapConfig.addMapPartitionLostListenerConfig(new MapPartitionLostListenerConfig(listener));
        MapPartitionLostListenerConfig listenerConfig = new MapPartitionLostListenerConfig();
        listenerConfig.setImplementation(listener);
        mapConfig.addMapPartitionLostListenerConfig(listenerConfig);
        List<MapPartitionLostListenerConfig> listenerConfigs = mapConfig.getPartitionLostListenerConfigs();
        Assert.assertEquals(2, listenerConfigs.size());
        Assert.assertEquals(listener, listenerConfigs.get(0).getImplementation());
        Assert.assertEquals(listener, listenerConfigs.get(1).getImplementation());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMapPartitionLostListenerReadOnlyConfig_withClassName() {
        MapPartitionLostListenerConfigReadOnly readOnly = new MapPartitionLostListenerConfig().getAsReadOnly();
        readOnly.setClassName("com.hz");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMapPartitionLostListenerReadOnlyConfig_withImplementation() {
        MapPartitionLostListener listener = Mockito.mock(MapPartitionLostListener.class);
        MapPartitionLostListenerConfig listenerConfig = new MapPartitionLostListenerConfig(listener);
        MapPartitionLostListenerConfigReadOnly readOnly = listenerConfig.getAsReadOnly();
        Assert.assertEquals(listener, readOnly.getImplementation());
        readOnly.setImplementation(Mockito.mock(MapPartitionLostListener.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMapPartitionLostListenerReadOnlyConfig_withEventListenerImplementation() {
        MapPartitionLostListenerConfigReadOnly readOnly = new MapPartitionLostListenerConfig().getAsReadOnly();
        readOnly.setImplementation(Mockito.mock(EventListener.class));
    }

    @Test
    public void testMapPartitionLostListener_equalsWithClassName() {
        MapPartitionLostListenerConfig config1 = new MapPartitionLostListenerConfig();
        config1.setClassName("com.hz");
        MapPartitionLostListenerConfig config2 = new MapPartitionLostListenerConfig();
        config2.setClassName("com.hz");
        MapPartitionLostListenerConfig config3 = new MapPartitionLostListenerConfig();
        config3.setClassName("com.hz2");
        Assert.assertEquals(config1, config2);
        Assert.assertNotEquals(config1, config3);
        Assert.assertNotEquals(config2, config3);
    }

    @Test
    public void testMapPartitionLostListener_equalsWithImplementation() {
        MapPartitionLostListener listener = Mockito.mock(MapPartitionLostListener.class);
        MapPartitionLostListenerConfig config1 = new MapPartitionLostListenerConfig();
        config1.setImplementation(listener);
        MapPartitionLostListenerConfig config2 = new MapPartitionLostListenerConfig();
        config2.setImplementation(listener);
        MapPartitionLostListenerConfig config3 = new MapPartitionLostListenerConfig();
        Assert.assertEquals(config1, config2);
        Assert.assertNotEquals(config1, config3);
        Assert.assertNotEquals(config2, config3);
    }

    @Test(expected = ConfigurationException.class)
    public void givenCacheDeserializedValuesSetToALWAYS_whenSetOptimizeQueriesToFalse_thenThrowConfigurationException() {
        // given
        MapConfig mapConfig = new MapConfig();
        mapConfig.setCacheDeserializedValues(ALWAYS);
        // when
        mapConfig.setOptimizeQueries(false);
    }

    @Test(expected = ConfigurationException.class)
    public void givenCacheDeserializedValuesSetToNEVER_whenSetOptimizeQueriesToTrue_thenThrowConfigurationException() {
        // given
        MapConfig mapConfig = new MapConfig();
        mapConfig.setCacheDeserializedValues(NEVER);
        // when
        mapConfig.setOptimizeQueries(true);
    }

    @Test
    public void givenCacheDeserializedValuesIsDefault_whenSetOptimizeQueriesToTrue_thenSetCacheDeserializedValuesToALWAYS() {
        // given
        MapConfig mapConfig = new MapConfig();
        // when
        mapConfig.setOptimizeQueries(true);
        // then
        CacheDeserializedValues cacheDeserializedValues = mapConfig.getCacheDeserializedValues();
        Assert.assertEquals(ALWAYS, cacheDeserializedValues);
    }

    @Test
    public void givenCacheDeserializedValuesIsDefault_thenIsOptimizeQueriesReturnFalse() {
        // given
        MapConfig mapConfig = new MapConfig();
        // then
        boolean optimizeQueries = mapConfig.isOptimizeQueries();
        Assert.assertFalse(optimizeQueries);
    }

    @Test
    public void givenCacheDeserializedValuesIsDefault_whenSetCacheDeserializedValuesToALWAYS_thenIsOptimizeQueriesReturnTrue() {
        // given
        MapConfig mapConfig = new MapConfig();
        // when
        mapConfig.setCacheDeserializedValues(ALWAYS);
        // then
        boolean optimizeQueries = mapConfig.isOptimizeQueries();
        Assert.assertTrue(optimizeQueries);
    }

    @Test
    public void givenCacheDeserializedValuesIsDefault_whenSetCacheDeserializedValuesToNEVER_thenIsOptimizeQueriesReturnFalse() {
        // given
        MapConfig mapConfig = new MapConfig();
        // when
        mapConfig.setCacheDeserializedValues(NEVER);
        // then
        boolean optimizeQueries = mapConfig.isOptimizeQueries();
        Assert.assertFalse(optimizeQueries);
    }

    @Test(expected = ConfigurationException.class)
    public void givenSetOptimizeQueryIsTrue_whenSetCacheDeserializedValuesToNEVER_thenThrowConfigurationException() {
        // given
        MapConfig mapConfig = new MapConfig();
        mapConfig.setOptimizeQueries(true);
        // when
        mapConfig.setCacheDeserializedValues(NEVER);
    }

    @Test(expected = ConfigurationException.class)
    public void givenSetOptimizeQueryIsFalse_whenSetCacheDeserializedValuesToALWAYS_thenThrowConfigurationException() {
        // given
        MapConfig mapConfig = new MapConfig();
        mapConfig.setOptimizeQueries(false);
        // when
        mapConfig.setCacheDeserializedValues(ALWAYS);
    }

    @Test(expected = ConfigurationException.class)
    public void givenSetOptimizeQueryIsTrue_whenSetCacheDeserializedValuesToINDEX_ONLY_thenThrowConfigurationException() {
        // given
        MapConfig mapConfig = new MapConfig();
        mapConfig.setOptimizeQueries(true);
        // when
        mapConfig.setCacheDeserializedValues(INDEX_ONLY);
    }

    @Test
    public void givenDefaultConfig_whenSerializedAndDeserialized_noExceptionIsThrown() {
        MapConfig mapConfig = new MapConfig();
        InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Data data = serializationService.toData(mapConfig);
        serializationService.toObject(data);
    }

    @Test
    public void testSetMergePolicyConfig() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(PassThroughMergePolicy.class.getName()).setBatchSize(2342);
        MapConfig config = new MapConfig();
        config.setMergePolicyConfig(mergePolicyConfig);
        Assert.assertEquals(PassThroughMergePolicy.class.getName(), config.getMergePolicyConfig().getPolicy());
        Assert.assertEquals(2342, config.getMergePolicyConfig().getBatchSize());
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(MapConfig.class).allFieldsShouldBeUsedExcept("readOnly").suppress(NULL_FIELDS, NONFINAL_FIELDS).withPrefabValues(MaxSizeConfig.class, new MaxSizeConfig(300, MaxSizePolicy.PER_PARTITION), new MaxSizeConfig(100, MaxSizePolicy.PER_NODE)).withPrefabValues(MapStoreConfig.class, new MapStoreConfig().setEnabled(true).setClassName("red"), new MapStoreConfig().setEnabled(true).setClassName("black")).withPrefabValues(NearCacheConfig.class, new NearCacheConfig(10, 20, false, BINARY), new NearCacheConfig(15, 25, true, OBJECT)).withPrefabValues(WanReplicationRef.class, new WanReplicationRef().setName("red"), new WanReplicationRef().setName("black")).withPrefabValues(PartitioningStrategyConfig.class, new PartitioningStrategyConfig("red"), new PartitioningStrategyConfig("black")).withPrefabValues(MapConfigReadOnly.class, new MapConfigReadOnly(new MapConfig("red")), new MapConfigReadOnly(new MapConfig("black"))).withPrefabValues(MergePolicyConfig.class, new MergePolicyConfig(PutIfAbsentMergePolicy.class.getName(), 100), new MergePolicyConfig(DiscardMergePolicy.class.getName(), 200)).verify();
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testDefaultHashCode() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.hashCode();
    }
}

