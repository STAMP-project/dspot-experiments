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
package smile.nd4j;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class NDMatrixTest {
    double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.0 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.0, 0.3, 0.8 } };

    double[] b = new double[]{ 0.5, 0.5, 0.5 };

    double[][] C = new double[][]{ new double[]{ 0.97, 0.56, 0.12 }, new double[]{ 0.56, 0.5, 0.39 }, new double[]{ 0.12, 0.39, 0.73 } };

    NDMatrix matrix = new NDMatrix(A);

    /**
     * Test of nrows method, of class ColumnMajorMatrix.
     */
    @Test
    public void testNrows() {
        System.out.println("nrows");
        Assert.assertEquals(3, matrix.nrows());
    }

    /**
     * Test of ncols method, of class ColumnMajorMatrix.
     */
    @Test
    public void testNcols() {
        System.out.println("ncols");
        Assert.assertEquals(3, matrix.ncols());
    }

    /**
     * Test of get method, of class ColumnMajorMatrix.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Assert.assertEquals(0.9, matrix.get(0, 0), 1.0E-7);
        Assert.assertEquals(0.8, matrix.get(2, 2), 1.0E-7);
        Assert.assertEquals(0.5, matrix.get(1, 1), 1.0E-7);
        Assert.assertEquals(0.0, matrix.get(2, 0), 1.0E-7);
        Assert.assertEquals(0.0, matrix.get(0, 2), 1.0E-7);
        Assert.assertEquals(0.4, matrix.get(0, 1), 1.0E-7);
    }

    /**
     * Test of ax method, of class ColumnMajorMatrix.
     */
    @Test
    public void testAx() {
        System.out.println("ax");
        double[] d = new double[matrix.nrows()];
        matrix.ax(b, d);
        Assert.assertEquals(0.65, d[0], 1.0E-7);
        Assert.assertEquals(0.6, d[1], 1.0E-7);
        Assert.assertEquals(0.55, d[2], 1.0E-7);
    }

    /**
     * Test of atx method, of class ColumnMajorMatrix.
     */
    @Test
    public void testAtx() {
        System.out.println("atx");
        double[] d = new double[matrix.nrows()];
        matrix.atx(b, d);
        Assert.assertEquals(0.65, d[0], 1.0E-7);
        Assert.assertEquals(0.6, d[1], 1.0E-7);
        Assert.assertEquals(0.55, d[2], 1.0E-7);
    }

    /**
     * Test of AAT method, of class ColumnMajorMatrix.
     */
    @Test
    public void testAAT() {
        System.out.println("AAT");
        NDMatrix c = matrix.aat();
        Assert.assertEquals(c.nrows(), 3);
        Assert.assertEquals(c.ncols(), 3);
        for (int i = 0; i < (C.length); i++) {
            for (int j = 0; j < (C[i].length); j++) {
                Assert.assertEquals(C[i][j], c.get(i, j), 1.0E-7);
            }
        }
    }

    /**
     * Test of mm method, of class ColumnMajorMatrix.
     */
    @Test
    public void testMm() {
        System.out.println("mm");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[][] B = new double[][]{ new double[]{ 0.6881997, -0.07121225, 0.722018 }, new double[]{ 0.3700456, 0.89044952, -0.2648886 }, new double[]{ 0.6240573, -0.44947578, -0.6391588 } };
        double[][] C = new double[][]{ new double[]{ 0.9527204, -0.2973347, 0.06257778 }, new double[]{ -0.2808735, -0.9403636, -0.19190231 }, new double[]{ 0.1159052, 0.1652528, -0.97941688 } };
        double[][] D = new double[][]{ new double[]{ 0.988714, 0.1482942, -0.0212965 }, new double[]{ 0.1482942, -0.9889421, -0.0015881 }, new double[]{ -0.0212965, -0.0015881, -0.9997719 } };
        double[][] E = new double[][]{ new double[]{ 0.0, 0.0, 1.0 }, new double[]{ 0.0, -1.0, 0.0 }, new double[]{ 1.0, 0.0, 0.0 } };
        NDMatrix a = new NDMatrix(A);
        NDMatrix b = new NDMatrix(B);
        System.out.println(a.atbmm(b));
        Assert.assertTrue(Math.equals(a.abmm(b).array(), C, 1.0E-7));
        Assert.assertTrue(Math.equals(a.abtmm(b).array(), D, 1.0E-7));
        Assert.assertTrue(Math.equals(a.atbmm(b).array(), E, 1.0E-5));
    }
}

