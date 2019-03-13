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
package org.nd4j.linalg.shape.indexing;


import DataType.DOUBLE;
import Nd4j.PadMode.CONSTANT;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.SpecifiedIndex;


/**
 *
 *
 * @author Adam Gibson
 */
@Slf4j
@RunWith(Parameterized.class)
public class IndexingTestsC extends BaseNd4jTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    public IndexingTestsC(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testExecSubArray() {
        INDArray nd = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6 }, new int[]{ 2, 3 });
        INDArray sub = nd.get(NDArrayIndex.all(), NDArrayIndex.interval(0, 2));
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.scalar.ScalarAdd(sub, 2));
        Assert.assertEquals(getFailureMessage(), Nd4j.create(new double[][]{ new double[]{ 3, 4 }, new double[]{ 6, 7 } }), sub);
    }

    @Test
    public void testLinearViewElementWiseMatching() {
        INDArray linspace = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray dup = linspace.dup();
        linspace.addi(dup);
    }

    @Test
    public void testGetRows() {
        INDArray arr = Nd4j.linspace(1, 9, 9, DOUBLE).reshape(3, 3);
        INDArray testAssertion = Nd4j.create(new double[][]{ new double[]{ 4, 5 }, new double[]{ 7, 8 } });
        INDArray test = arr.get(new SpecifiedIndex(1, 2), new SpecifiedIndex(0, 1));
        Assert.assertEquals(testAssertion, test);
    }

    @Test
    public void testFirstColumn() {
        INDArray arr = Nd4j.create(new double[][]{ new double[]{ 5, 7 }, new double[]{ 6, 8 } });
        INDArray assertion = Nd4j.create(new double[]{ 5, 6 });
        INDArray test = arr.get(NDArrayIndex.all(), NDArrayIndex.point(0));
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testMultiRow() {
        INDArray matrix = Nd4j.linspace(1, 9, 9, DOUBLE).reshape(3, 3);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 4, 7 } });
        INDArray test = matrix.get(new SpecifiedIndex(1, 2), NDArrayIndex.interval(0, 1));
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testPointIndexes() {
        INDArray arr = Nd4j.create(DOUBLE, 4, 3, 2);
        INDArray get = arr.get(NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.all());
        BaseNd4jTest.assertArrayEquals(new int[]{ 4, 2 }, get.shape());
        INDArray linspaced = Nd4j.linspace(1, 24, 24, DOUBLE).reshape(4, 3, 2);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 3, 4 }, new double[]{ 9, 10 }, new double[]{ 15, 16 }, new double[]{ 21, 22 } });
        INDArray linspacedGet = linspaced.get(NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.all());
        for (int i = 0; i < (linspacedGet.slices()); i++) {
            INDArray sliceI = linspacedGet.slice(i);
            Assert.assertEquals(assertion.slice(i), sliceI);
        }
        BaseNd4jTest.assertArrayEquals(new int[]{ 6, 1 }, linspacedGet.stride());
        Assert.assertEquals(assertion, linspacedGet);
    }

    @Test
    public void testGetWithVariedStride() {
        int ph = 0;
        int pw = 0;
        int sy = 2;
        int sx = 2;
        int iLim = 8;
        int jLim = 8;
        int i = 0;
        int j = 0;
        INDArray img = Nd4j.create(new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 }, new long[]{ 1, 1, 8, 8 });
        INDArray padded = Nd4j.pad(img, new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 0 }, new int[]{ ph, (ph + sy) - 1 }, new int[]{ pw, (pw + sx) - 1 } }, CONSTANT);
        INDArray get = padded.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(i, sy, iLim), NDArrayIndex.interval(j, sx, jLim));
        BaseNd4jTest.assertArrayEquals(new int[]{ 81, 81, 18, 2 }, get.stride());
        INDArray assertion = Nd4j.create(new double[]{ 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3 }, new int[]{ 1, 1, 4, 4 });
        Assert.assertEquals(assertion, get);
        i = 1;
        iLim = 9;
        INDArray get3 = padded.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(i, sy, iLim), NDArrayIndex.interval(j, sx, jLim));
        INDArray assertion2 = Nd4j.create(new double[]{ 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4 }, new int[]{ 1, 1, 4, 4 });
        BaseNd4jTest.assertArrayEquals(new int[]{ 81, 81, 18, 2 }, get3.stride());
        Assert.assertEquals(assertion2, get3);
        i = 0;
        iLim = 8;
        jLim = 9;
        j = 1;
        INDArray get2 = padded.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(i, sy, iLim), NDArrayIndex.interval(j, sx, jLim));
        BaseNd4jTest.assertArrayEquals(new int[]{ 81, 81, 18, 2 }, get2.stride());
        Assert.assertEquals(assertion, get2);
    }

    @Test
    public void testRowVectorInterval() {
        int len = 30;
        INDArray row = Nd4j.zeros(len);
        for (int i = 0; i < len; i++) {
            row.putScalar(i, i);
        }
        INDArray first10a = row.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 10));
        BaseNd4jTest.assertArrayEquals(first10a.shape(), new int[]{ 1, 10 });
        for (int i = 0; i < 10; i++)
            Assert.assertTrue(((first10a.getDouble(i)) == i));

        INDArray first10b = row.get(NDArrayIndex.interval(0, 10));
        BaseNd4jTest.assertArrayEquals(first10b.shape(), new int[]{ 1, 10 });
        for (int i = 0; i < 10; i++)
            Assert.assertTrue(((first10b.getDouble(i)) == i));

        INDArray last10a = row.get(NDArrayIndex.point(0), NDArrayIndex.interval(20, 30));
        BaseNd4jTest.assertArrayEquals(last10a.shape(), new int[]{ 1, 10 });
        for (int i = 0; i < 10; i++)
            Assert.assertTrue(((last10a.getDouble(i)) == (20 + i)));

        INDArray last10b = row.get(NDArrayIndex.interval(20, 30));
        BaseNd4jTest.assertArrayEquals(last10b.shape(), new int[]{ 1, 10 });
        for (int i = 0; i < 10; i++)
            Assert.assertTrue(((last10b.getDouble(i)) == (20 + i)));

    }

    @Test
    public void testGet() {
        System.out.println("Testing sub-array put and get with a 3D array ...");
        INDArray arr = Nd4j.linspace(0, 124, 125).reshape(5, 5, 5);
        /* Extract elements with the following indices:

        (2,1,1) (2,1,2) (2,1,3)
        (2,2,1) (2,2,2) (2,2,3)
        (2,3,1) (2,3,2) (2,3,3)
         */
        int slice = 2;
        int iStart = 1;
        int jStart = 1;
        int iEnd = 4;
        int jEnd = 4;
        // Method A: Element-wise.
        INDArray subArr_A = Nd4j.create(new int[]{ 3, 3 });
        for (int i = iStart; i < iEnd; i++) {
            for (int j = jStart; j < jEnd; j++) {
                double val = arr.getDouble(slice, i, j);
                int[] sub = new int[]{ i - iStart, j - jStart };
                subArr_A.putScalar(sub, val);
            }
        }
        // Method B: Using NDArray get and put with index classes.
        INDArray subArr_B = Nd4j.create(new int[]{ 3, 3 });
        INDArrayIndex ndi_Slice = NDArrayIndex.point(slice);
        INDArrayIndex ndi_J = NDArrayIndex.interval(jStart, jEnd);
        INDArrayIndex ndi_I = NDArrayIndex.interval(iStart, iEnd);
        INDArrayIndex[] whereToGet = new INDArrayIndex[]{ ndi_Slice, ndi_I, ndi_J };
        INDArray whatToPut = arr.get(whereToGet);
        System.out.println(whatToPut);
        INDArrayIndex[] whereToPut = new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all() };
        subArr_B.put(whereToPut, whatToPut);
        Assert.assertEquals(subArr_A, subArr_B);
        System.out.println("... done");
    }

    @Test
    public void testSimplePoint() {
        INDArray A = Nd4j.linspace(1, ((3 * 3) * 3), ((3 * 3) * 3)).reshape(3, 3, 3);
        /* c - ordering
        1,2,3   10,11,12    19,20,21
        4,5,6   13,14,15    22,23,24
        7,8,9   16,17,18    25,26,27
         */
        INDArray viewOne = A.get(NDArrayIndex.point(1), NDArrayIndex.interval(0, 2), NDArrayIndex.interval(1, 3));
        INDArray viewTwo = A.get(NDArrayIndex.point(1)).get(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(1, 3));
        INDArray expected = Nd4j.zeros(2, 2);
        expected.putScalar(0, 0, 11);
        expected.putScalar(0, 1, 12);
        expected.putScalar(1, 0, 14);
        expected.putScalar(1, 1, 15);
        Assert.assertEquals("View with two get", expected, viewTwo);
        Assert.assertEquals("View with one get", expected, viewOne);// FAILS!

        Assert.assertEquals("Two views should be the same", viewOne, viewTwo);// obviously fails

    }

    /* This is the same as the above test - just tests every possible window with a slice from the 0th dim
    They all fail - so it's possibly unrelated to the value of the index
     */
    @Test
    public void testPointIndexing() {
        int slices = 5;
        int rows = 5;
        int cols = 5;
        int l = (slices * rows) * cols;
        INDArray A = Nd4j.linspace(1, l, l).reshape(slices, rows, cols);
        for (int s = 0; s < slices; s++) {
            INDArrayIndex ndi_Slice = NDArrayIndex.point(s);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    log.info("Running for ( {}, {} - {} , {} - {} )", s, i, rows, j, cols);
                    INDArrayIndex ndi_I = NDArrayIndex.interval(i, rows);
                    INDArrayIndex ndi_J = NDArrayIndex.interval(j, cols);
                    INDArray aView = A.get(ndi_Slice).get(ndi_I, ndi_J);
                    INDArray sameView = A.get(ndi_Slice, ndi_I, ndi_J);
                    String failureMessage = String.format("Fails for (%d , %d - %d, %d - %d)\n", s, i, rows, j, cols);
                    try {
                        Assert.assertEquals(failureMessage, aView, sameView);
                    } catch (Throwable t) {
                        collector.addError(t);
                    }
                }
            }
        }
    }
}

