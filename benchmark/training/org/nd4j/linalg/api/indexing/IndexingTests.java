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
package org.nd4j.linalg.api.indexing;


import DataType.DOUBLE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class IndexingTests extends BaseNd4jTest {
    public IndexingTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testINDArrayIndexingEqualToRank() {
        INDArray x = Nd4j.linspace(1, 6, 6, DOUBLE).reshape('c', 3, 2).castTo(DOUBLE);
        INDArray indexes = Nd4j.create(new double[][]{ new double[]{ 0, 1, 2 }, new double[]{ 0, 1, 0 } });
        INDArray assertion = Nd4j.create(new double[]{ 1, 4, 5 });
        INDArray getTest = x.get(indexes);
        Assert.assertEquals(assertion, getTest);
    }

    @Test
    public void testINDArrayIndexingLessThanRankSimple() {
        INDArray x = Nd4j.linspace(1, 6, 6, DOUBLE).reshape('c', 3, 2).castTo(DOUBLE);
        INDArray indexes = Nd4j.create(new double[][]{ new double[]{ 0 } });
        INDArray assertion = Nd4j.create(new double[]{ 1, 2 });
        INDArray getTest = x.get(indexes);
        Assert.assertEquals(assertion, getTest);
    }

    @Test
    public void testINDArrayIndexingLessThanRankFourDimension() {
        INDArray x = Nd4j.linspace(1, 16, 16, DOUBLE).reshape('c', 2, 2, 2, 2).castTo(DOUBLE);
        INDArray indexes = Nd4j.create(new double[][]{ new double[]{ 0 }, new double[]{ 1 } });
        INDArray assertion = Nd4j.create(new double[]{ 5, 6, 7, 8 }).reshape('c', 1, 2, 2);
        INDArray getTest = x.get(indexes);
        Assert.assertEquals(assertion, getTest);
    }

    @Test
    public void testPutSimple() {
        INDArray x = Nd4j.linspace(1, 16, 16, DOUBLE).reshape('c', 2, 2, 2, 2);
        INDArray indexes = Nd4j.create(new double[][]{ new double[]{ 0 }, new double[]{ 1 } });
        x.put(indexes, Nd4j.create(new double[]{ 5, 5 }));
        INDArray vals = Nd4j.valueArrayOf(new long[]{ 2, 2, 2, 2 }, 5, DOUBLE);
        Assert.assertEquals(vals, x);
    }

    @Test
    public void testPutUnMatchDims() {
        List<List<Integer>> indices = new ArrayList<>();
        indices.add(Arrays.asList(0));
        indices.add(Arrays.asList(0, 1));
        INDArray linspace = Nd4j.linspace(1, 16, 16, DOUBLE).reshape('c', 2, 2, 2, 2);
        linspace.put(indices, Nd4j.scalar(99));
        INDArray assertion = Nd4j.valueArrayOf(new long[]{ 2, 2 }, 99.0, DOUBLE);
        for (int i = 0; i < 2; i++)
            Assert.assertEquals(assertion, linspace.slice(0).slice(i));

    }

    @Test
    public void testScalarPut() {
        List<List<Integer>> indices = new ArrayList<>();
        indices.add(Arrays.asList(0));
        indices.add(Arrays.asList(1));
        indices.add(Arrays.asList(0));
        indices.add(Arrays.asList(0));
        INDArray linspace = Nd4j.linspace(1, 16, 16, DOUBLE).reshape('c', 2, 2, 2, 2);
        linspace.put(indices, Nd4j.scalar(99.0));
        Assert.assertEquals(99.0, linspace.getDouble(0, 1, 0, 0), 0.1);
    }

    @Test
    public void testIndexGetDuplicate() {
        List<List<Integer>> indices = new ArrayList<>();
        indices.add(Arrays.asList(0, 0));
        INDArray linspace = Nd4j.linspace(1, 16, 16, DOUBLE).reshape('c', 2, 2, 2, 2).castTo(DOUBLE);
        INDArray get = linspace.get(indices);
        INDArray assertion = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8 }).reshape('c', 2, 2, 2, 2).castTo(DOUBLE);
        Assert.assertEquals(assertion, get);
    }

    @Test
    public void testGetScalar() {
        INDArray arr = Nd4j.linspace(1, 5, 5, DOUBLE).reshape(1, (-1));
        INDArray d = arr.get(NDArrayIndex.point(1));
        Assert.assertTrue(d.isScalar());
        Assert.assertEquals(2.0, d.getDouble(0), 0.1);
    }

    @Test
    public void testNewAxis() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        INDArray view = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.point(1));
        System.out.println(view);
    }

    @Test
    public void testVectorIndexing() {
        INDArray x = Nd4j.linspace(0, 10, 11, DOUBLE).reshape(1, 11).castTo(DOUBLE);
        int[] index = new int[]{ 5, 8, 9 };
        INDArray columnsTest = x.getColumns(index);
        Assert.assertEquals(Nd4j.create(new double[]{ 5, 8, 9 }), columnsTest);
        int[] index2 = new int[]{ 2, 2, 4 };// retrieve the same columns twice

        INDArray columnsTest2 = x.getColumns(index2);
        Assert.assertEquals(Nd4j.create(new double[]{ 2, 2, 4 }), columnsTest2);
    }

    @Test
    public void testGetRowsColumnsMatrix() {
        INDArray arr = Nd4j.linspace(1, 24, 24, DOUBLE).reshape(4, 6);
        INDArray firstAndSecondColumnsAssertion = Nd4j.create(new double[][]{ new double[]{ 1, 5 }, new double[]{ 2, 6 }, new double[]{ 3, 7 }, new double[]{ 4, 8 } });
        System.out.println(arr);
        INDArray firstAndSecondColumns = arr.getColumns(0, 1);
        Assert.assertEquals(firstAndSecondColumnsAssertion, firstAndSecondColumns);
        INDArray firstAndSecondRows = Nd4j.create(new double[][]{ new double[]{ 1.0, 5.0, 9.0, 13.0, 17.0, 21.0 }, new double[]{ 1.0, 5.0, 9.0, 13.0, 17.0, 21.0 }, new double[]{ 2.0, 6.0, 10.0, 14.0, 18.0, 22.0 } });
        INDArray rows = arr.getRows(0, 0, 1);
        Assert.assertEquals(firstAndSecondRows, rows);
    }

    @Test
    public void testSlicing() {
        INDArray arange = Nd4j.arange(1, 17).reshape(4, 4);
        INDArray slice1Assert = Nd4j.create(new double[]{ 2, 6, 10, 14 });
        INDArray slice1Test = arange.slice(1);
        Assert.assertEquals(slice1Assert, slice1Test);
    }

    @Test
    public void testArangeMul() {
        INDArray arange = Nd4j.arange(1, 17).reshape('f', 4, 4).castTo(DOUBLE);
        INDArrayIndex index = NDArrayIndex.interval(0, 2);
        INDArray get = arange.get(index, index);
        INDArray zeroPointTwoFive = Nd4j.ones(DOUBLE, 2, 2).mul(0.25);
        INDArray mul = get.mul(zeroPointTwoFive);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 0.25, 1.25 }, new double[]{ 0.5, 1.5 } }, 'f');
        Assert.assertEquals(assertion, mul);
    }

    @Test
    public void testGetIndicesVector() {
        INDArray line = Nd4j.linspace(1, 4, 4, DOUBLE).reshape(1, (-1));
        INDArray test = Nd4j.create(new double[]{ 2, 3 });
        INDArray result = line.get(NDArrayIndex.point(0), NDArrayIndex.interval(1, 3));
        Assert.assertEquals(test, result);
    }
}

