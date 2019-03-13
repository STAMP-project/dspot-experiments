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


import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link DiffBuilder}.
 */
public class DiffBuilderTest {
    private static final ToStringStyle SHORT_STYLE = ToStringStyle.SHORT_PREFIX_STYLE;

    private static class TypeTestClass implements Diffable<DiffBuilderTest.TypeTestClass> {
        private ToStringStyle style = DiffBuilderTest.SHORT_STYLE;

        private boolean booleanField = true;

        private boolean[] booleanArrayField = new boolean[]{ true };

        private byte byteField = ((byte) (255));

        private byte[] byteArrayField = new byte[]{ ((byte) (255)) };

        private char charField = 'a';

        private char[] charArrayField = new char[]{ 'a' };

        private double doubleField = 1.0;

        private double[] doubleArrayField = new double[]{ 1.0 };

        private float floatField = 1.0F;

        private float[] floatArrayField = new float[]{ 1.0F };

        private int intField = 1;

        private int[] intArrayField = new int[]{ 1 };

        private long longField = 1L;

        private long[] longArrayField = new long[]{ 1L };

        private short shortField = 1;

        private short[] shortArrayField = new short[]{ 1 };

        private Object objectField = null;

        private Object[] objectArrayField = new Object[]{ null };

        @Override
        public DiffResult diff(final DiffBuilderTest.TypeTestClass obj) {
            return new DiffBuilder(this, obj, style).append("boolean", booleanField, obj.booleanField).append("booleanArray", booleanArrayField, obj.booleanArrayField).append("byte", byteField, obj.byteField).append("byteArray", byteArrayField, obj.byteArrayField).append("char", charField, obj.charField).append("charArray", charArrayField, obj.charArrayField).append("double", doubleField, obj.doubleField).append("doubleArray", doubleArrayField, obj.doubleArrayField).append("float", floatField, obj.floatField).append("floatArray", floatArrayField, obj.floatArrayField).append("int", intField, obj.intField).append("intArray", intArrayField, obj.intArrayField).append("long", longField, obj.longField).append("longArray", longArrayField, obj.longArrayField).append("short", shortField, obj.shortField).append("shortArray", shortArrayField, obj.shortArrayField).append("objectField", objectField, obj.objectField).append("objectArrayField", objectArrayField, obj.objectArrayField).build();
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, false);
        }

        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj, false);
        }
    }

    @Test
    public void testBoolean() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.booleanField = false;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Boolean.class, diff.getType());
        Assertions.assertEquals(Boolean.TRUE, diff.getLeft());
        Assertions.assertEquals(Boolean.FALSE, diff.getRight());
    }

    @Test
    public void testBooleanArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.booleanArrayField = new boolean[]{ false, false };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.booleanArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.booleanArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testByte() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.byteField = 1;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Byte.valueOf(class1.byteField), diff.getLeft());
        Assertions.assertEquals(Byte.valueOf(class2.byteField), diff.getRight());
    }

    @Test
    public void testByteArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.byteArrayField = new byte[]{ 1, 2 };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.byteArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.byteArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testChar() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.charField = 'z';
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Character.valueOf(class1.charField), diff.getLeft());
        Assertions.assertEquals(Character.valueOf(class2.charField), diff.getRight());
    }

    @Test
    public void testCharArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.charArrayField = new char[]{ 'f', 'o', 'o' };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.charArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.charArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testDouble() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.doubleField = 99.99;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Double.valueOf(class1.doubleField), diff.getLeft());
        Assertions.assertEquals(Double.valueOf(class2.doubleField), diff.getRight());
    }

    @Test
    public void testDoubleArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.doubleArrayField = new double[]{ 3.0, 2.9, 2.8 };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.doubleArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.doubleArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testFloat() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.floatField = 99.99F;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Float.valueOf(class1.floatField), diff.getLeft());
        Assertions.assertEquals(Float.valueOf(class2.floatField), diff.getRight());
    }

    @Test
    public void testFloatArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.floatArrayField = new float[]{ 3.0F, 2.9F, 2.8F };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.floatArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.floatArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testInt() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.intField = 42;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Integer.valueOf(class1.intField), diff.getLeft());
        Assertions.assertEquals(Integer.valueOf(class2.intField), diff.getRight());
    }

    @Test
    public void testIntArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.intArrayField = new int[]{ 3, 2, 1 };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.intArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.intArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testLong() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.longField = 42L;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Long.valueOf(class1.longField), diff.getLeft());
        Assertions.assertEquals(Long.valueOf(class2.longField), diff.getRight());
    }

    @Test
    public void testLongArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.longArrayField = new long[]{ 3L, 2L, 1L };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.longArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.longArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testShort() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.shortField = 42;
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(Short.valueOf(class1.shortField), diff.getLeft());
        Assertions.assertEquals(Short.valueOf(class2.shortField), diff.getRight());
    }

    @Test
    public void testShortArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.shortArrayField = new short[]{ 3, 2, 1 };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(ArrayUtils.toObject(class1.shortArrayField), ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(ArrayUtils.toObject(class2.shortArrayField), ((Object[]) (diff.getRight())));
    }

    @Test
    public void testObject() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.objectField = "Some string";
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertEquals(class1.objectField, diff.getLeft());
        Assertions.assertEquals(class2.objectField, diff.getRight());
    }

    /**
     * Test that "left" and "right" are the same instance and are equal.
     */
    @Test
    public void testObjectsSameAndEqual() {
        final Integer sameObject = 1;
        final DiffBuilderTest.TypeTestClass left = new DiffBuilderTest.TypeTestClass();
        left.objectField = sameObject;
        final DiffBuilderTest.TypeTestClass right = new DiffBuilderTest.TypeTestClass();
        right.objectField = sameObject;
        Assertions.assertSame(left.objectField, right.objectField);
        Assertions.assertEquals(left.objectField, right.objectField);
        final DiffResult list = left.diff(right);
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    /**
     * Test that "left" and "right" are the same instance but are equal.
     */
    @Test
    public void testObjectsNotSameButEqual() {
        final DiffBuilderTest.TypeTestClass left = new DiffBuilderTest.TypeTestClass();
        left.objectField = new Integer(1);
        final DiffBuilderTest.TypeTestClass right = new DiffBuilderTest.TypeTestClass();
        right.objectField = new Integer(1);
        Assertions.assertNotSame(left.objectField, right.objectField);
        Assertions.assertEquals(left.objectField, right.objectField);
        final DiffResult list = left.diff(right);
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    /**
     * Test that "left" and "right" are not the same instance and are not equal.
     */
    @Test
    public void testObjectsNotSameNorEqual() {
        final DiffBuilderTest.TypeTestClass left = new DiffBuilderTest.TypeTestClass();
        left.objectField = 4;
        final DiffBuilderTest.TypeTestClass right = new DiffBuilderTest.TypeTestClass();
        right.objectField = 100;
        Assertions.assertNotSame(left.objectField, right.objectField);
        Assertions.assertNotEquals(left.objectField, right.objectField);
        final DiffResult list = left.diff(right);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
    }

    @Test
    public void testObjectArray() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.objectArrayField = new Object[]{ "string", 1, 2 };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        final Diff<?> diff = list.getDiffs().get(0);
        Assertions.assertArrayEquals(class1.objectArrayField, ((Object[]) (diff.getLeft())));
        Assertions.assertArrayEquals(class2.objectArrayField, ((Object[]) (diff.getRight())));
    }

    @Test
    public void testObjectArrayEqual() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class1.objectArrayField = new Object[]{ "string", 1, 2 };
        class2.objectArrayField = new Object[]{ "string", 1, 2 };
        final DiffResult list = class1.diff(class2);
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void testByteArrayEqualAsObject() {
        final DiffResult list = new DiffBuilder("String1", "String2", DiffBuilderTest.SHORT_STYLE).append("foo", new boolean[]{ false }, new boolean[]{ false }).append("foo", new byte[]{ 1 }, new byte[]{ 1 }).append("foo", new char[]{ 'a' }, new char[]{ 'a' }).append("foo", new double[]{ 1.0 }, new double[]{ 1.0 }).append("foo", new float[]{ 1.0F }, new float[]{ 1.0F }).append("foo", new int[]{ 1 }, new int[]{ 1 }).append("foo", new long[]{ 1L }, new long[]{ 1L }).append("foo", new short[]{ 1 }, new short[]{ 1 }).append("foo", new Object[]{ 1, "two" }, new Object[]{ 1, "two" }).build();
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void testDiffResult() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass class2 = new DiffBuilderTest.TypeTestClass();
        class2.intField = 2;
        final DiffResult list = new DiffBuilder(class1, class2, DiffBuilderTest.SHORT_STYLE).append("prop1", class1.diff(class2)).build();
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        Assertions.assertEquals("prop1.int", list.getDiffs().get(0).getFieldName());
    }

    @Test
    public void testNullLhs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DiffBuilder(null, this, ToStringStyle.DEFAULT_STYLE));
    }

    @Test
    public void testNullRhs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DiffBuilder(this, null, ToStringStyle.DEFAULT_STYLE));
    }

    @Test
    public void testSameObjectIgnoresAppends() {
        final DiffBuilderTest.TypeTestClass testClass = new DiffBuilderTest.TypeTestClass();
        final DiffResult list = new DiffBuilder(testClass, testClass, DiffBuilderTest.SHORT_STYLE).append("ignored", false, true).build();
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void testSimilarObjectIgnoresAppends() {
        final DiffBuilderTest.TypeTestClass testClass1 = new DiffBuilderTest.TypeTestClass();
        final DiffBuilderTest.TypeTestClass testClass2 = new DiffBuilderTest.TypeTestClass();
        final DiffResult list = new DiffBuilder(testClass1, testClass2, DiffBuilderTest.SHORT_STYLE).append("ignored", false, true).build();
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void testStylePassedToDiffResult() {
        final DiffBuilderTest.TypeTestClass class1 = new DiffBuilderTest.TypeTestClass();
        DiffResult list = class1.diff(class1);
        Assertions.assertEquals(DiffBuilderTest.SHORT_STYLE, list.getToStringStyle());
        class1.style = ToStringStyle.MULTI_LINE_STYLE;
        list = class1.diff(class1);
        Assertions.assertEquals(ToStringStyle.MULTI_LINE_STYLE, list.getToStringStyle());
    }

    @Test
    public void testTriviallyEqualTestDisabled() {
        final Matcher<Integer> equalToOne = IsEqual.equalTo(1);
        // Constructor's arguments are not trivially equal, but not testing for that.
        final DiffBuilder explicitTestAndNotEqual1 = new DiffBuilder(1, 2, null, false);
        explicitTestAndNotEqual1.append("letter", "X", "Y");
        MatcherAssert.assertThat(explicitTestAndNotEqual1.build().getNumberOfDiffs(), equalToOne);
        // Constructor's arguments are trivially equal, but not testing for that.
        final DiffBuilder explicitTestAndNotEqual2 = new DiffBuilder(1, 1, null, false);
        // This append(f, l, r) will not abort early.
        explicitTestAndNotEqual2.append("letter", "X", "Y");
        MatcherAssert.assertThat(explicitTestAndNotEqual2.build().getNumberOfDiffs(), equalToOne);
    }

    @Test
    public void testTriviallyEqualTestEnabled() {
        final Matcher<Integer> equalToZero = IsEqual.equalTo(0);
        final Matcher<Integer> equalToOne = IsEqual.equalTo(1);
        // The option to test if trivially equal is enabled by default.
        final DiffBuilder implicitTestAndEqual = new DiffBuilder(1, 1, null);
        // This append(f, l, r) will abort without creating a Diff for letter.
        implicitTestAndEqual.append("letter", "X", "Y");
        MatcherAssert.assertThat(implicitTestAndEqual.build().getNumberOfDiffs(), equalToZero);
        final DiffBuilder implicitTestAndNotEqual = new DiffBuilder(1, 2, null);
        // This append(f, l, r) will not abort early
        // because the constructor's arguments were not trivially equal.
        implicitTestAndNotEqual.append("letter", "X", "Y");
        MatcherAssert.assertThat(implicitTestAndNotEqual.build().getNumberOfDiffs(), equalToOne);
        // This is explicitly enabling the trivially equal test.
        final DiffBuilder explicitTestAndEqual = new DiffBuilder(1, 1, null, true);
        explicitTestAndEqual.append("letter", "X", "Y");
        MatcherAssert.assertThat(explicitTestAndEqual.build().getNumberOfDiffs(), equalToZero);
    }
}

