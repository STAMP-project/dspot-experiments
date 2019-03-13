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
package com.hazelcast.map.impl.nearcache.invalidation;


import com.hazelcast.internal.nearcache.impl.invalidation.BatchNearCacheInvalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.SingleNearCacheInvalidation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvalidationTest extends HazelcastTestSupport {
    private SerializationService serializationService;

    private SingleNearCacheInvalidation singleInvalidation;

    private BatchNearCacheInvalidation batchInvalidation;

    @Test
    public void testSingleDeserialization() {
        Data serializedInvalidation = serializationService.toData(singleInvalidation);
        SingleNearCacheInvalidation deserializedInvalidation = serializationService.toObject(serializedInvalidation);
        InvalidationTest.assertInvalidation(singleInvalidation, deserializedInvalidation, true);
    }

    @Test
    public void testBatchDeserialization() {
        Data serializedInvalidation = serializationService.toData(batchInvalidation);
        BatchNearCacheInvalidation deserializedInvalidation = serializationService.toObject(serializedInvalidation);
        InvalidationTest.assertInvalidation(batchInvalidation, deserializedInvalidation, false);
        Assert.assertEquals(batchInvalidation.getInvalidations().size(), deserializedInvalidation.getInvalidations().size());
        for (Invalidation invalidation : deserializedInvalidation.getInvalidations()) {
            InvalidationTest.assertInvalidation(singleInvalidation, invalidation, true);
        }
    }
}

