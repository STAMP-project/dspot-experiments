/**
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql;


import junit.framework.TestCase;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hive.conf.HiveConf;


/**
 * Tests DDL with remote metastore service and second namenode (HIVE-6374)
 */
public class TestDDLWithRemoteMetastoreSecondNamenode extends TestCase {
    static HiveConf conf;

    private static final String Database1Name = "db1_nondefault_nn";

    private static final String Database2Name = "db2_nondefault_nn";

    private static final String Table1Name = "table1_nondefault_nn";

    private static final String Table2Name = "table2_nondefault_nn";

    private static final String Table3Name = "table3_nondefault_nn";

    private static final String Table4Name = "table4_nondefault_nn";

    private static final String Table5Name = "table5_nondefault_nn";

    private static final String Table6Name = "table6_nondefault_nn";

    private static final String Table7Name = "table7_nondefault_nn";

    private static final String Index1Name = "index1_table1_nondefault_nn";

    private static final String Index2Name = "index2_table1_nondefault_nn";

    private static final String tmpdir = System.getProperty("test.tmp.dir");

    private static final String tmpdirFs2 = "/" + (TestDDLWithRemoteMetastoreSecondNamenode.class.getName());

    private static final Path tmppath = new Path(TestDDLWithRemoteMetastoreSecondNamenode.tmpdir);

    private static final Path tmppathFs2 = new Path(TestDDLWithRemoteMetastoreSecondNamenode.tmpdirFs2);

    private static String fs2Uri;

    private static MiniDFSCluster miniDfs = null;

    private static Hive db;

    private static FileSystem fs;

    private static FileSystem fs2;

    private static HiveConf jobConf;

    private static IDriver driver;

    private static int tests = 0;

    private static Boolean isInitialized = false;

    public void testAlterPartitionSetLocationNonDefaultNameNode() throws Exception {
        TestCase.assertTrue("Test suite should have been initialized", TestDDLWithRemoteMetastoreSecondNamenode.isInitialized);
        String tableLocation = ((TestDDLWithRemoteMetastoreSecondNamenode.tmppathFs2) + "/") + "test_set_part_loc";
        Table table = createTableAndCheck(TestDDLWithRemoteMetastoreSecondNamenode.Table7Name, tableLocation);
        addPartitionAndCheck(table, "p", "p1", "/tmp/test/1");
        alterPartitionAndCheck(table, "p", "p1", "/tmp/test/2");
    }

    public void testCreateDatabaseWithTableNonDefaultNameNode() throws Exception {
        TestCase.assertTrue("Test suite should be initialied", TestDDLWithRemoteMetastoreSecondNamenode.isInitialized);
        final String tableLocation = ((TestDDLWithRemoteMetastoreSecondNamenode.tmppathFs2) + "/") + (TestDDLWithRemoteMetastoreSecondNamenode.Table3Name);
        final String databaseLocation = ((TestDDLWithRemoteMetastoreSecondNamenode.tmppathFs2) + "/") + (TestDDLWithRemoteMetastoreSecondNamenode.Database1Name);
        // Create database in specific location (absolute non-qualified path)
        createDatabaseAndCheck(TestDDLWithRemoteMetastoreSecondNamenode.Database1Name, databaseLocation);
        // Create database without location clause
        createDatabaseAndCheck(TestDDLWithRemoteMetastoreSecondNamenode.Database2Name, null);
        // Create table in database in specific location
        createTableAndCheck((((TestDDLWithRemoteMetastoreSecondNamenode.Database1Name) + ".") + (TestDDLWithRemoteMetastoreSecondNamenode.Table3Name)), tableLocation);
        // Create table in database without location clause
        createTableAndCheck((((TestDDLWithRemoteMetastoreSecondNamenode.Database1Name) + ".") + (TestDDLWithRemoteMetastoreSecondNamenode.Table4Name)), null);
    }
}

