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
package com.hazelcast.replicatedmap.merge;


import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PutIfAbsentMapMergePolicyTest {
    private static final String EXISTING = "EXISTING";

    private static final String MERGING = "MERGING";

    protected ReplicatedMapMergePolicy policy;

    @Test
    public void merge_existingValueAbsent() {
        ReplicatedMapEntryView existing = entryWithGivenValue(null);
        ReplicatedMapEntryView merging = entryWithGivenValue(PutIfAbsentMapMergePolicyTest.MERGING);
        Assert.assertEquals(PutIfAbsentMapMergePolicyTest.MERGING, policy.merge("map", merging, existing));
    }

    @Test
    public void merge_existingValuePresent() {
        ReplicatedMapEntryView existing = entryWithGivenValue(PutIfAbsentMapMergePolicyTest.EXISTING);
        ReplicatedMapEntryView merging = entryWithGivenValue(PutIfAbsentMapMergePolicyTest.MERGING);
        Assert.assertEquals(PutIfAbsentMapMergePolicyTest.EXISTING, policy.merge("map", merging, existing));
    }

    @Test
    public void merge_bothValuesNull() {
        ReplicatedMapEntryView existing = entryWithGivenValue(null);
        ReplicatedMapEntryView merging = entryWithGivenValue(null);
        Assert.assertNull(policy.merge("map", merging, existing));
    }
}

