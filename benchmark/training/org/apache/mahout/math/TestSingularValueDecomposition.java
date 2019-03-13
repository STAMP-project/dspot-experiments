/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math;


import Functions.ABS;
import Functions.PLUS;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.mahout.common.RandomUtils;
import org.junit.Test;


// To launch this test only : mvn test -Dtest=org.apache.mahout.math.TestSingularValueDecomposition
public final class TestSingularValueDecomposition extends MahoutTestCase {
    private final double[][] testSquare = new double[][]{ new double[]{ 24.0 / 25.0, 43.0 / 25.0 }, new double[]{ 57.0 / 25.0, 24.0 / 25.0 } };

    private final double[][] testNonSquare = new double[][]{ new double[]{ (-540.0) / 625.0, 963.0 / 625.0, (-216.0) / 625.0 }, new double[]{ (-1730.0) / 625.0, (-744.0) / 625.0, 1008.0 / 625.0 }, new double[]{ (-720.0) / 625.0, 1284.0 / 625.0, (-288.0) / 625.0 }, new double[]{ (-360.0) / 625.0, 192.0 / 625.0, 1756.0 / 625.0 } };

    private static final double NORM_TOLERANCE = 1.0E-13;

    @Test
    public void testMoreRows() {
        double[] singularValues = new double[]{ 123.456, 2.3, 1.001, 0.999 };
        int rows = (singularValues.length) + 2;
        int columns = singularValues.length;
        Random r = RandomUtils.getRandom();
        SingularValueDecomposition svd = new SingularValueDecomposition(TestSingularValueDecomposition.createTestMatrix(r, rows, columns, singularValues));
        double[] computedSV = svd.getSingularValues();
        assertEquals(singularValues.length, computedSV.length);
        for (int i = 0; i < (singularValues.length); ++i) {
            assertEquals(singularValues[i], computedSV[i], 1.0E-10);
        }
    }

    @Test
    public void testMoreColumns() {
        double[] singularValues = new double[]{ 123.456, 2.3, 1.001, 0.999 };
        int rows = singularValues.length;
        int columns = (singularValues.length) + 2;
        Random r = RandomUtils.getRandom();
        SingularValueDecomposition svd = new SingularValueDecomposition(TestSingularValueDecomposition.createTestMatrix(r, rows, columns, singularValues));
        double[] computedSV = svd.getSingularValues();
        assertEquals(singularValues.length, computedSV.length);
        for (int i = 0; i < (singularValues.length); ++i) {
            assertEquals(singularValues[i], computedSV[i], 1.0E-10);
        }
    }

    /**
     * test dimensions
     */
    @Test
    public void testDimensions() {
        Matrix matrix = new DenseMatrix(testSquare);
        int m = matrix.numRows();
        int n = matrix.numCols();
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        assertEquals(m, svd.getU().numRows());
        assertEquals(m, svd.getU().numCols());
        assertEquals(m, svd.getS().numCols());
        assertEquals(n, svd.getS().numCols());
        assertEquals(n, svd.getV().numRows());
        assertEquals(n, svd.getV().numCols());
    }

    /**
     * Test based on a dimension 4 Hadamard matrix.
     */
    // getCovariance to be implemented
    @Test
    public void testHadamard() {
        Matrix matrix = new DenseMatrix(new double[][]{ new double[]{ 15.0 / 2.0, 5.0 / 2.0, 9.0 / 2.0, 3.0 / 2.0 }, new double[]{ 5.0 / 2.0, 15.0 / 2.0, 3.0 / 2.0, 9.0 / 2.0 }, new double[]{ 9.0 / 2.0, 3.0 / 2.0, 15.0 / 2.0, 5.0 / 2.0 }, new double[]{ 3.0 / 2.0, 9.0 / 2.0, 5.0 / 2.0, 15.0 / 2.0 } });
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        assertEquals(16.0, svd.getSingularValues()[0], 1.0E-14);
        assertEquals(8.0, svd.getSingularValues()[1], 1.0E-14);
        assertEquals(4.0, svd.getSingularValues()[2], 1.0E-14);
        assertEquals(2.0, svd.getSingularValues()[3], 1.0E-14);
        Matrix fullCovariance = new DenseMatrix(new double[][]{ new double[]{ 85.0 / 1024, (-51.0) / 1024, (-75.0) / 1024, 45.0 / 1024 }, new double[]{ (-51.0) / 1024, 85.0 / 1024, 45.0 / 1024, (-75.0) / 1024 }, new double[]{ (-75.0) / 1024, 45.0 / 1024, 85.0 / 1024, (-51.0) / 1024 }, new double[]{ 45.0 / 1024, (-75.0) / 1024, (-51.0) / 1024, 85.0 / 1024 } });
        assertEquals(0.0, Algebra.getNorm(fullCovariance.minus(svd.getCovariance(0.0))), 1.0E-14);
        Matrix halfCovariance = new DenseMatrix(new double[][]{ new double[]{ 5.0 / 1024, (-3.0) / 1024, 5.0 / 1024, (-3.0) / 1024 }, new double[]{ (-3.0) / 1024, 5.0 / 1024, (-3.0) / 1024, 5.0 / 1024 }, new double[]{ 5.0 / 1024, (-3.0) / 1024, 5.0 / 1024, (-3.0) / 1024 }, new double[]{ (-3.0) / 1024, 5.0 / 1024, (-3.0) / 1024, 5.0 / 1024 } });
        assertEquals(0.0, Algebra.getNorm(halfCovariance.minus(svd.getCovariance(6.0))), 1.0E-14);
    }

    /**
     * test A = USVt
     */
    @Test
    public void testAEqualUSVt() {
        TestSingularValueDecomposition.checkAEqualUSVt(new DenseMatrix(testSquare));
        TestSingularValueDecomposition.checkAEqualUSVt(new DenseMatrix(testNonSquare));
        TestSingularValueDecomposition.checkAEqualUSVt(new DenseMatrix(testNonSquare).transpose());
    }

    /**
     * test that U is orthogonal
     */
    @Test
    public void testUOrthogonal() {
        TestSingularValueDecomposition.checkOrthogonal(getU());
        TestSingularValueDecomposition.checkOrthogonal(getU());
        TestSingularValueDecomposition.checkOrthogonal(getU());
    }

    /**
     * test that V is orthogonal
     */
    @Test
    public void testVOrthogonal() {
        TestSingularValueDecomposition.checkOrthogonal(getV());
        TestSingularValueDecomposition.checkOrthogonal(getV());
        TestSingularValueDecomposition.checkOrthogonal(getV());
    }

    /**
     * test matrices values
     */
    @Test
    public void testMatricesValues1() {
        SingularValueDecomposition svd = new SingularValueDecomposition(new DenseMatrix(testSquare));
        Matrix uRef = new DenseMatrix(new double[][]{ new double[]{ 3.0 / 5.0, 4.0 / 5.0 }, new double[]{ 4.0 / 5.0, (-3.0) / 5.0 } });
        Matrix sRef = new DenseMatrix(new double[][]{ new double[]{ 3.0, 0.0 }, new double[]{ 0.0, 1.0 } });
        Matrix vRef = new DenseMatrix(new double[][]{ new double[]{ 4.0 / 5.0, (-3.0) / 5.0 }, new double[]{ 3.0 / 5.0, 4.0 / 5.0 } });
        // check values against known references
        Matrix u = svd.getU();
        assertEquals(0, Algebra.getNorm(u.minus(uRef)), TestSingularValueDecomposition.NORM_TOLERANCE);
        Matrix s = svd.getS();
        assertEquals(0, Algebra.getNorm(s.minus(sRef)), TestSingularValueDecomposition.NORM_TOLERANCE);
        Matrix v = svd.getV();
        assertEquals(0, Algebra.getNorm(v.minus(vRef)), TestSingularValueDecomposition.NORM_TOLERANCE);
    }

    /**
     * test condition number
     */
    @Test
    public void testConditionNumber() {
        SingularValueDecomposition svd = new SingularValueDecomposition(new DenseMatrix(testSquare));
        // replace 1.0e-15 with 1.5e-15
        assertEquals(3.0, svd.cond(), 1.5E-15);
    }

    @Test
    public void testSvdHang() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        System.out.printf("starting hanging-svd\n");
        final Matrix m = readTsv("hanging-svd.tsv");
        SingularValueDecomposition svd = new SingularValueDecomposition(m);
        assertEquals(0, m.minus(svd.getU().times(svd.getS()).times(svd.getV().transpose())).aggregate(PLUS, ABS), 1.0E-10);
        System.out.printf("No hang\n");
    }
}

