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
package org.apache.drill.exec.record;


import BitControl.PlanFragment;
import Charsets.UTF_8;
import UserBitShared.CoreOperatorType;
import java.util.List;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.ops.FragmentContextImpl;
import org.apache.drill.exec.ops.OpProfileDef;
import org.apache.drill.exec.ops.OperatorStats;
import org.apache.drill.exec.ops.OperatorUtilities;
import org.apache.drill.exec.physical.PhysicalPlan;
import org.apache.drill.exec.physical.base.FragmentRoot;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.impl.ImplCreator;
import org.apache.drill.exec.physical.impl.SimpleRootExec;
import org.apache.drill.exec.planner.PhysicalPlanReader;
import org.apache.drill.exec.planner.PhysicalPlanReaderTestFactory;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.rpc.UserClientConnection;
import org.apache.drill.exec.server.DrillbitContext;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(VectorTest.class)
public class TestRecordIterator extends PopUnitTestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestRecordIterator.class);

    DrillConfig c = DrillConfig.create();

    @Test
    public void testSimpleIterator() throws Throwable {
        final DrillbitContext bitContext = mockDrillbitContext();
        final UserClientConnection connection = Mockito.mock(UserClientConnection.class);
        final PhysicalPlanReader reader = PhysicalPlanReaderTestFactory.defaultPhysicalPlanReader(c);
        final String planStr = Files.asCharSource(DrillFileUtils.getResourceAsFile("/record/test_recorditerator.json"), UTF_8).read();
        final PhysicalPlan plan = reader.readPhysicalPlan(planStr);
        final FunctionImplementationRegistry registry = new FunctionImplementationRegistry(c);
        final FragmentContextImpl context = new FragmentContextImpl(bitContext, PlanFragment.getDefaultInstance(), connection, registry);
        final List<PhysicalOperator> operatorList = plan.getSortedOperators(false);
        SimpleRootExec exec = new SimpleRootExec(ImplCreator.getExec(context, ((FragmentRoot) (operatorList.iterator().next()))));
        RecordBatch singleBatch = exec.getIncoming();
        PhysicalOperator dummyPop = operatorList.iterator().next();
        OpProfileDef def = new OpProfileDef(dummyPop.getOperatorId(), CoreOperatorType.MOCK_SUB_SCAN_VALUE, OperatorUtilities.getChildCount(dummyPop));
        OperatorStats stats = exec.getContext().getStats().newOperatorStats(def, exec.getContext().getAllocator());
        RecordIterator iter = new RecordIterator(singleBatch, null, exec.getContext().newOperatorContext(dummyPop, stats), 0, false, null);
        int totalRecords = 0;
        List<ValueVector> vectors = null;
        while (true) {
            iter.next();
            if (iter.finished()) {
                break;
            } else {
                // First time save vectors.
                if (vectors == null) {
                    vectors = Lists.newArrayList();
                    for (VectorWrapper vw : iter) {
                        vectors.add(vw.getValueVector());
                    }
                }
                final int position = iter.getCurrentPosition();
                if ((position % 2) == 0) {
                    Assert.assertTrue(TestRecordIterator.checkValues(vectors, position));
                } else {
                    Assert.assertTrue(TestRecordIterator.checkValues(vectors, position));
                }
                totalRecords++;
            }
            Assert.assertEquals(0, iter.cachedBatches().size());
        } 
        Assert.assertEquals(11112, totalRecords);
        try {
            iter.mark();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            iter.reset();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void testMarkResetIterator() throws Throwable {
        final DrillbitContext bitContext = mockDrillbitContext();
        final UserClientConnection connection = Mockito.mock(UserClientConnection.class);
        final PhysicalPlanReader reader = PhysicalPlanReaderTestFactory.defaultPhysicalPlanReader(c);
        final String planStr = Files.asCharSource(DrillFileUtils.getResourceAsFile("/record/test_recorditerator.json"), UTF_8).read();
        final PhysicalPlan plan = reader.readPhysicalPlan(planStr);
        final FunctionImplementationRegistry registry = new FunctionImplementationRegistry(c);
        final FragmentContextImpl context = new FragmentContextImpl(bitContext, PlanFragment.getDefaultInstance(), connection, registry);
        final List<PhysicalOperator> operatorList = plan.getSortedOperators(false);
        SimpleRootExec exec = new SimpleRootExec(ImplCreator.getExec(context, ((FragmentRoot) (operatorList.iterator().next()))));
        RecordBatch singleBatch = exec.getIncoming();
        PhysicalOperator dummyPop = operatorList.iterator().next();
        OpProfileDef def = new OpProfileDef(dummyPop.getOperatorId(), CoreOperatorType.MOCK_SUB_SCAN_VALUE, OperatorUtilities.getChildCount(dummyPop));
        OperatorStats stats = exec.getContext().getStats().newOperatorStats(def, exec.getContext().getAllocator());
        RecordIterator iter = new RecordIterator(singleBatch, null, exec.getContext().newOperatorContext(dummyPop, stats), 0, null);
        List<ValueVector> vectors;
        // batche sizes
        // 1, 100, 10, 10000, 1, 1000
        // total = 11112
        // BATCH 1 : 1, starting outerposition: 0
        iter.next();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(1, iter.getTotalRecordCount());
        Assert.assertEquals(0, iter.getCurrentPosition());
        Assert.assertEquals(0, iter.getOuterPosition());
        Assert.assertEquals(1, iter.cachedBatches().size());
        vectors = Lists.newArrayList();
        for (VectorWrapper vw : iter) {
            vectors.add(vw.getValueVector());
        }
        // mark at position 0
        iter.mark();
        TestRecordIterator.checkValues(vectors, 0);
        // BATCH 2: 100, starting outerposition: 1
        iter.next();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(101, iter.getTotalRecordCount(), 101);
        Assert.assertEquals(0, iter.getCurrentPosition());
        Assert.assertEquals(100, iter.getInnerRecordCount());
        Assert.assertEquals(1, iter.getOuterPosition());
        Assert.assertEquals(2, iter.cachedBatches().size());
        for (int i = 0; i < 100; i++) {
            TestRecordIterator.checkValues(vectors, i);
            iter.next();
        }
        // BATCH 3 :10, starting outerposition: 101
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(111, iter.getTotalRecordCount());
        Assert.assertEquals(0, iter.getCurrentPosition());
        Assert.assertEquals(10, iter.getInnerRecordCount());
        Assert.assertEquals(101, iter.getOuterPosition());
        Assert.assertEquals(3, iter.cachedBatches().size());
        for (int i = 0; i < 10; i++) {
            TestRecordIterator.checkValues(vectors, i);
            iter.next();
        }
        // BATCH 4 : 10000, starting outerposition: 111
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(10111, iter.getTotalRecordCount());
        Assert.assertEquals(0, iter.getCurrentPosition(), 0);
        Assert.assertEquals(10000, iter.getInnerRecordCount());
        Assert.assertEquals(111, iter.getOuterPosition());
        Assert.assertEquals(4, iter.cachedBatches().size());
        for (int i = 0; i < 10000; i++) {
            TestRecordIterator.checkValues(vectors, i);
            iter.next();
        }
        // BATCH 5 : 1, starting outerposition: 10111
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(10112, iter.getTotalRecordCount());
        Assert.assertEquals(0, iter.getCurrentPosition());
        Assert.assertEquals(1, iter.getInnerRecordCount());
        Assert.assertEquals(10111, iter.getOuterPosition());
        Assert.assertEquals(5, iter.cachedBatches().size());
        TestRecordIterator.checkValues(vectors, 0);
        iter.next();
        // BATCH 6 : 1000, starting outerposition: 10112
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(11112, iter.getTotalRecordCount());
        Assert.assertEquals(0, iter.getCurrentPosition());
        Assert.assertEquals(1000, iter.getInnerRecordCount());
        Assert.assertEquals(10112, iter.getOuterPosition());
        Assert.assertEquals(6, iter.cachedBatches().size());
        for (int i = 0; i < 1000; i++) {
            TestRecordIterator.checkValues(vectors, i);
            iter.next();
        }
        Assert.assertTrue(iter.finished());
        Assert.assertEquals(6, iter.cachedBatches().size());
        // back to batch 1
        iter.reset();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(6, iter.cachedBatches().size());
        Assert.assertEquals(iter.getCurrentPosition(), 0);
        Assert.assertEquals(1, iter.getInnerRecordCount());
        TestRecordIterator.checkValues(vectors, 0);
        iter.next();
        // mark start of batch 2
        iter.mark();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(5, iter.cachedBatches().size());
        Assert.assertEquals(iter.getCurrentPosition(), 0);
        Assert.assertEquals(100, iter.getInnerRecordCount());
        for (int i = 0; i < 100; i++) {
            iter.next();
        }
        // mark start of batch 3
        iter.mark();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(4, iter.cachedBatches().size());
        Assert.assertEquals(iter.getCurrentPosition(), 0);
        Assert.assertEquals(10, iter.getInnerRecordCount());
        for (int i = 0; i < 10; i++) {
            iter.next();
        }
        // jump into middle of largest batch #4.
        for (int i = 0; i < 5000; i++) {
            iter.next();
        }
        Assert.assertEquals(4, iter.cachedBatches().size());
        iter.mark();
        Assert.assertEquals(3, iter.cachedBatches().size());
        for (int i = 0; i < 5000; i++) {
            iter.next();
        }
        // mark start of batch 5
        iter.mark();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(2, iter.cachedBatches().size());
        Assert.assertEquals(iter.getCurrentPosition(), 0);
        Assert.assertEquals(1, iter.getInnerRecordCount());
        // move to last batch
        iter.next();
        // skip to the middle of last batch
        for (int i = 0; i < 500; i++) {
            iter.next();
        }
        TestRecordIterator.checkValues(vectors, 499);
        TestRecordIterator.checkValues(vectors, 500);
        iter.reset();
        TestRecordIterator.checkValues(vectors, 0);
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(2, iter.cachedBatches().size());
        Assert.assertEquals(iter.getCurrentPosition(), 0);
        Assert.assertEquals(1, iter.getInnerRecordCount());
        // move to last batch
        iter.next();
        Assert.assertEquals(0, iter.getCurrentPosition());
        for (int i = 0; i < 500; i++) {
            iter.next();
        }
        // This should free 5th batch.
        iter.mark();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(1, iter.cachedBatches().size());
        Assert.assertEquals(500, iter.getCurrentPosition());
        Assert.assertEquals(1000, iter.getInnerRecordCount());
        // go to the end of iterator
        for (int i = 0; i < 500; i++) {
            iter.next();
        }
        Assert.assertTrue(iter.finished());
        iter.reset();
        Assert.assertFalse(iter.finished());
        Assert.assertEquals(iter.getTotalRecordCount(), 11112);
        Assert.assertEquals(1, iter.cachedBatches().size());
        Assert.assertEquals(500, iter.getCurrentPosition());
        Assert.assertEquals(1000, iter.getInnerRecordCount());
        iter.close();
        Assert.assertEquals(0, iter.cachedBatches().size());
    }
}

