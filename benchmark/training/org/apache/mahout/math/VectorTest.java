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


import Functions.IDENTITY;
import Functions.MIN;
import Functions.PLUS;
import com.google.common.collect.Sets;
import java.util.Collection;
import org.apache.mahout.math.Vector.Element;
import org.junit.Test;


public final class VectorTest extends MahoutTestCase {
    @Test
    public void testSparseVector() {
        Vector vec1 = new RandomAccessSparseVector(3);
        Vector vec2 = new RandomAccessSparseVector(3);
        VectorTest.doTestVectors(vec1, vec2);
    }

    @Test
    public void testSparseVectorFullIteration() {
        int[] index = new int[]{ 0, 1, 2, 3, 4, 5 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6 };
        assertEquals(index.length, values.length);
        int n = index.length;
        Vector vector = new SequentialAccessSparseVector(n);
        for (int i = 0; i < n; i++) {
            vector.set(index[i], values[i]);
        }
        for (int i = 0; i < n; i++) {
            assertEquals(vector.get(i), values[i], MahoutTestCase.EPSILON);
        }
        int elements = 0;
        for (Element ignore : vector.all()) {
            elements++;
        }
        assertEquals(n, elements);
        assertFalse(new SequentialAccessSparseVector(0).iterator().hasNext());
    }

    @Test
    public void testSparseVectorSparseIteration() {
        int[] index = new int[]{ 0, 1, 2, 3, 4, 5 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6 };
        assertEquals(index.length, values.length);
        int n = index.length;
        Vector vector = new SequentialAccessSparseVector(n);
        for (int i = 0; i < n; i++) {
            vector.set(index[i], values[i]);
        }
        for (int i = 0; i < n; i++) {
            assertEquals(vector.get(i), values[i], MahoutTestCase.EPSILON);
        }
        int elements = 0;
        for (Element ignored : vector.nonZeroes()) {
            elements++;
        }
        assertEquals(n, elements);
        Vector empty = new SequentialAccessSparseVector(0);
        assertFalse(empty.nonZeroes().iterator().hasNext());
    }

    @Test
    public void testEquivalent() {
        // names are not used for equivalent
        RandomAccessSparseVector randomAccessLeft = new RandomAccessSparseVector(3);
        Vector sequentialAccessLeft = new SequentialAccessSparseVector(3);
        Vector right = new DenseVector(3);
        randomAccessLeft.setQuick(0, 1);
        randomAccessLeft.setQuick(1, 2);
        randomAccessLeft.setQuick(2, 3);
        sequentialAccessLeft.setQuick(0, 1);
        sequentialAccessLeft.setQuick(1, 2);
        sequentialAccessLeft.setQuick(2, 3);
        right.setQuick(0, 1);
        right.setQuick(1, 2);
        right.setQuick(2, 3);
        assertEquals(randomAccessLeft, right);
        assertEquals(sequentialAccessLeft, right);
        assertEquals(sequentialAccessLeft, randomAccessLeft);
        Vector leftBar = new DenseVector(3);
        leftBar.setQuick(0, 1);
        leftBar.setQuick(1, 2);
        leftBar.setQuick(2, 3);
        assertEquals(leftBar, right);
        assertEquals(randomAccessLeft, right);
        assertEquals(sequentialAccessLeft, right);
        Vector rightBar = new RandomAccessSparseVector(3);
        rightBar.setQuick(0, 1);
        rightBar.setQuick(1, 2);
        rightBar.setQuick(2, 3);
        assertEquals(randomAccessLeft, rightBar);
        right.setQuick(2, 4);
        assertFalse(randomAccessLeft.equals(right));
        right = new DenseVector(4);
        right.setQuick(0, 1);
        right.setQuick(1, 2);
        right.setQuick(2, 3);
        right.setQuick(3, 3);
        assertFalse(randomAccessLeft.equals(right));
        randomAccessLeft = new RandomAccessSparseVector(2);
        randomAccessLeft.setQuick(0, 1);
        randomAccessLeft.setQuick(1, 2);
        assertFalse(randomAccessLeft.equals(right));
        Vector dense = new DenseVector(3);
        right = new DenseVector(3);
        right.setQuick(0, 1);
        right.setQuick(1, 2);
        right.setQuick(2, 3);
        dense.setQuick(0, 1);
        dense.setQuick(1, 2);
        dense.setQuick(2, 3);
        assertEquals(dense, right);
        RandomAccessSparseVector sparse = new RandomAccessSparseVector(3);
        randomAccessLeft = new RandomAccessSparseVector(3);
        sparse.setQuick(0, 1);
        sparse.setQuick(1, 2);
        sparse.setQuick(2, 3);
        randomAccessLeft.setQuick(0, 1);
        randomAccessLeft.setQuick(1, 2);
        randomAccessLeft.setQuick(2, 3);
        assertEquals(randomAccessLeft, sparse);
        Vector v1 = new VectorView(randomAccessLeft, 0, 2);
        Vector v2 = new VectorView(right, 0, 2);
        assertEquals(v1, v2);
        sparse = new RandomAccessSparseVector(2);
        sparse.setQuick(0, 1);
        sparse.setQuick(1, 2);
        assertEquals(v1, sparse);
    }

    @Test
    public void testGetDistanceSquared() {
        Vector v = new DenseVector(5);
        Vector w = new DenseVector(5);
        VectorTest.setUpV(v);
        VectorTest.setUpW(w);
        VectorTest.doTestGetDistanceSquared(v, w);
        v = new RandomAccessSparseVector(5);
        w = new RandomAccessSparseVector(5);
        VectorTest.setUpV(v);
        VectorTest.setUpW(w);
        VectorTest.doTestGetDistanceSquared(v, w);
        v = new SequentialAccessSparseVector(5);
        w = new SequentialAccessSparseVector(5);
        VectorTest.setUpV(v);
        VectorTest.setUpW(w);
        VectorTest.doTestGetDistanceSquared(v, w);
    }

    @Test
    public void testAddTo() throws Exception {
        Vector v = new DenseVector(4);
        Vector w = new DenseVector(4);
        v.setQuick(0, 1);
        v.setQuick(1, 2);
        v.setQuick(2, 0);
        v.setQuick(3, 4);
        w.setQuick(0, 1);
        w.setQuick(1, 1);
        w.setQuick(2, 1);
        w.setQuick(3, 1);
        w.assign(v, PLUS);
        Vector gold = new DenseVector(new double[]{ 2, 3, 1, 5 });
        assertEquals(w, gold);
        assertFalse(v.equals(gold));
    }

    @Test
    public void testGetLengthSquared() {
        Vector v = new DenseVector(5);
        VectorTest.setUpV(v);
        VectorTest.doTestGetLengthSquared(v);
        v = new RandomAccessSparseVector(5);
        VectorTest.setUpV(v);
        VectorTest.doTestGetLengthSquared(v);
        v = new SequentialAccessSparseVector(5);
        VectorTest.setUpV(v);
        VectorTest.doTestGetLengthSquared(v);
    }

    @Test
    public void testIterator() {
        Collection<Integer> expectedIndices = Sets.newHashSet();
        int i = 1;
        while (i <= 20) {
            expectedIndices.add(((i * (i + 1)) / 2));
            i++;
        } 
        Vector denseVector = new DenseVector((i * i));
        for (int index : expectedIndices) {
            denseVector.set(index, (((double) (2)) * index));
        }
        VectorTest.doTestIterators(denseVector, expectedIndices);
        Vector randomAccessVector = new RandomAccessSparseVector((i * i));
        for (int index : expectedIndices) {
            randomAccessVector.set(index, (((double) (2)) * index));
        }
        VectorTest.doTestIterators(randomAccessVector, expectedIndices);
        Vector sequentialVector = new SequentialAccessSparseVector((i * i));
        for (int index : expectedIndices) {
            sequentialVector.set(index, (((double) (2)) * index));
        }
        VectorTest.doTestIterators(sequentialVector, expectedIndices);
    }

    @Test
    public void testNormalize() {
        Vector vec1 = new RandomAccessSparseVector(3);
        vec1.setQuick(0, 1);
        vec1.setQuick(1, 2);
        vec1.setQuick(2, 3);
        Vector norm = vec1.normalize();
        assertNotNull("norm1 is null and it shouldn't be", norm);
        Vector vec2 = new SequentialAccessSparseVector(3);
        vec2.setQuick(0, 1);
        vec2.setQuick(1, 2);
        vec2.setQuick(2, 3);
        Vector norm2 = vec2.normalize();
        assertNotNull("norm1 is null and it shouldn't be", norm2);
        Vector expected = new RandomAccessSparseVector(3);
        expected.setQuick(0, 0.2672612419124244);
        expected.setQuick(1, 0.5345224838248488);
        expected.setQuick(2, 0.8017837257372732);
        assertEquals(expected, norm);
        norm = vec1.normalize(2);
        assertEquals(expected, norm);
        norm2 = vec2.normalize(2);
        assertEquals(expected, norm2);
        norm = vec1.normalize(1);
        norm2 = vec2.normalize(1);
        expected.setQuick(0, (1.0 / 6));
        expected.setQuick(1, (2.0 / 6));
        expected.setQuick(2, (3.0 / 6));
        assertEquals(expected, norm);
        assertEquals(expected, norm2);
        norm = vec1.normalize(3);
        // expected = vec1.times(vec1).times(vec1);
        // double sum = expected.zSum();
        // cube = Math.pow(sum, 1.0/3);
        double cube = Math.pow(36, (1.0 / 3));
        expected = vec1.divide(cube);
        assertEquals(norm, expected);
        norm = vec1.normalize(Double.POSITIVE_INFINITY);
        norm2 = vec2.normalize(Double.POSITIVE_INFINITY);
        // The max is 3, so we divide by that.
        expected.setQuick(0, (1.0 / 3));
        expected.setQuick(1, (2.0 / 3));
        expected.setQuick(2, (3.0 / 3));
        assertEquals(norm, expected);
        assertEquals(norm2, expected);
        norm = vec1.normalize(0);
        // The max is 3, so we divide by that.
        expected.setQuick(0, (1.0 / 3));
        expected.setQuick(1, (2.0 / 3));
        expected.setQuick(2, (3.0 / 3));
        assertEquals(norm, expected);
        try {
            vec1.normalize((-1));
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            vec2.normalize((-1));
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testLogNormalize() {
        Vector vec1 = new RandomAccessSparseVector(3);
        vec1.setQuick(0, 1);
        vec1.setQuick(1, 2);
        vec1.setQuick(2, 3);
        Vector norm = vec1.logNormalize();
        assertNotNull("norm1 is null and it shouldn't be", norm);
        Vector vec2 = new SequentialAccessSparseVector(3);
        vec2.setQuick(0, 1);
        vec2.setQuick(1, 2);
        vec2.setQuick(2, 3);
        Vector norm2 = vec2.logNormalize();
        assertNotNull("norm1 is null and it shouldn't be", norm2);
        Vector expected = new DenseVector(new double[]{ 0.2672612419124244, 0.4235990463273581, 0.5345224838248488 });
        VectorTest.assertVectorEquals(expected, norm, 1.0E-15);
        VectorTest.assertVectorEquals(expected, norm2, 1.0E-15);
        norm = vec1.logNormalize(2);
        VectorTest.assertVectorEquals(expected, norm, 1.0E-15);
        norm2 = vec2.logNormalize(2);
        VectorTest.assertVectorEquals(expected, norm2, 1.0E-15);
        try {
            vec1.logNormalize(1);
            fail("Should fail with power == 1");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            vec1.logNormalize((-1));
            fail("Should fail with negative power");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            vec2.logNormalize(Double.POSITIVE_INFINITY);
            fail("Should fail with positive infinity norm");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testMax() {
        Vector vec1 = new RandomAccessSparseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(1, (-3));
        vec1.setQuick(2, (-2));
        double max = vec1.maxValue();
        assertEquals((-1.0), max, 0.0);
        int idx = vec1.maxValueIndex();
        assertEquals(0, idx);
        vec1 = new RandomAccessSparseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(2, (-2));
        max = vec1.maxValue();
        assertEquals(0.0, max, 0.0);
        idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new SequentialAccessSparseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(2, (-2));
        max = vec1.maxValue();
        assertEquals(0.0, max, 0.0);
        idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new DenseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(2, (-2));
        max = vec1.maxValue();
        assertEquals(0.0, max, 0.0);
        idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new RandomAccessSparseVector(3);
        max = vec1.maxValue();
        assertEquals(0.0, max, MahoutTestCase.EPSILON);
        vec1 = new DenseVector(3);
        max = vec1.maxValue();
        assertEquals(0.0, max, MahoutTestCase.EPSILON);
        vec1 = new SequentialAccessSparseVector(3);
        max = vec1.maxValue();
        assertEquals(0.0, max, MahoutTestCase.EPSILON);
        vec1 = new RandomAccessSparseVector(0);
        max = vec1.maxValue();
        assertEquals(Double.NEGATIVE_INFINITY, max, MahoutTestCase.EPSILON);
        vec1 = new DenseVector(0);
        max = vec1.maxValue();
        assertEquals(Double.NEGATIVE_INFINITY, max, MahoutTestCase.EPSILON);
        vec1 = new SequentialAccessSparseVector(0);
        max = vec1.maxValue();
        assertEquals(Double.NEGATIVE_INFINITY, max, MahoutTestCase.EPSILON);
    }

    @Test
    public void testMin() {
        Vector vec1 = new RandomAccessSparseVector(3);
        vec1.setQuick(0, 1);
        vec1.setQuick(1, 3);
        vec1.setQuick(2, 2);
        double max = vec1.minValue();
        assertEquals(1.0, max, 0.0);
        int idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new RandomAccessSparseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(2, (-2));
        max = vec1.maxValue();
        assertEquals(0.0, max, 0.0);
        idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new SequentialAccessSparseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(2, (-2));
        max = vec1.maxValue();
        assertEquals(0.0, max, 0.0);
        idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new DenseVector(3);
        vec1.setQuick(0, (-1));
        vec1.setQuick(2, (-2));
        max = vec1.maxValue();
        assertEquals(0.0, max, 0.0);
        idx = vec1.maxValueIndex();
        assertEquals(1, idx);
        vec1 = new RandomAccessSparseVector(3);
        max = vec1.maxValue();
        assertEquals(0.0, max, MahoutTestCase.EPSILON);
        vec1 = new DenseVector(3);
        max = vec1.maxValue();
        assertEquals(0.0, max, MahoutTestCase.EPSILON);
        vec1 = new SequentialAccessSparseVector(3);
        max = vec1.maxValue();
        assertEquals(0.0, max, MahoutTestCase.EPSILON);
        vec1 = new RandomAccessSparseVector(0);
        max = vec1.maxValue();
        assertEquals(Double.NEGATIVE_INFINITY, max, MahoutTestCase.EPSILON);
        vec1 = new DenseVector(0);
        max = vec1.maxValue();
        assertEquals(Double.NEGATIVE_INFINITY, max, MahoutTestCase.EPSILON);
        vec1 = new SequentialAccessSparseVector(0);
        max = vec1.maxValue();
        assertEquals(Double.NEGATIVE_INFINITY, max, MahoutTestCase.EPSILON);
    }

    @Test
    public void testDenseVector() {
        Vector vec1 = new DenseVector(3);
        Vector vec2 = new DenseVector(3);
        VectorTest.doTestVectors(vec1, vec2);
    }

    @Test
    public void testVectorView() {
        RandomAccessSparseVector vec1 = new RandomAccessSparseVector(3);
        RandomAccessSparseVector vec2 = new RandomAccessSparseVector(6);
        SequentialAccessSparseVector vec3 = new SequentialAccessSparseVector(3);
        SequentialAccessSparseVector vec4 = new SequentialAccessSparseVector(6);
        Vector vecV1 = new VectorView(vec1, 0, 3);
        Vector vecV2 = new VectorView(vec2, 2, 3);
        Vector vecV3 = new VectorView(vec3, 0, 3);
        Vector vecV4 = new VectorView(vec4, 2, 3);
        VectorTest.doTestVectors(vecV1, vecV2);
        VectorTest.doTestVectors(vecV3, vecV4);
    }

    @Test
    public void testEnumeration() {
        double[] apriori = new double[]{ 0, 1, 2, 3, 4 };
        VectorTest.doTestEnumeration(apriori, new VectorView(new DenseVector(new double[]{ -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }), 2, 5));
        VectorTest.doTestEnumeration(apriori, new DenseVector(new double[]{ 0, 1, 2, 3, 4 }));
        Vector sparse = new RandomAccessSparseVector(5);
        sparse.set(0, 0);
        sparse.set(1, 1);
        sparse.set(2, 2);
        sparse.set(3, 3);
        sparse.set(4, 4);
        VectorTest.doTestEnumeration(apriori, sparse);
        sparse = new SequentialAccessSparseVector(5);
        sparse.set(0, 0);
        sparse.set(1, 1);
        sparse.set(2, 2);
        sparse.set(3, 3);
        sparse.set(4, 4);
        VectorTest.doTestEnumeration(apriori, sparse);
    }

    @Test
    public void testAggregation() {
        Vector v = new DenseVector(5);
        Vector w = new DenseVector(5);
        VectorTest.setUpFirstVector(v);
        VectorTest.setUpSecondVector(w);
        VectorTest.doTestAggregation(v, w);
        v = new RandomAccessSparseVector(5);
        w = new RandomAccessSparseVector(5);
        VectorTest.setUpFirstVector(v);
        VectorTest.doTestAggregation(v, w);
        VectorTest.setUpSecondVector(w);
        VectorTest.doTestAggregation(w, v);
        v = new SequentialAccessSparseVector(5);
        w = new SequentialAccessSparseVector(5);
        VectorTest.setUpFirstVector(v);
        VectorTest.doTestAggregation(v, w);
        VectorTest.setUpSecondVector(w);
        VectorTest.doTestAggregation(w, v);
    }

    @Test
    public void testEmptyAggregate1() {
        assertEquals(1.0, new DenseVector(new double[]{ 1 }).aggregate(MIN, IDENTITY), MahoutTestCase.EPSILON);
        assertEquals(1.0, new DenseVector(new double[]{ 2, 1 }).aggregate(MIN, IDENTITY), MahoutTestCase.EPSILON);
        assertEquals(0, new DenseVector(new double[0]).aggregate(MIN, IDENTITY), 0);
    }

    @Test
    public void testEmptyAggregate2() {
        assertEquals(3.0, new DenseVector(new double[]{ 1 }).aggregate(new DenseVector(new double[]{ 2 }), MIN, PLUS), MahoutTestCase.EPSILON);
        assertEquals(0, new DenseVector(new double[0]).aggregate(new DenseVector(new double[0]), MIN, PLUS), 0);
    }

    @Test
    public void testHashCodeEquivalence() {
        // Hash codes must be equal if the vectors are considered equal
        Vector sparseLeft = new RandomAccessSparseVector(3);
        Vector denseRight = new DenseVector(3);
        sparseLeft.setQuick(0, 1);
        sparseLeft.setQuick(1, 2);
        sparseLeft.setQuick(2, 3);
        denseRight.setQuick(0, 1);
        denseRight.setQuick(1, 2);
        denseRight.setQuick(2, 3);
        assertEquals(sparseLeft, denseRight);
        assertEquals(sparseLeft.hashCode(), denseRight.hashCode());
        sparseLeft = new SequentialAccessSparseVector(3);
        sparseLeft.setQuick(0, 1);
        sparseLeft.setQuick(1, 2);
        sparseLeft.setQuick(2, 3);
        assertEquals(sparseLeft, denseRight);
        assertEquals(sparseLeft.hashCode(), denseRight.hashCode());
        Vector denseLeft = new DenseVector(3);
        denseLeft.setQuick(0, 1);
        denseLeft.setQuick(1, 2);
        denseLeft.setQuick(2, 3);
        assertEquals(denseLeft, denseRight);
        assertEquals(denseLeft.hashCode(), denseRight.hashCode());
        Vector sparseRight = new SequentialAccessSparseVector(3);
        sparseRight.setQuick(0, 1);
        sparseRight.setQuick(1, 2);
        sparseRight.setQuick(2, 3);
        assertEquals(sparseLeft, sparseRight);
        assertEquals(sparseLeft.hashCode(), sparseRight.hashCode());
        DenseVector emptyLeft = new DenseVector(0);
        Vector emptyRight = new SequentialAccessSparseVector(0);
        assertEquals(emptyLeft, emptyRight);
        assertEquals(emptyLeft.hashCode(), emptyRight.hashCode());
        emptyRight = new RandomAccessSparseVector(0);
        assertEquals(emptyLeft, emptyRight);
        assertEquals(emptyLeft.hashCode(), emptyRight.hashCode());
    }

    @Test
    public void testHashCode() {
        // Make sure that hash([1,0,2]) != hash([1,2,0])
        Vector left = new SequentialAccessSparseVector(3);
        Vector right = new SequentialAccessSparseVector(3);
        left.setQuick(0, 1);
        left.setQuick(2, 2);
        right.setQuick(0, 1);
        right.setQuick(1, 2);
        assertFalse(left.equals(right));
        assertFalse(((left.hashCode()) == (right.hashCode())));
        left = new RandomAccessSparseVector(3);
        right = new RandomAccessSparseVector(3);
        left.setQuick(0, 1);
        left.setQuick(2, 2);
        right.setQuick(0, 1);
        right.setQuick(1, 2);
        assertFalse(left.equals(right));
        assertFalse(((left.hashCode()) == (right.hashCode())));
        // Make sure that hash([1,0,2,0,0,0]) != hash([1,0,2])
        right = new SequentialAccessSparseVector(5);
        right.setQuick(0, 1);
        right.setQuick(2, 2);
        assertFalse(left.equals(right));
        assertFalse(((left.hashCode()) == (right.hashCode())));
        right = new RandomAccessSparseVector(5);
        right.setQuick(0, 1);
        right.setQuick(2, 2);
        assertFalse(left.equals(right));
        assertFalse(((left.hashCode()) == (right.hashCode())));
    }

    @Test
    public void testIteratorRasv() {
        VectorTest.testIterator(new RandomAccessSparseVector(99));
        VectorTest.testEmptyAllIterator(new RandomAccessSparseVector(0));
        VectorTest.testExample1NonZeroIterator(new RandomAccessSparseVector(13));
    }

    @Test
    public void testIteratorSasv() {
        VectorTest.testIterator(new SequentialAccessSparseVector(99));
        VectorTest.testEmptyAllIterator(new SequentialAccessSparseVector(0));
        VectorTest.testExample1NonZeroIterator(new SequentialAccessSparseVector(13));
    }

    @Test
    public void testIteratorDense() {
        VectorTest.testIterator(new DenseVector(99));
        VectorTest.testEmptyAllIterator(new DenseVector(0));
        VectorTest.testExample1NonZeroIterator(new DenseVector(13));
    }

    @Test
    public void testNumNonZerosDense() {
        DenseVector vector = new DenseVector(10);
        vector.assign(1);
        vector.setQuick(3, 0);
        vector.set(5, 0);
        assertEquals(8, vector.getNumNonZeroElements());
    }

    @Test
    public void testNumNonZerosRandomAccessSparse() {
        RandomAccessSparseVector vector = new RandomAccessSparseVector(10);
        vector.setQuick(3, 1);
        vector.set(5, 1);
        vector.setQuick(7, 0);
        vector.set(9, 0);
        assertEquals(2, vector.getNumNonZeroElements());
    }

    @Test
    public void testNumNonZerosSequentialAccessSparse() {
        SequentialAccessSparseVector vector = new SequentialAccessSparseVector(10);
        vector.setQuick(3, 1);
        vector.set(5, 1);
        vector.setQuick(7, 0);
        vector.set(9, 0);
        assertEquals(2, vector.getNumNonZeroElements());
    }
}

