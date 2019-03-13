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
package com.hazelcast.replicatedmap.impl.record;


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
public class ReplicatedRecordTest {
    private ReplicatedRecord<String, String> replicatedRecord;

    private ReplicatedRecord<String, String> replicatedRecordSameAttributes;

    private ReplicatedRecord<String, String> replicatedRecordOtherKey;

    private ReplicatedRecord<String, String> replicatedRecordOtherValue;

    private ReplicatedRecord<String, String> replicatedRecordOtherTtl;

    private ReplicatedRecord<String, String> tombStone;

    @Test
    public void testGetKey() {
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("key", replicatedRecord.getKey());
        Assert.assertEquals(1, replicatedRecord.getHits());
    }

    @Test
    public void testGetKeyInternal() {
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("key", replicatedRecord.getKeyInternal());
        Assert.assertEquals(0, replicatedRecord.getHits());
    }

    @Test
    public void testGetValue() {
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("value", replicatedRecord.getValue());
        Assert.assertEquals(1, replicatedRecord.getHits());
    }

    @Test
    public void testGetValueInternal() {
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("value", replicatedRecord.getValueInternal());
        Assert.assertEquals(0, replicatedRecord.getHits());
    }

    @Test
    public void testGetTombStone() {
        Assert.assertFalse(replicatedRecord.isTombstone());
        Assert.assertTrue(tombStone.isTombstone());
    }

    @Test
    public void testGetTtlMillis() {
        Assert.assertEquals(0, replicatedRecord.getTtlMillis());
        Assert.assertEquals(1, replicatedRecordOtherTtl.getTtlMillis());
    }

    @Test
    public void testSetValue() {
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("value", replicatedRecord.getValueInternal());
        replicatedRecord.setValue("newValue", 0);
        Assert.assertEquals(1, replicatedRecord.getHits());
        Assert.assertEquals("newValue", replicatedRecord.getValueInternal());
    }

    @Test
    public void testSetValueInternal() {
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("value", replicatedRecord.getValueInternal());
        replicatedRecord.setValueInternal("newValue", 0);
        Assert.assertEquals(0, replicatedRecord.getHits());
        Assert.assertEquals("newValue", replicatedRecord.getValueInternal());
    }

    @Test
    public void testGetUpdateTime() {
        long lastUpdateTime = replicatedRecord.getUpdateTime();
        HazelcastTestSupport.sleepAtLeastMillis(100);
        replicatedRecord.setValue("newValue", 0);
        Assert.assertTrue("replicatedRecord.getUpdateTime() should return a greater update time", ((replicatedRecord.getUpdateTime()) > lastUpdateTime));
    }

    @Test
    public void testSetUpdateTime() {
        replicatedRecord.setUpdateTime(2342);
        Assert.assertEquals(2342, replicatedRecord.getUpdateTime());
    }

    @Test
    public void testSetHits() {
        replicatedRecord.setHits(4223);
        Assert.assertEquals(4223, replicatedRecord.getHits());
    }

    @Test
    public void getLastAccessTime() {
        long lastAccessTime = replicatedRecord.getLastAccessTime();
        HazelcastTestSupport.sleepAtLeastMillis(100);
        replicatedRecord.setValue("newValue", 0);
        Assert.assertTrue("replicatedRecord.getLastAccessTime() should return a greater access time", ((replicatedRecord.getLastAccessTime()) > lastAccessTime));
    }

    @Test
    public void testSetAccessTime() {
        replicatedRecord.setLastAccessTime(1234);
        Assert.assertEquals(1234, replicatedRecord.getLastAccessTime());
    }

    @Test
    public void testCreationTime() {
        replicatedRecord.setCreationTime(4321);
        Assert.assertEquals(4321, replicatedRecord.getCreationTime());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(replicatedRecord, replicatedRecord);
        Assert.assertEquals(replicatedRecord, replicatedRecordSameAttributes);
        Assert.assertNotEquals(replicatedRecord, null);
        Assert.assertNotEquals(replicatedRecord, new Object());
        Assert.assertNotEquals(replicatedRecord, replicatedRecordOtherKey);
        Assert.assertNotEquals(replicatedRecord, replicatedRecordOtherValue);
        Assert.assertNotEquals(replicatedRecord, replicatedRecordOtherTtl);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(replicatedRecord.hashCode(), replicatedRecord.hashCode());
        Assert.assertEquals(replicatedRecord.hashCode(), replicatedRecordSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(replicatedRecord.hashCode(), replicatedRecordOtherKey.hashCode());
        Assert.assertNotEquals(replicatedRecord.hashCode(), replicatedRecordOtherValue.hashCode());
        Assert.assertNotEquals(replicatedRecord.hashCode(), replicatedRecordOtherTtl.hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertNotNull(replicatedRecord.toString());
    }
}

