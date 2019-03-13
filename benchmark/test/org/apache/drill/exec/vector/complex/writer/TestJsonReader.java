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
package org.apache.drill.exec.vector.complex.writer;


import Charsets.UTF_8;
import UserBitShared.QueryType.PHYSICAL;
import UserBitShared.QueryType.SQL;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.store.easy.json.JSONRecordReader;
import org.apache.drill.exec.util.JsonStringHashMap;
import org.apache.drill.exec.util.Text;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestJsonReader extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(TestJsonReader.class);

    @Test
    public void testEmptyList() throws Exception {
        final String root = "store/json/emptyLists";
        BaseTestQuery.testBuilder().sqlQuery("select count(a[0]) as ct from dfs.`%s`", root, root).ordered().baselineColumns("ct").baselineValues(6L).build().run();
    }

    @Test
    public void schemaChange() throws Exception {
        BaseTestQuery.test("select b from dfs.`vector/complex/writer/schemaChange/`");
    }

    @Test
    public void testFieldSelectionBug() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select t.field_4.inner_3 as col_1, t.field_4 as col_2 from cp.`store/json/schema_change_int_to_string.json` t").unOrdered().optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = true").baselineColumns("col_1", "col_2").baselineValues(TestBuilder.mapOf(), TestBuilder.mapOf("inner_1", TestBuilder.listOf(), "inner_3", TestBuilder.mapOf())).baselineValues(TestBuilder.mapOf("inner_object_field_1", "2"), TestBuilder.mapOf("inner_1", TestBuilder.listOf("1", "2", "3"), "inner_2", "3", "inner_3", TestBuilder.mapOf("inner_object_field_1", "2"))).baselineValues(TestBuilder.mapOf(), TestBuilder.mapOf("inner_1", TestBuilder.listOf("4", "5", "6"), "inner_2", "3", "inner_3", TestBuilder.mapOf())).go();
        } finally {
            BaseTestQuery.test("alter session set `store.json.all_text_mode` = false");
        }
    }

    @Test
    public void testSplitAndTransferFailure() throws Exception {
        final String testVal = "a string";
        BaseTestQuery.testBuilder().sqlQuery("select flatten(config) as flat from cp.`store/json/null_list.json`").ordered().baselineColumns("flat").baselineValues(TestBuilder.listOf()).baselineValues(TestBuilder.listOf(testVal)).go();
        BaseTestQuery.test("select flatten(config) as flat from cp.`store/json/null_list_v2.json`");
        BaseTestQuery.testBuilder().sqlQuery("select flatten(config) as flat from cp.`store/json/null_list_v2.json`").ordered().baselineColumns("flat").baselineValues(TestBuilder.mapOf("repeated_varchar", TestBuilder.listOf())).baselineValues(TestBuilder.mapOf("repeated_varchar", TestBuilder.listOf(testVal))).go();
        BaseTestQuery.testBuilder().sqlQuery("select flatten(config) as flat from cp.`store/json/null_list_v3.json`").ordered().baselineColumns("flat").baselineValues(TestBuilder.mapOf("repeated_map", TestBuilder.listOf(TestBuilder.mapOf("repeated_varchar", TestBuilder.listOf())))).baselineValues(TestBuilder.mapOf("repeated_map", TestBuilder.listOf(TestBuilder.mapOf("repeated_varchar", TestBuilder.listOf(testVal))))).go();
    }

    @Test
    public void testReadCompressed() throws Exception {
        String filepath = "compressed_json.json";
        File f = new File(ExecTest.dirTestWatcher.getRootDir(), filepath);
        PrintWriter out = new PrintWriter(f);
        out.println("{\"a\" :5}");
        out.close();
        TestJsonReader.gzipIt(f);
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`%s.gz`", filepath).unOrdered().baselineColumns("a").baselineValues(5L).build().run();
        // test reading the uncompressed version as well
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`%s`", filepath).unOrdered().baselineColumns("a").baselineValues(5L).build().run();
    }

    @Test
    public void testDrill_1419() throws Exception {
        String[] queries = new String[]{ "select t.trans_id, t.trans_info.prod_id[0],t.trans_info.prod_id[1] from cp.`store/json/clicks.json` t limit 5" };
        long[] rowCounts = new long[]{ 5 };
        String filename = "/store/json/clicks.json";
        runTestsOnFile(filename, SQL, queries, rowCounts);
    }

    @Test
    public void testRepeatedCount() throws Exception {
        BaseTestQuery.test("select repeated_count(str_list) from cp.`store/json/json_basic_repeated_varchar.json`");
        BaseTestQuery.test("select repeated_count(INT_col) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_count(FLOAT4_col) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_count(VARCHAR_col) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_count(BIT_col) from cp.`parquet/alltypes_repeated.json`");
    }

    @Test
    public void testRepeatedContains() throws Exception {
        BaseTestQuery.test("select repeated_contains(str_list, 'asdf') from cp.`store/json/json_basic_repeated_varchar.json`");
        BaseTestQuery.test("select repeated_contains(INT_col, -2147483648) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_contains(FLOAT4_col, -1000000000000.0) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_contains(VARCHAR_col, 'qwerty' ) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_contains(BIT_col, true) from cp.`parquet/alltypes_repeated.json`");
        BaseTestQuery.test("select repeated_contains(BIT_col, false) from cp.`parquet/alltypes_repeated.json`");
    }

    @Test
    public void testSingleColumnRead_vector_fill_bug() throws Exception {
        String[] queries = new String[]{ "select * from cp.`store/json/single_column_long_file.json`" };
        long[] rowCounts = new long[]{ 13512 };
        String filename = "/store/json/single_column_long_file.json";
        runTestsOnFile(filename, SQL, queries, rowCounts);
    }

    @Test
    public void testNonExistentColumnReadAlone() throws Exception {
        String[] queries = new String[]{ "select non_existent_column from cp.`store/json/single_column_long_file.json`" };
        long[] rowCounts = new long[]{ 13512 };
        String filename = "/store/json/single_column_long_file.json";
        runTestsOnFile(filename, SQL, queries, rowCounts);
    }

    @Test
    public void testAllTextMode() throws Exception {
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = true");
        String[] queries = new String[]{ "select * from cp.`store/json/schema_change_int_to_string.json`" };
        long[] rowCounts = new long[]{ 3 };
        String filename = "/store/json/schema_change_int_to_string.json";
        runTestsOnFile(filename, SQL, queries, rowCounts);
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = false");
    }

    @Test
    public void readComplexWithStar() throws Exception {
        List<QueryDataBatch> results = BaseTestQuery.testSqlWithResults("select * from cp.`store/json/test_complex_read_with_star.json`");
        Assert.assertEquals(1, results.size());
        RecordBatchLoader batchLoader = new RecordBatchLoader(BaseTestQuery.getAllocator());
        QueryDataBatch batch = results.get(0);
        Assert.assertTrue(batchLoader.load(batch.getHeader().getDef(), batch.getData()));
        Assert.assertEquals(3, batchLoader.getSchema().getFieldCount());
        testExistentColumns(batchLoader);
        batch.release();
        batchLoader.clear();
    }

    @Test
    public void testNullWhereListExpected() throws Exception {
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = true");
        String[] queries = new String[]{ "select * from cp.`store/json/null_where_list_expected.json`" };
        long[] rowCounts = new long[]{ 3 };
        String filename = "/store/json/null_where_list_expected.json";
        runTestsOnFile(filename, SQL, queries, rowCounts);
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = false");
    }

    @Test
    public void testNullWhereMapExpected() throws Exception {
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = true");
        String[] queries = new String[]{ "select * from cp.`store/json/null_where_map_expected.json`" };
        long[] rowCounts = new long[]{ 3 };
        String filename = "/store/json/null_where_map_expected.json";
        runTestsOnFile(filename, SQL, queries, rowCounts);
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = false");
    }

    @Test
    public void ensureProjectionPushdown() throws Exception {
        // Tests to make sure that we are correctly eliminating schema changing columns.  If completes, means that the projection pushdown was successful.
        BaseTestQuery.test(("alter system set `store.json.all_text_mode` = false; " + ("select  t.field_1, t.field_3.inner_1, t.field_3.inner_2, t.field_4.inner_1 " + "from cp.`store/json/schema_change_int_to_string.json` t")));
    }

    // The project pushdown rule is correctly adding the projected columns to the scan, however it is not removing
    // the redundant project operator after the scan, this tests runs a physical plan generated from one of the tests to
    // ensure that the project is filtering out the correct data in the scan alone
    @Test
    public void testProjectPushdown() throws Exception {
        String[] queries = new String[]{ Files.asCharSource(DrillFileUtils.getResourceAsFile("/store/json/project_pushdown_json_physical_plan.json"), UTF_8).read() };
        long[] rowCounts = new long[]{ 3 };
        String filename = "/store/json/schema_change_int_to_string.json";
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = false");
        runTestsOnFile(filename, PHYSICAL, queries, rowCounts);
        List<QueryDataBatch> results = BaseTestQuery.testPhysicalWithResults(queries[0]);
        Assert.assertEquals(1, results.size());
        // "`field_1`", "`field_3`.`inner_1`", "`field_3`.`inner_2`", "`field_4`.`inner_1`"
        RecordBatchLoader batchLoader = new RecordBatchLoader(BaseTestQuery.getAllocator());
        QueryDataBatch batch = results.get(0);
        Assert.assertTrue(batchLoader.load(batch.getHeader().getDef(), batch.getData()));
        // this used to be five.  It is now three.  This is because the plan doesn't have a project.
        // Scanners are not responsible for projecting non-existent columns (as long as they project one column)
        Assert.assertEquals(3, batchLoader.getSchema().getFieldCount());
        testExistentColumns(batchLoader);
        batch.release();
        batchLoader.clear();
    }

    @Test
    public void testJsonDirectoryWithEmptyFile() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`store/json/jsonDirectoryWithEmpyFile`").unOrdered().baselineColumns("a").baselineValues(1L).build().run();
    }

    @Test
    public void testSelectStarWithUnionType() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select * from cp.`jsoninput/union/a.json`").ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("field1", "field2").baselineValues(1L, 1.2).baselineValues(TestBuilder.listOf(2L), 1.2).baselineValues(TestBuilder.mapOf("inner1", 3L, "inner2", 4L), TestBuilder.listOf(3L, 4.0, "5")).baselineValues(TestBuilder.mapOf("inner1", 3L, "inner2", TestBuilder.listOf(TestBuilder.mapOf("innerInner1", 1L, "innerInner2", TestBuilder.listOf(3L, "a")))), TestBuilder.listOf(TestBuilder.mapOf("inner3", 7L), 4.0, "5", TestBuilder.mapOf("inner4", 9L), TestBuilder.listOf(TestBuilder.mapOf("inner5", 10L, "inner6", 11L), TestBuilder.mapOf("inner5", 12L, "inner7", 13L)))).go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void testSelectFromListWithCase() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery(("select a, typeOf(a) `type` from " + ("(select case when is_list(field2) then field2[4][1].inner7 end a " + "from cp.`jsoninput/union/a.json`) where a is not null"))).ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("a", "type").baselineValues(13L, "BIGINT").go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void testTypeCase() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery(("select case when is_bigint(field1) " + ("then field1 when is_list(field1) then field1[0] " + "when is_map(field1) then t.field1.inner1 end f1 from cp.`jsoninput/union/a.json` t"))).ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("f1").baselineValues(1L).baselineValues(2L).baselineValues(3L).baselineValues(3L).go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void testSumWithTypeCase() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery(("select sum(cast(f1 as bigint)) sum_f1 from " + (("(select case when is_bigint(field1) then field1 " + "when is_list(field1) then field1[0] when is_map(field1) then t.field1.inner1 end f1 ") + "from cp.`jsoninput/union/a.json` t)"))).ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("sum_f1").baselineValues(9L).go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void testUnionExpressionMaterialization() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select a + b c from cp.`jsoninput/union/b.json`").ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("c").baselineValues(3L).baselineValues(7.0).baselineValues(11.0).go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void testSumMultipleBatches() throws Exception {
        File table_dir = ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get("multi_batch"));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "a.json")));
        for (int i = 0; i < 10000; i++) {
            os.write("{ type : \"map\", data : { a : 1 } }\n".getBytes());
            os.write("{ type : \"bigint\", data : 1 }\n".getBytes());
        }
        os.flush();
        os.close();
        try {
            BaseTestQuery.testBuilder().sqlQuery("select sum(cast(case when `type` = 'map' then t.data.a else data end as bigint)) `sum` from dfs.tmp.multi_batch t").ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("sum").baselineValues(20000L).go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void testSumFilesWithDifferentSchema() throws Exception {
        File table_dir = ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get("multi_file"));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "a.json")));
        for (int i = 0; i < 10000; i++) {
            os.write("{ type : \"map\", data : { a : 1 } }\n".getBytes());
        }
        os.flush();
        os.close();
        os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "b.json")));
        for (int i = 0; i < 10000; i++) {
            os.write("{ type : \"bigint\", data : 1 }\n".getBytes());
        }
        os.flush();
        os.close();
        try {
            BaseTestQuery.testBuilder().sqlQuery("select sum(cast(case when `type` = 'map' then t.data.a else data end as bigint)) `sum` from dfs.tmp.multi_file t").ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").baselineColumns("sum").baselineValues(20000L).go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `exec.enable_union_type` = false");
        }
    }

    @Test
    public void drill_4032() throws Exception {
        File table_dir = ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get("drill_4032"));
        table_dir.mkdir();
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "a.json")));
        os.write("{\"col1\": \"val1\",\"col2\": null}".getBytes());
        os.write("{\"col1\": \"val1\",\"col2\": {\"col3\":\"abc\", \"col4\":\"xyz\"}}".getBytes());
        os.flush();
        os.close();
        os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "b.json")));
        os.write("{\"col1\": \"val1\",\"col2\": null}".getBytes());
        os.write("{\"col1\": \"val1\",\"col2\": null}".getBytes());
        os.flush();
        os.close();
        BaseTestQuery.testNoResult("select t.col2.col3 from dfs.tmp.drill_4032 t");
    }

    @Test
    public void drill_4479() throws Exception {
        try {
            File table_dir = ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get("drill_4479"));
            table_dir.mkdir();
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "mostlynulls.json")));
            // Create an entire batch of null values for 3 columns
            for (int i = 0; i < (JSONRecordReader.DEFAULT_ROWS_PER_BATCH); i++) {
                os.write("{\"a\": null, \"b\": null, \"c\": null}".getBytes());
            }
            // Add a row with {bigint,  float, string} values
            os.write("{\"a\": 123456789123, \"b\": 99.999, \"c\": \"Hello World\"}".getBytes());
            os.flush();
            os.close();
            BaseTestQuery.testBuilder().sqlQuery("select c, count(*) as cnt from dfs.tmp.drill_4479 t group by c").ordered().optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = true").baselineColumns("c", "cnt").baselineValues(null, 4096L).baselineValues("Hello World", 1L).go();
            BaseTestQuery.testBuilder().sqlQuery("select a, b, c, count(*) as cnt from dfs.tmp.drill_4479 t group by a, b, c").ordered().optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = true").baselineColumns("a", "b", "c", "cnt").baselineValues(null, null, null, 4096L).baselineValues("123456789123", "99.999", "Hello World", 1L).go();
            BaseTestQuery.testBuilder().sqlQuery("select max(a) as x, max(b) as y, max(c) as z from dfs.tmp.drill_4479 t").ordered().optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = true").baselineColumns("x", "y", "z").baselineValues("123456789123", "99.999", "Hello World").go();
        } finally {
            BaseTestQuery.testNoResult("alter session set `store.json.all_text_mode` = false");
        }
    }

    @Test
    public void testFlattenEmptyArrayWithAllTextMode() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), "empty_array_all_text_mode.json")))) {
            writer.write("{ \"a\": { \"b\": { \"c\": [] }, \"c\": [] } }");
        }
        try {
            String query = "select flatten(t.a.b.c) as c from dfs.`empty_array_all_text_mode.json` t";
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = true").expectsEmptyResultSet().go();
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = false").expectsEmptyResultSet().go();
        } finally {
            BaseTestQuery.testNoResult("alter session reset `store.json.all_text_mode`");
        }
    }

    @Test
    public void testFlattenEmptyArrayWithUnionType() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), "empty_array.json")))) {
            writer.write("{ \"a\": { \"b\": { \"c\": [] }, \"c\": [] } }");
        }
        try {
            String query = "select flatten(t.a.b.c) as c from dfs.`empty_array.json` t";
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").expectsEmptyResultSet().go();
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type` = true").optionSettingQueriesForTestQuery("alter session set `store.json.all_text_mode` = true").expectsEmptyResultSet().go();
        } finally {
            BaseTestQuery.testNoResult("alter session reset `store.json.all_text_mode`");
            BaseTestQuery.testNoResult("alter session reset `exec.enable_union_type`");
        }
    }

    // DRILL-5521
    @Test
    public void testKvgenWithUnionAll() throws Exception {
        String fileName = "map.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), fileName)))) {
            writer.write("{\"rk\": \"a\", \"m\": {\"a\":\"1\"}}");
        }
        String query = String.format(("select kvgen(m) as res from (select m from dfs.`%s` union all " + "select convert_from(\'{\"a\" : null}\' ,\'json\') as m from (values(1)))"), fileName);
        Assert.assertEquals("Row count should match", 2, BaseTestQuery.testSql(query));
    }

    // DRILL-4264
    @Test
    public void testFieldWithDots() throws Exception {
        String fileName = "table.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), fileName)))) {
            writer.write("{\"rk.q\": \"a\", \"m\": {\"a.b\":\"1\", \"a\":{\"b\":\"2\"}, \"c\":\"3\"}}");
        }
        BaseTestQuery.testBuilder().sqlQuery(("select t.m.`a.b` as a,\n" + (((("t.m.a.b as b,\n" + "t.m[\'a.b\'] as c,\n") + "t.rk.q as d,\n") + "t.`rk.q` as e\n") + "from dfs.`%s` t")), fileName).unOrdered().baselineColumns("a", "b", "c", "d", "e").baselineValues("1", "2", "1", null, "a").go();
    }

    // DRILL-6020
    @Test
    public void testUntypedPathWithUnion() throws Exception {
        String fileName = "table.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), fileName)))) {
            writer.write("{\"rk\": {\"a\": {\"b\": \"1\"}}}");
            writer.write("{\"rk\": {\"a\": \"2\"}}");
        }
        JsonStringHashMap<String, Text> map = new JsonStringHashMap();
        map.put("b", new Text("1"));
        try {
            BaseTestQuery.testBuilder().sqlQuery("select t.rk.a as a from dfs.`%s` t", fileName).ordered().optionSettingQueriesForTestQuery("alter session set `exec.enable_union_type`=true").baselineColumns("a").baselineValues(map).baselineValues("2").go();
        } finally {
            BaseTestQuery.testNoResult("alter session reset `exec.enable_union_type`");
        }
    }
}

