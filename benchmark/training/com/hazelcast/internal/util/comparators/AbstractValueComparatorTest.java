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
package com.hazelcast.internal.util.comparators;


import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.map.impl.record.Person;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("WeakerAccess")
public abstract class AbstractValueComparatorTest extends HazelcastTestSupport {
    SerializationService serializationService;

    PartitioningStrategy partitioningStrategy;

    ValueComparator comparator;

    Person object1;

    Person object2;

    Data data1;

    Data data2;

    Data nullData;

    @Test
    public void testIsEqual() {
        newRecordComparator();
        Assert.assertTrue(comparator.isEqual(null, null, serializationService));
        Assert.assertTrue(comparator.isEqual(object1, object1, serializationService));
        Assert.assertTrue(comparator.isEqual(object1, data1, serializationService));
        Assert.assertTrue(comparator.isEqual(data1, data1, serializationService));
        Assert.assertTrue(comparator.isEqual(data1, object1, serializationService));
        Assert.assertTrue(comparator.isEqual(nullData, nullData, serializationService));
        Assert.assertFalse(comparator.isEqual(null, object1, serializationService));
        Assert.assertFalse(comparator.isEqual(null, data1, serializationService));
        Assert.assertFalse(comparator.isEqual(null, nullData, serializationService));
        Assert.assertFalse(comparator.isEqual(object1, null, serializationService));
        Assert.assertFalse(comparator.isEqual(object1, nullData, serializationService));
        Assert.assertFalse(comparator.isEqual(object1, object2, serializationService));
        Assert.assertFalse(comparator.isEqual(object1, data2, serializationService));
        Assert.assertFalse(comparator.isEqual(data1, null, serializationService));
        Assert.assertFalse(comparator.isEqual(data1, nullData, serializationService));
        Assert.assertFalse(comparator.isEqual(data1, object2, serializationService));
        Assert.assertFalse(comparator.isEqual(data1, data2, serializationService));
        Assert.assertFalse(comparator.isEqual(nullData, null, serializationService));
        Assert.assertFalse(comparator.isEqual(nullData, object1, serializationService));
        Assert.assertFalse(comparator.isEqual(nullData, data1, serializationService));
    }

    @Test
    public void testIsEqual_withCustomPartitioningStrategy() {
        partitioningStrategy = new AbstractValueComparatorTest.PersonPartitioningStrategy();
        data1 = serializationService.toData(object1, partitioningStrategy);
        data2 = serializationService.toData(object2, partitioningStrategy);
        testIsEqual();
    }

    static class PersonPartitioningStrategy implements PartitioningStrategy<Person> {
        @Override
        public Object getPartitionKey(Person key) {
            return key.getName();
        }
    }
}

