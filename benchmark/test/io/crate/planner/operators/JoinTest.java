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
package io.crate.planner.operators;


import DataTypes.LONG;
import Row.EMPTY;
import TableStats.Stats;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import io.crate.analyze.MultiSourceSelect;
import io.crate.analyze.TableDefinitions;
import io.crate.execution.dsl.phases.HashJoinPhase;
import io.crate.execution.dsl.phases.NestedLoopPhase;
import io.crate.execution.dsl.projection.builder.ProjectionBuilder;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Functions;
import io.crate.metadata.RelationName;
import io.crate.planner.ExecutionPlan;
import io.crate.planner.PlannerContext;
import io.crate.planner.SubqueryPlanner;
import io.crate.planner.TableStats;
import io.crate.planner.node.dql.Collect;
import io.crate.planner.node.dql.join.Join;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.T3;
import io.crate.testing.TestingHelpers;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Test;


public class JoinTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    private Functions functions = TestingHelpers.getFunctions();

    private ProjectionBuilder projectionBuilder = new ProjectionBuilder(functions);

    private PlannerContext plannerCtx;

    private CoordinatorTxnCtx txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testNestedLoop_TablesAreSwitchedIfLeftIsSmallerThanRight() {
        txnCtx.sessionContext().setHashJoinEnabled(false);
        MultiSourceSelect mss = e.normalize("select * from users, locations where users.id = locations.id");
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> rowCountByTable = new ObjectObjectHashMap();
        rowCountByTable.put(TableDefinitions.USER_TABLE_IDENT, new TableStats.Stats(10, 0));
        rowCountByTable.put(TableDefinitions.TEST_DOC_LOCATIONS_TABLE_IDENT, new TableStats.Stats(10000, 0));
        tableStats.updateTableStats(rowCountByTable);
        Join nl = plan(mss, tableStats);
        assertThat(ident().tableIdent().name(), Matchers.is("locations"));
        rowCountByTable.put(TableDefinitions.USER_TABLE_IDENT, new TableStats.Stats(10000, 0));
        rowCountByTable.put(TableDefinitions.TEST_DOC_LOCATIONS_TABLE_IDENT, new TableStats.Stats(10, 0));
        tableStats.updateTableStats(rowCountByTable);
        nl = plan(mss, tableStats);
        assertThat(ident().tableIdent().name(), Matchers.is("users"));
    }

    @Test
    public void testNestedLoop_TablesAreNotSwitchedIfLeftHasAPushedDownOrderBy() {
        txnCtx.sessionContext().setHashJoinEnabled(false);
        // we use a subselect to simulate the pushed-down order by
        MultiSourceSelect mss = e.normalize(("select users.id from (select id from users order by id) users, " + "locations where users.id = locations.id"));
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> rowCountByTable = new ObjectObjectHashMap();
        rowCountByTable.put(TableDefinitions.USER_TABLE_IDENT, new TableStats.Stats(10, 0));
        rowCountByTable.put(TableDefinitions.TEST_DOC_LOCATIONS_TABLE_IDENT, new TableStats.Stats(100000, 0));
        tableStats.updateTableStats(rowCountByTable);
        PlannerContext context = e.getPlannerContext(clusterService.state());
        LogicalPlanner logicalPlanner = new LogicalPlanner(functions, tableStats);
        SubqueryPlanner subqueryPlanner = new SubqueryPlanner(( s) -> logicalPlanner.planSubSelect(s, context));
        LogicalPlan operator = JoinPlanBuilder.createNodes(mss, mss.where(), subqueryPlanner, e.functions(), txnCtx).build(tableStats, Collections.emptySet());
        Join nl = ((Join) (operator.build(context, projectionBuilder, (-1), 0, null, null, EMPTY, SubQueryResults.EMPTY)));
        assertThat(collectPhase().toCollect(), TestingHelpers.isSQL("doc.users.id"));
        assertThat(nl.resultDescription().orderBy(), Matchers.notNullValue());
    }

    @Test
    public void testNestedLoop_TablesAreNotSwitchedAfterOrderByPushDown() {
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> rowCountByTable = new ObjectObjectHashMap();
        rowCountByTable.put(TableDefinitions.USER_TABLE_IDENT, new TableStats.Stats(10, 0));
        rowCountByTable.put(TableDefinitions.TEST_DOC_LOCATIONS_TABLE_IDENT, new TableStats.Stats(100000, 0));
        tableStats.updateTableStats(rowCountByTable);
        PlannerContext context = e.getPlannerContext(clusterService.state());
        context.transactionContext().sessionContext().setHashJoinEnabled(false);
        LogicalPlanner logicalPlanner = new LogicalPlanner(functions, tableStats);
        LogicalPlan plan = logicalPlanner.plan(e.analyze(("select users.id from users, locations " + "where users.id = locations.id order by users.id")), context);
        Join nl = ((Join) (plan.build(context, projectionBuilder, (-1), 0, null, null, EMPTY, SubQueryResults.EMPTY)));
        assertThat(collectPhase().toCollect(), TestingHelpers.isSQL("doc.users.id"));
        assertThat(nl.resultDescription().orderBy(), Matchers.notNullValue());
    }

    @Test
    public void testHashJoin_TableOrderInLogicalAndExecutionPlan() {
        MultiSourceSelect mss = e.normalize(("select users.name, locations.id " + ("from users " + "join locations on users.id = locations.id")));
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> rowCountByTable = new ObjectObjectHashMap();
        rowCountByTable.put(TableDefinitions.USER_TABLE_IDENT, new TableStats.Stats(100, 0));
        rowCountByTable.put(TableDefinitions.TEST_DOC_LOCATIONS_TABLE_IDENT, new TableStats.Stats(10, 0));
        tableStats.updateTableStats(rowCountByTable);
        LogicalPlan operator = createLogicalPlan(mss, tableStats);
        assertThat(operator, Matchers.instanceOf(HashJoin.class));
        assertThat(((HashJoin) (operator)).concreteRelation.toString(), Matchers.is("QueriedTable{DocTableRelation{doc.locations}}"));
        Join join = buildJoin(operator);
        assertThat(join.joinPhase().leftMergePhase().inputTypes(), contains(LONG, LONG));
        assertThat(join.joinPhase().rightMergePhase().inputTypes(), contains(LONG));
        assertThat(join.joinPhase().projections().get(0).outputs().toString(), Matchers.is("[IC{0, long}, IC{1, long}, IC{2, long}]"));
    }

    @Test
    public void testHashJoin_TablesSwitchWhenRightBiggerThanLeft() {
        MultiSourceSelect mss = e.normalize(("select users.name, locations.id " + ("from users " + "join locations on users.id = locations.id")));
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> rowCountByTable = new ObjectObjectHashMap();
        rowCountByTable.put(TableDefinitions.USER_TABLE_IDENT, new TableStats.Stats(10, 0));
        rowCountByTable.put(TableDefinitions.TEST_DOC_LOCATIONS_TABLE_IDENT, new TableStats.Stats(100, 0));
        tableStats.updateTableStats(rowCountByTable);
        LogicalPlan operator = createLogicalPlan(mss, tableStats);
        assertThat(operator, Matchers.instanceOf(HashJoin.class));
        assertThat(((HashJoin) (operator)).concreteRelation.toString(), Matchers.is("QueriedTable{DocTableRelation{doc.locations}}"));
        Join join = buildJoin(operator);
        // Plans must be switched (left<->right)
        assertThat(join.joinPhase().leftMergePhase().inputTypes(), Matchers.contains(LONG));
        assertThat(join.joinPhase().rightMergePhase().inputTypes(), Matchers.contains(LONG, LONG));
        assertThat(join.joinPhase().projections().get(0).outputs().toString(), Matchers.is("[IC{1, long}, IC{2, long}, IC{0, long}]"));
    }

    @Test
    public void testMultipleHashJoins() {
        MultiSourceSelect mss = e.normalize(("select * " + ("from t1 inner join t2 on t1.a = t2.b " + "inner join t3 on t3.c = t2.b")));
        LogicalPlan operator = createLogicalPlan(mss, new TableStats());
        assertThat(operator, Matchers.instanceOf(HashJoin.class));
        LogicalPlan leftPlan = ((HashJoin) (operator)).lhs;
        assertThat(leftPlan, Matchers.instanceOf(HashJoin.class));
        Join join = buildJoin(operator);
        assertThat(join.joinPhase(), Matchers.instanceOf(HashJoinPhase.class));
        assertThat(join.left(), Matchers.instanceOf(Join.class));
        assertThat(joinPhase(), Matchers.instanceOf(HashJoinPhase.class));
    }

    @Test
    public void testMixedHashJoinNestedLoop() {
        MultiSourceSelect mss = e.normalize(("select * " + ("from t1 inner join t2 on t1.a = t2.b " + "left join t3 on t3.c = t2.b")));
        LogicalPlan operator = createLogicalPlan(mss, new TableStats());
        assertThat(operator, Matchers.instanceOf(NestedLoopJoin.class));
        LogicalPlan leftPlan = ((NestedLoopJoin) (operator)).lhs;
        assertThat(leftPlan, Matchers.instanceOf(HashJoin.class));
        Join join = buildJoin(operator);
        assertThat(join.joinPhase(), Matchers.instanceOf(NestedLoopPhase.class));
        assertThat(join.left(), Matchers.instanceOf(Join.class));
        assertThat(joinPhase(), Matchers.instanceOf(HashJoinPhase.class));
    }

    @Test
    public void testBlockNestedLoopWhenTableSizeUnknownAndOneExecutionNode() {
        MultiSourceSelect mss = e.normalize("select * from t1, t4");
        LogicalPlan operator = createLogicalPlan(mss, new TableStats());
        assertThat(operator, Matchers.instanceOf(NestedLoopJoin.class));
        Join join = buildJoin(operator);
        assertThat(join.joinPhase(), Matchers.instanceOf(NestedLoopPhase.class));
        NestedLoopPhase joinPhase = ((NestedLoopPhase) (join.joinPhase()));
        assertThat(joinPhase.blockNestedLoop, Matchers.is(true));
    }

    @Test
    public void testBlockNestedLoopWhenLeftSideIsSmallerAndOneExecutionNode() {
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> stats = new ObjectObjectHashMap();
        stats.put(T3.T1_INFO.ident(), new TableStats.Stats(23, 64));
        stats.put(T3.T4_INFO.ident(), new TableStats.Stats(42, 64));
        tableStats.updateTableStats(stats);
        e = SQLExecutor.builder(clusterService).addDocTable(T3.T1_INFO).addDocTable(T3.T4_INFO).setTableStats(tableStats).build();
        MultiSourceSelect mss = e.normalize("select * from t1, t4");
        LogicalPlan operator = createLogicalPlan(mss, tableStats);
        assertThat(operator, Matchers.instanceOf(NestedLoopJoin.class));
        Join join = buildJoin(operator);
        assertThat(join.joinPhase(), Matchers.instanceOf(NestedLoopPhase.class));
        NestedLoopPhase joinPhase = ((NestedLoopPhase) (join.joinPhase()));
        assertThat(joinPhase.blockNestedLoop, Matchers.is(true));
        assertThat(join.left(), Matchers.instanceOf(Collect.class));
        // no table switch should have been made
        assertThat(ident().tableIdent(), Matchers.is(T3.T1_INFO.ident()));
    }

    @Test
    public void testBlockNestedLoopWhenRightSideIsSmallerAndOneExecutionNode() {
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> stats = new ObjectObjectHashMap();
        stats.put(T3.T1_INFO.ident(), new TableStats.Stats(23, 64));
        stats.put(T3.T4_INFO.ident(), new TableStats.Stats(42, 64));
        tableStats.updateTableStats(stats);
        e = SQLExecutor.builder(clusterService).addDocTable(T3.T1_INFO).addDocTable(T3.T4_INFO).setTableStats(tableStats).build();
        MultiSourceSelect mss = e.normalize("select * from t4, t1");
        LogicalPlan operator = createLogicalPlan(mss, tableStats);
        assertThat(operator, Matchers.instanceOf(NestedLoopJoin.class));
        Join join = buildJoin(operator);
        assertThat(join.joinPhase(), Matchers.instanceOf(NestedLoopPhase.class));
        NestedLoopPhase joinPhase = ((NestedLoopPhase) (join.joinPhase()));
        assertThat(joinPhase.blockNestedLoop, Matchers.is(true));
        assertThat(join.left(), Matchers.instanceOf(Collect.class));
        // right side will be flipped to the left
        assertThat(ident().tableIdent(), Matchers.is(T3.T1_INFO.ident()));
    }

    @Test
    public void testNoBlockNestedLoopWithOrderBy() {
        TableStats tableStats = new TableStats();
        ObjectObjectHashMap<RelationName, TableStats.Stats> stats = new ObjectObjectHashMap();
        stats.put(T3.T1_INFO.ident(), new TableStats.Stats(23, 64));
        stats.put(T3.T4_INFO.ident(), new TableStats.Stats(42, 64));
        tableStats.updateTableStats(stats);
        e = SQLExecutor.builder(clusterService).addDocTable(T3.T1_INFO).addDocTable(T3.T4_INFO).setTableStats(tableStats).build();
        MultiSourceSelect mss = e.normalize("select * from t1, t4 order by t1.x");
        LogicalPlanner logicalPlanner = new LogicalPlanner(functions, tableStats);
        LogicalPlan operator = logicalPlanner.plan(mss, plannerCtx);
        ExecutionPlan build = operator.build(plannerCtx, projectionBuilder, (-1), 0, null, null, EMPTY, SubQueryResults.EMPTY);
        assertThat(((NestedLoopPhase) (joinPhase())).blockNestedLoop, Matchers.is(false));
    }
}

