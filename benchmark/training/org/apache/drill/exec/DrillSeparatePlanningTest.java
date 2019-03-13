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


import QueryType.LOGICAL;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.exec.proto.BitControl.PlanFragment;
import org.apache.drill.exec.proto.UserProtos.QueryPlanFragments;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Class to test different planning use cases (separate from query execution)
 */
@Category({ SlowTest.class, PlannerTest.class })
public class DrillSeparatePlanningTest extends ClusterTest {
    @Test(timeout = 60000)
    public void testSingleFragmentQuery() throws Exception {
        final String query = "SELECT * FROM cp.`employee.json` where employee_id > 1 and employee_id < 1000";
        QueryPlanFragments planFragments = getFragmentsHelper(query);
        Assert.assertNotNull(planFragments);
        Assert.assertEquals(1, planFragments.getFragmentsCount());
        Assert.assertTrue(planFragments.getFragments(0).getLeafFragment());
        QueryBuilder.QuerySummary summary = ClusterTest.client.queryBuilder().plan(planFragments.getFragmentsList()).run();
        Assert.assertEquals(997, summary.recordCount());
    }

    @Test(timeout = 60000)
    public void testMultiMinorFragmentSimpleQuery() throws Exception {
        final String query = "SELECT o_orderkey FROM dfs.`multilevel/json`";
        QueryPlanFragments planFragments = getFragmentsHelper(query);
        Assert.assertNotNull(planFragments);
        Assert.assertTrue(((planFragments.getFragmentsCount()) > 1));
        for (PlanFragment planFragment : planFragments.getFragmentsList()) {
            Assert.assertTrue(planFragment.getLeafFragment());
        }
        int rowCount = getResultsHelper(planFragments);
        Assert.assertEquals(120, rowCount);
    }

    @Test(timeout = 60000)
    public void testMultiMinorFragmentComplexQuery() throws Exception {
        final String query = "SELECT dir0, sum(o_totalprice) FROM dfs.`multilevel/json` group by dir0 order by dir0";
        QueryPlanFragments planFragments = getFragmentsHelper(query);
        Assert.assertNotNull(planFragments);
        Assert.assertTrue(((planFragments.getFragmentsCount()) > 1));
        for (PlanFragment planFragment : planFragments.getFragmentsList()) {
            Assert.assertTrue(planFragment.getLeafFragment());
        }
        int rowCount = getResultsHelper(planFragments);
        Assert.assertEquals(8, rowCount);
    }

    @Test(timeout = 60000)
    public void testPlanningNoSplit() throws Exception {
        final String query = "SELECT dir0, sum(o_totalprice) FROM dfs.`multilevel/json` group by dir0 order by dir0";
        ClusterTest.client.alterSession("planner.slice_target", 1);
        try {
            final QueryPlanFragments planFragments = ClusterTest.client.planQuery(query);
            Assert.assertNotNull(planFragments);
            Assert.assertTrue(((planFragments.getFragmentsCount()) > 1));
            PlanFragment rootFragment = planFragments.getFragments(0);
            Assert.assertFalse(rootFragment.getLeafFragment());
            QueryBuilder.QuerySummary summary = ClusterTest.client.queryBuilder().plan(planFragments.getFragmentsList()).run();
            Assert.assertEquals(3, summary.recordCount());
        } finally {
            ClusterTest.client.resetSession("planner.slice_target");
        }
    }

    @Test(timeout = 60000)
    public void testPlanningNegative() throws Exception {
        final String query = "SELECT dir0, sum(o_totalprice) FROM dfs.`multilevel/json` group by dir0 order by dir0";
        // LOGICAL is not supported
        final QueryPlanFragments planFragments = ClusterTest.client.planQuery(LOGICAL, query, false);
        Assert.assertNotNull(planFragments);
        Assert.assertNotNull(planFragments.getError());
        Assert.assertTrue(((planFragments.getFragmentsCount()) == 0));
    }

    @Test(timeout = 60000)
    public void testPlanning() throws Exception {
        final String query = "SELECT dir0, columns[3] FROM dfs.`multilevel/csv` order by dir0";
        ClusterTest.client.alterSession("planner.slice_target", 1);
        try {
            // Original version, but no reason to dump output to test results.
            // long rows = client.queryBuilder().sql(query).print(Format.TSV, VectorUtil.DEFAULT_COLUMN_WIDTH);
            QueryBuilder.QuerySummary summary = ClusterTest.client.queryBuilder().sql(query).run();
            Assert.assertEquals(120, summary.recordCount());
        } finally {
            ClusterTest.client.resetSession("planner.slice_target");
        }
    }
}

