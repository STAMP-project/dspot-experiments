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
package org.apache.drill.exec.physical.impl.join;


import Charsets.UTF_8;
import java.util.List;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.client.DrillClient;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.ops.FragmentContextImpl;
import org.apache.drill.exec.physical.PhysicalPlan;
import org.apache.drill.exec.physical.base.FragmentRoot;
import org.apache.drill.exec.physical.impl.ImplCreator;
import org.apache.drill.exec.physical.impl.SimpleRootExec;
import org.apache.drill.exec.physical.impl.aggregate.HashAggBatch;
import org.apache.drill.exec.planner.PhysicalPlanReader;
import org.apache.drill.exec.planner.PhysicalPlanReaderTestFactory;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.proto.BitControl.PlanFragment;
import org.apache.drill.exec.proto.UserBitShared.QueryType.PHYSICAL;
import org.apache.drill.exec.rpc.UserClientConnection;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.server.Drillbit;
import org.apache.drill.exec.server.DrillbitContext;
import org.apache.drill.exec.server.RemoteServiceSet;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SlowTest.class, OperatorTest.class })
public class TestMergeJoin extends PopUnitTestBase {
    private static final Logger logger = LoggerFactory.getLogger(HashAggBatch.class);

    private final DrillConfig c = DrillConfig.create();

    @Test
    public void testJoinBatchSize() throws Throwable {
        final DrillbitContext bitContext = mockDrillbitContext();
        final UserClientConnection connection = Mockito.mock(UserClientConnection.class);
        final PhysicalPlanReader reader = PhysicalPlanReaderTestFactory.defaultPhysicalPlanReader(c);
        final PhysicalPlan plan = reader.readPhysicalPlan(Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/join_batchsize.json"), UTF_8).read());
        final FunctionImplementationRegistry registry = new FunctionImplementationRegistry(c);
        final FragmentContextImpl context = new FragmentContextImpl(bitContext, PlanFragment.getDefaultInstance(), connection, registry);
        final SimpleRootExec exec = new SimpleRootExec(ImplCreator.getExec(context, ((FragmentRoot) (plan.getSortedOperators(false).iterator().next()))));
        exec.next();// skip schema batch

        while (exec.next()) {
            Assert.assertEquals(100, exec.getRecordCount());
        } 
        if ((context.getExecutorState().getFailureCause()) != null) {
            throw context.getExecutorState().getFailureCause();
        }
        Assert.assertTrue((!(context.getExecutorState().isFailed())));
    }

    @Test
    public void testMergeJoinInnerEmptyBatch() throws Exception {
        final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();
        try (final Drillbit bit1 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            bit1.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/merge_join_empty_batch.json"), UTF_8).read().replace("${JOIN_TYPE}", "INNER"));
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(0, count);
        }
    }

    @Test
    public void testMergeJoinLeftEmptyBatch() throws Exception {
        final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();
        try (final Drillbit bit1 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            bit1.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/merge_join_empty_batch.json"), UTF_8).read().replace("${JOIN_TYPE}", "LEFT"));
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(50, count);
        }
    }

    @Test
    public void testMergeJoinRightEmptyBatch() throws Exception {
        final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();
        try (final Drillbit bit1 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            bit1.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/merge_join_empty_batch.json"), UTF_8).read().replace("${JOIN_TYPE}", "RIGHT"));
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(0, count);
        }
    }

    @Test
    public void testMergeJoinExprInCondition() throws Exception {
        final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();
        try (final Drillbit bit1 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            bit1.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/mergeJoinExpr.json"), UTF_8).read());
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(10, count);
        }
    }
}

