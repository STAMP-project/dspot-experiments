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
public class Nd4jTestsComparisonC extends BaseNd4jTest {
    private static Logger log = LoggerFactory.getLogger(Nd4jTestsComparisonC.class);

    public static final int SEED = 123;

    Type initialType;

    public Nd4jTestsComparisonC(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testGemmWithOpsCommonsMath() {
        List<Pair<INDArray, String>> first = NDArrayCreationUtil.getAllTestMatricesWithShape(3, 5, Nd4jTestsComparisonC.SEED);
        List<Pair<INDArray, String>> firstT = NDArrayCreationUtil.getAllTestMatricesWithShape(5, 3, Nd4jTestsComparisonC.SEED);
        List<Pair<INDArray, String>> second = NDArrayCreationUtil.getAllTestMatricesWithShape(5, 4, Nd4jTestsComparisonC.SEED);
        List<Pair<INDArray, String>> secondT = NDArrayCreationUtil.getAllTestMatricesWithShape(4, 5, Nd4jTestsComparisonC.SEED);
        double[] alpha = new double[]{ 1.0, -0.5, 2.5 };
        double[] beta = new double[]{ 0.0, -0.25, 1.5 };
        INDArray cOrig = Nd4j.linspace(1, 12, 12).reshape(3, 4);
        for (int i = 0; i < (first.size()); i++) {
            for (int j = 0; j < (second.size()); j++) {
                for (int k = 0; k < (alpha.length); k++) {
                    for (int m = 0; m < (beta.length); m++) {
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
                        String errorMsgff = Nd4jTestsComparisonC.getGemmErrorMsg(i, j, false, false, a, b, p1, p2);
                        String errorMsgft = Nd4jTestsComparisonC.getGemmErrorMsg(i, j, false, true, a, b, p1, p2T);
                        String errorMsgtf = Nd4jTestsComparisonC.getGemmErrorMsg(i, j, true, false, a, b, p1T, p2);
                        String errorMsgtt = Nd4jTestsComparisonC.getGemmErrorMsg(i, j, true, true, a, b, p1T, p2T);
                        System.out.println(String.format("Running iteration %d %d %d %d", i, j, k, m));
                        Assert.assertTrue(errorMsgff, CheckUtil.checkGemm(p1.getFirst(), p2.getFirst(), cff, false, false, a, b, 1.0E-4, 1.0E-6));
                        Assert.assertTrue(errorMsgft, CheckUtil.checkGemm(p1.getFirst(), p2T.getFirst(), cft, false, true, a, b, 1.0E-4, 1.0E-6));
                        Assert.assertTrue(errorMsgtf, CheckUtil.checkGemm(p1T.getFirst(), p2.getFirst(), ctf, true, false, a, b, 1.0E-4, 1.0E-6));
                        Assert.assertTrue(errorMsgtt, CheckUtil.checkGemm(p1T.getFirst(), p2T.getFirst(), ctt, true, true, a, b, 1.0E-4, 1.0E-6));
                        // Also: Confirm that if the C array is uninitialized and beta is 0.0, we don't have issues like 0*NaN = NaN
                        if (b == 0.0) {
                            cff.assign(Double.NaN);
                            cft.assign(Double.NaN);
                            ctf.assign(Double.NaN);
                            ctt.assign(Double.NaN);
                            Assert.assertTrue(errorMsgff, CheckUtil.checkGemm(p1.getFirst(), p2.getFirst(), cff, false, false, a, b, 1.0E-4, 1.0E-6));
                            Assert.assertTrue(errorMsgft, CheckUtil.checkGemm(p1.getFirst(), p2T.getFirst(), cft, false, true, a, b, 1.0E-4, 1.0E-6));
                            Assert.assertTrue(errorMsgtf, CheckUtil.checkGemm(p1T.getFirst(), p2.getFirst(), ctf, true, false, a, b, 1.0E-4, 1.0E-6));
                            Assert.assertTrue(errorMsgtt, CheckUtil.checkGemm(p1T.getFirst(), p2T.getFirst(), ctt, true, true, a, b, 1.0E-4, 1.0E-6));
                        }
                    }
                }
            }
        }
    }
}

