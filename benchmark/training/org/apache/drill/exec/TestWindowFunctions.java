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
package org.apache.drill.exec;


import UserBitShared.DrillPBError.ErrorType;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.work.foreman.UnsupportedFunctionException;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ExecConstants.SLICE_TARGET_DEFAULT;


public class TestWindowFunctions extends BaseTestQuery {
    // DRILL-3196
    @Test
    public void testSinglePartition() throws Exception {
        final String query = "select sum(n_nationKey) over(partition by n_nationKey) as col1, count(*) over(partition by n_nationKey) as col2 \n" + "from cp.`tpch/nation.parquet`";
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{0\\} order by \\[\\].*\\[SUM\\(\\$0\\), COUNT\\(\\)", "Scan.*columns=\\[`n_nationKey`\\].*" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col1", "col2").baselineValues(0L, 1L).baselineValues(1L, 1L).baselineValues(2L, 1L).baselineValues(3L, 1L).baselineValues(4L, 1L).baselineValues(5L, 1L).baselineValues(6L, 1L).baselineValues(7L, 1L).baselineValues(8L, 1L).baselineValues(9L, 1L).baselineValues(10L, 1L).baselineValues(11L, 1L).baselineValues(12L, 1L).baselineValues(13L, 1L).baselineValues(14L, 1L).baselineValues(15L, 1L).baselineValues(16L, 1L).baselineValues(17L, 1L).baselineValues(18L, 1L).baselineValues(19L, 1L).baselineValues(20L, 1L).baselineValues(21L, 1L).baselineValues(22L, 1L).baselineValues(23L, 1L).baselineValues(24L, 1L).build().run();
    }

    // DRILL-3196
    @Test
    public void testSinglePartitionDefinedInWindowList() throws Exception {
        final String query = "select sum(n_nationKey) over w as col \n" + ("from cp.`tpch/nation.parquet` \n" + "window w as (partition by n_nationKey order by n_nationKey)");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{0\\} order by \\[0\\].*SUM\\(\\$0\\)", "Scan.*columns=\\[`n_nationKey`\\].*" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col").baselineValues(0L).baselineValues(1L).baselineValues(2L).baselineValues(3L).baselineValues(4L).baselineValues(5L).baselineValues(6L).baselineValues(7L).baselineValues(8L).baselineValues(9L).baselineValues(10L).baselineValues(11L).baselineValues(12L).baselineValues(13L).baselineValues(14L).baselineValues(15L).baselineValues(16L).baselineValues(17L).baselineValues(18L).baselineValues(19L).baselineValues(20L).baselineValues(21L).baselineValues(22L).baselineValues(23L).baselineValues(24L).build().run();
    }

    // DRILL-3182
    @Test(expected = UnsupportedFunctionException.class)
    public void testWindowFunctionWithDistinct() throws Exception {
        try {
            final String query = "explain plan for select a2, count(distinct b2) over(partition by a2) \n" + "from cp.`tpch/nation.parquet`";
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            TestWindowFunctions.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3188
    @Test(expected = UnsupportedFunctionException.class)
    public void testWindowFrame() throws Exception {
        try {
            final String query = "select a2, sum(a2) over(partition by a2 order by a2 rows between 1 preceding and 1 following ) \n" + ("from cp.`tpch/nation.parquet` t \n" + "order by a2");
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            TestWindowFunctions.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3326
    @Test(expected = UnsupportedFunctionException.class)
    public void testWindowWithAlias() throws Exception {
        try {
            String query = "explain plan for SELECT sum(n_nationkey) OVER (PARTITION BY n_name ORDER BY n_name ROWS BETWEEN CURRENT ROW AND 1 FOLLOWING) as col2 \n" + "from cp.`tpch/nation.parquet`";
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            TestWindowFunctions.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3189
    @Test(expected = UnsupportedFunctionException.class)
    public void testWindowWithAllowDisallow() throws Exception {
        try {
            final String query = "select sum(n_nationKey) over(partition by n_nationKey \n" + (("rows between unbounded preceding and unbounded following disallow partial) \n" + "from cp.`tpch/nation.parquet` \n") + "order by n_nationKey");
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            TestWindowFunctions.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3360
    @Test
    public void testWindowInWindow() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
        String query = "select rank() over(order by row_number() over(order by n_nationkey)) \n" + "from cp.`tpch/nation.parquet`";
        BaseTestQuery.test(query);
    }

    // DRILL-3280
    @Test
    public void testMissingOverWithWindowClause() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
        String query = "select rank(), cume_dist() over w \n" + ("from cp.`tpch/nation.parquet` \n" + "window w as (partition by n_name order by n_nationkey)");
        BaseTestQuery.test(query);
    }

    // DRILL-3601
    @Test
    public void testLeadMissingOver() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
        String query = "select lead(n_nationkey) from cp.`tpch/nation.parquet`";
        BaseTestQuery.test(query);
    }

    // DRILL-3649
    @Test
    public void testMissingOverWithConstant() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
        String query = "select NTILE(1) from cp.`tpch/nation.parquet`";
        BaseTestQuery.test(query);
    }

    // DRILL-3344
    @Test
    public void testWindowGroupBy() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
        String query = "explain plan for SELECT max(n_nationkey) OVER (), n_name as col2 \n" + ("from cp.`tpch/nation.parquet` \n" + "group by n_name");
        BaseTestQuery.test(query);
    }

    // DRILL-3346
    @Test
    @Category(UnlikelyTest.class)
    public void testWindowGroupByOnView() throws Exception {
        try {
            thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION));
            String createView = "create view testWindowGroupByOnView(a, b) as \n" + "select n_nationkey, n_name from cp.`tpch/nation.parquet`";
            String query = "explain plan for SELECT max(a) OVER (), b as col2 \n" + ("from testWindowGroupByOnView \n" + "group by b");
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test(createView);
            BaseTestQuery.test(query);
        } finally {
            BaseTestQuery.test("drop view testWindowGroupByOnView");
        }
    }

    // DRILL-3188
    @Test
    public void testWindowFrameEquivalentToDefault() throws Exception {
        final String query1 = "select sum(n_nationKey) over(partition by n_nationKey order by n_nationKey) as col\n" + ("from cp.`tpch/nation.parquet` t \n" + "order by n_nationKey");
        final String query2 = "select sum(n_nationKey) over(partition by n_nationKey order by n_nationKey \n" + (("range between unbounded preceding and current row) as col \n" + "from cp.`tpch/nation.parquet` t \n") + "order by n_nationKey");
        final String query3 = "select sum(n_nationKey) over(partition by n_nationKey \n" + (("rows BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) as col \n" + "from cp.`tpch/nation.parquet` t \n") + "order by n_nationKey");
        // Validate the plan
        final String[] expectedPlan1 = new String[]{ "Window.*partition \\{0\\} order by \\[0\\].*SUM\\(\\$0\\)", "Scan.*columns=\\[`n_nationKey`\\].*" };
        final String[] excludedPatterns1 = new String[]{ "Scan.*columns=\\[`\\*`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query1, expectedPlan1, excludedPatterns1);
        BaseTestQuery.testBuilder().sqlQuery(query1).unOrdered().baselineColumns("col").baselineValues(0L).baselineValues(1L).baselineValues(2L).baselineValues(3L).baselineValues(4L).baselineValues(5L).baselineValues(6L).baselineValues(7L).baselineValues(8L).baselineValues(9L).baselineValues(10L).baselineValues(11L).baselineValues(12L).baselineValues(13L).baselineValues(14L).baselineValues(15L).baselineValues(16L).baselineValues(17L).baselineValues(18L).baselineValues(19L).baselineValues(20L).baselineValues(21L).baselineValues(22L).baselineValues(23L).baselineValues(24L).build().run();
        final String[] expectedPlan2 = new String[]{ "Window.*partition \\{0\\} order by \\[0\\].*SUM\\(\\$0\\)", "Scan.*columns=\\[`n_nationKey`\\].*" };
        final String[] excludedPatterns2 = new String[]{ "Scan.*columns=\\[`\\*`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query2, expectedPlan2, excludedPatterns2);
        BaseTestQuery.testBuilder().sqlQuery(query2).unOrdered().baselineColumns("col").baselineValues(0L).baselineValues(1L).baselineValues(2L).baselineValues(3L).baselineValues(4L).baselineValues(5L).baselineValues(6L).baselineValues(7L).baselineValues(8L).baselineValues(9L).baselineValues(10L).baselineValues(11L).baselineValues(12L).baselineValues(13L).baselineValues(14L).baselineValues(15L).baselineValues(16L).baselineValues(17L).baselineValues(18L).baselineValues(19L).baselineValues(20L).baselineValues(21L).baselineValues(22L).baselineValues(23L).baselineValues(24L).build().run();
        final String[] expectedPlan3 = new String[]{ "Window.*partition \\{0\\}.*SUM\\(\\$0\\)", "Scan.*columns=\\[`n_nationKey`\\].*" };
        final String[] excludedPatterns3 = new String[]{ "Scan.*columns=\\[`\\*`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query3, expectedPlan3, excludedPatterns3);
        BaseTestQuery.testBuilder().sqlQuery(query3).unOrdered().baselineColumns("col").baselineValues(0L).baselineValues(1L).baselineValues(2L).baselineValues(3L).baselineValues(4L).baselineValues(5L).baselineValues(6L).baselineValues(7L).baselineValues(8L).baselineValues(9L).baselineValues(10L).baselineValues(11L).baselineValues(12L).baselineValues(13L).baselineValues(14L).baselineValues(15L).baselineValues(16L).baselineValues(17L).baselineValues(18L).baselineValues(19L).baselineValues(20L).baselineValues(21L).baselineValues(22L).baselineValues(23L).baselineValues(24L).build().run();
    }

    // DRILL-3204
    @Test
    public void testWindowWithJoin() throws Exception {
        final String query = "select sum(t1.r_regionKey) over(partition by t1.r_regionKey) as col \n" + (("from cp.`tpch/region.parquet` t1, cp.`tpch/nation.parquet` t2 \n" + "where t1.r_regionKey = t2.n_nationKey \n") + "group by t1.r_regionKey");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{0\\}.*SUM\\(\\$0\\)", "Scan.*columns=\\[`n_nationKey`\\].*", "Scan.*columns=\\[`n_nationKey`\\].*" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col").baselineValues(0L).baselineValues(1L).baselineValues(2L).baselineValues(3L).baselineValues(4L).build().run();
    }

    // DRILL-3298
    @Test
    public void testCountEmptyPartitionByWithExchange() throws Exception {
        String query = "select count(*) over (order by o_orderpriority) as cnt from dfs.`multilevel/parquet` where o_custkey < 100";
        try {
            // Validate the plan
            final String[] expectedPlan = new String[]{ "Window.*partition \\{\\} order by \\[0\\].*COUNT\\(\\)", "Scan.*columns=\\[`o_custkey`, `o_orderpriority`\\]" };
            final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
            PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
            BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("cnt").optionSettingQueriesForTestQuery("alter session set `planner.slice_target` = 1").baselineValues(1L).baselineValues(4L).baselineValues(4L).baselineValues(4L).build().run();
        } finally {
            BaseTestQuery.test(("alter session set `planner.slice_target` = " + (SLICE_TARGET_DEFAULT)));
        }
    }

    /* Verify the output of aggregate functions (which are reduced
    eg: avg(x) = sum(x)/count(x)) return results of the correct
    data type (double)
     */
    @Test
    public void testAvgVarianceWindowFunctions() throws Exception {
        final String avgQuery = "select avg(n_nationkey) over (partition by n_nationkey) col1 " + ("from cp.`tpch/nation.parquet` " + "where n_nationkey = 1");
        // Validate the plan
        final String[] expectedPlan1 = new String[]{ "Window.*partition \\{0\\} order by \\[\\].*SUM\\(\\$0\\), COUNT\\(\\$0\\)", "Scan.*columns=\\[`n_nationkey`\\]" };
        final String[] excludedPatterns1 = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(avgQuery, expectedPlan1, excludedPatterns1);
        BaseTestQuery.testBuilder().sqlQuery(avgQuery).unOrdered().baselineColumns("col1").baselineValues(1.0).go();
        final String varianceQuery = "select var_pop(n_nationkey) over (partition by n_nationkey) col1 " + ("from cp.`tpch/nation.parquet` " + "where n_nationkey = 1");
        // Validate the plan
        final String[] expectedPlan2 = new String[]{ "Window.*partition \\{0\\} order by \\[\\].*SUM\\(\\$2\\), SUM\\(\\$1\\), COUNT\\(\\$1\\)", "Scan.*columns=\\[`n_nationkey`\\]" };
        final String[] excludedPatterns2 = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(varianceQuery, expectedPlan2, excludedPatterns2);
        BaseTestQuery.testBuilder().sqlQuery(varianceQuery).unOrdered().baselineColumns("col1").baselineValues(0.0).go();
    }

    @Test
    public void testWindowFunctionWithKnownType() throws Exception {
        final String query = "select sum(cast(col_int as int)) over (partition by col_varchar) as col1 " + "from cp.`jsoninput/large_int.json` limit 1";
        // Validate the plan
        final String[] expectedPlan1 = new String[]{ "Window.*partition \\{0\\} order by \\[\\].*SUM\\(\\$1\\)", "Scan.*columns=\\[`col_varchar`, `col_int`\\]" };
        final String[] excludedPatterns1 = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan1, excludedPatterns1);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col1").baselineValues(2147483649L).go();
        final String avgQuery = "select avg(cast(col_int as int)) over (partition by col_varchar) as col1 " + "from cp.`jsoninput/large_int.json` limit 1";
        // Validate the plan
        final String[] expectedPlan2 = new String[]{ "Window.*partition \\{0\\} order by \\[\\].*SUM\\(\\$1\\), COUNT\\(\\$1\\)", "Scan.*columns=\\[`col_varchar`, `col_int`\\]" };
        final String[] excludedPatterns2 = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(avgQuery, expectedPlan2, excludedPatterns2);
        BaseTestQuery.testBuilder().sqlQuery(avgQuery).unOrdered().baselineColumns("col1").baselineValues(1.0737418245E9).go();
    }

    @Test
    public void testCompoundIdentifierInWindowDefinition() throws Exception {
        String root = "/multilevel/csv/1994/Q1/orders_94_q1.csv";
        String query = String.format(("SELECT count(*) OVER w as col1, count(*) OVER w as col2 \n" + ("FROM cp.`%s` \n" + "WINDOW w AS (PARTITION BY columns[1] ORDER BY columns[0] DESC)")), root);
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{1\\} order by \\[0 DESC\\].*COUNT\\(\\)", "Scan.*columns=\\[`columns`\\[0\\], `columns`\\[1\\]\\]" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        // Validate the result
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("col1", "col2").baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).baselineValues(((long) (1)), ((long) (1))).build().run();
    }

    @Test
    @Category(UnlikelyTest.class)
    public void testRankWithGroupBy() throws Exception {
        final String query = "select dense_rank() over (order by l_suppkey) as rank1 " + " from cp.`tpch/lineitem.parquet` group by l_partkey, l_suppkey order by 1 desc limit 1";
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{\\} order by \\[1\\].*DENSE_RANK\\(\\)", "Scan.*columns=\\[`l_partkey`, `l_suppkey`\\]" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("rank1").baselineValues(100L).go();
    }

    // DRILL-3404
    @Test
    public void testWindowSumAggIsNotNull() throws Exception {
        String query = "select count(*) cnt from (select sum ( c1 ) over ( partition by c2 order by c1 asc nulls first ) w_sum from dfs.`window/table_with_nulls.parquet` ) sub_query where w_sum is not null";
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{1\\} order by \\[0 ASC-nulls-first\\].*SUM\\(\\$0\\)", "Scan.*columns=\\[`c1`, `c2`\\]" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("cnt").baselineValues(26L).build().run();
    }

    // DRILL-3292
    @Test
    public void testWindowConstants() throws Exception {
        String query = "select rank() over w fn, sum(2) over w sumINTEGER, sum(employee_id) over w sumEmpId,\n" + ((("sum(cast(0.5 as double)) over w sumFLOAT \n" + "from cp.`employee.json` \n") + "where position_id = 2 \n") + "window w as(partition by position_id order by employee_id)");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{0\\} order by \\[1\\].*RANK\\(\\), \\$SUM0\\(\\$2\\), SUM\\(\\$1\\), \\$SUM0\\(\\$3\\)", "Scan.*columns=\\[`position_id`, `employee_id`\\]" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("fn", "sumINTEGER", "sumEmpId", "sumFLOAT").baselineValues(1L, 2L, 2L, 0.5).baselineValues(2L, 4L, 6L, 1.0).baselineValues(3L, 6L, 11L, 1.5).baselineValues(4L, 8L, 31L, 2.0).baselineValues(5L, 10L, 52L, 2.5).baselineValues(6L, 12L, 74L, 3.0).build().run();
    }

    // DRILL-3567
    @Test
    public void testMultiplePartitions1() throws Exception {
        String root = "/store/text/data/t.json";
        String query = String.format(("select count(*) over(partition by b1 order by c1) as count1, \n" + (("sum(a1)  over(partition by b1 order by c1) as sum1, \n" + "count(*) over(partition by a1 order by c1) as count2 \n") + "from cp.`%s`")), root);
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{2\\} order by \\[1\\].*COUNT\\(\\)", "Window.*partition \\{0\\} order by \\[1\\].*COUNT\\(\\), SUM\\(\\$2\\)", "Scan.*columns=\\[`b1`, `c1`, `a1`\\]" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("count1", "sum1", "count2").baselineValues(1L, 0L, 2L).baselineValues(1L, 0L, 2L).baselineValues(2L, 0L, 5L).baselineValues(3L, 0L, 5L).baselineValues(3L, 0L, 5L).baselineValues(1L, 10L, 2L).baselineValues(1L, 10L, 2L).baselineValues(2L, 20L, 5L).baselineValues(3L, 30L, 5L).baselineValues(3L, 30L, 5L).build().run();
    }

    // DRILL-3567
    @Test
    public void testMultiplePartitions2() throws Exception {
        String root = "/store/text/data/t.json";
        String query = String.format(("select count(*) over(partition by b1 order by c1) as count1, \n" + (("count(*) over(partition by a1 order by c1) as count2, \n" + "sum(a1)  over(partition by b1 order by c1) as sum1 \n") + "from cp.`%s`")), root);
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{2\\} order by \\[1\\].*COUNT\\(\\)", "Window.*partition \\{0\\} order by \\[1\\].*COUNT\\(\\), SUM\\(\\$2\\)", "Scan.*columns=\\[`b1`, `c1`, `a1`\\]" };
        final String[] excludedPatterns = new String[]{ "Scan.*columns=\\[`\\*`\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("count1", "count2", "sum1").baselineValues(1L, 2L, 0L).baselineValues(1L, 2L, 0L).baselineValues(2L, 5L, 0L).baselineValues(3L, 5L, 0L).baselineValues(3L, 5L, 0L).baselineValues(1L, 2L, 10L).baselineValues(1L, 2L, 10L).baselineValues(2L, 5L, 20L).baselineValues(3L, 5L, 30L).baselineValues(3L, 5L, 30L).build().run();
    }

    // see DRILL-3574
    @Test
    public void testWithAndWithoutPartitions() throws Exception {
        String root = "/store/text/data/t.json";
        String query = String.format(("select sum(a1) over(partition by b1, c1) as s1, sum(a1) over() as s2 \n" + ("from cp.`%s` \n" + "order by a1")), root);
        BaseTestQuery.test("alter session set `planner.slice_target` = 1");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window\\(window#0=\\[window\\(partition \\{\\}.*\n" + ".*UnionExchange" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("s1", "s2").baselineValues(0L, 50L).baselineValues(0L, 50L).baselineValues(0L, 50L).baselineValues(0L, 50L).baselineValues(0L, 50L).baselineValues(10L, 50L).baselineValues(10L, 50L).baselineValues(10L, 50L).baselineValues(20L, 50L).baselineValues(20L, 50L).build().run();
    }

    // see DRILL-3657
    @Test
    public void testConstantsInMultiplePartitions() throws Exception {
        String root = "/store/text/data/t.json";
        String query = String.format(("select sum(1) over(partition by b1 order by a1) as sum1, sum(1) over(partition by a1) as sum2, rank() over(order by b1) as rank1, rank() over(order by 1) as rank2 \n" + ("from cp.`%s` \n" + "order by 1, 2, 3, 4")), root);
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*\\$SUM0\\(\\$3\\).*\n" + ((".*SelectionVectorRemover.*\n" + ".*Sort.*\n") + ".*Window.*\\$SUM0\\(\\$2\\).*") };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("sum1", "sum2", "rank1", "rank2").baselineValues(2L, 5L, 1L, 1L).baselineValues(2L, 5L, 1L, 1L).baselineValues(2L, 5L, 6L, 1L).baselineValues(2L, 5L, 6L, 1L).baselineValues(3L, 5L, 3L, 1L).baselineValues(3L, 5L, 3L, 1L).baselineValues(3L, 5L, 3L, 1L).baselineValues(3L, 5L, 8L, 1L).baselineValues(3L, 5L, 8L, 1L).baselineValues(3L, 5L, 8L, 1L).build().run();
    }

    // DRILL-3580
    @Test
    public void testExpressionInWindowFunction() throws Exception {
        String root = "/store/text/data/t.json";
        String query = String.format(("select a1, b1, sum(b1) over (partition by a1) as c1, sum(a1 + b1) over (partition by a1) as c2\n" + "from cp.`%s`"), root);
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window\\(window#0=\\[window\\(partition \\{0\\} order by \\[\\].*\\[SUM\\(\\$1\\), SUM\\(\\$2\\)\\]" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("a1", "b1", "c1", "c2").baselineValues(0L, 1L, 8L, 8L).baselineValues(0L, 1L, 8L, 8L).baselineValues(0L, 2L, 8L, 8L).baselineValues(0L, 2L, 8L, 8L).baselineValues(0L, 2L, 8L, 8L).baselineValues(10L, 3L, 21L, 71L).baselineValues(10L, 3L, 21L, 71L).baselineValues(10L, 5L, 21L, 71L).baselineValues(10L, 5L, 21L, 71L).baselineValues(10L, 5L, 21L, 71L).build().run();
    }

    // see DRILL-3657
    @Test
    public void testProjectPushPastWindow() throws Exception {
        String query = "select sum(n_nationkey) over(partition by 1 order by 1) as col1, \n" + (("count(n_nationkey) over(partition by 1 order by 1) as col2 \n" + "from cp.`tpch/nation.parquet` \n") + "limit 5");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Scan.*columns=\\[`n_nationkey`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col1", "col2").baselineValues(300L, 25L).baselineValues(300L, 25L).baselineValues(300L, 25L).baselineValues(300L, 25L).baselineValues(300L, 25L).build().run();
    }

    // DRILL-3679, DRILL-3680
    @Test
    @Category(UnlikelyTest.class)
    public void testWindowFunInNestSubQ() throws Exception {
        final String query = " select n_nationkey , n_regionkey , " + (((("        lead(n_regionkey) OVER ( PARTITION BY n_regionkey ORDER BY n_nationkey) lead_c2 " + " FROM (SELECT n_nationkey ,n_regionkey, ") + "          ntile(3) over(PARTITION BY n_regionkey ORDER BY n_nationkey) ") + "       FROM cp.`tpch/nation.parquet`) ") + " order by n_regionkey, n_nationkey");
        BaseTestQuery.test(query);
        final String baselineQuery = "select n_nationkey , n_regionkey , " + (("       lead(n_regionkey) OVER ( PARTITION BY n_regionkey ORDER BY n_nationkey) lead_c2 " + "FROM cp.`tpch/nation.parquet`   ") + "order by n_regionkey, n_nationkey");
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().sqlBaselineQuery(baselineQuery).build().run();
        final String query2 = " select rnum, position_id, " + ((("   ntile(4) over(order by position_id) " + " from (select position_id, row_number() ") + "       over(order by position_id) as rnum ") + "       from cp.`employee.json`)");
        final String baselineQuery2 = " select row_number() over(order by position_id) as rnum, " + (("    position_id, " + "    ntile(4) over(order by position_id) ") + " from cp.`employee.json`");
        BaseTestQuery.testBuilder().sqlQuery(query2).ordered().sqlBaselineQuery(baselineQuery2).build().run();
    }

    @Test
    public void testStatisticalWindowFunctions() throws Exception {
        final String sqlWindowFunctionQuery = "select " + (((("stddev_samp(employee_id) over (partition by 1) c1, " + "stddev_pop(employee_id) over (partition by 1) c2, ") + "var_samp(employee_id) over (partition by 1) c3, ") + "var_pop(employee_id) over (partition by 1) c4 from ") + "cp.`employee.json` limit 1");
        BaseTestQuery.testBuilder().sqlQuery(sqlWindowFunctionQuery).unOrdered().baselineColumns("c1", "c2", "c3", "c4").baselineValues(333.56708470261117, 333.4226520980038, 111266.99999699896, 111170.66493206649).build().run();
    }

    // DRILL-2330
    @Test
    @Category(UnlikelyTest.class)
    public void testNestedAggregates() throws Exception {
        final String query = "select sum(min(l_extendedprice))" + (" over (partition by l_suppkey order by l_suppkey) as totprice" + " from cp.`tpch/lineitem.parquet` where l_suppkey <= 10 group by l_suppkey order by 1 desc");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Window.*partition \\{0\\} order by \\[0\\].*SUM\\(\\$1\\).*", "HashAgg\\(group=\\[\\{0\\}\\].*\\[MIN\\(\\$1\\)\\]\\)" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        // Validate the results
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("totprice").baselineValues(1107.2).baselineValues(998.09).baselineValues(957.05).baselineValues(953.05).baselineValues(931.03).baselineValues(926.02).baselineValues(909.0).baselineValues(906.0).baselineValues(904.0).baselineValues(904.0).go();
    }

    // DRILL-4795, DRILL-4796
    @Test
    public void testNestedAggregates1() throws Exception {
        try {
            String query = "select sum(min(l_extendedprice)) over (partition by l_suppkey)\n" + " from cp.`tpch/nation.parquet` where l_suppkey <= 10";
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            assert ex.getMessage().contains("Expression 'l_suppkey' is not being grouped");
        }
        try {
            String query = "select sum(min(l_extendedprice)) over (partition by l_suppkey) as totprice\n" + " from cp.`tpch/nation.parquet` where l_suppkey <= 10";
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            assert ex.getMessage().contains("Expression 'l_suppkey' is not being grouped");
        }
        try {
            String query = "select sum(min(l_extendedprice)) over w1 as totprice\n" + (" from cp.`tpch/nation.parquet` where l_suppkey <= 10\n" + " window w1 as (partition by l_suppkey)");
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            assert ex.getMessage().contains("Expression 'l_suppkey' is not being grouped");
        }
        try {
            String query = "select sum(min(l_extendedprice)) over (partition by n_nationkey)\n" + " from cp.`tpch/nation.parquet` where l_suppkey <= 10 group by l_suppkey";
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            assert ex.getMessage().contains("Expression 'n_nationkey' is not being grouped");
        }
        try {
            String query = "select sum(min(l_extendedprice)) over (partition by n_nationkey) as totprice\n" + " from cp.`tpch/nation.parquet` where l_suppkey <= 10 group by l_suppkey";
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            assert ex.getMessage().contains("Expression 'n_nationkey' is not being grouped");
        }
        try {
            String query = "select sum(min(l_extendedprice)) over w2 as totprice\n" + (" from cp.`tpch/nation.parquet` where l_suppkey <= 10 group by l_suppkey\n" + " window w2 as (partition by n_nationkey)");
            BaseTestQuery.test(query);
        } catch (UserException ex) {
            assert ex.getMessage().contains("Expression 'n_nationkey' is not being grouped");
        }
    }

    // DRILL-4469
    @Test
    public void testWindowOnSubqueryWithStar() throws Exception {
        String query = "SELECT SUM(n_nationkey) OVER w as s\n" + (("FROM (SELECT * FROM cp.`tpch/nation.parquet`) subQry\n" + "WINDOW w AS (PARTITION BY region ORDER BY n_nationkey)\n") + "limit 1");
        final String[] expectedPlan = new String[]{ "Scan.*columns=\\[`n_nationkey`, `region`\\].*" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("s").baselineValues(0L).build().run();
    }
}

