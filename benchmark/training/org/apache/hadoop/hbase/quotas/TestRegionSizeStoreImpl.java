/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.quotas;


import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SmallTests.class })
public class TestRegionSizeStoreImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionSizeStoreImpl.class);

    private static final RegionInfo INFOA = RegionInfoBuilder.newBuilder(TableName.valueOf("TEST")).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();

    private static final RegionInfo INFOB = RegionInfoBuilder.newBuilder(TableName.valueOf("TEST")).setStartKey(Bytes.toBytes("b")).setEndKey(Bytes.toBytes("c")).build();

    @Test
    public void testSizeUpdates() {
        RegionSizeStore store = new RegionSizeStoreImpl();
        Assert.assertTrue(store.isEmpty());
        Assert.assertEquals(0, store.size());
        store.put(TestRegionSizeStoreImpl.INFOA, 1024L);
        Assert.assertFalse(store.isEmpty());
        Assert.assertEquals(1, store.size());
        Assert.assertEquals(1024L, store.getRegionSize(TestRegionSizeStoreImpl.INFOA).getSize());
        store.put(TestRegionSizeStoreImpl.INFOA, 2048L);
        Assert.assertEquals(1, store.size());
        Assert.assertEquals(2048L, store.getRegionSize(TestRegionSizeStoreImpl.INFOA).getSize());
        store.incrementRegionSize(TestRegionSizeStoreImpl.INFOA, 512L);
        Assert.assertEquals(1, store.size());
        Assert.assertEquals((2048L + 512L), store.getRegionSize(TestRegionSizeStoreImpl.INFOA).getSize());
        store.remove(TestRegionSizeStoreImpl.INFOA);
        Assert.assertTrue(store.isEmpty());
        Assert.assertEquals(0, store.size());
        store.put(TestRegionSizeStoreImpl.INFOA, 64L);
        store.put(TestRegionSizeStoreImpl.INFOB, 128L);
        Assert.assertEquals(2, store.size());
        Map<RegionInfo, RegionSize> records = new HashMap<>();
        for (Map.Entry<RegionInfo, RegionSize> entry : store) {
            records.put(entry.getKey(), entry.getValue());
        }
        Assert.assertEquals(64L, records.remove(TestRegionSizeStoreImpl.INFOA).getSize());
        Assert.assertEquals(128L, records.remove(TestRegionSizeStoreImpl.INFOB).getSize());
        Assert.assertTrue(records.isEmpty());
    }

    @Test
    public void testNegativeDeltaForMissingRegion() {
        RegionSizeStore store = new RegionSizeStoreImpl();
        Assert.assertNull(store.getRegionSize(TestRegionSizeStoreImpl.INFOA));
        // We shouldn't allow a negative size to enter the RegionSizeStore. Getting a negative size
        // like this shouldn't be possible, but we can prevent the bad state from propagating and
        // getting worse.
        store.incrementRegionSize(TestRegionSizeStoreImpl.INFOA, (-5));
        Assert.assertNotNull(store.getRegionSize(TestRegionSizeStoreImpl.INFOA));
        Assert.assertEquals(0, store.getRegionSize(TestRegionSizeStoreImpl.INFOA).getSize());
    }
}

