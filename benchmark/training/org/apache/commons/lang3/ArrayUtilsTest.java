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
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.ArrayUtils}.
 */
// deliberate use of deprecated code
@SuppressWarnings("deprecation")
public class ArrayUtilsTest {
    /**
     * A predefined seed used to initialize {@link Random} in order to get predictable results
     */
    private static final long SEED = 16111981L;

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new ArrayUtils());
        final Constructor<?>[] cons = ArrayUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(ArrayUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(ArrayUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToString() {
        Assertions.assertEquals("{}", ArrayUtils.toString(null));
        Assertions.assertEquals("{}", ArrayUtils.toString(new Object[0]));
        Assertions.assertEquals("{}", ArrayUtils.toString(new String[0]));
        Assertions.assertEquals("{<null>}", ArrayUtils.toString(new String[]{ null }));
        Assertions.assertEquals("{pink,blue}", ArrayUtils.toString(new String[]{ "pink", "blue" }));
        Assertions.assertEquals("<empty>", ArrayUtils.toString(null, "<empty>"));
        Assertions.assertEquals("{}", ArrayUtils.toString(new Object[0], "<empty>"));
        Assertions.assertEquals("{}", ArrayUtils.toString(new String[0], "<empty>"));
        Assertions.assertEquals("{<null>}", ArrayUtils.toString(new String[]{ null }, "<empty>"));
        Assertions.assertEquals("{pink,blue}", ArrayUtils.toString(new String[]{ "pink", "blue" }, "<empty>"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testHashCode() {
        final long[][] array1 = new long[][]{ new long[]{ 2, 5 }, new long[]{ 4, 5 } };
        final long[][] array2 = new long[][]{ new long[]{ 2, 5 }, new long[]{ 4, 6 } };
        Assertions.assertEquals(ArrayUtils.hashCode(array1), ArrayUtils.hashCode(array1));
        Assertions.assertNotEquals(ArrayUtils.hashCode(array1), ArrayUtils.hashCode(array2));
        final Object[] array3 = new Object[]{ new String(new char[]{ 'A', 'B' }) };
        final Object[] array4 = new Object[]{ "AB" };
        Assertions.assertEquals(ArrayUtils.hashCode(array3), ArrayUtils.hashCode(array3));
        Assertions.assertEquals(ArrayUtils.hashCode(array3), ArrayUtils.hashCode(array4));
        final Object[] arrayA = new Object[]{ new boolean[]{ true, false }, new int[]{ 6, 7 } };
        final Object[] arrayB = new Object[]{ new boolean[]{ true, false }, new int[]{ 6, 7 } };
        Assertions.assertEquals(ArrayUtils.hashCode(arrayB), ArrayUtils.hashCode(arrayA));
    }

    @Test
    public void testIsEquals() {
        final long[][] larray1 = new long[][]{ new long[]{ 2, 5 }, new long[]{ 4, 5 } };
        final long[][] larray2 = new long[][]{ new long[]{ 2, 5 }, new long[]{ 4, 6 } };
        final long[] larray3 = new long[]{ 2, 5 };
        this.assertIsEquals(larray1, larray2, larray3);
        final int[][] iarray1 = new int[][]{ new int[]{ 2, 5 }, new int[]{ 4, 5 } };
        final int[][] iarray2 = new int[][]{ new int[]{ 2, 5 }, new int[]{ 4, 6 } };
        final int[] iarray3 = new int[]{ 2, 5 };
        this.assertIsEquals(iarray1, iarray2, iarray3);
        final short[][] sarray1 = new short[][]{ new short[]{ 2, 5 }, new short[]{ 4, 5 } };
        final short[][] sarray2 = new short[][]{ new short[]{ 2, 5 }, new short[]{ 4, 6 } };
        final short[] sarray3 = new short[]{ 2, 5 };
        this.assertIsEquals(sarray1, sarray2, sarray3);
        final float[][] farray1 = new float[][]{ new float[]{ 2, 5 }, new float[]{ 4, 5 } };
        final float[][] farray2 = new float[][]{ new float[]{ 2, 5 }, new float[]{ 4, 6 } };
        final float[] farray3 = new float[]{ 2, 5 };
        this.assertIsEquals(farray1, farray2, farray3);
        final double[][] darray1 = new double[][]{ new double[]{ 2, 5 }, new double[]{ 4, 5 } };
        final double[][] darray2 = new double[][]{ new double[]{ 2, 5 }, new double[]{ 4, 6 } };
        final double[] darray3 = new double[]{ 2, 5 };
        this.assertIsEquals(darray1, darray2, darray3);
        final byte[][] byteArray1 = new byte[][]{ new byte[]{ 2, 5 }, new byte[]{ 4, 5 } };
        final byte[][] byteArray2 = new byte[][]{ new byte[]{ 2, 5 }, new byte[]{ 4, 6 } };
        final byte[] byteArray3 = new byte[]{ 2, 5 };
        this.assertIsEquals(byteArray1, byteArray2, byteArray3);
        final char[][] charArray1 = new char[][]{ new char[]{ 2, 5 }, new char[]{ 4, 5 } };
        final char[][] charArray2 = new char[][]{ new char[]{ 2, 5 }, new char[]{ 4, 6 } };
        final char[] charArray3 = new char[]{ 2, 5 };
        this.assertIsEquals(charArray1, charArray2, charArray3);
        final boolean[][] barray1 = new boolean[][]{ new boolean[]{ true, false }, new boolean[]{ true, true } };
        final boolean[][] barray2 = new boolean[][]{ new boolean[]{ true, false }, new boolean[]{ true, false } };
        final boolean[] barray3 = new boolean[]{ false, true };
        this.assertIsEquals(barray1, barray2, barray3);
        final Object[] array3 = new Object[]{ new String(new char[]{ 'A', 'B' }) };
        final Object[] array4 = new Object[]{ "AB" };
        Assertions.assertTrue(ArrayUtils.isEquals(array3, array3));
        Assertions.assertTrue(ArrayUtils.isEquals(array3, array4));
        Assertions.assertTrue(ArrayUtils.isEquals(null, null));
        Assertions.assertFalse(ArrayUtils.isEquals(null, array4));
    }

    // -----------------------------------------------------------------------
    /**
     * Tests generic array creation with parameters of same type.
     */
    @Test
    public void testArrayCreation() {
        final String[] array = ArrayUtils.toArray("foo", "bar");
        Assertions.assertEquals(2, array.length);
        Assertions.assertEquals("foo", array[0]);
        Assertions.assertEquals("bar", array[1]);
    }

    /**
     * Tests generic array creation with general return type.
     */
    @Test
    public void testArrayCreationWithGeneralReturnType() {
        final Object obj = ArrayUtils.toArray("foo", "bar");
        Assertions.assertTrue((obj instanceof String[]));
    }

    /**
     * Tests generic array creation with parameters of common base type.
     */
    @Test
    public void testArrayCreationWithDifferentTypes() {
        final Number[] array = ArrayUtils.<Number>toArray(Integer.valueOf(42), Double.valueOf(Math.PI));
        Assertions.assertEquals(2, array.length);
        Assertions.assertEquals(Integer.valueOf(42), array[0]);
        Assertions.assertEquals(Double.valueOf(Math.PI), array[1]);
    }

    /**
     * Tests generic array creation with generic type.
     */
    @Test
    public void testIndirectArrayCreation() {
        final String[] array = ArrayUtilsTest.toArrayPropagatingType("foo", "bar");
        Assertions.assertEquals(2, array.length);
        Assertions.assertEquals("foo", array[0]);
        Assertions.assertEquals("bar", array[1]);
    }

    /**
     * Tests generic empty array creation with generic type.
     */
    @Test
    public void testEmptyArrayCreation() {
        final String[] array = ArrayUtils.<String>toArray();
        Assertions.assertEquals(0, array.length);
    }

    /**
     * Tests indirect generic empty array creation with generic type.
     */
    @Test
    public void testIndirectEmptyArrayCreation() {
        final String[] array = ArrayUtilsTest.<String>toArrayPropagatingType();
        Assertions.assertEquals(0, array.length);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToMap() {
        Map<?, ?> map = ArrayUtils.toMap(new String[][]{ new String[]{ "foo", "bar" }, new String[]{ "hello", "world" } });
        Assertions.assertEquals("bar", map.get("foo"));
        Assertions.assertEquals("world", map.get("hello"));
        Assertions.assertNull(ArrayUtils.toMap(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.toMap(new String[][]{ new String[]{ "foo", "bar" }, new String[]{ "short" } }));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.toMap(new Object[]{ new Object[]{ "foo", "bar" }, "illegal type" }));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.toMap(new Object[]{ new Object[]{ "foo", "bar" }, null }));
        map = ArrayUtils.toMap(new Object[]{ new Map.Entry<Object, Object>() {
            @Override
            public Object getKey() {
                return "foo";
            }

            @Override
            public Object getValue() {
                return "bar";
            }

            @Override
            public Object setValue(final Object value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(final Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int hashCode() {
                throw new UnsupportedOperationException();
            }
        } });
        Assertions.assertEquals("bar", map.get("foo"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testClone() {
        Assertions.assertArrayEquals(null, ArrayUtils.clone(((Object[]) (null))));
        Object[] original1 = new Object[0];
        Object[] cloned1 = ArrayUtils.clone(original1);
        Assertions.assertArrayEquals(original1, cloned1);
        Assertions.assertNotSame(original1, cloned1);
        final StringBuilder builder = new StringBuilder("pick");
        original1 = new Object[]{ builder, "a", new String[]{ "stick" } };
        cloned1 = ArrayUtils.clone(original1);
        Assertions.assertArrayEquals(original1, cloned1);
        Assertions.assertNotSame(original1, cloned1);
        Assertions.assertSame(original1[0], cloned1[0]);
        Assertions.assertSame(original1[1], cloned1[1]);
        Assertions.assertSame(original1[2], cloned1[2]);
    }

    @Test
    public void testCloneBoolean() {
        Assertions.assertNull(ArrayUtils.clone(((boolean[]) (null))));
        final boolean[] original = new boolean[]{ true, false };
        final boolean[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneLong() {
        Assertions.assertNull(ArrayUtils.clone(((long[]) (null))));
        final long[] original = new long[]{ 0L, 1L };
        final long[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneInt() {
        Assertions.assertNull(ArrayUtils.clone(((int[]) (null))));
        final int[] original = new int[]{ 5, 8 };
        final int[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneShort() {
        Assertions.assertNull(ArrayUtils.clone(((short[]) (null))));
        final short[] original = new short[]{ 1, 4 };
        final short[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneChar() {
        Assertions.assertNull(ArrayUtils.clone(((char[]) (null))));
        final char[] original = new char[]{ 'a', '4' };
        final char[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneByte() {
        Assertions.assertNull(ArrayUtils.clone(((byte[]) (null))));
        final byte[] original = new byte[]{ 1, 6 };
        final byte[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneDouble() {
        Assertions.assertNull(ArrayUtils.clone(((double[]) (null))));
        final double[] original = new double[]{ 2.4, 5.7 };
        final double[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    @Test
    public void testCloneFloat() {
        Assertions.assertNull(ArrayUtils.clone(((float[]) (null))));
        final float[] original = new float[]{ 2.6F, 6.4F };
        final float[] cloned = ArrayUtils.clone(original);
        Assertions.assertArrayEquals(original, cloned);
        Assertions.assertNotSame(original, cloned);
    }

    // -----------------------------------------------------------------------
    private class TestClass {}

    @Test
    public void testNullToEmptyGenericNull() {
        final ArrayUtilsTest.TestClass[] output = ArrayUtils.nullToEmpty(null, ArrayUtilsTest.TestClass[].class);
        Assertions.assertNotNull(output);
        Assertions.assertEquals(0, output.length);
    }

    @Test
    public void testNullToEmptyGenericEmpty() {
        final ArrayUtilsTest.TestClass[] input = new ArrayUtilsTest.TestClass[]{  };
        final ArrayUtilsTest.TestClass[] output = ArrayUtils.nullToEmpty(input, ArrayUtilsTest.TestClass[].class);
        Assertions.assertSame(input, output);
    }

    @Test
    public void testNullToEmptyGeneric() {
        final ArrayUtilsTest.TestClass[] input = new ArrayUtilsTest.TestClass[]{ new ArrayUtilsTest.TestClass(), new ArrayUtilsTest.TestClass() };
        final ArrayUtilsTest.TestClass[] output = ArrayUtils.nullToEmpty(input, ArrayUtilsTest.TestClass[].class);
        Assertions.assertSame(input, output);
    }

    @Test
    public void testNullToEmptyGenericNullType() {
        final ArrayUtilsTest.TestClass[] input = new ArrayUtilsTest.TestClass[]{  };
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.nullToEmpty(input, null));
    }

    @Test
    public void testNullToEmptyBooleanNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.nullToEmpty(((boolean[]) (null))));
    }

    @Test
    public void testNullToEmptyBooleanEmptyArray() {
        final boolean[] empty = new boolean[]{  };
        final boolean[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyBoolean() {
        final boolean[] original = new boolean[]{ true, false };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyLongNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.nullToEmpty(((long[]) (null))));
    }

    @Test
    public void testNullToEmptyLongEmptyArray() {
        final long[] empty = new long[]{  };
        final long[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_LONG_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyLong() {
        final long[] original = new long[]{ 1L, 2L };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyIntNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.nullToEmpty(((int[]) (null))));
    }

    @Test
    public void testNullToEmptyIntEmptyArray() {
        final int[] empty = new int[]{  };
        final int[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_INT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyInt() {
        final int[] original = new int[]{ 1, 2 };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyShortNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.nullToEmpty(((short[]) (null))));
    }

    @Test
    public void testNullToEmptyShortEmptyArray() {
        final short[] empty = new short[]{  };
        final short[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_SHORT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyShort() {
        final short[] original = new short[]{ 1, 2 };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyCharNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.nullToEmpty(((char[]) (null))));
    }

    @Test
    public void testNullToEmptyCharEmptyArray() {
        final char[] empty = new char[]{  };
        final char[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyChar() {
        final char[] original = new char[]{ 'a', 'b' };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyByteNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.nullToEmpty(((byte[]) (null))));
    }

    @Test
    public void testNullToEmptyByteEmptyArray() {
        final byte[] empty = new byte[]{  };
        final byte[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_BYTE_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyByte() {
        final byte[] original = new byte[]{ 15, 14 };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyDoubleNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.nullToEmpty(((double[]) (null))));
    }

    @Test
    public void testNullToEmptyDoubleEmptyArray() {
        final double[] empty = new double[]{  };
        final double[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyDouble() {
        final double[] original = new double[]{ 1L, 2L };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyFloatNull() {
        Assertions.assertEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.nullToEmpty(((float[]) (null))));
    }

    @Test
    public void testNullToEmptyFloatEmptyArray() {
        final float[] empty = new float[]{  };
        final float[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyFloat() {
        final float[] original = new float[]{ 2.6F, 3.8F };
        Assertions.assertEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Object[]) (null))));
    }

    @Test
    public void testNullToEmptyObjectEmptyArray() {
        final Object[] empty = new Object[]{  };
        final Object[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyObject() {
        final Object[] original = new Object[]{ Boolean.TRUE, Boolean.FALSE };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyClassNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.nullToEmpty(((Class<?>[]) (null))));
    }

    @Test
    public void testNullToEmptyClassEmptyArray() {
        final Class<?>[] empty = new Class<?>[]{  };
        final Class<?>[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CLASS_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyClass() {
        final Class<?>[] original = new Class<?>[]{ Object.class, String.class };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyStringNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, ArrayUtils.nullToEmpty(((String[]) (null))));
    }

    @Test
    public void testNullToEmptyStringEmptyArray() {
        final String[] empty = new String[]{  };
        final String[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyString() {
        final String[] original = new String[]{ "abc", "def" };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyBooleanObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Boolean[]) (null))));
    }

    @Test
    public void testNullToEmptyBooleanObjectEmptyArray() {
        final Boolean[] empty = new Boolean[]{  };
        final Boolean[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyBooleanObject() {
        final Boolean[] original = new Boolean[]{ Boolean.TRUE, Boolean.FALSE };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyLongObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Long[]) (null))));
    }

    @Test
    public void testNullToEmptyLongObjectEmptyArray() {
        final Long[] empty = new Long[]{  };
        final Long[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyLongObject() {
        @SuppressWarnings("boxing")
        final Long[] original = new Long[]{ 1L, 2L };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyIntObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Integer[]) (null))));
    }

    @Test
    public void testNullToEmptyIntObjectEmptyArray() {
        final Integer[] empty = new Integer[]{  };
        final Integer[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyIntObject() {
        final Integer[] original = new Integer[]{ 1, 2 };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyShortObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Short[]) (null))));
    }

    @Test
    public void testNullToEmptyShortObjectEmptyArray() {
        final Short[] empty = new Short[]{  };
        final Short[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyShortObject() {
        @SuppressWarnings("boxing")
        final Short[] original = new Short[]{ 1, 2 };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNUllToEmptyCharObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Character[]) (null))));
    }

    @Test
    public void testNullToEmptyCharObjectEmptyArray() {
        final Character[] empty = new Character[]{  };
        final Character[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyCharObject() {
        final Character[] original = new Character[]{ 'a', 'b' };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyByteObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Byte[]) (null))));
    }

    @Test
    public void testNullToEmptyByteObjectEmptyArray() {
        final Byte[] empty = new Byte[]{  };
        final Byte[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyByteObject() {
        final Byte[] original = new Byte[]{ 15, 14 };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyDoubleObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Double[]) (null))));
    }

    @Test
    public void testNullToEmptyDoubleObjectEmptyArray() {
        final Double[] empty = new Double[]{  };
        final Double[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyDoubleObject() {
        final Double[] original = new Double[]{ 1.0, 2.0 };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    @Test
    public void testNullToEmptyFloatObjectNull() {
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY, ArrayUtils.nullToEmpty(((Float[]) (null))));
    }

    @Test
    public void testNullToEmptyFloatObjectEmptyArray() {
        final Float[] empty = new Float[]{  };
        final Float[] result = ArrayUtils.nullToEmpty(empty);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY, result);
        Assertions.assertNotSame(empty, result);
    }

    @Test
    public void testNullToEmptyFloatObject() {
        final Float[] original = new Float[]{ 2.6F, 3.8F };
        Assertions.assertArrayEquals(original, ArrayUtils.nullToEmpty(original));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSubarrayObject() {
        final Object[] nullArray = null;
        final Object[] objectArray = new Object[]{ "a", "b", "c", "d", "e", "f" };
        Assertions.assertEquals("abcd", StringUtils.join(ArrayUtils.subarray(objectArray, 0, 4)), "0 start, mid end");
        Assertions.assertEquals("abcdef", StringUtils.join(ArrayUtils.subarray(objectArray, 0, objectArray.length)), "0 start, length end");
        Assertions.assertEquals("bcd", StringUtils.join(ArrayUtils.subarray(objectArray, 1, 4)), "mid start, mid end");
        Assertions.assertEquals("bcdef", StringUtils.join(ArrayUtils.subarray(objectArray, 1, objectArray.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals("", StringUtils.join(ArrayUtils.subarray(ArrayUtils.EMPTY_OBJECT_ARRAY, 1, 2)), "empty array");
        Assertions.assertEquals("", StringUtils.join(ArrayUtils.subarray(objectArray, 4, 2)), "start > end");
        Assertions.assertEquals("", StringUtils.join(ArrayUtils.subarray(objectArray, 3, 3)), "start == end");
        Assertions.assertEquals("abcd", StringUtils.join(ArrayUtils.subarray(objectArray, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals("", StringUtils.join(ArrayUtils.subarray(objectArray, 33, 4)), "start overshoot, any end");
        Assertions.assertEquals("cdef", StringUtils.join(ArrayUtils.subarray(objectArray, 2, 33)), "normal start, end overshoot");
        Assertions.assertEquals("abcdef", StringUtils.join(ArrayUtils.subarray(objectArray, (-2), 12)), "start undershoot, end overshoot");
        // array type tests
        final Date[] dateArray = new Date[]{ new java.sql.Date(new Date().getTime()), new Date(), new Date(), new Date(), new Date() };
        Assertions.assertSame(Object.class, ArrayUtils.subarray(objectArray, 2, 4).getClass().getComponentType(), "Object type");
        Assertions.assertSame(Date.class, ArrayUtils.subarray(dateArray, 1, 4).getClass().getComponentType(), "java.util.Date type");
        Assertions.assertNotSame(java.sql.Date.class, ArrayUtils.subarray(dateArray, 1, 4).getClass().getComponentType(), "java.sql.Date type");
        Assertions.assertThrows(ClassCastException.class, () -> java.sql.Date[].class.cast(ArrayUtils.subarray(dateArray, 1, 3)), "Invalid downcast");
    }

    @Test
    public void testSubarrayLong() {
        final long[] nullArray = null;
        final long[] array = new long[]{ 999910, 999911, 999912, 999913, 999914, 999915 };
        final long[] leftSubarray = new long[]{ 999910, 999911, 999912, 999913 };
        final long[] midSubarray = new long[]{ 999911, 999912, 999913, 999914 };
        final long[] rightSubarray = new long[]{ 999912, 999913, 999914, 999915 };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_LONG_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_LONG_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(long.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "long type");
    }

    @Test
    public void testSubarrayInt() {
        final int[] nullArray = null;
        final int[] array = new int[]{ 10, 11, 12, 13, 14, 15 };
        final int[] leftSubarray = new int[]{ 10, 11, 12, 13 };
        final int[] midSubarray = new int[]{ 11, 12, 13, 14 };
        final int[] rightSubarray = new int[]{ 12, 13, 14, 15 };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_INT_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_INT_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(int.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "int type");
    }

    @Test
    public void testSubarrayShort() {
        final short[] nullArray = null;
        final short[] array = new short[]{ 10, 11, 12, 13, 14, 15 };
        final short[] leftSubarray = new short[]{ 10, 11, 12, 13 };
        final short[] midSubarray = new short[]{ 11, 12, 13, 14 };
        final short[] rightSubarray = new short[]{ 12, 13, 14, 15 };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_SHORT_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_SHORT_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(short.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "short type");
    }

    @Test
    public void testSubarrChar() {
        final char[] nullArray = null;
        final char[] array = new char[]{ 'a', 'b', 'c', 'd', 'e', 'f' };
        final char[] leftSubarray = new char[]{ 'a', 'b', 'c', 'd' };
        final char[] midSubarray = new char[]{ 'b', 'c', 'd', 'e' };
        final char[] rightSubarray = new char[]{ 'c', 'd', 'e', 'f' };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_CHAR_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_CHAR_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(char.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "char type");
    }

    @Test
    public void testSubarrayByte() {
        final byte[] nullArray = null;
        final byte[] array = new byte[]{ 10, 11, 12, 13, 14, 15 };
        final byte[] leftSubarray = new byte[]{ 10, 11, 12, 13 };
        final byte[] midSubarray = new byte[]{ 11, 12, 13, 14 };
        final byte[] rightSubarray = new byte[]{ 12, 13, 14, 15 };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_BYTE_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_BYTE_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(byte.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "byte type");
    }

    @Test
    public void testSubarrayDouble() {
        final double[] nullArray = null;
        final double[] array = new double[]{ 10.123, 11.234, 12.345, 13.456, 14.567, 15.678 };
        final double[] leftSubarray = new double[]{ 10.123, 11.234, 12.345, 13.456 };
        final double[] midSubarray = new double[]{ 11.234, 12.345, 13.456, 14.567 };
        final double[] rightSubarray = new double[]{ 12.345, 13.456, 14.567, 15.678 };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_DOUBLE_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_DOUBLE_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(double.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "double type");
    }

    @Test
    public void testSubarrayFloat() {
        final float[] nullArray = null;
        final float[] array = new float[]{ 10, 11, 12, 13, 14, 15 };
        final float[] leftSubarray = new float[]{ 10, 11, 12, 13 };
        final float[] midSubarray = new float[]{ 11, 12, 13, 14 };
        final float[] rightSubarray = new float[]{ 12, 13, 14, 15 };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_FLOAT_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_FLOAT_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(float.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "float type");
    }

    @Test
    public void testSubarrayBoolean() {
        final boolean[] nullArray = null;
        final boolean[] array = new boolean[]{ true, true, false, true, false, true };
        final boolean[] leftSubarray = new boolean[]{ true, true, false, true };
        final boolean[] midSubarray = new boolean[]{ true, false, true, false };
        final boolean[] rightSubarray = new boolean[]{ false, true, false, true };
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, 0, 4)), "0 start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, 0, array.length)), "0 start, length end");
        Assertions.assertTrue(ArrayUtils.isEquals(midSubarray, ArrayUtils.subarray(array, 1, 5)), "mid start, mid end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, array.length)), "mid start, length end");
        Assertions.assertNull(ArrayUtils.subarray(nullArray, 0, 3), "null input");
        Assertions.assertEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_BOOLEAN_ARRAY, 1, 2), "empty array");
        Assertions.assertEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(array, 4, 2), "start > end");
        Assertions.assertEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end");
        Assertions.assertTrue(ArrayUtils.isEquals(leftSubarray, ArrayUtils.subarray(array, (-2), 4)), "start undershoot, normal end");
        Assertions.assertEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(array, 33, 4), "start overshoot, any end");
        Assertions.assertTrue(ArrayUtils.isEquals(rightSubarray, ArrayUtils.subarray(array, 2, 33)), "normal start, end overshoot");
        Assertions.assertTrue(ArrayUtils.isEquals(array, ArrayUtils.subarray(array, (-2), 12)), "start undershoot, end overshoot");
        // empty-return tests
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(ArrayUtils.EMPTY_BOOLEAN_ARRAY, 1, 2), "empty array, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(array, 4, 1), "start > end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(array, 3, 3), "start == end, object test");
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.subarray(array, 8733, 4), "start overshoot, any end, object test");
        // array type tests
        Assertions.assertSame(boolean.class, ArrayUtils.subarray(array, 2, 4).getClass().getComponentType(), "boolean type");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSameLength() {
        final Object[] nullArray = null;
        final Object[] emptyArray = new Object[0];
        final Object[] oneArray = new Object[]{ "pick" };
        final Object[] twoArray = new Object[]{ "pick", "stick" };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthBoolean() {
        final boolean[] nullArray = null;
        final boolean[] emptyArray = new boolean[0];
        final boolean[] oneArray = new boolean[]{ true };
        final boolean[] twoArray = new boolean[]{ true, false };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthLong() {
        final long[] nullArray = null;
        final long[] emptyArray = new long[0];
        final long[] oneArray = new long[]{ 0L };
        final long[] twoArray = new long[]{ 0L, 76L };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthInt() {
        final int[] nullArray = null;
        final int[] emptyArray = new int[0];
        final int[] oneArray = new int[]{ 4 };
        final int[] twoArray = new int[]{ 5, 7 };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthShort() {
        final short[] nullArray = null;
        final short[] emptyArray = new short[0];
        final short[] oneArray = new short[]{ 4 };
        final short[] twoArray = new short[]{ 6, 8 };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthChar() {
        final char[] nullArray = null;
        final char[] emptyArray = new char[0];
        final char[] oneArray = new char[]{ 'f' };
        final char[] twoArray = new char[]{ 'd', 't' };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthByte() {
        final byte[] nullArray = null;
        final byte[] emptyArray = new byte[0];
        final byte[] oneArray = new byte[]{ 3 };
        final byte[] twoArray = new byte[]{ 4, 6 };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthDouble() {
        final double[] nullArray = null;
        final double[] emptyArray = new double[0];
        final double[] oneArray = new double[]{ 1.3 };
        final double[] twoArray = new double[]{ 4.5, 6.3 };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    @Test
    public void testSameLengthFloat() {
        final float[] nullArray = null;
        final float[] emptyArray = new float[0];
        final float[] oneArray = new float[]{ 2.5F };
        final float[] twoArray = new float[]{ 6.4F, 5.8F };
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(nullArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(nullArray, twoArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, nullArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(emptyArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(emptyArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, emptyArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(oneArray, oneArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(oneArray, twoArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, nullArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, emptyArray));
        Assertions.assertFalse(ArrayUtils.isSameLength(twoArray, oneArray));
        Assertions.assertTrue(ArrayUtils.isSameLength(twoArray, twoArray));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSameType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.isSameType(null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.isSameType(null, new Object[0]));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.isSameType(new Object[0], null));
        Assertions.assertTrue(ArrayUtils.isSameType(new Object[0], new Object[0]));
        Assertions.assertFalse(ArrayUtils.isSameType(new String[0], new Object[0]));
        Assertions.assertTrue(ArrayUtils.isSameType(new String[0][0], new String[0][0]));
        Assertions.assertFalse(ArrayUtils.isSameType(new String[0], new String[0][0]));
        Assertions.assertFalse(ArrayUtils.isSameType(new String[0][0], new String[0]));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReverse() {
        final StringBuffer str1 = new StringBuffer("pick");
        final String str2 = "a";
        final String[] str3 = new String[]{ "stick" };
        final String str4 = "up";
        Object[] array = new Object[]{ str1, str2, str3 };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], str3);
        Assertions.assertEquals(array[1], str2);
        Assertions.assertEquals(array[2], str1);
        array = new Object[]{ str1, str2, str3, str4 };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], str4);
        Assertions.assertEquals(array[1], str3);
        Assertions.assertEquals(array[2], str2);
        Assertions.assertEquals(array[3], str1);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertArrayEquals(null, array);
    }

    @Test
    public void testReverseLong() {
        long[] array = new long[]{ 1L, 2L, 3L };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], 3L);
        Assertions.assertEquals(array[1], 2L);
        Assertions.assertEquals(array[2], 1L);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseInt() {
        int[] array = new int[]{ 1, 2, 3 };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], 3);
        Assertions.assertEquals(array[1], 2);
        Assertions.assertEquals(array[2], 1);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseShort() {
        short[] array = new short[]{ 1, 2, 3 };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], 3);
        Assertions.assertEquals(array[1], 2);
        Assertions.assertEquals(array[2], 1);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseChar() {
        char[] array = new char[]{ 'a', 'f', 'C' };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], 'C');
        Assertions.assertEquals(array[1], 'f');
        Assertions.assertEquals(array[2], 'a');
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseByte() {
        byte[] array = new byte[]{ 2, 3, 4 };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(array[0], 4);
        Assertions.assertEquals(array[1], 3);
        Assertions.assertEquals(array[2], 2);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseDouble() {
        double[] array = new double[]{ 0.3, 0.4, 0.5 };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(0.5, array[0]);
        Assertions.assertEquals(0.4, array[1]);
        Assertions.assertEquals(0.3, array[2]);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseFloat() {
        float[] array = new float[]{ 0.3F, 0.4F, 0.5F };
        ArrayUtils.reverse(array);
        Assertions.assertEquals(0.5F, array[0]);
        Assertions.assertEquals(0.4F, array[1]);
        Assertions.assertEquals(0.3F, array[2]);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseBoolean() {
        boolean[] array = new boolean[]{ false, false, true };
        ArrayUtils.reverse(array);
        Assertions.assertTrue(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertFalse(array[2]);
        array = null;
        ArrayUtils.reverse(array);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseBooleanRange() {
        boolean[] array = new boolean[]{ false, false, true };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertTrue(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertFalse(array[2]);
        // a range
        array = new boolean[]{ false, false, true };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertFalse(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertTrue(array[2]);
        // a range with a negative start
        array = new boolean[]{ false, false, true };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertTrue(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertFalse(array[2]);
        // a range with a large stop index
        array = new boolean[]{ false, false, true };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertTrue(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertFalse(array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseByteRange() {
        byte[] array = new byte[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseCharRange() {
        char[] array = new char[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseDoubleRange() {
        double[] array = new double[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseFloatRange() {
        float[] array = new float[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseIntRange() {
        int[] array = new int[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseLongRange() {
        long[] array = new long[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseShortRange() {
        short[] array = new short[]{ 1, 2, 3 };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals(2, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(3, array[2]);
        // a range with a negative start
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // a range with a large stop index
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    @Test
    public void testReverseObjectRange() {
        String[] array = new String[]{ "1", "2", "3" };
        // The whole array
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("1", array[2]);
        // a range
        array = new String[]{ "1", "2", "3" };
        ArrayUtils.reverse(array, 0, 2);
        Assertions.assertEquals("2", array[0]);
        Assertions.assertEquals("1", array[1]);
        Assertions.assertEquals("3", array[2]);
        // a range with a negative start
        array = new String[]{ "1", "2", "3" };
        ArrayUtils.reverse(array, (-1), 3);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("1", array[2]);
        // a range with a large stop index
        array = new String[]{ "1", "2", "3" };
        ArrayUtils.reverse(array, (-1), ((array.length) + 1000));
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("1", array[2]);
        // null
        array = null;
        ArrayUtils.reverse(array, 0, 3);
        Assertions.assertNull(array);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSwapChar() {
        char[] array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertArrayEquals(new char[]{ 3, 2, 1 }, array);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 0);
        Assertions.assertArrayEquals(new char[]{ 1, 2, 3 }, array);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 1, 0);
        Assertions.assertArrayEquals(new char[]{ 2, 1, 3 }, array);
    }

    @Test
    public void testSwapCharRange() {
        char[] array = new char[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 3);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new char[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapByte() {
        final byte[] array = new byte[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
    }

    @Test
    public void testSwapNullByteArray() {
        final byte[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyByteArray() {
        final byte[] array = new byte[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapByteRange() {
        byte[] array = new byte[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 3);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new byte[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapFloat() {
        final float[] array = new float[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
    }

    @Test
    public void testSwapNullFloatArray() {
        final float[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyFloatArray() {
        final float[] array = new float[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapFloatRange() {
        float[] array = new float[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 3);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new float[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapDouble() {
        final double[] array = new double[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
    }

    @Test
    public void testSwapNullDoubleArray() {
        final double[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyDoubleArray() {
        final double[] array = new double[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapDoubleRange() {
        double[] array = new double[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 3);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new double[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapInt() {
        final int[] array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
    }

    @Test
    public void testSwapNullIntArray() {
        final int[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyIntArray() {
        final int[] array = new int[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapIntRange() {
        int[] array = new int[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 3, 0);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapIntExchangedOffsets() {
        int[] array;
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 1, 2);
        Assertions.assertArrayEquals(new int[]{ 2, 3, 1 }, array);
        array = new int[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 1, 0, 2);
        Assertions.assertArrayEquals(new int[]{ 2, 3, 1 }, array);
    }

    @Test
    public void testSwapShort() {
        final short[] array = new short[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
    }

    @Test
    public void testSwapNullShortArray() {
        final short[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyShortArray() {
        final short[] array = new short[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapShortRange() {
        short[] array = new short[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 3, 0);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new short[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapNullCharArray() {
        final char[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyCharArray() {
        final char[] array = new char[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapLong() {
        final long[] array = new long[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
    }

    @Test
    public void testSwapNullLongArray() {
        final long[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyLongArray() {
        final long[] array = new long[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapLongRange() {
        long[] array = new long[]{ 1, 2, 3, 4 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 3);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(1, array[2]);
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        array = new long[]{ 1, 2, 3 };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
    }

    @Test
    public void testSwapBoolean() {
        final boolean[] array = new boolean[]{ true, false, false };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertFalse(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertTrue(array[2]);
    }

    @Test
    public void testSwapNullBooleanArray() {
        final boolean[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyBooleanArray() {
        final boolean[] array = new boolean[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapBooleanRange() {
        boolean[] array = new boolean[]{ false, false, true, true };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertTrue(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertFalse(array[2]);
        Assertions.assertFalse(array[3]);
        array = new boolean[]{ false, true, false };
        ArrayUtils.swap(array, 0, 3);
        Assertions.assertFalse(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertFalse(array[2]);
        array = new boolean[]{ true, true, false };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertFalse(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertTrue(array[2]);
        array = new boolean[]{ true, true, false };
        ArrayUtils.swap(array, (-1), 2, 2);
        Assertions.assertFalse(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertTrue(array[2]);
        array = new boolean[]{ true, true, false };
        ArrayUtils.swap(array, 0, (-1), 2);
        Assertions.assertTrue(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertFalse(array[2]);
        array = new boolean[]{ true, true, false };
        ArrayUtils.swap(array, (-1), (-1), 2);
        Assertions.assertTrue(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertFalse(array[2]);
    }

    @Test
    public void testSwapObject() {
        final String[] array = new String[]{ "1", "2", "3" };
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("1", array[2]);
    }

    @Test
    public void testSwapNullObjectArray() {
        final String[] array = null;
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertNull(array);
    }

    @Test
    public void testSwapEmptyObjectArray() {
        final String[] array = new String[0];
        ArrayUtils.swap(array, 0, 2);
        Assertions.assertEquals(0, array.length);
    }

    @Test
    public void testSwapObjectRange() {
        String[] array = new String[]{ "1", "2", "3", "4" };
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("4", array[1]);
        Assertions.assertEquals("1", array[2]);
        Assertions.assertEquals("2", array[3]);
        array = new String[]{ "1", "2", "3", "4" };
        ArrayUtils.swap(array, (-1), 2, 3);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("4", array[1]);
        Assertions.assertEquals("1", array[2]);
        Assertions.assertEquals("2", array[3]);
        array = new String[]{ "1", "2", "3", "4", "5" };
        ArrayUtils.swap(array, (-3), 2, 3);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("4", array[1]);
        Assertions.assertEquals("5", array[2]);
        Assertions.assertEquals("2", array[3]);
        Assertions.assertEquals("1", array[4]);
        array = new String[]{ "1", "2", "3", "4", "5" };
        ArrayUtils.swap(array, 2, (-2), 3);
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("4", array[1]);
        Assertions.assertEquals("5", array[2]);
        Assertions.assertEquals("2", array[3]);
        Assertions.assertEquals("1", array[4]);
        array = new String[0];
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertEquals(0, array.length);
        array = null;
        ArrayUtils.swap(array, 0, 2, 2);
        Assertions.assertNull(array);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testShiftDouble() {
        final double[] array = new double[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
    }

    @Test
    public void testShiftRangeDouble() {
        final double[] array = new double[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemDouble() {
        final double[] array = new double[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullDouble() {
        final double[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftNullDouble() {
        final double[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllDouble() {
        final double[] array = new double[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftFloat() {
        final float[] array = new float[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
    }

    @Test
    public void testShiftRangeFloat() {
        final float[] array = new float[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemFloat() {
        final float[] array = new float[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullFloat() {
        final float[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftNullFloat() {
        final float[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllFloat() {
        final float[] array = new float[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftShort() {
        short[] array = new short[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        array = new short[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 2);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(5, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
        Assertions.assertEquals(3, array[4]);
    }

    @Test
    public void testShiftNullShort() {
        final short[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftRangeShort() {
        final short[] array = new short[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemShort() {
        final short[] array = new short[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullShort() {
        final short[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllShort() {
        final short[] array = new short[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftByte() {
        final byte[] array = new byte[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
    }

    @Test
    public void testShiftRangeByte() {
        final byte[] array = new byte[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemByte() {
        final byte[] array = new byte[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullByte() {
        final byte[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllByte() {
        final byte[] array = new byte[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftChar() {
        final char[] array = new char[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
    }

    @Test
    public void testShiftRangeChar() {
        final char[] array = new char[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemChar() {
        final char[] array = new char[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullChar() {
        final char[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllChar() {
        final char[] array = new char[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftLong() {
        final long[] array = new long[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
    }

    @Test
    public void testShiftRangeLong() {
        final long[] array = new long[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemLong() {
        final long[] array = new long[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullLong() {
        final long[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftNullLong() {
        final long[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllLong() {
        final long[] array = new long[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftInt() {
        final int[] array = new int[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals(4, array[0]);
        Assertions.assertEquals(1, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(3, array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals(3, array[0]);
        Assertions.assertEquals(4, array[1]);
        Assertions.assertEquals(1, array[2]);
        Assertions.assertEquals(2, array[3]);
    }

    @Test
    public void testShiftNullInt() {
        final int[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftRangeInt() {
        final int[] array = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(3, array[1]);
        Assertions.assertEquals(2, array[2]);
        Assertions.assertEquals(4, array[3]);
        Assertions.assertEquals(5, array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(4, array[2]);
        Assertions.assertEquals(3, array[3]);
        Assertions.assertEquals(5, array[4]);
    }

    @Test
    public void testShiftRangeNoElemInt() {
        final int[] array = new int[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftRangeNullInt() {
        final int[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllInt() {
        final int[] array = new int[]{ 1, 2, 3, 4 };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals(1, array[0]);
        Assertions.assertEquals(2, array[1]);
        Assertions.assertEquals(3, array[2]);
        Assertions.assertEquals(4, array[3]);
    }

    @Test
    public void testShiftObject() {
        final String[] array = new String[]{ "1", "2", "3", "4" };
        ArrayUtils.shift(array, 1);
        Assertions.assertEquals("4", array[0]);
        Assertions.assertEquals("1", array[1]);
        Assertions.assertEquals("2", array[2]);
        Assertions.assertEquals("3", array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("3", array[2]);
        Assertions.assertEquals("4", array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertEquals("4", array[0]);
        Assertions.assertEquals("1", array[1]);
        Assertions.assertEquals("2", array[2]);
        Assertions.assertEquals("3", array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertEquals("3", array[0]);
        Assertions.assertEquals("4", array[1]);
        Assertions.assertEquals("1", array[2]);
        Assertions.assertEquals("2", array[3]);
    }

    @Test
    public void testShiftNullObject() {
        final String[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftRangeObject() {
        final String[] array = new String[]{ "1", "2", "3", "4", "5" };
        ArrayUtils.shift(array, 1, 3, 1);
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("3", array[1]);
        Assertions.assertEquals("2", array[2]);
        Assertions.assertEquals("4", array[3]);
        Assertions.assertEquals("5", array[4]);
        ArrayUtils.shift(array, 1, 4, 2);
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("4", array[2]);
        Assertions.assertEquals("3", array[3]);
        Assertions.assertEquals("5", array[4]);
    }

    @Test
    public void testShiftRangeNoElemObject() {
        final String[] array = new String[]{ "1", "2", "3", "4" };
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("3", array[2]);
        Assertions.assertEquals("4", array[3]);
    }

    @Test
    public void testShiftRangeNullObject() {
        final String[] array = null;
        ArrayUtils.shift(array, 1, 1, 1);
        Assertions.assertNull(array);
    }

    @Test
    public void testShiftAllObject() {
        final String[] array = new String[]{ "1", "2", "3", "4" };
        ArrayUtils.shift(array, 4);
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("3", array[2]);
        Assertions.assertEquals("4", array[3]);
        ArrayUtils.shift(array, (-4));
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("3", array[2]);
        Assertions.assertEquals("4", array[3]);
    }

    @Test
    public void testShiftBoolean() {
        final boolean[] array = new boolean[]{ true, true, false, false };
        ArrayUtils.shift(array, 1);
        Assertions.assertFalse(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertTrue(array[2]);
        Assertions.assertFalse(array[3]);
        ArrayUtils.shift(array, (-1));
        Assertions.assertTrue(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertFalse(array[2]);
        Assertions.assertFalse(array[3]);
        ArrayUtils.shift(array, 5);
        Assertions.assertFalse(array[0]);
        Assertions.assertTrue(array[1]);
        Assertions.assertTrue(array[2]);
        Assertions.assertFalse(array[3]);
        ArrayUtils.shift(array, (-3));
        Assertions.assertFalse(array[0]);
        Assertions.assertFalse(array[1]);
        Assertions.assertTrue(array[2]);
        Assertions.assertTrue(array[3]);
    }

    @Test
    public void testShiftNullBoolean() {
        final boolean[] array = null;
        ArrayUtils.shift(array, 1);
        Assertions.assertNull(array);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOf() {
        final Object[] array = new Object[]{ "0", "1", "2", "3", null, "0" };
        Assertions.assertEquals((-1), ArrayUtils.indexOf(null, null));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(null, "0"));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(new Object[0], "0"));
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, "0"));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, "1"));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, "2"));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, "3"));
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, null));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, "notInArray"));
    }

    @Test
    public void testIndexOfWithStartIndex() {
        final Object[] array = new Object[]{ "0", "1", "2", "3", null, "0" };
        Assertions.assertEquals((-1), ArrayUtils.indexOf(null, null, 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(new Object[0], "0", 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(null, "0", 2));
        Assertions.assertEquals(5, ArrayUtils.indexOf(array, "0", 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, "1", 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, "2", 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, "3", 2));
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, null, 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, "notInArray", 2));
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, null, (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, null, 8));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, "0", 8));
    }

    @Test
    public void testLastIndexOf() {
        final Object[] array = new Object[]{ "0", "1", "2", "3", null, "0" };
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(null, null));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(null, "0"));
        Assertions.assertEquals(5, ArrayUtils.lastIndexOf(array, "0"));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, "1"));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, "2"));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, "3"));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, null));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, "notInArray"));
    }

    @Test
    public void testLastIndexOfWithStartIndex() {
        final Object[] array = new Object[]{ "0", "1", "2", "3", null, "0" };
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(null, null, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(null, "0", 2));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, "0", 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, "1", 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, "2", 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, "3", 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, "3", (-1)));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, null, 5));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, null, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, "notInArray", 5));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, null, (-1)));
        Assertions.assertEquals(5, ArrayUtils.lastIndexOf(array, "0", 88));
    }

    @Test
    public void testContains() {
        final Object[] array = new Object[]{ "0", "1", "2", "3", null, "0" };
        Assertions.assertFalse(ArrayUtils.contains(null, null));
        Assertions.assertFalse(ArrayUtils.contains(null, "1"));
        Assertions.assertTrue(ArrayUtils.contains(array, "0"));
        Assertions.assertTrue(ArrayUtils.contains(array, "1"));
        Assertions.assertTrue(ArrayUtils.contains(array, "2"));
        Assertions.assertTrue(ArrayUtils.contains(array, "3"));
        Assertions.assertTrue(ArrayUtils.contains(array, null));
        Assertions.assertFalse(ArrayUtils.contains(array, "notInArray"));
    }

    @Test
    public void testContains_LANG_1261() {
        class LANG1261ParentObject {
            @Override
            public boolean equals(final Object o) {
                return true;
            }
        }
        class LANG1261ChildObject extends LANG1261ParentObject {}
        final Object[] array = new LANG1261ChildObject[]{ new LANG1261ChildObject() };
        Assertions.assertTrue(ArrayUtils.contains(array, new LANG1261ParentObject()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfLong() {
        long[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 0));
        array = new long[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, 1));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 3));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 99));
    }

    @Test
    public void testIndexOfLongWithStartIndex() {
        long[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 0, 2));
        array = new long[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, 0, 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 1, 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 2, 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 3, 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 3, (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 99, 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 0, 6));
    }

    @Test
    public void testLastIndexOfLong() {
        long[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 0));
        array = new long[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, 0));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 1));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 2));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 3));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 99));
    }

    @Test
    public void testLastIndexOfLongWithStartIndex() {
        long[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 0, 2));
        array = new long[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, 0, 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 1, 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 2, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 3, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 3, (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 99, 4));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, 0, 88));
    }

    @Test
    public void testContainsLong() {
        long[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, 1));
        array = new long[]{ 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(array, 0));
        Assertions.assertTrue(ArrayUtils.contains(array, 1));
        Assertions.assertTrue(ArrayUtils.contains(array, 2));
        Assertions.assertTrue(ArrayUtils.contains(array, 3));
        Assertions.assertFalse(ArrayUtils.contains(array, 99));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfInt() {
        int[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 0));
        array = new int[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, 1));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 3));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 99));
    }

    @Test
    public void testIndexOfIntWithStartIndex() {
        int[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 0, 2));
        array = new int[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, 0, 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 1, 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 2, 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 3, 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 3, (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 99, 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 0, 6));
    }

    @Test
    public void testLastIndexOfInt() {
        int[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 0));
        array = new int[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, 0));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 1));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 2));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 3));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 99));
    }

    @Test
    public void testLastIndexOfIntWithStartIndex() {
        int[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 0, 2));
        array = new int[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, 0, 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 1, 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 2, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 3, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 3, (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 99));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, 0, 88));
    }

    @Test
    public void testContainsInt() {
        int[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, 1));
        array = new int[]{ 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(array, 0));
        Assertions.assertTrue(ArrayUtils.contains(array, 1));
        Assertions.assertTrue(ArrayUtils.contains(array, 2));
        Assertions.assertTrue(ArrayUtils.contains(array, 3));
        Assertions.assertFalse(ArrayUtils.contains(array, 99));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfShort() {
        short[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((short) (0))));
        array = new short[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, ((short) (0))));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, ((short) (1))));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((short) (2))));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((short) (3))));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((short) (99))));
    }

    @Test
    public void testIndexOfShortWithStartIndex() {
        short[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((short) (0)), 2));
        array = new short[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, ((short) (0)), 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((short) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((short) (2)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((short) (3)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((short) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((short) (99)), 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((short) (0)), 6));
    }

    @Test
    public void testLastIndexOfShort() {
        short[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((short) (0))));
        array = new short[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((short) (0))));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((short) (1))));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((short) (2))));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, ((short) (3))));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((short) (99))));
    }

    @Test
    public void testLastIndexOfShortWithStartIndex() {
        short[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((short) (0)), 2));
        array = new short[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, ((short) (0)), 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((short) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((short) (2)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((short) (3)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((short) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((short) (99))));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((short) (0)), 88));
    }

    @Test
    public void testContainsShort() {
        short[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, ((short) (1))));
        array = new short[]{ 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(array, ((short) (0))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((short) (1))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((short) (2))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((short) (3))));
        Assertions.assertFalse(ArrayUtils.contains(array, ((short) (99))));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfChar() {
        char[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 'a'));
        array = new char[]{ 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, 'a'));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, 'b'));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 'c'));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 'd'));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 'e'));
    }

    @Test
    public void testIndexOfCharWithStartIndex() {
        char[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 'a', 2));
        array = new char[]{ 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, 'a', 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 'b', 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 'c', 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 'd', 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 'd', (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 'e', 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, 'a', 6));
    }

    @Test
    public void testLastIndexOfChar() {
        char[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 'a'));
        array = new char[]{ 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, 'a'));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 'b'));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 'c'));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 'd'));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 'e'));
    }

    @Test
    public void testLastIndexOfCharWithStartIndex() {
        char[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 'a', 2));
        array = new char[]{ 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, 'a', 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 'b', 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 'c', 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 'd', 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 'd', (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 'e'));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, 'a', 88));
    }

    @Test
    public void testContainsChar() {
        char[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, 'b'));
        array = new char[]{ 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertTrue(ArrayUtils.contains(array, 'a'));
        Assertions.assertTrue(ArrayUtils.contains(array, 'b'));
        Assertions.assertTrue(ArrayUtils.contains(array, 'c'));
        Assertions.assertTrue(ArrayUtils.contains(array, 'd'));
        Assertions.assertFalse(ArrayUtils.contains(array, 'e'));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfByte() {
        byte[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((byte) (0))));
        array = new byte[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, ((byte) (0))));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, ((byte) (1))));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((byte) (2))));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((byte) (3))));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((byte) (99))));
    }

    @Test
    public void testIndexOfByteWithStartIndex() {
        byte[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((byte) (0)), 2));
        array = new byte[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, ((byte) (0)), 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((byte) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((byte) (2)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((byte) (3)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((byte) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((byte) (99)), 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((byte) (0)), 6));
    }

    @Test
    public void testLastIndexOfByte() {
        byte[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((byte) (0))));
        array = new byte[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((byte) (0))));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((byte) (1))));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((byte) (2))));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, ((byte) (3))));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((byte) (99))));
    }

    @Test
    public void testLastIndexOfByteWithStartIndex() {
        byte[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((byte) (0)), 2));
        array = new byte[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, ((byte) (0)), 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((byte) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((byte) (2)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((byte) (3)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((byte) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((byte) (99))));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((byte) (0)), 88));
    }

    @Test
    public void testContainsByte() {
        byte[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, ((byte) (1))));
        array = new byte[]{ 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(array, ((byte) (0))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((byte) (1))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((byte) (2))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((byte) (3))));
        Assertions.assertFalse(ArrayUtils.contains(array, ((byte) (99))));
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("cast")
    @Test
    public void testIndexOfDouble() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0))));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, ((double) (0))));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, ((double) (1))));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((double) (2))));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((double) (3))));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((double) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (99))));
    }

    @SuppressWarnings("cast")
    @Test
    public void testIndexOfDoubleTolerance() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), ((double) (0))));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, ((double) (0)), 0.3));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 2.2, 0.35));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 4.15, 2.0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, 1.00001324, 1.0E-4));
    }

    @SuppressWarnings("cast")
    @Test
    public void testIndexOfDoubleWithStartIndex() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), 2));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), 2));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, ((double) (0)), 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((double) (2)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((double) (3)), 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (99)), 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), 6));
    }

    @SuppressWarnings("cast")
    @Test
    public void testIndexOfDoubleWithStartIndexTolerance() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), 2, ((double) (0))));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), 2, ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((double) (0)), 99, 0.3));
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, ((double) (0)), 0, 0.3));
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, ((double) (0)), 3, 0.3));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, 2.2, 0, 0.35));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 4.15, 0, 2.0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, 1.00001324, 0, 1.0E-4));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, 4.15, (-1), 2.0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, 1.00001324, (-300), 1.0E-4));
    }

    @SuppressWarnings("cast")
    @Test
    public void testLastIndexOfDouble() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0))));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((double) (0))));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((double) (1))));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((double) (2))));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, ((double) (3))));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (99))));
    }

    @SuppressWarnings("cast")
    @Test
    public void testLastIndexOfDoubleTolerance() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0)), ((double) (0))));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0)), ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((double) (0)), 0.3));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 2.2, 0.35));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 4.15, 2.0));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 1.00001324, 1.0E-4));
    }

    @SuppressWarnings("cast")
    @Test
    public void testLastIndexOfDoubleWithStartIndex() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0)), 2));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0)), 2));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, ((double) (0)), 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((double) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((double) (2)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (3)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (99))));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((double) (0)), 88));
    }

    @SuppressWarnings("cast")
    @Test
    public void testLastIndexOfDoubleWithStartIndexTolerance() {
        double[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0)), 2, ((double) (0))));
        array = new double[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (0)), 2, ((double) (0))));
        array = new double[]{ ((double) (3)) };
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((double) (1)), 0, ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((double) (0)), 99, 0.3));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, ((double) (0)), 3, 0.3));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, 2.2, 3, 0.35));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 4.15, array.length, 2.0));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 1.00001324, array.length, 1.0E-4));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, 4.15, (-200), 2.0));
    }

    @SuppressWarnings("cast")
    @Test
    public void testContainsDouble() {
        double[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, ((double) (1))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(array, ((double) (0))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((double) (1))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((double) (2))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((double) (3))));
        Assertions.assertFalse(ArrayUtils.contains(array, ((double) (99))));
    }

    @SuppressWarnings("cast")
    @Test
    public void testContainsDoubleTolerance() {
        double[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, ((double) (1)), ((double) (0))));
        array = new double[]{ 0, 1, 2, 3, 0 };
        Assertions.assertFalse(ArrayUtils.contains(array, 4.0, 0.33));
        Assertions.assertFalse(ArrayUtils.contains(array, 2.5, 0.49));
        Assertions.assertTrue(ArrayUtils.contains(array, 2.5, 0.5));
        Assertions.assertTrue(ArrayUtils.contains(array, 2.5, 0.51));
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("cast")
    @Test
    public void testIndexOfFloat() {
        float[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (0))));
        array = new float[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (0))));
        array = new float[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, ((float) (0))));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, ((float) (1))));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((float) (2))));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((float) (3))));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (99))));
    }

    @SuppressWarnings("cast")
    @Test
    public void testIndexOfFloatWithStartIndex() {
        float[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (0)), 2));
        array = new float[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (0)), 2));
        array = new float[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, ((float) (0)), 2));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, ((float) (2)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((float) (3)), 2));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, ((float) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (99)), 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, ((float) (0)), 6));
    }

    @SuppressWarnings("cast")
    @Test
    public void testLastIndexOfFloat() {
        float[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (0))));
        array = new float[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (0))));
        array = new float[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((float) (0))));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((float) (1))));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((float) (2))));
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, ((float) (3))));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (99))));
    }

    @SuppressWarnings("cast")
    @Test
    public void testLastIndexOfFloatWithStartIndex() {
        float[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (0)), 2));
        array = new float[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (0)), 2));
        array = new float[]{ 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, ((float) (0)), 2));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, ((float) (1)), 2));
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, ((float) (2)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (3)), 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (3)), (-1)));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, ((float) (99))));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, ((float) (0)), 88));
    }

    @SuppressWarnings("cast")
    @Test
    public void testContainsFloat() {
        float[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, ((float) (1))));
        array = new float[]{ 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(array, ((float) (0))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((float) (1))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((float) (2))));
        Assertions.assertTrue(ArrayUtils.contains(array, ((float) (3))));
        Assertions.assertFalse(ArrayUtils.contains(array, ((float) (99))));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOfBoolean() {
        boolean[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, true));
        array = new boolean[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, true));
        array = new boolean[]{ true, false, true };
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, true));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, false));
        array = new boolean[]{ true, true };
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, false));
    }

    @Test
    public void testIndexOfBooleanWithStartIndex() {
        boolean[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, true, 2));
        array = new boolean[0];
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, true, 2));
        array = new boolean[]{ true, false, true };
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, true, 1));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, false, 2));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, false, 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, false, (-1)));
        array = new boolean[]{ true, true };
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, false, 0));
        Assertions.assertEquals((-1), ArrayUtils.indexOf(array, false, (-1)));
    }

    @Test
    public void testLastIndexOfBoolean() {
        boolean[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, true));
        array = new boolean[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, true));
        array = new boolean[]{ true, false, true };
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, true));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, false));
        array = new boolean[]{ true, true };
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, false));
    }

    @Test
    public void testLastIndexOfBooleanWithStartIndex() {
        boolean[] array = null;
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, true, 2));
        array = new boolean[0];
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, true, 2));
        array = new boolean[]{ true, false, true };
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(array, true, 2));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, true, 1));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, false, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, true, (-1)));
        array = new boolean[]{ true, true };
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, false, 2));
        Assertions.assertEquals((-1), ArrayUtils.lastIndexOf(array, true, (-1)));
    }

    @Test
    public void testContainsBoolean() {
        boolean[] array = null;
        Assertions.assertFalse(ArrayUtils.contains(array, true));
        array = new boolean[]{ true, false, true };
        Assertions.assertTrue(ArrayUtils.contains(array, true));
        Assertions.assertTrue(ArrayUtils.contains(array, false));
        array = new boolean[]{ true, true };
        Assertions.assertTrue(ArrayUtils.contains(array, true));
        Assertions.assertFalse(ArrayUtils.contains(array, false));
    }

    // testToPrimitive/Object for boolean
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_boolean() {
        final Boolean[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.toPrimitive(new Boolean[0]));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true }, ArrayUtils.toPrimitive(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Boolean[]{ Boolean.TRUE, null }));
    }

    @Test
    public void testToPrimitive_boolean_boolean() {
        Assertions.assertNull(ArrayUtils.toPrimitive(null, false));
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_ARRAY, ArrayUtils.toPrimitive(new Boolean[0], false));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true }, ArrayUtils.toPrimitive(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }, false));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false }, ArrayUtils.toPrimitive(new Boolean[]{ Boolean.TRUE, null, Boolean.FALSE }, false));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false }, ArrayUtils.toPrimitive(new Boolean[]{ Boolean.TRUE, null, Boolean.FALSE }, true));
    }

    @Test
    public void testToObject_boolean() {
        final boolean[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY, ArrayUtils.toObject(new boolean[0]));
        Assertions.assertArrayEquals(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }, ArrayUtils.toObject(new boolean[]{ true, false, true }));
    }

    // testToPrimitive/Object for byte
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_char() {
        final Character[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.toPrimitive(new Character[0]));
        Assertions.assertArrayEquals(new char[]{ Character.MIN_VALUE, Character.MAX_VALUE, '0' }, ArrayUtils.toPrimitive(new Character[]{ new Character(Character.MIN_VALUE), new Character(Character.MAX_VALUE), new Character('0') }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Character[]{ new Character(Character.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_char_char() {
        final Character[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b, Character.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_CHAR_ARRAY, ArrayUtils.toPrimitive(new Character[0], ((char) (0))));
        Assertions.assertArrayEquals(new char[]{ Character.MIN_VALUE, Character.MAX_VALUE, '0' }, ArrayUtils.toPrimitive(new Character[]{ new Character(Character.MIN_VALUE), new Character(Character.MAX_VALUE), new Character('0') }, Character.MIN_VALUE));
        Assertions.assertArrayEquals(new char[]{ Character.MIN_VALUE, Character.MAX_VALUE, '0' }, ArrayUtils.toPrimitive(new Character[]{ new Character(Character.MIN_VALUE), null, new Character('0') }, Character.MAX_VALUE));
    }

    @Test
    public void testToObject_char() {
        final char[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY, ArrayUtils.toObject(new char[0]));
        Assertions.assertArrayEquals(new Character[]{ new Character(Character.MIN_VALUE), new Character(Character.MAX_VALUE), new Character('0') }, ArrayUtils.toObject(new char[]{ Character.MIN_VALUE, Character.MAX_VALUE, '0' }));
    }

    // testToPrimitive/Object for byte
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_byte() {
        final Byte[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.toPrimitive(new Byte[0]));
        Assertions.assertArrayEquals(new byte[]{ Byte.MIN_VALUE, Byte.MAX_VALUE, ((byte) (9999999)) }, ArrayUtils.toPrimitive(new Byte[]{ Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf(Byte.MAX_VALUE), Byte.valueOf(((byte) (9999999))) }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Byte[]{ Byte.valueOf(Byte.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_byte_byte() {
        final Byte[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b, Byte.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.toPrimitive(new Byte[0], ((byte) (1))));
        Assertions.assertArrayEquals(new byte[]{ Byte.MIN_VALUE, Byte.MAX_VALUE, ((byte) (9999999)) }, ArrayUtils.toPrimitive(new Byte[]{ Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf(Byte.MAX_VALUE), Byte.valueOf(((byte) (9999999))) }, Byte.MIN_VALUE));
        Assertions.assertArrayEquals(new byte[]{ Byte.MIN_VALUE, Byte.MAX_VALUE, ((byte) (9999999)) }, ArrayUtils.toPrimitive(new Byte[]{ Byte.valueOf(Byte.MIN_VALUE), null, Byte.valueOf(((byte) (9999999))) }, Byte.MAX_VALUE));
    }

    @Test
    public void testToObject_byte() {
        final byte[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY, ArrayUtils.toObject(new byte[0]));
        Assertions.assertArrayEquals(new Byte[]{ Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf(Byte.MAX_VALUE), Byte.valueOf(((byte) (9999999))) }, ArrayUtils.toObject(new byte[]{ Byte.MIN_VALUE, Byte.MAX_VALUE, ((byte) (9999999)) }));
    }

    // testToPrimitive/Object for short
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_short() {
        final Short[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.toPrimitive(new Short[0]));
        Assertions.assertArrayEquals(new short[]{ Short.MIN_VALUE, Short.MAX_VALUE, ((short) (9999999)) }, ArrayUtils.toPrimitive(new Short[]{ Short.valueOf(Short.MIN_VALUE), Short.valueOf(Short.MAX_VALUE), Short.valueOf(((short) (9999999))) }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Short[]{ Short.valueOf(Short.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_short_short() {
        final Short[] s = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(s, Short.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_ARRAY, ArrayUtils.toPrimitive(new Short[0], Short.MIN_VALUE));
        Assertions.assertArrayEquals(new short[]{ Short.MIN_VALUE, Short.MAX_VALUE, ((short) (9999999)) }, ArrayUtils.toPrimitive(new Short[]{ Short.valueOf(Short.MIN_VALUE), Short.valueOf(Short.MAX_VALUE), Short.valueOf(((short) (9999999))) }, Short.MIN_VALUE));
        Assertions.assertArrayEquals(new short[]{ Short.MIN_VALUE, Short.MAX_VALUE, ((short) (9999999)) }, ArrayUtils.toPrimitive(new Short[]{ Short.valueOf(Short.MIN_VALUE), null, Short.valueOf(((short) (9999999))) }, Short.MAX_VALUE));
    }

    @Test
    public void testToObject_short() {
        final short[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY, ArrayUtils.toObject(new short[0]));
        Assertions.assertArrayEquals(new Short[]{ Short.valueOf(Short.MIN_VALUE), Short.valueOf(Short.MAX_VALUE), Short.valueOf(((short) (9999999))) }, ArrayUtils.toObject(new short[]{ Short.MIN_VALUE, Short.MAX_VALUE, ((short) (9999999)) }));
    }

    // testToPrimitive/Object for int
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_int() {
        final Integer[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.toPrimitive(new Integer[0]));
        Assertions.assertArrayEquals(new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Integer[]{ Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(9999999) }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Integer[]{ Integer.valueOf(Integer.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_int_int() {
        final Long[] l = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(l, Integer.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.toPrimitive(new Integer[0], 1));
        Assertions.assertArrayEquals(new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Integer[]{ Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(9999999) }, 1));
        Assertions.assertArrayEquals(new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Integer[]{ Integer.valueOf(Integer.MIN_VALUE), null, Integer.valueOf(9999999) }, Integer.MAX_VALUE));
    }

    @Test
    public void testToPrimitive_intNull() {
        final Integer[] iArray = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(iArray, Integer.MIN_VALUE));
    }

    @Test
    public void testToObject_int() {
        final int[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY, ArrayUtils.toObject(new int[0]));
        Assertions.assertArrayEquals(new Integer[]{ Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(9999999) }, ArrayUtils.toObject(new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE, 9999999 }));
    }

    // testToPrimitive/Object for long
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_long() {
        final Long[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.toPrimitive(new Long[0]));
        Assertions.assertArrayEquals(new long[]{ Long.MIN_VALUE, Long.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Long[]{ Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE), Long.valueOf(9999999) }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Long[]{ Long.valueOf(Long.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_long_long() {
        final Long[] l = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(l, Long.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.toPrimitive(new Long[0], 1));
        Assertions.assertArrayEquals(new long[]{ Long.MIN_VALUE, Long.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Long[]{ Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE), Long.valueOf(9999999) }, 1));
        Assertions.assertArrayEquals(new long[]{ Long.MIN_VALUE, Long.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Long[]{ Long.valueOf(Long.MIN_VALUE), null, Long.valueOf(9999999) }, Long.MAX_VALUE));
    }

    @Test
    public void testToObject_long() {
        final long[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY, ArrayUtils.toObject(new long[0]));
        Assertions.assertArrayEquals(new Long[]{ Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE), Long.valueOf(9999999) }, ArrayUtils.toObject(new long[]{ Long.MIN_VALUE, Long.MAX_VALUE, 9999999 }));
    }

    // testToPrimitive/Object for float
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_float() {
        final Float[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.toPrimitive(new Float[0]));
        Assertions.assertArrayEquals(new float[]{ Float.MIN_VALUE, Float.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Float[]{ Float.valueOf(Float.MIN_VALUE), Float.valueOf(Float.MAX_VALUE), Float.valueOf(9999999) }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Float[]{ Float.valueOf(Float.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_float_float() {
        final Float[] l = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(l, Float.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_ARRAY, ArrayUtils.toPrimitive(new Float[0], 1));
        Assertions.assertArrayEquals(new float[]{ Float.MIN_VALUE, Float.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Float[]{ Float.valueOf(Float.MIN_VALUE), Float.valueOf(Float.MAX_VALUE), Float.valueOf(9999999) }, 1));
        Assertions.assertArrayEquals(new float[]{ Float.MIN_VALUE, Float.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Float[]{ Float.valueOf(Float.MIN_VALUE), null, Float.valueOf(9999999) }, Float.MAX_VALUE));
    }

    @Test
    public void testToObject_float() {
        final float[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY, ArrayUtils.toObject(new float[0]));
        Assertions.assertArrayEquals(new Float[]{ Float.valueOf(Float.MIN_VALUE), Float.valueOf(Float.MAX_VALUE), Float.valueOf(9999999) }, ArrayUtils.toObject(new float[]{ Float.MIN_VALUE, Float.MAX_VALUE, 9999999 }));
    }

    // testToPrimitive/Object for double
    // -----------------------------------------------------------------------
    @Test
    public void testToPrimitive_double() {
        final Double[] b = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(b));
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.toPrimitive(new Double[0]));
        Assertions.assertArrayEquals(new double[]{ Double.MIN_VALUE, Double.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Double[]{ Double.valueOf(Double.MIN_VALUE), Double.valueOf(Double.MAX_VALUE), Double.valueOf(9999999) }));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toPrimitive(new Float[]{ Float.valueOf(Float.MIN_VALUE), null }));
    }

    @Test
    public void testToPrimitive_double_double() {
        final Double[] l = null;
        Assertions.assertNull(ArrayUtils.toPrimitive(l, Double.MIN_VALUE));
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_ARRAY, ArrayUtils.toPrimitive(new Double[0], 1));
        Assertions.assertArrayEquals(new double[]{ Double.MIN_VALUE, Double.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Double[]{ Double.valueOf(Double.MIN_VALUE), Double.valueOf(Double.MAX_VALUE), Double.valueOf(9999999) }, 1));
        Assertions.assertArrayEquals(new double[]{ Double.MIN_VALUE, Double.MAX_VALUE, 9999999 }, ArrayUtils.toPrimitive(new Double[]{ Double.valueOf(Double.MIN_VALUE), null, Double.valueOf(9999999) }, Double.MAX_VALUE));
    }

    @Test
    public void testToObject_double() {
        final double[] b = null;
        Assertions.assertArrayEquals(null, ArrayUtils.toObject(b));
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY, ArrayUtils.toObject(new double[0]));
        Assertions.assertArrayEquals(new Double[]{ Double.valueOf(Double.MIN_VALUE), Double.valueOf(Double.MAX_VALUE), Double.valueOf(9999999) }, ArrayUtils.toObject(new double[]{ Double.MIN_VALUE, Double.MAX_VALUE, 9999999 }));
    }

    // -----------------------------------------------------------------------
    /**
     * Test for {@link ArrayUtils#isEmpty(java.lang.Object[])}.
     */
    @Test
    public void testIsEmptyObject() {
        final Object[] emptyArray = new Object[]{  };
        final Object[] notEmptyArray = new Object[]{ new String("Value") };
        Assertions.assertTrue(ArrayUtils.isEmpty(((Object[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyArray));
    }

    /**
     * Tests for {@link ArrayUtils#isEmpty(long[])},
     * {@link ArrayUtils#isEmpty(int[])},
     * {@link ArrayUtils#isEmpty(short[])},
     * {@link ArrayUtils#isEmpty(char[])},
     * {@link ArrayUtils#isEmpty(byte[])},
     * {@link ArrayUtils#isEmpty(double[])},
     * {@link ArrayUtils#isEmpty(float[])} and
     * {@link ArrayUtils#isEmpty(boolean[])}.
     */
    @Test
    public void testIsEmptyPrimitives() {
        final long[] emptyLongArray = new long[]{  };
        final long[] notEmptyLongArray = new long[]{ 1L };
        Assertions.assertTrue(ArrayUtils.isEmpty(((long[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyLongArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyLongArray));
        final int[] emptyIntArray = new int[]{  };
        final int[] notEmptyIntArray = new int[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isEmpty(((int[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyIntArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyIntArray));
        final short[] emptyShortArray = new short[]{  };
        final short[] notEmptyShortArray = new short[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isEmpty(((short[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyShortArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyShortArray));
        final char[] emptyCharArray = new char[]{  };
        final char[] notEmptyCharArray = new char[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isEmpty(((char[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyCharArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyCharArray));
        final byte[] emptyByteArray = new byte[]{  };
        final byte[] notEmptyByteArray = new byte[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isEmpty(((byte[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyByteArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyByteArray));
        final double[] emptyDoubleArray = new double[]{  };
        final double[] notEmptyDoubleArray = new double[]{ 1.0 };
        Assertions.assertTrue(ArrayUtils.isEmpty(((double[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyDoubleArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyDoubleArray));
        final float[] emptyFloatArray = new float[]{  };
        final float[] notEmptyFloatArray = new float[]{ 1.0F };
        Assertions.assertTrue(ArrayUtils.isEmpty(((float[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyFloatArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyFloatArray));
        final boolean[] emptyBooleanArray = new boolean[]{  };
        final boolean[] notEmptyBooleanArray = new boolean[]{ true };
        Assertions.assertTrue(ArrayUtils.isEmpty(((boolean[]) (null))));
        Assertions.assertTrue(ArrayUtils.isEmpty(emptyBooleanArray));
        Assertions.assertFalse(ArrayUtils.isEmpty(notEmptyBooleanArray));
    }

    /**
     * Test for {@link ArrayUtils#isNotEmpty(java.lang.Object[])}.
     */
    @Test
    public void testIsNotEmptyObject() {
        final Object[] emptyArray = new Object[]{  };
        final Object[] notEmptyArray = new Object[]{ new String("Value") };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((Object[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyArray));
    }

    /**
     * Tests for {@link ArrayUtils#isNotEmpty(long[])},
     * {@link ArrayUtils#isNotEmpty(int[])},
     * {@link ArrayUtils#isNotEmpty(short[])},
     * {@link ArrayUtils#isNotEmpty(char[])},
     * {@link ArrayUtils#isNotEmpty(byte[])},
     * {@link ArrayUtils#isNotEmpty(double[])},
     * {@link ArrayUtils#isNotEmpty(float[])} and
     * {@link ArrayUtils#isNotEmpty(boolean[])}.
     */
    @Test
    public void testIsNotEmptyPrimitives() {
        final long[] emptyLongArray = new long[]{  };
        final long[] notEmptyLongArray = new long[]{ 1L };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((long[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyLongArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyLongArray));
        final int[] emptyIntArray = new int[]{  };
        final int[] notEmptyIntArray = new int[]{ 1 };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((int[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyIntArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyIntArray));
        final short[] emptyShortArray = new short[]{  };
        final short[] notEmptyShortArray = new short[]{ 1 };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((short[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyShortArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyShortArray));
        final char[] emptyCharArray = new char[]{  };
        final char[] notEmptyCharArray = new char[]{ 1 };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((char[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyCharArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyCharArray));
        final byte[] emptyByteArray = new byte[]{  };
        final byte[] notEmptyByteArray = new byte[]{ 1 };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((byte[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyByteArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyByteArray));
        final double[] emptyDoubleArray = new double[]{  };
        final double[] notEmptyDoubleArray = new double[]{ 1.0 };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((double[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyDoubleArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyDoubleArray));
        final float[] emptyFloatArray = new float[]{  };
        final float[] notEmptyFloatArray = new float[]{ 1.0F };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((float[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyFloatArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyFloatArray));
        final boolean[] emptyBooleanArray = new boolean[]{  };
        final boolean[] notEmptyBooleanArray = new boolean[]{ true };
        Assertions.assertFalse(ArrayUtils.isNotEmpty(((boolean[]) (null))));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(emptyBooleanArray));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(notEmptyBooleanArray));
    }

    // ------------------------------------------------------------------------
    @Test
    public void testGetLength() {
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        final Object[] emptyObjectArray = new Object[0];
        final Object[] notEmptyObjectArray = new Object[]{ "aValue" };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyObjectArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyObjectArray));
        final int[] emptyIntArray = new int[]{  };
        final int[] notEmptyIntArray = new int[]{ 1 };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyIntArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyIntArray));
        final short[] emptyShortArray = new short[]{  };
        final short[] notEmptyShortArray = new short[]{ 1 };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyShortArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyShortArray));
        final char[] emptyCharArray = new char[]{  };
        final char[] notEmptyCharArray = new char[]{ 1 };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyCharArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyCharArray));
        final byte[] emptyByteArray = new byte[]{  };
        final byte[] notEmptyByteArray = new byte[]{ 1 };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyByteArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyByteArray));
        final double[] emptyDoubleArray = new double[]{  };
        final double[] notEmptyDoubleArray = new double[]{ 1.0 };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyDoubleArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyDoubleArray));
        final float[] emptyFloatArray = new float[]{  };
        final float[] notEmptyFloatArray = new float[]{ 1.0F };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyFloatArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyFloatArray));
        final boolean[] emptyBooleanArray = new boolean[]{  };
        final boolean[] notEmptyBooleanArray = new boolean[]{ true };
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(emptyBooleanArray));
        Assertions.assertEquals(1, ArrayUtils.getLength(notEmptyBooleanArray));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.getLength("notAnArray"));
    }

    @Test
    public void testIsSorted() {
        Integer[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new Integer[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new Integer[]{ 1, 2, 3 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new Integer[]{ 1, 3, 2 };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedComparator() {
        final Comparator<Integer> c = new Comparator<Integer>() {
            @Override
            public int compare(final Integer o1, final Integer o2) {
                return o2.compareTo(o1);
            }
        };
        Integer[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array, c));
        array = new Integer[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isSorted(array, c));
        array = new Integer[]{ 3, 2, 1 };
        Assertions.assertTrue(ArrayUtils.isSorted(array, c));
        array = new Integer[]{ 1, 3, 2 };
        Assertions.assertFalse(ArrayUtils.isSorted(array, c));
    }

    @Test
    public void testIsSortedNullComparator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.isSorted(null, null));
    }

    @Test
    public void testIsSortedInt() {
        int[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new int[]{ 1 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new int[]{ 1, 2, 3 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new int[]{ 1, 3, 2 };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedFloat() {
        float[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new float[]{ 0.0F };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new float[]{ -1.0F, 0.0F, 0.1F, 0.2F };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new float[]{ -1.0F, 0.2F, 0.1F, 0.0F };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedLong() {
        long[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new long[]{ 0L };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new long[]{ -1L, 0L, 1L };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new long[]{ -1L, 1L, 0L };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedDouble() {
        double[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new double[]{ 0.0 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new double[]{ -1.0, 0.0, 0.1, 0.2 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new double[]{ -1.0, 0.2, 0.1, 0.0 };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedChar() {
        char[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new char[]{ 'a' };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new char[]{ 'a', 'b', 'c' };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new char[]{ 'a', 'c', 'b' };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedByte() {
        byte[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new byte[]{ 16 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new byte[]{ 16, 32, 48 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new byte[]{ 16, 48, 32 };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedShort() {
        short[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new short[]{ 0 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new short[]{ -1, 0, 1 };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new short[]{ -1, 1, 0 };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testIsSortedBool() {
        boolean[] array = null;
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new boolean[]{ true };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new boolean[]{ false, true };
        Assertions.assertTrue(ArrayUtils.isSorted(array));
        array = new boolean[]{ true, false };
        Assertions.assertFalse(ArrayUtils.isSorted(array));
    }

    @Test
    public void testCreatePrimitiveArray() {
        Assertions.assertNull(ArrayUtils.toPrimitive(((Object[]) (null))));
        Assertions.assertArrayEquals(new int[]{  }, ArrayUtils.toPrimitive(new Integer[]{  }));
        Assertions.assertArrayEquals(new short[]{ 2 }, ArrayUtils.toPrimitive(new Short[]{ 2 }));
        Assertions.assertArrayEquals(new long[]{ 2, 3 }, ArrayUtils.toPrimitive(new Long[]{ 2L, 3L }));
        Assertions.assertArrayEquals(new float[]{ 3.14F }, ArrayUtils.toPrimitive(new Float[]{ 3.14F }), 0.1F);
        Assertions.assertArrayEquals(new double[]{ 2.718 }, ArrayUtils.toPrimitive(new Double[]{ 2.718 }), 0.1);
    }

    @Test
    public void testToStringArray_array() {
        Assertions.assertNull(ArrayUtils.toStringArray(null));
        Assertions.assertArrayEquals(new String[0], ArrayUtils.toStringArray(new Object[0]));
        final Object[] array = new Object[]{ 1, 2, 3, "array", "test" };
        Assertions.assertArrayEquals(new String[]{ "1", "2", "3", "array", "test" }, ArrayUtils.toStringArray(array));
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.toStringArray(new Object[]{ null }));
    }

    @Test
    public void testToStringArray_array_string() {
        Assertions.assertNull(ArrayUtils.toStringArray(null, ""));
        Assertions.assertArrayEquals(new String[0], ArrayUtils.toStringArray(new Object[0], ""));
        final Object[] array = new Object[]{ 1, null, "test" };
        Assertions.assertArrayEquals(new String[]{ "1", "valueForNullElements", "test" }, ArrayUtils.toStringArray(array, "valueForNullElements"));
    }

    @Test
    public void testShuffle() {
        final String[] array1 = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        final String[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final String element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleBoolean() {
        final boolean[] array1 = new boolean[]{ true, false, true, true, false, false, true, false, false, true };
        final boolean[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        Assertions.assertEquals(5, ArrayUtils.removeAllOccurences(array1, true).length);
    }

    @Test
    public void testShuffleByte() {
        final byte[] array1 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final byte[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final byte element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleChar() {
        final char[] array1 = new char[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final char[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final char element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleShort() {
        final short[] array1 = new short[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final short[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final short element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleInt() {
        final int[] array1 = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final int[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final int element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleLong() {
        final long[] array1 = new long[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final long[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final long element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleFloat() {
        final float[] array1 = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final float[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final float element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testShuffleDouble() {
        final double[] array1 = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final double[] array2 = ArrayUtils.clone(array1);
        ArrayUtils.shuffle(array1, new Random(ArrayUtilsTest.SEED));
        Assertions.assertFalse(Arrays.equals(array1, array2));
        for (final double element : array2) {
            Assertions.assertTrue(ArrayUtils.contains(array1, element), (("Element " + element) + " not found"));
        }
    }

    @Test
    public void testIsArrayIndexValid() {
        Assertions.assertFalse(ArrayUtils.isArrayIndexValid(null, 0));
        String[] array = new String[1];
        // too big
        Assertions.assertFalse(ArrayUtils.isArrayIndexValid(array, 1));
        // negative index
        Assertions.assertFalse(ArrayUtils.isArrayIndexValid(array, (-1)));
        // good to go
        Assertions.assertTrue(ArrayUtils.isArrayIndexValid(array, 0));
    }
}

