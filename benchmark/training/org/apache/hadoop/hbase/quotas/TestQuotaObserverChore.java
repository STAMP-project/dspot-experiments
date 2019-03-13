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


import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Non-HBase cluster unit tests for {@link QuotaObserverChore}.
 */
@Category(SmallTests.class)
public class TestQuotaObserverChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaObserverChore.class);

    private Connection conn;

    private QuotaObserverChore chore;

    @Test
    public void testNumRegionsForTable() {
        TableName tn1 = TableName.valueOf("t1");
        TableName tn2 = TableName.valueOf("t2");
        TableName tn3 = TableName.valueOf("t3");
        final int numTable1Regions = 10;
        final int numTable2Regions = 15;
        final int numTable3Regions = 8;
        Map<RegionInfo, Long> regionReports = new HashMap<>();
        for (int i = 0; i < numTable1Regions; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < numTable2Regions; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn2).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < numTable3Regions; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn3).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        TableQuotaSnapshotStore store = new TableQuotaSnapshotStore(conn, chore, regionReports);
        Mockito.when(chore.getTableSnapshotStore()).thenReturn(store);
        Assert.assertEquals(numTable1Regions, Iterables.size(store.filterBySubject(tn1)));
        Assert.assertEquals(numTable2Regions, Iterables.size(store.filterBySubject(tn2)));
        Assert.assertEquals(numTable3Regions, Iterables.size(store.filterBySubject(tn3)));
    }
}

