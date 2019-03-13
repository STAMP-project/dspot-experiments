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
package org.nd4j.linalg.slicing;


import DataType.DOUBLE;
import DataType.FLOAT;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.SpecifiedIndex;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class SlicingTestsC extends BaseNd4jTest {
    public SlicingTestsC(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSliceRowVector() {
        INDArray arr = Nd4j.zeros(5);
        System.out.println(arr.slice(1));
    }

    @Test
    public void testSliceAssertion() {
        INDArray arr = Nd4j.linspace(1, 30, 30).reshape(3, 5, 2);
        INDArray firstRow = arr.slice(0).slice(0);
        for (int i = 0; i < (firstRow.length()); i++) {
            System.out.println(firstRow.getDouble(i));
        }
        System.out.println(firstRow);
    }

    @Test
    public void testSliceShape() {
        INDArray arr = Nd4j.linspace(1, 30, 30, DOUBLE).reshape(3, 5, 2);
        INDArray sliceZero = arr.slice(0);
        for (int i = 0; i < (sliceZero.rows()); i++) {
            INDArray row = sliceZero.slice(i);
            for (int j = 0; j < (row.length()); j++) {
                System.out.println(row.getDouble(j));
            }
            System.out.println(row);
        }
        INDArray assertion = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, new int[]{ 5, 2 });
        for (int i = 0; i < (assertion.rows()); i++) {
            INDArray row = assertion.slice(i);
            for (int j = 0; j < (row.length()); j++) {
                System.out.println(row.getDouble(j));
            }
            System.out.println(row);
        }
        BaseNd4jTest.assertArrayEquals(new long[]{ 5, 2 }, sliceZero.shape());
        Assert.assertEquals(assertion, sliceZero);
        INDArray assertionTwo = Nd4j.create(new double[]{ 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }, new int[]{ 5, 2 });
        INDArray sliceTest = arr.slice(1);
        Assert.assertEquals(assertionTwo, sliceTest);
    }

    @Test
    public void testSwapReshape() {
        INDArray n2 = Nd4j.create(Nd4j.linspace(1, 30, 30, FLOAT).data(), new int[]{ 3, 5, 2 });
        INDArray swapped = n2.swapAxes(((n2.shape().length) - 1), 1);
        INDArray firstSlice2 = swapped.slice(0).slice(0);
        INDArray oneThreeFiveSevenNine = Nd4j.create(new float[]{ 1, 3, 5, 7, 9 });
        Assert.assertEquals(firstSlice2, oneThreeFiveSevenNine);
        INDArray raveled = oneThreeFiveSevenNine.reshape(5, 1);
        INDArray raveledOneThreeFiveSevenNine = oneThreeFiveSevenNine.reshape(5, 1);
        Assert.assertEquals(raveled, raveledOneThreeFiveSevenNine);
        INDArray firstSlice3 = swapped.slice(0).slice(1);
        INDArray twoFourSixEightTen = Nd4j.create(new float[]{ 2, 4, 6, 8, 10 });
        Assert.assertEquals(firstSlice2, oneThreeFiveSevenNine);
        INDArray raveled2 = twoFourSixEightTen.reshape(5, 1);
        INDArray raveled3 = firstSlice3.reshape(5, 1);
        Assert.assertEquals(raveled2, raveled3);
    }

    @Test
    public void testGetRow() {
        INDArray arr = Nd4j.linspace(1, 6, 6, DOUBLE).reshape(2, 3);
        INDArray get = arr.getRow(1);
        INDArray get2 = arr.get(NDArrayIndex.point(1), NDArrayIndex.all());
        INDArray assertion = Nd4j.create(new double[]{ 4, 5, 6 });
        Assert.assertEquals(assertion, get);
        Assert.assertEquals(get, get2);
        get2.assign(Nd4j.linspace(1, 3, 3, DOUBLE));
        Assert.assertEquals(Nd4j.linspace(1, 3, 3, DOUBLE), get2);
        INDArray threeByThree = Nd4j.linspace(1, 9, 9, DOUBLE).reshape(3, 3);
        INDArray offsetTest = threeByThree.get(new SpecifiedIndex(1, 2), NDArrayIndex.all());
        INDArray threeByThreeAssertion = Nd4j.create(new double[][]{ new double[]{ 4, 5, 6 }, new double[]{ 7, 8, 9 } });
        Assert.assertEquals(threeByThreeAssertion, offsetTest);
    }

    @Test
    public void testVectorIndexing() {
        INDArray zeros = Nd4j.create(1, 400000);
        INDArray get = zeros.get(NDArrayIndex.interval(0, 300000));
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 300000 }, get.shape());
    }
}

