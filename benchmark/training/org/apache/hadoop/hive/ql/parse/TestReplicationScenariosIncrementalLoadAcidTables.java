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
package org.apache.hadoop.hive.ql.parse;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hive.ql.parse.ReplicationTestUtils.OperationType.REPL_TEST_ACID_INSERT;


/**
 * TestReplicationScenariosAcidTables - test replication for ACID tables
 */
public class TestReplicationScenariosIncrementalLoadAcidTables {
    @Rule
    public final TestName testName = new TestName();

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationScenariosIncrementalLoadAcidTables.class);

    static WarehouseInstance primary;

    private static WarehouseInstance replica;

    private static WarehouseInstance replicaNonAcid;

    private static HiveConf conf;

    private String primaryDbName;

    private String replicatedDbName;

    private String primaryDbNameExtra;

    @Test
    public void testAcidTableIncrementalReplication() throws Throwable {
        WarehouseInstance.Tuple bootStrapDump = TestReplicationScenariosIncrementalLoadAcidTables.primary.dump(primaryDbName, null);
        TestReplicationScenariosIncrementalLoadAcidTables.replica.load(replicatedDbName, bootStrapDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(bootStrapDump.lastReplicationId);
        List<String> selectStmtList = new ArrayList<>();
        List<String[]> expectedValues = new ArrayList<>();
        String tableName = (testName.getMethodName()) + "testInsert";
        String tableNameMM = tableName + "_MM";
        ReplicationTestUtils.appendInsert(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        appendDelete(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        appendUpdate(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        ReplicationTestUtils.appendTruncate(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        ReplicationTestUtils.appendInsertIntoFromSelect(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendMerge(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        ReplicationTestUtils.appendCreateAsSelect(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendImport(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendInsertOverwrite(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendLoadLocal(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendInsertUnion(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendMultiStatementTxn(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        appendMultiStatementTxnUpdateDelete(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        ReplicationTestUtils.appendAlterTable(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, selectStmtList, expectedValues);
        verifyIncrementalLoadInt(selectStmtList, expectedValues, bootStrapDump.lastReplicationId);
    }

    @Test
    public void testReplCM() throws Throwable {
        String tableName = "testcm";
        String tableNameMM = tableName + "_MM";
        String[] result = new String[]{ "5" };
        WarehouseInstance.Tuple incrementalDump;
        WarehouseInstance.Tuple bootStrapDump = TestReplicationScenariosIncrementalLoadAcidTables.primary.dump(primaryDbName, null);
        TestReplicationScenariosIncrementalLoadAcidTables.replica.load(replicatedDbName, bootStrapDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(bootStrapDump.lastReplicationId);
        ReplicationTestUtils.insertRecords(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableName, null, false, REPL_TEST_ACID_INSERT);
        incrementalDump = TestReplicationScenariosIncrementalLoadAcidTables.primary.dump(primaryDbName, bootStrapDump.lastReplicationId);
        TestReplicationScenariosIncrementalLoadAcidTables.primary.run(((("drop table " + (primaryDbName)) + ".") + tableName));
        TestReplicationScenariosIncrementalLoadAcidTables.replica.loadWithoutExplain(replicatedDbName, incrementalDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(incrementalDump.lastReplicationId);
        verifyResultsInReplicaInt(Lists.newArrayList(("select count(*) from " + tableName), (("select count(*) from " + tableName) + "_nopart")), Lists.newArrayList(result, result));
        ReplicationTestUtils.insertRecords(TestReplicationScenariosIncrementalLoadAcidTables.primary, primaryDbName, primaryDbNameExtra, tableNameMM, null, true, REPL_TEST_ACID_INSERT);
        incrementalDump = TestReplicationScenariosIncrementalLoadAcidTables.primary.dump(primaryDbName, bootStrapDump.lastReplicationId);
        TestReplicationScenariosIncrementalLoadAcidTables.primary.run(((("drop table " + (primaryDbName)) + ".") + tableNameMM));
        TestReplicationScenariosIncrementalLoadAcidTables.replica.loadWithoutExplain(replicatedDbName, incrementalDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(incrementalDump.lastReplicationId);
        verifyResultsInReplicaInt(Lists.newArrayList(("select count(*) from " + tableNameMM), (("select count(*) from " + tableNameMM) + "_nopart")), Lists.newArrayList(result, result));
    }
}

