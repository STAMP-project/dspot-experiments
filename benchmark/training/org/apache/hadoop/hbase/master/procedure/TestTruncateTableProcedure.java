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
package org.apache.hadoop.hbase.master.procedure;


import MasterProcedureProtos.TruncateTableState;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotDisabledException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.master.MasterFileSystem;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProcedureProtos;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.ModifyRegionUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Flow.HAS_MORE_STATE;


@Category({ MasterTests.class, MediumTests.class })
public class TestTruncateTableProcedure extends TestTableDDLProcedureBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTruncateTableProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTruncateTableProcedure.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testTruncateNotExistentTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        // HBASE-20178 has us fail-fast, in the constructor, so add try/catch for this case.
        // Keep old way of looking at procedure too.
        Throwable cause = null;
        try {
            long procId = ProcedureTestingUtility.submitAndWait(procExec, new TruncateTableProcedure(procExec.getEnvironment(), tableName, true));
            // Second delete should fail with TableNotFound
            Procedure<?> result = procExec.getResult(procId);
            Assert.assertTrue(result.isFailed());
            cause = ProcedureTestingUtility.getExceptionCause(result);
        } catch (Throwable t) {
            cause = t;
        }
        TestTruncateTableProcedure.LOG.debug(("Truncate failed with exception: " + cause));
        Assert.assertTrue((cause instanceof TableNotFoundException));
    }

    @Test
    public void testTruncateNotDisabledTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        MasterProcedureTestingUtility.createTable(procExec, tableName, null, "f");
        // HBASE-20178 has us fail-fast, in the constructor, so add try/catch for this case.
        // Keep old way of looking at procedure too.
        Throwable cause = null;
        try {
            long procId = ProcedureTestingUtility.submitAndWait(procExec, new TruncateTableProcedure(procExec.getEnvironment(), tableName, false));
            // Second delete should fail with TableNotDisabled
            Procedure<?> result = procExec.getResult(procId);
            Assert.assertTrue(result.isFailed());
            cause = ProcedureTestingUtility.getExceptionCause(result);
        } catch (Throwable t) {
            cause = t;
        }
        TestTruncateTableProcedure.LOG.debug(("Truncate failed with exception: " + cause));
        Assert.assertTrue((cause instanceof TableNotDisabledException));
    }

    @Test
    public void testSimpleTruncatePreserveSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testSimpleTruncate(tableName, true);
    }

    @Test
    public void testSimpleTruncateNoPreserveSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testSimpleTruncate(tableName, false);
    }

    @Test
    public void testRecoveryAndDoubleExecutionPreserveSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testRecoveryAndDoubleExecution(tableName, true);
    }

    @Test
    public void testRecoveryAndDoubleExecutionNoPreserveSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testRecoveryAndDoubleExecution(tableName, false);
    }

    @Test
    public void testOnHDFSFailurePreserveSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testOnHDFSFailure(tableName, true);
    }

    @Test
    public void testOnHDFSFailureNoPreserveSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testOnHDFSFailure(tableName, false);
    }

    public static class TruncateTableProcedureOnHDFSFailure extends TruncateTableProcedure {
        private boolean failOnce = false;

        public TruncateTableProcedureOnHDFSFailure() {
            // Required by the Procedure framework to create the procedure on replay
            super();
        }

        public TruncateTableProcedureOnHDFSFailure(final MasterProcedureEnv env, TableName tableName, boolean preserveSplits) throws HBaseIOException {
            super(env, tableName, preserveSplits);
        }

        @Override
        protected Flow executeFromState(MasterProcedureEnv env, MasterProcedureProtos.TruncateTableState state) throws InterruptedException {
            if ((!(failOnce)) && (state == (TruncateTableState.TRUNCATE_TABLE_CREATE_FS_LAYOUT))) {
                try {
                    // To emulate an HDFS failure, create only the first region directory
                    RegionInfo regionInfo = getFirstRegionInfo();
                    Configuration conf = env.getMasterConfiguration();
                    MasterFileSystem mfs = env.getMasterServices().getMasterFileSystem();
                    Path tempdir = mfs.getTempDir();
                    Path tableDir = FSUtils.getTableDir(tempdir, regionInfo.getTable());
                    Path regionDir = FSUtils.getRegionDir(tableDir, regionInfo);
                    FileSystem fs = FileSystem.get(conf);
                    fs.mkdirs(regionDir);
                    failOnce = true;
                    return HAS_MORE_STATE;
                } catch (IOException e) {
                    Assert.fail(("failed to create a region directory: " + e));
                }
            }
            return super.executeFromState(env, state);
        }
    }

    @Test
    public void testTruncateWithPreserveAfterSplit() throws Exception {
        String[] families = new String[]{ "f1", "f2" };
        byte[][] splitKeys = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("c") };
        TableName tableName = TableName.valueOf(name.getMethodName());
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(getMasterProcedureExecutor(), tableName, splitKeys, families);
        splitAndTruncate(tableName, regions, 1);
    }

    @Test
    public void testTruncatePreserveWithReplicaRegionAfterSplit() throws Exception {
        String[] families = new String[]{ "f1", "f2" };
        byte[][] splitKeys = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("c") };
        TableName tableName = TableName.valueOf(name.getMethodName());
        // create a table with region replications
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(tableName).setRegionReplication(3).setColumnFamilies(Arrays.stream(families).map(( fam) -> ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(fam)).build()).collect(Collectors.toList())).build();
        RegionInfo[] regions = ModifyRegionUtils.createRegionInfos(htd, splitKeys);
        ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        long procId = ProcedureTestingUtility.submitAndWait(procExec, new CreateTableProcedure(procExec.getEnvironment(), htd, regions));
        ProcedureTestingUtility.assertProcNotFailed(procExec.getResult(procId));
        splitAndTruncate(tableName, regions, 3);
    }
}

