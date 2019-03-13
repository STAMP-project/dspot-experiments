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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests ArrayUtils add methods.
 */
public class ArrayUtilsAddTest {
    @Test
    public void testJira567() {
        Number[] n;
        // Valid array construction
        n = ArrayUtils.addAll(new Number[]{ Integer.valueOf(1) }, new Long[]{ Long.valueOf(2) });
        Assertions.assertEquals(2, n.length);
        Assertions.assertEquals(Number.class, n.getClass().getComponentType());
        // Invalid - can't store Long in Integer array
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.addAll(new Integer[]{ Integer.valueOf(1) }, new Long[]{ Long.valueOf(2) }));
    }

    @Test
    public void testAddObjectArrayBoolean() {
        boolean[] newArray;
        newArray = ArrayUtils.add(null, false);
        Assertions.assertArrayEquals(new boolean[]{ false }, newArray);
        Assertions.assertEquals(Boolean.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(null, true);
        Assertions.assertArrayEquals(new boolean[]{ true }, newArray);
        Assertions.assertEquals(Boolean.TYPE, newArray.getClass().getComponentType());
        final boolean[] array1 = new boolean[]{ true, false, true };
        newArray = ArrayUtils.add(array1, false);
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false }, newArray);
        Assertions.assertEquals(Boolean.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayByte() {
        byte[] newArray;
        newArray = ArrayUtils.add(((byte[]) (null)), ((byte) (0)));
        Assertions.assertArrayEquals(new byte[]{ 0 }, newArray);
        Assertions.assertEquals(Byte.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((byte[]) (null)), ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 1 }, newArray);
        Assertions.assertEquals(Byte.TYPE, newArray.getClass().getComponentType());
        final byte[] array1 = new byte[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, ((byte) (0)));
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Byte.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, ((byte) (4)));
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Byte.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayChar() {
        char[] newArray;
        newArray = ArrayUtils.add(((char[]) (null)), ((char) (0)));
        Assertions.assertArrayEquals(new char[]{ 0 }, newArray);
        Assertions.assertEquals(Character.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((char[]) (null)), ((char) (1)));
        Assertions.assertArrayEquals(new char[]{ 1 }, newArray);
        Assertions.assertEquals(Character.TYPE, newArray.getClass().getComponentType());
        final char[] array1 = new char[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, ((char) (0)));
        Assertions.assertArrayEquals(new char[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Character.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, ((char) (4)));
        Assertions.assertArrayEquals(new char[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Character.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayDouble() {
        double[] newArray;
        newArray = ArrayUtils.add(((double[]) (null)), 0);
        Assertions.assertArrayEquals(new double[]{ 0 }, newArray);
        Assertions.assertEquals(Double.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((double[]) (null)), 1);
        Assertions.assertArrayEquals(new double[]{ 1 }, newArray);
        Assertions.assertEquals(Double.TYPE, newArray.getClass().getComponentType());
        final double[] array1 = new double[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, 0);
        Assertions.assertArrayEquals(new double[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Double.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, 4);
        Assertions.assertArrayEquals(new double[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Double.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayFloat() {
        float[] newArray;
        newArray = ArrayUtils.add(((float[]) (null)), 0);
        Assertions.assertArrayEquals(new float[]{ 0 }, newArray);
        Assertions.assertEquals(Float.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((float[]) (null)), 1);
        Assertions.assertArrayEquals(new float[]{ 1 }, newArray);
        Assertions.assertEquals(Float.TYPE, newArray.getClass().getComponentType());
        final float[] array1 = new float[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, 0);
        Assertions.assertArrayEquals(new float[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Float.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, 4);
        Assertions.assertArrayEquals(new float[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Float.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayInt() {
        int[] newArray;
        newArray = ArrayUtils.add(((int[]) (null)), 0);
        Assertions.assertArrayEquals(new int[]{ 0 }, newArray);
        Assertions.assertEquals(Integer.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((int[]) (null)), 1);
        Assertions.assertArrayEquals(new int[]{ 1 }, newArray);
        Assertions.assertEquals(Integer.TYPE, newArray.getClass().getComponentType());
        final int[] array1 = new int[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, 0);
        Assertions.assertArrayEquals(new int[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Integer.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, 4);
        Assertions.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Integer.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayLong() {
        long[] newArray;
        newArray = ArrayUtils.add(((long[]) (null)), 0);
        Assertions.assertArrayEquals(new long[]{ 0 }, newArray);
        Assertions.assertEquals(Long.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((long[]) (null)), 1);
        Assertions.assertArrayEquals(new long[]{ 1 }, newArray);
        Assertions.assertEquals(Long.TYPE, newArray.getClass().getComponentType());
        final long[] array1 = new long[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, 0);
        Assertions.assertArrayEquals(new long[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Long.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, 4);
        Assertions.assertArrayEquals(new long[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Long.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayShort() {
        short[] newArray;
        newArray = ArrayUtils.add(((short[]) (null)), ((short) (0)));
        Assertions.assertArrayEquals(new short[]{ 0 }, newArray);
        Assertions.assertEquals(Short.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(((short[]) (null)), ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 1 }, newArray);
        Assertions.assertEquals(Short.TYPE, newArray.getClass().getComponentType());
        final short[] array1 = new short[]{ 1, 2, 3 };
        newArray = ArrayUtils.add(array1, ((short) (0)));
        Assertions.assertArrayEquals(new short[]{ 1, 2, 3, 0 }, newArray);
        Assertions.assertEquals(Short.TYPE, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(array1, ((short) (4)));
        Assertions.assertArrayEquals(new short[]{ 1, 2, 3, 4 }, newArray);
        Assertions.assertEquals(Short.TYPE, newArray.getClass().getComponentType());
    }

    @Test
    public void testAddObjectArrayObject() {
        Object[] newArray;
        // show that not casting is okay
        newArray = ArrayUtils.add(((Object[]) (null)), "a");
        Assertions.assertArrayEquals(new String[]{ "a" }, newArray);
        Assertions.assertArrayEquals(new Object[]{ "a" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        // show that not casting to Object[] is okay and will assume String based on "a"
        final String[] newStringArray = ArrayUtils.add(null, "a");
        Assertions.assertArrayEquals(new String[]{ "a" }, newStringArray);
        Assertions.assertArrayEquals(new Object[]{ "a" }, newStringArray);
        Assertions.assertEquals(String.class, newStringArray.getClass().getComponentType());
        final String[] stringArray1 = new String[]{ "a", "b", "c" };
        newArray = ArrayUtils.add(stringArray1, null);
        Assertions.assertArrayEquals(new String[]{ "a", "b", "c", null }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(stringArray1, "d");
        Assertions.assertArrayEquals(new String[]{ "a", "b", "c", "d" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        Number[] numberArray1 = new Number[]{ Integer.valueOf(1), Double.valueOf(2) };
        newArray = ArrayUtils.add(numberArray1, Float.valueOf(3));
        Assertions.assertArrayEquals(new Number[]{ Integer.valueOf(1), Double.valueOf(2), Float.valueOf(3) }, newArray);
        Assertions.assertEquals(Number.class, newArray.getClass().getComponentType());
        numberArray1 = null;
        newArray = ArrayUtils.add(numberArray1, Float.valueOf(3));
        Assertions.assertArrayEquals(new Float[]{ Float.valueOf(3) }, newArray);
        Assertions.assertEquals(Float.class, newArray.getClass().getComponentType());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testLANG571() {
        final String[] stringArray = null;
        final String aString = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.add(stringArray, aString));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.add(stringArray, 0, aString));
    }

    @Test
    public void testAddObjectArrayToObjectArray() {
        Assertions.assertNull(ArrayUtils.addAll(null, ((Object[]) (null))));
        Object[] newArray;
        final String[] stringArray1 = new String[]{ "a", "b", "c" };
        final String[] stringArray2 = new String[]{ "1", "2", "3" };
        newArray = ArrayUtils.addAll(stringArray1, ((String[]) (null)));
        Assertions.assertNotSame(stringArray1, newArray);
        Assertions.assertArrayEquals(stringArray1, newArray);
        Assertions.assertArrayEquals(new String[]{ "a", "b", "c" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.addAll(null, stringArray2);
        Assertions.assertNotSame(stringArray2, newArray);
        Assertions.assertArrayEquals(stringArray2, newArray);
        Assertions.assertArrayEquals(new String[]{ "1", "2", "3" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.addAll(stringArray1, stringArray2);
        Assertions.assertArrayEquals(new String[]{ "a", "b", "c", "1", "2", "3" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.addAll(ArrayUtils.EMPTY_STRING_ARRAY, ((String[]) (null)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, newArray);
        Assertions.assertArrayEquals(new String[]{  }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.addAll(null, ArrayUtils.EMPTY_STRING_ARRAY);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, newArray);
        Assertions.assertArrayEquals(new String[]{  }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.addAll(ArrayUtils.EMPTY_STRING_ARRAY, ArrayUtils.EMPTY_STRING_ARRAY);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, newArray);
        Assertions.assertArrayEquals(new String[]{  }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        final String[] stringArrayNull = new String[]{ null };
        newArray = ArrayUtils.addAll(stringArrayNull, stringArrayNull);
        Assertions.assertArrayEquals(new String[]{ null, null }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        // boolean
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, true }, ArrayUtils.addAll(new boolean[]{ true, false }, false, true));
        Assertions.assertArrayEquals(new boolean[]{ false, true }, ArrayUtils.addAll(null, new boolean[]{ false, true }));
        Assertions.assertArrayEquals(new boolean[]{ true, false }, ArrayUtils.addAll(new boolean[]{ true, false }, null));
        // char
        Assertions.assertArrayEquals(new char[]{ 'a', 'b', 'c', 'd' }, ArrayUtils.addAll(new char[]{ 'a', 'b' }, 'c', 'd'));
        Assertions.assertArrayEquals(new char[]{ 'c', 'd' }, ArrayUtils.addAll(null, new char[]{ 'c', 'd' }));
        Assertions.assertArrayEquals(new char[]{ 'a', 'b' }, ArrayUtils.addAll(new char[]{ 'a', 'b' }, null));
        // byte
        Assertions.assertArrayEquals(new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (3)) }, ArrayUtils.addAll(new byte[]{ ((byte) (0)), ((byte) (1)) }, ((byte) (2)), ((byte) (3))));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (2)), ((byte) (3)) }, ArrayUtils.addAll(null, new byte[]{ ((byte) (2)), ((byte) (3)) }));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (0)), ((byte) (1)) }, ArrayUtils.addAll(new byte[]{ ((byte) (0)), ((byte) (1)) }, null));
        // short
        Assertions.assertArrayEquals(new short[]{ ((short) (10)), ((short) (20)), ((short) (30)), ((short) (40)) }, ArrayUtils.addAll(new short[]{ ((short) (10)), ((short) (20)) }, ((short) (30)), ((short) (40))));
        Assertions.assertArrayEquals(new short[]{ ((short) (30)), ((short) (40)) }, ArrayUtils.addAll(null, new short[]{ ((short) (30)), ((short) (40)) }));
        Assertions.assertArrayEquals(new short[]{ ((short) (10)), ((short) (20)) }, ArrayUtils.addAll(new short[]{ ((short) (10)), ((short) (20)) }, null));
        // int
        Assertions.assertArrayEquals(new int[]{ 1, 1000, -1000, -1 }, ArrayUtils.addAll(new int[]{ 1, 1000 }, (-1000), (-1)));
        Assertions.assertArrayEquals(new int[]{ -1000, -1 }, ArrayUtils.addAll(null, new int[]{ -1000, -1 }));
        Assertions.assertArrayEquals(new int[]{ 1, 1000 }, ArrayUtils.addAll(new int[]{ 1, 1000 }, null));
        // long
        Assertions.assertArrayEquals(new long[]{ 1L, -1L, 1000L, -1000L }, ArrayUtils.addAll(new long[]{ 1L, -1L }, 1000L, (-1000L)));
        Assertions.assertArrayEquals(new long[]{ 1000L, -1000L }, ArrayUtils.addAll(null, new long[]{ 1000L, -1000L }));
        Assertions.assertArrayEquals(new long[]{ 1L, -1L }, ArrayUtils.addAll(new long[]{ 1L, -1L }, null));
        // float
        Assertions.assertArrayEquals(new float[]{ 10.5F, 10.1F, 1.6F, 0.01F }, ArrayUtils.addAll(new float[]{ 10.5F, 10.1F }, 1.6F, 0.01F));
        Assertions.assertArrayEquals(new float[]{ 1.6F, 0.01F }, ArrayUtils.addAll(null, new float[]{ 1.6F, 0.01F }));
        Assertions.assertArrayEquals(new float[]{ 10.5F, 10.1F }, ArrayUtils.addAll(new float[]{ 10.5F, 10.1F }, null));
        // double
        Assertions.assertArrayEquals(new double[]{ Math.PI, -(Math.PI), 0, 9.99 }, ArrayUtils.addAll(new double[]{ Math.PI, -(Math.PI) }, 0, 9.99));
        Assertions.assertArrayEquals(new double[]{ 0, 9.99 }, ArrayUtils.addAll(null, new double[]{ 0, 9.99 }));
        Assertions.assertArrayEquals(new double[]{ Math.PI, -(Math.PI) }, ArrayUtils.addAll(new double[]{ Math.PI, -(Math.PI) }, null));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAddObjectAtIndex() {
        Object[] newArray;
        newArray = ArrayUtils.add(((Object[]) (null)), 0, "a");
        Assertions.assertArrayEquals(new String[]{ "a" }, newArray);
        Assertions.assertArrayEquals(new Object[]{ "a" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        final String[] stringArray1 = new String[]{ "a", "b", "c" };
        newArray = ArrayUtils.add(stringArray1, 0, null);
        Assertions.assertArrayEquals(new String[]{ null, "a", "b", "c" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(stringArray1, 1, null);
        Assertions.assertArrayEquals(new String[]{ "a", null, "b", "c" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(stringArray1, 3, null);
        Assertions.assertArrayEquals(new String[]{ "a", "b", "c", null }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        newArray = ArrayUtils.add(stringArray1, 3, "d");
        Assertions.assertArrayEquals(new String[]{ "a", "b", "c", "d" }, newArray);
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        Assertions.assertEquals(String.class, newArray.getClass().getComponentType());
        final Object[] o = new Object[]{ "1", "2", "4" };
        final Object[] result = ArrayUtils.add(o, 2, "3");
        final Object[] result2 = ArrayUtils.add(o, 3, "5");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.length);
        Assertions.assertEquals("1", result[0]);
        Assertions.assertEquals("2", result[1]);
        Assertions.assertEquals("3", result[2]);
        Assertions.assertEquals("4", result[3]);
        Assertions.assertNotNull(result2);
        Assertions.assertEquals(4, result2.length);
        Assertions.assertEquals("1", result2[0]);
        Assertions.assertEquals("2", result2[1]);
        Assertions.assertEquals("4", result2[2]);
        Assertions.assertEquals("5", result2[3]);
        // boolean tests
        boolean[] booleanArray = ArrayUtils.add(null, 0, true);
        Assertions.assertArrayEquals(new boolean[]{ true }, booleanArray);
        IndexOutOfBoundsException e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(null, (-1), true));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        booleanArray = ArrayUtils.add(new boolean[]{ true }, 0, false);
        Assertions.assertArrayEquals(new boolean[]{ false, true }, booleanArray);
        booleanArray = ArrayUtils.add(new boolean[]{ false }, 1, true);
        Assertions.assertArrayEquals(new boolean[]{ false, true }, booleanArray);
        booleanArray = ArrayUtils.add(new boolean[]{ true, false }, 1, true);
        Assertions.assertArrayEquals(new boolean[]{ true, true, false }, booleanArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new boolean[]{ true, false }, 4, true));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new boolean[]{ true, false }, (-1), true));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // char tests
        char[] charArray = ArrayUtils.add(((char[]) (null)), 0, 'a');
        Assertions.assertArrayEquals(new char[]{ 'a' }, charArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(((char[]) (null)), (-1), 'a'));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        charArray = ArrayUtils.add(new char[]{ 'a' }, 0, 'b');
        Assertions.assertArrayEquals(new char[]{ 'b', 'a' }, charArray);
        charArray = ArrayUtils.add(new char[]{ 'a', 'b' }, 0, 'c');
        Assertions.assertArrayEquals(new char[]{ 'c', 'a', 'b' }, charArray);
        charArray = ArrayUtils.add(new char[]{ 'a', 'b' }, 1, 'k');
        Assertions.assertArrayEquals(new char[]{ 'a', 'k', 'b' }, charArray);
        charArray = ArrayUtils.add(new char[]{ 'a', 'b', 'c' }, 1, 't');
        Assertions.assertArrayEquals(new char[]{ 'a', 't', 'b', 'c' }, charArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new char[]{ 'a', 'b' }, 4, 'c'));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new char[]{ 'a', 'b' }, (-1), 'c'));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // short tests
        short[] shortArray = ArrayUtils.add(new short[]{ 1 }, 0, ((short) (2)));
        Assertions.assertArrayEquals(new short[]{ 2, 1 }, shortArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(((short[]) (null)), (-1), ((short) (2))));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        shortArray = ArrayUtils.add(new short[]{ 2, 6 }, 2, ((short) (10)));
        Assertions.assertArrayEquals(new short[]{ 2, 6, 10 }, shortArray);
        shortArray = ArrayUtils.add(new short[]{ 2, 6 }, 0, ((short) (-4)));
        Assertions.assertArrayEquals(new short[]{ -4, 2, 6 }, shortArray);
        shortArray = ArrayUtils.add(new short[]{ 2, 6, 3 }, 2, ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 2, 6, 1, 3 }, shortArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new short[]{ 2, 6 }, 4, ((short) (10))));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new short[]{ 2, 6 }, (-1), ((short) (10))));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // byte tests
        byte[] byteArray = ArrayUtils.add(new byte[]{ 1 }, 0, ((byte) (2)));
        Assertions.assertArrayEquals(new byte[]{ 2, 1 }, byteArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(((byte[]) (null)), (-1), ((byte) (2))));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        byteArray = ArrayUtils.add(new byte[]{ 2, 6 }, 2, ((byte) (3)));
        Assertions.assertArrayEquals(new byte[]{ 2, 6, 3 }, byteArray);
        byteArray = ArrayUtils.add(new byte[]{ 2, 6 }, 0, ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 6 }, byteArray);
        byteArray = ArrayUtils.add(new byte[]{ 2, 6, 3 }, 2, ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 2, 6, 1, 3 }, byteArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new byte[]{ 2, 6 }, 4, ((byte) (3))));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new byte[]{ 2, 6 }, (-1), ((byte) (3))));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // int tests
        int[] intArray = ArrayUtils.add(new int[]{ 1 }, 0, 2);
        Assertions.assertArrayEquals(new int[]{ 2, 1 }, intArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(((int[]) (null)), (-1), 2));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        intArray = ArrayUtils.add(new int[]{ 2, 6 }, 2, 10);
        Assertions.assertArrayEquals(new int[]{ 2, 6, 10 }, intArray);
        intArray = ArrayUtils.add(new int[]{ 2, 6 }, 0, (-4));
        Assertions.assertArrayEquals(new int[]{ -4, 2, 6 }, intArray);
        intArray = ArrayUtils.add(new int[]{ 2, 6, 3 }, 2, 1);
        Assertions.assertArrayEquals(new int[]{ 2, 6, 1, 3 }, intArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new int[]{ 2, 6 }, 4, 10));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new int[]{ 2, 6 }, (-1), 10));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // long tests
        long[] longArray = ArrayUtils.add(new long[]{ 1L }, 0, 2L);
        Assertions.assertArrayEquals(new long[]{ 2L, 1L }, longArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(((long[]) (null)), (-1), 2L));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        longArray = ArrayUtils.add(new long[]{ 2L, 6L }, 2, 10L);
        Assertions.assertArrayEquals(new long[]{ 2L, 6L, 10L }, longArray);
        longArray = ArrayUtils.add(new long[]{ 2L, 6L }, 0, (-4L));
        Assertions.assertArrayEquals(new long[]{ -4L, 2L, 6L }, longArray);
        longArray = ArrayUtils.add(new long[]{ 2L, 6L, 3L }, 2, 1L);
        Assertions.assertArrayEquals(new long[]{ 2L, 6L, 1L, 3L }, longArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new long[]{ 2L, 6L }, 4, 10L));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new long[]{ 2L, 6L }, (-1), 10L));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // float tests
        float[] floatArray = ArrayUtils.add(new float[]{ 1.1F }, 0, 2.2F);
        Assertions.assertArrayEquals(new float[]{ 2.2F, 1.1F }, floatArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(((float[]) (null)), (-1), 2.2F));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        floatArray = ArrayUtils.add(new float[]{ 2.3F, 6.4F }, 2, 10.5F);
        Assertions.assertArrayEquals(new float[]{ 2.3F, 6.4F, 10.5F }, floatArray);
        floatArray = ArrayUtils.add(new float[]{ 2.6F, 6.7F }, 0, (-4.8F));
        Assertions.assertArrayEquals(new float[]{ -4.8F, 2.6F, 6.7F }, floatArray);
        floatArray = ArrayUtils.add(new float[]{ 2.9F, 6.0F, 0.3F }, 2, 1.0F);
        Assertions.assertArrayEquals(new float[]{ 2.9F, 6.0F, 1.0F, 0.3F }, floatArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new float[]{ 2.3F, 6.4F }, 4, 10.5F));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new float[]{ 2.3F, 6.4F }, (-1), 10.5F));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
        // double tests
        double[] doubleArray = ArrayUtils.add(new double[]{ 1.1 }, 0, 2.2);
        Assertions.assertArrayEquals(new double[]{ 2.2, 1.1 }, doubleArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(null, (-1), 2.2));
        Assertions.assertEquals("Index: -1, Length: 0", e.getMessage());
        doubleArray = ArrayUtils.add(new double[]{ 2.3, 6.4 }, 2, 10.5);
        Assertions.assertArrayEquals(new double[]{ 2.3, 6.4, 10.5 }, doubleArray);
        doubleArray = ArrayUtils.add(new double[]{ 2.6, 6.7 }, 0, (-4.8));
        Assertions.assertArrayEquals(new double[]{ -4.8, 2.6, 6.7 }, doubleArray);
        doubleArray = ArrayUtils.add(new double[]{ 2.9, 6.0, 0.3 }, 2, 1.0);
        Assertions.assertArrayEquals(new double[]{ 2.9, 6.0, 1.0, 0.3 }, doubleArray);
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new double[]{ 2.3, 6.4 }, 4, 10.5));
        Assertions.assertEquals("Index: 4, Length: 2", e.getMessage());
        e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(new double[]{ 2.3, 6.4 }, (-1), 10.5));
        Assertions.assertEquals("Index: -1, Length: 2", e.getMessage());
    }
}

