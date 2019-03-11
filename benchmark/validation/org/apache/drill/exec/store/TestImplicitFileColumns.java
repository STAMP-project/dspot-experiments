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
package org.apache.drill.exec.store;


import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.DATE;
import TypeProtos.MinorType.FLOAT8;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.util.JsonStringArrayList;
import org.apache.drill.exec.util.Text;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;


public class TestImplicitFileColumns extends BaseTestQuery {
    public static final String CSV = "csv";

    public static final String MAIN = "main";

    public static final String NESTED = "nested";

    public static final String MAIN_FILE = ((TestImplicitFileColumns.MAIN) + ".") + (TestImplicitFileColumns.CSV);

    public static final String NESTED_FILE = ((TestImplicitFileColumns.NESTED) + ".") + (TestImplicitFileColumns.CSV);

    public static final Path FILES = Paths.get("files");

    public static final Path NESTED_DIR = TestImplicitFileColumns.FILES.resolve(TestImplicitFileColumns.NESTED);

    public static final Path JSON_TBL = Paths.get("scan", "jsonTbl");// 1990/1.json : {id:100, name: "John"}, 1991/2.json : {id: 1000, name : "Joe"}


    public static final Path PARQUET_TBL = Paths.get("multilevel", "parquet");// 1990/Q1/orders_1990_q1.parquet, ...


    public static final Path PARQUET_CHANGE_TBL = Paths.get("multilevel", "parquetWithSchemaChange");

    public static final Path CSV_TBL = Paths.get("multilevel", "csv");// 1990/Q1/orders_1990_q1.csv, ..


    private static final JsonStringArrayList<Text> mainColumnValues = new JsonStringArrayList<Text>() {
        {
            add(new Text(TestImplicitFileColumns.MAIN));
        }
    };

    private static final JsonStringArrayList<Text> nestedColumnValues = new JsonStringArrayList<Text>() {
        {
            add(new Text(TestImplicitFileColumns.NESTED));
        }
    };

    private static File mainFile;

    private static File nestedFile;

    @Test
    public void testImplicitColumns() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select *, filename, suffix, fqn, filepath from dfs.`%s` order by filename", TestImplicitFileColumns.FILES).ordered().baselineColumns("columns", "dir0", "filename", "suffix", "fqn", "filepath").baselineValues(TestImplicitFileColumns.mainColumnValues, null, TestImplicitFileColumns.mainFile.getName(), TestImplicitFileColumns.CSV, TestImplicitFileColumns.mainFile.getCanonicalPath(), TestImplicitFileColumns.mainFile.getParentFile().getCanonicalPath()).baselineValues(TestImplicitFileColumns.nestedColumnValues, TestImplicitFileColumns.NESTED, TestImplicitFileColumns.NESTED_FILE, TestImplicitFileColumns.CSV, TestImplicitFileColumns.nestedFile.getCanonicalPath(), TestImplicitFileColumns.nestedFile.getParentFile().getCanonicalPath()).go();
    }

    @Test
    public void testImplicitColumnInWhereClause() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`%s` where filename = '%s'", TestImplicitFileColumns.NESTED_DIR, TestImplicitFileColumns.NESTED_FILE).unOrdered().baselineColumns("columns").baselineValues(TestImplicitFileColumns.nestedColumnValues).go();
    }

    @Test
    public void testImplicitColumnAlone() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select filename from dfs.`%s`", TestImplicitFileColumns.NESTED_DIR).unOrdered().baselineColumns("filename").baselineValues(TestImplicitFileColumns.NESTED_FILE).go();
    }

    @Test
    public void testImplicitColumnWithTableColumns() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select columns, filename from dfs.`%s`", TestImplicitFileColumns.NESTED_DIR).unOrdered().baselineColumns("columns", "filename").baselineValues(TestImplicitFileColumns.nestedColumnValues, TestImplicitFileColumns.NESTED_FILE).go();
    }

    @Test
    public void testCountStarWithImplicitColumnsInWhereClause() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(*) as cnt from dfs.`%s` where filename = '%s'", TestImplicitFileColumns.NESTED_DIR, TestImplicitFileColumns.NESTED_FILE).unOrdered().baselineColumns("cnt").baselineValues(1L).go();
    }

    @Test
    public void testImplicitAndPartitionColumnsInSelectClause() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select dir0, filename from dfs.`%s` order by filename", TestImplicitFileColumns.FILES).ordered().baselineColumns("dir0", "filename").baselineValues(null, TestImplicitFileColumns.MAIN_FILE).baselineValues(TestImplicitFileColumns.NESTED, TestImplicitFileColumns.NESTED_FILE).go();
    }

    @Test
    public void testImplicitColumnsForParquet() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select filename, suffix from cp.`tpch/region.parquet` limit 1").unOrdered().baselineColumns("filename", "suffix").baselineValues("region.parquet", "parquet").go();
    }

    // DRILL-4733
    @Test
    public void testMultilevelParquetWithSchemaChange() throws Exception {
        try {
            BaseTestQuery.test("alter session set `planner.enable_decimal_data_type` = true");
            BaseTestQuery.testBuilder().sqlQuery("select max(dir0) as max_dir from dfs.`%s`", TestImplicitFileColumns.PARQUET_CHANGE_TBL).unOrdered().baselineColumns("max_dir").baselineValues("voter50").go();
        } finally {
            BaseTestQuery.test("alter session set `planner.enable_decimal_data_type` = false");
        }
    }

    @Test
    public void testStarColumnJson() throws Exception {
        final BatchSchema expectedSchema = new SchemaBuilder().addNullable("dir0", VARCHAR).addNullable("id", BIGINT).addNullable("name", VARCHAR).build();
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`%s` ", TestImplicitFileColumns.JSON_TBL).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testStarColumnParquet() throws Exception {
        final BatchSchema expectedSchema = new SchemaBuilder().addNullable("dir0", VARCHAR).addNullable("dir1", VARCHAR).add("o_orderkey", INT).add("o_custkey", INT).add("o_orderstatus", VARCHAR).add("o_totalprice", FLOAT8).add("o_orderdate", DATE).add("o_orderpriority", VARCHAR).add("o_clerk", VARCHAR).add("o_shippriority", INT).add("o_comment", VARCHAR).build();
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`%s` ", TestImplicitFileColumns.PARQUET_TBL).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testStarColumnCsv() throws Exception {
        final BatchSchema expectedSchema = new SchemaBuilder().addNullable("dir0", VARCHAR).addNullable("dir1", VARCHAR).addArray("columns", VARCHAR).build();
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.`%s` ", TestImplicitFileColumns.CSV_TBL).schemaBaseLine(expectedSchema).build().run();
    }
}

