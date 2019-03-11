package io.crate.planner;


import DataTypes.LONG;
import RowGranularity.DOC;
import RowGranularity.SHARD;
import com.google.common.collect.Iterables;
import io.crate.execution.dsl.phases.MergePhase;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.dsl.projection.EvalProjection;
import io.crate.execution.dsl.projection.GroupProjection;
import io.crate.expression.symbol.Function;
import io.crate.planner.node.dql.Collect;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class GroupByScalarPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testGroupByWithScalarPlan() throws Exception {
        Merge merge = e.plan("select id + 1 from users group by id");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertEquals(LONG, collectPhase.outputTypes().get(0));
        assertThat(collectPhase.maxRowGranularity(), Matchers.is(DOC));
        assertThat(collectPhase.projections().size(), Matchers.is(2));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        assertThat(collectPhase.projections().get(1), Matchers.instanceOf(EvalProjection.class));
        assertThat(collectPhase.projections().get(1).outputs().get(0), Matchers.instanceOf(Function.class));
        assertThat(collectPhase.toCollect(), Matchers.contains(SymbolMatchers.isReference("id", LONG)));
        GroupProjection groupProjection = ((GroupProjection) (collectPhase.projections().get(0)));
        assertThat(groupProjection.keys().get(0).valueType(), Matchers.is(LONG));
        assertThat(collectPhase.projections().get(1).outputs(), Matchers.contains(SymbolMatchers.isFunction("add")));
        MergePhase mergePhase = merge.mergePhase();
        assertEquals(LONG, Iterables.get(mergePhase.inputTypes(), 0));
        assertEquals(LONG, mergePhase.outputTypes().get(0));
    }

    @Test
    public void testGroupByWithMultipleScalarPlan() throws Exception {
        Merge merge = e.plan("select abs(id + 1) from users group by id");
        Collect collect = ((Collect) (merge.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertEquals(LONG, collectPhase.outputTypes().get(0));
        assertThat(collectPhase.maxRowGranularity(), Matchers.is(DOC));
        assertThat(collectPhase.projections().size(), Matchers.is(2));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        assertThat(collectPhase.projections().get(1), Matchers.instanceOf(EvalProjection.class));
        assertThat(collectPhase.projections().get(1).outputs().get(0), SymbolMatchers.isFunction("abs"));
        assertThat(collectPhase.toCollect(), Matchers.contains(SymbolMatchers.isReference("id", LONG)));
        GroupProjection groupProjection = ((GroupProjection) (collectPhase.projections().get(0)));
        assertThat(groupProjection.keys().get(0).valueType(), Matchers.is(LONG));
        MergePhase mergePhase = merge.mergePhase();
        assertEquals(LONG, Iterables.get(mergePhase.inputTypes(), 0));
        assertEquals(LONG, mergePhase.outputTypes().get(0));
    }

    @Test
    public void testGroupByScalarWithMultipleColumnArgumentsPlan() throws Exception {
        Merge merge = e.plan("select abs(id + other_id) from users group by id, other_id");
        Merge subplan = ((Merge) (merge.subPlan()));
        Collect collect = ((Collect) (subplan.subPlan()));
        RoutedCollectPhase collectPhase = ((RoutedCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.projections().size(), Matchers.is(1));
        assertThat(collectPhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(collectPhase.projections().get(0).requiredGranularity(), Matchers.is(SHARD));
        assertThat(collectPhase.toCollect(), Matchers.contains(SymbolMatchers.isReference("id", LONG), SymbolMatchers.isReference("other_id", LONG)));
        GroupProjection groupProjection = ((GroupProjection) (collectPhase.projections().get(0)));
        assertThat(groupProjection.keys().size(), Matchers.is(2));
        assertThat(groupProjection.keys().get(0).valueType(), Matchers.is(LONG));
        assertThat(groupProjection.keys().get(1).valueType(), Matchers.is(LONG));
        MergePhase mergePhase = subplan.mergePhase();
        assertThat(mergePhase.projections().size(), Matchers.is(2));
        assertThat(mergePhase.projections().get(0), Matchers.instanceOf(GroupProjection.class));
        assertThat(mergePhase.projections().get(1), Matchers.instanceOf(EvalProjection.class));
        assertThat(mergePhase.projections().get(1).outputs(), Matchers.contains(SymbolMatchers.isFunction("abs")));
    }
}

