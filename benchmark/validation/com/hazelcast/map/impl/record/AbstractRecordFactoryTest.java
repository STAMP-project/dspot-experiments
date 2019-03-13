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
package com.hazelcast.map.impl.record;


import CacheDeserializedValues.ALWAYS;
import CacheDeserializedValues.NEVER;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("WeakerAccess")
public abstract class AbstractRecordFactoryTest<T> extends HazelcastTestSupport {
    SerializationService serializationService;

    PartitioningStrategy partitioningStrategy;

    RecordFactory<T> factory;

    Person object1;

    Person object2;

    Data data1;

    Data data2;

    Record<T> record;

    @Test
    public void testNewRecord_withStatisticsDisabledAndCacheDeserializedValuesIsALWAYS() {
        newRecordFactory(false, ALWAYS);
        record = newRecord(factory, data1, object1);
        HazelcastTestSupport.assertInstanceOf(getCachedRecordClass(), record);
    }

    @Test
    public void testNewRecord_withStatisticsDisabledAndCacheDeserializedValuesIsNEVER() {
        newRecordFactory(false, NEVER);
        record = newRecord(factory, data1, object1);
        HazelcastTestSupport.assertInstanceOf(getRecordClass(), record);
    }

    @Test
    public void testNewRecord_withStatisticsEnabledAndCacheDeserializedValuesIsALWAYS() {
        newRecordFactory(true, ALWAYS);
        record = newRecord(factory, data1, object1);
        HazelcastTestSupport.assertInstanceOf(getCachedRecordWithStatsClass(), record);
    }

    @Test
    public void testNewRecord_withStatisticsEnabledAndCacheDeserializedValuesIsNEVER() {
        newRecordFactory(true, NEVER);
        record = newRecord(factory, data1, object1);
        HazelcastTestSupport.assertInstanceOf(getRecordWithStatsClass(), record);
    }

    @Test(expected = AssertionError.class)
    public void testNewRecord_withNullValue() {
        newRecordFactory(false, ALWAYS);
        newRecord(factory, data1, null);
    }

    @Test
    public void testSetValue() {
        newRecordFactory(false, ALWAYS);
        record = factory.newRecord(data1, object1);
        factory.setValue(record, object2);
        Assert.assertEquals(getValue(data2, object2), record.getValue());
    }

    @Test
    public void testSetValue_withData() {
        newRecordFactory(false, ALWAYS);
        record = factory.newRecord(data1, object1);
        factory.setValue(record, data2);
        Assert.assertEquals(getValue(data2, object2), record.getValue());
    }

    @Test(expected = AssertionError.class)
    public void testSetValue_withNull() {
        newRecordFactory(false, ALWAYS);
        record = factory.newRecord(data1, object1);
        factory.setValue(record, null);
    }
}

