/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.math.primitives.matrix;


import org.apache.ignite.ml.math.exceptions.CardinalityException;
import org.apache.ignite.ml.math.exceptions.SingularMatrixException;
import org.apache.ignite.ml.math.primitives.matrix.impl.DenseMatrix;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link LUDecomposition}.
 */
public class LUDecompositionTest {
    /**
     *
     */
    private Matrix testL;

    /**
     *
     */
    private Matrix testU;

    /**
     *
     */
    private Matrix testP;

    /**
     *
     */
    private Matrix testMatrix;

    /**
     *
     */
    private int[] rawPivot;

    /**
     *
     */
    @Test
    public void getL() throws Exception {
        Matrix luDecompositionL = new LUDecomposition(testMatrix).getL();
        Assert.assertEquals("Unexpected row size.", testL.rowSize(), luDecompositionL.rowSize());
        Assert.assertEquals("Unexpected column size.", testL.columnSize(), luDecompositionL.columnSize());
        for (int i = 0; i < (testL.rowSize()); i++)
            for (int j = 0; j < (testL.columnSize()); j++)
                Assert.assertEquals((((("Unexpected value at (" + i) + ",") + j) + ")."), testL.getX(i, j), luDecompositionL.getX(i, j), 1.0E-7);


        luDecompositionL.destroy();
    }

    /**
     *
     */
    @Test
    public void getU() throws Exception {
        Matrix luDecompositionU = new LUDecomposition(testMatrix).getU();
        Assert.assertEquals("Unexpected row size.", testU.rowSize(), luDecompositionU.rowSize());
        Assert.assertEquals("Unexpected column size.", testU.columnSize(), luDecompositionU.columnSize());
        for (int i = 0; i < (testU.rowSize()); i++)
            for (int j = 0; j < (testU.columnSize()); j++)
                Assert.assertEquals((((("Unexpected value at (" + i) + ",") + j) + ")."), testU.getX(i, j), luDecompositionU.getX(i, j), 1.0E-7);


        luDecompositionU.destroy();
    }

    /**
     *
     */
    @Test
    public void getP() throws Exception {
        Matrix luDecompositionP = new LUDecomposition(testMatrix).getP();
        Assert.assertEquals("Unexpected row size.", testP.rowSize(), luDecompositionP.rowSize());
        Assert.assertEquals("Unexpected column size.", testP.columnSize(), luDecompositionP.columnSize());
        for (int i = 0; i < (testP.rowSize()); i++)
            for (int j = 0; j < (testP.columnSize()); j++)
                Assert.assertEquals((((("Unexpected value at (" + i) + ",") + j) + ")."), testP.getX(i, j), luDecompositionP.getX(i, j), 1.0E-7);


        luDecompositionP.destroy();
    }

    /**
     *
     */
    @Test
    public void getPivot() throws Exception {
        Vector pivot = new LUDecomposition(testMatrix).getPivot();
        Assert.assertEquals("Unexpected pivot size.", rawPivot.length, pivot.size());
        for (int i = 0; i < (testU.rowSize()); i++)
            Assert.assertEquals(("Unexpected value at " + i), rawPivot[i], (((int) (pivot.get(i))) + 1));

    }

    /**
     * Test for {@link MatrixUtil} features (more specifically, we test matrix which does not have a native like/copy
     * methods support).
     */
    @Test
    public void matrixUtilTest() {
        LUDecomposition dec = new LUDecomposition(testMatrix);
        Matrix luDecompositionL = dec.getL();
        Assert.assertEquals("Unexpected L row size.", testL.rowSize(), luDecompositionL.rowSize());
        Assert.assertEquals("Unexpected L column size.", testL.columnSize(), luDecompositionL.columnSize());
        for (int i = 0; i < (testL.rowSize()); i++)
            for (int j = 0; j < (testL.columnSize()); j++)
                Assert.assertEquals((((("Unexpected L value at (" + i) + ",") + j) + ")."), testL.getX(i, j), luDecompositionL.getX(i, j), 1.0E-7);


        Matrix luDecompositionU = dec.getU();
        Assert.assertEquals("Unexpected U row size.", testU.rowSize(), luDecompositionU.rowSize());
        Assert.assertEquals("Unexpected U column size.", testU.columnSize(), luDecompositionU.columnSize());
        for (int i = 0; i < (testU.rowSize()); i++)
            for (int j = 0; j < (testU.columnSize()); j++)
                Assert.assertEquals((((("Unexpected U value at (" + i) + ",") + j) + ")."), testU.getX(i, j), luDecompositionU.getX(i, j), 1.0E-7);


        Matrix luDecompositionP = dec.getP();
        Assert.assertEquals("Unexpected P row size.", testP.rowSize(), luDecompositionP.rowSize());
        Assert.assertEquals("Unexpected P column size.", testP.columnSize(), luDecompositionP.columnSize());
        for (int i = 0; i < (testP.rowSize()); i++)
            for (int j = 0; j < (testP.columnSize()); j++)
                Assert.assertEquals((((("Unexpected P value at (" + i) + ",") + j) + ")."), testP.getX(i, j), luDecompositionP.getX(i, j), 1.0E-7);


        dec.close();
    }

    /**
     *
     */
    @Test
    public void singularDeterminant() throws Exception {
        Assert.assertEquals("Unexpected determinant for singular matrix decomposition.", 0.0, determinant(), 0.0);
    }

    /**
     *
     */
    @Test(expected = CardinalityException.class)
    public void solveVecWrongSize() throws Exception {
        new LUDecomposition(testMatrix).solve(new org.apache.ignite.ml.math.primitives.vector.impl.DenseVector(((testMatrix.rowSize()) + 1)));
    }

    /**
     *
     */
    @Test(expected = SingularMatrixException.class)
    public void solveVecSingularMatrix() throws Exception {
        new LUDecomposition(new DenseMatrix(testMatrix.rowSize(), testMatrix.rowSize())).solve(new org.apache.ignite.ml.math.primitives.vector.impl.DenseVector(testMatrix.rowSize()));
    }

    /**
     *
     */
    @Test
    public void solveVec() throws Exception {
        Vector sol = new LUDecomposition(testMatrix).solve(new org.apache.ignite.ml.math.primitives.vector.impl.DenseVector(testMatrix.rowSize()));
        Assert.assertEquals("Wrong solution vector size.", testMatrix.rowSize(), sol.size());
        for (int i = 0; i < (sol.size()); i++)
            Assert.assertEquals(("Unexpected value at index " + i), 0.0, sol.getX(i), 1.0E-7);

    }

    /**
     *
     */
    @Test(expected = CardinalityException.class)
    public void solveMtxWrongSize() throws Exception {
        solve(new DenseMatrix(((testMatrix.rowSize()) + 1), testMatrix.rowSize()));
    }

    /**
     *
     */
    @Test(expected = SingularMatrixException.class)
    public void solveMtxSingularMatrix() throws Exception {
        new LUDecomposition(new DenseMatrix(testMatrix.rowSize(), testMatrix.rowSize())).solve(new DenseMatrix(testMatrix.rowSize(), testMatrix.rowSize()));
    }

    /**
     *
     */
    @Test
    public void solveMtx() throws Exception {
        Matrix sol = new LUDecomposition(testMatrix).solve(new DenseMatrix(testMatrix.rowSize(), testMatrix.rowSize()));
        Assert.assertEquals("Wrong solution matrix row size.", testMatrix.rowSize(), sol.rowSize());
        Assert.assertEquals("Wrong solution matrix column size.", testMatrix.rowSize(), sol.columnSize());
        for (int row = 0; row < (sol.rowSize()); row++)
            for (int col = 0; col < (sol.columnSize()); col++)
                Assert.assertEquals((((("Unexpected P value at (" + row) + ",") + col) + ")."), 0.0, sol.getX(row, col), 1.0E-7);


    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void nullMatrixTest() {
        new LUDecomposition(null);
    }

    /**
     *
     */
    @Test(expected = CardinalityException.class)
    public void nonSquareMatrixTest() {
        new LUDecomposition(new DenseMatrix(2, 3));
    }
}

