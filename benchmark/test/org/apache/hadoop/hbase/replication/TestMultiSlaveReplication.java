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
package org.apache.hadoop.hbase.replication;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.replication.ReplicationAdmin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestMultiSlaveReplication {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiSlaveReplication.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMultiSlaveReplication.class);

    private static Configuration conf1;

    private static Configuration conf2;

    private static Configuration conf3;

    private static HBaseTestingUtility utility1;

    private static HBaseTestingUtility utility2;

    private static HBaseTestingUtility utility3;

    private static final long SLEEP_TIME = 500;

    private static final int NB_RETRIES = 100;

    private static final TableName tableName = TableName.valueOf("test");

    private static final byte[] famName = Bytes.toBytes("f");

    private static final byte[] row = Bytes.toBytes("row");

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] row2 = Bytes.toBytes("row2");

    private static final byte[] row3 = Bytes.toBytes("row3");

    private static final byte[] noRepfamName = Bytes.toBytes("norep");

    private static HTableDescriptor table;

    @Test
    public void testMultiSlaveReplication() throws Exception {
        TestMultiSlaveReplication.LOG.info("testCyclicReplication");
        MiniHBaseCluster master = TestMultiSlaveReplication.utility1.startMiniCluster();
        TestMultiSlaveReplication.utility2.startMiniCluster();
        TestMultiSlaveReplication.utility3.startMiniCluster();
        ReplicationAdmin admin1 = new ReplicationAdmin(TestMultiSlaveReplication.conf1);
        TestMultiSlaveReplication.utility1.getAdmin().createTable(TestMultiSlaveReplication.table);
        TestMultiSlaveReplication.utility2.getAdmin().createTable(TestMultiSlaveReplication.table);
        TestMultiSlaveReplication.utility3.getAdmin().createTable(TestMultiSlaveReplication.table);
        Table htable1 = TestMultiSlaveReplication.utility1.getConnection().getTable(TestMultiSlaveReplication.tableName);
        Table htable2 = TestMultiSlaveReplication.utility2.getConnection().getTable(TestMultiSlaveReplication.tableName);
        Table htable3 = TestMultiSlaveReplication.utility3.getConnection().getTable(TestMultiSlaveReplication.tableName);
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(TestMultiSlaveReplication.utility2.getClusterKey());
        admin1.addPeer("1", rpc, null);
        // put "row" and wait 'til it got around, then delete
        putAndWait(TestMultiSlaveReplication.row, TestMultiSlaveReplication.famName, htable1, htable2);
        deleteAndWait(TestMultiSlaveReplication.row, htable1, htable2);
        // check it wasn't replication to cluster 3
        checkRow(TestMultiSlaveReplication.row, 0, htable3);
        putAndWait(TestMultiSlaveReplication.row2, TestMultiSlaveReplication.famName, htable1, htable2);
        // now roll the region server's logs
        rollWALAndWait(TestMultiSlaveReplication.utility1, htable1.getName(), TestMultiSlaveReplication.row2);
        // after the log was rolled put a new row
        putAndWait(TestMultiSlaveReplication.row3, TestMultiSlaveReplication.famName, htable1, htable2);
        rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(TestMultiSlaveReplication.utility3.getClusterKey());
        admin1.addPeer("2", rpc, null);
        // put a row, check it was replicated to all clusters
        putAndWait(TestMultiSlaveReplication.row1, TestMultiSlaveReplication.famName, htable1, htable2, htable3);
        // delete and verify
        deleteAndWait(TestMultiSlaveReplication.row1, htable1, htable2, htable3);
        // make sure row2 did not get replicated after
        // cluster 3 was added
        checkRow(TestMultiSlaveReplication.row2, 0, htable3);
        // row3 will get replicated, because it was in the
        // latest log
        checkRow(TestMultiSlaveReplication.row3, 1, htable3);
        Put p = new Put(TestMultiSlaveReplication.row);
        p.addColumn(TestMultiSlaveReplication.famName, TestMultiSlaveReplication.row, TestMultiSlaveReplication.row);
        htable1.put(p);
        // now roll the logs again
        rollWALAndWait(TestMultiSlaveReplication.utility1, htable1.getName(), TestMultiSlaveReplication.row);
        // cleanup "row2", also conveniently use this to wait replication
        // to finish
        deleteAndWait(TestMultiSlaveReplication.row2, htable1, htable2, htable3);
        // Even if the log was rolled in the middle of the replication
        // "row" is still replication.
        checkRow(TestMultiSlaveReplication.row, 1, htable2);
        // Replication thread of cluster 2 may be sleeping, and since row2 is not there in it,
        // we should wait before checking.
        checkWithWait(TestMultiSlaveReplication.row, 1, htable3);
        // cleanup the rest
        deleteAndWait(TestMultiSlaveReplication.row, htable1, htable2, htable3);
        deleteAndWait(TestMultiSlaveReplication.row3, htable1, htable2, htable3);
        TestMultiSlaveReplication.utility3.shutdownMiniCluster();
        TestMultiSlaveReplication.utility2.shutdownMiniCluster();
        TestMultiSlaveReplication.utility1.shutdownMiniCluster();
    }
}

