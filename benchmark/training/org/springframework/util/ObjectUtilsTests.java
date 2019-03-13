/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.util;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link ObjectUtils}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Sam Brannen
 */
public class ObjectUtilsTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void isCheckedException() {
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCheckedException(new Exception()));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCheckedException(new SQLException()));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCheckedException(new RuntimeException()));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCheckedException(new IllegalArgumentException("")));
        // Any Throwable other than RuntimeException and Error
        // has to be considered checked according to the JLS.
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCheckedException(new Throwable()));
    }

    @Test
    public void isCompatibleWithThrowsClause() {
        Class<?>[] empty = new Class<?>[0];
        Class<?>[] exception = new Class<?>[]{ Exception.class };
        Class<?>[] sqlAndIO = new Class<?>[]{ SQLException.class, IOException.class };
        Class<?>[] throwable = new Class<?>[]{ Throwable.class };
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new RuntimeException()));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new RuntimeException(), empty));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new RuntimeException(), exception));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new RuntimeException(), sqlAndIO));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new RuntimeException(), throwable));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Exception()));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Exception(), empty));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Exception(), exception));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Exception(), sqlAndIO));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Exception(), throwable));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new SQLException()));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new SQLException(), empty));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new SQLException(), exception));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new SQLException(), sqlAndIO));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new SQLException(), throwable));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Throwable()));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Throwable(), empty));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Throwable(), exception));
        Assert.assertFalse(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Throwable(), sqlAndIO));
        Assert.assertTrue(ObjectUtils.ObjectUtils.isCompatibleWithThrowsClause(new Throwable(), throwable));
    }

    @Test
    public void isEmptyNull() {
        Assert.assertTrue(isEmpty(null));
    }

    @Test
    public void isEmptyArray() {
        Assert.assertTrue(isEmpty(new char[0]));
        Assert.assertTrue(isEmpty(new Object[0]));
        Assert.assertTrue(isEmpty(new Integer[0]));
        Assert.assertFalse(isEmpty(new int[]{ 42 }));
        Assert.assertFalse(isEmpty(new Integer[]{ 42 }));
    }

    @Test
    public void isEmptyCollection() {
        Assert.assertTrue(isEmpty(Collections.emptyList()));
        Assert.assertTrue(isEmpty(Collections.emptySet()));
        Set<String> set = new HashSet<>();
        set.add("foo");
        Assert.assertFalse(isEmpty(set));
        Assert.assertFalse(isEmpty(Arrays.asList("foo")));
    }

    @Test
    public void isEmptyMap() {
        Assert.assertTrue(isEmpty(Collections.emptyMap()));
        HashMap<String, Object> map = new HashMap<>();
        map.put("foo", 42L);
        Assert.assertFalse(isEmpty(map));
    }

    @Test
    public void isEmptyCharSequence() {
        Assert.assertTrue(isEmpty(new StringBuilder()));
        Assert.assertTrue(isEmpty(""));
        Assert.assertFalse(isEmpty(new StringBuilder("foo")));
        Assert.assertFalse(isEmpty("   "));
        Assert.assertFalse(isEmpty("\t"));
        Assert.assertFalse(isEmpty("foo"));
    }

    @Test
    public void isEmptyUnsupportedObjectType() {
        Assert.assertFalse(isEmpty(42L));
        Assert.assertFalse(isEmpty(new Object()));
    }

    @Test
    public void toObjectArray() {
        int[] a = new int[]{ 1, 2, 3, 4, 5 };
        Integer[] wrapper = ((Integer[]) (ObjectUtils.ObjectUtils.toObjectArray(a)));
        Assert.assertTrue(((wrapper.length) == 5));
        for (int i = 0; i < (wrapper.length); i++) {
            Assert.assertEquals(a[i], wrapper[i].intValue());
        }
    }

    @Test
    public void toObjectArrayWithNull() {
        Object[] objects = ObjectUtils.ObjectUtils.toObjectArray(null);
        Assert.assertNotNull(objects);
        Assert.assertEquals(0, objects.length);
    }

    @Test
    public void toObjectArrayWithEmptyPrimitiveArray() {
        Object[] objects = ObjectUtils.ObjectUtils.toObjectArray(new byte[]{  });
        Assert.assertNotNull(objects);
        Assert.assertEquals(0, objects.length);
    }

    @Test
    public void toObjectArrayWithNonArrayType() {
        exception.expect(IllegalArgumentException.class);
        ObjectUtils.ObjectUtils.toObjectArray("Not an []");
    }

    @Test
    public void toObjectArrayWithNonPrimitiveArray() {
        String[] source = new String[]{ "Bingo" };
        Assert.assertArrayEquals(source, ObjectUtils.ObjectUtils.toObjectArray(source));
    }

    @Test
    public void addObjectToArraySunnyDay() {
        String[] array = new String[]{ "foo", "bar" };
        String newElement = "baz";
        Object[] newArray = ObjectUtils.ObjectUtils.addObjectToArray(array, newElement);
        Assert.assertEquals(3, newArray.length);
        Assert.assertEquals(newElement, newArray[2]);
    }

    @Test
    public void addObjectToArrayWhenEmpty() {
        String[] array = new String[0];
        String newElement = "foo";
        String[] newArray = ObjectUtils.ObjectUtils.addObjectToArray(array, newElement);
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals(newElement, newArray[0]);
    }

    @Test
    public void addObjectToSingleNonNullElementArray() {
        String existingElement = "foo";
        String[] array = new String[]{ existingElement };
        String newElement = "bar";
        String[] newArray = ObjectUtils.ObjectUtils.addObjectToArray(array, newElement);
        Assert.assertEquals(2, newArray.length);
        Assert.assertEquals(existingElement, newArray[0]);
        Assert.assertEquals(newElement, newArray[1]);
    }

    @Test
    public void addObjectToSingleNullElementArray() {
        String[] array = new String[]{ null };
        String newElement = "bar";
        String[] newArray = ObjectUtils.ObjectUtils.addObjectToArray(array, newElement);
        Assert.assertEquals(2, newArray.length);
        Assert.assertEquals(null, newArray[0]);
        Assert.assertEquals(newElement, newArray[1]);
    }

    @Test
    public void addObjectToNullArray() throws Exception {
        String newElement = "foo";
        String[] newArray = ObjectUtils.ObjectUtils.addObjectToArray(null, newElement);
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals(newElement, newArray[0]);
    }

    @Test
    public void addNullObjectToNullArray() throws Exception {
        Object[] newArray = ObjectUtils.ObjectUtils.addObjectToArray(null, null);
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals(null, newArray[0]);
    }

    @Test
    public void nullSafeEqualsWithArrays() throws Exception {
        Assert.assertTrue(ObjectUtils.ObjectUtils.nullSafeEquals(new String[]{ "a", "b", "c" }, new String[]{ "a", "b", "c" }));
        Assert.assertTrue(ObjectUtils.ObjectUtils.nullSafeEquals(new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 3 }));
    }

    @Test
    @Deprecated
    public void hashCodeWithBooleanFalse() {
        int expected = Boolean.FALSE.hashCode();
        Assert.assertEquals(expected, ObjectUtils.ObjectUtils.hashCode(false));
    }

    @Test
    @Deprecated
    public void hashCodeWithBooleanTrue() {
        int expected = Boolean.TRUE.hashCode();
        Assert.assertEquals(expected, ObjectUtils.ObjectUtils.hashCode(true));
    }

    @Test
    @Deprecated
    public void hashCodeWithDouble() {
        double dbl = 9830.43;
        int expected = new Double(dbl).hashCode();
        Assert.assertEquals(expected, ObjectUtils.ObjectUtils.hashCode(dbl));
    }

    @Test
    @Deprecated
    public void hashCodeWithFloat() {
        float flt = 34.8F;
        int expected = new Float(flt).hashCode();
        Assert.assertEquals(expected, ObjectUtils.ObjectUtils.hashCode(flt));
    }

    @Test
    @Deprecated
    public void hashCodeWithLong() {
        long lng = 883L;
        int expected = new Long(lng).hashCode();
        Assert.assertEquals(expected, ObjectUtils.ObjectUtils.hashCode(lng));
    }

    @Test
    public void identityToString() {
        Object obj = new Object();
        String expected = ((obj.getClass().getName()) + "@") + (ObjectUtils.ObjectUtils.getIdentityHexString(obj));
        String actual = ObjectUtils.ObjectUtils.identityToString(obj);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void identityToStringWithNullObject() {
        Assert.assertEquals("", ObjectUtils.ObjectUtils.identityToString(null));
    }

    @Test
    public void isArrayOfPrimitivesWithBooleanArray() {
        Assert.assertTrue(ClassUtils.isPrimitiveArray(boolean[].class));
    }

    @Test
    public void isArrayOfPrimitivesWithObjectArray() {
        Assert.assertFalse(ClassUtils.isPrimitiveArray(Object[].class));
    }

    @Test
    public void isArrayOfPrimitivesWithNonArray() {
        Assert.assertFalse(ClassUtils.isPrimitiveArray(String.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithBooleanPrimitiveClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(boolean.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithBooleanWrapperClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Boolean.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithBytePrimitiveClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(byte.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithByteWrapperClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Byte.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithCharacterClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Character.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithCharClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(char.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithDoublePrimitiveClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(double.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithDoubleWrapperClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Double.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithFloatPrimitiveClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(float.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithFloatWrapperClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Float.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithIntClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(int.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithIntegerClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithLongPrimitiveClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(long.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithLongWrapperClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Long.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithNonPrimitiveOrWrapperClass() {
        Assert.assertFalse(ClassUtils.isPrimitiveOrWrapper(Object.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithShortPrimitiveClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(short.class));
    }

    @Test
    public void isPrimitiveOrWrapperWithShortWrapperClass() {
        Assert.assertTrue(ClassUtils.isPrimitiveOrWrapper(Short.class));
    }

    @Test
    public void nullSafeHashCodeWithBooleanArray() {
        int expected = (31 * 7) + (Boolean.TRUE.hashCode());
        expected = (31 * expected) + (Boolean.FALSE.hashCode());
        boolean[] array = new boolean[]{ true, false };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithBooleanArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((boolean[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithByteArray() {
        int expected = (31 * 7) + 8;
        expected = (31 * expected) + 10;
        byte[] array = new byte[]{ 8, 10 };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithByteArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((byte[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithCharArray() {
        int expected = (31 * 7) + 'a';
        expected = (31 * expected) + 'E';
        char[] array = new char[]{ 'a', 'E' };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithCharArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((char[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithDoubleArray() {
        long bits = Double.doubleToLongBits(8449.65);
        int expected = (31 * 7) + ((int) (bits ^ (bits >>> 32)));
        bits = Double.doubleToLongBits(9944.923);
        expected = (31 * expected) + ((int) (bits ^ (bits >>> 32)));
        double[] array = new double[]{ 8449.65, 9944.923 };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithDoubleArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((double[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithFloatArray() {
        int expected = (31 * 7) + (Float.floatToIntBits(9.6F));
        expected = (31 * expected) + (Float.floatToIntBits(7.4F));
        float[] array = new float[]{ 9.6F, 7.4F };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithFloatArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((float[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithIntArray() {
        int expected = (31 * 7) + 884;
        expected = (31 * expected) + 340;
        int[] array = new int[]{ 884, 340 };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithIntArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((int[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithLongArray() {
        long lng = 7993L;
        int expected = (31 * 7) + ((int) (lng ^ (lng >>> 32)));
        lng = 84320L;
        expected = (31 * expected) + ((int) (lng ^ (lng >>> 32)));
        long[] array = new long[]{ 7993L, 84320L };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithLongArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((long[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithObject() {
        String str = "Luke";
        Assert.assertEquals(str.hashCode(), ObjectUtils.ObjectUtils.nullSafeHashCode(str));
    }

    @Test
    public void nullSafeHashCodeWithObjectArray() {
        int expected = (31 * 7) + ("Leia".hashCode());
        expected = (31 * expected) + ("Han".hashCode());
        Object[] array = new Object[]{ "Leia", "Han" };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithObjectArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((Object[]) (null))));
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingBooleanArray() {
        Object array = new boolean[]{ true, false };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((boolean[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingByteArray() {
        Object array = new byte[]{ 6, 39 };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((byte[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingCharArray() {
        Object array = new char[]{ 'l', 'M' };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((char[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingDoubleArray() {
        Object array = new double[]{ 68930.993, 9022.009 };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((double[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingFloatArray() {
        Object array = new float[]{ 9.9F, 9.54F };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((float[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingIntArray() {
        Object array = new int[]{ 89, 32 };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((int[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingLongArray() {
        Object array = new long[]{ 4389, 320 };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((long[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingObjectArray() {
        Object array = new Object[]{ "Luke", "Anakin" };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((Object[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectBeingShortArray() {
        Object array = new short[]{ 5, 3 };
        int expected = ObjectUtils.ObjectUtils.nullSafeHashCode(((short[]) (array)));
        assertEqualHashCodes(expected, array);
    }

    @Test
    public void nullSafeHashCodeWithObjectEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((Object) (null))));
    }

    @Test
    public void nullSafeHashCodeWithShortArray() {
        int expected = (31 * 7) + 70;
        expected = (31 * expected) + 8;
        short[] array = new short[]{ 70, 8 };
        int actual = ObjectUtils.ObjectUtils.nullSafeHashCode(array);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSafeHashCodeWithShortArrayEqualToNull() {
        Assert.assertEquals(0, ObjectUtils.ObjectUtils.nullSafeHashCode(((short[]) (null))));
    }

    @Test
    public void nullSafeToStringWithBooleanArray() {
        boolean[] array = new boolean[]{ true, false };
        Assert.assertEquals("{true, false}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithBooleanArrayBeingEmpty() {
        boolean[] array = new boolean[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithBooleanArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((boolean[]) (null))));
    }

    @Test
    public void nullSafeToStringWithByteArray() {
        byte[] array = new byte[]{ 5, 8 };
        Assert.assertEquals("{5, 8}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithByteArrayBeingEmpty() {
        byte[] array = new byte[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithByteArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((byte[]) (null))));
    }

    @Test
    public void nullSafeToStringWithCharArray() {
        char[] array = new char[]{ 'A', 'B' };
        Assert.assertEquals("{'A', 'B'}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithCharArrayBeingEmpty() {
        char[] array = new char[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithCharArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((char[]) (null))));
    }

    @Test
    public void nullSafeToStringWithDoubleArray() {
        double[] array = new double[]{ 8594.93, 8594023.95 };
        Assert.assertEquals("{8594.93, 8594023.95}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithDoubleArrayBeingEmpty() {
        double[] array = new double[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithDoubleArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((double[]) (null))));
    }

    @Test
    public void nullSafeToStringWithFloatArray() {
        float[] array = new float[]{ 8.6F, 43.8F };
        Assert.assertEquals("{8.6, 43.8}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithFloatArrayBeingEmpty() {
        float[] array = new float[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithFloatArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((float[]) (null))));
    }

    @Test
    public void nullSafeToStringWithIntArray() {
        int[] array = new int[]{ 9, 64 };
        Assert.assertEquals("{9, 64}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithIntArrayBeingEmpty() {
        int[] array = new int[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithIntArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((int[]) (null))));
    }

    @Test
    public void nullSafeToStringWithLongArray() {
        long[] array = new long[]{ 434L, 23423L };
        Assert.assertEquals("{434, 23423}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithLongArrayBeingEmpty() {
        long[] array = new long[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithLongArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((long[]) (null))));
    }

    @Test
    public void nullSafeToStringWithPlainOldString() {
        Assert.assertEquals("I shoh love tha taste of mangoes", ObjectUtils.ObjectUtils.nullSafeToString("I shoh love tha taste of mangoes"));
    }

    @Test
    public void nullSafeToStringWithObjectArray() {
        Object[] array = new Object[]{ "Han", Long.valueOf(43) };
        Assert.assertEquals("{Han, 43}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithObjectArrayBeingEmpty() {
        Object[] array = new Object[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithObjectArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((Object[]) (null))));
    }

    @Test
    public void nullSafeToStringWithShortArray() {
        short[] array = new short[]{ 7, 9 };
        Assert.assertEquals("{7, 9}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithShortArrayBeingEmpty() {
        short[] array = new short[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithShortArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((short[]) (null))));
    }

    @Test
    public void nullSafeToStringWithStringArray() {
        String[] array = new String[]{ "Luke", "Anakin" };
        Assert.assertEquals("{Luke, Anakin}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithStringArrayBeingEmpty() {
        String[] array = new String[]{  };
        Assert.assertEquals("{}", ObjectUtils.ObjectUtils.nullSafeToString(array));
    }

    @Test
    public void nullSafeToStringWithStringArrayEqualToNull() {
        Assert.assertEquals("null", ObjectUtils.ObjectUtils.nullSafeToString(((String[]) (null))));
    }

    @Test
    public void containsConstant() {
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "FOO"), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "foo"), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "BaR"), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "bar"), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "BAZ"), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "baz"), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "BOGUS"), CoreMatchers.is(false));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "FOO", true), CoreMatchers.is(true));
        Assert.assertThat(ObjectUtils.ObjectUtils.containsConstant(ObjectUtilsTests.Tropes.values(), "foo", true), CoreMatchers.is(false));
    }

    @Test
    public void caseInsensitiveValueOf() {
        Assert.assertThat(ObjectUtils.ObjectUtils.caseInsensitiveValueOf(ObjectUtilsTests.Tropes.values(), "foo"), CoreMatchers.is(ObjectUtilsTests.Tropes.FOO));
        Assert.assertThat(ObjectUtils.ObjectUtils.caseInsensitiveValueOf(ObjectUtilsTests.Tropes.values(), "BAR"), CoreMatchers.is(ObjectUtilsTests.Tropes.BAR));
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.is("Constant [bogus] does not exist in enum type org.springframework.util.ObjectUtilsTests$Tropes"));
        ObjectUtils.ObjectUtils.caseInsensitiveValueOf(ObjectUtilsTests.Tropes.values(), "bogus");
    }

    enum Tropes {

        FOO,
        BAR,
        baz;}
}

