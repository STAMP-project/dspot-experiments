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


import ImportTsv.BULK_OUTPUT_CONF_KEY;
import ImportTsv.COLUMNS_CONF_KEY;
import ImportTsv.CREATE_TABLE_CONF_KEY;
import ImportTsv.DRY_RUN_CONF_KEY;
import ImportTsv.MAPPER_CONF_KEY;
import ImportTsv.NO_STRICT_COL_FAMILY;
import ImportTsv.SEPARATOR_CONF_KEY;
import ImportTsv.SKIP_EMPTY_COLUMNS;
import java.util.Map;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ImportTsv.BULK_OUTPUT_CONF_KEY;
import static ImportTsv.COLUMNS_CONF_KEY;
import static ImportTsv.DRY_RUN_CONF_KEY;
import static ImportTsv.MAPPER_CONF_KEY;
import static ImportTsv.SEPARATOR_CONF_KEY;


@Category({ VerySlowMapReduceTests.class, LargeTests.class })
public class TestImportTsv implements Configurable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestImportTsv.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestImportTsv.class);

    protected static final String NAME = TestImportTsv.class.getSimpleName();

    protected static HBaseTestingUtility util = new HBaseTestingUtility();

    // Delete the tmp directory after running doMROnTableTest. Boolean. Default is true.
    protected static final String DELETE_AFTER_LOAD_CONF = (TestImportTsv.NAME) + ".deleteAfterLoad";

    /**
     * Force use of combiner in doMROnTableTest. Boolean. Default is true.
     */
    protected static final String FORCE_COMBINER_CONF = (TestImportTsv.NAME) + ".forceCombiner";

    private final String FAMILY = "FAM";

    private TableName tn;

    private Map<String, String> args;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testMROnTable() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        doMROnTableTest(null, 1);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testMROnTableWithTimestamp() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        args.put(COLUMNS_CONF_KEY, "HBASE_ROW_KEY,HBASE_TS_KEY,FAM:A,FAM:B");
        args.put(SEPARATOR_CONF_KEY, ",");
        String data = "KEY,1234,VALUE1,VALUE2\n";
        doMROnTableTest(data, 1);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testMROnTableWithCustomMapper() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        args.put(MAPPER_CONF_KEY, "org.apache.hadoop.hbase.mapreduce.TsvImporterCustomTestMapper");
        doMROnTableTest(null, 3);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testBulkOutputWithoutAnExistingTable() throws Exception {
        // Prepare the arguments required for the test.
        Path hfiles = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, hfiles.toString());
        doMROnTableTest(null, 3);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testBulkOutputWithAnExistingTable() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        // Prepare the arguments required for the test.
        Path hfiles = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, hfiles.toString());
        doMROnTableTest(null, 3);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testBulkOutputWithAnExistingTableNoStrictTrue() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        // Prepare the arguments required for the test.
        Path hfiles = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, hfiles.toString());
        args.put(NO_STRICT_COL_FAMILY, "true");
        doMROnTableTest(null, 3);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testJobConfigurationsWithTsvImporterTextMapper() throws Exception {
        Path bulkOutputPath = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        String INPUT_FILE = "InputFile1.csv";
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterTextMapper", ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B", ("-D" + (SEPARATOR_CONF_KEY)) + "=,", (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (bulkOutputPath.toString()), tn.getNameAsString(), INPUT_FILE };
        Assert.assertEquals("running test job configuration failed.", 0, ToolRunner.run(new Configuration(TestImportTsv.util.getConfiguration()), new ImportTsv() {
            @Override
            public int run(String[] args) throws Exception {
                Job job = createSubmittableJob(getConf(), args);
                Assert.assertTrue(job.getMapperClass().equals(TsvImporterTextMapper.class));
                Assert.assertTrue(job.getReducerClass().equals(TextSortReducer.class));
                Assert.assertTrue(job.getMapOutputValueClass().equals(Text.class));
                return 0;
            }
        }, args));
        // Delete table created by createSubmittableJob.
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testBulkOutputWithTsvImporterTextMapper() throws Exception {
        Path bulkOutputPath = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(MAPPER_CONF_KEY, "org.apache.hadoop.hbase.mapreduce.TsvImporterTextMapper");
        args.put(BULK_OUTPUT_CONF_KEY, bulkOutputPath.toString());
        String data = "KEY\u001bVALUE4\u001bVALUE8\n";
        doMROnTableTest(data, 4);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testWithoutAnExistingTableAndCreateTableSetToNo() throws Exception {
        String[] args = new String[]{ tn.getNameAsString(), "/inputFile" };
        Configuration conf = new Configuration(TestImportTsv.util.getConfiguration());
        conf.set(COLUMNS_CONF_KEY, "HBASE_ROW_KEY,FAM:A");
        conf.set(BULK_OUTPUT_CONF_KEY, "/output");
        conf.set(CREATE_TABLE_CONF_KEY, "no");
        exception.expect(TableNotFoundException.class);
        Assert.assertEquals("running test job configuration failed.", 0, ToolRunner.run(new Configuration(TestImportTsv.util.getConfiguration()), new ImportTsv() {
            @Override
            public int run(String[] args) throws Exception {
                createSubmittableJob(getConf(), args);
                return 0;
            }
        }, args));
    }

    @Test
    public void testMRWithoutAnExistingTable() throws Exception {
        String[] args = new String[]{ tn.getNameAsString(), "/inputFile" };
        exception.expect(TableNotFoundException.class);
        Assert.assertEquals("running test job configuration failed.", 0, ToolRunner.run(new Configuration(TestImportTsv.util.getConfiguration()), new ImportTsv() {
            @Override
            public int run(String[] args) throws Exception {
                createSubmittableJob(getConf(), args);
                return 0;
            }
        }, args));
    }

    @Test
    public void testJobConfigurationsWithDryMode() throws Exception {
        Path bulkOutputPath = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        String INPUT_FILE = "InputFile1.csv";
        // Prepare the arguments required for the test.
        String[] argsArray = new String[]{ ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B", ("-D" + (SEPARATOR_CONF_KEY)) + "=,", (("-D" + (BULK_OUTPUT_CONF_KEY)) + "=") + (bulkOutputPath.toString()), ("-D" + (DRY_RUN_CONF_KEY)) + "=true", tn.getNameAsString(), INPUT_FILE };
        Assert.assertEquals("running test job configuration failed.", 0, ToolRunner.run(new Configuration(TestImportTsv.util.getConfiguration()), new ImportTsv() {
            @Override
            public int run(String[] args) throws Exception {
                Job job = createSubmittableJob(getConf(), args);
                Assert.assertTrue(job.getOutputFormatClass().equals(NullOutputFormat.class));
                return 0;
            }
        }, argsArray));
        // Delete table created by createSubmittableJob.
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testDryModeWithoutBulkOutputAndTableExists() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        args.put(DRY_RUN_CONF_KEY, "true");
        doMROnTableTest(null, 1);
        // Dry mode should not delete an existing table. If it's not present,
        // this will throw TableNotFoundException.
        TestImportTsv.util.deleteTable(tn);
    }

    /**
     * If table is not present in non-bulk mode, dry run should fail just like
     * normal mode.
     */
    @Test
    public void testDryModeWithoutBulkOutputAndTableDoesNotExists() throws Exception {
        args.put(DRY_RUN_CONF_KEY, "true");
        exception.expect(TableNotFoundException.class);
        doMROnTableTest(null, 1);
    }

    @Test
    public void testDryModeWithBulkOutputAndTableExists() throws Exception {
        TestImportTsv.util.createTable(tn, FAMILY);
        // Prepare the arguments required for the test.
        Path hfiles = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, hfiles.toString());
        args.put(DRY_RUN_CONF_KEY, "true");
        doMROnTableTest(null, 1);
        // Dry mode should not delete an existing table. If it's not present,
        // this will throw TableNotFoundException.
        TestImportTsv.util.deleteTable(tn);
    }

    /**
     * If table is not present in bulk mode and create.table is not set to yes,
     * import should fail with TableNotFoundException.
     */
    @Test
    public void testDryModeWithBulkOutputAndTableDoesNotExistsCreateTableSetToNo() throws Exception {
        // Prepare the arguments required for the test.
        Path hfiles = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, hfiles.toString());
        args.put(DRY_RUN_CONF_KEY, "true");
        args.put(CREATE_TABLE_CONF_KEY, "no");
        exception.expect(TableNotFoundException.class);
        doMROnTableTest(null, 1);
    }

    @Test
    public void testDryModeWithBulkModeAndTableDoesNotExistsCreateTableSetToYes() throws Exception {
        // Prepare the arguments required for the test.
        Path hfiles = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, hfiles.toString());
        args.put(DRY_RUN_CONF_KEY, "true");
        args.put(CREATE_TABLE_CONF_KEY, "yes");
        doMROnTableTest(null, 1);
        // Verify temporary table was deleted.
        exception.expect(TableNotFoundException.class);
        TestImportTsv.util.deleteTable(tn);
    }

    /**
     * If there are invalid data rows as inputs, then only those rows should be ignored.
     */
    @Test
    public void testTsvImporterTextMapperWithInvalidData() throws Exception {
        Path bulkOutputPath = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(MAPPER_CONF_KEY, "org.apache.hadoop.hbase.mapreduce.TsvImporterTextMapper");
        args.put(BULK_OUTPUT_CONF_KEY, bulkOutputPath.toString());
        args.put(COLUMNS_CONF_KEY, "HBASE_ROW_KEY,HBASE_TS_KEY,FAM:A,FAM:B");
        args.put(SEPARATOR_CONF_KEY, ",");
        // 3 Rows of data as input. 2 Rows are valid and 1 row is invalid as it doesn't have TS
        String data = "KEY,1234,VALUE1,VALUE2\nKEY\nKEY,1235,VALUE1,VALUE2\n";
        TestImportTsv.doMROnTableTest(TestImportTsv.util, tn, FAMILY, data, args, 1, 4);
        TestImportTsv.util.deleteTable(tn);
    }

    @Test
    public void testSkipEmptyColumns() throws Exception {
        Path bulkOutputPath = new Path(TestImportTsv.util.getDataTestDirOnTestFS(tn.getNameAsString()), "hfiles");
        args.put(BULK_OUTPUT_CONF_KEY, bulkOutputPath.toString());
        args.put(COLUMNS_CONF_KEY, "HBASE_ROW_KEY,HBASE_TS_KEY,FAM:A,FAM:B");
        args.put(SEPARATOR_CONF_KEY, ",");
        args.put(SKIP_EMPTY_COLUMNS, "true");
        // 2 Rows of data as input. Both rows are valid and only 3 columns are no-empty among 4
        String data = "KEY,1234,VALUE1,VALUE2\nKEY,1235,,VALUE2\n";
        TestImportTsv.doMROnTableTest(TestImportTsv.util, tn, FAMILY, data, args, 1, 3);
        TestImportTsv.util.deleteTable(tn);
    }
}

