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


import FunctionGenerationHelper.IS_FALSE;
import FunctionGenerationHelper.IS_NOT_FALSE;
import FunctionGenerationHelper.IS_NOT_TRUE;
import FunctionGenerationHelper.IS_TRUE;
import PlannerSettings.PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_KEY;
import PlannerSettings.PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_THRESHOLD_KEY;
import RowsMatch.ALL;
import RowsMatch.NONE;
import RowsMatch.SOME;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.drill.PlanTestBase;
import org.apache.drill.common.expression.LogicalExpression;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.expr.stat.ParquetIsPredicate;
import org.apache.drill.exec.expr.stat.RangeExprEvaluator;
import org.apache.drill.exec.ops.FragmentContextImpl;
import org.apache.drill.exec.planner.physical.PlannerSettings;
import org.apache.drill.test.BaseTestQuery;
import org.apache.hadoop.fs.FileSystem;
import org.apache.parquet.column.statistics.BooleanStatistics;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestParquetFilterPushDown extends PlanTestBase {
    private static final String CTAS_TABLE = "order_ctas";

    private static FragmentContextImpl fragContext;

    private static FileSystem fs;

    @Rule
    public final TestWatcher ctasWatcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            deleteCtasTable();
        }

        @Override
        protected void starting(Description description) {
            deleteCtasTable();
        }

        @Override
        protected void finished(Description description) {
            deleteCtasTable();
        }

        private void deleteCtasTable() {
            FileUtils.deleteQuietly(new File(ExecTest.dirTestWatcher.getDfsTestTmpDir(), TestParquetFilterPushDown.CTAS_TABLE));
        }
    };

    // Test filter evaluation directly without go through SQL queries.
    @Test
    public void testIntPredicateWithEval() throws Exception {
        // intTbl.parquet has only one int column
        // intCol : [0, 100].
        final File file = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(Paths.get("parquetFilterPush", "intTbl", "intTbl.parquet")).toFile();
        ParquetMetadata footer = getParquetMetaData(file);
        testParquetRowGroupFilterEval(footer, "intCol = 100", SOME);
        testParquetRowGroupFilterEval(footer, "intCol = 0", SOME);
        testParquetRowGroupFilterEval(footer, "intCol = 50", SOME);
        testParquetRowGroupFilterEval(footer, "intCol = -1", NONE);
        testParquetRowGroupFilterEval(footer, "intCol = 101", NONE);
        testParquetRowGroupFilterEval(footer, "intCol > 100", NONE);
        testParquetRowGroupFilterEval(footer, "intCol > 99", SOME);
        testParquetRowGroupFilterEval(footer, "intCol >= 100", SOME);
        testParquetRowGroupFilterEval(footer, "intCol >= 101", NONE);
        testParquetRowGroupFilterEval(footer, "intCol < 100", SOME);
        testParquetRowGroupFilterEval(footer, "intCol < 1", SOME);
        testParquetRowGroupFilterEval(footer, "intCol < 0", NONE);
        testParquetRowGroupFilterEval(footer, "intCol <= 100", ALL);
        testParquetRowGroupFilterEval(footer, "intCol <= 1", SOME);
        testParquetRowGroupFilterEval(footer, "intCol <= 0", SOME);
        testParquetRowGroupFilterEval(footer, "intCol <= -1", NONE);
        // "and"
        testParquetRowGroupFilterEval(footer, "intCol > 100 and intCol < 200", NONE);
        testParquetRowGroupFilterEval(footer, "intCol > 50 and intCol < 200", SOME);
        testParquetRowGroupFilterEval(footer, "intCol > 50 and intCol > 200", NONE);// essentially, intCol > 200

        // "or"
        testParquetRowGroupFilterEval(footer, "intCol = 150 or intCol = 160", NONE);
        testParquetRowGroupFilterEval(footer, "intCol = 50 or intCol = 160", SOME);
        // "nonExistCol" does not exist in the table. "AND" with a filter on exist column
        testParquetRowGroupFilterEval(footer, "intCol > 100 and nonExistCol = 100", NONE);
        testParquetRowGroupFilterEval(footer, "intCol > 50 and nonExistCol = 100", NONE);// since nonExistCol = 100 -> Unknown -> could drop.

        testParquetRowGroupFilterEval(footer, "nonExistCol = 100 and intCol > 50", NONE);// since nonExistCol = 100 -> Unknown -> could drop.

        testParquetRowGroupFilterEval(footer, "intCol > 100 and nonExistCol < 'abc'", NONE);
        testParquetRowGroupFilterEval(footer, "nonExistCol < 'abc' and intCol > 100", NONE);// nonExistCol < 'abc' hit NumberException and is ignored, but intCol >100 will

        // say "drop".
        testParquetRowGroupFilterEval(footer, "intCol > 50 and nonExistCol < 'abc'", SOME);// because nonExistCol < 'abc' hit NumberException and

        // is ignored.
        // "nonExistCol" does not exist in the table. "OR" with a filter on exist column
        testParquetRowGroupFilterEval(footer, "intCol > 100 or nonExistCol = 100", NONE);// nonExistCol = 100 -> could drop.

        testParquetRowGroupFilterEval(footer, "nonExistCol = 100 or intCol > 100", NONE);// nonExistCol = 100 -> could drop.

        testParquetRowGroupFilterEval(footer, "intCol > 50 or nonExistCol < 100", SOME);
        testParquetRowGroupFilterEval(footer, "nonExistCol < 100 or intCol > 50", SOME);
        // cast function on column side (LHS)
        testParquetRowGroupFilterEval(footer, "cast(intCol as bigint) = 100", SOME);
        testParquetRowGroupFilterEval(footer, "cast(intCol as bigint) = 0", SOME);
        testParquetRowGroupFilterEval(footer, "cast(intCol as bigint) = 50", SOME);
        testParquetRowGroupFilterEval(footer, "cast(intCol as bigint) = 101", NONE);
        testParquetRowGroupFilterEval(footer, "cast(intCol as bigint) = -1", NONE);
        // cast function on constant side (RHS)
        testParquetRowGroupFilterEval(footer, "intCol = cast(100 as bigint)", SOME);
        testParquetRowGroupFilterEval(footer, "intCol = cast(0 as bigint)", SOME);
        testParquetRowGroupFilterEval(footer, "intCol = cast(50 as bigint)", SOME);
        testParquetRowGroupFilterEval(footer, "intCol = cast(101 as bigint)", NONE);
        testParquetRowGroupFilterEval(footer, "intCol = cast(-1 as bigint)", NONE);
        // cast into float4/float8
        testParquetRowGroupFilterEval(footer, "cast(intCol as float4) = cast(101.0 as float4)", NONE);
        testParquetRowGroupFilterEval(footer, "cast(intCol as float4) = cast(-1.0 as float4)", NONE);
        testParquetRowGroupFilterEval(footer, "cast(intCol as float4) = cast(1.0 as float4)", SOME);
        testParquetRowGroupFilterEval(footer, "cast(intCol as float8) = 101.0", NONE);
        testParquetRowGroupFilterEval(footer, "cast(intCol as float8) = -1.0", NONE);
        testParquetRowGroupFilterEval(footer, "cast(intCol as float8) = 1.0", SOME);
    }

    @Test
    public void testIntPredicateAgainstAllNullColWithEval() throws Exception {
        // intAllNull.parquet has only one int column with all values being NULL.
        // column values statistics: num_nulls: 25, min/max is not defined
        final File file = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(Paths.get("parquetFilterPush", "intTbl", "intAllNull.parquet")).toFile();
        ParquetMetadata footer = getParquetMetaData(file);
        testParquetRowGroupFilterEval(footer, "intCol = 100", NONE);
        testParquetRowGroupFilterEval(footer, "intCol = 0", NONE);
        testParquetRowGroupFilterEval(footer, "intCol = -100", NONE);
        testParquetRowGroupFilterEval(footer, "intCol > 10", NONE);
        testParquetRowGroupFilterEval(footer, "intCol >= 10", NONE);
        testParquetRowGroupFilterEval(footer, "intCol < 10", NONE);
        testParquetRowGroupFilterEval(footer, "intCol <= 10", NONE);
    }

    @Test
    public void testDatePredicateAgainstDrillCTAS1_8WithEval() throws Exception {
        // The parquet file is created on drill 1.8.0 with DRILL CTAS:
        // create table dfs.tmp.`dateTblCorrupted/t1` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and date '1992-01-03';
        final File file = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(Paths.get("parquetFilterPush", "dateTblCorrupted", "t1", "0_0_0.parquet")).toFile();
        ParquetMetadata footer = getParquetMetaData(file);
        testDatePredicateAgainstDrillCTASHelper(footer);
    }

    @Test
    public void testDatePredicateAgainstDrillCTASPost1_8WithEval() throws Exception {
        // The parquet file is created on drill 1.9.0-SNAPSHOT (commit id:03e8f9f3e01c56a9411bb4333e4851c92db6e410) with DRILL CTAS:
        // create table dfs.tmp.`dateTbl1_9/t1` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and date '1992-01-03';
        final File file = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(Paths.get("parquetFilterPush", "dateTbl1_9", "t1", "0_0_0.parquet")).toFile();
        ParquetMetadata footer = getParquetMetaData(file);
        testDatePredicateAgainstDrillCTASHelper(footer);
    }

    @Test
    public void testTimeStampPredicateWithEval() throws Exception {
        // Table dateTblCorrupted is created by CTAS in drill 1.8.0.
        // create table dfs.tmp.`tsTbl/t1` as select DATE_ADD(cast(o_orderdate as date), INTERVAL '0 10:20:30' DAY TO SECOND) as o_ordertimestamp from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and date '1992-01-03';
        final File file = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(Paths.get("parquetFilterPush", "tsTbl", "t1", "0_0_0.parquet")).toFile();
        ParquetMetadata footer = getParquetMetaData(file);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp = cast('1992-01-01 10:20:30' as timestamp)", SOME);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp = cast('1992-01-01 10:20:29' as timestamp)", NONE);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp >= cast('1992-01-01 10:20:29' as timestamp)", ALL);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp >= cast('1992-01-03 10:20:30' as timestamp)", SOME);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp >= cast('1992-01-03 10:20:31' as timestamp)", NONE);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp > cast('1992-01-03 10:20:29' as timestamp)", SOME);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp > cast('1992-01-03 10:20:30' as timestamp)", NONE);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp <= cast('1992-01-01 10:20:30' as timestamp)", SOME);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp <= cast('1992-01-01 10:20:29' as timestamp)", NONE);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp < cast('1992-01-01 10:20:31' as timestamp)", SOME);
        testParquetRowGroupFilterEval(footer, "o_ordertimestamp < cast('1992-01-01 10:20:30' as timestamp)", NONE);
    }

    @Test
    public void testFilterPruning() throws Exception {
        // multirowgroup2 is a parquet file with 3 rowgroups inside. One with a=0, another with a=1 and a=2, and the last with a=3 and a=4;
        // FilterPushDown should be able to prune the filter from the scan operator according to the rowgroup statistics.
        final String sql = "select * from dfs.`parquet/multirowgroup2.parquet` where ";
        PlanTestBase.testPlanMatchingPatterns((sql + "a > 1"), new String[]{ "numRowGroups=2" });// No filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a > 2"), new String[]{ "numRowGroups=1" }, new String[]{ "Filter\\(" });// Filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a < 2"), new String[]{ "numRowGroups=2" });// No filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a < 1"), new String[]{ "numRowGroups=1" }, new String[]{ "Filter\\(" });// Filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a >= 2"), new String[]{ "numRowGroups=2" });// No filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a >= 1"), new String[]{ "numRowGroups=2" }, new String[]{ "Filter\\(" });// Filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a <= 1"), new String[]{ "numRowGroups=2" });// No filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a <= 2"), new String[]{ "numRowGroups=2" }, new String[]{ "Filter\\(" });// Filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a > 0 and a < 2"), new String[]{ "numRowGroups=1" });// No filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a > 0 and a < 3"), new String[]{ "numRowGroups=1" }, new String[]{ "Filter\\(" });// Filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a < 1 or a > 1"), new String[]{ "numRowGroups=3" });// No filter pruning

        PlanTestBase.testPlanMatchingPatterns((sql + "a < 1 or a > 2"), new String[]{ "numRowGroups=2" }, new String[]{ "Filter\\(" });// Filter pruning

        // Partial filter pruning
        testParquetFilterPruning((sql + "a >=1 and cast(a as varchar) like '%3%'"), 1, 2, new String[]{ ">\\($1, 1\\)" });
        testParquetFilterPruning((sql + "a >=1 and a/3>=1"), 2, 2, new String[]{ ">\\($1, 1\\)" });
    }

    @Test
    public void testFilterPruningWithNulls() throws Exception {
        // multirowgroupwithNulls is a parquet file with 4 rowgroups inside and some groups contain null values.
        // RG1 : [min: 20, max: 29, num_nulls: 0]
        // RG2 : [min: 31, max: 39, num_nulls: 1]
        // RG3 : [min: 40, max: 49, num_nulls: 1]
        // RG4 : [min: 50, max: 59, num_nulls: 0]
        final String sql = "select a from dfs.`parquet/multirowgroupwithNulls.parquet` where ";
        // "<" "and" ">" with filter
        testParquetFilterPruning((sql + "30 < a and 40 > a"), 9, 1, null);
        testParquetFilterPruning((sql + "30 < a and a < 40"), 9, 1, null);
        testParquetFilterPruning((sql + "a > 30 and 40 > a"), 9, 1, null);
        testParquetFilterPruning((sql + "a > 30 and a < 40"), 9, 1, null);
        // "<" "and" ">" with no filter
        testParquetFilterPruning((sql + "19 < a and 30 > a"), 10, 1, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "19 < a and a < 30"), 10, 1, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "a > 19 and 30 > a"), 10, 1, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "a > 19 and a < 30"), 10, 1, new String[]{ "Filter\\(" });
        // "<=" "and" ">=" with filter
        testParquetFilterPruning((sql + "a >= 30 and 39 >= a"), 9, 1, null);
        testParquetFilterPruning((sql + "a >= 30 and a <= 39"), 9, 1, null);
        testParquetFilterPruning((sql + "30 <= a and 39 >= a"), 9, 1, null);
        testParquetFilterPruning((sql + "30 <= a and a <= 39"), 9, 1, null);
        // "<=" "and" ">=" with no filter
        testParquetFilterPruning((sql + "a >= 20 and a <= 29"), 10, 1, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "a >= 20 and 29 >= a"), 10, 1, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "20 <= a and a <= 29"), 10, 1, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "20 <= a and 29 >= a"), 10, 1, new String[]{ "Filter\\(" });
        // "<" "or" ">" with filter
        testParquetFilterPruning((sql + "a < 40 or a > 49"), 29, 3, null);
        testParquetFilterPruning((sql + "a < 40 or 49 < a"), 29, 3, null);
        testParquetFilterPruning((sql + "40 > a or a > 49"), 29, 3, null);
        testParquetFilterPruning((sql + "40 > a or 49 < a"), 29, 3, null);
        // "<" "or" ">" with no filter
        testParquetFilterPruning((sql + "a < 30 or a > 49"), 20, 2, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "a < 30 or 49 < a"), 20, 2, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "30 > a or a > 49"), 20, 2, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "30 > a or 49 < a"), 20, 2, new String[]{ "Filter\\(" });
        // "<=" "or" ">=" with filter
        testParquetFilterPruning((sql + "a <= 39 or a >= 50"), 29, 3, null);
        testParquetFilterPruning((sql + "a <= 39 or 50 <= a"), 29, 3, null);
        testParquetFilterPruning((sql + "39 >= a or a >= 50"), 29, 3, null);
        testParquetFilterPruning((sql + "39 >= a or 50 <= a"), 29, 3, null);
        // "<=" "or" ">=" with no filter
        testParquetFilterPruning((sql + "a <= 29 or a >= 50"), 20, 2, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "a <= 29 or 50 <= a"), 20, 2, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "29 >= a or a >= 50"), 20, 2, new String[]{ "Filter\\(" });
        testParquetFilterPruning((sql + "29 >= a or 50 <= a"), 20, 2, new String[]{ "Filter\\(" });
    }

    // Test against parquet files from Drill CTAS post 1.8.0 release.
    @Test
    public void testDatePredicateAgaistDrillCTASPost1_8() throws Exception {
        BaseTestQuery.test("use dfs.tmp");
        BaseTestQuery.test(("create table `%s/t1` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and " + "date '1992-01-03'"), TestParquetFilterPushDown.CTAS_TABLE);
        BaseTestQuery.test(("create table `%s/t2` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-04' and " + "date '1992-01-06'"), TestParquetFilterPushDown.CTAS_TABLE);
        BaseTestQuery.test(("create table `%s/t3` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-07' and " + "date '1992-01-09'"), TestParquetFilterPushDown.CTAS_TABLE);
        final String query1 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate = date '1992-01-01'";
        testParquetFilterPD(query1, 9, 1, false);
        final String query2 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate < date '1992-01-01'";
        testParquetFilterPD(query2, 0, 1, false);
        final String query3 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate between date '1992-01-01' and date '1992-01-03'";
        testParquetFilterPD(query3, 22, 1, false);
        final String query4 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate between date '1992-01-01' and date '1992-01-04'";
        testParquetFilterPD(query4, 33, 2, false);
        final String query5 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate between date '1992-01-01' and date '1992-01-06'";
        testParquetFilterPD(query5, 49, 2, false);
        final String query6 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate > date '1992-01-10'";
        testParquetFilterPD(query6, 0, 1, false);
        // Test parquet files with metadata cache files available.
        // Now, create parquet metadata cache files, and run the above queries again. Flag "usedMetadataFile" should be true.
        BaseTestQuery.test(String.format("refresh table metadata %s", TestParquetFilterPushDown.CTAS_TABLE));
        testParquetFilterPD(query1, 9, 1, true);
        testParquetFilterPD(query2, 0, 1, true);
        testParquetFilterPD(query3, 22, 1, true);
        testParquetFilterPD(query4, 33, 2, true);
        testParquetFilterPD(query5, 49, 2, true);
        testParquetFilterPD(query6, 0, 1, true);
    }

    @Test
    public void testParquetFilterPDOptionsDisabled() throws Exception {
        try {
            BaseTestQuery.test("alter session set `%s` = false", PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_KEY);
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test(("create table `%s/t1` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and " + "date '1992-01-03'"), TestParquetFilterPushDown.CTAS_TABLE);
            BaseTestQuery.test(("create table `%s/t2` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-04' and " + "date '1992-01-06'"), TestParquetFilterPushDown.CTAS_TABLE);
            BaseTestQuery.test(("create table `%s/t3` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-07' and " + "date '1992-01-09'"), TestParquetFilterPushDown.CTAS_TABLE);
            final String query1 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate = date '1992-01-01'";
            testParquetFilterPD(query1, 9, 3, false);
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_KEY);
        }
    }

    @Test
    public void testParquetFilterPDOptionsThreshold() throws Exception {
        try {
            BaseTestQuery.test((("alter session set `" + (PlannerSettings.PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_THRESHOLD_KEY)) + "` = 2 "));
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test(("create table `%s/t1` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and " + "date '1992-01-03'"), TestParquetFilterPushDown.CTAS_TABLE);
            BaseTestQuery.test(("create table `%s/t2` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-04' and " + "date '1992-01-06'"), TestParquetFilterPushDown.CTAS_TABLE);
            BaseTestQuery.test(("create table `%s/t3` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-07' and " + "date '1992-01-09'"), TestParquetFilterPushDown.CTAS_TABLE);
            final String query1 = "select o_orderdate from dfs.tmp.order_ctas where o_orderdate = date '1992-01-01'";
            testParquetFilterPD(query1, 9, 3, false);
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_ROWGROUP_FILTER_PUSHDOWN_PLANNING_THRESHOLD_KEY);
        }
    }

    @Test
    public void testDatePredicateAgainstCorruptedDateCol() throws Exception {
        // Table dateTblCorrupted is created by CTAS in drill 1.8.0. Per DRILL-4203, the date column is shifted by some value.
        // The CTAS are the following, then copy to drill test resource directory.
        // create table dfs.tmp.`dateTblCorrupted/t1` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and date '1992-01-03';
        // create table dfs.tmp.`dateTblCorrupted/t2` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-04' and date '1992-01-06';
        // create table dfs.tmp.`dateTblCorrupted/t3` as select cast(o_orderdate as date) as o_orderdate from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-07' and date '1992-01-09';
        final String query1 = "select o_orderdate from dfs.`parquetFilterPush/dateTblCorrupted` where o_orderdate = date '1992-01-01'";
        testParquetFilterPD(query1, 9, 1, false);
        final String query2 = "select o_orderdate from dfs.`parquetFilterPush/dateTblCorrupted` where o_orderdate < date '1992-01-01'";
        testParquetFilterPD(query2, 0, 1, false);
        final String query3 = "select o_orderdate from dfs.`parquetFilterPush/dateTblCorrupted` where o_orderdate between date '1992-01-01' and date '1992-01-03'";
        testParquetFilterPD(query3, 22, 1, false);
        final String query4 = "select o_orderdate from dfs.`parquetFilterPush/dateTblCorrupted` where o_orderdate between date '1992-01-01' and date '1992-01-04'";
        testParquetFilterPD(query4, 33, 2, false);
        final String query5 = "select o_orderdate from dfs.`parquetFilterPush/dateTblCorrupted` where o_orderdate between date '1992-01-01' and date '1992-01-06'";
        testParquetFilterPD(query5, 49, 2, false);
        final String query6 = "select o_orderdate from dfs.`parquetFilterPush/dateTblCorrupted` where o_orderdate > date '1992-01-10'";
        testParquetFilterPD(query6, 0, 1, false);
    }

    @Test
    public void testTimeStampPredicate() throws Exception {
        // Table dateTblCorrupted is created by CTAS in drill 1.8.0.
        // create table dfs.tmp.`tsTbl/t1` as select DATE_ADD(cast(o_orderdate as date), INTERVAL '0 10:20:30' DAY TO SECOND) as o_ordertimestamp from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-01' and date '1992-01-03';
        // create table dfs.tmp.`tsTbl/t2` as select DATE_ADD(cast(o_orderdate as date), INTERVAL '0 10:20:30' DAY TO SECOND) as o_ordertimestamp from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-04' and date '1992-01-06';
        // create table dfs.tmp.`tsTbl/t3` as select DATE_ADD(cast(o_orderdate as date), INTERVAL '0 10:20:30' DAY TO SECOND) as o_ordertimestamp from cp.`tpch/orders.parquet` where o_orderdate between date '1992-01-07' and date '1992-01-09';
        final String query1 = "select o_ordertimestamp from dfs.`parquetFilterPush/tsTbl` where o_ordertimestamp = timestamp '1992-01-01 10:20:30'";
        testParquetFilterPD(query1, 9, 1, false);
        final String query2 = "select o_ordertimestamp from dfs.`parquetFilterPush/tsTbl` where o_ordertimestamp < timestamp '1992-01-01 10:20:30'";
        testParquetFilterPD(query2, 0, 1, false);
        final String query3 = "select o_ordertimestamp from dfs.`parquetFilterPush/tsTbl` where o_ordertimestamp between timestamp '1992-01-01 00:00:00' and timestamp '1992-01-06 10:20:30'";
        testParquetFilterPD(query3, 49, 2, false);
    }

    @Test
    public void testBooleanPredicate() throws Exception {
        // Table blnTbl was created by CTAS in drill 1.12.0 and consist of 4 files withe the next data:
        // File 0_0_0.parquet has col_bln column with the next values: true, true, true.
        // File 0_0_1.parquet has col_bln column with the next values: false, false, false.
        // File 0_0_2.parquet has col_bln column with the next values: true, null, false.
        // File 0_0_3.parquet has col_bln column with the next values: null, null, null.
        final String queryIsNull = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln is null";
        testParquetFilterPD(queryIsNull, 4, 2, false);
        final String queryIsNotNull = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln is not null";
        testParquetFilterPD(queryIsNotNull, 8, 3, false);
        final String queryIsTrue = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln is true";
        testParquetFilterPD(queryIsTrue, 4, 2, false);
        final String queryIsNotTrue = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln is not true";
        testParquetFilterPD(queryIsNotTrue, 8, 3, false);
        final String queryIsFalse = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln is false";
        testParquetFilterPD(queryIsFalse, 4, 2, false);
        final String queryIsNotFalse = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln is not false";
        testParquetFilterPD(queryIsNotFalse, 8, 3, false);
        final String queryEqualTrue = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln = true";
        testParquetFilterPD(queryEqualTrue, 4, 2, false);
        final String queryNotEqualTrue = "select col_bln from dfs.`parquetFilterPush/blnTbl` where not col_bln = true";
        testParquetFilterPD(queryNotEqualTrue, 4, 2, false);
        final String queryEqualFalse = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln = false";
        testParquetFilterPD(queryEqualFalse, 4, 2, false);
        final String queryNotEqualFalse = "select col_bln from dfs.`parquetFilterPush/blnTbl` where not col_bln = false";
        testParquetFilterPD(queryNotEqualFalse, 4, 2, false);
        final String queryEqualTrueWithAnd = "select col_bln from dfs.`parquetFilterPush/blnTbl` where col_bln = true and unk_col = 'a'";
        testParquetFilterPD(queryEqualTrueWithAnd, 0, 2, false);
        // File ff1.parquet has column with the values: false, null, false.
        // File tt1.parquet has column with the values: true, null, true.
        // File ft0.parquet has column with the values: false, true.
        final String query = "select a from dfs.`parquetFilterPush/tfTbl` where ";
        testParquetFilterPD((query + "a is true"), 3, 2, false);
        testParquetFilterPD((query + "a is false"), 3, 2, false);
        testParquetFilterPD((query + "a is not true"), 5, 1, false);
        testParquetFilterPD((query + "a is not false"), 5, 1, false);
    }

    // DRILL-5359
    @Test
    public void testFilterWithItemFlatten() throws Exception {
        final String sql = "select n_regionkey\n" + ((("from (select n_regionkey, \n" + "            flatten(nation.cities) as cities \n") + "      from cp.`tpch/nation.parquet` nation) as flattenedCities \n") + "where flattenedCities.cities.`zip` = '12345'");
        final String[] expectedPlan = new String[]{ "(?s)Filter.*Flatten" };
        final String[] excludedPlan = new String[]{  };
        PlanTestBase.testPlanMatchingPatterns(sql, expectedPlan, excludedPlan);
    }

    @Test
    public void testMultiRowGroup() throws Exception {
        // multirowgroup is a parquet file with 2 rowgroups inside. One with a = 1 and the other with a = 2;
        // FilterPushDown should be able to remove the rowgroup with a = 1 from the scan operator.
        final String sql = "select * from dfs.`parquet/multirowgroup.parquet` where a > 1";
        final String[] expectedPlan = new String[]{ "numRowGroups=1" };
        PlanTestBase.testPlanMatchingPatterns(sql, expectedPlan);
    }

    @Test
    public void testWithMissingStatistics() throws Exception {
        /* wide_string.parquet

        Schema:
        message root {
        optional binary col_str (UTF8);
        }

        Content:
        first row -> `a` character repeated 2050 times
        second row -> null
         */
        String tableName = "wide_string_table";
        Path wideStringFilePath = Paths.get("parquet", "wide_string.parquet");
        ExecTest.dirTestWatcher.copyResourceToRoot(wideStringFilePath, Paths.get(tableName, "0_0_0.parquet"));
        ExecTest.dirTestWatcher.copyResourceToRoot(wideStringFilePath, Paths.get(tableName, "0_0_1.parquet"));
        String query = String.format("select count(1) as cnt from dfs.`%s` where col_str is null", tableName);
        String[] expectedPlan = new String[]{ "numRowGroups=2" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt").baselineValues(2L).go();
    }

    // testing min=false, max=true, min/max set, no nulls
    @Test
    public void testMinFalseMaxTrue() throws Exception {
        LogicalExpression le = Mockito.mock(LogicalExpression.class);
        BooleanStatistics booleanStatistics = Mockito.mock(BooleanStatistics.class);
        Mockito.doReturn(booleanStatistics).when(le).accept(ArgumentMatchers.any(), ArgumentMatchers.any());
        RangeExprEvaluator<Boolean> re = Mockito.mock(RangeExprEvaluator.class);
        Mockito.when(re.getRowCount()).thenReturn(Long.valueOf(2));// 2 rows

        Mockito.when(booleanStatistics.isEmpty()).thenReturn(false);// stat is not empty

        Mockito.when(booleanStatistics.isNumNullsSet()).thenReturn(true);// num_nulls set

        Mockito.when(booleanStatistics.getNumNulls()).thenReturn(Long.valueOf(0));// no nulls

        Mockito.when(booleanStatistics.hasNonNullValue()).thenReturn(true);// min/max set

        Mockito.when(booleanStatistics.getMin()).thenReturn(false);// min false

        Mockito.when(booleanStatistics.getMax()).thenReturn(true);// max true

        ParquetIsPredicate isTrue = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_TRUE, le)));
        Assert.assertEquals(SOME, isTrue.matches(re));
        ParquetIsPredicate isFalse = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_FALSE, le)));
        Assert.assertEquals(SOME, isFalse.matches(re));
        ParquetIsPredicate isNotTrue = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_NOT_TRUE, le)));
        Assert.assertEquals(SOME, isNotTrue.matches(re));
        ParquetIsPredicate isNotFalse = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_NOT_FALSE, le)));
        Assert.assertEquals(SOME, isNotFalse.matches(re));
    }

    // testing min=false, max=false, min/max set, no nulls
    @Test
    public void testMinFalseMaxFalse() throws Exception {
        LogicalExpression le = Mockito.mock(LogicalExpression.class);
        BooleanStatistics booleanStatistics = Mockito.mock(BooleanStatistics.class);
        Mockito.doReturn(booleanStatistics).when(le).accept(ArgumentMatchers.any(), ArgumentMatchers.any());
        RangeExprEvaluator<Boolean> re = Mockito.mock(RangeExprEvaluator.class);
        Mockito.when(re.getRowCount()).thenReturn(Long.valueOf(2));// 2 rows

        Mockito.when(booleanStatistics.isEmpty()).thenReturn(false);// stat is not empty

        Mockito.when(booleanStatistics.isNumNullsSet()).thenReturn(true);// num_nulls set

        Mockito.when(booleanStatistics.getNumNulls()).thenReturn(Long.valueOf(0));// no nulls

        Mockito.when(booleanStatistics.hasNonNullValue()).thenReturn(true);// min/max set

        Mockito.when(booleanStatistics.getMin()).thenReturn(false);// min false

        Mockito.when(booleanStatistics.getMax()).thenReturn(false);// max false

        ParquetIsPredicate isTrue = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_TRUE, le)));
        Assert.assertEquals(NONE, isTrue.matches(re));
        ParquetIsPredicate isFalse = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_FALSE, le)));
        Assert.assertEquals(ALL, isFalse.matches(re));
        ParquetIsPredicate isNotTrue = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_NOT_TRUE, le)));
        Assert.assertEquals(ALL, isNotTrue.matches(re));
        ParquetIsPredicate isNotFalse = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_NOT_FALSE, le)));
        Assert.assertEquals(NONE, isNotFalse.matches(re));
    }

    // testing min=true, max=true, min/max set, no nulls
    @Test
    public void testMinTrueMaxTrue() throws Exception {
        LogicalExpression le = Mockito.mock(LogicalExpression.class);
        BooleanStatistics booleanStatistics = Mockito.mock(BooleanStatistics.class);
        Mockito.doReturn(booleanStatistics).when(le).accept(ArgumentMatchers.any(), ArgumentMatchers.any());
        RangeExprEvaluator<Boolean> re = Mockito.mock(RangeExprEvaluator.class);
        Mockito.when(re.getRowCount()).thenReturn(Long.valueOf(2));// 2 rows

        Mockito.when(booleanStatistics.isEmpty()).thenReturn(false);// stat is not empty

        Mockito.when(booleanStatistics.isNumNullsSet()).thenReturn(true);// num_nulls set

        Mockito.when(booleanStatistics.getNumNulls()).thenReturn(Long.valueOf(0));// no nulls

        Mockito.when(booleanStatistics.hasNonNullValue()).thenReturn(true);// min/max set

        Mockito.when(booleanStatistics.getMin()).thenReturn(true);// min false

        Mockito.when(booleanStatistics.getMax()).thenReturn(true);// max true

        ParquetIsPredicate isTrue = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_TRUE, le)));
        Assert.assertEquals(ALL, isTrue.matches(re));
        ParquetIsPredicate isFalse = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_FALSE, le)));
        Assert.assertEquals(NONE, isFalse.matches(re));
        ParquetIsPredicate isNotTrue = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_NOT_TRUE, le)));
        Assert.assertEquals(NONE, isNotTrue.matches(re));
        ParquetIsPredicate isNotFalse = ((ParquetIsPredicate) (ParquetIsPredicate.createIsPredicate(IS_NOT_FALSE, le)));
        Assert.assertEquals(ALL, isNotFalse.matches(re));
    }

    @Test
    public void testParquetSingleRowGroupFilterRemoving() throws Exception {
        BaseTestQuery.test("create table dfs.tmp.`singleRowGroupTable` as select * from cp.`tpch/nation.parquet`");
        String query = "select * from dfs.tmp.`singleRowGroupTable` where n_nationkey > -1";
        testParquetFilterPruning(query, 25, 1, new String[]{ "Filter\\(" });
    }
}

