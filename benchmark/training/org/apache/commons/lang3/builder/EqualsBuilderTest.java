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


import java.lang.reflect.Method;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.builder.EqualsBuilder}.
 */
public class EqualsBuilderTest {
    // -----------------------------------------------------------------------
    static class TestObject {
        private int a;

        TestObject() {
        }

        TestObject(final int a) {
            this.a = a;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (o == (this)) {
                return true;
            }
            if ((o.getClass()) != (getClass())) {
                return false;
            }
            final EqualsBuilderTest.TestObject rhs = ((EqualsBuilderTest.TestObject) (o));
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

    static class TestSubObject extends EqualsBuilderTest.TestObject {
        private int b;

        TestSubObject() {
            super(0);
        }

        TestSubObject(final int a, final int b) {
            super(a);
            this.b = b;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (o == (this)) {
                return true;
            }
            if ((o.getClass()) != (getClass())) {
                return false;
            }
            final EqualsBuilderTest.TestSubObject rhs = ((EqualsBuilderTest.TestSubObject) (o));
            return (super.equals(o)) && ((b) == (rhs.b));
        }

        @Override
        public int hashCode() {
            return ((b) * 17) + (super.hashCode());
        }

        public void setB(final int b) {
            this.b = b;
        }

        public int getB() {
            return b;
        }
    }

    static class TestEmptySubObject extends EqualsBuilderTest.TestObject {
        TestEmptySubObject(final int a) {
            super(a);
        }
    }

    static class TestTSubObject extends EqualsBuilderTest.TestObject {
        @SuppressWarnings("unused")
        private transient int t;

        TestTSubObject(final int a, final int t) {
            super(a);
            this.t = t;
        }
    }

    static class TestTTSubObject extends EqualsBuilderTest.TestTSubObject {
        @SuppressWarnings("unused")
        private transient int tt;

        TestTTSubObject(final int a, final int t, final int tt) {
            super(a, t);
            this.tt = tt;
        }
    }

    static class TestTTLeafObject extends EqualsBuilderTest.TestTTSubObject {
        @SuppressWarnings("unused")
        private final int leafValue;

        TestTTLeafObject(final int a, final int t, final int tt, final int leafValue) {
            super(a, t, tt);
            this.leafValue = leafValue;
        }
    }

    static class TestTSubObject2 extends EqualsBuilderTest.TestObject {
        private transient int t;

        TestTSubObject2(final int a, final int t) {
            super(a);
        }

        public int getT() {
            return t;
        }

        public void setT(final int t) {
            this.t = t;
        }
    }

    static class TestRecursiveGenericObject<T> {
        private final T a;

        TestRecursiveGenericObject(final T a) {
            this.a = a;
        }

        public T getA() {
            return a;
        }
    }

    static class TestRecursiveObject {
        private final EqualsBuilderTest.TestRecursiveInnerObject a;

        private final EqualsBuilderTest.TestRecursiveInnerObject b;

        private int z;

        TestRecursiveObject(final EqualsBuilderTest.TestRecursiveInnerObject a, final EqualsBuilderTest.TestRecursiveInnerObject b, final int z) {
            this.a = a;
            this.b = b;
        }

        public EqualsBuilderTest.TestRecursiveInnerObject getA() {
            return a;
        }

        public EqualsBuilderTest.TestRecursiveInnerObject getB() {
            return b;
        }

        public int getZ() {
            return z;
        }
    }

    static class TestRecursiveInnerObject {
        private final int n;

        TestRecursiveInnerObject(final int n) {
            this.n = n;
        }

        public int getN() {
            return n;
        }
    }

    static class TestRecursiveCycleObject {
        private EqualsBuilderTest.TestRecursiveCycleObject cycle;

        private final int n;

        TestRecursiveCycleObject(final int n) {
            this.n = n;
            this.cycle = this;
        }

        TestRecursiveCycleObject(final EqualsBuilderTest.TestRecursiveCycleObject cycle, final int n) {
            this.n = n;
            this.cycle = cycle;
        }

        public int getN() {
            return n;
        }

        public EqualsBuilderTest.TestRecursiveCycleObject getCycle() {
            return cycle;
        }

        public void setCycle(final EqualsBuilderTest.TestRecursiveCycleObject cycle) {
            this.cycle = cycle;
        }
    }

    @Test
    public void testReflectionEquals() {
        final EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        final EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(o1, o1));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(o1, o2));
        o2.setA(4);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(o1, o2));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(o1, this));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(o1, null));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(null, o2));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(null, null));
    }

    @Test
    public void testReflectionHierarchyEquals() {
        testReflectionHierarchyEquals(false);
        testReflectionHierarchyEquals(true);
        // Transients
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), true));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), false));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 0, 0, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), true));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 0), true));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(0, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), true));
    }

    @Test
    public void testSuper() {
        final EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        final EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assertions.assertTrue(new EqualsBuilder().appendSuper(true).append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().appendSuper(false).append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().appendSuper(true).append(o1, o2).isEquals());
        Assertions.assertFalse(new EqualsBuilder().appendSuper(false).append(o1, o2).isEquals());
    }

    @Test
    public void testObject() {
        final EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        final EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
        o2.setA(4);
        Assertions.assertTrue(new EqualsBuilder().append(o1, o2).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, this).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, null).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(null, o2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(((Object) (null)), null).isEquals());
    }

    @Test
    public void testObjectBuild() {
        final EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        final EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assertions.assertEquals(Boolean.TRUE, new EqualsBuilder().append(o1, o1).build());
        Assertions.assertEquals(Boolean.FALSE, new EqualsBuilder().append(o1, o2).build());
        o2.setA(4);
        Assertions.assertEquals(Boolean.TRUE, new EqualsBuilder().append(o1, o2).build());
        Assertions.assertEquals(Boolean.FALSE, new EqualsBuilder().append(o1, this).build());
        Assertions.assertEquals(Boolean.FALSE, new EqualsBuilder().append(o1, null).build());
        Assertions.assertEquals(Boolean.FALSE, new EqualsBuilder().append(null, o2).build());
        Assertions.assertEquals(Boolean.TRUE, new EqualsBuilder().append(((Object) (null)), null).build());
    }

    @Test
    public void testObjectRecursiveGenericInteger() {
        final EqualsBuilderTest.TestRecursiveGenericObject<Integer> o1_a = new EqualsBuilderTest.TestRecursiveGenericObject<>(1);
        final EqualsBuilderTest.TestRecursiveGenericObject<Integer> o1_b = new EqualsBuilderTest.TestRecursiveGenericObject<>(1);
        final EqualsBuilderTest.TestRecursiveGenericObject<Integer> o2 = new EqualsBuilderTest.TestRecursiveGenericObject<>(2);
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_b).isEquals());
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_b, o1_a).isEquals());
        Assertions.assertFalse(new EqualsBuilder().setTestRecursive(true).append(o1_b, o2).isEquals());
    }

    @Test
    public void testObjectRecursiveGenericString() {
        // Note: Do not use literals, because string literals are always mapped by same object (internal() of String))!
        String s1_a = String.valueOf(1);
        final EqualsBuilderTest.TestRecursiveGenericObject<String> o1_a = new EqualsBuilderTest.TestRecursiveGenericObject<>(s1_a);
        final EqualsBuilderTest.TestRecursiveGenericObject<String> o1_b = new EqualsBuilderTest.TestRecursiveGenericObject<>(String.valueOf(1));
        final EqualsBuilderTest.TestRecursiveGenericObject<String> o2 = new EqualsBuilderTest.TestRecursiveGenericObject<>(String.valueOf(2));
        // To trigger bug reported in LANG-1356, call hashCode only on string in instance o1_a
        s1_a.hashCode();
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_b).isEquals());
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_b, o1_a).isEquals());
        Assertions.assertFalse(new EqualsBuilder().setTestRecursive(true).append(o1_b, o2).isEquals());
    }

    @Test
    public void testObjectRecursive() {
        final EqualsBuilderTest.TestRecursiveInnerObject i1_1 = new EqualsBuilderTest.TestRecursiveInnerObject(1);
        final EqualsBuilderTest.TestRecursiveInnerObject i1_2 = new EqualsBuilderTest.TestRecursiveInnerObject(1);
        final EqualsBuilderTest.TestRecursiveInnerObject i2_1 = new EqualsBuilderTest.TestRecursiveInnerObject(2);
        final EqualsBuilderTest.TestRecursiveInnerObject i2_2 = new EqualsBuilderTest.TestRecursiveInnerObject(2);
        final EqualsBuilderTest.TestRecursiveInnerObject i3 = new EqualsBuilderTest.TestRecursiveInnerObject(3);
        final EqualsBuilderTest.TestRecursiveInnerObject i4 = new EqualsBuilderTest.TestRecursiveInnerObject(4);
        final EqualsBuilderTest.TestRecursiveObject o1_a = new EqualsBuilderTest.TestRecursiveObject(i1_1, i2_1, 1);
        final EqualsBuilderTest.TestRecursiveObject o1_b = new EqualsBuilderTest.TestRecursiveObject(i1_2, i2_2, 1);
        final EqualsBuilderTest.TestRecursiveObject o2 = new EqualsBuilderTest.TestRecursiveObject(i3, i4, 2);
        final EqualsBuilderTest.TestRecursiveObject oNull = new EqualsBuilderTest.TestRecursiveObject(null, null, 2);
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_a).isEquals());
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_b).isEquals());
        Assertions.assertFalse(new EqualsBuilder().setTestRecursive(true).append(o1_a, o2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(oNull, oNull).isEquals());
        Assertions.assertFalse(new EqualsBuilder().setTestRecursive(true).append(o1_a, oNull).isEquals());
    }

    @Test
    public void testObjectRecursiveCycleSelfreference() {
        final EqualsBuilderTest.TestRecursiveCycleObject o1_a = new EqualsBuilderTest.TestRecursiveCycleObject(1);
        final EqualsBuilderTest.TestRecursiveCycleObject o1_b = new EqualsBuilderTest.TestRecursiveCycleObject(1);
        final EqualsBuilderTest.TestRecursiveCycleObject o2 = new EqualsBuilderTest.TestRecursiveCycleObject(2);
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_a).isEquals());
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_b).isEquals());
        Assertions.assertFalse(new EqualsBuilder().setTestRecursive(true).append(o1_a, o2).isEquals());
    }

    @Test
    public void testObjectRecursiveCycle() {
        final EqualsBuilderTest.TestRecursiveCycleObject o1_a = new EqualsBuilderTest.TestRecursiveCycleObject(1);
        final EqualsBuilderTest.TestRecursiveCycleObject i1_a = new EqualsBuilderTest.TestRecursiveCycleObject(o1_a, 100);
        o1_a.setCycle(i1_a);
        final EqualsBuilderTest.TestRecursiveCycleObject o1_b = new EqualsBuilderTest.TestRecursiveCycleObject(1);
        final EqualsBuilderTest.TestRecursiveCycleObject i1_b = new EqualsBuilderTest.TestRecursiveCycleObject(o1_b, 100);
        o1_b.setCycle(i1_b);
        final EqualsBuilderTest.TestRecursiveCycleObject o2 = new EqualsBuilderTest.TestRecursiveCycleObject(2);
        final EqualsBuilderTest.TestRecursiveCycleObject i2 = new EqualsBuilderTest.TestRecursiveCycleObject(o1_b, 200);
        o2.setCycle(i2);
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_a).isEquals());
        Assertions.assertTrue(new EqualsBuilder().setTestRecursive(true).append(o1_a, o1_b).isEquals());
        Assertions.assertFalse(new EqualsBuilder().setTestRecursive(true).append(o1_a, o2).isEquals());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(o1_a, o1_b, false, null, true));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(o1_a, o2, false, null, true));
    }

    @Test
    public void testLong() {
        final long o1 = 1L;
        final long o2 = 2L;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testInt() {
        final int o1 = 1;
        final int o2 = 2;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testShort() {
        final short o1 = 1;
        final short o2 = 2;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testChar() {
        final char o1 = 1;
        final char o2 = 2;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testByte() {
        final byte o1 = 1;
        final byte o2 = 2;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testDouble() {
        final double o1 = 1;
        final double o2 = 2;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, Double.NaN).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(Double.NaN, Double.NaN).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).isEquals());
    }

    @Test
    public void testFloat() {
        final float o1 = 1;
        final float o2 = 2;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, Float.NaN).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(Float.NaN, Float.NaN).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY).isEquals());
    }

    @Test
    public void testAccessors() {
        final EqualsBuilder equalsBuilder = new EqualsBuilder();
        Assertions.assertTrue(equalsBuilder.isEquals());
        equalsBuilder.setEquals(true);
        Assertions.assertTrue(equalsBuilder.isEquals());
        equalsBuilder.setEquals(false);
        Assertions.assertFalse(equalsBuilder.isEquals());
    }

    @Test
    public void testReset() {
        final EqualsBuilder equalsBuilder = new EqualsBuilder();
        Assertions.assertTrue(equalsBuilder.isEquals());
        equalsBuilder.setEquals(false);
        Assertions.assertFalse(equalsBuilder.isEquals());
        equalsBuilder.reset();
        Assertions.assertTrue(equalsBuilder.isEquals());
    }

    @Test
    public void testBoolean() {
        final boolean o1 = true;
        final boolean o2 = false;
        Assertions.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testObjectArray() {
        EqualsBuilderTest.TestObject[] obj1 = new EqualsBuilderTest.TestObject[3];
        obj1[0] = new EqualsBuilderTest.TestObject(4);
        obj1[1] = new EqualsBuilderTest.TestObject(5);
        obj1[2] = null;
        EqualsBuilderTest.TestObject[] obj2 = new EqualsBuilderTest.TestObject[3];
        obj2[0] = new EqualsBuilderTest.TestObject(4);
        obj2[1] = new EqualsBuilderTest.TestObject(5);
        obj2[2] = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj2, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1].setA(6);
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1].setA(5);
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[2] = obj1[1];
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[2] = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testLongArray() {
        long[] obj1 = new long[2];
        obj1[0] = 5L;
        obj1[1] = 6L;
        long[] obj2 = new long[2];
        obj2[0] = 5L;
        obj2[1] = 6L;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testIntArray() {
        int[] obj1 = new int[2];
        obj1[0] = 5;
        obj1[1] = 6;
        int[] obj2 = new int[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testShortArray() {
        short[] obj1 = new short[2];
        obj1[0] = 5;
        obj1[1] = 6;
        short[] obj2 = new short[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testCharArray() {
        char[] obj1 = new char[2];
        obj1[0] = 5;
        obj1[1] = 6;
        char[] obj2 = new char[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testByteArray() {
        byte[] obj1 = new byte[2];
        obj1[0] = 5;
        obj1[1] = 6;
        byte[] obj2 = new byte[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testDoubleArray() {
        double[] obj1 = new double[2];
        obj1[0] = 5;
        obj1[1] = 6;
        double[] obj2 = new double[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testFloatArray() {
        float[] obj1 = new float[2];
        obj1[0] = 5;
        obj1[1] = 6;
        float[] obj2 = new float[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testBooleanArray() {
        boolean[] obj1 = new boolean[2];
        obj1[0] = true;
        obj1[1] = false;
        boolean[] obj2 = new boolean[2];
        obj2[0] = true;
        obj2[1] = false;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = true;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1 = null;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testMultiLongArray() {
        final long[][] array1 = new long[2][2];
        final long[][] array2 = new long[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiIntArray() {
        final int[][] array1 = new int[2][2];
        final int[][] array2 = new int[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiShortArray() {
        final short[][] array1 = new short[2][2];
        final short[][] array2 = new short[2][2];
        for (short i = 0; i < (array1.length); ++i) {
            for (short j = 0; j < (array1[0].length); j++) {
                array1[i][j] = i;
                array2[i][j] = i;
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiCharArray() {
        final char[][] array1 = new char[2][2];
        final char[][] array2 = new char[2][2];
        for (char i = 0; i < (array1.length); ++i) {
            for (char j = 0; j < (array1[0].length); j++) {
                array1[i][j] = i;
                array2[i][j] = i;
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiByteArray() {
        final byte[][] array1 = new byte[2][2];
        final byte[][] array2 = new byte[2][2];
        for (byte i = 0; i < (array1.length); ++i) {
            for (byte j = 0; j < (array1[0].length); j++) {
                array1[i][j] = i;
                array2[i][j] = i;
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiFloatArray() {
        final float[][] array1 = new float[2][2];
        final float[][] array2 = new float[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiDoubleArray() {
        final double[][] array1 = new double[2][2];
        final double[][] array2 = new double[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMultiBooleanArray() {
        final boolean[][] array1 = new boolean[2][2];
        final boolean[][] array2 = new boolean[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i == 1) || (j == 1);
                array2[i][j] = (i == 1) || (j == 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = false;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
        // compare 1 dim to 2.
        final boolean[] array3 = new boolean[]{ true, true };
        Assertions.assertFalse(new EqualsBuilder().append(array1, array3).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(array3, array1).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(array2, array3).isEquals());
        Assertions.assertFalse(new EqualsBuilder().append(array3, array2).isEquals());
    }

    @Test
    public void testRaggedArray() {
        final long[][] array1 = new long[2][];
        final long[][] array2 = new long[2][];
        for (int i = 0; i < (array1.length); ++i) {
            array1[i] = new long[2];
            array2[i] = new long[2];
            for (int j = 0; j < (array1[i].length); ++j) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testMixedArray() {
        final Object[] array1 = new Object[2];
        final Object[] array2 = new Object[2];
        for (int i = 0; i < (array1.length); ++i) {
            array1[i] = new long[2];
            array2[i] = new long[2];
            for (int j = 0; j < 2; ++j) {
                ((long[]) (array1[i]))[j] = (i + 1) * (j + 1);
                ((long[]) (array2[i]))[j] = (i + 1) * (j + 1);
            }
        }
        Assertions.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        ((long[]) (array1[1]))[1] = 0;
        Assertions.assertFalse(new EqualsBuilder().append(array1, array2).isEquals());
    }

    @Test
    public void testObjectArrayHiddenByObject() {
        final EqualsBuilderTest.TestObject[] array1 = new EqualsBuilderTest.TestObject[2];
        array1[0] = new EqualsBuilderTest.TestObject(4);
        array1[1] = new EqualsBuilderTest.TestObject(5);
        final EqualsBuilderTest.TestObject[] array2 = new EqualsBuilderTest.TestObject[2];
        array2[0] = new EqualsBuilderTest.TestObject(4);
        array2[1] = new EqualsBuilderTest.TestObject(5);
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1].setA(6);
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testLongArrayHiddenByObject() {
        final long[] array1 = new long[2];
        array1[0] = 5L;
        array1[1] = 6L;
        final long[] array2 = new long[2];
        array2[0] = 5L;
        array2[1] = 6L;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testIntArrayHiddenByObject() {
        final int[] array1 = new int[2];
        array1[0] = 5;
        array1[1] = 6;
        final int[] array2 = new int[2];
        array2[0] = 5;
        array2[1] = 6;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testShortArrayHiddenByObject() {
        final short[] array1 = new short[2];
        array1[0] = 5;
        array1[1] = 6;
        final short[] array2 = new short[2];
        array2[0] = 5;
        array2[1] = 6;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testCharArrayHiddenByObject() {
        final char[] array1 = new char[2];
        array1[0] = 5;
        array1[1] = 6;
        final char[] array2 = new char[2];
        array2[0] = 5;
        array2[1] = 6;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testByteArrayHiddenByObject() {
        final byte[] array1 = new byte[2];
        array1[0] = 5;
        array1[1] = 6;
        final byte[] array2 = new byte[2];
        array2[0] = 5;
        array2[1] = 6;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testDoubleArrayHiddenByObject() {
        final double[] array1 = new double[2];
        array1[0] = 5;
        array1[1] = 6;
        final double[] array2 = new double[2];
        array2[0] = 5;
        array2[1] = 6;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testFloatArrayHiddenByObject() {
        final float[] array1 = new float[2];
        array1[0] = 5;
        array1[1] = 6;
        final float[] array2 = new float[2];
        array2[0] = 5;
        array2[1] = 6;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testBooleanArrayHiddenByObject() {
        final boolean[] array1 = new boolean[2];
        array1[0] = true;
        array1[1] = false;
        final boolean[] array2 = new boolean[2];
        array2[0] = true;
        array2[1] = false;
        final Object obj1 = array1;
        final Object obj2 = array2;
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = true;
        Assertions.assertFalse(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    public static class TestACanEqualB {
        private final int a;

        public TestACanEqualB(final int a) {
            this.a = a;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == (this)) {
                return true;
            }
            if (o instanceof EqualsBuilderTest.TestACanEqualB) {
                return (this.a) == (((EqualsBuilderTest.TestACanEqualB) (o)).getA());
            }
            if (o instanceof EqualsBuilderTest.TestBCanEqualA) {
                return (this.a) == (((EqualsBuilderTest.TestBCanEqualA) (o)).getB());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return a;
        }

        public int getA() {
            return this.a;
        }
    }

    public static class TestBCanEqualA {
        private final int b;

        public TestBCanEqualA(final int b) {
            this.b = b;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == (this)) {
                return true;
            }
            if (o instanceof EqualsBuilderTest.TestACanEqualB) {
                return (this.b) == (((EqualsBuilderTest.TestACanEqualB) (o)).getA());
            }
            if (o instanceof EqualsBuilderTest.TestBCanEqualA) {
                return (this.b) == (((EqualsBuilderTest.TestBCanEqualA) (o)).getB());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return b;
        }

        public int getB() {
            return this.b;
        }
    }

    /**
     * Tests two instances of classes that can be equal and that are not "related". The two classes are not subclasses
     * of each other and do not share a parent aside from Object.
     * See http://issues.apache.org/bugzilla/show_bug.cgi?id=33069
     */
    @Test
    public void testUnrelatedClasses() {
        final Object[] x = new Object[]{ new EqualsBuilderTest.TestACanEqualB(1) };
        final Object[] y = new Object[]{ new EqualsBuilderTest.TestBCanEqualA(1) };
        // sanity checks:
        Assertions.assertArrayEquals(x, x);
        Assertions.assertArrayEquals(y, y);
        Assertions.assertArrayEquals(x, y);
        Assertions.assertArrayEquals(y, x);
        // real tests:
        Assertions.assertEquals(x[0], x[0]);
        Assertions.assertEquals(y[0], y[0]);
        Assertions.assertEquals(x[0], y[0]);
        Assertions.assertEquals(y[0], x[0]);
        Assertions.assertTrue(new EqualsBuilder().append(x, x).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(y, y).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(x, y).isEquals());
        Assertions.assertTrue(new EqualsBuilder().append(y, x).isEquals());
    }

    /**
     * Test from http://issues.apache.org/bugzilla/show_bug.cgi?id=33067
     */
    @Test
    public void testNpeForNullElement() {
        final Object[] x1 = new Object[]{ Integer.valueOf(1), null, Integer.valueOf(3) };
        final Object[] x2 = new Object[]{ Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) };
        // causes an NPE in 2.0 according to:
        // http://issues.apache.org/bugzilla/show_bug.cgi?id=33067
        new EqualsBuilder().append(x1, x2);
    }

    @Test
    public void testReflectionEqualsExcludeFields() {
        final EqualsBuilderTest.TestObjectWithMultipleFields x1 = new EqualsBuilderTest.TestObjectWithMultipleFields(1, 2, 3);
        final EqualsBuilderTest.TestObjectWithMultipleFields x2 = new EqualsBuilderTest.TestObjectWithMultipleFields(1, 3, 4);
        // not equal when including all fields
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(x1, x2));
        // doesn't barf on null, empty array, or non-existent field, but still tests as not equal
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(x1, x2, ((String[]) (null))));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(x1, x2));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(x1, x2, "xxx"));
        // not equal if only one of the differing fields excluded
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(x1, x2, "two"));
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(x1, x2, "three"));
        // equal if both differing fields excluded
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(x1, x2, "two", "three"));
        // still equal as long as both differing fields are among excluded
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(x1, x2, "one", "two", "three"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(x1, x2, "one", "two", "three", "xxx"));
    }

    static class TestObjectWithMultipleFields {
        @SuppressWarnings("unused")
        private final EqualsBuilderTest.TestObject one;

        @SuppressWarnings("unused")
        private final EqualsBuilderTest.TestObject two;

        @SuppressWarnings("unused")
        private final EqualsBuilderTest.TestObject three;

        TestObjectWithMultipleFields(final int one, final int two, final int three) {
            this.one = new EqualsBuilderTest.TestObject(one);
            this.two = new EqualsBuilderTest.TestObject(two);
            this.three = new EqualsBuilderTest.TestObject(three);
        }
    }

    /**
     * Test cyclical object references which cause a StackOverflowException if
     * not handled properly. s. LANG-606
     */
    @Test
    public void testCyclicalObjectReferences() {
        final EqualsBuilderTest.TestObjectReference refX1 = new EqualsBuilderTest.TestObjectReference(1);
        final EqualsBuilderTest.TestObjectReference x1 = new EqualsBuilderTest.TestObjectReference(1);
        x1.setObjectReference(refX1);
        refX1.setObjectReference(x1);
        final EqualsBuilderTest.TestObjectReference refX2 = new EqualsBuilderTest.TestObjectReference(1);
        final EqualsBuilderTest.TestObjectReference x2 = new EqualsBuilderTest.TestObjectReference(1);
        x2.setObjectReference(refX2);
        refX2.setObjectReference(x2);
        final EqualsBuilderTest.TestObjectReference refX3 = new EqualsBuilderTest.TestObjectReference(2);
        final EqualsBuilderTest.TestObjectReference x3 = new EqualsBuilderTest.TestObjectReference(2);
        x3.setObjectReference(refX3);
        refX3.setObjectReference(x3);
        Assertions.assertEquals(x1, x2);
        Assertions.assertNull(EqualsBuilder.getRegistry());
        Assertions.assertNotEquals(x1, x3);
        Assertions.assertNull(EqualsBuilder.getRegistry());
        Assertions.assertNotEquals(x2, x3);
        Assertions.assertNull(EqualsBuilder.getRegistry());
    }

    static class TestObjectReference {
        @SuppressWarnings("unused")
        private EqualsBuilderTest.TestObjectReference reference;

        @SuppressWarnings("unused")
        private final EqualsBuilderTest.TestObject one;

        TestObjectReference(final int one) {
            this.one = new EqualsBuilderTest.TestObject(one);
        }

        public void setObjectReference(final EqualsBuilderTest.TestObjectReference reference) {
            this.reference = reference;
        }

        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
    }

    @Test
    public void testReflectionArrays() {
        final EqualsBuilderTest.TestObject one = new EqualsBuilderTest.TestObject(1);
        final EqualsBuilderTest.TestObject two = new EqualsBuilderTest.TestObject(2);
        final Object[] o1 = new Object[]{ one };
        final Object[] o2 = new Object[]{ two };
        final Object[] o3 = new Object[]{ one };
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(o1, o2));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(o1, o1));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(o1, o3));
        final double[] d1 = new double[]{ 0, 1 };
        final double[] d2 = new double[]{ 2, 3 };
        final double[] d3 = new double[]{ 0, 1 };
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(d1, d2));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(d1, d1));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(d1, d3));
    }

    static class TestObjectEqualsExclude {
        @EqualsExclude
        private final int a;

        private final int b;

        TestObjectEqualsExclude(final int a, final int b) {
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
    public void testToEqualsExclude() {
        EqualsBuilderTest.TestObjectEqualsExclude one = new EqualsBuilderTest.TestObjectEqualsExclude(1, 2);
        EqualsBuilderTest.TestObjectEqualsExclude two = new EqualsBuilderTest.TestObjectEqualsExclude(1, 3);
        Assertions.assertFalse(EqualsBuilder.reflectionEquals(one, two));
        one = new EqualsBuilderTest.TestObjectEqualsExclude(1, 2);
        two = new EqualsBuilderTest.TestObjectEqualsExclude(2, 2);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(one, two));
    }

    @Test
    public void testReflectionAppend() {
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(null, null));
        final EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        final EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assertions.assertTrue(new EqualsBuilder().reflectionAppend(o1, o1).build());
        Assertions.assertFalse(new EqualsBuilder().reflectionAppend(o1, o2).build());
        o2.setA(4);
        Assertions.assertTrue(new EqualsBuilder().reflectionAppend(o1, o2).build());
        Assertions.assertFalse(new EqualsBuilder().reflectionAppend(o1, this).build());
        Assertions.assertFalse(new EqualsBuilder().reflectionAppend(o1, null).build());
        Assertions.assertFalse(new EqualsBuilder().reflectionAppend(null, o2).build());
    }

    @Test
    public void testIsRegistered() throws Exception {
        final Object firstObject = new Object();
        final Object secondObject = new Object();
        try {
            final Method registerMethod = MethodUtils.getMatchingMethod(EqualsBuilder.class, "register", Object.class, Object.class);
            registerMethod.setAccessible(true);
            registerMethod.invoke(null, firstObject, secondObject);
            Assertions.assertTrue(EqualsBuilder.isRegistered(firstObject, secondObject));
            Assertions.assertTrue(EqualsBuilder.isRegistered(secondObject, firstObject));// LANG-1349

        } finally {
            final Method unregisterMethod = MethodUtils.getMatchingMethod(EqualsBuilder.class, "unregister", Object.class, Object.class);
            unregisterMethod.setAccessible(true);
            unregisterMethod.invoke(null, firstObject, secondObject);
        }
    }
}

