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
public class LUTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    double[] b = new double[]{ 0.5, 0.5, 0.5 };

    double[] x = new double[]{ -0.2027027, 0.8783784, 0.472973 };

    double[][] B = new double[][]{ new double[]{ 0.5, 0.2 }, new double[]{ 0.5, 0.8 }, new double[]{ 0.5, 0.3 } };

    double[][] X = new double[][]{ new double[]{ -0.2027027, -1.2837838 }, new double[]{ 0.8783784, 2.2297297 }, new double[]{ 0.472973, 0.6621622 } };

    public LUTest() {
    }

    /**
     * Test of solve method, of class LUDecomposition.
     */
    @Test
    public void testSolve() {
        System.out.println("solve a vector");
        JMatrix a = new JMatrix(A);
        LU result = a.lu();
        result.solve(b);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(x[i], b[i], 1.0E-7);
        }
    }

    /**
     * Test of solve method, of class LUDecomposition.
     */
    @Test
    public void testSolveMatrix() {
        System.out.println("solve a matrix");
        JMatrix a = new JMatrix(A);
        LU result = a.lu();
        JMatrix b = new JMatrix(B);
        result.solve(b);
        for (int i = 0; i < (X.length); i++) {
            for (int j = 0; j < (X[i].length); j++) {
                Assert.assertEquals(X[i][j], b.get(i, j), 1.0E-7);
            }
        }
    }
}

