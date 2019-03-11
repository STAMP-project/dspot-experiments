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


import org.junit.Assert;
import org.junit.Test;
import smile.math.matrix.EVD;


/**
 *
 *
 * @author Haifeng Li
 */
public class EVDTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    double[][] eigenVectors = new double[][]{ new double[]{ 0.6881997, -0.07121225, 0.722018 }, new double[]{ 0.3700456, 0.89044952, -0.2648886 }, new double[]{ 0.6240573, -0.44947578, -0.6391588 } };

    double[] eigenValues = new double[]{ 1.7498382, 0.3165784, 0.1335834 };

    double[][] B = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.8, 0.3, 0.8 } };

    double[][] eigenVectorsB = new double[][]{ new double[]{ 0.6706167, 0.05567643, 0.6876103 }, new double[]{ 0.3584968, -0.88610002, -0.1508644 }, new double[]{ 0.6494254, 0.46013791, -0.7102337 } };

    double[] eigenValuesB = new double[]{ 1.79171122, 0.31908143, 0.08920735 };

    public EVDTest() {
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testDecompose() {
        System.out.println("decompose");
        NLMatrix a = new NLMatrix(A);
        a.setSymmetric(true);
        EVD result = a.eigen();
        Assert.assertTrue(Math.equals(eigenValues, result.getEigenValues(), 1.0E-7));
        Assert.assertEquals(eigenVectors.length, result.getEigenVectors().nrows());
        Assert.assertEquals(eigenVectors[0].length, result.getEigenVectors().ncols());
        for (int i = 0; i < (eigenVectors.length); i++) {
            for (int j = 0; j < (eigenVectors[i].length); j++) {
                Assert.assertEquals(Math.abs(eigenVectors[i][j]), Math.abs(result.getEigenVectors().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testDecompose2() {
        System.out.println("decompose");
        NLMatrix a = new NLMatrix(A);
        a.setSymmetric(true);
        double[] result = a.eig();
        Assert.assertEquals((2 * (a.nrows())), result.length);
        for (int i = 0; i < (eigenValues.length); i++)
            Assert.assertEquals(eigenValues[i], result[i], 1.0E-7);

        for (int i = eigenValues.length; i < (result.length); i++)
            Assert.assertEquals(0.0, result[i], 1.0E-7);

    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testDecompose3() {
        System.out.println("decompose");
        NLMatrix a = new NLMatrix(B);
        EVD result = a.eigen();
        Assert.assertTrue(Math.equals(eigenValuesB, result.getEigenValues(), 1.0E-7));
        Assert.assertEquals(eigenVectorsB.length, result.getEigenVectors().nrows());
        Assert.assertEquals(eigenVectorsB[0].length, result.getEigenVectors().ncols());
        for (int i = 0; i < (eigenVectorsB.length); i++) {
            for (int j = 0; j < (eigenVectorsB[i].length); j++) {
                Assert.assertEquals(Math.abs(eigenVectorsB[i][j]), Math.abs(result.getEigenVectors().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testDecompose4() {
        System.out.println("decompose");
        NLMatrix a = new NLMatrix(B);
        double[] result = a.eig();
        Assert.assertEquals((2 * (a.nrows())), result.length);
        for (int i = 0; i < (eigenValuesB.length); i++)
            Assert.assertEquals(eigenValuesB[i], result[i], 1.0E-7);

        for (int i = eigenValuesB.length; i < (result.length); i++)
            Assert.assertEquals(0.0, result[i], 1.0E-7);

    }
}

