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
package org.apache.hadoop.hbase.backup;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestBackupHFileCleaner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupHFileCleaner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupHFileCleaner.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf = TestBackupHFileCleaner.TEST_UTIL.getConfiguration();

    private static TableName tableName = TableName.valueOf("backup.hfile.cleaner");

    private static String famName = "fam";

    static FileSystem fs = null;

    Path root;

    @Test
    public void testGetDeletableFiles() throws IOException {
        // 1. Create a file
        Path file = new Path(root, "testIsFileDeletableWithNoHFileRefs");
        TestBackupHFileCleaner.fs.createNewFile(file);
        // 2. Assert file is successfully created
        Assert.assertTrue("Test file not created!", TestBackupHFileCleaner.fs.exists(file));
        BackupHFileCleaner cleaner = new BackupHFileCleaner();
        cleaner.setConf(TestBackupHFileCleaner.conf);
        cleaner.setCheckForFullyBackedUpTables(false);
        // 3. Assert that file as is should be deletable
        List<FileStatus> stats = new ArrayList<>();
        FileStatus stat = TestBackupHFileCleaner.fs.getFileStatus(file);
        stats.add(stat);
        Iterable<FileStatus> deletable = cleaner.getDeletableFiles(stats);
        deletable = cleaner.getDeletableFiles(stats);
        boolean found = false;
        for (FileStatus stat1 : deletable) {
            if (stat.equals(stat1)) {
                found = true;
            }
        }
        Assert.assertTrue(("Cleaner should allow to delete this file as there is no hfile reference " + "for it."), found);
        // 4. Add the file as bulk load
        List<Path> list = new ArrayList<>(1);
        list.add(file);
        try (Connection conn = ConnectionFactory.createConnection(TestBackupHFileCleaner.conf);BackupSystemTable sysTbl = new BackupSystemTable(conn)) {
            List<TableName> sTableList = new ArrayList<>();
            sTableList.add(TestBackupHFileCleaner.tableName);
            Map<byte[], List<Path>>[] maps = new Map[1];
            maps[0] = new HashMap();
            maps[0].put(Bytes.toBytes(TestBackupHFileCleaner.famName), list);
            sysTbl.writeBulkLoadedFiles(sTableList, maps, "1");
        }
        // 5. Assert file should not be deletable
        deletable = cleaner.getDeletableFiles(stats);
        deletable = cleaner.getDeletableFiles(stats);
        found = false;
        for (FileStatus stat1 : deletable) {
            if (stat.equals(stat1)) {
                found = true;
            }
        }
        Assert.assertFalse(("Cleaner should not allow to delete this file as there is a hfile reference " + "for it."), found);
    }
}

