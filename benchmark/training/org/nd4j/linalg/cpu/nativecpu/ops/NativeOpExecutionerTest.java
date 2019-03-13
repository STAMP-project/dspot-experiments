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
package org.nd4j.linalg.cpu.nativecpu.ops;


import DataType.DOUBLE;
import DataType.FLOAT;
import OpExecutioner.ProfilingMode.INF_PANIC;
import java.util.Arrays;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.reduce.MatchCondition;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarAdd;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@Ignore
public class NativeOpExecutionerTest {
    @Test
    public void execBroadcastOp() throws Exception {
        INDArray array = Nd4j.ones(1024, 1024);
        INDArray arrayRow = Nd4j.linspace(1, 1024, 1024);
        float sum = ((float) (array.sumNumber().doubleValue()));
        array.addiRowVector(arrayRow);
        long time1 = System.nanoTime();
        for (int x = 0; x < 100000; x++) {
            array.addiRowVector(arrayRow);
        }
        long time2 = System.nanoTime();
        /* time1 = System.nanoTime();
        array.addiRowVector(arrayRow);
        time2 = System.nanoTime();
         */
        System.out.println(("Execution time: " + ((time2 - time1) / 100000)));
        Assert.assertEquals(1002, array.getFloat(0), 0.1F);
        Assert.assertEquals(2003, array.getFloat(1), 0.1F);
    }

    @Test
    public void execReduceOp1() throws Exception {
        INDArray array = Nd4j.ones(1024, 1024);
        INDArray arrayRow1 = Nd4j.linspace(1, 1024, 1024);
        INDArray arrayRow2 = Nd4j.linspace(0, 1023, 1024);
        float sum = ((float) (array.sumNumber().doubleValue()));
        long time1 = System.nanoTime();
        array.sum(0);
        long time2 = System.nanoTime();
        System.out.println(("Execution time: " + (time2 - time1)));
        System.out.println(("Result: " + sum));
    }

    @Test
    public void execReduceOp2() throws Exception {
        INDArray array = Nd4j.ones(3, 1024);
        INDArray arrayRow = Nd4j.linspace(1, 1024, 1024);
        float sum = array.sumNumber().floatValue();
        long time1 = System.nanoTime();
        sum = array.sumNumber().floatValue();
        long time2 = System.nanoTime();
        System.out.println(("Execution time: " + (time2 - time1)));
    }

    @Test
    public void execTransformOp1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 20480, 20480);
        INDArray array2 = Nd4j.linspace(1, 20480, 20480);
        Nd4j.getExecutioner().exec(new Exp(array1, array2));
        long time1 = System.nanoTime();
        for (int x = 0; x < 10000; x++) {
            Nd4j.getExecutioner().exec(new Exp(array1, array2));
        }
        long time2 = System.nanoTime();
        System.out.println(("Execution time: " + ((time2 - time1) / 10000)));
        // System.out.println("Array1: " + array1);
        // System.out.println("Array2: " + array2);
        Assert.assertEquals(2.71F, array2.getFloat(0), 0.01);
    }

    @Test
    public void execPairwiseOp1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 20480, 20480);
        INDArray array2 = Nd4j.linspace(1, 20480, 20480);
        array1.sumNumber();
        array2.sumNumber();
        long time1 = System.nanoTime();
        for (int x = 0; x < 10000; x++) {
            array1.addiRowVector(array2);
        }
        long time2 = System.nanoTime();
        System.out.println(("Execution time: " + ((time2 - time1) / 10000)));
        // System.out.println("Array1: " + array1);
        // System.out.println("Array2: " + array2);
        Assert.assertEquals(10001.0F, array1.getFloat(0), 0.01);
    }

    @Test
    public void testScalarOp1() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 20480, 20480);
        INDArray array2 = Nd4j.linspace(1, 20480, 20480);
        array2.addi(0.5F);
        long time1 = System.nanoTime();
        for (int x = 0; x < 10000; x++) {
            array2.addi(0.5F);
        }
        long time2 = System.nanoTime();
        System.out.println(("Execution time: " + ((time2 - time1) / 10000)));
        System.out.println(("Divi result: " + (array2.getFloat(0))));
        Assert.assertEquals(5001.5, array2.getFloat(0), 0.01F);
    }

    @Test
    public void testSoftmax1D_1() throws Exception {
        INDArray input1T = Nd4j.create(new double[]{ -0.75, 0.58, 0.42, 1.03, -0.61, 0.19, -0.37, -0.4, -1.42, -0.04 });
        INDArray input1 = Nd4j.create(new double[]{ -0.75, 0.58, 0.42, 1.03, -0.61, 0.19, -0.37, -0.4, -1.42, -0.04 });
        INDArray input2 = Nd4j.zerosLike(input1);
        Nd4j.copy(input1, input2);
        INDArray output1 = Nd4j.create(1, 10);
        INDArray output1T = Nd4j.create(1, 10);
        System.out.println("FA --------------------");
        Nd4j.getExecutioner().exec(new OldSoftMax(input1, output1));
        Nd4j.getExecutioner().exec(new OldSoftMax(input1T, output1T));
        System.out.println("FB --------------------");
        System.out.println(("Softmax = " + output1));
        INDArray output2 = Nd4j.create(1, 10);
        Nd4j.getExecutioner().exec(new SoftMaxDerivative(input2, output2));
        System.out.println(("Softmax Derivative = " + output2));
        INDArray assertion1 = Nd4j.create(new double[]{ 0.04, 0.16, 0.14, 0.26, 0.05, 0.11, 0.06, 0.06, 0.02, 0.09 });
        Assert.assertArrayEquals(assertion1.data().asFloat(), output1.data().asFloat(), 0.01F);
        Assert.assertArrayEquals(assertion1.data().asFloat(), output1T.data().asFloat(), 0.01F);
    }

    @Test
    public void testNd4jDup() {
        /* set the dType */
        DataTypeUtil.setDTypeForContext(DOUBLE);
        /* create NDArray from a double[][] */
        int cnt = 0;
        double[][] data = new double[50][50];
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                data[x][y] = cnt;
                cnt++;
            }
        }
        INDArray testNDArray = Nd4j.create(data);
        /* print the first row */
        System.out.println(("A: " + (testNDArray.getRow(0))));
        /* set the dType again! */
        DataTypeUtil.setDTypeForContext(DOUBLE);
        /* print the first row */
        System.out.println(("B: " + (testNDArray.getRow(0))));
        /* print the first row dup -- it should be different now! */
        System.out.println(("C: " + (testNDArray.getRow(0).dup())));
    }

    @Test
    public void testPinnedManhattanDistance2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 1000, 1000);
        INDArray array2 = Nd4j.linspace(1, 900, 1000);
        double result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.reduce.distances.ManhattanDistance(array1, array2)).getFinalResult().doubleValue();
        Assert.assertEquals(50000.0, result, 0.001F);
    }

    @Test
    public void testPinnedCosineSimilarity2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 1000, 1000);
        INDArray array2 = Nd4j.linspace(100, 200, 1000);
        double result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.reduce.distances.CosineSimilarity(array1, array2)).getFinalResult().doubleValue();
        Assert.assertEquals(0.945F, result, 0.001F);
    }

    @Test
    public void testArgMax1() {
        INDArray array1 = Nd4j.create(new float[]{ -1.0F, 2.0F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F });
        INDArray res = Nd4j.argMax(array1);
        System.out.println(("Res length: " + (res.length())));
        Assert.assertEquals(1.0F, res.getFloat(0), 0.01F);
        System.out.println("--------------------");
        res = Nd4j.argMax(array2);
        System.out.println(("Res length: " + (res.length())));
        Assert.assertEquals(0.0F, res.getFloat(0), 0.01F);
    }

    @Test
    public void testBroadcastWithPermute() {
        Nd4j.getRandom().setSeed(12345);
        int length = ((4 * 4) * 5) * 2;
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', 4, 4, 5, 2).permute(2, 3, 1, 0);
        // INDArray arr = Nd4j.linspace(1,length,length).reshape('f',4,4,5,2).permute(2,3,1,0);
        INDArray arrDup = arr.dup('c');
        INDArray row = Nd4j.rand(1, 2);
        Assert.assertEquals(row.length(), arr.size(1));
        Assert.assertEquals(row.length(), arrDup.size(1));
        Assert.assertEquals(arr, arrDup);
        INDArray first = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.broadcast.BroadcastSubOp(arr, row, Nd4j.createUninitialized(arr.shape(), 'c'), 1));
        INDArray second = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.broadcast.BroadcastSubOp(arrDup, row, Nd4j.createUninitialized(arr.shape(), 'c'), 1));
        System.out.println(("A1: " + (Arrays.toString(arr.shapeInfoDataBuffer().asInt()))));
        System.out.println(("A2: " + (Arrays.toString(first.shapeInfoDataBuffer().asInt()))));
        System.out.println(("B1: " + (Arrays.toString(arrDup.shapeInfoDataBuffer().asInt()))));
        System.out.println(("B2: " + (Arrays.toString(second.shapeInfoDataBuffer().asInt()))));
        INDArray resultSameStrides = Nd4j.zeros(new int[]{ 4, 4, 5, 2 }, 'c').permute(2, 3, 1, 0);
        Assert.assertArrayEquals(arr.stride(), resultSameStrides.stride());
        // INDArray third = Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(arr, row, resultSameStrides, 1));
        // assertEquals(second, third);    //Original and result w/ same strides: passes
        // assertEquals(first,second);     //Original and result w/ different strides: fails
    }

    @Test
    public void testBroadcastEquality1() {
        INDArray array = Nd4j.zeros(new int[]{ 4, 5 }, 'f');
        INDArray array2 = Nd4j.zeros(new int[]{ 4, 5 }, 'f');
        INDArray row = Nd4j.create(new float[]{ 1, 2, 3, 4, 5 });
        array.addiRowVector(row);
        System.out.println(array);
        System.out.println("-------");
        ScalarAdd add = new ScalarAdd(array2, row, array2, array2.length(), 0.0F);
        add.setDimension(0);
        Nd4j.getExecutioner().exec(add);
        System.out.println(array2);
        Assert.assertEquals(array, array2);
    }

    @Test
    public void testBroadcastEquality2() {
        INDArray array = Nd4j.zeros(new int[]{ 4, 5 }, 'c');
        INDArray array2 = Nd4j.zeros(new int[]{ 4, 5 }, 'c');
        INDArray column = Nd4j.create(new float[]{ 1, 2, 3, 4 }).reshape(4, 1);
        array.addiColumnVector(column);
        System.out.println(array);
        System.out.println("-------");
        ScalarAdd add = new ScalarAdd(array2, column, array2, array2.length(), 0.0F);
        add.setDimension(1);
        Nd4j.getExecutioner().exec(add);
        System.out.println(array2);
        Assert.assertEquals(array, array2);
    }

    @Test
    public void testIsMaxC1() throws Exception {
        INDArray array = Nd4j.zeros(new int[]{ 4, 5 }, 'c');
        Nd4j.getExecutioner().exec(new IsMax(array, 1));
    }

    @Test
    public void testIsMaxC2() throws Exception {
        INDArray array = Nd4j.zeros(new int[]{ 4, 5 }, 'c');
        Nd4j.getExecutioner().exec(new IsMax(array, 0));
    }

    @Test
    public void testIsMaxF1() throws Exception {
        INDArray array = Nd4j.zeros(new int[]{ 4, 5 }, 'f');
        Nd4j.getExecutioner().exec(new IsMax(array, 1));
    }

    @Test
    public void testEnvironment() throws Exception {
        INDArray array = Nd4j.zeros(new int[]{ 4, 5 }, 'f');
        Properties properties = Nd4j.getExecutioner().getEnvironmentInformation();
        System.out.println(("Props: " + (properties.toString())));
    }

    @Test
    public void testIsView() {
        INDArray array = Nd4j.zeros(100, 100);
        Assert.assertFalse(array.isView());
    }

    @Test
    public void testIMaxIAMax() {
        INDArray arr = Nd4j.create(new double[]{ -0.24, -0.26, -0.07, -0.01 });
        double imax = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.indexaccum.IMax(arr.dup())).getFinalResult();
        double iamax = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.indexaccum.IAMax(arr.dup())).getFinalResult();
        System.out.println(("IMAX: " + imax));
        System.out.println(("IAMAX: " + iamax));
        Assert.assertEquals(3, imax, 0.0);
        Assert.assertEquals(1, iamax, 0.0);
    }

    @Test
    public void testIMinIAMin() {
        INDArray arr = Nd4j.create(new double[]{ -0.24, -0.26, -0.07, -0.01 });
        double imin = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.indexaccum.IMin(arr.dup())).getFinalResult();
        double iamin = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.indexaccum.IAMin(arr.dup())).getFinalResult();
        System.out.println(("IMin: " + imin));
        System.out.println(("IAMin: " + iamin));
        Assert.assertEquals(1, imin, 0.0);
        Assert.assertEquals(3, iamin, 0.0);
    }

    @Test
    public void testViewData1() {
        INDArray in = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } }, 'c');
        System.out.println(("Input data: " + (Arrays.toString(in.data().asDouble()))));
        INDArray out = in.getRow(1);
        System.out.println(("Out:        " + out));
        System.out.println(("Out data:   " + (Arrays.toString(out.data().asFloat()))));
        Assert.assertTrue(out.isView());
        Assert.assertEquals(2, out.data().length());
        out.addi(2.0F);
        System.out.println(("Out data:   " + (Arrays.toString(out.data().asFloat()))));
        System.out.println(("Input data: " + (Arrays.toString(in.data().asDouble()))));
    }

    @Test
    public void testViewData2() {
        INDArray in = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } }, 'f');
        System.out.println(("Input data: " + (Arrays.toString(in.data().asDouble()))));
        INDArray out = in.getRow(1);
        System.out.println(("Out:        " + out));
        System.out.println(("Out data:   " + (Arrays.toString(out.data().asFloat()))));
        Assert.assertTrue(out.isView());
        Assert.assertEquals(2, out.data().length());
        out.addi(2.0F);
        System.out.println(("Out data:   " + (Arrays.toString(out.data().asFloat()))));
        System.out.println(("Input data: " + (Arrays.toString(in.data().asDouble()))));
    }

    @Test
    public void testMmulC1() throws Exception {
        INDArray A = Nd4j.linspace(0, 11, 12).reshape('c', 4, 3);
        INDArray B = Nd4j.linspace(0, 11, 12).reshape('c', 3, 4);
        System.out.println(("A: \n" + A));
        INDArray C = A.mmul(B);
        INDArray expC = Nd4j.create(new double[]{ 20.0, 23.0, 26.0, 29.0, 56.0, 68.0, 80.0, 92.0, 92.0, 113.0, 134.0, 155.0, 128.0, 158.0, 188.0, 218.0 }).reshape(4, 4);
        Assert.assertEquals(expC, C);
        Nd4j.enableFallbackMode(true);
        INDArray CF = A.mmul(B);
        Assert.assertEquals(expC, CF);
        Nd4j.enableFallbackMode(false);
    }

    @Test
    public void testMmulF1() throws Exception {
        INDArray A = Nd4j.linspace(0, 11, 12).reshape('f', 4, 3);
        INDArray B = Nd4j.linspace(0, 11, 12).reshape('f', 3, 4);
        System.out.println(("A: \n" + A));
        INDArray C = A.mmul(B);
        System.out.println(("C: \n" + (Arrays.toString(C.data().asFloat()))));
        INDArray expF = Nd4j.create(new double[]{ 20.0, 23.0, 26.0, 29.0, 56.0, 68.0, 80.0, 92.0, 92.0, 113.0, 134.0, 155.0, 128.0, 158.0, 188.0, 218.0 }).reshape('f', 4, 4);
        Assert.assertEquals(expF, C);
        Nd4j.enableFallbackMode(true);
        INDArray CF = A.mmul(B);
        Assert.assertEquals(expF, CF);
        Nd4j.enableFallbackMode(false);
    }

    @Test
    public void testDebugEdgeCase() {
        INDArray l1 = Nd4j.create(new double[]{ -0.2585039112684677, -0.005179485353710878, 0.4348343401770497, 0.020356532375728764, -0.1970793298488186 });
        INDArray l2 = Nd4j.create(3, l1.size(1));
        INDArray p1 = Nd4j.create(new double[]{ 1.3979850406519119, 0.6169451410155852, 1.128993957530918, 0.21000426084450596, 0.3171215178932696 });
        INDArray p2 = Nd4j.create(3, p1.size(1));
        for (int i = 0; i < 3; i++) {
            l2.putRow(i, l1);
            p2.putRow(i, p1);
        }
        INDArray s1 = NativeOpExecutionerTest.scoreArray(l1, p1);
        INDArray s2 = NativeOpExecutionerTest.scoreArray(l2, p2);
        // Outputs here should be identical:
        System.out.println(Arrays.toString(s1.data().asDouble()));
        System.out.println(Arrays.toString(s2.getRow(0).dup().data().asDouble()));
    }

    @Test
    public void testGemmPerf() {
        INDArray A = Nd4j.create(new int[]{ 10000, 1000 }, 'c');
        INDArray B = Nd4j.create(new int[]{ 1000, 10000 }, 'f');
        Nd4j.enableFallbackMode(false);
        A.mmul(B);
        long time1 = System.currentTimeMillis();
        INDArray C1 = A.mmul(B);
        long time2 = System.currentTimeMillis();
        log.info("OpenBLAS time: {}", (time2 - time1));
        Nd4j.enableFallbackMode(true);
        A.mmul(B);
        time1 = System.currentTimeMillis();
        INDArray C2 = A.mmul(B);
        time2 = System.currentTimeMillis();
        log.info("Fallback time: {}", (time2 - time1));
        Nd4j.enableFallbackMode(false);
    }

    @Test
    public void testDebugEdgeCase2() {
        DataTypeUtil.setDTypeForContext(DOUBLE);
        INDArray l1 = Nd4j.create(new double[]{ -0.2585039112684677, -0.005179485353710878, 0.4348343401770497, 0.020356532375728764, -0.1970793298488186 });
        INDArray l2 = Nd4j.create(2, l1.size(1));
        INDArray p1 = Nd4j.create(new double[]{ 1.3979850406519119, 0.6169451410155852, 1.128993957530918, 0.21000426084450596, 0.3171215178932696 });
        INDArray p2 = Nd4j.create(2, p1.size(1));
        for (int i = 0; i < 2; i++) {
            l2.putRow(i, l1);
            p2.putRow(i, p1);
        }
        INDArray norm2_1 = l1.norm2(1);
        INDArray temp1 = p1.mul(l1);
        INDArray out1 = temp1.diviColumnVector(norm2_1);
        INDArray norm2_2 = l2.norm2(1);
        INDArray temp2 = p2.mul(l2);
        INDArray out2 = temp2.diviColumnVector(norm2_2);
        System.out.println(("norm2_1: " + (Arrays.toString(norm2_1.data().asDouble()))));
        System.out.println(("norm2_2: " + (Arrays.toString(norm2_2.data().asDouble()))));
        System.out.println(("temp1: " + (Arrays.toString(temp1.data().asDouble()))));
        System.out.println(("temp2: " + (Arrays.toString(temp2.data().asDouble()))));
        // Outputs here should be identical:
        System.out.println(Arrays.toString(out1.data().asDouble()));
        System.out.println(Arrays.toString(out2.getRow(0).dup().data().asDouble()));
    }

    @Test
    public void testMul_Scalar1() throws Exception {
        DataTypeUtil.setDTypeForContext(DOUBLE);
        INDArray x = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        INDArray y = Nd4j.create(10).assign(3.0E-6);
        x.muli(y);
        x.divi(2.2E-6);
        System.out.println(("Data: " + (Arrays.toString(x.data().asDouble()))));
    }

    @Test
    public void testEuclideanManhattanDistanceAlongDimension_Rank4() {
        Nd4j.getRandom().setSeed(12345);
        INDArray firstOneExample = Nd4j.rand('c', new int[]{ 1, 2, 2, 2 });
        INDArray secondOneExample = Nd4j.rand('c', new int[]{ 1, 2, 2, 2 });
        double[] d1 = firstOneExample.data().asDouble();
        double[] d2 = secondOneExample.data().asDouble();
        double sumSquaredDiff = 0.0;
        double expManhattanDistance = 0.0;
        for (int i = 0; i < (d1.length); i++) {
            double diff = (d1[i]) - (d2[i]);
            sumSquaredDiff += diff * diff;
            expManhattanDistance += Math.abs(diff);
        }
        double expected = Math.sqrt(sumSquaredDiff);
        System.out.println(("Expected, Euclidean: " + expected));
        System.out.println(("Expected, Manhattan: " + expManhattanDistance));
        int mb = 2;
        INDArray firstOrig = Nd4j.create(mb, 2, 2, 2);
        INDArray secondOrig = Nd4j.create(mb, 2, 2, 2);
        for (int i = 0; i < mb; i++) {
            firstOrig.put(new INDArrayIndex[]{ NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all() }, firstOneExample);
            secondOrig.put(new INDArrayIndex[]{ NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all() }, secondOneExample);
        }
        for (char order : new char[]{ 'c', 'f' }) {
            INDArray first = firstOrig.dup(order);
            INDArray second = secondOrig.dup(order);
            Assert.assertEquals(firstOrig, first);
            Assert.assertEquals(secondOrig, second);
            INDArray out = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.distances.EuclideanDistance(first, second), 1, 2, 3);
            INDArray outManhattan = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.distances.ManhattanDistance(first, second), 1, 2, 3);
            System.out.println(("\n\nOrder: " + order));
            System.out.println("Euclidean:");
            System.out.println(Arrays.toString(out.getRow(0).dup().data().asDouble()));
            System.out.println(Arrays.toString(out.getRow(1).dup().data().asDouble()));
            Assert.assertEquals(out.getRow(0), out.getRow(1));
            System.out.println("Manhattan:");
            System.out.println(Arrays.toString(outManhattan.getRow(0).dup().data().asDouble()));
            System.out.println(Arrays.toString(outManhattan.getRow(1).dup().data().asDouble()));
            Assert.assertEquals(expected, out.getRow(0).getDouble(0), 1.0E-5);
            Assert.assertEquals(expManhattanDistance, outManhattan.getRow(0).getDouble(0), 1.0E-5);
        }
    }

    @Test
    public void testWrongDimensions() {
        INDArray arr = Nd4j.create(10, 10, 10);
        arr.mean(0, 2, 3);
    }

    @Test
    public void testFallbackEcho() {
        log.info("Fallback enabled? {}", Nd4j.isFallbackModeEnabled());
    }

    @Test
    public void testPewPew() {
        INDArray array = Nd4j.ones(2, 2).eq(1);
        log.info("Result: {} ", array);
    }

    @Test
    public void testCreate() {
        INDArray array0 = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        INDArray array0_1 = array0.dup();
        INDArray array0_2 = array0.dup();
        INDArray array0_3 = array0.dup();
        INDArray array1 = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new int[]{ 3, 3 }, 'c');
        INDArray array1_1 = array1.dup();
        INDArray array1_2 = array1.dup();
        INDArray array1_3 = array1.dup();
        INDArray array2 = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29 }, new int[]{ 3, 3, 3 }, 'c');
        INDArray array2_1 = array2.dup();
        INDArray array2_2 = array2.dup();
        INDArray array2_3 = array2.dup();
        Assert.assertEquals(2, array1.rank());
        Assert.assertEquals(3, array2.rank());
        INDArray array2D = Nd4j.vstack(array0, array0_1, array0_2, array0_3);
        INDArray array3D = Nd4j.vstack(array1, array1_1, array1_2, array1_3);
        INDArray array4D = Nd4j.vstack(array2, array2_1, array2_2, array2_3);
        log.info("Output Array2D rank: {}", array2D.rank());
        log.info("Output Array3D rank: {}", array3D.rank());
        log.info("Output Array4D rank: {}", array4D.rank());
    }

    @Test
    public void testConditionalUpdate() {
        INDArray arr = Nd4j.linspace((-2), 2, 5);
        INDArray ones = Nd4j.ones(5);
        System.out.println(("arr: " + arr));
        System.out.println(("ones: " + ones));
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.comparison.CompareAndSet(ones, arr, ones, Conditions.equals(0.0)));
        System.out.println("After:");
        System.out.println(("arr: " + arr));
        System.out.println(("ones: " + ones));
    }

    @Test
    public void testBroadcastMultiDim() throws Exception {
        // Broadcast 1d: OK
        INDArray arr2d = Nd4j.ones(2, 3);
        INDArray toBCRow = Nd4j.create(new double[]{ 1, 0, 0 });
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.broadcast.BroadcastMulOp(arr2d, toBCRow, arr2d, 1));
        INDArray exp2d = Nd4j.create(new double[][]{ new double[]{ 1, 0, 0 }, new double[]{ 1, 0, 0 } });
        Assert.assertEquals(exp2d, arr2d);
        // Broadcast 2d on 3d:
        INDArray arr3d = Nd4j.ones(2, 3, 5);
        INDArray bc2d = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 0, 0 } });
        bc2d.get(NDArrayIndex.point(1), NDArrayIndex.interval(3, 5)).assign(0);
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.broadcast.BroadcastMulOp(arr3d, bc2d, arr3d, 0, 2));
        INDArray exp3d = Nd4j.ones(2, 3, 5);
        exp3d.get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5)).assign(0);
        for (int i = 0; i < 2; i++) {
            System.out.println(("Arr - " + i));
            System.out.println(arr3d.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all()));
            System.out.println(("Exp - " + i));
            System.out.println(exp3d.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all()));
            System.out.println();
        }
        Assert.assertEquals(exp3d, arr3d);
    }

    @Test
    public void testGet() throws Exception {
        Nd4j.setDataType(DOUBLE);
        INDArray recurrentWeights = Nd4j.create(127, 511);
        for (int i = 0; i < 5; i++) {
            INDArray recurrentWeightsIFOG = recurrentWeights.get(NDArrayIndex.all(), NDArrayIndex.interval(0, (4 * 127))).dup('f');
            log.info("orig: {}", Arrays.toString(recurrentWeights.shapeInfoDataBuffer().asInt()));
            log.info("data: {}", Arrays.toString(recurrentWeightsIFOG.shapeInfoDataBuffer().asInt()));
            log.info("--------------");
        }
    }

    @Test
    public void testInf() {
        Nd4j.setDataType(FLOAT);
        INDArray x = Nd4j.create(10, 10);
        x.minNumber();
        MatchCondition condition = new MatchCondition(x, Conditions.isInfinite());
        int match = Nd4j.getExecutioner().exec(condition, Integer.MAX_VALUE).getInt(0);
        log.info("Matches: {}", match);
        Nd4j.getExecutioner().setProfilingMode(INF_PANIC);
        x = Nd4j.create(10, 10);
        x.minNumber();
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testMismatch() {
        INDArray y = Nd4j.create(100, 100);
        INDArray x = Nd4j.create(50, 50);
        x.add(1.0, y);
    }

    @Test
    public void testIsMax2Of3d() {
        double[][][] slices = new double[3][][];
        double[][][] isMax = new double[3][][];
        slices[0] = new double[][]{ new double[]{ 1, 10, 2 }, new double[]{ 3, 4, 5 } };
        slices[1] = new double[][]{ new double[]{ -10, -9, -8 }, new double[]{ -7, -6, -5 } };
        slices[2] = new double[][]{ new double[]{ 4, 3, 2 }, new double[]{ 1, 0, -1 } };
        isMax[0] = new double[][]{ new double[]{ 0, 1, 0 }, new double[]{ 0, 0, 0 } };
        isMax[1] = new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 0, 0, 1 } };
        isMax[2] = new double[][]{ new double[]{ 1, 0, 0 }, new double[]{ 0, 0, 0 } };
        INDArray arr = Nd4j.create(3, 2, 3);
        INDArray expected = Nd4j.create(3, 2, 3);
        for (int i = 0; i < 3; i++) {
            arr.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.create(slices[i]));
            expected.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.create(isMax[i]));
        }
        Nd4j.getExecutioner().exec(new IsMax(arr, 1, 2));
        Assert.assertEquals(expected, arr);
    }

    @Test
    public void testPewPew2() throws Exception {
        INDArray nd3 = Nd4j.create(new double[]{ 30, 40, 50 }, new int[]{ 3 });
    }
}

