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
 * Tests ArrayUtils remove and removeElement methods.
 */
public class ArrayUtilsRemoveMultipleTest {
    @Test
    public void testRemoveAllObjectArray() {
        Object[] array;
        array = ArrayUtils.removeAll(new Object[]{ "a" }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b" }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c" }, 1, 2);
        Assertions.assertArrayEquals(new Object[]{ "a" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d" }, 1, 2);
        Assertions.assertArrayEquals(new Object[]{ "a", "d" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d" }, 0, 3);
        Assertions.assertArrayEquals(new Object[]{ "b", "c" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d" }, 0, 1, 3);
        Assertions.assertArrayEquals(new Object[]{ "c" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d", "e" }, 0, 1, 3);
        Assertions.assertArrayEquals(new Object[]{ "c", "e" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d", "e" }, 0, 2, 4);
        Assertions.assertArrayEquals(new Object[]{ "b", "d" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d" }, 0, 1, 3, 0, 1, 3);
        Assertions.assertArrayEquals(new Object[]{ "c" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d" }, 2, 1, 0, 3);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new Object[]{ "a", "b", "c", "d" }, 2, 0, 1, 3, 0, 2, 1, 3);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllObjectArrayRemoveNone() {
        final Object[] array1 = new Object[]{ "foo", "bar", "baz" };
        final Object[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(Object.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllObjectArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new Object[]{ "a", "b" }, (-1)));
    }

    @Test
    public void testRemoveAllObjectArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new Object[]{ "a", "b" }, 2));
    }

    @Test
    public void testRemoveAllNullObjectArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.remove(((Object[]) (null)), 0));
    }

    @Test
    public void testRemoveAllNumberArray() {
        final Number[] inarray = new Number[]{ Integer.valueOf(1), Long.valueOf(2L), Byte.valueOf(((byte) (3))) };
        Assertions.assertEquals(3, inarray.length);
        Number[] outarray;
        outarray = ArrayUtils.removeAll(inarray, 1);
        Assertions.assertArrayEquals(new Number[]{ Integer.valueOf(1), Byte.valueOf(((byte) (3))) }, outarray);
        Assertions.assertEquals(Number.class, outarray.getClass().getComponentType());
        outarray = ArrayUtils.removeAll(outarray, 1);
        Assertions.assertArrayEquals(new Number[]{ Integer.valueOf(1) }, outarray);
        Assertions.assertEquals(Number.class, outarray.getClass().getComponentType());
        outarray = ArrayUtils.removeAll(outarray, 0);
        Assertions.assertEquals(0, outarray.length);
        Assertions.assertEquals(Number.class, outarray.getClass().getComponentType());
        outarray = ArrayUtils.removeAll(inarray, 0, 1);
        Assertions.assertArrayEquals(new Number[]{ Byte.valueOf(((byte) (3))) }, outarray);
        Assertions.assertEquals(Number.class, outarray.getClass().getComponentType());
        outarray = ArrayUtils.removeAll(inarray, 0, 2);
        Assertions.assertArrayEquals(new Number[]{ Long.valueOf(2L) }, outarray);
        Assertions.assertEquals(Number.class, outarray.getClass().getComponentType());
        outarray = ArrayUtils.removeAll(inarray, 1, 2);
        Assertions.assertArrayEquals(new Number[]{ Integer.valueOf(1) }, outarray);
        Assertions.assertEquals(Number.class, outarray.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllBooleanArray() {
        boolean[] array;
        array = ArrayUtils.removeAll(new boolean[]{ true }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false }, 0);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false }, 1);
        Assertions.assertArrayEquals(new boolean[]{ true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true }, 1);
        Assertions.assertArrayEquals(new boolean[]{ true, true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, false }, 0, 1);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, false }, 0, 2);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, false }, 1, 2);
        Assertions.assertArrayEquals(new boolean[]{ true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true, false, true }, 0, 2, 4);
        Assertions.assertArrayEquals(new boolean[]{ false, false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true, false, true }, 1, 3);
        Assertions.assertArrayEquals(new boolean[]{ true, true, true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true, false, true }, 1, 3, 4);
        Assertions.assertArrayEquals(new boolean[]{ true, true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true, false, true, false, true }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new boolean[]{ false, false, false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true, false, true, false, true }, 1, 3, 5);
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new boolean[]{ true, false, true, false, true, false, true }, 0, 1, 2);
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllBooleanArrayRemoveNone() {
        final boolean[] array1 = new boolean[]{ true, false };
        final boolean[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(boolean.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllBooleanArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new boolean[]{ true, false }, (-1)));
    }

    @Test
    public void testRemoveAllBooleanArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new boolean[]{ true, false }, 2));
    }

    @Test
    public void testRemoveAllNullBooleanArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((boolean[]) (null)), 0));
    }

    @Test
    public void testRemoveAllByteArray() {
        byte[] array;
        array = ArrayUtils.removeAll(new byte[]{ 1 }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2 }, 0);
        Assertions.assertArrayEquals(new byte[]{ 2 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new byte[]{ 1 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new byte[]{ 1, 1 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2 }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3 }, 0, 1);
        Assertions.assertArrayEquals(new byte[]{ 3 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3 }, 1, 2);
        Assertions.assertArrayEquals(new byte[]{ 1 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3 }, 0, 2);
        Assertions.assertArrayEquals(new byte[]{ 2 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertArrayEquals(new byte[]{ 1, 3, 5 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3, 4, 5 }, 0, 2, 4);
        Assertions.assertArrayEquals(new byte[]{ 2, 4 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3, 4, 5, 6, 7 }, 1, 3, 5);
        Assertions.assertArrayEquals(new byte[]{ 1, 3, 5, 7 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new byte[]{ 1, 2, 3, 4, 5, 6, 7 }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new byte[]{ 2, 4, 6 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllByteArrayRemoveNone() {
        final byte[] array1 = new byte[]{ 1, 2 };
        final byte[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(byte.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllByteArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new byte[]{ 1, 2 }, (-1)));
    }

    @Test
    public void testRemoveAllByteArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new byte[]{ 1, 2 }, 2));
    }

    @Test
    public void testRemoveAllNullByteArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((byte[]) (null)), 0));
    }

    @Test
    public void testRemoveAllCharArray() {
        char[] array;
        array = ArrayUtils.removeAll(new char[]{ 'a' }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b' }, 0);
        Assertions.assertArrayEquals(new char[]{ 'b' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b' }, 1);
        Assertions.assertArrayEquals(new char[]{ 'a' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c' }, 1);
        Assertions.assertArrayEquals(new char[]{ 'a', 'c' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b' }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c' }, 0, 1);
        Assertions.assertArrayEquals(new char[]{ 'c' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c' }, 1, 2);
        Assertions.assertArrayEquals(new char[]{ 'a' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c' }, 0, 2);
        Assertions.assertArrayEquals(new char[]{ 'b' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c', 'd', 'e' }, 1, 3);
        Assertions.assertArrayEquals(new char[]{ 'a', 'c', 'e' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c', 'd', 'e' }, 0, 2, 4);
        Assertions.assertArrayEquals(new char[]{ 'b', 'd' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g' }, 1, 3, 5);
        Assertions.assertArrayEquals(new char[]{ 'a', 'c', 'e', 'g' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new char[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g' }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new char[]{ 'b', 'd', 'f' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllCharArrayRemoveNone() {
        final char[] array1 = new char[]{ 'a', 'b' };
        final char[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(char.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllCharArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new char[]{ 'a', 'b' }, (-1)));
    }

    @Test
    public void testRemoveAllCharArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new char[]{ 'a', 'b' }, 2));
    }

    @Test
    public void testRemoveAllNullCharArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((char[]) (null)), 0));
    }

    @Test
    public void testRemoveAllDoubleArray() {
        double[] array;
        array = ArrayUtils.removeAll(new double[]{ 1 }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2 }, 0);
        Assertions.assertArrayEquals(new double[]{ 2 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new double[]{ 1 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new double[]{ 1, 1 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2 }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3 }, 0, 1);
        Assertions.assertArrayEquals(new double[]{ 3 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3 }, 1, 2);
        Assertions.assertArrayEquals(new double[]{ 1 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3 }, 0, 2);
        Assertions.assertArrayEquals(new double[]{ 2 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertArrayEquals(new double[]{ 1, 3, 5 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3, 4, 5 }, 0, 2, 4);
        Assertions.assertArrayEquals(new double[]{ 2, 4 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3, 4, 5, 6, 7 }, 1, 3, 5);
        Assertions.assertArrayEquals(new double[]{ 1, 3, 5, 7 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new double[]{ 1, 2, 3, 4, 5, 6, 7 }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new double[]{ 2, 4, 6 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllDoubleArrayRemoveNone() {
        final double[] array1 = new double[]{ 1, 2 };
        final double[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(double.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllDoubleArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new double[]{ 1, 2 }, (-1)));
    }

    @Test
    public void testRemoveAllDoubleArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new double[]{ 1, 2 }, 2));
    }

    @Test
    public void testRemoveAllNullDoubleArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((double[]) (null)), 0));
    }

    @Test
    public void testRemoveAllFloatArray() {
        float[] array;
        array = ArrayUtils.removeAll(new float[]{ 1 }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2 }, 0);
        Assertions.assertArrayEquals(new float[]{ 2 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new float[]{ 1 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new float[]{ 1, 1 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2 }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3 }, 0, 1);
        Assertions.assertArrayEquals(new float[]{ 3 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3 }, 1, 2);
        Assertions.assertArrayEquals(new float[]{ 1 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3 }, 0, 2);
        Assertions.assertArrayEquals(new float[]{ 2 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertArrayEquals(new float[]{ 1, 3, 5 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3, 4, 5 }, 0, 2, 4);
        Assertions.assertArrayEquals(new float[]{ 2, 4 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3, 4, 5, 6, 7 }, 1, 3, 5);
        Assertions.assertArrayEquals(new float[]{ 1, 3, 5, 7 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new float[]{ 1, 2, 3, 4, 5, 6, 7 }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new float[]{ 2, 4, 6 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllFloatArrayRemoveNone() {
        final float[] array1 = new float[]{ 1, 2 };
        final float[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(float.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllFloatArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new float[]{ 1, 2 }, (-1)));
    }

    @Test
    public void testRemoveAllFloatArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new float[]{ 1, 2 }, 2));
    }

    @Test
    public void testRemoveAllNullFloatArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((float[]) (null)), 0));
    }

    @Test
    public void testRemoveAllIntArray() {
        int[] array;
        array = ArrayUtils.removeAll(ArrayUtils.EMPTY_INT_ARRAY, ArrayUtils.EMPTY_INT_ARRAY);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        array = ArrayUtils.removeAll(new int[]{ 1 }, ArrayUtils.EMPTY_INT_ARRAY);
        Assertions.assertArrayEquals(new int[]{ 1 }, array);
        array = ArrayUtils.removeAll(new int[]{ 1 }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2 }, 0);
        Assertions.assertArrayEquals(new int[]{ 2 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new int[]{ 1 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new int[]{ 1, 1 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2 }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3 }, 0, 1);
        Assertions.assertArrayEquals(new int[]{ 3 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3 }, 1, 2);
        Assertions.assertArrayEquals(new int[]{ 1 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3 }, 0, 2);
        Assertions.assertArrayEquals(new int[]{ 2 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertArrayEquals(new int[]{ 1, 3, 5 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3, 4, 5 }, 0, 2, 4);
        Assertions.assertArrayEquals(new int[]{ 2, 4 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3, 4, 5, 6, 7 }, 1, 3, 5);
        Assertions.assertArrayEquals(new int[]{ 1, 3, 5, 7 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new int[]{ 1, 2, 3, 4, 5, 6, 7 }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new int[]{ 2, 4, 6 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllIntArrayRemoveNone() {
        final int[] array1 = new int[]{ 1, 2 };
        final int[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(int.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllIntArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new int[]{ 1, 2 }, (-1)));
    }

    @Test
    public void testRemoveAllIntArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new int[]{ 1, 2 }, 2));
    }

    @Test
    public void testRemoveAllNullIntArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((int[]) (null)), 0));
    }

    @Test
    public void testRemoveAllLongArray() {
        long[] array;
        array = ArrayUtils.removeAll(new long[]{ 1 }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2 }, 0);
        Assertions.assertArrayEquals(new long[]{ 2 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new long[]{ 1 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new long[]{ 1, 1 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2 }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3 }, 0, 1);
        Assertions.assertArrayEquals(new long[]{ 3 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3 }, 1, 2);
        Assertions.assertArrayEquals(new long[]{ 1 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3 }, 0, 2);
        Assertions.assertArrayEquals(new long[]{ 2 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertArrayEquals(new long[]{ 1, 3, 5 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3, 4, 5 }, 0, 2, 4);
        Assertions.assertArrayEquals(new long[]{ 2, 4 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3, 4, 5, 6, 7 }, 1, 3, 5);
        Assertions.assertArrayEquals(new long[]{ 1, 3, 5, 7 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new long[]{ 1, 2, 3, 4, 5, 6, 7 }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new long[]{ 2, 4, 6 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllLongArrayRemoveNone() {
        final long[] array1 = new long[]{ 1, 2 };
        final long[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(long.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllLongArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new long[]{ 1, 2 }, (-1)));
    }

    @Test
    public void testRemoveAllLongArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new long[]{ 1, 2 }, 2));
    }

    @Test
    public void testRemoveAllNullLongArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((long[]) (null)), 0));
    }

    @Test
    public void testRemoveAllShortArray() {
        short[] array;
        array = ArrayUtils.removeAll(new short[]{ 1 }, 0);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2 }, 0);
        Assertions.assertArrayEquals(new short[]{ 2 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new short[]{ 1 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new short[]{ 1, 1 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2 }, 0, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3 }, 0, 1);
        Assertions.assertArrayEquals(new short[]{ 3 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3 }, 1, 2);
        Assertions.assertArrayEquals(new short[]{ 1 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3 }, 0, 2);
        Assertions.assertArrayEquals(new short[]{ 2 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertArrayEquals(new short[]{ 1, 3, 5 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3, 4, 5 }, 0, 2, 4);
        Assertions.assertArrayEquals(new short[]{ 2, 4 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3, 4, 5, 6, 7 }, 1, 3, 5);
        Assertions.assertArrayEquals(new short[]{ 1, 3, 5, 7 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeAll(new short[]{ 1, 2, 3, 4, 5, 6, 7 }, 0, 2, 4, 6);
        Assertions.assertArrayEquals(new short[]{ 2, 4, 6 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllShortArrayRemoveNone() {
        final short[] array1 = new short[]{ 1, 2 };
        final short[] array2 = ArrayUtils.removeAll(array1);
        Assertions.assertNotSame(array1, array2);
        Assertions.assertArrayEquals(array1, array2);
        Assertions.assertEquals(short.class, array2.getClass().getComponentType());
    }

    @Test
    public void testRemoveAllShortArrayNegativeIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new short[]{ 1, 2 }, (-1), 0));
    }

    @Test
    public void testRemoveAllShortArrayOutOfBoundsIndex() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(new short[]{ 1, 2 }, 2, 0));
    }

    @Test
    public void testRemoveAllNullShortArray() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.removeAll(((short[]) (null)), 0));
    }

    @Test
    public void testRemoveElementsObjectArray() {
        Object[] array;
        array = ArrayUtils.removeElements(((Object[]) (null)), "a");
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_OBJECT_ARRAY, "a");
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a" }, "a");
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b" }, "a");
        Assertions.assertArrayEquals(new Object[]{ "b" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b", "a" }, "a");
        Assertions.assertArrayEquals(new Object[]{ "b", "a" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((Object[]) (null)), "a", "b");
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_OBJECT_ARRAY, "a", "b");
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a" }, "a", "b");
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_OBJECT_ARRAY, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b" }, "a", "c");
        Assertions.assertArrayEquals(new Object[]{ "b" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b", "a" }, "a");
        Assertions.assertArrayEquals(new Object[]{ "b", "a" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b", "a" }, "a", "b");
        Assertions.assertArrayEquals(new Object[]{ "a" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b", "a" }, "a", "a");
        Assertions.assertArrayEquals(new Object[]{ "b" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new Object[]{ "a", "b", "a" }, "a", "a", "a", "a");
        Assertions.assertArrayEquals(new Object[]{ "b" }, array);
        Assertions.assertEquals(Object.class, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveElementBooleanArray() {
        boolean[] array;
        array = ArrayUtils.removeElements(((boolean[]) (null)), true);
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_BOOLEAN_ARRAY, true);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true }, true);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false }, true);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false, true }, true);
        Assertions.assertArrayEquals(new boolean[]{ false, true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((boolean[]) (null)), true, false);
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_BOOLEAN_ARRAY, true, false);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true }, true, false);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false }, true, false);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false }, true, true);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false, true }, true, false);
        Assertions.assertArrayEquals(new boolean[]{ true }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false, true }, true, true);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new boolean[]{ true, false, true }, true, true, true, true);
        Assertions.assertArrayEquals(new boolean[]{ false }, array);
        Assertions.assertEquals(Boolean.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveElementByteArray() {
        byte[] array;
        array = ArrayUtils.removeElements(((byte[]) (null)), ((byte) (1)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_BYTE_ARRAY, ((byte) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1 }, ((byte) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2 }, ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 2 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2, 1 }, ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 2, 1 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((byte[]) (null)), ((byte) (1)), ((byte) (2)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_BYTE_ARRAY, ((byte) (1)), ((byte) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1 }, ((byte) (1)), ((byte) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2 }, ((byte) (1)), ((byte) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2 }, ((byte) (1)), ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 2 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2, 1 }, ((byte) (1)), ((byte) (2)));
        Assertions.assertArrayEquals(new byte[]{ 1 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2, 1 }, ((byte) (1)), ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 2 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new byte[]{ 1, 2, 1 }, ((byte) (1)), ((byte) (1)), ((byte) (1)), ((byte) (1)));
        Assertions.assertArrayEquals(new byte[]{ 2 }, array);
        Assertions.assertEquals(Byte.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveElementCharArray() {
        char[] array;
        array = ArrayUtils.removeElements(((char[]) (null)), 'a');
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_CHAR_ARRAY, 'a');
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a' }, 'a');
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b' }, 'a');
        Assertions.assertArrayEquals(new char[]{ 'b' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b', 'a' }, 'a');
        Assertions.assertArrayEquals(new char[]{ 'b', 'a' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((char[]) (null)), 'a', 'b');
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_CHAR_ARRAY, 'a', 'b');
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a' }, 'a', 'b');
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b' }, 'a', 'b');
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CHAR_ARRAY, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b' }, 'a', 'a');
        Assertions.assertArrayEquals(new char[]{ 'b' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b', 'a' }, 'a', 'b');
        Assertions.assertArrayEquals(new char[]{ 'a' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b', 'a' }, 'a', 'a');
        Assertions.assertArrayEquals(new char[]{ 'b' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new char[]{ 'a', 'b', 'a' }, 'a', 'a', 'a', 'a');
        Assertions.assertArrayEquals(new char[]{ 'b' }, array);
        Assertions.assertEquals(Character.TYPE, array.getClass().getComponentType());
    }

    @Test
    @SuppressWarnings("cast")
    public void testRemoveElementDoubleArray() {
        double[] array;
        array = ArrayUtils.removeElements(((double[]) (null)), ((double) (1)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_DOUBLE_ARRAY, ((double) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1 }, ((double) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2 }, ((double) (1)));
        Assertions.assertArrayEquals(new double[]{ 2 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2, 1 }, ((double) (1)));
        Assertions.assertArrayEquals(new double[]{ 2, 1 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((double[]) (null)), ((double) (1)), ((double) (2)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_DOUBLE_ARRAY, ((double) (1)), ((double) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1 }, ((double) (1)), ((double) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2 }, ((double) (1)), ((double) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_DOUBLE_ARRAY, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2 }, ((double) (1)), ((double) (1)));
        Assertions.assertArrayEquals(new double[]{ 2 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2, 1 }, ((double) (1)), ((double) (2)));
        Assertions.assertArrayEquals(new double[]{ 1 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2, 1 }, ((double) (1)), ((double) (1)));
        Assertions.assertArrayEquals(new double[]{ 2 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new double[]{ 1, 2, 1 }, ((double) (1)), ((double) (1)), ((double) (1)), ((double) (1)));
        Assertions.assertArrayEquals(new double[]{ 2 }, array);
        Assertions.assertEquals(Double.TYPE, array.getClass().getComponentType());
    }

    @Test
    @SuppressWarnings("cast")
    public void testRemoveElementFloatArray() {
        float[] array;
        array = ArrayUtils.removeElements(((float[]) (null)), ((float) (1)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_FLOAT_ARRAY, ((float) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1 }, ((float) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2 }, ((float) (1)));
        Assertions.assertArrayEquals(new float[]{ 2 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2, 1 }, ((float) (1)));
        Assertions.assertArrayEquals(new float[]{ 2, 1 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((float[]) (null)), ((float) (1)), ((float) (1)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_FLOAT_ARRAY, ((float) (1)), ((float) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1 }, ((float) (1)), ((float) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2 }, ((float) (1)), ((float) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_FLOAT_ARRAY, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2 }, ((float) (1)), ((float) (1)));
        Assertions.assertArrayEquals(new float[]{ 2 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2, 1 }, ((float) (1)), ((float) (1)));
        Assertions.assertArrayEquals(new float[]{ 2 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2, 1 }, ((float) (1)), ((float) (2)));
        Assertions.assertArrayEquals(new float[]{ 1 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new float[]{ 1, 2, 1 }, ((float) (1)), ((float) (1)), ((float) (1)), ((float) (1)));
        Assertions.assertArrayEquals(new float[]{ 2 }, array);
        Assertions.assertEquals(Float.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveElementIntArray() {
        int[] array;
        array = ArrayUtils.removeElements(((int[]) (null)), 1);
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_INT_ARRAY, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1 }, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2 }, 1);
        Assertions.assertArrayEquals(new int[]{ 2 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2, 1 }, 1);
        Assertions.assertArrayEquals(new int[]{ 2, 1 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((int[]) (null)), 1);
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_INT_ARRAY, 1, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1 }, 1, 1);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2 }, 1, 2);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2 }, 1, 1);
        Assertions.assertArrayEquals(new int[]{ 2 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2, 1 }, 1, 2);
        Assertions.assertArrayEquals(new int[]{ 1 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2, 1 }, 1, 1);
        Assertions.assertArrayEquals(new int[]{ 2 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new int[]{ 1, 2, 1 }, 1, 1, 1, 1);
        Assertions.assertArrayEquals(new int[]{ 2 }, array);
        Assertions.assertEquals(Integer.TYPE, array.getClass().getComponentType());
    }

    @Test
    @SuppressWarnings("cast")
    public void testRemoveElementLongArray() {
        long[] array;
        array = ArrayUtils.removeElements(((long[]) (null)), 1L);
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_LONG_ARRAY, 1L);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1 }, 1L);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2 }, 1L);
        Assertions.assertArrayEquals(new long[]{ 2 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2, 1 }, 1L);
        Assertions.assertArrayEquals(new long[]{ 2, 1 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((long[]) (null)), 1L, 1L);
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_LONG_ARRAY, 1L, 1L);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1 }, 1L, 1L);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2 }, 1L, 2L);
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2 }, 1L, 1L);
        Assertions.assertArrayEquals(new long[]{ 2 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2, 1 }, 1L, 1L);
        Assertions.assertArrayEquals(new long[]{ 2 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2, 1 }, 1L, 2L);
        Assertions.assertArrayEquals(new long[]{ 1 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new long[]{ 1, 2, 1 }, 1L, 1L, 1L, 1L);
        Assertions.assertArrayEquals(new long[]{ 2 }, array);
        Assertions.assertEquals(Long.TYPE, array.getClass().getComponentType());
    }

    @Test
    public void testRemoveElementShortArray() {
        short[] array;
        array = ArrayUtils.removeElements(((short[]) (null)), ((short) (1)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_SHORT_ARRAY, ((short) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1 }, ((short) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2 }, ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 2 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2, 1 }, ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 2, 1 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(((short[]) (null)), ((short) (1)), ((short) (1)));
        Assertions.assertNull(array);
        array = ArrayUtils.removeElements(ArrayUtils.EMPTY_SHORT_ARRAY, ((short) (1)), ((short) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1 }, ((short) (1)), ((short) (1)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2 }, ((short) (1)), ((short) (2)));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_SHORT_ARRAY, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2 }, ((short) (1)), ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 2 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2, 1 }, ((short) (1)), ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 2 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2, 1 }, ((short) (1)), ((short) (2)));
        Assertions.assertArrayEquals(new short[]{ 1 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
        array = ArrayUtils.removeElements(new short[]{ 1, 2, 1 }, ((short) (1)), ((short) (1)), ((short) (1)), ((short) (1)));
        Assertions.assertArrayEquals(new short[]{ 2 }, array);
        Assertions.assertEquals(Short.TYPE, array.getClass().getComponentType());
    }
}

