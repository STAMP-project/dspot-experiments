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


import com.hazelcast.internal.serialization.InternalSerializationService;
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
public class LocalEntryEventDataTest extends HazelcastTestSupport {
    private InternalSerializationService serializationService;

    // passed params are type of OBJECT to this event
    private LocalEntryEventData objectEvent;

    // passed params are type of DATA to this event
    private LocalEntryEventData dataEvent;

    @Test
    public void testGetValue_withDataValue() {
        Assert.assertEquals("value", dataEvent.getValue());
    }

    @Test
    public void testGetValue_withObjectValue() {
        Assert.assertEquals("value", objectEvent.getValue());
    }

    @Test
    public void testGetOldValue_withDataValue() {
        Assert.assertEquals("oldValue", dataEvent.getOldValue());
    }

    @Test
    public void testGetOldValue_withObjectValue() {
        Assert.assertEquals("oldValue", objectEvent.getOldValue());
    }

    @Test
    public void testGetKey_withDataKey() {
        Assert.assertEquals("key", dataEvent.getKey());
    }

    @Test
    public void testGetKey_withObjectKey() {
        Assert.assertEquals("key", objectEvent.getKey());
    }

    @Test
    public void testGetKeyData_withDataKey() {
        Assert.assertEquals(toData("key"), dataEvent.getKeyData());
    }

    @Test
    public void testGetKeyData_withObjectKey() {
        Assert.assertEquals(toData("key"), objectEvent.getKeyData());
    }

    @Test
    public void testGetValueData_withDataValue() {
        Assert.assertEquals(toData("value"), dataEvent.getValueData());
    }

    @Test
    public void testGetValueData_withObjectValue() {
        Assert.assertEquals(toData("value"), objectEvent.getValueData());
    }

    @Test
    public void testGetOldValueData_withDataValue() {
        Assert.assertEquals(toData("oldValue"), dataEvent.getOldValueData());
    }

    @Test
    public void testGetOldValueData_withObjectValue() {
        Assert.assertEquals(toData("oldValue"), objectEvent.getOldValueData());
    }

    @Test
    public void testGetSource() {
        Assert.assertEquals("source", dataEvent.getSource());
        Assert.assertEquals("source", objectEvent.getSource());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMapName() {
        dataEvent.getMapName();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCaller() {
        dataEvent.getCaller();
    }

    @Test
    public void testGetEventType() {
        Assert.assertEquals(23, dataEvent.getEventType());
        Assert.assertEquals(23, objectEvent.getEventType());
    }

    @Test
    public void testGetPartitionId() {
        Assert.assertEquals(42, dataEvent.getPartitionId());
        Assert.assertEquals(42, objectEvent.getPartitionId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWriteData() throws Exception {
        dataEvent.writeData(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadData() throws Exception {
        dataEvent.readData(null);
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(dataEvent.toString(), "LocalEntryEventData");
    }
}

