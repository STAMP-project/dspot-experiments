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


import EvictionPolicy.LFU;
import EvictionPolicy.RANDOM;
import InMemoryFormat.NATIVE;
import NearCacheConfig.LocalUpdatePolicy.CACHE_ON_UPDATE;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static InMemoryFormat.NATIVE;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NearCacheConfigTest {
    private NearCacheConfig config = new NearCacheConfig();

    @Test
    public void testConstructor_withName() {
        config = new NearCacheConfig("foobar");
        Assert.assertEquals("foobar", config.getName());
    }

    @Test
    public void testConstructor_withMultipleParameters() {
        config = new NearCacheConfig(23, 42, true, NATIVE);
        Assert.assertEquals(23, config.getTimeToLiveSeconds());
        Assert.assertEquals(42, config.getMaxIdleSeconds());
        Assert.assertTrue(config.isInvalidateOnChange());
        Assert.assertEquals(NATIVE, config.getInMemoryFormat());
    }

    @Test
    public void testConstructor_withMultipleParametersAndEvictionConfig() {
        EvictionConfig evictionConfig = new EvictionConfig().setEvictionPolicy(LFU).setMaximumSizePolicy(MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE).setSize(66);
        config = new NearCacheConfig(23, 42, true, NATIVE, evictionConfig);
        Assert.assertEquals(23, config.getTimeToLiveSeconds());
        Assert.assertEquals(42, config.getMaxIdleSeconds());
        Assert.assertTrue(config.isInvalidateOnChange());
        Assert.assertEquals(NATIVE, config.getInMemoryFormat());
        Assert.assertEquals(LFU, config.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE, config.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(66, config.getEvictionConfig().getSize());
    }

    @Test
    public void testDeprecatedConstructor_withMultipleParameters() {
        config = new NearCacheConfig(23, 5000, RANDOM.name(), 42, true, NATIVE);
        Assert.assertEquals(23, config.getTimeToLiveSeconds());
        Assert.assertEquals(5000, config.getMaxSize());
        Assert.assertEquals(RANDOM.name(), config.getEvictionPolicy());
        Assert.assertEquals(42, config.getMaxIdleSeconds());
        Assert.assertTrue(config.isInvalidateOnChange());
        Assert.assertEquals(NATIVE, config.getInMemoryFormat());
    }

    @Test
    public void testDeprecatedConstructor_withMultipleParametersAndEvictionConfig() {
        EvictionConfig evictionConfig = new EvictionConfig().setEvictionPolicy(LFU).setMaximumSizePolicy(MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE).setSize(66);
        config = new NearCacheConfig(23, 5000, RANDOM.name(), 42, true, NATIVE, evictionConfig);
        Assert.assertEquals(23, config.getTimeToLiveSeconds());
        Assert.assertEquals(5000, config.getMaxSize());
        Assert.assertEquals(RANDOM.name(), config.getEvictionPolicy());
        Assert.assertEquals(42, config.getMaxIdleSeconds());
        Assert.assertTrue(config.isInvalidateOnChange());
        Assert.assertEquals(NATIVE, config.getInMemoryFormat());
        Assert.assertEquals(LFU, config.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE, config.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(66, config.getEvictionConfig().getSize());
    }

    @Test
    public void testSetInMemoryFormat_withString() {
        config.setInMemoryFormat("NATIVE");
        Assert.assertEquals(NATIVE, config.getInMemoryFormat());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInMemoryFormat_withInvalidString() {
        config.setInMemoryFormat("UNKNOWN");
    }

    @Test(expected = NullPointerException.class)
    public void testSetInMemoryFormat_withString_whenNull() {
        config.setInMemoryFormat(((String) (null)));
    }

    @Test
    public void testIsSerializeKeys_whenEnabled() {
        config.setSerializeKeys(true);
        Assert.assertTrue(config.isSerializeKeys());
    }

    @Test
    public void testIsSerializeKeys_whenDisabled() {
        config.setSerializeKeys(false);
        Assert.assertFalse(config.isSerializeKeys());
    }

    @Test
    public void testIsSerializeKeys_whenNativeMemoryFormat_thenAlwaysReturnTrue() {
        config.setSerializeKeys(false);
        config.setInMemoryFormat(NATIVE);
        Assert.assertTrue(config.isSerializeKeys());
    }

    @Test
    public void testMaxSize_whenValueIsZero_thenSetIntegerMax() {
        config.setMaxSize(0);
        Assert.assertEquals(Integer.MAX_VALUE, config.getMaxSize());
    }

    @Test
    public void testMaxSize_whenValueIsPositive_thenSetValue() {
        config.setMaxSize(4531);
        Assert.assertEquals(4531, config.getMaxSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxSize_whenValueIsNegative_thenThrowException() {
        config.setMaxSize((-1));
    }

    @Test(expected = NullPointerException.class)
    public void testSetEvictionConfig_whenNull_thenThrowException() {
        config.setEvictionConfig(null);
    }

    @Test
    public void testEvictionConversion_whenMaxSizeAndEvictionPolicyIsSet_thenEvictionIsConfigured() {
        config.setMaxSize(123);
        config.setEvictionPolicy("LFU");
        EvictionConfig evictionConfig = config.getEvictionConfig();
        Assert.assertEquals(123, evictionConfig.getSize());
        Assert.assertEquals(LFU, evictionConfig.getEvictionPolicy());
        Assert.assertEquals(EvictionPolicyType.LFU, evictionConfig.getEvictionPolicyType());
        Assert.assertEquals(MaxSizePolicy.ENTRY_COUNT, evictionConfig.getMaximumSizePolicy());
    }

    @Test
    public void testEvictionConversion_whenEvictionIsSet_thenMaxSizeAndEvictionPolicyIsNotConfigured() {
        EvictionConfig evictionConfig = new EvictionConfig().setSize(4453).setEvictionPolicy(LFU).setMaximumSizePolicy(MaxSizePolicy.ENTRY_COUNT);
        config.setEvictionConfig(evictionConfig);
        Assert.assertNotEquals(4453, config.getMaxSize());
        Assert.assertNotEquals("LFU", config.getEvictionPolicy());
    }

    @Test
    public void testEvictionConversion_whenExistingEvictionIsModified_thenMaxSizeAndEvictionPolicyIsNotConfigured() {
        config.getEvictionConfig().setSize(15125).setEvictionPolicy(LFU).setMaximumSizePolicy(MaxSizePolicy.ENTRY_COUNT);
        Assert.assertNotEquals(15125, config.getMaxSize());
        Assert.assertNotEquals("LFU", config.getEvictionPolicy());
    }

    @Test
    public void testSetNearCachePreloaderConfig() {
        NearCachePreloaderConfig preloaderConfig = new NearCachePreloaderConfig();
        config.setPreloaderConfig(preloaderConfig);
        Assert.assertEquals(preloaderConfig, config.getPreloaderConfig());
    }

    @Test(expected = NullPointerException.class)
    public void testSetNearCachePreloaderConfig_whenNull_thenThrowException() {
        config.setPreloaderConfig(null);
    }

    @Test
    public void testSerialization() {
        config.setInvalidateOnChange(true);
        config.setCacheLocalEntries(true);
        config.setName("foobar");
        config.setInMemoryFormat(NATIVE);
        config.setTimeToLiveSeconds(23);
        config.setMaxIdleSeconds(42);
        config.setLocalUpdatePolicy(CACHE_ON_UPDATE);
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Data serialized = serializationService.toData(config);
        NearCacheConfig deserialized = serializationService.toObject(serialized);
        Assert.assertEquals(config.isInvalidateOnChange(), deserialized.isInvalidateOnChange());
        Assert.assertEquals(config.isCacheLocalEntries(), deserialized.isCacheLocalEntries());
        Assert.assertEquals(config.getName(), deserialized.getName());
        Assert.assertEquals(config.getInMemoryFormat(), deserialized.getInMemoryFormat());
        Assert.assertEquals(config.getTimeToLiveSeconds(), deserialized.getTimeToLiveSeconds());
        Assert.assertEquals(config.getMaxIdleSeconds(), deserialized.getMaxIdleSeconds());
        Assert.assertEquals(config.getLocalUpdatePolicy(), deserialized.getLocalUpdatePolicy());
        Assert.assertEquals(config.getEvictionPolicy(), deserialized.getEvictionPolicy());
        Assert.assertEquals(config.getMaxSize(), deserialized.getMaxSize());
        Assert.assertEquals(config.toString(), deserialized.toString());
    }
}

