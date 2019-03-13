/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.partitionsender;


import Metric.BYTES_SENT;
import OptionValue.AccessibleScopes.SESSION;
import SelectionVectorMode.FOUR_BYTE;
import TopNBatch.SimpleSV4RecordBatch;
import UserBitShared.UserCredentials;
import UserSession.Builder;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.exec.exception.OutOfMemoryException;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.ops.FragmentContextImpl;
import org.apache.drill.exec.ops.OperatorStats;
import org.apache.drill.exec.physical.PhysicalPlan;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.config.HashPartitionSender;
import org.apache.drill.exec.physical.config.HashToRandomExchange;
import org.apache.drill.exec.physical.impl.TopN.TopNBatch;
import org.apache.drill.exec.physical.impl.partitionsender.PartitionerDecorator.GeneralExecuteIface;
import org.apache.drill.exec.planner.PhysicalPlanReader;
import org.apache.drill.exec.planner.fragment.Fragment;
import org.apache.drill.exec.planner.fragment.PlanningSet;
import org.apache.drill.exec.planner.fragment.SimpleParallelizer;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.selection.SelectionVector4;
import org.apache.drill.exec.rpc.user.UserSession;
import org.apache.drill.exec.server.DrillbitContext;
import org.apache.drill.exec.server.options.OptionList;
import org.apache.drill.exec.server.options.OptionValue;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * PartitionerSenderRootExec test to cover mostly part that deals with multithreaded
 * ability to copy and flush data
 */
@Category(OperatorTest.class)
public class TestPartitionSender extends PlanTestBase {
    private static final SimpleParallelizer PARALLELIZER = /* parallelizationThreshold (slice_count) */
    /* maxWidthPerNode */
    /* maxGlobalWidth */
    /* affinityFactor */
    new SimpleParallelizer(1, 6, 1000, 1.2);

    private static final UserSession USER_SESSION = Builder.newBuilder().withCredentials(UserCredentials.newBuilder().setUserName("foo").build()).build();

    private static final int NUM_DEPTS = 40;

    private static final int NUM_EMPLOYEES = 1000;

    private static final int DRILLBITS_COUNT = 3;

    private static final String TABLE = "table";

    private static String groupByQuery;

    /**
     * Main test to go over different scenarios
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartitionSenderCostToThreads() throws Exception {
        final VectorContainer container = new VectorContainer();
        container.buildSchema(FOUR_BYTE);
        final SelectionVector4 sv = Mockito.mock(SelectionVector4.class, "SelectionVector4");
        Mockito.when(sv.getCount()).thenReturn(100);
        Mockito.when(sv.getTotalCount()).thenReturn(100);
        for (int i = 0; i < 100; i++) {
            Mockito.when(sv.get(i)).thenReturn(i);
        }
        final TopNBatch.SimpleSV4RecordBatch incoming = new TopNBatch.SimpleSV4RecordBatch(container, sv, null);
        BaseTestQuery.updateTestCluster(TestPartitionSender.DRILLBITS_COUNT, null);
        BaseTestQuery.test("ALTER SESSION SET `planner.slice_target`=1");
        String plan = PlanTestBase.getPlanInString(("EXPLAIN PLAN FOR " + (TestPartitionSender.groupByQuery)), PlanTestBase.JSON_FORMAT);
        final DrillbitContext drillbitContext = BaseTestQuery.getDrillbitContext();
        final PhysicalPlanReader planReader = drillbitContext.getPlanReader();
        final PhysicalPlan physicalPlan = planReader.readPhysicalPlan(plan);
        final Fragment rootFragment = PopUnitTestBase.getRootFragmentFromPlanString(planReader, plan);
        final PlanningSet planningSet = new PlanningSet();
        final FunctionImplementationRegistry registry = new FunctionImplementationRegistry(BaseTestQuery.config);
        // Create a planningSet to get the assignment of major fragment ids to fragments.
        TestPartitionSender.PARALLELIZER.initFragmentWrappers(rootFragment, planningSet);
        final List<PhysicalOperator> operators = physicalPlan.getSortedOperators(false);
        // get HashToRandomExchange physical operator
        HashToRandomExchange hashToRandomExchange = null;
        for (PhysicalOperator operator : operators) {
            if (operator instanceof HashToRandomExchange) {
                hashToRandomExchange = ((HashToRandomExchange) (operator));
                break;
            }
        }
        final OptionList options = new OptionList();
        // try multiple scenarios with different set of options
        options.add(OptionValue.create(SESSION, "planner.slice_target", 1, OptionScope.SESSION));
        testThreadsHelper(hashToRandomExchange, drillbitContext, options, incoming, registry, planReader, planningSet, rootFragment, 1);
        options.clear();
        options.add(OptionValue.create(AccessibleScopes.SESSION, "planner.slice_target", 1, OptionScope.SESSION));
        options.add(OptionValue.create(SESSION, "planner.partitioner_sender_max_threads", 10, OptionScope.SESSION));
        hashToRandomExchange.setCost(1000);
        testThreadsHelper(hashToRandomExchange, drillbitContext, options, incoming, registry, planReader, planningSet, rootFragment, 10);
        options.clear();
        options.add(OptionValue.create(AccessibleScopes.SESSION, "planner.slice_target", 1000, OptionScope.SESSION));
        options.add(OptionValue.create(AccessibleScopes.SESSION, "planner.partitioner_sender_threads_factor", 2, OptionScope.SESSION));
        hashToRandomExchange.setCost(14000);
        testThreadsHelper(hashToRandomExchange, drillbitContext, options, incoming, registry, planReader, planningSet, rootFragment, 2);
    }

    /**
     * Testing partitioners distribution algorithm
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAlgorithm() throws Exception {
        int outGoingBatchCount;
        int numberPartitions;
        int k = 0;
        final Random rand = new Random();
        while (k < 1000) {
            outGoingBatchCount = (rand.nextInt(1000)) + 1;
            numberPartitions = (rand.nextInt(32)) + 1;
            final int actualPartitions = (outGoingBatchCount > numberPartitions) ? numberPartitions : outGoingBatchCount;
            final int divisor = Math.max(1, (outGoingBatchCount / actualPartitions));
            final int longTail = outGoingBatchCount % actualPartitions;
            int startIndex = 0;
            int endIndex = 0;
            for (int i = 0; i < actualPartitions; i++) {
                startIndex = endIndex;
                endIndex = startIndex + divisor;
                if (i < longTail) {
                    endIndex++;
                }
            }
            Assert.assertTrue("endIndex can not be > outGoingBatchCount", (endIndex == outGoingBatchCount));
            k++;
        } 
    }

    /**
     * Helper class to expose some functionality of PartitionSenderRootExec
     */
    private static class MockPartitionSenderRootExec extends PartitionSenderRootExec {
        public MockPartitionSenderRootExec(FragmentContextImpl context, RecordBatch incoming, HashPartitionSender operator) throws OutOfMemoryException {
            super(context, incoming, operator);
        }

        @Override
        public void close() {
            // Don't close the context here; it is closed
            // separately. Close only resources this sender
            // controls.
            // ((AutoCloseable) oContext).close();
        }

        public int getNumberPartitions() {
            return numberPartitions;
        }

        public OperatorStats getStats() {
            return this.stats;
        }
    }

    /**
     * Helper class to inject exceptions in the threads
     */
    private static class InjectExceptionTest implements GeneralExecuteIface {
        @Override
        public void execute(Partitioner partitioner) throws IOException {
            // throws IOException
            partitioner.getStats().addLongStat(BYTES_SENT, 5);
            throw new IOException("Test exception handling");
        }
    }
}

