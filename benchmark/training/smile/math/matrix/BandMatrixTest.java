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
public class BandMatrixTest {
    public BandMatrixTest() {
    }

    /**
     * Test of solve method, of class BandMatrix.
     */
    @Test
    public void testSolve() {
        System.out.println("solve");
        double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.0 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.0, 0.3, 0.8 } };
        double[] b = new double[]{ 0.5, 0.5, 0.5 };
        DenseMatrix a = Matrix.newInstance(A);
        LU lu = a.lu();
        double[] x = b.clone();
        lu.solve(x);
        BandMatrix instance = new BandMatrix(3, 1, 1);
        for (int i = 0; i < (A.length); i++) {
            for (int j = 0; j < (A[i].length); j++)
                if ((A[i][j]) != 0.0)
                    instance.set(i, j, A[i][j]);


        }
        instance.decompose();
        double[] result = new double[b.length];
        instance.solve(b, result);
        Assert.assertEquals(result.length, x.length);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(result[i], x[i], 1.0E-7);
        }
        instance.improve(b, result);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(result[i], x[i], 1.0E-15);
        }
    }
}

