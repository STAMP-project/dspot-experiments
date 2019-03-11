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
package org.apache.drill.exec.store.parquet;


import ExecConstants.OUTPUT_FORMAT_OPTION;
import java.math.BigDecimal;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.test.BaseTestQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVarlenDecimal extends BaseTestQuery {
    private static final String DATAFILE = "cp.`parquet/varlenDecimal.parquet`";

    @Test
    public void testNullCount() throws Exception {
        String query = "select count(*) as c from %s where department_id is null";
        BaseTestQuery.testBuilder().sqlQuery(query, TestVarlenDecimal.DATAFILE).unOrdered().baselineColumns("c").baselineValues(1L).go();
    }

    @Test
    public void testNotNullCount() throws Exception {
        String query = "select count(*) as c from %s where department_id is not null";
        BaseTestQuery.testBuilder().sqlQuery(query, TestVarlenDecimal.DATAFILE).unOrdered().baselineColumns("c").baselineValues(106L).go();
    }

    @Test
    public void testSimpleQuery() throws Exception {
        String query = "select cast(department_id as bigint) as c from %s where cast(employee_id as decimal) = 170";
        BaseTestQuery.testBuilder().sqlQuery(query, TestVarlenDecimal.DATAFILE).unOrdered().baselineColumns("c").baselineValues(80L).go();
    }

    @Test
    public void testWriteReadJson() throws Exception {
        // Drill stores decimal values in JSON files correctly, but it can read only double values, but not big decimal.
        // See JsonToken class.
        String tableName = "jsonWithDecimals";
        try {
            BaseTestQuery.alterSession(OUTPUT_FORMAT_OPTION, "json");
            String bigDecimalValue = "987654321987654321987654321.987654321";
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + "select cast('%s' as decimal(36, 9)) dec36"), tableName, bigDecimalValue);
            String json = FileUtils.readFileToString(Paths.get(ExecTest.dirTestWatcher.getDfsTestTmpDir().getPath(), tableName, "0_0_0.json").toFile());
            Assert.assertThat(json, CoreMatchers.containsString(bigDecimalValue));
            // checks that decimal value may be read as a double value
            BaseTestQuery.testBuilder().sqlQuery("select dec36 from dfs.tmp.%s", tableName).unOrdered().baselineColumns("dec36").baselineValues(new BigDecimal(bigDecimalValue).doubleValue()).go();
        } finally {
            BaseTestQuery.resetSessionOption(OUTPUT_FORMAT_OPTION);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteReadCsv() throws Exception {
        String tableName = "csvWithDecimals";
        try {
            BaseTestQuery.alterSession(OUTPUT_FORMAT_OPTION, "csvh");
            String bigDecimalValue = "987654321987654321987654321.987654321";
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + "select cast('%s' as decimal(36, 9)) dec36"), tableName, bigDecimalValue);
            String csv = FileUtils.readFileToString(Paths.get(ExecTest.dirTestWatcher.getDfsTestTmpDir().getPath(), tableName, "0_0_0.csvh").toFile());
            Assert.assertThat(csv, CoreMatchers.containsString(bigDecimalValue));
            BaseTestQuery.testBuilder().sqlQuery("select cast(dec36 as decimal(36, 9)) as dec36 from dfs.tmp.%s", tableName).ordered().baselineColumns("dec36").baselineValues(new BigDecimal(bigDecimalValue)).go();
        } finally {
            BaseTestQuery.resetSessionOption(OUTPUT_FORMAT_OPTION);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }
}

