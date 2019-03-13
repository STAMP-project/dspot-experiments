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


import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
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
@SuppressWarnings("WeakerAccess")
public class AbstractBaseReplicatedRecordStoreTest extends HazelcastTestSupport {
    AbstractBaseReplicatedRecordStoreTest.TestReplicatedRecordStore recordStore;

    AbstractBaseReplicatedRecordStoreTest.TestReplicatedRecordStore recordStoreSameAttributes;

    AbstractBaseReplicatedRecordStoreTest.TestReplicatedRecordStore recordStoreOtherStorage;

    AbstractBaseReplicatedRecordStoreTest.TestReplicatedRecordStore recordStoreOtherName;

    @Test
    public void testGetRecords() {
        Assert.assertTrue(getRecords().isEmpty());
        put("key1", "value1");
        put("key2", "value2");
        Assert.assertEquals(2, getRecords().size());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(recordStore, recordStore);
        Assert.assertEquals(recordStoreSameAttributes, recordStore);
        Assert.assertNotEquals(recordStore, null);
        Assert.assertNotEquals(recordStore, new Object());
        Assert.assertNotEquals(recordStoreOtherStorage, recordStore);
        Assert.assertNotEquals(recordStoreOtherName, recordStore);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(recordStore.hashCode(), recordStore.hashCode());
        Assert.assertEquals(recordStoreSameAttributes.hashCode(), recordStore.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(recordStoreOtherStorage.hashCode(), recordStore.hashCode());
        Assert.assertNotEquals(recordStoreOtherName.hashCode(), recordStore.hashCode());
    }

    private class TestReplicatedRecordStore extends AbstractReplicatedRecordStore<String, String> {
        TestReplicatedRecordStore(String name, ReplicatedMapService replicatedMapService, int partitionId) {
            super(name, replicatedMapService, partitionId);
        }

        @Override
        public Object unmarshall(Object key) {
            return key;
        }

        @Override
        public Object marshall(Object key) {
            return key;
        }
    }
}

