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


import ExecConstants.PARQUET_BLOCK_SIZE;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.drill.categories.ParquetTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ParquetTest.class, UnlikelyTest.class })
public class TestParquetWriterEmptyFiles extends BaseTestQuery {
    // see DRILL-2408
    @Test
    public void testWriteEmptyFile() throws Exception {
        final String outputFileName = "testparquetwriteremptyfiles_testwriteemptyfile";
        final File outputFile = FileUtils.getFile(ExecTest.dirTestWatcher.getDfsTestTmpDir(), outputFileName);
        BaseTestQuery.test("CREATE TABLE dfs.tmp.%s AS SELECT * FROM cp.`employee.json` WHERE 1=0", outputFileName);
        Assert.assertFalse(outputFile.exists());
    }

    @Test
    public void testMultipleWriters() throws Exception {
        final String outputFile = "testparquetwriteremptyfiles_testmultiplewriters";
        BaseTestQuery.runSQL("alter session set `planner.slice_target` = 1");
        try {
            final String query = "SELECT position_id FROM cp.`employee.json` WHERE position_id IN (15, 16) GROUP BY position_id";
            BaseTestQuery.test("CREATE TABLE dfs.tmp.%s AS %s", outputFile, query);
            // this query will fail if an "empty" file was created
            BaseTestQuery.testBuilder().unOrdered().sqlQuery("SELECT * FROM dfs.tmp.%s", outputFile).sqlBaselineQuery(query).go();
        } finally {
            BaseTestQuery.runSQL(("alter session set `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    // see DRILL-2408
    @Test
    public void testWriteEmptyFileAfterFlush() throws Exception {
        final String outputFile = "testparquetwriteremptyfiles_test_write_empty_file_after_flush";
        try {
            // this specific value will force a flush just after the final row is written
            // this may cause the creation of a new "empty" parquet file
            BaseTestQuery.test("ALTER SESSION SET `store.parquet.block-size` = 19926");
            final String query = "SELECT * FROM cp.`employee.json` LIMIT 100";
            BaseTestQuery.test("CREATE TABLE dfs.tmp.%s AS %s", outputFile, query);
            // this query will fail if an "empty" file was created
            BaseTestQuery.testBuilder().unOrdered().sqlQuery("SELECT * FROM dfs.tmp.%s", outputFile).sqlBaselineQuery(query).go();
        } finally {
            // restore the session option
            BaseTestQuery.resetSessionOption(PARQUET_BLOCK_SIZE);
        }
    }
}

