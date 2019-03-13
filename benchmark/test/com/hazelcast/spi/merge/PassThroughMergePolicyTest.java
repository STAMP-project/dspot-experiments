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
public class PassThroughMergePolicyTest {
    private static final SerializationService SERIALIZATION_SERVICE = new DefaultSerializationServiceBuilder().build();

    private static final Data EXISTING = PassThroughMergePolicyTest.SERIALIZATION_SERVICE.toData("EXISTING");

    private static final Data MERGING = PassThroughMergePolicyTest.SERIALIZATION_SERVICE.toData("MERGING");

    private SplitBrainMergePolicy<Data, MapMergeTypes> mergePolicy;

    @Test
    public void merge_mergingNotNull() {
        MapMergeTypes existing = mergingValueWithGivenValue(PassThroughMergePolicyTest.EXISTING);
        MapMergeTypes merging = mergingValueWithGivenValue(PassThroughMergePolicyTest.MERGING);
        Assert.assertEquals(PassThroughMergePolicyTest.MERGING, mergePolicy.merge(merging, existing));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void merge_mergingNull() {
        MapMergeTypes existing = mergingValueWithGivenValue(PassThroughMergePolicyTest.EXISTING);
        MapMergeTypes merging = null;
        Assert.assertEquals(PassThroughMergePolicyTest.EXISTING, mergePolicy.merge(merging, existing));
    }
}

