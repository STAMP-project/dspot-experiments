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
package com.hazelcast.internal.nearcache.impl.store;


import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractNearCacheRecordStoreTest {
    private static final int KEY = 23;

    private static final int VALUE1 = 42;

    private static final int VALUE2 = 2342;

    private SerializationService serializationService;

    private AbstractNearCacheRecordStore store;

    @Test
    @SuppressWarnings("unchecked")
    public void testRecordCreation_withReservation() {
        Data keyData = serializationService.toData(AbstractNearCacheRecordStoreTest.KEY);
        long reservationId1 = store.tryReserveForUpdate(AbstractNearCacheRecordStoreTest.KEY, keyData);
        long reservationId2 = store.tryReserveForUpdate(AbstractNearCacheRecordStoreTest.KEY, keyData);
        // only one reservation ID is given for the same key
        Assert.assertNotEquals(NearCacheRecord.NOT_RESERVED, reservationId1);
        Assert.assertEquals(NearCacheRecord.NOT_RESERVED, reservationId2);
        assertRecordState(reservationId1);
        // cannot publish the value with the wrong reservation ID
        Assert.assertNull(store.tryPublishReserved(AbstractNearCacheRecordStoreTest.KEY, AbstractNearCacheRecordStoreTest.VALUE2, reservationId2, true));
        assertRecordState(reservationId1);
        // can publish the value with the correct reservation ID
        Assert.assertEquals(AbstractNearCacheRecordStoreTest.VALUE1, store.tryPublishReserved(AbstractNearCacheRecordStoreTest.KEY, AbstractNearCacheRecordStoreTest.VALUE1, reservationId1, true));
        assertRecordState(NearCacheRecord.READ_PERMITTED);
        // cannot change a published value with the wrong reservation ID
        Assert.assertEquals(AbstractNearCacheRecordStoreTest.VALUE1, store.tryPublishReserved(AbstractNearCacheRecordStoreTest.KEY, AbstractNearCacheRecordStoreTest.VALUE2, reservationId2, true));
        assertRecordState(NearCacheRecord.READ_PERMITTED);
        // cannot change a published value with the correct reservation ID
        Assert.assertEquals(AbstractNearCacheRecordStoreTest.VALUE1, store.tryPublishReserved(AbstractNearCacheRecordStoreTest.KEY, AbstractNearCacheRecordStoreTest.VALUE2, reservationId1, true));
        assertRecordState(NearCacheRecord.READ_PERMITTED);
        // only a single record has been created
        Assert.assertEquals(1, store.records.size());
        Assert.assertEquals(1, store.getNearCacheStats().getOwnedEntryCount());
    }
}

