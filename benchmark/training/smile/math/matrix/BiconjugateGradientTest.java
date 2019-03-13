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
public class BiconjugateGradientTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.0 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.0, 0.3, 0.8 } };

    double[] b = new double[]{ 0.5, 0.5, 0.5 };

    double[] x = new double[b.length];

    public BiconjugateGradientTest() {
    }

    /**
     * Test of solve method on Matrix.
     */
    @Test
    public void testSolveMatrix() {
        System.out.println("naive matrix");
        DenseMatrix naive = Matrix.newInstance(A);
        double[] result = new double[3];
        BiconjugateGradient.solve(naive, b, result);
        Assert.assertEquals(result.length, x.length);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(result[i], x[i], 1.0E-7);
        }
    }

    /**
     * Test of solve method on SparseMatrix.
     */
    @Test
    public void testSolveSparseMatrix() {
        System.out.println("naive matrix");
        int[] rowIndex = new int[]{ 0, 1, 0, 1, 2, 1, 2 };
        int[] colIndex = new int[]{ 0, 2, 5, 7 };
        double[] val = new double[]{ 0.9, 0.4, 0.4, 0.5, 0.3, 0.3, 0.8 };
        SparseMatrix sparse = new SparseMatrix(3, 3, val, rowIndex, colIndex);
        double[] result = new double[3];
        BiconjugateGradient.solve(sparse, b, result);
        Assert.assertEquals(result.length, x.length);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(result[i], x[i], 1.0E-7);
        }
    }
}

