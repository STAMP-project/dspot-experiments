/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.rel.rules;


import EnumerableRules.ENUMERABLE_CORRELATE_RULE;
import EnumerableRules.ENUMERABLE_FILTER_RULE;
import EnumerableRules.ENUMERABLE_JOIN_RULE;
import EnumerableRules.ENUMERABLE_PROJECT_RULE;
import EnumerableRules.ENUMERABLE_SEMI_JOIN_RULE;
import EnumerableRules.ENUMERABLE_SORT_RULE;
import EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE;
import JoinToCorrelateRule.JOIN;
import SemiJoinRule.PROJECT;
import SortProjectTransposeRule.INSTANCE;
import java.util.Arrays;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.tools.RuleSet;
import org.apache.calcite.tools.RuleSets;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the application of the {@link SortRemoveRule}.
 */
public final class SortRemoveRuleTest {
    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]
     * Enrich enumerable join operators with order preserving information</a>.
     *
     * Since join inputs are sorted, and this join preserves the order of the left input, there
     * shouldn't be any sort operator above the join.
     */
    @Test
    public void removeSortOverEnumerableJoin() throws Exception {
        RuleSet prepareRules = RuleSets.ofList(INSTANCE, ENUMERABLE_JOIN_RULE, ENUMERABLE_PROJECT_RULE, ENUMERABLE_SORT_RULE, ENUMERABLE_TABLE_SCAN_RULE);
        for (String joinType : Arrays.asList("left", "right", "full", "inner")) {
            String sql = ((("select e.\"deptno\" from \"hr\".\"emps\" e " + joinType) + " join \"hr\".\"depts\" d ") + " on e.\"deptno\" = d.\"deptno\" ") + "order by e.\"empid\" ";
            RelNode actualPlan = transform(sql, prepareRules);
            Assert.assertThat(toString(actualPlan), CoreMatchers.allOf(CoreMatchers.containsString("EnumerableJoin"), CoreMatchers.not(CoreMatchers.containsString("EnumerableSort"))));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]
     * Enrich enumerable join operators with order preserving information</a>.
     *
     * Since join inputs are sorted, and this join preserves the order of the left input, there
     * shouldn't be any sort operator above the join.
     */
    @Test
    public void removeSortOverEnumerableThetaJoin() throws Exception {
        RuleSet prepareRules = RuleSets.ofList(INSTANCE, ENUMERABLE_JOIN_RULE, ENUMERABLE_PROJECT_RULE, ENUMERABLE_SORT_RULE, ENUMERABLE_TABLE_SCAN_RULE);
        // Inner join is not considered since the ENUMERABLE_JOIN_RULE does not generate a theta join
        // in the case of inner joins.
        for (String joinType : Arrays.asList("left", "right", "full")) {
            String sql = ((("select e.\"deptno\" from \"hr\".\"emps\" e " + joinType) + " join \"hr\".\"depts\" d ") + " on e.\"deptno\" > d.\"deptno\" ") + "order by e.\"empid\" ";
            RelNode actualPlan = transform(sql, prepareRules);
            Assert.assertThat(toString(actualPlan), CoreMatchers.allOf(CoreMatchers.containsString("EnumerableThetaJoin"), CoreMatchers.not(CoreMatchers.containsString("EnumerableSort"))));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]
     * Enrich enumerable join operators with order preserving information</a>.
     *
     * Since join inputs are sorted, and this join preserves the order of the left input, there
     * shouldn't be any sort operator above the join.
     */
    @Test
    public void removeSortOverEnumerableCorrelate() throws Exception {
        RuleSet prepareRules = RuleSets.ofList(INSTANCE, JOIN, ENUMERABLE_SORT_RULE, ENUMERABLE_PROJECT_RULE, ENUMERABLE_CORRELATE_RULE, ENUMERABLE_FILTER_RULE, ENUMERABLE_TABLE_SCAN_RULE);
        for (String joinType : Arrays.asList("left", "inner")) {
            String sql = ((("select e.\"deptno\" from \"hr\".\"emps\" e " + joinType) + " join \"hr\".\"depts\" d ") + " on e.\"deptno\" = d.\"deptno\" ") + "order by e.\"empid\" ";
            RelNode actualPlan = transform(sql, prepareRules);
            Assert.assertThat(toString(actualPlan), CoreMatchers.allOf(CoreMatchers.containsString("EnumerableCorrelate"), CoreMatchers.not(CoreMatchers.containsString("EnumerableSort"))));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]
     * Enrich enumerable join operators with order preserving information</a>.
     *
     * Since join inputs are sorted, and this join preserves the order of the left input, there
     * shouldn't be any sort operator above the join.
     */
    @Test
    public void removeSortOverEnumerableSemiJoin() throws Exception {
        RuleSet prepareRules = RuleSets.ofList(INSTANCE, PROJECT, SemiJoinRule.JOIN, ENUMERABLE_PROJECT_RULE, ENUMERABLE_SORT_RULE, ENUMERABLE_SEMI_JOIN_RULE, ENUMERABLE_FILTER_RULE, ENUMERABLE_TABLE_SCAN_RULE);
        String sql = "select e.\"deptno\" from \"hr\".\"emps\" e\n" + (" where e.\"deptno\" in (select d.\"deptno\" from \"hr\".\"depts\" d)\n" + " order by e.\"empid\"");
        RelNode actualPlan = transform(sql, prepareRules);
        Assert.assertThat(toString(actualPlan), CoreMatchers.allOf(CoreMatchers.containsString("EnumerableSemiJoin"), CoreMatchers.not(CoreMatchers.containsString("EnumerableSort"))));
    }
}

/**
 * End SortRemoveRuleTest.java
 */
