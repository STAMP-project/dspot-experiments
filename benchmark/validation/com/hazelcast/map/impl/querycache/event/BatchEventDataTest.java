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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BatchEventDataTest extends HazelcastTestSupport {
    private QueryCacheEventData eventData;

    private QueryCacheEventData otherEventData;

    private BatchEventData batchEventData;

    private BatchEventData batchEventDataSameAttribute;

    private BatchEventData batchEventDataOtherSource;

    private BatchEventData batchEventDataOtherPartitionId;

    private BatchEventData batchEventDataOtherEvent;

    private BatchEventData batchEventDataNoEvent;

    @Test
    public void testAdd() {
        batchEventData.add(otherEventData);
        Assert.assertEquals(2, batchEventData.size());
        Collection<QueryCacheEventData> events = batchEventData.getEvents();
        HazelcastTestSupport.assertContains(events, eventData);
        HazelcastTestSupport.assertContains(events, otherEventData);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertFalse(batchEventData.isEmpty());
        Assert.assertFalse(batchEventDataSameAttribute.isEmpty());
        Assert.assertFalse(batchEventDataOtherSource.isEmpty());
        Assert.assertFalse(batchEventDataOtherPartitionId.isEmpty());
        Assert.assertFalse(batchEventDataOtherEvent.isEmpty());
        Assert.assertTrue(batchEventDataNoEvent.isEmpty());
    }

    @Test
    public void testSize() {
        Assert.assertEquals(1, batchEventData.size());
        Assert.assertEquals(1, batchEventDataSameAttribute.size());
        Assert.assertEquals(1, batchEventDataOtherSource.size());
        Assert.assertEquals(1, batchEventDataOtherPartitionId.size());
        Assert.assertEquals(1, batchEventDataOtherEvent.size());
        Assert.assertEquals(0, batchEventDataNoEvent.size());
    }

    @Test
    public void testGetPartitionId() {
        Assert.assertEquals(1, batchEventData.getPartitionId());
        Assert.assertEquals(1, batchEventDataSameAttribute.getPartitionId());
        Assert.assertEquals(1, batchEventDataOtherSource.getPartitionId());
        Assert.assertEquals(2, batchEventDataOtherPartitionId.getPartitionId());
        Assert.assertEquals(1, batchEventDataOtherEvent.getPartitionId());
        Assert.assertEquals(1, batchEventDataNoEvent.getPartitionId());
    }

    @Test
    public void testGetSource() {
        Assert.assertEquals("source", batchEventData.getSource());
        Assert.assertEquals("source", batchEventDataSameAttribute.getSource());
        Assert.assertEquals("otherSource", batchEventDataOtherSource.getSource());
        Assert.assertEquals("source", batchEventDataOtherPartitionId.getSource());
        Assert.assertEquals("source", batchEventDataOtherEvent.getSource());
        Assert.assertEquals("source", batchEventDataNoEvent.getSource());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMapName() {
        batchEventData.getMapName();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCaller() {
        batchEventData.getCaller();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetEventType() {
        batchEventData.getEventType();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSequence() {
        batchEventData.getSequence();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetSequence() {
        batchEventData.setSequence(1);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(batchEventData, batchEventData);
        Assert.assertEquals(batchEventData, batchEventDataSameAttribute);
        Assert.assertNotEquals(batchEventData, null);
        Assert.assertNotEquals(batchEventData, new Object());
        Assert.assertEquals(batchEventData, batchEventDataOtherSource);
        Assert.assertEquals(batchEventData, batchEventDataOtherPartitionId);
        Assert.assertNotEquals(batchEventData, batchEventDataOtherEvent);
        Assert.assertNotEquals(batchEventData, batchEventDataNoEvent);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(batchEventData.hashCode(), batchEventData.hashCode());
        Assert.assertEquals(batchEventData.hashCode(), batchEventDataSameAttribute.hashCode());
        Assert.assertEquals(batchEventData.hashCode(), batchEventDataOtherSource.hashCode());
        Assert.assertEquals(batchEventData.hashCode(), batchEventDataOtherPartitionId.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(batchEventData.hashCode(), batchEventDataOtherEvent.hashCode());
        Assert.assertNotEquals(batchEventData.hashCode(), batchEventDataNoEvent.hashCode());
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(batchEventData.toString(), "BatchEventData");
    }
}

