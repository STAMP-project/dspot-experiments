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


import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ImportTsv.BULK_OUTPUT_CONF_KEY;
import static ImportTsv.COLUMNS_CONF_KEY;
import static ImportTsv.MAPPER_CONF_KEY;
import static ImportTsv.SEPARATOR_CONF_KEY;


@Category({ MapReduceTests.class, LargeTests.class })
public class TestImportTSVWithVisibilityLabels implements Configurable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestImportTSVWithVisibilityLabels.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestImportTSVWithVisibilityLabels.class);

    protected static final String NAME = TestImportTsv.class.getSimpleName();

    protected static HBaseTestingUtility util = new HBaseTestingUtility();

    /**
     * Delete the tmp directory after running doMROnTableTest. Boolean. Default is
     * false.
     */
    protected static final String DELETE_AFTER_LOAD_CONF = (TestImportTSVWithVisibilityLabels.NAME) + ".deleteAfterLoad";

    /**
     * Force use of combiner in doMROnTableTest. Boolean. Default is true.
     */
    protected static final String FORCE_COMBINER_CONF = (TestImportTSVWithVisibilityLabels.NAME) + ".forceCombiner";

    private final String FAMILY = "FAM";

    private static final String TOPSECRET = "topsecret";

    private static final String PUBLIC = "public";

    private static final String PRIVATE = "private";

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static User SUPERUSER;

    private static Configuration conf;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMROnTable() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterMapper", ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001bsecret&private\n";
        TestImportTSVWithVisibilityLabels.util.createTable(tableName, FAMILY);
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 1);
        TestImportTSVWithVisibilityLabels.util.deleteTable(tableName);
    }

    @Test
    public void testMROnTableWithDeletes() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterMapper", ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001bsecret&private\n";
        TestImportTSVWithVisibilityLabels.util.createTable(tableName, FAMILY);
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 1);
        issueDeleteAndVerifyData(tableName);
        TestImportTSVWithVisibilityLabels.util.deleteTable(tableName);
    }

    @Test
    public void testMROnTableWithBulkload() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        Path hfiles = new Path(TestImportTSVWithVisibilityLabels.util.getDataTestDirOnTestFS(tableName.getNameAsString()), "hfiles");
        // Prepare the arguments required for the test.
        String[] args = new String[]{ (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (hfiles.toString()), ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001bsecret&private\n";
        TestImportTSVWithVisibilityLabels.util.createTable(tableName, FAMILY);
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 1);
        TestImportTSVWithVisibilityLabels.util.deleteTable(tableName);
    }

    @Test
    public void testBulkOutputWithTsvImporterTextMapper() throws Exception {
        final TableName table = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        String FAMILY = "FAM";
        Path bulkOutputPath = new Path(TestImportTSVWithVisibilityLabels.util.getDataTestDirOnTestFS(table.getNameAsString()), "hfiles");
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterTextMapper", ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (bulkOutputPath.toString()), table.getNameAsString() };
        String data = "KEY\u001bVALUE4\u001bVALUE8\u001bsecret&private\n";
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 4);
        TestImportTSVWithVisibilityLabels.util.deleteTable(table);
    }

    @Test
    public void testMRWithOutputFormat() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        Path hfiles = new Path(TestImportTSVWithVisibilityLabels.util.getDataTestDirOnTestFS(tableName.getNameAsString()), "hfiles");
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterMapper", (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (hfiles.toString()), ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        String data = "KEY\u001bVALUE4\u001bVALUE8\u001bsecret&private\n";
        TestImportTSVWithVisibilityLabels.util.createTable(tableName, FAMILY);
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 1);
        TestImportTSVWithVisibilityLabels.util.deleteTable(tableName);
    }

    @Test
    public void testBulkOutputWithInvalidLabels() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        Path hfiles = new Path(TestImportTSVWithVisibilityLabels.util.getDataTestDirOnTestFS(tableName.getNameAsString()), "hfiles");
        // Prepare the arguments required for the test.
        String[] args = new String[]{ (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (hfiles.toString()), ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        // 2 Data rows, one with valid label and one with invalid label
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001bprivate\nKEY1\u001bVALUE1\u001bVALUE2\u001binvalid\n";
        TestImportTSVWithVisibilityLabels.util.createTable(tableName, FAMILY);
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 1, 2);
        TestImportTSVWithVisibilityLabels.util.deleteTable(tableName);
    }

    @Test
    public void testBulkOutputWithTsvImporterTextMapperWithInvalidLabels() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithVisibilityLabels.util.getRandomUUID())));
        Path hfiles = new Path(TestImportTSVWithVisibilityLabels.util.getDataTestDirOnTestFS(tableName.getNameAsString()), "hfiles");
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterTextMapper", (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (hfiles.toString()), ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_CELL_VISIBILITY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        // 2 Data rows, one with valid label and one with invalid label
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001bprivate\nKEY1\u001bVALUE1\u001bVALUE2\u001binvalid\n";
        TestImportTSVWithVisibilityLabels.util.createTable(tableName, FAMILY);
        TestImportTSVWithVisibilityLabels.doMROnTableTest(TestImportTSVWithVisibilityLabels.util, FAMILY, data, args, 1, 2);
        TestImportTSVWithVisibilityLabels.util.deleteTable(tableName);
    }
}

