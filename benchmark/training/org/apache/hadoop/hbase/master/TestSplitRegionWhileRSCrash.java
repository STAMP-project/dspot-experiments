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
package org.apache.hadoop.hbase.master;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.assignment.SplitTableRegionProcedure;
import org.apache.hadoop.hbase.master.assignment.TransitRegionStateProcedure;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestSplitRegionWhileRSCrash {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitRegionWhileRSCrash.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSplitRegionWhileRSCrash.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("test");

    private static Admin admin;

    private static byte[] CF = Bytes.toBytes("cf");

    private static CountDownLatch mergeCommitArrive = new CountDownLatch(1);

    private static Table TABLE;

    @Test
    public void test() throws Exception {
        MasterProcedureEnv env = TestSplitRegionWhileRSCrash.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor().getEnvironment();
        final ProcedureExecutor<MasterProcedureEnv> executor = TestSplitRegionWhileRSCrash.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor();
        List<RegionInfo> regionInfos = TestSplitRegionWhileRSCrash.admin.getRegions(TestSplitRegionWhileRSCrash.TABLE_NAME);
        // Since a flush request will be sent while initializing SplitTableRegionProcedure
        // Create SplitTableRegionProcedure first before put data
        SplitTableRegionProcedure splitProcedure = new SplitTableRegionProcedure(env, regionInfos.get(0), Bytes.toBytes("row5"));
        // write some rows to the table
        TestSplitRegionWhileRSCrash.LOG.info("Begin to put data");
        for (int i = 0; i < 10; i++) {
            byte[] row = Bytes.toBytes(("row" + i));
            Put put = new Put(row);
            put.addColumn(TestSplitRegionWhileRSCrash.CF, TestSplitRegionWhileRSCrash.CF, TestSplitRegionWhileRSCrash.CF);
            TestSplitRegionWhileRSCrash.TABLE.put(put);
        }
        executor.submitProcedure(splitProcedure);
        TestSplitRegionWhileRSCrash.LOG.info("SplitProcedure submitted");
        waitFor(30000, () -> executor.getProcedures().stream().filter(( p) -> p instanceof TransitRegionStateProcedure).map(( p) -> ((TransitRegionStateProcedure) (p))).anyMatch(( p) -> TestSplitRegionWhileRSCrash.TABLE_NAME.equals(p.getTableName())));
        TestSplitRegionWhileRSCrash.UTIL.getMiniHBaseCluster().killRegionServer(TestSplitRegionWhileRSCrash.UTIL.getMiniHBaseCluster().getRegionServer(0).getServerName());
        TestSplitRegionWhileRSCrash.UTIL.getMiniHBaseCluster().startRegionServer();
        TestSplitRegionWhileRSCrash.UTIL.waitUntilNoRegionsInTransition();
        Scan scan = new Scan();
        ResultScanner results = TestSplitRegionWhileRSCrash.TABLE.getScanner(scan);
        int count = 0;
        Result result = null;
        while ((result = results.next()) != null) {
            count++;
        } 
        Assert.assertEquals("There should be 10 rows!", 10, count);
    }
}

