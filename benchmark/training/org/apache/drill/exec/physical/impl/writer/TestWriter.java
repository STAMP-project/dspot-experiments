/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.writer;


import Charsets.UTF_8;
import ExecConstants.OUTPUT_FORMAT_OPTION;
import PlannerSettings.ENABLE_DECIMAL_DATA_TYPE_KEY;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.vector.BigIntVector;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestWriter extends BaseTestQuery {
    private static final String ROOT_DIR_REPLACEMENT = "%ROOT_DIR%";

    private static final String TMP_DIR_REPLACEMENT = "%TMP_DIR%";

    private static final String TEST_DIR_REPLACEMENT = "%TEST_DIR%";

    private static final String ALTER_SESSION = String.format("ALTER SESSION SET `%s` = 'csv'", OUTPUT_FORMAT_OPTION);

    @Test
    public void simpleCsv() throws Exception {
        File testDir = ExecTest.dirTestWatcher.makeRootSubDir(Paths.get("csvtest"));
        String plan = Files.asCharSource(DrillFileUtils.getResourceAsFile("/writer/simple_csv_writer.json"), UTF_8).read();
        plan = plan.replace(TestWriter.ROOT_DIR_REPLACEMENT, ExecTest.dirTestWatcher.getRootDir().getAbsolutePath()).replace(TestWriter.TMP_DIR_REPLACEMENT, ExecTest.dirTestWatcher.getTmpDir().getAbsolutePath()).replace(TestWriter.TEST_DIR_REPLACEMENT, testDir.getAbsolutePath());
        List<QueryDataBatch> results = BaseTestQuery.testPhysicalWithResults(plan);
        RecordBatchLoader batchLoader = new RecordBatchLoader(BaseTestQuery.getAllocator());
        QueryDataBatch batch = results.get(0);
        Assert.assertTrue(batchLoader.load(batch.getHeader().getDef(), batch.getData()));
        VarCharVector fragmentIdV = ((VarCharVector) (batchLoader.getValueAccessorById(VarCharVector.class, 0).getValueVector()));
        BigIntVector recordWrittenV = ((BigIntVector) (batchLoader.getValueAccessorById(BigIntVector.class, 1).getValueVector()));
        // expected only one row in output
        Assert.assertEquals(1, batchLoader.getRecordCount());
        Assert.assertEquals("0_0", fragmentIdV.getAccessor().getObject(0).toString());
        Assert.assertEquals(132000, recordWrittenV.getAccessor().get(0));
        // now verify csv files are written to disk
        Assert.assertTrue(testDir.exists());
        // expect two files
        int count = FileUtils.listFiles(testDir, new String[]{ "csv" }, false).size();
        Assert.assertEquals(2, count);
        for (QueryDataBatch b : results) {
            b.release();
        }
        batchLoader.clear();
    }

    @Test
    public void simpleCTAS() throws Exception {
        final String tableName = "simplectas";
        BaseTestQuery.runSQL("Use dfs.tmp");
        BaseTestQuery.runSQL(TestWriter.ALTER_SESSION);
        final String testQuery = String.format("CREATE TABLE %s AS SELECT * FROM cp.`employee.json`", tableName);
        testCTASQueryHelper(testQuery, 1155);
    }

    @Test
    public void complex1CTAS() throws Exception {
        final String tableName = "complex1ctas";
        BaseTestQuery.runSQL("Use dfs.tmp");
        BaseTestQuery.runSQL(TestWriter.ALTER_SESSION);
        final String testQuery = String.format(("CREATE TABLE %s AS SELECT first_name, last_name, " + "position_id FROM cp.`employee.json`"), tableName);
        testCTASQueryHelper(testQuery, 1155);
    }

    @Test
    public void complex2CTAS() throws Exception {
        final String tableName = "complex2ctas";
        BaseTestQuery.runSQL("Use dfs.tmp");
        BaseTestQuery.runSQL(TestWriter.ALTER_SESSION);
        final String testQuery = String.format(("CREATE TABLE %s AS SELECT CAST(`birth_date` as Timestamp) FROM " + "cp.`employee.json` GROUP BY birth_date"), tableName);
        testCTASQueryHelper(testQuery, 52);
    }

    @Test
    public void simpleCTASWithSchemaInTableName() throws Exception {
        final String tableName = "/test/simplectas2";
        BaseTestQuery.runSQL(TestWriter.ALTER_SESSION);
        final String testQuery = String.format("CREATE TABLE dfs.tmp.`%s` AS SELECT * FROM cp.`employee.json`", tableName);
        testCTASQueryHelper(testQuery, 1155);
    }

    @Test
    public void simpleParquetDecimal() throws Exception {
        try {
            final String tableName = "simpleparquetdecimal";
            final String testQuery = String.format(("CREATE TABLE dfs.tmp.`%s` AS SELECT cast(salary as " + "decimal(30,2)) * -1 as salary FROM cp.`employee.json`"), tableName);
            // enable decimal
            BaseTestQuery.test(String.format("alter session set `%s` = true", ENABLE_DECIMAL_DATA_TYPE_KEY));
            testCTASQueryHelper(testQuery, 1155);
            // disable decimal
        } finally {
            BaseTestQuery.test(String.format("alter session set `%s` = false", ENABLE_DECIMAL_DATA_TYPE_KEY));
        }
    }
}

