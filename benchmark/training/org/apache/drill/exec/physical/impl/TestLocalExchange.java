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
package org.apache.drill.exec.physical.impl;


import UserBitShared.UserCredentials;
import UserSession.Builder;
import java.util.List;
import org.apache.drill.PlanTestBase;
import org.apache.drill.exec.planner.fragment.SimpleParallelizer;
import org.apache.drill.exec.rpc.user.UserSession;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.junit.Test;


/**
 * This test starts a Drill cluster with CLUSTER_SIZE nodes and generates data for test tables.
 *
 * Tests queries involve HashToRandomExchange (group by and join) and test the following.
 *   1. Plan that has mux and demux exchanges inserted
 *   2. Run the query and check the output record count
 *   3. Take the plan we got in (1), use SimpleParallelizer to get PlanFragments and test that the number of
 *   partition senders in a major fragment is not more than the number of Drillbit nodes in cluster and there exists
 *   at most one partition sender per Drillbit.
 */
public class TestLocalExchange extends PlanTestBase {
    private static final int CLUSTER_SIZE = 3;

    private static final String MUX_EXCHANGE = "\"unordered-mux-exchange\"";

    private static final String DEMUX_EXCHANGE = "\"unordered-demux-exchange\"";

    private static final String MUX_EXCHANGE_CONST = "unordered-mux-exchange";

    private static final String DEMUX_EXCHANGE_CONST = "unordered-demux-exchange";

    private static final String HASH_EXCHANGE = "hash-to-random-exchange";

    private static final String EMPT_TABLE = "empTable";

    private static final String DEPT_TABLE = "deptTable";

    private static final UserSession USER_SESSION = Builder.newBuilder().withCredentials(UserCredentials.newBuilder().setUserName("foo").build()).build();

    private static final SimpleParallelizer PARALLELIZER = /* parallelizationThreshold (slice_count) */
    /* maxWidthPerNode */
    /* maxGlobalWidth */
    /* affinityFactor */
    new SimpleParallelizer(1, 6, 1000, 1.2);

    private static final int NUM_DEPTS = 40;

    private static final int NUM_EMPLOYEES = 1000;

    private static final int NUM_MNGRS = 1;

    private static final int NUM_IDS = 1;

    private static String groupByQuery;

    private static String joinQuery;

    private static String[] joinQueryBaselineColumns;

    private static String[] groupByQueryBaselineColumns;

    private static List<Object[]> groupByQueryBaselineValues;

    private static List<Object[]> joinQueryBaselineValues;

    @Test
    public void testGroupByMultiFields() throws Exception {
        // Test multifield hash generation
        BaseTestQuery.test("ALTER SESSION SET `planner.slice_target`=1");
        BaseTestQuery.test(("ALTER SESSION SET `planner.enable_mux_exchange`=" + true));
        BaseTestQuery.test(("ALTER SESSION SET `planner.enable_demux_exchange`=" + false));
        final String groupByMultipleQuery = String.format("SELECT dept_id, mng_id, some_id, count(*) as numEmployees FROM dfs.`%s` e GROUP BY dept_id, mng_id, some_id", TestLocalExchange.EMPT_TABLE);
        final String[] groupByMultipleQueryBaselineColumns = new String[]{ "dept_id", "mng_id", "some_id", "numEmployees" };
        final int numOccurrances = (TestLocalExchange.NUM_EMPLOYEES) / (TestLocalExchange.NUM_DEPTS);
        final String plan = PlanTestBase.getPlanInString(("EXPLAIN PLAN FOR " + groupByMultipleQuery), PlanTestBase.JSON_FORMAT);
        TestLocalExchange.jsonExchangeOrderChecker(plan, false, 1, "hash32asdouble\\(.*, hash32asdouble\\(.*, hash32asdouble\\(.*\\) \\) \\) ");
        // Run the query and verify the output
        final TestBuilder testBuilder = BaseTestQuery.testBuilder().sqlQuery(groupByMultipleQuery).unOrdered().baselineColumns(groupByMultipleQueryBaselineColumns);
        for (int i = 0; i < (TestLocalExchange.NUM_DEPTS); i++) {
            testBuilder.baselineValues(new Object[]{ ((long) (i)), ((long) (0)), ((long) (0)), ((long) (numOccurrances)) });
        }
        testBuilder.go();
    }

    @Test
    public void testGroupBy_NoMux_NoDeMux() throws Exception {
        TestLocalExchange.testGroupByHelper(false, false);
    }

    @Test
    public void testJoin_NoMux_NoDeMux() throws Exception {
        TestLocalExchange.testJoinHelper(false, false);
    }

    @Test
    public void testGroupBy_Mux_NoDeMux() throws Exception {
        TestLocalExchange.testGroupByHelper(true, false);
    }

    @Test
    public void testJoin_Mux_NoDeMux() throws Exception {
        TestLocalExchange.testJoinHelper(true, false);
    }

    @Test
    public void testGroupBy_NoMux_DeMux() throws Exception {
        TestLocalExchange.testGroupByHelper(false, true);
    }

    @Test
    public void testJoin_NoMux_DeMux() throws Exception {
        TestLocalExchange.testJoinHelper(false, true);
    }

    @Test
    public void testGroupBy_Mux_DeMux() throws Exception {
        TestLocalExchange.testGroupByHelper(true, true);
    }

    @Test
    public void testJoin_Mux_DeMux() throws Exception {
        TestLocalExchange.testJoinHelper(true, true);
    }
}

