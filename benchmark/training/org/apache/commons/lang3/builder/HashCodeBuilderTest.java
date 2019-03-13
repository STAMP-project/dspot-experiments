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
package org.apache.commons.lang3.builder;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.builder.HashCodeBuilder}.
 */
public class HashCodeBuilderTest {
    /**
     * A reflection test fixture.
     */
    static class ReflectionTestCycleA {
        HashCodeBuilderTest.ReflectionTestCycleB b;

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    /**
     * A reflection test fixture.
     */
    static class ReflectionTestCycleB {
        HashCodeBuilderTest.ReflectionTestCycleA a;

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructorExZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HashCodeBuilder(0, 0));
    }

    @Test
    public void testConstructorExEvenFirst() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HashCodeBuilder(2, 3));
    }

    @Test
    public void testConstructorExEvenSecond() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HashCodeBuilder(3, 2));
    }

    @Test
    public void testConstructorExEvenNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HashCodeBuilder((-2), (-2)));
    }

    static class TestObject {
        private int a;

        TestObject(final int a) {
            this.a = a;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == (this)) {
                return true;
            }
            if (!(o instanceof HashCodeBuilderTest.TestObject)) {
                return false;
            }
            final HashCodeBuilderTest.TestObject rhs = ((HashCodeBuilderTest.TestObject) (o));
            return (a) == (rhs.a);
        }

        @Override
        public int hashCode() {
            return a;
        }

        public void setA(final int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }
    }

    static class TestSubObject extends HashCodeBuilderTest.TestObject {
        private int b;

        @SuppressWarnings("unused")
        private transient int t;

        TestSubObject() {
            super(0);
        }

        TestSubObject(final int a, final int b, final int t) {
            super(a);
            this.b = b;
            this.t = t;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == (this)) {
                return true;
            }
            if (!(o instanceof HashCodeBuilderTest.TestSubObject)) {
                return false;
            }
            final HashCodeBuilderTest.TestSubObject rhs = ((HashCodeBuilderTest.TestSubObject) (o));
            return (super.equals(o)) && ((b) == (rhs.b));
        }

        @Override
        public int hashCode() {
            return ((b) * 17) + (super.hashCode());
        }
    }

    @Test
    public void testReflectionHashCode() {
        Assertions.assertEquals((17 * 37), HashCodeBuilder.reflectionHashCode(new HashCodeBuilderTest.TestObject(0)));
        Assertions.assertEquals(((17 * 37) + 123456), HashCodeBuilder.reflectionHashCode(new HashCodeBuilderTest.TestObject(123456)));
    }

    @Test
    public void testReflectionHierarchyHashCode() {
        Assertions.assertEquals(((17 * 37) * 37), HashCodeBuilder.reflectionHashCode(new HashCodeBuilderTest.TestSubObject(0, 0, 0)));
        Assertions.assertEquals((((17 * 37) * 37) * 37), HashCodeBuilder.reflectionHashCode(new HashCodeBuilderTest.TestSubObject(0, 0, 0), true));
        Assertions.assertEquals(((((17 * 37) + 7890) * 37) + 123456), HashCodeBuilder.reflectionHashCode(new HashCodeBuilderTest.TestSubObject(123456, 7890, 0)));
        Assertions.assertEquals(((((((17 * 37) + 7890) * 37) + 0) * 37) + 123456), HashCodeBuilder.reflectionHashCode(new HashCodeBuilderTest.TestSubObject(123456, 7890, 0), true));
    }

    @Test
    public void testReflectionHierarchyHashCodeEx1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashCodeBuilder.reflectionHashCode(0, 0, new HashCodeBuilderTest.TestSubObject(0, 0, 0), true));
    }

    @Test
    public void testReflectionHierarchyHashCodeEx2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashCodeBuilder.reflectionHashCode(2, 2, new HashCodeBuilderTest.TestSubObject(0, 0, 0), true));
    }

    @Test
    public void testReflectionHashCodeEx1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashCodeBuilder.reflectionHashCode(0, 0, new HashCodeBuilderTest.TestObject(0), true));
    }

    @Test
    public void testReflectionHashCodeEx2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashCodeBuilder.reflectionHashCode(2, 2, new HashCodeBuilderTest.TestObject(0), true));
    }

    @Test
    public void testReflectionHashCodeEx3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashCodeBuilder.reflectionHashCode(13, 19, null, true));
    }

    @Test
    public void testSuper() {
        final Object obj = new Object();
        Assertions.assertEquals((((17 * 37) + (19 * 41)) + (obj.hashCode())), new HashCodeBuilder(17, 37).appendSuper(new HashCodeBuilder(19, 41).append(obj).toHashCode()).toHashCode());
    }

    @Test
    public void testObject() {
        Object obj = null;
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj = new Object();
        Assertions.assertEquals(((17 * 37) + (obj.hashCode())), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testObjectBuild() {
        Object obj = null;
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(obj).build().intValue());
        obj = new Object();
        Assertions.assertEquals(((17 * 37) + (obj.hashCode())), new HashCodeBuilder(17, 37).append(obj).build().intValue());
    }

    // cast is not really needed, keep for consistency
    @Test
    @SuppressWarnings("cast")
    public void testLong() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(0L).toHashCode());
        Assertions.assertEquals(((17 * 37) + ((int) (123456789L ^ (123456789L >> 32)))), new HashCodeBuilder(17, 37).append(123456789L).toHashCode());
    }

    // cast is not really needed, keep for consistency
    @Test
    @SuppressWarnings("cast")
    public void testInt() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(0).toHashCode());
        Assertions.assertEquals(((17 * 37) + 123456), new HashCodeBuilder(17, 37).append(123456).toHashCode());
    }

    @Test
    public void testShort() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((short) (0))).toHashCode());
        Assertions.assertEquals(((17 * 37) + 12345), new HashCodeBuilder(17, 37).append(((short) (12345))).toHashCode());
    }

    @Test
    public void testChar() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((char) (0))).toHashCode());
        Assertions.assertEquals(((17 * 37) + 1234), new HashCodeBuilder(17, 37).append(((char) (1234))).toHashCode());
    }

    @Test
    public void testByte() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((byte) (0))).toHashCode());
        Assertions.assertEquals(((17 * 37) + 123), new HashCodeBuilder(17, 37).append(((byte) (123))).toHashCode());
    }

    // cast is not really needed, keep for consistency
    @Test
    @SuppressWarnings("cast")
    public void testDouble() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(0.0).toHashCode());
        final double d = 1234567.89;
        final long l = Double.doubleToLongBits(d);
        Assertions.assertEquals(((17 * 37) + ((int) (l ^ (l >> 32)))), new HashCodeBuilder(17, 37).append(d).toHashCode());
    }

    // cast is not really needed, keep for consistency
    @Test
    @SuppressWarnings("cast")
    public void testFloat() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(0.0F).toHashCode());
        final float f = 1234.89F;
        final int i = Float.floatToIntBits(f);
        Assertions.assertEquals(((17 * 37) + i), new HashCodeBuilder(17, 37).append(f).toHashCode());
    }

    @Test
    public void testBoolean() {
        Assertions.assertEquals(((17 * 37) + 0), new HashCodeBuilder(17, 37).append(true).toHashCode());
        Assertions.assertEquals(((17 * 37) + 1), new HashCodeBuilder(17, 37).append(false).toHashCode());
    }

    @Test
    public void testObjectArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((Object[]) (null))).toHashCode());
        final Object[] obj = new Object[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = new Object();
        Assertions.assertEquals((((17 * 37) + (obj[0].hashCode())) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = new Object();
        Assertions.assertEquals(((((17 * 37) + (obj[0].hashCode())) * 37) + (obj[1].hashCode())), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testObjectArrayAsObject() {
        final Object[] obj = new Object[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = new Object();
        Assertions.assertEquals((((17 * 37) + (obj[0].hashCode())) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = new Object();
        Assertions.assertEquals(((((17 * 37) + (obj[0].hashCode())) * 37) + (obj[1].hashCode())), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testLongArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((long[]) (null))).toHashCode());
        final long[] obj = new long[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = 5L;
        final int h1 = ((int) (5L ^ (5L >> 32)));
        Assertions.assertEquals((((17 * 37) + h1) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = 6L;
        final int h2 = ((int) (6L ^ (6L >> 32)));
        Assertions.assertEquals(((((17 * 37) + h1) * 37) + h2), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testLongArrayAsObject() {
        final long[] obj = new long[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = 5L;
        final int h1 = ((int) (5L ^ (5L >> 32)));
        Assertions.assertEquals((((17 * 37) + h1) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = 6L;
        final int h2 = ((int) (6L ^ (6L >> 32)));
        Assertions.assertEquals(((((17 * 37) + h1) * 37) + h2), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testIntArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((int[]) (null))).toHashCode());
        final int[] obj = new int[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = 5;
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = 6;
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testIntArrayAsObject() {
        final int[] obj = new int[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = 5;
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = 6;
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testShortArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((short[]) (null))).toHashCode());
        final short[] obj = new short[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = ((short) (5));
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = ((short) (6));
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testShortArrayAsObject() {
        final short[] obj = new short[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = ((short) (5));
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = ((short) (6));
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testCharArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((char[]) (null))).toHashCode());
        final char[] obj = new char[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = ((char) (5));
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = ((char) (6));
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testCharArrayAsObject() {
        final char[] obj = new char[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = ((char) (5));
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = ((char) (6));
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testByteArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((byte[]) (null))).toHashCode());
        final byte[] obj = new byte[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = ((byte) (5));
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = ((byte) (6));
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testByteArrayAsObject() {
        final byte[] obj = new byte[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = ((byte) (5));
        Assertions.assertEquals((((17 * 37) + 5) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = ((byte) (6));
        Assertions.assertEquals(((((17 * 37) + 5) * 37) + 6), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testDoubleArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((double[]) (null))).toHashCode());
        final double[] obj = new double[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = 5.4;
        final long l1 = Double.doubleToLongBits(5.4);
        final int h1 = ((int) (l1 ^ (l1 >> 32)));
        Assertions.assertEquals((((17 * 37) + h1) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = 6.3;
        final long l2 = Double.doubleToLongBits(6.3);
        final int h2 = ((int) (l2 ^ (l2 >> 32)));
        Assertions.assertEquals(((((17 * 37) + h1) * 37) + h2), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testDoubleArrayAsObject() {
        final double[] obj = new double[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = 5.4;
        final long l1 = Double.doubleToLongBits(5.4);
        final int h1 = ((int) (l1 ^ (l1 >> 32)));
        Assertions.assertEquals((((17 * 37) + h1) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = 6.3;
        final long l2 = Double.doubleToLongBits(6.3);
        final int h2 = ((int) (l2 ^ (l2 >> 32)));
        Assertions.assertEquals(((((17 * 37) + h1) * 37) + h2), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testFloatArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((float[]) (null))).toHashCode());
        final float[] obj = new float[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = 5.4F;
        final int h1 = Float.floatToIntBits(5.4F);
        Assertions.assertEquals((((17 * 37) + h1) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = 6.3F;
        final int h2 = Float.floatToIntBits(6.3F);
        Assertions.assertEquals(((((17 * 37) + h1) * 37) + h2), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testFloatArrayAsObject() {
        final float[] obj = new float[2];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = 5.4F;
        final int h1 = Float.floatToIntBits(5.4F);
        Assertions.assertEquals((((17 * 37) + h1) * 37), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = 6.3F;
        final int h2 = Float.floatToIntBits(6.3F);
        Assertions.assertEquals(((((17 * 37) + h1) * 37) + h2), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testBooleanArray() {
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(((boolean[]) (null))).toHashCode());
        final boolean[] obj = new boolean[2];
        Assertions.assertEquals(((((17 * 37) + 1) * 37) + 1), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = true;
        Assertions.assertEquals(((((17 * 37) + 0) * 37) + 1), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = false;
        Assertions.assertEquals(((((17 * 37) + 0) * 37) + 1), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testBooleanArrayAsObject() {
        final boolean[] obj = new boolean[2];
        Assertions.assertEquals(((((17 * 37) + 1) * 37) + 1), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[0] = true;
        Assertions.assertEquals(((((17 * 37) + 0) * 37) + 1), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
        obj[1] = false;
        Assertions.assertEquals(((((17 * 37) + 0) * 37) + 1), new HashCodeBuilder(17, 37).append(((Object) (obj))).toHashCode());
    }

    @Test
    public void testBooleanMultiArray() {
        final boolean[][] obj = new boolean[2][];
        Assertions.assertEquals(((17 * 37) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = new boolean[0];
        Assertions.assertEquals((17 * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = new boolean[1];
        Assertions.assertEquals((((17 * 37) + 1) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0] = new boolean[2];
        Assertions.assertEquals((((((17 * 37) + 1) * 37) + 1) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[0][0] = true;
        Assertions.assertEquals((((((17 * 37) + 0) * 37) + 1) * 37), new HashCodeBuilder(17, 37).append(obj).toHashCode());
        obj[1] = new boolean[1];
        Assertions.assertEquals(((((((17 * 37) + 0) * 37) + 1) * 37) + 1), new HashCodeBuilder(17, 37).append(obj).toHashCode());
    }

    @Test
    public void testReflectionHashCodeExcludeFields() {
        final HashCodeBuilderTest.TestObjectWithMultipleFields x = new HashCodeBuilderTest.TestObjectWithMultipleFields(1, 2, 3);
        Assertions.assertEquals(((((((17 * 37) + 1) * 37) + 2) * 37) + 3), HashCodeBuilder.reflectionHashCode(x));
        Assertions.assertEquals(((((((17 * 37) + 1) * 37) + 2) * 37) + 3), HashCodeBuilder.reflectionHashCode(x, ((String[]) (null))));
        Assertions.assertEquals(((((((17 * 37) + 1) * 37) + 2) * 37) + 3), HashCodeBuilder.reflectionHashCode(x));
        Assertions.assertEquals(((((((17 * 37) + 1) * 37) + 2) * 37) + 3), HashCodeBuilder.reflectionHashCode(x, "xxx"));
        Assertions.assertEquals(((((17 * 37) + 1) * 37) + 3), HashCodeBuilder.reflectionHashCode(x, "two"));
        Assertions.assertEquals(((((17 * 37) + 1) * 37) + 2), HashCodeBuilder.reflectionHashCode(x, "three"));
        Assertions.assertEquals(((17 * 37) + 1), HashCodeBuilder.reflectionHashCode(x, "two", "three"));
        Assertions.assertEquals(17, HashCodeBuilder.reflectionHashCode(x, "one", "two", "three"));
        Assertions.assertEquals(17, HashCodeBuilder.reflectionHashCode(x, "one", "two", "three", "xxx"));
    }

    static class TestObjectWithMultipleFields {
        @SuppressWarnings("unused")
        private int one = 0;

        @SuppressWarnings("unused")
        private int two = 0;

        @SuppressWarnings("unused")
        private int three = 0;

        TestObjectWithMultipleFields(final int one, final int two, final int three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }
    }

    /**
     * Test Objects pointing to each other.
     */
    @Test
    public void testReflectionObjectCycle() {
        final HashCodeBuilderTest.ReflectionTestCycleA a = new HashCodeBuilderTest.ReflectionTestCycleA();
        final HashCodeBuilderTest.ReflectionTestCycleB b = new HashCodeBuilderTest.ReflectionTestCycleB();
        a.b = b;
        b.a = a;
        // Used to caused:
        // java.lang.StackOverflowError
        // at java.lang.ClassLoader.getCallerClassLoader(Native Method)
        // at java.lang.Class.getDeclaredFields(Class.java:992)
        // at org.apache.commons.lang.builder.HashCodeBuilder.reflectionAppend(HashCodeBuilder.java:373)
        // at org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode(HashCodeBuilder.java:349)
        // at org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode(HashCodeBuilder.java:155)
        // at
        // org.apache.commons.lang.builder.HashCodeBuilderTest$ReflectionTestCycleB.hashCode(HashCodeBuilderTest.java:53)
        // at org.apache.commons.lang.builder.HashCodeBuilder.append(HashCodeBuilder.java:422)
        // at org.apache.commons.lang.builder.HashCodeBuilder.reflectionAppend(HashCodeBuilder.java:383)
        // at org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode(HashCodeBuilder.java:349)
        // at org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode(HashCodeBuilder.java:155)
        // at
        // org.apache.commons.lang.builder.HashCodeBuilderTest$ReflectionTestCycleA.hashCode(HashCodeBuilderTest.java:42)
        // at org.apache.commons.lang.builder.HashCodeBuilder.append(HashCodeBuilder.java:422)
        a.hashCode();
        Assertions.assertNull(HashCodeBuilder.getRegistry());
        b.hashCode();
        Assertions.assertNull(HashCodeBuilder.getRegistry());
    }

    /**
     * Ensures LANG-520 remains true
     */
    @Test
    public void testToHashCodeEqualsHashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder(17, 37).append(new Object()).append('a');
        Assertions.assertEquals(hcb.toHashCode(), hcb.hashCode(), "hashCode() is no longer returning the same value as toHashCode() - see LANG-520");
    }

    static class TestObjectHashCodeExclude {
        @HashCodeExclude
        private final int a;

        private final int b;

        TestObjectHashCodeExclude(final int a, final int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    static class TestObjectHashCodeExclude2 {
        @HashCodeExclude
        private final int a;

        @HashCodeExclude
        private final int b;

        TestObjectHashCodeExclude2(final int a, final int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    @Test
    public void testToHashCodeExclude() {
        final HashCodeBuilderTest.TestObjectHashCodeExclude one = new HashCodeBuilderTest.TestObjectHashCodeExclude(1, 2);
        final HashCodeBuilderTest.TestObjectHashCodeExclude2 two = new HashCodeBuilderTest.TestObjectHashCodeExclude2(1, 2);
        Assertions.assertEquals(((17 * 37) + 2), HashCodeBuilder.reflectionHashCode(one));
        Assertions.assertEquals(17, HashCodeBuilder.reflectionHashCode(two));
    }
}

