/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.regionserver;


import FlushType.ABOVE_OFFHEAP_HIGHER_MARK;
import FlushType.ABOVE_OFFHEAP_LOWER_MARK;
import FlushType.ABOVE_ONHEAP_HIGHER_MARK;
import FlushType.ABOVE_ONHEAP_LOWER_MARK;
import MemorySizeUtil.OFFHEAP_MEMSTORE_SIZE_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SmallTests.class)
public class TestRegionServerAccounting {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerAccounting.class);

    private static final float DEFAULT_MEMSTORE_SIZE = 0.2F;

    private static Configuration conf;

    @Test
    public void testOnheapMemstoreHigherWaterMarkLimits() {
        RegionServerAccounting regionServerAccounting = new RegionServerAccounting(TestRegionServerAccounting.conf);
        long dataSize = regionServerAccounting.getGlobalMemStoreLimit();
        MemStoreSize memstoreSize = new MemStoreSize(dataSize, dataSize, 0, 0);
        regionServerAccounting.incGlobalMemStoreSize(memstoreSize);
        Assert.assertEquals(ABOVE_ONHEAP_HIGHER_MARK, regionServerAccounting.isAboveHighWaterMark());
    }

    @Test
    public void testOnheapMemstoreLowerWaterMarkLimits() {
        RegionServerAccounting regionServerAccounting = new RegionServerAccounting(TestRegionServerAccounting.conf);
        long dataSize = regionServerAccounting.getGlobalMemStoreLimit();
        MemStoreSize memstoreSize = new MemStoreSize(dataSize, dataSize, 0, 0);
        regionServerAccounting.incGlobalMemStoreSize(memstoreSize);
        Assert.assertEquals(ABOVE_ONHEAP_LOWER_MARK, regionServerAccounting.isAboveLowWaterMark());
    }

    @Test
    public void testOffheapMemstoreHigherWaterMarkLimitsDueToDataSize() {
        // setting 1G as offheap data size
        TestRegionServerAccounting.conf.setLong(OFFHEAP_MEMSTORE_SIZE_KEY, (1L * 1024L));
        // try for default cases
        RegionServerAccounting regionServerAccounting = new RegionServerAccounting(TestRegionServerAccounting.conf);
        // this will breach offheap limit as data size is higher and not due to heap size
        MemStoreSize memstoreSize = new MemStoreSize((((3L * 1024L) * 1024L) * 1024L), 0, (((1L * 1024L) * 1024L) * 1024L), 100);
        regionServerAccounting.incGlobalMemStoreSize(memstoreSize);
        Assert.assertEquals(ABOVE_OFFHEAP_HIGHER_MARK, regionServerAccounting.isAboveHighWaterMark());
    }

    @Test
    public void testOffheapMemstoreHigherWaterMarkLimitsDueToHeapSize() {
        // setting 1G as offheap data size
        TestRegionServerAccounting.conf.setLong(OFFHEAP_MEMSTORE_SIZE_KEY, (1L * 1024L));
        // try for default cases
        RegionServerAccounting regionServerAccounting = new RegionServerAccounting(TestRegionServerAccounting.conf);
        // this will breach higher limit as heap size is higher and not due to offheap size
        long dataSize = regionServerAccounting.getGlobalOnHeapMemStoreLimit();
        MemStoreSize memstoreSize = new MemStoreSize(dataSize, dataSize, 0, 100);
        regionServerAccounting.incGlobalMemStoreSize(memstoreSize);
        Assert.assertEquals(ABOVE_ONHEAP_HIGHER_MARK, regionServerAccounting.isAboveHighWaterMark());
    }

    @Test
    public void testOffheapMemstoreLowerWaterMarkLimitsDueToDataSize() {
        // setting 1G as offheap data size
        TestRegionServerAccounting.conf.setLong(OFFHEAP_MEMSTORE_SIZE_KEY, (1L * 1024L));
        // try for default cases
        RegionServerAccounting regionServerAccounting = new RegionServerAccounting(TestRegionServerAccounting.conf);
        // this will breach offheap limit as data size is higher and not due to heap size
        MemStoreSize memstoreSize = new MemStoreSize((((3L * 1024L) * 1024L) * 1024L), 0, (((1L * 1024L) * 1024L) * 1024L), 100);
        regionServerAccounting.incGlobalMemStoreSize(memstoreSize);
        Assert.assertEquals(ABOVE_OFFHEAP_LOWER_MARK, regionServerAccounting.isAboveLowWaterMark());
    }

    @Test
    public void testOffheapMemstoreLowerWaterMarkLimitsDueToHeapSize() {
        // setting 1G as offheap data size
        TestRegionServerAccounting.conf.setLong(OFFHEAP_MEMSTORE_SIZE_KEY, (1L * 1024L));
        // try for default cases
        RegionServerAccounting regionServerAccounting = new RegionServerAccounting(TestRegionServerAccounting.conf);
        // this will breach higher limit as heap size is higher and not due to offheap size
        long dataSize = regionServerAccounting.getGlobalOnHeapMemStoreLimit();
        MemStoreSize memstoreSize = new MemStoreSize(dataSize, dataSize, 0, 100);
        regionServerAccounting.incGlobalMemStoreSize(memstoreSize);
        Assert.assertEquals(ABOVE_ONHEAP_LOWER_MARK, regionServerAccounting.isAboveLowWaterMark());
    }
}

