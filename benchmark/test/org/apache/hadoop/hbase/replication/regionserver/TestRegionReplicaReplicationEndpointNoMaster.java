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
package org.apache.hadoop.hbase.replication.regionserver;


import ReplicationEndpoint.Context;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.WALCoprocessor;
import org.apache.hadoop.hbase.coprocessor.WALCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.WALObserver;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.regionserver.TestRegionServerNoMaster;
import org.apache.hadoop.hbase.replication.ReplicationEndpoint;
import org.apache.hadoop.hbase.replication.ReplicationEndpoint.ReplicateContext;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Tests RegionReplicaReplicationEndpoint. Unlike TestRegionReplicaReplicationEndpoint this
 * class contains lower level tests using callables.
 */
@Category({ ReplicationTests.class, MediumTests.class })
public class TestRegionReplicaReplicationEndpointNoMaster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionReplicaReplicationEndpointNoMaster.class);

    private static final int NB_SERVERS = 2;

    private static TableName tableName = TableName.valueOf(TestRegionReplicaReplicationEndpointNoMaster.class.getSimpleName());

    private static Table table;

    private static final byte[] row = Bytes.toBytes("TestRegionReplicaReplicator");

    private static HRegionServer rs0;

    private static HRegionServer rs1;

    private static HRegionInfo hriPrimary;

    private static HRegionInfo hriSecondary;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private static final byte[] f = HConstants.CATALOG_FAMILY;

    static ConcurrentLinkedQueue<Entry> entries = new ConcurrentLinkedQueue<>();

    public static class WALEditCopro implements WALCoprocessor , WALObserver {
        public WALEditCopro() {
            TestRegionReplicaReplicationEndpointNoMaster.entries.clear();
        }

        @Override
        public Optional<WALObserver> getWALObserver() {
            return Optional.of(this);
        }

        @Override
        public void postWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> ctx, RegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
            // only keep primary region's edits
            if ((logKey.getTableName().equals(TestRegionReplicaReplicationEndpointNoMaster.tableName)) && ((info.getReplicaId()) == 0)) {
                // Presume type is a WALKeyImpl
                TestRegionReplicaReplicationEndpointNoMaster.entries.add(new Entry(((WALKeyImpl) (logKey)), logEdit));
            }
        }
    }

    @Test
    public void testReplayCallable() throws Exception {
        // tests replaying the edits to a secondary region replica using the Callable directly
        TestRegionServerNoMaster.openRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs0, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        ClusterConnection connection = ((ClusterConnection) (ConnectionFactory.createConnection(TestRegionReplicaReplicationEndpointNoMaster.HTU.getConfiguration())));
        // load some data to primary
        TestRegionReplicaReplicationEndpointNoMaster.HTU.loadNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        Assert.assertEquals(1000, TestRegionReplicaReplicationEndpointNoMaster.entries.size());
        // replay the edits to the secondary using replay callable
        replicateUsingCallable(connection, TestRegionReplicaReplicationEndpointNoMaster.entries);
        Region region = TestRegionReplicaReplicationEndpointNoMaster.rs0.getRegion(TestRegionReplicaReplicationEndpointNoMaster.hriSecondary.getEncodedName());
        TestRegionReplicaReplicationEndpointNoMaster.HTU.verifyNumericRows(region, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        TestRegionReplicaReplicationEndpointNoMaster.HTU.deleteNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        TestRegionServerNoMaster.closeRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs0, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        connection.close();
    }

    @Test
    public void testReplayCallableWithRegionMove() throws Exception {
        // tests replaying the edits to a secondary region replica using the Callable directly while
        // the region is moved to another location.It tests handling of RME.
        TestRegionServerNoMaster.openRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs0, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        ClusterConnection connection = ((ClusterConnection) (ConnectionFactory.createConnection(TestRegionReplicaReplicationEndpointNoMaster.HTU.getConfiguration())));
        // load some data to primary
        TestRegionReplicaReplicationEndpointNoMaster.HTU.loadNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        Assert.assertEquals(1000, TestRegionReplicaReplicationEndpointNoMaster.entries.size());
        // replay the edits to the secondary using replay callable
        replicateUsingCallable(connection, TestRegionReplicaReplicationEndpointNoMaster.entries);
        Region region = TestRegionReplicaReplicationEndpointNoMaster.rs0.getRegion(TestRegionReplicaReplicationEndpointNoMaster.hriSecondary.getEncodedName());
        TestRegionReplicaReplicationEndpointNoMaster.HTU.verifyNumericRows(region, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        TestRegionReplicaReplicationEndpointNoMaster.HTU.loadNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 1000, 2000);// load some more data to primary

        // move the secondary region from RS0 to RS1
        TestRegionServerNoMaster.closeRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs0, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        TestRegionServerNoMaster.openRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs1, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        // replicate the new data
        replicateUsingCallable(connection, TestRegionReplicaReplicationEndpointNoMaster.entries);
        region = TestRegionReplicaReplicationEndpointNoMaster.rs1.getRegion(TestRegionReplicaReplicationEndpointNoMaster.hriSecondary.getEncodedName());
        // verify the new data. old data may or may not be there
        TestRegionReplicaReplicationEndpointNoMaster.HTU.verifyNumericRows(region, TestRegionReplicaReplicationEndpointNoMaster.f, 1000, 2000);
        TestRegionReplicaReplicationEndpointNoMaster.HTU.deleteNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 2000);
        TestRegionServerNoMaster.closeRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs1, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        connection.close();
    }

    @Test
    public void testRegionReplicaReplicationEndpointReplicate() throws Exception {
        // tests replaying the edits to a secondary region replica using the RRRE.replicate()
        TestRegionServerNoMaster.openRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs0, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        ClusterConnection connection = ((ClusterConnection) (ConnectionFactory.createConnection(TestRegionReplicaReplicationEndpointNoMaster.HTU.getConfiguration())));
        RegionReplicaReplicationEndpoint replicator = new RegionReplicaReplicationEndpoint();
        ReplicationEndpoint.Context context = Mockito.mock(Context.class);
        Mockito.when(context.getConfiguration()).thenReturn(TestRegionReplicaReplicationEndpointNoMaster.HTU.getConfiguration());
        Mockito.when(context.getMetrics()).thenReturn(Mockito.mock(MetricsSource.class));
        replicator.init(context);
        replicator.startAsync();
        // load some data to primary
        TestRegionReplicaReplicationEndpointNoMaster.HTU.loadNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        Assert.assertEquals(1000, TestRegionReplicaReplicationEndpointNoMaster.entries.size());
        // replay the edits to the secondary using replay callable
        final String fakeWalGroupId = "fakeWALGroup";
        replicator.replicate(new ReplicateContext().setEntries(Lists.newArrayList(TestRegionReplicaReplicationEndpointNoMaster.entries)).setWalGroupId(fakeWalGroupId));
        Region region = TestRegionReplicaReplicationEndpointNoMaster.rs0.getRegion(TestRegionReplicaReplicationEndpointNoMaster.hriSecondary.getEncodedName());
        TestRegionReplicaReplicationEndpointNoMaster.HTU.verifyNumericRows(region, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        TestRegionReplicaReplicationEndpointNoMaster.HTU.deleteNumericRows(TestRegionReplicaReplicationEndpointNoMaster.table, TestRegionReplicaReplicationEndpointNoMaster.f, 0, 1000);
        TestRegionServerNoMaster.closeRegion(TestRegionReplicaReplicationEndpointNoMaster.HTU, TestRegionReplicaReplicationEndpointNoMaster.rs0, TestRegionReplicaReplicationEndpointNoMaster.hriSecondary);
        connection.close();
    }
}

