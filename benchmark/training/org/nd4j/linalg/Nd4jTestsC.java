/**
 * -
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */
package org.nd4j.linalg;


import DataBuffer.Type;
import DataBuffer.Type.DOUBLE;
import DataBuffer.Type.FLOAT;
import DataBuffer.Type.HALF;
import Nd4j.EPS_THRESHOLD;
import Nd4jEnvironment.CPU_CORES_KEY;
import OpExecutioner.ProfilingMode.ALL;
import OpExecutionerUtil.Tensor1DStats;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.blas.params.MMulTranspose;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.environment.Nd4jEnvironment;
import org.nd4j.linalg.api.iter.NdIndexIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.Accumulation;
import org.nd4j.linalg.api.ops.BroadcastOp;
import org.nd4j.linalg.api.ops.DynamicCustomOp;
import org.nd4j.linalg.api.ops.Op;
import org.nd4j.linalg.api.ops.executioner.GridExecutioner;
import org.nd4j.linalg.api.ops.executioner.OpExecutionerUtil;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMin;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMin;
import org.nd4j.linalg.api.ops.impl.layers.convolution.Im2col;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.linalg.util.ArrayUtil;
import org.nd4j.linalg.util.MathUtils;


/**
 * NDArrayTests
 *
 * @author Adam Gibson
 */
@Slf4j
@RunWith(Parameterized.class)
public class Nd4jTestsC extends BaseNd4jTest {
    Type initialType;

    public Nd4jTestsC(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testArangeNegative() {
        INDArray arr = Nd4j.arange((-2), 2);
        INDArray assertion = Nd4j.create(new double[]{ -2, -1, 0, 1 });
        Assert.assertEquals(assertion, arr);
    }

    @Test
    public void testTri() {
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 0, 0 }, new double[]{ 1, 1, 1, 1, 0 }, new double[]{ 1, 1, 1, 1, 1 } });
        INDArray tri = Nd4j.tri(3, 5, 2);
        Assert.assertEquals(assertion, tri);
    }

    @Test
    public void testTriu() {
        INDArray input = Nd4j.linspace(1, 12, 12).reshape(4, 3);
        int k = -1;
        INDArray test = Nd4j.triu(input, k);
        INDArray create = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 4, 5, 6 }, new double[]{ 0, 8, 9 }, new double[]{ 0, 0, 12 } });
        Assert.assertEquals(test, create);
    }

    @Test
    public void testDiag() {
        INDArray diag = Nd4j.diag(Nd4j.linspace(1, 4, 4).reshape(4, 1));
        BaseNd4jTest.assertArrayEquals(new long[]{ 4, 4 }, diag.shape());
    }

    @Test
    public void testSoftmaxDerivativeGradient() {
        INDArray input = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray inputDup = input.dup();
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.gradient.SoftMaxDerivative(input, Nd4j.ones(2, 2), input));
        Nd4j.getExecutioner().exec(new SoftMaxDerivative(inputDup));
        Assert.assertEquals(input, inputDup);
    }

    @Test
    public void testGetRowEdgeCase() {
        INDArray orig = Nd4j.linspace(1, 300, 300).reshape('c', 100, 3);
        INDArray col = orig.getColumn(0);
        for (int i = 0; i < 100; i++) {
            INDArray row = col.getRow(i);
            INDArray rowDup = row.dup();
            double d = orig.getDouble(i, 0);
            double d2 = col.getDouble(i, 0);
            double dRowDup = rowDup.getDouble(0);
            double dRow = row.getDouble(0);
            String s = String.valueOf(i);
            Assert.assertEquals(s, d, d2, 0.0);
            Assert.assertEquals(s, d, dRowDup, 0.0);// Fails

            Assert.assertEquals(s, d, dRow, 0.0);// Fails

        }
    }

    @Test
    public void testNd4jEnvironment() {
        System.out.println(Nd4j.getExecutioner().getEnvironmentInformation());
        int manualNumCores = Integer.parseInt(Nd4j.getExecutioner().getEnvironmentInformation().get(CPU_CORES_KEY).toString());
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), manualNumCores);
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), Nd4jEnvironment.getEnvironment().getNumCores());
        System.out.println(Nd4jEnvironment.getEnvironment());
    }

    @Test
    public void testSerialization() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        INDArray arr = Nd4j.rand(1, 20);
        String temp = System.getProperty("java.io.tmpdir");
        String outPath = FilenameUtils.concat(temp, "dl4jtestserialization.bin");
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(outPath)))) {
            Nd4j.write(arr, dos);
        }
        INDArray in;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(outPath))) {
            in = Nd4j.read(dis);
        }
        INDArray inDup = in.dup();
        System.out.println(in);
        System.out.println(inDup);
        Assert.assertEquals(arr, in);// Passes:   Original array "in" is OK, but array "inDup" is not!?

        Assert.assertEquals(in, inDup);// Fails

    }

    @Test
    public void testTensorAlongDimension2() {
        INDArray array = Nd4j.create(new float[100], new long[]{ 50, 1, 2 });
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 2 }, array.slice(0, 0).shape());
    }

    @Test
    public void testIsMax() {
        INDArray arr = Nd4j.create(new double[]{ 1, 2, 4, 3 }, new long[]{ 2, 2 });
        INDArray assertion = Nd4j.create(new double[]{ 0, 0, 1, 0 }, new long[]{ 2, 2 });
        INDArray test = Nd4j.getExecutioner().exec(new IsMax(arr)).z();
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testArgMax() {
        INDArray toArgMax = Nd4j.linspace(1, 24, 24).reshape(4, 3, 2);
        INDArray argMaxZero = Nd4j.argMax(toArgMax, 0);
        INDArray argMax = Nd4j.argMax(toArgMax, 1);
        INDArray argMaxTwo = Nd4j.argMax(toArgMax, 2);
        INDArray valueArray = Nd4j.valueArrayOf(new long[]{ 4, 2 }, 2.0);
        INDArray valueArrayTwo = Nd4j.valueArrayOf(new long[]{ 3, 2 }, 3.0);
        INDArray valueArrayThree = Nd4j.valueArrayOf(new long[]{ 4, 3 }, 1.0);
        Assert.assertEquals(valueArrayTwo, argMaxZero);
        Assert.assertEquals(valueArray, argMax);
        Assert.assertEquals(valueArrayThree, argMaxTwo);
    }

    @Test
    public void testAutoBroadcastShape() {
        val assertion = new long[]{ 2, 2, 2, 5 };
        val shapeTest = Shape.broadcastOutputShape(new long[]{ 2, 1, 2, 1 }, new long[]{ 2, 1, 5 });
        BaseNd4jTest.assertArrayEquals(assertion, shapeTest);
    }

    @Test
    public void testAudoBroadcastAddMatrix() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray row = Nd4j.ones(2);
        INDArray assertion = arr.add(1.0);
        INDArray test = arr.add(row);
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testScalarOps() throws Exception {
        INDArray n = Nd4j.create(Nd4j.ones(27).data(), new long[]{ 3, 3, 3 });
        Assert.assertEquals(27.0, n.length(), 0.1);
        n.checkDimensions(n.addi(Nd4j.scalar(1.0)));
        n.checkDimensions(n.subi(Nd4j.scalar(1.0)));
        n.checkDimensions(n.muli(Nd4j.scalar(1.0)));
        n.checkDimensions(n.divi(Nd4j.scalar(1.0)));
        n = Nd4j.create(Nd4j.ones(27).data(), new long[]{ 3, 3, 3 });
        Assert.assertEquals(getFailureMessage(), 27, n.sumNumber().doubleValue(), 0.1);
        INDArray a = n.slice(2);
        Assert.assertEquals(getFailureMessage(), true, Arrays.equals(new long[]{ 3, 3 }, a.shape()));
    }

    @Test
    public void testTensorAlongDimension() {
        val shape = new long[]{ 4, 5, 7 };
        int length = ArrayUtil.prod(shape);
        INDArray arr = Nd4j.linspace(1, length, length).reshape(shape);
        int[] dim0s = new int[]{ 0, 1, 2, 0, 1, 2 };
        int[] dim1s = new int[]{ 1, 0, 0, 2, 2, 1 };
        double[] sums = new double[]{ 1350.0, 1350.0, 1582, 1582, 630, 630 };
        for (int i = 0; i < (dim0s.length); i++) {
            int firstDim = dim0s[i];
            int secondDim = dim1s[i];
            INDArray tad = arr.tensorAlongDimension(0, firstDim, secondDim);
            tad.sumNumber();
            // assertEquals("I " + i + " failed ",sums[i],tad.sumNumber().doubleValue(),1e-1);
        }
        INDArray testMem = Nd4j.create(10, 10);
    }

    @Test
    public void testMmulWithTranspose() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray arr2 = Nd4j.linspace(1, 4, 4).reshape(2, 2).transpose();
        INDArray arrTransposeAssertion = arr.transpose().mmul(arr2);
        MMulTranspose mMulTranspose = MMulTranspose.builder().transposeA(true).a(arr).b(arr2).build();
        INDArray testResult = arr.mmul(arr2, mMulTranspose);
        Assert.assertEquals(arrTransposeAssertion, testResult);
        INDArray bTransposeAssertion = arr.mmul(arr2.transpose());
        mMulTranspose = MMulTranspose.builder().transposeB(true).a(arr).b(arr2).build();
        INDArray bTest = arr.mmul(arr2, mMulTranspose);
        Assert.assertEquals(bTransposeAssertion, bTest);
    }

    @Test
    public void testGetDouble() {
        INDArray n2 = Nd4j.create(Nd4j.linspace(1, 30, 30).data(), new long[]{ 3, 5, 2 });
        INDArray swapped = n2.swapAxes(((n2.shape().length) - 1), 1);
        INDArray slice0 = swapped.slice(0).slice(1);
        INDArray assertion = Nd4j.create(new double[]{ 2, 4, 6, 8, 10 });
        Assert.assertEquals(assertion, slice0);
    }

    @Test
    public void testWriteTxt() throws Exception {
        INDArray row = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } });
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Nd4j.write(row, new DataOutputStream(bos));
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        INDArray ret = Nd4j.read(bis);
        Assert.assertEquals(row, ret);
    }

    @Test
    public void test2dMatrixOrderingSwitch() throws Exception {
        char order = Nd4j.order();
        INDArray c = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } }, 'c');
        Assert.assertEquals('c', c.ordering());
        Assert.assertEquals(order, Nd4j.order().charValue());
        INDArray f = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } }, 'f');
        Assert.assertEquals('f', f.ordering());
        Assert.assertEquals(order, Nd4j.order().charValue());
    }

    @Test
    public void testMatrix() {
        INDArray arr = Nd4j.create(new float[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
        INDArray brr = Nd4j.create(new float[]{ 5, 6 }, new long[]{ 1, 2 });
        INDArray row = arr.getRow(0);
        row.subi(brr);
        Assert.assertEquals(Nd4j.create(new double[]{ -4, -4 }), arr.getRow(0));
    }

    @Test
    public void testMMul() {
        INDArray arr = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 4, 5, 6 } });
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 14, 32 }, new double[]{ 32, 77 } });
        INDArray test = arr.mmul(arr.transpose());
        Assert.assertEquals(getFailureMessage(), assertion, test);
    }

    @Test
    public void testMmulOp() {
        INDArray arr = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 4, 5, 6 } });
        INDArray z = Nd4j.create(2, 2);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 14, 32 }, new double[]{ 32, 77 } });
        MMulTranspose mMulTranspose = MMulTranspose.builder().transposeB(true).a(arr).b(arr).build();
        DynamicCustomOp op = new org.nd4j.linalg.api.ops.impl.accum.Mmul(arr, arr, z, mMulTranspose);
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(getFailureMessage(), assertion, z);
    }

    @Test
    public void testSubiRowVector() throws Exception {
        INDArray oneThroughFour = Nd4j.linspace(1, 4, 4).reshape('c', 2, 2);
        INDArray row1 = oneThroughFour.getRow(1);
        oneThroughFour.subiRowVector(row1);
        INDArray result = Nd4j.create(new float[]{ -2, -2, 0, 0 }, new long[]{ 2, 2 });
        Assert.assertEquals(getFailureMessage(), result, oneThroughFour);
    }

    @Test
    public void testAddiRowVectorWithScalar() {
        INDArray colVector = Nd4j.create(5, 1).assign(0.0);
        INDArray scalar = Nd4j.create(1, 1).assign(0.0);
        scalar.putScalar(0, 1);
        Assert.assertEquals(scalar.getDouble(0), 1.0, 0.0);
        colVector.addiRowVector(scalar);// colVector is all zeros after this

        for (int i = 0; i < 5; i++)
            Assert.assertEquals(colVector.getDouble(i), 1.0, 0.0);

    }

    @Test
    public void testTADOnVector() {
        Nd4j.getRandom().setSeed(12345);
        INDArray rowVec = Nd4j.rand(1, 10);
        INDArray thirdElem = rowVec.tensorAlongDimension(2, 0);
        Assert.assertEquals(rowVec.getDouble(2), thirdElem.getDouble(0), 0.0);
        thirdElem.putScalar(0, 5);
        Assert.assertEquals(5, thirdElem.getDouble(0), 0.0);
        Assert.assertEquals(5, rowVec.getDouble(2), 0.0);// Both should be modified if thirdElem is a view

        // Same thing for column vector:
        INDArray colVec = Nd4j.rand(10, 1);
        thirdElem = colVec.tensorAlongDimension(2, 1);
        Assert.assertEquals(colVec.getDouble(2), thirdElem.getDouble(0), 0.0);
        thirdElem.putScalar(0, 5);
        Assert.assertEquals(5, thirdElem.getDouble(0), 0.0);
        Assert.assertEquals(5, colVec.getDouble(2), 0.0);
    }

    @Test
    public void testLength() {
        INDArray values = Nd4j.create(2, 2);
        INDArray values2 = Nd4j.create(2, 2);
        values.put(0, 0, 0);
        values2.put(0, 0, 2);
        values.put(1, 0, 0);
        values2.put(1, 0, 2);
        values.put(0, 1, 0);
        values2.put(0, 1, 0);
        values.put(1, 1, 2);
        values2.put(1, 1, 2);
        INDArray expected = Nd4j.repeat(Nd4j.scalar(2), 2).reshape(2, 1);
        Accumulation accum = Nd4j.getOpFactory().createAccum("euclidean", values, values2);
        INDArray results = Nd4j.getExecutioner().exec(accum, 1);
        Assert.assertEquals(expected, results);
    }

    @Test
    public void testBroadCasting() {
        INDArray first = Nd4j.arange(0, 3).reshape(3, 1);
        INDArray ret = first.broadcast(3, 4);
        INDArray testRet = Nd4j.create(new double[][]{ new double[]{ 0, 0, 0, 0 }, new double[]{ 1, 1, 1, 1 }, new double[]{ 2, 2, 2, 2 } });
        Assert.assertEquals(testRet, ret);
        INDArray r = Nd4j.arange(0, 4).reshape(1, 4);
        INDArray r2 = r.broadcast(4, 4);
        INDArray testR2 = Nd4j.create(new double[][]{ new double[]{ 0, 1, 2, 3 }, new double[]{ 0, 1, 2, 3 }, new double[]{ 0, 1, 2, 3 }, new double[]{ 0, 1, 2, 3 } });
        Assert.assertEquals(testR2, r2);
    }

    @Test
    public void testGetColumns() {
        INDArray matrix = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray matrixGet = matrix.getColumns(new int[]{ 1, 2 });
        INDArray matrixAssertion = Nd4j.create(new double[][]{ new double[]{ 2, 3 }, new double[]{ 5, 6 } });
        Assert.assertEquals(matrixAssertion, matrixGet);
    }

    @Test
    public void testSort() throws Exception {
        INDArray toSort = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray ascending = Nd4j.sort(toSort.dup(), 1, true);
        // rows already already sorted
        Assert.assertEquals(toSort, ascending);
        INDArray columnSorted = Nd4j.create(new float[]{ 2, 1, 4, 3 }, new long[]{ 2, 2 });
        INDArray sorted = Nd4j.sort(toSort.dup(), 1, false);
        Assert.assertEquals(columnSorted, sorted);
    }

    @Test
    public void testSortRows() {
        int nRows = 10;
        int nCols = 5;
        Random r = new Random(12345);
        for (int i = 0; i < nCols; i++) {
            INDArray in = Nd4j.linspace(1, (nRows * nCols), (nRows * nCols)).reshape(nRows, nCols);
            List<Integer> order = new ArrayList<>(nRows);
            // in.row(order(i)) should end up as out.row(i) - ascending
            // in.row(order(i)) should end up as out.row(nRows-j-1) - descending
            for (int j = 0; j < nRows; j++)
                order.add(j);

            Collections.shuffle(order, r);
            for (int j = 0; j < nRows; j++)
                in.putScalar(new long[]{ j, i }, order.get(j));

            INDArray outAsc = Nd4j.sortRows(in, i, true);
            INDArray outDesc = Nd4j.sortRows(in, i, false);
            System.out.println(("outDesc: " + (Arrays.toString(outAsc.data().asFloat()))));
            for (int j = 0; j < nRows; j++) {
                Assert.assertEquals(outAsc.getDouble(j, i), j, 0.1);
                int origRowIdxAsc = order.indexOf(j);
                Assert.assertTrue(outAsc.getRow(j).equals(in.getRow(origRowIdxAsc)));
                Assert.assertEquals(((nRows - j) - 1), outDesc.getDouble(j, i), 0.001F);
                int origRowIdxDesc = order.indexOf(((nRows - j) - 1));
                Assert.assertTrue(outDesc.getRow(j).equals(in.getRow(origRowIdxDesc)));
            }
        }
    }

    @Test
    public void testToFlattenedOrder() {
        INDArray concatC = Nd4j.linspace(1, 4, 4).reshape('c', 2, 2);
        INDArray concatF = Nd4j.create(new long[]{ 2, 2 }, 'f');
        concatF.assign(concatC);
        INDArray assertionC = Nd4j.create(new double[]{ 1, 2, 3, 4, 1, 2, 3, 4 });
        INDArray testC = Nd4j.toFlattened('c', concatC, concatF);
        Assert.assertEquals(assertionC, testC);
        INDArray test = Nd4j.toFlattened('f', concatC, concatF);
        INDArray assertion = Nd4j.create(new double[]{ 1, 3, 2, 4, 1, 3, 2, 4 });
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testZero() {
        Nd4j.ones(11).sumNumber();
        Nd4j.ones(12).sumNumber();
        Nd4j.ones(2).sumNumber();
    }

    @Test
    public void testSumNumberRepeatability() {
        INDArray arr = Nd4j.ones(1, 450).reshape('c', 150, 3);
        double first = arr.sumNumber().doubleValue();
        double assertion = 450;
        Assert.assertEquals(assertion, first, 0.1);
        for (int i = 0; i < 50; i++) {
            double second = arr.sumNumber().doubleValue();
            Assert.assertEquals(assertion, second, 0.1);
            Assert.assertEquals(String.valueOf(i), first, second, 0.01);
        }
    }

    @Test
    public void testToFlattened2() {
        int rows = 3;
        int cols = 4;
        int dim2 = 5;
        int dim3 = 6;
        int length2d = rows * cols;
        int length3d = (rows * cols) * dim2;
        int length4d = ((rows * cols) * dim2) * dim3;
        INDArray c2d = Nd4j.linspace(1, length2d, length2d).reshape('c', rows, cols);
        INDArray f2d = Nd4j.create(new long[]{ rows, cols }, 'f').assign(c2d).addi(0.1);
        INDArray c3d = Nd4j.linspace(1, length3d, length3d).reshape('c', rows, cols, dim2);
        INDArray f3d = Nd4j.create(new long[]{ rows, cols, dim2 }).assign(c3d).addi(0.3);
        c3d.addi(0.2);
        INDArray c4d = Nd4j.linspace(1, length4d, length4d).reshape('c', rows, cols, dim2, dim3);
        INDArray f4d = Nd4j.create(new long[]{ rows, cols, dim2, dim3 }).assign(c4d).addi(0.3);
        c4d.addi(0.4);
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('c', c2d, f2d), Nd4j.toFlattened('c', c2d, f2d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('f', c2d, f2d), Nd4j.toFlattened('f', c2d, f2d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('c', f2d, c2d), Nd4j.toFlattened('c', f2d, c2d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('f', f2d, c2d), Nd4j.toFlattened('f', f2d, c2d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('c', c3d, f3d), Nd4j.toFlattened('c', c3d, f3d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('f', c3d, f3d), Nd4j.toFlattened('f', c3d, f3d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('c', c2d, f2d, c3d, f3d), Nd4j.toFlattened('c', c2d, f2d, c3d, f3d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('f', c2d, f2d, c3d, f3d), Nd4j.toFlattened('f', c2d, f2d, c3d, f3d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('c', c4d, f4d), Nd4j.toFlattened('c', c4d, f4d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('f', c4d, f4d), Nd4j.toFlattened('f', c4d, f4d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('c', c2d, f2d, c3d, f3d, c4d, f4d), Nd4j.toFlattened('c', c2d, f2d, c3d, f3d, c4d, f4d));
        Assert.assertEquals(Nd4jTestsC.toFlattenedViaIterator('f', c2d, f2d, c3d, f3d, c4d, f4d), Nd4j.toFlattened('f', c2d, f2d, c3d, f3d, c4d, f4d));
    }

    @Test
    public void testToFlattenedOnViews() {
        int rows = 8;
        int cols = 8;
        int dim2 = 4;
        int length = rows * cols;
        int length3d = (rows * cols) * dim2;
        INDArray first = Nd4j.linspace(1, length, length).reshape('c', rows, cols);
        INDArray second = Nd4j.create(new long[]{ rows, cols }, 'f').assign(first);
        INDArray third = Nd4j.linspace(1, length3d, length3d).reshape('c', rows, cols, dim2);
        first.addi(0.1);
        second.addi(0.2);
        third.addi(0.3);
        first = first.get(NDArrayIndex.interval(4, 8), NDArrayIndex.interval(0, 2, 8));
        second = second.get(NDArrayIndex.interval(3, 7), NDArrayIndex.all());
        third = third.permute(0, 2, 1);
        INDArray cAssertion = Nd4j.create(new double[]{ 33.1, 35.1, 37.1, 39.1, 41.1, 43.1, 45.1, 47.1, 49.1, 51.1, 53.1, 55.1, 57.1, 59.1, 61.1, 63.1, 25.2, 26.2, 27.2, 28.2, 29.2, 30.2, 31.2, 32.2, 33.2, 34.2, 35.2, 36.2, 37.2, 38.2, 39.2, 40.2, 41.2, 42.2, 43.2, 44.2, 45.2, 46.2, 47.2, 48.2, 49.2, 50.2, 51.2, 52.2, 53.2, 54.2, 55.2, 56.2, 1.3, 5.3, 9.3, 13.3, 17.3, 21.3, 25.3, 29.3, 2.3, 6.3, 10.3, 14.3, 18.3, 22.3, 26.3, 30.3, 3.3, 7.3, 11.3, 15.3, 19.3, 23.3, 27.3, 31.3, 4.3, 8.3, 12.3, 16.3, 20.3, 24.3, 28.3, 32.3, 33.3, 37.3, 41.3, 45.3, 49.3, 53.3, 57.3, 61.3, 34.3, 38.3, 42.3, 46.3, 50.3, 54.3, 58.3, 62.3, 35.3, 39.3, 43.3, 47.3, 51.3, 55.3, 59.3, 63.3, 36.3, 40.3, 44.3, 48.3, 52.3, 56.3, 60.3, 64.3, 65.3, 69.3, 73.3, 77.3, 81.3, 85.3, 89.3, 93.3, 66.3, 70.3, 74.3, 78.3, 82.3, 86.3, 90.3, 94.3, 67.3, 71.3, 75.3, 79.3, 83.3, 87.3, 91.3, 95.3, 68.3, 72.3, 76.3, 80.3, 84.3, 88.3, 92.3, 96.3, 97.3, 101.3, 105.3, 109.3, 113.3, 117.3, 121.3, 125.3, 98.3, 102.3, 106.3, 110.3, 114.3, 118.3, 122.3, 126.3, 99.3, 103.3, 107.3, 111.3, 115.3, 119.3, 123.3, 127.3, 100.3, 104.3, 108.3, 112.3, 116.3, 120.3, 124.3, 128.3, 129.3, 133.3, 137.3, 141.3, 145.3, 149.3, 153.3, 157.3, 130.3, 134.3, 138.3, 142.3, 146.3, 150.3, 154.3, 158.3, 131.3, 135.3, 139.3, 143.3, 147.3, 151.3, 155.3, 159.3, 132.3, 136.3, 140.3, 144.3, 148.3, 152.3, 156.3, 160.3, 161.3, 165.3, 169.3, 173.3, 177.3, 181.3, 185.3, 189.3, 162.3, 166.3, 170.3, 174.3, 178.3, 182.3, 186.3, 190.3, 163.3, 167.3, 171.3, 175.3, 179.3, 183.3, 187.3, 191.3, 164.3, 168.3, 172.3, 176.3, 180.3, 184.3, 188.3, 192.3, 193.3, 197.3, 201.3, 205.3, 209.3, 213.3, 217.3, 221.3, 194.3, 198.3, 202.3, 206.3, 210.3, 214.3, 218.3, 222.3, 195.3, 199.3, 203.3, 207.3, 211.3, 215.3, 219.3, 223.3, 196.3, 200.3, 204.3, 208.3, 212.3, 216.3, 220.3, 224.3, 225.3, 229.3, 233.3, 237.3, 241.3, 245.3, 249.3, 253.3, 226.3, 230.3, 234.3, 238.3, 242.3, 246.3, 250.3, 254.3, 227.3, 231.3, 235.3, 239.3, 243.3, 247.3, 251.3, 255.3, 228.3, 232.3, 236.3, 240.3, 244.3, 248.3, 252.3, 256.3 });
        INDArray fAssertion = Nd4j.create(new double[]{ 33.1, 41.1, 49.1, 57.1, 35.1, 43.1, 51.1, 59.1, 37.1, 45.1, 53.1, 61.1, 39.1, 47.1, 55.1, 63.1, 25.2, 33.2, 41.2, 49.2, 26.2, 34.2, 42.2, 50.2, 27.2, 35.2, 43.2, 51.2, 28.2, 36.2, 44.2, 52.2, 29.2, 37.2, 45.2, 53.2, 30.2, 38.2, 46.2, 54.2, 31.2, 39.2, 47.2, 55.2, 32.2, 40.2, 48.2, 56.2, 1.3, 33.3, 65.3, 97.3, 129.3, 161.3, 193.3, 225.3, 2.3, 34.3, 66.3, 98.3, 130.3, 162.3, 194.3, 226.3, 3.3, 35.3, 67.3, 99.3, 131.3, 163.3, 195.3, 227.3, 4.3, 36.3, 68.3, 100.3, 132.3, 164.3, 196.3, 228.3, 5.3, 37.3, 69.3, 101.3, 133.3, 165.3, 197.3, 229.3, 6.3, 38.3, 70.3, 102.3, 134.3, 166.3, 198.3, 230.3, 7.3, 39.3, 71.3, 103.3, 135.3, 167.3, 199.3, 231.3, 8.3, 40.3, 72.3, 104.3, 136.3, 168.3, 200.3, 232.3, 9.3, 41.3, 73.3, 105.3, 137.3, 169.3, 201.3, 233.3, 10.3, 42.3, 74.3, 106.3, 138.3, 170.3, 202.3, 234.3, 11.3, 43.3, 75.3, 107.3, 139.3, 171.3, 203.3, 235.3, 12.3, 44.3, 76.3, 108.3, 140.3, 172.3, 204.3, 236.3, 13.3, 45.3, 77.3, 109.3, 141.3, 173.3, 205.3, 237.3, 14.3, 46.3, 78.3, 110.3, 142.3, 174.3, 206.3, 238.3, 15.3, 47.3, 79.3, 111.3, 143.3, 175.3, 207.3, 239.3, 16.3, 48.3, 80.3, 112.3, 144.3, 176.3, 208.3, 240.3, 17.3, 49.3, 81.3, 113.3, 145.3, 177.3, 209.3, 241.3, 18.3, 50.3, 82.3, 114.3, 146.3, 178.3, 210.3, 242.3, 19.3, 51.3, 83.3, 115.3, 147.3, 179.3, 211.3, 243.3, 20.3, 52.3, 84.3, 116.3, 148.3, 180.3, 212.3, 244.3, 21.3, 53.3, 85.3, 117.3, 149.3, 181.3, 213.3, 245.3, 22.3, 54.3, 86.3, 118.3, 150.3, 182.3, 214.3, 246.3, 23.3, 55.3, 87.3, 119.3, 151.3, 183.3, 215.3, 247.3, 24.3, 56.3, 88.3, 120.3, 152.3, 184.3, 216.3, 248.3, 25.3, 57.3, 89.3, 121.3, 153.3, 185.3, 217.3, 249.3, 26.3, 58.3, 90.3, 122.3, 154.3, 186.3, 218.3, 250.3, 27.3, 59.3, 91.3, 123.3, 155.3, 187.3, 219.3, 251.3, 28.3, 60.3, 92.3, 124.3, 156.3, 188.3, 220.3, 252.3, 29.3, 61.3, 93.3, 125.3, 157.3, 189.3, 221.3, 253.3, 30.3, 62.3, 94.3, 126.3, 158.3, 190.3, 222.3, 254.3, 31.3, 63.3, 95.3, 127.3, 159.3, 191.3, 223.3, 255.3, 32.3, 64.3, 96.3, 128.3, 160.3, 192.3, 224.3, 256.3 });
        Assert.assertEquals(cAssertion, Nd4j.toFlattened('c', first, second, third));
        Assert.assertEquals(fAssertion, Nd4j.toFlattened('f', first, second, third));
    }

    @Test
    public void testIsMax2() {
        // Tests: full buffer...
        // 1d
        INDArray arr1 = Nd4j.create(new double[]{ 1, 2, 3, 1 });
        Nd4j.getExecutioner().execAndReturn(new IsMax(arr1));
        INDArray exp1 = Nd4j.create(new double[]{ 0, 0, 1, 0 });
        Assert.assertEquals(exp1, arr1);
        arr1 = Nd4j.create(new double[]{ 1, 2, 3, 1 });
        INDArray result = Nd4j.zeros(4);
        Nd4j.getExecutioner().execAndReturn(new IsMax(arr1, result));
        Assert.assertEquals(Nd4j.create(new double[]{ 1, 2, 3, 1 }), arr1);
        Assert.assertEquals(exp1, result);
        // 2d
        INDArray arr2d = Nd4j.create(new double[][]{ new double[]{ 0, 1, 2 }, new double[]{ 2, 9, 1 } });
        INDArray exp2d = Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 0, 1, 0 } });
        INDArray f = arr2d.dup('f');
        INDArray out2dc = Nd4j.getExecutioner().execAndReturn(new IsMax(arr2d.dup('c')));
        INDArray out2df = Nd4j.getExecutioner().execAndReturn(new IsMax(arr2d.dup('f')));
        Assert.assertEquals(exp2d, out2dc);
        Assert.assertEquals(exp2d, out2df);
    }

    @Test
    public void testToFlattened3() {
        INDArray inC1 = Nd4j.create(new long[]{ 10, 100 }, 'c');
        INDArray inC2 = Nd4j.create(new long[]{ 1, 100 }, 'c');
        INDArray inF1 = Nd4j.create(new long[]{ 10, 100 }, 'f');
        // INDArray inF1 = Nd4j.create(new long[]{784,1000},'f');
        INDArray inF2 = Nd4j.create(new long[]{ 1, 100 }, 'f');
        Nd4j.toFlattened('f', inF1);// ok

        Nd4j.toFlattened('f', inF2);// ok

        Nd4j.toFlattened('f', inC1);// crash

        Nd4j.toFlattened('f', inC2);// crash

        Nd4j.toFlattened('c', inF1);// crash on shape [784,1000]. infinite loop on shape [10,100]

        Nd4j.toFlattened('c', inF2);// ok

        Nd4j.toFlattened('c', inC1);// ok

        Nd4j.toFlattened('c', inC2);// ok

    }

    @Test
    public void testIsMaxEqualValues() {
        // Assumption here: should only have a 1 for *first* maximum value, if multiple values are exactly equal
        // [1 1 1] -> [1 0 0]
        // Loop to double check against any threading weirdness...
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(Nd4j.create(new double[]{ 1, 0, 0 }), Nd4j.getExecutioner().execAndReturn(new IsMax(Nd4j.ones(3))));
        }
        // [0 0 0 2 2 0] -> [0 0 0 1 0 0]
        Assert.assertEquals(Nd4j.create(new double[]{ 0, 0, 0, 1, 0, 0 }), Nd4j.getExecutioner().execAndReturn(new IsMax(Nd4j.create(new double[]{ 0, 0, 0, 2, 2, 0 }))));
        // [0 2]    [0 1]
        // [2 1] -> [0 0]
        INDArray orig = Nd4j.create(new double[][]{ new double[]{ 0, 2 }, new double[]{ 2, 1 } });
        INDArray exp = Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 0, 0 } });
        INDArray outc = Nd4j.getExecutioner().execAndReturn(new IsMax(orig.dup('c')));
        INDArray outf = Nd4j.getExecutioner().execAndReturn(new IsMax(orig.dup('f')));
        Assert.assertEquals(exp, outc);
        Assert.assertEquals(exp, outf);
    }

    @Test
    public void testIsMaxAlongDimension() {
        // 1d: row vector
        INDArray orig = Nd4j.create(new double[]{ 1, 2, 3, 1 });
        INDArray alongDim0 = Nd4j.getExecutioner().execAndReturn(new IsMax(orig.dup(), 0));
        INDArray alongDim1 = Nd4j.getExecutioner().execAndReturn(new IsMax(orig.dup(), 1));
        INDArray expAlong0 = Nd4j.ones(4);
        INDArray expAlong1 = Nd4j.create(new double[]{ 0, 0, 1, 0 });
        Assert.assertEquals(expAlong0, alongDim0);
        Assert.assertEquals(expAlong1, alongDim1);
        // 1d: col vector
        System.out.println("----------------------------------");
        INDArray col = Nd4j.create(new double[]{ 1, 2, 3, 1 }, new long[]{ 4, 1 });
        INDArray alongDim0col = Nd4j.getExecutioner().execAndReturn(new IsMax(col.dup(), 0));
        INDArray alongDim1col = Nd4j.getExecutioner().execAndReturn(new IsMax(col.dup(), 1));
        INDArray expAlong0col = Nd4j.create(new double[]{ 0, 0, 1, 0 }, new long[]{ 4, 1 });
        INDArray expAlong1col = Nd4j.ones(new long[]{ 4, 1 });
        Assert.assertEquals(expAlong1col, alongDim1col);
        Assert.assertEquals(expAlong0col, alongDim0col);
        /* if (blockIdx.x == 0) {
        printf("original Z shape: \n");
        shape::printShapeInfoLinear(zShapeInfo);

        printf("Target dimension: [%i], dimensionLength: [%i]\n", dimension[0], dimensionLength);

        printf("TAD shape: \n");
        shape::printShapeInfoLinear(tad->tadOnlyShapeInfo);
        }
         */
        // 2d:
        // [1 0 2]
        // [2 3 1]
        // Along dim 0:
        // [0 0 1]
        // [1 1 0]
        // Along dim 1:
        // [0 0 1]
        // [0 1 0]
        System.out.println("---------------------");
        INDArray orig2d = Nd4j.create(new double[][]{ new double[]{ 1, 0, 2 }, new double[]{ 2, 3, 1 } });
        INDArray alongDim0c_2d = Nd4j.getExecutioner().execAndReturn(new IsMax(orig2d.dup('c'), 0));
        INDArray alongDim0f_2d = Nd4j.getExecutioner().execAndReturn(new IsMax(orig2d.dup('f'), 0));
        INDArray alongDim1c_2d = Nd4j.getExecutioner().execAndReturn(new IsMax(orig2d.dup('c'), 1));
        INDArray alongDim1f_2d = Nd4j.getExecutioner().execAndReturn(new IsMax(orig2d.dup('f'), 1));
        INDArray expAlong0_2d = Nd4j.create(new double[][]{ new double[]{ 0, 0, 1 }, new double[]{ 1, 1, 0 } });
        INDArray expAlong1_2d = Nd4j.create(new double[][]{ new double[]{ 0, 0, 1 }, new double[]{ 0, 1, 0 } });
        Assert.assertEquals(expAlong0_2d, alongDim0c_2d);
        Assert.assertEquals(expAlong0_2d, alongDim0f_2d);
        Assert.assertEquals(expAlong1_2d, alongDim1c_2d);
        Assert.assertEquals(expAlong1_2d, alongDim1f_2d);
    }

    @Test
    public void testIMaxSingleDim1() {
        INDArray orig2d = Nd4j.create(new double[][]{ new double[]{ 1, 0, 2 }, new double[]{ 2, 3, 1 } });
        INDArray result = Nd4j.argMax(orig2d.dup('c'), 0);
        System.out.println(("IMAx result: " + result));
    }

    @Test
    public void testIsMaxSingleDim1() {
        INDArray orig2d = Nd4j.create(new double[][]{ new double[]{ 1, 0, 2 }, new double[]{ 2, 3, 1 } });
        INDArray alongDim0c_2d = Nd4j.getExecutioner().execAndReturn(new IsMax(orig2d.dup('c'), 0));
        INDArray expAlong0_2d = Nd4j.create(new double[][]{ new double[]{ 0, 0, 1 }, new double[]{ 1, 1, 0 } });
        System.out.println(("Original shapeInfo: " + (orig2d.dup('c').shapeInfoDataBuffer())));
        System.out.println(("Expected: " + (Arrays.toString(expAlong0_2d.data().asFloat()))));
        System.out.println(("Actual: " + (Arrays.toString(alongDim0c_2d.data().asFloat()))));
        Assert.assertEquals(expAlong0_2d, alongDim0c_2d);
    }

    @Test
    public void testBroadcastRepeated() {
        INDArray z = Nd4j.create(1, 4, 4, 3);
        INDArray bias = Nd4j.create(1, 3);
        BroadcastOp op = new BroadcastAddOp(z, bias, z, 3);
        Nd4j.getExecutioner().exec(op);
        System.out.println("First: OK");
        // OK at this point: executes successfully
        z = Nd4j.create(1, 4, 4, 3);
        bias = Nd4j.create(1, 3);
        op = new BroadcastAddOp(z, bias, z, 3);
        Nd4j.getExecutioner().exec(op);// Crashing here, when we are doing exactly the same thing as before...

        System.out.println("Second: OK");
    }

    @Test
    public void testTadShape() {
        INDArray arr = Nd4j.linspace(1, 12, 12).reshape(4, 3, 1, 1);
        INDArray javaTad = arr.javaTensorAlongDimension(0, 0, 2, 3);
        BaseNd4jTest.assertArrayEquals(new long[]{ 4, 1, 1 }, javaTad.shape());
        INDArray tad = arr.tensorAlongDimension(0, 0, 2, 3);
        BaseNd4jTest.assertArrayEquals(javaTad.shapeInfoDataBuffer().asLong(), tad.shapeInfoDataBuffer().asLong());
        Assert.assertEquals(javaTad.shapeInfoDataBuffer(), tad.shapeInfoDataBuffer());
    }

    @Test
    public void testSoftmaxDerivative() {
        INDArray input = Nd4j.create(new double[]{ -1.07, -0.01, 0.45, 0.95, 0.45, 0.16, 0.2, 0.8, 0.89, 0.25 }).transpose();
        INDArray output = Nd4j.create(10, 1);
        Nd4j.getExecutioner().exec(new SoftMaxDerivative(input, output));
    }

    @Test
    public void testVStackDifferentOrders() {
        INDArray expected = Nd4j.linspace(1, 9, 9).reshape('c', 3, 3);
        for (char order : new char[]{ 'c', 'f' }) {
            System.out.println(order);
            Nd4j.factory().setOrder(order);
            INDArray arr1 = Nd4j.linspace(1, 6, 6).reshape('c', 2, 3);
            INDArray arr2 = Nd4j.linspace(7, 9, 3).reshape('c', 1, 3);
            INDArray merged = Nd4j.vstack(arr1, arr2);
            System.out.println(merged);
            System.out.println(expected);
            Assert.assertEquals(expected, merged);
        }
    }

    @Test
    public void testVStackEdgeCase() {
        INDArray arr = Nd4j.linspace(1, 4, 4);
        INDArray vstacked = Nd4j.vstack(arr);
        Assert.assertEquals(arr, vstacked);
    }

    @Test
    public void testEps3() {
        INDArray first = Nd4j.linspace(1, 10, 10);
        INDArray second = Nd4j.linspace(20, 30, 10);
        INDArray expAllZeros = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.comparison.Eps(first, second, Nd4j.create(10), 10));
        INDArray expAllOnes = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.comparison.Eps(first, first, Nd4j.create(10), 10));
        System.out.println(expAllZeros);
        System.out.println(expAllOnes);
        Assert.assertEquals(0, expAllZeros.sumNumber().doubleValue(), 0.0);
        Assert.assertEquals(10, expAllOnes.sumNumber().doubleValue(), 0.0);
    }

    @Test
    public void testIsMaxAlongDimensionSimple() {
        // Simple test: when doing IsMax along a dimension, we expect all values to be either 0 or 1
        // Do IsMax along dims 0&1 for rank 2, along 0,1&2 for rank 3, etc
        for (int rank = 2; rank <= 6; rank++) {
            int[] shape = new int[rank];
            for (int i = 0; i < rank; i++)
                shape[i] = 2;

            int length = ArrayUtil.prod(shape);
            for (int alongDimension = 0; alongDimension < rank; alongDimension++) {
                System.out.println((((((("Testing rank " + rank) + " along dimension ") + alongDimension) + ", (shape=") + (Arrays.toString(shape))) + ")"));
                INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', shape);
                INDArray arrF = arrC.dup('f');
                Nd4j.getExecutioner().execAndReturn(new IsMax(arrC, alongDimension));
                Nd4j.getExecutioner().execAndReturn(new IsMax(arrF, alongDimension));
                double[] cBuffer = arrC.data().asDouble();
                double[] fBuffer = arrF.data().asDouble();
                for (int i = 0; i < length; i++) {
                    Assert.assertTrue(((((((((("c buffer value at [" + i) + "]=") + (cBuffer[i])) + ", expected 0 or 1; dimension = ") + alongDimension) + ", rank = ") + rank) + ", shape=") + (Arrays.toString(shape))), (((cBuffer[i]) == 0.0) || ((cBuffer[i]) == 1.0)));
                }
                for (int i = 0; i < length; i++) {
                    Assert.assertTrue(((((((((("f buffer value at [" + i) + "]=") + (fBuffer[i])) + ", expected 0 or 1; dimension = ") + alongDimension) + ", rank = ") + rank) + ", shape=") + (Arrays.toString(shape))), (((fBuffer[i]) == 0.0) || ((fBuffer[i]) == 1.0)));
                }
            }
        }
    }

    @Test
    public void testSortColumns() {
        int nRows = 5;
        int nCols = 10;
        Random r = new Random(12345);
        for (int i = 0; i < nRows; i++) {
            INDArray in = Nd4j.rand(new long[]{ nRows, nCols });
            List<Integer> order = new ArrayList<>(nRows);
            for (int j = 0; j < nCols; j++)
                order.add(j);

            Collections.shuffle(order, r);
            for (int j = 0; j < nCols; j++)
                in.putScalar(new long[]{ i, j }, order.get(j));

            INDArray outAsc = Nd4j.sortColumns(in, i, true);
            INDArray outDesc = Nd4j.sortColumns(in, i, false);
            for (int j = 0; j < nCols; j++) {
                Assert.assertTrue(((outAsc.getDouble(i, j)) == j));
                int origColIdxAsc = order.indexOf(j);
                Assert.assertTrue(outAsc.getColumn(j).equals(in.getColumn(origColIdxAsc)));
                Assert.assertTrue(((outDesc.getDouble(i, j)) == ((nCols - j) - 1)));
                int origColIdxDesc = order.indexOf(((nCols - j) - 1));
                Assert.assertTrue(outDesc.getColumn(j).equals(in.getColumn(origColIdxDesc)));
            }
        }
    }

    @Test
    public void testAddVectorWithOffset() throws Exception {
        INDArray oneThroughFour = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray row1 = oneThroughFour.getRow(1);
        row1.addi(1);
        INDArray result = Nd4j.create(new float[]{ 1, 2, 4, 5 }, new long[]{ 2, 2 });
        Assert.assertEquals(getFailureMessage(), result, oneThroughFour);
    }

    @Test
    public void testLinearViewGetAndPut() throws Exception {
        INDArray test = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray linear = test.linearView();
        linear.putScalar(2, 6);
        linear.putScalar(3, 7);
        Assert.assertEquals(getFailureMessage(), 6, linear.getFloat(2), 0.1);
        Assert.assertEquals(getFailureMessage(), 7, linear.getFloat(3), 0.1);
    }

    @Test
    public void testRowVectorGemm() {
        INDArray linspace = Nd4j.linspace(1, 4, 4);
        INDArray other = Nd4j.linspace(1, 16, 16).reshape(4, 4);
        INDArray result = linspace.mmul(other);
        INDArray assertion = Nd4j.create(new double[]{ 90, 100, 110, 120 });
        Assert.assertEquals(assertion, result);
    }

    @Test
    public void testGemmStrided() {
        for (val x : new int[]{ 5, 1 }) {
            List<Pair<INDArray, String>> la = NDArrayCreationUtil.getAllTestMatricesWithShape(5, x, 12345);
            List<Pair<INDArray, String>> lb = NDArrayCreationUtil.getAllTestMatricesWithShape(x, 4, 12345);
            for (int i = 0; i < (la.size()); i++) {
                for (int j = 0; j < (lb.size()); j++) {
                    String msg = (((("x=" + x) + ", i=") + i) + ", j=") + j;
                    INDArray a = la.get(i).getFirst();
                    INDArray b = lb.get(i).getFirst();
                    INDArray result1 = Nd4j.createUninitialized(5, 4);
                    INDArray result2 = Nd4j.createUninitialized(5, 4);
                    INDArray result3 = Nd4j.createUninitialized(5, 4);
                    Nd4j.gemm(a.dup('c'), b.dup('c'), result1, false, false, 1.0, 0.0);
                    Nd4j.gemm(a.dup('f'), b.dup('f'), result2, false, false, 1.0, 0.0);
                    Nd4j.gemm(a, b, result3, false, false, 1.0, 0.0);
                    Assert.assertEquals(msg, result1, result2);
                    Assert.assertEquals(msg, result1, result3);// Fails here

                }
            }
        }
    }

    @Test
    public void testMultiSum() {
        /**
         * ([[[ 0.,  1.],
         * [ 2.,  3.]],
         *
         * [[ 4.,  5.],
         * [ 6.,  7.]]])
         *
         * [0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0]
         *
         *
         * Rank: 3,Offset: 0
         * Order: c shape: [2,2,2], stride: [4,2,1]
         */
        /*  */
        INDArray arr = Nd4j.linspace(0, 7, 8).reshape('c', 2, 2, 2);
        /* [0.0,4.0,2.0,6.0,1.0,5.0,3.0,7.0]

        Rank: 3,Offset: 0
        Order: f shape: [2,2,2], stride: [1,2,4]
         */
        INDArray arrF = Nd4j.create(new long[]{ 2, 2, 2 }, 'f').assign(arr);
        Assert.assertEquals(arr, arrF);
        // 0,2,4,6 and 1,3,5,7
        Assert.assertEquals(Nd4j.create(new double[]{ 12, 16 }), arr.sum(0, 1));
        // 0,1,4,5 and 2,3,6,7
        Assert.assertEquals(Nd4j.create(new double[]{ 10, 18 }), arr.sum(0, 2));
        // 0,2,4,6 and 1,3,5,7
        Assert.assertEquals(Nd4j.create(new double[]{ 12, 16 }), arrF.sum(0, 1));
        // 0,1,4,5 and 2,3,6,7
        Assert.assertEquals(Nd4j.create(new double[]{ 10, 18 }), arrF.sum(0, 2));
        // 0,1,2,3 and 4,5,6,7
        Assert.assertEquals(Nd4j.create(new double[]{ 6, 22 }), arr.sum(1, 2));
        // 0,1,2,3 and 4,5,6,7
        Assert.assertEquals(Nd4j.create(new double[]{ 6, 22 }), arrF.sum(1, 2));
        double[] data = new double[]{ 10, 26, 42 };
        INDArray assertion = Nd4j.create(data);
        for (int i = 0; i < (data.length); i++) {
            Assert.assertEquals(data[i], assertion.getDouble(i), 0.1);
        }
        INDArray twoTwoByThree = Nd4j.linspace(1, 12, 12).reshape('f', 2, 2, 3);
        INDArray multiSum = twoTwoByThree.sum(0, 1);
        Assert.assertEquals(assertion, multiSum);
    }

    @Test
    public void testSum2dv2() {
        INDArray in = Nd4j.linspace(1, 8, 8).reshape('c', 2, 2, 2);
        val dims = new int[][]{ new int[]{ 0, 1 }, new int[]{ 1, 0 }, new int[]{ 0, 2 }, new int[]{ 2, 0 }, new int[]{ 1, 2 }, new int[]{ 2, 1 } };
        double[][] exp = new double[][]{ new double[]{ 16, 20 }, new double[]{ 16, 20 }, new double[]{ 14, 22 }, new double[]{ 14, 22 }, new double[]{ 10, 26 }, new double[]{ 10, 26 } };
        System.out.println("dims\texpected\t\tactual");
        for (int i = 0; i < (dims.length); i++) {
            val d = dims[i];
            double[] e = exp[i];
            INDArray out = in.sum(d);
            System.out.println((((((Arrays.toString(d)) + "\t") + (Arrays.toString(e))) + "\t") + out));
            Assert.assertEquals(Nd4j.create(e, out.shape()), out);
        }
    }

    // Passes on 3.9:
    @Test
    public void testSum3Of4_2222() {
        int[] shape = new int[]{ 2, 2, 2, 2 };
        int length = ArrayUtil.prod(shape);
        INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', shape);
        INDArray arrF = Nd4j.create(arrC.shape()).assign(arrC);
        int[][] dimsToSum = new int[][]{ new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 2, 3 }, new int[]{ 1, 2, 3 } };
        double[][] expD = new double[][]{ new double[]{ 64, 72 }, new double[]{ 60, 76 }, new double[]{ 52, 84 }, new double[]{ 36, 100 } };
        for (int i = 0; i < (dimsToSum.length); i++) {
            int[] d = dimsToSum[i];
            INDArray outC = arrC.sum(d);
            INDArray outF = arrF.sum(d);
            INDArray exp = Nd4j.create(expD[i], outC.shape());
            Assert.assertEquals(exp, outC);
            Assert.assertEquals(exp, outF);
            System.out.println((((((Arrays.toString(d)) + "\t") + outC) + "\t") + outF));
        }
    }

    @Test
    public void testBroadcast1d() {
        int[] shape = new int[]{ 4, 3, 2 };
        int[] toBroadcastDims = new int[]{ 0, 1, 2 };
        int[][] toBroadcastShapes = new int[][]{ new int[]{ 1, 4 }, new int[]{ 1, 3 }, new int[]{ 1, 2 } };
        // Expected result values in buffer: c order, need to reshape to {4,3,2}. Values taken from 0.4-rc3.8
        double[][] expFlat = new double[][]{ new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0 }, new double[]{ 1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0 }, new double[]{ 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0 } };
        double[][] expLinspaced = new double[][]{ new double[]{ 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0 }, new double[]{ 2.0, 3.0, 5.0, 6.0, 8.0, 9.0, 8.0, 9.0, 11.0, 12.0, 14.0, 15.0, 14.0, 15.0, 17.0, 18.0, 20.0, 21.0, 20.0, 21.0, 23.0, 24.0, 26.0, 27.0 }, new double[]{ 2.0, 4.0, 4.0, 6.0, 6.0, 8.0, 8.0, 10.0, 10.0, 12.0, 12.0, 14.0, 14.0, 16.0, 16.0, 18.0, 18.0, 20.0, 20.0, 22.0, 22.0, 24.0, 24.0, 26.0 } };
        for (int i = 0; i < (toBroadcastDims.length); i++) {
            int dim = toBroadcastDims[i];
            int[] vectorShape = toBroadcastShapes[i];
            int length = ArrayUtil.prod(vectorShape);
            INDArray zC = Nd4j.create(shape, 'c');
            zC.setData(Nd4j.linspace(1, 24, 24).data());
            for (int tad = 0; tad < (zC.tensorssAlongDimension(dim)); tad++) {
                INDArray javaTad = zC.javaTensorAlongDimension(tad, dim);
                System.out.println(((("Tad " + tad) + " is ") + (zC.tensorAlongDimension(tad, dim))));
            }
            INDArray zF = Nd4j.create(shape, 'f');
            zF.assign(zC);
            INDArray toBroadcast = Nd4j.linspace(1, length, length);
            Op opc = new BroadcastAddOp(zC, toBroadcast, zC, dim);
            Op opf = new BroadcastAddOp(zF, toBroadcast, zF, dim);
            INDArray exp = Nd4j.create(expLinspaced[i], shape, 'c');
            INDArray expF = Nd4j.create(shape, 'f');
            expF.assign(exp);
            for (int tad = 0; tad < (zC.tensorssAlongDimension(dim)); tad++) {
                System.out.println((((zC.tensorAlongDimension(tad, dim).offset()) + " and f offset is ") + (zF.tensorAlongDimension(tad, dim).offset())));
            }
            Nd4j.getExecutioner().exec(opc);
            Nd4j.getExecutioner().exec(opf);
            Assert.assertEquals(exp, zC);
            Assert.assertEquals(exp, zF);
        }
    }

    @Test
    public void testSum3Of4_3322() {
        int[] shape = new int[]{ 3, 3, 2, 2 };
        int length = ArrayUtil.prod(shape);
        INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', shape);
        INDArray arrF = Nd4j.create(arrC.shape()).assign(arrC);
        int[][] dimsToSum = new int[][]{ new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 2, 3 }, new int[]{ 1, 2, 3 } };
        double[][] expD = new double[][]{ new double[]{ 324, 342 }, new double[]{ 315, 351 }, new double[]{ 174, 222, 270 }, new double[]{ 78, 222, 366 } };
        for (int i = 0; i < (dimsToSum.length); i++) {
            int[] d = dimsToSum[i];
            INDArray outC = arrC.sum(d);
            INDArray outF = arrF.sum(d);
            INDArray exp = Nd4j.create(expD[i], outC.shape());
            Assert.assertEquals(exp, outC);
            Assert.assertEquals(exp, outF);
            // System.out.println(Arrays.toString(d) + "\t" + outC + "\t" + outF);
        }
    }

    @Test
    public void testToFlattened() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        List<INDArray> concat = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            concat.add(arr.dup());
        }
        INDArray assertion = Nd4j.create(new double[]{ 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4 });
        INDArray flattened = Nd4j.toFlattened(concat);
        Assert.assertEquals(assertion, flattened);
    }

    @Test
    public void testDup() {
        for (int x = 0; x < 100; x++) {
            INDArray orig = Nd4j.linspace(1, 4, 4);
            INDArray dup = orig.dup();
            Assert.assertEquals(orig, dup);
            INDArray matrix = Nd4j.create(new float[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
            INDArray dup2 = matrix.dup();
            Assert.assertEquals(matrix, dup2);
            INDArray row1 = matrix.getRow(1);
            INDArray dupRow = row1.dup();
            Assert.assertEquals(row1, dupRow);
            INDArray columnSorted = Nd4j.create(new float[]{ 2, 1, 4, 3 }, new long[]{ 2, 2 });
            INDArray dup3 = columnSorted.dup();
            Assert.assertEquals(columnSorted, dup3);
        }
    }

    @Test
    public void testSortWithIndicesDescending() {
        INDArray toSort = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        // indices,data
        INDArray[] sorted = Nd4j.sortWithIndices(toSort.dup(), 1, false);
        INDArray sorted2 = Nd4j.sort(toSort.dup(), 1, false);
        Assert.assertEquals(sorted[1], sorted2);
        INDArray shouldIndex = Nd4j.create(new float[]{ 1, 0, 1, 0 }, new long[]{ 2, 2 });
        Assert.assertEquals(shouldIndex, sorted[0]);
    }

    @Test
    public void testGetFromRowVector() {
        INDArray matrix = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray rowGet = matrix.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 2));
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 2 }, rowGet.shape());
    }

    @Test
    public void testSubRowVector() {
        INDArray matrix = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray row = Nd4j.linspace(1, 3, 3);
        INDArray test = matrix.subRowVector(row);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 3, 3, 3 } });
        Assert.assertEquals(assertion, test);
        INDArray threeByThree = Nd4j.linspace(1, 9, 9).reshape(3, 3);
        INDArray offsetTest = threeByThree.get(NDArrayIndex.interval(1, 3), NDArrayIndex.all());
        Assert.assertEquals(2, offsetTest.rows());
        INDArray offsetAssertion = Nd4j.create(new double[][]{ new double[]{ 3, 3, 3 }, new double[]{ 6, 6, 6 } });
        INDArray offsetSub = offsetTest.subRowVector(row);
        Assert.assertEquals(offsetAssertion, offsetSub);
    }

    @Test
    public void testDimShuffle() {
        INDArray n = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray twoOneTwo = n.dimShuffle(new Object[]{ 0, 'x', 1 }, new int[]{ 0, 1 }, new boolean[]{ false, false });
        Assert.assertTrue(Arrays.equals(new long[]{ 2, 1, 2 }, twoOneTwo.shape()));
        INDArray reverse = n.dimShuffle(new Object[]{ 1, 'x', 0 }, new int[]{ 1, 0 }, new boolean[]{ false, false });
        Assert.assertTrue(Arrays.equals(new long[]{ 2, 1, 2 }, reverse.shape()));
    }

    @Test
    public void testGetVsGetScalar() {
        INDArray a = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        float element = a.getFloat(0, 1);
        double element2 = a.getDouble(0, 1);
        Assert.assertEquals(element, element2, 0.1);
        INDArray a2 = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        float element23 = a2.getFloat(0, 1);
        double element22 = a2.getDouble(0, 1);
        Assert.assertEquals(element23, element22, 0.1);
    }

    @Test
    public void testDivide() {
        INDArray two = Nd4j.create(new float[]{ 2, 2, 2, 2 });
        INDArray div = two.div(two);
        Assert.assertEquals(Nd4j.ones(4), div);
        INDArray half = Nd4j.create(new float[]{ 0.5F, 0.5F, 0.5F, 0.5F }, new long[]{ 2, 2 });
        INDArray divi = Nd4j.create(new float[]{ 0.3F, 0.6F, 0.9F, 0.1F }, new long[]{ 2, 2 });
        INDArray assertion = Nd4j.create(new float[]{ 1.6666666F, 0.8333333F, 0.5555556F, 5 }, new long[]{ 2, 2 });
        INDArray result = half.div(divi);
        Assert.assertEquals(assertion, result);
    }

    @Test
    public void testSigmoid() {
        INDArray n = Nd4j.create(new float[]{ 1, 2, 3, 4 });
        INDArray assertion = Nd4j.create(new float[]{ 0.7310586F, 0.8807971F, 0.95257413F, 0.98201376F });
        INDArray sigmoid = Transforms.sigmoid(n, false);
        Assert.assertEquals(assertion, sigmoid);
    }

    @Test
    public void testNeg() {
        INDArray n = Nd4j.create(new float[]{ 1, 2, 3, 4 });
        INDArray assertion = Nd4j.create(new float[]{ -1, -2, -3, -4 });
        INDArray neg = Transforms.neg(n);
        Assert.assertEquals(getFailureMessage(), assertion, neg);
    }

    @Test
    public void testNorm2Double() {
        DataBuffer.Type initialType = Nd4j.dataType();
        DataTypeUtil.setDTypeForContext(DOUBLE);
        INDArray n = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        double assertion = 5.47722557505;
        double norm3 = n.norm2Number().doubleValue();
        Assert.assertEquals(getFailureMessage(), assertion, norm3, 0.1);
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
        INDArray row1 = row.getRow(1);
        double norm2 = row1.norm2Number().doubleValue();
        double assertion2 = 5.0F;
        Assert.assertEquals(getFailureMessage(), assertion2, norm2, 0.1);
        DataTypeUtil.setDTypeForContext(initialType);
    }

    @Test
    public void testNorm2() {
        INDArray n = Nd4j.create(new float[]{ 1, 2, 3, 4 });
        float assertion = 5.477226F;
        float norm3 = n.norm2Number().floatValue();
        Assert.assertEquals(getFailureMessage(), assertion, norm3, 0.1);
        INDArray row = Nd4j.create(new float[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
        INDArray row1 = row.getRow(1);
        float norm2 = row1.norm2Number().floatValue();
        float assertion2 = 5.0F;
        Assert.assertEquals(getFailureMessage(), assertion2, norm2, 0.1);
    }

    @Test
    public void testCosineSim() {
        INDArray vec1 = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        INDArray vec2 = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        double sim = Transforms.cosineSim(vec1, vec2);
        Assert.assertEquals(getFailureMessage(), 1, sim, 0.1);
        INDArray vec3 = Nd4j.create(new float[]{ 0.2F, 0.3F, 0.4F, 0.5F });
        INDArray vec4 = Nd4j.create(new float[]{ 0.6F, 0.7F, 0.8F, 0.9F });
        sim = Transforms.cosineSim(vec3, vec4);
        Assert.assertEquals(0.98, sim, 0.1);
    }

    @Test
    public void testScal() {
        double assertion = 2;
        INDArray answer = Nd4j.create(new double[]{ 2, 4, 6, 8 });
        INDArray scal = Nd4j.getBlasWrapper().scal(assertion, answer);
        Assert.assertEquals(getFailureMessage(), answer, scal);
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
        INDArray row1 = row.getRow(1);
        double assertion2 = 5.0;
        INDArray answer2 = Nd4j.create(new double[]{ 15, 20 });
        INDArray scal2 = Nd4j.getBlasWrapper().scal(assertion2, row1);
        Assert.assertEquals(getFailureMessage(), answer2, scal2);
    }

    @Test
    public void testExp() {
        INDArray n = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        INDArray assertion = Nd4j.create(new double[]{ 2.7182817F, 7.389056F, 20.085537F, 54.59815F });
        INDArray exped = Transforms.exp(n);
        Assert.assertEquals(assertion, exped);
        BaseNd4jTest.assertArrayEquals(new double[]{ 2.7182817F, 7.389056F, 20.085537F, 54.59815F }, exped.toDoubleVector(), 1.0E-5);
        BaseNd4jTest.assertArrayEquals(new double[]{ 2.7182817F, 7.389056F, 20.085537F, 54.59815F }, assertion.toDoubleVector(), 1.0E-5);
    }

    @Test
    public void testSlices() {
        INDArray arr = Nd4j.create(Nd4j.linspace(1, 24, 24).data(), new long[]{ 4, 3, 2 });
        for (int i = 0; i < (arr.slices()); i++) {
            Assert.assertEquals(2, arr.slice(i).slice(1).slices());
        }
    }

    @Test
    public void testScalar() {
        INDArray a = Nd4j.scalar(1.0);
        Assert.assertEquals(true, a.isScalar());
        INDArray n = Nd4j.create(new float[]{ 1.0F }, new long[]{ 1, 1 });
        Assert.assertEquals(n, a);
        Assert.assertTrue(n.isScalar());
    }

    @Test
    public void testWrap() throws Exception {
        int[] shape = new int[]{ 2, 4 };
        INDArray d = Nd4j.linspace(1, 8, 8).reshape(shape[0], shape[1]);
        INDArray n = d;
        Assert.assertEquals(d.rows(), n.rows());
        Assert.assertEquals(d.columns(), n.columns());
        INDArray vector = Nd4j.linspace(1, 3, 3);
        INDArray testVector = vector;
        for (int i = 0; i < (vector.length()); i++)
            Assert.assertEquals(vector.getDouble(i), testVector.getDouble(i), 0.1);

        Assert.assertEquals(3, testVector.length());
        Assert.assertEquals(true, testVector.isVector());
        Assert.assertEquals(true, Shape.shapeEquals(new long[]{ 3 }, testVector.shape()));
        INDArray row12 = Nd4j.linspace(1, 2, 2).reshape(2, 1);
        INDArray row22 = Nd4j.linspace(3, 4, 2).reshape(1, 2);
        Assert.assertEquals(row12.rows(), 2);
        Assert.assertEquals(row12.columns(), 1);
        Assert.assertEquals(row22.rows(), 1);
        Assert.assertEquals(row22.columns(), 2);
    }

    @Test
    public void testVectorInit() {
        DataBuffer data = Nd4j.linspace(1, 4, 4).data();
        INDArray arr = Nd4j.create(data, new long[]{ 1, 4 });
        Assert.assertEquals(true, arr.isRowVector());
        INDArray arr2 = Nd4j.create(data, new long[]{ 1, 4 });
        Assert.assertEquals(true, arr2.isRowVector());
        INDArray columnVector = Nd4j.create(data, new long[]{ 4, 1 });
        Assert.assertEquals(true, columnVector.isColumnVector());
    }

    @Test
    public void testColumns() {
        INDArray arr = Nd4j.create(new long[]{ 3, 2 });
        INDArray column2 = arr.getColumn(0);
        // assertEquals(true, Shape.shapeEquals(new long[]{3, 1}, column2.shape()));
        INDArray column = Nd4j.create(new double[]{ 1, 2, 3 }, new long[]{ 1, 3 });
        arr.putColumn(0, column);
        INDArray firstColumn = arr.getColumn(0);
        Assert.assertEquals(column, firstColumn);
        INDArray column1 = Nd4j.create(new double[]{ 4, 5, 6 }, new long[]{ 1, 3 });
        arr.putColumn(1, column1);
        // assertEquals(true, Shape.shapeEquals(new long[]{3, 1}, arr.getColumn(1).shape()));
        INDArray testRow1 = arr.getColumn(1);
        Assert.assertEquals(column1, testRow1);
        INDArray evenArr = Nd4j.create(new double[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
        INDArray put = Nd4j.create(new double[]{ 5, 6 }, new long[]{ 1, 2 });
        evenArr.putColumn(1, put);
        INDArray testColumn = evenArr.getColumn(1);
        Assert.assertEquals(put, testColumn);
        INDArray n = Nd4j.create(Nd4j.linspace(1, 4, 4).data(), new long[]{ 2, 2 });
        INDArray column23 = n.getColumn(0);
        INDArray column12 = Nd4j.create(new double[]{ 1, 3 }, new long[]{ 1, 2 });
        Assert.assertEquals(column23, column12);
        INDArray column0 = n.getColumn(1);
        INDArray column01 = Nd4j.create(new double[]{ 2, 4 }, new long[]{ 1, 2 });
        Assert.assertEquals(column0, column01);
    }

    @Test
    public void testPutRow() {
        INDArray d = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray slice1 = d.slice(1);
        INDArray n = d.dup();
        // works fine according to matlab, let's go with it..
        // reproduce with:  A = newShapeNoCopy(linspace(1,4,4),[2 2 ]);
        // A(1,2) % 1 index based
        float nFirst = 2;
        float dFirst = d.getFloat(0, 1);
        Assert.assertEquals(nFirst, dFirst, 0.1);
        Assert.assertEquals(d, n);
        Assert.assertEquals(true, Arrays.equals(new long[]{ 2, 2 }, n.shape()));
        INDArray newRow = Nd4j.linspace(5, 6, 2);
        n.putRow(0, newRow);
        d.putRow(0, newRow);
        INDArray testRow = n.getRow(0);
        Assert.assertEquals(newRow.length(), testRow.length());
        Assert.assertEquals(true, Shape.shapeEquals(new long[]{ 1, 2 }, testRow.shape()));
        INDArray nLast = Nd4j.create(Nd4j.linspace(1, 4, 4).data(), new long[]{ 2, 2 });
        INDArray row = nLast.getRow(1);
        INDArray row1 = Nd4j.create(new double[]{ 3, 4 }, new long[]{ 1, 2 });
        Assert.assertEquals(row, row1);
        INDArray arr = Nd4j.create(new long[]{ 3, 2 });
        INDArray evenRow = Nd4j.create(new double[]{ 1, 2 }, new long[]{ 1, 2 });
        arr.putRow(0, evenRow);
        INDArray firstRow = arr.getRow(0);
        Assert.assertEquals(true, Shape.shapeEquals(new long[]{ 1, 2 }, firstRow.shape()));
        INDArray testRowEven = arr.getRow(0);
        Assert.assertEquals(evenRow, testRowEven);
        INDArray row12 = Nd4j.create(new double[]{ 5, 6 }, new long[]{ 1, 2 });
        arr.putRow(1, row12);
        Assert.assertEquals(true, Shape.shapeEquals(new long[]{ 1, 2 }, arr.getRow(0).shape()));
        INDArray testRow1 = arr.getRow(1);
        Assert.assertEquals(row12, testRow1);
        INDArray multiSliceTest = Nd4j.create(Nd4j.linspace(1, 16, 16).data(), new long[]{ 4, 2, 2 });
        INDArray test = Nd4j.create(new double[]{ 5, 6 }, new long[]{ 1, 2 });
        INDArray test2 = Nd4j.create(new double[]{ 7, 8 }, new long[]{ 1, 2 });
        INDArray multiSliceRow1 = multiSliceTest.slice(1).getRow(0);
        INDArray multiSliceRow2 = multiSliceTest.slice(1).getRow(1);
        Assert.assertEquals(test, multiSliceRow1);
        Assert.assertEquals(test2, multiSliceRow2);
        INDArray threeByThree = Nd4j.create(3, 3);
        INDArray threeByThreeRow1AndTwo = threeByThree.get(NDArrayIndex.interval(1, 3), NDArrayIndex.all());
        threeByThreeRow1AndTwo.putRow(1, Nd4j.ones(3));
        Assert.assertEquals(Nd4j.ones(3), threeByThreeRow1AndTwo.getRow(1));
    }

    @Test
    public void testMulRowVector() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        arr.muliRowVector(Nd4j.linspace(1, 2, 2));
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 1, 4 }, new double[]{ 3, 8 } });
        Assert.assertEquals(assertion, arr);
    }

    @Test
    public void testSum() {
        INDArray n = Nd4j.create(Nd4j.linspace(1, 8, 8).data(), new long[]{ 2, 2, 2 });
        INDArray test = Nd4j.create(new float[]{ 3, 7, 11, 15 }, new long[]{ 2, 2 });
        INDArray sum = n.sum((-1));
        Assert.assertEquals(test, sum);
    }

    @Test
    public void testInplaceTranspose() {
        INDArray test = Nd4j.rand(34, 484);
        INDArray transposei = test.transposei();
        for (int i = 0; i < (test.rows()); i++) {
            for (int j = 0; j < (test.columns()); j++) {
                Assert.assertEquals(test.getDouble(i, j), transposei.getDouble(j, i), 0.1);
            }
        }
    }

    @Test
    public void testTADMMul() {
        Nd4j.getRandom().setSeed(12345);
        val shape = new long[]{ 4, 5, 7 };
        INDArray arr = Nd4j.rand(shape);
        INDArray tad = arr.tensorAlongDimension(0, 1, 2);
        BaseNd4jTest.assertArrayEquals(tad.shape(), new long[]{ 5, 7 });
        INDArray copy = Nd4j.zeros(5, 7).assign(0.0);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                copy.putScalar(new long[]{ i, j }, tad.getDouble(i, j));
            }
        }
        Assert.assertTrue(tad.equals(copy));
        tad = tad.reshape(7, 5);
        copy = copy.reshape(7, 5);
        INDArray first = Nd4j.rand(new long[]{ 2, 7 });
        INDArray mmul = first.mmul(tad);
        INDArray mmulCopy = first.mmul(copy);
        Assert.assertEquals(mmul, mmulCopy);
    }

    @Test
    public void testTADMMulLeadingOne() {
        Nd4j.getRandom().setSeed(12345);
        val shape = new long[]{ 1, 5, 7 };
        INDArray arr = Nd4j.rand(shape);
        INDArray tad = arr.tensorAlongDimension(0, 1, 2);
        boolean order = Shape.cOrFortranOrder(tad.shape(), tad.stride(), tad.elementStride());
        BaseNd4jTest.assertArrayEquals(tad.shape(), new long[]{ 5, 7 });
        INDArray copy = Nd4j.zeros(5, 7);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                copy.putScalar(new long[]{ i, j }, tad.getDouble(i, j));
            }
        }
        Assert.assertTrue(tad.equals(copy));
        tad = tad.reshape(7, 5);
        copy = copy.reshape(7, 5);
        INDArray first = Nd4j.rand(new long[]{ 2, 7 });
        INDArray mmul = first.mmul(tad);
        INDArray mmulCopy = first.mmul(copy);
        Assert.assertTrue(mmul.equals(mmulCopy));
    }

    @Test
    public void testSum2() {
        INDArray test = Nd4j.create(new float[]{ 1, 2, 3, 4 }, new long[]{ 2, 2 });
        INDArray sum = test.sum(1);
        INDArray assertion = Nd4j.create(new float[]{ 3, 7 });
        Assert.assertEquals(assertion, sum);
        INDArray sum0 = Nd4j.create(new double[]{ 4, 6 });
        Assert.assertEquals(sum0, test.sum(0));
    }

    @Test
    public void testGetIntervalEdgeCase() {
        Nd4j.getRandom().setSeed(12345);
        int[] shape = new int[]{ 3, 2, 4 };
        INDArray arr3d = Nd4j.rand(shape);
        INDArray get0 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 1));
        INDArray getPoint0 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0));
        get0 = get0.reshape(getPoint0.shape());
        INDArray tad0 = arr3d.tensorAlongDimension(0, 1, 0);
        Assert.assertTrue(get0.equals(getPoint0));// OK

        Assert.assertTrue(getPoint0.equals(tad0));// OK

        INDArray get1 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(1, 2));
        INDArray getPoint1 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1));
        get1 = get1.reshape(getPoint1.shape());
        INDArray tad1 = arr3d.tensorAlongDimension(1, 1, 0);
        Assert.assertTrue(getPoint1.equals(tad1));// OK

        Assert.assertTrue(get1.equals(getPoint1));// Fails

        Assert.assertTrue(get1.equals(tad1));
        INDArray get2 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(2, 3));
        INDArray getPoint2 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2));
        get2 = get2.reshape(getPoint2.shape());
        INDArray tad2 = arr3d.tensorAlongDimension(2, 1, 0);
        Assert.assertTrue(getPoint2.equals(tad2));// OK

        Assert.assertTrue(get2.equals(getPoint2));// Fails

        Assert.assertTrue(get2.equals(tad2));
        INDArray get3 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(3, 4));
        INDArray getPoint3 = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(3));
        get3 = get3.reshape(getPoint3.shape());
        INDArray tad3 = arr3d.tensorAlongDimension(3, 1, 0);
        Assert.assertTrue(getPoint3.equals(tad3));// OK

        Assert.assertTrue(get3.equals(getPoint3));// Fails

        Assert.assertTrue(get3.equals(tad3));
    }

    @Test
    public void testGetIntervalEdgeCase2() {
        Nd4j.getRandom().setSeed(12345);
        int[] shape = new int[]{ 3, 2, 4 };
        INDArray arr3d = Nd4j.rand(shape);
        for (int x = 0; x < 4; x++) {
            INDArray getInterval = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(x, (x + 1)));// 3d

            INDArray getPoint = arr3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(x));// 2d

            INDArray tad = arr3d.tensorAlongDimension(x, 1, 0);// 2d

            Assert.assertEquals(getPoint, tad);
            // assertTrue(getPoint.equals(tad));   //OK, comparing 2d with 2d
            BaseNd4jTest.assertArrayEquals(getInterval.shape(), new long[]{ 3, 2, 1 });
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    Assert.assertEquals(getInterval.getDouble(i, j, 0), getPoint.getDouble(i, j), 0.1);
                }
            }
        }
    }

    @Test
    public void testMmul() {
        DataBuffer data = Nd4j.linspace(1, 10, 10).data();
        INDArray n = Nd4j.create(data, new long[]{ 1, 10 });
        INDArray transposed = n.transpose();
        Assert.assertEquals(true, n.isRowVector());
        Assert.assertEquals(true, transposed.isColumnVector());
        INDArray d = Nd4j.create(n.rows(), n.columns());
        d.setData(n.data());
        INDArray d3 = Nd4j.create(new double[]{ 1, 2 }).reshape(2, 1);
        INDArray d4 = Nd4j.create(new double[]{ 3, 4 });
        INDArray resultNDArray = d3.mmul(d4);
        INDArray result = Nd4j.create(new double[][]{ new double[]{ 3, 4 }, new double[]{ 6, 8 } });
        Assert.assertEquals(result, resultNDArray);
        INDArray innerProduct = n.mmul(transposed);
        INDArray scalar = Nd4j.scalar(385);
        Assert.assertEquals(getFailureMessage(), scalar, innerProduct);
        INDArray outerProduct = transposed.mmul(n);
        Assert.assertEquals(getFailureMessage(), true, Shape.shapeEquals(new long[]{ 10, 10 }, outerProduct.shape()));
        INDArray three = Nd4j.create(new double[]{ 3, 4 }, new long[]{ 1, 2 });
        INDArray test = Nd4j.create(Nd4j.linspace(1, 30, 30).data(), new long[]{ 3, 5, 2 });
        INDArray sliceRow = test.slice(0).getRow(1);
        Assert.assertEquals(getFailureMessage(), three, sliceRow);
        INDArray twoSix = Nd4j.create(new double[]{ 2, 6 }, new long[]{ 2, 1 });
        INDArray threeTwoSix = three.mmul(twoSix);
        INDArray sliceRowTwoSix = sliceRow.mmul(twoSix);
        Assert.assertEquals(threeTwoSix, sliceRowTwoSix);
        INDArray vectorVector = Nd4j.create(new double[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 0, 6, 12, 18, 24, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 0, 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84, 91, 98, 105, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120, 0, 9, 18, 27, 36, 45, 54, 63, 72, 81, 90, 99, 108, 117, 126, 135, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 0, 11, 22, 33, 44, 55, 66, 77, 88, 99, 110, 121, 132, 143, 154, 165, 0, 12, 24, 36, 48, 60, 72, 84, 96, 108, 120, 132, 144, 156, 168, 180, 0, 13, 26, 39, 52, 65, 78, 91, 104, 117, 130, 143, 156, 169, 182, 195, 0, 14, 28, 42, 56, 70, 84, 98, 112, 126, 140, 154, 168, 182, 196, 210, 0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180, 195, 210, 225 }, new long[]{ 16, 16 });
        INDArray n1 = Nd4j.create(Nd4j.linspace(0, 15, 16).data(), new long[]{ 1, 16 });
        INDArray k1 = n1.transpose();
        INDArray testVectorVector = k1.mmul(n1);
        Assert.assertEquals(getFailureMessage(), vectorVector, testVectorVector);
    }

    @Test
    public void testRowsColumns() {
        DataBuffer data = Nd4j.linspace(1, 6, 6).data();
        INDArray rows = Nd4j.create(data, new long[]{ 2, 3 });
        Assert.assertEquals(2, rows.rows());
        Assert.assertEquals(3, rows.columns());
        INDArray columnVector = Nd4j.create(data, new long[]{ 6, 1 });
        Assert.assertEquals(6, columnVector.rows());
        Assert.assertEquals(1, columnVector.columns());
        INDArray rowVector = Nd4j.create(data, new long[]{ 1, 6 });
        Assert.assertEquals(1, rowVector.rows());
        Assert.assertEquals(6, rowVector.columns());
    }

    @Test
    public void testTranspose() {
        INDArray n = Nd4j.create(Nd4j.ones(100).data(), new long[]{ 5, 5, 4 });
        INDArray transpose = n.transpose();
        Assert.assertEquals(n.length(), transpose.length());
        Assert.assertEquals(true, Arrays.equals(new long[]{ 4, 5, 5 }, transpose.shape()));
        INDArray rowVector = Nd4j.linspace(1, 10, 10);
        Assert.assertTrue(rowVector.isRowVector());
        INDArray columnVector = rowVector.transpose();
        Assert.assertTrue(columnVector.isColumnVector());
        INDArray linspaced = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray transposed = Nd4j.create(new float[]{ 1, 3, 2, 4 }, new long[]{ 2, 2 });
        INDArray linSpacedT = linspaced.transpose();
        Assert.assertEquals(transposed, linSpacedT);
    }

    @Test
    public void testLogX1() {
        INDArray x = Nd4j.create(10).assign(7);
        INDArray logX5 = Transforms.log(x, 5, true);
        INDArray exp = Transforms.log(x, true).div(Transforms.log(Nd4j.create(10).assign(5)));
        Assert.assertEquals(exp, logX5);
    }

    @Test
    public void testAddMatrix() {
        INDArray five = Nd4j.ones(5);
        five.addi(five);
        INDArray twos = Nd4j.valueArrayOf(5, 2);
        Assert.assertEquals(twos, five);
    }

    @Test
    public void testPutSlice() {
        INDArray n = Nd4j.linspace(1, 27, 27).reshape(3, 3, 3);
        INDArray newSlice = Nd4j.zeros(3, 3);
        n.putSlice(0, newSlice);
        Assert.assertEquals(newSlice, n.slice(0));
        INDArray firstDimensionAs1 = newSlice.reshape(1, 3, 3);
        n.putSlice(0, firstDimensionAs1);
    }

    @Test
    public void testRowVectorMultipleIndices() {
        INDArray linear = Nd4j.create(1, 4);
        linear.putScalar(new long[]{ 0, 1 }, 1);
        Assert.assertEquals(linear.getDouble(0, 1), 1, 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSize() {
        INDArray arr = Nd4j.create(4, 5);
        for (int i = 0; i < 6; i++) {
            // This should fail for i >= 2, but doesn't
            System.out.println(arr.size(i));
        }
    }

    @Test
    public void testNullPointerDataBuffer() {
        DataBuffer.Type initialType = Nd4j.dataType();
        DataTypeUtil.setDTypeForContext(FLOAT);
        ByteBuffer allocate = ByteBuffer.allocateDirect((10 * 4)).order(ByteOrder.nativeOrder());
        allocate.asFloatBuffer().put(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        DataBuffer buff = Nd4j.createBuffer(allocate, FLOAT, 10);
        float sum = Nd4j.create(buff).sumNumber().floatValue();
        System.out.println(sum);
        Assert.assertEquals(55.0F, sum, 0.001F);
        DataTypeUtil.setDTypeForContext(initialType);
    }

    @Test
    public void testEps() {
        INDArray ones = Nd4j.ones(5);
        double sum = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.comparison.Eps(ones, ones, ones, ones.length())).z().sumNumber().doubleValue();
        Assert.assertEquals(5, sum, 0.1);
    }

    @Test
    public void testEps2() {
        INDArray first = Nd4j.valueArrayOf(10, 0.01);// 0.01

        INDArray second = Nd4j.zeros(10);// 0.0

        INDArray expAllZeros1 = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.comparison.Eps(first, second, Nd4j.create(new long[]{ 1, 10 }, 'f'), 10));
        INDArray expAllZeros2 = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.comparison.Eps(second, first, Nd4j.create(new long[]{ 1, 10 }, 'f'), 10));
        System.out.println(expAllZeros1);
        System.out.println(expAllZeros2);
        Assert.assertEquals(0, expAllZeros1.sumNumber().doubleValue(), 0.0);
        Assert.assertEquals(0, expAllZeros2.sumNumber().doubleValue(), 0.0);
    }

    @Test
    public void testLogDouble() {
        INDArray linspace = Nd4j.linspace(1, 6, 6);
        INDArray log = Transforms.log(linspace);
        INDArray assertion = Nd4j.create(new double[]{ 0, 0.6931471805599453, 1.0986122886681098, 1.3862943611198906, 1.6094379124341005, 1.791759469228055 });
        Assert.assertEquals(assertion, log);
    }

    @Test
    public void testDupDimension() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        Assert.assertEquals(arr.tensorAlongDimension(0, 1), arr.tensorAlongDimension(0, 1));
    }

    @Test
    public void testIterator() {
        INDArray x = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray repeated = x.repeat(1, new int[]{ 2 });
        Assert.assertEquals(8, repeated.length());
        Iterator<Double> arrayIter = new org.nd4j.linalg.api.iter.INDArrayIterator(x);
        double[] vals = Nd4j.linspace(1, 4, 4).data().asDouble();
        for (int i = 0; i < (vals.length); i++)
            Assert.assertEquals(vals[i], arrayIter.next().doubleValue(), 0.1);

    }

    @Test
    public void testTile() {
        INDArray x = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray repeated = x.repeat(0, new int[]{ 2 });
        Assert.assertEquals(8, repeated.length());
        INDArray repeatAlongDimension = x.repeat(1, new long[]{ 2 });
        INDArray assertionRepeat = Nd4j.create(new double[][]{ new double[]{ 1, 1, 2, 2 }, new double[]{ 3, 3, 4, 4 } });
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 4 }, assertionRepeat.shape());
        Assert.assertEquals(assertionRepeat, repeatAlongDimension);
        System.out.println(repeatAlongDimension);
        INDArray ret = Nd4j.create(new double[]{ 0, 1, 2 });
        INDArray tile = Nd4j.tile(ret, 2, 2);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 0, 1, 2, 0, 1, 2 }, new double[]{ 0, 1, 2, 0, 1, 2 } });
        Assert.assertEquals(assertion, tile);
    }

    @Test
    public void testNegativeOneReshape() {
        INDArray arr = Nd4j.create(new double[]{ 0, 1, 2 });
        INDArray newShape = arr.reshape((-1), 3);
        Assert.assertEquals(newShape, arr);
    }

    @Test
    public void testSmallSum() {
        INDArray base = Nd4j.create(new double[]{ 5.843333333333335, 3.0540000000000007 });
        base.addi(1.0E-12);
        INDArray assertion = Nd4j.create(new double[]{ 5.84333433, 3.054001 });
        Assert.assertEquals(assertion, base);
    }

    @Test
    public void test2DArraySlice() {
        INDArray array2D = Nd4j.ones(5, 7);
        /**
         * This should be reverse.
         * This is compatibility with numpy.
         *
         * If you do numpy.sum along dimension
         * 1 you will find its row sums.
         *
         * 0 is columns sums.
         *
         * slice(0,axis)
         * should be consistent with this behavior
         */
        for (int i = 0; i < 7; i++) {
            INDArray slice = array2D.slice(i, 1);
            Assert.assertTrue(Arrays.equals(slice.shape(), new long[]{ 5, 1 }));
        }
        for (int i = 0; i < 5; i++) {
            INDArray slice = array2D.slice(i, 0);
            Assert.assertTrue(Arrays.equals(slice.shape(), new long[]{ 1, 7 }));
        }
    }

    @Test
    public void testTensorDot() {
        INDArray oneThroughSixty = Nd4j.arange(60).reshape(3, 4, 5);
        INDArray oneThroughTwentyFour = Nd4j.arange(24).reshape(4, 3, 2);
        INDArray result = Nd4j.tensorMmul(oneThroughSixty, oneThroughTwentyFour, new int[][]{ new int[]{ 1, 0 }, new int[]{ 0, 1 } });
        BaseNd4jTest.assertArrayEquals(new long[]{ 5, 2 }, result.shape());
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 4400, 4730 }, new double[]{ 4532, 4874 }, new double[]{ 4664, 5018 }, new double[]{ 4796, 5162 }, new double[]{ 4928, 5306 } });
        Assert.assertEquals(assertion, result);
        INDArray w = Nd4j.valueArrayOf(new long[]{ 2, 1, 2, 2 }, 0.5);
        INDArray col = Nd4j.create(new double[]{ 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4 }, new long[]{ 1, 1, 2, 2, 4, 4 });
        INDArray test = Nd4j.tensorMmul(col, w, new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 3 } });
        INDArray assertion2 = Nd4j.create(new double[]{ 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 7.0, 7.0, 7.0, 7.0, 7.0, 7.0, 7.0, 7.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 7.0, 7.0, 7.0, 7.0, 7.0, 7.0, 7.0, 7.0 }, new long[]{ 1, 4, 4, 2 }, new long[]{ 16, 8, 2, 1 }, 0, 'f');
        // assertion2.setOrder('f');
        Assert.assertEquals(assertion2, test);
    }

    @Test
    public void testGetRow() {
        INDArray arr = Nd4j.ones(10, 4);
        for (int i = 0; i < 10; i++) {
            INDArray row = arr.getRow(i);
            BaseNd4jTest.assertArrayEquals(row.shape(), new long[]{ 1, 4 });
        }
    }

    @Test
    public void testGetPermuteReshapeSub() {
        Nd4j.getRandom().setSeed(12345);
        INDArray first = Nd4j.rand(new long[]{ 10, 4 });
        // Reshape, as per RnnOutputLayer etc on labels
        INDArray orig3d = Nd4j.rand(new long[]{ 2, 4, 15 });
        INDArray subset3d = orig3d.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(5, 10));
        INDArray permuted = subset3d.permute(0, 2, 1);
        val newShape = new long[]{ (subset3d.size(0)) * (subset3d.size(2)), subset3d.size(1) };
        INDArray second = permuted.reshape(newShape);
        BaseNd4jTest.assertArrayEquals(first.shape(), second.shape());
        Assert.assertEquals(first.length(), second.length());
        BaseNd4jTest.assertArrayEquals(first.stride(), second.stride());
        first.sub(second);// Exception

    }

    @Test
    public void testPutAtIntervalIndexWithStride() {
        INDArray n1 = Nd4j.create(3, 3).assign(0.0);
        INDArrayIndex[] indices = new INDArrayIndex[]{ NDArrayIndex.interval(0, 2, 3), NDArrayIndex.all() };
        n1.put(indices, 1);
        INDArray expected = Nd4j.create(new double[][]{ new double[]{ 1.0, 1.0, 1.0 }, new double[]{ 0.0, 0.0, 0.0 }, new double[]{ 1.0, 1.0, 1.0 } });
        Assert.assertEquals(expected, n1);
    }

    @Test
    public void testMMulMatrixTimesColVector() {
        // [1 1 1 1 1; 10 10 10 10 10; 100 100 100 100 100] x [1; 1; 1; 1; 1] = [5; 50; 500]
        INDArray matrix = Nd4j.ones(3, 5);
        matrix.getRow(1).muli(10);
        matrix.getRow(2).muli(100);
        INDArray colVector = Nd4j.ones(5, 1);
        INDArray out = matrix.mmul(colVector);
        INDArray expected = Nd4j.create(new double[]{ 5, 50, 500 }, new long[]{ 3, 1 });
        Assert.assertEquals(expected, out);
    }

    @Test
    public void testMMulMixedOrder() {
        INDArray first = Nd4j.ones(5, 2);
        INDArray second = Nd4j.ones(2, 3);
        INDArray out = first.mmul(second);
        BaseNd4jTest.assertArrayEquals(out.shape(), new long[]{ 5, 3 });
        Assert.assertTrue(out.equals(Nd4j.ones(5, 3).muli(2)));
        // Above: OK
        INDArray firstC = Nd4j.create(new long[]{ 5, 2 }, 'c');
        INDArray secondF = Nd4j.create(new long[]{ 2, 3 }, 'f');
        for (int i = 0; i < (firstC.length()); i++)
            firstC.putScalar(i, 1.0);

        for (int i = 0; i < (secondF.length()); i++)
            secondF.putScalar(i, 1.0);

        Assert.assertTrue(first.equals(firstC));
        Assert.assertTrue(second.equals(secondF));
        INDArray outCF = firstC.mmul(secondF);
        BaseNd4jTest.assertArrayEquals(outCF.shape(), new long[]{ 5, 3 });
        Assert.assertEquals(outCF, Nd4j.ones(5, 3).muli(2));
    }

    @Test
    public void testFTimesCAddiRow() {
        INDArray arrF = Nd4j.create(2, 3, 'f').assign(1.0);
        INDArray arrC = Nd4j.create(2, 3, 'c').assign(1.0);
        INDArray arr2 = Nd4j.create(new long[]{ 3, 4 }, 'c').assign(1.0);
        INDArray mmulC = arrC.mmul(arr2);// [2,4] with elements 3.0

        INDArray mmulF = arrF.mmul(arr2);// [2,4] with elements 3.0

        BaseNd4jTest.assertArrayEquals(mmulC.shape(), new long[]{ 2, 4 });
        BaseNd4jTest.assertArrayEquals(mmulF.shape(), new long[]{ 2, 4 });
        Assert.assertTrue(arrC.equals(arrF));
        INDArray row = Nd4j.zeros(1, 4).assign(0.0).addi(0.5);
        mmulC.addiRowVector(row);// OK

        mmulF.addiRowVector(row);// Exception

        Assert.assertTrue(mmulC.equals(mmulF));
        for (int i = 0; i < (mmulC.length()); i++)
            Assert.assertEquals(mmulC.getDouble(i), 3.5, 0.1);
        // OK

        for (int i = 0; i < (mmulF.length()); i++)
            Assert.assertEquals(mmulF.getDouble(i), 3.5, 0.1);
        // Exception

    }

    @Test
    public void testMmulGet() {
        Nd4j.getRandom().setSeed(12345L);
        INDArray elevenByTwo = Nd4j.rand(new long[]{ 11, 2 });
        INDArray twoByEight = Nd4j.rand(new long[]{ 2, 8 });
        INDArray view = twoByEight.get(NDArrayIndex.all(), NDArrayIndex.interval(0, 2));
        INDArray viewCopy = view.dup();
        Assert.assertTrue(view.equals(viewCopy));
        INDArray mmul1 = elevenByTwo.mmul(view);
        INDArray mmul2 = elevenByTwo.mmul(viewCopy);
        Assert.assertTrue(mmul1.equals(mmul2));
    }

    @Test
    public void testMMulRowColVectorMixedOrder() {
        INDArray colVec = Nd4j.ones(5, 1);
        INDArray rowVec = Nd4j.ones(1, 3);
        INDArray out = colVec.mmul(rowVec);
        BaseNd4jTest.assertArrayEquals(out.shape(), new long[]{ 5, 3 });
        Assert.assertTrue(out.equals(Nd4j.ones(5, 3)));
        // Above: OK
        INDArray colVectorC = Nd4j.create(new long[]{ 5, 1 }, 'c');
        INDArray rowVectorF = Nd4j.create(new long[]{ 1, 3 }, 'f');
        for (int i = 0; i < (colVectorC.length()); i++)
            colVectorC.putScalar(i, 1.0);

        for (int i = 0; i < (rowVectorF.length()); i++)
            rowVectorF.putScalar(i, 1.0);

        Assert.assertTrue(colVec.equals(colVectorC));
        Assert.assertTrue(rowVec.equals(rowVectorF));
        INDArray outCF = colVectorC.mmul(rowVectorF);
        BaseNd4jTest.assertArrayEquals(outCF.shape(), new long[]{ 5, 3 });
        Assert.assertEquals(outCF, Nd4j.ones(5, 3));
    }

    @Test
    public void testMMulFTimesC() {
        int nRows = 3;
        int nCols = 3;
        Random r = new Random(12345);
        INDArray arrC = Nd4j.create(new long[]{ nRows, nCols }, 'c');
        INDArray arrF = Nd4j.create(new long[]{ nRows, nCols }, 'f');
        INDArray arrC2 = Nd4j.create(new long[]{ nRows, nCols }, 'c');
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                double rv = r.nextDouble();
                arrC.putScalar(new long[]{ i, j }, rv);
                arrF.putScalar(new long[]{ i, j }, rv);
                arrC2.putScalar(new long[]{ i, j }, r.nextDouble());
            }
        }
        Assert.assertTrue(arrF.equals(arrC));
        INDArray fTimesC = arrF.mmul(arrC2);
        INDArray cTimesC = arrC.mmul(arrC2);
        Assert.assertEquals(fTimesC, cTimesC);
    }

    @Test
    public void testMMulColVectorRowVectorMixedOrder() {
        INDArray colVec = Nd4j.ones(5, 1);
        INDArray rowVec = Nd4j.ones(1, 5);
        INDArray out = rowVec.mmul(colVec);
        BaseNd4jTest.assertArrayEquals(out.shape(), new long[]{ 1, 1 });
        Assert.assertTrue(out.equals(Nd4j.ones(1, 1).muli(5)));
        INDArray colVectorC = Nd4j.create(new long[]{ 5, 1 }, 'c');
        INDArray rowVectorF = Nd4j.create(new long[]{ 1, 5 }, 'f');
        for (int i = 0; i < (colVectorC.length()); i++)
            colVectorC.putScalar(i, 1.0);

        for (int i = 0; i < (rowVectorF.length()); i++)
            rowVectorF.putScalar(i, 1.0);

        Assert.assertTrue(colVec.equals(colVectorC));
        Assert.assertTrue(rowVec.equals(rowVectorF));
        INDArray outCF = rowVectorF.mmul(colVectorC);
        BaseNd4jTest.assertArrayEquals(outCF.shape(), new long[]{ 1, 1 });
        Assert.assertTrue(outCF.equals(Nd4j.ones(1, 1).muli(5)));
    }

    @Test
    public void testPermute() {
        INDArray n = Nd4j.create(Nd4j.linspace(1, 20, 20).data(), new long[]{ 5, 4 });
        INDArray transpose = n.transpose();
        INDArray permute = n.permute(1, 0);
        Assert.assertEquals(permute, transpose);
        Assert.assertEquals(transpose.length(), permute.length(), 0.1);
        INDArray toPermute = Nd4j.create(Nd4j.linspace(0, 7, 8).data(), new long[]{ 2, 2, 2 });
        INDArray permuted = toPermute.permute(2, 1, 0);
        INDArray assertion = Nd4j.create(new float[]{ 0, 4, 2, 6, 1, 5, 3, 7 }, new long[]{ 2, 2, 2 });
        Assert.assertEquals(permuted, assertion);
    }

    @Test
    public void testPermutei() {
        // Check in-place permute vs. copy array permute
        // 2d:
        INDArray orig = Nd4j.linspace(1, (3 * 4), (3 * 4)).reshape('c', 3, 4);
        INDArray exp01 = orig.permute(0, 1);
        INDArray exp10 = orig.permute(1, 0);
        List<Pair<INDArray, String>> list1 = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 4, 12345);
        List<Pair<INDArray, String>> list2 = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 4, 12345);
        for (int i = 0; i < (list1.size()); i++) {
            INDArray p1 = list1.get(i).getFirst().assign(orig).permutei(0, 1);
            INDArray p2 = list2.get(i).getFirst().assign(orig).permutei(1, 0);
            Assert.assertEquals(exp01, p1);
            Assert.assertEquals(exp10, p2);
            Assert.assertEquals(3, p1.rows());
            Assert.assertEquals(4, p1.columns());
            Assert.assertEquals(4, p2.rows());
            Assert.assertEquals(3, p2.columns());
        }
        // 2d, v2
        orig = Nd4j.linspace(1, 4, 4).reshape('c', 1, 4);
        exp01 = orig.permute(0, 1);
        exp10 = orig.permute(1, 0);
        list1 = NDArrayCreationUtil.getAllTestMatricesWithShape(1, 4, 12345);
        list2 = NDArrayCreationUtil.getAllTestMatricesWithShape(1, 4, 12345);
        for (int i = 0; i < (list1.size()); i++) {
            INDArray p1 = list1.get(i).getFirst().assign(orig).permutei(0, 1);
            INDArray p2 = list2.get(i).getFirst().assign(orig).permutei(1, 0);
            Assert.assertEquals(exp01, p1);
            Assert.assertEquals(exp10, p2);
            Assert.assertEquals(1, p1.rows());
            Assert.assertEquals(4, p1.columns());
            Assert.assertEquals(4, p2.rows());
            Assert.assertEquals(1, p2.columns());
            Assert.assertTrue(p1.isRowVector());
            Assert.assertFalse(p1.isColumnVector());
            Assert.assertFalse(p2.isRowVector());
            Assert.assertTrue(p2.isColumnVector());
        }
        // 3d:
        INDArray orig3d = Nd4j.linspace(1, ((3 * 4) * 5), ((3 * 4) * 5)).reshape('c', 3, 4, 5);
        INDArray exp012 = orig3d.permute(0, 1, 2);
        INDArray exp021 = orig3d.permute(0, 2, 1);
        INDArray exp120 = orig3d.permute(1, 2, 0);
        INDArray exp102 = orig3d.permute(1, 0, 2);
        INDArray exp201 = orig3d.permute(2, 0, 1);
        INDArray exp210 = orig3d.permute(2, 1, 0);
        List<Pair<INDArray, String>> list012 = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 3, 4, 5);
        List<Pair<INDArray, String>> list021 = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 3, 4, 5);
        List<Pair<INDArray, String>> list120 = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 3, 4, 5);
        List<Pair<INDArray, String>> list102 = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 3, 4, 5);
        List<Pair<INDArray, String>> list201 = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 3, 4, 5);
        List<Pair<INDArray, String>> list210 = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 3, 4, 5);
        for (int i = 0; i < (list012.size()); i++) {
            INDArray p1 = list012.get(i).getFirst().assign(orig3d).permutei(0, 1, 2);
            INDArray p2 = list021.get(i).getFirst().assign(orig3d).permutei(0, 2, 1);
            INDArray p3 = list120.get(i).getFirst().assign(orig3d).permutei(1, 2, 0);
            INDArray p4 = list102.get(i).getFirst().assign(orig3d).permutei(1, 0, 2);
            INDArray p5 = list201.get(i).getFirst().assign(orig3d).permutei(2, 0, 1);
            INDArray p6 = list210.get(i).getFirst().assign(orig3d).permutei(2, 1, 0);
            Assert.assertEquals(exp012, p1);
            Assert.assertEquals(exp021, p2);
            Assert.assertEquals(exp120, p3);
            Assert.assertEquals(exp102, p4);
            Assert.assertEquals(exp201, p5);
            Assert.assertEquals(exp210, p6);
        }
    }

    @Test
    public void testPermuteiShape() {
        INDArray row = Nd4j.create(1, 10);
        INDArray permutedCopy = row.permute(1, 0);
        INDArray permutedInplace = row.permutei(1, 0);
        BaseNd4jTest.assertArrayEquals(new long[]{ 10, 1 }, permutedCopy.shape());
        BaseNd4jTest.assertArrayEquals(new long[]{ 10, 1 }, permutedInplace.shape());
        Assert.assertEquals(10, permutedCopy.rows());
        Assert.assertEquals(10, permutedInplace.rows());
        Assert.assertEquals(1, permutedCopy.columns());
        Assert.assertEquals(1, permutedInplace.columns());
        INDArray col = Nd4j.create(10, 1);
        INDArray cPermutedCopy = col.permute(1, 0);
        INDArray cPermutedInplace = col.permutei(1, 0);
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 10 }, cPermutedCopy.shape());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 10 }, cPermutedInplace.shape());
        Assert.assertEquals(1, cPermutedCopy.rows());
        Assert.assertEquals(1, cPermutedInplace.rows());
        Assert.assertEquals(10, cPermutedCopy.columns());
        Assert.assertEquals(10, cPermutedInplace.columns());
    }

    @Test
    public void testSwapAxes() {
        INDArray n = Nd4j.create(Nd4j.linspace(0, 7, 8).data(), new long[]{ 2, 2, 2 });
        INDArray assertion = n.permute(2, 1, 0);
        INDArray permuteTranspose = assertion.slice(1).slice(1);
        INDArray validate = Nd4j.create(new float[]{ 0, 4, 2, 6, 1, 5, 3, 7 }, new long[]{ 2, 2, 2 });
        Assert.assertEquals(validate, assertion);
        INDArray thirty = Nd4j.linspace(1, 30, 30).reshape(3, 5, 2);
        INDArray swapped = thirty.swapAxes(2, 1);
        INDArray slice = swapped.slice(0).slice(0);
        INDArray assertion2 = Nd4j.create(new double[]{ 1, 3, 5, 7, 9 });
        Assert.assertEquals(assertion2, slice);
    }

    @Test
    public void testMuliRowVector() {
        INDArray arrC = Nd4j.linspace(1, 6, 6).reshape('c', 3, 2);
        INDArray arrF = Nd4j.create(new long[]{ 3, 2 }, 'f').assign(arrC);
        INDArray temp = Nd4j.create(new long[]{ 2, 11 }, 'c');
        INDArray vec = temp.get(NDArrayIndex.all(), NDArrayIndex.interval(9, 10)).transpose();
        vec.assign(Nd4j.linspace(1, 2, 2));
        // Passes if we do one of these...
        // vec = vec.dup('c');
        // vec = vec.dup('f');
        System.out.println(("Vec: " + vec));
        INDArray outC = arrC.muliRowVector(vec);
        INDArray outF = arrF.muliRowVector(vec);
        double[][] expD = new double[][]{ new double[]{ 1, 4 }, new double[]{ 3, 8 }, new double[]{ 5, 12 } };
        INDArray exp = Nd4j.create(expD);
        Assert.assertEquals(exp, outC);
        Assert.assertEquals(exp, outF);
    }

    @Test
    public void testSliceConstructor() throws Exception {
        List<INDArray> testList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            testList.add(Nd4j.scalar((i + 1)));

        INDArray test = Nd4j.create(testList, new long[]{ 1, testList.size() }).reshape(1, 5);
        INDArray expected = Nd4j.create(new float[]{ 1, 2, 3, 4, 5 }, new long[]{ 1, 5 });
        Assert.assertEquals(expected, test);
    }

    @Test
    public void testStdev0() {
        double[][] ind = new double[][]{ new double[]{ 5.1, 3.5, 1.4 }, new double[]{ 4.9, 3.0, 1.4 }, new double[]{ 4.7, 3.2, 1.3 } };
        INDArray in = Nd4j.create(ind);
        INDArray stdev = in.std(0);
        INDArray exp = Nd4j.create(new double[]{ 0.19999999999999973, 0.2516611478423583, 0.057735026918962505 });
        Assert.assertEquals(exp, stdev);
    }

    @Test
    public void testStdev1() {
        double[][] ind = new double[][]{ new double[]{ 5.1, 3.5, 1.4 }, new double[]{ 4.9, 3.0, 1.4 }, new double[]{ 4.7, 3.2, 1.3 } };
        INDArray in = Nd4j.create(ind);
        INDArray stdev = in.std(1);
        log.info("StdDev: {}", stdev.toDoubleVector());
        INDArray exp = Nd4j.create(new double[]{ 1.8556220879622372, 1.7521415467935233, 1.7039170558842744 });
        Assert.assertEquals(exp, stdev);
    }

    @Test
    public void testSignXZ() {
        double[] d = new double[]{ 1.0, -1.1, 1.2, 1.3, -1.4, -1.5, 1.6, -1.7, -1.8, -1.9, -1.01, -1.011 };
        double[] e = new double[]{ 1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 1.0, -1.0, -1.0, -1.0, -1.0, -1.0 };
        INDArray arrF = Nd4j.create(d, new long[]{ 4, 3 }, 'f');
        INDArray arrC = Nd4j.create(new long[]{ 4, 3 }, 'c').assign(arrF);
        INDArray exp = Nd4j.create(e, new long[]{ 4, 3 }, 'f');
        // First: do op with just x (inplace)
        INDArray arrFCopy = arrF.dup('f');
        INDArray arrCCopy = arrC.dup('c');
        Nd4j.getExecutioner().exec(new Sign(arrFCopy));
        Nd4j.getExecutioner().exec(new Sign(arrCCopy));
        Assert.assertEquals(exp, arrFCopy);
        Assert.assertEquals(exp, arrCCopy);
        // Second: do op with both x and z:
        INDArray zOutFC = Nd4j.create(new long[]{ 4, 3 }, 'c');
        INDArray zOutFF = Nd4j.create(new long[]{ 4, 3 }, 'f');
        INDArray zOutCC = Nd4j.create(new long[]{ 4, 3 }, 'c');
        INDArray zOutCF = Nd4j.create(new long[]{ 4, 3 }, 'f');
        Nd4j.getExecutioner().exec(new Sign(arrF, zOutFC));
        Nd4j.getExecutioner().exec(new Sign(arrF, zOutFF));
        Nd4j.getExecutioner().exec(new Sign(arrC, zOutCC));
        Nd4j.getExecutioner().exec(new Sign(arrC, zOutCF));
        Assert.assertEquals(exp, zOutFC);// fails

        Assert.assertEquals(exp, zOutFF);// pass

        Assert.assertEquals(exp, zOutCC);// pass

        Assert.assertEquals(exp, zOutCF);// fails

    }

    @Test
    public void testTanhXZ() {
        INDArray arrC = Nd4j.linspace((-6), 6, 12).reshape('c', 4, 3);
        INDArray arrF = Nd4j.create(new long[]{ 4, 3 }, 'f').assign(arrC);
        double[] d = arrC.data().asDouble();
        double[] e = new double[d.length];
        for (int i = 0; i < (e.length); i++)
            e[i] = Math.tanh(d[i]);

        INDArray exp = Nd4j.create(e, new long[]{ 4, 3 }, 'c');
        // First: do op with just x (inplace)
        INDArray arrFCopy = arrF.dup('f');
        INDArray arrCCopy = arrF.dup('c');
        Nd4j.getExecutioner().exec(new Tanh(arrFCopy));
        Nd4j.getExecutioner().exec(new Tanh(arrCCopy));
        Assert.assertEquals(exp, arrFCopy);
        Assert.assertEquals(exp, arrCCopy);
        // Second: do op with both x and z:
        INDArray zOutFC = Nd4j.create(new long[]{ 4, 3 }, 'c');
        INDArray zOutFF = Nd4j.create(new long[]{ 4, 3 }, 'f');
        INDArray zOutCC = Nd4j.create(new long[]{ 4, 3 }, 'c');
        INDArray zOutCF = Nd4j.create(new long[]{ 4, 3 }, 'f');
        Nd4j.getExecutioner().exec(new Tanh(arrF, zOutFC));
        Nd4j.getExecutioner().exec(new Tanh(arrF, zOutFF));
        Nd4j.getExecutioner().exec(new Tanh(arrC, zOutCC));
        Nd4j.getExecutioner().exec(new Tanh(arrC, zOutCF));
        Assert.assertEquals(exp, zOutFC);// fails

        Assert.assertEquals(exp, zOutFF);// pass

        Assert.assertEquals(exp, zOutCC);// pass

        Assert.assertEquals(exp, zOutCF);// fails

    }

    @Test
    public void testBroadcastDiv() {
        INDArray num = Nd4j.create(new double[]{ 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, -1.0, -1.0, -1.0, -1.0, -2.0, -2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -2.0, -2.0, -2.0, -2.0 }).reshape(2, 16);
        INDArray denom = Nd4j.create(new double[]{ 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0 });
        INDArray expected = Nd4j.create(new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0 }, new long[]{ 2, 16 });
        INDArray actual = Nd4j.getExecutioner().execAndReturn(new BroadcastDivOp(num, denom, num.dup(), (-1)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBroadcastMult() {
        INDArray num = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0, -7.0, -8.0 }).reshape(2, 8);
        INDArray denom = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 });
        INDArray expected = Nd4j.create(new double[]{ 1, 4, 9, 16, 25, 36, 49, 64, -1, -4, -9, -16, -25, -36, -49, -64 }, new long[]{ 2, 8 });
        INDArray actual = Nd4j.getExecutioner().execAndReturn(new BroadcastMulOp(num, denom, num.dup(), (-1)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBroadcastSub() {
        INDArray num = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0, -7.0, -8.0 }).reshape(2, 8);
        INDArray denom = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 });
        INDArray expected = Nd4j.create(new double[]{ 0, 0, 0, 0, 0, 0, 0, 0, -2, -4, -6, -8, -10, -12, -14, -16 }, new long[]{ 2, 8 });
        INDArray actual = Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(num, denom, num.dup(), (-1)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBroadcastAdd() {
        INDArray num = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0, -7.0, -8.0 }).reshape(2, 8);
        INDArray denom = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 });
        INDArray expected = Nd4j.create(new double[]{ 2, 4, 6, 8, 10, 12, 14, 16, 0, 0, 0, 0, 0, 0, 0, 0 }, new long[]{ 2, 8 });
        INDArray dup = num.dup();
        INDArray actual = Nd4j.getExecutioner().execAndReturn(new BroadcastAddOp(num, denom, dup, (-1)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDimension() {
        INDArray test = Nd4j.create(Nd4j.linspace(1, 4, 4).data(), new long[]{ 2, 2 });
        // row
        INDArray slice0 = test.slice(0, 1);
        INDArray slice02 = test.slice(1, 1);
        INDArray assertSlice0 = Nd4j.create(new float[]{ 1, 3 });
        INDArray assertSlice02 = Nd4j.create(new float[]{ 2, 4 });
        Assert.assertEquals(assertSlice0, slice0);
        Assert.assertEquals(assertSlice02, slice02);
        // column
        INDArray assertSlice1 = Nd4j.create(new float[]{ 1, 2 });
        INDArray assertSlice12 = Nd4j.create(new float[]{ 3, 4 });
        INDArray slice1 = test.slice(0, 0);
        INDArray slice12 = test.slice(1, 0);
        Assert.assertEquals(assertSlice1, slice1);
        Assert.assertEquals(assertSlice12, slice12);
        INDArray arr = Nd4j.create(Nd4j.linspace(1, 24, 24).data(), new long[]{ 4, 3, 2 });
        INDArray secondSliceFirstDimension = arr.slice(1, 1);
        Assert.assertEquals(secondSliceFirstDimension, secondSliceFirstDimension);
    }

    @Test
    public void testReshape() {
        INDArray arr = Nd4j.create(Nd4j.linspace(1, 24, 24).data(), new long[]{ 4, 3, 2 });
        INDArray reshaped = arr.reshape(2, 3, 4);
        Assert.assertEquals(arr.length(), reshaped.length());
        Assert.assertEquals(true, Arrays.equals(new long[]{ 4, 3, 2 }, arr.shape()));
        Assert.assertEquals(true, Arrays.equals(new long[]{ 2, 3, 4 }, reshaped.shape()));
    }

    @Test
    public void testDot() {
        INDArray vec1 = Nd4j.create(new float[]{ 1, 2, 3, 4 });
        INDArray vec2 = Nd4j.create(new float[]{ 1, 2, 3, 4 });
        Assert.assertEquals(30, Nd4j.getBlasWrapper().dot(vec1, vec2), 0.1);
        INDArray matrix = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray row = matrix.getRow(1);
        Assert.assertEquals(25, Nd4j.getBlasWrapper().dot(row, row), 0.1);
    }

    @Test
    public void testIdentity() {
        INDArray eye = Nd4j.eye(5);
        Assert.assertTrue(Arrays.equals(new long[]{ 5, 5 }, eye.shape()));
        eye = Nd4j.eye(5);
        Assert.assertTrue(Arrays.equals(new long[]{ 5, 5 }, eye.shape()));
    }

    @Test
    public void testTemp() {
        Nd4j.getRandom().setSeed(12345);
        INDArray in = Nd4j.rand(new long[]{ 2, 2, 2 });
        System.out.println(("In:\n" + in));
        INDArray permuted = in.permute(0, 2, 1);// Permute, so we get correct order after reshaping

        INDArray out = permuted.reshape(4, 2);
        System.out.println(("Out:\n" + out));
        int countZero = 0;
        for (int i = 0; i < 8; i++)
            if ((out.getDouble(i)) == 0.0)
                countZero++;


        Assert.assertEquals(countZero, 0);
    }

    @Test
    public void testMeans() {
        INDArray a = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray mean1 = a.mean(1);
        Assert.assertEquals(getFailureMessage(), Nd4j.create(new double[]{ 1.5, 3.5 }), mean1);
        Assert.assertEquals(getFailureMessage(), Nd4j.create(new double[]{ 2, 3 }), a.mean(0));
        Assert.assertEquals(getFailureMessage(), 2.5, Nd4j.linspace(1, 4, 4).meanNumber().doubleValue(), 0.1);
        Assert.assertEquals(getFailureMessage(), 2.5, a.meanNumber().doubleValue(), 0.1);
    }

    @Test
    public void testSums() {
        INDArray a = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        Assert.assertEquals(getFailureMessage(), Nd4j.create(new float[]{ 3, 7 }), a.sum(1));
        Assert.assertEquals(getFailureMessage(), Nd4j.create(new float[]{ 4, 6 }), a.sum(0));
        Assert.assertEquals(getFailureMessage(), 10, a.sumNumber().doubleValue(), 0.1);
    }

    @Test
    public void testRSubi() {
        INDArray n2 = Nd4j.ones(2);
        INDArray n2Assertion = Nd4j.zeros(2);
        INDArray nRsubi = n2.rsubi(1);
        Assert.assertEquals(n2Assertion, nRsubi);
    }

    @Test
    public void testConcat() {
        INDArray A = Nd4j.linspace(1, 8, 8).reshape(2, 2, 2);
        INDArray B = Nd4j.linspace(1, 12, 12).reshape(3, 2, 2);
        INDArray concat = Nd4j.concat(0, A, B);
        Assert.assertTrue(Arrays.equals(new long[]{ 5, 2, 2 }, concat.shape()));
        INDArray columnConcat = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray concatWith = Nd4j.zeros(2, 3);
        INDArray columnWiseConcat = Nd4j.concat(0, columnConcat, concatWith);
        System.out.println(columnConcat);
    }

    @Test
    public void testConcatHorizontally() {
        INDArray rowVector = Nd4j.ones(5);
        INDArray other = Nd4j.ones(5);
        INDArray concat = Nd4j.hstack(other, rowVector);
        Assert.assertEquals(rowVector.rows(), concat.rows());
        Assert.assertEquals(((rowVector.columns()) * 2), concat.columns());
    }

    @Test
    public void testArgMaxSameValues() {
        // Here: assume that by convention, argmax returns the index of the FIRST maximum value
        // Thus, argmax(ones(...)) = 0 by convention
        INDArray arr = Nd4j.ones(10);
        for (int i = 0; i < 10; i++) {
            double argmax = Nd4j.argMax(arr, 1).getDouble(0);
            // System.out.println(argmax);
            Assert.assertEquals(0.0, argmax, 0.0);
        }
    }

    @Test
    public void testSoftmaxStability() {
        INDArray input = Nd4j.create(new double[]{ -0.75, 0.58, 0.42, 1.03, -0.61, 0.19, -0.37, -0.4, -1.42, -0.04 }).transpose();
        System.out.println(("Input transpose " + (Shape.shapeToString(input.shapeInfo()))));
        INDArray output = Nd4j.create(10, 1);
        System.out.println(("Element wise stride of output " + (output.elementWiseStride())));
        Nd4j.getExecutioner().exec(new OldSoftMax(input, output));
    }

    @Test
    public void testAssignOffset() {
        INDArray arr = Nd4j.ones(5, 5);
        INDArray row = arr.slice(1);
        row.assign(1);
        Assert.assertEquals(Nd4j.ones(5), row);
    }

    @Test
    public void testAddScalar() {
        INDArray div = Nd4j.valueArrayOf(new long[]{ 1, 4 }, 4);
        INDArray rdiv = div.add(1);
        INDArray answer = Nd4j.valueArrayOf(new long[]{ 1, 4 }, 5);
        Assert.assertEquals(answer, rdiv);
    }

    @Test
    public void testRdivScalar() {
        INDArray div = Nd4j.valueArrayOf(new long[]{ 1, 4 }, 4);
        INDArray rdiv = div.rdiv(1);
        INDArray answer = Nd4j.valueArrayOf(new long[]{ 1, 4 }, 0.25);
        Assert.assertEquals(rdiv, answer);
    }

    @Test
    public void testRDivi() {
        INDArray n2 = Nd4j.valueArrayOf(new long[]{ 1, 2 }, 4);
        INDArray n2Assertion = Nd4j.valueArrayOf(new long[]{ 1, 2 }, 0.5);
        INDArray nRsubi = n2.rdivi(2);
        Assert.assertEquals(n2Assertion, nRsubi);
    }

    @Test
    public void testElementWiseAdd() {
        INDArray linspace = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray linspace2 = linspace.dup();
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 2, 4 }, new double[]{ 6, 8 } });
        linspace.addi(linspace2);
        Assert.assertEquals(assertion, linspace);
    }

    @Test
    public void testSquareMatrix() {
        INDArray n = Nd4j.create(Nd4j.linspace(1, 8, 8).data(), new long[]{ 2, 2, 2 });
        INDArray eightFirstTest = n.vectorAlongDimension(0, 2);
        INDArray eightFirstAssertion = Nd4j.create(new float[]{ 1, 2 }, new long[]{ 1, 2 });
        Assert.assertEquals(eightFirstAssertion, eightFirstTest);
        INDArray eightFirstTestSecond = n.vectorAlongDimension(1, 2);
        INDArray eightFirstTestSecondAssertion = Nd4j.create(new float[]{ 3, 4 });
        Assert.assertEquals(eightFirstTestSecondAssertion, eightFirstTestSecond);
    }

    @Test
    public void testNumVectorsAlongDimension() {
        INDArray arr = Nd4j.linspace(1, 24, 24).reshape(4, 3, 2);
        Assert.assertEquals(12, arr.vectorsAlongDimension(2));
    }

    @Test
    public void testBroadCast() {
        INDArray n = Nd4j.linspace(1, 4, 4);
        INDArray broadCasted = n.broadcast(5, 4);
        for (int i = 0; i < (broadCasted.rows()); i++) {
            INDArray row = broadCasted.getRow(i);
            Assert.assertEquals(n, broadCasted.getRow(i));
        }
        INDArray broadCast2 = broadCasted.getRow(0).broadcast(5, 4);
        Assert.assertEquals(broadCasted, broadCast2);
        INDArray columnBroadcast = n.transpose().broadcast(4, 5);
        for (int i = 0; i < (columnBroadcast.columns()); i++) {
            INDArray column = columnBroadcast.getColumn(i);
            Assert.assertEquals(column, n.transpose());
        }
        INDArray fourD = Nd4j.create(1, 2, 1, 1);
        INDArray broadCasted3 = fourD.broadcast(1, 2, 36, 36);
        Assert.assertTrue(Arrays.equals(new long[]{ 1, 2, 36, 36 }, broadCasted3.shape()));
        INDArray ones = Nd4j.ones(1, 1, 1).broadcast(2, 1, 1);
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 1, 1 }, ones.shape());
    }

    @Test
    public void testScalarBroadcast() {
        INDArray fiveThree = Nd4j.ones(5, 3);
        INDArray fiveThreeTest = Nd4j.scalar(1.0).broadcast(5, 3);
        Assert.assertEquals(fiveThree, fiveThreeTest);
    }

    @Test
    public void testPutRowGetRowOrdering() {
        INDArray row1 = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray put = Nd4j.create(new double[]{ 5, 6 });
        row1.putRow(1, put);
        INDArray row1Fortran = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray putFortran = Nd4j.create(new double[]{ 5, 6 });
        row1Fortran.putRow(1, putFortran);
        Assert.assertEquals(row1, row1Fortran);
        INDArray row1CTest = row1.getRow(1);
        INDArray row1FortranTest = row1Fortran.getRow(1);
        Assert.assertEquals(row1CTest, row1FortranTest);
    }

    @Test
    public void testElementWiseOps() {
        INDArray n1 = Nd4j.scalar(1);
        INDArray n2 = Nd4j.scalar(2);
        INDArray nClone = n1.add(n2);
        Assert.assertEquals(Nd4j.scalar(3), nClone);
        Assert.assertFalse(n1.add(n2).equals(n1));
        INDArray n3 = Nd4j.scalar(3);
        INDArray n4 = Nd4j.scalar(4);
        INDArray subbed = n4.sub(n3);
        INDArray mulled = n4.mul(n3);
        INDArray div = n4.div(n3);
        Assert.assertFalse(subbed.equals(n4));
        Assert.assertFalse(mulled.equals(n4));
        Assert.assertEquals(Nd4j.scalar(1), subbed);
        Assert.assertEquals(Nd4j.scalar(12), mulled);
        Assert.assertEquals(Nd4j.scalar(1.3333333333333333), div);
    }

    @Test
    public void testNdArrayCreation() {
        double delta = 0.1;
        INDArray n1 = Nd4j.create(new double[]{ 0.0, 1.0, 2.0, 3.0 }, new long[]{ 2, 2 }, 'c');
        INDArray lv = n1.linearView();
        Assert.assertEquals(0.0, lv.getDouble(0), delta);
        Assert.assertEquals(1.0, lv.getDouble(1), delta);
        Assert.assertEquals(2.0, lv.getDouble(2), delta);
        Assert.assertEquals(3.0, lv.getDouble(3), delta);
    }

    @Test
    public void testToFlattenedWithOrder() {
        int[] firstShape = new int[]{ 10, 3 };
        int firstLen = ArrayUtil.prod(firstShape);
        int[] secondShape = new int[]{ 2, 7 };
        int secondLen = ArrayUtil.prod(secondShape);
        int[] thirdShape = new int[]{ 3, 3 };
        int thirdLen = ArrayUtil.prod(thirdShape);
        INDArray firstC = Nd4j.linspace(1, firstLen, firstLen).reshape('c', firstShape);
        INDArray firstF = Nd4j.create(firstShape, 'f').assign(firstC);
        INDArray secondC = Nd4j.linspace(1, secondLen, secondLen).reshape('c', secondShape);
        INDArray secondF = Nd4j.create(secondShape, 'f').assign(secondC);
        INDArray thirdC = Nd4j.linspace(1, thirdLen, thirdLen).reshape('c', thirdShape);
        INDArray thirdF = Nd4j.create(thirdShape, 'f').assign(thirdC);
        Assert.assertEquals(firstC, firstF);
        Assert.assertEquals(secondC, secondF);
        Assert.assertEquals(thirdC, thirdF);
        INDArray cc = Nd4j.toFlattened('c', firstC, secondC, thirdC);
        INDArray cf = Nd4j.toFlattened('c', firstF, secondF, thirdF);
        Assert.assertEquals(cc, cf);
        INDArray cmixed = Nd4j.toFlattened('c', firstC, secondF, thirdF);
        Assert.assertEquals(cc, cmixed);
        INDArray fc = Nd4j.toFlattened('f', firstC, secondC, thirdC);
        Assert.assertNotEquals(cc, fc);
        INDArray ff = Nd4j.toFlattened('f', firstF, secondF, thirdF);
        Assert.assertEquals(fc, ff);
        INDArray fmixed = Nd4j.toFlattened('f', firstC, secondF, thirdF);
        Assert.assertEquals(fc, fmixed);
    }

    @Test
    public void testLeakyRelu() {
        INDArray arr = Nd4j.linspace((-1), 1, 10);
        double[] expected = new double[10];
        for (int i = 0; i < 10; i++) {
            double in = arr.getDouble(i);
            expected[i] = (in <= 0.0) ? 0.01 * in : in;
        }
        INDArray out = Nd4j.getExecutioner().execAndReturn(new LeakyReLU(arr, 0.01));
        INDArray exp = Nd4j.create(expected);
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testSoftmaxRow() {
        for (int i = 0; i < 20; i++) {
            INDArray arr1 = Nd4j.zeros(100);
            Nd4j.getExecutioner().execAndReturn(new OldSoftMax(arr1));
            System.out.println(Arrays.toString(arr1.data().asFloat()));
        }
    }

    @Test
    public void testLeakyRelu2() {
        INDArray arr = Nd4j.linspace((-1), 1, 10);
        double[] expected = new double[10];
        for (int i = 0; i < 10; i++) {
            double in = arr.getDouble(i);
            expected[i] = (in <= 0.0) ? 0.01 * in : in;
        }
        INDArray out = Nd4j.getExecutioner().execAndReturn(new LeakyReLU(arr, 0.01));
        System.out.println(("Expected: " + (Arrays.toString(expected))));
        System.out.println(("Actual:   " + (Arrays.toString(out.data().asDouble()))));
        INDArray exp = Nd4j.create(expected);
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testDupAndDupWithOrder() {
        List<Pair<INDArray, String>> testInputs = NDArrayCreationUtil.getAllTestMatricesWithShape(ordering(), 4, 5, 123);
        for (Pair<INDArray, String> pair : testInputs) {
            String msg = pair.getSecond();
            INDArray in = pair.getFirst();
            INDArray dup = in.dup();
            INDArray dupc = in.dup('c');
            INDArray dupf = in.dup('f');
            Assert.assertEquals(dup.ordering(), ordering());
            Assert.assertEquals(dupc.ordering(), 'c');
            Assert.assertEquals(dupf.ordering(), 'f');
            Assert.assertEquals(msg, in, dupc);
            Assert.assertEquals(msg, in, dupf);
        }
    }

    @Test
    public void testToOffsetZeroCopy() {
        List<Pair<INDArray, String>> testInputs = NDArrayCreationUtil.getAllTestMatricesWithShape(ordering(), 4, 5, 123);
        for (int i = 0; i < (testInputs.size()); i++) {
            Pair<INDArray, String> pair = testInputs.get(i);
            String msg = pair.getSecond();
            msg += "Failed on " + i;
            INDArray in = pair.getFirst();
            INDArray dup = Shape.toOffsetZeroCopy(in, ordering());
            INDArray dupc = Shape.toOffsetZeroCopy(in, 'c');
            INDArray dupf = Shape.toOffsetZeroCopy(in, 'f');
            INDArray dupany = Shape.toOffsetZeroCopyAnyOrder(in);
            Assert.assertEquals(msg, in, dup);
            Assert.assertEquals(msg, in, dupc);
            Assert.assertEquals(msg, in, dupf);
            Assert.assertEquals(msg, dupc.ordering(), 'c');
            Assert.assertEquals(msg, dupf.ordering(), 'f');
            Assert.assertEquals(msg, in, dupany);
            Assert.assertEquals(dup.offset(), 0);
            Assert.assertEquals(dupc.offset(), 0);
            Assert.assertEquals(dupf.offset(), 0);
            Assert.assertEquals(dupany.offset(), 0);
            Assert.assertEquals(dup.length(), dup.data().length());
            Assert.assertEquals(dupc.length(), dupc.data().length());
            Assert.assertEquals(dupf.length(), dupf.data().length());
            Assert.assertEquals(dupany.length(), dupany.data().length());
        }
    }

    @Test
    public void testTensorStats() {
        List<Pair<INDArray, String>> testInputs = NDArrayCreationUtil.getAllTestMatricesWithShape(9, 13, 123);
        for (Pair<INDArray, String> pair : testInputs) {
            INDArray arr = pair.getFirst();
            String msg = pair.getSecond();
            val nTAD0 = arr.tensorssAlongDimension(0);
            val nTAD1 = arr.tensorssAlongDimension(1);
            OpExecutionerUtil.Tensor1DStats t0 = OpExecutionerUtil.get1DTensorStats(arr, 0);
            OpExecutionerUtil.Tensor1DStats t1 = OpExecutionerUtil.get1DTensorStats(arr, 1);
            Assert.assertEquals(nTAD0, t0.getNumTensors());
            Assert.assertEquals(nTAD1, t1.getNumTensors());
            INDArray tFirst0 = arr.tensorAlongDimension(0, 0);
            INDArray tSecond0 = arr.tensorAlongDimension(1, 0);
            INDArray tFirst1 = arr.tensorAlongDimension(0, 1);
            INDArray tSecond1 = arr.tensorAlongDimension(1, 1);
            Assert.assertEquals(tFirst0.offset(), t0.getFirstTensorOffset());
            Assert.assertEquals(tFirst1.offset(), t1.getFirstTensorOffset());
            long separation0 = (tSecond0.offset()) - (tFirst0.offset());
            long separation1 = (tSecond1.offset()) - (tFirst1.offset());
            Assert.assertEquals(separation0, t0.getTensorStartSeparation());
            Assert.assertEquals(separation1, t1.getTensorStartSeparation());
            for (int i = 0; i < nTAD0; i++) {
                INDArray tad0 = arr.tensorAlongDimension(i, 0);
                Assert.assertEquals(tad0.length(), t0.getTensorLength());
                Assert.assertEquals(tad0.elementWiseStride(), t0.getElementWiseStride());
                long offset = tad0.offset();
                long calcOffset = (t0.getFirstTensorOffset()) + (i * (t0.getTensorStartSeparation()));
                Assert.assertEquals(offset, calcOffset);
            }
            for (int i = 0; i < nTAD1; i++) {
                INDArray tad1 = arr.tensorAlongDimension(i, 1);
                Assert.assertEquals(tad1.length(), t1.getTensorLength());
                Assert.assertEquals(tad1.elementWiseStride(), t1.getElementWiseStride());
                long offset = tad1.offset();
                long calcOffset = (t1.getFirstTensorOffset()) + (i * (t1.getTensorStartSeparation()));
                Assert.assertEquals(offset, calcOffset);
            }
        }
    }

    @Test
    public void testAssignNumber() {
        int nRows = 10;
        int nCols = 20;
        INDArray in = Nd4j.linspace(1, (nRows * nCols), (nRows * nCols)).reshape('c', new long[]{ nRows, nCols });
        INDArray subset1 = in.get(NDArrayIndex.interval(0, 1), NDArrayIndex.interval(0, (nCols / 2)));
        subset1.assign(1.0);
        INDArray subset2 = in.get(NDArrayIndex.interval(5, 8), NDArrayIndex.interval((nCols / 2), nCols));
        subset2.assign(2.0);
        INDArray assertion = Nd4j.create(new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0, 50.0, 51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 57.0, 58.0, 59.0, 60.0, 61.0, 62.0, 63.0, 64.0, 65.0, 66.0, 67.0, 68.0, 69.0, 70.0, 71.0, 72.0, 73.0, 74.0, 75.0, 76.0, 77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 86.0, 87.0, 88.0, 89.0, 90.0, 91.0, 92.0, 93.0, 94.0, 95.0, 96.0, 97.0, 98.0, 99.0, 100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0, 110.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 121.0, 122.0, 123.0, 124.0, 125.0, 126.0, 127.0, 128.0, 129.0, 130.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 141.0, 142.0, 143.0, 144.0, 145.0, 146.0, 147.0, 148.0, 149.0, 150.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 161.0, 162.0, 163.0, 164.0, 165.0, 166.0, 167.0, 168.0, 169.0, 170.0, 171.0, 172.0, 173.0, 174.0, 175.0, 176.0, 177.0, 178.0, 179.0, 180.0, 181.0, 182.0, 183.0, 184.0, 185.0, 186.0, 187.0, 188.0, 189.0, 190.0, 191.0, 192.0, 193.0, 194.0, 195.0, 196.0, 197.0, 198.0, 199.0, 200.0 }, in.shape(), 0, 'c');
        Assert.assertEquals(assertion, in);
    }

    @Test
    public void testSumDifferentOrdersSquareMatrix() {
        INDArray arrc = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray arrf = Nd4j.create(new long[]{ 2, 2 }, 'f').assign(arrc);
        INDArray cSum = arrc.sum(0);
        INDArray fSum = arrf.sum(0);
        Assert.assertEquals(arrc, arrf);
        Assert.assertEquals(cSum, fSum);// Expect: 4,6. Getting [4, 4] for f order

    }

    @Test
    public void testAssignMixedC() {
        int[] shape1 = new int[]{ 3, 2, 2, 2, 2, 2 };
        int[] shape2 = new int[]{ 12, 8 };
        int length = ArrayUtil.prod(shape1);
        Assert.assertEquals(ArrayUtil.prod(shape1), ArrayUtil.prod(shape2));
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape1);
        INDArray arr2c = Nd4j.create(shape2, 'c');
        INDArray arr2f = Nd4j.create(shape2, 'f');
        log.info("2f data: {}", Arrays.toString(arr2f.data().asFloat()));
        arr2c.assign(arr);
        System.out.println("--------------");
        arr2f.assign(arr);
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape2);
        log.info("arr data: {}", Arrays.toString(arr.data().asFloat()));
        log.info("2c data: {}", Arrays.toString(arr2c.data().asFloat()));
        log.info("2f data: {}", Arrays.toString(arr2f.data().asFloat()));
        log.info("2c shape: {}", Arrays.toString(arr2c.shapeInfoDataBuffer().asInt()));
        log.info("2f shape: {}", Arrays.toString(arr2f.shapeInfoDataBuffer().asInt()));
        Assert.assertEquals(exp, arr2c);
        Assert.assertEquals(exp, arr2f);
    }

    @Test
    public void testDummy() {
        INDArray arr2f = Nd4j.create(new double[]{ 1.0, 13.0, 25.0, 37.0, 49.0, 61.0, 73.0, 85.0, 2.0, 14.0, 26.0, 38.0, 50.0, 62.0, 74.0, 86.0, 3.0, 15.0, 27.0, 39.0, 51.0, 63.0, 75.0, 87.0, 4.0, 16.0, 28.0, 40.0, 52.0, 64.0, 76.0, 88.0, 5.0, 17.0, 29.0, 41.0, 53.0, 65.0, 77.0, 89.0, 6.0, 18.0, 30.0, 42.0, 54.0, 66.0, 78.0, 90.0, 7.0, 19.0, 31.0, 43.0, 55.0, 67.0, 79.0, 91.0, 8.0, 20.0, 32.0, 44.0, 56.0, 68.0, 80.0, 92.0, 9.0, 21.0, 33.0, 45.0, 57.0, 69.0, 81.0, 93.0, 10.0, 22.0, 34.0, 46.0, 58.0, 70.0, 82.0, 94.0, 11.0, 23.0, 35.0, 47.0, 59.0, 71.0, 83.0, 95.0, 12.0, 24.0, 36.0, 48.0, 60.0, 72.0, 84.0, 96.0 }, new long[]{ 12, 8 }, 'f');
        log.info("arr2f shape: {}", Arrays.toString(arr2f.shapeInfoDataBuffer().asInt()));
        log.info("arr2f data: {}", Arrays.toString(arr2f.data().asFloat()));
        log.info("render: {}", arr2f);
        log.info("----------------------");
        INDArray array = Nd4j.linspace(1, 96, 96).reshape('c', 12, 8);
        log.info("array render: {}", array);
        log.info("----------------------");
        INDArray arrayf = array.dup('f');
        log.info("arrayf render: {}", arrayf);
        log.info("arrayf shape: {}", Arrays.toString(arrayf.shapeInfoDataBuffer().asInt()));
        log.info("arrayf data: {}", Arrays.toString(arrayf.data().asFloat()));
    }

    @Test
    public void testPairwiseMixedC() {
        int[] shape2 = new int[]{ 12, 8 };
        int length = ArrayUtil.prod(shape2);
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape2);
        INDArray arr2c = arr.dup('c');
        INDArray arr2f = arr.dup('f');
        arr2c.addi(arr);
        System.out.println("--------------");
        arr2f.addi(arr);
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape2).mul(2.0);
        Assert.assertEquals(exp, arr2c);
        Assert.assertEquals(exp, arr2f);
        log.info("2c data: {}", Arrays.toString(arr2c.data().asFloat()));
        log.info("2f data: {}", Arrays.toString(arr2f.data().asFloat()));
        Assert.assertTrue(Nd4jTestsC.arrayNotEquals(arr2c.data().asFloat(), arr2f.data().asFloat(), 1.0E-5F));
    }

    @Test
    public void testPairwiseMixedF() {
        int[] shape2 = new int[]{ 12, 8 };
        int length = ArrayUtil.prod(shape2);
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape2).dup('f');
        INDArray arr2c = arr.dup('c');
        INDArray arr2f = arr.dup('f');
        arr2c.addi(arr);
        System.out.println("--------------");
        arr2f.addi(arr);
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape2).dup('f').mul(2.0);
        Assert.assertEquals(exp, arr2c);
        Assert.assertEquals(exp, arr2f);
        log.info("2c data: {}", Arrays.toString(arr2c.data().asFloat()));
        log.info("2f data: {}", Arrays.toString(arr2f.data().asFloat()));
        Assert.assertTrue(Nd4jTestsC.arrayNotEquals(arr2c.data().asFloat(), arr2f.data().asFloat(), 1.0E-5F));
    }

    @Test
    public void testAssign2D() {
        int[] shape2 = new int[]{ 8, 4 };
        int length = ArrayUtil.prod(shape2);
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape2);
        INDArray arr2c = Nd4j.create(shape2, 'c');
        INDArray arr2f = Nd4j.create(shape2, 'f');
        arr2c.assign(arr);
        System.out.println("--------------");
        arr2f.assign(arr);
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape2);
        Assert.assertEquals(exp, arr2c);
        Assert.assertEquals(exp, arr2f);
    }

    @Test
    public void testAssign2D_2() {
        int[] shape2 = new int[]{ 8, 4 };
        int length = ArrayUtil.prod(shape2);
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape2);
        INDArray arr2c = Nd4j.create(shape2, 'c');
        INDArray arr2f = Nd4j.create(shape2, 'f');
        INDArray z_f = Nd4j.create(shape2, 'f');
        INDArray z_c = Nd4j.create(shape2, 'c');
        Nd4j.getExecutioner().exec(new java.util.Set(arr2f, arr, z_f, arr2c.length()));
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().exec(new java.util.Set(arr2f, arr, z_c, arr2c.length()));
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape2);
        System.out.println(("Zf data: " + (Arrays.toString(z_f.data().asFloat()))));
        System.out.println(("Zc data: " + (Arrays.toString(z_c.data().asFloat()))));
        Assert.assertEquals(exp, z_f);
        Assert.assertEquals(exp, z_c);
    }

    @Test
    public void testAssign3D_2() {
        int[] shape3 = new int[]{ 8, 4, 8 };
        int length = ArrayUtil.prod(shape3);
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape3).dup('f');
        INDArray arr3c = Nd4j.create(shape3, 'c');
        INDArray arr3f = Nd4j.create(shape3, 'f');
        Nd4j.getExecutioner().exec(new java.util.Set(arr3c, arr, arr3f, arr3c.length()));
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().exec(new java.util.Set(arr3f, arr, arr3c, arr3c.length()));
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape3);
        Assert.assertEquals(exp, arr3c);
        Assert.assertEquals(exp, arr3f);
    }

    @Test
    public void testSumDifferentOrders() {
        INDArray arrc = Nd4j.linspace(1, 6, 6).reshape('c', 3, 2);
        INDArray arrf = Nd4j.create(new double[6], new long[]{ 3, 2 }, 'f').assign(arrc);
        Assert.assertEquals(arrc, arrf);
        INDArray cSum = arrc.sum(0);
        INDArray fSum = arrf.sum(0);
        Assert.assertEquals(cSum, fSum);// Expect: 0.51, 1.79; getting [0.51,1.71] for f order

    }

    @Test
    public void testCreateUnitialized() {
        INDArray arrC = Nd4j.createUninitialized(new long[]{ 10, 10 }, 'c');
        INDArray arrF = Nd4j.createUninitialized(new long[]{ 10, 10 }, 'f');
        Assert.assertEquals('c', arrC.ordering());
        BaseNd4jTest.assertArrayEquals(new long[]{ 10, 10 }, arrC.shape());
        Assert.assertEquals('f', arrF.ordering());
        BaseNd4jTest.assertArrayEquals(new long[]{ 10, 10 }, arrF.shape());
        // Can't really test that it's *actually* uninitialized...
        arrC.assign(0);
        arrF.assign(0);
        Assert.assertEquals(Nd4j.create(new long[]{ 10, 10 }), arrC);
        Assert.assertEquals(Nd4j.create(new long[]{ 10, 10 }), arrF);
    }

    @Test
    public void testVarConst() {
        INDArray x = Nd4j.linspace(1, 100, 100).reshape(10, 10);
        System.out.println(x);
        Assert.assertFalse(Double.isNaN(x.var(0).sumNumber().doubleValue()));
        System.out.println(x.var(0));
        Assert.assertFalse(Double.isNaN(x.var(1).sumNumber().doubleValue()));
        System.out.println(x.var(1));
        System.out.println("=================================");
        // 2d array - all elements are the same
        INDArray a = Nd4j.ones(10, 10).mul(10);
        System.out.println(a);
        Assert.assertFalse(Double.isNaN(a.var(0).sumNumber().doubleValue()));
        System.out.println(a.var(0));
        Assert.assertFalse(Double.isNaN(a.var(1).sumNumber().doubleValue()));
        System.out.println(a.var(1));
        // 2d array - constant in one dimension
        System.out.println("=================================");
        INDArray nums = Nd4j.linspace(1, 10, 10);
        INDArray b = Nd4j.ones(10, 10).mulRowVector(nums);
        System.out.println(b);
        Assert.assertFalse(Double.isNaN(((Double) (b.var(0).sumNumber()))));
        System.out.println(b.var(0));
        Assert.assertFalse(Double.isNaN(((Double) (b.var(1).sumNumber()))));
        System.out.println(b.var(1));
        System.out.println("=================================");
        System.out.println(b.transpose());
        Assert.assertFalse(Double.isNaN(((Double) (b.transpose().var(0).sumNumber()))));
        System.out.println(b.transpose().var(0));
        Assert.assertFalse(Double.isNaN(((Double) (b.transpose().var(1).sumNumber()))));
        System.out.println(b.transpose().var(1));
    }

    @Test
    public void testVPull1() {
        int[] indexes = new int[]{ 0, 2, 4 };
        INDArray array = Nd4j.linspace(1, 25, 25).reshape(5, 5);
        INDArray assertion = Nd4j.createUninitialized(new long[]{ 3, 5 }, 'f');
        for (int i = 0; i < 3; i++) {
            assertion.putRow(i, array.getRow(indexes[i]));
        }
        INDArray result = Nd4j.pullRows(array, 1, indexes, 'f');
        Assert.assertEquals(3, result.rows());
        Assert.assertEquals(5, result.columns());
        Assert.assertEquals(assertion, result);
    }

    @Test(expected = IllegalStateException.class)
    public void testPullRowsValidation1() {
        Nd4j.pullRows(Nd4j.create(10, 10), 2, new int[]{ 0, 1, 2 });
    }

    @Test(expected = IllegalStateException.class)
    public void testPullRowsValidation2() {
        Nd4j.pullRows(Nd4j.create(10, 10), 1, new int[]{ 0, -1, 2 });
    }

    @Test(expected = IllegalStateException.class)
    public void testPullRowsValidation3() {
        Nd4j.pullRows(Nd4j.create(10, 10), 1, new int[]{ 0, 1, 10 });
    }

    @Test(expected = IllegalStateException.class)
    public void testPullRowsValidation4() {
        Nd4j.pullRows(Nd4j.create(3, 10), 1, new int[]{ 0, 1, 2, 3 });
    }

    @Test(expected = IllegalStateException.class)
    public void testPullRowsValidation5() {
        Nd4j.pullRows(Nd4j.create(3, 10), 1, new int[]{ 0, 1, 2 }, 'e');
    }

    @Test
    public void testVPull2() {
        val indexes = new int[]{ 0, 2, 4 };
        INDArray array = Nd4j.linspace(1, 25, 25).reshape(5, 5);
        INDArray assertion = Nd4j.createUninitialized(new long[]{ 3, 5 }, 'c');
        for (int i = 0; i < 3; i++) {
            assertion.putRow(i, array.getRow(indexes[i]));
        }
        INDArray result = Nd4j.pullRows(array, 1, indexes, 'c');
        Assert.assertEquals(3, result.rows());
        Assert.assertEquals(5, result.columns());
        Assert.assertEquals(assertion, result);
        System.out.println(assertion.toString());
        System.out.println(result.toString());
    }

    @Test
    public void testCompareAndSet1() {
        INDArray array = Nd4j.zeros(25);
        INDArray assertion = Nd4j.zeros(25);
        array.putScalar(0, 0.1F);
        array.putScalar(10, 0.1F);
        array.putScalar(20, 0.1F);
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.comparison.CompareAndSet(array, 0.1, 0.0, 0.01));
        Assert.assertEquals(assertion, array);
    }

    @Test
    public void testReplaceNaNs() {
        INDArray array = Nd4j.zeros(25);
        INDArray assertion = Nd4j.zeros(25);
        array.putScalar(0, Float.NaN);
        array.putScalar(10, Float.NaN);
        array.putScalar(20, Float.NaN);
        Assert.assertNotEquals(assertion, array);
        Nd4j.getExecutioner().exec(new ReplaceNans(array, 0.0));
        System.out.println(("Array After: " + array));
        Assert.assertEquals(assertion, array);
    }

    @Test
    public void testNaNEquality() {
        INDArray array = Nd4j.zeros(25);
        INDArray assertion = Nd4j.zeros(25);
        array.putScalar(0, Float.NaN);
        array.putScalar(10, Float.NaN);
        array.putScalar(20, Float.NaN);
        Assert.assertNotEquals(assertion, array);
    }

    @Test
    public void testSingleDeviceAveraging() throws Exception {
        int LENGTH = (512 * 1024) * 2;
        INDArray array1 = Nd4j.valueArrayOf(LENGTH, 1.0);
        INDArray array2 = Nd4j.valueArrayOf(LENGTH, 2.0);
        INDArray array3 = Nd4j.valueArrayOf(LENGTH, 3.0);
        INDArray array4 = Nd4j.valueArrayOf(LENGTH, 4.0);
        INDArray array5 = Nd4j.valueArrayOf(LENGTH, 5.0);
        INDArray array6 = Nd4j.valueArrayOf(LENGTH, 6.0);
        INDArray array7 = Nd4j.valueArrayOf(LENGTH, 7.0);
        INDArray array8 = Nd4j.valueArrayOf(LENGTH, 8.0);
        INDArray array9 = Nd4j.valueArrayOf(LENGTH, 9.0);
        INDArray array10 = Nd4j.valueArrayOf(LENGTH, 10.0);
        INDArray array11 = Nd4j.valueArrayOf(LENGTH, 11.0);
        INDArray array12 = Nd4j.valueArrayOf(LENGTH, 12.0);
        INDArray array13 = Nd4j.valueArrayOf(LENGTH, 13.0);
        INDArray array14 = Nd4j.valueArrayOf(LENGTH, 14.0);
        INDArray array15 = Nd4j.valueArrayOf(LENGTH, 15.0);
        INDArray array16 = Nd4j.valueArrayOf(LENGTH, 16.0);
        long time1 = System.currentTimeMillis();
        INDArray arrayMean = Nd4j.averageAndPropagate(new INDArray[]{ array1, array2, array3, array4, array5, array6, array7, array8, array9, array10, array11, array12, array13, array14, array15, array16 });
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time: " + (time2 - time1)));
        Assert.assertNotEquals(null, arrayMean);
        Assert.assertEquals(8.5F, arrayMean.getFloat(12), 0.1F);
        Assert.assertEquals(8.5F, arrayMean.getFloat(150), 0.1F);
        Assert.assertEquals(8.5F, arrayMean.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array1.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array2.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array3.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array5.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array16.getFloat(475), 0.1F);
    }

    @Test
    public void testDistance1and2() {
        double[] d1 = new double[]{ -1, 3, 2 };
        double[] d2 = new double[]{ 0, 1.5, -3.5 };
        INDArray arr1 = Nd4j.create(d1);
        INDArray arr2 = Nd4j.create(d2);
        double expD1 = 0.0;
        double expD2 = 0.0;
        for (int i = 0; i < (d1.length); i++) {
            double diff = (d1[i]) - (d2[i]);
            expD1 += Math.abs(diff);
            expD2 += diff * diff;
        }
        expD2 = Math.sqrt(expD2);
        Assert.assertEquals(expD1, arr1.distance1(arr2), 1.0E-5);
        Assert.assertEquals(expD2, arr1.distance2(arr2), 1.0E-5);
        Assert.assertEquals((expD2 * expD2), arr1.squaredDistance(arr2), 1.0E-5);
    }

    @Test
    public void testEqualsWithEps1() throws Exception {
        INDArray array1 = Nd4j.create(new float[]{ 0.5F, 1.5F, 2.5F, 3.5F, 4.5F });
        INDArray array2 = Nd4j.create(new float[]{ 0.0F, 1.0F, 2.0F, 3.0F, 4.0F });
        INDArray array3 = Nd4j.create(new float[]{ 0.0F, 1.000001F, 2.0F, 3.0F, 4.0F });
        Assert.assertFalse(array1.equalsWithEps(array2, EPS_THRESHOLD));
        Assert.assertTrue(array2.equalsWithEps(array3, EPS_THRESHOLD));
        Assert.assertTrue(array1.equalsWithEps(array2, 0.7F));
        Assert.assertEquals(array2, array3);
    }

    @Test
    public void testIMaxIAMax() {
        Nd4j.getExecutioner().setProfilingMode(ALL);
        INDArray arr = Nd4j.create(new double[]{ -0.24, -0.26, -0.07, -0.01 });
        IMax iMax = new IMax(arr.dup());
        IAMax iaMax = new IAMax(arr.dup());
        double imax = Nd4j.getExecutioner().execAndReturn(iMax).getFinalResult();
        double iamax = Nd4j.getExecutioner().execAndReturn(iaMax).getFinalResult();
        System.out.println(("IMAX: " + imax));
        System.out.println(("IAMAX: " + iamax));
        Assert.assertEquals(1, iamax, 0.0);
        Assert.assertEquals(3, imax, 0.0);
    }

    @Test
    public void testIMinIAMin() {
        INDArray arr = Nd4j.create(new double[]{ -0.24, -0.26, -0.07, -0.01 });
        INDArray abs = Transforms.abs(arr);
        IAMin iaMin = new IAMin(abs);
        IMin iMin = new IMin(arr.dup());
        double imin = Nd4j.getExecutioner().execAndReturn(iMin).getFinalResult();
        double iamin = Nd4j.getExecutioner().execAndReturn(iaMin).getFinalResult();
        System.out.println(("IMin: " + imin));
        System.out.println(("IAMin: " + iamin));
        Assert.assertEquals(3, iamin, 1.0E-12);
        Assert.assertEquals(1, imin, 1.0E-12);
    }

    @Test
    public void testBroadcast3d2d() {
        char[] orders = new char[]{ 'c', 'f' };
        for (char orderArr : orders) {
            for (char orderbc : orders) {
                System.out.println(((orderArr + "\t") + orderbc));
                INDArray arrOrig = Nd4j.ones(3, 4, 5).dup(orderArr);
                // Broadcast on dimensions 0,1
                INDArray bc01 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1 }, new double[]{ 1, 0, 1, 1 }, new double[]{ 1, 1, 0, 0 } }).dup(orderbc);
                INDArray result01 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(arrOrig, bc01, result01, 0, 1));
                for (int i = 0; i < 5; i++) {
                    INDArray subset = result01.tensorAlongDimension(i, 0, 1);// result01.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(i));

                    Assert.assertEquals(bc01, subset);
                }
                // Broadcast on dimensions 0,2
                INDArray bc02 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1 }, new double[]{ 1, 1, 1, 0, 0 } }).dup(orderbc);
                INDArray result02 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(arrOrig, bc02, result02, 0, 2));
                for (int i = 0; i < 4; i++) {
                    INDArray subset = result02.tensorAlongDimension(i, 0, 2);// result02.get(NDArrayIndex.all(), NDArrayIndex.point(i), NDArrayIndex.all());

                    Assert.assertEquals(bc02, subset);
                }
                // Broadcast on dimensions 1,2
                INDArray bc12 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 0, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1 }, new double[]{ 1, 1, 1, 0, 0 } }).dup(orderbc);
                INDArray result12 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(arrOrig, bc12, result12, 1, 2));
                for (int i = 0; i < 3; i++) {
                    INDArray subset = result12.tensorAlongDimension(i, 1, 2);// result12.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all());

                    Assert.assertEquals(("Failed for subset " + i), bc12, subset);
                }
            }
        }
    }

    @Test
    public void testBroadcast4d2d() {
        char[] orders = new char[]{ 'c', 'f' };
        for (char orderArr : orders) {
            for (char orderbc : orders) {
                System.out.println(((orderArr + "\t") + orderbc));
                INDArray arrOrig = Nd4j.ones(3, 4, 5, 6).dup(orderArr);
                // Broadcast on dimensions 0,1
                INDArray bc01 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1 }, new double[]{ 1, 0, 1, 1 }, new double[]{ 1, 1, 0, 0 } }).dup(orderbc);
                INDArray result01 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(result01, bc01, result01, 0, 1));
                for (int d2 = 0; d2 < 5; d2++) {
                    for (int d3 = 0; d3 < 6; d3++) {
                        INDArray subset = result01.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(d2), NDArrayIndex.point(d3));
                        Assert.assertEquals(bc01, subset);
                    }
                }
                // Broadcast on dimensions 0,2
                INDArray bc02 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1 }, new double[]{ 1, 1, 1, 0, 0 } }).dup(orderbc);
                INDArray result02 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(result02, bc02, result02, 0, 2));
                for (int d1 = 0; d1 < 4; d1++) {
                    for (int d3 = 0; d3 < 6; d3++) {
                        INDArray subset = result02.get(NDArrayIndex.all(), NDArrayIndex.point(d1), NDArrayIndex.all(), NDArrayIndex.point(d3));
                        Assert.assertEquals(bc02, subset);
                    }
                }
                // Broadcast on dimensions 0,3
                INDArray bc03 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1, 1 }, new double[]{ 1, 1, 1, 0, 0, 0 } }).dup(orderbc);
                INDArray result03 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(result03, bc03, result03, 0, 3));
                for (int d1 = 0; d1 < 4; d1++) {
                    for (int d2 = 0; d2 < 5; d2++) {
                        INDArray subset = result03.get(NDArrayIndex.all(), NDArrayIndex.point(d1), NDArrayIndex.point(d2), NDArrayIndex.all());
                        Assert.assertEquals(bc03, subset);
                    }
                }
                // Broadcast on dimensions 1,2
                INDArray bc12 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 0, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1 }, new double[]{ 1, 1, 1, 0, 0 } }).dup(orderbc);
                INDArray result12 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(result12, bc12, result12, 1, 2));
                for (int d0 = 0; d0 < 3; d0++) {
                    for (int d3 = 0; d3 < 6; d3++) {
                        INDArray subset = result12.get(NDArrayIndex.point(d0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(d3));
                        Assert.assertEquals(bc12, subset);
                    }
                }
                // Broadcast on dimensions 1,3
                INDArray bc13 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1, 1 }, new double[]{ 0, 1, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1, 1 }, new double[]{ 1, 1, 1, 0, 0, 1 } }).dup(orderbc);
                INDArray result13 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(result13, bc13, result13, 1, 3));
                for (int d0 = 0; d0 < 3; d0++) {
                    for (int d2 = 0; d2 < 5; d2++) {
                        INDArray subset = result13.get(NDArrayIndex.point(d0), NDArrayIndex.all(), NDArrayIndex.point(d2), NDArrayIndex.all());
                        Assert.assertEquals(bc13, subset);
                    }
                }
                // Broadcast on dimensions 2,3
                INDArray bc23 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1, 1 }, new double[]{ 1, 0, 0, 1, 1, 1 }, new double[]{ 1, 1, 1, 0, 0, 0 }, new double[]{ 1, 1, 1, 0, 0, 0 }, new double[]{ 1, 1, 1, 0, 0, 0 } }).dup(orderbc);
                INDArray result23 = arrOrig.dup(orderArr);
                Nd4j.getExecutioner().exec(new BroadcastMulOp(result23, bc23, result23, 2, 3));
                for (int d0 = 0; d0 < 3; d0++) {
                    for (int d1 = 0; d1 < 4; d1++) {
                        INDArray subset = result23.get(NDArrayIndex.point(d0), NDArrayIndex.point(d1), NDArrayIndex.all(), NDArrayIndex.all());
                        Assert.assertEquals(bc23, subset);
                    }
                }
            }
        }
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
    public void testIsMax2of4d() {
        Nd4j.getRandom().setSeed(12345);
        val s = new long[]{ 2, 3, 4, 5 };
        INDArray arr = Nd4j.rand(s);
        // Test 0,1
        INDArray exp = Nd4j.create(s);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                INDArray subset = arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(i), NDArrayIndex.point(j));
                INDArray subsetExp = exp.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(i), NDArrayIndex.point(j));
                BaseNd4jTest.assertArrayEquals(new long[]{ 2, 3 }, subset.shape());
                NdIndexIterator iter = new NdIndexIterator(2, 3);
                val maxIdx = new long[]{ 0, 0 };
                double max = -(Double.MAX_VALUE);
                while (iter.hasNext()) {
                    val next = iter.next();
                    double d = subset.getDouble(next);
                    if (d > max) {
                        max = d;
                        maxIdx[0] = next[0];
                        maxIdx[1] = next[1];
                    }
                } 
                subsetExp.putScalar(maxIdx, 1.0);
            }
        }
        INDArray actC = Nd4j.getExecutioner().execAndReturn(new IsMax(arr.dup('c'), 0, 1));
        INDArray actF = Nd4j.getExecutioner().execAndReturn(new IsMax(arr.dup('f'), 0, 1));
        Assert.assertEquals(exp, actC);
        Assert.assertEquals(exp, actF);
        // Test 2,3
        exp = Nd4j.create(s);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                INDArray subset = arr.get(NDArrayIndex.point(i), NDArrayIndex.point(j), NDArrayIndex.all(), NDArrayIndex.all());
                INDArray subsetExp = exp.get(NDArrayIndex.point(i), NDArrayIndex.point(j), NDArrayIndex.all(), NDArrayIndex.all());
                BaseNd4jTest.assertArrayEquals(new long[]{ 4, 5 }, subset.shape());
                NdIndexIterator iter = new NdIndexIterator(4, 5);
                val maxIdx = new long[]{ 0, 0 };
                double max = -(Double.MAX_VALUE);
                while (iter.hasNext()) {
                    val next = iter.next();
                    double d = subset.getDouble(next);
                    if (d > max) {
                        max = d;
                        maxIdx[0] = next[0];
                        maxIdx[1] = next[1];
                    }
                } 
                subsetExp.putScalar(maxIdx, 1.0);
            }
        }
        actC = Nd4j.getExecutioner().execAndReturn(new IsMax(arr.dup('c'), 2, 3));
        actF = Nd4j.getExecutioner().execAndReturn(new IsMax(arr.dup('f'), 2, 3));
        Assert.assertEquals(exp, actC);
        Assert.assertEquals(exp, actF);
    }

    @Test
    public void testIMax2Of3d() {
        double[][][] slices = new double[3][][];
        slices[0] = new double[][]{ new double[]{ 1, 10, 2 }, new double[]{ 3, 4, 5 } };
        slices[1] = new double[][]{ new double[]{ -10, -9, -8 }, new double[]{ -7, -6, -5 } };
        slices[2] = new double[][]{ new double[]{ 4, 3, 2 }, new double[]{ 1, 0, -1 } };
        // Based on a c-order traversal of each tensor
        double[] imax = new double[]{ 1, 5, 0 };
        INDArray arr = Nd4j.create(3, 2, 3);
        for (int i = 0; i < 3; i++) {
            arr.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.create(slices[i]));
        }
        INDArray out = Nd4j.getExecutioner().exec(new IMax(arr), 1, 2);
        INDArray exp = Nd4j.create(imax);
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testIMax2of4d() {
        Nd4j.getRandom().setSeed(12345);
        val s = new long[]{ 2, 3, 4, 5 };
        INDArray arr = Nd4j.rand(s);
        // Test 0,1
        INDArray exp = Nd4j.create(new long[]{ 4, 5 });
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                INDArray subset = arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(i), NDArrayIndex.point(j));
                BaseNd4jTest.assertArrayEquals(new long[]{ 2, 3 }, subset.shape());
                NdIndexIterator iter = new NdIndexIterator('c', 2, 3);
                double max = -(Double.MAX_VALUE);
                int maxIdxPos = -1;
                int count = 0;
                while (iter.hasNext()) {
                    val next = iter.next();
                    double d = subset.getDouble(next);
                    if (d > max) {
                        max = d;
                        maxIdxPos = count;
                    }
                    count++;
                } 
                exp.putScalar(i, j, maxIdxPos);
            }
        }
        INDArray actC = Nd4j.getExecutioner().exec(new IMax(arr.dup('c')), 0, 1);
        INDArray actF = Nd4j.getExecutioner().exec(new IMax(arr.dup('f')), 0, 1);
        // 
        Assert.assertEquals(exp, actC);
        Assert.assertEquals(exp, actF);
        // Test 2,3
        exp = Nd4j.create(new long[]{ 2, 3 });
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                INDArray subset = arr.get(NDArrayIndex.point(i), NDArrayIndex.point(j), NDArrayIndex.all(), NDArrayIndex.all());
                BaseNd4jTest.assertArrayEquals(new long[]{ 4, 5 }, subset.shape());
                NdIndexIterator iter = new NdIndexIterator('c', 4, 5);
                int maxIdxPos = -1;
                double max = -(Double.MAX_VALUE);
                int count = 0;
                while (iter.hasNext()) {
                    val next = iter.next();
                    double d = subset.getDouble(next);
                    if (d > max) {
                        max = d;
                        maxIdxPos = count;
                    }
                    count++;
                } 
                exp.putScalar(i, j, maxIdxPos);
            }
        }
        actC = Nd4j.getExecutioner().exec(new IMax(arr.dup('c')), 2, 3);
        actF = Nd4j.getExecutioner().exec(new IMax(arr.dup('f')), 2, 3);
        Assert.assertEquals(exp, actC);
        Assert.assertEquals(exp, actF);
    }

    @Test
    public void testTadPermuteEquals() {
        INDArray d3c = Nd4j.linspace(1, 5, 5).reshape('c', 1, 5, 1);
        INDArray d3f = d3c.dup('f');
        INDArray tadCi = d3c.tensorAlongDimension(0, 1, 2).permutei(1, 0);
        INDArray tadFi = d3f.tensorAlongDimension(0, 1, 2).permutei(1, 0);
        INDArray tadC = d3c.tensorAlongDimension(0, 1, 2).permute(1, 0);
        INDArray tadF = d3f.tensorAlongDimension(0, 1, 2).permute(1, 0);
        BaseNd4jTest.assertArrayEquals(tadCi.shape(), tadC.shape());
        BaseNd4jTest.assertArrayEquals(tadCi.stride(), tadC.stride());
        BaseNd4jTest.assertArrayEquals(tadCi.data().asDouble(), tadC.data().asDouble(), 1.0E-8);
        Assert.assertEquals(tadC, tadCi.dup());
        Assert.assertEquals(tadC, tadCi);
        BaseNd4jTest.assertArrayEquals(tadFi.shape(), tadF.shape());
        BaseNd4jTest.assertArrayEquals(tadFi.stride(), tadF.stride());
        BaseNd4jTest.assertArrayEquals(tadFi.data().asDouble(), tadF.data().asDouble(), 1.0E-8);
        Assert.assertEquals(tadF, tadFi.dup());
        Assert.assertEquals(tadF, tadFi);
    }

    @Test
    public void testRemainder1() throws Exception {
        INDArray x = Nd4j.create(10).assign(5.3);
        INDArray y = Nd4j.create(10).assign(2.0);
        INDArray exp = Nd4j.create(10).assign((-0.7));
        INDArray result = x.remainder(2.0);
        Assert.assertEquals(exp, result);
        result = x.remainder(y);
        Assert.assertEquals(exp, result);
    }

    @Test
    public void testFMod1() throws Exception {
        INDArray x = Nd4j.create(10).assign(5.3);
        INDArray y = Nd4j.create(10).assign(2.0);
        INDArray exp = Nd4j.create(10).assign(1.3);
        INDArray result = x.fmod(2.0);
        Assert.assertEquals(exp, result);
        result = x.fmod(y);
        Assert.assertEquals(exp, result);
    }

    @Test
    public void testStrangeDups1() throws Exception {
        INDArray array = Nd4j.create(10).assign(0);
        INDArray exp = Nd4j.create(10).assign(1.0F);
        INDArray copy = null;
        for (int x = 0; x < (array.length()); x++) {
            array.putScalar(x, 1.0F);
            copy = array.dup();
        }
        Assert.assertEquals(exp, array);
        Assert.assertEquals(exp, copy);
    }

    @Test
    public void testStrangeDups2() throws Exception {
        INDArray array = Nd4j.create(10).assign(0);
        INDArray exp1 = Nd4j.create(10).assign(1.0F);
        INDArray exp2 = Nd4j.create(10).assign(1.0F).putScalar(9, 0.0F);
        INDArray copy = null;
        for (int x = 0; x < (array.length()); x++) {
            copy = array.dup();
            array.putScalar(x, 1.0F);
        }
        Assert.assertEquals(exp1, array);
        Assert.assertEquals(exp2, copy);
    }

    @Test
    public void testReductionAgreement1() throws Exception {
        INDArray row = Nd4j.linspace(1, 3, 3);
        INDArray mean0 = row.mean(0);
        Assert.assertFalse((mean0 == row));// True: same object (should be a copy)

        INDArray col = Nd4j.linspace(1, 3, 3).transpose();
        INDArray mean1 = col.mean(1);
        Assert.assertFalse((mean1 == col));
    }

    @Test
    public void testSpecialConcat1() throws Exception {
        for (int i = 0; i < 10; i++) {
            List<INDArray> arrays = new ArrayList<>();
            for (int x = 0; x < 10; x++) {
                arrays.add(Nd4j.create(100).assign(x));
            }
            INDArray matrix = Nd4j.specialConcat(0, arrays.toArray(new INDArray[0]));
            Assert.assertEquals(10, matrix.rows());
            Assert.assertEquals(100, matrix.columns());
            for (int x = 0; x < 10; x++) {
                Assert.assertEquals(((double) (x)), matrix.getRow(x).meanNumber().doubleValue(), 0.1);
                Assert.assertEquals(arrays.get(x), matrix.getRow(x));
            }
        }
    }

    @Test
    public void testSpecialConcat2() throws Exception {
        List<INDArray> arrays = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            arrays.add(Nd4j.create(new double[]{ x, x, x, x, x, x }));
        }
        INDArray matrix = Nd4j.specialConcat(0, arrays.toArray(new INDArray[0]));
        Assert.assertEquals(10, matrix.rows());
        Assert.assertEquals(6, matrix.columns());
        for (int x = 0; x < 10; x++) {
            Assert.assertEquals(((double) (x)), matrix.getRow(x).meanNumber().doubleValue(), 0.1);
            Assert.assertEquals(arrays.get(x), matrix.getRow(x));
        }
    }

    @Test
    public void testPutScalar1() {
        INDArray array = Nd4j.create(10, 3, 96, 96);
        for (int i = 0; i < 10; i++) {
            log.info("Trying i: {}", i);
            array.tensorAlongDimension(i, 1, 2, 3).putScalar(1, 2, 3, 1);
        }
    }

    @Test
    public void testAveraging1() {
        Nd4j.getAffinityManager().allowCrossDeviceAccess(false);
        List<INDArray> arrays = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrays.add(Nd4j.create(100).assign(((double) (i))));
        }
        INDArray result = Nd4j.averageAndPropagate(arrays);
        Assert.assertEquals(4.5, result.meanNumber().doubleValue(), 0.01);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(result, arrays.get(i));
        }
    }

    @Test
    public void testAveraging2() {
        List<INDArray> arrays = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrays.add(Nd4j.create(100).assign(((double) (i))));
        }
        Nd4j.averageAndPropagate(null, arrays);
        INDArray result = arrays.get(0);
        Assert.assertEquals(4.5, result.meanNumber().doubleValue(), 0.01);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(("Failed on iteration " + i), result, arrays.get(i));
        }
    }

    @Test
    public void testAveraging3() {
        Nd4j.getAffinityManager().allowCrossDeviceAccess(false);
        List<INDArray> arrays = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrays.add(Nd4j.create(100).assign(((double) (i))));
        }
        Nd4j.averageAndPropagate(null, arrays);
        INDArray result = arrays.get(0);
        Assert.assertEquals(4.5, result.meanNumber().doubleValue(), 0.01);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(("Failed on iteration " + i), result, arrays.get(i));
        }
    }

    @Test
    public void testZ1() throws Exception {
        INDArray matrix = Nd4j.create(10, 10).assign(1.0);
        INDArray exp = Nd4j.create(10).assign(10.0);
        INDArray res = Nd4j.create(10);
        INDArray sums = matrix.sum(res, 0);
        Assert.assertTrue((res == sums));
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testDupDelayed() {
        if (!((Nd4j.getExecutioner()) instanceof GridExecutioner))
            return;

        // Nd4j.getExecutioner().commit();
        val executioner = ((GridExecutioner) (Nd4j.getExecutioner()));
        log.info("Starting: -------------------------------");
        // log.info("Point A: [{}]", executioner.getQueueLength());
        INDArray in = Nd4j.zeros(10);
        List<INDArray> out = new ArrayList<>();
        List<INDArray> comp = new ArrayList<>();
        // log.info("Point B: [{}]", executioner.getQueueLength());
        // log.info("\n\n");
        for (int i = 0; i < (in.length()); i++) {
            // log.info("Point C: [{}]", executioner.getQueueLength());
            in.putScalar(i, 1);
            // log.info("Point D: [{}]", executioner.getQueueLength());
            out.add(in.dup());
            // log.info("Point E: [{}]", executioner.getQueueLength());
            // Nd4j.getExecutioner().commit();
            in.putScalar(i, 0);
            // Nd4j.getExecutioner().commit();
            // log.info("Point F: [{}]\n\n", executioner.getQueueLength());
        }
        for (int i = 0; i < (in.length()); i++) {
            in.putScalar(i, 1);
            comp.add(Nd4j.create(in.data().dup()));
            // Nd4j.getExecutioner().commit();
            in.putScalar(i, 0);
        }
        for (int i = 0; i < (out.size()); i++) {
            Assert.assertEquals((("Failed at iteration: [" + i) + "]"), out.get(i), comp.get(i));
        }
    }

    @Test
    public void testScalarReduction1() {
        Accumulation op = new Norm2(Nd4j.create(1).assign(1.0));
        double norm2 = Nd4j.getExecutioner().execAndReturn(op).getFinalResult().doubleValue();
        double norm1 = Nd4j.getExecutioner().execAndReturn(new Norm1(Nd4j.create(1).assign(1.0))).getFinalResult().doubleValue();
        double sum = Nd4j.getExecutioner().execAndReturn(new Sum(Nd4j.create(1).assign(1.0))).getFinalResult().doubleValue();
        Assert.assertEquals(1.0, norm2, 0.001);
        Assert.assertEquals(1.0, norm1, 0.001);
        Assert.assertEquals(1.0, sum, 0.001);
    }

    @Test
    public void sumResultArrayEdgeCase() {
        INDArray delta = Nd4j.create(1, 3);
        delta.assign(Nd4j.rand(delta.shape()));
        INDArray out = delta.sum(0);
        INDArray out2 = Nd4j.zeros(new long[]{ 1, 3 }, 'c');
        INDArray res = delta.sum(out2, 0);
        Assert.assertEquals(out, out2);
        Assert.assertTrue((res == out2));
    }

    @Test
    public void tesAbsReductions1() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -1, -2, -3, -4 });
        Assert.assertEquals(4, array.amaxNumber().intValue());
    }

    @Test
    public void tesAbsReductions2() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -1, -2, -3, -4 });
        Assert.assertEquals(1, array.aminNumber().intValue());
    }

    @Test
    public void tesAbsReductions3() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -2, -2, 2, 2 });
        Assert.assertEquals(2, array.ameanNumber().intValue());
    }

    @Test
    public void tesAbsReductions4() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -2, -2, 2, 2 });
        Assert.assertEquals(4, array.scan(Conditions.absGreaterThanOrEqual(0.0)).intValue());
    }

    @Test
    public void tesAbsReductions5() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -2, 0.0, 2, 2 });
        Assert.assertEquals(3, array.scan(Conditions.absGreaterThan(0.0)).intValue());
    }

    @Test
    public void testNewBroadcastComparison1() throws Exception {
        INDArray initial = Nd4j.create(3, 5);
        INDArray mask = Nd4j.create(new double[]{ 5, 4, 3, 2, 1 });
        INDArray exp = Nd4j.create(new double[]{ 1, 1, 1, 0, 0 });
        for (int i = 0; i < (initial.columns()); i++) {
            initial.getColumn(i).assign(i);
        }
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().exec(new BroadcastLessThan(initial, mask, initial, 1));
        for (int i = 0; i < (initial.rows()); i++) {
            Assert.assertEquals(exp, initial.getRow(i));
        }
    }

    @Test
    public void testNewBroadcastComparison2() throws Exception {
        INDArray initial = Nd4j.create(3, 5);
        INDArray mask = Nd4j.create(new double[]{ 5, 4, 3, 2, 1 });
        INDArray exp = Nd4j.create(new double[]{ 0, 0, 0, 1, 1 });
        for (int i = 0; i < (initial.columns()); i++) {
            initial.getColumn(i).assign(i);
        }
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().exec(new BroadcastGreaterThan(initial, mask, initial, 1));
        for (int i = 0; i < (initial.rows()); i++) {
            Assert.assertEquals(exp, initial.getRow(i));
        }
    }

    @Test
    public void testNewBroadcastComparison3() throws Exception {
        INDArray initial = Nd4j.create(3, 5);
        INDArray mask = Nd4j.create(new double[]{ 5, 4, 3, 2, 1 });
        INDArray exp = Nd4j.create(new double[]{ 0, 0, 1, 1, 1 });
        for (int i = 0; i < (initial.columns()); i++) {
            initial.getColumn(i).assign((i + 1));
        }
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().exec(new BroadcastGreaterThanOrEqual(initial, mask, initial, 1));
        for (int i = 0; i < (initial.rows()); i++) {
            Assert.assertEquals(exp, initial.getRow(i));
        }
    }

    @Test
    public void testNewBroadcastComparison4() throws Exception {
        INDArray initial = Nd4j.create(3, 5);
        INDArray mask = Nd4j.create(new double[]{ 5, 4, 3, 2, 1 });
        INDArray exp = Nd4j.create(new double[]{ 0, 0, 1, 0, 0 });
        for (int i = 0; i < (initial.columns()); i++) {
            initial.getColumn(i).assign((i + 1));
        }
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().exec(new BroadcastEqualTo(initial, mask, initial, 1));
        for (int i = 0; i < (initial.rows()); i++) {
            Assert.assertEquals(exp, initial.getRow(i));
        }
    }

    @Test
    public void testTadReduce3_0() throws Exception {
        INDArray haystack = Nd4j.create(new double[]{ -0.84443557262, -0.06822254508, 0.74266910552, 0.61765557527, -0.77555125951, -0.99536740779, -0.0257304441183, -0.651210606, -0.34578949213, -1.25485503673, 0.62955373525, -0.31357592344, 1.03362500667, -0.59279078245, 1.1914824247 }).reshape(3, 5);
        INDArray needle = Nd4j.create(new double[]{ -0.99536740779, -0.0257304441183, -0.651210606, -0.34578949213, -1.25485503673 });
        INDArray reduced = Nd4j.getExecutioner().exec(new CosineDistance(haystack, needle), 1);
        log.info("Reduced: {}", reduced);
        INDArray exp = Nd4j.create(new double[]{ 0.577452, 0.0, 1.80182 });
        Assert.assertEquals(exp, reduced);
        for (int i = 0; i < (haystack.rows()); i++) {
            double res = Nd4j.getExecutioner().execAndReturn(new CosineDistance(haystack.getRow(i).dup(), needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
        }
        // cosinedistance([-0.84443557262, -0.06822254508, 0.74266910552, 0.61765557527, -0.77555125951], [-0.99536740779, -0.0257304441183, -0.6512106060, -0.345789492130, -1.25485503673)
        // cosinedistance([.62955373525, -0.31357592344, 1.03362500667, -0.59279078245, 1.1914824247], [-0.99536740779, -0.0257304441183, -0.6512106060, -0.345789492130, -1.25485503673)
    }

    @Test
    public void testTadReduce3_1() throws Exception {
        INDArray initial = Nd4j.create(5, 10);
        for (int i = 0; i < (initial.rows()); i++) {
            initial.getRow(i).assign((i + 1));
        }
        INDArray needle = Nd4j.create(new double[]{ 0.01, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 });
        INDArray reduced = Nd4j.getExecutioner().exec(new CosineSimilarity(initial, needle), 1);
        log.warn("Reduced: {}", reduced);
        for (int i = 0; i < (initial.rows()); i++) {
            double res = Nd4j.getExecutioner().execAndReturn(new CosineSimilarity(initial.getRow(i).dup(), needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
        }
    }

    @Test
    public void testTadReduce3_2() throws Exception {
        INDArray initial = Nd4j.create(5, 10);
        for (int i = 0; i < (initial.rows()); i++) {
            initial.getRow(i).assign((i + 1));
        }
        INDArray needle = Nd4j.create(10).assign(1.0);
        INDArray reduced = Nd4j.getExecutioner().exec(new ManhattanDistance(initial, needle), 1);
        log.warn("Reduced: {}", reduced);
        for (int i = 0; i < (initial.rows()); i++) {
            double res = Nd4j.getExecutioner().execAndReturn(new ManhattanDistance(initial.getRow(i).dup(), needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
        }
    }

    @Test
    public void testTadReduce3_3() throws Exception {
        INDArray initial = Nd4j.create(5, 10);
        for (int i = 0; i < (initial.rows()); i++) {
            initial.getRow(i).assign((i + 1));
        }
        INDArray needle = Nd4j.create(10).assign(1.0);
        INDArray reduced = Nd4j.getExecutioner().exec(new EuclideanDistance(initial, needle), 1);
        log.warn("Reduced: {}", reduced);
        for (int i = 0; i < (initial.rows()); i++) {
            INDArray x = initial.getRow(i).dup();
            double res = Nd4j.getExecutioner().execAndReturn(new EuclideanDistance(x, needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
            log.info("Euclidean: {} vs {} is {}", x, needle, res);
        }
    }

    @Test
    public void testTadReduce3_3_NEG() throws Exception {
        INDArray initial = Nd4j.create(5, 10);
        for (int i = 0; i < (initial.rows()); i++) {
            initial.getRow(i).assign((i + 1));
        }
        INDArray needle = Nd4j.create(10).assign(1.0);
        INDArray reduced = Nd4j.getExecutioner().exec(new EuclideanDistance(initial, needle), (-1));
        log.warn("Reduced: {}", reduced);
        for (int i = 0; i < (initial.rows()); i++) {
            INDArray x = initial.getRow(i).dup();
            double res = Nd4j.getExecutioner().execAndReturn(new EuclideanDistance(x, needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
            log.info("Euclidean: {} vs {} is {}", x, needle, res);
        }
    }

    @Test
    public void testTadReduce3_3_NEG_2() throws Exception {
        INDArray initial = Nd4j.create(5, 10);
        for (int i = 0; i < (initial.rows()); i++) {
            initial.getRow(i).assign((i + 1));
        }
        INDArray needle = Nd4j.create(10).assign(1.0);
        INDArray reduced = Nd4j.create(5);
        Nd4j.getExecutioner().exec(new CosineSimilarity(initial, needle, reduced, initial.lengthLong()), (-1));
        log.warn("Reduced: {}", reduced);
        for (int i = 0; i < (initial.rows()); i++) {
            INDArray x = initial.getRow(i).dup();
            double res = Nd4j.getExecutioner().execAndReturn(new CosineSimilarity(x, needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
            log.info("Cosine: {} vs {} is {}", x, needle, res);
        }
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testTadReduce3_5() throws Exception {
        INDArray initial = Nd4j.create(5, 10);
        for (int i = 0; i < (initial.rows()); i++) {
            initial.getRow(i).assign((i + 1));
        }
        INDArray needle = Nd4j.create(2, 10).assign(1.0);
        INDArray reduced = Nd4j.getExecutioner().exec(new EuclideanDistance(initial, needle), 1);
    }

    @Test
    public void testTadReduce3_4() throws Exception {
        INDArray initial = Nd4j.create(5, 6, 7);
        for (int i = 0; i < 5; i++) {
            initial.tensorAlongDimension(i, 1, 2).assign((i + 1));
        }
        INDArray needle = Nd4j.create(6, 7).assign(1.0);
        INDArray reduced = Nd4j.getExecutioner().exec(new ManhattanDistance(initial, needle), 1, 2);
        log.warn("Reduced: {}", reduced);
        for (int i = 0; i < 5; i++) {
            double res = Nd4j.getExecutioner().execAndReturn(new ManhattanDistance(initial.tensorAlongDimension(i, 1, 2).dup(), needle)).getFinalResult().doubleValue();
            Assert.assertEquals(("Failed at " + i), reduced.getDouble(i), res, 0.001);
        }
    }

    @Test
    public void testAtan2_1() throws Exception {
        INDArray x = Nd4j.create(10).assign((-1.0));
        INDArray y = Nd4j.create(10).assign(0.0);
        INDArray exp = Nd4j.create(10).assign(Math.PI);
        INDArray z = Transforms.atan2(x, y);
        Assert.assertEquals(exp, z);
    }

    @Test
    public void testAtan2_2() throws Exception {
        INDArray x = Nd4j.create(10).assign(1.0);
        INDArray y = Nd4j.create(10).assign(0.0);
        INDArray exp = Nd4j.create(10).assign(0.0);
        INDArray z = Transforms.atan2(x, y);
        Assert.assertEquals(exp, z);
    }

    @Test
    public void testJaccardDistance1() throws Exception {
        INDArray x = Nd4j.create(new double[]{ 0, 1, 0, 0, 1, 0 });
        INDArray y = Nd4j.create(new double[]{ 1, 1, 0, 1, 0, 0 });
        double val = Transforms.jaccardDistance(x, y);
        Assert.assertEquals(0.75, val, 1.0E-5);
    }

    @Test
    public void testJaccardDistance2() throws Exception {
        INDArray x = Nd4j.create(new double[]{ 0, 1, 0, 0, 1, 1 });
        INDArray y = Nd4j.create(new double[]{ 1, 1, 0, 1, 0, 0 });
        double val = Transforms.jaccardDistance(x, y);
        Assert.assertEquals(0.8, val, 1.0E-5);
    }

    @Test
    public void testHammingDistance1() throws Exception {
        INDArray x = Nd4j.create(new double[]{ 0, 0, 0, 1, 0, 0 });
        INDArray y = Nd4j.create(new double[]{ 0, 0, 0, 0, 1, 0 });
        double val = Transforms.hammingDistance(x, y);
        Assert.assertEquals((2.0 / 6), val, 1.0E-5);
    }

    @Test
    public void testHammingDistance2() throws Exception {
        INDArray x = Nd4j.create(new double[]{ 0, 0, 0, 1, 0, 0 });
        INDArray y = Nd4j.create(new double[]{ 0, 1, 0, 0, 1, 0 });
        double val = Transforms.hammingDistance(x, y);
        Assert.assertEquals((3.0 / 6), val, 1.0E-5);
    }

    @Test
    public void testHammingDistance3() throws Exception {
        INDArray x = Nd4j.create(10, 6);
        for (int r = 0; r < (x.rows()); r++) {
            x.getRow(r).putScalar((r % (x.columns())), 1);
        }
        INDArray y = Nd4j.create(new double[]{ 0, 0, 0, 0, 1, 0 });
        INDArray res = Nd4j.getExecutioner().exec(new HammingDistance(x, y), 1);
        Assert.assertEquals(10, res.length());
        for (int r = 0; r < (x.rows()); r++) {
            if (r == 4) {
                Assert.assertEquals(0.0, res.getDouble(r), 1.0E-5);
            } else {
                Assert.assertEquals((2.0 / 6), res.getDouble(r), 1.0E-5);
            }
        }
    }

    @Test
    public void testAllDistances1() throws Exception {
        INDArray initialX = Nd4j.create(5, 10);
        INDArray initialY = Nd4j.create(7, 10);
        for (int i = 0; i < (initialX.rows()); i++) {
            initialX.getRow(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.rows()); i++) {
            initialY.getRow(i).assign((i + 101));
        }
        INDArray result = Transforms.allEuclideanDistances(initialX, initialY, 1);
        Nd4j.getExecutioner().commit();
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.rows()); x++) {
            INDArray rowX = initialX.getRow(x).dup();
            for (int y = 0; y < (initialY.rows()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.euclideanDistance(rowX, initialY.getRow(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances2() throws Exception {
        INDArray initialX = Nd4j.create(5, 10);
        INDArray initialY = Nd4j.create(7, 10);
        for (int i = 0; i < (initialX.rows()); i++) {
            initialX.getRow(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.rows()); i++) {
            initialY.getRow(i).assign((i + 101));
        }
        INDArray result = Transforms.allManhattanDistances(initialX, initialY, 1);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.rows()); x++) {
            INDArray rowX = initialX.getRow(x).dup();
            for (int y = 0; y < (initialY.rows()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.manhattanDistance(rowX, initialY.getRow(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances2_Large() throws Exception {
        INDArray initialX = Nd4j.create(5, 2000);
        INDArray initialY = Nd4j.create(7, 2000);
        for (int i = 0; i < (initialX.rows()); i++) {
            initialX.getRow(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.rows()); i++) {
            initialY.getRow(i).assign((i + 101));
        }
        INDArray result = Transforms.allManhattanDistances(initialX, initialY, 1);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.rows()); x++) {
            INDArray rowX = initialX.getRow(x).dup();
            for (int y = 0; y < (initialY.rows()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.manhattanDistance(rowX, initialY.getRow(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances3_Large() throws Exception {
        INDArray initialX = Nd4j.create(5, 2000);
        INDArray initialY = Nd4j.create(7, 2000);
        for (int i = 0; i < (initialX.rows()); i++) {
            initialX.getRow(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.rows()); i++) {
            initialY.getRow(i).assign((i + 101));
        }
        INDArray result = Transforms.allEuclideanDistances(initialX, initialY, 1);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.rows()); x++) {
            INDArray rowX = initialX.getRow(x).dup();
            for (int y = 0; y < (initialY.rows()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.euclideanDistance(rowX, initialY.getRow(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances3_Large_Columns() throws Exception {
        INDArray initialX = Nd4j.create(2000, 5);
        INDArray initialY = Nd4j.create(2000, 7);
        for (int i = 0; i < (initialX.columns()); i++) {
            initialX.getColumn(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.columns()); i++) {
            initialY.getColumn(i).assign((i + 101));
        }
        INDArray result = Transforms.allEuclideanDistances(initialX, initialY, 0);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.columns()); x++) {
            INDArray colX = initialX.getColumn(x).dup();
            for (int y = 0; y < (initialY.columns()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.euclideanDistance(colX, initialY.getColumn(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances4_Large_Columns() throws Exception {
        INDArray initialX = Nd4j.create(2000, 5);
        INDArray initialY = Nd4j.create(2000, 7);
        for (int i = 0; i < (initialX.columns()); i++) {
            initialX.getColumn(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.columns()); i++) {
            initialY.getColumn(i).assign((i + 101));
        }
        INDArray result = Transforms.allManhattanDistances(initialX, initialY, 0);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.columns()); x++) {
            INDArray colX = initialX.getColumn(x).dup();
            for (int y = 0; y < (initialY.columns()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.manhattanDistance(colX, initialY.getColumn(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances5_Large_Columns() throws Exception {
        INDArray initialX = Nd4j.create(2000, 5);
        INDArray initialY = Nd4j.create(2000, 7);
        for (int i = 0; i < (initialX.columns()); i++) {
            initialX.getColumn(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.columns()); i++) {
            initialY.getColumn(i).assign((i + 101));
        }
        INDArray result = Transforms.allCosineDistances(initialX, initialY, 0);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.columns()); x++) {
            INDArray colX = initialX.getColumn(x).dup();
            for (int y = 0; y < (initialY.columns()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.cosineDistance(colX, initialY.getColumn(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances3_Small_Columns() throws Exception {
        INDArray initialX = Nd4j.create(200, 5);
        INDArray initialY = Nd4j.create(200, 7);
        for (int i = 0; i < (initialX.columns()); i++) {
            initialX.getColumn(i).assign((i + 1));
        }
        for (int i = 0; i < (initialY.columns()); i++) {
            initialY.getColumn(i).assign((i + 101));
        }
        INDArray result = Transforms.allManhattanDistances(initialX, initialY, 0);
        Assert.assertEquals((5 * 7), result.length());
        for (int x = 0; x < (initialX.columns()); x++) {
            INDArray colX = initialX.getColumn(x).dup();
            for (int y = 0; y < (initialY.columns()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.manhattanDistance(colX, initialY.getColumn(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testAllDistances3() throws Exception {
        Nd4j.getRandom().setSeed(123);
        INDArray initialX = Nd4j.rand(5, 10);
        INDArray initialY = initialX.mul((-1));
        INDArray result = Transforms.allCosineSimilarities(initialX, initialY, 1);
        Assert.assertEquals((5 * 5), result.length());
        for (int x = 0; x < (initialX.rows()); x++) {
            INDArray rowX = initialX.getRow(x).dup();
            for (int y = 0; y < (initialY.rows()); y++) {
                double res = result.getDouble(x, y);
                double exp = Transforms.cosineSim(rowX, initialY.getRow(y).dup());
                Assert.assertEquals((((("Failed for [" + x) + ", ") + y) + "]"), exp, res, 0.001);
            }
        }
    }

    @Test
    public void testStridedTransforms1() throws Exception {
        // output: Rank: 2,Offset: 0
        // Order: c Shape: [5,2],  stride: [2,1]
        // output: [0.5086864, 0.49131358, 0.50720876, 0.4927912, 0.46074104, 0.53925896, 0.49314, 0.50686, 0.5217741, 0.4782259]
        double[] d = new double[]{ 0.5086864, 0.49131358, 0.50720876, 0.4927912, 0.46074104, 0.53925896, 0.49314, 0.50686, 0.5217741, 0.4782259 };
        INDArray in = Nd4j.create(d, new long[]{ 5, 2 }, 'c');
        INDArray col0 = in.getColumn(0);
        INDArray col1 = in.getColumn(1);
        float[] exp0 = new float[(d.length) / 2];
        float[] exp1 = new float[(d.length) / 2];
        for (int i = 0; i < (col0.length()); i++) {
            exp0[i] = ((float) (Math.log(col0.getDouble(i))));
            exp1[i] = ((float) (Math.log(col1.getDouble(i))));
        }
        INDArray out0 = Transforms.log(col0, true);
        INDArray out1 = Transforms.log(col1, true);
        BaseNd4jTest.assertArrayEquals(exp0, out0.data().asFloat(), 1.0E-4F);
        BaseNd4jTest.assertArrayEquals(exp1, out1.data().asFloat(), 1.0E-4F);
    }

    @Test
    public void testEntropy1() throws Exception {
        INDArray x = Nd4j.rand(1, 100);
        double exp = MathUtils.entropy(x.data().asDouble());
        double res = x.entropyNumber().doubleValue();
        Assert.assertEquals(exp, res, 1.0E-5);
    }

    @Test
    public void testEntropy2() throws Exception {
        INDArray x = Nd4j.rand(10, 100);
        INDArray res = x.entropy(1);
        Assert.assertEquals(10, res.lengthLong());
        for (int t = 0; t < (x.rows()); t++) {
            double exp = MathUtils.entropy(x.getRow(t).dup().data().asDouble());
            Assert.assertEquals(exp, res.getDouble(t), 1.0E-5);
        }
    }

    @Test
    public void testEntropy3() throws Exception {
        INDArray x = Nd4j.rand(1, 100);
        double exp = getShannonEntropy(x.data().asDouble());
        double res = x.shannonEntropyNumber().doubleValue();
        Assert.assertEquals(exp, res, 1.0E-5);
    }

    @Test
    public void testEntropy4() throws Exception {
        INDArray x = Nd4j.rand(1, 100);
        double exp = getLogEntropy(x.data().asDouble());
        double res = x.logEntropyNumber().doubleValue();
        Assert.assertEquals(exp, res, 1.0E-5);
    }

    @Test
    public void testReverse1() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray exp = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        INDArray rev = Nd4j.reverse(array);
        Assert.assertEquals(exp, rev);
    }

    @Test
    public void testReverse2() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray exp = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        INDArray rev = Nd4j.reverse(array);
        Assert.assertEquals(exp, rev);
    }

    @Test
    public void testReverse3() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray exp = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        INDArray rev = Nd4j.getExecutioner().exec(new OldReverse(array, Nd4j.createUninitialized(array.length()))).z();
        Assert.assertEquals(exp, rev);
    }

    @Test
    public void testReverse4() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray exp = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        INDArray rev = Nd4j.getExecutioner().exec(new OldReverse(array, Nd4j.createUninitialized(array.length()))).z();
        Assert.assertEquals(exp, rev);
    }

    @Test
    public void testReverse5() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray exp = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        INDArray rev = Transforms.reverse(array, true);
        Assert.assertEquals(exp, rev);
        Assert.assertFalse((rev == array));
    }

    @Test
    public void testReverse6() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray exp = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        INDArray rev = Transforms.reverse(array, false);
        Assert.assertEquals(exp, rev);
        Assert.assertTrue((rev == array));
    }

    @Test
    public void testNativeSortView1() {
        INDArray matrix = Nd4j.create(10, 10);
        INDArray exp = Nd4j.linspace(0, 9, 10);
        int cnt = 0;
        for (long i = (matrix.rows()) - 1; i >= 0; i--) {
            // FIXME: int cast
            matrix.getRow(((int) (i))).assign(cnt);
            cnt++;
        }
        Nd4j.sort(matrix.getColumn(0), true);
        log.info("Matrix: {}", matrix);
        Assert.assertEquals(exp, matrix.getColumn(0));
    }

    @Test
    public void testNativeSort1() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 9, 2, 1, 7, 6, 5, 4, 3, 8, 0 });
        INDArray exp1 = Nd4j.create(new double[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        INDArray exp2 = Nd4j.create(new double[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        INDArray res = Nd4j.sort(array, true);
        Assert.assertEquals(exp1, res);
        res = Nd4j.sort(res, false);
        Assert.assertEquals(exp2, res);
    }

    @Test
    public void testNativeSort2() throws Exception {
        INDArray array = Nd4j.rand(1, 10000);
        INDArray res = Nd4j.sort(array, true);
        INDArray exp = res.dup();
        res = Nd4j.sort(res, false);
        res = Nd4j.sort(res, true);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testNativeSort3() throws Exception {
        INDArray array = Nd4j.linspace(1, 1048576, 1048576);
        INDArray exp = array.dup();
        Nd4j.shuffle(array, 0);
        long time1 = System.currentTimeMillis();
        INDArray res = Nd4j.sort(array, true);
        long time2 = System.currentTimeMillis();
        log.info("Time spent: {} ms", (time2 - time1));
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testNativeSort3_1() throws Exception {
        INDArray array = Nd4j.linspace(1, 2017152, 2017152);
        INDArray exp = array.dup();
        Transforms.reverse(array, false);
        long time1 = System.currentTimeMillis();
        INDArray res = Nd4j.sort(array, true);
        long time2 = System.currentTimeMillis();
        log.info("Time spent: {} ms", (time2 - time1));
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testNativeSortAlongDimension1() throws Exception {
        INDArray array = Nd4j.create(1000, 1000);
        INDArray exp1 = Nd4j.linspace(1, 1000, 1000);
        INDArray dps = exp1.dup();
        Nd4j.shuffle(dps, 0);
        Assert.assertNotEquals(exp1, dps);
        for (int r = 0; r < (array.rows()); r++) {
            array.getRow(r).assign(dps);
        }
        long time1 = System.currentTimeMillis();
        INDArray res = Nd4j.sort(array, 1, true);
        long time2 = System.currentTimeMillis();
        log.info("Time spent: {} ms", (time2 - time1));
        for (int r = 0; r < (array.rows()); r++) {
            Assert.assertEquals(("Failed at " + r), exp1, res.getRow(r).dup());
        }
    }

    @Test
    public void testNativeSortAlongDimension3() throws Exception {
        INDArray array = Nd4j.create(2000, 2000);
        INDArray exp1 = Nd4j.linspace(1, 2000, 2000);
        INDArray dps = exp1.dup();
        Nd4j.getExecutioner().commit();
        Nd4j.shuffle(dps, 0);
        Assert.assertNotEquals(exp1, dps);
        for (int r = 0; r < (array.rows()); r++) {
            array.getRow(r).assign(dps);
        }
        long time1 = System.currentTimeMillis();
        INDArray res = Nd4j.sort(array, 1, true);
        long time2 = System.currentTimeMillis();
        log.info("Time spent: {} ms", (time2 - time1));
        for (int r = 0; r < (array.rows()); r++) {
            Assert.assertEquals(("Failed at " + r), exp1, res.getRow(r));
            // assertArrayEquals("Failed at " + r, exp1.data().asDouble(), res.getRow(r).dup().data().asDouble(), 1e-5);
        }
    }

    @Test
    public void testNativeSortAlongDimension2() throws Exception {
        INDArray array = Nd4j.create(100, 10);
        INDArray exp1 = Nd4j.create(new double[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
        for (int r = 0; r < (array.rows()); r++) {
            array.getRow(r).assign(Nd4j.create(new double[]{ 3, 8, 2, 7, 5, 6, 4, 9, 1, 0 }));
        }
        INDArray res = Nd4j.sort(array, 1, false);
        for (int r = 0; r < (array.rows()); r++) {
            Assert.assertEquals(("Failed at " + r), exp1, res.getRow(r).dup());
        }
    }

    @Test
    public void testPercentile1() throws Exception {
        INDArray array = Nd4j.linspace(1, 10, 10);
        Percentile percentile = new Percentile(50);
        double exp = percentile.evaluate(array.data().asDouble());
        Assert.assertEquals(exp, array.percentileNumber(50));
    }

    @Test
    public void testPercentile2() throws Exception {
        INDArray array = Nd4j.linspace(1, 9, 9);
        Percentile percentile = new Percentile(50);
        double exp = percentile.evaluate(array.data().asDouble());
        Assert.assertEquals(exp, array.percentileNumber(50));
    }

    @Test
    public void testPercentile3() throws Exception {
        INDArray array = Nd4j.linspace(1, 9, 9);
        Percentile percentile = new Percentile(75);
        double exp = percentile.evaluate(array.data().asDouble());
        Assert.assertEquals(exp, array.percentileNumber(75));
    }

    @Test
    public void testPercentile4() throws Exception {
        INDArray array = Nd4j.linspace(1, 10, 10);
        Percentile percentile = new Percentile(75);
        double exp = percentile.evaluate(array.data().asDouble());
        Assert.assertEquals(exp, array.percentileNumber(75));
    }

    @Test
    public void testTadPercentile1() throws Exception {
        INDArray array = Nd4j.linspace(1, 10, 10);
        Transforms.reverse(array, false);
        Percentile percentile = new Percentile(75);
        double exp = percentile.evaluate(array.data().asDouble());
        INDArray matrix = Nd4j.create(10, 10);
        for (int i = 0; i < (matrix.rows()); i++)
            matrix.getRow(i).assign(array);

        INDArray res = matrix.percentile(75, 1);
        for (int i = 0; i < (matrix.rows()); i++)
            Assert.assertEquals(exp, res.getDouble(i), 1.0E-5);

    }

    @Test
    public void testPutiRowVector() throws Exception {
        INDArray matrix = Nd4j.createUninitialized(10, 10);
        INDArray exp = Nd4j.create(10, 10).assign(1.0);
        INDArray row = Nd4j.create(10).assign(1.0);
        matrix.putiRowVector(row);
        Assert.assertEquals(exp, matrix);
    }

    @Test
    public void testPutiColumnsVector() throws Exception {
        INDArray matrix = Nd4j.createUninitialized(5, 10);
        INDArray exp = Nd4j.create(5, 10).assign(1.0);
        INDArray row = Nd4j.create(5, 1).assign(1.0);
        matrix.putiColumnVector(row);
        Assert.assertEquals(exp, matrix);
    }

    @Test
    public void testRsub1() throws Exception {
        INDArray arr = Nd4j.ones(5).assign(2.0);
        INDArray exp_0 = Nd4j.ones(5).assign(2.0);
        INDArray exp_1 = Nd4j.create(5).assign((-1));
        Nd4j.getExecutioner().commit();
        INDArray res = arr.rsub(1.0);
        Assert.assertEquals(exp_0, arr);
        Assert.assertEquals(exp_1, res);
    }

    @Test
    public void testBroadcastMin() throws Exception {
        INDArray matrix = Nd4j.create(5, 5);
        for (int r = 0; r < (matrix.rows()); r++) {
            matrix.getRow(r).assign(Nd4j.create(new double[]{ 2, 3, 3, 4, 5 }));
        }
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        Nd4j.getExecutioner().exec(new BroadcastMin(matrix, row, matrix, 1));
        for (int r = 0; r < (matrix.rows()); r++) {
            Assert.assertEquals(Nd4j.create(new double[]{ 1, 2, 3, 4, 5 }), matrix.getRow(r));
        }
    }

    @Test
    public void testBroadcastMax() throws Exception {
        INDArray matrix = Nd4j.create(5, 5);
        for (int r = 0; r < (matrix.rows()); r++) {
            matrix.getRow(r).assign(Nd4j.create(new double[]{ 1, 2, 3, 2, 1 }));
        }
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        Nd4j.getExecutioner().exec(new BroadcastMax(matrix, row, matrix, 1));
        for (int r = 0; r < (matrix.rows()); r++) {
            Assert.assertEquals(Nd4j.create(new double[]{ 1, 2, 3, 4, 5 }), matrix.getRow(r));
        }
    }

    @Test
    public void testBroadcastAMax() throws Exception {
        INDArray matrix = Nd4j.create(5, 5);
        for (int r = 0; r < (matrix.rows()); r++) {
            matrix.getRow(r).assign(Nd4j.create(new double[]{ 1, 2, 3, 2, 1 }));
        }
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3, -4, -5 });
        Nd4j.getExecutioner().exec(new BroadcastAMax(matrix, row, matrix, 1));
        for (int r = 0; r < (matrix.rows()); r++) {
            Assert.assertEquals(Nd4j.create(new double[]{ 1, 2, 3, -4, -5 }), matrix.getRow(r));
        }
    }

    @Test
    public void testBroadcastAMin() throws Exception {
        INDArray matrix = Nd4j.create(5, 5);
        for (int r = 0; r < (matrix.rows()); r++) {
            matrix.getRow(r).assign(Nd4j.create(new double[]{ 2, 3, 3, 4, 1 }));
        }
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3, 4, -5 });
        Nd4j.getExecutioner().exec(new BroadcastAMin(matrix, row, matrix, 1));
        for (int r = 0; r < (matrix.rows()); r++) {
            Assert.assertEquals(Nd4j.create(new double[]{ 1, 2, 3, 4, 1 }), matrix.getRow(r));
        }
    }

    @Test
    public void testLogExpSum1() throws Exception {
        INDArray matrix = Nd4j.create(3, 3);
        for (int r = 0; r < (matrix.rows()); r++) {
            matrix.getRow(r).assign(Nd4j.create(new double[]{ 1, 2, 3 }));
        }
        INDArray res = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.accum.LogSumExp(matrix), 1);
        for (int e = 0; e < (res.length()); e++) {
            Assert.assertEquals(3.407605, res.getDouble(e), 1.0E-5);
        }
    }

    @Test
    public void testLogExpSum2() throws Exception {
        INDArray row = Nd4j.create(new double[]{ 1, 2, 3 });
        double res = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.accum.LogSumExp(row)).z().getDouble(0);
        Assert.assertEquals(3.407605, res, 1.0E-5);
    }

    @Test
    public void testPow1() throws Exception {
        val argX = Nd4j.create(3).assign(2.0);
        val argY = Nd4j.create(new double[]{ 1.0, 2.0, 3.0 });
        val exp = Nd4j.create(new double[]{ 2.0, 4.0, 8.0 });
        val res = Transforms.pow(argX, argY);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testRDiv1() throws Exception {
        val argX = Nd4j.create(3).assign(2.0);
        val argY = Nd4j.create(new double[]{ 1.0, 2.0, 3.0 });
        val exp = Nd4j.create(new double[]{ 0.5, 1.0, 1.5 });
        val res = argX.rdiv(argY);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testEqualOrder1() throws Exception {
        val array = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        val arrayC = array.dup('c');
        val arrayF = array.dup('f');
        Assert.assertEquals(array, arrayC);
        Assert.assertEquals(array, arrayF);
        Assert.assertEquals(arrayC, arrayF);
    }

    @Test
    public void testMatchTransform() throws Exception {
        val array = Nd4j.create(new double[]{ 1, 1, 1, 0, 1, 1 }, 'c');
        val exp = Nd4j.create(new double[]{ 0, 0, 0, 1, 0, 0 }, 'c');
        Op op = new MatchConditionTransform(array, array, 1.0E-5, Conditions.epsEquals(0.0));
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, array);
    }

    @Test
    public void test4DSumView() throws Exception {
        INDArray labels = Nd4j.linspace(1, 160, 160).reshape(new long[]{ 2, 5, 4, 4 });
        // INDArray labels = Nd4j.linspace(1, 192, 192).reshape(new long[]{2, 6, 4, 4});
        val size1 = labels.size(1);
        INDArray classLabels = labels.get(NDArrayIndex.all(), NDArrayIndex.interval(4, size1), NDArrayIndex.all(), NDArrayIndex.all());
        /* Should be 0s and 1s only in the "classLabels" subset - specifically a 1-hot vector, or all 0s
        double minNumber = classLabels.minNumber().doubleValue();
        double maxNumber = classLabels.maxNumber().doubleValue();
        System.out.println("Min/max: " + minNumber + "\t" + maxNumber);
        System.out.println(sum1);
         */
        Assert.assertEquals(classLabels, classLabels.dup());
        // Expect 0 or 1 for each entry (sum of all 0s, or 1-hot vector = 0 or 1)
        INDArray sum1 = classLabels.max(1);
        INDArray sum1_dup = classLabels.dup().max(1);
        Assert.assertEquals(sum1_dup, sum1);
    }

    @Test
    public void testMatMul1() {
        val x = 2;
        val A1 = 3;
        val A2 = 4;
        val B1 = 4;
        val B2 = 3;
        val a = Nd4j.linspace(1, ((x * A1) * A2), ((x * A1) * A2)).reshape(x, A1, A2);
        val b = Nd4j.linspace(1, ((x * B1) * B2), ((x * B1) * B2)).reshape(x, B1, B2);
        // 
        // log.info("C shape: {}", Arrays.toString(c.shapeInfoDataBuffer().asInt()));
    }

    @Test
    public void testReduction_Z1() throws Exception {
        val arrayX = Nd4j.create(10, 10, 10);
        val res = arrayX.max(1, 2);
        Nd4j.getExecutioner().commit();
    }

    @Test
    public void testReduction_Z2() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val res = arrayX.max(0);
        Nd4j.getExecutioner().commit();
    }

    @Test
    public void testReduction_Z3() throws Exception {
        val arrayX = Nd4j.create(200, 300);
        val res = arrayX.maxNumber().doubleValue();
        Nd4j.getExecutioner().commit();
    }

    @Test
    public void testSoftmaxZ1() throws Exception {
        val original = Nd4j.linspace(1, 100, 100).reshape(10, 10);
        val reference = original.dup(original.ordering());
        val expected = original.dup(original.ordering());
        Nd4j.getExecutioner().commit();
        Nd4j.getExecutioner().execAndReturn(new OldSoftMax(expected));
        val result = Nd4j.getExecutioner().execAndReturn(new OldSoftMax(original, original.dup(original.ordering())));
        Assert.assertEquals(reference, original);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testRDiv() throws Exception {
        val x = Nd4j.create(new double[]{ 2, 2, 2 });
        val y = Nd4j.create(new double[]{ 4, 6, 8 });
        val result = Nd4j.createUninitialized(1, 3);
        val op = DynamicCustomOp.builder("RDiv").addInputs(x, y).addOutputs(result).callInplace(false).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(Nd4j.create(new double[]{ 2, 3, 4 }), result);
    }

    @Test
    public void testIm2Col() {
        int kY = 5;
        int kX = 5;
        int sY = 1;
        int sX = 1;
        int pY = 0;
        int pX = 0;
        int dY = 1;
        int dX = 1;
        int inY = 28;
        int inX = 28;
        boolean isSameMode = true;
        val input = Nd4j.linspace(1, ((2 * inY) * inX), ((2 * inY) * inX)).reshape(2, 1, inY, inX);
        val output = Nd4j.create(2, 1, 5, 5, 28, 28);
        val im2colOp = Im2col.builder().inputArrays(new INDArray[]{ input }).outputs(new INDArray[]{ output }).conv2DConfig(org.nd4j.linalg.api.ops.impl.layers.convolution.config.Conv2DConfig.builder().kh(kY).kw(kX).kh(kY).kw(kX).sy(sY).sx(sX).ph(pY).pw(pX).dh(dY).dw(dX).isSameMode(isSameMode).build()).build();
        Nd4j.getExecutioner().exec(im2colOp);
        log.info("result: {}", output);
    }

    @Test
    public void testGemmStrides() {
        // 4x5 matrix from arange(20)
        final INDArray X = Nd4j.arange(20).reshape(4, 5);
        for (int i = 0; i < 5; i++) {
            // Get i-th column vector
            final INDArray xi = X.get(NDArrayIndex.all(), NDArrayIndex.point(i));
            // Build outer product
            val trans = xi.transpose();
            final INDArray outerProduct = xi.mmul(trans);
            // Build outer product from duplicated column vectors
            final INDArray outerProductDuped = xi.dup().mmul(xi.transpose().dup());
            // Matrices should equal
            // final boolean eq = outerProduct.equalsWithEps(outerProductDuped, 1e-5);
            // assertTrue(eq);
            Assert.assertEquals(outerProductDuped, outerProduct);
        }
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testReshapeFailure() {
        val a = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        val b = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        val score = a.mmul(b);
        val reshaped1 = score.reshape(2, 100);
        val reshaped2 = score.reshape(2, 1);
    }

    @Test
    public void testScalar_1() {
        val scalar = Nd4j.create(new float[]{ 2.0F }, new long[]{  });
        Assert.assertTrue(scalar.isScalar());
        Assert.assertEquals(1, scalar.length());
        Assert.assertFalse(scalar.isMatrix());
        Assert.assertFalse(scalar.isVector());
        Assert.assertFalse(scalar.isRowVector());
        Assert.assertFalse(scalar.isColumnVector());
        Assert.assertEquals(2.0F, scalar.getFloat(0), 1.0E-5);
    }

    @Test
    public void testScalar_2() {
        val scalar = Nd4j.trueScalar(2.0F);
        val scalar2 = Nd4j.trueScalar(2.0F);
        val scalar3 = Nd4j.trueScalar(3.0F);
        Assert.assertTrue(scalar.isScalar());
        Assert.assertEquals(1, scalar.length());
        Assert.assertFalse(scalar.isMatrix());
        Assert.assertFalse(scalar.isVector());
        Assert.assertFalse(scalar.isRowVector());
        Assert.assertFalse(scalar.isColumnVector());
        Assert.assertEquals(2.0F, scalar.getFloat(0), 1.0E-5);
        Assert.assertEquals(scalar, scalar2);
        Assert.assertNotEquals(scalar, scalar3);
    }

    @Test
    public void testVector_1() {
        val vector = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 5 });
        val vector2 = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 5 });
        val vector3 = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 6 });
        Assert.assertFalse(vector.isScalar());
        Assert.assertEquals(5, vector.length());
        Assert.assertFalse(vector.isMatrix());
        Assert.assertTrue(vector.isVector());
        Assert.assertTrue(vector.isRowVector());
        Assert.assertFalse(vector.isColumnVector());
        Assert.assertEquals(vector, vector2);
        Assert.assertNotEquals(vector, vector3);
    }

    @Test
    public void testVectorScalar_2() {
        val vector = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 5 });
        val scalar = Nd4j.trueScalar(2.0F);
        val exp = Nd4j.trueVector(new float[]{ 3, 4, 5, 6, 7 });
        vector.addi(scalar);
        Assert.assertEquals(exp, vector);
    }

    @Test
    public void testReshapeScalar() {
        val scalar = Nd4j.trueScalar(2.0F);
        val newShape = scalar.reshape(1, 1, 1, 1);
        Assert.assertEquals(4, newShape.rank());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1, 1, 1 }, newShape.shape());
    }

    @Test
    public void testReshapeVector() {
        val vector = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 5, 6 });
        val newShape = vector.reshape(3, 2);
        Assert.assertEquals(2, newShape.rank());
        BaseNd4jTest.assertArrayEquals(new long[]{ 3, 2 }, newShape.shape());
    }

    @Test
    public void testTranspose1() {
        val vector = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 5, 6 });
        BaseNd4jTest.assertArrayEquals(new long[]{ 6 }, vector.shape());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1 }, vector.stride());
        val transposed = vector.transpose();
        BaseNd4jTest.assertArrayEquals(vector.shape(), transposed.shape());
    }

    @Test
    public void testTranspose2() {
        val scalar = Nd4j.trueScalar(2.0F);
        BaseNd4jTest.assertArrayEquals(new long[]{  }, scalar.shape());
        BaseNd4jTest.assertArrayEquals(new long[]{  }, scalar.stride());
        val transposed = scalar.transpose();
        BaseNd4jTest.assertArrayEquals(scalar.shape(), transposed.shape());
    }

    @Test
    public void testMatmul_128by256() throws Exception {
        val mA = Nd4j.create(128, 156).assign(1.0F);
        val mB = Nd4j.create(156, 256).assign(1.0F);
        val mC = Nd4j.create(128, 256);
        val mE = Nd4j.create(128, 256).assign(156.0F);
        val mL = mA.mmul(mB);
        val op = DynamicCustomOp.builder("matmul").addInputs(mA, mB).addOutputs(mC).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(mE, mC);
    }

    @Test
    public void testScalarSqueeze() {
        val scalar = Nd4j.create(new float[]{ 2.0F }, new long[]{ 1, 1 });
        val output = Nd4j.trueScalar(0.0F);
        val exp = Nd4j.trueScalar(2.0F);
        val op = DynamicCustomOp.builder("squeeze").addInputs(scalar).addOutputs(output).build();
        val shape = Nd4j.getExecutioner().calculateOutputShape(op).get(0);
        BaseNd4jTest.assertArrayEquals(new long[]{  }, shape);
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, output);
    }

    @Test
    public void testScalarVectorSqueeze() {
        val scalar = Nd4j.create(new float[]{ 2.0F }, new long[]{ 1 });
        BaseNd4jTest.assertArrayEquals(new long[]{ 1 }, scalar.shape());
        val output = Nd4j.trueScalar(0.0F);
        val exp = Nd4j.trueScalar(2.0F);
        val op = DynamicCustomOp.builder("squeeze").addInputs(scalar).addOutputs(output).build();
        val shape = Nd4j.getExecutioner().calculateOutputShape(op).get(0);
        BaseNd4jTest.assertArrayEquals(new long[]{  }, shape);
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, output);
    }

    @Test
    public void testVectorSqueeze() {
        val vector = Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6 }, new long[]{ 1, 6 });
        val output = Nd4j.trueVector(new float[]{ 0, 0, 0, 0, 0, 0 });
        val exp = Nd4j.trueVector(new float[]{ 1, 2, 3, 4, 5, 6 });
        val op = DynamicCustomOp.builder("squeeze").addInputs(vector).addOutputs(output).build();
        val shape = Nd4j.getExecutioner().calculateOutputShape(op).get(0);
        BaseNd4jTest.assertArrayEquals(new long[]{ 6 }, shape);
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, output);
    }

    @Test
    public void testVectorGemv() {
        val vectorL = Nd4j.create(new float[]{ 1, 2, 3 }, new long[]{ 3, 1 });
        val vectorN = Nd4j.create(new float[]{ 1, 2, 3 }, new long[]{ 3 });
        val matrix = Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new long[]{ 3, 3 });
        log.info("vectorN: {}", vectorN);
        log.info("vectorL: {}", vectorL);
        val outN = matrix.mmul(vectorN);
        val outL = matrix.mmul(vectorL);
        Assert.assertEquals(outL, outN);
        Assert.assertEquals(1, outN.rank());
    }

    @Test
    public void testMatrixReshape() {
        val matrix = Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new long[]{ 3, 3 });
        val exp = Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new long[]{ 9 });
        val reshaped = matrix.reshape((-1));
        BaseNd4jTest.assertArrayEquals(exp.shape(), reshaped.shape());
        Assert.assertEquals(exp, reshaped);
    }

    @Test
    public void testVectorScalarConcat() {
        val vector = Nd4j.trueVector(new float[]{ 1, 2 });
        val scalar = Nd4j.trueScalar(3.0F);
        val output = Nd4j.trueVector(new float[]{ 0, 0, 0 });
        val exp = Nd4j.trueVector(new float[]{ 1, 2, 3 });
        val op = // axis
        DynamicCustomOp.builder("concat").addInputs(vector, scalar).addOutputs(output).addIntegerArguments(0).build();
        val shape = Nd4j.getExecutioner().calculateOutputShape(op).get(0);
        BaseNd4jTest.assertArrayEquals(exp.shape(), shape);
        Nd4j.getExecutioner().exec(op);
        BaseNd4jTest.assertArrayEquals(exp.shape(), output.shape());
        Assert.assertEquals(exp, output);
    }

    @Test
    public void testValueArrayOf_1() {
        val vector = Nd4j.valueArrayOf(new long[]{ 5 }, 2.0F);
        val exp = Nd4j.trueVector(new float[]{ 2, 2, 2, 2, 2 });
        BaseNd4jTest.assertArrayEquals(exp.shape(), vector.shape());
        Assert.assertEquals(exp, vector);
    }

    @Test
    public void testValueArrayOf_2() {
        val scalar = Nd4j.valueArrayOf(new long[]{  }, 2.0F);
        val exp = Nd4j.trueScalar(2.0F);
        BaseNd4jTest.assertArrayEquals(exp.shape(), scalar.shape());
        Assert.assertEquals(exp, scalar);
    }

    @Test
    public void testArrayCreation() {
        val vector = Nd4j.create(new float[]{ 1, 2, 3 }, new long[]{ 3 }, 'c');
        val exp = Nd4j.trueVector(new float[]{ 1, 2, 3 });
        BaseNd4jTest.assertArrayEquals(exp.shape(), vector.shape());
        Assert.assertEquals(exp, vector);
    }

    @Test
    public void testACosh() {
        // http://www.wolframalpha.com/input/?i=acosh(x)
        INDArray in = Nd4j.linspace(1, 3, 20);
        INDArray out = Nd4j.getExecutioner().execAndReturn(new ACosh(in.dup()));
        INDArray exp = Nd4j.create(in.shape());
        for (int i = 0; i < (in.length()); i++) {
            double x = in.getDouble(i);
            double y = Math.log((x + ((Math.sqrt((x - 1))) * (Math.sqrt((x + 1))))));
            exp.putScalar(i, y);
        }
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testCosh() {
        // http://www.wolframalpha.com/input/?i=cosh(x)
        INDArray in = Nd4j.linspace((-2), 2, 20);
        INDArray out = Transforms.cosh(in, true);
        INDArray exp = Nd4j.create(in.shape());
        for (int i = 0; i < (in.length()); i++) {
            double x = in.getDouble(i);
            double y = 0.5 * ((Math.exp((-x))) + (Math.exp(x)));
            exp.putScalar(i, y);
        }
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testAtanh() {
        // http://www.wolframalpha.com/input/?i=atanh(x)
        INDArray in = Nd4j.linspace((-0.9), 0.9, 10);
        INDArray out = Transforms.atanh(in, true);
        INDArray exp = Nd4j.create(in.shape());
        for (int i = 0; i < 10; i++) {
            double x = in.getDouble(i);
            // Using "alternative form" from: http://www.wolframalpha.com/input/?i=atanh(x)
            double y = (0.5 * (Math.log((x + 1.0)))) - (0.5 * (Math.log((1.0 - x))));
            exp.putScalar(i, y);
        }
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testLastIndex() {
        INDArray in = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 0 }, new double[]{ 1, 1, 0, 0 } });
        INDArray exp0 = Nd4j.create(new double[]{ 1, 1, 0, -1 });
        INDArray exp1 = Nd4j.create(new double[]{ 2, 1 }).transpose();
        INDArray out0 = BooleanIndexing.lastIndex(in, Conditions.equals(1), 0);
        INDArray out1 = BooleanIndexing.lastIndex(in, Conditions.equals(1), 1);
        Assert.assertEquals(exp0, out0);
        Assert.assertEquals(exp1, out1);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testBadReduce3Call() {
        val x = Nd4j.create(400, 20);
        val y = Nd4j.ones(1, 20);
        x.distance2(y);
    }

    @Test
    public void testReduce3AlexBug() {
        val arr = Nd4j.linspace(1, 100, 100).reshape('f', 10, 10).dup('c');
        val arr2 = Nd4j.linspace(1, 100, 100).reshape('c', 10, 10);
        val out = Nd4j.getExecutioner().exec(new EuclideanDistance(arr, arr2), 1);
        val exp = Nd4j.create(new double[]{ 151.93748, 128.86038, 108.37435, 92.22256, 82.9759, 82.9759, 92.22256, 108.37435, 128.86038, 151.93748 });
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testAllDistancesEdgeCase1() {
        val x = Nd4j.create(400, 20).assign(2.0);
        val y = Nd4j.ones(1, 20);
        val z = Transforms.allEuclideanDistances(x, y, 1);
        val exp = Nd4j.create(400, 1).assign(4.47214);
        Assert.assertEquals(exp, z);
    }

    @Test
    public void testConcat_1() throws Exception {
        for (char order : new char[]{ 'c', 'f' }) {
            INDArray arr1 = Nd4j.create(new double[]{ 1, 2 }, order);
            INDArray arr2 = Nd4j.create(new double[]{ 3, 4 }, order);
            INDArray out = Nd4j.concat(0, arr1, arr2);
            Nd4j.getExecutioner().commit();
            INDArray exp = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } });
            Assert.assertEquals(String.valueOf(order), exp, out);
        }
    }

    @Test
    public void testRdiv() {
        final INDArray a = Nd4j.create(new double[]{ 2.0, 2.0, 2.0, 2.0 });
        final INDArray b = Nd4j.create(new double[]{ 1.0, 2.0, 4.0, 8.0 });
        final INDArray c = Nd4j.create(new double[]{ 2.0, 2.0 }).reshape(2, 1);
        final INDArray d = Nd4j.create(new double[]{ 1.0, 2.0, 4.0, 8.0 }).reshape(2, 2);
        final INDArray expected = Nd4j.create(new double[]{ 2.0, 1.0, 0.5, 0.25 });
        final INDArray expected2 = Nd4j.create(new double[]{ 2.0, 1.0, 0.5, 0.25 }).reshape(2, 2);
        Assert.assertEquals(expected, a.div(b));
        Assert.assertEquals(expected, b.rdiv(a));
        Assert.assertEquals(expected, b.rdiv(2));
        Assert.assertEquals(expected2, d.rdivColumnVector(c));
        Assert.assertEquals(expected, b.rdiv(Nd4j.scalar(2)));
        Assert.assertEquals(expected, b.rdivColumnVector(Nd4j.scalar(2)));
    }

    @Test
    public void testRsub() {
        final INDArray a = Nd4j.create(new double[]{ 2.0, 2.0, 2.0, 2.0 });
        final INDArray b = Nd4j.create(new double[]{ 1.0, 2.0, 4.0, 8.0 });
        final INDArray c = Nd4j.create(new double[]{ 2.0, 2.0 }).reshape(2, 1);
        final INDArray d = Nd4j.create(new double[]{ 1.0, 2.0, 4.0, 8.0 }).reshape('c', 2, 2);
        final INDArray expected = Nd4j.create(new double[]{ 1.0, 0.0, -2.0, -6.0 });
        final INDArray expected2 = Nd4j.create(new double[]{ 1, 0, -2.0, -6.0 }).reshape('c', 2, 2);
        Assert.assertEquals(expected, a.sub(b));
        Assert.assertEquals(expected, b.rsub(a));
        Assert.assertEquals(expected, b.rsub(2));
        Assert.assertEquals(expected2, d.rsubColumnVector(c));
        Assert.assertEquals(expected, b.rsub(Nd4j.scalar(2)));
        Assert.assertEquals(expected, b.rsubColumnVector(Nd4j.scalar(2)));
    }

    @Test
    public void testHalfStuff() {
        if (!(Nd4j.getExecutioner().getClass().getSimpleName().toLowerCase().contains("cuda")))
            return;

        val dtype = Nd4j.dataType();
        Nd4j.setDataType(HALF);
        val arr = Nd4j.ones(3, 3);
        arr.addi(2.0F);
        val exp = Nd4j.create(3, 3).assign(3.0F);
        Assert.assertEquals(exp, arr);
        Nd4j.setDataType(dtype);
    }

    @Test
    public void testInconsistentOutput() {
        INDArray in = Nd4j.rand(1, 802816);
        INDArray W = Nd4j.rand(802816, 1);
        INDArray b = Nd4j.create(1);
        INDArray out = Nd4jTestsC.fwd(in, W, b);
        for (int i = 0; i < 100; i++) {
            INDArray out2 = Nd4jTestsC.fwd(in, W, b);// l.activate(inToLayer1, false, LayerWorkspaceMgr.noWorkspaces());

            Assert.assertEquals((("Failed at iteration [" + (String.valueOf(i))) + "]"), out, out2);
        }
    }

    @Test
    public void test3D_create_1() {
        val jArray = new float[2][3][4];
        Nd4jTestsC.fillJvmArray3D(jArray);
        val iArray = Nd4j.create(jArray);
        val fArray = ArrayUtil.flatten(jArray);
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 3, 4 }, iArray.shape());
        BaseNd4jTest.assertArrayEquals(fArray, iArray.data().asFloat(), 1.0E-5F);
        int cnt = 0;
        for (val f : fArray)
            Assert.assertTrue((("Failed for element [" + (cnt++)) + "]"), (f > 0.0F));

    }

    @Test
    public void test4D_create_1() {
        val jArray = new float[2][3][4][5];
        Nd4jTestsC.fillJvmArray4D(jArray);
        val iArray = Nd4j.create(jArray);
        val fArray = ArrayUtil.flatten(jArray);
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 3, 4, 5 }, iArray.shape());
        BaseNd4jTest.assertArrayEquals(fArray, iArray.data().asFloat(), 1.0E-5F);
        int cnt = 0;
        for (val f : fArray)
            Assert.assertTrue((("Failed for element [" + (cnt++)) + "]"), (f > 0.0F));

    }

    @Test
    public void testBroadcast_1() {
        val array1 = Nd4j.linspace(1, 10, 10).reshape(5, 1, 2).broadcast(5, 4, 2);
        val array2 = Nd4j.linspace(1, 20, 20).reshape(5, 4, 1).broadcast(5, 4, 2);
        val exp = Nd4j.create(new float[]{ 2.0F, 3.0F, 3.0F, 4.0F, 4.0F, 5.0F, 5.0F, 6.0F, 8.0F, 9.0F, 9.0F, 10.0F, 10.0F, 11.0F, 11.0F, 12.0F, 14.0F, 15.0F, 15.0F, 16.0F, 16.0F, 17.0F, 17.0F, 18.0F, 20.0F, 21.0F, 21.0F, 22.0F, 22.0F, 23.0F, 23.0F, 24.0F, 26.0F, 27.0F, 27.0F, 28.0F, 28.0F, 29.0F, 29.0F, 30.0F }).reshape(5, 4, 2);
        array1.addi(array2);
        Assert.assertEquals(exp, array1);
    }

    @Test
    public void testAddiColumnEdge() {
        INDArray arr1 = Nd4j.create(1, 5);
        arr1.addiColumnVector(Nd4j.ones(1));
        Assert.assertEquals(Nd4j.ones(1, 5), arr1);
    }

    @Test
    public void testMmulViews_1() {
        val arrayX = Nd4j.linspace(1, 27, 27).reshape(3, 3, 3);
        val arrayA = Nd4j.linspace(1, 9, 9).reshape(3, 3);
        val arrayB = arrayX.dup('f');
        val arraya = arrayX.slice(0);
        val arrayb = arrayB.slice(0);
        val exp = arrayA.mmul(arrayA);
        Assert.assertEquals(exp, arraya.mmul(arrayA));
        Assert.assertEquals(exp, arraya.mmul(arraya));
        Assert.assertEquals(exp, arrayb.mmul(arrayb));
    }

    @Test
    public void testTile_1() {
        val array = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        val exp = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 4.0, 5.0, 6.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 4.0, 5.0, 6.0 }, new int[]{ 4, 6 });
        val output = Nd4j.create(4, 6);
        val op = DynamicCustomOp.builder("tile").addInputs(array).addIntegerArguments(2, 2).addOutputs(output).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, output);
    }

    @Test
    public void testRelativeError_1() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val arrayY = Nd4j.ones(10, 10);
        val exp = Nd4j.ones(10, 10);
        Nd4j.getExecutioner().exec(new BinaryRelativeError(arrayX, arrayY, arrayX, 0.1));
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testMeshGrid() {
        INDArray x1 = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        INDArray y1 = Nd4j.create(new double[]{ 5, 6, 7 });
        INDArray expX = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3, 4 }, new double[]{ 1, 2, 3, 4 }, new double[]{ 1, 2, 3, 4 } });
        INDArray expY = Nd4j.create(new double[][]{ new double[]{ 5, 5, 5, 5 }, new double[]{ 6, 6, 6, 6 }, new double[]{ 7, 7, 7, 7 } });
        INDArray[] exp = new INDArray[]{ expX, expY };
        INDArray[] out1 = Nd4j.meshgrid(x1, y1);
        BaseNd4jTest.assertArrayEquals(out1, exp);
        INDArray[] out2 = Nd4j.meshgrid(x1.transpose(), y1.transpose());
        BaseNd4jTest.assertArrayEquals(out2, exp);
        INDArray[] out3 = Nd4j.meshgrid(x1, y1.transpose());
        BaseNd4jTest.assertArrayEquals(out3, exp);
        INDArray[] out4 = Nd4j.meshgrid(x1.transpose(), y1);
        BaseNd4jTest.assertArrayEquals(out4, exp);
        // Test views:
        INDArray x2 = Nd4j.create(1, 9).get(NDArrayIndex.all(), NDArrayIndex.interval(1, 2, 7, true)).assign(x1);
        INDArray y2 = Nd4j.create(1, 7).get(NDArrayIndex.all(), NDArrayIndex.interval(1, 2, 5, true)).assign(y1);
        INDArray[] out5 = Nd4j.meshgrid(x2, y2);
        BaseNd4jTest.assertArrayEquals(out5, exp);
    }

    @Test
    public void testAccumuationWithoutAxis_1() {
        val array = Nd4j.create(3, 3).assign(1.0);
        val result = array.sum();
        Assert.assertEquals(1, result.length());
        Assert.assertEquals(9.0, result.getDouble(0), 1.0E-5);
    }

    @Test
    public void testSummaryStatsEquality_1() {
        log.info("Datatype: {}", Nd4j.dataType());
        for (boolean biasCorrected : new boolean[]{ false, true }) {
            INDArray indArray1 = Nd4j.rand(1, 4, 10);
            double std = indArray1.stdNumber(biasCorrected).doubleValue();
            val standardDeviation = new StandardDeviation(biasCorrected);
            double std2 = standardDeviation.evaluate(indArray1.data().asDouble());
            log.info("Bias corrected = {}", biasCorrected);
            log.info("nd4j std: {}", std);
            log.info("apache math3 std: {}", std2);
            Assert.assertEquals(std, std2, 1.0E-5);
        }
    }

    @Test
    public void testMeanEdgeCase_C() {
        INDArray arr = Nd4j.linspace(1, 30, 30).reshape(new int[]{ 3, 10, 1 }).dup('c');
        INDArray arr2 = arr.mean(2);
        INDArray exp = arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0));
        Assert.assertEquals(exp, arr2);
    }

    @Test
    public void testMeanEdgeCase_F() {
        INDArray arr = Nd4j.linspace(1, 30, 30).reshape(new int[]{ 3, 10, 1 }).dup('f');
        INDArray arr2 = arr.mean(2);
        INDArray exp = arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0));
        Assert.assertEquals(exp, arr2);
    }

    @Test
    public void testMeanEdgeCase2_C() {
        INDArray arr = Nd4j.linspace(1, 60, 60).reshape(new int[]{ 3, 10, 2 }).dup('c');
        INDArray arr2 = arr.mean(2);
        INDArray exp = arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0));
        exp.addi(arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1)));
        exp.divi(2);
        Assert.assertEquals(exp, arr2);
    }

    @Test
    public void testMeanEdgeCase2_F() {
        INDArray arr = Nd4j.linspace(1, 60, 60).reshape(new int[]{ 3, 10, 2 }).dup('f');
        INDArray arr2 = arr.mean(2);
        INDArray exp = arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0));
        exp.addi(arr.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1)));
        exp.divi(2);
        Assert.assertEquals(exp, arr2);
    }

    @Test
    public void testLegacyDeserialization_1() throws Exception {
        val f = new ClassPathResource("legacy/NDArray.bin").getFile();
        val array = Nd4j.read(new FileInputStream(f));
        val exp = Nd4j.linspace(1, 120, 120).reshape(2, 3, 4, 5);
        Assert.assertEquals(120, array.length());
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 3, 4, 5 }, array.shape());
        Assert.assertEquals(exp, array);
    }

    @Test
    public void testTearPile_1() {
        val source = Nd4j.rand(new int[]{ 10, 15 });
        val list = Nd4j.tear(source, 1);
        // just want to ensure that axis is right one
        Assert.assertEquals(10, list.length);
        val result = Nd4j.pile(list);
        Assert.assertEquals(source.shapeInfoDataBuffer(), result.shapeInfoDataBuffer());
        Assert.assertEquals(source, result);
    }
}

