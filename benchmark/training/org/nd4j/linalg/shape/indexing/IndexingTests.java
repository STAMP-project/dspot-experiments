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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
public class IndexingTests extends BaseNd4jTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    public IndexingTests(Nd4jBackend backend) {
        super(backend);
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
        Assert.assertEquals(subArr_A, whatToPut);
        System.out.println(whatToPut);
        INDArrayIndex[] whereToPut = new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all() };
        subArr_B.put(whereToPut, whatToPut);
        Assert.assertEquals(subArr_A, subArr_B);
        System.out.println("... done");
    }

    /* Simple test that checks indexing through different ways that fails */
    @Test
    public void testSimplePoint() {
        INDArray A = Nd4j.linspace(1, ((3 * 3) * 3), ((3 * 3) * 3)).reshape(3, 3, 3);
        /* f - ordering
        1,10,19   2,11,20   3,12,21
        4,13,22   5,14,23   6,15,24
        7,16,25   8,17,26   9,18,27

        subsetting the
        11,20
        14,24 ndarray
         */
        INDArray viewOne = A.get(NDArrayIndex.point(1), NDArrayIndex.interval(0, 2), NDArrayIndex.interval(1, 3));
        INDArray viewTwo = A.get(NDArrayIndex.point(1)).get(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(1, 3));
        INDArray expected = Nd4j.zeros(2, 2);
        expected.putScalar(0, 0, 11);
        expected.putScalar(0, 1, 20);
        expected.putScalar(1, 0, 14);
        expected.putScalar(1, 1, 23);
        Assert.assertEquals("View with two get", expected, viewTwo);
        Assert.assertEquals("View with one get", expected, viewOne);// FAILS!

        Assert.assertEquals("Two views should be the same", viewOne, viewTwo);// Obviously fails

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

    @Test
    public void concatGetBug() {
        int width = 5;
        int height = 4;
        int depth = 3;
        int nExamples1 = 2;
        int nExamples2 = 1;
        int length1 = ((width * height) * depth) * nExamples1;
        int length2 = ((width * height) * depth) * nExamples2;
        INDArray first = Nd4j.linspace(1, length1, length1).reshape('c', nExamples1, depth, width, height);
        INDArray second = Nd4j.linspace(1, length2, length2).reshape('c', nExamples2, depth, width, height).addi(0.1);
        INDArray fMerged = Nd4j.concat(0, first, second);
        Assert.assertEquals(first, fMerged.get(NDArrayIndex.interval(0, nExamples1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all()));
        INDArray get = fMerged.get(NDArrayIndex.interval(nExamples1, (nExamples1 + nExamples2), true), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
        Assert.assertEquals(second, get.dup());// Passes

        Assert.assertEquals(second, get);// Fails

    }

    @Test
    public void testShape() {
        INDArray ndarray = Nd4j.create(new float[][]{ new float[]{ 1.0F, 2.0F }, new float[]{ 3.0F, 4.0F } });
        INDArray subarray = ndarray.get(NDArrayIndex.point(0), NDArrayIndex.all());
        Assert.assertTrue(subarray.isRowVector());
        val shape = subarray.shape();
        Assert.assertEquals(shape[0], 1);
        Assert.assertEquals(shape[1], 2);
    }

    @Test
    public void testGetRows() {
        INDArray arr = Nd4j.linspace(1, 9, 9, DOUBLE).reshape(3, 3);
        INDArray testAssertion = Nd4j.create(new double[][]{ new double[]{ 5, 8 }, new double[]{ 6, 9 } });
        INDArray test = arr.get(new SpecifiedIndex(1, 2), new SpecifiedIndex(1, 2));
        Assert.assertEquals(testAssertion, test);
    }

    @Test
    public void testFirstColumn() {
        INDArray arr = Nd4j.create(new double[][]{ new double[]{ 5, 6 }, new double[]{ 7, 8 } });
        INDArray assertion = Nd4j.create(new double[]{ 5, 7 });
        INDArray test = arr.get(NDArrayIndex.all(), NDArrayIndex.point(0));
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testLinearIndex() {
        INDArray linspace = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        for (int i = 0; i < (linspace.length()); i++) {
            Assert.assertEquals((i + 1), linspace.getDouble(i), 0.1);
        }
    }
}

