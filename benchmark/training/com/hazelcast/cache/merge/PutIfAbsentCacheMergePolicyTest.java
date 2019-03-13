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
package com.hazelcast.cache.merge;


import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PutIfAbsentCacheMergePolicyTest {
    private static final String EXISTING = "EXISTING";

    private static final String MERGING = "MERGING";

    protected CacheMergePolicy policy;

    @Test
    @SuppressWarnings("ConstantConditions")
    public void merge_existingValueAbsent() {
        CacheEntryView existing = null;
        CacheEntryView merging = entryWithGivenValue(PutIfAbsentCacheMergePolicyTest.MERGING);
        Assert.assertEquals(PutIfAbsentCacheMergePolicyTest.MERGING, policy.merge("map", merging, existing));
    }

    @Test
    public void merge_existingValuePresent() {
        CacheEntryView existing = entryWithGivenValue(PutIfAbsentCacheMergePolicyTest.EXISTING);
        CacheEntryView merging = entryWithGivenValue(PutIfAbsentCacheMergePolicyTest.MERGING);
        Assert.assertEquals(PutIfAbsentCacheMergePolicyTest.EXISTING, policy.merge("map", merging, existing));
    }

    @Test
    public void merge_bothValuesNull() {
        CacheEntryView existing = entryWithGivenValue(null);
        CacheEntryView merging = entryWithGivenValue(null);
        Assert.assertNull(policy.merge("map", merging, existing));
    }
}

