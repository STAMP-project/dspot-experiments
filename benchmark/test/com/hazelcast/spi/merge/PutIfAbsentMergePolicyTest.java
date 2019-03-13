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
package com.hazelcast.spi.merge;


import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.merge.SplitBrainMergeTypes.MapMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PutIfAbsentMergePolicyTest {
    private static final SerializationService SERIALIZATION_SERVICE = new DefaultSerializationServiceBuilder().build();

    private static final Data EXISTING = PutIfAbsentMergePolicyTest.SERIALIZATION_SERVICE.toData("EXISTING");

    private static final Data MERGING = PutIfAbsentMergePolicyTest.SERIALIZATION_SERVICE.toData("MERGING");

    private SplitBrainMergePolicy<Data, MapMergeTypes> mergePolicy;

    @Test
    @SuppressWarnings("ConstantConditions")
    public void merge_existingValueAbsent() {
        MapMergeTypes existing = null;
        MapMergeTypes merging = mergingValueWithGivenValue(PutIfAbsentMergePolicyTest.MERGING);
        Assert.assertEquals(PutIfAbsentMergePolicyTest.MERGING, mergePolicy.merge(merging, existing));
    }

    @Test
    public void merge_existingValuePresent() {
        MapMergeTypes existing = mergingValueWithGivenValue(PutIfAbsentMergePolicyTest.EXISTING);
        MapMergeTypes merging = mergingValueWithGivenValue(PutIfAbsentMergePolicyTest.MERGING);
        Assert.assertEquals(PutIfAbsentMergePolicyTest.EXISTING, mergePolicy.merge(merging, existing));
    }

    @Test
    public void merge_bothValuesNull() {
        MapMergeTypes existing = mergingValueWithGivenValue(null);
        MapMergeTypes merging = mergingValueWithGivenValue(null);
        Assert.assertNull(mergePolicy.merge(merging, existing));
    }
}

