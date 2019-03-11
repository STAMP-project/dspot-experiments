/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import LightWeightGSet.LinkedElement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;


public class TestGSet {
    private static final Random ran = new Random();

    private static final long starttime = Time.now();

    @Test
    public void testExceptionCases() {
        testExceptionCases(false);
        testExceptionCases(true);
    }

    @Test
    public void testGSet() {
        // The parameters are: table length, data size, modulus.
        TestGSet.check(new TestGSet.GSetTestCase(1, (1 << 4), 65537));
        TestGSet.check(new TestGSet.GSetTestCase(17, (1 << 16), 17));
        TestGSet.check(new TestGSet.GSetTestCase(255, (1 << 10), 65537));
    }

    @Test
    public void testResizableGSet() {
        // The parameters are: table length, data size, modulus, resizable.
        TestGSet.check(new TestGSet.GSetTestCase(1, (1 << 4), 65537, true));
        TestGSet.check(new TestGSet.GSetTestCase(17, (1 << 16), 17, true));
        TestGSet.check(new TestGSet.GSetTestCase(255, (1 << 10), 65537, true));
    }

    /**
     * Test cases
     */
    private static class GSetTestCase implements GSet<TestGSet.IntElement, TestGSet.IntElement> {
        final GSet<TestGSet.IntElement, TestGSet.IntElement> expected = new GSetByHashMap<TestGSet.IntElement, TestGSet.IntElement>(1024, 0.75F);

        final GSet<TestGSet.IntElement, TestGSet.IntElement> gset;

        final TestGSet.IntData data;

        final String info;

        final long starttime = Time.now();

        /**
         * Determine the probability in {@link #check()}.
         */
        final int denominator;

        int iterate_count = 0;

        int contain_count = 0;

        GSetTestCase(int tablelength, int datasize, int modulus) {
            this(tablelength, datasize, modulus, false);
        }

        GSetTestCase(int tablelength, int datasize, int modulus, boolean resizable) {
            denominator = Math.min(((datasize >> 7) + 1), (1 << 16));
            info = ((((((((getClass().getSimpleName()) + ": tablelength=") + tablelength) + ", datasize=") + datasize) + ", modulus=") + modulus) + ", denominator=") + (denominator);
            TestGSet.println(info);
            data = new TestGSet.IntData(datasize, modulus);
            gset = (resizable) ? new LightWeightResizableGSet<TestGSet.IntElement, TestGSet.IntElement>() : new LightWeightGSet<TestGSet.IntElement, TestGSet.IntElement>(tablelength);
            Assert.assertEquals(0, gset.size());
        }

        private boolean containsTest(TestGSet.IntElement key) {
            final boolean e = expected.contains(key);
            Assert.assertEquals(e, gset.contains(key));
            return e;
        }

        @Override
        public boolean contains(TestGSet.IntElement key) {
            final boolean e = containsTest(key);
            check();
            return e;
        }

        private TestGSet.IntElement getTest(TestGSet.IntElement key) {
            final TestGSet.IntElement e = expected.get(key);
            Assert.assertEquals(e.id, gset.get(key).id);
            return e;
        }

        @Override
        public TestGSet.IntElement get(TestGSet.IntElement key) {
            final TestGSet.IntElement e = getTest(key);
            check();
            return e;
        }

        private TestGSet.IntElement putTest(TestGSet.IntElement element) {
            final TestGSet.IntElement e = expected.put(element);
            if (e == null) {
                Assert.assertEquals(null, gset.put(element));
            } else {
                Assert.assertEquals(e.id, gset.put(element).id);
            }
            return e;
        }

        @Override
        public TestGSet.IntElement put(TestGSet.IntElement element) {
            final TestGSet.IntElement e = putTest(element);
            check();
            return e;
        }

        private TestGSet.IntElement removeTest(TestGSet.IntElement key) {
            final TestGSet.IntElement e = expected.remove(key);
            if (e == null) {
                Assert.assertEquals(null, gset.remove(key));
            } else {
                Assert.assertEquals(e.id, gset.remove(key).id);
            }
            return e;
        }

        @Override
        public TestGSet.IntElement remove(TestGSet.IntElement key) {
            final TestGSet.IntElement e = removeTest(key);
            check();
            return e;
        }

        private int sizeTest() {
            final int s = expected.size();
            Assert.assertEquals(s, gset.size());
            return s;
        }

        @Override
        public int size() {
            final int s = sizeTest();
            check();
            return s;
        }

        @Override
        public Iterator<TestGSet.IntElement> iterator() {
            throw new UnsupportedOperationException();
        }

        void check() {
            // test size
            sizeTest();
            if ((TestGSet.ran.nextInt(denominator)) == 0) {
                // test get(..), check content and test iterator
                (iterate_count)++;
                for (TestGSet.IntElement i : gset) {
                    getTest(i);
                }
            }
            if ((TestGSet.ran.nextInt(denominator)) == 0) {
                // test contains(..)
                (contain_count)++;
                final int count = Math.min(data.size(), 1000);
                if (count == (data.size())) {
                    for (TestGSet.IntElement i : data.integers) {
                        containsTest(i);
                    }
                } else {
                    for (int j = 0; j < count; j++) {
                        containsTest(data.get(TestGSet.ran.nextInt(data.size())));
                    }
                }
            }
        }

        String stat() {
            final long t = (Time.now()) - (starttime);
            return String.format(" iterate=%5d, contain=%5d, time elapsed=%5d.%03ds", iterate_count, contain_count, (t / 1000), (t % 1000));
        }

        @Override
        public void clear() {
            expected.clear();
            gset.clear();
            Assert.assertEquals(0, size());
        }

        @Override
        public Collection<TestGSet.IntElement> values() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Test data set
     */
    private static class IntData {
        final TestGSet.IntElement[] integers;

        IntData(int size, int modulus) {
            integers = new TestGSet.IntElement[size];
            for (int i = 0; i < (integers.length); i++) {
                integers[i] = new TestGSet.IntElement(i, TestGSet.ran.nextInt(modulus));
            }
        }

        TestGSet.IntElement get(int i) {
            return integers[i];
        }

        int size() {
            return integers.length;
        }
    }

    /**
     * Elements of {@link LightWeightGSet} in this test
     */
    private static class IntElement implements Comparable<TestGSet.IntElement> , LightWeightGSet.LinkedElement {
        private LinkedElement next;

        final int id;

        final int value;

        IntElement(int id, int value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return ((obj != null) && (obj instanceof TestGSet.IntElement)) && ((value) == (((TestGSet.IntElement) (obj)).value));
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public int compareTo(TestGSet.IntElement that) {
            return (value) - (that.value);
        }

        @Override
        public String toString() {
            return ((id) + "#") + (value);
        }

        @Override
        public LinkedElement getNext() {
            return next;
        }

        @Override
        public void setNext(LightWeightGSet.LinkedElement e) {
            next = e;
        }
    }

    /**
     * Test for {@link LightWeightGSet#computeCapacity(double, String)}
     * with invalid percent less than 0.
     */
    @Test(expected = HadoopIllegalArgumentException.class)
    public void testComputeCapacityNegativePercent() {
        LightWeightGSet.computeCapacity(1024, (-1.0), "testMap");
    }

    /**
     * Test for {@link LightWeightGSet#computeCapacity(double, String)}
     * with invalid percent greater than 100.
     */
    @Test(expected = HadoopIllegalArgumentException.class)
    public void testComputeCapacityInvalidPercent() {
        LightWeightGSet.computeCapacity(1024, 101.0, "testMap");
    }

    /**
     * Test for {@link LightWeightGSet#computeCapacity(double, String)}
     * with invalid negative max memory
     */
    @Test(expected = HadoopIllegalArgumentException.class)
    public void testComputeCapacityInvalidMemory() {
        LightWeightGSet.computeCapacity((-1), 50.0, "testMap");
    }

    /**
     * Test for {@link LightWeightGSet#computeCapacity(double, String)}
     */
    @Test
    public void testComputeCapacity() {
        // Tests for boundary conditions where percent or memory are zero
        TestGSet.testCapacity(0, 0.0);
        TestGSet.testCapacity(100, 0.0);
        TestGSet.testCapacity(0, 100.0);
        // Compute capacity for some 100 random max memory and percentage
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            long maxMemory = r.nextInt(Integer.MAX_VALUE);
            double percent = r.nextInt(101);
            TestGSet.testCapacity(maxMemory, percent);
        }
    }
}

