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
package org.nd4j.linalg.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class ShapeTest extends BaseNd4jTest {
    public ShapeTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testToOffsetZero() {
        INDArray matrix = Nd4j.rand(3, 5);
        INDArray rowOne = matrix.getRow(1);
        INDArray row1Copy = Shape.toOffsetZero(rowOne);
        Assert.assertEquals(rowOne, row1Copy);
        INDArray rows = matrix.getRows(1, 2);
        INDArray rowsOffsetZero = Shape.toOffsetZero(rows);
        Assert.assertEquals(rows, rowsOffsetZero);
        INDArray tensor = Nd4j.rand(new int[]{ 3, 3, 3 });
        INDArray getTensor = tensor.slice(1).slice(1);
        INDArray getTensorZero = Shape.toOffsetZero(getTensor);
        Assert.assertEquals(getTensor, getTensorZero);
    }

    @Test
    public void testDupLeadingTrailingZeros() {
        testDupHelper(1, 10);
        testDupHelper(10, 1);
        testDupHelper(1, 10, 1);
        testDupHelper(1, 10, 1, 1);
        testDupHelper(1, 10, 2);
        testDupHelper(2, 10, 1, 1);
        testDupHelper(1, 1, 1, 10);
        testDupHelper(10, 1, 1, 1);
        testDupHelper(1, 1);
    }

    @Test
    public void testLeadingOnes() {
        INDArray arr = Nd4j.create(1, 5, 5);
        Assert.assertEquals(1, arr.getLeadingOnes());
        INDArray arr2 = Nd4j.create(2, 2);
        Assert.assertEquals(0, arr2.getLeadingOnes());
        INDArray arr4 = Nd4j.create(1, 1, 5, 5);
        Assert.assertEquals(2, arr4.getLeadingOnes());
    }

    @Test
    public void testTrailingOnes() {
        INDArray arr2 = Nd4j.create(5, 5, 1);
        Assert.assertEquals(1, arr2.getTrailingOnes());
        INDArray arr4 = Nd4j.create(5, 5, 1, 1);
        Assert.assertEquals(2, arr4.getTrailingOnes());
    }

    @Test
    public void testElementWiseCompareOnesInMiddle() {
        INDArray arr = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray onesInMiddle = Nd4j.linspace(1, 6, 6).reshape(2, 1, 3);
        for (int i = 0; i < (arr.length()); i++) {
            double val = arr.getDouble(i);
            double middleVal = onesInMiddle.getDouble(i);
            Assert.assertEquals(val, middleVal, 0.1);
        }
    }

    @Test
    public void testSumLeadingTrailingZeros() {
        testSumHelper(1, 5, 5);
        testSumHelper(5, 5, 1);
        testSumHelper(1, 5, 1);
        testSumHelper(1, 5, 5, 5);
        testSumHelper(5, 5, 5, 1);
        testSumHelper(1, 5, 5, 1);
        testSumHelper(1, 5, 5, 5, 5);
        testSumHelper(5, 5, 5, 5, 1);
        testSumHelper(1, 5, 5, 5, 1);
        testSumHelper(1, 5, 5, 5, 5, 5);
        testSumHelper(5, 5, 5, 5, 5, 1);
        testSumHelper(1, 5, 5, 5, 5, 1);
    }

    @Test
    public void testEqualsWithSqueeze() {
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(null, null));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[0], new long[0]));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[0], new long[]{ 1 }));
        Assert.assertFalse(Shape.shapeEqualWithSqueeze(new long[0], new long[]{ 1, 2 }));
        Assert.assertFalse(Shape.shapeEqualWithSqueeze(new long[0], new long[]{ 2, 1 }));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[]{ 1 }, new long[0]));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[0], new long[]{ 1, 1, 1, 1, 1 }));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[]{ 1, 1, 1, 1, 1 }, new long[0]));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[]{ 1 }, new long[]{ 1, 1, 1 }));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[]{ 2, 3 }, new long[]{ 2, 3 }));
        Assert.assertFalse(Shape.shapeEqualWithSqueeze(new long[]{ 2, 3 }, new long[]{ 3, 2 }));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[]{ 1, 2, 2 }, new long[]{ 2, 2 }));
        Assert.assertTrue(Shape.shapeEqualWithSqueeze(new long[]{ 1, 2, 3 }, new long[]{ 2, 1, 1, 3 }));
        Assert.assertFalse(Shape.shapeEqualWithSqueeze(new long[]{ 1, 2, 3 }, new long[]{ 2, 1, 1, 4 }));
    }
}

