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


import Functions.MAX_ABS;
import Functions.MINUS;
import Functions.MINUS_ABS;
import Functions.MINUS_SQUARED;
import Functions.MULT;
import Functions.MULT_SQUARE_LEFT;
import Functions.PLUS;
import VectorBinaryAggregate.AggregateIterateIntersection;
import VectorBinaryAggregate.AggregateIterateUnionRandom;
import VectorBinaryAggregate.AggregateIterateUnionSequential;
import VectorBinaryAggregate.AggregateNonzerosIterateThatLookupThis;
import VectorBinaryAggregate.AggregateNonzerosIterateThisLookupThat;
import org.apache.mahout.math.function.Functions;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class VectorBinaryAggregateCostTest {
    RandomAccessSparseVector realRasv = new RandomAccessSparseVector(1000000);

    SequentialAccessSparseVector realSasv = new SequentialAccessSparseVector(1000000);

    DenseVector realDense = new DenseVector(1000000);

    Vector rasv = EasyMock.createMock(Vector.class);

    Vector sasv = EasyMock.createMock(Vector.class);

    Vector dense = EasyMock.createMock(Vector.class);

    @Test
    public void denseInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(dense, dense, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, dense, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, dense, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, dense, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, dense, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(dense, dense, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void sasvInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateIterateIntersection.class, VectorBinaryAggregate.getBestOperation(sasv, sasv, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, sasv, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, sasv, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, sasv, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, sasv, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateIterateIntersection.class, VectorBinaryAggregate.getBestOperation(sasv, sasv, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void rasvInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(rasv, rasv, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, rasv, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, rasv, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, rasv, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, rasv, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(rasv, rasv, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void sasvDenseInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(sasv, dense, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, dense, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, dense, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, dense, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(sasv, dense, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(sasv, dense, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void denseSasvInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThatLookupThis.class, VectorBinaryAggregate.getBestOperation(dense, sasv, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, sasv, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, sasv, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, sasv, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionSequential.class, VectorBinaryAggregate.getBestOperation(dense, sasv, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThatLookupThis.class, VectorBinaryAggregate.getBestOperation(dense, sasv, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void denseRasvInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThatLookupThis.class, VectorBinaryAggregate.getBestOperation(dense, rasv, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(dense, rasv, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(dense, rasv, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(dense, rasv, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(dense, rasv, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThatLookupThis.class, VectorBinaryAggregate.getBestOperation(dense, rasv, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void rasvDenseInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(rasv, dense, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, dense, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, dense, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, dense, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, dense, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(rasv, dense, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void sasvRasvInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(sasv, rasv, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(sasv, rasv, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(sasv, rasv, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(sasv, rasv, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(sasv, rasv, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThisLookupThat.class, VectorBinaryAggregate.getBestOperation(sasv, rasv, PLUS, MULT_SQUARE_LEFT).getClass());
    }

    @Test
    public void rasvSasvInteractions() {
        replayAll();
        // Dot product
        Assert.assertEquals(AggregateNonzerosIterateThatLookupThis.class, VectorBinaryAggregate.getBestOperation(rasv, sasv, PLUS, MULT).getClass());
        // Chebyshev distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, sasv, MAX_ABS, MINUS).getClass());
        // Euclidean distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, sasv, PLUS, MINUS_SQUARED).getClass());
        // Manhattan distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, sasv, PLUS, MINUS_ABS).getClass());
        // Minkowski distance
        Assert.assertEquals(AggregateIterateUnionRandom.class, VectorBinaryAggregate.getBestOperation(rasv, sasv, PLUS, Functions.minusAbsPow(1.2)).getClass());
        // Tanimoto distance
        Assert.assertEquals(AggregateNonzerosIterateThatLookupThis.class, VectorBinaryAggregate.getBestOperation(rasv, sasv, PLUS, MULT_SQUARE_LEFT).getClass());
    }
}

