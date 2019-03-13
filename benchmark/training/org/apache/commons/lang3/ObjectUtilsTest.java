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


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.text.StrBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.ObjectUtils}.
 */
// deliberate use of deprecated code
@SuppressWarnings("deprecation")
public class ObjectUtilsTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String[] NON_EMPTY_ARRAY = new String[]{ ObjectUtilsTest.FOO, ObjectUtilsTest.BAR };

    private static final List<String> NON_EMPTY_LIST = Arrays.asList(ObjectUtilsTest.NON_EMPTY_ARRAY);

    private static final Set<String> NON_EMPTY_SET = new HashSet<>(ObjectUtilsTest.NON_EMPTY_LIST);

    private static final Map<String, String> NON_EMPTY_MAP = new HashMap<>();

    static {
        ObjectUtilsTest.NON_EMPTY_MAP.put(ObjectUtilsTest.FOO, ObjectUtilsTest.BAR);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new ObjectUtils());
        final Constructor<?>[] cons = ObjectUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(ObjectUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(ObjectUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIsEmpty() {
        Assertions.assertTrue(ObjectUtils.isEmpty(null));
        Assertions.assertTrue(ObjectUtils.isEmpty(""));
        Assertions.assertTrue(ObjectUtils.isEmpty(new int[]{  }));
        Assertions.assertTrue(ObjectUtils.isEmpty(Collections.emptyList()));
        Assertions.assertTrue(ObjectUtils.isEmpty(Collections.emptySet()));
        Assertions.assertTrue(ObjectUtils.isEmpty(Collections.emptyMap()));
        Assertions.assertFalse(ObjectUtils.isEmpty("  "));
        Assertions.assertFalse(ObjectUtils.isEmpty("ab"));
        Assertions.assertFalse(ObjectUtils.isEmpty(ObjectUtilsTest.NON_EMPTY_ARRAY));
        Assertions.assertFalse(ObjectUtils.isEmpty(ObjectUtilsTest.NON_EMPTY_LIST));
        Assertions.assertFalse(ObjectUtils.isEmpty(ObjectUtilsTest.NON_EMPTY_SET));
        Assertions.assertFalse(isEmpty(ObjectUtilsTest.NON_EMPTY_MAP));
    }

    @Test
    public void testIsNotEmpty() {
        Assertions.assertFalse(ObjectUtils.isNotEmpty(null));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(""));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(new int[]{  }));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyList()));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(Collections.emptySet()));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyMap()));
        Assertions.assertTrue(ObjectUtils.isNotEmpty("  "));
        Assertions.assertTrue(ObjectUtils.isNotEmpty("ab"));
        Assertions.assertTrue(ObjectUtils.isNotEmpty(ObjectUtilsTest.NON_EMPTY_ARRAY));
        Assertions.assertTrue(ObjectUtils.isNotEmpty(ObjectUtilsTest.NON_EMPTY_LIST));
        Assertions.assertTrue(ObjectUtils.isNotEmpty(ObjectUtilsTest.NON_EMPTY_SET));
        Assertions.assertTrue(isNotEmpty(ObjectUtilsTest.NON_EMPTY_MAP));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIsNull() {
        final Object o = ObjectUtilsTest.FOO;
        final Object dflt = ObjectUtilsTest.BAR;
        Assertions.assertSame(dflt, ObjectUtils.defaultIfNull(null, dflt), "dflt was not returned when o was null");
        Assertions.assertSame(o, ObjectUtils.defaultIfNull(o, dflt), "dflt was returned when o was not null");
    }

    @Test
    public void testFirstNonNull() {
        Assertions.assertEquals("", ObjectUtils.firstNonNull(null, ""));
        final String firstNonNullGenerics = ObjectUtils.firstNonNull(null, null, "123", "456");
        Assertions.assertEquals("123", firstNonNullGenerics);
        Assertions.assertEquals("123", ObjectUtils.firstNonNull("123", null, "456", null));
        Assertions.assertSame(Boolean.TRUE, ObjectUtils.firstNonNull(Boolean.TRUE));
        // Explicitly pass in an empty array of Object type to ensure compiler doesn't complain of unchecked generic array creation
        Assertions.assertNull(ObjectUtils.firstNonNull());
        // Cast to Object in line below ensures compiler doesn't complain of unchecked generic array creation
        Assertions.assertNull(ObjectUtils.firstNonNull(null, null));
        Assertions.assertNull(ObjectUtils.firstNonNull(((Object) (null))));
        Assertions.assertNull(ObjectUtils.firstNonNull(((Object[]) (null))));
    }

    /**
     * Tests {@link ObjectUtils#anyNotNull(Object...)}.
     */
    @Test
    public void testAnyNotNull() {
        Assertions.assertFalse(ObjectUtils.anyNotNull());
        Assertions.assertFalse(ObjectUtils.anyNotNull(((Object) (null))));
        Assertions.assertFalse(ObjectUtils.anyNotNull(((Object[]) (null))));
        Assertions.assertFalse(ObjectUtils.anyNotNull(null, null, null));
        Assertions.assertTrue(ObjectUtils.anyNotNull(ObjectUtilsTest.FOO));
        Assertions.assertTrue(ObjectUtils.anyNotNull(null, ObjectUtilsTest.FOO, null));
        Assertions.assertTrue(ObjectUtils.anyNotNull(null, null, null, null, ObjectUtilsTest.FOO, ObjectUtilsTest.BAR));
    }

    /**
     * Tests {@link ObjectUtils#allNotNull(Object...)}.
     */
    @Test
    public void testAllNotNull() {
        Assertions.assertFalse(ObjectUtils.allNotNull(((Object) (null))));
        Assertions.assertFalse(ObjectUtils.allNotNull(((Object[]) (null))));
        Assertions.assertFalse(ObjectUtils.allNotNull(null, null, null));
        Assertions.assertFalse(ObjectUtils.allNotNull(null, ObjectUtilsTest.FOO, ObjectUtilsTest.BAR));
        Assertions.assertFalse(ObjectUtils.allNotNull(ObjectUtilsTest.FOO, ObjectUtilsTest.BAR, null));
        Assertions.assertFalse(ObjectUtils.allNotNull(ObjectUtilsTest.FOO, ObjectUtilsTest.BAR, null, ObjectUtilsTest.FOO, ObjectUtilsTest.BAR));
        Assertions.assertTrue(ObjectUtils.allNotNull());
        Assertions.assertTrue(ObjectUtils.allNotNull(ObjectUtilsTest.FOO));
        Assertions.assertTrue(ObjectUtils.allNotNull(ObjectUtilsTest.FOO, ObjectUtilsTest.BAR, 1, Boolean.TRUE, new Object(), new Object[]{  }));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEquals() {
        Assertions.assertTrue(ObjectUtils.equals(null, null), "ObjectUtils.equals(null, null) returned false");
        Assertions.assertTrue((!(ObjectUtils.equals(ObjectUtilsTest.FOO, null))), "ObjectUtils.equals(\"foo\", null) returned true");
        Assertions.assertTrue((!(ObjectUtils.equals(null, ObjectUtilsTest.BAR))), "ObjectUtils.equals(null, \"bar\") returned true");
        Assertions.assertTrue((!(ObjectUtils.equals(ObjectUtilsTest.FOO, ObjectUtilsTest.BAR))), "ObjectUtils.equals(\"foo\", \"bar\") returned true");
        Assertions.assertTrue(ObjectUtils.equals(ObjectUtilsTest.FOO, ObjectUtilsTest.FOO), "ObjectUtils.equals(\"foo\", \"foo\") returned false");
    }

    @Test
    public void testNotEqual() {
        Assertions.assertFalse(ObjectUtils.notEqual(null, null), "ObjectUtils.notEqual(null, null) returned false");
        Assertions.assertTrue(ObjectUtils.notEqual(ObjectUtilsTest.FOO, null), "ObjectUtils.notEqual(\"foo\", null) returned true");
        Assertions.assertTrue(ObjectUtils.notEqual(null, ObjectUtilsTest.BAR), "ObjectUtils.notEqual(null, \"bar\") returned true");
        Assertions.assertTrue(ObjectUtils.notEqual(ObjectUtilsTest.FOO, ObjectUtilsTest.BAR), "ObjectUtils.notEqual(\"foo\", \"bar\") returned true");
        Assertions.assertFalse(ObjectUtils.notEqual(ObjectUtilsTest.FOO, ObjectUtilsTest.FOO), "ObjectUtils.notEqual(\"foo\", \"foo\") returned false");
    }

    @Test
    public void testHashCode() {
        Assertions.assertEquals(0, ObjectUtils.hashCode(null));
        Assertions.assertEquals("a".hashCode(), ObjectUtils.hashCode("a"));
    }

    @Test
    public void testHashCodeMulti_multiple_emptyArray() {
        final Object[] array = new Object[0];
        Assertions.assertEquals(1, ObjectUtils.hashCodeMulti(array));
    }

    @Test
    public void testHashCodeMulti_multiple_nullArray() {
        final Object[] array = null;
        Assertions.assertEquals(1, ObjectUtils.hashCodeMulti(array));
    }

    @Test
    public void testHashCodeMulti_multiple_likeList() {
        final List<Object> list0 = new ArrayList<>(Arrays.asList(new Object[0]));
        Assertions.assertEquals(list0.hashCode(), ObjectUtils.hashCodeMulti());
        final List<Object> list1 = new ArrayList<>(Arrays.asList("a"));
        Assertions.assertEquals(list1.hashCode(), ObjectUtils.hashCodeMulti("a"));
        final List<Object> list2 = new ArrayList<>(Arrays.asList("a", "b"));
        Assertions.assertEquals(list2.hashCode(), ObjectUtils.hashCodeMulti("a", "b"));
        final List<Object> list3 = new ArrayList<>(Arrays.asList("a", "b", "c"));
        Assertions.assertEquals(list3.hashCode(), ObjectUtils.hashCodeMulti("a", "b", "c"));
    }

    @Test
    public void testIdentityToStringStringBuffer() {
        final Integer i = Integer.valueOf(45);
        final String expected = "java.lang.Integer@" + (Integer.toHexString(System.identityHashCode(i)));
        final StringBuffer buffer = new StringBuffer();
        ObjectUtils.identityToString(buffer, i);
        Assertions.assertEquals(expected, buffer.toString());
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(((StringBuffer) (null)), "tmp"));
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(new StringBuffer(), null));
    }

    @Test
    public void testIdentityToStringObjectNull() {
        Assertions.assertNull(ObjectUtils.identityToString(null));
    }

    @Test
    public void testIdentityToStringInteger() {
        final Integer i = Integer.valueOf(90);
        final String expected = "java.lang.Integer@" + (Integer.toHexString(System.identityHashCode(i)));
        Assertions.assertEquals(expected, ObjectUtils.identityToString(i));
    }

    @Test
    public void testIdentityToStringString() {
        Assertions.assertEquals(("java.lang.String@" + (Integer.toHexString(System.identityHashCode(ObjectUtilsTest.FOO)))), ObjectUtils.identityToString(ObjectUtilsTest.FOO));
    }

    @Test
    public void testIdentityToStringStringBuilder() {
        final Integer i = Integer.valueOf(90);
        final String expected = "java.lang.Integer@" + (Integer.toHexString(System.identityHashCode(i)));
        final StringBuilder builder = new StringBuilder();
        ObjectUtils.identityToString(builder, i);
        Assertions.assertEquals(expected, builder.toString());
    }

    @Test
    public void testIdentityToStringStringBuilderInUse() {
        final Integer i = Integer.valueOf(90);
        final String expected = "ABC = java.lang.Integer@" + (Integer.toHexString(System.identityHashCode(i)));
        final StringBuilder builder = new StringBuilder("ABC = ");
        ObjectUtils.identityToString(builder, i);
        Assertions.assertEquals(expected, builder.toString());
    }

    @Test
    public void testIdentityToStringStringBuilderNullValue() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(new StringBuilder(), null));
    }

    @Test
    public void testIdentityToStringStringBuilderNullStringBuilder() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(((StringBuilder) (null)), "tmp"));
    }

    @Test
    public void testIdentityToStringStrBuilder() {
        final Integer i = Integer.valueOf(102);
        final String expected = "java.lang.Integer@" + (Integer.toHexString(System.identityHashCode(i)));
        final StrBuilder builder = new StrBuilder();
        ObjectUtils.identityToString(builder, i);
        Assertions.assertEquals(expected, builder.toString());
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(((StrBuilder) (null)), "tmp"));
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(new StrBuilder(), null));
    }

    @Test
    public void testIdentityToStringAppendable() throws IOException {
        final Integer i = Integer.valueOf(121);
        final String expected = "java.lang.Integer@" + (Integer.toHexString(System.identityHashCode(i)));
        final Appendable appendable = new StringBuilder();
        ObjectUtils.identityToString(appendable, i);
        Assertions.assertEquals(expected, appendable.toString());
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(((Appendable) (null)), "tmp"));
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.identityToString(((Appendable) (new StringBuilder())), null));
    }

    @Test
    public void testToString_Object() {
        Assertions.assertEquals("", ObjectUtils.toString(null));
        Assertions.assertEquals(Boolean.TRUE.toString(), ObjectUtils.toString(Boolean.TRUE));
    }

    @Test
    public void testToString_ObjectString() {
        Assertions.assertEquals(ObjectUtilsTest.BAR, ObjectUtils.toString(null, ObjectUtilsTest.BAR));
        Assertions.assertEquals(Boolean.TRUE.toString(), ObjectUtils.toString(Boolean.TRUE, ObjectUtilsTest.BAR));
    }

    // 1 OK, because we are checking for code change
    @SuppressWarnings("cast")
    @Test
    public void testNull() {
        Assertions.assertNotNull(ObjectUtils.NULL);
        // 1 Check that NULL really is a Null i.e. the definition has not been changed
        Assertions.assertTrue(((ObjectUtils.NULL) instanceof ObjectUtils.Null));
        Assertions.assertSame(ObjectUtils.NULL, SerializationUtils.clone(ObjectUtils.NULL));
    }

    @Test
    public void testMax() {
        final Calendar calendar = Calendar.getInstance();
        final Date nonNullComparable1 = calendar.getTime();
        final Date nonNullComparable2 = calendar.getTime();
        final String[] nullArray = null;
        calendar.set(Calendar.YEAR, ((calendar.get(Calendar.YEAR)) - 1));
        final Date minComparable = calendar.getTime();
        Assertions.assertNotSame(nonNullComparable1, nonNullComparable2);
        Assertions.assertNull(ObjectUtils.max(((String) (null))));
        Assertions.assertNull(ObjectUtils.max(nullArray));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(null, nonNullComparable1));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(nonNullComparable1, null));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(null, nonNullComparable1, null));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(nonNullComparable1, nonNullComparable2));
        Assertions.assertSame(nonNullComparable2, ObjectUtils.max(nonNullComparable2, nonNullComparable1));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(nonNullComparable1, minComparable));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(minComparable, nonNullComparable1));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.max(null, minComparable, null, nonNullComparable1));
        Assertions.assertNull(ObjectUtils.max(null, null));
    }

    @Test
    public void testMin() {
        final Calendar calendar = Calendar.getInstance();
        final Date nonNullComparable1 = calendar.getTime();
        final Date nonNullComparable2 = calendar.getTime();
        final String[] nullArray = null;
        calendar.set(Calendar.YEAR, ((calendar.get(Calendar.YEAR)) - 1));
        final Date minComparable = calendar.getTime();
        Assertions.assertNotSame(nonNullComparable1, nonNullComparable2);
        Assertions.assertNull(ObjectUtils.min(((String) (null))));
        Assertions.assertNull(ObjectUtils.min(nullArray));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.min(null, nonNullComparable1));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.min(nonNullComparable1, null));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.min(null, nonNullComparable1, null));
        Assertions.assertSame(nonNullComparable1, ObjectUtils.min(nonNullComparable1, nonNullComparable2));
        Assertions.assertSame(nonNullComparable2, ObjectUtils.min(nonNullComparable2, nonNullComparable1));
        Assertions.assertSame(minComparable, ObjectUtils.min(nonNullComparable1, minComparable));
        Assertions.assertSame(minComparable, ObjectUtils.min(minComparable, nonNullComparable1));
        Assertions.assertSame(minComparable, ObjectUtils.min(null, nonNullComparable1, null, minComparable));
        Assertions.assertNull(ObjectUtils.min(null, null));
    }

    /**
     * Tests {@link ObjectUtils#compare(Comparable, Comparable, boolean)}.
     */
    @Test
    public void testCompare() {
        final Integer one = Integer.valueOf(1);
        final Integer two = Integer.valueOf(2);
        final Integer nullValue = null;
        Assertions.assertEquals(0, ObjectUtils.compare(nullValue, nullValue), "Null Null false");
        Assertions.assertEquals(0, ObjectUtils.compare(nullValue, nullValue, true), "Null Null true");
        Assertions.assertEquals((-1), ObjectUtils.compare(nullValue, one), "Null one false");
        Assertions.assertEquals(1, ObjectUtils.compare(nullValue, one, true), "Null one true");
        Assertions.assertEquals(1, ObjectUtils.compare(one, nullValue), "one Null false");
        Assertions.assertEquals((-1), ObjectUtils.compare(one, nullValue, true), "one Null true");
        Assertions.assertEquals((-1), ObjectUtils.compare(one, two), "one two false");
        Assertions.assertEquals((-1), ObjectUtils.compare(one, two, true), "one two true");
    }

    @Test
    public void testMedian() {
        Assertions.assertEquals("foo", ObjectUtils.median("foo"));
        Assertions.assertEquals("bar", ObjectUtils.median("foo", "bar"));
        Assertions.assertEquals("baz", ObjectUtils.median("foo", "bar", "baz"));
        Assertions.assertEquals("baz", ObjectUtils.median("foo", "bar", "baz", "blah"));
        Assertions.assertEquals("blah", ObjectUtils.median("foo", "bar", "baz", "blah", "wah"));
        Assertions.assertEquals(Integer.valueOf(5), ObjectUtils.median(Integer.valueOf(1), Integer.valueOf(5), Integer.valueOf(10)));
        Assertions.assertEquals(Integer.valueOf(7), ObjectUtils.median(Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(9)));
        Assertions.assertEquals(Integer.valueOf(6), ObjectUtils.median(Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8)));
    }

    @Test
    public void testMedian_nullItems() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.median(((String[]) (null))));
    }

    @Test
    public void testMedian_emptyItems() {
        Assertions.assertThrows(IllegalArgumentException.class, String::median);
    }

    @Test
    public void testComparatorMedian() {
        final ObjectUtilsTest.CharSequenceComparator cmp = new ObjectUtilsTest.CharSequenceComparator();
        final ObjectUtilsTest.NonComparableCharSequence foo = new ObjectUtilsTest.NonComparableCharSequence("foo");
        final ObjectUtilsTest.NonComparableCharSequence bar = new ObjectUtilsTest.NonComparableCharSequence("bar");
        final ObjectUtilsTest.NonComparableCharSequence baz = new ObjectUtilsTest.NonComparableCharSequence("baz");
        final ObjectUtilsTest.NonComparableCharSequence blah = new ObjectUtilsTest.NonComparableCharSequence("blah");
        final ObjectUtilsTest.NonComparableCharSequence wah = new ObjectUtilsTest.NonComparableCharSequence("wah");
        Assertions.assertSame(foo, ObjectUtils.median(cmp, foo));
        Assertions.assertSame(bar, ObjectUtils.median(cmp, foo, bar));
        Assertions.assertSame(baz, ObjectUtils.median(cmp, foo, bar, baz));
        Assertions.assertSame(baz, ObjectUtils.median(cmp, foo, bar, baz, blah));
        Assertions.assertSame(blah, ObjectUtils.median(cmp, foo, bar, baz, blah, wah));
    }

    @Test
    public void testComparatorMedian_nullComparator() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.median(((Comparator<CharSequence>) (null)), new ObjectUtilsTest.NonComparableCharSequence("foo")));
    }

    @Test
    public void testComparatorMedian_nullItems() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectUtils.median(new ObjectUtilsTest.CharSequenceComparator(), ((CharSequence[]) (null))));
    }

    @Test
    public void testComparatorMedian_emptyItems() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ObjectUtils.median(new ObjectUtilsTest.CharSequenceComparator()));
    }

    @Test
    public void testMode() {
        Assertions.assertNull(ObjectUtils.mode(((Object[]) (null))));
        Assertions.assertNull(ObjectUtils.mode());
        Assertions.assertNull(ObjectUtils.mode("foo", "bar", "baz"));
        Assertions.assertNull(ObjectUtils.mode("foo", "bar", "baz", "foo", "bar"));
        Assertions.assertEquals("foo", ObjectUtils.mode("foo", "bar", "baz", "foo"));
        Assertions.assertEquals(Integer.valueOf(9), ObjectUtils.mode("foo", "bar", "baz", Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(9)));
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with a cloneable object.
     */
    @Test
    public void testCloneOfCloneable() {
        final ObjectUtilsTest.CloneableString string = new ObjectUtilsTest.CloneableString("apache");
        final ObjectUtilsTest.CloneableString stringClone = ObjectUtils.clone(string);
        Assertions.assertEquals("apache", stringClone.getValue());
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with a not cloneable object.
     */
    @Test
    public void testCloneOfNotCloneable() {
        final String string = new String("apache");
        Assertions.assertNull(ObjectUtils.clone(string));
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with an uncloneable object.
     */
    @Test
    public void testCloneOfUncloneable() {
        final ObjectUtilsTest.UncloneableString string = new ObjectUtilsTest.UncloneableString("apache");
        CloneFailedException e = Assertions.assertThrows(CloneFailedException.class, () -> ObjectUtils.clone(string));
        Assertions.assertEquals(NoSuchMethodException.class, e.getCause().getClass());
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with an object array.
     */
    @Test
    public void testCloneOfStringArray() {
        Assertions.assertTrue(Arrays.deepEquals(new String[]{ "string" }, ObjectUtils.clone(new String[]{ "string" })));
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with an array of primitives.
     */
    @Test
    public void testCloneOfPrimitiveArray() {
        Assertions.assertArrayEquals(new int[]{ 1 }, ObjectUtils.clone(new int[]{ 1 }));
    }

    /**
     * Tests {@link ObjectUtils#cloneIfPossible(Object)} with a cloneable object.
     */
    @Test
    public void testPossibleCloneOfCloneable() {
        final ObjectUtilsTest.CloneableString string = new ObjectUtilsTest.CloneableString("apache");
        final ObjectUtilsTest.CloneableString stringClone = ObjectUtils.cloneIfPossible(string);
        Assertions.assertEquals("apache", stringClone.getValue());
    }

    /**
     * Tests {@link ObjectUtils#cloneIfPossible(Object)} with a not cloneable object.
     */
    @Test
    public void testPossibleCloneOfNotCloneable() {
        final String string = new String("apache");
        Assertions.assertSame(string, ObjectUtils.cloneIfPossible(string));
    }

    /**
     * Tests {@link ObjectUtils#cloneIfPossible(Object)} with an uncloneable object.
     */
    @Test
    public void testPossibleCloneOfUncloneable() {
        final ObjectUtilsTest.UncloneableString string = new ObjectUtilsTest.UncloneableString("apache");
        CloneFailedException e = Assertions.assertThrows(CloneFailedException.class, () -> ObjectUtils.cloneIfPossible(string));
        Assertions.assertEquals(NoSuchMethodException.class, e.getCause().getClass());
    }

    @Test
    public void testConstMethods() {
        // To truly test the CONST() method, we'd want to look in the
        // bytecode to see if the literals were folded into the
        // class, or if the bytecode kept the method call.
        Assertions.assertTrue(ObjectUtils.CONST(true), "CONST(boolean)");
        Assertions.assertEquals(((byte) (3)), ObjectUtils.CONST(((byte) (3))), "CONST(byte)");
        Assertions.assertEquals(((char) (3)), ObjectUtils.CONST(((char) (3))), "CONST(char)");
        Assertions.assertEquals(((short) (3)), ObjectUtils.CONST(((short) (3))), "CONST(short)");
        Assertions.assertEquals(3, ObjectUtils.CONST(3), "CONST(int)");
        Assertions.assertEquals(3L, ObjectUtils.CONST(3L), "CONST(long)");
        Assertions.assertEquals(3.0F, ObjectUtils.CONST(3.0F), "CONST(float)");
        Assertions.assertEquals(3.0, ObjectUtils.CONST(3.0), "CONST(double)");
        Assertions.assertEquals("abc", ObjectUtils.CONST("abc"), "CONST(Object)");
        // Make sure documentation examples from Javadoc all work
        // (this fixed a lot of my bugs when I these!)
        // 
        // My bugs should be in a software engineering textbook
        // for "Can you screw this up?"  The answer is, yes,
        // you can even screw this up.  (When you == Julius)
        // .
        final boolean MAGIC_FLAG = ObjectUtils.CONST(true);
        final byte MAGIC_BYTE1 = ObjectUtils.CONST(((byte) (127)));
        final byte MAGIC_BYTE2 = ObjectUtils.CONST_BYTE(127);
        final char MAGIC_CHAR = ObjectUtils.CONST('a');
        final short MAGIC_SHORT1 = ObjectUtils.CONST(((short) (123)));
        final short MAGIC_SHORT2 = ObjectUtils.CONST_SHORT(127);
        final int MAGIC_INT = ObjectUtils.CONST(123);
        final long MAGIC_LONG1 = ObjectUtils.CONST(123L);
        final long MAGIC_LONG2 = ObjectUtils.CONST(3);
        final float MAGIC_FLOAT = ObjectUtils.CONST(1.0F);
        final double MAGIC_DOUBLE = ObjectUtils.CONST(1.0);
        final String MAGIC_STRING = ObjectUtils.CONST("abc");
        Assertions.assertTrue(MAGIC_FLAG);
        Assertions.assertEquals(127, MAGIC_BYTE1);
        Assertions.assertEquals(127, MAGIC_BYTE2);
        Assertions.assertEquals('a', MAGIC_CHAR);
        Assertions.assertEquals(123, MAGIC_SHORT1);
        Assertions.assertEquals(127, MAGIC_SHORT2);
        Assertions.assertEquals(123, MAGIC_INT);
        Assertions.assertEquals(123, MAGIC_LONG1);
        Assertions.assertEquals(3, MAGIC_LONG2);
        Assertions.assertEquals(1.0F, MAGIC_FLOAT);
        Assertions.assertEquals(1.0, MAGIC_DOUBLE);
        Assertions.assertEquals("abc", MAGIC_STRING);
        Assertions.assertThrows(IllegalArgumentException.class, () -> ObjectUtils.CONST_BYTE((-129)), "CONST_BYTE(-129): IllegalArgumentException should have been thrown.");
        Assertions.assertThrows(IllegalArgumentException.class, () -> ObjectUtils.CONST_BYTE(128), "CONST_BYTE(128): IllegalArgumentException should have been thrown.");
        Assertions.assertThrows(IllegalArgumentException.class, () -> ObjectUtils.CONST_SHORT((-32769)), "CONST_SHORT(-32769): IllegalArgumentException should have been thrown.");
        Assertions.assertThrows(IllegalArgumentException.class, () -> ObjectUtils.CONST_BYTE(32768), "CONST_SHORT(32768): IllegalArgumentException should have been thrown.");
    }

    /**
     * String that is cloneable.
     */
    static final class CloneableString extends MutableObject<String> implements Cloneable {
        private static final long serialVersionUID = 1L;

        CloneableString(final String s) {
            super(s);
        }

        @Override
        public ObjectUtilsTest.CloneableString clone() throws CloneNotSupportedException {
            return ((ObjectUtilsTest.CloneableString) (super.clone()));
        }
    }

    /**
     * String that is not cloneable.
     */
    static final class UncloneableString extends MutableObject<String> implements Cloneable {
        private static final long serialVersionUID = 1L;

        UncloneableString(final String s) {
            super(s);
        }
    }

    static final class NonComparableCharSequence implements CharSequence {
        final String value;

        /**
         * Create a new NonComparableCharSequence instance.
         *
         * @param value
         * 		
         */
        NonComparableCharSequence(final String value) {
            super();
            Validate.notNull(value);
            this.value = value;
        }

        @Override
        public char charAt(final int arg0) {
            return value.charAt(arg0);
        }

        @Override
        public int length() {
            return value.length();
        }

        @Override
        public CharSequence subSequence(final int arg0, final int arg1) {
            return value.subSequence(arg0, arg1);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static final class CharSequenceComparator implements Comparator<CharSequence> {
        @Override
        public int compare(final CharSequence o1, final CharSequence o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }
}

