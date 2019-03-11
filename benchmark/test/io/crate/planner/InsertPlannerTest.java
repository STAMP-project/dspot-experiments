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
import LegacyUpsertById.Item;
import io.crate.analyze.TableDefinitions;
import io.crate.exceptions.UnsupportedFeatureException;
import io.crate.execution.dsl.phases.MergePhase;
import io.crate.execution.dsl.phases.PKLookupPhase;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.dsl.projection.AggregationProjection;
import io.crate.execution.dsl.projection.ColumnIndexWriterProjection;
import io.crate.execution.dsl.projection.EvalProjection;
import io.crate.execution.dsl.projection.FilterProjection;
import io.crate.execution.dsl.projection.GroupProjection;
import io.crate.execution.dsl.projection.MergeCountProjection;
import io.crate.execution.dsl.projection.Projection;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.Reference;
import io.crate.metadata.RowGranularity;
import io.crate.planner.node.dml.LegacyUpsertById;
import io.crate.planner.node.dql.Collect;
import io.crate.planner.node.dql.join.Join;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.types.DataTypes;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class InsertPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testInsertPlan() {
        LegacyUpsertById legacyUpsertById = e.plan("insert into users (id, name) values (42, 'Deep Thought')");
        assertThat(legacyUpsertById.insertColumns().length, Is.is(2));
        Reference idRef = legacyUpsertById.insertColumns()[0];
        assertThat(idRef.column().fqn(), Is.is("id"));
        Reference nameRef = legacyUpsertById.insertColumns()[1];
        assertThat(nameRef.column().fqn(), Is.is("name"));
        assertThat(legacyUpsertById.items().size(), Is.is(1));
        LegacyUpsertById.Item item = legacyUpsertById.items().get(0);
        assertThat(item.index(), Is.is("users"));
        assertThat(item.id(), Is.is("42"));
        assertThat(item.routing(), Is.is("42"));
        assertThat(item.insertValues().length, Is.is(2));
        assertThat(item.insertValues()[0], Is.is(42L));
        assertThat(item.insertValues()[1], Is.is("Deep Thought"));
    }

    @Test
    public void testInsertPlanMultipleValues() {
        LegacyUpsertById legacyUpsertById = e.plan("insert into users (id, name) values (42, 'Deep Thought'), (99, 'Marvin')");
        assertThat(legacyUpsertById.insertColumns().length, Is.is(2));
        Reference idRef = legacyUpsertById.insertColumns()[0];
        assertThat(idRef.column().fqn(), Is.is("id"));
        Reference nameRef = legacyUpsertById.insertColumns()[1];
        assertThat(nameRef.column().fqn(), Is.is("name"));
        assertThat(legacyUpsertById.items().size(), Is.is(2));
        LegacyUpsertById.Item item1 = legacyUpsertById.items().get(0);
        assertThat(item1.index(), Is.is("users"));
        assertThat(item1.id(), Is.is("42"));
        assertThat(item1.routing(), Is.is("42"));
        assertThat(item1.insertValues().length, Is.is(2));
        assertThat(item1.insertValues()[0], Is.is(42L));
        assertThat(item1.insertValues()[1], Is.is("Deep Thought"));
        LegacyUpsertById.Item item2 = legacyUpsertById.items().get(1);
        assertThat(item2.index(), Is.is("users"));
        assertThat(item2.id(), Is.is("99"));
        assertThat(item2.routing(), Is.is("99"));
        assertThat(item2.insertValues().length, Is.is(2));
        assertThat(item2.insertValues()[0], Is.is(99L));
        assertThat(item2.insertValues()[1], Is.is("Marvin"));
    }

    @Test
    public void testInsertFromSubQueryNonDistributedGroupBy() {
        Collect nonDistributedGroupBy = e.plan("insert into users (id, name) (select count(*), name from sys.nodes group by name)");
        assertThat("nodeIds size must 1 one if there is no mergePhase", nonDistributedGroupBy.nodeIds().size(), Is.is(1));
        assertThat(nonDistributedGroupBy.collectPhase().projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
    }

    @Test
    public void testInsertFromSubQueryNonDistributedGroupByWithCast() {
        Collect nonDistributedGroupBy = e.plan("insert into users (id, name) (select name, count(*) from sys.nodes group by name)");
        assertThat("nodeIds size must 1 one if there is no mergePhase", nonDistributedGroupBy.nodeIds().size(), Is.is(1));
        assertThat(nonDistributedGroupBy.collectPhase().projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
    }

    @Test
    public void testInsertFromSubQueryDistributedGroupByWithLimit() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("Using limit, offset or order by is not supported on insert using a sub-query");
        e.plan("insert into users (id, name) (select name, count(*) from users group by name order by name limit 10)");
    }

    @Test
    public void testInsertFromSubQueryDistributedGroupByWithoutLimit() {
        Merge planNode = e.plan("insert into users (id, name) (select name, count(*) from users group by name)");
        Merge groupBy = ((Merge) (planNode.subPlan()));
        MergePhase mergePhase = groupBy.mergePhase();
        assertThat(mergePhase.projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        ColumnIndexWriterProjection projection = ((ColumnIndexWriterProjection) (mergePhase.projections().get(2)));
        assertThat(projection.primaryKeys().size(), Is.is(1));
        assertThat(projection.primaryKeys().get(0).fqn(), Is.is("id"));
        assertThat(projection.columnReferences().size(), Is.is(2));
        assertThat(projection.columnReferences().get(0).column().fqn(), Is.is("id"));
        assertThat(projection.columnReferences().get(1).column().fqn(), Is.is("name"));
        assertNotNull(projection.clusteredByIdent());
        assertThat(projection.clusteredByIdent().fqn(), Is.is("id"));
        assertThat(projection.tableIdent().fqn(), Is.is("doc.users"));
        assertThat(projection.partitionedBySymbols().isEmpty(), Is.is(true));
        MergePhase localMergeNode = planNode.mergePhase();
        assertThat(localMergeNode.projections().size(), Is.is(1));
        assertThat(localMergeNode.projections().get(0), Matchers.instanceOf(MergeCountProjection.class));
        assertThat(localMergeNode.finalProjection().get().outputs().size(), Is.is(1));
    }

    @Test
    public void testInsertFromSubQueryDistributedGroupByPartitioned() {
        Merge planNode = e.plan("insert into parted_pks (id, date) (select id, date from users group by id, date)");
        Merge groupBy = ((Merge) (planNode.subPlan()));
        MergePhase mergePhase = groupBy.mergePhase();
        assertThat(mergePhase.projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        ColumnIndexWriterProjection projection = ((ColumnIndexWriterProjection) (mergePhase.projections().get(2)));
        assertThat(projection.primaryKeys().size(), Is.is(2));
        assertThat(projection.primaryKeys().get(0).fqn(), Is.is("id"));
        assertThat(projection.primaryKeys().get(1).fqn(), Is.is("date"));
        assertThat(projection.columnReferences().size(), Is.is(1));
        assertThat(projection.columnReferences().get(0).column().fqn(), Is.is("id"));
        assertThat(projection.partitionedBySymbols().size(), Is.is(1));
        assertThat(index(), Is.is(1));
        assertNotNull(projection.clusteredByIdent());
        assertThat(projection.clusteredByIdent().fqn(), Is.is("id"));
        assertThat(projection.tableIdent().fqn(), Is.is("doc.parted_pks"));
        MergePhase localMergeNode = planNode.mergePhase();
        assertThat(localMergeNode.projections().size(), Is.is(1));
        assertThat(localMergeNode.projections().get(0), Matchers.instanceOf(MergeCountProjection.class));
        assertThat(localMergeNode.finalProjection().get().outputs().size(), Is.is(1));
    }

    @Test
    public void testInsertFromSubQueryGlobalAggregate() {
        Merge globalAggregate = e.plan("insert into users (name, id) (select arbitrary(name), count(*) from users)");
        MergePhase mergePhase = globalAggregate.mergePhase();
        assertThat(mergePhase.projections(), contains(Matchers.instanceOf(AggregationProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        assertThat(mergePhase.projections().get(1), Matchers.instanceOf(ColumnIndexWriterProjection.class));
        ColumnIndexWriterProjection projection = ((ColumnIndexWriterProjection) (mergePhase.projections().get(1)));
        assertThat(projection.columnReferences().size(), Is.is(2));
        assertThat(projection.columnReferences().get(0).column().fqn(), Is.is("name"));
        assertThat(projection.columnReferences().get(1).column().fqn(), Is.is("id"));
        assertThat(projection.columnSymbols().size(), Is.is(2));
        assertThat(index(), Is.is(0));
        assertThat(index(), Is.is(1));
        assertNotNull(projection.clusteredByIdent());
        assertThat(projection.clusteredByIdent().fqn(), Is.is("id"));
        assertThat(projection.tableIdent().fqn(), Is.is("doc.users"));
        assertThat(projection.partitionedBySymbols().isEmpty(), Is.is(true));
    }

    @Test
    public void testInsertFromSubQueryESGet() {
        Merge merge = e.plan("insert into users (date, id, name) (select date, id, name from users where id=1)");
        Collect queryAndFetch = ((Collect) (merge.subPlan()));
        PKLookupPhase collectPhase = ((PKLookupPhase) (queryAndFetch.collectPhase()));
        assertThat(collectPhase.projections().size(), Is.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(ColumnIndexWriterProjection.class));
        ColumnIndexWriterProjection projection = ((ColumnIndexWriterProjection) (collectPhase.projections().get(0)));
        assertThat(projection.columnReferences().size(), Is.is(3));
        assertThat(projection.columnReferences().get(0).column().fqn(), Is.is("date"));
        assertThat(projection.columnReferences().get(1).column().fqn(), Is.is("id"));
        assertThat(projection.columnReferences().get(2).column().fqn(), Is.is("name"));
        assertThat(index(), Is.is(1));
        assertThat(index(), Is.is(1));
        assertThat(projection.partitionedBySymbols().isEmpty(), Is.is(true));
    }

    @Test
    public void testInsertFromSubQueryJoin() {
        Join join = e.plan("insert into users (id, name) (select u1.id, u2.name from users u1 CROSS JOIN users u2)");
        assertThat(join.joinPhase().projections(), contains(Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        assertThat(join.joinPhase().projections().get(1), Matchers.instanceOf(ColumnIndexWriterProjection.class));
        ColumnIndexWriterProjection projection = ((ColumnIndexWriterProjection) (join.joinPhase().projections().get(1)));
        assertThat(projection.columnReferences().size(), Is.is(2));
        assertThat(projection.columnReferences().get(0).column().fqn(), Is.is("id"));
        assertThat(projection.columnReferences().get(1).column().fqn(), Is.is("name"));
        assertThat(index(), Is.is(0));
        assertThat(index(), Is.is(0));
        assertThat(projection.partitionedBySymbols().isEmpty(), Is.is(true));
    }

    @Test
    public void testInsertFromSubQueryWithLimit() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("Using limit, offset or order by is not supported on insert using a sub-query");
        e.plan("insert into users (date, id, name) (select date, id, name from users limit 10)");
    }

    @Test
    public void testInsertFromSubQueryWithOffset() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("Using limit, offset or order by is not supported on insert using a sub-query");
        e.plan("insert into users (id, name) (select id, name from users offset 10)");
    }

    @Test
    public void testInsertFromSubQueryWithOrderBy() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("Using limit, offset or order by is not supported on insert using a sub-query");
        e.plan("insert into users (date, id, name) (select date, id, name from users order by id)");
    }

    @Test
    public void testInsertFromSubQueryWithoutLimit() {
        Merge planNode = e.plan("insert into users (id, name) (select id, name from users)");
        Collect collect = ((Collect) (planNode.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections().size(), Is.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(ColumnIndexWriterProjection.class));
        MergePhase localMergeNode = planNode.mergePhase();
        assertThat(localMergeNode.projections().size(), Is.is(1));
        assertThat(localMergeNode.projections().get(0), Matchers.instanceOf(MergeCountProjection.class));
    }

    @Test
    public void testInsertFromSubQueryReduceOnCollectorGroupBy() {
        Merge merge = e.plan("insert into users (id, name) (select id, arbitrary(name) from users group by id)");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        ColumnIndexWriterProjection columnIndexWriterProjection = ((ColumnIndexWriterProjection) (collectPhase.projections().get(1)));
        assertThat(columnIndexWriterProjection.columnReferences(), contains(SymbolMatchers.isReference("id"), SymbolMatchers.isReference("name")));
        MergePhase mergePhase = merge.mergePhase();
        assertThat(mergePhase.projections(), contains(Matchers.instanceOf(MergeCountProjection.class)));
    }

    @Test
    public void testInsertFromSubQueryReduceOnCollectorGroupByWithCast() {
        Merge merge = e.plan("insert into users (id, name) (select id, count(*) from users group by id)");
        Collect nonDistributedGroupBy = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (nonDistributedGroupBy.collectPhase()));
        assertThat(collectPhase.projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        EvalProjection collectTopN = ((EvalProjection) (collectPhase.projections().get(1)));
        assertThat(collectTopN.outputs(), contains(SymbolMatchers.isInputColumn(0), SymbolMatchers.isFunction("to_string")));
        ColumnIndexWriterProjection columnIndexWriterProjection = ((ColumnIndexWriterProjection) (collectPhase.projections().get(2)));
        assertThat(columnIndexWriterProjection.columnReferences(), contains(SymbolMatchers.isReference("id"), SymbolMatchers.isReference("name")));
        MergePhase mergePhase = merge.mergePhase();
        assertThat(mergePhase.projections(), contains(Matchers.instanceOf(MergeCountProjection.class)));
    }

    @Test
    public void testInsertFromValuesWithOnDuplicateKey() {
        LegacyUpsertById node = e.plan("insert into users (id, name) values (1, null) on conflict (id) do update set name = excluded.name");
        assertThat(node.updateColumns(), Is.is(new String[]{ "name" }));
        assertThat(node.insertColumns().length, Is.is(2));
        Reference idRef = node.insertColumns()[0];
        assertThat(idRef.column().fqn(), Is.is("id"));
        Reference nameRef = node.insertColumns()[1];
        assertThat(nameRef.column().fqn(), Is.is("name"));
        assertThat(node.items().size(), Is.is(1));
        LegacyUpsertById.Item item = node.items().get(0);
        assertThat(item.index(), Is.is("users"));
        assertThat(item.id(), Is.is("1"));
        assertThat(item.routing(), Is.is("1"));
        assertThat(item.insertValues().length, Is.is(2));
        assertThat(item.insertValues()[0], Is.is(1L));
        assertNull(item.insertValues()[1]);
        assertThat(item.updateAssignments().length, Is.is(1));
        assertThat(item.updateAssignments()[0], SymbolMatchers.isLiteral(null, STRING));
    }

    @Test
    public void testInsertFromQueryWithPartitionedColumn() {
        Merge planNode = e.plan("insert into users (id, date) (select id, date from parted_pks)");
        Collect queryAndFetch = ((Collect) (planNode.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (queryAndFetch.collectPhase()));
        List<Symbol> toCollect = collectPhase.toCollect();
        assertThat(toCollect.size(), Is.is(2));
        assertThat(toCollect.get(0), SymbolMatchers.isReference("_doc['id']"));
        assertThat(toCollect.get(1), Matchers.equalTo(new Reference(new io.crate.metadata.ReferenceIdent(TableDefinitions.PARTED_PKS_IDENT, "date"), RowGranularity.PARTITION, DataTypes.TIMESTAMP)));
    }

    @Test
    public void testGroupByHavingInsertInto() {
        Merge planNode = e.plan("insert into users (id, name) (select name, count(*) from users group by name having count(*) > 3)");
        Merge groupByNode = ((Merge) (planNode.subPlan()));
        MergePhase mergePhase = groupByNode.mergePhase();
        assertThat(mergePhase.projections(), contains(Matchers.instanceOf(GroupProjection.class), Matchers.instanceOf(FilterProjection.class), Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        FilterProjection filterProjection = ((FilterProjection) (mergePhase.projections().get(1)));
        assertThat(filterProjection.outputs().size(), Is.is(2));
        assertThat(filterProjection.outputs().get(0), Matchers.instanceOf(InputColumn.class));
        assertThat(filterProjection.outputs().get(1), Matchers.instanceOf(InputColumn.class));
        InputColumn inputColumn = ((InputColumn) (filterProjection.outputs().get(0)));
        assertThat(inputColumn.index(), Is.is(0));
        inputColumn = ((InputColumn) (filterProjection.outputs().get(1)));
        assertThat(inputColumn.index(), Is.is(1));
        MergePhase localMergeNode = planNode.mergePhase();
        assertThat(localMergeNode.projections().size(), Is.is(1));
        assertThat(localMergeNode.projections().get(0), Matchers.instanceOf(MergeCountProjection.class));
        assertThat(localMergeNode.finalProjection().get().outputs().size(), Is.is(1));
    }

    @Test
    public void testProjectionWithCastsIsAddedIfSourceTypeDoNotMatchTargetTypes() {
        Merge plan = e.plan("insert into users (id, name) (select id, name from source)");
        List<Projection> projections = collectPhase().projections();
        assertThat(projections, contains(Matchers.instanceOf(EvalProjection.class), Matchers.instanceOf(ColumnIndexWriterProjection.class)));
        assertThat(projections.get(0).outputs(), contains(SymbolMatchers.isFunction("to_long"), SymbolMatchers.isInputColumn(1)));
    }
}

