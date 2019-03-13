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
import Functions.MINUS;
import Functions.MULT;
import Functions.NEGATE;
import Functions.PLUS;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.Random;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.function.Functions;
import org.apache.mahout.math.jet.random.Normal;
import org.apache.mahout.math.random.MultiNormal;
import org.junit.Test;


/**
 * Makes sure that a vector under test acts the same as a DenseVector or RandomAccessSparseVector
 * (according to whether it is dense or sparse).  Most operations need to be done within a reasonable
 * tolerance.
 *
 * The idea is that a new vector implementation can extend AbstractVectorTest to get pretty high
 * confidence that it is working correctly.
 */
public abstract class AbstractVectorTest<T extends Vector> extends MahoutTestCase {
    private static final double FUZZ = 1.0E-13;

    private static final double[] values = new double[]{ 1.1, 2.2, 3.3 };

    private static final double[] gold = new double[]{ 0.0, 1.1, 0.0, 2.2, 0.0, 3.3, 0.0 };

    private Vector test;

    @Test
    public void testSimpleOps() {
        T v0 = vectorToTest(20);
        Random gen = RandomUtils.getRandom();
        Vector v1 = v0.assign(new Normal(0, 1, gen));
        // verify that v0 and v1 share and are identical
        assertEquals(get(12), v1.get(12), 0);
        set(12, gen.nextDouble());
        assertEquals(get(12), v1.get(12), 0);
        assertSame(v0, v1);
        Vector v2 = vectorToTest(20).assign(new Normal(0, 1, gen));
        Vector dv1 = new DenseVector(v1);
        Vector dv2 = new DenseVector(v2);
        Vector sv1 = new RandomAccessSparseVector(v1);
        Vector sv2 = new RandomAccessSparseVector(v2);
        assertEquals(0, dv1.plus(dv2).getDistanceSquared(v1.plus(v2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.plus(dv2).getDistanceSquared(v1.plus(dv2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.plus(dv2).getDistanceSquared(v1.plus(sv2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.plus(dv2).getDistanceSquared(sv1.plus(v2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.times(dv2).getDistanceSquared(v1.times(v2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.times(dv2).getDistanceSquared(v1.times(dv2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.times(dv2).getDistanceSquared(v1.times(sv2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.times(dv2).getDistanceSquared(sv1.times(v2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.minus(dv2).getDistanceSquared(v1.minus(v2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.minus(dv2).getDistanceSquared(v1.minus(dv2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.minus(dv2).getDistanceSquared(v1.minus(sv2)), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.minus(dv2).getDistanceSquared(sv1.minus(v2)), AbstractVectorTest.FUZZ);
        double z = gen.nextDouble();
        assertEquals(0, dv1.divide(z).getDistanceSquared(v1.divide(z)), 1.0E-12);
        assertEquals(0, dv1.times(z).getDistanceSquared(v1.times(z)), 1.0E-12);
        assertEquals(0, dv1.plus(z).getDistanceSquared(v1.plus(z)), 1.0E-12);
        assertEquals(dv1.dot(dv2), v1.dot(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.dot(dv2), v1.dot(dv2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.dot(dv2), v1.dot(sv2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.dot(dv2), sv1.dot(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.dot(dv2), dv1.dot(v2), AbstractVectorTest.FUZZ);
        // first attempt has no cached distances
        assertEquals(dv1.getDistanceSquared(dv2), v1.getDistanceSquared(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), dv1.getDistanceSquared(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), sv1.getDistanceSquared(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), v1.getDistanceSquared(dv2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), v1.getDistanceSquared(sv2), AbstractVectorTest.FUZZ);
        // now repeat with cached sizes
        assertEquals(dv1.getLengthSquared(), v1.getLengthSquared(), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), v1.getDistanceSquared(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), dv1.getDistanceSquared(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), sv1.getDistanceSquared(v2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), v1.getDistanceSquared(dv2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.getDistanceSquared(dv2), v1.getDistanceSquared(sv2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.minValue(), v1.minValue(), AbstractVectorTest.FUZZ);
        assertEquals(dv1.minValueIndex(), v1.minValueIndex());
        assertEquals(dv1.maxValue(), v1.maxValue(), AbstractVectorTest.FUZZ);
        assertEquals(dv1.maxValueIndex(), v1.maxValueIndex());
        Vector nv1 = v1.normalize();
        assertEquals(0, dv1.getDistanceSquared(v1), AbstractVectorTest.FUZZ);
        assertEquals(1, nv1.norm(2), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.normalize().getDistanceSquared(nv1), AbstractVectorTest.FUZZ);
        nv1 = v1.normalize(1);
        assertEquals(0, dv1.getDistanceSquared(v1), AbstractVectorTest.FUZZ);
        assertEquals(1, nv1.norm(1), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.normalize(1).getDistanceSquared(nv1), AbstractVectorTest.FUZZ);
        assertEquals(dv1.norm(0), v1.norm(0), AbstractVectorTest.FUZZ);
        assertEquals(dv1.norm(1), v1.norm(1), AbstractVectorTest.FUZZ);
        assertEquals(dv1.norm(1.5), v1.norm(1.5), AbstractVectorTest.FUZZ);
        assertEquals(dv1.norm(2), v1.norm(2), AbstractVectorTest.FUZZ);
        assertEquals(dv1.zSum(), v1.zSum(), AbstractVectorTest.FUZZ);
        assertEquals((3.1 * (v1.size())), zSum(), AbstractVectorTest.FUZZ);
        assertEquals(0, v1.plus((-3.1)).norm(1), AbstractVectorTest.FUZZ);
        v1.assign(dv1);
        assertEquals(0, v1.getDistanceSquared(dv1), AbstractVectorTest.FUZZ);
        assertEquals(((dv1.zSum()) - ((dv1.size()) * 3.4)), zSum(), AbstractVectorTest.FUZZ);
        assertEquals(((dv1.zSum()) - ((dv1.size()) * 4.5)), zSum(), AbstractVectorTest.FUZZ);
        v1.assign(dv1);
        assertEquals(0, dv1.minus(dv2).getDistanceSquared(v1.assign(v2, MINUS)), AbstractVectorTest.FUZZ);
        v1.assign(dv1);
        assertEquals(dv1.norm(2), Math.sqrt(v1.aggregate(PLUS, Functions.pow(2))), AbstractVectorTest.FUZZ);
        assertEquals(dv1.dot(dv2), v1.aggregate(v2, PLUS, MULT), AbstractVectorTest.FUZZ);
        assertEquals(zSum(), zSum(), AbstractVectorTest.FUZZ);
        Vector v3 = v1.clone();
        // must be the right type ... tricky to tell that in the face of type erasure
        assertTrue(v0.getClass().isAssignableFrom(v3.getClass()));
        assertTrue(v3.getClass().isAssignableFrom(v0.getClass()));
        assertEquals(0, v1.getDistanceSquared(v3), AbstractVectorTest.FUZZ);
        assertNotSame(v1, v3);
        v3.assign(0);
        assertEquals(0, dv1.getDistanceSquared(v1), AbstractVectorTest.FUZZ);
        assertEquals(0, v3.getLengthSquared(), AbstractVectorTest.FUZZ);
        dv1.assign(ABS);
        v1.assign(ABS);
        assertEquals(0, dv1.logNormalize().getDistanceSquared(v1.logNormalize()), AbstractVectorTest.FUZZ);
        assertEquals(0, dv1.logNormalize(1.5).getDistanceSquared(v1.logNormalize(1.5)), AbstractVectorTest.FUZZ);
        // aggregate
        // cross,
        // getNumNondefaultElements
        for (Vector.Element element : v1.all()) {
            assertEquals(dv1.get(element.index()), element.get(), 0);
            assertEquals(dv1.get(element.index()), v1.get(element.index()), 0);
            assertEquals(dv1.get(element.index()), v1.getQuick(element.index()), 0);
        }
    }

    @Test
    public void testCardinality() {
        assertEquals("size", 7, test.size());
    }

    @Test
    public void testIterator() {
        Iterator<Vector.Element> iterator = test.nonZeroes().iterator();
        AbstractVectorTest.checkIterator(iterator, AbstractVectorTest.gold);
        iterator = test.all().iterator();
        AbstractVectorTest.checkIterator(iterator, AbstractVectorTest.gold);
        double[] doubles = new double[]{ 0.0, 5.0, 0, 3.0 };
        RandomAccessSparseVector zeros = new RandomAccessSparseVector(doubles.length);
        for (int i = 0; i < (doubles.length); i++) {
            zeros.setQuick(i, doubles[i]);
        }
        iterator = zeros.iterateNonZero();
        AbstractVectorTest.checkIterator(iterator, doubles);
        iterator = zeros.iterator();
        AbstractVectorTest.checkIterator(iterator, doubles);
        doubles = new double[]{ 0.0, 0.0, 0, 0.0 };
        zeros = new RandomAccessSparseVector(doubles.length);
        for (int i = 0; i < (doubles.length); i++) {
            zeros.setQuick(i, doubles[i]);
        }
        iterator = zeros.iterateNonZero();
        AbstractVectorTest.checkIterator(iterator, doubles);
        iterator = zeros.iterator();
        AbstractVectorTest.checkIterator(iterator, doubles);
    }

    @Test
    public void testIteratorSet() {
        Vector clone = test.clone();
        for (Element e : clone.nonZeroes()) {
            e.set(((e.get()) * 2.0));
        }
        for (Element e : clone.nonZeroes()) {
            assertEquals(((test.get(e.index())) * 2.0), e.get(), MahoutTestCase.EPSILON);
        }
        clone = test.clone();
        for (Element e : clone.all()) {
            e.set(((e.get()) * 2.0));
        }
        for (Element e : clone.all()) {
            assertEquals(((test.get(e.index())) * 2.0), e.get(), MahoutTestCase.EPSILON);
        }
    }

    @Test
    public void testCopy() {
        Vector copy = test.clone();
        for (int i = 0; i < (test.size()); i++) {
            assertEquals((("copy [" + i) + ']'), test.get(i), copy.get(i), MahoutTestCase.EPSILON);
        }
    }

    @Test
    public void testGet() {
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, test.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("get [" + i) + ']'), AbstractVectorTest.values[(i / 2)], test.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test(expected = IndexException.class)
    public void testGetOver() {
        test.get(test.size());
    }

    @Test(expected = IndexException.class)
    public void testGetUnder() {
        test.get((-1));
    }

    @Test
    public void testSet() {
        test.set(3, 4.5);
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, test.get(i), MahoutTestCase.EPSILON);
            } else
                if (i == 3) {
                    assertEquals((("set [" + i) + ']'), 4.5, test.get(i), MahoutTestCase.EPSILON);
                } else {
                    assertEquals((("set [" + i) + ']'), AbstractVectorTest.values[(i / 2)], test.get(i), MahoutTestCase.EPSILON);
                }

        }
    }

    @Test
    public void testSize() {
        assertEquals("size", 3, test.getNumNondefaultElements());
    }

    @Test
    public void testViewPart() {
        Vector part = test.viewPart(1, 2);
        assertEquals("part size", 2, part.getNumNondefaultElements());
        for (int i = 0; i < (part.size()); i++) {
            assertEquals((("part[" + i) + ']'), test.get((i + 1)), part.get(i), MahoutTestCase.EPSILON);
        }
    }

    @Test(expected = IndexException.class)
    public void testViewPartUnder() {
        test.viewPart((-1), AbstractVectorTest.values.length);
    }

    @Test(expected = IndexException.class)
    public void testViewPartOver() {
        test.viewPart(2, 7);
    }

    @Test(expected = IndexException.class)
    public void testViewPartCardinality() {
        test.viewPart(1, 8);
    }

    @Test
    public void testSparseDoubleVectorInt() {
        Vector val = new RandomAccessSparseVector(4);
        assertEquals("size", 4, val.size());
        for (int i = 0; i < 4; i++) {
            assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
        }
    }

    @Test
    public void testDot() {
        double res = test.dot(test);
        double expected = ((3.3 * 3.3) + (2.2 * 2.2)) + (1.1 * 1.1);
        AbstractVectorTest.assertEquals("dot", expected, res, MahoutTestCase.EPSILON);
    }

    @Test
    public void testDot2() {
        Vector test2 = test.clone();
        test2.set(1, 0.0);
        test2.set(3, 0.0);
        assertEquals((3.3 * 3.3), test2.dot(test), MahoutTestCase.EPSILON);
    }

    @Test(expected = CardinalityException.class)
    public void testDotCardinality() {
        test.dot(new DenseVector(((test.size()) + 1)));
    }

    @Test
    public void testNormalize() {
        Vector val = test.normalize();
        double mag = Math.sqrt((((1.1 * 1.1) + (2.2 * 2.2)) + (3.3 * 3.3)));
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals("dot", ((AbstractVectorTest.values[(i / 2)]) / mag), val.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testMinus() {
        Vector val = test.minus(test);
        assertEquals("size", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
        }
        val = test.minus(test).minus(test);
        assertEquals("cardinality", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            assertEquals((("get [" + i) + ']'), 0.0, ((val.get(i)) + (test.get(i))), MahoutTestCase.EPSILON);
        }
        Vector val1 = test.plus(1);
        val = val1.minus(test);
        for (int i = 0; i < (test.size()); i++) {
            assertEquals((("get [" + i) + ']'), 1.0, val.get(i), MahoutTestCase.EPSILON);
        }
        val1 = test.plus((-1));
        val = val1.minus(test);
        for (int i = 0; i < (test.size()); i++) {
            assertEquals((("get [" + i) + ']'), (-1.0), val.get(i), MahoutTestCase.EPSILON);
        }
    }

    @Test
    public void testPlusDouble() {
        Vector val = test.plus(1);
        assertEquals("size", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 1.0, val.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("get [" + i) + ']'), ((AbstractVectorTest.values[(i / 2)]) + 1.0), val.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testPlusVector() {
        Vector val = test.plus(test);
        assertEquals("size", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("get [" + i) + ']'), ((AbstractVectorTest.values[(i / 2)]) * 2.0), val.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test(expected = CardinalityException.class)
    public void testPlusVectorCardinality() {
        test.plus(new DenseVector(((test.size()) + 1)));
    }

    @Test
    public void testTimesDouble() {
        Vector val = test.times(3);
        assertEquals("size", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("get [" + i) + ']'), ((AbstractVectorTest.values[(i / 2)]) * 3.0), val.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testDivideDouble() {
        Vector val = test.divide(3);
        assertEquals("size", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("get [" + i) + ']'), ((AbstractVectorTest.values[(i / 2)]) / 3.0), val.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testTimesVector() {
        Vector val = test.times(test);
        assertEquals("size", test.size(), val.size());
        for (int i = 0; i < (test.size()); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, val.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("get [" + i) + ']'), ((AbstractVectorTest.values[(i / 2)]) * (AbstractVectorTest.values[(i / 2)])), val.get(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test(expected = CardinalityException.class)
    public void testTimesVectorCardinality() {
        test.times(new DenseVector(((test.size()) + 1)));
    }

    @Test
    public void testZSum() {
        double expected = 0;
        for (double value : AbstractVectorTest.values) {
            expected += value;
        }
        assertEquals("wrong zSum", expected, test.zSum(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testGetDistanceSquared() {
        Vector other = new RandomAccessSparseVector(test.size());
        other.set(1, (-2));
        other.set(2, (-5));
        other.set(3, (-9));
        other.set(4, 1);
        double expected = test.minus(other).getLengthSquared();
        assertTrue("a.getDistanceSquared(b) != a.minus(b).getLengthSquared", ((Math.abs((expected - (test.getDistanceSquared(other))))) < 1.0E-6));
    }

    @Test
    public void testAssignDouble() {
        test.assign(0);
        for (int i = 0; i < (AbstractVectorTest.values.length); i++) {
            assertEquals((("value[" + i) + ']'), 0.0, test.getQuick(i), MahoutTestCase.EPSILON);
        }
    }

    @Test
    public void testAssignDoubleArray() {
        double[] array = new double[test.size()];
        test.assign(array);
        for (int i = 0; i < (AbstractVectorTest.values.length); i++) {
            assertEquals((("value[" + i) + ']'), 0.0, test.getQuick(i), MahoutTestCase.EPSILON);
        }
    }

    @Test(expected = CardinalityException.class)
    public void testAssignDoubleArrayCardinality() {
        double[] array = new double[(test.size()) + 1];
        test.assign(array);
    }

    @Test
    public void testAssignVector() {
        Vector other = new DenseVector(test.size());
        test.assign(other);
        for (int i = 0; i < (AbstractVectorTest.values.length); i++) {
            assertEquals((("value[" + i) + ']'), 0.0, test.getQuick(i), MahoutTestCase.EPSILON);
        }
    }

    @Test(expected = CardinalityException.class)
    public void testAssignVectorCardinality() {
        Vector other = new DenseVector(((test.size()) - 1));
        test.assign(other);
    }

    @Test
    public void testAssignUnaryFunction() {
        test.assign(NEGATE);
        for (int i = 1; i < (AbstractVectorTest.values.length); i += 2) {
            assertEquals((("value[" + i) + ']'), (-(AbstractVectorTest.values[i])), test.getQuick((i + 2)), MahoutTestCase.EPSILON);
        }
    }

    @Test
    public void testAssignBinaryFunction() {
        test.assign(test, PLUS);
        for (int i = 0; i < (AbstractVectorTest.values.length); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, test.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("value[" + i) + ']'), (2 * (AbstractVectorTest.values[(i - 1)])), test.getQuick(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testAssignBinaryFunction2() {
        test.assign(Functions.plus(4));
        for (int i = 0; i < (AbstractVectorTest.values.length); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 4.0, test.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("value[" + i) + ']'), ((AbstractVectorTest.values[(i - 1)]) + 4), test.getQuick(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testAssignBinaryFunction3() {
        test.assign(Functions.mult(4));
        for (int i = 0; i < (AbstractVectorTest.values.length); i++) {
            if ((i % 2) == 0) {
                assertEquals((("get [" + i) + ']'), 0.0, test.get(i), MahoutTestCase.EPSILON);
            } else {
                assertEquals((("value[" + i) + ']'), ((AbstractVectorTest.values[(i - 1)]) * 4), test.getQuick(i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testLike() {
        Vector other = test.like();
        assertTrue("not like", test.getClass().isAssignableFrom(other.getClass()));
        assertEquals("size", test.size(), other.size());
    }

    @Test
    public void testCrossProduct() {
        Matrix result = test.cross(test);
        assertEquals("row size", test.size(), result.rowSize());
        assertEquals("col size", test.size(), result.columnSize());
        for (int row = 0; row < (result.rowSize()); row++) {
            for (int col = 0; col < (result.columnSize()); col++) {
                assertEquals((((("cross[" + row) + "][") + col) + ']'), ((test.getQuick(row)) * (test.getQuick(col))), result.getQuick(row, col), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testIterators() {
        final T v0 = vectorToTest(20);
        double sum = 0;
        int elements = 0;
        int nonZero = 0;
        for (Element element : all()) {
            elements++;
            sum += element.get();
            if ((element.get()) != 0) {
                nonZero++;
            }
        }
        int nonZeroIterated = Iterables.size(nonZeroes());
        AbstractVectorTest.assertEquals(20, elements);
        assertEquals(size(), elements);
        AbstractVectorTest.assertEquals(nonZeroIterated, nonZero);
        assertEquals(zSum(), sum, 0);
    }

    @Test
    public void testSmallDistances() {
        for (double fuzz : new double[]{ 1.0E-5, 1.0E-6, 1.0E-7, 1.0E-8, 1.0E-9, 1.0E-10 }) {
            MultiNormal x = new MultiNormal(fuzz, new ConstantVector(0, 20));
            for (int i = 0; i < 10000; i++) {
                final T v1 = vectorToTest(20);
                Vector v2 = v1.plus(x.sample());
                if ((1 + (fuzz * fuzz)) > 1) {
                    String msg = String.format("fuzz = %.1g, >", fuzz);
                    assertTrue(msg, ((v1.getDistanceSquared(v2)) > 0));
                    assertTrue(msg, ((v2.getDistanceSquared(v1)) > 0));
                } else {
                    String msg = String.format("fuzz = %.1g, >=", fuzz);
                    assertTrue(msg, ((v1.getDistanceSquared(v2)) >= 0));
                    assertTrue(msg, ((v2.getDistanceSquared(v1)) >= 0));
                }
            }
        }
    }
}

