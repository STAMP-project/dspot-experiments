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
package org.apache.calcite.plan.volcano;


import CalciteSystemProperty.DEBUG;
import Convention.NONE;
import RelCollations.EMPTY;
import RelFieldCollation.Direction;
import RelFieldCollation.NullDirection;
import SortRemoveRule.INSTANCE;
import SqlExplainFormat.TEXT;
import SqlExplainLevel.ALL_ATTRIBUTES;
import SqlExplainLevel.DIGEST_ATTRIBUTES;
import SqlStdOperatorTable.COUNT;
import SqlTypeName.BIGINT;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.apache.calcite.adapter.enumerable.EnumerableTableScan;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptAbstractTable;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.AbstractRelNode;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.metadata.RelMdCollation;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.tools.RuleSet;
import org.apache.calcite.tools.RuleSets;
import org.apache.calcite.util.ImmutableBitSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that determine whether trait propagation work in Volcano Planner.
 */
public class TraitPropagationTest {
    static final Convention PHYSICAL = new Convention.Impl("PHYSICAL", TraitPropagationTest.Phys.class);

    static final RelCollation COLLATION = RelCollations.of(new org.apache.calcite.rel.RelFieldCollation(0, Direction.ASCENDING, NullDirection.FIRST));

    static final RuleSet RULES = RuleSets.ofList(TraitPropagationTest.PhysAggRule.INSTANCE, TraitPropagationTest.PhysProjRule.INSTANCE, TraitPropagationTest.PhysTableRule.INSTANCE, TraitPropagationTest.PhysSortRule.INSTANCE, INSTANCE, ExpandConversionRule.INSTANCE);

    @Test
    public void testOne() throws Exception {
        RelNode planned = TraitPropagationTest.run(new TraitPropagationTest.PropAction(), TraitPropagationTest.RULES);
        if (DEBUG.value()) {
            System.out.println(RelOptUtil.dumpPlan("LOGICAL PLAN", planned, TEXT, ALL_ATTRIBUTES));
        }
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Assert.assertEquals("Sortedness was not propagated", 3, mq.getCumulativeCost(planned).getRows(), 0);
    }

    /**
     * Materialized anonymous class for simplicity
     */
    private static class PropAction {
        public RelNode apply(RelOptCluster cluster, RelOptSchema relOptSchema, SchemaPlus rootSchema) {
            final RelDataTypeFactory typeFactory = cluster.getTypeFactory();
            final RexBuilder rexBuilder = cluster.getRexBuilder();
            final RelOptPlanner planner = cluster.getPlanner();
            final RelDataType stringType = typeFactory.createJavaType(String.class);
            final RelDataType integerType = typeFactory.createJavaType(Integer.class);
            final RelDataType sqlBigInt = typeFactory.createSqlType(BIGINT);
            // SELECT * from T;
            final Table table = new AbstractTable() {
                public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                    return typeFactory.builder().add("s", stringType).add("i", integerType).build();
                }

                @Override
                public Statistic getStatistic() {
                    return Statistics.of(100.0, ImmutableList.of(), ImmutableList.of(TraitPropagationTest.COLLATION));
                }
            };
            final RelOptAbstractTable t1 = new RelOptAbstractTable(relOptSchema, "t1", table.getRowType(typeFactory)) {
                @Override
                public <T> T unwrap(Class<T> clazz) {
                    return clazz.isInstance(table) ? clazz.cast(table) : super.unwrap(clazz);
                }
            };
            final RelNode rt1 = EnumerableTableScan.create(cluster, t1);
            // project s column
            RelNode project = LogicalProject.create(rt1, ImmutableList.of(((RexNode) (rexBuilder.makeInputRef(stringType, 0))), rexBuilder.makeInputRef(integerType, 1)), typeFactory.builder().add("s", stringType).add("i", integerType).build());
            // aggregate on s, count
            AggregateCall aggCall = AggregateCall.create(COUNT, false, false, Collections.singletonList(1), (-1), EMPTY, sqlBigInt, "cnt");
            RelNode agg = new LogicalAggregate(cluster, cluster.traitSetOf(NONE), project, false, ImmutableBitSet.of(0), null, Collections.singletonList(aggCall));
            final RelNode rootRel = agg;
            RelOptUtil.dumpPlan("LOGICAL PLAN", rootRel, TEXT, DIGEST_ATTRIBUTES);
            RelTraitSet desiredTraits = rootRel.getTraitSet().replace(TraitPropagationTest.PHYSICAL);
            final RelNode rootRel2 = planner.changeTraits(rootRel, desiredTraits);
            planner.setRoot(rootRel2);
            return planner.findBestExp();
        }
    }

    // RULES
    /**
     * Rule for PhysAgg
     */
    private static class PhysAggRule extends RelOptRule {
        static final TraitPropagationTest.PhysAggRule INSTANCE = new TraitPropagationTest.PhysAggRule();

        private PhysAggRule() {
            super(TraitPropagationTest.anyChild(LogicalAggregate.class), "PhysAgg");
        }

        public void onMatch(RelOptRuleCall call) {
            RelTraitSet empty = call.getPlanner().emptyTraitSet();
            LogicalAggregate rel = call.rel(0);
            assert (rel.getGroupSet().cardinality()) == 1;
            int aggIndex = rel.getGroupSet().iterator().next();
            RelTrait collation = RelCollations.of(new org.apache.calcite.rel.RelFieldCollation(aggIndex, Direction.ASCENDING, NullDirection.FIRST));
            RelTraitSet desiredTraits = empty.replace(TraitPropagationTest.PHYSICAL).replace(collation);
            RelNode convertedInput = convert(rel.getInput(), desiredTraits);
            call.transformTo(new TraitPropagationTest.PhysAgg(rel.getCluster(), empty.replace(TraitPropagationTest.PHYSICAL), convertedInput, rel.indicator, rel.getGroupSet(), rel.getGroupSets(), rel.getAggCallList()));
        }
    }

    /**
     * Rule for PhysProj
     */
    private static class PhysProjRule extends RelOptRule {
        static final TraitPropagationTest.PhysProjRule INSTANCE = new TraitPropagationTest.PhysProjRule(false);

        final boolean subsetHack;

        private PhysProjRule(boolean subsetHack) {
            super(RelOptRule.operand(LogicalProject.class, TraitPropagationTest.anyChild(RelNode.class)), "PhysProj");
            this.subsetHack = subsetHack;
        }

        public void onMatch(RelOptRuleCall call) {
            LogicalProject rel = call.rel(0);
            RelNode rawInput = call.rel(1);
            RelNode input = convert(rawInput, TraitPropagationTest.PHYSICAL);
            if ((subsetHack) && (input instanceof RelSubset)) {
                RelSubset subset = ((RelSubset) (input));
                for (RelNode child : subset.getRels()) {
                    // skip logical nodes
                    if ((child.getTraitSet().getTrait(ConventionTraitDef.INSTANCE)) == (Convention.NONE)) {
                        continue;
                    } else {
                        RelTraitSet outcome = child.getTraitSet().replace(TraitPropagationTest.PHYSICAL);
                        call.transformTo(new TraitPropagationTest.PhysProj(rel.getCluster(), outcome, convert(child, outcome), rel.getChildExps(), rel.getRowType()));
                    }
                }
            } else {
                call.transformTo(TraitPropagationTest.PhysProj.create(input, rel.getChildExps(), rel.getRowType()));
            }
        }
    }

    /**
     * Rule for PhysSort
     */
    private static class PhysSortRule extends ConverterRule {
        static final TraitPropagationTest.PhysSortRule INSTANCE = new TraitPropagationTest.PhysSortRule();

        PhysSortRule() {
            super(Sort.class, NONE, TraitPropagationTest.PHYSICAL, "PhysSortRule");
        }

        public RelNode convert(RelNode rel) {
            final Sort sort = ((Sort) (rel));
            final RelNode input = convert(sort.getInput(), rel.getCluster().traitSetOf(TraitPropagationTest.PHYSICAL));
            return new TraitPropagationTest.PhysSort(rel.getCluster(), input.getTraitSet().plus(sort.getCollation()), convert(input, input.getTraitSet().replace(TraitPropagationTest.PHYSICAL)), sort.getCollation(), null, null);
        }
    }

    /**
     * Rule for PhysTable
     */
    private static class PhysTableRule extends RelOptRule {
        static final TraitPropagationTest.PhysTableRule INSTANCE = new TraitPropagationTest.PhysTableRule();

        private PhysTableRule() {
            super(TraitPropagationTest.anyChild(EnumerableTableScan.class), "PhysScan");
        }

        public void onMatch(RelOptRuleCall call) {
            EnumerableTableScan rel = call.rel(0);
            call.transformTo(new TraitPropagationTest.PhysTable(rel.getCluster()));
        }
    }

    /* RELS */
    /**
     * Market interface for Phys nodes
     */
    private interface Phys extends RelNode {}

    /**
     * Physical Aggregate RelNode
     */
    private static class PhysAgg extends Aggregate implements TraitPropagationTest.Phys {
        PhysAgg(RelOptCluster cluster, RelTraitSet traits, RelNode child, boolean indicator, ImmutableBitSet groupSet, List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls) {
            super(cluster, traits, child, indicator, groupSet, groupSets, aggCalls);
        }

        public Aggregate copy(RelTraitSet traitSet, RelNode input, boolean indicator, ImmutableBitSet groupSet, List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls) {
            return new TraitPropagationTest.PhysAgg(getCluster(), traitSet, input, indicator, groupSet, groupSets, aggCalls);
        }

        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeCost(1, 1, 1);
        }
    }

    /**
     * Physical Project RelNode
     */
    private static class PhysProj extends Project implements TraitPropagationTest.Phys {
        PhysProj(RelOptCluster cluster, RelTraitSet traits, RelNode child, List<RexNode> exps, RelDataType rowType) {
            super(cluster, traits, child, exps, rowType);
        }

        public static TraitPropagationTest.PhysProj create(final RelNode input, final List<RexNode> projects, RelDataType rowType) {
            final RelOptCluster cluster = input.getCluster();
            final RelMetadataQuery mq = RelMetadataQuery.instance();
            final RelTraitSet traitSet = cluster.traitSet().replace(TraitPropagationTest.PHYSICAL).replaceIfs(RelCollationTraitDef.INSTANCE, () -> RelMdCollation.project(mq, input, projects));
            return new TraitPropagationTest.PhysProj(cluster, traitSet, input, projects, rowType);
        }

        public TraitPropagationTest.PhysProj copy(RelTraitSet traitSet, RelNode input, List<RexNode> exps, RelDataType rowType) {
            return new TraitPropagationTest.PhysProj(getCluster(), traitSet, input, exps, rowType);
        }

        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeCost(1, 1, 1);
        }
    }

    /**
     * Physical Sort RelNode
     */
    private static class PhysSort extends Sort implements TraitPropagationTest.Phys {
        PhysSort(RelOptCluster cluster, RelTraitSet traits, RelNode child, RelCollation collation, RexNode offset, RexNode fetch) {
            super(cluster, traits, child, collation, offset, fetch);
        }

        public TraitPropagationTest.PhysSort copy(RelTraitSet traitSet, RelNode newInput, RelCollation newCollation, RexNode offset, RexNode fetch) {
            return new TraitPropagationTest.PhysSort(getCluster(), traitSet, newInput, newCollation, offset, fetch);
        }

        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeCost(1, 1, 1);
        }
    }

    /**
     * Physical Table RelNode
     */
    private static class PhysTable extends AbstractRelNode implements TraitPropagationTest.Phys {
        PhysTable(RelOptCluster cluster) {
            super(cluster, cluster.traitSet().replace(TraitPropagationTest.PHYSICAL).replace(TraitPropagationTest.COLLATION));
            RelDataTypeFactory typeFactory = cluster.getTypeFactory();
            final RelDataType stringType = typeFactory.createJavaType(String.class);
            final RelDataType integerType = typeFactory.createJavaType(Integer.class);
            this.rowType = typeFactory.builder().add("s", stringType).add("i", integerType).build();
        }

        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeCost(1, 1, 1);
        }
    }
}

/**
 * End TraitPropagationTest.java
 */
