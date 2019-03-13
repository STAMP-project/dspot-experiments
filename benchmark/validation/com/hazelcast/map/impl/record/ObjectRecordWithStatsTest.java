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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ObjectRecordWithStatsTest {
    private static final Object VALUE = new Object();

    private ObjectRecordWithStats record;

    private ObjectRecordWithStats recordSameAttributes;

    private ObjectRecordWithStats recordOtherLastStoredTime;

    private ObjectRecordWithStats recordOtherExpirationTime;

    private ObjectRecordWithStats recordOtherKeyAndValue;

    private DataRecordWithStats dataRecord;

    @Test
    public void testGetValue() {
        Assert.assertEquals(ObjectRecordWithStatsTest.VALUE, record.getValue());
        Assert.assertEquals(ObjectRecordWithStatsTest.VALUE, recordSameAttributes.getValue());
        Assert.assertNotEquals(ObjectRecordWithStatsTest.VALUE, recordOtherKeyAndValue.getValue());
    }

    @Test
    public void testGetCosts() {
        Assert.assertEquals(0, record.getCost());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(record, record);
        Assert.assertEquals(record, recordSameAttributes);
        Assert.assertNotEquals(record, null);
        Assert.assertNotEquals(record, new Object());
        Assert.assertNotEquals(record, dataRecord);
        Assert.assertNotEquals(record, recordOtherLastStoredTime);
        Assert.assertNotEquals(record, recordOtherExpirationTime);
        Assert.assertNotEquals(record, recordOtherKeyAndValue);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(record.hashCode(), record.hashCode());
        Assert.assertEquals(record.hashCode(), recordSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(record.hashCode(), dataRecord.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherLastStoredTime.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherExpirationTime.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherKeyAndValue.hashCode());
    }
}

