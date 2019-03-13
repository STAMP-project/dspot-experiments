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
package com.hazelcast.cache.eviction;


import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheEvictionPolicyComparatorTest extends AbstractCacheEvictionPolicyComparatorTest {
    @Test
    public void test_evictionPolicyComparator_with_comparatorClassName_when_maxSizePolicy_is_entryCount() {
        int partitionCount = Integer.parseInt(PARTITION_COUNT.getDefaultValue());
        int iterationCount = ((calculateMaxPartitionSize(EvictionConfig.DEFAULT_MAX_ENTRY_COUNT, partitionCount)) * partitionCount) * 2;
        EvictionConfig evictionConfig = new EvictionConfig().setComparatorClassName(AbstractCacheEvictionPolicyComparatorTest.MyEvictionPolicyComparator.class.getName());
        testEvictionPolicyComparator(evictionConfig, iterationCount);
    }

    @Test
    public void test_evictionPolicyComparator_with_comparatorInstance_when_maxSizePolicy_is_entryCount() {
        int partitionCount = Integer.parseInt(PARTITION_COUNT.getDefaultValue());
        int iterationCount = ((calculateMaxPartitionSize(EvictionConfig.DEFAULT_MAX_ENTRY_COUNT, partitionCount)) * partitionCount) * 2;
        EvictionConfig evictionConfig = new EvictionConfig().setComparator(new AbstractCacheEvictionPolicyComparatorTest.MyEvictionPolicyComparator());
        testEvictionPolicyComparator(evictionConfig, iterationCount);
    }
}

