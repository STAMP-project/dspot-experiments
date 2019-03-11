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
package org.apache.flink.optimizer.operators;


import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_HASH;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


public class JoinWithDistributionTest extends CompilerTestBase {
    @Test
    public void JoinWithSameDistributionTest() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        TestDistribution dist1 = new TestDistribution(3);
        TestDistribution dist2 = new TestDistribution(3);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = org.apache.flink.api.java.utils.DataSetUtils.partitionByRange(set1, dist1, 0).join(org.apache.flink.api.java.utils.DataSetUtils.partitionByRange(set2, dist2, 0)).where(0).equalTo(0).with(new JoinWithDistributionTest.JoinFunc());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        Channel input1 = join.getInput1();
        Channel input2 = join.getInput2();
        Assert.assertEquals(FORWARD, input1.getShipStrategy());
        Assert.assertEquals(FORWARD, input2.getShipStrategy());
    }

    @Test
    public void JoinWithDifferentDistributionTest() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        TestDistribution dist1 = new TestDistribution(3);
        TestDistribution dist2 = new TestDistribution(4);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = org.apache.flink.api.java.utils.DataSetUtils.partitionByRange(set1, dist1, 0).join(org.apache.flink.api.java.utils.DataSetUtils.partitionByRange(set2, dist2, 0)).where(0).equalTo(0).with(new JoinWithDistributionTest.JoinFunc());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        Channel input1 = join.getInput1();
        Channel input2 = join.getInput2();
        Assert.assertEquals(PARTITION_HASH, input1.getShipStrategy());
        Assert.assertEquals(PARTITION_HASH, input2.getShipStrategy());
    }

    public static class JoinFunc implements JoinFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
        @Override
        public Tuple3<Integer, Integer, Integer> join(Tuple3<Integer, Integer, Integer> first, Tuple3<Integer, Integer, Integer> second) throws Exception {
            return null;
        }
    }
}

