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
public class SparseMatrixTest {
    SparseMatrix sm;

    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.0 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.0, 0.3, 0.8 } };

    double[] b = new double[]{ 0.5, 0.5, 0.5 };

    double[][] C = new double[][]{ new double[]{ 0.97, 0.56, 0.12 }, new double[]{ 0.56, 0.5, 0.39 }, new double[]{ 0.12, 0.39, 0.73 } };

    public SparseMatrixTest() {
        int[] rowIndex = new int[]{ 0, 1, 0, 1, 2, 1, 2 };
        int[] colIndex = new int[]{ 0, 2, 5, 7 };
        double[] val = new double[]{ 0.9, 0.4, 0.4, 0.5, 0.3, 0.3, 0.8 };
        sm = new SparseMatrix(3, 3, val, rowIndex, colIndex);
    }

    /**
     * Test of nrows method, of class SparseMatrix.
     */
    @Test
    public void testNrows() {
        System.out.println("nrows");
        Assert.assertEquals(3, sm.nrows());
    }

    /**
     * Test of ncols method, of class SparseMatrix.
     */
    @Test
    public void testNcols() {
        System.out.println("ncols");
        Assert.assertEquals(3, sm.ncols());
    }

    /**
     * Test of size method, of class SparseMatrix.
     */
    @Test
    public void testNvals() {
        System.out.println("nvals");
        Assert.assertEquals(7, sm.size());
    }

    /**
     * Test of get method, of class SparseMatrix.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Assert.assertEquals(0.9, sm.get(0, 0), 1.0E-7);
        Assert.assertEquals(0.8, sm.get(2, 2), 1.0E-7);
        Assert.assertEquals(0.5, sm.get(1, 1), 1.0E-7);
        Assert.assertEquals(0.0, sm.get(2, 0), 1.0E-7);
        Assert.assertEquals(0.0, sm.get(0, 2), 1.0E-7);
        Assert.assertEquals(0.4, sm.get(0, 1), 1.0E-7);
    }

    /**
     * Test of times method, of class SparseMatrix.
     */
    @Test
    public void testTimes() {
        System.out.println("times");
        SparseMatrix c = sm.abmm(sm);
        Assert.assertEquals(c.nrows(), 3);
        Assert.assertEquals(c.ncols(), 3);
        Assert.assertEquals(c.size(), 9);
        for (int i = 0; i < (C.length); i++) {
            for (int j = 0; j < (C[i].length); j++) {
                Assert.assertEquals(C[i][j], c.get(i, j), 1.0E-7);
            }
        }
    }

    /**
     * Test of AAT method, of class SparseMatrix.
     */
    @Test
    public void testAAT() {
        System.out.println("AAT");
        SparseMatrix c = sm.aat();
        Assert.assertEquals(c.nrows(), 3);
        Assert.assertEquals(c.ncols(), 3);
        Assert.assertEquals(c.size(), 9);
        for (int i = 0; i < (C.length); i++) {
            for (int j = 0; j < (C[i].length); j++) {
                Assert.assertEquals(C[i][j], c.get(i, j), 1.0E-7);
            }
        }
    }
}

