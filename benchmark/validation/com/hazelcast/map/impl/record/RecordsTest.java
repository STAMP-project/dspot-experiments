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


import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.Clock;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RecordsTest extends HazelcastTestSupport {
    private SerializationService serializationService;

    @Test
    public void getValueOrCachedValue_whenRecordIsNotCachable_thenDoNotCache() {
        String objectPayload = "foo";
        Data dataPayload = serializationService.toData(objectPayload);
        Record record = new DataRecord(dataPayload);
        Object value = Records.getValueOrCachedValue(record, null);
        Assert.assertSame(dataPayload, value);
    }

    @Test
    public void getValueOrCachedValue_whenRecordIsCachedDataRecordWithStats_thenCache() {
        String objectPayload = "foo";
        Data dataPayload = serializationService.toData(objectPayload);
        Record record = new CachedDataRecordWithStats(dataPayload);
        Object firstDeserializedValue = Records.getValueOrCachedValue(record, serializationService);
        Assert.assertEquals(objectPayload, firstDeserializedValue);
        // we don't need serialization service for the 2nd call
        Object secondDeserializedValue = Records.getValueOrCachedValue(record, null);
        Assert.assertSame(firstDeserializedValue, secondDeserializedValue);
    }

    @Test
    public void getValueOrCachedValue_whenRecordIsCachedDataRecord_thenCache() {
        String objectPayload = "foo";
        Data dataPayload = serializationService.toData(objectPayload);
        Record record = new CachedDataRecord(dataPayload);
        Object firstDeserializedValue = Records.getValueOrCachedValue(record, serializationService);
        Assert.assertEquals(objectPayload, firstDeserializedValue);
        // we don't need serialization service for the 2nd call
        Object secondDeserializedValue = Records.getValueOrCachedValue(record, null);
        Assert.assertSame(firstDeserializedValue, secondDeserializedValue);
    }

    @Test
    public void givenCachedDataRecord_whenThreadIsInside_thenGetValueOrCachedValueReturnsTheThread() {
        // given
        CachedDataRecord record = new CachedDataRecord();
        // when
        RecordsTest.SerializableThread objectPayload = new RecordsTest.SerializableThread();
        Data dataPayload = serializationService.toData(objectPayload);
        record.setValue(dataPayload);
        // then
        Object cachedValue = Records.getValueOrCachedValue(record, serializationService);
        HazelcastTestSupport.assertInstanceOf(RecordsTest.SerializableThread.class, cachedValue);
    }

    @Test
    public void givenCachedDataRecordValueIsThread_whenCachedValueIsCreated_thenGetCachedValueReturnsTheThread() {
        // given
        RecordsTest.SerializableThread objectPayload = new RecordsTest.SerializableThread();
        Data dataPayload = serializationService.toData(objectPayload);
        CachedDataRecord record = new CachedDataRecord(dataPayload);
        // when
        Records.getValueOrCachedValue(record, serializationService);
        // then
        Object cachedValue = Records.getCachedValue(record);
        HazelcastTestSupport.assertInstanceOf(RecordsTest.SerializableThread.class, cachedValue);
    }

    @Test
    public void applyRecordInfo() {
        // Shared key&value by referenceRecord and recordInfo-applied-recordInfoAppliedRecord
        Data key = new HeapData();
        Data value = new HeapData();
        // Create recordInfo from a reference record
        Record referenceRecord = new DataRecordWithStats();
        referenceRecord.setKey(key);
        referenceRecord.setValue(value);
        referenceRecord.setHits(123);
        referenceRecord.setVersion(12);
        RecordInfo recordInfo = RecordsTest.toRecordInfo(referenceRecord);
        // Apply created recordInfo to recordInfoAppliedRecord
        Record recordInfoAppliedRecord = new DataRecordWithStats();
        recordInfoAppliedRecord.setKey(key);
        recordInfoAppliedRecord.setValue(value);
        Records.applyRecordInfo(recordInfoAppliedRecord, recordInfo);
        // Check recordInfo applied correctly to recordInfoAppliedRecord
        Assert.assertEquals(referenceRecord, recordInfoAppliedRecord);
    }

    @Test
    public void buildRecordInfo() throws Exception {
        long now = Clock.currentTimeMillis();
        Record record = RecordsTest.newRecord(now);
        RecordInfo recordInfo = Records.buildRecordInfo(record);
        Assert.assertEquals(now, recordInfo.getCreationTime());
        Assert.assertEquals(now, recordInfo.getLastAccessTime());
        Assert.assertEquals(now, recordInfo.getLastUpdateTime());
        Assert.assertEquals(12, recordInfo.getHits());
        Assert.assertEquals(123, recordInfo.getVersion());
        Assert.assertEquals(now, recordInfo.getExpirationTime());
        Assert.assertEquals(now, recordInfo.getLastStoredTime());
    }

    private static class SerializableThread extends Thread implements Serializable {}
}

