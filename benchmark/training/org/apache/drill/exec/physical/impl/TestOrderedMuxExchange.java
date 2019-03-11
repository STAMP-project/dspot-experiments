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


import ExecConstants.ORDERED_MUX_EXCHANGE;
import ExecConstants.SLICE_TARGET;
import ExecConstants.SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE;
import org.apache.drill.PlanTestBase;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.test.ClientFixture;
import org.apache.drill.test.ClusterFixture;
import org.apache.drill.test.ClusterFixtureBuilder;
import org.junit.Assert;
import org.junit.Test;


public class TestOrderedMuxExchange extends PlanTestBase {
    private static final String ORDERED_MUX_EXCHANGE = "OrderedMuxExchange";

    private static final String TOPN = "TopN";

    private static final int NUM_DEPTS = 40;

    private static final int NUM_EMPLOYEES = 1000;

    private static final int NUM_MNGRS = 1;

    private static final int NUM_IDS = 1;

    private static final String MATCH_PATTERN_ACROSS_LINES = "((?s).*[\\n\\r].*)";

    private static final String EMPT_TABLE = "empTable";

    /**
     * Test case to verify the OrderedMuxExchange created for order by clause.
     * It checks by forcing the plan to create OrderedMuxExchange and also verifies the
     * output column is ordered.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void testOrderedMuxForOrderBy() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(ExecTest.dirTestWatcher).maxParallelization(1).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, true);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            client.alterSession(SLICE_TARGET, 10);
            String sql = "SELECT emp_id, emp_name FROM dfs.`empTable` e order BY emp_name, emp_id";
            // Use default option setting.
            client.testBuilder().unOrdered().optionSettingQueriesForTestQuery("alter session set `planner.slice_target` = 10;").sqlQuery(sql).optionSettingQueriesForBaseline("alter session set `planner.enable_ordered_mux_exchange` = false").sqlBaselineQuery(sql).build().run();
            client.alterSession(ExecConstants.ORDERED_MUX_EXCHANGE, true);
            String explainText = client.queryBuilder().sql(sql).explainText();
            Assert.assertTrue(explainText.contains(TestOrderedMuxExchange.ORDERED_MUX_EXCHANGE));
        }
    }

    /**
     * Test case to verify the OrderedMuxExchange created for window functions.
     * It checks by forcing the plan to create OrderedMuxExchange and also verifies the
     * output column is ordered.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void testOrderedMuxForWindowAgg() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(ExecTest.dirTestWatcher).maxParallelization(1).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, true);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            client.alterSession(SLICE_TARGET, 10);
            String sql = "SELECT emp_name, max(emp_id) over (order by emp_name) FROM dfs.`empTable` e order BY emp_name";
            // Use default option setting.
            client.testBuilder().unOrdered().optionSettingQueriesForTestQuery("alter session set `planner.slice_target` = 10;").sqlQuery(sql).optionSettingQueriesForBaseline("alter session set `planner.enable_ordered_mux_exchange` = false").sqlBaselineQuery(sql).build().run();
            client.alterSession(ExecConstants.ORDERED_MUX_EXCHANGE, true);
            String explainText = client.queryBuilder().sql(sql).explainText();
            Assert.assertTrue(explainText.contains(TestOrderedMuxExchange.ORDERED_MUX_EXCHANGE));
        }
    }

    /**
     * Test case to verify the OrderedMuxExchange created for order by with limit.
     * It checks that the limit is pushed down the OrderedMuxExchange.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void testLimitOnOrderedMux() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(ExecTest.dirTestWatcher).maxParallelization(1).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, true);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            client.alterSession(SLICE_TARGET, 10);
            String sql = "SELECT emp_id, emp_name FROM dfs.`empTable` e order BY emp_name, emp_id limit 10";
            // Use default option setting.
            client.testBuilder().unOrdered().optionSettingQueriesForTestQuery("alter session set `planner.slice_target` = 10;").sqlQuery(sql).optionSettingQueriesForBaseline("alter session set `planner.enable_ordered_mux_exchange` = false").sqlBaselineQuery(sql).build().run();
            client.alterSession(ExecConstants.ORDERED_MUX_EXCHANGE, true);
            String explainText = client.queryBuilder().sql(sql).explainText();
            Assert.assertTrue(explainText.matches(String.format((((((TestOrderedMuxExchange.MATCH_PATTERN_ACROSS_LINES) + "%s") + (TestOrderedMuxExchange.MATCH_PATTERN_ACROSS_LINES)) + "%s") + (TestOrderedMuxExchange.MATCH_PATTERN_ACROSS_LINES)), TestOrderedMuxExchange.ORDERED_MUX_EXCHANGE, TestOrderedMuxExchange.TOPN)));
        }
    }

    /**
     * Test case to verify the OrderedMuxExchange created for order by with limit and window agg.
     * It checks that the limit is pushed down the OrderedMuxExchange.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void testLimitOnOrderedMuxWindow() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(ExecTest.dirTestWatcher).maxParallelization(1).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, true);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            client.alterSession(SLICE_TARGET, 10);
            String sql = "SELECT emp_name, max(emp_id) over (order by emp_name) FROM dfs.`empTable` e order BY emp_name limit 10";
            // Use default option setting.
            client.testBuilder().unOrdered().optionSettingQueriesForTestQuery("alter session set `planner.slice_target` = 10;").sqlQuery(sql).optionSettingQueriesForBaseline("alter session set `planner.enable_ordered_mux_exchange` = false").sqlBaselineQuery(sql).build().run();
            client.alterSession(ExecConstants.ORDERED_MUX_EXCHANGE, true);
            String explainText = client.queryBuilder().sql(sql).explainText();
            Assert.assertTrue(explainText.matches(String.format((((((TestOrderedMuxExchange.MATCH_PATTERN_ACROSS_LINES) + "%s") + (TestOrderedMuxExchange.MATCH_PATTERN_ACROSS_LINES)) + "%s") + (TestOrderedMuxExchange.MATCH_PATTERN_ACROSS_LINES)), TestOrderedMuxExchange.ORDERED_MUX_EXCHANGE, TestOrderedMuxExchange.TOPN)));
        }
    }
}

