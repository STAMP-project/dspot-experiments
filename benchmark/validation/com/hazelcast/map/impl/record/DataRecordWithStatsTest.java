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


import com.hazelcast.nio.serialization.Data;
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
public class DataRecordWithStatsTest {
    private static final Data VALUE = Mockito.mock(Data.class);

    private DataRecordWithStats record;

    private DataRecordWithStats recordSameAttributes;

    private DataRecordWithStats recordOtherKeyAndValue;

    private ObjectRecordWithStats objectRecord;

    @Test
    public void testGetValue() {
        Assert.assertEquals(DataRecordWithStatsTest.VALUE, record.getValue());
        Assert.assertEquals(DataRecordWithStatsTest.VALUE, recordSameAttributes.getValue());
        Assert.assertNotEquals(DataRecordWithStatsTest.VALUE, recordOtherKeyAndValue.getValue());
    }

    @Test
    public void testGetCosts() {
        Assert.assertTrue(((record.getCost()) > 0));
        Assert.assertTrue(((recordSameAttributes.getCost()) > 0));
        Assert.assertTrue(((recordOtherKeyAndValue.getCost()) > 0));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(record, record);
        Assert.assertEquals(record, recordSameAttributes);
        Assert.assertNotEquals(record, null);
        Assert.assertNotEquals(record, new Object());
        Assert.assertNotEquals(record, objectRecord);
        Assert.assertNotEquals(record, recordOtherKeyAndValue);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(record.hashCode(), record.hashCode());
        Assert.assertEquals(record.hashCode(), recordSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(record.hashCode(), objectRecord.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherKeyAndValue.hashCode());
    }
}

