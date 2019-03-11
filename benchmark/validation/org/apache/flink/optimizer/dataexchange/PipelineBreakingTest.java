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
package org.apache.flink.optimizer.dataexchange;


import java.util.List;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.optimizer.dag.DataSinkNode;
import org.apache.flink.optimizer.dag.SingleInputNode;
import org.apache.flink.optimizer.dag.TwoInputNode;
import org.apache.flink.optimizer.testfunctions.DummyCoGroupFunction;
import org.apache.flink.optimizer.testfunctions.DummyFlatJoinFunction;
import org.apache.flink.optimizer.testfunctions.IdentityFlatMapper;
import org.apache.flink.optimizer.testfunctions.IdentityKeyExtractor;
import org.apache.flink.optimizer.testfunctions.SelectOneReducer;
import org.apache.flink.optimizer.testfunctions.Top1GroupReducer;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test checks whether connections are correctly marked as pipelined breaking.
 */
@SuppressWarnings("serial")
public class PipelineBreakingTest {
    /**
     * Tests that no pipeline breakers are inserted into a simple forward
     * pipeline.
     *
     * <pre>
     *     (source) -> (map) -> (filter) -> (groupBy / reduce)
     * </pre>
     */
    @Test
    public void testSimpleForwardPlan() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<String> dataSet = env.readTextFile("/never/accessed");
            dataSet.map(new org.apache.flink.api.common.functions.MapFunction<String, Integer>() {
                @Override
                public Integer map(String value) {
                    return 0;
                }
            }).filter(new org.apache.flink.api.common.functions.FilterFunction<Integer>() {
                @Override
                public boolean filter(Integer value) {
                    return false;
                }
            }).groupBy(new IdentityKeyExtractor<Integer>()).reduceGroup(new Top1GroupReducer<Integer>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Integer>());
            DataSinkNode sinkNode = PipelineBreakingTest.convertPlan(env.createProgramPlan()).get(0);
            SingleInputNode reduceNode = ((SingleInputNode) (sinkNode.getPredecessorNode()));
            SingleInputNode keyExtractorNode = ((SingleInputNode) (reduceNode.getPredecessorNode()));
            SingleInputNode filterNode = ((SingleInputNode) (keyExtractorNode.getPredecessorNode()));
            SingleInputNode mapNode = ((SingleInputNode) (filterNode.getPredecessorNode()));
            Assert.assertFalse(sinkNode.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(reduceNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(keyExtractorNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(filterNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(mapNode.getIncomingConnection().isBreakingPipeline());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests that branching plans, where the branches are not re-joined,
     * do not place pipeline breakers.
     *
     * <pre>
     *                      /---> (filter) -> (sink)
     *                     /
     *                    /
     * (source) -> (map) -----------------\
     *                    \               (join) -> (sink)
     *                     \   (source) --/
     *                      \
     *                       \
     *                        \-> (sink)
     * </pre>
     */
    @Test
    public void testBranchingPlanNotReJoined() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Integer> data = env.readTextFile("/never/accessed").map(new org.apache.flink.api.common.functions.MapFunction<String, Integer>() {
                @Override
                public Integer map(String value) {
                    return 0;
                }
            });
            // output 1
            data.filter(new org.apache.flink.api.common.functions.FilterFunction<Integer>() {
                @Override
                public boolean filter(Integer value) {
                    return false;
                }
            }).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Integer>());
            // output 2 does a join before a join
            data.join(env.fromElements(1, 2, 3, 4)).where(new IdentityKeyExtractor<Integer>()).equalTo(new IdentityKeyExtractor<Integer>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Integer, Integer>>());
            // output 3 is direct
            data.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Integer>());
            List<DataSinkNode> sinks = PipelineBreakingTest.convertPlan(env.createProgramPlan());
            // gather the optimizer DAG nodes
            DataSinkNode sinkAfterFilter = sinks.get(0);
            DataSinkNode sinkAfterJoin = sinks.get(1);
            DataSinkNode sinkDirect = sinks.get(2);
            SingleInputNode filterNode = ((SingleInputNode) (sinkAfterFilter.getPredecessorNode()));
            SingleInputNode mapNode = ((SingleInputNode) (filterNode.getPredecessorNode()));
            TwoInputNode joinNode = ((TwoInputNode) (sinkAfterJoin.getPredecessorNode()));
            SingleInputNode joinInput = ((SingleInputNode) (joinNode.getSecondPredecessorNode()));
            // verify the non-pipeline breaking status
            Assert.assertFalse(sinkAfterFilter.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(sinkAfterJoin.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(sinkDirect.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(filterNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(mapNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(joinNode.getFirstIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(joinNode.getSecondIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(joinInput.getIncomingConnection().isBreakingPipeline());
            // some other sanity checks on the plan construction (cannot hurt)
            Assert.assertEquals(mapNode, getPredecessorNode());
            Assert.assertEquals(mapNode, sinkDirect.getPredecessorNode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests that branches that are re-joined have place pipeline breakers.
     *
     * <pre>
     *                                         /-> (sink)
     *                                        /
     *                         /-> (reduce) -+          /-> (flatmap) -> (sink)
     *                        /               \        /
     *     (source) -> (map) -                (join) -+-----\
     *                        \               /              \
     *                         \-> (filter) -+                \
     *                                       \                (co group) -> (sink)
     *                                        \                /
     *                                         \-> (reduce) - /
     * </pre>
     */
    @Test
    public void testReJoinedBranches() {
        try {
            // build a test program
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Long, Long>> data = env.fromElements(33L, 44L).map(new org.apache.flink.api.common.functions.MapFunction<Long, Tuple2<Long, Long>>() {
                @Override
                public Tuple2<Long, Long> map(Long value) {
                    return new Tuple2<Long, Long>(value, value);
                }
            });
            DataSet<Tuple2<Long, Long>> reduced = data.groupBy(0).reduce(new SelectOneReducer<Tuple2<Long, Long>>());
            reduced.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Long, Long>>());
            DataSet<Tuple2<Long, Long>> filtered = data.filter(new org.apache.flink.api.common.functions.FilterFunction<Tuple2<Long, Long>>() {
                @Override
                public boolean filter(Tuple2<Long, Long> value) throws Exception {
                    return false;
                }
            });
            DataSet<Tuple2<Long, Long>> joined = reduced.join(filtered).where(1).equalTo(1).with(new DummyFlatJoinFunction<Tuple2<Long, Long>>());
            joined.flatMap(new IdentityFlatMapper<Tuple2<Long, Long>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Long, Long>>());
            joined.coGroup(filtered.groupBy(1).reduceGroup(new Top1GroupReducer<Tuple2<Long, Long>>())).where(0).equalTo(0).with(new DummyCoGroupFunction<Tuple2<Long, Long>, Tuple2<Long, Long>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Tuple2<Long, Long>, Tuple2<Long, Long>>>());
            List<DataSinkNode> sinks = PipelineBreakingTest.convertPlan(env.createProgramPlan());
            // gather the optimizer DAG nodes
            DataSinkNode sinkAfterReduce = sinks.get(0);
            DataSinkNode sinkAfterFlatMap = sinks.get(1);
            DataSinkNode sinkAfterCoGroup = sinks.get(2);
            SingleInputNode reduceNode = ((SingleInputNode) (sinkAfterReduce.getPredecessorNode()));
            SingleInputNode mapNode = ((SingleInputNode) (reduceNode.getPredecessorNode()));
            SingleInputNode flatMapNode = ((SingleInputNode) (sinkAfterFlatMap.getPredecessorNode()));
            TwoInputNode joinNode = ((TwoInputNode) (flatMapNode.getPredecessorNode()));
            SingleInputNode filterNode = ((SingleInputNode) (joinNode.getSecondPredecessorNode()));
            TwoInputNode coGroupNode = ((TwoInputNode) (sinkAfterCoGroup.getPredecessorNode()));
            SingleInputNode otherReduceNode = ((SingleInputNode) (coGroupNode.getSecondPredecessorNode()));
            // test sanity checks (that we constructed the DAG correctly)
            Assert.assertEquals(reduceNode, joinNode.getFirstPredecessorNode());
            Assert.assertEquals(mapNode, filterNode.getPredecessorNode());
            Assert.assertEquals(joinNode, coGroupNode.getFirstPredecessorNode());
            Assert.assertEquals(filterNode, otherReduceNode.getPredecessorNode());
            // verify the pipeline breaking status
            Assert.assertFalse(sinkAfterReduce.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(sinkAfterFlatMap.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(sinkAfterCoGroup.getInputConnection().isBreakingPipeline());
            Assert.assertFalse(mapNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(flatMapNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(joinNode.getFirstIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(coGroupNode.getFirstIncomingConnection().isBreakingPipeline());
            Assert.assertFalse(coGroupNode.getSecondIncomingConnection().isBreakingPipeline());
            // these should be pipeline breakers
            Assert.assertTrue(reduceNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertTrue(filterNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertTrue(otherReduceNode.getIncomingConnection().isBreakingPipeline());
            Assert.assertTrue(joinNode.getSecondIncomingConnection().isBreakingPipeline());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

