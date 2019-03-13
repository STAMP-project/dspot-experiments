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


import CreateTableState.CREATE_TABLE_ASSIGN_REGIONS;
import DeleteTableState.DELETE_TABLE_UNASSIGN_REGIONS;
import DisableTableState.DISABLE_TABLE_MARK_REGIONS_OFFLINE;
import EnableTableState.ENABLE_TABLE_MARK_REGIONS_ONLINE;
import TruncateTableState.TRUNCATE_TABLE_ADD_TO_META;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
public class TestMasterFailoverWithProcedures {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterFailoverWithProcedures.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterFailoverWithProcedures.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    // ==========================================================================
    // Test Create Table
    // ==========================================================================
    @Test
    public void testCreateWithFailover() throws Exception {
        // TODO: Should we try every step? (master failover takes long time)
        // It is already covered by TestCreateTableProcedure
        // but without the master restart, only the executor/store is restarted.
        // Without Master restart we may not find bug in the procedure code
        // like missing "wait" for resources to be available (e.g. RS)
        testCreateWithFailoverAtStep(CREATE_TABLE_ASSIGN_REGIONS.ordinal());
    }

    // ==========================================================================
    // Test Delete Table
    // ==========================================================================
    @Test
    public void testDeleteWithFailover() throws Exception {
        // TODO: Should we try every step? (master failover takes long time)
        // It is already covered by TestDeleteTableProcedure
        // but without the master restart, only the executor/store is restarted.
        // Without Master restart we may not find bug in the procedure code
        // like missing "wait" for resources to be available (e.g. RS)
        testDeleteWithFailoverAtStep(DELETE_TABLE_UNASSIGN_REGIONS.ordinal());
    }

    // ==========================================================================
    // Test Truncate Table
    // ==========================================================================
    @Test
    public void testTruncateWithFailover() throws Exception {
        // TODO: Should we try every step? (master failover takes long time)
        // It is already covered by TestTruncateTableProcedure
        // but without the master restart, only the executor/store is restarted.
        // Without Master restart we may not find bug in the procedure code
        // like missing "wait" for resources to be available (e.g. RS)
        testTruncateWithFailoverAtStep(true, TRUNCATE_TABLE_ADD_TO_META.ordinal());
    }

    // ==========================================================================
    // Test Disable Table
    // ==========================================================================
    @Test
    public void testDisableTableWithFailover() throws Exception {
        // TODO: Should we try every step? (master failover takes long time)
        // It is already covered by TestDisableTableProcedure
        // but without the master restart, only the executor/store is restarted.
        // Without Master restart we may not find bug in the procedure code
        // like missing "wait" for resources to be available (e.g. RS)
        testDisableTableWithFailoverAtStep(DISABLE_TABLE_MARK_REGIONS_OFFLINE.ordinal());
    }

    // ==========================================================================
    // Test Enable Table
    // ==========================================================================
    @Test
    public void testEnableTableWithFailover() throws Exception {
        // TODO: Should we try every step? (master failover takes long time)
        // It is already covered by TestEnableTableProcedure
        // but without the master restart, only the executor/store is restarted.
        // Without Master restart we may not find bug in the procedure code
        // like missing "wait" for resources to be available (e.g. RS)
        testEnableTableWithFailoverAtStep(ENABLE_TABLE_MARK_REGIONS_ONLINE.ordinal());
    }
}

