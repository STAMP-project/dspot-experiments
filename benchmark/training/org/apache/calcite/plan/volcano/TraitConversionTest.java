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
import java.util.List;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.rel.RelDistributionTraitDef}.
 */
public class TraitConversionTest {
    private static final TraitConversionTest.ConvertRelDistributionTraitDef NEW_TRAIT_DEF_INSTANCE = new TraitConversionTest.ConvertRelDistributionTraitDef();

    private static final TraitConversionTest.SimpleDistribution SIMPLE_DISTRIBUTION_ANY = new TraitConversionTest.SimpleDistribution("ANY");

    private static final TraitConversionTest.SimpleDistribution SIMPLE_DISTRIBUTION_RANDOM = new TraitConversionTest.SimpleDistribution("RANDOM");

    private static final TraitConversionTest.SimpleDistribution SIMPLE_DISTRIBUTION_SINGLETON = new TraitConversionTest.SimpleDistribution("SINGLETON");

    @Test
    public void testTraitConversion() {
        final VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(INSTANCE);
        planner.addRelTraitDef(TraitConversionTest.NEW_TRAIT_DEF_INSTANCE);
        planner.addRule(new TraitConversionTest.RandomSingleTraitRule());
        planner.addRule(new TraitConversionTest.SingleLeafTraitRule());
        planner.addRule(ExpandConversionRule.INSTANCE);
        final RelOptCluster cluster = PlannerTests.newCluster(planner);
        final TraitConversionTest.NoneLeafRel leafRel = new TraitConversionTest.NoneLeafRel(cluster, "a");
        final TraitConversionTest.NoneSingleRel singleRel = new TraitConversionTest.NoneSingleRel(cluster, leafRel);
        final RelNode convertedRel = planner.changeTraits(singleRel, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION));
        planner.setRoot(convertedRel);
        final RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof TraitConversionTest.RandomSingleRel));
        Assert.assertTrue(result.getTraitSet().contains(PlannerTests.PHYS_CALLING_CONVENTION));
        Assert.assertTrue(result.getTraitSet().contains(TraitConversionTest.SIMPLE_DISTRIBUTION_RANDOM));
        final RelNode input = result.getInput(0);
        Assert.assertTrue((input instanceof TraitConversionTest.BridgeRel));
        Assert.assertTrue(input.getTraitSet().contains(PlannerTests.PHYS_CALLING_CONVENTION));
        Assert.assertTrue(input.getTraitSet().contains(TraitConversionTest.SIMPLE_DISTRIBUTION_RANDOM));
        final RelNode input2 = input.getInput(0);
        Assert.assertTrue((input2 instanceof TraitConversionTest.SingletonLeafRel));
        Assert.assertTrue(input2.getTraitSet().contains(PlannerTests.PHYS_CALLING_CONVENTION));
        Assert.assertTrue(input2.getTraitSet().contains(TraitConversionTest.SIMPLE_DISTRIBUTION_SINGLETON));
    }

    /**
     * Converts a {@link NoneSingleRel} (none convention, distribution any)
     * to {@link RandomSingleRel} (physical convention, distribution random).
     */
    private static class RandomSingleTraitRule extends RelOptRule {
        RandomSingleTraitRule() {
            super(operand(TraitConversionTest.NoneSingleRel.class, any()));
        }

        @Override
        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            TraitConversionTest.NoneSingleRel single = call.rel(0);
            RelNode input = getInput();
            RelNode physInput = convert(input, getTraitSet().replace(PlannerTests.PHYS_CALLING_CONVENTION).plus(TraitConversionTest.SIMPLE_DISTRIBUTION_RANDOM));
            call.transformTo(new TraitConversionTest.RandomSingleRel(getCluster(), physInput));
        }
    }

    /**
     * Rel with physical convention and random distribution.
     */
    private static class RandomSingleRel extends PlannerTests.TestSingleRel {
        RandomSingleRel(RelOptCluster cluster, RelNode input) {
            super(cluster, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION).plus(TraitConversionTest.SIMPLE_DISTRIBUTION_RANDOM), input);
        }

        @Override
        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeTinyCost();
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            return new TraitConversionTest.RandomSingleRel(getCluster(), sole(inputs));
        }
    }

    /**
     * Converts {@link NoneLeafRel} (none convention, any distribution) to
     * {@link SingletonLeafRel} (physical convention, singleton distribution).
     */
    private static class SingleLeafTraitRule extends RelOptRule {
        SingleLeafTraitRule() {
            super(operand(TraitConversionTest.NoneLeafRel.class, any()));
        }

        @Override
        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            TraitConversionTest.NoneLeafRel leafRel = call.rel(0);
            call.transformTo(new TraitConversionTest.SingletonLeafRel(getCluster(), leafRel.label));
        }
    }

    /**
     * Rel with singleton distribution, physical convention.
     */
    private static class SingletonLeafRel extends PlannerTests.TestLeafRel {
        SingletonLeafRel(RelOptCluster cluster, String label) {
            super(cluster, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION).plus(TraitConversionTest.SIMPLE_DISTRIBUTION_SINGLETON), label);
        }

        @Override
        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeTinyCost();
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            return new TraitConversionTest.SingletonLeafRel(getCluster(), label);
        }
    }

    /**
     * Bridges the {@link SimpleDistribution}, difference between
     * {@link SingletonLeafRel} and {@link RandomSingleRel}.
     */
    private static class BridgeRel extends PlannerTests.TestSingleRel {
        BridgeRel(RelOptCluster cluster, RelNode input) {
            super(cluster, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION).plus(TraitConversionTest.SIMPLE_DISTRIBUTION_RANDOM), input);
        }

        @Override
        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeTinyCost();
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            return new TraitConversionTest.BridgeRel(getCluster(), sole(inputs));
        }
    }

    /**
     * Dummy distribution for test (simplified version of RelDistribution).
     */
    private static class SimpleDistribution implements RelTrait {
        private final String name;

        SimpleDistribution(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public RelTraitDef getTraitDef() {
            return TraitConversionTest.NEW_TRAIT_DEF_INSTANCE;
        }

        @Override
        public boolean satisfies(RelTrait trait) {
            return (trait == (this)) || (trait == (TraitConversionTest.SIMPLE_DISTRIBUTION_ANY));
        }

        @Override
        public void register(RelOptPlanner planner) {
        }
    }

    /**
     * Dummy distribution trait def for test (handles conversion of SimpleDistribution)
     */
    private static class ConvertRelDistributionTraitDef extends RelTraitDef<TraitConversionTest.SimpleDistribution> {
        @Override
        public Class<TraitConversionTest.SimpleDistribution> getTraitClass() {
            return TraitConversionTest.SimpleDistribution.class;
        }

        @Override
        public String toString() {
            return getSimpleName();
        }

        @Override
        public String getSimpleName() {
            return "ConvertRelDistributionTraitDef";
        }

        @Override
        public RelNode convert(RelOptPlanner planner, RelNode rel, TraitConversionTest.SimpleDistribution toTrait, boolean allowInfiniteCostConverters) {
            if (toTrait == (TraitConversionTest.SIMPLE_DISTRIBUTION_ANY)) {
                return rel;
            }
            return new TraitConversionTest.BridgeRel(rel.getCluster(), rel);
        }

        @Override
        public boolean canConvert(RelOptPlanner planner, TraitConversionTest.SimpleDistribution fromTrait, TraitConversionTest.SimpleDistribution toTrait) {
            return ((fromTrait == toTrait) || (toTrait == (TraitConversionTest.SIMPLE_DISTRIBUTION_ANY))) || ((fromTrait == (TraitConversionTest.SIMPLE_DISTRIBUTION_SINGLETON)) && (toTrait == (TraitConversionTest.SIMPLE_DISTRIBUTION_RANDOM)));
        }

        @Override
        public TraitConversionTest.SimpleDistribution getDefault() {
            return TraitConversionTest.SIMPLE_DISTRIBUTION_ANY;
        }
    }

    /**
     * Any distribution and none convention.
     */
    private static class NoneLeafRel extends PlannerTests.TestLeafRel {
        NoneLeafRel(RelOptCluster cluster, String label) {
            super(cluster, cluster.traitSetOf(NONE), label);
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            assert traitSet.comprises(NONE, TraitConversionTest.SIMPLE_DISTRIBUTION_ANY);
            assert inputs.isEmpty();
            return this;
        }
    }

    /**
     * Rel with any distribution and none convention.
     */
    private static class NoneSingleRel extends PlannerTests.TestSingleRel {
        NoneSingleRel(RelOptCluster cluster, RelNode input) {
            super(cluster, cluster.traitSetOf(NONE), input);
        }

        @Override
        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            assert traitSet.comprises(NONE, TraitConversionTest.SIMPLE_DISTRIBUTION_ANY);
            return new TraitConversionTest.NoneSingleRel(getCluster(), sole(inputs));
        }
    }
}

/**
 * End TraitConversionTest.java
 */
