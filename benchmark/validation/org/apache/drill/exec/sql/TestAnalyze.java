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
package org.apache.drill.exec.sql;


import org.apache.drill.PlanTestBase;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;


public class TestAnalyze extends BaseTestQuery {
    // Analyze for all columns
    @Test
    public void basic1() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.region_basic1 AS SELECT * from cp.`region.json`");
            BaseTestQuery.test("ANALYZE TABLE dfs.tmp.region_basic1 COMPUTE STATISTICS");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`region_basic1/.stats.drill`");
            BaseTestQuery.test(("create table dfs.tmp.flatstats1 as select flatten(`directories`[0].`columns`) as `columns`" + " from dfs.tmp.`region_basic1/.stats.drill`"));
            BaseTestQuery.testBuilder().sqlQuery(("SELECT tbl.`columns`.`column` as `column`, tbl.`columns`.rowcount as rowcount," + ((" tbl.`columns`.nonnullrowcount as nonnullrowcount, tbl.`columns`.ndv as ndv," + " tbl.`columns`.avgwidth as avgwidth") + " FROM dfs.tmp.flatstats1 tbl"))).unOrdered().baselineColumns("column", "rowcount", "nonnullrowcount", "ndv", "avgwidth").baselineValues("`region_id`", 110.0, 110.0, 110L, 8.0).baselineValues("`sales_city`", 110.0, 110.0, 109L, 8.663636363636364).baselineValues("`sales_state_province`", 110.0, 110.0, 13L, 2.4272727272727272).baselineValues("`sales_district`", 110.0, 110.0, 23L, 9.318181818181818).baselineValues("`sales_region`", 110.0, 110.0, 8L, 10.8).baselineValues("`sales_country`", 110.0, 110.0, 4L, 3.909090909090909).baselineValues("`sales_district_id`", 110.0, 110.0, 23L, 8.0).go();
        } finally {
            BaseTestQuery.test(("ALTER SESSION SET `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    // Analyze for only a subset of the columns in table
    @Test
    public void basic2() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.employee_basic2 AS SELECT * from cp.`employee.json`");
            BaseTestQuery.test("ANALYZE TABLE dfs.tmp.employee_basic2 COMPUTE STATISTICS (employee_id, birth_date)");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`employee_basic2/.stats.drill`");
            BaseTestQuery.test(("create table dfs.tmp.flatstats2 as select flatten(`directories`[0].`columns`) as `columns`" + " from dfs.tmp.`employee_basic2/.stats.drill`"));
            BaseTestQuery.testBuilder().sqlQuery(("SELECT tbl.`columns`.`column` as `column`, tbl.`columns`.rowcount as rowcount," + ((" tbl.`columns`.nonnullrowcount as nonnullrowcount, tbl.`columns`.ndv as ndv," + " tbl.`columns`.avgwidth as avgwidth") + " FROM dfs.tmp.flatstats2 tbl"))).unOrdered().baselineColumns("column", "rowcount", "nonnullrowcount", "ndv", "avgwidth").baselineValues("`employee_id`", 1155.0, 1155.0, 1155L, 8.0).baselineValues("`birth_date`", 1155.0, 1155.0, 52L, 10.0).go();
        } finally {
            BaseTestQuery.test(("ALTER SESSION SET `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    // Analyze with sampling percentage
    @Test
    public void basic3() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
            BaseTestQuery.test("ALTER SESSION SET `exec.statistics.deterministic_sampling` = true");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.employee_basic3 AS SELECT * from cp.`employee.json`");
            BaseTestQuery.test("ANALYZE TABLE dfs.tmp.employee_basic3 COMPUTE STATISTICS (employee_id, birth_date) SAMPLE 55 PERCENT");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`employee_basic3/.stats.drill`");
            BaseTestQuery.test(("create table dfs.tmp.flatstats3 as select flatten(`directories`[0].`columns`) as `columns`" + " from dfs.tmp.`employee_basic3/.stats.drill`"));
            BaseTestQuery.testBuilder().sqlQuery(("SELECT tbl.`columns`.`column` as `column`, tbl.`columns`.rowcount as rowcount," + ((" tbl.`columns`.nonnullrowcount as nonnullrowcount, tbl.`columns`.ndv as ndv," + " tbl.`columns`.avgwidth as avgwidth") + " FROM dfs.tmp.flatstats3 tbl"))).unOrdered().baselineColumns("column", "rowcount", "nonnullrowcount", "ndv", "avgwidth").baselineValues("`employee_id`", 1138.0, 1138.0, 1138L, 8.00127815945039).baselineValues("`birth_date`", 1138.0, 1138.0, 38L, 10.001597699312988).go();
        } finally {
            BaseTestQuery.test("ALTER SESSION SET `exec.statistics.deterministic_sampling` = false");
            BaseTestQuery.test(("ALTER SESSION SET `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    @Test
    public void join() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.lineitem AS SELECT * FROM cp.`tpch/lineitem.parquet`");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.orders AS select * FROM cp.`tpch/orders.parquet`");
            BaseTestQuery.test("ANALYZE TABLE dfs.tmp.lineitem COMPUTE STATISTICS");
            BaseTestQuery.test("ANALYZE TABLE dfs.tmp.orders COMPUTE STATISTICS");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`lineitem/.stats.drill`");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`orders/.stats.drill`");
            BaseTestQuery.test("ALTER SESSION SET `planner.statistics.use` = true");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`lineitem` l JOIN dfs.tmp.`orders` o ON l.l_orderkey = o.o_orderkey");
        } finally {
            BaseTestQuery.test(("ALTER SESSION SET `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    @Test
    public void testAnalyzeSupportedFormats() throws Exception {
        // Only allow computing statistics on PARQUET files.
        try {
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'json'");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.employee_basic4 AS SELECT * from cp.`employee.json`");
            // Should display not supported
            verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.employee_basic4 COMPUTE STATISTICS", ("Table employee_basic4 is not supported by ANALYZE. " + "Support is currently limited to directory-based Parquet tables."));
            BaseTestQuery.test("DROP TABLE dfs.tmp.employee_basic4");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.employee_basic4 AS SELECT * from cp.`employee.json`");
            // Should complete successfully (16 columns in employee.json)
            verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.employee_basic4 COMPUTE STATISTICS", "16");
        } finally {
            BaseTestQuery.test(("ALTER SESSION SET `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    @Test
    public void testAnalyzePartitionedTables() throws Exception {
        // Computing statistics on columns, dir0, dir1
        try {
            final String tmpLocation = "/multilevel/parquet";
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
            BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
            BaseTestQuery.test("CREATE TABLE dfs.tmp.parquet1 AS SELECT * from dfs.`%s`", tmpLocation);
            verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.parquet1 COMPUTE STATISTICS", "11");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`parquet1/.stats.drill`");
            BaseTestQuery.test(("create table dfs.tmp.flatstats4 as select flatten(`directories`[0].`columns`) as `columns` " + "from dfs.tmp.`parquet1/.stats.drill`"));
            // Verify statistics
            BaseTestQuery.testBuilder().sqlQuery(("SELECT tbl.`columns`.`column` as `column`, tbl.`columns`.rowcount as rowcount," + ((" tbl.`columns`.nonnullrowcount as nonnullrowcount, tbl.`columns`.ndv as ndv," + " tbl.`columns`.avgwidth as avgwidth") + " FROM dfs.tmp.flatstats4 tbl"))).unOrdered().baselineColumns("column", "rowcount", "nonnullrowcount", "ndv", "avgwidth").baselineValues("`o_orderkey`", 120.0, 120.0, 119L, 4.0).baselineValues("`o_custkey`", 120.0, 120.0, 113L, 4.0).baselineValues("`o_orderstatus`", 120.0, 120.0, 3L, 1.0).baselineValues("`o_totalprice`", 120.0, 120.0, 120L, 8.0).baselineValues("`o_orderdate`", 120.0, 120.0, 111L, 4.0).baselineValues("`o_orderpriority`", 120.0, 120.0, 5L, 8.458333333333334).baselineValues("`o_clerk`", 120.0, 120.0, 114L, 15.0).baselineValues("`o_shippriority`", 120.0, 120.0, 1L, 4.0).baselineValues("`o_comment`", 120.0, 120.0, 120L, 46.333333333333336).baselineValues("`dir0`", 120.0, 120.0, 3L, 4.0).baselineValues("`dir1`", 120.0, 120.0, 4L, 2.0).go();
        } finally {
            BaseTestQuery.test(("ALTER SESSION SET `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    @Test
    public void testStaleness() throws Exception {
        // copy the data into the temporary location
        final String tmpLocation = "/multilevel/parquet";
        BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
        BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
        BaseTestQuery.test(("CREATE TABLE dfs.tmp.parquetStale AS SELECT o_orderkey, o_custkey, o_orderstatus, " + "o_totalprice, o_orderdate, o_orderpriority, o_clerk, o_shippriority, o_comment from dfs.`%s`"), tmpLocation);
        verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.parquetStale COMPUTE STATISTICS", "9");
        verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.parquetStale COMPUTE STATISTICS", "Table parquetStale has not changed since last ANALYZE!");
        // Verify we recompute statistics once a new file/directory is added. Update the directory some
        // time after ANALYZE so that the timestamps are different.
        Thread.sleep(1000);
        final String Q4 = "/multilevel/parquet/1996/Q4";
        BaseTestQuery.test(("CREATE TABLE dfs.tmp.`parquetStale/1996/Q5` AS SELECT o_orderkey, o_custkey, o_orderstatus, " + "o_totalprice, o_orderdate, o_orderpriority, o_clerk, o_shippriority, o_comment from dfs.`%s`"), Q4);
        verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.parquetStale COMPUTE STATISTICS", "9");
        Thread.sleep(1000);
        BaseTestQuery.test("DROP TABLE dfs.tmp.`parquetStale/1996/Q5`");
        verifyAnalyzeOutput("ANALYZE TABLE dfs.tmp.parquetStale COMPUTE STATISTICS", "9");
    }

    @Test
    public void testUseStatistics() throws Exception {
        // Test ndv/rowcount for scan
        BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 1");
        BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
        BaseTestQuery.test("CREATE TABLE dfs.tmp.employeeUseStat AS SELECT * from cp.`employee.json`");
        BaseTestQuery.test("CREATE TABLE dfs.tmp.departmentUseStat AS SELECT * from cp.`department.json`");
        BaseTestQuery.test("ANALYZE TABLE dfs.tmp.employeeUseStat COMPUTE STATISTICS");
        BaseTestQuery.test("ANALYZE TABLE dfs.tmp.departmentUseStat COMPUTE STATISTICS");
        BaseTestQuery.test("ALTER SESSION SET `planner.statistics.use` = true");
        String query = " select employee_id from dfs.tmp.employeeUseStat where department_id = 2";
        String[] expectedPlan1 = new String[]{ "Filter\\(condition.*\\).*rowcount = 96.25,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan1, new String[]{  });
        query = " select employee_id from dfs.tmp.employeeUseStat where department_id IN (2, 5)";
        String[] expectedPlan2 = new String[]{ "Filter\\(condition.*\\).*rowcount = 192.5,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan2, new String[]{  });
        query = "select employee_id from dfs.tmp.employeeUseStat where department_id IN (2, 5) and employee_id = 5";
        String[] expectedPlan3 = new String[]{ "Filter\\(condition.*\\).*rowcount = 1.0,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan3, new String[]{  });
        query = " select emp.employee_id from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept" + " on emp.department_id = dept.department_id";
        String[] expectedPlan4 = new String[]{ "HashJoin\\(condition.*\\).*rowcount = 1154.9999999999995,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*", "Scan.*columns=\\[`department_id`\\].*rowcount = 12.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan4, new String[]{  });
        query = " select emp.employee_id from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept" + " on emp.department_id = dept.department_id where dept.department_id = 5";
        String[] expectedPlan5 = new String[]{ "HashJoin\\(condition.*\\).*rowcount = 96.24999999999997,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*", "Scan.*columns=\\[`department_id`\\].*rowcount = 12.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan5, new String[]{  });
        query = " select emp.employee_id from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept" + (" on emp.department_id = dept.department_id" + " where dept.department_id = 5 and emp.employee_id = 10");
        String[] expectedPlan6 = new String[]{ "MergeJoin\\(condition.*\\).*rowcount = 1.0,.*", "Filter\\(condition=\\[AND\\(=\\(\\$1, 10\\), =\\(\\$0, 5\\)\\)\\]\\).*rowcount = 1.0,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*", "Filter\\(condition=\\[=\\(\\$0, 5\\)\\]\\).*rowcount = 1.0,.*", "Scan.*columns=\\[`department_id`\\].*rowcount = 12.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan6, new String[]{  });
        query = " select emp.employee_id, count(*)" + (" from dfs.tmp.employeeUseStat emp" + " group by emp.employee_id");
        String[] expectedPlan7 = new String[]{ "HashAgg\\(group=\\[\\{0\\}\\], EXPR\\$1=\\[COUNT\\(\\)\\]\\).*rowcount = 1155.0,.*", "Scan.*columns=\\[`employee_id`\\].*rowcount = 1155.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan7, new String[]{  });
        query = " select emp.employee_id from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept" + (" on emp.department_id = dept.department_id " + " group by emp.employee_id");
        String[] expectedPlan8 = new String[]{ "HashAgg\\(group=\\[\\{0\\}\\]\\).*rowcount = 730.0992454469839,.*", "HashJoin\\(condition.*\\).*rowcount = 1154.9999999999995,.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*", "Scan.*columns=\\[`department_id`\\].*rowcount = 12.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan8, new String[]{  });
        query = "select emp.employee_id, dept.department_description" + (((" from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept" + " on emp.department_id = dept.department_id ") + " group by emp.employee_id, emp.store_id, dept.department_description ") + " having dept.department_description = 'FINANCE'");
        String[] expectedPlan9 = new String[]{ "HashAgg\\(group=\\[\\{0, 1, 2\\}\\]\\).*rowcount = 92.3487011031316.*", "HashJoin\\(condition.*\\).*rowcount = 96.24999999999997,.*", "Scan.*columns=\\[`department_id`, `employee_id`, `store_id`\\].*rowcount = 1155.0.*", "Filter\\(condition=\\[=\\(\\$1, \'FINANCE\'\\)\\]\\).*rowcount = 1.0,.*", "Scan.*columns=\\[`department_id`, `department_description`\\].*rowcount = 12.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan9, new String[]{  });
        query = " select emp.employee_id from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept\n" + ((" on emp.department_id = dept.department_id " + " group by emp.employee_id, emp.store_id ") + " having emp.store_id = 7");
        String[] expectedPlan10 = new String[]{ "HashAgg\\(group=\\[\\{0, 1\\}\\]\\).*rowcount = 29.203969817879365.*", "HashJoin\\(condition.*\\).*rowcount = 46.2,.*", "Filter\\(condition=\\[=\\(\\$2, 7\\)\\]\\).*rowcount = 46.2,.*", "Scan.*columns=\\[`department_id`, `employee_id`, `store_id`\\].*rowcount = 1155.0.*", "Scan.*columns=\\[`department_id`\\].*rowcount = 12.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan10, new String[]{  });
        query = " select emp.employee_id from dfs.tmp.employeeUseStat emp join dfs.tmp.departmentUseStat dept\n" + ((" on emp.department_id = dept.department_id " + " group by emp.employee_id ") + " having emp.employee_id = 7");
        String[] expectedPlan11 = new String[]{ "StreamAgg\\(group=\\[\\{0\\}\\]\\).*rowcount = 1.0.*", "HashJoin\\(condition.*\\).*rowcount = 1.0,.*", "Filter\\(condition=\\[=\\(\\$1, 7\\)\\]\\).*rowcount = 1.0.*", "Scan.*columns=\\[`department_id`\\].*rowcount = 12.0.*", "Scan.*columns=\\[`department_id`, `employee_id`\\].*rowcount = 1155.0.*" };
        PlanTestBase.testPlanWithAttributesMatchingPatterns(query, expectedPlan11, new String[]{  });
    }
}

