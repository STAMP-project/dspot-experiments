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


import DataTypes.STRING;
import Row.EMPTY;
import UpdatePlanner.Update;
import io.crate.execution.dsl.phases.MergePhase;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.dsl.projection.MergeCountProjection;
import io.crate.execution.dsl.projection.UpdateProjection;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Reference;
import io.crate.metadata.TransactionContext;
import io.crate.planner.consumer.UpdatePlanner;
import io.crate.planner.node.dml.UpdateById;
import io.crate.planner.node.dql.Collect;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class UpdatePlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testUpdateByQueryPlan() throws Exception {
        UpdatePlanner.Update plan = e.plan("update users set name='Vogon lyric fan'");
        Merge merge = ((Merge) (plan.createExecutionPlan.create(e.getPlannerContext(clusterService.state()), EMPTY, SubQueryResults.EMPTY)));
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.where(), TestingHelpers.isSQL("true"));
        assertThat(collectPhase.projections().size(), Is.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(UpdateProjection.class));
        assertThat(collectPhase.toCollect().size(), Is.is(1));
        assertThat(collectPhase.toCollect().get(0), Matchers.instanceOf(Reference.class));
        assertThat(column().fqn(), Is.is("_id"));
        UpdateProjection updateProjection = ((UpdateProjection) (collectPhase.projections().get(0)));
        assertThat(updateProjection.uidSymbol(), Matchers.instanceOf(InputColumn.class));
        assertThat(updateProjection.assignmentsColumns()[0], Is.is("name"));
        Symbol symbol = updateProjection.assignments()[0];
        assertThat(symbol, SymbolMatchers.isLiteral("Vogon lyric fan", STRING));
        MergePhase mergePhase = merge.mergePhase();
        assertThat(mergePhase.projections().size(), Is.is(1));
        assertThat(mergePhase.projections().get(0), Matchers.instanceOf(MergeCountProjection.class));
        assertThat(mergePhase.outputTypes().size(), Is.is(1));
    }

    @Test
    public void testUpdateByIdPlan() throws Exception {
        UpdateById updateById = e.plan("update users set name='Vogon lyric fan' where id=1");
        assertThat(updateById.assignmentByTargetCol().keySet(), Matchers.contains(SymbolMatchers.isReference("name")));
        assertThat(updateById.assignmentByTargetCol().values(), Matchers.contains(SymbolMatchers.isLiteral("Vogon lyric fan")));
        assertThat(updateById.docKeys().size(), Is.is(1));
        assertThat(updateById.docKeys().getOnlyKey().getId(txnCtx, e.functions(), EMPTY, SubQueryResults.EMPTY), Is.is("1"));
    }

    @Test
    public void testUpdatePlanWithMultiplePrimaryKeyValues() throws Exception {
        UpdateById update = e.plan("update users set name='Vogon lyric fan' where id in (1,2,3)");
        assertThat(update.docKeys().size(), Is.is(3));
    }

    @Test
    public void testUpdatePlanWithMultiplePrimaryKeyValuesPartitioned() throws Exception {
        Plan update = e.plan(("update parted_pks set name='Vogon lyric fan' where " + ("(id=2 and date = 0) OR" + "(id=3 and date=123)")));
        assertThat(update, Matchers.instanceOf(UpdateById.class));
        assertThat(docKeys().size(), Is.is(2));
    }

    @Test
    public void testUpdateOnEmptyPartitionedTable() throws Exception {
        UpdatePlanner.Update update = e.plan("update empty_parted set name='Vogon lyric fan'");
        Collect collect = ((Collect) (update.createExecutionPlan.create(e.getPlannerContext(clusterService.state()), EMPTY, SubQueryResults.EMPTY)));
        assertThat(routing().nodes(), Matchers.emptyIterable());
    }
}

