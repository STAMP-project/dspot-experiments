/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.netlib;


import ARPACK.Ritz.LA;
import ARPACK.Ritz.SA;
import org.junit.Assert;
import org.junit.Test;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.EVD;
import smile.math.matrix.Matrix;


/**
 *
 *
 * @author Haifeng Li
 */
public class ARPACKTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    double[][] eigenVectors = new double[][]{ new double[]{ 0.6881997, -0.07121225, 0.722018 }, new double[]{ 0.3700456, 0.89044952, -0.2648886 }, new double[]{ 0.6240573, -0.44947578, -0.6391588 } };

    double[] eigenValues = new double[]{ 1.7498382, 0.3165784, 0.1335834 };

    public ARPACKTest() {
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testARPACK() {
        System.out.println("ARPACK");
        DenseMatrix a = Matrix.newInstance(A);
        a.setSymmetric(true);
        EVD result = ARPACK.eigen(a, 2, SA);
        Assert.assertEquals(eigenValues[1], result.getEigenValues()[0], 1.0E-4);
        Assert.assertEquals(eigenValues[2], result.getEigenValues()[1], 1.0E-4);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Math.abs(eigenVectors[i][1]), Math.abs(result.getEigenVectors().get(i, 0)), 1.0E-4);
        }
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Math.abs(eigenVectors[i][2]), Math.abs(result.getEigenVectors().get(i, 1)), 1.0E-4);
        }
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testARPACK1() {
        System.out.println("ARPACK1");
        DenseMatrix a = Matrix.newInstance(A);
        a.setSymmetric(true);
        EVD result = ARPACK.eigen(a, 1, LA);
        Assert.assertEquals(eigenValues[0], result.getEigenValues()[0], 1.0E-4);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Math.abs(eigenVectors[i][0]), Math.abs(result.getEigenVectors().get(i, 0)), 1.0E-4);
        }
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testARPACK2() {
        System.out.println("ARPACK2");
        A = new double[500][500];
        A[0][0] = A[1][1] = A[2][2] = A[3][3] = 2.0;
        for (int i = 4; i < 500; i++)
            A[i][i] = (500 - i) / 500.0;

        DenseMatrix a = Matrix.newInstance(A);
        a.setSymmetric(true);
        EVD result = ARPACK.eigen(a, 6, LA);
        Assert.assertEquals(2.0, result.getEigenValues()[0], 1.0E-4);
        Assert.assertEquals(2.0, result.getEigenValues()[1], 1.0E-4);
        Assert.assertEquals(2.0, result.getEigenValues()[2], 1.0E-4);
        Assert.assertEquals(2.0, result.getEigenValues()[3], 1.0E-4);
        Assert.assertEquals(0.992, result.getEigenValues()[4], 1.0E-4);
        Assert.assertEquals(0.99, result.getEigenValues()[5], 1.0E-4);
    }
}

