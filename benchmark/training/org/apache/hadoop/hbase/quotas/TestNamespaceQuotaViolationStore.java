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


import NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR;
import QuotaProtos.SpaceViolationPolicy.DISABLE;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
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


/**
 * Test class for {@link NamespaceQuotaSnapshotStore}.
 */
@Category(SmallTests.class)
public class TestNamespaceQuotaViolationStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespaceQuotaViolationStore.class);

    private static final long ONE_MEGABYTE = 1024L * 1024L;

    private Connection conn;

    private QuotaObserverChore chore;

    private Map<RegionInfo, Long> regionReports;

    private NamespaceQuotaSnapshotStore store;

    @Test
    public void testGetSpaceQuota() throws Exception {
        NamespaceQuotaSnapshotStore mockStore = Mockito.mock(NamespaceQuotaSnapshotStore.class);
        Mockito.when(mockStore.getSpaceQuota(ArgumentMatchers.any())).thenCallRealMethod();
        Quotas quotaWithSpace = Quotas.newBuilder().setSpace(SpaceQuota.newBuilder().setSoftLimit(1024L).setViolationPolicy(DISABLE).build()).build();
        Quotas quotaWithoutSpace = Quotas.newBuilder().build();
        AtomicReference<Quotas> quotaRef = new AtomicReference<>();
        Mockito.when(mockStore.getQuotaForNamespace(ArgumentMatchers.any())).then(new Answer<Quotas>() {
            @Override
            public Quotas answer(InvocationOnMock invocation) throws Throwable {
                return quotaRef.get();
            }
        });
        quotaRef.set(quotaWithSpace);
        Assert.assertEquals(quotaWithSpace.getSpace(), mockStore.getSpaceQuota("ns"));
        quotaRef.set(quotaWithoutSpace);
        Assert.assertNull(mockStore.getSpaceQuota("ns"));
    }

    @Test
    public void testTargetViolationState() throws IOException {
        mockNoSnapshotSizes();
        final String NS = "ns";
        TableName tn1 = TableName.valueOf(NS, "tn1");
        TableName tn2 = TableName.valueOf(NS, "tn2");
        TableName tn3 = TableName.valueOf("tn3");
        SpaceQuota quota = SpaceQuota.newBuilder().setSoftLimit(TestNamespaceQuotaViolationStore.ONE_MEGABYTE).setViolationPolicy(ProtobufUtil.toProtoViolationPolicy(SpaceViolationPolicy.DISABLE)).build();
        // Create some junk data to filter. Makes sure it's so large that it would
        // immediately violate the quota.
        for (int i = 0; i < 3; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn3).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), (5L * (TestNamespaceQuotaViolationStore.ONE_MEGABYTE)));
        }
        regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(0)).setEndKey(Bytes.toBytes(1)).build(), (1024L * 512L));
        regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(1)).setEndKey(Bytes.toBytes(2)).build(), (1024L * 256L));
        // Below the quota
        Assert.assertEquals(false, store.getTargetState(NS, quota).getQuotaStatus().isInViolation());
        regionReports.put(RegionInfoBuilder.newBuilder(tn2).setStartKey(Bytes.toBytes(2)).setEndKey(Bytes.toBytes(3)).build(), (1024L * 256L));
        // Equal to the quota is still in observance
        Assert.assertEquals(false, store.getTargetState(NS, quota).getQuotaStatus().isInViolation());
        regionReports.put(RegionInfoBuilder.newBuilder(tn2).setStartKey(Bytes.toBytes(3)).setEndKey(Bytes.toBytes(4)).build(), 1024L);
        // Exceeds the quota, should be in violation
        Assert.assertEquals(true, store.getTargetState(NS, quota).getQuotaStatus().isInViolation());
        Assert.assertEquals(SpaceViolationPolicy.DISABLE, store.getTargetState(NS, quota).getQuotaStatus().getPolicy().get());
    }

    @Test
    public void testFilterRegionsByNamespace() {
        TableName tn1 = TableName.valueOf("foo");
        TableName tn2 = TableName.valueOf("sn", "bar");
        TableName tn3 = TableName.valueOf("ns", "foo");
        TableName tn4 = TableName.valueOf("ns", "bar");
        Assert.assertEquals(0, size(store.filterBySubject("asdf")));
        for (int i = 0; i < 5; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn1).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < 3; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn2).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < 10; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn3).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        for (int i = 0; i < 8; i++) {
            regionReports.put(RegionInfoBuilder.newBuilder(tn4).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build(), 0L);
        }
        Assert.assertEquals(26, regionReports.size());
        Assert.assertEquals(5, size(store.filterBySubject(DEFAULT_NAMESPACE_NAME_STR)));
        Assert.assertEquals(3, size(store.filterBySubject("sn")));
        Assert.assertEquals(18, size(store.filterBySubject("ns")));
    }
}

