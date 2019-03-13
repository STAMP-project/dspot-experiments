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


import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_CUSTOM;
import ShipStrategyType.PARTITION_FORCED_REBALANCE;
import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.testfunctions.IdentityPartitionerMapper;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "serial", "unchecked" })
public class CustomPartitioningTest extends CompilerTestBase {
    @Test
    public void testPartitionTuples() {
        try {
            final Partitioner<Integer> part = new CustomPartitioningTest.TestPartitionerInt();
            final int parallelism = 4;
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            DataSet<Tuple2<Integer, Integer>> data = env.fromElements(new Tuple2<Integer, Integer>(0, 0)).rebalance();
            data.partitionCustom(part, 0).mapPartition(new IdentityPartitionerMapper<Tuple2<Integer, Integer>>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Integer, Integer>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode mapper = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode partitioner = ((SingleInputPlanNode) (mapper.getInput().getSource()));
            SingleInputPlanNode balancer = ((SingleInputPlanNode) (partitioner.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, sink.getParallelism());
            Assert.assertEquals(FORWARD, mapper.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, mapper.getParallelism());
            Assert.assertEquals(PARTITION_CUSTOM, partitioner.getInput().getShipStrategy());
            Assert.assertEquals(part, partitioner.getInput().getPartitioner());
            Assert.assertEquals(parallelism, partitioner.getParallelism());
            Assert.assertEquals(PARTITION_FORCED_REBALANCE, balancer.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, balancer.getParallelism());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartitionTuplesInvalidType() {
        try {
            final int parallelism = 4;
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            DataSet<Tuple2<Integer, Integer>> data = env.fromElements(new Tuple2<Integer, Integer>(0, 0)).rebalance();
            try {
                data.partitionCustom(new CustomPartitioningTest.TestPartitionerLong(), 0);
                Assert.fail("Should throw an exception");
            } catch (InvalidProgramException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartitionPojo() {
        try {
            final Partitioner<Integer> part = new CustomPartitioningTest.TestPartitionerInt();
            final int parallelism = 4;
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            DataSet<CustomPartitioningTest.Pojo> data = env.fromElements(new CustomPartitioningTest.Pojo()).rebalance();
            data.partitionCustom(part, "a").mapPartition(new IdentityPartitionerMapper<CustomPartitioningTest.Pojo>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<CustomPartitioningTest.Pojo>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode mapper = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode partitioner = ((SingleInputPlanNode) (mapper.getInput().getSource()));
            SingleInputPlanNode balancer = ((SingleInputPlanNode) (partitioner.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, sink.getParallelism());
            Assert.assertEquals(FORWARD, mapper.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, mapper.getParallelism());
            Assert.assertEquals(PARTITION_CUSTOM, partitioner.getInput().getShipStrategy());
            Assert.assertEquals(part, partitioner.getInput().getPartitioner());
            Assert.assertEquals(parallelism, partitioner.getParallelism());
            Assert.assertEquals(PARTITION_FORCED_REBALANCE, balancer.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, balancer.getParallelism());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartitionPojoInvalidType() {
        try {
            final int parallelism = 4;
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            DataSet<CustomPartitioningTest.Pojo> data = env.fromElements(new CustomPartitioningTest.Pojo()).rebalance();
            try {
                data.partitionCustom(new CustomPartitioningTest.TestPartitionerLong(), "a");
                Assert.fail("Should throw an exception");
            } catch (InvalidProgramException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartitionKeySelector() {
        try {
            final Partitioner<Integer> part = new CustomPartitioningTest.TestPartitionerInt();
            final int parallelism = 4;
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            DataSet<CustomPartitioningTest.Pojo> data = env.fromElements(new CustomPartitioningTest.Pojo()).rebalance();
            data.partitionCustom(part, new CustomPartitioningTest.TestKeySelectorInt<CustomPartitioningTest.Pojo>()).mapPartition(new IdentityPartitionerMapper<CustomPartitioningTest.Pojo>()).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<CustomPartitioningTest.Pojo>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode mapper = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode keyRemover = ((SingleInputPlanNode) (mapper.getInput().getSource()));
            SingleInputPlanNode partitioner = ((SingleInputPlanNode) (keyRemover.getInput().getSource()));
            SingleInputPlanNode keyExtractor = ((SingleInputPlanNode) (partitioner.getInput().getSource()));
            SingleInputPlanNode balancer = ((SingleInputPlanNode) (keyExtractor.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, sink.getParallelism());
            Assert.assertEquals(FORWARD, mapper.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, mapper.getParallelism());
            Assert.assertEquals(FORWARD, keyRemover.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, keyRemover.getParallelism());
            Assert.assertEquals(PARTITION_CUSTOM, partitioner.getInput().getShipStrategy());
            Assert.assertEquals(part, partitioner.getInput().getPartitioner());
            Assert.assertEquals(parallelism, partitioner.getParallelism());
            Assert.assertEquals(FORWARD, keyExtractor.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, keyExtractor.getParallelism());
            Assert.assertEquals(PARTITION_FORCED_REBALANCE, balancer.getInput().getShipStrategy());
            Assert.assertEquals(parallelism, balancer.getParallelism());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartitionKeySelectorInvalidType() {
        try {
            final Partitioner<Integer> part = ((Partitioner<Integer>) ((Partitioner<?>) (new CustomPartitioningTest.TestPartitionerLong())));
            final int parallelism = 4;
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            DataSet<CustomPartitioningTest.Pojo> data = env.fromElements(new CustomPartitioningTest.Pojo()).rebalance();
            try {
                data.partitionCustom(part, new CustomPartitioningTest.TestKeySelectorInt<CustomPartitioningTest.Pojo>());
                Assert.fail("Should throw an exception");
            } catch (InvalidProgramException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------
    public static class Pojo {
        public int a;

        public int b;
    }

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

    private static class TestKeySelectorInt<T> implements KeySelector<T, Integer> {
        @Override
        public Integer getKey(T value) {
            return null;
        }
    }
}

