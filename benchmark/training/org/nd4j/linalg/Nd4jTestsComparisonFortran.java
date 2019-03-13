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
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.checkutil.CheckUtil;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.primitives.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests comparing Nd4j ops to other libraries
 */
@RunWith(Parameterized.class)
public class Nd4jTestsComparisonFortran extends BaseNd4jTest {
    private static Logger log = LoggerFactory.getLogger(Nd4jTestsComparisonFortran.class);

    public static final int SEED = 123;

    Type initialType;

    public Nd4jTestsComparisonFortran(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testCrash() {
        INDArray array3d = Nd4j.ones(1, 10, 10);
        Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(array3d, new int[]{ 0 });
        Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(array3d, new int[]{ 1 });
        INDArray array4d = Nd4j.ones(1, 10, 10, 10);
        Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(array4d, new int[]{ 0 });
    }

    @Test
    public void testMmulWithOpsCommonsMath() {
        List<Pair<INDArray, String>> first = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonFortran.SEED);
        List<Pair<INDArray, String>> second = NDArrayCreationUtil.getAllTestMatricesWithShape(5, 4, Nd4jTestsComparisonFortran.SEED);
        for (int i = 0; i < (first.size()); i++) {
            for (int j = 0; j < (second.size()); j++) {
                Pair<INDArray, String> p1 = first.get(i);
                Pair<INDArray, String> p2 = second.get(j);
                String errorMsg = Nd4jTestsComparisonFortran.getTestWithOpsErrorMsg(i, j, "mmul", p1, p2);
                Assert.assertTrue(errorMsg, CheckUtil.checkMmul(p1.getFirst(), p2.getFirst(), 1.0E-4, 1.0E-6));
            }
        }
    }

    @Test
    public void testGemmWithOpsCommonsMath() {
        List<Pair<INDArray, String>> first = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonFortran.SEED);
        List<Pair<INDArray, String>> firstT = NDArrayCreationUtil.getAllTestMatricesWithShape(5, 3, Nd4jTestsComparisonFortran.SEED);
        List<Pair<INDArray, String>> second = NDArrayCreationUtil.getAllTestMatricesWithShape(5, 4, Nd4jTestsComparisonFortran.SEED);
        List<Pair<INDArray, String>> secondT = NDArrayCreationUtil.getAllTestMatricesWithShape(4, 5, Nd4jTestsComparisonFortran.SEED);
        double[] alpha = new double[]{ 1.0, -0.5, 2.5 };
        double[] beta = new double[]{ 0.0, -0.25, 1.5 };
        INDArray cOrig = Nd4j.create(new int[]{ 3, 4 });
        Random r = new Random(12345);
        for (int i = 0; i < (cOrig.size(0)); i++) {
            for (int j = 0; j < (cOrig.size(1)); j++) {
                cOrig.putScalar(new int[]{ i, j }, r.nextDouble());
            }
        }
        for (int i = 0; i < (first.size()); i++) {
            for (int j = 0; j < (second.size()); j++) {
                for (int k = 0; k < (alpha.length); k++) {
                    for (int m = 0; m < (beta.length); m++) {
                        System.out.println(String.format("Running iteration %d %d %d %d", i, j, k, m));
                        INDArray cff = Nd4j.create(cOrig.shape(), 'f');
                        cff.assign(cOrig);
                        INDArray cft = Nd4j.create(cOrig.shape(), 'f');
                        cft.assign(cOrig);
                        INDArray ctf = Nd4j.create(cOrig.shape(), 'f');
                        ctf.assign(cOrig);
                        INDArray ctt = Nd4j.create(cOrig.shape(), 'f');
                        ctt.assign(cOrig);
                        double a = alpha[k];
                        double b = beta[k];
                        Pair<INDArray, String> p1 = first.get(i);
                        Pair<INDArray, String> p1T = firstT.get(i);
                        Pair<INDArray, String> p2 = second.get(j);
                        Pair<INDArray, String> p2T = secondT.get(j);
                        String errorMsgff = Nd4jTestsComparisonFortran.getGemmErrorMsg(i, j, false, false, a, b, p1, p2);
                        String errorMsgft = Nd4jTestsComparisonFortran.getGemmErrorMsg(i, j, false, true, a, b, p1, p2T);
                        String errorMsgtf = Nd4jTestsComparisonFortran.getGemmErrorMsg(i, j, true, false, a, b, p1T, p2);
                        String errorMsgtt = Nd4jTestsComparisonFortran.getGemmErrorMsg(i, j, true, true, a, b, p1T, p2T);
                        Assert.assertTrue(errorMsgff, CheckUtil.checkGemm(p1.getFirst(), p2.getFirst(), cff, false, false, a, b, 1.0E-4, 1.0E-6));
                        Assert.assertTrue(errorMsgft, CheckUtil.checkGemm(p1.getFirst(), p2T.getFirst(), cft, false, true, a, b, 1.0E-4, 1.0E-6));
                        Assert.assertTrue(errorMsgtf, CheckUtil.checkGemm(p1T.getFirst(), p2.getFirst(), ctf, true, false, a, b, 1.0E-4, 1.0E-6));
                        Assert.assertTrue(errorMsgtt, CheckUtil.checkGemm(p1T.getFirst(), p2T.getFirst(), ctt, true, true, a, b, 1.0E-4, 1.0E-6));
                    }
                }
            }
        }
    }

    @Test
    public void testGemvApacheCommons() {
        int[] rowsArr = new int[]{ 4, 4, 4, 8, 8, 8 };
        int[] colsArr = new int[]{ 2, 1, 10, 2, 1, 10 };
        for (int x = 0; x < (rowsArr.length); x++) {
            int rows = rowsArr[x];
            int cols = colsArr[x];
            List<Pair<INDArray, String>> matrices = NDArrayCreationUtil.getAllTestMatricesWithShape(rows, cols, 12345);
            List<Pair<INDArray, String>> vectors = NDArrayCreationUtil.getAllTestMatricesWithShape(cols, 1, 12345);
            for (int i = 0; i < (matrices.size()); i++) {
                for (int j = 0; j < (vectors.size()); j++) {
                    Pair<INDArray, String> p1 = matrices.get(i);
                    Pair<INDArray, String> p2 = vectors.get(j);
                    String errorMsg = Nd4jTestsComparisonFortran.getTestWithOpsErrorMsg(i, j, "mmul", p1, p2);
                    INDArray m = p1.getFirst();
                    INDArray v = p2.getFirst();
                    RealMatrix rm = new BlockRealMatrix(((int) (m.rows())), ((int) (m.columns())));
                    for (int r = 0; r < (m.rows()); r++) {
                        for (int c = 0; c < (m.columns()); c++) {
                            double d = m.getDouble(r, c);
                            rm.setEntry(r, c, d);
                        }
                    }
                    RealMatrix rv = new BlockRealMatrix(cols, 1);
                    for (int r = 0; r < (v.rows()); r++) {
                        double d = v.getDouble(r, 0);
                        rv.setEntry(r, 0, d);
                    }
                    INDArray gemv = m.mmul(v);
                    RealMatrix gemv2 = rm.multiply(rv);
                    BaseNd4jTest.assertArrayEquals(new int[]{ rows, 1 }, gemv.shape());
                    BaseNd4jTest.assertArrayEquals(new int[]{ rows, 1 }, new int[]{ gemv2.getRowDimension(), gemv2.getColumnDimension() });
                    // Check entries:
                    for (int r = 0; r < rows; r++) {
                        double exp = gemv2.getEntry(r, 0);
                        double act = gemv.getDouble(r, 0);
                        Assert.assertEquals(errorMsg, exp, act, 1.0E-5);
                    }
                }
            }
        }
    }

    @Test
    public void testAddSubtractWithOpsCommonsMath() {
        List<Pair<INDArray, String>> first = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonFortran.SEED);
        List<Pair<INDArray, String>> second = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonFortran.SEED);
        for (int i = 0; i < (first.size()); i++) {
            for (int j = 0; j < (second.size()); j++) {
                Pair<INDArray, String> p1 = first.get(i);
                Pair<INDArray, String> p2 = second.get(j);
                String errorMsg1 = Nd4jTestsComparisonFortran.getTestWithOpsErrorMsg(i, j, "add", p1, p2);
                String errorMsg2 = Nd4jTestsComparisonFortran.getTestWithOpsErrorMsg(i, j, "sub", p1, p2);
                boolean addFail = CheckUtil.checkAdd(p1.getFirst(), p2.getFirst(), 1.0E-4, 1.0E-6);
                Assert.assertTrue(errorMsg1, addFail);
                boolean subFail = CheckUtil.checkSubtract(p1.getFirst(), p2.getFirst(), 1.0E-4, 1.0E-6);
                Assert.assertTrue(errorMsg2, subFail);
            }
        }
    }

    @Test
    public void testMulDivOnCheckUtilMatrices() {
        List<Pair<INDArray, String>> first = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonFortran.SEED);
        List<Pair<INDArray, String>> second = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonFortran.SEED);
        for (int i = 0; i < (first.size()); i++) {
            for (int j = 0; j < (second.size()); j++) {
                Pair<INDArray, String> p1 = first.get(i);
                Pair<INDArray, String> p2 = second.get(j);
                String errorMsg1 = Nd4jTestsComparisonFortran.getTestWithOpsErrorMsg(i, j, "mul", p1, p2);
                String errorMsg2 = Nd4jTestsComparisonFortran.getTestWithOpsErrorMsg(i, j, "div", p1, p2);
                Assert.assertTrue(errorMsg1, CheckUtil.checkMulManually(p1.getFirst(), p2.getFirst(), 1.0E-4, 1.0E-6));
                Assert.assertTrue(errorMsg2, CheckUtil.checkDivManually(p1.getFirst(), p2.getFirst(), 1.0E-4, 1.0E-6));
            }
        }
    }
}

