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
package io.crate.planner.consumer;


import AggregateMode.ITER_FINAL;
import AggregateMode.ITER_PARTIAL;
import AggregateMode.PARTIAL_FINAL;
import DataTypes.DOUBLE;
import DataTypes.LONG;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import RowGranularity.DOC;
import RowGranularity.SHARD;
import SymbolType.INPUT_COLUMN;
import com.google.common.collect.Iterables;
import io.crate.execution.dsl.phases.CollectPhase;
import io.crate.execution.dsl.phases.MergePhase;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.dsl.projection.EvalProjection;
import io.crate.execution.dsl.projection.FilterProjection;
import io.crate.execution.dsl.projection.GroupProjection;
import io.crate.execution.dsl.projection.OrderedTopNProjection;
import io.crate.execution.dsl.projection.Projection;
import io.crate.execution.dsl.projection.TopNProjection;
import io.crate.expression.symbol.Aggregation;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Symbol;
import io.crate.expression.symbol.Symbols;
import io.crate.metadata.Reference;
import io.crate.planner.ExecutionPlan;
import io.crate.planner.Merge;
import io.crate.planner.PositionalOrderBy;
import io.crate.planner.node.dql.Collect;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.types.DataType;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class GroupByPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testGroupByWithAggregationStringLiteralArguments() {
        Merge distributedGroupByMerge = e.plan("select count('foo'), name from users group by name");
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collectPhase()));
        assertThat(collectPhase.toCollect(), Matchers.contains(SymbolMatchers.isReference("name")));
        GroupProjection groupProjection = ((GroupProjection) (collectPhase.projections().get(0)));
        assertThat(groupProjection.values().get(0), SymbolMatchers.isAggregation("count"));
    }

    @Test
    public void testGroupByWithAggregationPlan() throws Exception {
        Merge distributedGroupByMerge = e.plan("select count(*), name from users group by name");
        Merge reducerMerge = ((Merge) (distributedGroupByMerge.subPlan()));
        // distributed collect
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collectPhase()));
        assertThat(collectPhase.maxRowGranularity(), Matchers.is(DOC));
        assertThat(collectPhase.nodeIds().size(), Matchers.is(2));
        assertThat(collectPhase.toCollect().size(), Matchers.is(1));
        assertThat(collectPhase.projections().size(), Matchers.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.outputTypes().size(), Matchers.is(2));
        assertEquals(STRING, collectPhase.outputTypes().get(0));
        assertEquals(CountAggregation.LongStateType.INSTANCE, collectPhase.outputTypes().get(1));
        MergePhase mergePhase = reducerMerge.mergePhase();
        assertThat(mergePhase.numUpstreams(), Matchers.is(2));
        assertThat(mergePhase.nodeIds().size(), Matchers.is(2));
        assertEquals(mergePhase.inputTypes(), collectPhase.outputTypes());
        // for function evaluation and column-reordering there is always a EvalProjection
        assertThat(mergePhase.projections().size(), Matchers.is(2));
        assertThat(mergePhase.projections().get(1), Matchers.instanceOf(EvalProjection.class));
        assertThat(mergePhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        GroupProjection groupProjection = ((GroupProjection) (mergePhase.projections().get(0)));
        InputColumn inputColumn = ((InputColumn) (groupProjection.values().get(0).inputs().get(0)));
        assertThat(inputColumn.index(), Matchers.is(1));
        assertThat(mergePhase.outputTypes().size(), Matchers.is(2));
        assertEquals(LONG, mergePhase.outputTypes().get(0));
        assertEquals(STRING, mergePhase.outputTypes().get(1));
        MergePhase localMerge = distributedGroupByMerge.mergePhase();
        assertThat(localMerge.numUpstreams(), Matchers.is(2));
        assertThat(localMerge.nodeIds().size(), Matchers.is(1));
        assertThat(Iterables.getOnlyElement(localMerge.nodeIds()), Matchers.is(NODE_ID));
        assertEquals(mergePhase.outputTypes(), localMerge.inputTypes());
        assertThat(localMerge.projections(), Matchers.empty());
    }

    @Test
    public void testGroupByWithAggregationAndLimit() throws Exception {
        Merge distributedGroupByMerge = e.plan("select count(*), name from users group by name limit 1 offset 1");
        Merge reducerMerge = ((Merge) (distributedGroupByMerge.subPlan()));
        // distributed merge
        MergePhase distributedMergePhase = reducerMerge.mergePhase();
        assertThat(distributedMergePhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(distributedMergePhase.projections().get(1), Matchers.instanceOf(TopNProjection.class));
        // limit must include offset because the real limit can only be applied on the handler
        // after all rows have been gathered.
        TopNProjection topN = ((TopNProjection) (distributedMergePhase.projections().get(1)));
        assertThat(topN.limit(), Matchers.is(2));
        assertThat(topN.offset(), Matchers.is(0));
        // local merge
        MergePhase localMergePhase = distributedGroupByMerge.mergePhase();
        assertThat(localMergePhase.projections().get(0), Matchers.instanceOf(TopNProjection.class));
        topN = ((TopNProjection) (localMergePhase.projections().get(0)));
        assertThat(topN.limit(), Matchers.is(1));
        assertThat(topN.offset(), Matchers.is(1));
        assertThat(topN.outputs().get(0), Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(0));
        assertThat(topN.outputs().get(1), Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(1));
    }

    @Test
    public void testGroupByOnNodeLevel() throws Exception {
        Collect collect = e.plan("select count(*), name from sys.nodes group by name");
        assertThat("number of nodeIds must be 1, otherwise there must be a merge", collect.resultDescription().nodeIds().size(), Matchers.is(1));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class)));
        GroupProjection groupProjection = ((GroupProjection) (collectPhase.projections().get(0)));
        assertThat(Symbols.typeView(groupProjection.outputs()), Matchers.contains(Matchers.is(STRING), Matchers.is(LONG)));
        assertThat(Symbols.typeView(collectPhase.projections().get(1).outputs()), Matchers.contains(Matchers.is(LONG), Matchers.is(STRING)));
    }

    @Test
    public void testNonDistributedGroupByOnClusteredColumn() throws Exception {
        Merge merge = e.plan("select count(*), id from users group by id limit 20");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections(), // swaps id, count(*) output from group by to count(*), id
        Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(TopNProjection.class), Matchers.instanceOf(EvalProjection.class)));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        MergePhase mergePhase = merge.mergePhase();
        assertThat(mergePhase.projections(), Matchers.contains(Matchers.instanceOf(TopNProjection.class)));
    }

    @Test
    public void testNonDistributedGroupByOnClusteredColumnSorted() throws Exception {
        Merge merge = e.plan("select count(*), id from users group by id order by 1 desc nulls last limit 20");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        List<Projection> collectProjections = collectPhase.projections();
        assertThat(collectProjections, // swap id, count(*) -> count(*), id
        Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(EvalProjection.class)));
        assertThat(collectProjections.get(1), Matchers.instanceOf(OrderedTopNProjection.class));
        assertThat(orderBy().size(), Matchers.is(1));
        assertThat(collectProjections.get(0).requiredGranularity(), Matchers.is(SHARD));
        MergePhase mergePhase = merge.mergePhase();
        assertThat(mergePhase.projections(), Matchers.contains(Matchers.instanceOf(TopNProjection.class)));
        PositionalOrderBy positionalOrderBy = mergePhase.orderByPositions();
        assertThat(positionalOrderBy, Matchers.notNullValue());
        assertThat(positionalOrderBy.indices().length, Matchers.is(1));
        assertThat(positionalOrderBy.indices()[0], Matchers.is(0));
        assertThat(positionalOrderBy.reverseFlags()[0], Matchers.is(true));
        assertThat(positionalOrderBy.nullsFirst()[0], Matchers.is(false));
    }

    @Test
    public void testNonDistributedGroupByOnClusteredColumnSortedScalar() throws Exception {
        Merge merge = e.plan("select count(*) + 1, id from users group by id order by count(*) + 1 limit 20");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(EvalProjection.class)));
        assertThat(orderBy().size(), Matchers.is(1));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        MergePhase mergePhase = merge.mergePhase();
        assertThat(mergePhase.projections(), Matchers.contains(Matchers.instanceOf(TopNProjection.class)));
        PositionalOrderBy positionalOrderBy = mergePhase.orderByPositions();
        assertThat(positionalOrderBy, Matchers.notNullValue());
        assertThat(positionalOrderBy.indices().length, Matchers.is(1));
        assertThat(positionalOrderBy.indices()[0], Matchers.is(0));
        assertThat(positionalOrderBy.reverseFlags()[0], Matchers.is(false));
        assertThat(positionalOrderBy.nullsFirst()[0], Matchers.nullValue());
    }

    @Test
    public void testGroupByWithOrderOnAggregate() throws Exception {
        Merge merge = e.plan("select count(*), name from users group by name order by count(*)");
        assertThat(merge.mergePhase().orderByPositions(), Matchers.notNullValue());
        Merge reducerMerge = ((Merge) (merge.subPlan()));
        MergePhase mergePhase = reducerMerge.mergePhase();
        assertThat(mergePhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(EvalProjection.class)));
        OrderedTopNProjection topNProjection = ((OrderedTopNProjection) (mergePhase.projections().get(1)));
        Symbol orderBy = topNProjection.orderBy().get(0);
        assertThat(orderBy, Matchers.instanceOf(InputColumn.class));
        assertThat(orderBy.valueType(), Is.is(LONG));
    }

    @Test
    public void testHandlerSideRoutingGroupBy() throws Exception {
        Collect collect = e.plan("select count(*) from sys.cluster group by name");
        // just testing the dispatching here.. making sure it is not a ESSearchNode
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.toCollect().get(0), Matchers.instanceOf(Reference.class));
        assertThat(collectPhase.toCollect().size(), Matchers.is(1));
        assertThat(collectPhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class)));
    }

    @Test
    public void testCountDistinctWithGroupBy() throws Exception {
        Merge distributedGroupByMerge = e.plan("select count(distinct id), name from users group by name order by count(distinct id)");
        Merge reducerMerge = ((Merge) (distributedGroupByMerge.subPlan()));
        CollectPhase collectPhase = ((Collect) (reducerMerge.subPlan())).collectPhase();
        // collect
        assertThat(collectPhase.toCollect().get(0), Matchers.instanceOf(Reference.class));
        assertThat(collectPhase.toCollect().size(), Matchers.is(2));
        assertThat(column().name(), Matchers.is("id"));
        assertThat(column().name(), Matchers.is("name"));
        Projection projection = collectPhase.projections().get(0);
        assertThat(projection, Matchers.instanceOf(GroupProjection.class));
        GroupProjection groupProjection = ((GroupProjection) (projection));
        Symbol groupKey = groupProjection.keys().get(0);
        assertThat(groupKey, Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(1));
        assertThat(groupProjection.values().size(), Matchers.is(1));
        assertThat(groupProjection.mode(), Matchers.is(ITER_PARTIAL));
        Aggregation aggregation = groupProjection.values().get(0);
        Symbol aggregationInput = aggregation.inputs().get(0);
        assertThat(aggregationInput.symbolType(), Matchers.is(INPUT_COLUMN));
        // reducer
        MergePhase mergePhase = reducerMerge.mergePhase();
        assertThat(mergePhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(OrderedTopNProjection.class), Matchers.instanceOf(EvalProjection.class)));
        Projection groupProjection1 = mergePhase.projections().get(0);
        groupProjection = ((GroupProjection) (groupProjection1));
        assertThat(groupProjection.keys().get(0), Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(0));
        assertThat(groupProjection.mode(), Matchers.is(PARTIAL_FINAL));
        assertThat(groupProjection.values().get(0), Matchers.instanceOf(Aggregation.class));
        OrderedTopNProjection topNProjection = ((OrderedTopNProjection) (mergePhase.projections().get(1)));
        Symbol collection_count = topNProjection.outputs().get(0);
        assertThat(collection_count, SymbolMatchers.isInputColumn(0));
        // handler
        MergePhase localMergeNode = distributedGroupByMerge.mergePhase();
        assertThat(localMergeNode.projections(), Matchers.emptyIterable());
    }

    @Test
    public void testGroupByHavingNonDistributed() throws Exception {
        Merge merge = e.plan("select id from users group by id having id > 0");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(FilterProjection.class)));
        FilterProjection filterProjection = ((FilterProjection) (collectPhase.projections().get(1)));
        assertThat(filterProjection.requiredGranularity(), Matchers.is(SHARD));
        assertThat(filterProjection.outputs().size(), Matchers.is(1));
        assertThat(filterProjection.outputs().get(0), Matchers.instanceOf(InputColumn.class));
        InputColumn inputColumn = ((InputColumn) (filterProjection.outputs().get(0)));
        assertThat(inputColumn.index(), Matchers.is(0));
        MergePhase localMergeNode = merge.mergePhase();
        assertThat(localMergeNode.projections(), Matchers.empty());
    }

    @Test
    public void testGroupByWithHavingAndLimit() throws Exception {
        Merge planNode = e.plan("select count(*), name from users group by name having count(*) > 1 limit 100");
        Merge reducerMerge = ((Merge) (planNode.subPlan()));
        MergePhase mergePhase = reducerMerge.mergePhase();// reducer

        Projection projection = mergePhase.projections().get(1);
        assertThat(projection, Matchers.instanceOf(FilterProjection.class));
        FilterProjection filterProjection = ((FilterProjection) (projection));
        Symbol countArgument = arguments().get(0);
        assertThat(countArgument, Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(1));// pointing to second output from group projection

        // outputs: name, count(*)
        TopNProjection topN = ((TopNProjection) (mergePhase.projections().get(2)));
        assertThat(topN.outputs().get(0).valueType(), Is.is(STRING));
        assertThat(topN.outputs().get(1).valueType(), Is.<DataType>is(LONG));
        MergePhase localMerge = planNode.mergePhase();
        // topN projection
        // outputs: count(*), name
        topN = ((TopNProjection) (localMerge.projections().get(0)));
        assertThat(topN.outputs().get(0).valueType(), Is.<DataType>is(LONG));
        assertThat(topN.outputs().get(1).valueType(), Is.<DataType>is(STRING));
    }

    @Test
    public void testGroupByWithHavingAndNoLimit() throws Exception {
        Merge planNode = e.plan("select count(*), name from users group by name having count(*) > 1");
        Merge reducerMerge = ((Merge) (planNode.subPlan()));
        MergePhase mergePhase = reducerMerge.mergePhase();// reducer

        // group projection
        // outputs: name, count(*)
        Projection projection = mergePhase.projections().get(1);
        assertThat(projection, Matchers.instanceOf(FilterProjection.class));
        FilterProjection filterProjection = ((FilterProjection) (projection));
        Symbol countArgument = arguments().get(0);
        assertThat(countArgument, Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(1));// pointing to second output from group projection

        assertThat(mergePhase.outputTypes().get(0), Matchers.equalTo(LONG));
        assertThat(mergePhase.outputTypes().get(1), Matchers.equalTo(STRING));
        mergePhase = planNode.mergePhase();
        assertThat(mergePhase.outputTypes().get(0), Matchers.equalTo(LONG));
        assertThat(mergePhase.outputTypes().get(1), Matchers.equalTo(STRING));
    }

    @Test
    public void testGroupByWithHavingAndNoSelectListReordering() throws Exception {
        Merge planNode = e.plan("select name, count(*) from users group by name having count(*) > 1");
        Merge reducerMerge = ((Merge) (planNode.subPlan()));
        MergePhase reduceMergePhase = reducerMerge.mergePhase();
        // group projection
        // outputs: name, count(*)
        // filter projection
        // outputs: name, count(*)
        assertThat(reduceMergePhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(FilterProjection.class)));
        Projection projection = reduceMergePhase.projections().get(1);
        assertThat(projection, Matchers.instanceOf(FilterProjection.class));
        FilterProjection filterProjection = ((FilterProjection) (projection));
        Symbol countArgument = arguments().get(0);
        assertThat(countArgument, Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(1));// pointing to second output from group projection

        // outputs: name, count(*)
        assertThat(index(), Matchers.is(0));
        assertThat(index(), Matchers.is(1));
        MergePhase localMerge = planNode.mergePhase();
        assertThat(localMerge.projections(), Matchers.empty());
    }

    @Test
    public void testGroupByHavingAndNoSelectListReOrderingWithLimit() throws Exception {
        Merge planNode = e.plan("select name, count(*) from users group by name having count(*) > 1 limit 100");
        Merge reducerMerge = ((Merge) (planNode.subPlan()));
        MergePhase reducePhase = reducerMerge.mergePhase();
        // group projection
        // outputs: name, count(*)
        // filter projection
        // outputs: name, count(*)
        // topN projection
        // outputs: name, count(*)
        Projection projection = reducePhase.projections().get(1);
        assertThat(projection, Matchers.instanceOf(FilterProjection.class));
        FilterProjection filterProjection = ((FilterProjection) (projection));
        Symbol countArgument = arguments().get(0);
        assertThat(countArgument, Matchers.instanceOf(InputColumn.class));
        assertThat(index(), Matchers.is(1));// pointing to second output from group projection

        // outputs: name, count(*)
        assertThat(index(), Matchers.is(0));
        assertThat(index(), Matchers.is(1));
        // outputs: name, count(*)
        TopNProjection topN = ((TopNProjection) (reducePhase.projections().get(2)));
        assertThat(index(), Matchers.is(0));
        assertThat(index(), Matchers.is(1));
        MergePhase localMerge = planNode.mergePhase();
        // topN projection
        // outputs: name, count(*)
        topN = ((TopNProjection) (localMerge.projections().get(0)));
        assertThat(index(), Matchers.is(0));
        assertThat(index(), Matchers.is(1));
    }

    @Test
    public void testGroupByOnAnalyzed() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot GROUP BY 'text': grouping on analyzed/fulltext columns is not possible");
        e.plan("select text from users u group by 1");
    }

    @Test
    public void testSelectAnalyzedReferenceInFunctionGroupBy() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot GROUP BY 'text': grouping on analyzed/fulltext columns is not possible");
        e.plan("select substr(text, 0, 2) from users u group by 1");
    }

    @Test
    public void testDistributedGroupByProjectionHasShardLevelGranularity() throws Exception {
        Merge distributedGroupByMerge = e.plan("select count(*) from users group by name");
        Merge reduceMerge = ((Merge) (distributedGroupByMerge.subPlan()));
        CollectPhase collectPhase = ((Collect) (reduceMerge.subPlan())).collectPhase();
        assertThat(collectPhase.projections().size(), Matchers.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
    }

    @Test
    public void testNonDistributedGroupByProjectionHasShardLevelGranularity() throws Exception {
        Merge distributedGroupByMerge = e.plan(("select count(distinct id), name from users" + " group by name order by count(distinct id)"));
        Merge reduceMerge = ((Merge) (distributedGroupByMerge.subPlan()));
        CollectPhase collectPhase = ((Collect) (reduceMerge.subPlan())).collectPhase();
        assertThat(collectPhase.projections().size(), Matchers.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
    }

    @Test
    public void testNoDistributedGroupByOnAllPrimaryKeys() throws Exception {
        Collect collect = e.plan("select count(*), id, date from empty_parted group by id, date limit 20");
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections().size(), Matchers.is(3));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        assertThat(collectPhase.projections().get(1), Matchers.instanceOf(TopNProjection.class));
        assertThat(collectPhase.projections().get(2), Matchers.instanceOf(EvalProjection.class));
    }

    @Test
    public void testNonDistributedGroupByAggregationsWrappedInScalar() throws Exception {
        Collect collect = e.plan("select (count(*) + 1), id from empty_parted group by id");
        CollectPhase collectPhase = collect.collectPhase();
        assertThat(collectPhase.projections(), // shard level
        // node level
        // count(*) + 1
        Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class)));
    }

    @Test
    public void testGroupByHaving() throws Exception {
        Merge distributedGroupByMerge = e.plan("select avg(date), name from users group by name having min(date) > '1970-01-01'");
        Merge reduceMerge = ((Merge) (distributedGroupByMerge.subPlan()));
        CollectPhase collectPhase = ((Collect) (reduceMerge.subPlan())).collectPhase();
        assertThat(collectPhase.projections().size(), Matchers.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        MergePhase reducePhase = reduceMerge.mergePhase();
        assertThat(reducePhase.projections().size(), Matchers.is(3));
        // grouping
        assertThat(reducePhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        GroupProjection groupProjection = ((GroupProjection) (reducePhase.projections().get(0)));
        assertThat(groupProjection.values().size(), Matchers.is(2));
        // filter the having clause
        assertThat(reducePhase.projections().get(1), Matchers.instanceOf(FilterProjection.class));
        FilterProjection filterProjection = ((FilterProjection) (reducePhase.projections().get(1)));
        assertThat(reducePhase.projections().get(2), Matchers.instanceOf(EvalProjection.class));
        EvalProjection eval = ((EvalProjection) (reducePhase.projections().get(2)));
        assertThat(eval.outputs().get(0).valueType(), Is.<DataType>is(DOUBLE));
        assertThat(eval.outputs().get(1).valueType(), Is.<DataType>is(STRING));
    }

    @Test
    public void testNestedGroupByAggregation() throws Exception {
        Collect collect = e.plan(("select count(*) from (" + ((("  select max(load['1']) as maxLoad, hostname " + "  from sys.nodes ") + "  group by hostname having max(load['1']) > 50) as nodes ") + "group by hostname")));
        assertThat("would require merge if more than 1 nodeIds", collect.nodeIds().size(), Matchers.is(1));
        CollectPhase collectPhase = collect.collectPhase();
        assertThat(collectPhase.projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(FilterProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class)));
        Projection firstGroupProjection = collectPhase.projections().get(0);
        assertThat(mode(), Matchers.is(ITER_FINAL));
        Projection secondGroupProjection = collectPhase.projections().get(3);
        assertThat(mode(), Matchers.is(ITER_FINAL));
    }

    @Test
    public void testGroupByOnClusteredByColumnPartitionedOnePartition() throws Exception {
        // only one partition hit
        Merge optimizedPlan = e.plan("select count(*), city from clustered_parted where date=1395874800000 group by city");
        Collect collect = ((Collect) (optimizedPlan.subPlan()));
        assertThat(collect.collectPhase().projections(), Matchers.contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class)));
        assertThat(collect.collectPhase().projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(optimizedPlan.mergePhase().projections().size(), Matchers.is(0));
        // > 1 partition hit
        ExecutionPlan executionPlan = e.plan("select count(*), city from clustered_parted where date=1395874800000 or date=1395961200000 group by city");
        assertThat(executionPlan, Matchers.instanceOf(Merge.class));
        assertThat(subPlan(), Matchers.instanceOf(Merge.class));
    }

    @Test
    public void testGroupByOrderByPartitionedClolumn() throws Exception {
        Merge plan = e.plan("select date from clustered_parted group by date order by date");
        Merge reduceMerge = ((Merge) (plan.subPlan()));
        OrderedTopNProjection topNProjection = ((OrderedTopNProjection) (reduceMerge.mergePhase().projections().get(1)));
        Symbol orderBy = topNProjection.orderBy().get(0);
        assertThat(orderBy, Matchers.instanceOf(InputColumn.class));
        assertThat(orderBy.valueType(), Matchers.is(TIMESTAMP));
    }
}

