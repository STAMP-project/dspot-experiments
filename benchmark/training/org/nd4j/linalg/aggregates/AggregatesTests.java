/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.aggregates;


import DataType.FLOAT;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.OpValidationSuite;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.aggregates.Aggregate;
import org.nd4j.linalg.api.ops.aggregates.impl.AggregateAxpy;
import org.nd4j.linalg.api.ops.aggregates.impl.AggregateSkipGram;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author raver119@gmail.com
 */
@RunWith(Parameterized.class)
public class AggregatesTests extends BaseNd4jTest {
    public AggregatesTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testAggregate1() {
        INDArray arrayX = Nd4j.ones(10);
        INDArray arrayY = Nd4j.zeros(10);
        INDArray exp1 = Nd4j.ones(10);
        AggregateAxpy axpy = new AggregateAxpy(arrayX, arrayY, 1.0F);
        Nd4j.getExecutioner().exec(axpy);
        Assert.assertEquals(exp1, arrayY);
    }

    @Test
    public void testBatchedAggregate1() {
        OpValidationSuite.ignoreFailing();// CRASHING

        INDArray arrayX1 = Nd4j.ones(FLOAT, 10);
        INDArray arrayY1 = Nd4j.zeros(FLOAT, 10);
        INDArray arrayX2 = Nd4j.ones(FLOAT, 10);
        INDArray arrayY2 = Nd4j.zeros(FLOAT, 10);
        INDArray exp1 = Nd4j.create(FLOAT, 10).assign(1.0F);
        INDArray exp2 = Nd4j.create(FLOAT, 10).assign(1.0F);
        AggregateAxpy axpy1 = new AggregateAxpy(arrayX1, arrayY1, 1.0F);
        AggregateAxpy axpy2 = new AggregateAxpy(arrayX2, arrayY2, 1.0F);
        List<Aggregate> batch = new ArrayList<>();
        batch.add(axpy1);
        batch.add(axpy2);
        Nd4j.getExecutioner().exec(batch);
        Assert.assertEquals(exp1, arrayY1);
        Assert.assertEquals(exp2, arrayY2);
    }

    @Test
    public void testBatchedAggregate2() {
        INDArray arrayX1 = Nd4j.ones(10);
        INDArray arrayY1 = Nd4j.zeros(10).assign(2.0F);
        INDArray arrayX2 = Nd4j.ones(10);
        INDArray arrayY2 = Nd4j.zeros(10).assign(2.0F);
        INDArray arrayX3 = Nd4j.ones(10);
        INDArray arrayY3 = Nd4j.ones(10);
        INDArray exp1 = Nd4j.create(10).assign(4.0F);
        INDArray exp2 = Nd4j.create(10).assign(3.0F);
        INDArray exp3 = Nd4j.create(10).assign(3.0F);
        AggregateAxpy axpy1 = new AggregateAxpy(arrayX1, arrayY1, 2.0F);
        AggregateAxpy axpy2 = new AggregateAxpy(arrayX2, arrayY2, 1.0F);
        AggregateAxpy axpy3 = new AggregateAxpy(arrayX3, arrayY3, 2.0F);
        List<Aggregate> batch = new ArrayList<>();
        batch.add(axpy1);
        batch.add(axpy2);
        batch.add(axpy3);
        Nd4j.getExecutioner().exec(batch);
        Assert.assertEquals(exp1, arrayY1);
        Assert.assertEquals(exp2, arrayY2);
        Assert.assertEquals(exp3, arrayY3);
    }

    @Test
    public void testBatchedSkipGram1() {
        OpValidationSuite.ignoreFailing();// CRASHING

        INDArray syn0 = Nd4j.create(FLOAT, 10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(FLOAT, 10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.ones(FLOAT, 10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(FLOAT, 10000).assign(0.5F);
        double lr = 0.001;
        int idxSyn0_1 = 0;
        int idxSyn0_2 = 3;
        INDArray expSyn0 = Nd4j.create(FLOAT, 10).assign(0.01F);
        INDArray expSyn1_1 = Nd4j.create(FLOAT, 10).assign(0.020005);// gradient is 0.00005

        INDArray expSyn1_2 = Nd4j.create(FLOAT, 10).assign(0.019995F);// gradient is -0.00005

        INDArray syn0row_1 = syn0.getRow(idxSyn0_1);
        INDArray syn0row_2 = syn0.getRow(idxSyn0_2);
        AggregateSkipGram op1 = new AggregateSkipGram(syn0, syn1, syn1Neg, expTable, null, idxSyn0_1, new int[]{ 1, 2 }, new int[]{ 0, 1 }, 0, 0, 10, lr, 1L, 10);
        AggregateSkipGram op2 = new AggregateSkipGram(syn0, syn1, syn1Neg, expTable, null, idxSyn0_2, new int[]{ 4, 5 }, new int[]{ 0, 1 }, 0, 0, 10, lr, 1L, 10);
        List<Aggregate> batch = new ArrayList<>();
        batch.add(op1);
        batch.add(op2);
        Nd4j.getExecutioner().exec(batch);
        /* Since expTable contains all-equal values, and only difference for ANY index is code being 0 or 1, syn0 row will stay intact,
        because neu1e will be full of 0.0f, and axpy will have no actual effect
         */
        Assert.assertEquals(expSyn0, syn0row_1);
        Assert.assertEquals(expSyn0, syn0row_2);
        // syn1 row 1 modified only once
        Assert.assertEquals(expSyn1_1, syn1.getRow(1));
        Assert.assertEquals(expSyn1_1, syn1.getRow(4));
        // syn1 row 2 modified only once
        Assert.assertEquals(expSyn1_2, syn1.getRow(2));
        Assert.assertEquals(expSyn1_2, syn1.getRow(5));
    }
}

