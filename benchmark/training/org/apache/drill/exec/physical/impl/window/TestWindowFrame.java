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
package org.apache.drill.exec.physical.impl.window;


import ErrorType.FUNCTION;
import ErrorType.UNSUPPORTED_OPERATION;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class TestWindowFrame extends BaseTestQuery {
    /**
     * Single batch with a single partition (position_id column)
     */
    @Test
    public void testB1P1() throws Exception {
        runTest("b1.p1", 1);
    }

    /**
     * Single batch with 2 partitions (position_id column)
     */
    @Test
    public void testB1P2() throws Exception {
        runTest("b1.p2", 1);
    }

    @Test
    public void testMultipleFramers() throws Exception {
        final String window = " OVER(PARTITION BY position_id ORDER by sub)";
        BaseTestQuery.test(((((((((("SELECT COUNT(*)" + window) + ", SUM(salary)") + window) + ", ROW_NUMBER()") + window) + ", RANK()") + window) + " ") + "FROM dfs.`window/b1.p1`"));
    }

    @Test
    public void testUnboundedFollowing() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/q3.sql")).ordered().sqlBaselineQuery(BaseTestQuery.getFile("window/q4.sql")).build().run();
    }

    @Test
    public void testAggregateRowsUnboundedAndCurrentRow() throws Exception {
        final String table = "dfs.`window/b4.p4`";
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/aggregate_rows_unbounded_current.sql"), table).ordered().sqlBaselineQuery(BaseTestQuery.getFile("window/aggregate_rows_unbounded_current_baseline.sql"), table).build().run();
    }

    @Test
    public void testLastValueRowsUnboundedAndCurrentRow() throws Exception {
        final String table = "dfs.`window/b4.p4`";
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/last_value_rows_unbounded_current.sql"), table).unOrdered().sqlBaselineQuery(BaseTestQuery.getFile("window/last_value_rows_unbounded_current_baseline.sql"), table).build().run();
    }

    @Test
    public void testAggregateRangeCurrentAndCurrent() throws Exception {
        final String table = "dfs.`window/b4.p4`";
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/aggregate_range_current_current.sql"), table).unOrdered().sqlBaselineQuery(BaseTestQuery.getFile("window/aggregate_range_current_current_baseline.sql"), table).build().run();
    }

    @Test
    public void testFirstValueRangeCurrentAndCurrent() throws Exception {
        final String table = "dfs.`window/b4.p4`";
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/first_value_range_current_current.sql"), table).unOrdered().sqlBaselineQuery(BaseTestQuery.getFile("window/first_value_range_current_current_baseline.sql"), table).build().run();
    }

    /**
     * 2 batches with 2 partitions (position_id column), each batch contains a different partition
     */
    @Test
    public void testB2P2() throws Exception {
        runTest("b2.p2", 2);
    }

    /**
     * 2 batches with 4 partitions, one partition has rows in both batches
     */
    @Test
    public void testB2P4() throws Exception {
        runTest("b2.p4", 2);
    }

    /**
     * 3 batches with 2 partitions, one partition has rows in all 3 batches
     */
    @Test
    public void testB3P2() throws Exception {
        runTest("b3.p2", 3);
    }

    /**
     * 4 batches with 4 partitions. After processing 1st batch, when innerNext() is called again, framer can process
     * current batch without the need to call next(incoming).
     */
    @Test
    public void testB4P4() throws Exception {
        runTest("b4.p4", 4);
    }

    // DRILL-1862
    @Test
    @Category(UnlikelyTest.class)
    public void testEmptyPartitionBy() throws Exception {
        BaseTestQuery.test("SELECT employee_id, position_id, salary, SUM(salary) OVER(ORDER BY position_id) FROM cp.`employee.json` LIMIT 10");
    }

    // DRILL-3172
    @Test
    @Category(UnlikelyTest.class)
    public void testEmptyOverClause() throws Exception {
        BaseTestQuery.test("SELECT employee_id, position_id, salary, SUM(salary) OVER() FROM cp.`employee.json` LIMIT 10");
    }

    // DRILL-3218
    @Test
    @Category(UnlikelyTest.class)
    public void testMaxVarChar() throws Exception {
        BaseTestQuery.test(BaseTestQuery.getFile("window/q3218.sql"));
    }

    // DRILL-3220
    @Test
    @Category(UnlikelyTest.class)
    public void testCountConst() throws Exception {
        BaseTestQuery.test(BaseTestQuery.getFile("window/q3220.sql"));
    }

    // DRILL-3604
    @Test
    @Category(UnlikelyTest.class)
    public void testFix3604() throws Exception {
        // make sure the query doesn't fail
        BaseTestQuery.test(BaseTestQuery.getFile("window/3604.sql"));
    }

    // DRILL-3605
    @Test
    @Category(UnlikelyTest.class)
    public void testFix3605() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/3605.sql")).ordered().csvBaselineFile("window/3605.tsv").baselineColumns("col2", "lead_col2").build().run();
    }

    // DRILL-3606
    @Test
    @Category(UnlikelyTest.class)
    public void testFix3606() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/3606.sql")).ordered().csvBaselineFile("window/3606.tsv").baselineColumns("col2", "lead_col2").build().run();
    }

    @Test
    public void testLead() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/lead.oby.sql")).ordered().csvBaselineFile("window/b4.p4.lead.oby.tsv").baselineColumns("lead").build().run();
    }

    @Test
    public void testLagWithPby() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/lag.pby.oby.sql")).ordered().csvBaselineFile("window/b4.p4.lag.pby.oby.tsv").baselineColumns("lag").build().run();
    }

    @Test
    public void testLag() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/lag.oby.sql")).ordered().csvBaselineFile("window/b4.p4.lag.oby.tsv").baselineColumns("lag").build().run();
    }

    @Test
    @Category(UnlikelyTest.class)
    public void testLeadWithPby() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/lead.pby.oby.sql")).ordered().csvBaselineFile("window/b4.p4.lead.pby.oby.tsv").baselineColumns("lead").build().run();
    }

    @Test
    public void testFirstValue() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/fval.pby.sql")).ordered().csvBaselineFile("window/b4.p4.fval.pby.tsv").baselineColumns("first_value").build().run();
    }

    @Test
    public void testLastValue() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/lval.pby.oby.sql")).ordered().csvBaselineFile("window/b4.p4.lval.pby.oby.tsv").baselineColumns("last_value").build().run();
    }

    @Test
    public void testFirstValueAllTypes() throws Exception {
        // make sure all types are handled properly
        BaseTestQuery.test(BaseTestQuery.getFile("window/fval.alltypes.sql"));
    }

    @Test
    public void testLastValueAllTypes() throws Exception {
        // make sure all types are handled properly
        BaseTestQuery.test(BaseTestQuery.getFile("window/fval.alltypes.sql"));
    }

    @Test
    public void testNtile() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/ntile.sql")).ordered().csvBaselineFile("window/b2.p4.ntile.tsv").baselineColumns("ntile").build().run();
    }

    @Test
    public void test3648Fix() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/3648.sql")).ordered().csvBaselineFile("window/3648.tsv").baselineColumns("ntile").build().run();
    }

    @Test
    public void test3654Fix() throws Exception {
        BaseTestQuery.test("SELECT FIRST_VALUE(col8) OVER(PARTITION BY col7 ORDER BY col8) FROM dfs.`window/3648.parquet`");
    }

    @Test
    public void test3643Fix() throws Exception {
        try {
            BaseTestQuery.test("SELECT NTILE(0) OVER(PARTITION BY col7 ORDER BY col8) FROM dfs.`window/3648.parquet`");
            Assert.fail("Query should have failed");
        } catch (UserRemoteException e) {
            Assert.assertEquals(FUNCTION, e.getErrorType());
        }
    }

    @Test
    public void test3668Fix() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(BaseTestQuery.getFile("window/3668.sql")).ordered().baselineColumns("cnt").baselineValues(2L).build().run();
    }

    @Test
    public void testLeadParams() throws Exception {
        // make sure we only support default arguments for LEAD/LAG functions
        final String query = "SELECT %s OVER(PARTITION BY col7 ORDER BY col8) FROM dfs.`window/3648.parquet`";
        BaseTestQuery.test(query, "LEAD(col8, 1)");
        BaseTestQuery.test(query, "LAG(col8, 1)");
        try {
            BaseTestQuery.test(query, "LEAD(col8, 2)");
            Assert.fail("query should fail");
        } catch (UserRemoteException e) {
            Assert.assertEquals(UNSUPPORTED_OPERATION, e.getErrorType());
        }
        try {
            BaseTestQuery.test(query, "LAG(col8, 2)");
            Assert.fail("query should fail");
        } catch (UserRemoteException e) {
            Assert.assertEquals(UNSUPPORTED_OPERATION, e.getErrorType());
        }
    }

    @Test
    public void testPartitionNtile() {
        Partition partition = new Partition();
        partition.updateLength(12, false);
        Assert.assertEquals(1, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(1, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(1, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(2, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(2, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(2, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(3, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(3, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(4, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(4, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(5, partition.ntile(5));
        partition.rowAggregated();
        Assert.assertEquals(5, partition.ntile(5));
    }

    @Test
    public void test4457() throws Exception {
        BaseTestQuery.runSQL(("CREATE TABLE dfs.tmp.`4457` AS " + ("SELECT columns[0] AS c0, NULLIF(columns[1], 'null') AS c1 " + "FROM dfs.`window/4457.csv`")));
        BaseTestQuery.testBuilder().sqlQuery("SELECT COALESCE(FIRST_VALUE(c1) OVER(ORDER BY c0 RANGE BETWEEN CURRENT ROW AND CURRENT ROW), 'EMPTY') AS fv FROM dfs.tmp.`4457`").ordered().baselineColumns("fv").baselineValues("a").baselineValues("b").baselineValues("EMPTY").go();
    }

    // Note: This test is unstable. It works when forcing the merge/sort batch
    // size to 20, but not for other sizes. The problem is either that the results
    // are not ordered (and so subject to sort instability), or there is some bug
    // somewhere in the window functions.
    @Test
    @Category(UnlikelyTest.class)
    public void test4657() throws Exception {
        // we expect 3 data batches and the fast schema
        BaseTestQuery.testBuilder().sqlQuery("select row_number() over(order by position_id) rn, rank() over(order by position_id) rnk from dfs.`window/b3.p2`").ordered().csvBaselineFile("window/4657.tsv").baselineColumns("rn", "rnk").expectsNumBatches(4).go();
    }
}

