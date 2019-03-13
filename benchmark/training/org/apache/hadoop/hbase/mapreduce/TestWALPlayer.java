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
package org.apache.hadoop.hbase.mapreduce;


import WALPlayer.TABLES_KEY;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LauncherSecurityManager;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Basic test for the WALPlayer M/R tool
 */
@Category({ MapReduceTests.class, LargeTests.class })
public class TestWALPlayer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALPlayer.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static MiniHBaseCluster cluster;

    private static Path rootDir;

    private static Path walRootDir;

    private static FileSystem fs;

    private static FileSystem logFs;

    private static Configuration conf;

    @Rule
    public TestName name = new TestName();

    /**
     * Simple end-to-end test
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWALPlayer() throws Exception {
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "2"));
        final byte[] FAMILY = Bytes.toBytes("family");
        final byte[] COLUMN1 = Bytes.toBytes("c1");
        final byte[] COLUMN2 = Bytes.toBytes("c2");
        final byte[] ROW = Bytes.toBytes("row");
        Table t1 = TestWALPlayer.TEST_UTIL.createTable(tableName1, FAMILY);
        Table t2 = TestWALPlayer.TEST_UTIL.createTable(tableName2, FAMILY);
        // put a row into the first table
        Put p = new Put(ROW);
        p.addColumn(FAMILY, COLUMN1, COLUMN1);
        p.addColumn(FAMILY, COLUMN2, COLUMN2);
        t1.put(p);
        // delete one column
        Delete d = new Delete(ROW);
        d.addColumns(FAMILY, COLUMN1);
        t1.delete(d);
        // replay the WAL, map table 1 to table 2
        WAL log = TestWALPlayer.cluster.getRegionServer(0).getWAL(null);
        log.rollWriter();
        String walInputDir = new Path(TestWALPlayer.cluster.getMaster().getMasterFileSystem().getWALRootDir(), HConstants.HREGION_LOGDIR_NAME).toString();
        Configuration configuration = TestWALPlayer.TEST_UTIL.getConfiguration();
        WALPlayer player = new WALPlayer(configuration);
        String optionName = "_test_.name";
        configuration.set(optionName, "1000");
        player.setupTime(configuration, optionName);
        Assert.assertEquals(1000, configuration.getLong(optionName, 0));
        Assert.assertEquals(0, ToolRunner.run(configuration, player, new String[]{ walInputDir, tableName1.getNameAsString(), tableName2.getNameAsString() }));
        // verify the WAL was player into table 2
        Get g = new Get(ROW);
        Result r = t2.get(g);
        Assert.assertEquals(1, r.size());
        Assert.assertTrue(CellUtil.matchingQualifier(r.rawCells()[0], COLUMN2));
    }

    /**
     * Test WALKeyValueMapper setup and map
     */
    @Test
    public void testWALKeyValueMapper() throws Exception {
        testWALKeyValueMapper(TABLES_KEY);
    }

    @Test
    public void testWALKeyValueMapperWithDeprecatedConfig() throws Exception {
        testWALKeyValueMapper("hlog.input.tables");
    }

    /**
     * Test main method
     */
    @Test
    public void testMainMethod() throws Exception {
        PrintStream oldPrintStream = System.err;
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String[] args = new String[]{  };
        System.setErr(new PrintStream(data));
        try {
            System.setErr(new PrintStream(data));
            try {
                WALPlayer.main(args);
                Assert.fail("should be SecurityException");
            } catch (SecurityException e) {
                Assert.assertEquals((-1), newSecurityManager.getExitCode());
                Assert.assertTrue(data.toString().contains("ERROR: Wrong number of arguments:"));
                Assert.assertTrue(data.toString().contains(("Usage: WALPlayer [options] <wal inputdir>" + " <tables> [<tableMappings>]")));
                Assert.assertTrue(data.toString().contains("-Dwal.bulk.output=/path/for/output"));
            }
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }
}

