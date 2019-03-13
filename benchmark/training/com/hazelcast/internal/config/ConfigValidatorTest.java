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
package com.hazelcast.internal.config;


import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.map.merge.MergePolicyProvider;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigValidatorTest extends HazelcastTestSupport {
    private MergePolicyProvider mapMergePolicyProvider;

    private CacheMergePolicyProvider cacheMergePolicyProvider;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ConfigValidator.class);
    }

    @Test
    public void checkMapConfig_BINARY() {
        ConfigValidator.checkMapConfig(getMapConfig(InMemoryFormat.BINARY), mapMergePolicyProvider);
    }

    @Test
    public void checkMapConfig_OBJECT() {
        ConfigValidator.checkMapConfig(getMapConfig(InMemoryFormat.OBJECT), mapMergePolicyProvider);
    }

    /**
     * Not supported in open source version, so test is expected to throw exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void checkMapConfig_NATIVE() {
        ConfigValidator.checkMapConfig(getMapConfig(InMemoryFormat.NATIVE), mapMergePolicyProvider);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void checkMapConfig_withIgnoredConfigMinEvictionCheckMillis() {
        MapConfig mapConfig = getMapConfig(InMemoryFormat.BINARY).setMinEvictionCheckMillis(100);
        ConfigValidator.checkMapConfig(mapConfig, mapMergePolicyProvider);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void checkMapConfig_withIgnoredConfigEvictionPercentage() {
        MapConfig mapConfig = getMapConfig(InMemoryFormat.BINARY).setEvictionPercentage(50);
        ConfigValidator.checkMapConfig(mapConfig, mapMergePolicyProvider);
    }

    @Test
    public void checkCacheConfig_withEntryCountMaxSizePolicy_OBJECT() {
        EvictionConfig evictionConfig = new EvictionConfig().setMaximumSizePolicy(ENTRY_COUNT);
        CacheSimpleConfig cacheSimpleConfig = new CacheSimpleConfig().setInMemoryFormat(InMemoryFormat.OBJECT).setEvictionConfig(evictionConfig);
        ConfigValidator.checkCacheConfig(cacheSimpleConfig, cacheMergePolicyProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkCacheConfig_withEntryCountMaxSizePolicy_NATIVE() {
        EvictionConfig evictionConfig = new EvictionConfig().setMaximumSizePolicy(ENTRY_COUNT);
        CacheSimpleConfig cacheSimpleConfig = new CacheSimpleConfig().setInMemoryFormat(InMemoryFormat.NATIVE).setEvictionConfig(evictionConfig);
        ConfigValidator.checkCacheConfig(cacheSimpleConfig, cacheMergePolicyProvider);
    }

    @Test
    public void checkNearCacheNativeMemoryConfig_shouldNotNeedNativeMemoryConfig_BINARY_onOS() {
        ConfigValidator.checkNearCacheNativeMemoryConfig(InMemoryFormat.BINARY, null, false);
    }

    @Test
    public void checkNearCacheNativeMemoryConfig_shouldNotNeedNativeMemoryConfig_BINARY_onEE() {
        ConfigValidator.checkNearCacheNativeMemoryConfig(InMemoryFormat.BINARY, null, true);
    }

    @Test
    public void checkNearCacheNativeMemoryConfig_shouldNotThrowExceptionWithoutNativeMemoryConfig_NATIVE_onOS() {
        ConfigValidator.checkNearCacheNativeMemoryConfig(InMemoryFormat.NATIVE, null, false);
    }

    @Test
    public void checkNearCacheNativeMemoryConfig_shouldNotThrowExceptionWithNativeMemoryConfig_NATIVE_onEE() {
        NativeMemoryConfig nativeMemoryConfig = new NativeMemoryConfig().setEnabled(true);
        ConfigValidator.checkNearCacheNativeMemoryConfig(InMemoryFormat.NATIVE, nativeMemoryConfig, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNearCacheNativeMemoryConfig_shouldThrowExceptionWithoutNativeMemoryConfig_NATIVE_onEE() {
        ConfigValidator.checkNearCacheNativeMemoryConfig(InMemoryFormat.NATIVE, null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFails_whenGroupSizeSetCPMemberCountNotSet() {
        CPSubsystemConfig config = new CPSubsystemConfig();
        config.setGroupSize(3);
        ConfigValidator.checkCPSubsystemConfig(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFails_whenGroupSizeGreaterThanCPMemberCount() {
        CPSubsystemConfig config = new CPSubsystemConfig();
        config.setGroupSize(5);
        config.setCPMemberCount(3);
        ConfigValidator.checkCPSubsystemConfig(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFails_whenSessionHeartbeatIntervalGreaterThanSessionTTL() {
        CPSubsystemConfig config = new CPSubsystemConfig();
        config.setSessionTimeToLiveSeconds(5);
        config.setSessionHeartbeatIntervalSeconds(10);
        ConfigValidator.checkCPSubsystemConfig(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFails_whenSessionTTLGreaterThanMissingCPMemberAutoRemovalSeconds() {
        CPSubsystemConfig config = new CPSubsystemConfig();
        config.setMissingCPMemberAutoRemovalSeconds(5);
        config.setSessionTimeToLiveSeconds(10);
        ConfigValidator.checkCPSubsystemConfig(config);
    }
}

