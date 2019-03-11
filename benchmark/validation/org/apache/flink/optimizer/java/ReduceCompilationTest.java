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
package org.apache.flink.optimizer.java;


import CombineHint.HASH;
import CombineHint.NONE;
import DriverStrategy.ALL_REDUCE;
import DriverStrategy.HASHED_PARTIAL_REDUCE;
import DriverStrategy.SORTED_PARTIAL_REDUCE;
import DriverStrategy.SORTED_REDUCE;
import java.io.Serializable;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.operators.util.FieldList;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.plan.SourcePlanNode;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class ReduceCompilationTest extends CompilerTestBase implements Serializable {
    @Test
    public void testAllReduceNoCombiner() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Double> data = env.fromElements(0.2, 0.3, 0.4, 0.5).name("source");
            data.reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Double>() {
                @Override
                public Double reduce(Double value1, Double value2) {
                    return value1 + value2;
                }
            }).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Double>()).name("sink");
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(op);
            // the all-reduce has no combiner, when the parallelism of the input is one
            SourcePlanNode sourceNode = resolver.getNode("source");
            SingleInputPlanNode reduceNode = resolver.getNode("reducer");
            SinkPlanNode sinkNode = resolver.getNode("sink");
            // check wiring
            Assert.assertEquals(sourceNode, reduceNode.getInput().getSource());
            Assert.assertEquals(reduceNode, sinkNode.getInput().getSource());
            // check parallelism
            Assert.assertEquals(1, sourceNode.getParallelism());
            Assert.assertEquals(1, reduceNode.getParallelism());
            Assert.assertEquals(1, sinkNode.getParallelism());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " in test: ") + (e.getMessage())));
        }
    }

    @Test
    public void testAllReduceWithCombiner() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Long> data = env.generateSequence(1, 8000000).name("source");
            data.reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Long>() {
                @Override
                public Long reduce(Long value1, Long value2) {
                    return value1 + value2;
                }
            }).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Long>()).name("sink");
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(op);
            // get the original nodes
            SourcePlanNode sourceNode = resolver.getNode("source");
            SingleInputPlanNode reduceNode = resolver.getNode("reducer");
            SinkPlanNode sinkNode = resolver.getNode("sink");
            // get the combiner
            SingleInputPlanNode combineNode = ((SingleInputPlanNode) (reduceNode.getInput().getSource()));
            // check wiring
            Assert.assertEquals(sourceNode, combineNode.getInput().getSource());
            Assert.assertEquals(reduceNode, sinkNode.getInput().getSource());
            // check that both reduce and combiner have the same strategy
            Assert.assertEquals(ALL_REDUCE, reduceNode.getDriverStrategy());
            Assert.assertEquals(ALL_REDUCE, combineNode.getDriverStrategy());
            // check parallelism
            Assert.assertEquals(8, sourceNode.getParallelism());
            Assert.assertEquals(8, combineNode.getParallelism());
            Assert.assertEquals(1, reduceNode.getParallelism());
            Assert.assertEquals(1, sinkNode.getParallelism());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " in test: ") + (e.getMessage())));
        }
    }

    @Test
    public void testGroupedReduceWithFieldPositionKey() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            data.groupBy(1).reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Tuple2<String, Double>>() {
                @Override
                public Tuple2<String, Double> reduce(Tuple2<String, Double> value1, Tuple2<String, Double> value2) {
                    return null;
                }
            }).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(op);
            // get the original nodes
            SourcePlanNode sourceNode = resolver.getNode("source");
            SingleInputPlanNode reduceNode = resolver.getNode("reducer");
            SinkPlanNode sinkNode = resolver.getNode("sink");
            // get the combiner
            SingleInputPlanNode combineNode = ((SingleInputPlanNode) (reduceNode.getInput().getSource()));
            // check wiring
            Assert.assertEquals(sourceNode, combineNode.getInput().getSource());
            Assert.assertEquals(reduceNode, sinkNode.getInput().getSource());
            // check the strategies
            Assert.assertEquals(SORTED_REDUCE, reduceNode.getDriverStrategy());
            Assert.assertEquals(SORTED_PARTIAL_REDUCE, combineNode.getDriverStrategy());
            // check the keys
            Assert.assertEquals(new FieldList(1), reduceNode.getKeys(0));
            Assert.assertEquals(new FieldList(1), combineNode.getKeys(0));
            Assert.assertEquals(new FieldList(1), reduceNode.getInput().getLocalStrategyKeys());
            // check parallelism
            Assert.assertEquals(6, sourceNode.getParallelism());
            Assert.assertEquals(6, combineNode.getParallelism());
            Assert.assertEquals(8, reduceNode.getParallelism());
            Assert.assertEquals(8, sinkNode.getParallelism());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " in test: ") + (e.getMessage())));
        }
    }

    @Test
    public void testGroupedReduceWithSelectorFunctionKey() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            data.groupBy(new org.apache.flink.api.java.functions.KeySelector<Tuple2<String, Double>, String>() {
                public String getKey(Tuple2<String, Double> value) {
                    return value.f0;
                }
            }).reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Tuple2<String, Double>>() {
                @Override
                public Tuple2<String, Double> reduce(Tuple2<String, Double> value1, Tuple2<String, Double> value2) {
                    return null;
                }
            }).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(op);
            // get the original nodes
            SourcePlanNode sourceNode = resolver.getNode("source");
            SingleInputPlanNode reduceNode = resolver.getNode("reducer");
            SinkPlanNode sinkNode = resolver.getNode("sink");
            // get the combiner
            SingleInputPlanNode combineNode = ((SingleInputPlanNode) (reduceNode.getInput().getSource()));
            // get the key extractors and projectors
            SingleInputPlanNode keyExtractor = ((SingleInputPlanNode) (combineNode.getInput().getSource()));
            SingleInputPlanNode keyProjector = ((SingleInputPlanNode) (sinkNode.getInput().getSource()));
            // check wiring
            Assert.assertEquals(sourceNode, keyExtractor.getInput().getSource());
            Assert.assertEquals(keyProjector, sinkNode.getInput().getSource());
            // check the strategies
            Assert.assertEquals(SORTED_REDUCE, reduceNode.getDriverStrategy());
            Assert.assertEquals(SORTED_PARTIAL_REDUCE, combineNode.getDriverStrategy());
            // check the keys
            Assert.assertEquals(new FieldList(0), reduceNode.getKeys(0));
            Assert.assertEquals(new FieldList(0), combineNode.getKeys(0));
            Assert.assertEquals(new FieldList(0), reduceNode.getInput().getLocalStrategyKeys());
            // check parallelism
            Assert.assertEquals(6, sourceNode.getParallelism());
            Assert.assertEquals(6, keyExtractor.getParallelism());
            Assert.assertEquals(6, combineNode.getParallelism());
            Assert.assertEquals(8, reduceNode.getParallelism());
            Assert.assertEquals(8, keyProjector.getParallelism());
            Assert.assertEquals(8, sinkNode.getParallelism());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " in test: ") + (e.getMessage())));
        }
    }

    @Test
    public void testGroupedReduceWithHint() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            data.groupBy(new org.apache.flink.api.java.functions.KeySelector<Tuple2<String, Double>, String>() {
                public String getKey(Tuple2<String, Double> value) {
                    return value.f0;
                }
            }).reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Tuple2<String, Double>>() {
                @Override
                public Tuple2<String, Double> reduce(Tuple2<String, Double> value1, Tuple2<String, Double> value2) {
                    return null;
                }
            }).setCombineHint(HASH).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(op);
            // get the original nodes
            SourcePlanNode sourceNode = resolver.getNode("source");
            SingleInputPlanNode reduceNode = resolver.getNode("reducer");
            SinkPlanNode sinkNode = resolver.getNode("sink");
            // get the combiner
            SingleInputPlanNode combineNode = ((SingleInputPlanNode) (reduceNode.getInput().getSource()));
            // get the key extractors and projectors
            SingleInputPlanNode keyExtractor = ((SingleInputPlanNode) (combineNode.getInput().getSource()));
            SingleInputPlanNode keyProjector = ((SingleInputPlanNode) (sinkNode.getInput().getSource()));
            // check wiring
            Assert.assertEquals(sourceNode, keyExtractor.getInput().getSource());
            Assert.assertEquals(keyProjector, sinkNode.getInput().getSource());
            // check the strategies
            Assert.assertEquals(SORTED_REDUCE, reduceNode.getDriverStrategy());
            Assert.assertEquals(HASHED_PARTIAL_REDUCE, combineNode.getDriverStrategy());
            // check the keys
            Assert.assertEquals(new FieldList(0), reduceNode.getKeys(0));
            Assert.assertEquals(new FieldList(0), combineNode.getKeys(0));
            Assert.assertEquals(new FieldList(0), reduceNode.getInput().getLocalStrategyKeys());
            // check parallelism
            Assert.assertEquals(6, sourceNode.getParallelism());
            Assert.assertEquals(6, keyExtractor.getParallelism());
            Assert.assertEquals(6, combineNode.getParallelism());
            Assert.assertEquals(8, reduceNode.getParallelism());
            Assert.assertEquals(8, keyProjector.getParallelism());
            Assert.assertEquals(8, sinkNode.getParallelism());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " in test: ") + (e.getMessage())));
        }
    }

    /**
     * Test program compilation when the Reduce's combiner has been excluded
     * by setting {@code CombineHint.NONE}.
     */
    @Test
    public void testGroupedReduceWithoutCombiner() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(8);
        DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
        data.groupBy(0).reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> reduce(Tuple2<String, Double> value1, Tuple2<String, Double> value2) {
                return null;
            }
        }).setCombineHint(NONE).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
        Plan p = env.createProgramPlan();
        OptimizedPlan op = compileNoStats(p);
        CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(op);
        // get the original nodes
        SourcePlanNode sourceNode = resolver.getNode("source");
        SingleInputPlanNode reduceNode = resolver.getNode("reducer");
        SinkPlanNode sinkNode = resolver.getNode("sink");
        // check wiring
        Assert.assertEquals(sourceNode, reduceNode.getInput().getSource());
        // check the strategies
        Assert.assertEquals(SORTED_REDUCE, reduceNode.getDriverStrategy());
        // check the keys
        Assert.assertEquals(new FieldList(0), reduceNode.getKeys(0));
        Assert.assertEquals(new FieldList(0), reduceNode.getInput().getLocalStrategyKeys());
        // check parallelism
        Assert.assertEquals(6, sourceNode.getParallelism());
        Assert.assertEquals(8, reduceNode.getParallelism());
        Assert.assertEquals(8, sinkNode.getParallelism());
    }
}

