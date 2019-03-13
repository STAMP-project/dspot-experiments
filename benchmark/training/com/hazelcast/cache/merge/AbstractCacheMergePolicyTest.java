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
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractCacheMergePolicyTest {
    private static final String EXISTING = "EXISTING";

    private static final String MERGING = "MERGING";

    protected CacheMergePolicy policy;

    @Test
    public void merge_mergingWins() {
        CacheEntryView existing = entryWithGivenPropertyAndValue(1, AbstractCacheMergePolicyTest.EXISTING);
        CacheEntryView merging = entryWithGivenPropertyAndValue(333, AbstractCacheMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractCacheMergePolicyTest.MERGING, policy.merge("cache", merging, existing));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void merge_mergingWins_sinceExistingIsNotExist() {
        CacheEntryView existing = null;
        CacheEntryView merging = entryWithGivenPropertyAndValue(1, AbstractCacheMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractCacheMergePolicyTest.MERGING, policy.merge("cache", merging, existing));
    }

    @Test
    public void merge_existingWins() {
        CacheEntryView existing = entryWithGivenPropertyAndValue(333, AbstractCacheMergePolicyTest.EXISTING);
        CacheEntryView merging = entryWithGivenPropertyAndValue(1, AbstractCacheMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractCacheMergePolicyTest.EXISTING, policy.merge("cache", merging, existing));
    }

    @Test
    public void merge_draw_mergingWins() {
        CacheEntryView existing = entryWithGivenPropertyAndValue(1, AbstractCacheMergePolicyTest.EXISTING);
        CacheEntryView merging = entryWithGivenPropertyAndValue(1, AbstractCacheMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractCacheMergePolicyTest.MERGING, policy.merge("cache", merging, existing));
    }
}

