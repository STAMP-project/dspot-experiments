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


import ConventionTraitDef.INSTANCE;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptListener;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterImpl;
import org.apache.calcite.rel.logical.LogicalProject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link VolcanoPlanner the optimizer}.
 */
public class VolcanoPlannerTest {
    public VolcanoPlannerTest() {
    }

    // ~ Methods ----------------------------------------------------------------
    /**
     * Tests transformation of a leaf from NONE to PHYS.
     */
    @Test
    public void testTransformLeaf() {
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(INSTANCE);
        planner.addRule(new PlannerTests.PhysLeafRule());
        RelOptCluster cluster = PlannerTests.newCluster(planner);
        PlannerTests.NoneLeafRel leafRel = new PlannerTests.NoneLeafRel(cluster, "a");
        RelNode convertedRel = planner.changeTraits(leafRel, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION));
        planner.setRoot(convertedRel);
        RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof PlannerTests.PhysLeafRel));
    }

    /**
     * Tests transformation of a single+leaf from NONE to PHYS.
     */
    @Test
    public void testTransformSingleGood() {
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(INSTANCE);
        planner.addRule(new PlannerTests.PhysLeafRule());
        planner.addRule(new PlannerTests.GoodSingleRule());
        RelOptCluster cluster = PlannerTests.newCluster(planner);
        PlannerTests.NoneLeafRel leafRel = new PlannerTests.NoneLeafRel(cluster, "a");
        PlannerTests.NoneSingleRel singleRel = new PlannerTests.NoneSingleRel(cluster, leafRel);
        RelNode convertedRel = planner.changeTraits(singleRel, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION));
        planner.setRoot(convertedRel);
        RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof PlannerTests.PhysSingleRel));
    }

    /**
     * Tests a rule that is fired once per subset (whereas most rules are fired
     * once per rel in a set or rel in a subset)
     */
    @Test
    public void testSubsetRule() {
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(INSTANCE);
        planner.addRule(new PlannerTests.PhysLeafRule());
        planner.addRule(new PlannerTests.GoodSingleRule());
        final List<String> buf = new ArrayList<>();
        planner.addRule(new VolcanoPlannerTest.SubsetRule(buf));
        RelOptCluster cluster = PlannerTests.newCluster(planner);
        PlannerTests.NoneLeafRel leafRel = new PlannerTests.NoneLeafRel(cluster, "a");
        PlannerTests.NoneSingleRel singleRel = new PlannerTests.NoneSingleRel(cluster, leafRel);
        RelNode convertedRel = planner.changeTraits(singleRel, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION));
        planner.setRoot(convertedRel);
        RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof PlannerTests.PhysSingleRel));
        Assert.assertThat(VolcanoPlannerTest.sort(buf), CoreMatchers.equalTo(VolcanoPlannerTest.sort("NoneSingleRel:Subset#0.NONE", "PhysSingleRel:Subset#0.NONE", "PhysSingleRel:Subset#0.PHYS")));
    }

    // NOTE:  this used to fail but now works
    @Test
    public void testWithRemoveTrivialProject() {
        removeTrivialProject(true);
    }

    // NOTE:  this always worked; it's here as contrast to
    // testWithRemoveTrivialProject()
    @Test
    public void testWithoutRemoveTrivialProject() {
        removeTrivialProject(false);
    }

    /**
     * This always worked (in contrast to testRemoveSingleReformed) because it
     * uses a completely-physical pattern (requiring GoodSingleRule to fire
     * first).
     */
    @Test
    public void testRemoveSingleGood() {
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.ambitious = true;
        planner.addRelTraitDef(INSTANCE);
        planner.addRule(new PlannerTests.PhysLeafRule());
        planner.addRule(new PlannerTests.GoodSingleRule());
        planner.addRule(new VolcanoPlannerTest.GoodRemoveSingleRule());
        RelOptCluster cluster = PlannerTests.newCluster(planner);
        PlannerTests.NoneLeafRel leafRel = new PlannerTests.NoneLeafRel(cluster, "a");
        PlannerTests.NoneSingleRel singleRel = new PlannerTests.NoneSingleRel(cluster, leafRel);
        RelNode convertedRel = planner.changeTraits(singleRel, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION));
        planner.setRoot(convertedRel);
        RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof PlannerTests.PhysLeafRel));
        PlannerTests.PhysLeafRel resultLeaf = ((PlannerTests.PhysLeafRel) (result));
        Assert.assertEquals("c", resultLeaf.label);
    }

    // ~ Inner Classes ----------------------------------------------------------
    /**
     * Converter from PHYS to ENUMERABLE convention.
     */
    class PhysToIteratorConverter extends ConverterImpl {
        PhysToIteratorConverter(RelOptCluster cluster, RelNode child) {
            super(cluster, INSTANCE, cluster.traitSetOf(EnumerableConvention.INSTANCE), child);
        }

        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            assert traitSet.comprises(EnumerableConvention.INSTANCE);
            return new VolcanoPlannerTest.PhysToIteratorConverter(getCluster(), sole(inputs));
        }
    }

    /**
     * Rule that matches a {@link RelSubset}.
     */
    private static class SubsetRule extends RelOptRule {
        private final List<String> buf;

        SubsetRule(List<String> buf) {
            super(operand(PlannerTests.TestSingleRel.class, operand(RelSubset.class, any())));
            this.buf = buf;
        }

        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            // Do not transform to anything; just log the calls.
            PlannerTests.TestSingleRel singleRel = call.rel(0);
            RelSubset childRel = call.rel(1);
            Assert.assertThat(call.rels.length, CoreMatchers.equalTo(2));
            buf.add((((singleRel.getClass().getSimpleName()) + ":") + (childRel.getDigest())));
        }
    }

    // NOTE: Previously, ReformedSingleRule didn't work because it explicitly
    // specifies PhysLeafRel rather than RelNode for the single input.  Since
    // the PhysLeafRel is in a different subset from the original NoneLeafRel,
    // ReformedSingleRule never saw it.  (GoodSingleRule saw the NoneLeafRel
    // instead and fires off of that; later the NoneLeafRel gets converted into
    // a PhysLeafRel).  Now Volcano supports rules which match across subsets.
    /**
     * Planner rule that matches a {@link NoneSingleRel} whose input is
     * a {@link PhysLeafRel} in a different subset.
     */
    private static class ReformedSingleRule extends RelOptRule {
        ReformedSingleRule() {
            super(operand(PlannerTests.NoneSingleRel.class, operand(PlannerTests.PhysLeafRel.class, any())));
        }

        @Override
        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            PlannerTests.NoneSingleRel singleRel = call.rel(0);
            RelNode childRel = call.rel(1);
            RelNode physInput = convert(childRel, getTraitSet().replace(PlannerTests.PHYS_CALLING_CONVENTION));
            call.transformTo(new PlannerTests.PhysSingleRel(getCluster(), physInput));
        }
    }

    /**
     * Planner rule that converts a {@link LogicalProject} to PHYS convention.
     */
    private static class PhysProjectRule extends RelOptRule {
        PhysProjectRule() {
            super(operand(LogicalProject.class, any()));
        }

        @Override
        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            final LogicalProject project = call.rel(0);
            RelNode childRel = project.getInput();
            call.transformTo(new PlannerTests.PhysLeafRel(childRel.getCluster(), "b"));
        }
    }

    /**
     * Planner rule that successfully removes a {@link PhysSingleRel}.
     */
    private static class GoodRemoveSingleRule extends RelOptRule {
        GoodRemoveSingleRule() {
            super(operand(PlannerTests.PhysSingleRel.class, operand(PlannerTests.PhysLeafRel.class, any())));
        }

        @Override
        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            PlannerTests.PhysSingleRel singleRel = call.rel(0);
            PlannerTests.PhysLeafRel leafRel = call.rel(1);
            call.transformTo(new PlannerTests.PhysLeafRel(getCluster(), "c"));
        }
    }

    /**
     * Planner rule that removes a {@link NoneSingleRel}.
     */
    private static class ReformedRemoveSingleRule extends RelOptRule {
        ReformedRemoveSingleRule() {
            super(operand(PlannerTests.NoneSingleRel.class, operand(PlannerTests.PhysLeafRel.class, any())));
        }

        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            PlannerTests.NoneSingleRel singleRel = call.rel(0);
            PlannerTests.PhysLeafRel leafRel = call.rel(1);
            call.transformTo(new PlannerTests.PhysLeafRel(getCluster(), "c"));
        }
    }

    /**
     * Implementation of {@link RelOptListener}.
     */
    private static class TestListener implements RelOptListener {
        private List<RelEvent> eventList;

        TestListener() {
            eventList = new ArrayList();
        }

        List<RelEvent> getEventList() {
            return eventList;
        }

        private void recordEvent(RelEvent event) {
            eventList.add(event);
        }

        public void relChosen(RelChosenEvent event) {
            recordEvent(event);
        }

        public void relDiscarded(RelDiscardedEvent event) {
            // Volcano is quite a pack rat--it never discards anything!
            throw new AssertionError(event);
        }

        public void relEquivalenceFound(RelEquivalenceEvent event) {
            if (!(event.isPhysical())) {
                return;
            }
            recordEvent(event);
        }

        public void ruleAttempted(RuleAttemptedEvent event) {
            recordEvent(event);
        }

        public void ruleProductionSucceeded(RuleProductionEvent event) {
            recordEvent(event);
        }
    }
}

/**
 * End VolcanoPlannerTest.java
 */
