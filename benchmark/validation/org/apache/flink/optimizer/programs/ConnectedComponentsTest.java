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
package org.apache.flink.optimizer.programs;


import DataExchangeMode.BATCH;
import DriverStrategy.HYBRIDHASH_BUILD_FIRST;
import DriverStrategy.HYBRIDHASH_BUILD_SECOND;
import DriverStrategy.HYBRIDHASH_BUILD_SECOND_CACHED;
import DriverStrategy.NONE;
import LocalStrategy.COMBININGSORT;
import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_HASH;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.operators.util.FieldList;
import org.apache.flink.optimizer.plan.DualInputPlanNode;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.plan.SourcePlanNode;
import org.apache.flink.optimizer.plan.WorksetIterationPlanNode;
import org.apache.flink.optimizer.plantranslate.JobGraphGenerator;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class ConnectedComponentsTest extends CompilerTestBase {
    private static final String VERTEX_SOURCE = "Vertices";

    private static final String ITERATION_NAME = "Connected Components Iteration";

    private static final String EDGES_SOURCE = "Edges";

    private static final String JOIN_NEIGHBORS_MATCH = "Join Candidate Id With Neighbor";

    private static final String MIN_ID_REDUCER = "Find Minimum Candidate Id";

    private static final String UPDATE_ID_MATCH = "Update Component Id";

    private static final String SINK = "Result";

    private final FieldList set0 = new FieldList(0);

    @Test
    public void testWorksetConnectedComponents() {
        Plan plan = ConnectedComponentsTest.getConnectedComponentsPlan(CompilerTestBase.DEFAULT_PARALLELISM, 100, false);
        OptimizedPlan optPlan = compileNoStats(plan);
        CompilerTestBase.OptimizerPlanNodeResolver or = CompilerTestBase.getOptimizerPlanNodeResolver(optPlan);
        SourcePlanNode vertexSource = or.getNode(ConnectedComponentsTest.VERTEX_SOURCE);
        SourcePlanNode edgesSource = or.getNode(ConnectedComponentsTest.EDGES_SOURCE);
        SinkPlanNode sink = or.getNode(ConnectedComponentsTest.SINK);
        WorksetIterationPlanNode iter = or.getNode(ConnectedComponentsTest.ITERATION_NAME);
        DualInputPlanNode neighborsJoin = or.getNode(ConnectedComponentsTest.JOIN_NEIGHBORS_MATCH);
        SingleInputPlanNode minIdReducer = or.getNode(ConnectedComponentsTest.MIN_ID_REDUCER);
        SingleInputPlanNode minIdCombiner = ((SingleInputPlanNode) (minIdReducer.getPredecessor()));
        DualInputPlanNode updatingMatch = or.getNode(ConnectedComponentsTest.UPDATE_ID_MATCH);
        // test all drivers
        Assert.assertEquals(NONE, sink.getDriverStrategy());
        Assert.assertEquals(NONE, vertexSource.getDriverStrategy());
        Assert.assertEquals(NONE, edgesSource.getDriverStrategy());
        Assert.assertEquals(HYBRIDHASH_BUILD_SECOND_CACHED, neighborsJoin.getDriverStrategy());
        Assert.assertTrue((!(neighborsJoin.getInput1().getTempMode().isCached())));
        Assert.assertTrue((!(neighborsJoin.getInput2().getTempMode().isCached())));
        Assert.assertEquals(set0, neighborsJoin.getKeysForInput1());
        Assert.assertEquals(set0, neighborsJoin.getKeysForInput2());
        Assert.assertEquals(HYBRIDHASH_BUILD_SECOND, updatingMatch.getDriverStrategy());
        Assert.assertEquals(set0, updatingMatch.getKeysForInput1());
        Assert.assertEquals(set0, updatingMatch.getKeysForInput2());
        // test all the shipping strategies
        Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, iter.getInitialSolutionSetInput().getShipStrategy());
        Assert.assertEquals(set0, iter.getInitialSolutionSetInput().getShipStrategyKeys());
        Assert.assertEquals(PARTITION_HASH, iter.getInitialWorksetInput().getShipStrategy());
        Assert.assertEquals(set0, iter.getInitialWorksetInput().getShipStrategyKeys());
        Assert.assertEquals(FORWARD, neighborsJoin.getInput1().getShipStrategy());// workset

        Assert.assertEquals(PARTITION_HASH, neighborsJoin.getInput2().getShipStrategy());// edges

        Assert.assertEquals(set0, neighborsJoin.getInput2().getShipStrategyKeys());
        Assert.assertEquals(PARTITION_HASH, minIdReducer.getInput().getShipStrategy());
        Assert.assertEquals(set0, minIdReducer.getInput().getShipStrategyKeys());
        Assert.assertEquals(FORWARD, minIdCombiner.getInput().getShipStrategy());
        Assert.assertEquals(FORWARD, updatingMatch.getInput1().getShipStrategy());// min id

        Assert.assertEquals(FORWARD, updatingMatch.getInput2().getShipStrategy());// solution set

        // test all the local strategies
        Assert.assertEquals(LocalStrategy.NONE, sink.getInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, iter.getInitialSolutionSetInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, iter.getInitialWorksetInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, neighborsJoin.getInput1().getLocalStrategy());// workset

        Assert.assertEquals(LocalStrategy.NONE, neighborsJoin.getInput2().getLocalStrategy());// edges

        Assert.assertEquals(COMBININGSORT, minIdReducer.getInput().getLocalStrategy());
        Assert.assertEquals(set0, minIdReducer.getInput().getLocalStrategyKeys());
        Assert.assertEquals(LocalStrategy.NONE, minIdCombiner.getInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, updatingMatch.getInput1().getLocalStrategy());// min id

        Assert.assertEquals(LocalStrategy.NONE, updatingMatch.getInput2().getLocalStrategy());// solution set

        // check the dams
        Assert.assertEquals(TempMode.NONE, iter.getInitialWorksetInput().getTempMode());
        Assert.assertEquals(TempMode.NONE, iter.getInitialSolutionSetInput().getTempMode());
        Assert.assertEquals(BATCH, iter.getInitialWorksetInput().getDataExchangeMode());
        Assert.assertEquals(BATCH, iter.getInitialSolutionSetInput().getDataExchangeMode());
        JobGraphGenerator jgg = new JobGraphGenerator();
        jgg.compileJobGraph(optPlan);
    }

    @Test
    public void testWorksetConnectedComponentsWithSolutionSetAsFirstInput() {
        Plan plan = ConnectedComponentsTest.getConnectedComponentsPlan(CompilerTestBase.DEFAULT_PARALLELISM, 100, true);
        OptimizedPlan optPlan = compileNoStats(plan);
        CompilerTestBase.OptimizerPlanNodeResolver or = CompilerTestBase.getOptimizerPlanNodeResolver(optPlan);
        SourcePlanNode vertexSource = or.getNode(ConnectedComponentsTest.VERTEX_SOURCE);
        SourcePlanNode edgesSource = or.getNode(ConnectedComponentsTest.EDGES_SOURCE);
        SinkPlanNode sink = or.getNode(ConnectedComponentsTest.SINK);
        WorksetIterationPlanNode iter = or.getNode(ConnectedComponentsTest.ITERATION_NAME);
        DualInputPlanNode neighborsJoin = or.getNode(ConnectedComponentsTest.JOIN_NEIGHBORS_MATCH);
        SingleInputPlanNode minIdReducer = or.getNode(ConnectedComponentsTest.MIN_ID_REDUCER);
        SingleInputPlanNode minIdCombiner = ((SingleInputPlanNode) (minIdReducer.getPredecessor()));
        DualInputPlanNode updatingMatch = or.getNode(ConnectedComponentsTest.UPDATE_ID_MATCH);
        // test all drivers
        Assert.assertEquals(NONE, sink.getDriverStrategy());
        Assert.assertEquals(NONE, vertexSource.getDriverStrategy());
        Assert.assertEquals(NONE, edgesSource.getDriverStrategy());
        Assert.assertEquals(HYBRIDHASH_BUILD_SECOND_CACHED, neighborsJoin.getDriverStrategy());
        Assert.assertTrue((!(neighborsJoin.getInput1().getTempMode().isCached())));
        Assert.assertTrue((!(neighborsJoin.getInput2().getTempMode().isCached())));
        Assert.assertEquals(set0, neighborsJoin.getKeysForInput1());
        Assert.assertEquals(set0, neighborsJoin.getKeysForInput2());
        Assert.assertEquals(HYBRIDHASH_BUILD_FIRST, updatingMatch.getDriverStrategy());
        Assert.assertEquals(set0, updatingMatch.getKeysForInput1());
        Assert.assertEquals(set0, updatingMatch.getKeysForInput2());
        // test all the shipping strategies
        Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, iter.getInitialSolutionSetInput().getShipStrategy());
        Assert.assertEquals(set0, iter.getInitialSolutionSetInput().getShipStrategyKeys());
        Assert.assertEquals(PARTITION_HASH, iter.getInitialWorksetInput().getShipStrategy());
        Assert.assertEquals(set0, iter.getInitialWorksetInput().getShipStrategyKeys());
        Assert.assertEquals(FORWARD, neighborsJoin.getInput1().getShipStrategy());// workset

        Assert.assertEquals(PARTITION_HASH, neighborsJoin.getInput2().getShipStrategy());// edges

        Assert.assertEquals(set0, neighborsJoin.getInput2().getShipStrategyKeys());
        Assert.assertEquals(PARTITION_HASH, minIdReducer.getInput().getShipStrategy());
        Assert.assertEquals(set0, minIdReducer.getInput().getShipStrategyKeys());
        Assert.assertEquals(FORWARD, minIdCombiner.getInput().getShipStrategy());
        Assert.assertEquals(FORWARD, updatingMatch.getInput1().getShipStrategy());// solution set

        Assert.assertEquals(FORWARD, updatingMatch.getInput2().getShipStrategy());// min id

        // test all the local strategies
        Assert.assertEquals(LocalStrategy.NONE, sink.getInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, iter.getInitialSolutionSetInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, iter.getInitialWorksetInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, neighborsJoin.getInput1().getLocalStrategy());// workset

        Assert.assertEquals(LocalStrategy.NONE, neighborsJoin.getInput2().getLocalStrategy());// edges

        Assert.assertEquals(COMBININGSORT, minIdReducer.getInput().getLocalStrategy());
        Assert.assertEquals(set0, minIdReducer.getInput().getLocalStrategyKeys());
        Assert.assertEquals(LocalStrategy.NONE, minIdCombiner.getInput().getLocalStrategy());
        Assert.assertEquals(LocalStrategy.NONE, updatingMatch.getInput1().getLocalStrategy());// min id

        Assert.assertEquals(LocalStrategy.NONE, updatingMatch.getInput2().getLocalStrategy());// solution set

        // check the dams
        Assert.assertEquals(TempMode.NONE, iter.getInitialWorksetInput().getTempMode());
        Assert.assertEquals(TempMode.NONE, iter.getInitialSolutionSetInput().getTempMode());
        Assert.assertEquals(BATCH, iter.getInitialWorksetInput().getDataExchangeMode());
        Assert.assertEquals(BATCH, iter.getInitialSolutionSetInput().getDataExchangeMode());
        JobGraphGenerator jgg = new JobGraphGenerator();
        jgg.compileJobGraph(optPlan);
    }
}

