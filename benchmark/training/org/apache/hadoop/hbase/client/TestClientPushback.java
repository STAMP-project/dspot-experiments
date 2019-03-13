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


import AsyncProcessTask.SubmittedRows.AT_LEAST_ONE;
import MetricsConnection.RegionStats;
import MetricsConnection.RunnerStats;
import ServerStatistics.RegionStatistics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.backoff.ClientBackoffPolicy;
import org.apache.hadoop.hbase.client.backoff.ExponentialClientBackoffPolicy;
import org.apache.hadoop.hbase.client.backoff.ServerStatistics;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that we can actually send and use region metrics to slowdown client writes
 */
@Category(MediumTests.class)
public class TestClientPushback {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClientPushback.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestClientPushback.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName tableName = TableName.valueOf("client-pushback");

    private static final byte[] family = Bytes.toBytes("f");

    private static final byte[] qualifier = Bytes.toBytes("q");

    private static final long flushSizeBytes = 512;

    @Test
    public void testClientTracksServerPushback() throws Exception {
        Configuration conf = TestClientPushback.UTIL.getConfiguration();
        ClusterConnection conn = ((ClusterConnection) (ConnectionFactory.createConnection(conf)));
        BufferedMutatorImpl mutator = ((BufferedMutatorImpl) (conn.getBufferedMutator(TestClientPushback.tableName)));
        HRegionServer rs = TestClientPushback.UTIL.getHBaseCluster().getRegionServer(0);
        Region region = rs.getRegions(TestClientPushback.tableName).get(0);
        TestClientPushback.LOG.debug(("Writing some data to " + (TestClientPushback.tableName)));
        // write some data
        Put p = new Put(Bytes.toBytes("row"));
        p.addColumn(TestClientPushback.family, TestClientPushback.qualifier, Bytes.toBytes("value1"));
        mutator.mutate(p);
        mutator.flush();
        // get the current load on RS. Hopefully memstore isn't flushed since we wrote the the data
        int load = ((int) (((region.getMemStoreHeapSize()) * 100) / (TestClientPushback.flushSizeBytes)));
        TestClientPushback.LOG.debug(("Done writing some data to " + (TestClientPushback.tableName)));
        // get the stats for the region hosting our table
        ClientBackoffPolicy backoffPolicy = conn.getBackoffPolicy();
        Assert.assertTrue("Backoff policy is not correctly configured", (backoffPolicy instanceof ExponentialClientBackoffPolicy));
        ServerStatisticTracker stats = conn.getStatisticsTracker();
        Assert.assertNotNull("No stats configured for the client!", stats);
        // get the names so we can query the stats
        ServerName server = rs.getServerName();
        byte[] regionName = region.getRegionInfo().getRegionName();
        // check to see we found some load on the memstore
        ServerStatistics serverStats = stats.getServerStatsForTesting(server);
        ServerStatistics.RegionStatistics regionStats = serverStats.getStatsForRegion(regionName);
        Assert.assertEquals("We did not find some load on the memstore", load, regionStats.getMemStoreLoadPercent());
        // check that the load reported produces a nonzero delay
        long backoffTime = backoffPolicy.getBackoffTime(server, regionName, serverStats);
        Assert.assertNotEquals("Reported load does not produce a backoff", 0, backoffTime);
        TestClientPushback.LOG.debug(((((("Backoff calculated for " + (region.getRegionInfo().getRegionNameAsString())) + " @ ") + server) + " is ") + backoffTime));
        // Reach into the connection and submit work directly to AsyncProcess so we can
        // monitor how long the submission was delayed via a callback
        List<Row> ops = new ArrayList<>(1);
        ops.add(p);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicLong endTime = new AtomicLong();
        long startTime = EnvironmentEdgeManager.currentTime();
        Batch.Callback<Result> callback = (byte[] r,byte[] row,Result result) -> {
            endTime.set(EnvironmentEdgeManager.currentTime());
            latch.countDown();
        };
        AsyncProcessTask<Result> task = AsyncProcessTask.newBuilder(callback).setPool(mutator.getPool()).setTableName(TestClientPushback.tableName).setRowAccess(ops).setSubmittedRows(AT_LEAST_ONE).setOperationTimeout(conn.getConnectionConfiguration().getOperationTimeout()).setRpcTimeout((60 * 1000)).build();
        mutator.getAsyncProcess().submit(task);
        // Currently the ExponentialClientBackoffPolicy under these test conditions
        // produces a backoffTime of 151 milliseconds. This is long enough so the
        // wait and related checks below are reasonable. Revisit if the backoff
        // time reported by above debug logging has significantly deviated.
        String name = ((server.getServerName()) + ",") + (Bytes.toStringBinary(regionName));
        MetricsConnection.RegionStats rsStats = conn.getConnectionMetrics().serverStats.get(server).get(regionName);
        Assert.assertEquals(name, rsStats.name);
        Assert.assertEquals(rsStats.heapOccupancyHist.getSnapshot().getMean(), ((double) (regionStats.getHeapOccupancyPercent())), 0.1);
        Assert.assertEquals(rsStats.memstoreLoadHist.getSnapshot().getMean(), ((double) (regionStats.getMemStoreLoadPercent())), 0.1);
        MetricsConnection.RunnerStats runnerStats = conn.getConnectionMetrics().runnerStats;
        Assert.assertEquals(1, runnerStats.delayRunners.getCount());
        Assert.assertEquals(1, runnerStats.normalRunners.getCount());
        Assert.assertEquals("", runnerStats.delayIntevalHist.getSnapshot().getMean(), ((double) (backoffTime)), 0.1);
        latch.await((backoffTime * 2), TimeUnit.MILLISECONDS);
        Assert.assertNotEquals("AsyncProcess did not submit the work time", 0, endTime.get());
        Assert.assertTrue("AsyncProcess did not delay long enough", (((endTime.get()) - startTime) >= backoffTime));
    }

    @Test
    public void testMutateRowStats() throws IOException {
        Configuration conf = TestClientPushback.UTIL.getConfiguration();
        ClusterConnection conn = ((ClusterConnection) (ConnectionFactory.createConnection(conf)));
        Table table = conn.getTable(TestClientPushback.tableName);
        HRegionServer rs = TestClientPushback.UTIL.getHBaseCluster().getRegionServer(0);
        Region region = rs.getRegions(TestClientPushback.tableName).get(0);
        RowMutations mutations = new RowMutations(Bytes.toBytes("row"));
        Put p = new Put(Bytes.toBytes("row"));
        p.addColumn(TestClientPushback.family, TestClientPushback.qualifier, Bytes.toBytes("value2"));
        mutations.add(p);
        table.mutateRow(mutations);
        ServerStatisticTracker stats = conn.getStatisticsTracker();
        Assert.assertNotNull("No stats configured for the client!", stats);
        // get the names so we can query the stats
        ServerName server = rs.getServerName();
        byte[] regionName = region.getRegionInfo().getRegionName();
        // check to see we found some load on the memstore
        ServerStatistics serverStats = stats.getServerStatsForTesting(server);
        ServerStatistics.RegionStatistics regionStats = serverStats.getStatsForRegion(regionName);
        Assert.assertNotNull(regionStats);
        Assert.assertTrue(((regionStats.getMemStoreLoadPercent()) > 0));
    }
}

