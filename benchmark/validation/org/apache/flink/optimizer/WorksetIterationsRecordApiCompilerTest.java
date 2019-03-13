/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.optimizer;


import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_HASH;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.operators.util.FieldList;
import org.apache.flink.optimizer.plan.DualInputPlanNode;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plantranslate.JobGraphGenerator;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.apache.flink.runtime.operators.shipping.ShipStrategyType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that validate optimizer choices when using operators that are requesting certain specific execution
 * strategies.
 */
public class WorksetIterationsRecordApiCompilerTest extends CompilerTestBase {
    private static final long serialVersionUID = 1L;

    private static final String ITERATION_NAME = "Test Workset Iteration";

    private static final String JOIN_WITH_INVARIANT_NAME = "Test Join Invariant";

    private static final String JOIN_WITH_SOLUTION_SET = "Test Join SolutionSet";

    private static final String NEXT_WORKSET_REDUCER_NAME = "Test Reduce Workset";

    private static final String SOLUTION_DELTA_MAPPER_NAME = "Test Map Delta";

    private final FieldList list0 = new FieldList(0);

    @Test
    public void testRecordApiWithDeferredSoltionSetUpdateWithMapper() {
        Plan plan = getTestPlan(false, true);
        OptimizedPlan oPlan;
        try {
            oPlan = compileNoStats(plan);
        } catch (CompilerException ce) {
            ce.printStackTrace();
            Assert.fail("The pact compiler is unable to compile this plan correctly.");
            return;// silence the compiler

        }
        CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
        DualInputPlanNode joinWithInvariantNode = resolver.getNode(WorksetIterationsRecordApiCompilerTest.JOIN_WITH_INVARIANT_NAME);
        DualInputPlanNode joinWithSolutionSetNode = resolver.getNode(WorksetIterationsRecordApiCompilerTest.JOIN_WITH_SOLUTION_SET);
        SingleInputPlanNode worksetReducer = resolver.getNode(WorksetIterationsRecordApiCompilerTest.NEXT_WORKSET_REDUCER_NAME);
        SingleInputPlanNode deltaMapper = resolver.getNode(WorksetIterationsRecordApiCompilerTest.SOLUTION_DELTA_MAPPER_NAME);
        // iteration preserves partitioning in reducer, so the first partitioning is out of the loop,
        // the in-loop partitioning is before the final reducer
        // verify joinWithInvariant
        Assert.assertEquals(FORWARD, joinWithInvariantNode.getInput1().getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, joinWithInvariantNode.getInput2().getShipStrategy());
        Assert.assertEquals(list0, joinWithInvariantNode.getKeysForInput1());
        Assert.assertEquals(list0, joinWithInvariantNode.getKeysForInput2());
        // verify joinWithSolutionSet
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getInput1().getShipStrategy());
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getInput2().getShipStrategy());
        // verify reducer
        Assert.assertEquals(PARTITION_HASH, worksetReducer.getInput().getShipStrategy());
        Assert.assertEquals(list0, worksetReducer.getKeys(0));
        // currently, the system may partition before or after the mapper
        ShipStrategyType ss1 = deltaMapper.getInput().getShipStrategy();
        ShipStrategyType ss2 = deltaMapper.getOutgoingChannels().get(0).getShipStrategy();
        Assert.assertTrue((((ss1 == (ShipStrategyType.FORWARD)) && (ss2 == (ShipStrategyType.PARTITION_HASH))) || ((ss2 == (ShipStrategyType.FORWARD)) && (ss1 == (ShipStrategyType.PARTITION_HASH)))));
        new JobGraphGenerator().compileJobGraph(oPlan);
    }

    @Test
    public void testRecordApiWithDeferredSoltionSetUpdateWithNonPreservingJoin() {
        Plan plan = getTestPlan(false, false);
        OptimizedPlan oPlan;
        try {
            oPlan = compileNoStats(plan);
        } catch (CompilerException ce) {
            ce.printStackTrace();
            Assert.fail("The pact compiler is unable to compile this plan correctly.");
            return;// silence the compiler

        }
        CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
        DualInputPlanNode joinWithInvariantNode = resolver.getNode(WorksetIterationsRecordApiCompilerTest.JOIN_WITH_INVARIANT_NAME);
        DualInputPlanNode joinWithSolutionSetNode = resolver.getNode(WorksetIterationsRecordApiCompilerTest.JOIN_WITH_SOLUTION_SET);
        SingleInputPlanNode worksetReducer = resolver.getNode(WorksetIterationsRecordApiCompilerTest.NEXT_WORKSET_REDUCER_NAME);
        // iteration preserves partitioning in reducer, so the first partitioning is out of the loop,
        // the in-loop partitioning is before the final reducer
        // verify joinWithInvariant
        Assert.assertEquals(FORWARD, joinWithInvariantNode.getInput1().getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, joinWithInvariantNode.getInput2().getShipStrategy());
        Assert.assertEquals(list0, joinWithInvariantNode.getKeysForInput1());
        Assert.assertEquals(list0, joinWithInvariantNode.getKeysForInput2());
        // verify joinWithSolutionSet
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getInput1().getShipStrategy());
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getInput2().getShipStrategy());
        // verify reducer
        Assert.assertEquals(PARTITION_HASH, worksetReducer.getInput().getShipStrategy());
        Assert.assertEquals(list0, worksetReducer.getKeys(0));
        // verify solution delta
        Assert.assertEquals(2, joinWithSolutionSetNode.getOutgoingChannels().size());
        Assert.assertEquals(PARTITION_HASH, joinWithSolutionSetNode.getOutgoingChannels().get(0).getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, joinWithSolutionSetNode.getOutgoingChannels().get(1).getShipStrategy());
        new JobGraphGenerator().compileJobGraph(oPlan);
    }

    @Test
    public void testRecordApiWithDirectSoltionSetUpdate() {
        Plan plan = getTestPlan(true, false);
        OptimizedPlan oPlan;
        try {
            oPlan = compileNoStats(plan);
        } catch (CompilerException ce) {
            ce.printStackTrace();
            Assert.fail("The pact compiler is unable to compile this plan correctly.");
            return;// silence the compiler

        }
        CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
        DualInputPlanNode joinWithInvariantNode = resolver.getNode(WorksetIterationsRecordApiCompilerTest.JOIN_WITH_INVARIANT_NAME);
        DualInputPlanNode joinWithSolutionSetNode = resolver.getNode(WorksetIterationsRecordApiCompilerTest.JOIN_WITH_SOLUTION_SET);
        SingleInputPlanNode worksetReducer = resolver.getNode(WorksetIterationsRecordApiCompilerTest.NEXT_WORKSET_REDUCER_NAME);
        // iteration preserves partitioning in reducer, so the first partitioning is out of the loop,
        // the in-loop partitioning is before the final reducer
        // verify joinWithInvariant
        Assert.assertEquals(FORWARD, joinWithInvariantNode.getInput1().getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, joinWithInvariantNode.getInput2().getShipStrategy());
        Assert.assertEquals(list0, joinWithInvariantNode.getKeysForInput1());
        Assert.assertEquals(list0, joinWithInvariantNode.getKeysForInput2());
        // verify joinWithSolutionSet
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getInput1().getShipStrategy());
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getInput2().getShipStrategy());
        // verify reducer
        Assert.assertEquals(FORWARD, worksetReducer.getInput().getShipStrategy());
        Assert.assertEquals(list0, worksetReducer.getKeys(0));
        // verify solution delta
        Assert.assertEquals(1, joinWithSolutionSetNode.getOutgoingChannels().size());
        Assert.assertEquals(FORWARD, joinWithSolutionSetNode.getOutgoingChannels().get(0).getShipStrategy());
        new JobGraphGenerator().compileJobGraph(oPlan);
    }
}

