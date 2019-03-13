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


import Vector.Element;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.ignite.ml.math.primitives.matrix.Matrix;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for methods of Vector that involve Matrix.
 */
public class VectorToMatrixTest {
    /**
     *
     */
    private static final Map<Class<? extends Vector>, Class<? extends Matrix>> typesMap = VectorToMatrixTest.typesMap();

    /**
     *
     */
    @Test
    public void testHaveLikeMatrix() {
        for (Class<? extends Vector> key : VectorToMatrixTest.typesMap.keySet()) {
            Class<? extends Matrix> val = VectorToMatrixTest.typesMap.get(key);
            if (val == null)
                System.out.println(("Missing test for implementation of likeMatrix for " + (key.getSimpleName())));

        }
    }

    /**
     *
     */
    @Test
    public void testLikeMatrix() {
        consumeSampleVectors(( v, desc) -> {
            if (!(availableForTesting(v)))
                return;

            final Matrix matrix = v.likeMatrix(1, 1);
            Class<? extends Vector> key = v.getClass();
            Class<? extends Matrix> expMatrixType = VectorToMatrixTest.typesMap.get(key);
            Assert.assertNotNull(((("Expect non-null matrix for " + (key.getSimpleName())) + " in ") + desc), matrix);
            Class<? extends Matrix> actualMatrixType = matrix.getClass();
            Assert.assertTrue(((((("Expected matrix type " + (expMatrixType.getSimpleName())) + " should be assignable from actual type ") + (actualMatrixType.getSimpleName())) + " in ") + desc), expMatrixType.isAssignableFrom(actualMatrixType));
            for (int rows : new int[]{ 1, 2 })
                for (int cols : new int[]{ 1, 2 }) {
                    final Matrix actualMatrix = v.likeMatrix(rows, cols);
                    String details = (("rows " + rows) + " cols ") + cols;
                    Assert.assertNotNull(((("Expect non-null matrix for " + details) + " in ") + desc), actualMatrix);
                    Assert.assertEquals(("Unexpected number of rows in " + desc), rows, actualMatrix.rowSize());
                    Assert.assertEquals(("Unexpected number of cols in " + desc), cols, actualMatrix.columnSize());
                }

        });
    }

    /**
     *
     */
    @Test
    public void testToMatrix() {
        consumeSampleVectors(( v, desc) -> {
            if (!(availableForTesting(v)))
                return;

            fillWithNonZeroes(v);
            final Matrix matrixRow = v.toMatrix(true);
            final Matrix matrixCol = v.toMatrix(false);
            for (Vector.Element e : v.all())
                assertToMatrixValue(desc, matrixRow, matrixCol, e.get(), e.index());

        });
    }

    /**
     *
     */
    @Test
    public void testToMatrixPlusOne() {
        consumeSampleVectors(( v, desc) -> {
            if (!(availableForTesting(v)))
                return;

            fillWithNonZeroes(v);
            for (double zeroVal : new double[]{ -1, 0, 1, 2 }) {
                final Matrix matrixRow = v.toMatrixPlusOne(true, zeroVal);
                final Matrix matrixCol = v.toMatrixPlusOne(false, zeroVal);
                final VectorToMatrixTest.Metric metricRow0 = new VectorToMatrixTest.Metric(zeroVal, matrixRow.get(0, 0));
                Assert.assertTrue(((("Not close enough row like " + metricRow0) + " at index 0 in ") + desc), metricRow0.closeEnough());
                final VectorToMatrixTest.Metric metricCol0 = new VectorToMatrixTest.Metric(zeroVal, matrixCol.get(0, 0));
                Assert.assertTrue(((("Not close enough cols like " + metricCol0) + " at index 0 in ") + desc), metricCol0.closeEnough());
                for (Vector.Element e : v.all())
                    assertToMatrixValue(desc, matrixRow, matrixCol, e.get(), ((e.index()) + 1));

            }
        });
    }

    /**
     *
     */
    @Test
    public void testCross() {
        consumeSampleVectors(( v, desc) -> {
            if (!(availableForTesting(v)))
                return;

            fillWithNonZeroes(v);
            for (int delta : new int[]{ -1, 0, 1 }) {
                final int size2 = (v.size()) + delta;
                if (size2 < 1)
                    return;

                final Vector v2 = new DenseVector(size2);
                for (Vector.Element e : v2.all())
                    e.set((size2 - (e.index())));

                assertCross(v, v2, desc);
            }
        });
    }

    /**
     *
     */
    private static class Metric {
        // TODO: IGNITE-5824, consider if softer tolerance (like say 0.1 or 0.01) would make sense here.
        /**
         *
         */
        private final double exp;

        /**
         *
         */
        private final double obtained;

        /**
         * *
         */
        Metric(double exp, double obtained) {
            this.exp = exp;
            this.obtained = obtained;
        }

        /**
         *
         */
        boolean closeEnough() {
            return new Double(exp).equals(obtained);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((("Metric{" + "expected=") + (exp)) + ", obtained=") + (obtained)) + '}';
        }
    }
}

