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
package org.apache.flink.graph.gsa;


import org.apache.flink.api.common.aggregators.LongSumAggregator;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DeltaIteration;
import org.apache.flink.api.java.operators.DeltaIterationResultSet;
import org.apache.flink.api.java.operators.SingleInputUdfOperator;
import org.apache.flink.api.java.operators.TwoInputUdfOperator;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.types.NullValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the creation of a {@link GatherSumApplyIteration} program.
 */
public class GSATranslationTest {
    private static final String ITERATION_NAME = "Test Name";

    private static final String AGGREGATOR_NAME = "AggregatorName";

    private static final String BC_SET_GATHER_NAME = "gather messages";

    private static final String BC_SET_SUM_NAME = "sum updates";

    private static final String BC_SET_APLLY_NAME = "apply updates";

    private static final int NUM_ITERATIONS = 13;

    private static final int ITERATION_parallelism = 77;

    @Test
    public void testTranslation() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Long> bcGather = env.fromElements(1L);
        DataSet<Long> bcSum = env.fromElements(1L);
        DataSet<Long> bcApply = env.fromElements(1L);
        DataSet<Vertex<Long, Long>> result;
        // ------------ construct the test program ------------------
        DataSet<Edge<Long, NullValue>> edges = env.fromElements(new org.apache.flink.api.java.tuple.Tuple3(1L, 2L, NullValue.getInstance())).map(new org.apache.flink.graph.utils.Tuple3ToEdgeMap());
        Graph<Long, Long, NullValue> graph = Graph.fromDataSet(edges, new GSATranslationTest.InitVertices(), env);
        GSAConfiguration parameters = new GSAConfiguration();
        parameters.registerAggregator(GSATranslationTest.AGGREGATOR_NAME, new LongSumAggregator());
        parameters.setName(GSATranslationTest.ITERATION_NAME);
        parameters.setParallelism(GSATranslationTest.ITERATION_parallelism);
        parameters.addBroadcastSetForGatherFunction(GSATranslationTest.BC_SET_GATHER_NAME, bcGather);
        parameters.addBroadcastSetForSumFunction(GSATranslationTest.BC_SET_SUM_NAME, bcSum);
        parameters.addBroadcastSetForApplyFunction(GSATranslationTest.BC_SET_APLLY_NAME, bcApply);
        result = graph.runGatherSumApplyIteration(new GSATranslationTest.GatherNeighborIds(), new GSATranslationTest.SelectMinId(), new GSATranslationTest.UpdateComponentId(), GSATranslationTest.NUM_ITERATIONS, parameters).getVertices();
        result.output(new org.apache.flink.api.java.io.DiscardingOutputFormat());
        // ------------- validate the java program ----------------
        Assert.assertTrue((result instanceof DeltaIterationResultSet));
        DeltaIterationResultSet<?, ?> resultSet = ((DeltaIterationResultSet<?, ?>) (result));
        DeltaIteration<?, ?> iteration = resultSet.getIterationHead();
        // check the basic iteration properties
        Assert.assertEquals(GSATranslationTest.NUM_ITERATIONS, resultSet.getMaxIterations());
        Assert.assertArrayEquals(new int[]{ 0 }, resultSet.getKeyPositions());
        Assert.assertEquals(GSATranslationTest.ITERATION_parallelism, iteration.getParallelism());
        Assert.assertEquals(GSATranslationTest.ITERATION_NAME, iteration.getName());
        Assert.assertEquals(GSATranslationTest.AGGREGATOR_NAME, iteration.getAggregators().getAllRegisteredAggregators().iterator().next().getName());
        // validate that the semantic properties are set as they should
        TwoInputUdfOperator<?, ?, ?, ?> solutionSetJoin = ((TwoInputUdfOperator<?, ?, ?, ?>) (resultSet.getNextWorkset()));
        Assert.assertTrue(solutionSetJoin.getSemanticProperties().getForwardingTargetFields(0, 0).contains(0));
        Assert.assertTrue(solutionSetJoin.getSemanticProperties().getForwardingTargetFields(1, 0).contains(0));
        SingleInputUdfOperator<?, ?, ?> sumReduce = ((SingleInputUdfOperator<?, ?, ?>) (solutionSetJoin.getInput1()));
        SingleInputUdfOperator<?, ?, ?> gatherMap = ((SingleInputUdfOperator<?, ?, ?>) (sumReduce.getInput()));
        // validate that the broadcast sets are forwarded
        Assert.assertEquals(bcGather, gatherMap.getBroadcastSets().get(GSATranslationTest.BC_SET_GATHER_NAME));
        Assert.assertEquals(bcSum, sumReduce.getBroadcastSets().get(GSATranslationTest.BC_SET_SUM_NAME));
        Assert.assertEquals(bcApply, solutionSetJoin.getBroadcastSets().get(GSATranslationTest.BC_SET_APLLY_NAME));
    }

    @SuppressWarnings("serial")
    private static final class InitVertices implements MapFunction<Long, Long> {
        public Long map(Long vertexId) {
            return vertexId;
        }
    }

    @SuppressWarnings("serial")
    private static final class GatherNeighborIds extends GatherFunction<Long, NullValue, Long> {
        public Long gather(Neighbor<Long, NullValue> neighbor) {
            return neighbor.getNeighborValue();
        }
    }

    @SuppressWarnings("serial")
    private static final class SelectMinId extends SumFunction<Long, NullValue, Long> {
        public Long sum(Long newValue, Long currentValue) {
            return Math.min(newValue, currentValue);
        }
    }

    @SuppressWarnings("serial")
    private static final class UpdateComponentId extends ApplyFunction<Long, Long, Long> {
        public void apply(Long summedValue, Long origValue) {
            if (summedValue < origValue) {
                setResult(summedValue);
            }
        }
    }
}

