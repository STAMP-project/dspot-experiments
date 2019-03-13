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


import BackupState.COMPLETE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test cases for backup system table API
 */
@Category(MediumTests.class)
public class TestBackupSystemTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupSystemTable.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static Configuration conf = TestBackupSystemTable.UTIL.getConfiguration();

    protected static MiniHBaseCluster cluster;

    protected static Connection conn;

    protected BackupSystemTable table;

    @Test
    public void testUpdateReadDeleteBackupStatus() throws IOException {
        BackupInfo ctx = createBackupInfo();
        table.updateBackupInfo(ctx);
        BackupInfo readCtx = table.readBackupInfo(ctx.getBackupId());
        Assert.assertTrue(compare(ctx, readCtx));
        // try fake backup id
        readCtx = table.readBackupInfo("fake");
        Assert.assertNull(readCtx);
        // delete backup info
        table.deleteBackupInfo(ctx.getBackupId());
        readCtx = table.readBackupInfo(ctx.getBackupId());
        Assert.assertNull(readCtx);
        cleanBackupTable();
    }

    @Test
    public void testWriteReadBackupStartCode() throws IOException {
        Long code = 100L;
        table.writeBackupStartCode(code, "root");
        String readCode = table.readBackupStartCode("root");
        Assert.assertEquals(code, new Long(Long.parseLong(readCode)));
        cleanBackupTable();
    }

    @Test
    public void testBackupHistory() throws IOException {
        int n = 10;
        List<BackupInfo> list = createBackupInfoList(n);
        // Load data
        for (BackupInfo bc : list) {
            // Make sure we set right status
            bc.setState(COMPLETE);
            table.updateBackupInfo(bc);
        }
        // Reverse list for comparison
        Collections.reverse(list);
        List<BackupInfo> history = table.getBackupHistory();
        Assert.assertTrue(((history.size()) == n));
        for (int i = 0; i < n; i++) {
            BackupInfo ctx = list.get(i);
            BackupInfo data = history.get(i);
            Assert.assertTrue(compare(ctx, data));
        }
        cleanBackupTable();
    }

    @Test
    public void testBackupDelete() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            int n = 10;
            List<BackupInfo> list = createBackupInfoList(n);
            // Load data
            for (BackupInfo bc : list) {
                // Make sure we set right status
                bc.setState(COMPLETE);
                table.updateBackupInfo(bc);
            }
            // Verify exists
            for (BackupInfo bc : list) {
                Assert.assertNotNull(table.readBackupInfo(bc.getBackupId()));
            }
            // Delete all
            for (BackupInfo bc : list) {
                table.deleteBackupInfo(bc.getBackupId());
            }
            // Verify do not exists
            for (BackupInfo bc : list) {
                Assert.assertNull(table.readBackupInfo(bc.getBackupId()));
            }
            cleanBackupTable();
        }
    }

    @Test
    public void testRegionServerLastLogRollResults() throws IOException {
        String[] servers = new String[]{ "server1", "server2", "server3" };
        Long[] timestamps = new Long[]{ 100L, 102L, 107L };
        for (int i = 0; i < (servers.length); i++) {
            table.writeRegionServerLastLogRollResult(servers[i], timestamps[i], "root");
        }
        HashMap<String, Long> result = table.readRegionServerLastLogRollResult("root");
        Assert.assertTrue(((servers.length) == (result.size())));
        Set<String> keys = result.keySet();
        String[] keysAsArray = new String[keys.size()];
        keys.toArray(keysAsArray);
        Arrays.sort(keysAsArray);
        for (int i = 0; i < (keysAsArray.length); i++) {
            Assert.assertEquals(keysAsArray[i], servers[i]);
            Long ts1 = timestamps[i];
            Long ts2 = result.get(keysAsArray[i]);
            Assert.assertEquals(ts1, ts2);
        }
        cleanBackupTable();
    }

    @Test
    public void testIncrementalBackupTableSet() throws IOException {
        TreeSet<TableName> tables1 = new TreeSet<>();
        tables1.add(TableName.valueOf("t1"));
        tables1.add(TableName.valueOf("t2"));
        tables1.add(TableName.valueOf("t3"));
        TreeSet<TableName> tables2 = new TreeSet<>();
        tables2.add(TableName.valueOf("t3"));
        tables2.add(TableName.valueOf("t4"));
        tables2.add(TableName.valueOf("t5"));
        table.addIncrementalBackupTableSet(tables1, "root");
        BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn);
        TreeSet<TableName> res1 = ((TreeSet<TableName>) (table.getIncrementalBackupTableSet("root")));
        Assert.assertTrue(((tables1.size()) == (res1.size())));
        Iterator<TableName> desc1 = tables1.descendingIterator();
        Iterator<TableName> desc2 = res1.descendingIterator();
        while (desc1.hasNext()) {
            Assert.assertEquals(desc1.next(), desc2.next());
        } 
        table.addIncrementalBackupTableSet(tables2, "root");
        TreeSet<TableName> res2 = ((TreeSet<TableName>) (table.getIncrementalBackupTableSet("root")));
        Assert.assertTrue(((((tables2.size()) + (tables1.size())) - 1) == (res2.size())));
        tables1.addAll(tables2);
        desc1 = tables1.descendingIterator();
        desc2 = res2.descendingIterator();
        while (desc1.hasNext()) {
            Assert.assertEquals(desc1.next(), desc2.next());
        } 
        cleanBackupTable();
    }

    @Test
    public void testRegionServerLogTimestampMap() throws IOException {
        TreeSet<TableName> tables = new TreeSet<>();
        tables.add(TableName.valueOf("t1"));
        tables.add(TableName.valueOf("t2"));
        tables.add(TableName.valueOf("t3"));
        HashMap<String, Long> rsTimestampMap = new HashMap<>();
        rsTimestampMap.put("rs1:100", 100L);
        rsTimestampMap.put("rs2:100", 101L);
        rsTimestampMap.put("rs3:100", 103L);
        table.writeRegionServerLogTimestamp(tables, rsTimestampMap, "root");
        HashMap<TableName, HashMap<String, Long>> result = table.readLogTimestampMap("root");
        Assert.assertTrue(((tables.size()) == (result.size())));
        for (TableName t : tables) {
            HashMap<String, Long> rstm = result.get(t);
            Assert.assertNotNull(rstm);
            Assert.assertEquals(rstm.get("rs1:100"), new Long(100L));
            Assert.assertEquals(rstm.get("rs2:100"), new Long(101L));
            Assert.assertEquals(rstm.get("rs3:100"), new Long(103L));
        }
        Set<TableName> tables1 = new TreeSet<>();
        tables1.add(TableName.valueOf("t3"));
        tables1.add(TableName.valueOf("t4"));
        tables1.add(TableName.valueOf("t5"));
        HashMap<String, Long> rsTimestampMap1 = new HashMap<>();
        rsTimestampMap1.put("rs1:100", 200L);
        rsTimestampMap1.put("rs2:100", 201L);
        rsTimestampMap1.put("rs3:100", 203L);
        table.writeRegionServerLogTimestamp(tables1, rsTimestampMap1, "root");
        result = table.readLogTimestampMap("root");
        Assert.assertTrue((5 == (result.size())));
        for (TableName t : tables) {
            HashMap<String, Long> rstm = result.get(t);
            Assert.assertNotNull(rstm);
            if ((t.equals(TableName.valueOf("t3"))) == false) {
                Assert.assertEquals(rstm.get("rs1:100"), new Long(100L));
                Assert.assertEquals(rstm.get("rs2:100"), new Long(101L));
                Assert.assertEquals(rstm.get("rs3:100"), new Long(103L));
            } else {
                Assert.assertEquals(rstm.get("rs1:100"), new Long(200L));
                Assert.assertEquals(rstm.get("rs2:100"), new Long(201L));
                Assert.assertEquals(rstm.get("rs3:100"), new Long(203L));
            }
        }
        for (TableName t : tables1) {
            HashMap<String, Long> rstm = result.get(t);
            Assert.assertNotNull(rstm);
            Assert.assertEquals(rstm.get("rs1:100"), new Long(200L));
            Assert.assertEquals(rstm.get("rs2:100"), new Long(201L));
            Assert.assertEquals(rstm.get("rs3:100"), new Long(203L));
        }
        cleanBackupTable();
    }

    @Test
    public void testAddWALFiles() throws IOException {
        List<String> files = Arrays.asList("hdfs://server/WALs/srv1,101,15555/srv1,101,15555.default.1", "hdfs://server/WALs/srv2,102,16666/srv2,102,16666.default.2", "hdfs://server/WALs/srv3,103,17777/srv3,103,17777.default.3");
        String newFile = "hdfs://server/WALs/srv1,101,15555/srv1,101,15555.default.5";
        table.addWALFiles(files, "backup", "root");
        Assert.assertTrue(table.isWALFileDeletable(files.get(0)));
        Assert.assertTrue(table.isWALFileDeletable(files.get(1)));
        Assert.assertTrue(table.isWALFileDeletable(files.get(2)));
        Assert.assertFalse(table.isWALFileDeletable(newFile));
        // test for isWALFilesDeletable
        List<FileStatus> fileStatues = new ArrayList<>();
        for (String file : files) {
            FileStatus fileStatus = new FileStatus();
            fileStatus.setPath(new Path(file));
            fileStatues.add(fileStatus);
        }
        FileStatus newFileStatus = new FileStatus();
        newFileStatus.setPath(new Path(newFile));
        fileStatues.add(newFileStatus);
        Map<FileStatus, Boolean> walFilesDeletableMap = table.areWALFilesDeletable(fileStatues);
        Assert.assertTrue(walFilesDeletableMap.get(fileStatues.get(0)));
        Assert.assertTrue(walFilesDeletableMap.get(fileStatues.get(1)));
        Assert.assertTrue(walFilesDeletableMap.get(fileStatues.get(2)));
        Assert.assertFalse(walFilesDeletableMap.get(newFileStatus));
        cleanBackupTable();
    }

    /**
     * Backup set tests
     */
    @Test
    public void testBackupSetAddNotExists() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3" };
            String setName = "name";
            table.addToBackupSet(setName, tables);
            List<TableName> tnames = table.describeBackupSet(setName);
            Assert.assertTrue((tnames != null));
            Assert.assertTrue(((tnames.size()) == (tables.length)));
            for (int i = 0; i < (tnames.size()); i++) {
                Assert.assertTrue(tnames.get(i).getNameAsString().equals(tables[i]));
            }
            cleanBackupTable();
        }
    }

    @Test
    public void testBackupSetAddExists() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3" };
            String setName = "name";
            table.addToBackupSet(setName, tables);
            String[] addTables = new String[]{ "table4", "table5", "table6" };
            table.addToBackupSet(setName, addTables);
            Set<String> expectedTables = new HashSet<>(Arrays.asList("table1", "table2", "table3", "table4", "table5", "table6"));
            List<TableName> tnames = table.describeBackupSet(setName);
            Assert.assertTrue((tnames != null));
            Assert.assertTrue(((tnames.size()) == (expectedTables.size())));
            for (TableName tableName : tnames) {
                Assert.assertTrue(expectedTables.remove(tableName.getNameAsString()));
            }
            cleanBackupTable();
        }
    }

    @Test
    public void testBackupSetAddExistsIntersects() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3" };
            String setName = "name";
            table.addToBackupSet(setName, tables);
            String[] addTables = new String[]{ "table3", "table4", "table5", "table6" };
            table.addToBackupSet(setName, addTables);
            Set<String> expectedTables = new HashSet<>(Arrays.asList("table1", "table2", "table3", "table4", "table5", "table6"));
            List<TableName> tnames = table.describeBackupSet(setName);
            Assert.assertTrue((tnames != null));
            Assert.assertTrue(((tnames.size()) == (expectedTables.size())));
            for (TableName tableName : tnames) {
                Assert.assertTrue(expectedTables.remove(tableName.getNameAsString()));
            }
            cleanBackupTable();
        }
    }

    @Test
    public void testBackupSetRemoveSomeNotExists() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3", "table4" };
            String setName = "name";
            table.addToBackupSet(setName, tables);
            String[] removeTables = new String[]{ "table4", "table5", "table6" };
            table.removeFromBackupSet(setName, removeTables);
            Set<String> expectedTables = new HashSet<>(Arrays.asList("table1", "table2", "table3"));
            List<TableName> tnames = table.describeBackupSet(setName);
            Assert.assertTrue((tnames != null));
            Assert.assertTrue(((tnames.size()) == (expectedTables.size())));
            for (TableName tableName : tnames) {
                Assert.assertTrue(expectedTables.remove(tableName.getNameAsString()));
            }
            cleanBackupTable();
        }
    }

    @Test
    public void testBackupSetRemove() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3", "table4" };
            String setName = "name";
            table.addToBackupSet(setName, tables);
            String[] removeTables = new String[]{ "table4", "table3" };
            table.removeFromBackupSet(setName, removeTables);
            Set<String> expectedTables = new HashSet<>(Arrays.asList("table1", "table2"));
            List<TableName> tnames = table.describeBackupSet(setName);
            Assert.assertTrue((tnames != null));
            Assert.assertTrue(((tnames.size()) == (expectedTables.size())));
            for (TableName tableName : tnames) {
                Assert.assertTrue(expectedTables.remove(tableName.getNameAsString()));
            }
            cleanBackupTable();
        }
    }

    @Test
    public void testBackupSetDelete() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3", "table4" };
            String setName = "name";
            table.addToBackupSet(setName, tables);
            table.deleteBackupSet(setName);
            List<TableName> tnames = table.describeBackupSet(setName);
            Assert.assertTrue((tnames == null));
            cleanBackupTable();
        }
    }

    @Test
    public void testBackupSetList() throws IOException {
        try (BackupSystemTable table = new BackupSystemTable(TestBackupSystemTable.conn)) {
            String[] tables = new String[]{ "table1", "table2", "table3", "table4" };
            String setName1 = "name1";
            String setName2 = "name2";
            table.addToBackupSet(setName1, tables);
            table.addToBackupSet(setName2, tables);
            List<String> list = table.listBackupSets();
            Assert.assertTrue(((list.size()) == 2));
            Assert.assertTrue(list.get(0).equals(setName1));
            Assert.assertTrue(list.get(1).equals(setName2));
            cleanBackupTable();
        }
    }
}

