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
package com.hazelcast.map.impl.querycache.event;


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DefaultQueryCacheEventDataTest {
    private static final Object KEY = new Object();

    private static final Object VALUE = new Object();

    private static final Object DESERIALIZED_KEY = new Object();

    private static final Object DESERIALIZED_VALUE = new Object();

    private static final Data DATA_KEY = Mockito.mock(Data.class);

    private static final Data DATA_OLD_VALUE = Mockito.mock(Data.class);

    private static final Data DATA_NEW_VALUE = Mockito.mock(Data.class);

    private SerializationService serializationService;

    private DefaultQueryCacheEventData queryCacheEventData;

    private DefaultQueryCacheEventData queryCacheEventDataSameAttributes;

    private DefaultQueryCacheEventData queryCacheEventDataOtherSequence;

    private DefaultQueryCacheEventData queryCacheEventDataOtherEventType;

    private DefaultQueryCacheEventData queryCacheEventDataOtherPartitionId;

    private DefaultQueryCacheEventData queryCacheEventDataOtherKey;

    private DefaultQueryCacheEventData queryCacheEventDataOtherValue;

    private DefaultQueryCacheEventData queryCacheEventDataOtherDataKey;

    private DefaultQueryCacheEventData queryCacheEventDataOtherDataNewValue;

    private DefaultQueryCacheEventData queryCacheEventDataOtherDataOldValue;

    private DefaultQueryCacheEventData queryCacheEventDataOtherSerializationService;

    @Test
    public void testKey() {
        queryCacheEventData.setSerializationService(serializationService);
        Assert.assertNull(queryCacheEventData.getDataKey());
        Assert.assertNull(queryCacheEventData.getKey());
        queryCacheEventData.setDataKey(DefaultQueryCacheEventDataTest.DATA_KEY);
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DATA_KEY, queryCacheEventData.getDataKey());
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DESERIALIZED_KEY, queryCacheEventData.getKey());
        queryCacheEventData.setKey(DefaultQueryCacheEventDataTest.KEY);
        Assert.assertEquals(DefaultQueryCacheEventDataTest.KEY, queryCacheEventData.getKey());
    }

    @Test
    public void testValue() {
        queryCacheEventData.setSerializationService(serializationService);
        Assert.assertNull(queryCacheEventData.getDataOldValue());
        Assert.assertNull(queryCacheEventData.getDataNewValue());
        Assert.assertNull(queryCacheEventData.getValue());
        queryCacheEventData.setDataOldValue(DefaultQueryCacheEventDataTest.DATA_OLD_VALUE);
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DATA_OLD_VALUE, queryCacheEventData.getDataOldValue());
        Assert.assertNull(queryCacheEventData.getDataNewValue());
        Assert.assertNull(queryCacheEventData.getValue());
        queryCacheEventData.setDataNewValue(DefaultQueryCacheEventDataTest.DATA_NEW_VALUE);
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DATA_OLD_VALUE, queryCacheEventData.getDataOldValue());
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DATA_NEW_VALUE, queryCacheEventData.getDataNewValue());
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DESERIALIZED_VALUE, queryCacheEventData.getValue());
        queryCacheEventData.setValue(DefaultQueryCacheEventDataTest.VALUE);
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DATA_OLD_VALUE, queryCacheEventData.getDataOldValue());
        Assert.assertEquals(DefaultQueryCacheEventDataTest.DATA_NEW_VALUE, queryCacheEventData.getDataNewValue());
        Assert.assertEquals(DefaultQueryCacheEventDataTest.VALUE, queryCacheEventData.getValue());
    }

    @Test
    public void testGetCreationTime() {
        Assert.assertTrue(((queryCacheEventData.getCreationTime()) > 0));
    }

    @Test
    public void testSequence() {
        Assert.assertEquals(0, queryCacheEventData.getSequence());
        queryCacheEventData.setSequence(5123);
        Assert.assertEquals(5123, queryCacheEventData.getSequence());
    }

    @Test
    public void testPartitionId() {
        Assert.assertEquals(0, queryCacheEventData.getPartitionId());
        queryCacheEventData.setPartitionId(9981);
        Assert.assertEquals(9981, queryCacheEventData.getPartitionId());
    }

    @Test
    public void testEventType() {
        Assert.assertEquals(0, queryCacheEventData.getEventType());
        queryCacheEventData.setEventType(42);
        Assert.assertEquals(42, queryCacheEventData.getEventType());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSource() {
        queryCacheEventData.getSource();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMapName() {
        queryCacheEventData.getMapName();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCaller() {
        queryCacheEventData.getCaller();
    }

    @Test
    public void testToString() {
        Assert.assertNotNull(queryCacheEventData.toString());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(queryCacheEventData, queryCacheEventData);
        Assert.assertEquals(queryCacheEventData, queryCacheEventDataSameAttributes);
        Assert.assertNotEquals(queryCacheEventData, null);
        Assert.assertNotEquals(queryCacheEventData, new Object());
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherSequence);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherEventType);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherPartitionId);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherKey);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherValue);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherDataKey);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherDataNewValue);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherDataOldValue);
        Assert.assertNotEquals(queryCacheEventData, queryCacheEventDataOtherSerializationService);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(queryCacheEventData.hashCode(), queryCacheEventData.hashCode());
        Assert.assertEquals(queryCacheEventData.hashCode(), queryCacheEventDataSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherSequence.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherEventType.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherPartitionId.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherKey.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherValue.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherDataKey.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherDataNewValue.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherDataOldValue.hashCode());
        Assert.assertNotEquals(queryCacheEventData.hashCode(), queryCacheEventDataOtherSerializationService.hashCode());
    }

    @Test
    public void testCopyConstructor() throws Exception {
        DefaultQueryCacheEventData actual = new DefaultQueryCacheEventData();
        actual.setPartitionId(1);
        actual.setEventType(2);
        actual.setKey(3);
        actual.setValue(4);
        DefaultQueryCacheEventData copied = new DefaultQueryCacheEventData(actual);
        Assert.assertEquals(copied, actual);
    }
}

