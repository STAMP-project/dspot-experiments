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


import CombineHint.HASH;
import DriverStrategy.HASHED_PARTIAL_REDUCE;
import DriverStrategy.SORTED_PARTIAL_REDUCE;
import DriverStrategy.SORTED_REDUCE;
import java.io.Serializable;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.operators.util.FieldList;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DistinctOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.plan.SourcePlanNode;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class DistinctCompilationTest extends CompilerTestBase implements Serializable {
    @Test
    public void testDistinctPlain() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            data.distinct().name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
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
            Assert.assertEquals(SORTED_REDUCE, reduceNode.getDriverStrategy());
            Assert.assertEquals(SORTED_PARTIAL_REDUCE, combineNode.getDriverStrategy());
            // check the keys
            Assert.assertEquals(new FieldList(0, 1), reduceNode.getKeys(0));
            Assert.assertEquals(new FieldList(0, 1), combineNode.getKeys(0));
            Assert.assertEquals(new FieldList(0, 1), reduceNode.getInput().getLocalStrategyKeys());
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
    public void testDistinctWithCombineHint() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            data.distinct().setCombineHint(HASH).name("reducer").output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
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
            Assert.assertEquals(SORTED_REDUCE, reduceNode.getDriverStrategy());
            Assert.assertEquals(HASHED_PARTIAL_REDUCE, combineNode.getDriverStrategy());
            // check the keys
            Assert.assertEquals(new FieldList(0, 1), reduceNode.getKeys(0));
            Assert.assertEquals(new FieldList(0, 1), combineNode.getKeys(0));
            Assert.assertEquals(new FieldList(0, 1), reduceNode.getInput().getLocalStrategyKeys());
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
    public void testDistinctWithSelectorFunctionKey() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            data.distinct(new org.apache.flink.api.java.functions.KeySelector<Tuple2<String, Double>, String>() {
                public String getKey(Tuple2<String, Double> value) {
                    return value.f0;
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
            // check that both reduce and combiner have the same strategy
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
    public void testDistinctWithFieldPositionKeyCombinable() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(8);
            DataSet<Tuple2<String, Double>> data = env.readCsvFile("file:///will/never/be/read").types(String.class, Double.class).name("source").setParallelism(6);
            DistinctOperator<Tuple2<String, Double>> reduced = data.distinct(1).name("reducer");
            reduced.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<String, Double>>()).name("sink");
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
}

