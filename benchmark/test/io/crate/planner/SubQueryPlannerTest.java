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


import RowGranularity.NODE;
import RowGranularity.SHARD;
import io.crate.execution.dsl.projection.AggregationProjection;
import io.crate.execution.dsl.projection.EvalProjection;
import io.crate.execution.dsl.projection.FetchProjection;
import io.crate.execution.dsl.projection.FilterProjection;
import io.crate.execution.dsl.projection.GroupProjection;
import io.crate.execution.dsl.projection.OrderedTopNProjection;
import io.crate.execution.dsl.projection.Projection;
import io.crate.execution.dsl.projection.TopNProjection;
import io.crate.expression.symbol.Function;
import io.crate.planner.node.dql.Collect;
import io.crate.planner.node.dql.QueryThenFetch;
import io.crate.planner.node.dql.join.Join;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.ProjectionMatchers;
import io.crate.testing.SQLExecutor;
import io.crate.testing.TestingHelpers;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SubQueryPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testNestedSimpleSelectUsesFetch() throws Exception {
        QueryThenFetch qtf = e.plan("select x, i from (select x, i from t1 order by x asc limit 10) ti order by x desc limit 3");
        Collect collect = ((Collect) (qtf.subPlan()));
        List<Projection> projections = collect.collectPhase().projections();
        assertThat(projections, Matchers.contains(Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(FetchProjection.class)));
        // Assert that the OrderedTopNProjection has correct outputs
        assertThat(projections.get(1).outputs(), TestingHelpers.isSQL("INPUT(0), INPUT(1)"));
    }

    @Test
    public void testNestedSimpleSelectWithEarlyFetchBecauseOfWhereClause() throws Exception {
        QueryThenFetch qtf = e.plan("select x, i from (select x, i from t1 order by x asc limit 10) ti where ti.i = 10 order by x desc limit 3");
        Collect collect = ((Collect) (qtf.subPlan()));
        List<Projection> projections = collect.collectPhase().projections();
        assertThat(projections, // order by is on query symbol but LIMIT must be applied after WHERE
        Matchers.contains(ProjectionMatchers.isTopN(10, 0), Matchers.instanceOf(FetchProjection.class), Matchers.instanceOf(FilterProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), ProjectionMatchers.isTopN(3, 0)));
    }

    @Test
    public void testTwoLevelFetchPropagation() throws Exception {
        QueryThenFetch qtf = e.plan(("select x, i, a from (" + ((((("    select a, i, x from (" + "        select x, i, a from t1 order by x asc limit 100") + "    ) tt ") + "    order by tt.x desc limit 50") + ") ttt ") + "order by ttt.x asc limit 10")));
        Collect collect = ((Collect) (qtf.subPlan()));
        List<Projection> projections = collect.collectPhase().projections();
        assertThat(projections, Matchers.contains(Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(FetchProjection.class)));
    }

    @Test
    public void testSimpleSubSelectWithLateFetchWhereClauseMatchesQueryColumn() throws Exception {
        QueryThenFetch qtf = e.plan(("select xx, i from (select x + x as xx, i from t1 order by x asc limit 10) ti " + "where ti.xx = 10 order by xx desc limit 3"));
        Collect collect = ((Collect) (qtf.subPlan()));
        List<Projection> projections = collect.collectPhase().projections();
        assertThat(projections, Matchers.contains(Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(FilterProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(FetchProjection.class)));
        assertThat(projections.get(1).outputs(), TestingHelpers.isSQL("INPUT(0), add(INPUT(1), INPUT(1))"));
        FilterProjection filterProjection = ((FilterProjection) (projections.get(2)));
        // filter is before fetch; preFetchOutputs: [_fetchId, add(x, x)]
        assertThat(filterProjection.query(), TestingHelpers.isSQL("(INPUT(1) = 10)"));
    }

    @Test
    public void testNestedSimpleSelectContainsFilterProjectionForWhereClause() throws Exception {
        QueryThenFetch qtf = e.plan(("select x, i from " + (("   (select x, i from t1 order by x asc limit 10) ti " + "where ti.x = 10 ") + "order by x desc limit 3")));
        Collect collect = ((Collect) (qtf.subPlan()));
        List<Projection> projections = collect.collectPhase().projections();
        assertThat(projections, Matchers.hasItem(Matchers.instanceOf(FilterProjection.class)));
    }

    @Test
    public void testNestedSimpleSelectWithJoin() throws Exception {
        Join nl = e.plan(("select t1x from (" + ("select t1.x as t1x, t2.i as t2i from t1 as t1, t1 as t2 order by t1x asc limit 10" + ") t order by t1x desc limit 3")));
        List<Projection> projections = nl.joinPhase().projections();
        assertThat(projections, Matchers.contains(Matchers.instanceOf(EvalProjection.class), ProjectionMatchers.isTopN(10, 0), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), ProjectionMatchers.isTopN(3, 0), Matchers.instanceOf(EvalProjection.class)));
        assertThat(projections.get(0).outputs(), TestingHelpers.isSQL("INPUT(0), INPUT(1)"));
        assertThat(projections.get(5).outputs(), TestingHelpers.isSQL("INPUT(1)"));
    }

    @Test
    public void testNestedSimpleSelectContainsGroupProjectionWithFunction() throws Exception {
        Collect collect = e.plan(("select c + 100, max(max) from " + (("    (select x + 10 as c, max(i) as max from t1 group by x + 10) t " + "group by c + 100 order by c + 100 ") + "limit 100")));
        // We assume that an add function is present in the group projection keys.
        List<Projection> projections = collect.collectPhase().projections();
        GroupProjection projection = ((GroupProjection) (projections.get(2)));
        Function function = ((Function) (projection.keys().get(0)));
        assertEquals(ArithmeticFunctions.Names.ADD, function.info().ident().name());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testJoinOnSubSelectsWithLimitAndOffset() throws Exception {
        Join join = e.plan(("select * from " + (((" (select i, a from t1 order by a limit 10 offset 2) t1 " + "join") + " (select i from t2 order by b limit 5 offset 5) t2 ") + "on t1.i = t2.i")));
        assertThat(join.joinPhase().projections().size(), Matchers.is(1));
        assertThat(join.joinPhase().projections().get(0), Matchers.instanceOf(EvalProjection.class));
        Collect left = ((Collect) (join.left()));
        assertThat("1 node, otherwise mergePhases would be required", left.nodeIds().size(), Matchers.is(1));
        assertThat(left.orderBy(), TestingHelpers.isSQL("OrderByPositions{indices=[1], reverseFlags=[false], nullsFirst=[null]}"));
        assertThat(left.collectPhase().projections(), Matchers.contains(ProjectionMatchers.isTopN(10, 2)));
        Collect right = ((Collect) (join.right()));
        assertThat("1 node, otherwise mergePhases would be required", right.nodeIds().size(), Matchers.is(1));
        assertThat(orderBy(), TestingHelpers.isSQL("doc.t2.b"));
        assertThat(right.collectPhase().projections(), // strips `b` used in order by from the outputs
        Matchers.contains(ProjectionMatchers.isTopN(5, 5), Matchers.instanceOf(EvalProjection.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testJoinWithAggregationOnSubSelectsWithLimitAndOffset() throws Exception {
        Join join = e.plan(("select t1.a, count(*) from " + ((((" (select i, a from t1 order by a limit 10 offset 2) t1 " + "join") + " (select i from t2 order by i desc limit 5 offset 5) t2 ") + "on t1.i = t2.i ") + "group by t1.a")));
        Collect left = ((Collect) (join.left()));
        assertThat("1 node, otherwise mergePhases would be required", left.nodeIds().size(), Matchers.is(1));
        assertThat(orderBy(), TestingHelpers.isSQL("doc.t1.a"));
        assertThat(left.collectPhase().projections(), Matchers.contains(ProjectionMatchers.isTopN(10, 2), Matchers.instanceOf(EvalProjection.class)));
        assertThat(left.collectPhase().toCollect(), TestingHelpers.isSQL("doc.t1.i, doc.t1.a"));
        Collect right = ((Collect) (join.right()));
        assertThat("1 node, otherwise mergePhases would be required", right.nodeIds().size(), Matchers.is(1));
        assertThat(orderBy(), TestingHelpers.isSQL("doc.t2.i DESC"));
        assertThat(right.collectPhase().projections(), Matchers.contains(ProjectionMatchers.isTopN(5, 5)));
        List<Projection> nlProjections = join.joinPhase().projections();
        assertThat(nlProjections, Matchers.contains(Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(GroupProjection.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testJoinWithGlobalAggregationOnSubSelectsWithLimitAndOffset() throws Exception {
        Join join = e.plan(("select count(*) from " + (((" (select i, a from t1 order by a limit 10 offset 2) t1 " + "join") + " (select i from t2 order by i desc limit 5 offset 5) t2 ") + "on t1.i = t2.i")));
        Collect left = ((Collect) (join.left()));
        assertThat("1 node, otherwise mergePhases would be required", left.nodeIds().size(), Matchers.is(1));
        assertThat(left.collectPhase().toCollect(), TestingHelpers.isSQL("doc.t1.i, doc.t1.a"));
        assertThat(orderBy(), TestingHelpers.isSQL("doc.t1.a"));
        assertThat(left.collectPhase().projections(), Matchers.contains(ProjectionMatchers.isTopN(10, 2), Matchers.instanceOf(EvalProjection.class)));
        Collect right = ((Collect) (join.right()));
        assertThat("1 node, otherwise mergePhases would be required", right.nodeIds().size(), Matchers.is(1));
        assertThat(orderBy(), TestingHelpers.isSQL("doc.t2.i DESC"));
        assertThat(right.collectPhase().projections(), Matchers.contains(ProjectionMatchers.isTopN(5, 5)));
        List<Projection> nlProjections = join.joinPhase().projections();
        assertThat(nlProjections, Matchers.contains(Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(AggregationProjection.class)));
    }

    @Test
    public void testJoinWithAggregationOnSubSelectsWithAggregations() throws Exception {
        Join nl = e.plan(("select t1.a, count(*) from " + ((((" (select a, count(*) as cnt from t1 group by a) t1 " + "join") + " (select distinct i from t2) t2 ") + "on t1.cnt = t2.i::long ") + "group by t1.a")));
        assertThat(nl.joinPhase().projections(), Matchers.contains(Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(GroupProjection.class)));
        assertThat(nl.left(), Matchers.instanceOf(Collect.class));
        Collect leftPlan = ((Collect) (nl.left()));
        assertThat(leftPlan.collectPhase().projections().size(), Matchers.is(2));
        assertThat(leftPlan.collectPhase().projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(leftPlan.collectPhase().projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        assertThat(leftPlan.collectPhase().projections().get(1), Matchers.instanceOf(GroupProjection.class));
        assertThat(leftPlan.collectPhase().projections().get(1).requiredGranularity(), Matchers.is(NODE));
        Collect rightPlan = ((Collect) (nl.right()));
        assertThat(rightPlan.collectPhase().projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(GroupProjection.class)));
    }
}

