/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.planner;


import io.crate.execution.dsl.projection.OrderedTopNProjection;
import io.crate.execution.dsl.projection.TopNProjection;
import io.crate.planner.node.dql.Collect;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class UnionPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testSimpleUnion() {
        ExecutionPlan plan = e.plan(("select id from users " + ("union all " + "select id from locations ")));
        assertThat(plan, Matchers.instanceOf(UnionExecutionPlan.class));
        UnionExecutionPlan unionExecutionPlan = ((UnionExecutionPlan) (plan));
        assertThat(unionExecutionPlan.orderBy(), Is.is(Matchers.nullValue()));
        assertThat(unionExecutionPlan.mergePhase().numInputs(), Is.is(2));
        assertThat(unionExecutionPlan.left(), Matchers.instanceOf(Collect.class));
        assertThat(unionExecutionPlan.right(), Matchers.instanceOf(Collect.class));
    }

    @Test
    public void testUnionWithOrderByLimit() {
        ExecutionPlan plan = e.plan(("select id from users " + (("union all " + "select id from locations ") + "order by id limit 2")));
        assertThat(plan, Matchers.instanceOf(UnionExecutionPlan.class));
        UnionExecutionPlan unionExecutionPlan = ((UnionExecutionPlan) (plan));
        assertThat(unionExecutionPlan.mergePhase().numInputs(), Is.is(2));
        assertThat(unionExecutionPlan.mergePhase().orderByPositions(), Matchers.instanceOf(PositionalOrderBy.class));
        assertThat(unionExecutionPlan.mergePhase().projections(), Matchers.contains(Matchers.instanceOf(TopNProjection.class)));
        assertThat(unionExecutionPlan.left(), Matchers.instanceOf(Collect.class));
        assertThat(unionExecutionPlan.right(), Matchers.instanceOf(Collect.class));
    }

    @Test
    public void testUnionWithSubselects() {
        ExecutionPlan plan = e.plan(("select * from (select id from users order by id limit 2) a " + (("union all " + "select id from locations ") + "order by id limit 2")));
        assertThat(plan, Matchers.instanceOf(UnionExecutionPlan.class));
        UnionExecutionPlan unionExecutionPlan = ((UnionExecutionPlan) (plan));
        assertThat(unionExecutionPlan.mergePhase().numInputs(), Is.is(2));
        assertThat(unionExecutionPlan.orderBy(), Is.is(Matchers.nullValue()));
        assertThat(unionExecutionPlan.mergePhase().projections(), Matchers.contains(Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(TopNProjection.class)));
        assertThat(unionExecutionPlan.left(), Matchers.instanceOf(Merge.class));
        Merge merge = ((Merge) (unionExecutionPlan.left()));
        assertThat(merge.subPlan(), Matchers.instanceOf(Collect.class));
        assertThat(unionExecutionPlan.right(), Matchers.instanceOf(Collect.class));
    }
}

