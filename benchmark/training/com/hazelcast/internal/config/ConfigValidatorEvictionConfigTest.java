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
import EvictionPolicy.LFU;
import EvictionPolicy.NONE;
import EvictionPolicy.RANDOM;
import InMemoryFormat.BINARY;
import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigValidatorEvictionConfigTest extends HazelcastTestSupport {
    @Test
    public void checkEvictionConfig_forMapAndCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, false), false);
    }

    @Test
    public void checkEvictionConfig_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, false, RANDOM), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_withNull() {
        ConfigValidator.checkEvictionConfig(null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_withNull_forNearCache() {
        ConfigValidator.checkEvictionConfig(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenBothOfComparatorAndComparatorClassNameAreSet() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, true), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenBothOfComparatorAndComparatorClassNameAreSet_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, true), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenNoneOfTheComparatorAndComparatorClassNameAreSetIfEvictionPolicyIsNone() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, false, NONE), false);
    }

    @Test
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsNone() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false, NONE), false);
    }

    @Test
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsNone() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true, NONE), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenNoneOfTheComparatorAndComparatorClassNameAreSetIfEvictionPolicyIsRandom() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, false, RANDOM), false);
    }

    @Test
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsRandom() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false, RANDOM), false);
    }

    @Test
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsRandom() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true, RANDOM), false);
    }

    @Test
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsNotSet() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false), false);
    }

    @Test
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsNotSet() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsAlsoSet() {
        // default eviction policy is LRU (see EvictionConfig.DEFAULT_EVICTION_POLICY)
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false, LFU), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsAlsoSet() {
        // default eviction policy is LRU (see EvictionConfig.DEFAULT_EVICTION_POLICY)
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true, LFU), false);
    }

    @Test
    public void checkEvictionConfig_whenNoneOfTheComparatorAndComparatorClassNameAreSetIfEvictionPolicyIsNone_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, false, NONE), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsNone_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false, NONE), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsNone_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true, NONE), true);
    }

    @Test
    public void checkEvictionConfig_whenNoneOfTheComparatorAndComparatorClassNameAreSetIfEvictionPolicyIsRandom_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, false, RANDOM), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsRandom_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false, RANDOM), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsRandom_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true, RANDOM), true);
    }

    @Test
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsNotSet_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false), true);
    }

    @Test
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsNotSet_forNearCache() {
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorClassNameIsSetIfEvictionPolicyIsAlsoSet_forNearCache() {
        // default eviction policy is LRU (see EvictionConfig.DEFAULT_EVICTION_POLICY)
        ConfigValidator.checkEvictionConfig(getEvictionConfig(true, false, LFU), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenComparatorIsSetIfEvictionPolicyIsAlsoSet_forNearCache() {
        // default eviction policy is LRU (see EvictionConfig.DEFAULT_EVICTION_POLICY)
        ConfigValidator.checkEvictionConfig(getEvictionConfig(false, true, LFU), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_whenNoneOfTheComparatorAndComparatorClassNameAreSetIfEvictionPolicyIsNull() {
        ConfigValidator.checkEvictionConfig(null, null, null, false);
    }

    @Test
    public void checkEvictionConfig_whenNoneOfTheComparatorAndComparatorClassNameAreSetIfEvictionPolicyIsNull_forNearCache() {
        ConfigValidator.checkEvictionConfig(null, null, null, true);
    }

    @Test
    public void checkEvictionConfig_withEntryCountMaxSizePolicy_OBJECT() {
        EvictionConfig evictionConfig = new EvictionConfig().setMaximumSizePolicy(ENTRY_COUNT);
        ConfigValidator.checkEvictionConfig(OBJECT, evictionConfig);
    }

    @Test
    public void checkEvictionConfig_withEntryCountMaxSizePolicy_BINARY() {
        EvictionConfig evictionConfig = new EvictionConfig().setMaximumSizePolicy(ENTRY_COUNT);
        ConfigValidator.checkEvictionConfig(BINARY, evictionConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEvictionConfig_withEntryCountMaxSizePolicy_NATIVE() {
        EvictionConfig evictionConfig = new EvictionConfig().setMaximumSizePolicy(ENTRY_COUNT);
        ConfigValidator.checkEvictionConfig(NATIVE, evictionConfig);
    }
}

