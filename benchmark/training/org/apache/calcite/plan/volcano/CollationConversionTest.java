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


import Convention.NONE;
import ConventionTraitDef.INSTANCE;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelCollationImpl;
import org.apache.calcite.rel.RelFieldCollation;
import org.apache.calcite.rel.RelFieldCollation.Direction;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rex.RexNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.rel.RelCollationTraitDef}.
 */
public class CollationConversionTest {
    private static final CollationConversionTest.TestRelCollationImpl LEAF_COLLATION = new CollationConversionTest.TestRelCollationImpl(ImmutableList.of(new RelFieldCollation(0, Direction.CLUSTERED)));

    private static final CollationConversionTest.TestRelCollationImpl ROOT_COLLATION = new CollationConversionTest.TestRelCollationImpl(ImmutableList.of(new RelFieldCollation(0)));

    private static final CollationConversionTest.TestRelCollationTraitDef COLLATION_TRAIT_DEF = new CollationConversionTest.TestRelCollationTraitDef();

    @Test
    public void testCollationConversion() {
        final VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(INSTANCE);
        planner.addRelTraitDef(CollationConversionTest.COLLATION_TRAIT_DEF);
        planner.addRule(new CollationConversionTest.SingleNodeRule());
        planner.addRule(new CollationConversionTest.LeafTraitRule());
        planner.addRule(ExpandConversionRule.INSTANCE);
        final RelOptCluster cluster = PlannerTests.newCluster(planner);
        final CollationConversionTest.NoneLeafRel leafRel = new CollationConversionTest.NoneLeafRel(cluster, "a");
        final CollationConversionTest.NoneSingleRel singleRel = new CollationConversionTest.NoneSingleRel(cluster, leafRel);
        final RelNode convertedRel = planner.changeTraits(singleRel, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION).plus(CollationConversionTest.ROOT_COLLATION));
        planner.setRoot(convertedRel);
        RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof CollationConversionTest.RootSingleRel));
        Assert.assertTrue(result.getTraitSet().contains(CollationConversionTest.ROOT_COLLATION));
        Assert.assertTrue(result.getTraitSet().contains(PlannerTests.PHYS_CALLING_CONVENTION));
        final RelNode input = result.getInput(0);
        Assert.assertTrue((input instanceof CollationConversionTest.PhysicalSort));
        Assert.assertTrue(result.getTraitSet().contains(CollationConversionTest.ROOT_COLLATION));
        Assert.assertTrue(input.getTraitSet().contains(PlannerTests.PHYS_CALLING_CONVENTION));
        final RelNode input2 = input.getInput(0);
        Assert.assertTrue((input2 instanceof CollationConversionTest.LeafRel));
        Assert.assertTrue(input2.getTraitSet().contains(CollationConversionTest.LEAF_COLLATION));
        Assert.assertTrue(input.getTraitSet().contains(PlannerTests.PHYS_CALLING_CONVENTION));
    }

    /**
     * Converts a NoneSingleRel to RootSingleRel.
     */
    private class SingleNodeRule extends RelOptRule {
        SingleNodeRule() {
            super(operand(CollationConversionTest.NoneSingleRel.class, any()));
        }

        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            CollationConversionTest.NoneSingleRel single = call.rel(0);
            RelNode input = getInput();
            RelNode physInput = convert(input, getTraitSet().replace(PlannerTests.PHYS_CALLING_CONVENTION).plus(CollationConversionTest.ROOT_COLLATION));
            call.transformTo(new CollationConversionTest.RootSingleRel(getCluster(), physInput));
        }
    }

    /**
     * Root node with physical convention and ROOT_COLLATION trait.
     */
    private class RootSingleRel extends PlannerTests.TestSingleRel {
        RootSingleRel(RelOptCluster cluster, RelNode input) {
            super(cluster, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION).plus(CollationConversionTest.ROOT_COLLATION), input);
        }

        @Override
        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeTinyCost();
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            return new CollationConversionTest.RootSingleRel(getCluster(), sole(inputs));
        }
    }

    /**
     * Converts a {@link NoneLeafRel} (with none convention) to {@link LeafRel}
     * (with physical convention).
     */
    private class LeafTraitRule extends RelOptRule {
        LeafTraitRule() {
            super(operand(CollationConversionTest.NoneLeafRel.class, any()));
        }

        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            CollationConversionTest.NoneLeafRel leafRel = call.rel(0);
            call.transformTo(new CollationConversionTest.LeafRel(getCluster(), leafRel.label));
        }
    }

    /**
     * Leaf node with physical convention and LEAF_COLLATION trait.
     */
    private class LeafRel extends PlannerTests.TestLeafRel {
        LeafRel(RelOptCluster cluster, String label) {
            super(cluster, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION).plus(CollationConversionTest.LEAF_COLLATION), label);
        }

        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeTinyCost();
        }

        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            return new CollationConversionTest.LeafRel(getCluster(), label);
        }
    }

    /**
     * Leaf node with none convention and LEAF_COLLATION trait.
     */
    private class NoneLeafRel extends PlannerTests.TestLeafRel {
        NoneLeafRel(RelOptCluster cluster, String label) {
            super(cluster, cluster.traitSetOf(NONE).plus(CollationConversionTest.LEAF_COLLATION), label);
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            assert traitSet.comprises(NONE, CollationConversionTest.LEAF_COLLATION);
            assert inputs.isEmpty();
            return this;
        }
    }

    /**
     * A single-input node with none convention and LEAF_COLLATION trait.
     */
    private static class NoneSingleRel extends PlannerTests.TestSingleRel {
        NoneSingleRel(RelOptCluster cluster, RelNode input) {
            super(cluster, cluster.traitSetOf(NONE).plus(CollationConversionTest.LEAF_COLLATION), input);
        }

        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            assert traitSet.comprises(NONE, CollationConversionTest.LEAF_COLLATION);
            return new CollationConversionTest.NoneSingleRel(getCluster(), sole(inputs));
        }
    }

    /**
     * Dummy collation trait implementation for the test.
     */
    private static class TestRelCollationImpl extends RelCollationImpl {
        TestRelCollationImpl(ImmutableList<RelFieldCollation> fieldCollations) {
            super(fieldCollations);
        }

        @Override
        public RelTraitDef getTraitDef() {
            return CollationConversionTest.COLLATION_TRAIT_DEF;
        }
    }

    /**
     * Dummy collation trait def implementation for the test (uses
     * {@link PhysicalSort} below).
     */
    private static class TestRelCollationTraitDef extends RelTraitDef<RelCollation> {
        public Class<RelCollation> getTraitClass() {
            return RelCollation.class;
        }

        public String getSimpleName() {
            return "testsort";
        }

        @Override
        public boolean multiple() {
            return true;
        }

        public RelCollation getDefault() {
            return CollationConversionTest.LEAF_COLLATION;
        }

        public RelNode convert(RelOptPlanner planner, RelNode rel, RelCollation toCollation, boolean allowInfiniteCostConverters) {
            if (toCollation.getFieldCollations().isEmpty()) {
                // An empty sort doesn't make sense.
                return null;
            }
            return new CollationConversionTest.PhysicalSort(rel.getCluster(), rel.getTraitSet().replace(toCollation), rel, toCollation, null, null);
        }

        public boolean canConvert(RelOptPlanner planner, RelCollation fromTrait, RelCollation toTrait) {
            return true;
        }
    }

    /**
     * Physical sort node (not logical).
     */
    private static class PhysicalSort extends Sort {
        PhysicalSort(RelOptCluster cluster, RelTraitSet traits, RelNode input, RelCollation collation, RexNode offset, RexNode fetch) {
            super(cluster, traits, input, collation, offset, fetch);
        }

        public Sort copy(RelTraitSet traitSet, RelNode newInput, RelCollation newCollation, RexNode offset, RexNode fetch) {
            return new CollationConversionTest.PhysicalSort(getCluster(), traitSet, newInput, newCollation, offset, fetch);
        }

        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeTinyCost();
        }
    }
}

/**
 * End CollationConversionTest.java
 */
