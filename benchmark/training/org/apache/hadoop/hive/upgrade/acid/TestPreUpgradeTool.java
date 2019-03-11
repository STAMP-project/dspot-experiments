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
package org.apache.hadoop.hive.upgrade.acid;


import TxnStore.CLEANING_RESPONSE;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponseElement;
import org.apache.hadoop.hive.metastore.txn.TxnStore;
import org.apache.hadoop.hive.metastore.txn.TxnUtils;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static PreUpgradeTool.callback;
import static PreUpgradeTool.hiveConf;
import static PreUpgradeTool.pollIntervalMs;


public class TestPreUpgradeTool {
    private static final String TEST_DATA_DIR = new File((((((System.getProperty("java.io.tmpdir")) + (File.separator)) + (TestPreUpgradeTool.class.getCanonicalName())) + "-") + (System.currentTimeMillis()))).getPath().replaceAll("\\\\", "/");

    /**
     * preUpgrade: test tables that need to be compacted, waits for compaction
     * postUpgrade: generates scripts w/o asserts
     */
    @Test
    public void testUpgrade() throws Exception {
        int[][] data = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 }, new int[]{ 5, 6 } };
        int[][] dataPart = new int[][]{ new int[]{ 1, 2, 10 }, new int[]{ 3, 4, 11 }, new int[]{ 5, 6, 12 } };
        runStatementOnDriver("drop table if exists TAcid");
        runStatementOnDriver("drop table if exists TAcidPart");
        runStatementOnDriver("drop table if exists TFlat");
        runStatementOnDriver("drop table if exists TFlatText");
        try {
            runStatementOnDriver("create table TAcid (a int, b int) clustered by (b) into 2 buckets stored as orc TBLPROPERTIES ('transactional'='true')");
            runStatementOnDriver(("create table TAcidPart (a int, b int) partitioned by (p tinyint)  clustered by (b) into 2 buckets  stored" + " as orc TBLPROPERTIES ('transactional'='true')"));
            // on 2.x these are guaranteed to not be acid
            runStatementOnDriver("create table TFlat (a int, b int) stored as orc tblproperties('transactional'='false')");
            runStatementOnDriver("create table TFlatText (a int, b int) stored as textfile tblproperties('transactional'='false')");
            // this needs major compaction
            runStatementOnDriver(("insert into TAcid" + (TestPreUpgradeTool.makeValuesClause(data))));
            runStatementOnDriver("update TAcid set a = 1 where b = 2");
            // this table needs to be converted to CRUD Acid
            runStatementOnDriver(("insert into TFlat" + (TestPreUpgradeTool.makeValuesClause(data))));
            // this table needs to be converted to MM
            runStatementOnDriver(("insert into TFlatText" + (TestPreUpgradeTool.makeValuesClause(data))));
            // p=10 needs major compaction
            runStatementOnDriver(("insert into TAcidPart partition(p)" + (TestPreUpgradeTool.makeValuesClause(dataPart))));
            runStatementOnDriver("update TAcidPart set a = 1 where b = 2 and p = 10");
            // todo: add partitioned table that needs conversion to MM/Acid
            // todo: rename files case
            String[] args = new String[]{ "-location", getTestDataDir(), "-execute" };
            callback = new PreUpgradeTool.Callback() {
                @Override
                void onWaitForCompaction() throws MetaException {
                    TestPreUpgradeTool.runWorker(hiveConf);
                }
            };
            pollIntervalMs = 1;
            hiveConf = hiveConf;
            PreUpgradeTool.main(args);
            /* todo: parse
            target/tmp/org.apache.hadoop.hive.upgrade.acid.TestPreUpgradeTool-1527286256834/compacts_1527286277624.sql
            make sure it's the only 'compacts' file and contains
            ALTER TABLE default.tacid COMPACT 'major';
            ALTER TABLE default.tacidpart PARTITION(p=10Y) COMPACT 'major';
             */
            TxnStore txnHandler = TxnUtils.getTxnStore(hiveConf);
            ShowCompactResponse resp = txnHandler.showCompact(new ShowCompactRequest());
            Assert.assertEquals(2, resp.getCompactsSize());
            for (ShowCompactResponseElement e : resp.getCompacts()) {
                Assert.assertEquals(e.toString(), CLEANING_RESPONSE, e.getState());
            }
            String[] args2 = new String[]{ "-location", getTestDataDir() };
            PreUpgradeTool.main(args2);
            /* todo: parse compacts script - make sure there is nothing in it */
        } finally {
            runStatementOnDriver("drop table if exists TAcid");
            runStatementOnDriver("drop table if exists TAcidPart");
            runStatementOnDriver("drop table if exists TFlat");
            runStatementOnDriver("drop table if exists TFlatText");
        }
    }

    @Test
    public void testUpgradeExternalTableNoReadPermissionForDatabase() throws Exception {
        int[][] data = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 }, new int[]{ 5, 6 } };
        runStatementOnDriver("drop database if exists test cascade");
        runStatementOnDriver("drop table if exists TExternal");
        runStatementOnDriver("create database test");
        runStatementOnDriver(("create table test.TExternal (a int, b int) stored as orc tblproperties" + "('transactional'='false')"));
        // this needs major compaction
        runStatementOnDriver(("insert into test.TExternal" + (TestPreUpgradeTool.makeValuesClause(data))));
        String dbDir = (getWarehouseDir()) + "/test.db";
        File dbPath = new File(dbDir);
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("-w-------");
            Files.setPosixFilePermissions(dbPath.toPath(), perms);
            String[] args = new String[]{ "-location", getTestDataDir(), "-execute" };
            pollIntervalMs = 1;
            hiveConf = hiveConf;
            Exception expected = null;
            try {
                PreUpgradeTool.main(args);
            } catch (Exception e) {
                expected = e;
            }
            Assert.assertNotNull(expected);
            Assert.assertTrue((expected instanceof HiveException));
            Assert.assertTrue(expected.getMessage().contains(("Pre-upgrade tool requires " + "read-access to databases and tables to determine if a table has to be compacted.")));
        } finally {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrw----");
            Files.setPosixFilePermissions(dbPath.toPath(), perms);
        }
    }

    @Test
    public void testUpgradeExternalTableNoReadPermissionForTable() throws Exception {
        int[][] data = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 }, new int[]{ 5, 6 } };
        runStatementOnDriver("drop table if exists TExternal");
        runStatementOnDriver("create table TExternal (a int, b int) stored as orc tblproperties('transactional'='false')");
        // this needs major compaction
        runStatementOnDriver(("insert into TExternal" + (TestPreUpgradeTool.makeValuesClause(data))));
        String tableDir = (getWarehouseDir()) + "/texternal";
        File tablePath = new File(tableDir);
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("-w-------");
            Files.setPosixFilePermissions(tablePath.toPath(), perms);
            String[] args = new String[]{ "-location", getTestDataDir(), "-execute" };
            pollIntervalMs = 1;
            hiveConf = hiveConf;
            Exception expected = null;
            try {
                PreUpgradeTool.main(args);
            } catch (Exception e) {
                expected = e;
            }
            Assert.assertNotNull(expected);
            Assert.assertTrue((expected instanceof HiveException));
            Assert.assertTrue(expected.getMessage().contains(("Pre-upgrade tool requires" + " read-access to databases and tables to determine if a table has to be compacted.")));
        } finally {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrw----");
            Files.setPosixFilePermissions(tablePath.toPath(), perms);
        }
    }

    @Rule
    public TestName testName = new TestName();

    private HiveConf hiveConf;

    private Driver d;
}

