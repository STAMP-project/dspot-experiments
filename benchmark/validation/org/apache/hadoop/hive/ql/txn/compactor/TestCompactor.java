/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.txn.compactor;


import AcidUtils.baseFileFilter;
import AcidUtils.deleteEventDeltaDirFilter;
import AcidUtils.deltaFileFilter;
import AcidUtils.hiddenFileFilter;
import CompactionType.MAJOR;
import HiveConf.ConfVars.COMPACTOR_JOB_QUEUE;
import HiveConf.ConfVars.HIVE_COMPACTOR_DELTA_NUM_THRESHOLD;
import HiveConf.ConfVars.HIVE_COMPACTOR_DELTA_PCT_THRESHOLD;
import OrcConf.BUFFER_SIZE;
import TxnStore.INITIATED_RESPONSE;
import TxnStore.SUCCEEDED_RESPONSE;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponseElement;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.txn.CompactionInfo;
import org.apache.hadoop.hive.metastore.txn.TxnStore;
import org.apache.hadoop.hive.metastore.txn.TxnUtils;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.TestTxnCommands2;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hive.common.util.Retry;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.streaming.StreamingConnection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCompactor {
    private static final AtomicInteger salt = new AtomicInteger(new Random().nextInt());

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactor.class);

    private final String TEST_DATA_DIR = HCatUtil.makePathASafeFileName((((((((System.getProperty("java.io.tmpdir")) + (File.separator)) + (TestCompactor.class.getCanonicalName())) + "-") + (System.currentTimeMillis())) + "_") + (TestCompactor.salt.getAndIncrement())));

    private final String BASIC_FILE_NAME = (TEST_DATA_DIR) + "/basic.input.data";

    private final String TEST_WAREHOUSE_DIR = (TEST_DATA_DIR) + "/warehouse";

    @Rule
    public TemporaryFolder stagingFolder = new TemporaryFolder();

    @Rule
    public Retry retry = new Retry(2);

    private HiveConf conf;

    IMetaStoreClient msClient;

    private IDriver driver;

    /**
     * Simple schema evolution add columns with partitioning.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void schemaEvolutionAddColDynamicPartitioningInsert() throws Exception {
        String tblName = "dpct";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds string)") + " CLUSTERED BY(a) INTO 2 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC TBLPROPERTIES ('transactional'='true')"), driver);
        // First INSERT round.
        TestCompactor.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values (1, 'fred', ") + "'today'), (2, 'wilma', 'yesterday')"), driver);
        // ALTER TABLE ... ADD COLUMNS
        TestCompactor.executeStatementOnDriver((("ALTER TABLE " + tblName) + " ADD COLUMNS(c int)"), driver);
        // Validate there is an added NULL for column c.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        ArrayList<String> valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(2, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tfred\tNULL\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\twilma\tNULL\tyesterday", valuesReadFromHiveDriver.get(1));
        // Second INSERT round with new inserts into previously existing partition 'yesterday'.
        TestCompactor.executeStatementOnDriver((((("insert into " + tblName) + " partition (ds) values ") + "(3, 'mark', 1900, 'soon'), (4, 'douglas', 1901, 'last_century'), ") + "(5, 'doc', 1902, 'yesterday')"), driver);
        // Validate there the new insertions for column c.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(5, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tfred\tNULL\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\twilma\tNULL\tyesterday", valuesReadFromHiveDriver.get(1));
        Assert.assertEquals("3\tmark\t1900\tsoon", valuesReadFromHiveDriver.get(2));
        Assert.assertEquals("4\tdouglas\t1901\tlast_century", valuesReadFromHiveDriver.get(3));
        Assert.assertEquals("5\tdoc\t1902\tyesterday", valuesReadFromHiveDriver.get(4));
        conf.setIntVar(HIVE_COMPACTOR_DELTA_NUM_THRESHOLD, 0);
        TestTxnCommands2.runInitiator(conf);
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        List<ShowCompactResponseElement> compacts = rsp.getCompacts();
        Assert.assertEquals(4, compacts.size());
        SortedSet<String> partNames = new TreeSet<String>();
        verifyCompactions(compacts, partNames, tblName);
        List<String> names = new ArrayList<String>(partNames);
        Assert.assertEquals("ds=last_century", names.get(0));
        Assert.assertEquals("ds=soon", names.get(1));
        Assert.assertEquals("ds=today", names.get(2));
        Assert.assertEquals("ds=yesterday", names.get(3));
        // Validate after compaction.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(5, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tfred\tNULL\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\twilma\tNULL\tyesterday", valuesReadFromHiveDriver.get(1));
        Assert.assertEquals("3\tmark\t1900\tsoon", valuesReadFromHiveDriver.get(2));
        Assert.assertEquals("4\tdouglas\t1901\tlast_century", valuesReadFromHiveDriver.get(3));
        Assert.assertEquals("5\tdoc\t1902\tyesterday", valuesReadFromHiveDriver.get(4));
    }

    @Test
    public void schemaEvolutionAddColDynamicPartitioningUpdate() throws Exception {
        String tblName = "udpct";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds string)") + " CLUSTERED BY(a) INTO 2 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC TBLPROPERTIES ('transactional'='true')"), driver);
        TestCompactor.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values (1, 'fred', ") + "'today'), (2, 'wilma', 'yesterday')"), driver);
        TestCompactor.executeStatementOnDriver((("update " + tblName) + " set b = 'barney'"), driver);
        // Validate the update.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        ArrayList<String> valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(2, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tbarney\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\tbarney\tyesterday", valuesReadFromHiveDriver.get(1));
        // ALTER TABLE ... ADD COLUMNS
        TestCompactor.executeStatementOnDriver((("ALTER TABLE " + tblName) + " ADD COLUMNS(c int)"), driver);
        // Validate there is an added NULL for column c.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(2, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tbarney\tNULL\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\tbarney\tNULL\tyesterday", valuesReadFromHiveDriver.get(1));
        // Second INSERT round with new inserts into previously existing partition 'yesterday'.
        TestCompactor.executeStatementOnDriver((((("insert into " + tblName) + " partition (ds) values ") + "(3, 'mark', 1900, 'soon'), (4, 'douglas', 1901, 'last_century'), ") + "(5, 'doc', 1902, 'yesterday')"), driver);
        // Validate there the new insertions for column c.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(5, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tbarney\tNULL\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\tbarney\tNULL\tyesterday", valuesReadFromHiveDriver.get(1));
        Assert.assertEquals("3\tmark\t1900\tsoon", valuesReadFromHiveDriver.get(2));
        Assert.assertEquals("4\tdouglas\t1901\tlast_century", valuesReadFromHiveDriver.get(3));
        Assert.assertEquals("5\tdoc\t1902\tyesterday", valuesReadFromHiveDriver.get(4));
        TestCompactor.executeStatementOnDriver((("update " + tblName) + " set c = 2000"), driver);
        // Validate the update of new column c, even in old rows.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(5, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tbarney\t2000\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\tbarney\t2000\tyesterday", valuesReadFromHiveDriver.get(1));
        Assert.assertEquals("3\tmark\t2000\tsoon", valuesReadFromHiveDriver.get(2));
        Assert.assertEquals("4\tdouglas\t2000\tlast_century", valuesReadFromHiveDriver.get(3));
        Assert.assertEquals("5\tdoc\t2000\tyesterday", valuesReadFromHiveDriver.get(4));
        // Set to 1 so insert doesn't set it off but update does
        conf.setIntVar(HIVE_COMPACTOR_DELTA_NUM_THRESHOLD, 1);
        TestTxnCommands2.runInitiator(conf);
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        List<ShowCompactResponseElement> compacts = rsp.getCompacts();
        Assert.assertEquals(4, compacts.size());
        SortedSet<String> partNames = new TreeSet<String>();
        verifyCompactions(compacts, partNames, tblName);
        List<String> names = new ArrayList<String>(partNames);
        Assert.assertEquals("ds=last_century", names.get(0));
        Assert.assertEquals("ds=soon", names.get(1));
        Assert.assertEquals("ds=today", names.get(2));
        Assert.assertEquals("ds=yesterday", names.get(3));
        // Validate after compaction.
        TestCompactor.executeStatementOnDriver((("SELECT * FROM " + tblName) + " ORDER BY a"), driver);
        valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(5, valuesReadFromHiveDriver.size());
        Assert.assertEquals("1\tbarney\t2000\ttoday", valuesReadFromHiveDriver.get(0));
        Assert.assertEquals("2\tbarney\t2000\tyesterday", valuesReadFromHiveDriver.get(1));
        Assert.assertEquals("3\tmark\t2000\tsoon", valuesReadFromHiveDriver.get(2));
        Assert.assertEquals("4\tdouglas\t2000\tlast_century", valuesReadFromHiveDriver.get(3));
        Assert.assertEquals("5\tdoc\t2000\tyesterday", valuesReadFromHiveDriver.get(4));
    }

    /**
     * After each major compaction, stats need to be updated on each column of the
     * table/partition which previously had stats.
     * 1. create a bucketed ORC backed table (Orc is currently required by ACID)
     * 2. populate 2 partitions with data
     * 3. compute stats
     * 4. insert some data into the table using StreamingAPI
     * 5. Trigger major compaction (which should update stats)
     * 6. check that stats have been updated
     *
     * @throws Exception
     * 		todo:
     * 		2. add non-partitioned test
     * 		4. add a test with sorted table?
     */
    @Test
    public void testStatsAfterCompactionPartTbl() throws Exception {
        testStatsAfterCompactionPartTbl(false);
    }

    @Test
    public void testStatsAfterCompactionPartTblNew() throws Exception {
        testStatsAfterCompactionPartTbl(true);
    }

    @Test
    public void dynamicPartitioningInsert() throws Exception {
        String tblName = "dpct";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds string)") + " CLUSTERED BY(a) INTO 2 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC TBLPROPERTIES ('transactional'='true')"), driver);
        TestCompactor.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values (1, 'fred', ") + "'today'), (2, 'wilma', 'yesterday')"), driver);
        conf.setIntVar(HIVE_COMPACTOR_DELTA_NUM_THRESHOLD, 0);
        TestTxnCommands2.runInitiator(conf);
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        List<ShowCompactResponseElement> compacts = rsp.getCompacts();
        Assert.assertEquals(2, compacts.size());
        SortedSet<String> partNames = new TreeSet<String>();
        verifyCompactions(compacts, partNames, tblName);
        List<String> names = new ArrayList<String>(partNames);
        Assert.assertEquals("ds=today", names.get(0));
        Assert.assertEquals("ds=yesterday", names.get(1));
    }

    @Test
    public void dynamicPartitioningUpdate() throws Exception {
        String tblName = "udpct";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds string)") + " CLUSTERED BY(a) INTO 2 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC TBLPROPERTIES ('transactional'='true')"), driver);
        TestCompactor.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values (1, 'fred', ") + "'today'), (2, 'wilma', 'yesterday')"), driver);
        TestCompactor.executeStatementOnDriver((("update " + tblName) + " set b = 'barney'"), driver);
        // Set to 1 so insert doesn't set it off but update does
        conf.setIntVar(HIVE_COMPACTOR_DELTA_NUM_THRESHOLD, 1);
        TestTxnCommands2.runInitiator(conf);
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        List<ShowCompactResponseElement> compacts = rsp.getCompacts();
        Assert.assertEquals(2, compacts.size());
        SortedSet<String> partNames = new TreeSet<String>();
        verifyCompactions(compacts, partNames, tblName);
        List<String> names = new ArrayList<String>(partNames);
        Assert.assertEquals("ds=today", names.get(0));
        Assert.assertEquals("ds=yesterday", names.get(1));
    }

    @Test
    public void dynamicPartitioningDelete() throws Exception {
        String tblName = "ddpct";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds string)") + " CLUSTERED BY(a) INTO 2 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC TBLPROPERTIES ('transactional'='true')"), driver);
        TestCompactor.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values (1, 'fred', ") + "'today'), (2, 'wilma', 'yesterday')"), driver);
        TestCompactor.executeStatementOnDriver((("update " + tblName) + " set b = 'fred' where a = 1"), driver);
        TestCompactor.executeStatementOnDriver((("delete from " + tblName) + " where b = 'fred'"), driver);
        // Set to 2 so insert and update don't set it off but delete does
        conf.setIntVar(HIVE_COMPACTOR_DELTA_NUM_THRESHOLD, 2);
        TestTxnCommands2.runInitiator(conf);
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        List<ShowCompactResponseElement> compacts = rsp.getCompacts();
        Assert.assertEquals(1, compacts.size());
        SortedSet<String> partNames = new TreeSet<String>();
        verifyCompactions(compacts, partNames, tblName);
        List<String> names = new ArrayList<String>(partNames);
        Assert.assertEquals("ds=today", names.get(0));
    }

    @Test
    public void minorCompactWhileStreaming() throws Exception {
        String dbName = "default";
        String tblName = "cws";
        String columnNamesProperty = "a,b";
        String columnTypesProperty = "int:string";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " CLUSTERED BY(a) INTO 1 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC  TBLPROPERTIES ('transactional'='true')"), driver);
        StreamingConnection connection = null;
        try {
            // Write a couple of batches
            for (int i = 0; i < 2; i++) {
                writeBatch(dbName, tblName, false);
            }
            // Start a third batch, but don't close it.
            connection = writeBatch(dbName, tblName, true);
            // Now, compact
            TxnStore txnHandler = TxnUtils.getTxnStore(conf);
            txnHandler.compact(new org.apache.hadoop.hive.metastore.api.CompactionRequest(dbName, tblName, CompactionType.MINOR));
            TestTxnCommands2.runWorker(conf);
            // Find the location of the table
            IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
            Table table = msClient.getTable(dbName, tblName);
            FileSystem fs = FileSystem.get(conf);
            FileStatus[] stat = fs.listStatus(new Path(table.getSd().getLocation()), deltaFileFilter);
            String[] names = new String[stat.length];
            Path resultFile = null;
            for (int i = 0; i < (names.length); i++) {
                names[i] = stat[i].getPath().getName();
                if (names[i].equals("delta_0000001_0000004_v0000009")) {
                    resultFile = stat[i].getPath();
                }
            }
            Arrays.sort(names);
            String[] expected = new String[]{ "delta_0000001_0000002", "delta_0000001_0000004_v0000009", "delta_0000003_0000004", "delta_0000005_0000006" };
            if (!(Arrays.deepEquals(expected, names))) {
                Assert.fail(((((("Expected: " + (Arrays.toString(expected))) + ", found: ") + (Arrays.toString(names))) + ",stat=") + (TestCompactor.toString(stat))));
            }
            checkExpectedTxnsPresent(null, new Path[]{ resultFile }, columnNamesProperty, columnTypesProperty, 0, 1L, 4L, 1);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Test
    public void majorCompactWhileStreaming() throws Exception {
        String dbName = "default";
        String tblName = "cws";
        String columnNamesProperty = "a,b";
        String columnTypesProperty = "int:string";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " CLUSTERED BY(a) INTO 1 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC  TBLPROPERTIES ('transactional'='true') "), driver);
        StreamingConnection connection = null;
        try {
            // Write a couple of batches
            for (int i = 0; i < 2; i++) {
                writeBatch(dbName, tblName, false);
            }
            // Start a third batch, but don't close it.  this delta will be ignored by compaction since
            // it has an open txn in it
            connection = writeBatch(dbName, tblName, true);
            runMajorCompaction(dbName, tblName);
            // Find the location of the table
            IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
            Table table = msClient.getTable(dbName, tblName);
            FileSystem fs = FileSystem.get(conf);
            FileStatus[] stat = fs.listStatus(new Path(table.getSd().getLocation()), baseFileFilter);
            if (1 != (stat.length)) {
                Assert.fail(((("Expecting 1 file \"base_0000004\" and found " + (stat.length)) + " files ") + (Arrays.toString(stat))));
            }
            String name = stat[0].getPath().getName();
            Assert.assertEquals("base_0000004_v0000009", name);
            checkExpectedTxnsPresent(stat[0].getPath(), null, columnNamesProperty, columnTypesProperty, 0, 1L, 4L, 1);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Test
    public void minorCompactAfterAbort() throws Exception {
        minorCompactAfterAbort(false);
    }

    @Test
    public void minorCompactAfterAbortNew() throws Exception {
        minorCompactAfterAbort(true);
    }

    @Test
    public void majorCompactAfterAbort() throws Exception {
        majorCompactAfterAbort(false);
    }

    @Test
    public void majorCompactAfterAbortNew() throws Exception {
        majorCompactAfterAbort(true);
    }

    @Test
    public void mmTable() throws Exception {
        String dbName = "default";
        String tblName = "mm_nonpart";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((("CREATE TABLE " + tblName) + "(a INT, b STRING) STORED AS ORC") + " TBLPROPERTIES ('transactional'='true', 'transactional_properties'='insert_only')"), driver);
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        msClient.close();
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(1, 'foo')"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(2, 'bar')"), driver);
        verifyFooBarResult(tblName, 1);
        // Check that we have two deltas.
        FileSystem fs = FileSystem.get(conf);
        verifyDeltaCount(table.getSd(), fs, 2);
        runMajorCompaction(dbName, tblName);
        verifyFooBarResult(tblName, 1);
        verifyHasBase(table.getSd(), fs, "base_0000002_v0000006");
        // Make sure we don't compact if we don't need to compact.
        runMajorCompaction(dbName, tblName);
        verifyFooBarResult(tblName, 1);
        verifyHasBase(table.getSd(), fs, "base_0000002_v0000006");
    }

    @Test
    public void mmTableOriginalsOrc() throws Exception {
        mmTableOriginals("ORC");
    }

    @Test
    public void mmTableOriginalsText() throws Exception {
        mmTableOriginals("TEXTFILE");
    }

    @Test
    public void mmTableBucketed() throws Exception {
        String dbName = "default";
        String tblName = "mm_nonpart";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver((((("CREATE TABLE " + tblName) + "(a INT, b STRING) CLUSTERED BY (a) ") + "INTO 64 BUCKETS STORED AS ORC TBLPROPERTIES ('transactional'='true', ") + "'transactional_properties'='insert_only')"), driver);
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        msClient.close();
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(1, 'foo')"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(2, 'bar')"), driver);
        verifyFooBarResult(tblName, 1);
        // Check that we have two deltas.
        FileSystem fs = FileSystem.get(conf);
        verifyDeltaCount(table.getSd(), fs, 2);
        runMajorCompaction(dbName, tblName);
        verifyFooBarResult(tblName, 1);
        String baseDir = "base_0000002_v0000006";
        verifyHasBase(table.getSd(), fs, baseDir);
        FileStatus[] files = fs.listStatus(new Path(table.getSd().getLocation(), baseDir), hiddenFileFilter);
        Assert.assertEquals(Lists.newArrayList(files).toString(), 64, files.length);
    }

    @Test
    public void mmTableOpenWriteId() throws Exception {
        String dbName = "default";
        String tblName = "mm_nonpart";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((("CREATE TABLE " + tblName) + "(a INT, b STRING) STORED AS TEXTFILE") + " TBLPROPERTIES ('transactional'='true', 'transactional_properties'='insert_only')"), driver);
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        msClient.close();
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(1, 'foo')"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(2, 'bar')"), driver);
        verifyFooBarResult(tblName, 1);
        long openTxnId = msClient.openTxn("test");
        long openWriteId = msClient.allocateTableWriteId(openTxnId, dbName, tblName);
        Assert.assertEquals(3, openWriteId);// Just check to make sure base_5 below is not new.

        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(1, 'foo')"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(2, 'bar')"), driver);
        verifyFooBarResult(tblName, 2);
        runMajorCompaction(dbName, tblName);// Don't compact 4 and 5; 3 is opened.

        FileSystem fs = FileSystem.get(conf);
        verifyHasBase(table.getSd(), fs, "base_0000002_v0000010");
        verifyDirCount(table.getSd(), fs, 1, baseFileFilter);
        verifyFooBarResult(tblName, 2);
        TestTxnCommands2.runCleaner(conf);
        verifyHasDir(table.getSd(), fs, "delta_0000004_0000004_0000", deltaFileFilter);
        verifyHasDir(table.getSd(), fs, "delta_0000005_0000005_0000", deltaFileFilter);
        verifyFooBarResult(tblName, 2);
        msClient.abortTxns(Lists.newArrayList(openTxnId));// Now abort 3.

        runMajorCompaction(dbName, tblName);// Compact 4 and 5.

        verifyFooBarResult(tblName, 2);
        verifyHasBase(table.getSd(), fs, "base_0000005_v0000016");
        TestTxnCommands2.runCleaner(conf);
        verifyDeltaCount(table.getSd(), fs, 0);
    }

    @Test
    public void mmTablePartitioned() throws Exception {
        String dbName = "default";
        String tblName = "mm_part";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds int) STORED AS TEXTFILE") + " TBLPROPERTIES ('transactional'='true', 'transactional_properties'='insert_only')"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(1, 'foo', 1)"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(1, 'foo', 1)"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(2, 'bar', 1)"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(1, 'foo', 2)"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(2, 'bar', 2)"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(2, 'bar', 3)"), driver);
        verifyFooBarResult(tblName, 3);
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Partition p1 = msClient.getPartition(dbName, tblName, "ds=1");
        Partition p2 = msClient.getPartition(dbName, tblName, "ds=2");
        Partition p3 = msClient.getPartition(dbName, tblName, "ds=3");
        msClient.close();
        FileSystem fs = FileSystem.get(conf);
        verifyDeltaCount(p1.getSd(), fs, 3);
        verifyDeltaCount(p2.getSd(), fs, 2);
        verifyDeltaCount(p3.getSd(), fs, 1);
        runMajorCompaction(dbName, tblName, "ds=1", "ds=2", "ds=3");
        verifyFooBarResult(tblName, 3);
        verifyDeltaCount(p3.getSd(), fs, 1);
        verifyHasBase(p1.getSd(), fs, "base_0000006_v0000010");
        verifyHasBase(p2.getSd(), fs, "base_0000006_v0000014");
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(1, 'foo', 2)"), driver);
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + " partition (ds) VALUES(2, 'bar', 2)"), driver);
        runMajorCompaction(dbName, tblName, "ds=1", "ds=2", "ds=3");
        // Make sure we don't compact if we don't need to compact; but do if we do.
        verifyFooBarResult(tblName, 4);
        verifyDeltaCount(p3.getSd(), fs, 1);
        verifyHasBase(p1.getSd(), fs, "base_0000006_v0000010");
        verifyHasBase(p2.getSd(), fs, "base_0000008_v0000023");
    }

    @Test
    public void majorCompactWhileStreamingForSplitUpdate() throws Exception {
        majorCompactWhileStreamingForSplitUpdate(false);
    }

    @Test
    public void majorCompactWhileStreamingForSplitUpdateNew() throws Exception {
        majorCompactWhileStreamingForSplitUpdate(true);
    }

    @Test
    public void testMinorCompactionForSplitUpdateWithInsertsAndDeletes() throws Exception {
        String dbName = "default";
        String tblName = "cws";
        String columnNamesProperty = "a,b";
        String columnTypesProperty = "int:string";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " CLUSTERED BY(a) INTO 1 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC  TBLPROPERTIES ('transactional'='true',") + "'transactional_properties'='default')"), driver);
        // Insert some data -> this will generate only insert deltas and no delete deltas: delta_3_3
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(1, 'foo')"), driver);
        // Insert some data -> this will again generate only insert deltas and no delete deltas: delta_4_4
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(2, 'bar')"), driver);
        // Delete some data -> this will generate only delete deltas and no insert deltas: delete_delta_5_5
        TestCompactor.executeStatementOnDriver((("DELETE FROM " + tblName) + " WHERE a = 2"), driver);
        // Now, compact -> Compaction produces a single range for both delta and delete delta
        // That is, both delta and delete_deltas would be compacted into delta_3_5 and delete_delta_3_5
        // even though there are only two delta_3_3, delta_4_4 and one delete_delta_5_5.
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        txnHandler.compact(new org.apache.hadoop.hive.metastore.api.CompactionRequest(dbName, tblName, CompactionType.MINOR));
        TestTxnCommands2.runWorker(conf);
        // Find the location of the table
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        FileSystem fs = FileSystem.get(conf);
        // Verify that we have got correct set of deltas.
        FileStatus[] stat = fs.listStatus(new Path(table.getSd().getLocation()), deltaFileFilter);
        String[] deltas = new String[stat.length];
        Path minorCompactedDelta = null;
        for (int i = 0; i < (deltas.length); i++) {
            deltas[i] = stat[i].getPath().getName();
            if (deltas[i].equals("delta_0000001_0000003_v0000006")) {
                minorCompactedDelta = stat[i].getPath();
            }
        }
        Arrays.sort(deltas);
        String[] expectedDeltas = new String[]{ "delta_0000001_0000001_0000", "delta_0000001_0000003_v0000006", "delta_0000002_0000002_0000" };
        if (!(Arrays.deepEquals(expectedDeltas, deltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeltas))) + ", found: ") + (Arrays.toString(deltas))));
        }
        checkExpectedTxnsPresent(null, new Path[]{ minorCompactedDelta }, columnNamesProperty, columnTypesProperty, 0, 1L, 2L, 1);
        // Verify that we have got correct set of delete_deltas.
        FileStatus[] deleteDeltaStat = fs.listStatus(new Path(table.getSd().getLocation()), deleteEventDeltaDirFilter);
        String[] deleteDeltas = new String[deleteDeltaStat.length];
        Path minorCompactedDeleteDelta = null;
        for (int i = 0; i < (deleteDeltas.length); i++) {
            deleteDeltas[i] = deleteDeltaStat[i].getPath().getName();
            if (deleteDeltas[i].equals("delete_delta_0000001_0000003_v0000006")) {
                minorCompactedDeleteDelta = deleteDeltaStat[i].getPath();
            }
        }
        Arrays.sort(deleteDeltas);
        String[] expectedDeleteDeltas = new String[]{ "delete_delta_0000001_0000003_v0000006", "delete_delta_0000003_0000003_0000" };
        if (!(Arrays.deepEquals(expectedDeleteDeltas, deleteDeltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeleteDeltas))) + ", found: ") + (Arrays.toString(deleteDeltas))));
        }
        checkExpectedTxnsPresent(null, new Path[]{ minorCompactedDeleteDelta }, columnNamesProperty, columnTypesProperty, 0, 2L, 2L, 1);
    }

    @Test
    public void testMinorCompactionForSplitUpdateWithOnlyInserts() throws Exception {
        String dbName = "default";
        String tblName = "cws";
        String columnNamesProperty = "a,b";
        String columnTypesProperty = "int:string";
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCompactor.executeStatementOnDriver(((((("CREATE TABLE " + tblName) + "(a INT, b STRING) ") + " CLUSTERED BY(a) INTO 1 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC  TBLPROPERTIES ('transactional'='true',") + "'transactional_properties'='default')"), driver);
        // Insert some data -> this will generate only insert deltas and no delete deltas: delta_1_1
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(1, 'foo')"), driver);
        // Insert some data -> this will again generate only insert deltas and no delete deltas: delta_2_2
        TestCompactor.executeStatementOnDriver((("INSERT INTO " + tblName) + "(a,b) VALUES(2, 'bar')"), driver);
        // Now, compact
        // One important thing to note in this test is that minor compaction always produces
        // delta_x_y and a counterpart delete_delta_x_y, even when there are no delete_delta events.
        // Such a choice has been made to simplify processing of AcidUtils.getAcidState().
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        txnHandler.compact(new org.apache.hadoop.hive.metastore.api.CompactionRequest(dbName, tblName, CompactionType.MINOR));
        TestTxnCommands2.runWorker(conf);
        // Find the location of the table
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        FileSystem fs = FileSystem.get(conf);
        // Verify that we have got correct set of deltas.
        FileStatus[] stat = fs.listStatus(new Path(table.getSd().getLocation()), deltaFileFilter);
        String[] deltas = new String[stat.length];
        Path minorCompactedDelta = null;
        for (int i = 0; i < (deltas.length); i++) {
            deltas[i] = stat[i].getPath().getName();
            if (deltas[i].equals("delta_0000001_0000002_v0000005")) {
                minorCompactedDelta = stat[i].getPath();
            }
        }
        Arrays.sort(deltas);
        String[] expectedDeltas = new String[]{ "delta_0000001_0000001_0000", "delta_0000001_0000002_v0000005", "delta_0000002_0000002_0000" };
        if (!(Arrays.deepEquals(expectedDeltas, deltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeltas))) + ", found: ") + (Arrays.toString(deltas))));
        }
        checkExpectedTxnsPresent(null, new Path[]{ minorCompactedDelta }, columnNamesProperty, columnTypesProperty, 0, 1L, 2L, 1);
        // Assert that we have no delete deltas if there are no input delete events.
        FileStatus[] deleteDeltaStat = fs.listStatus(new Path(table.getSd().getLocation()), deleteEventDeltaDirFilter);
        Assert.assertEquals(0, deleteDeltaStat.length);
    }

    @Test
    public void minorCompactWhileStreamingWithSplitUpdate() throws Exception {
        minorCompactWhileStreamingWithSplitUpdate(true);
    }

    @Test
    public void minorCompactWhileStreamingWithSplitUpdateNew() throws Exception {
        minorCompactWhileStreamingWithSplitUpdate(true);
    }

    /**
     * Users have the choice of specifying compaction related tblproperties either in CREATE TABLE
     * statement or in ALTER TABLE .. COMPACT statement. This tests both cases.
     */
    @Test
    public void testTableProperties() throws Exception {
        conf.setVar(COMPACTOR_JOB_QUEUE, "root.user1");
        String tblName1 = "ttp1";// plain acid table

        String tblName2 = "ttp2";// acid table with customized tblproperties

        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName1), driver);
        TestCompactor.executeStatementOnDriver(("drop table if exists " + tblName2), driver);
        TestCompactor.executeStatementOnDriver((((("CREATE TABLE " + tblName1) + "(a INT, b STRING) ") + " CLUSTERED BY(a) INTO 2 BUCKETS STORED AS ORC") + " TBLPROPERTIES ('transactional'='true', 'orc.compress.size'='2700')"), driver);
        TestCompactor.executeStatementOnDriver(((((((((("CREATE TABLE " + tblName2) + "(a INT, b STRING) ") + " CLUSTERED BY(a) INTO 2 BUCKETS STORED AS ORC TBLPROPERTIES (") + "'transactional'='true',") + "'compactor.mapreduce.map.memory.mb'='2048',")// 2048 MB memory for compaction map job
         + "'compactorthreshold.hive.compactor.delta.num.threshold'='4',")// minor compaction if more than 4 delta dirs
         + "'compactorthreshold.hive.compactor.delta.pct.threshold'='0.47',")// major compaction if more than 47%
         + "'compactor.hive.compactor.job.queue'='root.user2'")// Override the system wide compactor queue for this table
         + ")"), driver);
        // Insert 5 rows to both tables
        TestCompactor.executeStatementOnDriver((("insert into " + tblName1) + " values (1, 'a')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName1) + " values (2, 'b')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName1) + " values (3, 'c')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName1) + " values (4, 'd')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName1) + " values (5, 'e')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (1, 'a')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (2, 'b')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (3, 'c')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (4, 'd')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (5, 'e')"), driver);
        TestTxnCommands2.runInitiator(conf);
        // Compactor should only schedule compaction for ttp2 (delta.num.threshold=4), not ttp1
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        Assert.assertEquals(1, rsp.getCompacts().size());
        Assert.assertEquals(INITIATED_RESPONSE, rsp.getCompacts().get(0).getState());
        Assert.assertEquals("ttp2", rsp.getCompacts().get(0).getTablename());
        Assert.assertEquals(MAJOR, rsp.getCompacts().get(0).getType());// type is MAJOR since there's no base yet

        // Finish the scheduled compaction for ttp2, and manually compact ttp1, to make them comparable again
        TestCompactor.executeStatementOnDriver((("alter table " + tblName1) + " compact 'major'"), driver);
        rsp = txnHandler.showCompact(new ShowCompactRequest());
        Assert.assertEquals(2, rsp.getCompacts().size());
        Assert.assertEquals("ttp2", rsp.getCompacts().get(0).getTablename());
        Assert.assertEquals(INITIATED_RESPONSE, rsp.getCompacts().get(0).getState());
        Assert.assertEquals("ttp1", rsp.getCompacts().get(1).getTablename());
        Assert.assertEquals(INITIATED_RESPONSE, rsp.getCompacts().get(1).getState());
        // compact ttp2, by running the Worker explicitly, in order to get the reference to the compactor MR job
        TestTxnCommands2.runWorker(conf);
        // Compact ttp1
        TestTxnCommands2.runWorker(conf);
        // Clean up
        TestTxnCommands2.runCleaner(conf);
        rsp = txnHandler.showCompact(new ShowCompactRequest());
        Assert.assertEquals(2, rsp.getCompacts().size());
        Assert.assertEquals("ttp2", rsp.getCompacts().get(0).getTablename());
        Assert.assertEquals(SUCCEEDED_RESPONSE, rsp.getCompacts().get(0).getState());
        Assert.assertEquals("ttp1", rsp.getCompacts().get(1).getTablename());
        Assert.assertEquals(SUCCEEDED_RESPONSE, rsp.getCompacts().get(1).getState());
        /**
         * we just did a major compaction on ttp1.  Open any file produced by it and check buffer size.
         * It should be the default.
         */
        List<String> rs = TestCompactor.execSelectAndDumpData(("select distinct INPUT__FILE__NAME from " + tblName1), driver, "Find Orc File bufer default");
        Assert.assertTrue("empty rs?", ((rs != null) && ((rs.size()) > 0)));
        Path p = new Path(rs.get(0));
        Reader orcReader = OrcFile.createReader(p.getFileSystem(conf), p);
        Assert.assertEquals("Expected default compression size", 2700, orcReader.getCompressionSize());
        // make sure 2700 is not the default so that we are testing if tblproperties indeed propagate
        Assert.assertNotEquals("Unexpected default compression size", 2700, BUFFER_SIZE.getDefaultValue());
        // Insert one more row - this should trigger hive.compactor.delta.pct.threshold to be reached for ttp2
        TestCompactor.executeStatementOnDriver((("insert into " + tblName1) + " values (6, 'f')"), driver);
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (6, 'f')"), driver);
        // Intentionally set this high so that it will not trigger major compaction for ttp1.
        // Only trigger major compaction for ttp2 (delta.pct.threshold=0.5) because of the newly inserted row (actual pct: 0.66)
        conf.setFloatVar(HIVE_COMPACTOR_DELTA_PCT_THRESHOLD, 0.8F);
        TestTxnCommands2.runInitiator(conf);
        rsp = txnHandler.showCompact(new ShowCompactRequest());
        Assert.assertEquals(3, rsp.getCompacts().size());
        Assert.assertEquals("ttp2", rsp.getCompacts().get(0).getTablename());
        Assert.assertEquals(INITIATED_RESPONSE, rsp.getCompacts().get(0).getState());
        // Finish the scheduled compaction for ttp2
        TestTxnCommands2.runWorker(conf);
        TestTxnCommands2.runCleaner(conf);
        rsp = txnHandler.showCompact(new ShowCompactRequest());
        Assert.assertEquals(3, rsp.getCompacts().size());
        Assert.assertEquals("ttp2", rsp.getCompacts().get(0).getTablename());
        Assert.assertEquals(SUCCEEDED_RESPONSE, rsp.getCompacts().get(0).getState());
        // Now test tblproperties specified on ALTER TABLE .. COMPACT .. statement
        TestCompactor.executeStatementOnDriver((("insert into " + tblName2) + " values (7, 'g')"), driver);
        TestCompactor.executeStatementOnDriver((((((("alter table " + tblName2) + " compact 'major'") + " with overwrite tblproperties (") + "'compactor.mapreduce.map.memory.mb'='3072',") + "'tblprops.orc.compress.size'='3141',") + "'compactor.hive.compactor.job.queue'='root.user2')"), driver);
        rsp = txnHandler.showCompact(new ShowCompactRequest());
        Assert.assertEquals(4, rsp.getCompacts().size());
        Assert.assertEquals("ttp2", rsp.getCompacts().get(0).getTablename());
        Assert.assertEquals(INITIATED_RESPONSE, rsp.getCompacts().get(0).getState());
        // make sure we are checking the right (latest) compaction entry
        Assert.assertEquals(4, rsp.getCompacts().get(0).getId());
        // Run the Worker explicitly, in order to get the reference to the compactor MR job
        TestTxnCommands2.runWorker(conf);
        /* createReader(FileSystem fs, Path path) throws IOException { */
        // we just ran Major compaction so we should have a base_x in tblName2 that has the new files
        // Get the name of a file and look at its properties to see if orc.compress.size was respected.
        rs = TestCompactor.execSelectAndDumpData(("select distinct INPUT__FILE__NAME from " + tblName2), driver, "Find Compacted Orc File");
        Assert.assertTrue("empty rs?", ((rs != null) && ((rs.size()) > 0)));
        p = new Path(rs.get(0));
        orcReader = OrcFile.createReader(p.getFileSystem(conf), p);
        Assert.assertEquals("File written with wrong buffer size", 3141, orcReader.getCompressionSize());
    }

    @Test
    public void testCompactionInfoEquals() {
        CompactionInfo compactionInfo = new CompactionInfo("dbName", "tableName", "partName", CompactionType.MINOR);
        CompactionInfo compactionInfo1 = new CompactionInfo("dbName", "tableName", "partName", CompactionType.MINOR);
        Assert.assertTrue("The object must be equal", compactionInfo.equals(compactionInfo));
        Assert.assertFalse("The object must be not equal", compactionInfo.equals(new Object()));
        Assert.assertTrue("The object must be equal", compactionInfo.equals(compactionInfo1));
    }

    @Test
    public void testCompactionInfoHashCode() {
        CompactionInfo compactionInfo = new CompactionInfo("dbName", "tableName", "partName", CompactionType.MINOR);
        CompactionInfo compactionInfo1 = new CompactionInfo("dbName", "tableName", "partName", CompactionType.MINOR);
        Assert.assertEquals("The hash codes must be equal", compactionInfo.hashCode(), compactionInfo1.hashCode());
    }

    @Test
    public void testDisableCompactionDuringReplLoad() throws Exception {
        String tblName = "discomp";
        String database = "discomp_db";
        TestCompactor.executeStatementOnDriver((("drop database if exists " + database) + " cascade"), driver);
        TestCompactor.executeStatementOnDriver(("create database " + database), driver);
        TestCompactor.executeStatementOnDriver(((((((("CREATE TABLE " + database) + ".") + tblName) + "(a INT, b STRING) ") + " PARTITIONED BY(ds string)") + " CLUSTERED BY(a) INTO 2 BUCKETS")// currently ACID requires table to be bucketed
         + " STORED AS ORC TBLPROPERTIES ('transactional'='true')"), driver);
        TestCompactor.executeStatementOnDriver(((((("insert into " + database) + ".") + tblName) + " partition (ds) values (1, 'fred', ") + "'today'), (2, 'wilma', 'yesterday')"), driver);
        TestCompactor.executeStatementOnDriver((((("ALTER TABLE " + database) + ".") + tblName) + " SET TBLPROPERTIES ( 'hive.repl.first.inc.pending' = 'true')"), driver);
        List<ShowCompactResponseElement> compacts = getCompactionList();
        Assert.assertEquals(0, compacts.size());
        TestCompactor.executeStatementOnDriver((("alter database " + database) + " set dbproperties ('hive.repl.first.inc.pending' = 'true')"), driver);
        TestCompactor.executeStatementOnDriver((((("ALTER TABLE " + database) + ".") + tblName) + " SET TBLPROPERTIES ( 'hive.repl.first.inc.pending' = 'false')"), driver);
        compacts = getCompactionList();
        Assert.assertEquals(0, compacts.size());
        TestCompactor.executeStatementOnDriver((("alter database " + database) + " set dbproperties ('hive.repl.first.inc.pending' = 'false')"), driver);
        TestCompactor.executeStatementOnDriver((((("ALTER TABLE " + database) + ".") + tblName) + " SET TBLPROPERTIES ( 'hive.repl.first.inc.pending' = 'false')"), driver);
        compacts = getCompactionList();
        Assert.assertEquals(2, compacts.size());
        List<String> partNames = new ArrayList<String>();
        for (int i = 0; i < (compacts.size()); i++) {
            Assert.assertEquals(database, compacts.get(i).getDbname());
            Assert.assertEquals(tblName, compacts.get(i).getTablename());
            Assert.assertEquals("initiated", compacts.get(i).getState());
            partNames.add(compacts.get(i).getPartitionname());
        }
        Assert.assertEquals("ds=today", partNames.get(1));
        Assert.assertEquals("ds=yesterday", partNames.get(0));
        TestCompactor.executeStatementOnDriver((("drop database if exists " + database) + " cascade"), driver);
        // Finish the scheduled compaction for ttp2
        TestTxnCommands2.runWorker(conf);
        TestTxnCommands2.runCleaner(conf);
    }
}

