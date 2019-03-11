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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.ignite.IgniteException;
import org.apache.ignite.ml.math.ExternalizeTest;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.apache.ignite.ml.math.primitives.vector.impl.SparseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * See also: {@link AbstractVectorTest} and {@link VectorToMatrixTest}.
 */
public class VectorImplementationsTest {
    // TODO: IGNITE-5723, split this to smaller cohesive test classes
    /**
     *
     */
    @Test
    public void setGetTest() {
        consumeSampleVectors(( v, desc) -> mutateAtIdxTest(v, desc, ( vec, idx, val) -> {
            vec.set(idx, val);
            return val;
        }));
    }

    /**
     *
     */
    @Test
    public void setXTest() {
        consumeSampleVectors(( v, desc) -> mutateAtIdxTest(v, desc, ( vec, idx, val) -> {
            vec.setX(idx, val);
            return val;
        }));
    }

    /**
     *
     */
    @Test
    public void incrementTest() {
        consumeSampleVectors(( v, desc) -> mutateAtIdxTest(v, desc, ( vec, idx, val) -> {
            double old = vec.get(idx);
            vec.increment(idx, val);
            return old + val;
        }));
    }

    /**
     *
     */
    @Test
    public void incrementXTest() {
        consumeSampleVectors(( v, desc) -> mutateAtIdxTest(v, desc, ( vec, idx, val) -> {
            double old = vec.getX(idx);
            vec.incrementX(idx, val);
            return old + val;
        }));
    }

    /**
     *
     */
    @Test
    public void operateXOutOfBoundsTest() {
        consumeSampleVectors(( v, desc) -> {
            if (v instanceof SparseVector)
                return;
            // TODO: IGNITE-5723, find out if it's OK to skip by instances here

            boolean expECaught = false;
            try {
                v.getX((-1));
            } catch (ArrayIndexOutOfBoundsException | IgniteException e) {
                expECaught = true;
            }
            if (!(getXOutOfBoundsOK(v)))
                Assert.assertTrue(("Expect exception at negative index getX in " + desc), expECaught);

            expECaught = false;
            try {
                v.setX((-1), 0);
            } catch (ArrayIndexOutOfBoundsException | IgniteException e) {
                expECaught = true;
            }
            Assert.assertTrue(("Expect exception at negative index setX in " + desc), expECaught);
            expECaught = false;
            try {
                v.incrementX((-1), 1);
            } catch (ArrayIndexOutOfBoundsException | IgniteException e) {
                expECaught = true;
            }
            Assert.assertTrue(("Expect exception at negative index incrementX in " + desc), expECaught);
            expECaught = false;
            try {
                v.getX(v.size());
            } catch (ArrayIndexOutOfBoundsException | IgniteException e) {
                expECaught = true;
            }
            if (!(getXOutOfBoundsOK(v)))
                Assert.assertTrue(("Expect exception at too large index getX in " + desc), expECaught);

            expECaught = false;
            try {
                v.setX(v.size(), 1);
            } catch (ArrayIndexOutOfBoundsException | IgniteException e) {
                expECaught = true;
            }
            Assert.assertTrue(("Expect exception at too large index setX in " + desc), expECaught);
            expECaught = false;
            try {
                v.incrementX(v.size(), 1);
            } catch (ArrayIndexOutOfBoundsException | IgniteException e) {
                expECaught = true;
            }
            Assert.assertTrue(("Expect exception at too large index incrementX in " + desc), expECaught);
        });
    }

    /**
     *
     */
    @Test
    public void sizeTest() {
        final AtomicReference<Integer> expSize = new AtomicReference<>(0);
        consumeSampleVectors(expSize::set, ( v, desc) -> Assert.assertEquals(("Expected size for " + desc), ((int) (expSize.get())), v.size()));
    }

    /**
     *
     */
    @Test
    public void getElementTest() {
        consumeSampleVectors(( v, desc) -> new VectorImplementationsTest.ElementsChecker(v, desc).assertCloseEnough(v));
    }

    /**
     *
     */
    @Test
    public void copyTest() {
        consumeSampleVectors(( v, desc) -> new VectorImplementationsTest.ElementsChecker(v, desc).assertCloseEnough(v.copy()));
    }

    /**
     *
     */
    @Test
    public void divideTest() {
        operationTest(( val, operand) -> val / operand, Vector::divide);
    }

    /**
     *
     */
    @Test
    public void likeTest() {
        for (int card : new int[]{ 1, 2, 4, 8, 16, 32, 64, 128 })
            consumeSampleVectors(( v, desc) -> {
                Class<? extends Vector> expType = expLikeType(v);
                if (expType == null) {
                    try {
                        v.like(card);
                    } catch (UnsupportedOperationException uoe) {
                        return;
                    }
                    Assert.fail(("Expected exception wasn't caught for " + desc));
                    return;
                }
                Vector vLike = v.like(card);
                Assert.assertNotNull(((("Expect non-null like vector for " + (expType.getSimpleName())) + " in ") + desc), vLike);
                Assert.assertEquals(("Expect size equal to cardinality at " + desc), card, vLike.size());
                Class<? extends Vector> actualType = vLike.getClass();
                Assert.assertTrue(((((("Actual vector type " + (actualType.getSimpleName())) + " should be assignable from expected type ") + (expType.getSimpleName())) + " in ") + desc), actualType.isAssignableFrom(expType));
            });

    }

    /**
     *
     */
    @Test
    public void minusTest() {
        operationVectorTest(( operand1, operand2) -> operand1 - operand2, Vector::minus);
    }

    /**
     *
     */
    @Test
    public void plusVectorTest() {
        operationVectorTest(( operand1, operand2) -> operand1 + operand2, Vector::plus);
    }

    /**
     *
     */
    @Test
    public void plusDoubleTest() {
        operationTest(( val, operand) -> val + operand, Vector::plus);
    }

    /**
     *
     */
    @Test
    public void timesVectorTest() {
        operationVectorTest(( operand1, operand2) -> operand1 * operand2, Vector::times);
    }

    /**
     *
     */
    @Test
    public void timesDoubleTest() {
        operationTest(( val, operand) -> val * operand, Vector::times);
    }

    /**
     *
     */
    @Test
    public void viewPartTest() {
        consumeSampleVectors(( v, desc) -> {
            final int size = v.size();
            final double[] ref = new double[size];
            final int delta = (size > 32) ? 3 : 1;// IMPL NOTE this is for faster test execution

            final VectorImplementationsTest.ElementsChecker checker = new VectorImplementationsTest.ElementsChecker(v, ref, desc);
            for (int off = 0; off < size; off += delta)
                for (int len = 1; len < (size - off); len += delta)
                    checker.assertCloseEnough(v.viewPart(off, len), Arrays.copyOfRange(ref, off, (off + len)));


        });
    }

    /**
     *
     */
    @Test
    public void sumTest() {
        toDoubleTest(( ref) -> Arrays.stream(ref).sum(), Vector::sum);
    }

    /**
     *
     */
    @Test
    public void minValueTest() {
        toDoubleTest(( ref) -> Arrays.stream(ref).min().getAsDouble(), Vector::minValue);
    }

    /**
     *
     */
    @Test
    public void maxValueTest() {
        toDoubleTest(( ref) -> Arrays.stream(ref).max().getAsDouble(), Vector::maxValue);
    }

    /**
     *
     */
    @Test
    public void sortTest() {
        consumeSampleVectors(( v, desc) -> {
            if ((VectorImplementationsTest.readOnly()) || (!(v.isArrayBased()))) {
                boolean expECaught = false;
                try {
                    v.sort();
                } catch (UnsupportedOperationException uoe) {
                    expECaught = true;
                }
                Assert.assertTrue(("Expected exception was not caught for sort in " + desc), expECaught);
                return;
            }
            final int size = v.size();
            final double[] ref = new double[size];
            new VectorImplementationsTest.ElementsChecker(v, ref, desc).assertCloseEnough(v.sort(), Arrays.stream(ref).sorted().toArray());
        });
    }

    /**
     *
     */
    @Test
    public void metaAttributesTest() {
        consumeSampleVectors(( v, desc) -> {
            Assert.assertNotNull(("Null meta storage in " + desc), v.getMetaStorage());
            final String key = "test key";
            final String val = "test value";
            final String details = (("key [" + key) + "] for ") + desc;
            v.setAttribute(key, val);
            Assert.assertTrue(("Expect to have meta attribute for " + details), v.hasAttribute(key));
            Assert.assertEquals(("Unexpected meta attribute value for " + details), val, v.getAttribute(key));
            v.removeAttribute(key);
            Assert.assertFalse(("Expect not to have meta attribute for " + details), v.hasAttribute(key));
            Assert.assertNull(("Unexpected meta attribute value for " + details), v.getAttribute(key));
        });
    }

    /**
     *
     */
    @Test
    public void assignDoubleTest() {
        consumeSampleVectors(( v, desc) -> {
            if (VectorImplementationsTest.readOnly())
                return;

            for (double val : new double[]{ 0, -1, 0, 1 }) {
                v.assign(val);
                for (int idx = 0; idx < (v.size()); idx++) {
                    final VectorImplementationsTest.Metric metric = new VectorImplementationsTest.Metric(val, v.get(idx));
                    Assert.assertTrue(((((((("Not close enough at index " + idx) + ", val ") + val) + ", ") + metric) + ", ") + desc), metric.closeEnough());
                }
            }
        });
    }

    /**
     *
     */
    @Test
    public void assignDoubleArrTest() {
        consumeSampleVectors(( v, desc) -> {
            if (VectorImplementationsTest.readOnly())
                return;

            final int size = v.size();
            final double[] ref = new double[size];
            final VectorImplementationsTest.ElementsChecker checker = new VectorImplementationsTest.ElementsChecker(v, ref, desc);
            for (int idx = 0; idx < size; idx++)
                ref[idx] = -(ref[idx]);

            v.assign(ref);
            checker.assertCloseEnough(v, ref);
            assignDoubleArrWrongCardinality(v, desc);
        });
    }

    /**
     *
     */
    @Test
    public void assignVectorTest() {
        consumeSampleVectors(( v, desc) -> {
            if (VectorImplementationsTest.readOnly())
                return;

            final int size = v.size();
            final double[] ref = new double[size];
            final VectorImplementationsTest.ElementsChecker checker = new VectorImplementationsTest.ElementsChecker(v, ref, desc);
            for (int idx = 0; idx < size; idx++)
                ref[idx] = -(ref[idx]);

            v.assign(new DenseVector(ref));
            checker.assertCloseEnough(v, ref);
            assignVectorWrongCardinality(v, desc);
        });
    }

    /**
     *
     */
    @Test
    public void assignFunctionTest() {
        consumeSampleVectors(( v, desc) -> {
            if (VectorImplementationsTest.readOnly())
                return;

            final int size = v.size();
            final double[] ref = new double[size];
            final VectorImplementationsTest.ElementsChecker checker = new VectorImplementationsTest.ElementsChecker(v, ref, desc);
            for (int idx = 0; idx < size; idx++)
                ref[idx] = -(ref[idx]);

            v.assign(( idx) -> ref[idx]);
            checker.assertCloseEnough(v, ref);
        });
    }

    /**
     *
     */
    @Test
    public void minElementTest() {
        consumeSampleVectors(( v, desc) -> {
            final VectorImplementationsTest.ElementsChecker checker = new VectorImplementationsTest.ElementsChecker(v, desc);
            final Vector.Element minE = v.minElement();
            final int minEIdx = minE.index();
            Assert.assertTrue(((("Unexpected index from minElement " + minEIdx) + ", ") + desc), ((minEIdx >= 0) && (minEIdx < (v.size()))));
            final VectorImplementationsTest.Metric metric = new VectorImplementationsTest.Metric(minE.get(), v.minValue());
            Assert.assertTrue(((((("Not close enough minElement at index " + minEIdx) + ", ") + metric) + ", ") + desc), metric.closeEnough());
            checker.assertNewMinElement(v);
        });
    }

    /**
     *
     */
    @Test
    public void maxElementTest() {
        consumeSampleVectors(( v, desc) -> {
            final VectorImplementationsTest.ElementsChecker checker = new VectorImplementationsTest.ElementsChecker(v, desc);
            final Vector.Element maxE = v.maxElement();
            final int minEIdx = maxE.index();
            Assert.assertTrue(((("Unexpected index from minElement " + minEIdx) + ", ") + desc), ((minEIdx >= 0) && (minEIdx < (v.size()))));
            final VectorImplementationsTest.Metric metric = new VectorImplementationsTest.Metric(maxE.get(), v.maxValue());
            Assert.assertTrue(((((("Not close enough maxElement at index " + minEIdx) + ", ") + metric) + ", ") + desc), metric.closeEnough());
            checker.assertNewMaxElement(v);
        });
    }

    /**
     *
     */
    @Test
    public void externalizeTest() {
        new ExternalizeTest<Vector>() {
            /**
             * {@inheritDoc }
             */
            @Override
            public void externalizeTest() {
                consumeSampleVectors(( v, desc) -> externalizeTest(v));
            }
        }.externalizeTest();
    }

    /**
     *
     */
    @Test
    public void hashCodeTest() {
        consumeSampleVectors(( v, desc) -> Assert.assertTrue(("Zero hash code for " + desc), ((v.hashCode()) != 0)));
    }

    /**
     *
     */
    private interface MutateAtIdx {
        /**
         *
         */
        double apply(Vector v, int idx, double val);
    }

    /**
     *
     */
    static class ElementsChecker {
        /**
         *
         */
        private final String fixtureDesc;

        /**
         *
         */
        private final double[] refReadOnly;

        /**
         *
         */
        private final boolean nonNegative;

        /**
         *
         */
        ElementsChecker(Vector v, double[] ref, String fixtureDesc, boolean nonNegative) {
            this.fixtureDesc = fixtureDesc;
            this.nonNegative = nonNegative;
            refReadOnly = ((VectorImplementationsTest.readOnly()) && (ref == null)) ? new double[v.size()] : null;
            init(v, ref);
        }

        /**
         *
         */
        ElementsChecker(Vector v, double[] ref, String fixtureDesc) {
            this(v, ref, fixtureDesc, false);
        }

        /**
         *
         */
        ElementsChecker(Vector v, String fixtureDesc) {
            this(v, null, fixtureDesc);
        }

        /**
         *
         */
        void assertCloseEnough(Vector obtained, double[] exp) {
            final int size = obtained.size();
            for (int i = 0; i < size; i++) {
                final Vector.Element e = obtained.getElement(i);
                if (((refReadOnly) != null) && (exp == null))
                    exp = refReadOnly;

                final VectorImplementationsTest.Metric metric = new VectorImplementationsTest.Metric((exp == null ? generated(i) : exp[i]), e.get());
                Assert.assertEquals(("Unexpected vector index at " + (fixtureDesc)), i, e.index());
                Assert.assertTrue(((((((("Not close enough at index " + i) + ", size ") + size) + ", ") + metric) + ", ") + (fixtureDesc)), metric.closeEnough());
            }
        }

        /**
         *
         */
        void assertCloseEnough(Vector obtained) {
            assertCloseEnough(obtained, null);
        }

        /**
         *
         */
        void assertNewMinElement(Vector v) {
            if (VectorImplementationsTest.readOnly())
                return;

            int exp = (v.size()) / 2;
            v.set(exp, (-(((v.size()) * 2) + 1)));
            Assert.assertEquals(("Unexpected minElement index at " + (fixtureDesc)), exp, v.minElement().index());
        }

        /**
         *
         */
        void assertNewMaxElement(Vector v) {
            if (VectorImplementationsTest.readOnly())
                return;

            int exp = (v.size()) / 2;
            v.set(exp, (((v.size()) * 2) + 1));
            Assert.assertEquals(("Unexpected minElement index at " + (fixtureDesc)), exp, v.maxElement().index());
        }

        /**
         *
         */
        private void init(Vector v, double[] ref) {
            if (VectorImplementationsTest.readOnly()) {
                initReadonly(v, ref);
                return;
            }
            for (Vector.Element e : v.all()) {
                int idx = e.index();
                // IMPL NOTE introduce negative values because their absence
                // blocked catching an ugly bug in AbstractVector#kNorm
                int val = generated(idx);
                e.set(val);
                if (ref != null)
                    ref[idx] = val;

            }
        }

        /**
         *
         */
        private void initReadonly(Vector v, double[] ref) {
            if ((refReadOnly) != null)
                for (Vector.Element e : v.all())
                    refReadOnly[e.index()] = e.get();


            if (ref != null)
                for (Vector.Element e : v.all())
                    ref[e.index()] = e.get();


        }

        /**
         *
         */
        private int generated(int idx) {
            return (nonNegative) || ((idx & 1) == 0) ? idx : -idx;
        }
    }

    /**
     *
     */
    static class Metric {
        // TODO: IGNITE-5824, consider if softer tolerance (like say 0.1 or 0.01) would make sense here
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
            return (new Double(exp).equals(obtained)) || (closeEnoughToZero());
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((("Metric{" + "expected=") + (exp)) + ", obtained=") + (obtained)) + '}';
        }

        /**
         *
         */
        private boolean closeEnoughToZero() {
            return ((new Double(exp).equals(0.0)) && (new Double(obtained).equals((-0.0)))) || ((new Double(exp).equals((-0.0))) && (new Double(obtained).equals(0.0)));
        }
    }
}

