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


import DataType.DOUBLE;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.shape.Tile;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Adam Gibson
 */
@Slf4j
@RunWith(Parameterized.class)
public class ShapeTestC extends BaseNd4jTest {
    public ShapeTestC(Nd4jBackend backend) {
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
    public void testTile() {
        INDArray arr = Nd4j.scalar(DOUBLE, 1.0).reshape(1, 1);
        // INDArray[] inputs, INDArray[] outputs, int[] axis
        INDArray result = Nd4j.createUninitialized(DOUBLE, 2, 2);
        Tile tile = new Tile(new INDArray[]{ arr }, new INDArray[]{ result }, new int[]{ 2, 2 });
        Nd4j.getExecutioner().execAndReturn(tile);
        INDArray tiled = Nd4j.tile(arr, 2, 2).castTo(DOUBLE);
        Assert.assertEquals(tiled, result);
    }

    @Test
    public void testElementWiseCompareOnesInMiddle() {
        INDArray arr = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray onesInMiddle = Nd4j.linspace(1, 6, 6).reshape(2, 1, 3);
        for (int i = 0; i < (arr.length()); i++)
            Assert.assertEquals(arr.getDouble(i), onesInMiddle.getDouble(i), 0.001);

    }

    @Test
    public void testKeepDimsShape_1_T() {
        val shape = new int[]{ 5, 5 };
        val axis = new int[]{ 1, 0, 1 };
        val result = Shape.getReducedShape(shape, axis, true, true);
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1 }, result);
    }

    @Test
    public void testKeepDimsShape_1_F() {
        val shape = new int[]{ 5, 5 };
        val axis = new int[]{ 0, 0, 1 };
        val result = Shape.getReducedShape(shape, axis, false, true);
        BaseNd4jTest.assertArrayEquals(new long[]{  }, result);
    }

    @Test
    public void testKeepDimsShape_2_T() {
        val shape = new int[]{ 5, 5, 5 };
        val axis = new int[]{ 1, 0, 1 };
        val result = Shape.getReducedShape(shape, axis, true, true);
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1, 5 }, result);
    }

    @Test
    public void testKeepDimsShape_2_F() {
        val shape = new int[]{ 5, 5, 5 };
        val axis = new int[]{ 0, 0, 1 };
        val result = Shape.getReducedShape(shape, axis, false, true);
        BaseNd4jTest.assertArrayEquals(new long[]{ 5 }, result);
    }

    @Test
    public void testKeepDimsShape_3_T() {
        val shape = new int[]{ 1, 1 };
        val axis = new int[]{ 1, 0, 1 };
        val result = Shape.getReducedShape(shape, axis, true, true);
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1 }, result);
    }

    @Test
    public void testKeepDimsShape_3_F() {
        val shape = new int[]{ 1, 1 };
        val axis = new int[]{ 0, 0 };
        val result = Shape.getReducedShape(shape, axis, false, true);
        log.info("Result: {}", result);
        BaseNd4jTest.assertArrayEquals(new long[]{ 1 }, result);
    }

    @Test
    public void testKeepDimsShape_4_F() {
        val shape = new int[]{ 4, 4 };
        val axis = new int[]{ 0, 0 };
        val result = Shape.getReducedShape(shape, axis, false, true);
        log.info("Result: {}", result);
        BaseNd4jTest.assertArrayEquals(new long[]{ 4 }, result);
    }

    @Test
    public void testAxisNormalization_1() {
        val axis = new int[]{ 1, -2 };
        val rank = 2;
        val exp = new int[]{ 0, 1 };
        val norm = Shape.normalizeAxis(rank, axis);
        BaseNd4jTest.assertArrayEquals(exp, norm);
    }

    @Test
    public void testAxisNormalization_2() {
        val axis = new int[]{ 1, -2, 0 };
        val rank = 2;
        val exp = new int[]{ 0, 1 };
        val norm = Shape.normalizeAxis(rank, axis);
        BaseNd4jTest.assertArrayEquals(exp, norm);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testAxisNormalization_3() {
        val axis = new int[]{ 1, -2, 2 };
        val rank = 2;
        val exp = new int[]{ 0, 1 };
        val norm = Shape.normalizeAxis(rank, axis);
        BaseNd4jTest.assertArrayEquals(exp, norm);
    }

    @Test
    public void testAxisNormalization_4() {
        val axis = new int[]{ 1, 2, 0 };
        val rank = 3;
        val exp = new int[]{ 0, 1, 2 };
        val norm = Shape.normalizeAxis(rank, axis);
        BaseNd4jTest.assertArrayEquals(exp, norm);
    }
}

