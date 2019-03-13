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


import DriverStrategy.HYBRIDHASH_BUILD_FIRST;
import DriverStrategy.HYBRIDHASH_BUILD_FIRST_CACHED;
import DriverStrategy.HYBRIDHASH_BUILD_SECOND;
import DriverStrategy.HYBRIDHASH_BUILD_SECOND_CACHED;
import Optimizer.HINT_LOCAL_STRATEGY_HASH_BUILD_FIRST;
import Optimizer.HINT_LOCAL_STRATEGY_HASH_BUILD_SECOND;
import TempMode.CACHED;
import TempMode.NONE;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.functions.RichJoinFunction;
import org.apache.flink.api.common.operators.GenericDataSourceBase;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.optimizer.plan.DualInputPlanNode;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plantranslate.JobGraphGenerator;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that validate optimizer choice when using hash joins inside of iterations
 */
@SuppressWarnings("serial")
public class CachedMatchStrategyCompilerTest extends CompilerTestBase {
    /**
     * This tests whether a HYBRIDHASH_BUILD_SECOND is correctly transformed to a HYBRIDHASH_BUILD_SECOND_CACHED
     * when inside of an iteration an on the static path
     */
    @Test
    public void testRightSide() {
        try {
            Plan plan = getTestPlanRightStatic(HINT_LOCAL_STRATEGY_HASH_BUILD_SECOND);
            OptimizedPlan oPlan = compileNoStats(plan);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
            DualInputPlanNode innerJoin = resolver.getNode("DummyJoiner");
            // verify correct join strategy
            Assert.assertEquals(HYBRIDHASH_BUILD_SECOND_CACHED, innerJoin.getDriverStrategy());
            Assert.assertEquals(NONE, innerJoin.getInput1().getTempMode());
            Assert.assertEquals(NONE, innerJoin.getInput2().getTempMode());
            new JobGraphGenerator().compileJobGraph(oPlan);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test errored: " + (e.getMessage())));
        }
    }

    /**
     * This test makes sure that only a HYBRIDHASH on the static path is transformed to the cached variant
     */
    @Test
    public void testRightSideCountercheck() {
        try {
            Plan plan = getTestPlanRightStatic(HINT_LOCAL_STRATEGY_HASH_BUILD_FIRST);
            OptimizedPlan oPlan = compileNoStats(plan);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
            DualInputPlanNode innerJoin = resolver.getNode("DummyJoiner");
            // verify correct join strategy
            Assert.assertEquals(HYBRIDHASH_BUILD_FIRST, innerJoin.getDriverStrategy());
            Assert.assertEquals(NONE, innerJoin.getInput1().getTempMode());
            Assert.assertEquals(CACHED, innerJoin.getInput2().getTempMode());
            new JobGraphGenerator().compileJobGraph(oPlan);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test errored: " + (e.getMessage())));
        }
    }

    /**
     * This tests whether a HYBRIDHASH_BUILD_FIRST is correctly transformed to a HYBRIDHASH_BUILD_FIRST_CACHED
     * when inside of an iteration an on the static path
     */
    @Test
    public void testLeftSide() {
        try {
            Plan plan = getTestPlanLeftStatic(HINT_LOCAL_STRATEGY_HASH_BUILD_FIRST);
            OptimizedPlan oPlan = compileNoStats(plan);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
            DualInputPlanNode innerJoin = resolver.getNode("DummyJoiner");
            // verify correct join strategy
            Assert.assertEquals(HYBRIDHASH_BUILD_FIRST_CACHED, innerJoin.getDriverStrategy());
            Assert.assertEquals(NONE, innerJoin.getInput1().getTempMode());
            Assert.assertEquals(NONE, innerJoin.getInput2().getTempMode());
            new JobGraphGenerator().compileJobGraph(oPlan);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test errored: " + (e.getMessage())));
        }
    }

    /**
     * This test makes sure that only a HYBRIDHASH on the static path is transformed to the cached variant
     */
    @Test
    public void testLeftSideCountercheck() {
        try {
            Plan plan = getTestPlanLeftStatic(HINT_LOCAL_STRATEGY_HASH_BUILD_SECOND);
            OptimizedPlan oPlan = compileNoStats(plan);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
            DualInputPlanNode innerJoin = resolver.getNode("DummyJoiner");
            // verify correct join strategy
            Assert.assertEquals(HYBRIDHASH_BUILD_SECOND, innerJoin.getDriverStrategy());
            Assert.assertEquals(CACHED, innerJoin.getInput1().getTempMode());
            Assert.assertEquals(NONE, innerJoin.getInput2().getTempMode());
            new JobGraphGenerator().compileJobGraph(oPlan);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test errored: " + (e.getMessage())));
        }
    }

    /**
     * This test simulates a join of a big left side with a small right side inside of an iteration, where the small side is on a static path.
     * Currently the best execution plan is a HYBRIDHASH_BUILD_SECOND_CACHED, where the small side is hashed and cached.
     * This test also makes sure that all relevant plans are correctly enumerated by the optimizer.
     */
    @Test
    public void testCorrectChoosing() {
        try {
            Plan plan = getTestPlanRightStatic("");
            CompilerTestBase.SourceCollectorVisitor sourceCollector = new CompilerTestBase.SourceCollectorVisitor();
            plan.accept(sourceCollector);
            for (GenericDataSourceBase<?, ?> s : sourceCollector.getSources()) {
                if (s.getName().equals("bigFile")) {
                    this.setSourceStatistics(s, 10000000, 1000);
                } else
                    if (s.getName().equals("smallFile")) {
                        this.setSourceStatistics(s, 100, 100);
                    }

            }
            OptimizedPlan oPlan = compileNoStats(plan);
            CompilerTestBase.OptimizerPlanNodeResolver resolver = CompilerTestBase.getOptimizerPlanNodeResolver(oPlan);
            DualInputPlanNode innerJoin = resolver.getNode("DummyJoiner");
            // verify correct join strategy
            Assert.assertEquals(HYBRIDHASH_BUILD_SECOND_CACHED, innerJoin.getDriverStrategy());
            Assert.assertEquals(NONE, innerJoin.getInput1().getTempMode());
            Assert.assertEquals(NONE, innerJoin.getInput2().getTempMode());
            new JobGraphGenerator().compileJobGraph(oPlan);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test errored: " + (e.getMessage())));
        }
    }

    private static class DummyJoiner extends RichJoinFunction<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> {
        @Override
        public Tuple3<Long, Long, Long> join(Tuple3<Long, Long, Long> first, Tuple3<Long, Long, Long> second) throws Exception {
            return first;
        }
    }
}

