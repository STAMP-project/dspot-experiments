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
package org.apache.hadoop.hbase.client;


import ByteString.EMPTY;
import MetricsConnection.CallTracker;
import MutationType.APPEND;
import MutationType.DELETE;
import MutationType.INCREMENT;
import MutationType.PUT;
import RegionSpecifierType.REGION_NAME;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.RatioGauge.Ratio;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ClientService;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.GetRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MultiRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MutateRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.HBaseProtos.RegionSpecifier;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MetricsTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, MetricsTests.class, SmallTests.class })
public class TestMetricsConnection {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetricsConnection.class);

    private static MetricsConnection METRICS;

    private static final ExecutorService BATCH_POOL = Executors.newFixedThreadPool(2);

    @Test
    public void testStaticMetrics() throws IOException {
        final byte[] foo = Bytes.toBytes("foo");
        final RegionSpecifier region = RegionSpecifier.newBuilder().setValue(EMPTY).setType(REGION_NAME).build();
        final int loop = 5;
        for (int i = 0; i < loop; i++) {
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Get"), GetRequest.getDefaultInstance(), MetricsConnection.newCallStats());
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Scan"), ScanRequest.getDefaultInstance(), MetricsConnection.newCallStats());
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Multi"), MultiRequest.getDefaultInstance(), MetricsConnection.newCallStats());
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Mutate"), MutateRequest.newBuilder().setMutation(ProtobufUtil.toMutation(APPEND, new Append(foo))).setRegion(region).build(), MetricsConnection.newCallStats());
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Mutate"), MutateRequest.newBuilder().setMutation(ProtobufUtil.toMutation(DELETE, new Delete(foo))).setRegion(region).build(), MetricsConnection.newCallStats());
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Mutate"), MutateRequest.newBuilder().setMutation(ProtobufUtil.toMutation(INCREMENT, new Increment(foo))).setRegion(region).build(), MetricsConnection.newCallStats());
            TestMetricsConnection.METRICS.updateRpc(ClientService.getDescriptor().findMethodByName("Mutate"), MutateRequest.newBuilder().setMutation(ProtobufUtil.toMutation(PUT, new Put(foo))).setRegion(region).build(), MetricsConnection.newCallStats());
        }
        for (MetricsConnection.CallTracker t : new MetricsConnection.CallTracker[]{ TestMetricsConnection.METRICS.getTracker, TestMetricsConnection.METRICS.scanTracker, TestMetricsConnection.METRICS.multiTracker, TestMetricsConnection.METRICS.appendTracker, TestMetricsConnection.METRICS.deleteTracker, TestMetricsConnection.METRICS.incrementTracker, TestMetricsConnection.METRICS.putTracker }) {
            Assert.assertEquals(("Failed to invoke callTimer on " + t), loop, t.callTimer.getCount());
            Assert.assertEquals(("Failed to invoke reqHist on " + t), loop, t.reqHist.getCount());
            Assert.assertEquals(("Failed to invoke respHist on " + t), loop, t.respHist.getCount());
        }
        RatioGauge executorMetrics = ((RatioGauge) (TestMetricsConnection.METRICS.getMetricRegistry().getMetrics().get(TestMetricsConnection.METRICS.getExecutorPoolName())));
        RatioGauge metaMetrics = ((RatioGauge) (TestMetricsConnection.METRICS.getMetricRegistry().getMetrics().get(TestMetricsConnection.METRICS.getMetaPoolName())));
        Assert.assertEquals(Ratio.of(0, 3).getValue(), executorMetrics.getValue(), 0);
        Assert.assertEquals(Double.NaN, metaMetrics.getValue(), 0);
    }
}

