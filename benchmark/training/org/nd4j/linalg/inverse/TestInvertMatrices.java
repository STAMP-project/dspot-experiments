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
package org.nd4j.linalg.inverse;


import DataType.DOUBLE;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.checkutil.CheckUtil;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by agibsoncccc on 12/7/15.
 */
@RunWith(Parameterized.class)
public class TestInvertMatrices extends BaseNd4jTest {
    public TestInvertMatrices(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testInverse() {
        RealMatrix matrix = new Array2DRowRealMatrix(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } });
        RealMatrix inverse = MatrixUtils.inverse(matrix);
        INDArray arr = InvertMatrix.invert(Nd4j.linspace(1, 4, 4).reshape(2, 2), false);
        for (int i = 0; i < (inverse.getRowDimension()); i++) {
            for (int j = 0; j < (inverse.getColumnDimension()); j++) {
                Assert.assertEquals(arr.getDouble(i, j), inverse.getEntry(i, j), 0.1);
            }
        }
    }

    @Test
    public void testInverseComparison() {
        List<Pair<INDArray, String>> list = NDArrayCreationUtil.getAllTestMatricesWithShape(10, 10, 12345, DOUBLE);
        for (Pair<INDArray, String> p : list) {
            INDArray orig = p.getFirst();
            orig.assign(Nd4j.rand(orig.shape()));
            INDArray inverse = InvertMatrix.invert(orig, false);
            RealMatrix rm = CheckUtil.convertToApacheMatrix(orig);
            RealMatrix rmInverse = getSolver().getInverse();
            INDArray expected = CheckUtil.convertFromApacheMatrix(rmInverse, orig.dataType());
            Assert.assertTrue(p.getSecond(), CheckUtil.checkEntries(expected, inverse, 0.001, 1.0E-4));
        }
    }

    @Test
    public void testInvalidMatrixInversion() {
        try {
            InvertMatrix.invert(Nd4j.create(5, 4), false);
            Assert.fail("No exception thrown for invalid input");
        } catch (Exception e) {
        }
        try {
            InvertMatrix.invert(Nd4j.create(5, 5, 5), false);
            Assert.fail("No exception thrown for invalid input");
        } catch (Exception e) {
        }
        try {
            InvertMatrix.invert(Nd4j.create(1, 5), false);
            Assert.fail("No exception thrown for invalid input");
        } catch (Exception e) {
        }
    }

    @Test
    public void testInvertMatrixScalar() {
        INDArray in = Nd4j.valueArrayOf(new int[]{ 1, 1 }, 2);
        INDArray out1 = InvertMatrix.invert(in, false);
        Assert.assertEquals(Nd4j.valueArrayOf(new int[]{ 1, 1 }, 0.5), out1);
        Assert.assertEquals(Nd4j.valueArrayOf(new int[]{ 1, 1 }, 2), in);
        INDArray out2 = InvertMatrix.invert(in, true);
        Assert.assertTrue((out2 == in));
        Assert.assertEquals(Nd4j.valueArrayOf(new int[]{ 1, 1 }, 0.5), out2);
    }

    /**
     * Example from: <a href="https://www.wolframalpha.com/input/?i=invert+matrix+((1,2),(3,4),(5,6))">here</a>
     */
    @Test
    public void testLeftPseudoInvert() {
        INDArray X = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 }, new double[]{ 5, 6 } });
        INDArray expectedLeftInverse = Nd4j.create(new double[][]{ new double[]{ -16, -4, 8 }, new double[]{ 13, 4, -5 } }).mul((1 / 12.0));
        INDArray leftInverse = InvertMatrix.pLeftInvert(X, false);
        Assert.assertEquals(expectedLeftInverse, leftInverse);
        final INDArray identity3x3 = Nd4j.create(new double[][]{ new double[]{ 1, 0, 0 }, new double[]{ 0, 1, 0 }, new double[]{ 0, 0, 1 } });
        final INDArray identity2x2 = Nd4j.create(new double[][]{ new double[]{ 1, 0 }, new double[]{ 0, 1 } });
        final double precision = 1.0E-5;
        // right inverse
        final INDArray rightInverseCheck = X.mmul(leftInverse);
        // right inverse must not hold since X rows are not linear independent (x_3 + x_1 = 2*x_2)
        Assert.assertFalse(rightInverseCheck.equalsWithEps(identity3x3, precision));
        // left inverse must hold since X columns are linear independent
        final INDArray leftInverseCheck = leftInverse.mmul(X);
        Assert.assertTrue(leftInverseCheck.equalsWithEps(identity2x2, precision));
        // general condition X = X * X^-1 * X
        final INDArray generalCond = X.mmul(leftInverse).mmul(X);
        Assert.assertTrue(X.equalsWithEps(generalCond, precision));
        checkMoorePenroseConditions(X, leftInverse, precision);
    }

    /**
     * Example from: <a href="https://www.wolframalpha.com/input/?i=invert+matrix+((1,2),(3,4),(5,6))^T">here</a>
     */
    @Test
    public void testRightPseudoInvert() {
        INDArray X = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 }, new double[]{ 5, 6 } }).transpose();
        INDArray expectedRightInverse = Nd4j.create(new double[][]{ new double[]{ -16, 13 }, new double[]{ -4, 4 }, new double[]{ 8, -5 } }).mul((1 / 12.0));
        INDArray rightInverse = InvertMatrix.pRightInvert(X, false);
        Assert.assertEquals(expectedRightInverse, rightInverse);
        final INDArray identity3x3 = Nd4j.create(new double[][]{ new double[]{ 1, 0, 0 }, new double[]{ 0, 1, 0 }, new double[]{ 0, 0, 1 } });
        final INDArray identity2x2 = Nd4j.create(new double[][]{ new double[]{ 1, 0 }, new double[]{ 0, 1 } });
        final double precision = 1.0E-5;
        // left inverse
        final INDArray leftInverseCheck = rightInverse.mmul(X);
        // left inverse must not hold since X columns are not linear independent (x_3 + x_1 = 2*x_2)
        Assert.assertFalse(leftInverseCheck.equalsWithEps(identity3x3, precision));
        // left inverse must hold since X rows are linear independent
        final INDArray rightInverseCheck = X.mmul(rightInverse);
        Assert.assertTrue(rightInverseCheck.equalsWithEps(identity2x2, precision));
        // general condition X = X * X^-1 * X
        final INDArray generalCond = X.mmul(rightInverse).mmul(X);
        Assert.assertTrue(X.equalsWithEps(generalCond, precision));
        checkMoorePenroseConditions(X, rightInverse, precision);
    }

    /**
     * Try to compute the right pseudo inverse of a matrix without full row rank (x1 = 2*x2)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRightPseudoInvertWithNonFullRowRank() {
        INDArray X = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 6 }, new double[]{ 5, 10 } }).transpose();
        INDArray rightInverse = InvertMatrix.pRightInvert(X, false);
    }

    /**
     * Try to compute the left pseudo inverse of a matrix without full column rank (x1 = 2*x2)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLeftPseudoInvertWithNonFullColumnRank() {
        INDArray X = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 6 }, new double[]{ 5, 10 } });
        INDArray leftInverse = InvertMatrix.pLeftInvert(X, false);
    }
}

