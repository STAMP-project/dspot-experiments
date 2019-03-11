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
package org.apache.hadoop.hive.metastore;


import RecycleType.MOVE;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.columnar.LazyBinaryColumnarSerDe;
import org.junit.Assert;
import org.junit.Test;


public class TestReplChangeManager {
    private static HiveMetaStoreClient client;

    private static HiveConf hiveConf;

    private static Warehouse warehouse;

    private static MiniDFSCluster m_dfs;

    private static String cmroot;

    private static FileSystem fs;

    @Test
    public void testRecyclePartTable() throws Exception {
        // Create db1/t1/dt=20160101/part
        // /dt=20160102/part
        // /dt=20160103/part
        // Test: recycle single file (dt=20160101/part)
        // recycle single partition (dt=20160102)
        // recycle table t1
        String dbName = "db1";
        TestReplChangeManager.client.dropDatabase(dbName, true, true);
        Database db = new Database();
        db.putToParameters(ReplChangeManager.SOURCE_OF_REPLICATION, "1,2,3");
        db.setName(dbName);
        TestReplChangeManager.client.createDatabase(db);
        String tblName = "t1";
        List<FieldSchema> columns = new ArrayList<FieldSchema>();
        columns.add(new FieldSchema("foo", "string", ""));
        columns.add(new FieldSchema("bar", "string", ""));
        List<FieldSchema> partColumns = new ArrayList<FieldSchema>();
        partColumns.add(new FieldSchema("dt", "string", ""));
        SerDeInfo serdeInfo = new SerDeInfo("LBCSerDe", LazyBinaryColumnarSerDe.class.getCanonicalName(), new HashMap<String, String>());
        StorageDescriptor sd = new StorageDescriptor(columns, null, "org.apache.hadoop.hive.ql.io.orc.OrcInputFormat", "org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat", false, 0, serdeInfo, null, null, null);
        Map<String, String> tableParameters = new HashMap<String, String>();
        Table tbl = new Table(tblName, dbName, "", 0, 0, 0, sd, partColumns, tableParameters, "", "", "");
        TestReplChangeManager.client.createTable(tbl);
        List<String> values = Arrays.asList("20160101");
        Partition part1 = createPartition(dbName, tblName, columns, values, serdeInfo);
        TestReplChangeManager.client.add_partition(part1);
        values = Arrays.asList("20160102");
        Partition part2 = createPartition(dbName, tblName, columns, values, serdeInfo);
        TestReplChangeManager.client.add_partition(part2);
        values = Arrays.asList("20160103");
        Partition part3 = createPartition(dbName, tblName, columns, values, serdeInfo);
        TestReplChangeManager.client.add_partition(part3);
        Path part1Path = new Path(TestReplChangeManager.warehouse.getDefaultPartitionPath(db, tbl, ImmutableMap.of("dt", "20160101")), "part");
        createFile(part1Path, "p1");
        String path1Chksum = ReplChangeManager.checksumFor(part1Path, TestReplChangeManager.fs);
        Path part2Path = new Path(TestReplChangeManager.warehouse.getDefaultPartitionPath(db, tbl, ImmutableMap.of("dt", "20160102")), "part");
        createFile(part2Path, "p2");
        String path2Chksum = ReplChangeManager.checksumFor(part2Path, TestReplChangeManager.fs);
        Path part3Path = new Path(TestReplChangeManager.warehouse.getDefaultPartitionPath(db, tbl, ImmutableMap.of("dt", "20160103")), "part");
        createFile(part3Path, "p3");
        String path3Chksum = ReplChangeManager.checksumFor(part3Path, TestReplChangeManager.fs);
        Assert.assertTrue(part1Path.getFileSystem(TestReplChangeManager.hiveConf).exists(part1Path));
        Assert.assertTrue(part2Path.getFileSystem(TestReplChangeManager.hiveConf).exists(part2Path));
        Assert.assertTrue(part3Path.getFileSystem(TestReplChangeManager.hiveConf).exists(part3Path));
        ReplChangeManager cm = ReplChangeManager.getInstance(TestReplChangeManager.hiveConf);
        // verify cm.recycle(db, table, part) api moves file to cmroot dir
        int ret = cm.recycle(part1Path, MOVE, false);
        Assert.assertEquals(ret, 1);
        Path cmPart1Path = ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part1Path.getName(), path1Chksum, TestReplChangeManager.cmroot.toString());
        Assert.assertTrue(cmPart1Path.getFileSystem(TestReplChangeManager.hiveConf).exists(cmPart1Path));
        // Verify dropPartition recycle part files
        TestReplChangeManager.client.dropPartition(dbName, tblName, Arrays.asList("20160102"));
        Assert.assertFalse(part2Path.getFileSystem(TestReplChangeManager.hiveConf).exists(part2Path));
        Path cmPart2Path = ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part2Path.getName(), path2Chksum, TestReplChangeManager.cmroot.toString());
        Assert.assertTrue(cmPart2Path.getFileSystem(TestReplChangeManager.hiveConf).exists(cmPart2Path));
        // Verify dropTable recycle partition files
        TestReplChangeManager.client.dropTable(dbName, tblName);
        Assert.assertFalse(part3Path.getFileSystem(TestReplChangeManager.hiveConf).exists(part3Path));
        Path cmPart3Path = ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part3Path.getName(), path3Chksum, TestReplChangeManager.cmroot.toString());
        Assert.assertTrue(cmPart3Path.getFileSystem(TestReplChangeManager.hiveConf).exists(cmPart3Path));
        TestReplChangeManager.client.dropDatabase(dbName, true, true);
    }

    @Test
    public void testRecycleNonPartTable() throws Exception {
        // Create db2/t1/part1
        // /part2
        // /part3
        // Test: recycle single file (part1)
        // recycle table t1
        String dbName = "db2";
        TestReplChangeManager.client.dropDatabase(dbName, true, true);
        Database db = new Database();
        db.putToParameters(ReplChangeManager.SOURCE_OF_REPLICATION, "1, 2, 3");
        db.setName(dbName);
        TestReplChangeManager.client.createDatabase(db);
        String tblName = "t1";
        List<FieldSchema> columns = new ArrayList<FieldSchema>();
        columns.add(new FieldSchema("foo", "string", ""));
        columns.add(new FieldSchema("bar", "string", ""));
        SerDeInfo serdeInfo = new SerDeInfo("LBCSerDe", LazyBinaryColumnarSerDe.class.getCanonicalName(), new HashMap<String, String>());
        StorageDescriptor sd = new StorageDescriptor(columns, null, "org.apache.hadoop.hive.ql.io.orc.OrcInputFormat", "org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat", false, 0, serdeInfo, null, null, null);
        Map<String, String> tableParameters = new HashMap<String, String>();
        Table tbl = new Table(tblName, dbName, "", 0, 0, 0, sd, null, tableParameters, "", "", "");
        TestReplChangeManager.client.createTable(tbl);
        Path filePath1 = new Path(TestReplChangeManager.warehouse.getDefaultTablePath(db, tblName), "part1");
        createFile(filePath1, "f1");
        String fileChksum1 = ReplChangeManager.checksumFor(filePath1, TestReplChangeManager.fs);
        Path filePath2 = new Path(TestReplChangeManager.warehouse.getDefaultTablePath(db, tblName), "part2");
        createFile(filePath2, "f2");
        String fileChksum2 = ReplChangeManager.checksumFor(filePath2, TestReplChangeManager.fs);
        Path filePath3 = new Path(TestReplChangeManager.warehouse.getDefaultTablePath(db, tblName), "part3");
        createFile(filePath3, "f3");
        String fileChksum3 = ReplChangeManager.checksumFor(filePath3, TestReplChangeManager.fs);
        Assert.assertTrue(filePath1.getFileSystem(TestReplChangeManager.hiveConf).exists(filePath1));
        Assert.assertTrue(filePath2.getFileSystem(TestReplChangeManager.hiveConf).exists(filePath2));
        Assert.assertTrue(filePath3.getFileSystem(TestReplChangeManager.hiveConf).exists(filePath3));
        ReplChangeManager cm = ReplChangeManager.getInstance(TestReplChangeManager.hiveConf);
        // verify cm.recycle(Path) api moves file to cmroot dir
        cm.recycle(filePath1, MOVE, false);
        Assert.assertFalse(filePath1.getFileSystem(TestReplChangeManager.hiveConf).exists(filePath1));
        Path cmPath1 = ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, filePath1.getName(), fileChksum1, TestReplChangeManager.cmroot.toString());
        Assert.assertTrue(cmPath1.getFileSystem(TestReplChangeManager.hiveConf).exists(cmPath1));
        // Verify dropTable recycle table files
        TestReplChangeManager.client.dropTable(dbName, tblName);
        Path cmPath2 = ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, filePath2.getName(), fileChksum2, TestReplChangeManager.cmroot.toString());
        Assert.assertFalse(filePath2.getFileSystem(TestReplChangeManager.hiveConf).exists(filePath2));
        Assert.assertTrue(cmPath2.getFileSystem(TestReplChangeManager.hiveConf).exists(cmPath2));
        Path cmPath3 = ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, filePath3.getName(), fileChksum3, TestReplChangeManager.cmroot.toString());
        Assert.assertFalse(filePath3.getFileSystem(TestReplChangeManager.hiveConf).exists(filePath3));
        Assert.assertTrue(cmPath3.getFileSystem(TestReplChangeManager.hiveConf).exists(cmPath3));
        TestReplChangeManager.client.dropDatabase(dbName, true, true);
    }

    @Test
    public void testClearer() throws Exception {
        FileSystem fs = TestReplChangeManager.warehouse.getWhRoot().getFileSystem(TestReplChangeManager.hiveConf);
        long now = System.currentTimeMillis();
        Path dirDb = new Path(TestReplChangeManager.warehouse.getWhRoot(), "db3");
        fs.mkdirs(dirDb);
        Path dirTbl1 = new Path(dirDb, "tbl1");
        fs.mkdirs(dirTbl1);
        Path part11 = new Path(dirTbl1, "part1");
        createFile(part11, "testClearer11");
        String fileChksum11 = ReplChangeManager.checksumFor(part11, fs);
        Path part12 = new Path(dirTbl1, "part2");
        createFile(part12, "testClearer12");
        String fileChksum12 = ReplChangeManager.checksumFor(part12, fs);
        Path dirTbl2 = new Path(dirDb, "tbl2");
        fs.mkdirs(dirTbl2);
        Path part21 = new Path(dirTbl2, "part1");
        createFile(part21, "testClearer21");
        String fileChksum21 = ReplChangeManager.checksumFor(part21, fs);
        Path part22 = new Path(dirTbl2, "part2");
        createFile(part22, "testClearer22");
        String fileChksum22 = ReplChangeManager.checksumFor(part22, fs);
        Path dirTbl3 = new Path(dirDb, "tbl3");
        fs.mkdirs(dirTbl3);
        Path part31 = new Path(dirTbl3, "part1");
        createFile(part31, "testClearer31");
        String fileChksum31 = ReplChangeManager.checksumFor(part31, fs);
        Path part32 = new Path(dirTbl3, "part2");
        createFile(part32, "testClearer32");
        String fileChksum32 = ReplChangeManager.checksumFor(part32, fs);
        ReplChangeManager.getInstance(TestReplChangeManager.hiveConf).recycle(dirTbl1, MOVE, false);
        ReplChangeManager.getInstance(TestReplChangeManager.hiveConf).recycle(dirTbl2, MOVE, false);
        ReplChangeManager.getInstance(TestReplChangeManager.hiveConf).recycle(dirTbl3, MOVE, true);
        Assert.assertTrue(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part11.getName(), fileChksum11, TestReplChangeManager.cmroot.toString())));
        Assert.assertTrue(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part12.getName(), fileChksum12, TestReplChangeManager.cmroot.toString())));
        Assert.assertTrue(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part21.getName(), fileChksum21, TestReplChangeManager.cmroot.toString())));
        Assert.assertTrue(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part22.getName(), fileChksum22, TestReplChangeManager.cmroot.toString())));
        Assert.assertTrue(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part31.getName(), fileChksum31, TestReplChangeManager.cmroot.toString())));
        Assert.assertTrue(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part32.getName(), fileChksum32, TestReplChangeManager.cmroot.toString())));
        fs.setTimes(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part11.getName(), fileChksum11, TestReplChangeManager.cmroot.toString()), (now - ((86400 * 1000) * 2)), (now - ((86400 * 1000) * 2)));
        fs.setTimes(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part21.getName(), fileChksum21, TestReplChangeManager.cmroot.toString()), (now - ((86400 * 1000) * 2)), (now - ((86400 * 1000) * 2)));
        fs.setTimes(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part31.getName(), fileChksum31, TestReplChangeManager.cmroot.toString()), (now - ((86400 * 1000) * 2)), (now - ((86400 * 1000) * 2)));
        fs.setTimes(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part32.getName(), fileChksum32, TestReplChangeManager.cmroot.toString()), (now - ((86400 * 1000) * 2)), (now - ((86400 * 1000) * 2)));
        ReplChangeManager.scheduleCMClearer(TestReplChangeManager.hiveConf);
        long start = System.currentTimeMillis();
        long end;
        boolean cleared = false;
        do {
            Thread.sleep(200);
            end = System.currentTimeMillis();
            if ((end - start) > 5000) {
                Assert.fail("timeout, cmroot has not been cleared");
            }
            if ((((((!(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part11.getName(), fileChksum11, TestReplChangeManager.cmroot.toString())))) && (fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part12.getName(), fileChksum12, TestReplChangeManager.cmroot.toString())))) && (!(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part21.getName(), fileChksum21, TestReplChangeManager.cmroot.toString()))))) && (fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part22.getName(), fileChksum22, TestReplChangeManager.cmroot.toString())))) && (!(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part31.getName(), fileChksum31, TestReplChangeManager.cmroot.toString()))))) && (!(fs.exists(ReplChangeManager.getCMPath(TestReplChangeManager.hiveConf, part32.getName(), fileChksum32, TestReplChangeManager.cmroot.toString()))))) {
                cleared = true;
            }
        } while (!cleared );
    }

    @Test
    public void shouldIdentifyCMURIs() {
        Assert.assertTrue(ReplChangeManager.isCMFileUri(new Path("hdfs://localhost:90000/somepath/adir/", "ab.jar#e239s2233")));
        Assert.assertFalse(ReplChangeManager.isCMFileUri(new Path("/somepath/adir/", "ab.jar")));
    }
}

