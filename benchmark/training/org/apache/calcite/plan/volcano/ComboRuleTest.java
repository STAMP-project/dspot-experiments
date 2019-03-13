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
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link VolcanoPlanner}
 */
public class ComboRuleTest {
    @Test
    public void testCombo() {
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(INSTANCE);
        planner.addRule(new ComboRuleTest.ComboRule());
        planner.addRule(new ComboRuleTest.AddIntermediateNodeRule());
        planner.addRule(new PlannerTests.GoodSingleRule());
        RelOptCluster cluster = PlannerTests.newCluster(planner);
        PlannerTests.NoneLeafRel leafRel = new PlannerTests.NoneLeafRel(cluster, "a");
        PlannerTests.NoneSingleRel singleRel = new PlannerTests.NoneSingleRel(cluster, leafRel);
        PlannerTests.NoneSingleRel singleRel2 = new PlannerTests.NoneSingleRel(cluster, singleRel);
        RelNode convertedRel = planner.changeTraits(singleRel2, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION));
        planner.setRoot(convertedRel);
        RelNode result = planner.chooseDelegate().findBestExp();
        Assert.assertTrue((result instanceof ComboRuleTest.IntermediateNode));
    }

    /**
     * Intermediate node, the cost decreases as it is pushed up the tree
     * (more inputs it has, cheaper it gets).
     */
    private static class IntermediateNode extends PlannerTests.TestSingleRel {
        final int nodesBelowCount;

        IntermediateNode(RelOptCluster cluster, RelNode input, int nodesBelowCount) {
            super(cluster, cluster.traitSetOf(PlannerTests.PHYS_CALLING_CONVENTION), input);
            this.nodesBelowCount = nodesBelowCount;
        }

        @Override
        public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
            return planner.getCostFactory().makeCost(100, 100, 100).multiplyBy((1.0 / (nodesBelowCount)));
        }

        public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
            assert traitSet.comprises(PlannerTests.PHYS_CALLING_CONVENTION);
            return new ComboRuleTest.IntermediateNode(getCluster(), sole(inputs), nodesBelowCount);
        }
    }

    /**
     * Rule that adds an intermediate node above the {@link PhysLeafRel}.
     */
    private static class AddIntermediateNodeRule extends RelOptRule {
        AddIntermediateNodeRule() {
            super(operand(PlannerTests.NoneLeafRel.class, any()));
        }

        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        public void onMatch(RelOptRuleCall call) {
            PlannerTests.NoneLeafRel leaf = call.rel(0);
            RelNode physLeaf = new PlannerTests.PhysLeafRel(getCluster(), leaf.label);
            RelNode intermediateNode = new ComboRuleTest.IntermediateNode(physLeaf.getCluster(), physLeaf, 1);
            call.transformTo(intermediateNode);
        }
    }

    /**
     * Matches {@link PhysSingleRel}-{@link IntermediateNode}-Any
     * and converts to {@link IntermediateNode}-{@link PhysSingleRel}-Any.
     */
    private static class ComboRule extends RelOptRule {
        ComboRule() {
            super(ComboRuleTest.ComboRule.createOperand());
        }

        private static RelOptRuleOperand createOperand() {
            RelOptRuleOperand input = operand(RelNode.class, any());
            input = operand(ComboRuleTest.IntermediateNode.class, some(input));
            input = operand(PlannerTests.PhysSingleRel.class, some(input));
            return input;
        }

        @Override
        public Convention getOutConvention() {
            return PlannerTests.PHYS_CALLING_CONVENTION;
        }

        @Override
        public boolean matches(RelOptRuleCall call) {
            if ((call.rels.length) < 3) {
                return false;
            }
            if ((((call.rel(0)) instanceof PlannerTests.PhysSingleRel) && ((call.rel(1)) instanceof ComboRuleTest.IntermediateNode)) && ((call.rel(2)) instanceof RelNode)) {
                return true;
            }
            return false;
        }

        @Override
        public void onMatch(RelOptRuleCall call) {
            List<RelNode> newInputs = ImmutableList.of(call.rel(2));
            ComboRuleTest.IntermediateNode oldInter = call.rel(1);
            RelNode physRel = call.rel(0).copy(call.rel(0).getTraitSet(), newInputs);
            RelNode converted = new ComboRuleTest.IntermediateNode(physRel.getCluster(), physRel, ((oldInter.nodesBelowCount) + 1));
            call.transformTo(converted);
        }
    }
}

/**
 * End ComboRuleTest.java
 */
