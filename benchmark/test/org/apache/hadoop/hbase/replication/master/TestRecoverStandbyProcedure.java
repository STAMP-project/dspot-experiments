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
package org.apache.hadoop.hbase.replication.master;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.replication.RecoverStandbyProcedure;
import org.apache.hadoop.hbase.master.replication.SyncReplicationReplayWALManager;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CommonFSUtils.StreamLacksCapabilityException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
public class TestRecoverStandbyProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRecoverStandbyProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRecoverStandbyProcedure.class);

    private static final TableName tableName = TableName.valueOf("TestRecoverStandbyProcedure");

    private static final RegionInfo regionInfo = RegionInfoBuilder.newBuilder(TestRecoverStandbyProcedure.tableName).build();

    private static final byte[] family = Bytes.toBytes("CF");

    private static final byte[] qualifier = Bytes.toBytes("q");

    private static final long timestamp = System.currentTimeMillis();

    private static final int ROW_COUNT = 1000;

    private static final int WAL_NUMBER = 10;

    private static final int RS_NUMBER = 3;

    private static final String PEER_ID = "1";

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static SyncReplicationReplayWALManager syncReplicationReplayWALManager;

    private static ProcedureExecutor<MasterProcedureEnv> procExec;

    private static FileSystem fs;

    private static Configuration conf;

    @Test
    public void testRecoverStandby() throws IOException, StreamLacksCapabilityException {
        setupSyncReplicationWALs();
        long procId = TestRecoverStandbyProcedure.procExec.submitProcedure(new RecoverStandbyProcedure(TestRecoverStandbyProcedure.PEER_ID, false));
        ProcedureTestingUtility.waitProcedure(TestRecoverStandbyProcedure.procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(TestRecoverStandbyProcedure.procExec, procId);
        try (Table table = TestRecoverStandbyProcedure.UTIL.getConnection().getTable(TestRecoverStandbyProcedure.tableName)) {
            for (int i = 0; i < ((TestRecoverStandbyProcedure.WAL_NUMBER) * (TestRecoverStandbyProcedure.ROW_COUNT)); i++) {
                Result result = table.get(setTimestamp(TestRecoverStandbyProcedure.timestamp));
                Assert.assertNotNull(result);
                Assert.assertEquals(i, Bytes.toInt(result.getValue(TestRecoverStandbyProcedure.family, TestRecoverStandbyProcedure.qualifier)));
            }
        }
    }
}

