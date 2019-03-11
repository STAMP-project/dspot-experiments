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
package smile.math.matrix;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class PowerIterationTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    double[][] eigenVectors = new double[][]{ new double[]{ 0.6881997, -0.07121225, 0.722018 }, new double[]{ 0.3700456, 0.89044952, -0.2648886 }, new double[]{ 0.6240573, -0.44947578, -0.6391588 } };

    double[] eigenValues = new double[]{ 1.7498382, 0.3165784, 0.1335834 };

    public PowerIterationTest() {
    }

    /**
     * Test of decompose method, of class EigenValueDecomposition.
     */
    @Test
    public void testEigen() {
        System.out.println("Eigen");
        double[] v = new double[3];
        for (int i = 0; i < (v.length); i++)
            v[i] = 1.0;

        double eigenvalue = PowerIteration.eigen(Matrix.newInstance(A), v, 1.0E-6);
        Assert.assertEquals(eigenValues[0], eigenvalue, 1.0E-4);
        double ratio = Math.abs(((eigenVectors[0][0]) / (v[0])));
        for (int i = 1; i < 3; i++) {
            Assert.assertEquals(ratio, Math.abs(((eigenVectors[i][0]) / (v[i]))), 1.0E-4);
        }
        for (int i = 0; i < (A.length); i++) {
            for (int j = 0; j < (A[i].length); j++)
                A[i][j] = -(A[i][j]);

        }
        for (int i = 0; i < (v.length); i++)
            v[i] = 1.0;

        eigenvalue = PowerIteration.eigen(Matrix.newInstance(A), v, 0.22, 1.0E-4, 4);
        Assert.assertEquals((-(eigenValues[0])), eigenvalue, 0.001);
        ratio = Math.abs(((eigenVectors[0][0]) / (v[0])));
        for (int i = 1; i < 3; i++) {
            Assert.assertEquals(ratio, Math.abs(((eigenVectors[i][0]) / (v[i]))), 0.001);
        }
    }
}

