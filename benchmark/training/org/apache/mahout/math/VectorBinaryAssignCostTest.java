/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.mahout.math;


import Functions.DIV;
import Functions.MINUS;
import Functions.MULT;
import Functions.PLUS;
import Functions.SECOND_LEFT_ZERO;
import VectorBinaryAssign.AssignAllIterateSequentialMergeUpdates;
import VectorBinaryAssign.AssignAllIterateThatLookupThisInplaceUpdates;
import VectorBinaryAssign.AssignAllIterateThisLookupThatMergeUpdates;
import VectorBinaryAssign.AssignAllLoopInplaceUpdates;
import VectorBinaryAssign.AssignIterateUnionSequentialInplaceUpdates;
import VectorBinaryAssign.AssignIterateUnionSequentialMergeUpdates;
import VectorBinaryAssign.AssignNonzerosIterateThatLookupThisInplaceUpdates;
import VectorBinaryAssign.AssignNonzerosIterateThatLookupThisMergeUpdates;
import VectorBinaryAssign.AssignNonzerosIterateThisLookupThat;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class VectorBinaryAssignCostTest {
    RandomAccessSparseVector realRasv = new RandomAccessSparseVector(1000000);

    SequentialAccessSparseVector realSasv = new SequentialAccessSparseVector(1000000);

    DenseVector realDense = new DenseVector(1000000);

    Vector rasv = EasyMock.createMock(Vector.class);

    Vector sasv = EasyMock.createMock(Vector.class);

    Vector dense = EasyMock.createMock(Vector.class);

    @Test
    public void denseInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, dense, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, dense, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(dense, dense, MULT).getClass());
        Assert.assertEquals(AssignAllLoopInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, dense, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, dense, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void sasvInteractions() {
        replayAll();
        Assert.assertEquals(AssignIterateUnionSequentialMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, sasv, PLUS).getClass());
        Assert.assertEquals(AssignIterateUnionSequentialMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, sasv, MINUS).getClass());
        Assert.assertEquals(AssignIterateUnionSequentialMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, sasv, MULT).getClass());
        Assert.assertEquals(AssignAllIterateSequentialMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, sasv, DIV).getClass());
        Assert.assertEquals(AssignIterateUnionSequentialMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, sasv, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void rasvInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, rasv, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, rasv, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(rasv, rasv, MULT).getClass());
        Assert.assertEquals(AssignAllLoopInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, rasv, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, rasv, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void sasvDenseInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, dense, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, dense, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(sasv, dense, MULT).getClass());
        Assert.assertEquals(AssignAllIterateThisLookupThatMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, dense, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, dense, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void denseSasvInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, sasv, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, sasv, MINUS).getClass());
        Assert.assertEquals(AssignIterateUnionSequentialInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, sasv, MULT).getClass());
        Assert.assertEquals(AssignAllIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, sasv, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, sasv, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void denseRasvInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, rasv, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, rasv, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(dense, rasv, MULT).getClass());
        Assert.assertEquals(AssignAllLoopInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, rasv, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(dense, rasv, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void rasvDenseInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, dense, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, dense, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(rasv, dense, MULT).getClass());
        Assert.assertEquals(AssignAllLoopInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, dense, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, dense, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void sasvRasvInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(sasv, rasv, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(sasv, rasv, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(sasv, rasv, MULT).getClass());
        Assert.assertEquals(AssignAllIterateThisLookupThatMergeUpdates.class, VectorBinaryAssign.getBestOperation(sasv, rasv, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(sasv, rasv, SECOND_LEFT_ZERO).getClass());
    }

    @Test
    public void rasvSasvInteractions() {
        replayAll();
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, sasv, PLUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, sasv, MINUS).getClass());
        Assert.assertEquals(AssignNonzerosIterateThisLookupThat.class, VectorBinaryAssign.getBestOperation(rasv, sasv, MULT).getClass());
        Assert.assertEquals(AssignAllIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, sasv, DIV).getClass());
        Assert.assertEquals(AssignNonzerosIterateThatLookupThisInplaceUpdates.class, VectorBinaryAssign.getBestOperation(rasv, sasv, SECOND_LEFT_ZERO).getClass());
    }
}

