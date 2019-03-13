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


import SpaceViolationPolicy.DISABLE;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.Quotas;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.SpaceQuota;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static SpaceViolationPolicy.DISABLE;


/**
 * Test class for {@link TableQuotaSnapshotStore}.
 */
@Category(SmallTests.class)
public class TestTableQuotaViolationStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableQuotaViolationStore.class);

    private static final long ONE_MEGABYTE = 1024L * 1024L;

    private Connection conn;

    private QuotaObserverChore chore;

    private Map<RegionInfo, Long> regionReports;

    private TableQuotaSnapshotStore store;

    @Test
    public void testFilterRegionsByTable() throws Exception {
        TableName tn1 = TableName.valueOf("foo");
        TableName tn2 = TableName.valueOf("bar");
        TableName tn3 = TableName.valueOf("ns", "foo");
        Assert.assertEquals(0, size(store.filterBySubject(tn1)));
        for (int i = 0; i < 5; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < 3; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn2).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < 10; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn3).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        Assert.assertEquals(18, regionReports.size());
        Assert.assertEquals(5, size(store.filterBySubject(tn1)));
        Assert.assertEquals(3, size(store.filterBySubject(tn2)));
        Assert.assertEquals(10, size(store.filterBySubject(tn3)));
    }

    @Test
    public void testTargetViolationState() throws IOException {
        mockNoSnapshotSizes();
        TableName tn1 = TableName.valueOf("violation1");
        TableName tn2 = TableName.valueOf("observance1");
        TableName tn3 = TableName.valueOf("observance2");
        SpaceQuota quota = SpaceQuota.newBuilder().setSoftLimit((1024L * 1024L)).setViolationPolicy(ProtobufUtil.toProtoViolationPolicy(DISABLE)).build();
        // Create some junk data to filter. Makes sure it's so large that it would
        // immediately violate the quota.
        for (int i = 0; i < 3; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn2).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), (5L * (TestTableQuotaViolationStore.ONE_MEGABYTE)));
            regionReports.put(RegionInfoBuilder.newBuilder(tn3).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), (5L * (TestTableQuotaViolationStore.ONE_MEGABYTE)));
        }
        regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(0)).setEndKey(Bytes.toBytes(1)).build(), (1024L * 512L));
        regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(1)).setEndKey(Bytes.toBytes(2)).build(), (1024L * 256L));
        SpaceQuotaSnapshot tn1Snapshot = new SpaceQuotaSnapshot(SpaceQuotaStatus.notInViolation(), (1024L * 768L), (1024L * 1024L));
        // Below the quota
        Assert.assertEquals(tn1Snapshot, store.getTargetState(tn1, quota));
        regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(2)).setEndKey(Bytes.toBytes(3)).build(), (1024L * 256L));
        tn1Snapshot = new SpaceQuotaSnapshot(SpaceQuotaStatus.notInViolation(), (1024L * 1024L), (1024L * 1024L));
        // Equal to the quota is still in observance
        Assert.assertEquals(tn1Snapshot, store.getTargetState(tn1, quota));
        regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(3)).setEndKey(Bytes.toBytes(4)).build(), 1024L);
        tn1Snapshot = new SpaceQuotaSnapshot(new SpaceQuotaStatus(DISABLE), ((1024L * 1024L) + 1024L), (1024L * 1024L));
        // Exceeds the quota, should be in violation
        Assert.assertEquals(tn1Snapshot, store.getTargetState(tn1, quota));
    }

    @Test
    public void testGetSpaceQuota() throws Exception {
        TableQuotaSnapshotStore mockStore = Mockito.mock(TableQuotaSnapshotStore.class);
        Mockito.when(mockStore.getSpaceQuota(ArgumentMatchers.any())).thenCallRealMethod();
        Quotas quotaWithSpace = Quotas.newBuilder().setSpace(SpaceQuota.newBuilder().setSoftLimit(1024L).setViolationPolicy(QuotaProtos.SpaceViolationPolicy.DISABLE).build()).build();
        Quotas quotaWithoutSpace = Quotas.newBuilder().build();
        AtomicReference<Quotas> quotaRef = new AtomicReference<>();
        Mockito.when(mockStore.getQuotaForTable(ArgumentMatchers.any())).then(new Answer<Quotas>() {
            @Override
            public Quotas answer(InvocationOnMock invocation) throws Throwable {
                return quotaRef.get();
            }
        });
        quotaRef.set(quotaWithSpace);
        Assert.assertEquals(quotaWithSpace.getSpace(), mockStore.getSpaceQuota(TableName.valueOf("foo")));
        quotaRef.set(quotaWithoutSpace);
        Assert.assertNull(mockStore.getSpaceQuota(TableName.valueOf("foo")));
    }
}

