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
package org.apache.hadoop.hive.ql;


import ConfVars.HIVEFETCHTASKCONVERSION;
import ConfVars.HIVE_EXECUTION_ENGINE;
import HiveConf.ConfVars.LLAP_DAEMON_SERVICE_HOSTS;
import HiveConf.ConfVars.SPLIT_GROUPING_MODE;
import LockState.ACQUIRED;
import LockType.SHARED_READ;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.ShowLocksRequest;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponse;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponseElement;
import org.apache.hadoop.hive.metastore.txn.TxnStore;
import org.apache.hadoop.hive.metastore.txn.TxnUtils;
import org.apache.hadoop.hive.ql.exec.AbstractFileMergeOperator;
import org.apache.hadoop.hive.ql.lockmgr.TestDbTxnManager2;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class resides in itests to facilitate running query using Tez engine, since the jars are
 * fully loaded here, which is not the case if it stays in ql.
 */
public class TestAcidOnTez {
    private static final Logger LOG = LoggerFactory.getLogger(TestAcidOnTez.class);

    private static final String TEST_DATA_DIR = new File((((((System.getProperty("java.io.tmpdir")) + (File.separator)) + (TestAcidOnTez.class.getCanonicalName())) + "-") + (System.currentTimeMillis()))).getPath().replaceAll("\\\\", "/");

    private static final String TEST_WAREHOUSE_DIR = (TestAcidOnTez.TEST_DATA_DIR) + "/warehouse";

    // bucket count for test tables; set it to 1 for easier debugging
    private static int BUCKET_COUNT = 2;

    @Rule
    public TestName testName = new TestName();

    private HiveConf hiveConf;

    private IDriver d;

    private static enum Table {

        ACIDTBL("acidTbl"),
        ACIDTBLPART("acidTblPart"),
        ACIDNOBUCKET("acidNoBucket"),
        NONACIDORCTBL("nonAcidOrcTbl"),
        NONACIDPART("nonAcidPart"),
        NONACIDNONBUCKET("nonAcidNonBucket");
        private final String name;

        @Override
        public String toString() {
            return name;
        }

        Table(String name) {
            this.name = name;
        }
    }

    @Test
    public void testMergeJoinOnMR() throws Exception {
        testJoin("mr", "MergeJoin");
    }

    @Test
    public void testMapJoinOnMR() throws Exception {
        testJoin("mr", "MapJoin");
    }

    @Test
    public void testMergeJoinOnTez() throws Exception {
        testJoin("tez", "MergeJoin");
    }

    @Test
    public void testMapJoinOnTez() throws Exception {
        testJoin("tez", "MapJoin");
    }

    /**
     * 1. Insert into regular unbucketed table from Union all - union is removed and data is placed in
     * subdirs of target table.
     * 2. convert to acid table and check data
     * 3. compact and check data
     * Compare with {@link #testAcidInsertWithRemoveUnion()} where T is transactional=true
     */
    @Test
    public void testInsertWithRemoveUnion() throws Exception {
        int[][] values = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 }, new int[]{ 5, 6 }, new int[]{ 7, 8 }, new int[]{ 9, 10 } };
        HiveConf confForTez = new HiveConf(hiveConf);// make a clone of existing hive conf

        setupTez(confForTez);
        runStatementOnDriver("drop table if exists T", confForTez);
        runStatementOnDriver("create table T (a int, b int) stored as ORC  TBLPROPERTIES ('transactional'='false')", confForTez);
        /* ekoifman:apache-hive-3.0.0-SNAPSHOT-bin ekoifman$ tree  ~/dev/hiverwgit/itests/hive-unit/target/tmp/org.apache.hadoop.hive.ql.TestAcidOnTez-1505502329802/warehouse/t/.hive-staging_hive_2017-09-15_12-07-33_224_7717909516029836949-1/
        /Users/ekoifman/dev/hiverwgit/itests/hive-unit/target/tmp/org.apache.hadoop.hive.ql.TestAcidOnTez-1505502329802/warehouse/t/.hive-staging_hive_2017-09-15_12-07-33_224_7717909516029836949-1/
        ??? -ext-10000
        ??? HIVE_UNION_SUBDIR_1
        ??? ??? 000000_0
        ??? HIVE_UNION_SUBDIR_2
        ??? ??? 000000_0
        ??? HIVE_UNION_SUBDIR_3
        ??? 000000_0

        4 directories, 3 files
         */
        runStatementOnDriver((((((("insert into T(a,b) select a, b from " + (TestAcidOnTez.Table.ACIDTBL)) + " where a between 1 and 3 group by a, b union all select a, b from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a between 5 and 7 union all select a, b from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a >= 9"), confForTez);
        List<String> rs = runStatementOnDriver("select a, b, INPUT__FILE__NAME from T order by a, b, INPUT__FILE__NAME", confForTez);
        TestAcidOnTez.LOG.warn(((testName.getMethodName()) + ": before converting to acid"));
        for (String s : rs) {
            TestAcidOnTez.LOG.warn(s);
        }
        String[][] expected = new String[][]{ new String[]{ "1\t2", ("warehouse/t/" + (AbstractFileMergeOperator.UNION_SUDBIR_PREFIX)) + "1/000000_0" }, new String[]{ "3\t4", ("warehouse/t/" + (AbstractFileMergeOperator.UNION_SUDBIR_PREFIX)) + "1/000000_0" }, new String[]{ "5\t6", ("warehouse/t/" + (AbstractFileMergeOperator.UNION_SUDBIR_PREFIX)) + "2/000000_0" }, new String[]{ "7\t8", ("warehouse/t/" + (AbstractFileMergeOperator.UNION_SUDBIR_PREFIX)) + "2/000000_0" }, new String[]{ "9\t10", ("warehouse/t/" + (AbstractFileMergeOperator.UNION_SUDBIR_PREFIX)) + "3/000000_0" } };
        Assert.assertEquals("Unexpected row count after conversion", expected.length, rs.size());
        for (int i = 0; i < (expected.length); i++) {
            Assert.assertTrue(((("Actual line " + i) + " bc: ") + (rs.get(i))), rs.get(i).startsWith(expected[i][0]));
            Assert.assertTrue(((("Actual line(file) " + i) + " bc: ") + (rs.get(i))), rs.get(i).endsWith(expected[i][1]));
        }
        // make the table ACID
        runStatementOnDriver("alter table T SET TBLPROPERTIES ('transactional'='true')", confForTez);
        rs = runStatementOnDriver("select a,b from T order by a, b", confForTez);
        Assert.assertEquals("After to Acid conversion", TestTxnCommands2.stringifyValues(values), rs);
        // run Major compaction
        runStatementOnDriver("alter table T compact 'major'", confForTez);
        TestTxnCommands2.runWorker(hiveConf);
        rs = runStatementOnDriver("select ROW__ID, a, b, INPUT__FILE__NAME from T order by ROW__ID", confForTez);
        TestAcidOnTez.LOG.warn(((testName.getMethodName()) + ": after compact major of T:"));
        for (String s : rs) {
            TestAcidOnTez.LOG.warn(s);
        }
        String[][] expected2 = new String[][]{ new String[]{ "{\"writeid\":0,\"bucketid\":536870912,\"rowid\":0}\t1\t2", "warehouse/t/base_-9223372036854775808_v0000024/bucket_00000" }, new String[]{ "{\"writeid\":0,\"bucketid\":536870912,\"rowid\":1}\t3\t4", "warehouse/t/base_-9223372036854775808_v0000024/bucket_00000" }, new String[]{ "{\"writeid\":0,\"bucketid\":536870912,\"rowid\":2}\t5\t6", "warehouse/t/base_-9223372036854775808_v0000024/bucket_00000" }, new String[]{ "{\"writeid\":0,\"bucketid\":536870912,\"rowid\":3}\t7\t8", "warehouse/t/base_-9223372036854775808_v0000024/bucket_00000" }, new String[]{ "{\"writeid\":0,\"bucketid\":536870912,\"rowid\":4}\t9\t10", "warehouse/t/base_-9223372036854775808_v0000024/bucket_00000" } };
        Assert.assertEquals("Unexpected row count after major compact", expected2.length, rs.size());
        for (int i = 0; i < (expected2.length); i++) {
            Assert.assertTrue(((("Actual line " + i) + " ac: ") + (rs.get(i))), rs.get(i).startsWith(expected2[i][0]));
            Assert.assertTrue(((("Actual line(file) " + i) + " ac: ") + (rs.get(i))), rs.get(i).endsWith(expected2[i][1]));
        }
    }

    /**
     * 1. Insert into unbucketed acid table from Union all - union is removed and data is placed in
     * subdirs of target table.
     * 2. convert to acid table and check data
     * 3. compact and check data
     * Compare with {@link #testInsertWithRemoveUnion()} where T is transactional=false
     */
    @Test
    public void testAcidInsertWithRemoveUnion() throws Exception {
        HiveConf confForTez = new HiveConf(hiveConf);// make a clone of existing hive conf

        setupTez(confForTez);
        int[][] values = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 }, new int[]{ 5, 6 }, new int[]{ 7, 8 }, new int[]{ 9, 10 } };
        runStatementOnDriver("drop table if exists T", confForTez);
        runStatementOnDriver("create table T (a int, b int) stored as ORC  TBLPROPERTIES ('transactional'='true')", confForTez);
        /* On Tez, below (T is transactional), we get the following layout
        ekoifman:apache-hive-3.0.0-SNAPSHOT-bin ekoifman$ tree  ~/dev/hiverwgit/itests/hive-unit/target/tmp/org.apache.hadoop.hive.ql.TestAcidOnTez-1505500035574/warehouse/t/.hive-staging_hive_2017-09-15_11-28-33_960_9111484239090506828-1/
        /Users/ekoifman/dev/hiverwgit/itests/hive-unit/target/tmp/org.apache.hadoop.hive.ql.TestAcidOnTez-1505500035574/warehouse/t/.hive-staging_hive_2017-09-15_11-28-33_960_9111484239090506828-1/
        ??? -ext-10000
        ??? HIVE_UNION_SUBDIR_1
        ??? ??? 000000_0
        ???     ??? _orc_acid_version
        ???     ??? delta_0000001_0000001_0001
        ???         ??? bucket_00000
        ??? HIVE_UNION_SUBDIR_2
        ??? ??? 000000_0
        ???     ??? _orc_acid_version
        ???     ??? delta_0000001_0000001_0002
        ???         ??? bucket_00000
        ??? HIVE_UNION_SUBDIR_3
        ??? 000000_0
        ??? _orc_acid_version
        ??? delta_0000001_0000001_0003
        ??? bucket_00000

        10 directories, 6 files
         */
        runStatementOnDriver((((((("insert into T(a,b) select a, b from " + (TestAcidOnTez.Table.ACIDTBL)) + " where a between 1 and 3 union all select a, b from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a between 5 and 7 union all select a, b from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a >= 9"), confForTez);
        List<String> rs = runStatementOnDriver("select ROW__ID, a, b, INPUT__FILE__NAME from T order by a, b", confForTez);
        TestAcidOnTez.LOG.warn(((testName.getMethodName()) + ": reading acid table T"));
        for (String s : rs) {
            TestAcidOnTez.LOG.warn(s);
        }
        String[][] expected2 = new String[][]{ new String[]{ "{\"writeid\":1,\"bucketid\":536870913,\"rowid\":0}\t1\t2", "warehouse/t/delta_0000001_0000001_0001/bucket_00000" }, new String[]{ "{\"writeid\":1,\"bucketid\":536870913,\"rowid\":1}\t3\t4", "warehouse/t/delta_0000001_0000001_0001/bucket_00000" }, new String[]{ "{\"writeid\":1,\"bucketid\":536870914,\"rowid\":0}\t5\t6", "warehouse/t/delta_0000001_0000001_0002/bucket_00000" }, new String[]{ "{\"writeid\":1,\"bucketid\":536870914,\"rowid\":1}\t7\t8", "warehouse/t/delta_0000001_0000001_0002/bucket_00000" }, new String[]{ "{\"writeid\":1,\"bucketid\":536870915,\"rowid\":0}\t9\t10", "warehouse/t/delta_0000001_0000001_0003/bucket_00000" } };
        Assert.assertEquals("Unexpected row count", expected2.length, rs.size());
        for (int i = 0; i < (expected2.length); i++) {
            Assert.assertTrue(((("Actual line " + i) + " ac: ") + (rs.get(i))), rs.get(i).startsWith(expected2[i][0]));
            Assert.assertTrue(((("Actual line(file) " + i) + " ac: ") + (rs.get(i))), rs.get(i).endsWith(expected2[i][1]));
        }
    }

    @Test
    public void testBucketedAcidInsertWithRemoveUnion() throws Exception {
        HiveConf confForTez = new HiveConf(hiveConf);// make a clone of existing hive conf

        setupTez(confForTez);
        int[][] values = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 4 }, new int[]{ 5, 6 }, new int[]{ 6, 8 }, new int[]{ 9, 10 } };
        runStatementOnDriver(("delete from " + (TestAcidOnTez.Table.ACIDTBL)), confForTez);
        runStatementOnDriver((("insert into " + (TestAcidOnTez.Table.ACIDTBL)) + (TestTxnCommands2.makeValuesClause(values))));// make sure both buckets are not empty

        runStatementOnDriver("drop table if exists T", confForTez);
        /* With bucketed target table Union All is not removed

        ekoifman:apache-hive-3.0.0-SNAPSHOT-bin ekoifman$ tree  ~/dev/hiverwgit/itests/hive-unit/target/tmp/org.apache.hadoop.hive.ql.TestAcidOnTez-1505510130462/warehouse/t/.hive-staging_hive_2017-09-15_14-16-32_422_4626314315862498838-1/
        /Users/ekoifman/dev/hiverwgit/itests/hive-unit/target/tmp/org.apache.hadoop.hive.ql.TestAcidOnTez-1505510130462/warehouse/t/.hive-staging_hive_2017-09-15_14-16-32_422_4626314315862498838-1/
        ??? -ext-10000
        ??? 000000_0
        ??? ??? _orc_acid_version
        ??? ??? delta_0000001_0000001_0000
        ???     ??? bucket_00000
        ??? 000001_0
        ??? _orc_acid_version
        ??? delta_0000001_0000001_0000
        ??? bucket_00001

        5 directories, 4 files
         */
        runStatementOnDriver("create table T (a int, b int) clustered by (a) into 2 buckets stored as ORC  TBLPROPERTIES ('transactional'='true')", confForTez);
        runStatementOnDriver((((((("insert into T(a,b) select a, b from " + (TestAcidOnTez.Table.ACIDTBL)) + " where a between 1 and 3 union all select a, b from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a between 5 and 7 union all select a, b from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a >= 9"), confForTez);
        List<String> rs = runStatementOnDriver("select ROW__ID, a, b, INPUT__FILE__NAME from T order by a, b", confForTez);
        TestAcidOnTez.LOG.warn(((testName.getMethodName()) + ": reading bucketed acid table T"));
        for (String s : rs) {
            TestAcidOnTez.LOG.warn(s);
        }
        String[][] expected2 = new String[][]{ new String[]{ "{\"writeid\":1,\"bucketid\":536936448,\"rowid\":0}\t1\t2", "warehouse/t/delta_0000001_0000001_0000/bucket_00001" }, new String[]{ "{\"writeid\":1,\"bucketid\":536870912,\"rowid\":0}\t2\t4", "warehouse/t/delta_0000001_0000001_0000/bucket_00000" }, new String[]{ "{\"writeid\":1,\"bucketid\":536936448,\"rowid\":2}\t5\t6", "warehouse/t/delta_0000001_0000001_0000/bucket_00001" }, new String[]{ "{\"writeid\":1,\"bucketid\":536870912,\"rowid\":1}\t6\t8", "warehouse/t/delta_0000001_0000001_0000/bucket_00000" }, new String[]{ "{\"writeid\":1,\"bucketid\":536936448,\"rowid\":1}\t9\t10", "warehouse/t/delta_0000001_0000001_0000/bucket_00001" } };
        Assert.assertEquals("Unexpected row count", expected2.length, rs.size());
        for (int i = 0; i < (expected2.length); i++) {
            Assert.assertTrue(((("Actual line " + i) + " ac: ") + (rs.get(i))), rs.get(i).startsWith(expected2[i][0]));
            Assert.assertTrue(((("Actual line(file) " + i) + " ac: ") + (rs.get(i))), rs.get(i).endsWith(expected2[i][1]));
        }
    }

    @Test
    public void testGetSplitsLocks() throws Exception {
        // Need to test this with LLAP settings, which requires some additional configurations set.
        HiveConf modConf = new HiveConf(hiveConf);
        setupTez(modConf);
        modConf.setVar(HIVE_EXECUTION_ENGINE, "tez");
        modConf.setVar(HIVEFETCHTASKCONVERSION, "more");
        modConf.setVar(LLAP_DAEMON_SERVICE_HOSTS, "localhost");
        // SessionState/Driver needs to be restarted with the Tez conf settings.
        restartSessionAndDriver(modConf);
        TxnStore txnHandler = TxnUtils.getTxnStore(modConf);
        try {
            // Request LLAP splits for a table.
            String queryParam = "select * from " + (TestAcidOnTez.Table.ACIDTBL);
            runStatementOnDriver((("select get_splits(\"" + queryParam) + "\", 1)"));
            // The get_splits call should have resulted in a lock on ACIDTBL
            ShowLocksResponse slr = txnHandler.showLocks(new ShowLocksRequest());
            TestDbTxnManager2.checkLock(SHARED_READ, ACQUIRED, "default", TestAcidOnTez.Table.ACIDTBL.name, null, slr.getLocks());
            Assert.assertEquals(1, slr.getLocksSize());
            // Try another table.
            queryParam = "select * from " + (TestAcidOnTez.Table.ACIDTBLPART);
            runStatementOnDriver((("select get_splits(\"" + queryParam) + "\", 1)"));
            // Should now have new lock on ACIDTBLPART
            slr = txnHandler.showLocks(new ShowLocksRequest());
            TestDbTxnManager2.checkLock(SHARED_READ, ACQUIRED, "default", TestAcidOnTez.Table.ACIDTBLPART.name, null, slr.getLocks());
            Assert.assertEquals(2, slr.getLocksSize());
            // There should be different txn IDs associated with each lock.
            Set<Long> txnSet = new HashSet<Long>();
            for (ShowLocksResponseElement lockResponseElem : slr.getLocks()) {
                txnSet.add(lockResponseElem.getTxnid());
            }
            Assert.assertEquals(2, txnSet.size());
            List<String> rows = runStatementOnDriver("show transactions");
            // Header row + 2 transactions = 3 rows
            Assert.assertEquals(3, rows.size());
        } finally {
            // Close the session which should free up the TxnHandler/locks held by the session.
            // Done in the finally block to make sure we free up the locks; otherwise
            // the cleanup in tearDown() will get stuck waiting on the lock held here on ACIDTBL.
            restartSessionAndDriver(hiveConf);
        }
        // Lock should be freed up now.
        ShowLocksResponse slr = txnHandler.showLocks(new ShowLocksRequest());
        Assert.assertEquals(0, slr.getLocksSize());
        List<String> rows = runStatementOnDriver("show transactions");
        // Transactions should be committed.
        // No transactions - just the header row
        Assert.assertEquals(1, rows.size());
    }

    @Test
    public void testGetSplitsLocksWithMaterializedView() throws Exception {
        // Need to test this with LLAP settings, which requires some additional configurations set.
        HiveConf modConf = new HiveConf(hiveConf);
        setupTez(modConf);
        modConf.setVar(HIVE_EXECUTION_ENGINE, "tez");
        modConf.setVar(HIVEFETCHTASKCONVERSION, "more");
        modConf.setVar(LLAP_DAEMON_SERVICE_HOSTS, "localhost");
        // SessionState/Driver needs to be restarted with the Tez conf settings.
        restartSessionAndDriver(modConf);
        TxnStore txnHandler = TxnUtils.getTxnStore(modConf);
        String mvName = "mv_acidTbl";
        try {
            runStatementOnDriver((((("create materialized view " + mvName) + " as select a from ") + (TestAcidOnTez.Table.ACIDTBL)) + " where a > 5"));
            // Request LLAP splits for a table.
            String queryParam = ("select a from " + (TestAcidOnTez.Table.ACIDTBL)) + " where a > 5";
            runStatementOnDriver((("select get_splits(\"" + queryParam) + "\", 1)"));
            // The get_splits call should have resulted in a lock on ACIDTBL and materialized view mv_acidTbl
            ShowLocksResponse slr = txnHandler.showLocks(new ShowLocksRequest());
            TestDbTxnManager2.checkLock(SHARED_READ, ACQUIRED, "default", TestAcidOnTez.Table.ACIDTBL.name, null, slr.getLocks());
            TestDbTxnManager2.checkLock(SHARED_READ, ACQUIRED, "default", mvName, null, slr.getLocks());
            Assert.assertEquals(2, slr.getLocksSize());
        } finally {
            // Close the session which should free up the TxnHandler/locks held by the session.
            // Done in the finally block to make sure we free up the locks; otherwise
            // the cleanup in tearDown() will get stuck waiting on the lock held here on ACIDTBL.
            restartSessionAndDriver(hiveConf);
            runStatementOnDriver(("drop materialized view if exists " + mvName));
        }
        // Lock should be freed up now.
        ShowLocksResponse slr = txnHandler.showLocks(new ShowLocksRequest());
        Assert.assertEquals(0, slr.getLocksSize());
        List<String> rows = runStatementOnDriver("show transactions");
        // Transactions should be committed.
        // No transactions - just the header row
        Assert.assertEquals(1, rows.size());
    }

    /**
     * HIVE-20699
     *
     * see TestTxnCommands3.testCompactor
     */
    @Test
    public void testCrudMajorCompactionSplitGrouper() throws Exception {
        String tblName = "test_split_grouper";
        // make a clone of existing hive conf
        HiveConf confForTez = new HiveConf(hiveConf);
        setupTez(confForTez);// one-time setup to make query able to run with Tez

        HiveConf.setVar(confForTez, HiveConf.ConfVars.HIVEFETCHTASKCONVERSION, "none");
        runStatementOnDriver((((("create transactional table " + tblName) + " (a int, b int) clustered by (a) into 2 buckets ") + "stored as ORC TBLPROPERTIES('bucketing_version'='2', 'transactional'='true',") + " 'transactional_properties'='default')"), confForTez);
        runStatementOnDriver((("insert into " + tblName) + " values(1,2),(1,3),(1,4),(2,2),(2,3),(2,4)"), confForTez);
        runStatementOnDriver((("insert into " + tblName) + " values(3,2),(3,3),(3,4),(4,2),(4,3),(4,4)"), confForTez);
        runStatementOnDriver((("delete from " + tblName) + " where b = 2"));
        List<String> expectedRs = new ArrayList<>();
        expectedRs.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":0}\t2\t4");
        expectedRs.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":1}\t2\t3");
        expectedRs.add("{\"writeid\":2,\"bucketid\":536870912,\"rowid\":0}\t3\t4");
        expectedRs.add("{\"writeid\":2,\"bucketid\":536870912,\"rowid\":1}\t3\t3");
        expectedRs.add("{\"writeid\":1,\"bucketid\":536936448,\"rowid\":0}\t1\t4");
        expectedRs.add("{\"writeid\":1,\"bucketid\":536936448,\"rowid\":1}\t1\t3");
        expectedRs.add("{\"writeid\":2,\"bucketid\":536936448,\"rowid\":0}\t4\t4");
        expectedRs.add("{\"writeid\":2,\"bucketid\":536936448,\"rowid\":1}\t4\t3");
        List<String> rs = runStatementOnDriver((("select ROW__ID, * from " + tblName) + " order by ROW__ID.bucketid, ROW__ID"), confForTez);
        HiveConf.setVar(confForTez, SPLIT_GROUPING_MODE, "compactor");
        // No order by needed: this should use the compactor split grouping to return the rows in correct order
        List<String> rsCompact = runStatementOnDriver(("select ROW__ID, * from  " + tblName), confForTez);
        Assert.assertEquals("normal read", expectedRs, rs);
        Assert.assertEquals("compacted read", rs, rsCompact);
    }
}

