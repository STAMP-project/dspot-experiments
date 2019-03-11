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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestReplicationWithTableMigration - test replication for Hive2 to Hive3 (Strict managed tables)
 */
public class TestReplicationWithTableMigration {
    private static final String AVRO_SCHEMA_FILE_NAME = "avro_table.avsc";

    @Rule
    public final TestName testName = new TestName();

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationWithTableMigration.class);

    private static WarehouseInstance primary;

    private static WarehouseInstance replica;

    private String primaryDbName;

    private String replicatedDbName;

    private Path avroSchemaFile = null;

    @Test
    public void testBootstrapLoadMigrationManagedToAcid() throws Throwable {
        WarehouseInstance.Tuple tuple = prepareDataAndDump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
    }

    @Test
    public void testIncrementalLoadMigrationManagedToAcid() throws Throwable {
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigration.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        tuple = prepareDataAndDump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
    }

    @Test
    public void testIncrementalLoadMigrationManagedToAcidFailure() throws Throwable {
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigration.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        tuple = prepareDataAndDump(primaryDbName, tuple.lastReplicationId);
        loadWithFailureInAddNotification("tacid", tuple.dumpLocation);
        TestReplicationWithTableMigration.replica.run(("use " + (replicatedDbName))).run("show tables like tacid").verifyResult(null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
    }

    @Test
    public void testIncrementalLoadMigrationManagedToAcidFailurePart() throws Throwable {
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigration.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        tuple = prepareDataAndDump(primaryDbName, tuple.lastReplicationId);
        loadWithFailureInAddNotification("tacidpart", tuple.dumpLocation);
        TestReplicationWithTableMigration.replica.run(("use " + (replicatedDbName))).run("show tables like tacidpart").verifyResult(null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
    }

    @Test
    public void testIncrementalLoadMigrationManagedToAcidAllOp() throws Throwable {
        WarehouseInstance.Tuple bootStrapDump = TestReplicationWithTableMigration.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, bootStrapDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(bootStrapDump.lastReplicationId);
        List<String> selectStmtList = new ArrayList<>();
        List<String[]> expectedValues = new ArrayList<>();
        String tableName = (testName.getMethodName()) + "testInsert";
        String tableNameMM = tableName + "_MM";
        ReplicationTestUtils.appendInsert(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendTruncate(TestReplicationWithTableMigration.primary, primaryDbName, null, selectStmtList, expectedValues);
        ReplicationTestUtils.appendInsertIntoFromSelect(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendCreateAsSelect(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendImport(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendInsertOverwrite(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendLoadLocal(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendInsertUnion(TestReplicationWithTableMigration.primary, primaryDbName, null, tableName, tableNameMM, selectStmtList, expectedValues);
        ReplicationTestUtils.appendAlterTable(TestReplicationWithTableMigration.primary, primaryDbName, null, selectStmtList, expectedValues);
        ReplicationTestUtils.verifyIncrementalLoad(TestReplicationWithTableMigration.primary, TestReplicationWithTableMigration.replica, primaryDbName, replicatedDbName, selectStmtList, expectedValues, bootStrapDump.lastReplicationId);
    }

    @Test
    public void testBootstrapLoadMigrationToAcidWithMoveOptimization() throws Throwable {
        List<String> withConfigs = Collections.singletonList("'hive.repl.enable.move.optimization'='true'");
        WarehouseInstance.Tuple tuple = prepareDataAndDump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation, withConfigs);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
    }

    @Test
    public void testIncrementalLoadMigrationToAcidWithMoveOptimization() throws Throwable {
        List<String> withConfigs = Collections.singletonList("'hive.repl.enable.move.optimization'='true'");
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigration.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation);
        tuple = prepareDataAndDump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigration.replica.load(replicatedDbName, tuple.dumpLocation, withConfigs);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
    }
}

