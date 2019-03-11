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
package org.apache.drill.exec.physical.impl.join;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestTools;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;


@Category(OperatorTest.class)
public class TestMergeJoinAdvanced extends JoinTestBase {
    private static final String LEFT = "merge-join-left.json";

    private static final String RIGHT = "merge-join-right.json";

    private static File leftFile;

    private static File rightFile;

    @Rule
    public final TestRule TIMEOUT = TestTools.getTimeoutRule(120000);// Longer timeout than usual.


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @Category({ UnlikelyTest.class })
    public void testJoinWithDifferentTypesInCondition() throws Exception {
        String query = "select count(*) col1 from " + ("(select t1.date_opt from cp.`parquet/date_dictionary.parquet` t1, cp.`parquet/timestamp_table.parquet` t2 " + "where t1.date_opt = t2.timestamp_col)");// join condition contains date and timestamp

        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col1").baselineValues(4L).go();
        try {
            BaseTestQuery.test(JoinTestBase.ENABLE_HJ);
            query = "select t1.full_name from cp.`employee.json` t1, cp.`department.json` t2 " + "where cast(t1.department_id as double) = t2.department_id and t1.employee_id = 1";
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("full_name").baselineValues("Sheri Nowmer").go();
            query = "select t1.bigint_col from cp.`jsoninput/implicit_cast_join_1.json` t1, cp.`jsoninput/implicit_cast_join_1.json` t2 " + ((" where t1.bigint_col = cast(t2.bigint_col as int) and"// join condition with bigint and int
             + " t1.double_col  = cast(t2.double_col as float) and")// join condition with double and float
             + " t1.bigint_col = cast(t2.bigint_col as double)");// join condition with bigint and double

            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("bigint_col").baselineValues(1L).go();
        } finally {
            BaseTestQuery.test(JoinTestBase.RESET_HJ);
        }
    }

    @Test
    public void testMergeInnerJoinLargeRight() throws Exception {
        TestMergeJoinAdvanced.testMultipleBatchJoin(1000L, 5000L, "inner", (5000L * 1000L));
    }

    @Test
    public void testMergeLeftJoinLargeRight() throws Exception {
        TestMergeJoinAdvanced.testMultipleBatchJoin(1000L, 5000L, "left", ((5000L * 1000L) + 2L));
    }

    @Test
    public void testMergeRightJoinLargeRight() throws Exception {
        TestMergeJoinAdvanced.testMultipleBatchJoin(1000L, 5000L, "right", ((5000L * 1000L) + 3L));
    }

    @Test
    public void testMergeInnerJoinLargeLeft() throws Exception {
        TestMergeJoinAdvanced.testMultipleBatchJoin(5000L, 1000L, "inner", (5000L * 1000L));
    }

    @Test
    @Category({ UnlikelyTest.class })
    public void testMergeLeftJoinLargeLeft() throws Exception {
        TestMergeJoinAdvanced.testMultipleBatchJoin(5000L, 1000L, "left", ((5000L * 1000L) + 2L));
    }

    @Test
    public void testMergeRightJoinLargeLeft() throws Exception {
        TestMergeJoinAdvanced.testMultipleBatchJoin(5000L, 1000L, "right", ((5000L * 1000L) + 3L));
    }

    @Test
    @Category({ UnlikelyTest.class })
    public void testDrill4165() throws Exception {
        final String query = "select count(*) cnt from cp.`tpch/lineitem.parquet` l1, cp.`tpch/lineitem.parquet` l2 " + "where l1.l_partkey = l2.l_partkey and l1.l_suppkey < 30 and l2.l_suppkey < 30";
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt").baselineValues(202452L).go();
    }

    @Test
    public void testDrill4196() throws Exception {
        final BufferedWriter leftWriter = new BufferedWriter(new FileWriter(TestMergeJoinAdvanced.leftFile));
        final BufferedWriter rightWriter = new BufferedWriter(new FileWriter(TestMergeJoinAdvanced.rightFile));
        // output batch is 32k, create 60k left batch
        leftWriter.write(String.format("{ \"k\" : %d , \"v\": %d }", 9999, 9999));
        for (int i = 0; i < 6000; ++i) {
            leftWriter.write(String.format("{ \"k\" : %d , \"v\": %d }", 10000, 10000));
        }
        leftWriter.write(String.format("{ \"k\" : %d , \"v\": %d }", 10001, 10001));
        leftWriter.write(String.format("{ \"k\" : %d , \"v\": %d }", 10002, 10002));
        // Keep all values same. Jon will consume entire right side.
        for (int i = 0; i < 800; ++i) {
            rightWriter.write(String.format("{ \"k1\" : %d , \"v1\": %d }", 10000, 10000));
        }
        leftWriter.close();
        rightWriter.close();
        final String query = String.format("select count(*) c1 from dfs.`%s` L %s join dfs.`%s` R on L.k=R.k1", TestMergeJoinAdvanced.LEFT, "inner", TestMergeJoinAdvanced.RIGHT);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("c1").baselineValues((6000 * 800L)).go();
    }

    @Test
    public void testMergeLeftJoinWithEmptyTable() throws Exception {
        testJoinWithEmptyFile(ExecTest.dirTestWatcher.getRootDir(), "left outer", new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.LEFT_JOIN_TYPE }, 1155L);
    }

    @Test
    public void testMergeInnerJoinWithEmptyTable() throws Exception {
        testJoinWithEmptyFile(ExecTest.dirTestWatcher.getRootDir(), "inner", new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, 0L);
    }

    @Test
    public void testMergeRightJoinWithEmptyTable() throws Exception {
        testJoinWithEmptyFile(ExecTest.dirTestWatcher.getRootDir(), "right outer", new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.RIGHT_JOIN_TYPE }, 0L);
    }

    // DRILL-6491
    @Test
    public void testMergeIsNotSelectedForFullJoin() throws Exception {
        try {
            BaseTestQuery.test(JoinTestBase.ENABLE_HJ);
            String query = "select * " + (((" from (select employee_id from cp.`employee.json` order by employee_id) e1 " + " full outer join (select employee_id from cp.`employee.json` order by employee_id) e2 ") + " on e1.employee_id = e2.employee_id ") + " limit 10");
            PlanTestBase.testPlanMatchingPatterns(query, null, new String[]{ JoinTestBase.MJ_PATTERN });
        } finally {
            BaseTestQuery.test(JoinTestBase.RESET_HJ);
        }
    }

    // DRILL-6491
    @Test
    @Category({ UnlikelyTest.class })
    public void testFullJoinIsNotSupported() throws Exception {
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("SYSTEM ERROR: CannotPlanException"));
        String query = "select * " + (((" from (select employee_id from cp.`employee.json` order by employee_id) e1 " + " full outer join (select employee_id from cp.`employee.json` order by employee_id) e2 ") + " on e1.employee_id = e2.employee_id ") + " limit 10");
        BaseTestQuery.test(query);
    }
}

