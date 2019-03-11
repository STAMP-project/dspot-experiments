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
package org.apache.ignite.ml.math.primitives.vector;


import org.apache.ignite.ml.math.exceptions.IndexException;
import org.apache.ignite.ml.math.primitives.matrix.Matrix;
import org.apache.ignite.ml.math.primitives.vector.impl.VectorizedViewMatrix;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link VectorizedViewMatrix}.
 */
public class MatrixVectorViewTest {
    /**
     *
     */
    private static final String UNEXPECTED_VALUE = "Unexpected value";

    /**
     *
     */
    private static final int SMALL_SIZE = 3;

    /**
     *
     */
    private static final int IMPOSSIBLE_SIZE = -1;

    /**
     *
     */
    private Matrix parent;

    /**
     *
     */
    @Test
    public void testDiagonal() {
        Vector vector = parent.viewDiagonal();
        for (int i = 0; i < (MatrixVectorViewTest.SMALL_SIZE); i++)
            assertView(i, i, vector, i);

    }

    /**
     *
     */
    @Test
    public void testRow() {
        for (int i = 0; i < (MatrixVectorViewTest.SMALL_SIZE); i++) {
            Vector viewRow = parent.viewRow(i);
            for (int j = 0; j < (MatrixVectorViewTest.SMALL_SIZE); j++)
                assertView(i, j, viewRow, j);

        }
    }

    /**
     *
     */
    @Test
    public void testCols() {
        for (int i = 0; i < (MatrixVectorViewTest.SMALL_SIZE); i++) {
            Vector viewCol = parent.viewColumn(i);
            for (int j = 0; j < (MatrixVectorViewTest.SMALL_SIZE); j++)
                assertView(j, i, viewCol, j);

        }
    }

    /**
     *
     */
    @Test
    public void basicTest() {
        for (int rowSize : new int[]{ 1, 2, 3, 4 })
            for (int colSize : new int[]{ 1, 2, 3, 4 })
                for (int row = 0; row < rowSize; row++)
                    for (int col = 0; col < colSize; col++)
                        for (int rowStride = 0; rowStride < rowSize; rowStride++)
                            for (int colStride = 0; colStride < colSize; colStride++)
                                if ((rowStride != 0) || (colStride != 0))
                                    assertMatrixVectorView(newMatrix(rowSize, colSize), row, col, rowStride, colStride);







    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void parentNullTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, new VectorizedViewMatrix(null, 1, 1, 1, 1).size());
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void rowNegativeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void colNegativeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void rowTooLargeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void colTooLargeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void rowStrideNegativeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void colStrideNegativeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void rowStrideTooLargeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void colStrideTooLargeTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void bothStridesZeroTest() {
        Assert.assertEquals(MatrixVectorViewTest.IMPOSSIBLE_SIZE, size());
    }
}

