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


/**
 *
 *
 * @author Haifeng Li
 */
public class LUTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    double[] B = new double[]{ 0.5, 0.5, 0.5 };

    double[] X = new double[]{ -0.2027027, 0.8783784, 0.472973 };

    double[][] B2 = new double[][]{ new double[]{ 0.5, 0.2 }, new double[]{ 0.5, 0.8 }, new double[]{ 0.5, 0.3 } };

    double[][] X2 = new double[][]{ new double[]{ -0.2027027, -1.2837838 }, new double[]{ 0.8783784, 2.2297297 }, new double[]{ 0.472973, 0.6621622 } };

    public LUTest() {
    }

    /**
     * Test of solve method, of class LUDecomposition.
     */
    @Test
    public void testSolve() {
        System.out.println("solve");
        NLMatrix a = new NLMatrix(A);
        LU result = a.lu();
        double[] x = B.clone();
        result.solve(x);
        Assert.assertEquals(X.length, x.length);
        for (int i = 0; i < (X.length); i++) {
            Assert.assertEquals(X[i], x[i], 1.0E-7);
        }
    }

    /**
     * Test of solve method, of class LUDecomposition.
     */
    @Test
    public void testSolveMatrix() {
        System.out.println("solve");
        NLMatrix a = new NLMatrix(A);
        LU result = a.lu();
        NLMatrix x = new NLMatrix(B2);
        result.solve(x);
        Assert.assertEquals(X2.length, x.nrows());
        Assert.assertEquals(X2[0].length, x.ncols());
        for (int i = 0; i < (X2.length); i++) {
            for (int j = 0; j < (X2[i].length); j++) {
                Assert.assertEquals(X2[i][j], x.get(i, j), 1.0E-7);
            }
        }
    }
}

