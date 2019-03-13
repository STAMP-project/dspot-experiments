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


import JoinOperatorBase.JoinHint.REPARTITION_HASH_FIRST;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.optimizer.plan.DualInputPlanNode;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.apache.flink.util.Collector;
import org.junit.Test;


@SuppressWarnings("serial")
public class PartitioningReusageTest extends CompilerTestBase {
    @Test
    public void noPreviousPartitioningJoin1() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.join(set2, REPARTITION_HASH_FIRST).where(0, 1).equalTo(0, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void noPreviousPartitioningJoin2() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.join(set2, REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseSinglePartitioningJoin1() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").join(set2, REPARTITION_HASH_FIRST).where(0, 1).equalTo(0, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseSinglePartitioningJoin2() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").join(set2, REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseSinglePartitioningJoin3() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.join(set2.partitionByHash(2, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2;1"), REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseSinglePartitioningJoin4() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0").join(set2, REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseSinglePartitioningJoin5() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.join(set2.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2"), REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin1() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").join(set2.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1"), REPARTITION_HASH_FIRST).where(0, 1).equalTo(0, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin2() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").join(set2.partitionByHash(1, 2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1;2"), REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin3() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0").join(set2.partitionByHash(2, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2;1"), REPARTITION_HASH_FIRST).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin4() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0, 2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;2").join(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1"), REPARTITION_HASH_FIRST).where(0, 2).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin5() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2").join(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1"), REPARTITION_HASH_FIRST).where(0, 2).equalTo(2, 1).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin6() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(0).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0").join(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1"), REPARTITION_HASH_FIRST).where(0, 2).equalTo(1, 2).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void reuseBothPartitioningJoin7() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> joined = set1.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2").join(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1"), REPARTITION_HASH_FIRST).where(0, 2).equalTo(1, 2).with(new PartitioningReusageTest.MockJoin());
        joined.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode join = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidJoinInputProperties(join);
    }

    @Test
    public void noPreviousPartitioningCoGroup1() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.coGroup(set2).where(0, 1).equalTo(0, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void noPreviousPartitioningCoGroup2() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.coGroup(set2).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseSinglePartitioningCoGroup1() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").coGroup(set2).where(0, 1).equalTo(0, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseSinglePartitioningCoGroup2() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").coGroup(set2).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseSinglePartitioningCoGroup3() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.coGroup(set2.partitionByHash(2, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2;1")).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseSinglePartitioningCoGroup4() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0").coGroup(set2).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseSinglePartitioningCoGroup5() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.coGroup(set2.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2")).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup1() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").coGroup(set2.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1")).where(0, 1).equalTo(0, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup2() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;1").coGroup(set2.partitionByHash(1, 2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1;2")).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup3() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0").coGroup(set2.partitionByHash(2, 1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2;1")).where(0, 1).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup4() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(0, 2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("0;2").coGroup(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1")).where(0, 2).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup5() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2").coGroup(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1")).where(0, 2).equalTo(2, 1).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup6() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2").coGroup(set2.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2")).where(0, 2).equalTo(1, 2).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    @Test
    public void reuseBothPartitioningCoGroup7() {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, Integer, Integer>> set1 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> set2 = env.readCsvFile(CompilerTestBase.IN_FILE).types(Integer.class, Integer.class, Integer.class);
        DataSet<Tuple3<Integer, Integer, Integer>> coGrouped = set1.partitionByHash(2).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("2").coGroup(set2.partitionByHash(1).map(new PartitioningReusageTest.MockMapper()).withForwardedFields("1")).where(0, 2).equalTo(1, 2).with(new PartitioningReusageTest.MockCoGroup());
        coGrouped.output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple3<Integer, Integer, Integer>>());
        Plan plan = env.createProgramPlan();
        OptimizedPlan oPlan = compileWithStats(plan);
        SinkPlanNode sink = oPlan.getDataSinks().iterator().next();
        DualInputPlanNode coGroup = ((DualInputPlanNode) (sink.getInput().getSource()));
        checkValidCoGroupInputProperties(coGroup);
    }

    public static class MockMapper implements MapFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
        @Override
        public Tuple3<Integer, Integer, Integer> map(Tuple3<Integer, Integer, Integer> value) throws Exception {
            return null;
        }
    }

    public static class MockJoin implements JoinFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
        @Override
        public Tuple3<Integer, Integer, Integer> join(Tuple3<Integer, Integer, Integer> first, Tuple3<Integer, Integer, Integer> second) throws Exception {
            return null;
        }
    }

    public static class MockCoGroup implements CoGroupFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
        @Override
        public void coGroup(Iterable<Tuple3<Integer, Integer, Integer>> first, Iterable<Tuple3<Integer, Integer, Integer>> second, Collector<Tuple3<Integer, Integer, Integer>> out) throws Exception {
        }
    }
}

