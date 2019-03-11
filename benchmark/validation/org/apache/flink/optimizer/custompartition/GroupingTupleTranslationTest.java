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
package org.apache.flink.optimizer.custompartition;


import Order.ASCENDING;
import Order.DESCENDING;
import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_CUSTOM;
import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.testfunctions.IdentityGroupReducerCombinable;
import org.apache.flink.optimizer.testfunctions.SelectOneReducer;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "serial", "unchecked" })
public class GroupingTupleTranslationTest extends CompilerTestBase {
    @Test
    public void testCustomPartitioningTupleAgg() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Integer, Integer>> data = env.fromElements(new Tuple2<Integer, Integer>(0, 0)).rebalance().setParallelism(4);
            data.groupBy(0).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerInt()).sum(1).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Integer, Integer>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode combiner = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(PARTITION_CUSTOM, reducer.getInput().getShipStrategy());
            Assert.assertEquals(FORWARD, combiner.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleReduce() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Integer, Integer>> data = env.fromElements(new Tuple2<Integer, Integer>(0, 0)).rebalance().setParallelism(4);
            data.groupBy(0).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerInt()).reduce(new SelectOneReducer<Tuple2<Integer, Integer>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Integer, Integer>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode combiner = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(PARTITION_CUSTOM, reducer.getInput().getShipStrategy());
            Assert.assertEquals(FORWARD, combiner.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleGroupReduce() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Integer, Integer>> data = env.fromElements(new Tuple2<Integer, Integer>(0, 0)).rebalance().setParallelism(4);
            data.groupBy(0).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerInt()).reduceGroup(new IdentityGroupReducerCombinable<Tuple2<Integer, Integer>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Integer, Integer>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode combiner = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(PARTITION_CUSTOM, reducer.getInput().getShipStrategy());
            Assert.assertEquals(FORWARD, combiner.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleGroupReduceSorted() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple3<Integer, Integer, Integer>> data = env.fromElements(new Tuple3<Integer, Integer, Integer>(0, 0, 0)).rebalance().setParallelism(4);
            data.groupBy(0).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerInt()).sortGroup(1, ASCENDING).reduceGroup(new IdentityGroupReducerCombinable<Tuple3<Integer, Integer, Integer>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode combiner = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(PARTITION_CUSTOM, reducer.getInput().getShipStrategy());
            Assert.assertEquals(FORWARD, combiner.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleGroupReduceSorted2() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple4<Integer, Integer, Integer, Integer>> data = env.fromElements(new Tuple4<Integer, Integer, Integer, Integer>(0, 0, 0, 0)).rebalance().setParallelism(4);
            data.groupBy(0).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerInt()).sortGroup(1, ASCENDING).sortGroup(2, DESCENDING).reduceGroup(new IdentityGroupReducerCombinable<Tuple4<Integer, Integer, Integer, Integer>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple4<Integer, Integer, Integer, Integer>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode combiner = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(PARTITION_CUSTOM, reducer.getInput().getShipStrategy());
            Assert.assertEquals(FORWARD, combiner.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleInvalidType() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Integer, Integer>> data = env.fromElements(new Tuple2<Integer, Integer>(0, 0)).rebalance().setParallelism(4);
            try {
                data.groupBy(0).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerLong());
                Assert.fail("Should throw an exception");
            } catch (InvalidProgramException e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleInvalidTypeSorted() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple3<Integer, Integer, Integer>> data = env.fromElements(new Tuple3<Integer, Integer, Integer>(0, 0, 0)).rebalance().setParallelism(4);
            try {
                data.groupBy(0).sortGroup(1, ASCENDING).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerLong());
                Assert.fail("Should throw an exception");
            } catch (InvalidProgramException e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCustomPartitioningTupleRejectCompositeKey() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple3<Integer, Integer, Integer>> data = env.fromElements(new Tuple3<Integer, Integer, Integer>(0, 0, 0)).rebalance().setParallelism(4);
            try {
                data.groupBy(0, 1).withPartitioner(new GroupingTupleTranslationTest.TestPartitionerInt());
                Assert.fail("Should throw an exception");
            } catch (InvalidProgramException e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------
    private static class TestPartitionerInt implements Partitioner<Integer> {
        @Override
        public int partition(Integer key, int numPartitions) {
            return 0;
        }
    }

    private static class TestPartitionerLong implements Partitioner<Long> {
        @Override
        public int partition(Long key, int numPartitions) {
            return 0;
        }
    }
}

