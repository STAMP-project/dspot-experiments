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


import AcidUtils.deleteEventDeltaDirFilter;
import AcidUtils.deltaFileFilter;
import CompactionType.MAJOR;
import HiveConf.ConfVars.HADOOPNUMREDUCERS;
import HiveConf.ConfVars.MAXREDUCERS;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponseElement;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.txn.TxnStore;
import org.apache.hadoop.hive.metastore.txn.TxnUtils;
import org.apache.hadoop.hive.ql.DriverFactory;
import org.apache.hadoop.hive.ql.IDriver;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Add tests for bucketing_version=1 when HIVE-21167 is fixed
@SuppressWarnings("deprecation")
public class TestCrudCompactorOnTez {
    private static final AtomicInteger salt = new AtomicInteger(new Random().nextInt());

    private static final Logger LOG = LoggerFactory.getLogger(TestCrudCompactorOnTez.class);

    private static final String TEST_DATA_DIR = new File((((((((System.getProperty("java.io.tmpdir")) + (File.separator)) + (TestCrudCompactorOnTez.class.getCanonicalName())) + "-") + (System.currentTimeMillis())) + "_") + (TestCrudCompactorOnTez.salt.getAndIncrement()))).getPath().replaceAll("\\\\", "/");

    private static final String TEST_WAREHOUSE_DIR = (TestCrudCompactorOnTez.TEST_DATA_DIR) + "/warehouse";

    private HiveConf conf;

    IMetaStoreClient msClient;

    private IDriver driver;

    @Test
    public void testMajorCompaction() throws Exception {
        String dbName = "default";
        String tblName = "testMajorCompaction";
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((((("create transactional table " + tblName) + " (a int, b int) clustered by (a) into 2 buckets") + " stored as ORC TBLPROPERTIES('bucketing_version'='2', 'transactional'='true',") + " 'transactional_properties'='default')"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("insert into " + tblName) + " values(1,2),(1,3),(1,4),(2,2),(2,3),(2,4)"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("insert into " + tblName) + " values(3,2),(3,3),(3,4),(4,2),(4,3),(4,4)"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("delete from " + tblName) + " where b = 2"), driver);
        // Find the location of the table
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        FileSystem fs = FileSystem.get(conf);
        // Verify deltas (delta_0000001_0000001_0000, delta_0000002_0000002_0000) are present
        FileStatus[] filestatus = fs.listStatus(new org.apache.hadoop.fs.Path(table.getSd().getLocation()), deltaFileFilter);
        String[] deltas = new String[filestatus.length];
        for (int i = 0; i < (deltas.length); i++) {
            deltas[i] = filestatus[i].getPath().getName();
        }
        Arrays.sort(deltas);
        String[] expectedDeltas = new String[]{ "delta_0000001_0000001_0000", "delta_0000002_0000002_0000" };
        if (!(Arrays.deepEquals(expectedDeltas, deltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeltas))) + ", found: ") + (Arrays.toString(deltas))));
        }
        // Verify that delete delta (delete_delta_0000003_0000003_0000) is present
        FileStatus[] deleteDeltaStat = fs.listStatus(new org.apache.hadoop.fs.Path(table.getSd().getLocation()), deleteEventDeltaDirFilter);
        String[] deleteDeltas = new String[deleteDeltaStat.length];
        for (int i = 0; i < (deleteDeltas.length); i++) {
            deleteDeltas[i] = deleteDeltaStat[i].getPath().getName();
        }
        Arrays.sort(deleteDeltas);
        String[] expectedDeleteDeltas = new String[]{ "delete_delta_0000003_0000003_0000" };
        if (!(Arrays.deepEquals(expectedDeleteDeltas, deleteDeltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeleteDeltas))) + ", found: ") + (Arrays.toString(deleteDeltas))));
        }
        List<String> expectedRsBucket0 = new ArrayList<>();
        expectedRsBucket0.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":0}\t2\t4");
        expectedRsBucket0.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":1}\t2\t3");
        expectedRsBucket0.add("{\"writeid\":2,\"bucketid\":536870912,\"rowid\":0}\t3\t4");
        expectedRsBucket0.add("{\"writeid\":2,\"bucketid\":536870912,\"rowid\":1}\t3\t3");
        List<String> expectedRsBucket1 = new ArrayList<>();
        expectedRsBucket1.add("{\"writeid\":1,\"bucketid\":536936448,\"rowid\":0}\t1\t4");
        expectedRsBucket1.add("{\"writeid\":1,\"bucketid\":536936448,\"rowid\":1}\t1\t3");
        expectedRsBucket1.add("{\"writeid\":2,\"bucketid\":536936448,\"rowid\":0}\t4\t4");
        expectedRsBucket1.add("{\"writeid\":2,\"bucketid\":536936448,\"rowid\":1}\t4\t3");
        // Bucket 0
        List<String> rsBucket0 = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from " + tblName) + " where ROW__ID.bucketid = 536870912 order by ROW__ID"), driver);
        Assert.assertEquals("normal read", expectedRsBucket0, rsBucket0);
        // Bucket 1
        List<String> rsBucket1 = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from " + tblName) + " where ROW__ID.bucketid = 536936448 order by ROW__ID"), driver);
        Assert.assertEquals("normal read", expectedRsBucket1, rsBucket1);
        // Run major compaction and cleaner
        runCompaction(dbName, tblName, MAJOR);
        TestCrudCompactorOnTez.runCleaner(conf);
        // Should contain only one base directory now
        filestatus = fs.listStatus(new org.apache.hadoop.fs.Path(table.getSd().getLocation()));
        String[] bases = new String[filestatus.length];
        for (int i = 0; i < (bases.length); i++) {
            bases[i] = filestatus[i].getPath().getName();
        }
        Arrays.sort(bases);
        String[] expectedBases = new String[]{ "base_0000003_v0000008" };
        if (!(Arrays.deepEquals(expectedBases, bases))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedBases))) + ", found: ") + (Arrays.toString(bases))));
        }
        // Bucket 0
        List<String> rsCompactBucket0 = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from  " + tblName) + " where ROW__ID.bucketid = 536870912"), driver);
        Assert.assertEquals("compacted read", rsBucket0, rsCompactBucket0);
        // Bucket 1
        List<String> rsCompactBucket1 = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from  " + tblName) + " where ROW__ID.bucketid = 536936448"), driver);
        Assert.assertEquals("compacted read", rsBucket1, rsCompactBucket1);
        // Clean up
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table " + tblName), driver);
    }

    @Test
    public void testMinorCompactionDisabled() throws Exception {
        String dbName = "default";
        String tblName = "testMinorCompactionDisabled";
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((((("create transactional table " + tblName) + " (a int, b int) clustered by (a) into 2 buckets") + " stored as ORC TBLPROPERTIES('bucketing_version'='2', 'transactional'='true',") + " 'transactional_properties'='default')"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("insert into " + tblName) + " values(1,2),(1,3),(1,4),(2,2),(2,3),(2,4)"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("insert into " + tblName) + " values(3,2),(3,3),(3,4),(4,2),(4,3),(4,4)"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("delete from " + tblName) + " where b = 2"), driver);
        // Find the location of the table
        IMetaStoreClient msClient = new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(conf);
        Table table = msClient.getTable(dbName, tblName);
        FileSystem fs = FileSystem.get(conf);
        // Verify deltas (delta_0000001_0000001_0000, delta_0000002_0000002_0000) are present
        FileStatus[] filestatus = fs.listStatus(new org.apache.hadoop.fs.Path(table.getSd().getLocation()), deltaFileFilter);
        String[] deltas = new String[filestatus.length];
        for (int i = 0; i < (deltas.length); i++) {
            deltas[i] = filestatus[i].getPath().getName();
        }
        Arrays.sort(deltas);
        String[] expectedDeltas = new String[]{ "delta_0000001_0000001_0000", "delta_0000002_0000002_0000" };
        if (!(Arrays.deepEquals(expectedDeltas, deltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeltas))) + ", found: ") + (Arrays.toString(deltas))));
        }
        // Verify that delete delta (delete_delta_0000003_0000003_0000) is present
        FileStatus[] deleteDeltaStat = fs.listStatus(new org.apache.hadoop.fs.Path(table.getSd().getLocation()), deleteEventDeltaDirFilter);
        String[] deleteDeltas = new String[deleteDeltaStat.length];
        for (int i = 0; i < (deleteDeltas.length); i++) {
            deleteDeltas[i] = deleteDeltaStat[i].getPath().getName();
        }
        Arrays.sort(deleteDeltas);
        String[] expectedDeleteDeltas = new String[]{ "delete_delta_0000003_0000003_0000" };
        if (!(Arrays.deepEquals(expectedDeleteDeltas, deleteDeltas))) {
            Assert.fail(((("Expected: " + (Arrays.toString(expectedDeleteDeltas))) + ", found: ") + (Arrays.toString(deleteDeltas))));
        }
        // Initiate a compaction, make sure it's not queued
        TestCrudCompactorOnTez.runInitiator(conf);
        TxnStore txnHandler = TxnUtils.getTxnStore(conf);
        ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
        List<ShowCompactResponseElement> compacts = rsp.getCompacts();
        Assert.assertEquals(0, compacts.size());
        // Clean up
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table " + tblName), driver);
    }

    @Test
    public void testCompactionWithSchemaEvolutionAndBuckets() throws Exception {
        String dbName = "default";
        String tblName = "testCompactionWithSchemaEvolutionAndBuckets";
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((((("create transactional table " + tblName) + " (a int, b int) partitioned by(ds string) clustered by (a) into 2 buckets") + " stored as ORC TBLPROPERTIES('bucketing_version'='2', 'transactional'='true',") + " 'transactional_properties'='default')"), driver);
        // Insert some data
        TestCrudCompactorOnTez.executeStatementOnDriver((("insert into " + tblName) + " partition (ds) values(1,2,'today'),(1,3,'today'),(1,4,'yesterday'),(2,2,'yesterday'),(2,3,'today'),(2,4,'today')"), driver);
        // Add a new column
        TestCrudCompactorOnTez.executeStatementOnDriver((("alter table " + tblName) + " add columns(c int)"), driver);
        // Insert more data
        TestCrudCompactorOnTez.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values(3,2,1000,'yesterday'),(3,3,1001,'today'),(3,4,1002,'yesterday'),(4,2,1003,'today'),") + "(4,3,1004,'yesterday'),(4,4,1005,'today')"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("delete from " + tblName) + " where b = 2"), driver);
        // Run major compaction and cleaner
        runCompaction(dbName, tblName, MAJOR, "ds=yesterday", "ds=today");
        TestCrudCompactorOnTez.runCleaner(conf);
        List<String> expectedRsBucket0PtnToday = new ArrayList<>();
        expectedRsBucket0PtnToday.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":0}\t2\t3\tNULL\ttoday");
        expectedRsBucket0PtnToday.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":1}\t2\t4\tNULL\ttoday");
        expectedRsBucket0PtnToday.add("{\"writeid\":3,\"bucketid\":536870912,\"rowid\":0}\t3\t3\t1001\ttoday");
        List<String> expectedRsBucket1PtnToday = new ArrayList<>();
        expectedRsBucket1PtnToday.add("{\"writeid\":1,\"bucketid\":536936448,\"rowid\":1}\t1\t3\tNULL\ttoday");
        expectedRsBucket1PtnToday.add("{\"writeid\":3,\"bucketid\":536936448,\"rowid\":1}\t4\t4\t1005\ttoday");
        // Bucket 0, partition 'today'
        List<String> rsCompactBucket0PtnToday = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from  " + tblName) + " where ROW__ID.bucketid = 536870912 and ds='today'"), driver);
        Assert.assertEquals("compacted read", expectedRsBucket0PtnToday, rsCompactBucket0PtnToday);
        // Bucket 1, partition 'today'
        List<String> rsCompactBucket1PtnToday = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from  " + tblName) + " where ROW__ID.bucketid = 536936448 and ds='today'"), driver);
        Assert.assertEquals("compacted read", expectedRsBucket1PtnToday, rsCompactBucket1PtnToday);
        // Clean up
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table " + tblName), driver);
    }

    @Test
    public void testCompactionWithSchemaEvolutionNoBucketsMultipleReducers() throws Exception {
        HiveConf hiveConf = new HiveConf(conf);
        hiveConf.setIntVar(MAXREDUCERS, 2);
        hiveConf.setIntVar(HADOOPNUMREDUCERS, 2);
        driver = DriverFactory.newDriver(hiveConf);
        String dbName = "default";
        String tblName = "testCompactionWithSchemaEvolutionNoBucketsMultipleReducers";
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table if exists " + tblName), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((((("create transactional table " + tblName) + " (a int, b int) partitioned by(ds string)") + " stored as ORC TBLPROPERTIES('transactional'='true',") + " 'transactional_properties'='default')"), driver);
        // Insert some data
        TestCrudCompactorOnTez.executeStatementOnDriver((("insert into " + tblName) + " partition (ds) values(1,2,'today'),(1,3,'today'),(1,4,'yesterday'),(2,2,'yesterday'),(2,3,'today'),(2,4,'today')"), driver);
        // Add a new column
        TestCrudCompactorOnTez.executeStatementOnDriver((("alter table " + tblName) + " add columns(c int)"), driver);
        // Insert more data
        TestCrudCompactorOnTez.executeStatementOnDriver(((("insert into " + tblName) + " partition (ds) values(3,2,1000,'yesterday'),(3,3,1001,'today'),(3,4,1002,'yesterday'),(4,2,1003,'today'),") + "(4,3,1004,'yesterday'),(4,4,1005,'today')"), driver);
        TestCrudCompactorOnTez.executeStatementOnDriver((("delete from " + tblName) + " where b = 2"), driver);
        // Run major compaction and cleaner
        runCompaction(dbName, tblName, MAJOR, "ds=yesterday", "ds=today");
        TestCrudCompactorOnTez.runCleaner(hiveConf);
        List<String> expectedRsPtnToday = new ArrayList<>();
        expectedRsPtnToday.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":1}\t1\t3\tNULL\ttoday");
        expectedRsPtnToday.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":2}\t2\t3\tNULL\ttoday");
        expectedRsPtnToday.add("{\"writeid\":1,\"bucketid\":536870912,\"rowid\":3}\t2\t4\tNULL\ttoday");
        expectedRsPtnToday.add("{\"writeid\":3,\"bucketid\":536870912,\"rowid\":0}\t3\t3\t1001\ttoday");
        expectedRsPtnToday.add("{\"writeid\":3,\"bucketid\":536870912,\"rowid\":2}\t4\t4\t1005\ttoday");
        List<String> expectedRsPtnYesterday = new ArrayList<>();
        expectedRsPtnYesterday.add("{\"writeid\":1,\"bucketid\":536936448,\"rowid\":0}\t1\t4\tNULL\tyesterday");
        expectedRsPtnYesterday.add("{\"writeid\":3,\"bucketid\":536936448,\"rowid\":1}\t3\t4\t1002\tyesterday");
        expectedRsPtnYesterday.add("{\"writeid\":3,\"bucketid\":536936448,\"rowid\":2}\t4\t3\t1004\tyesterday");
        // Partition 'today'
        List<String> rsCompactPtnToday = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from  " + tblName) + " where ds='today'"), driver);
        Assert.assertEquals("compacted read", expectedRsPtnToday, rsCompactPtnToday);
        // Partition 'yesterday'
        List<String> rsCompactPtnYesterday = TestCrudCompactorOnTez.executeStatementOnDriverAndReturnResults((("select ROW__ID, * from  " + tblName) + " where ds='yesterday'"), driver);
        Assert.assertEquals("compacted read", expectedRsPtnYesterday, rsCompactPtnYesterday);
        // Clean up
        TestCrudCompactorOnTez.executeStatementOnDriver(("drop table " + tblName), driver);
    }
}

