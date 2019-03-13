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
package org.apache.hadoop.hbase.master.assignment;


import ProcedureProtos.ProcedureState.SUCCESS;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.ModifyTableProcedure;
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
public class TestModifyTableWhileMerging {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestModifyTableWhileMerging.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestModifyTableWhileMerging.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("test");

    private static Admin admin;

    private static Table client;

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] SPLITKEY = Bytes.toBytes("bbbbbbb");

    @Test
    public void test() throws Exception {
        TableDescriptor tableDescriptor = TestModifyTableWhileMerging.client.getDescriptor();
        ProcedureExecutor<MasterProcedureEnv> executor = TestModifyTableWhileMerging.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor();
        MasterProcedureEnv env = executor.getEnvironment();
        List<RegionInfo> regionInfos = TestModifyTableWhileMerging.admin.getRegions(TestModifyTableWhileMerging.TABLE_NAME);
        MergeTableRegionsProcedure mergeTableRegionsProcedure = new MergeTableRegionsProcedure(TestModifyTableWhileMerging.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor().getEnvironment(), regionInfos.get(0), regionInfos.get(1));
        ModifyTableProcedure modifyTableProcedure = new ModifyTableProcedure(env, tableDescriptor);
        long procModify = executor.submitProcedure(modifyTableProcedure);
        waitFor(30000, () -> executor.getProcedures().stream().filter(( p) -> p instanceof ModifyTableProcedure).map(( p) -> ((ModifyTableProcedure) (p))).anyMatch(( p) -> TestModifyTableWhileMerging.TABLE_NAME.equals(p.getTableName())));
        long proc = executor.submitProcedure(mergeTableRegionsProcedure);
        waitFor(3000000, () -> UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor().isFinished(procModify));
        Assert.assertEquals("Modify Table procedure should success!", SUCCESS, modifyTableProcedure.getState());
    }
}

