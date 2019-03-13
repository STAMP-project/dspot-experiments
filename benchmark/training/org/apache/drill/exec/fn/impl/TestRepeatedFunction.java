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
package org.apache.drill.exec.fn.impl;


import Charsets.UTF_8;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.expression.ExpressionPosition;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.ops.FragmentContextImpl;
import org.apache.drill.exec.physical.PhysicalPlan;
import org.apache.drill.exec.physical.base.FragmentRoot;
import org.apache.drill.exec.physical.impl.ImplCreator;
import org.apache.drill.exec.physical.impl.SimpleRootExec;
import org.apache.drill.exec.planner.PhysicalPlanReader;
import org.apache.drill.exec.planner.PhysicalPlanReaderTestFactory;
import org.apache.drill.exec.proto.BitControl.PlanFragment;
import org.apache.drill.exec.rpc.UserClientConnection;
import org.apache.drill.exec.server.DrillbitContext;
import org.apache.drill.exec.vector.BitVector;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(OperatorTest.class)
public class TestRepeatedFunction extends ExecTest {
    private final DrillConfig c = DrillConfig.create();

    @Test
    public void testRepeated() throws Throwable {
        final DrillbitContext bitContext = mockDrillbitContext();
        final UserClientConnection connection = Mockito.mock(UserClientConnection.class);
        final PhysicalPlanReader reader = PhysicalPlanReaderTestFactory.defaultPhysicalPlanReader(c);
        final PhysicalPlan plan = reader.readPhysicalPlan(Files.asCharSource(DrillFileUtils.getResourceAsFile("/physical_repeated_1.json"), UTF_8).read());
        final FunctionImplementationRegistry registry = new FunctionImplementationRegistry(c);
        final FragmentContextImpl context = new FragmentContextImpl(bitContext, PlanFragment.getDefaultInstance(), connection, registry);
        final SimpleRootExec exec = new SimpleRootExec(ImplCreator.getExec(context, ((FragmentRoot) (plan.getSortedOperators(false).iterator().next()))));
        boolean oneIsOne = false;
        int size = 0;
        final int[] sizes = new int[]{ 1, 2, 0, 6 };
        while (exec.next()) {
            final IntVector c1 = exec.getValueVectorById(new org.apache.drill.common.expression.SchemaPath("cnt", ExpressionPosition.UNKNOWN), IntVector.class);
            final BitVector c2 = exec.getValueVectorById(new org.apache.drill.common.expression.SchemaPath("has_min", ExpressionPosition.UNKNOWN), BitVector.class);
            for (int i = 0; i < (exec.getRecordCount()); i++) {
                final int curSize = sizes[(size % (sizes.length))];
                Assert.assertEquals(curSize, c1.getAccessor().get(i));
                switch (curSize) {
                    case 1 :
                        Assert.assertEquals(oneIsOne, (1 == (c2.getAccessor().get(i))));
                        oneIsOne = !oneIsOne;
                        break;
                    case 2 :
                        Assert.assertEquals(1, c2.getAccessor().get(i));
                        break;
                    case 0 :
                        Assert.assertEquals(0, c2.getAccessor().get(i));
                        break;
                    case 6 :
                        Assert.assertEquals(1, c2.getAccessor().get(i));
                        break;
                }
                size++;
            }
        } 
        if ((context.getExecutorState().getFailureCause()) != null) {
            throw context.getExecutorState().getFailureCause();
        }
        Assert.assertTrue((!(context.getExecutorState().isFailed())));
    }
}

