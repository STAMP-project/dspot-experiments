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
package org.apache.hadoop.hbase.quotas;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.MasterServices;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(SmallTests.class)
public class TestMasterQuotaManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterQuotaManager.class);

    @Test
    public void testUninitializedQuotaManangerDoesNotFail() {
        MasterServices masterServices = Mockito.mock(MasterServices.class);
        MasterQuotaManager manager = new MasterQuotaManager(masterServices);
        manager.addRegionSize(null, 0, 0);
        Assert.assertNotNull(manager.snapshotRegionSizes());
    }

    @Test
    public void testOldEntriesRemoved() {
        MasterServices masterServices = Mockito.mock(MasterServices.class);
        MasterQuotaManager manager = new MasterQuotaManager(masterServices);
        manager.initializeRegionSizes();
        // Mock out some regions
        TableName tableName = TableName.valueOf("foo");
        HRegionInfo region1 = new HRegionInfo(tableName, null, Bytes.toBytes("a"));
        HRegionInfo region2 = new HRegionInfo(tableName, Bytes.toBytes("a"), Bytes.toBytes("b"));
        HRegionInfo region3 = new HRegionInfo(tableName, Bytes.toBytes("b"), Bytes.toBytes("c"));
        HRegionInfo region4 = new HRegionInfo(tableName, Bytes.toBytes("c"), Bytes.toBytes("d"));
        HRegionInfo region5 = new HRegionInfo(tableName, Bytes.toBytes("d"), null);
        final long size = 0;
        long time1 = 10;
        manager.addRegionSize(region1, size, time1);
        manager.addRegionSize(region2, size, time1);
        long time2 = 20;
        manager.addRegionSize(region3, size, time2);
        manager.addRegionSize(region4, size, time2);
        long time3 = 30;
        manager.addRegionSize(region5, size, time3);
        Assert.assertEquals(5, manager.snapshotRegionSizes().size());
        // Prune nothing
        Assert.assertEquals(0, manager.pruneEntriesOlderThan(0));
        Assert.assertEquals(5, manager.snapshotRegionSizes().size());
        Assert.assertEquals(0, manager.pruneEntriesOlderThan(10));
        Assert.assertEquals(5, manager.snapshotRegionSizes().size());
        // Prune the elements at time1
        Assert.assertEquals(2, manager.pruneEntriesOlderThan(15));
        Assert.assertEquals(3, manager.snapshotRegionSizes().size());
        // Prune the elements at time2
        Assert.assertEquals(2, manager.pruneEntriesOlderThan(30));
        Assert.assertEquals(1, manager.snapshotRegionSizes().size());
    }
}

