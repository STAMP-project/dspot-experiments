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


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests for methods of {@link org.apache.commons.lang3.StringUtils}
 * which been moved to their own test classes.
 */
// deliberate use of deprecated code
@SuppressWarnings("deprecation")
public class StringUtilsTest {
    static final String WHITESPACE;

    static final String NON_WHITESPACE;

    static final String HARD_SPACE;

    static final String TRIMMABLE;

    static final String NON_TRIMMABLE;

    static {
        String ws = "";
        String nws = "";
        final String hs = String.valueOf(((char) (160)));
        String tr = "";
        String ntr = "";
        for (int i = 0; i < (Character.MAX_VALUE); i++) {
            if (Character.isWhitespace(((char) (i)))) {
                ws += String.valueOf(((char) (i)));
                if (i > 32) {
                    ntr += String.valueOf(((char) (i)));
                }
            } else
                if (i < 40) {
                    nws += String.valueOf(((char) (i)));
                }

        }
        for (int i = 0; i <= 32; i++) {
            tr += String.valueOf(((char) (i)));
        }
        WHITESPACE = ws;
        NON_WHITESPACE = nws;
        HARD_SPACE = hs;
        TRIMMABLE = tr;
        NON_TRIMMABLE = ntr;
    }

    private static final String[] ARRAY_LIST = new String[]{ "foo", "bar", "baz" };

    private static final String[] EMPTY_ARRAY_LIST = new String[]{  };

    private static final String[] NULL_ARRAY_LIST = new String[]{ null };

    private static final Object[] NULL_TO_STRING_LIST = new Object[]{ new Object() {
        @Override
        public String toString() {
            return null;
        }
    } };

    private static final String[] MIXED_ARRAY_LIST = new String[]{ null, "", "foo" };

    private static final Object[] MIXED_TYPE_LIST = new Object[]{ "foo", Long.valueOf(2L) };

    private static final long[] LONG_PRIM_LIST = new long[]{ 1, 2 };

    private static final int[] INT_PRIM_LIST = new int[]{ 1, 2 };

    private static final byte[] BYTE_PRIM_LIST = new byte[]{ 1, 2 };

    private static final short[] SHORT_PRIM_LIST = new short[]{ 1, 2 };

    private static final char[] CHAR_PRIM_LIST = new char[]{ '1', '2' };

    private static final float[] FLOAT_PRIM_LIST = new float[]{ 1, 2 };

    private static final double[] DOUBLE_PRIM_LIST = new double[]{ 1, 2 };

    private static final List<String> MIXED_STRING_LIST = Arrays.asList(null, "", "foo");

    private static final List<Object> MIXED_TYPE_OBJECT_LIST = Arrays.<Object>asList("foo", Long.valueOf(2L));

    private static final List<String> STRING_LIST = Arrays.asList("foo", "bar", "baz");

    private static final List<String> EMPTY_STRING_LIST = Collections.emptyList();

    private static final List<String> NULL_STRING_LIST = Collections.singletonList(null);

    private static final String SEPARATOR = ",";

    private static final char SEPARATOR_CHAR = ';';

    private static final String TEXT_LIST = "foo,bar,baz";

    private static final String TEXT_LIST_CHAR = "foo;bar;baz";

    private static final String TEXT_LIST_NOSEP = "foobarbaz";

    private static final String FOO_UNCAP = "foo";

    private static final String FOO_CAP = "Foo";

    private static final String SENTENCE_UNCAP = "foo bar baz";

    private static final String SENTENCE_CAP = "Foo Bar Baz";

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new StringUtils());
        final Constructor<?>[] cons = StringUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(StringUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(StringUtils.class.getModifiers()));
    }

    @Test
    public void testUpperCase() {
        Assertions.assertNull(StringUtils.upperCase(null));
        Assertions.assertNull(StringUtils.upperCase(null, Locale.ENGLISH));
        Assertions.assertEquals("FOO TEST THING", StringUtils.upperCase("fOo test THING"), "upperCase(String) failed");
        Assertions.assertEquals("", StringUtils.upperCase(""), "upperCase(empty-string) failed");
        Assertions.assertEquals("FOO TEST THING", StringUtils.upperCase("fOo test THING", Locale.ENGLISH), "upperCase(String, Locale) failed");
        Assertions.assertEquals("", StringUtils.upperCase("", Locale.ENGLISH), "upperCase(empty-string, Locale) failed");
    }

    @Test
    public void testLowerCase() {
        Assertions.assertNull(StringUtils.lowerCase(null));
        Assertions.assertNull(StringUtils.lowerCase(null, Locale.ENGLISH));
        Assertions.assertEquals("foo test thing", StringUtils.lowerCase("fOo test THING"), "lowerCase(String) failed");
        Assertions.assertEquals("", StringUtils.lowerCase(""), "lowerCase(empty-string) failed");
        Assertions.assertEquals("foo test thing", StringUtils.lowerCase("fOo test THING", Locale.ENGLISH), "lowerCase(String, Locale) failed");
        Assertions.assertEquals("", StringUtils.lowerCase("", Locale.ENGLISH), "lowerCase(empty-string, Locale) failed");
    }

    @Test
    public void testCapitalize() {
        Assertions.assertNull(StringUtils.capitalize(null));
        Assertions.assertEquals("", StringUtils.capitalize(""), "capitalize(empty-string) failed");
        Assertions.assertEquals("X", StringUtils.capitalize("x"), "capitalize(single-char-string) failed");
        Assertions.assertEquals(StringUtilsTest.FOO_CAP, StringUtils.capitalize(StringUtilsTest.FOO_CAP), "capitalize(String) failed");
        Assertions.assertEquals(StringUtilsTest.FOO_CAP, StringUtils.capitalize(StringUtilsTest.FOO_UNCAP), "capitalize(string) failed");
        Assertions.assertEquals("\u01c8", StringUtils.capitalize("\u01c9"), "capitalize(String) is not using TitleCase");
        // Javadoc examples
        Assertions.assertNull(StringUtils.capitalize(null));
        Assertions.assertEquals("", StringUtils.capitalize(""));
        Assertions.assertEquals("Cat", StringUtils.capitalize("cat"));
        Assertions.assertEquals("CAt", StringUtils.capitalize("cAt"));
        Assertions.assertEquals("'cat'", StringUtils.capitalize("'cat'"));
    }

    @Test
    public void testUnCapitalize() {
        Assertions.assertNull(StringUtils.uncapitalize(null));
        Assertions.assertEquals(StringUtilsTest.FOO_UNCAP, StringUtils.uncapitalize(StringUtilsTest.FOO_CAP), "uncapitalize(String) failed");
        Assertions.assertEquals(StringUtilsTest.FOO_UNCAP, StringUtils.uncapitalize(StringUtilsTest.FOO_UNCAP), "uncapitalize(string) failed");
        Assertions.assertEquals("", StringUtils.uncapitalize(""), "uncapitalize(empty-string) failed");
        Assertions.assertEquals("x", StringUtils.uncapitalize("X"), "uncapitalize(single-char-string) failed");
        // Examples from uncapitalize Javadoc
        Assertions.assertEquals("cat", StringUtils.uncapitalize("cat"));
        Assertions.assertEquals("cat", StringUtils.uncapitalize("Cat"));
        Assertions.assertEquals("cAT", StringUtils.uncapitalize("CAT"));
    }

    @Test
    public void testReCapitalize() {
        // reflection type of tests: Sentences.
        Assertions.assertEquals(StringUtilsTest.SENTENCE_UNCAP, StringUtils.uncapitalize(StringUtils.capitalize(StringUtilsTest.SENTENCE_UNCAP)), "uncapitalize(capitalize(String)) failed");
        Assertions.assertEquals(StringUtilsTest.SENTENCE_CAP, StringUtils.capitalize(StringUtils.uncapitalize(StringUtilsTest.SENTENCE_CAP)), "capitalize(uncapitalize(String)) failed");
        // reflection type of tests: One word.
        Assertions.assertEquals(StringUtilsTest.FOO_UNCAP, StringUtils.uncapitalize(StringUtils.capitalize(StringUtilsTest.FOO_UNCAP)), "uncapitalize(capitalize(String)) failed");
        Assertions.assertEquals(StringUtilsTest.FOO_CAP, StringUtils.capitalize(StringUtils.uncapitalize(StringUtilsTest.FOO_CAP)), "capitalize(uncapitalize(String)) failed");
    }

    @Test
    public void testSwapCase_String() {
        Assertions.assertNull(StringUtils.swapCase(null));
        Assertions.assertEquals("", StringUtils.swapCase(""));
        Assertions.assertEquals("  ", StringUtils.swapCase("  "));
        Assertions.assertEquals("i", WordUtils.swapCase("I"));
        Assertions.assertEquals("I", WordUtils.swapCase("i"));
        Assertions.assertEquals("I AM HERE 123", StringUtils.swapCase("i am here 123"));
        Assertions.assertEquals("i aM hERE 123", StringUtils.swapCase("I Am Here 123"));
        Assertions.assertEquals("I AM here 123", StringUtils.swapCase("i am HERE 123"));
        Assertions.assertEquals("i am here 123", StringUtils.swapCase("I AM HERE 123"));
        final String test = "This String contains a TitleCase character: \u01c8";
        final String expect = "tHIS sTRING CONTAINS A tITLEcASE CHARACTER: \u01c9";
        Assertions.assertEquals(expect, WordUtils.swapCase(test));
        Assertions.assertEquals(expect, StringUtils.swapCase(test));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testJoin_Objects() {
        Assertions.assertEquals("abc", StringUtils.join("a", "b", "c"));
        Assertions.assertEquals("a", StringUtils.join(null, "", "a"));
        Assertions.assertNull(StringUtils.join(((Object[]) (null))));
    }

    @Test
    public void testJoin_Objectarray() {
        // assertNull(StringUtils.join(null)); // generates warning
        Assertions.assertNull(StringUtils.join(((Object[]) (null))));// equivalent explicit cast

        // test additional varargs calls
        Assertions.assertEquals("", StringUtils.join());// empty array

        Assertions.assertEquals("", StringUtils.join(((Object) (null))));// => new Object[]{null}

        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_ARRAY_LIST));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.NULL_ARRAY_LIST));
        Assertions.assertEquals("null", StringUtils.join(StringUtilsTest.NULL_TO_STRING_LIST));
        Assertions.assertEquals("abc", StringUtils.join("a", "b", "c"));
        Assertions.assertEquals("a", StringUtils.join(null, "a", ""));
        Assertions.assertEquals("foo", StringUtils.join(StringUtilsTest.MIXED_ARRAY_LIST));
        Assertions.assertEquals("foo2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST));
    }

    @Test
    public void testJoin_ArrayCharSeparator() {
        Assertions.assertNull(StringUtils.join(((Object[]) (null)), ','));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_CHAR, StringUtils.join(StringUtilsTest.ARRAY_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_ARRAY_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals(";;foo", StringUtils.join(StringUtilsTest.MIXED_ARRAY_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("foo;2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertNull(StringUtils.join(((Object[]) (null)), ',', 0, 1));
        Assertions.assertEquals("/", StringUtils.join(StringUtilsTest.MIXED_ARRAY_LIST, '/', 0, ((StringUtilsTest.MIXED_ARRAY_LIST.length) - 1)));
        Assertions.assertEquals("foo", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, '/', 0, 1));
        Assertions.assertEquals("null", StringUtils.join(StringUtilsTest.NULL_TO_STRING_LIST, '/', 0, 1));
        Assertions.assertEquals("foo/2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, '/', 0, 2));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, '/', 1, 2));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, '/', 2, 1));
    }

    @Test
    public void testJoin_ArrayOfChars() {
        Assertions.assertNull(StringUtils.join(((char[]) (null)), ','));
        Assertions.assertEquals("1;2", StringUtils.join(StringUtilsTest.CHAR_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.CHAR_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((char[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.CHAR_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.CHAR_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayOfBytes() {
        Assertions.assertNull(StringUtils.join(((byte[]) (null)), ','));
        Assertions.assertEquals("1;2", StringUtils.join(StringUtilsTest.BYTE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.BYTE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((byte[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.BYTE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.BYTE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayOfInts() {
        Assertions.assertNull(StringUtils.join(((int[]) (null)), ','));
        Assertions.assertEquals("1;2", StringUtils.join(StringUtilsTest.INT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.INT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((int[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.INT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.INT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayOfLongs() {
        Assertions.assertNull(StringUtils.join(((long[]) (null)), ','));
        Assertions.assertEquals("1;2", StringUtils.join(StringUtilsTest.LONG_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.LONG_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((long[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.LONG_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.LONG_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayOfFloats() {
        Assertions.assertNull(StringUtils.join(((float[]) (null)), ','));
        Assertions.assertEquals("1.0;2.0", StringUtils.join(StringUtilsTest.FLOAT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2.0", StringUtils.join(StringUtilsTest.FLOAT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((float[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.FLOAT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.FLOAT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayOfDoubles() {
        Assertions.assertNull(StringUtils.join(((double[]) (null)), ','));
        Assertions.assertEquals("1.0;2.0", StringUtils.join(StringUtilsTest.DOUBLE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2.0", StringUtils.join(StringUtilsTest.DOUBLE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((double[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.DOUBLE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.DOUBLE_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayOfShorts() {
        Assertions.assertNull(StringUtils.join(((short[]) (null)), ','));
        Assertions.assertEquals("1;2", StringUtils.join(StringUtilsTest.SHORT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.SHORT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 2));
        Assertions.assertNull(StringUtils.join(((short[]) (null)), StringUtilsTest.SEPARATOR_CHAR, 0, 1));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.SHORT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 0, 0));
        Assertions.assertEquals(StringUtils.EMPTY, StringUtils.join(StringUtilsTest.SHORT_PRIM_LIST, StringUtilsTest.SEPARATOR_CHAR, 1, 0));
    }

    @Test
    public void testJoin_ArrayString() {
        Assertions.assertNull(StringUtils.join(((Object[]) (null)), null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(StringUtilsTest.ARRAY_LIST, null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(StringUtilsTest.ARRAY_LIST, ""));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.NULL_ARRAY_LIST, null));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_ARRAY_LIST, null));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_ARRAY_LIST, ""));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_ARRAY_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST, StringUtils.join(StringUtilsTest.ARRAY_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals(",,foo", StringUtils.join(StringUtilsTest.MIXED_ARRAY_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals("foo,2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals("/", StringUtils.join(StringUtilsTest.MIXED_ARRAY_LIST, "/", 0, ((StringUtilsTest.MIXED_ARRAY_LIST.length) - 1)));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.MIXED_ARRAY_LIST, "", 0, ((StringUtilsTest.MIXED_ARRAY_LIST.length) - 1)));
        Assertions.assertEquals("foo", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, "/", 0, 1));
        Assertions.assertEquals("foo/2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, "/", 0, 2));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, "/", 1, 2));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.MIXED_TYPE_LIST, "/", 2, 1));
    }

    @Test
    public void testJoin_List() {
        Assertions.assertNull(StringUtils.join(((List<String>) (null)), null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(StringUtilsTest.STRING_LIST, null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(StringUtilsTest.STRING_LIST, ""));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.NULL_STRING_LIST, null));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_STRING_LIST, null));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_STRING_LIST, ""));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.EMPTY_STRING_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST, StringUtils.join(StringUtilsTest.STRING_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals(",,foo", StringUtils.join(StringUtilsTest.MIXED_STRING_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals("foo,2", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, StringUtilsTest.SEPARATOR));
        Assertions.assertEquals("/", StringUtils.join(StringUtilsTest.MIXED_STRING_LIST, "/", 0, ((StringUtilsTest.MIXED_STRING_LIST.size()) - 1)));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.MIXED_STRING_LIST, "", 0, ((StringUtilsTest.MIXED_STRING_LIST.size()) - 1)));
        Assertions.assertEquals("foo", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, "/", 0, 1));
        Assertions.assertEquals("foo/2", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, "/", 0, 2));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, "/", 1, 2));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, "/", 2, 1));
        Assertions.assertNull(null, StringUtils.join(((List<?>) (null)), "/", 0, 1));
        Assertions.assertEquals("/", StringUtils.join(StringUtilsTest.MIXED_STRING_LIST, '/', 0, ((StringUtilsTest.MIXED_STRING_LIST.size()) - 1)));
        Assertions.assertEquals("foo", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, '/', 0, 1));
        Assertions.assertEquals("foo/2", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, '/', 0, 2));
        Assertions.assertEquals("2", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, '/', 1, 2));
        Assertions.assertEquals("", StringUtils.join(StringUtilsTest.MIXED_TYPE_OBJECT_LIST, '/', 2, 1));
        Assertions.assertNull(null, StringUtils.join(((List<?>) (null)), '/', 0, 1));
    }

    @Test
    public void testJoin_IteratorChar() {
        Assertions.assertNull(StringUtils.join(((Iterator<?>) (null)), ','));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_CHAR, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST).iterator(), StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.NULL_ARRAY_LIST).iterator(), StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST).iterator(), StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("foo", StringUtils.join(Collections.singleton("foo").iterator(), 'x'));
    }

    @Test
    public void testJoin_IteratorString() {
        Assertions.assertNull(StringUtils.join(((Iterator<?>) (null)), null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST).iterator(), null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST).iterator(), ""));
        Assertions.assertEquals("foo", StringUtils.join(Collections.singleton("foo").iterator(), "x"));
        Assertions.assertEquals("foo", StringUtils.join(Collections.singleton("foo").iterator(), null));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.NULL_ARRAY_LIST).iterator(), null));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST).iterator(), null));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST).iterator(), ""));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST).iterator(), StringUtilsTest.SEPARATOR));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST).iterator(), StringUtilsTest.SEPARATOR));
        Assertions.assertNull(StringUtils.join(Arrays.asList(StringUtilsTest.NULL_TO_STRING_LIST).iterator(), StringUtilsTest.SEPARATOR));
    }

    @Test
    public void testJoin_IterableChar() {
        Assertions.assertNull(StringUtils.join(((Iterable<?>) (null)), ','));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_CHAR, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST), StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.NULL_ARRAY_LIST), StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST), StringUtilsTest.SEPARATOR_CHAR));
        Assertions.assertEquals("foo", StringUtils.join(Collections.singleton("foo"), 'x'));
    }

    @Test
    public void testJoin_IterableString() {
        Assertions.assertNull(StringUtils.join(((Iterable<?>) (null)), null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST), null));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST_NOSEP, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST), ""));
        Assertions.assertEquals("foo", StringUtils.join(Collections.singleton("foo"), "x"));
        Assertions.assertEquals("foo", StringUtils.join(Collections.singleton("foo"), null));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.NULL_ARRAY_LIST), null));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST), null));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST), ""));
        Assertions.assertEquals("", StringUtils.join(Arrays.asList(StringUtilsTest.EMPTY_ARRAY_LIST), StringUtilsTest.SEPARATOR));
        Assertions.assertEquals(StringUtilsTest.TEXT_LIST, StringUtils.join(Arrays.asList(StringUtilsTest.ARRAY_LIST), StringUtilsTest.SEPARATOR));
    }

    @Test
    public void testJoinWith() {
        Assertions.assertEquals("", StringUtils.joinWith(","));// empty array

        Assertions.assertEquals("", StringUtils.joinWith(",", ((Object[]) (StringUtilsTest.NULL_ARRAY_LIST))));
        Assertions.assertEquals("null", StringUtils.joinWith(",", StringUtilsTest.NULL_TO_STRING_LIST));// toString method prints 'null'

        Assertions.assertEquals("a,b,c", StringUtils.joinWith(",", "a", "b", "c"));
        Assertions.assertEquals(",a,", StringUtils.joinWith(",", null, "a", ""));
        Assertions.assertEquals("ab", StringUtils.joinWith(null, "a", "b"));
    }

    @Test
    public void testJoinWithThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.joinWith(",", ((Object[]) (null))));
    }

    @Test
    public void testSplit_String() {
        Assertions.assertNull(StringUtils.split(null));
        Assertions.assertEquals(0, StringUtils.split("").length);
        String str = "a b  .c";
        String[] res = StringUtils.split(str);
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals(".c", res[2]);
        str = " a ";
        res = StringUtils.split(str);
        Assertions.assertEquals(1, res.length);
        Assertions.assertEquals("a", res[0]);
        str = ((("a" + (StringUtilsTest.WHITESPACE)) + "b") + (StringUtilsTest.NON_WHITESPACE)) + "c";
        res = StringUtils.split(str);
        Assertions.assertEquals(2, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals((("b" + (StringUtilsTest.NON_WHITESPACE)) + "c"), res[1]);
    }

    @Test
    public void testSplit_StringChar() {
        Assertions.assertNull(StringUtils.split(null, '.'));
        Assertions.assertEquals(0, StringUtils.split("", '.').length);
        String str = "a.b.. c";
        String[] res = StringUtils.split(str, '.');
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals(" c", res[2]);
        str = ".a.";
        res = StringUtils.split(str, '.');
        Assertions.assertEquals(1, res.length);
        Assertions.assertEquals("a", res[0]);
        str = "a b c";
        res = StringUtils.split(str, ' ');
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals("c", res[2]);
    }

    @Test
    public void testSplit_StringString_StringStringInt() {
        Assertions.assertNull(StringUtils.split(null, "."));
        Assertions.assertNull(StringUtils.split(null, ".", 3));
        Assertions.assertEquals(0, StringUtils.split("", ".").length);
        Assertions.assertEquals(0, StringUtils.split("", ".", 3).length);
        innerTestSplit('.', ".", ' ');
        innerTestSplit('.', ".", ',');
        innerTestSplit('.', ".,", 'x');
        for (int i = 0; i < (StringUtilsTest.WHITESPACE.length()); i++) {
            for (int j = 0; j < (StringUtilsTest.NON_WHITESPACE.length()); j++) {
                innerTestSplit(StringUtilsTest.WHITESPACE.charAt(i), null, StringUtilsTest.NON_WHITESPACE.charAt(j));
                innerTestSplit(StringUtilsTest.WHITESPACE.charAt(i), String.valueOf(StringUtilsTest.WHITESPACE.charAt(i)), StringUtilsTest.NON_WHITESPACE.charAt(j));
            }
        }
        String[] results;
        final String[] expectedResults = new String[]{ "ab", "de fg" };
        results = StringUtils.split("ab   de fg", null, 2);
        Assertions.assertEquals(expectedResults.length, results.length);
        for (int i = 0; i < (expectedResults.length); i++) {
            Assertions.assertEquals(expectedResults[i], results[i]);
        }
        final String[] expectedResults2 = new String[]{ "ab", "cd:ef" };
        results = StringUtils.split("ab:cd:ef", ":", 2);
        Assertions.assertEquals(expectedResults2.length, results.length);
        for (int i = 0; i < (expectedResults2.length); i++) {
            Assertions.assertEquals(expectedResults2[i], results[i]);
        }
    }

    @Test
    public void testSplitByWholeString_StringStringBoolean() {
        Assertions.assertArrayEquals(null, StringUtils.splitByWholeSeparator(null, "."));
        Assertions.assertEquals(0, StringUtils.splitByWholeSeparator("", ".").length);
        final String stringToSplitOnNulls = "ab   de fg";
        final String[] splitOnNullExpectedResults = new String[]{ "ab", "de", "fg" };
        final String[] splitOnNullResults = StringUtils.splitByWholeSeparator(stringToSplitOnNulls, null);
        Assertions.assertEquals(splitOnNullExpectedResults.length, splitOnNullResults.length);
        for (int i = 0; i < (splitOnNullExpectedResults.length); i += 1) {
            Assertions.assertEquals(splitOnNullExpectedResults[i], splitOnNullResults[i]);
        }
        final String stringToSplitOnCharactersAndString = "abstemiouslyaeiouyabstemiously";
        final String[] splitOnStringExpectedResults = new String[]{ "abstemiously", "abstemiously" };
        final String[] splitOnStringResults = StringUtils.splitByWholeSeparator(stringToSplitOnCharactersAndString, "aeiouy");
        Assertions.assertEquals(splitOnStringExpectedResults.length, splitOnStringResults.length);
        for (int i = 0; i < (splitOnStringExpectedResults.length); i += 1) {
            Assertions.assertEquals(splitOnStringExpectedResults[i], splitOnStringResults[i]);
        }
        final String[] splitWithMultipleSeparatorExpectedResults = new String[]{ "ab", "cd", "ef" };
        final String[] splitWithMultipleSeparator = StringUtils.splitByWholeSeparator("ab:cd::ef", ":");
        Assertions.assertEquals(splitWithMultipleSeparatorExpectedResults.length, splitWithMultipleSeparator.length);
        for (int i = 0; i < (splitWithMultipleSeparatorExpectedResults.length); i++) {
            Assertions.assertEquals(splitWithMultipleSeparatorExpectedResults[i], splitWithMultipleSeparator[i]);
        }
    }

    @Test
    public void testSplitByWholeString_StringStringBooleanInt() {
        Assertions.assertArrayEquals(null, StringUtils.splitByWholeSeparator(null, ".", 3));
        Assertions.assertEquals(0, StringUtils.splitByWholeSeparator("", ".", 3).length);
        final String stringToSplitOnNulls = "ab   de fg";
        final String[] splitOnNullExpectedResults = new String[]{ "ab", "de fg" };
        // String[] splitOnNullExpectedResults = { "ab", "de" } ;
        final String[] splitOnNullResults = StringUtils.splitByWholeSeparator(stringToSplitOnNulls, null, 2);
        Assertions.assertEquals(splitOnNullExpectedResults.length, splitOnNullResults.length);
        for (int i = 0; i < (splitOnNullExpectedResults.length); i += 1) {
            Assertions.assertEquals(splitOnNullExpectedResults[i], splitOnNullResults[i]);
        }
        final String stringToSplitOnCharactersAndString = "abstemiouslyaeiouyabstemiouslyaeiouyabstemiously";
        final String[] splitOnStringExpectedResults = new String[]{ "abstemiously", "abstemiouslyaeiouyabstemiously" };
        // String[] splitOnStringExpectedResults = { "abstemiously", "abstemiously" } ;
        final String[] splitOnStringResults = StringUtils.splitByWholeSeparator(stringToSplitOnCharactersAndString, "aeiouy", 2);
        Assertions.assertEquals(splitOnStringExpectedResults.length, splitOnStringResults.length);
        for (int i = 0; i < (splitOnStringExpectedResults.length); i++) {
            Assertions.assertEquals(splitOnStringExpectedResults[i], splitOnStringResults[i]);
        }
    }

    @Test
    public void testSplitByWholeSeparatorPreserveAllTokens_StringString() {
        Assertions.assertArrayEquals(null, StringUtils.splitByWholeSeparatorPreserveAllTokens(null, "."));
        Assertions.assertEquals(0, StringUtils.splitByWholeSeparatorPreserveAllTokens("", ".").length);
        // test whitespace
        String input = "ab   de fg";
        String[] expected = new String[]{ "ab", "", "", "de", "fg" };
        String[] actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, null);
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
        // test delimiter singlechar
        input = "1::2:::3::::4";
        expected = new String[]{ "1", "", "2", "", "", "3", "", "", "", "4" };
        actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, ":");
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
        // test delimiter multichar
        input = "1::2:::3::::4";
        expected = new String[]{ "1", "2", ":3", "", "4" };
        actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, "::");
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    public void testSplitByWholeSeparatorPreserveAllTokens_StringStringInt() {
        Assertions.assertArrayEquals(null, StringUtils.splitByWholeSeparatorPreserveAllTokens(null, ".", (-1)));
        Assertions.assertEquals(0, StringUtils.splitByWholeSeparatorPreserveAllTokens("", ".", (-1)).length);
        // test whitespace
        String input = "ab   de fg";
        String[] expected = new String[]{ "ab", "", "", "de", "fg" };
        String[] actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, null, (-1));
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
        // test delimiter singlechar
        input = "1::2:::3::::4";
        expected = new String[]{ "1", "", "2", "", "", "3", "", "", "", "4" };
        actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, ":", (-1));
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
        // test delimiter multichar
        input = "1::2:::3::::4";
        expected = new String[]{ "1", "2", ":3", "", "4" };
        actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, "::", (-1));
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
        // test delimiter char with max
        input = "1::2::3:4";
        expected = new String[]{ "1", "", "2", ":3:4" };
        actual = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, ":", 4);
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i += 1) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    public void testSplitPreserveAllTokens_String() {
        Assertions.assertNull(StringUtils.splitPreserveAllTokens(null));
        Assertions.assertEquals(0, StringUtils.splitPreserveAllTokens("").length);
        String str = "abc def";
        String[] res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(2, res.length);
        Assertions.assertEquals("abc", res[0]);
        Assertions.assertEquals("def", res[1]);
        str = "abc  def";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("abc", res[0]);
        Assertions.assertEquals("", res[1]);
        Assertions.assertEquals("def", res[2]);
        str = " abc ";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("abc", res[1]);
        Assertions.assertEquals("", res[2]);
        str = "a b .c";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals(".c", res[2]);
        str = " a b .c";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("a", res[1]);
        Assertions.assertEquals("b", res[2]);
        Assertions.assertEquals(".c", res[3]);
        str = "a  b  .c";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(5, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("", res[1]);
        Assertions.assertEquals("b", res[2]);
        Assertions.assertEquals("", res[3]);
        Assertions.assertEquals(".c", res[4]);
        str = " a  ";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("a", res[1]);
        Assertions.assertEquals("", res[2]);
        Assertions.assertEquals("", res[3]);
        str = " a  b";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("a", res[1]);
        Assertions.assertEquals("", res[2]);
        Assertions.assertEquals("b", res[3]);
        str = ((("a" + (StringUtilsTest.WHITESPACE)) + "b") + (StringUtilsTest.NON_WHITESPACE)) + "c";
        res = StringUtils.splitPreserveAllTokens(str);
        Assertions.assertEquals(((StringUtilsTest.WHITESPACE.length()) + 1), res.length);
        Assertions.assertEquals("a", res[0]);
        for (int i = 1; i < ((StringUtilsTest.WHITESPACE.length()) - 1); i++) {
            Assertions.assertEquals("", res[i]);
        }
        Assertions.assertEquals((("b" + (StringUtilsTest.NON_WHITESPACE)) + "c"), res[StringUtilsTest.WHITESPACE.length()]);
    }

    @Test
    public void testSplitPreserveAllTokens_StringChar() {
        Assertions.assertNull(StringUtils.splitPreserveAllTokens(null, '.'));
        Assertions.assertEquals(0, StringUtils.splitPreserveAllTokens("", '.').length);
        String str = "a.b. c";
        String[] res = StringUtils.splitPreserveAllTokens(str, '.');
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals(" c", res[2]);
        str = "a.b.. c";
        res = StringUtils.splitPreserveAllTokens(str, '.');
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals("", res[2]);
        Assertions.assertEquals(" c", res[3]);
        str = ".a.";
        res = StringUtils.splitPreserveAllTokens(str, '.');
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("a", res[1]);
        Assertions.assertEquals("", res[2]);
        str = ".a..";
        res = StringUtils.splitPreserveAllTokens(str, '.');
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("a", res[1]);
        Assertions.assertEquals("", res[2]);
        Assertions.assertEquals("", res[3]);
        str = "..a.";
        res = StringUtils.splitPreserveAllTokens(str, '.');
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("", res[1]);
        Assertions.assertEquals("a", res[2]);
        Assertions.assertEquals("", res[3]);
        str = "..a";
        res = StringUtils.splitPreserveAllTokens(str, '.');
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("", res[1]);
        Assertions.assertEquals("a", res[2]);
        str = "a b c";
        res = StringUtils.splitPreserveAllTokens(str, ' ');
        Assertions.assertEquals(3, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals("c", res[2]);
        str = "a  b  c";
        res = StringUtils.splitPreserveAllTokens(str, ' ');
        Assertions.assertEquals(5, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("", res[1]);
        Assertions.assertEquals("b", res[2]);
        Assertions.assertEquals("", res[3]);
        Assertions.assertEquals("c", res[4]);
        str = " a b c";
        res = StringUtils.splitPreserveAllTokens(str, ' ');
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("a", res[1]);
        Assertions.assertEquals("b", res[2]);
        Assertions.assertEquals("c", res[3]);
        str = "  a b c";
        res = StringUtils.splitPreserveAllTokens(str, ' ');
        Assertions.assertEquals(5, res.length);
        Assertions.assertEquals("", res[0]);
        Assertions.assertEquals("", res[1]);
        Assertions.assertEquals("a", res[2]);
        Assertions.assertEquals("b", res[3]);
        Assertions.assertEquals("c", res[4]);
        str = "a b c ";
        res = StringUtils.splitPreserveAllTokens(str, ' ');
        Assertions.assertEquals(4, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals("c", res[2]);
        Assertions.assertEquals("", res[3]);
        str = "a b c  ";
        res = StringUtils.splitPreserveAllTokens(str, ' ');
        Assertions.assertEquals(5, res.length);
        Assertions.assertEquals("a", res[0]);
        Assertions.assertEquals("b", res[1]);
        Assertions.assertEquals("c", res[2]);
        Assertions.assertEquals("", res[3]);
        Assertions.assertEquals("", res[3]);
        // Match example in javadoc
        {
            String[] results;
            final String[] expectedResults = new String[]{ "a", "", "b", "c" };
            results = StringUtils.splitPreserveAllTokens("a..b.c", '.');
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
    }

    @Test
    public void testSplitPreserveAllTokens_StringString_StringStringInt() {
        Assertions.assertNull(StringUtils.splitPreserveAllTokens(null, "."));
        Assertions.assertNull(StringUtils.splitPreserveAllTokens(null, ".", 3));
        Assertions.assertEquals(0, StringUtils.splitPreserveAllTokens("", ".").length);
        Assertions.assertEquals(0, StringUtils.splitPreserveAllTokens("", ".", 3).length);
        innerTestSplitPreserveAllTokens('.', ".", ' ');
        innerTestSplitPreserveAllTokens('.', ".", ',');
        innerTestSplitPreserveAllTokens('.', ".,", 'x');
        for (int i = 0; i < (StringUtilsTest.WHITESPACE.length()); i++) {
            for (int j = 0; j < (StringUtilsTest.NON_WHITESPACE.length()); j++) {
                innerTestSplitPreserveAllTokens(StringUtilsTest.WHITESPACE.charAt(i), null, StringUtilsTest.NON_WHITESPACE.charAt(j));
                innerTestSplitPreserveAllTokens(StringUtilsTest.WHITESPACE.charAt(i), String.valueOf(StringUtilsTest.WHITESPACE.charAt(i)), StringUtilsTest.NON_WHITESPACE.charAt(j));
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "de fg" };
            results = StringUtils.splitPreserveAllTokens("ab de fg", null, 2);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "  de fg" };
            results = StringUtils.splitPreserveAllTokens("ab   de fg", null, 2);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "::de:fg" };
            results = StringUtils.splitPreserveAllTokens("ab:::de:fg", ":", 2);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "", " de fg" };
            results = StringUtils.splitPreserveAllTokens("ab   de fg", null, 3);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "", "", "de fg" };
            results = StringUtils.splitPreserveAllTokens("ab   de fg", null, 4);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            final String[] expectedResults = new String[]{ "ab", "cd:ef" };
            String[] results;
            results = StringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 2);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", ":cd:ef" };
            results = StringUtils.splitPreserveAllTokens("ab::cd:ef", ":", 2);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "", ":cd:ef" };
            results = StringUtils.splitPreserveAllTokens("ab:::cd:ef", ":", 3);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "ab", "", "", "cd:ef" };
            results = StringUtils.splitPreserveAllTokens("ab:::cd:ef", ":", 4);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "", "ab", "", "", "cd:ef" };
            results = StringUtils.splitPreserveAllTokens(":ab:::cd:ef", ":", 5);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
        {
            String[] results;
            final String[] expectedResults = new String[]{ "", "", "ab", "", "", "cd:ef" };
            results = StringUtils.splitPreserveAllTokens("::ab:::cd:ef", ":", 6);
            Assertions.assertEquals(expectedResults.length, results.length);
            for (int i = 0; i < (expectedResults.length); i++) {
                Assertions.assertEquals(expectedResults[i], results[i]);
            }
        }
    }

    @Test
    public void testSplitByCharacterType() {
        Assertions.assertNull(StringUtils.splitByCharacterType(null));
        Assertions.assertEquals(0, StringUtils.splitByCharacterType("").length);
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ab", " ", "de", " ", "fg" }, StringUtils.splitByCharacterType("ab de fg")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ab", "   ", "de", " ", "fg" }, StringUtils.splitByCharacterType("ab   de fg")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ab", ":", "cd", ":", "ef" }, StringUtils.splitByCharacterType("ab:cd:ef")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "number", "5" }, StringUtils.splitByCharacterType("number5")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "foo", "B", "ar" }, StringUtils.splitByCharacterType("fooBar")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "foo", "200", "B", "ar" }, StringUtils.splitByCharacterType("foo200Bar")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ASFR", "ules" }, StringUtils.splitByCharacterType("ASFRules")));
    }

    @Test
    public void testSplitByCharacterTypeCamelCase() {
        Assertions.assertNull(StringUtils.splitByCharacterTypeCamelCase(null));
        Assertions.assertEquals(0, StringUtils.splitByCharacterTypeCamelCase("").length);
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ab", " ", "de", " ", "fg" }, StringUtils.splitByCharacterTypeCamelCase("ab de fg")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ab", "   ", "de", " ", "fg" }, StringUtils.splitByCharacterTypeCamelCase("ab   de fg")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ab", ":", "cd", ":", "ef" }, StringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "number", "5" }, StringUtils.splitByCharacterTypeCamelCase("number5")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "foo", "Bar" }, StringUtils.splitByCharacterTypeCamelCase("fooBar")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "foo", "200", "Bar" }, StringUtils.splitByCharacterTypeCamelCase("foo200Bar")));
        Assertions.assertTrue(Objects.deepEquals(new String[]{ "ASF", "Rules" }, StringUtils.splitByCharacterTypeCamelCase("ASFRules")));
    }

    @Test
    public void testDeleteWhitespace_String() {
        Assertions.assertNull(StringUtils.deleteWhitespace(null));
        Assertions.assertEquals("", StringUtils.deleteWhitespace(""));
        Assertions.assertEquals("", StringUtils.deleteWhitespace("  \f  \t\t\u001f\n\n \u000b  "));
        Assertions.assertEquals("", StringUtils.deleteWhitespace(StringUtilsTest.WHITESPACE));
        Assertions.assertEquals(StringUtilsTest.NON_WHITESPACE, StringUtils.deleteWhitespace(StringUtilsTest.NON_WHITESPACE));
        // Note: u-2007 and u-000A both cause problems in the source code
        // it should ignore 2007 but delete 000A
        Assertions.assertEquals("\u00a0\u202f", StringUtils.deleteWhitespace("  \u00a0  \t\t\n\n \u202f  "));
        Assertions.assertEquals("\u00a0\u202f", StringUtils.deleteWhitespace("\u00a0\u202f"));
        Assertions.assertEquals("test", StringUtils.deleteWhitespace("\u000bt  \t\n\te\rs\n\n   \tt"));
    }

    @Test
    public void testLang623() {
        Assertions.assertEquals("t", StringUtils.replaceChars("\u00de", '\u00de', 't'));
        Assertions.assertEquals("t", StringUtils.replaceChars("\u00fe", '\u00fe', 't'));
    }

    @Test
    public void testReplace_StringStringString() {
        Assertions.assertNull(StringUtils.replace(null, null, null));
        Assertions.assertNull(StringUtils.replace(null, null, "any"));
        Assertions.assertNull(StringUtils.replace(null, "any", null));
        Assertions.assertNull(StringUtils.replace(null, "any", "any"));
        Assertions.assertEquals("", StringUtils.replace("", null, null));
        Assertions.assertEquals("", StringUtils.replace("", null, "any"));
        Assertions.assertEquals("", StringUtils.replace("", "any", null));
        Assertions.assertEquals("", StringUtils.replace("", "any", "any"));
        Assertions.assertEquals("FOO", StringUtils.replace("FOO", "", "any"));
        Assertions.assertEquals("FOO", StringUtils.replace("FOO", null, "any"));
        Assertions.assertEquals("FOO", StringUtils.replace("FOO", "F", null));
        Assertions.assertEquals("FOO", StringUtils.replace("FOO", null, null));
        Assertions.assertEquals("", StringUtils.replace("foofoofoo", "foo", ""));
        Assertions.assertEquals("barbarbar", StringUtils.replace("foofoofoo", "foo", "bar"));
        Assertions.assertEquals("farfarfar", StringUtils.replace("foofoofoo", "oo", "ar"));
    }

    @Test
    public void testReplaceIgnoreCase_StringStringString() {
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, null, null));
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, null, "any"));
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, "any", null));
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, "any", "any"));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", null, null));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", null, "any"));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", "any", null));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", "any", "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceIgnoreCase("FOO", "", "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceIgnoreCase("FOO", null, "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceIgnoreCase("FOO", "F", null));
        Assertions.assertEquals("FOO", StringUtils.replaceIgnoreCase("FOO", null, null));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("foofoofoo", "foo", ""));
        Assertions.assertEquals("barbarbar", StringUtils.replaceIgnoreCase("foofoofoo", "foo", "bar"));
        Assertions.assertEquals("farfarfar", StringUtils.replaceIgnoreCase("foofoofoo", "oo", "ar"));
        // IgnoreCase
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("foofoofoo", "FOO", ""));
        Assertions.assertEquals("barbarbar", StringUtils.replaceIgnoreCase("fooFOOfoo", "foo", "bar"));
        Assertions.assertEquals("farfarfar", StringUtils.replaceIgnoreCase("foofOOfoo", "OO", "ar"));
    }

    @Test
    public void testReplacePattern_StringStringString() {
        Assertions.assertNull(StringUtils.replacePattern(null, "", ""));
        Assertions.assertEquals("any", StringUtils.replacePattern("any", null, ""));
        Assertions.assertEquals("any", StringUtils.replacePattern("any", "", null));
        Assertions.assertEquals("zzz", StringUtils.replacePattern("", "", "zzz"));
        Assertions.assertEquals("zzz", StringUtils.replacePattern("", ".*", "zzz"));
        Assertions.assertEquals("", StringUtils.replacePattern("", ".+", "zzz"));
        Assertions.assertEquals("z", StringUtils.replacePattern("<__>\n<__>", "<.*>", "z"));
        Assertions.assertEquals("z", StringUtils.replacePattern("<__>\\n<__>", "<.*>", "z"));
        Assertions.assertEquals("X", StringUtils.replacePattern("<A>\nxy\n</A>", "<A>.*</A>", "X"));
        Assertions.assertEquals("ABC___123", StringUtils.replacePattern("ABCabc123", "[a-z]", "_"));
        Assertions.assertEquals("ABC_123", StringUtils.replacePattern("ABCabc123", "[^A-Z0-9]+", "_"));
        Assertions.assertEquals("ABC123", StringUtils.replacePattern("ABCabc123", "[^A-Z0-9]+", ""));
        Assertions.assertEquals("Lorem_ipsum_dolor_sit", StringUtils.replacePattern("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2"));
    }

    @Test
    public void testRemovePattern_StringString() {
        Assertions.assertNull(StringUtils.removePattern(null, ""));
        Assertions.assertEquals("any", StringUtils.removePattern("any", null));
        Assertions.assertEquals("", StringUtils.removePattern("", ""));
        Assertions.assertEquals("", StringUtils.removePattern("", ".*"));
        Assertions.assertEquals("", StringUtils.removePattern("", ".+"));
        Assertions.assertEquals("AB", StringUtils.removePattern("A<__>\n<__>B", "<.*>"));
        Assertions.assertEquals("AB", StringUtils.removePattern("A<__>\\n<__>B", "<.*>"));
        Assertions.assertEquals("", StringUtils.removePattern("<A>x\\ny</A>", "<A>.*</A>"));
        Assertions.assertEquals("", StringUtils.removePattern("<A>\nxy\n</A>", "<A>.*</A>"));
        Assertions.assertEquals("ABC123", StringUtils.removePattern("ABCabc123", "[a-z]"));
    }

    @Test
    public void testReplaceAll_StringStringString() {
        Assertions.assertNull(StringUtils.replaceAll(null, "", ""));
        Assertions.assertEquals("any", StringUtils.replaceAll("any", null, ""));
        Assertions.assertEquals("any", StringUtils.replaceAll("any", "", null));
        Assertions.assertEquals("zzz", StringUtils.replaceAll("", "", "zzz"));
        Assertions.assertEquals("zzz", StringUtils.replaceAll("", ".*", "zzz"));
        Assertions.assertEquals("", StringUtils.replaceAll("", ".+", "zzz"));
        Assertions.assertEquals("ZZaZZbZZcZZ", StringUtils.replaceAll("abc", "", "ZZ"));
        Assertions.assertEquals("z\nz", StringUtils.replaceAll("<__>\n<__>", "<.*>", "z"));
        Assertions.assertEquals("z", StringUtils.replaceAll("<__>\n<__>", "(?s)<.*>", "z"));
        Assertions.assertEquals("ABC___123", StringUtils.replaceAll("ABCabc123", "[a-z]", "_"));
        Assertions.assertEquals("ABC_123", StringUtils.replaceAll("ABCabc123", "[^A-Z0-9]+", "_"));
        Assertions.assertEquals("ABC123", StringUtils.replaceAll("ABCabc123", "[^A-Z0-9]+", ""));
        Assertions.assertEquals("Lorem_ipsum_dolor_sit", StringUtils.replaceAll("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> StringUtils.replaceAll("any", "{badRegexSyntax}", ""), "StringUtils.replaceAll expecting PatternSyntaxException");
    }

    @Test
    public void testReplaceFirst_StringStringString() {
        Assertions.assertNull(StringUtils.replaceFirst(null, "", ""));
        Assertions.assertEquals("any", StringUtils.replaceFirst("any", null, ""));
        Assertions.assertEquals("any", StringUtils.replaceFirst("any", "", null));
        Assertions.assertEquals("zzz", StringUtils.replaceFirst("", "", "zzz"));
        Assertions.assertEquals("zzz", StringUtils.replaceFirst("", ".*", "zzz"));
        Assertions.assertEquals("", StringUtils.replaceFirst("", ".+", "zzz"));
        Assertions.assertEquals("ZZabc", StringUtils.replaceFirst("abc", "", "ZZ"));
        Assertions.assertEquals("z\n<__>", StringUtils.replaceFirst("<__>\n<__>", "<.*>", "z"));
        Assertions.assertEquals("z", StringUtils.replaceFirst("<__>\n<__>", "(?s)<.*>", "z"));
        Assertions.assertEquals("ABC_bc123", StringUtils.replaceFirst("ABCabc123", "[a-z]", "_"));
        Assertions.assertEquals("ABC_123abc", StringUtils.replaceFirst("ABCabc123abc", "[^A-Z0-9]+", "_"));
        Assertions.assertEquals("ABC123abc", StringUtils.replaceFirst("ABCabc123abc", "[^A-Z0-9]+", ""));
        Assertions.assertEquals("Lorem_ipsum  dolor   sit", StringUtils.replaceFirst("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> StringUtils.replaceFirst("any", "{badRegexSyntax}", ""), "StringUtils.replaceFirst expecting PatternSyntaxException");
    }

    @Test
    public void testReplace_StringStringStringInt() {
        Assertions.assertNull(StringUtils.replace(null, null, null, 2));
        Assertions.assertNull(StringUtils.replace(null, null, "any", 2));
        Assertions.assertNull(StringUtils.replace(null, "any", null, 2));
        Assertions.assertNull(StringUtils.replace(null, "any", "any", 2));
        Assertions.assertEquals("", StringUtils.replace("", null, null, 2));
        Assertions.assertEquals("", StringUtils.replace("", null, "any", 2));
        Assertions.assertEquals("", StringUtils.replace("", "any", null, 2));
        Assertions.assertEquals("", StringUtils.replace("", "any", "any", 2));
        final String str = new String(new char[]{ 'o', 'o', 'f', 'o', 'o' });
        Assertions.assertSame(str, StringUtils.replace(str, "x", "", (-1)));
        Assertions.assertEquals("f", StringUtils.replace("oofoo", "o", "", (-1)));
        Assertions.assertEquals("oofoo", StringUtils.replace("oofoo", "o", "", 0));
        Assertions.assertEquals("ofoo", StringUtils.replace("oofoo", "o", "", 1));
        Assertions.assertEquals("foo", StringUtils.replace("oofoo", "o", "", 2));
        Assertions.assertEquals("fo", StringUtils.replace("oofoo", "o", "", 3));
        Assertions.assertEquals("f", StringUtils.replace("oofoo", "o", "", 4));
        Assertions.assertEquals("f", StringUtils.replace("oofoo", "o", "", (-5)));
        Assertions.assertEquals("f", StringUtils.replace("oofoo", "o", "", 1000));
    }

    @Test
    public void testReplaceIgnoreCase_StringStringStringInt() {
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, null, null, 2));
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, null, "any", 2));
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, "any", null, 2));
        Assertions.assertNull(StringUtils.replaceIgnoreCase(null, "any", "any", 2));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", null, null, 2));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", null, "any", 2));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", "any", null, 2));
        Assertions.assertEquals("", StringUtils.replaceIgnoreCase("", "any", "any", 2));
        final String str = new String(new char[]{ 'o', 'o', 'f', 'o', 'o' });
        Assertions.assertSame(str, StringUtils.replaceIgnoreCase(str, "x", "", (-1)));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "o", "", (-1)));
        Assertions.assertEquals("oofoo", StringUtils.replaceIgnoreCase("oofoo", "o", "", 0));
        Assertions.assertEquals("ofoo", StringUtils.replaceIgnoreCase("oofoo", "o", "", 1));
        Assertions.assertEquals("foo", StringUtils.replaceIgnoreCase("oofoo", "o", "", 2));
        Assertions.assertEquals("fo", StringUtils.replaceIgnoreCase("oofoo", "o", "", 3));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "o", "", 4));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "o", "", (-5)));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "o", "", 1000));
        // IgnoreCase
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "O", "", (-1)));
        Assertions.assertEquals("oofoo", StringUtils.replaceIgnoreCase("oofoo", "O", "", 0));
        Assertions.assertEquals("ofoo", StringUtils.replaceIgnoreCase("oofoo", "O", "", 1));
        Assertions.assertEquals("foo", StringUtils.replaceIgnoreCase("oofoo", "O", "", 2));
        Assertions.assertEquals("fo", StringUtils.replaceIgnoreCase("oofoo", "O", "", 3));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "O", "", 4));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "O", "", (-5)));
        Assertions.assertEquals("f", StringUtils.replaceIgnoreCase("oofoo", "O", "", 1000));
    }

    @Test
    public void testReplaceOnce_StringStringString() {
        Assertions.assertNull(StringUtils.replaceOnce(null, null, null));
        Assertions.assertNull(StringUtils.replaceOnce(null, null, "any"));
        Assertions.assertNull(StringUtils.replaceOnce(null, "any", null));
        Assertions.assertNull(StringUtils.replaceOnce(null, "any", "any"));
        Assertions.assertEquals("", StringUtils.replaceOnce("", null, null));
        Assertions.assertEquals("", StringUtils.replaceOnce("", null, "any"));
        Assertions.assertEquals("", StringUtils.replaceOnce("", "any", null));
        Assertions.assertEquals("", StringUtils.replaceOnce("", "any", "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceOnce("FOO", "", "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceOnce("FOO", null, "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceOnce("FOO", "F", null));
        Assertions.assertEquals("FOO", StringUtils.replaceOnce("FOO", null, null));
        Assertions.assertEquals("foofoo", StringUtils.replaceOnce("foofoofoo", "foo", ""));
    }

    @Test
    public void testReplaceOnceIgnoreCase_StringStringString() {
        Assertions.assertNull(StringUtils.replaceOnceIgnoreCase(null, null, null));
        Assertions.assertNull(StringUtils.replaceOnceIgnoreCase(null, null, "any"));
        Assertions.assertNull(StringUtils.replaceOnceIgnoreCase(null, "any", null));
        Assertions.assertNull(StringUtils.replaceOnceIgnoreCase(null, "any", "any"));
        Assertions.assertEquals("", StringUtils.replaceOnceIgnoreCase("", null, null));
        Assertions.assertEquals("", StringUtils.replaceOnceIgnoreCase("", null, "any"));
        Assertions.assertEquals("", StringUtils.replaceOnceIgnoreCase("", "any", null));
        Assertions.assertEquals("", StringUtils.replaceOnceIgnoreCase("", "any", "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceOnceIgnoreCase("FOO", "", "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceOnceIgnoreCase("FOO", null, "any"));
        Assertions.assertEquals("FOO", StringUtils.replaceOnceIgnoreCase("FOO", "F", null));
        Assertions.assertEquals("FOO", StringUtils.replaceOnceIgnoreCase("FOO", null, null));
        Assertions.assertEquals("foofoo", StringUtils.replaceOnceIgnoreCase("foofoofoo", "foo", ""));
        // Ignore Case
        Assertions.assertEquals("Foofoo", StringUtils.replaceOnceIgnoreCase("FoOFoofoo", "foo", ""));
    }

    /**
     * Test method for 'StringUtils.replaceEach(String, String[], String[])'
     */
    @Test
    public void testReplace_StringStringArrayStringArray() {
        // JAVADOC TESTS START
        Assertions.assertNull(StringUtils.replaceEach(null, new String[]{ "a" }, new String[]{ "b" }));
        Assertions.assertEquals(StringUtils.replaceEach("", new String[]{ "a" }, new String[]{ "b" }), "");
        Assertions.assertEquals(StringUtils.replaceEach("aba", null, null), "aba");
        Assertions.assertEquals(StringUtils.replaceEach("aba", new String[0], null), "aba");
        Assertions.assertEquals(StringUtils.replaceEach("aba", null, new String[0]), "aba");
        Assertions.assertEquals(StringUtils.replaceEach("aba", new String[]{ "a" }, null), "aba");
        Assertions.assertEquals(StringUtils.replaceEach("aba", new String[]{ "a" }, new String[]{ "" }), "b");
        Assertions.assertEquals(StringUtils.replaceEach("aba", new String[]{ null }, new String[]{ "a" }), "aba");
        Assertions.assertEquals(StringUtils.replaceEach("abcde", new String[]{ "ab", "d" }, new String[]{ "w", "t" }), "wcte");
        Assertions.assertEquals(StringUtils.replaceEach("abcde", new String[]{ "ab", "d" }, new String[]{ "d", "t" }), "dcte");
        // JAVADOC TESTS END
        Assertions.assertEquals("bcc", StringUtils.replaceEach("abc", new String[]{ "a", "b" }, new String[]{ "b", "c" }));
        Assertions.assertEquals("q651.506bera", StringUtils.replaceEach("d216.102oren", new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9" }, new String[]{ "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "5", "6", "7", "8", "9", "1", "2", "3", "4" }));
        // Test null safety inside arrays - LANG-552
        Assertions.assertEquals(StringUtils.replaceEach("aba", new String[]{ "a" }, new String[]{ null }), "aba");
        Assertions.assertEquals(StringUtils.replaceEach("aba", new String[]{ "a", "b" }, new String[]{ "c", null }), "cbc");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.replaceEach("abba", new String[]{ "a" }, new String[]{ "b", "a" }), "StringUtils.replaceEach(String, String[], String[]) expecting IllegalArgumentException");
    }

    /**
     * Test method for 'StringUtils.replaceEachRepeatedly(String, String[], String[])'
     */
    @Test
    public void testReplace_StringStringArrayStringArrayBoolean() {
        // JAVADOC TESTS START
        Assertions.assertNull(StringUtils.replaceEachRepeatedly(null, new String[]{ "a" }, new String[]{ "b" }));
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("", new String[]{ "a" }, new String[]{ "b" }), "");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("aba", null, null), "aba");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("aba", new String[0], null), "aba");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("aba", null, new String[0]), "aba");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("aba", new String[0], null), "aba");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("aba", new String[]{ "a" }, new String[]{ "" }), "b");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("aba", new String[]{ null }, new String[]{ "a" }), "aba");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("abcde", new String[]{ "ab", "d" }, new String[]{ "w", "t" }), "wcte");
        Assertions.assertEquals(StringUtils.replaceEachRepeatedly("abcde", new String[]{ "ab", "d" }, new String[]{ "d", "t" }), "tcte");
        Assertions.assertThrows(IllegalStateException.class, () -> StringUtils.replaceEachRepeatedly("abcde", new String[]{ "ab", "d" }, new String[]{ "d", "ab" }), "Should be a circular reference");
        // JAVADOC TESTS END
    }

    @Test
    public void testReplaceChars_StringCharChar() {
        Assertions.assertNull(StringUtils.replaceChars(null, 'b', 'z'));
        Assertions.assertEquals("", StringUtils.replaceChars("", 'b', 'z'));
        Assertions.assertEquals("azcza", StringUtils.replaceChars("abcba", 'b', 'z'));
        Assertions.assertEquals("abcba", StringUtils.replaceChars("abcba", 'x', 'z'));
    }

    @Test
    public void testReplaceChars_StringStringString() {
        Assertions.assertNull(StringUtils.replaceChars(null, null, null));
        Assertions.assertNull(StringUtils.replaceChars(null, "", null));
        Assertions.assertNull(StringUtils.replaceChars(null, "a", null));
        Assertions.assertNull(StringUtils.replaceChars(null, null, ""));
        Assertions.assertNull(StringUtils.replaceChars(null, null, "x"));
        Assertions.assertEquals("", StringUtils.replaceChars("", null, null));
        Assertions.assertEquals("", StringUtils.replaceChars("", "", null));
        Assertions.assertEquals("", StringUtils.replaceChars("", "a", null));
        Assertions.assertEquals("", StringUtils.replaceChars("", null, ""));
        Assertions.assertEquals("", StringUtils.replaceChars("", null, "x"));
        Assertions.assertEquals("abc", StringUtils.replaceChars("abc", null, null));
        Assertions.assertEquals("abc", StringUtils.replaceChars("abc", null, ""));
        Assertions.assertEquals("abc", StringUtils.replaceChars("abc", null, "x"));
        Assertions.assertEquals("abc", StringUtils.replaceChars("abc", "", null));
        Assertions.assertEquals("abc", StringUtils.replaceChars("abc", "", ""));
        Assertions.assertEquals("abc", StringUtils.replaceChars("abc", "", "x"));
        Assertions.assertEquals("ac", StringUtils.replaceChars("abc", "b", null));
        Assertions.assertEquals("ac", StringUtils.replaceChars("abc", "b", ""));
        Assertions.assertEquals("axc", StringUtils.replaceChars("abc", "b", "x"));
        Assertions.assertEquals("ayzya", StringUtils.replaceChars("abcba", "bc", "yz"));
        Assertions.assertEquals("ayya", StringUtils.replaceChars("abcba", "bc", "y"));
        Assertions.assertEquals("ayzya", StringUtils.replaceChars("abcba", "bc", "yzx"));
        Assertions.assertEquals("abcba", StringUtils.replaceChars("abcba", "z", "w"));
        Assertions.assertSame("abcba", StringUtils.replaceChars("abcba", "z", "w"));
        // Javadoc examples:
        Assertions.assertEquals("jelly", StringUtils.replaceChars("hello", "ho", "jy"));
        Assertions.assertEquals("ayzya", StringUtils.replaceChars("abcba", "bc", "yz"));
        Assertions.assertEquals("ayya", StringUtils.replaceChars("abcba", "bc", "y"));
        Assertions.assertEquals("ayzya", StringUtils.replaceChars("abcba", "bc", "yzx"));
        // From http://issues.apache.org/bugzilla/show_bug.cgi?id=25454
        Assertions.assertEquals("bcc", StringUtils.replaceChars("abc", "ab", "bc"));
        Assertions.assertEquals("q651.506bera", StringUtils.replaceChars("d216.102oren", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", "nopqrstuvwxyzabcdefghijklmNOPQRSTUVWXYZABCDEFGHIJKLM567891234"));
    }

    @Test
    public void testOverlay_StringStringIntInt() {
        Assertions.assertNull(StringUtils.overlay(null, null, 2, 4));
        Assertions.assertNull(StringUtils.overlay(null, null, (-2), (-4)));
        Assertions.assertEquals("", StringUtils.overlay("", null, 0, 0));
        Assertions.assertEquals("", StringUtils.overlay("", "", 0, 0));
        Assertions.assertEquals("zzzz", StringUtils.overlay("", "zzzz", 0, 0));
        Assertions.assertEquals("zzzz", StringUtils.overlay("", "zzzz", 2, 4));
        Assertions.assertEquals("zzzz", StringUtils.overlay("", "zzzz", (-2), (-4)));
        Assertions.assertEquals("abef", StringUtils.overlay("abcdef", null, 2, 4));
        Assertions.assertEquals("abef", StringUtils.overlay("abcdef", null, 4, 2));
        Assertions.assertEquals("abef", StringUtils.overlay("abcdef", "", 2, 4));
        Assertions.assertEquals("abef", StringUtils.overlay("abcdef", "", 4, 2));
        Assertions.assertEquals("abzzzzef", StringUtils.overlay("abcdef", "zzzz", 2, 4));
        Assertions.assertEquals("abzzzzef", StringUtils.overlay("abcdef", "zzzz", 4, 2));
        Assertions.assertEquals("zzzzef", StringUtils.overlay("abcdef", "zzzz", (-1), 4));
        Assertions.assertEquals("zzzzef", StringUtils.overlay("abcdef", "zzzz", 4, (-1)));
        Assertions.assertEquals("zzzzabcdef", StringUtils.overlay("abcdef", "zzzz", (-2), (-1)));
        Assertions.assertEquals("zzzzabcdef", StringUtils.overlay("abcdef", "zzzz", (-1), (-2)));
        Assertions.assertEquals("abcdzzzz", StringUtils.overlay("abcdef", "zzzz", 4, 10));
        Assertions.assertEquals("abcdzzzz", StringUtils.overlay("abcdef", "zzzz", 10, 4));
        Assertions.assertEquals("abcdefzzzz", StringUtils.overlay("abcdef", "zzzz", 8, 10));
        Assertions.assertEquals("abcdefzzzz", StringUtils.overlay("abcdef", "zzzz", 10, 8));
    }

    @Test
    public void testRepeat_StringInt() {
        Assertions.assertNull(StringUtils.repeat(null, 2));
        Assertions.assertEquals("", StringUtils.repeat("ab", 0));
        Assertions.assertEquals("", StringUtils.repeat("", 3));
        Assertions.assertEquals("aaa", StringUtils.repeat("a", 3));
        Assertions.assertEquals("", StringUtils.repeat("a", (-2)));
        Assertions.assertEquals("ababab", StringUtils.repeat("ab", 3));
        Assertions.assertEquals("abcabcabc", StringUtils.repeat("abc", 3));
        final String str = StringUtils.repeat("a", 10000);// bigger than pad limit

        Assertions.assertEquals(10000, str.length());
        Assertions.assertTrue(StringUtils.containsOnly(str, 'a'));
    }

    @Test
    public void testRepeat_StringStringInt() {
        Assertions.assertNull(StringUtils.repeat(null, null, 2));
        Assertions.assertNull(StringUtils.repeat(null, "x", 2));
        Assertions.assertEquals("", StringUtils.repeat("", null, 2));
        Assertions.assertEquals("", StringUtils.repeat("ab", "", 0));
        Assertions.assertEquals("", StringUtils.repeat("", "", 2));
        Assertions.assertEquals("xx", StringUtils.repeat("", "x", 3));
        Assertions.assertEquals("?, ?, ?", StringUtils.repeat("?", ", ", 3));
    }

    @Test
    public void testRepeat_CharInt() {
        Assertions.assertEquals("zzz", StringUtils.repeat('z', 3));
        Assertions.assertEquals("", StringUtils.repeat('z', 0));
        Assertions.assertEquals("", StringUtils.repeat('z', (-2)));
    }

    @Test
    public void testChop() {
        final String[][] chopCases = new String[][]{ new String[]{ (StringUtilsTest.FOO_UNCAP) + "\r\n", StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\n", StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\r", StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + " \r", (StringUtilsTest.FOO_UNCAP) + " " }, new String[]{ "foo", "fo" }, new String[]{ "foo\nfoo", "foo\nfo" }, new String[]{ "\n", "" }, new String[]{ "\r", "" }, new String[]{ "\r\n", "" }, new String[]{ null, null }, new String[]{ "", "" }, new String[]{ "a", "" } };
        for (final String[] chopCase : chopCases) {
            final String original = chopCase[0];
            final String expectedResult = chopCase[1];
            Assertions.assertEquals(expectedResult, StringUtils.chop(original), "chop(String) failed");
        }
    }

    @Test
    public void testChomp() {
        final String[][] chompCases = new String[][]{ new String[]{ (StringUtilsTest.FOO_UNCAP) + "\r\n", StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\n", StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\r", StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + " \r", (StringUtilsTest.FOO_UNCAP) + " " }, new String[]{ StringUtilsTest.FOO_UNCAP, StringUtilsTest.FOO_UNCAP }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\n\n", (StringUtilsTest.FOO_UNCAP) + "\n" }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\r\n\r\n", (StringUtilsTest.FOO_UNCAP) + "\r\n" }, new String[]{ "foo\nfoo", "foo\nfoo" }, new String[]{ "foo\n\rfoo", "foo\n\rfoo" }, new String[]{ "\n", "" }, new String[]{ "\r", "" }, new String[]{ "a", "a" }, new String[]{ "\r\n", "" }, new String[]{ "", "" }, new String[]{ null, null }, new String[]{ (StringUtilsTest.FOO_UNCAP) + "\n\r", (StringUtilsTest.FOO_UNCAP) + "\n" } };
        for (final String[] chompCase : chompCases) {
            final String original = chompCase[0];
            final String expectedResult = chompCase[1];
            Assertions.assertEquals(expectedResult, StringUtils.chomp(original), "chomp(String) failed");
        }
        Assertions.assertEquals("foo", StringUtils.chomp("foobar", "bar"), "chomp(String, String) failed");
        Assertions.assertEquals("foobar", StringUtils.chomp("foobar", "baz"), "chomp(String, String) failed");
        Assertions.assertEquals("foo", StringUtils.chomp("foo", "foooo"), "chomp(String, String) failed");
        Assertions.assertEquals("foobar", StringUtils.chomp("foobar", ""), "chomp(String, String) failed");
        Assertions.assertEquals("foobar", StringUtils.chomp("foobar", null), "chomp(String, String) failed");
        Assertions.assertEquals("", StringUtils.chomp("", "foo"), "chomp(String, String) failed");
        Assertions.assertEquals("", StringUtils.chomp("", null), "chomp(String, String) failed");
        Assertions.assertEquals("", StringUtils.chomp("", ""), "chomp(String, String) failed");
        Assertions.assertNull(StringUtils.chomp(null, "foo"), "chomp(String, String) failed");
        Assertions.assertNull(StringUtils.chomp(null, null), "chomp(String, String) failed");
        Assertions.assertNull(StringUtils.chomp(null, ""), "chomp(String, String) failed");
        Assertions.assertEquals("", StringUtils.chomp("foo", "foo"), "chomp(String, String) failed");
        Assertions.assertEquals(" ", StringUtils.chomp(" foo", "foo"), "chomp(String, String) failed");
        Assertions.assertEquals("foo ", StringUtils.chomp("foo ", "foo"), "chomp(String, String) failed");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testRightPad_StringInt() {
        Assertions.assertNull(StringUtils.rightPad(null, 5));
        Assertions.assertEquals("     ", StringUtils.rightPad("", 5));
        Assertions.assertEquals("abc  ", StringUtils.rightPad("abc", 5));
        Assertions.assertEquals("abc", StringUtils.rightPad("abc", 2));
        Assertions.assertEquals("abc", StringUtils.rightPad("abc", (-1)));
    }

    @Test
    public void testRightPad_StringIntChar() {
        Assertions.assertNull(StringUtils.rightPad(null, 5, ' '));
        Assertions.assertEquals("     ", StringUtils.rightPad("", 5, ' '));
        Assertions.assertEquals("abc  ", StringUtils.rightPad("abc", 5, ' '));
        Assertions.assertEquals("abc", StringUtils.rightPad("abc", 2, ' '));
        Assertions.assertEquals("abc", StringUtils.rightPad("abc", (-1), ' '));
        Assertions.assertEquals("abcxx", StringUtils.rightPad("abc", 5, 'x'));
        final String str = StringUtils.rightPad("aaa", 10000, 'a');// bigger than pad length

        Assertions.assertEquals(10000, str.length());
        Assertions.assertTrue(StringUtils.containsOnly(str, 'a'));
    }

    @Test
    public void testRightPad_StringIntString() {
        Assertions.assertNull(StringUtils.rightPad(null, 5, "-+"));
        Assertions.assertEquals("     ", StringUtils.rightPad("", 5, " "));
        Assertions.assertNull(StringUtils.rightPad(null, 8, null));
        Assertions.assertEquals("abc-+-+", StringUtils.rightPad("abc", 7, "-+"));
        Assertions.assertEquals("abc-+~", StringUtils.rightPad("abc", 6, "-+~"));
        Assertions.assertEquals("abc-+", StringUtils.rightPad("abc", 5, "-+~"));
        Assertions.assertEquals("abc", StringUtils.rightPad("abc", 2, " "));
        Assertions.assertEquals("abc", StringUtils.rightPad("abc", (-1), " "));
        Assertions.assertEquals("abc  ", StringUtils.rightPad("abc", 5, null));
        Assertions.assertEquals("abc  ", StringUtils.rightPad("abc", 5, ""));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testLeftPad_StringInt() {
        Assertions.assertNull(StringUtils.leftPad(null, 5));
        Assertions.assertEquals("     ", StringUtils.leftPad("", 5));
        Assertions.assertEquals("  abc", StringUtils.leftPad("abc", 5));
        Assertions.assertEquals("abc", StringUtils.leftPad("abc", 2));
    }

    @Test
    public void testLeftPad_StringIntChar() {
        Assertions.assertNull(StringUtils.leftPad(null, 5, ' '));
        Assertions.assertEquals("     ", StringUtils.leftPad("", 5, ' '));
        Assertions.assertEquals("  abc", StringUtils.leftPad("abc", 5, ' '));
        Assertions.assertEquals("xxabc", StringUtils.leftPad("abc", 5, 'x'));
        Assertions.assertEquals("\uffff\uffffabc", StringUtils.leftPad("abc", 5, '\uffff'));
        Assertions.assertEquals("abc", StringUtils.leftPad("abc", 2, ' '));
        final String str = StringUtils.leftPad("aaa", 10000, 'a');// bigger than pad length

        Assertions.assertEquals(10000, str.length());
        Assertions.assertTrue(StringUtils.containsOnly(str, 'a'));
    }

    @Test
    public void testLeftPad_StringIntString() {
        Assertions.assertNull(StringUtils.leftPad(null, 5, "-+"));
        Assertions.assertNull(StringUtils.leftPad(null, 5, null));
        Assertions.assertEquals("     ", StringUtils.leftPad("", 5, " "));
        Assertions.assertEquals("-+-+abc", StringUtils.leftPad("abc", 7, "-+"));
        Assertions.assertEquals("-+~abc", StringUtils.leftPad("abc", 6, "-+~"));
        Assertions.assertEquals("-+abc", StringUtils.leftPad("abc", 5, "-+~"));
        Assertions.assertEquals("abc", StringUtils.leftPad("abc", 2, " "));
        Assertions.assertEquals("abc", StringUtils.leftPad("abc", (-1), " "));
        Assertions.assertEquals("  abc", StringUtils.leftPad("abc", 5, null));
        Assertions.assertEquals("  abc", StringUtils.leftPad("abc", 5, ""));
    }

    @Test
    public void testLengthString() {
        Assertions.assertEquals(0, StringUtils.length(null));
        Assertions.assertEquals(0, StringUtils.length(""));
        Assertions.assertEquals(0, StringUtils.length(StringUtils.EMPTY));
        Assertions.assertEquals(1, StringUtils.length("A"));
        Assertions.assertEquals(1, StringUtils.length(" "));
        Assertions.assertEquals(8, StringUtils.length("ABCDEFGH"));
    }

    @Test
    public void testLengthStringBuffer() {
        Assertions.assertEquals(0, StringUtils.length(new StringBuffer("")));
        Assertions.assertEquals(0, StringUtils.length(new StringBuffer(StringUtils.EMPTY)));
        Assertions.assertEquals(1, StringUtils.length(new StringBuffer("A")));
        Assertions.assertEquals(1, StringUtils.length(new StringBuffer(" ")));
        Assertions.assertEquals(8, StringUtils.length(new StringBuffer("ABCDEFGH")));
    }

    @Test
    public void testLengthStringBuilder() {
        Assertions.assertEquals(0, StringUtils.length(new StringBuilder("")));
        Assertions.assertEquals(0, StringUtils.length(new StringBuilder(StringUtils.EMPTY)));
        Assertions.assertEquals(1, StringUtils.length(new StringBuilder("A")));
        Assertions.assertEquals(1, StringUtils.length(new StringBuilder(" ")));
        Assertions.assertEquals(8, StringUtils.length(new StringBuilder("ABCDEFGH")));
    }

    @Test
    public void testLength_CharBuffer() {
        Assertions.assertEquals(0, StringUtils.length(CharBuffer.wrap("")));
        Assertions.assertEquals(1, StringUtils.length(CharBuffer.wrap("A")));
        Assertions.assertEquals(1, StringUtils.length(CharBuffer.wrap(" ")));
        Assertions.assertEquals(8, StringUtils.length(CharBuffer.wrap("ABCDEFGH")));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCenter_StringInt() {
        Assertions.assertNull(StringUtils.center(null, (-1)));
        Assertions.assertNull(StringUtils.center(null, 4));
        Assertions.assertEquals("    ", StringUtils.center("", 4));
        Assertions.assertEquals("ab", StringUtils.center("ab", 0));
        Assertions.assertEquals("ab", StringUtils.center("ab", (-1)));
        Assertions.assertEquals("ab", StringUtils.center("ab", 1));
        Assertions.assertEquals("    ", StringUtils.center("", 4));
        Assertions.assertEquals(" ab ", StringUtils.center("ab", 4));
        Assertions.assertEquals("abcd", StringUtils.center("abcd", 2));
        Assertions.assertEquals(" a  ", StringUtils.center("a", 4));
        Assertions.assertEquals("  a  ", StringUtils.center("a", 5));
    }

    @Test
    public void testCenter_StringIntChar() {
        Assertions.assertNull(StringUtils.center(null, (-1), ' '));
        Assertions.assertNull(StringUtils.center(null, 4, ' '));
        Assertions.assertEquals("    ", StringUtils.center("", 4, ' '));
        Assertions.assertEquals("ab", StringUtils.center("ab", 0, ' '));
        Assertions.assertEquals("ab", StringUtils.center("ab", (-1), ' '));
        Assertions.assertEquals("ab", StringUtils.center("ab", 1, ' '));
        Assertions.assertEquals("    ", StringUtils.center("", 4, ' '));
        Assertions.assertEquals(" ab ", StringUtils.center("ab", 4, ' '));
        Assertions.assertEquals("abcd", StringUtils.center("abcd", 2, ' '));
        Assertions.assertEquals(" a  ", StringUtils.center("a", 4, ' '));
        Assertions.assertEquals("  a  ", StringUtils.center("a", 5, ' '));
        Assertions.assertEquals("xxaxx", StringUtils.center("a", 5, 'x'));
    }

    @Test
    public void testCenter_StringIntString() {
        Assertions.assertNull(StringUtils.center(null, 4, null));
        Assertions.assertNull(StringUtils.center(null, (-1), " "));
        Assertions.assertNull(StringUtils.center(null, 4, " "));
        Assertions.assertEquals("    ", StringUtils.center("", 4, " "));
        Assertions.assertEquals("ab", StringUtils.center("ab", 0, " "));
        Assertions.assertEquals("ab", StringUtils.center("ab", (-1), " "));
        Assertions.assertEquals("ab", StringUtils.center("ab", 1, " "));
        Assertions.assertEquals("    ", StringUtils.center("", 4, " "));
        Assertions.assertEquals(" ab ", StringUtils.center("ab", 4, " "));
        Assertions.assertEquals("abcd", StringUtils.center("abcd", 2, " "));
        Assertions.assertEquals(" a  ", StringUtils.center("a", 4, " "));
        Assertions.assertEquals("yayz", StringUtils.center("a", 4, "yz"));
        Assertions.assertEquals("yzyayzy", StringUtils.center("a", 7, "yz"));
        Assertions.assertEquals("  abc  ", StringUtils.center("abc", 7, null));
        Assertions.assertEquals("  abc  ", StringUtils.center("abc", 7, ""));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testRotate_StringInt() {
        Assertions.assertNull(StringUtils.rotate(null, 1));
        Assertions.assertEquals("", StringUtils.rotate("", 1));
        Assertions.assertEquals("abcdefg", StringUtils.rotate("abcdefg", 0));
        Assertions.assertEquals("fgabcde", StringUtils.rotate("abcdefg", 2));
        Assertions.assertEquals("cdefgab", StringUtils.rotate("abcdefg", (-2)));
        Assertions.assertEquals("abcdefg", StringUtils.rotate("abcdefg", 7));
        Assertions.assertEquals("abcdefg", StringUtils.rotate("abcdefg", (-7)));
        Assertions.assertEquals("fgabcde", StringUtils.rotate("abcdefg", 9));
        Assertions.assertEquals("cdefgab", StringUtils.rotate("abcdefg", (-9)));
        Assertions.assertEquals("efgabcd", StringUtils.rotate("abcdefg", 17));
        Assertions.assertEquals("defgabc", StringUtils.rotate("abcdefg", (-17)));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReverse_String() {
        Assertions.assertNull(StringUtils.reverse(null));
        Assertions.assertEquals("", StringUtils.reverse(""));
        Assertions.assertEquals("sdrawkcab", StringUtils.reverse("backwards"));
    }

    @Test
    public void testReverseDelimited_StringChar() {
        Assertions.assertNull(StringUtils.reverseDelimited(null, '.'));
        Assertions.assertEquals("", StringUtils.reverseDelimited("", '.'));
        Assertions.assertEquals("c.b.a", StringUtils.reverseDelimited("a.b.c", '.'));
        Assertions.assertEquals("a b c", StringUtils.reverseDelimited("a b c", '.'));
        Assertions.assertEquals("", StringUtils.reverseDelimited("", '.'));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDefault_String() {
        Assertions.assertEquals("", StringUtils.defaultString(null));
        Assertions.assertEquals("", StringUtils.defaultString(""));
        Assertions.assertEquals("abc", StringUtils.defaultString("abc"));
    }

    @Test
    public void testDefault_StringString() {
        Assertions.assertEquals("NULL", StringUtils.defaultString(null, "NULL"));
        Assertions.assertEquals("", StringUtils.defaultString("", "NULL"));
        Assertions.assertEquals("abc", StringUtils.defaultString("abc", "NULL"));
    }

    @Test
    public void testDefaultIfEmpty_StringString() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfEmpty(null, "NULL"));
        Assertions.assertEquals("NULL", StringUtils.defaultIfEmpty("", "NULL"));
        Assertions.assertEquals("abc", StringUtils.defaultIfEmpty("abc", "NULL"));
        Assertions.assertNull(StringUtils.defaultIfEmpty("", null));
        // Tests compatibility for the API return type
        final String s = StringUtils.defaultIfEmpty("abc", "NULL");
        Assertions.assertEquals("abc", s);
    }

    @Test
    public void testDefaultIfBlank_StringString() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(null, "NULL"));
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank("", "NULL"));
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(" ", "NULL"));
        Assertions.assertEquals("abc", StringUtils.defaultIfBlank("abc", "NULL"));
        Assertions.assertNull(StringUtils.defaultIfBlank("", null));
        // Tests compatibility for the API return type
        final String s = StringUtils.defaultIfBlank("abc", "NULL");
        Assertions.assertEquals("abc", s);
    }

    @Test
    public void testDefaultIfEmpty_StringBuilders() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfEmpty(new StringBuilder(""), new StringBuilder("NULL")).toString());
        Assertions.assertEquals("abc", StringUtils.defaultIfEmpty(new StringBuilder("abc"), new StringBuilder("NULL")).toString());
        Assertions.assertNull(StringUtils.defaultIfEmpty(new StringBuilder(""), null));
        // Tests compatibility for the API return type
        final StringBuilder s = StringUtils.defaultIfEmpty(new StringBuilder("abc"), new StringBuilder("NULL"));
        Assertions.assertEquals("abc", s.toString());
    }

    @Test
    public void testDefaultIfBlank_StringBuilders() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(new StringBuilder(""), new StringBuilder("NULL")).toString());
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(new StringBuilder(" "), new StringBuilder("NULL")).toString());
        Assertions.assertEquals("abc", StringUtils.defaultIfBlank(new StringBuilder("abc"), new StringBuilder("NULL")).toString());
        Assertions.assertNull(StringUtils.defaultIfBlank(new StringBuilder(""), null));
        // Tests compatibility for the API return type
        final StringBuilder s = StringUtils.defaultIfBlank(new StringBuilder("abc"), new StringBuilder("NULL"));
        Assertions.assertEquals("abc", s.toString());
    }

    @Test
    public void testDefaultIfEmpty_StringBuffers() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfEmpty(new StringBuffer(""), new StringBuffer("NULL")).toString());
        Assertions.assertEquals("abc", StringUtils.defaultIfEmpty(new StringBuffer("abc"), new StringBuffer("NULL")).toString());
        Assertions.assertNull(StringUtils.defaultIfEmpty(new StringBuffer(""), null));
        // Tests compatibility for the API return type
        final StringBuffer s = StringUtils.defaultIfEmpty(new StringBuffer("abc"), new StringBuffer("NULL"));
        Assertions.assertEquals("abc", s.toString());
    }

    @Test
    public void testDefaultIfBlank_StringBuffers() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(new StringBuffer(""), new StringBuffer("NULL")).toString());
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(new StringBuffer(" "), new StringBuffer("NULL")).toString());
        Assertions.assertEquals("abc", StringUtils.defaultIfBlank(new StringBuffer("abc"), new StringBuffer("NULL")).toString());
        Assertions.assertNull(StringUtils.defaultIfBlank(new StringBuffer(""), null));
        // Tests compatibility for the API return type
        final StringBuffer s = StringUtils.defaultIfBlank(new StringBuffer("abc"), new StringBuffer("NULL"));
        Assertions.assertEquals("abc", s.toString());
    }

    @Test
    public void testDefaultIfEmpty_CharBuffers() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfEmpty(CharBuffer.wrap(""), CharBuffer.wrap("NULL")).toString());
        Assertions.assertEquals("abc", StringUtils.defaultIfEmpty(CharBuffer.wrap("abc"), CharBuffer.wrap("NULL")).toString());
        Assertions.assertNull(StringUtils.defaultIfEmpty(CharBuffer.wrap(""), null));
        // Tests compatibility for the API return type
        final CharBuffer s = StringUtils.defaultIfEmpty(CharBuffer.wrap("abc"), CharBuffer.wrap("NULL"));
        Assertions.assertEquals("abc", s.toString());
    }

    @Test
    public void testDefaultIfBlank_CharBuffers() {
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(CharBuffer.wrap(""), CharBuffer.wrap("NULL")).toString());
        Assertions.assertEquals("NULL", StringUtils.defaultIfBlank(CharBuffer.wrap(" "), CharBuffer.wrap("NULL")).toString());
        Assertions.assertEquals("abc", StringUtils.defaultIfBlank(CharBuffer.wrap("abc"), CharBuffer.wrap("NULL")).toString());
        Assertions.assertNull(StringUtils.defaultIfBlank(CharBuffer.wrap(""), null));
        // Tests compatibility for the API return type
        final CharBuffer s = StringUtils.defaultIfBlank(CharBuffer.wrap("abc"), CharBuffer.wrap("NULL"));
        Assertions.assertEquals("abc", s.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAbbreviate_StringInt() {
        Assertions.assertNull(StringUtils.abbreviate(null, 10));
        Assertions.assertEquals("", StringUtils.abbreviate("", 10));
        Assertions.assertEquals("short", StringUtils.abbreviate("short", 10));
        Assertions.assertEquals("Now is ...", StringUtils.abbreviate("Now is the time for all good men to come to the aid of their party.", 10));
        final String raspberry = "raspberry peach";
        Assertions.assertEquals("raspberry p...", StringUtils.abbreviate(raspberry, 14));
        Assertions.assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", 15));
        Assertions.assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", 16));
        Assertions.assertEquals("abc...", StringUtils.abbreviate("abcdefg", 6));
        Assertions.assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", 7));
        Assertions.assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", 8));
        Assertions.assertEquals("a...", StringUtils.abbreviate("abcdefg", 4));
        Assertions.assertEquals("", StringUtils.abbreviate("", 4));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("abc", 3), "StringUtils.abbreviate expecting IllegalArgumentException");
    }

    @Test
    public void testAbbreviate_StringStringInt() {
        Assertions.assertNull(StringUtils.abbreviate(null, null, 10));
        Assertions.assertNull(StringUtils.abbreviate(null, "...", 10));
        Assertions.assertEquals("paranaguacu", StringUtils.abbreviate("paranaguacu", null, 10));
        Assertions.assertEquals("", StringUtils.abbreviate("", "...", 2));
        Assertions.assertEquals("wai**", StringUtils.abbreviate("waiheke", "**", 5));
        Assertions.assertEquals("And af,,,,", StringUtils.abbreviate("And after a long time, he finally met his son.", ",,,,", 10));
        final String raspberry = "raspberry peach";
        Assertions.assertEquals("raspberry pe..", StringUtils.abbreviate(raspberry, "..", 14));
        Assertions.assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", "---*---", 15));
        Assertions.assertEquals("raspberry peach", StringUtils.abbreviate("raspberry peach", ".", 16));
        Assertions.assertEquals("abc()(", StringUtils.abbreviate("abcdefg", "()(", 6));
        Assertions.assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", ";", 7));
        Assertions.assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", "_-", 8));
        Assertions.assertEquals("abc.", StringUtils.abbreviate("abcdefg", ".", 4));
        Assertions.assertEquals("", StringUtils.abbreviate("", 4));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("abcdefghij", "...", 3), "StringUtils.abbreviate expecting IllegalArgumentException");
    }

    @Test
    public void testAbbreviate_StringIntInt() {
        Assertions.assertNull(StringUtils.abbreviate(null, 10, 12));
        Assertions.assertEquals("", StringUtils.abbreviate("", 0, 10));
        Assertions.assertEquals("", StringUtils.abbreviate("", 2, 10));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("abcdefghij", 0, 3), "StringUtils.abbreviate expecting IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("abcdefghij", 5, 6), "StringUtils.abbreviate expecting IllegalArgumentException");
        final String raspberry = "raspberry peach";
        Assertions.assertEquals("raspberry peach", StringUtils.abbreviate(raspberry, 11, 15));
        Assertions.assertNull(StringUtils.abbreviate(null, 7, 14));
        assertAbbreviateWithOffset("abcdefg...", (-1), 10);
        assertAbbreviateWithOffset("abcdefg...", 0, 10);
        assertAbbreviateWithOffset("abcdefg...", 1, 10);
        assertAbbreviateWithOffset("abcdefg...", 2, 10);
        assertAbbreviateWithOffset("abcdefg...", 3, 10);
        assertAbbreviateWithOffset("abcdefg...", 4, 10);
        assertAbbreviateWithOffset("...fghi...", 5, 10);
        assertAbbreviateWithOffset("...ghij...", 6, 10);
        assertAbbreviateWithOffset("...hijk...", 7, 10);
        assertAbbreviateWithOffset("...ijklmno", 8, 10);
        assertAbbreviateWithOffset("...ijklmno", 9, 10);
        assertAbbreviateWithOffset("...ijklmno", 10, 10);
        assertAbbreviateWithOffset("...ijklmno", 10, 10);
        assertAbbreviateWithOffset("...ijklmno", 11, 10);
        assertAbbreviateWithOffset("...ijklmno", 12, 10);
        assertAbbreviateWithOffset("...ijklmno", 13, 10);
        assertAbbreviateWithOffset("...ijklmno", 14, 10);
        assertAbbreviateWithOffset("...ijklmno", 15, 10);
        assertAbbreviateWithOffset("...ijklmno", 16, 10);
        assertAbbreviateWithOffset("...ijklmno", Integer.MAX_VALUE, 10);
    }

    @Test
    public void testAbbreviate_StringStringIntInt() {
        Assertions.assertNull(StringUtils.abbreviate(null, null, 10, 12));
        Assertions.assertNull(StringUtils.abbreviate(null, "...", 10, 12));
        Assertions.assertEquals("", StringUtils.abbreviate("", null, 0, 10));
        Assertions.assertEquals("", StringUtils.abbreviate("", "...", 2, 10));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("abcdefghij", "::", 0, 2), "StringUtils.abbreviate expecting IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.abbreviate("abcdefghij", "!!!", 5, 6), "StringUtils.abbreviate expecting IllegalArgumentException");
        final String raspberry = "raspberry peach";
        Assertions.assertEquals("raspberry peach", StringUtils.abbreviate(raspberry, "--", 12, 15));
        Assertions.assertNull(StringUtils.abbreviate(null, ";", 7, 14));
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefgh;;", ";;", (-1), 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefghi.", ".", 0, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefgh++", "++", 1, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefghi*", "*", 2, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdef{{{{", "{{{{", 4, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdef____", "____", 5, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("==fghijk==", "==", 5, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("___ghij___", "___", 6, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 7, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 8, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 9, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("///ijklmno", "///", 10, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("//hijklmno", "//", 10, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("//hijklmno", "//", 11, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("...ijklmno", "...", 12, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 13, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 14, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("999ijklmno", "999", 15, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("_ghijklmno", "_", 16, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("+ghijklmno", "+", Integer.MAX_VALUE, 10);
    }

    @Test
    public void testAbbreviateMiddle() {
        // javadoc examples
        Assertions.assertNull(StringUtils.abbreviateMiddle(null, null, 0));
        Assertions.assertEquals("abc", StringUtils.abbreviateMiddle("abc", null, 0));
        Assertions.assertEquals("abc", StringUtils.abbreviateMiddle("abc", ".", 0));
        Assertions.assertEquals("abc", StringUtils.abbreviateMiddle("abc", ".", 3));
        Assertions.assertEquals("ab.f", StringUtils.abbreviateMiddle("abcdef", ".", 4));
        // JIRA issue (LANG-405) example (slightly different than actual expected result)
        Assertions.assertEquals("A very long text with un...f the text is complete.", StringUtils.abbreviateMiddle(("A very long text with unimportant stuff in the middle but interesting start and " + "end to see if the text is complete."), "...", 50));
        // Test a much longer text :)
        final String longText = ("Start text" + (StringUtils.repeat("x", 10000))) + "Close text";
        Assertions.assertEquals("Start text->Close text", StringUtils.abbreviateMiddle(longText, "->", 22));
        // Test negative length
        Assertions.assertEquals("abc", StringUtils.abbreviateMiddle("abc", ".", (-1)));
        // Test boundaries
        // Fails to change anything as method ensures first and last char are kept
        Assertions.assertEquals("abc", StringUtils.abbreviateMiddle("abc", ".", 1));
        Assertions.assertEquals("abc", StringUtils.abbreviateMiddle("abc", ".", 2));
        // Test length of n=1
        Assertions.assertEquals("a", StringUtils.abbreviateMiddle("a", ".", 1));
        // Test smallest length that can lead to success
        Assertions.assertEquals("a.d", StringUtils.abbreviateMiddle("abcd", ".", 3));
        // More from LANG-405
        Assertions.assertEquals("a..f", StringUtils.abbreviateMiddle("abcdef", "..", 4));
        Assertions.assertEquals("ab.ef", StringUtils.abbreviateMiddle("abcdef", ".", 5));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTruncate_StringInt() {
        Assertions.assertNull(StringUtils.truncate(null, 12));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate(null, (-1)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate(null, (-10)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate(null, Integer.MIN_VALUE), "maxWith cannot be negative");
        Assertions.assertEquals("", StringUtils.truncate("", 10));
        Assertions.assertEquals("", StringUtils.truncate("", 10));
        Assertions.assertEquals("abc", StringUtils.truncate("abcdefghij", 3));
        Assertions.assertEquals("abcdef", StringUtils.truncate("abcdefghij", 6));
        Assertions.assertEquals("", StringUtils.truncate("abcdefghij", 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-1)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-100)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", Integer.MIN_VALUE), "maxWith cannot be negative");
        Assertions.assertEquals("abcdefghij", StringUtils.truncate("abcdefghijklmno", 10));
        Assertions.assertEquals("abcdefghijklmno", StringUtils.truncate("abcdefghijklmno", Integer.MAX_VALUE));
        Assertions.assertEquals("abcde", StringUtils.truncate("abcdefghijklmno", 5));
        Assertions.assertEquals("abc", StringUtils.truncate("abcdefghijklmno", 3));
    }

    @Test
    public void testTruncate_StringIntInt() {
        Assertions.assertNull(StringUtils.truncate(null, 0, 12));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate(null, (-1), 0), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate(null, (-10), (-4)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate(null, Integer.MIN_VALUE, Integer.MIN_VALUE), "maxWith cannot be negative");
        Assertions.assertNull(StringUtils.truncate(null, 10, 12));
        Assertions.assertEquals("", StringUtils.truncate("", 0, 10));
        Assertions.assertEquals("", StringUtils.truncate("", 2, 10));
        Assertions.assertEquals("abc", StringUtils.truncate("abcdefghij", 0, 3));
        Assertions.assertEquals("fghij", StringUtils.truncate("abcdefghij", 5, 6));
        Assertions.assertEquals("", StringUtils.truncate("abcdefghij", 0, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", 0, (-1)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", 0, (-10)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", 0, (-100)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", 1, (-100)), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", 0, Integer.MIN_VALUE), "maxWith cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-1), 0), "offset cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-10), 0), "offset cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-100), 1), "offset cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", Integer.MIN_VALUE, 0), "offset cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-1), (-1)), "offset cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-10), (-10)), "offset cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", (-100), (-100)), "offset  cannot be negative");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.truncate("abcdefghij", Integer.MIN_VALUE, Integer.MIN_VALUE), "offset  cannot be negative");
        final String raspberry = "raspberry peach";
        Assertions.assertEquals("peach", StringUtils.truncate(raspberry, 10, 15));
        Assertions.assertEquals("abcdefghij", StringUtils.truncate("abcdefghijklmno", 0, 10));
        Assertions.assertEquals("abcdefghijklmno", StringUtils.truncate("abcdefghijklmno", 0, Integer.MAX_VALUE));
        Assertions.assertEquals("bcdefghijk", StringUtils.truncate("abcdefghijklmno", 1, 10));
        Assertions.assertEquals("cdefghijkl", StringUtils.truncate("abcdefghijklmno", 2, 10));
        Assertions.assertEquals("defghijklm", StringUtils.truncate("abcdefghijklmno", 3, 10));
        Assertions.assertEquals("efghijklmn", StringUtils.truncate("abcdefghijklmno", 4, 10));
        Assertions.assertEquals("fghijklmno", StringUtils.truncate("abcdefghijklmno", 5, 10));
        Assertions.assertEquals("fghij", StringUtils.truncate("abcdefghijklmno", 5, 5));
        Assertions.assertEquals("fgh", StringUtils.truncate("abcdefghijklmno", 5, 3));
        Assertions.assertEquals("klm", StringUtils.truncate("abcdefghijklmno", 10, 3));
        Assertions.assertEquals("klmno", StringUtils.truncate("abcdefghijklmno", 10, Integer.MAX_VALUE));
        Assertions.assertEquals("n", StringUtils.truncate("abcdefghijklmno", 13, 1));
        Assertions.assertEquals("no", StringUtils.truncate("abcdefghijklmno", 13, Integer.MAX_VALUE));
        Assertions.assertEquals("o", StringUtils.truncate("abcdefghijklmno", 14, 1));
        Assertions.assertEquals("o", StringUtils.truncate("abcdefghijklmno", 14, Integer.MAX_VALUE));
        Assertions.assertEquals("", StringUtils.truncate("abcdefghijklmno", 15, 1));
        Assertions.assertEquals("", StringUtils.truncate("abcdefghijklmno", 15, Integer.MAX_VALUE));
        Assertions.assertEquals("", StringUtils.truncate("abcdefghijklmno", Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDifference_StringString() {
        Assertions.assertNull(StringUtils.difference(null, null));
        Assertions.assertEquals("", StringUtils.difference("", ""));
        Assertions.assertEquals("abc", StringUtils.difference("", "abc"));
        Assertions.assertEquals("", StringUtils.difference("abc", ""));
        Assertions.assertEquals("i am a robot", StringUtils.difference(null, "i am a robot"));
        Assertions.assertEquals("i am a machine", StringUtils.difference("i am a machine", null));
        Assertions.assertEquals("robot", StringUtils.difference("i am a machine", "i am a robot"));
        Assertions.assertEquals("", StringUtils.difference("abc", "abc"));
        Assertions.assertEquals("you are a robot", StringUtils.difference("i am a robot", "you are a robot"));
    }

    @Test
    public void testDifferenceAt_StringString() {
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(null, null));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(null, "i am a robot"));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference("", ""));
        Assertions.assertEquals(0, StringUtils.indexOfDifference("", "abc"));
        Assertions.assertEquals(0, StringUtils.indexOfDifference("abc", ""));
        Assertions.assertEquals(0, StringUtils.indexOfDifference("i am a machine", null));
        Assertions.assertEquals(7, StringUtils.indexOfDifference("i am a machine", "i am a robot"));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference("foo", "foo"));
        Assertions.assertEquals(0, StringUtils.indexOfDifference("i am a robot", "you are a robot"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetLevenshteinDistance_StringString() {
        Assertions.assertEquals(0, StringUtils.getLevenshteinDistance("", ""));
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("", "a"));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("aaapppp", ""));
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("frog", "fog"));
        Assertions.assertEquals(3, StringUtils.getLevenshteinDistance("fly", "ant"));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("elephant", "hippo"));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("hippo", "elephant"));
        Assertions.assertEquals(8, StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz"));
        Assertions.assertEquals(8, StringUtils.getLevenshteinDistance("zzzzzzzz", "hippo"));
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("hello", "hallo"));
    }

    @Test
    public void testGetLevenshteinDistance_NullString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getLevenshteinDistance("a", null));
    }

    @Test
    public void testGetLevenshteinDistance_StringNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getLevenshteinDistance(null, "a"));
    }

    @Test
    public void testGetLevenshteinDistance_StringStringInt() {
        // empty strings
        Assertions.assertEquals(0, StringUtils.getLevenshteinDistance("", "", 0));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("aaapppp", "", 8));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("aaapppp", "", 7));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("aaapppp", "", 6));
        // unequal strings, zero threshold
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("b", "a", 0));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("a", "b", 0));
        // equal strings
        Assertions.assertEquals(0, StringUtils.getLevenshteinDistance("aa", "aa", 0));
        Assertions.assertEquals(0, StringUtils.getLevenshteinDistance("aa", "aa", 2));
        // same length
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("aaa", "bbb", 2));
        Assertions.assertEquals(3, StringUtils.getLevenshteinDistance("aaa", "bbb", 3));
        // big stripe
        Assertions.assertEquals(6, StringUtils.getLevenshteinDistance("aaaaaa", "b", 10));
        // distance less than threshold
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("aaapppp", "b", 8));
        Assertions.assertEquals(3, StringUtils.getLevenshteinDistance("a", "bbb", 4));
        // distance equal to threshold
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("aaapppp", "b", 7));
        Assertions.assertEquals(3, StringUtils.getLevenshteinDistance("a", "bbb", 3));
        // distance greater than threshold
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("a", "bbb", 2));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("bbb", "a", 2));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("aaapppp", "b", 6));
        // stripe runs off array, strings not similar
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("a", "bbb", 1));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("bbb", "a", 1));
        // stripe runs off array, strings are similar
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("12345", "1234567", 1));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("1234567", "12345", 1));
        // old getLevenshteinDistance test cases
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("frog", "fog", 1));
        Assertions.assertEquals(3, StringUtils.getLevenshteinDistance("fly", "ant", 3));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("elephant", "hippo", 7));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("elephant", "hippo", 6));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("hippo", "elephant", 7));
        Assertions.assertEquals((-1), StringUtils.getLevenshteinDistance("hippo", "elephant", 6));
        Assertions.assertEquals(8, StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz", 8));
        Assertions.assertEquals(8, StringUtils.getLevenshteinDistance("zzzzzzzz", "hippo", 8));
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("hello", "hallo", 1));
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("frog", "fog", Integer.MAX_VALUE));
        Assertions.assertEquals(3, StringUtils.getLevenshteinDistance("fly", "ant", Integer.MAX_VALUE));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("elephant", "hippo", Integer.MAX_VALUE));
        Assertions.assertEquals(7, StringUtils.getLevenshteinDistance("hippo", "elephant", Integer.MAX_VALUE));
        Assertions.assertEquals(8, StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz", Integer.MAX_VALUE));
        Assertions.assertEquals(8, StringUtils.getLevenshteinDistance("zzzzzzzz", "hippo", Integer.MAX_VALUE));
        Assertions.assertEquals(1, StringUtils.getLevenshteinDistance("hello", "hallo", Integer.MAX_VALUE));
    }

    @Test
    public void testGetLevenshteinDistance_NullStringInt() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getLevenshteinDistance(null, "a", 0));
    }

    @Test
    public void testGetLevenshteinDistance_StringNullInt() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getLevenshteinDistance("a", null, 0));
    }

    @Test
    public void testGetLevenshteinDistance_StringStringNegativeInt() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getLevenshteinDistance("a", "a", (-1)));
    }

    @Test
    public void testGetJaroWinklerDistance_StringString() {
        Assertions.assertEquals(0.93, StringUtils.getJaroWinklerDistance("frog", "fog"));
        Assertions.assertEquals(0.0, StringUtils.getJaroWinklerDistance("fly", "ant"));
        Assertions.assertEquals(0.44, StringUtils.getJaroWinklerDistance("elephant", "hippo"));
        Assertions.assertEquals(0.84, StringUtils.getJaroWinklerDistance("dwayne", "duane"));
        Assertions.assertEquals(0.93, StringUtils.getJaroWinklerDistance("ABC Corporation", "ABC Corp"));
        Assertions.assertEquals(0.95, StringUtils.getJaroWinklerDistance("D N H Enterprises Inc", "D & H Enterprises, Inc."));
        Assertions.assertEquals(0.92, StringUtils.getJaroWinklerDistance("My Gym Children's Fitness Center", "My Gym. Childrens Fitness"));
        Assertions.assertEquals(0.88, StringUtils.getJaroWinklerDistance("PENNSYLVANIA", "PENNCISYLVNIA"));
        Assertions.assertEquals(0.63, StringUtils.getJaroWinklerDistance("Haus Ingeborg", "Ingeborg Esser"));
    }

    @Test
    public void testGetJaroWinklerDistance_NullNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getJaroWinklerDistance(null, null));
    }

    @Test
    public void testGetJaroWinklerDistance_StringNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getJaroWinklerDistance(" ", null));
    }

    @Test
    public void testGetJaroWinklerDistance_NullString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getJaroWinklerDistance(null, "clear"));
    }

    @Test
    public void testGetFuzzyDistance() {
        Assertions.assertEquals(0, StringUtils.getFuzzyDistance("", "", Locale.ENGLISH));
        Assertions.assertEquals(0, StringUtils.getFuzzyDistance("Workshop", "b", Locale.ENGLISH));
        Assertions.assertEquals(1, StringUtils.getFuzzyDistance("Room", "o", Locale.ENGLISH));
        Assertions.assertEquals(1, StringUtils.getFuzzyDistance("Workshop", "w", Locale.ENGLISH));
        Assertions.assertEquals(2, StringUtils.getFuzzyDistance("Workshop", "ws", Locale.ENGLISH));
        Assertions.assertEquals(4, StringUtils.getFuzzyDistance("Workshop", "wo", Locale.ENGLISH));
        Assertions.assertEquals(3, StringUtils.getFuzzyDistance("Apache Software Foundation", "asf", Locale.ENGLISH));
    }

    @Test
    public void testGetFuzzyDistance_NullNullNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getFuzzyDistance(null, null, null));
    }

    @Test
    public void testGetFuzzyDistance_StringNullLoclae() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getFuzzyDistance(" ", null, Locale.ENGLISH));
    }

    @Test
    public void testGetFuzzyDistance_NullStringLocale() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getFuzzyDistance(null, "clear", Locale.ENGLISH));
    }

    @Test
    public void testGetFuzzyDistance_StringStringNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.getFuzzyDistance(" ", "clear", null));
    }

    /**
     * A sanity check for {@link StringUtils#EMPTY}.
     */
    @Test
    public void testEMPTY() {
        Assertions.assertNotNull(StringUtils.EMPTY);
        Assertions.assertEquals("", StringUtils.EMPTY);
        Assertions.assertEquals(0, StringUtils.EMPTY.length());
    }

    /**
     * Test for {@link StringUtils#isAllLowerCase(CharSequence)}.
     */
    @Test
    public void testIsAllLowerCase() {
        Assertions.assertFalse(StringUtils.isAllLowerCase(null));
        Assertions.assertFalse(StringUtils.isAllLowerCase(StringUtils.EMPTY));
        Assertions.assertFalse(StringUtils.isAllLowerCase("  "));
        Assertions.assertTrue(StringUtils.isAllLowerCase("abc"));
        Assertions.assertFalse(StringUtils.isAllLowerCase("abc "));
        Assertions.assertFalse(StringUtils.isAllLowerCase("abc\n"));
        Assertions.assertFalse(StringUtils.isAllLowerCase("abC"));
        Assertions.assertFalse(StringUtils.isAllLowerCase("ab c"));
        Assertions.assertFalse(StringUtils.isAllLowerCase("ab1c"));
        Assertions.assertFalse(StringUtils.isAllLowerCase("ab/c"));
    }

    /**
     * Test for {@link StringUtils#isAllUpperCase(CharSequence)}.
     */
    @Test
    public void testIsAllUpperCase() {
        Assertions.assertFalse(StringUtils.isAllUpperCase(null));
        Assertions.assertFalse(StringUtils.isAllUpperCase(StringUtils.EMPTY));
        Assertions.assertFalse(StringUtils.isAllUpperCase("  "));
        Assertions.assertTrue(StringUtils.isAllUpperCase("ABC"));
        Assertions.assertFalse(StringUtils.isAllUpperCase("ABC "));
        Assertions.assertFalse(StringUtils.isAllUpperCase("ABC\n"));
        Assertions.assertFalse(StringUtils.isAllUpperCase("aBC"));
        Assertions.assertFalse(StringUtils.isAllUpperCase("A C"));
        Assertions.assertFalse(StringUtils.isAllUpperCase("A1C"));
        Assertions.assertFalse(StringUtils.isAllUpperCase("A/C"));
    }

    /**
     * Test for {@link StringUtils#isMixedCase(CharSequence)}.
     */
    @Test
    public void testIsMixedCase() {
        Assertions.assertFalse(StringUtils.isMixedCase(null));
        Assertions.assertFalse(StringUtils.isMixedCase(StringUtils.EMPTY));
        Assertions.assertFalse(StringUtils.isMixedCase(" "));
        Assertions.assertFalse(StringUtils.isMixedCase("A"));
        Assertions.assertFalse(StringUtils.isMixedCase("a"));
        Assertions.assertFalse(StringUtils.isMixedCase("/"));
        Assertions.assertFalse(StringUtils.isMixedCase("A/"));
        Assertions.assertFalse(StringUtils.isMixedCase("/b"));
        Assertions.assertFalse(StringUtils.isMixedCase("abc"));
        Assertions.assertFalse(StringUtils.isMixedCase("ABC"));
        Assertions.assertTrue(StringUtils.isMixedCase("aBc"));
        Assertions.assertTrue(StringUtils.isMixedCase("aBc "));
        Assertions.assertTrue(StringUtils.isMixedCase("A c"));
        Assertions.assertTrue(StringUtils.isMixedCase("aBc\n"));
        Assertions.assertTrue(StringUtils.isMixedCase("A1c"));
        Assertions.assertTrue(StringUtils.isMixedCase("a/C"));
    }

    @Test
    public void testRemoveStart() {
        // StringUtils.removeStart("", *)        = ""
        Assertions.assertNull(StringUtils.removeStart(null, null));
        Assertions.assertNull(StringUtils.removeStart(null, ""));
        Assertions.assertNull(StringUtils.removeStart(null, "a"));
        // StringUtils.removeStart(*, null)      = *
        Assertions.assertEquals(StringUtils.removeStart("", null), "");
        Assertions.assertEquals(StringUtils.removeStart("", ""), "");
        Assertions.assertEquals(StringUtils.removeStart("", "a"), "");
        // All others:
        Assertions.assertEquals(StringUtils.removeStart("www.domain.com", "www."), "domain.com");
        Assertions.assertEquals(StringUtils.removeStart("domain.com", "www."), "domain.com");
        Assertions.assertEquals(StringUtils.removeStart("domain.com", ""), "domain.com");
        Assertions.assertEquals(StringUtils.removeStart("domain.com", null), "domain.com");
    }

    @Test
    public void testRemoveStartIgnoreCase() {
        // StringUtils.removeStart("", *)        = ""
        Assertions.assertNull(StringUtils.removeStartIgnoreCase(null, null), "removeStartIgnoreCase(null, null)");
        Assertions.assertNull(StringUtils.removeStartIgnoreCase(null, ""), "removeStartIgnoreCase(null, \"\")");
        Assertions.assertNull(StringUtils.removeStartIgnoreCase(null, "a"), "removeStartIgnoreCase(null, \"a\")");
        // StringUtils.removeStart(*, null)      = *
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("", null), "", "removeStartIgnoreCase(\"\", null)");
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("", ""), "", "removeStartIgnoreCase(\"\", \"\")");
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("", "a"), "", "removeStartIgnoreCase(\"\", \"a\")");
        // All others:
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("www.domain.com", "www."), "domain.com", "removeStartIgnoreCase(\"www.domain.com\", \"www.\")");
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("domain.com", "www."), "domain.com", "removeStartIgnoreCase(\"domain.com\", \"www.\")");
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("domain.com", ""), "domain.com", "removeStartIgnoreCase(\"domain.com\", \"\")");
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("domain.com", null), "domain.com", "removeStartIgnoreCase(\"domain.com\", null)");
        // Case insensitive:
        Assertions.assertEquals(StringUtils.removeStartIgnoreCase("www.domain.com", "WWW."), "domain.com", "removeStartIgnoreCase(\"www.domain.com\", \"WWW.\")");
    }

    @Test
    public void testRemoveEnd() {
        // StringUtils.removeEnd("", *)        = ""
        Assertions.assertNull(StringUtils.removeEnd(null, null));
        Assertions.assertNull(StringUtils.removeEnd(null, ""));
        Assertions.assertNull(StringUtils.removeEnd(null, "a"));
        // StringUtils.removeEnd(*, null)      = *
        Assertions.assertEquals(StringUtils.removeEnd("", null), "");
        Assertions.assertEquals(StringUtils.removeEnd("", ""), "");
        Assertions.assertEquals(StringUtils.removeEnd("", "a"), "");
        // All others:
        Assertions.assertEquals(StringUtils.removeEnd("www.domain.com.", ".com"), "www.domain.com.");
        Assertions.assertEquals(StringUtils.removeEnd("www.domain.com", ".com"), "www.domain");
        Assertions.assertEquals(StringUtils.removeEnd("www.domain", ".com"), "www.domain");
        Assertions.assertEquals(StringUtils.removeEnd("domain.com", ""), "domain.com");
        Assertions.assertEquals(StringUtils.removeEnd("domain.com", null), "domain.com");
    }

    @Test
    public void testRemoveEndIgnoreCase() {
        // StringUtils.removeEndIgnoreCase("", *)        = ""
        Assertions.assertNull(StringUtils.removeEndIgnoreCase(null, null), "removeEndIgnoreCase(null, null)");
        Assertions.assertNull(StringUtils.removeEndIgnoreCase(null, ""), "removeEndIgnoreCase(null, \"\")");
        Assertions.assertNull(StringUtils.removeEndIgnoreCase(null, "a"), "removeEndIgnoreCase(null, \"a\")");
        // StringUtils.removeEnd(*, null)      = *
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("", null), "", "removeEndIgnoreCase(\"\", null)");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("", ""), "", "removeEndIgnoreCase(\"\", \"\")");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("", "a"), "", "removeEndIgnoreCase(\"\", \"a\")");
        // All others:
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("www.domain.com.", ".com"), "www.domain.com.", "removeEndIgnoreCase(\"www.domain.com.\", \".com\")");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("www.domain.com", ".com"), "www.domain", "removeEndIgnoreCase(\"www.domain.com\", \".com\")");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("www.domain", ".com"), "www.domain", "removeEndIgnoreCase(\"www.domain\", \".com\")");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("domain.com", ""), "domain.com", "removeEndIgnoreCase(\"domain.com\", \"\")");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("domain.com", null), "domain.com", "removeEndIgnoreCase(\"domain.com\", null)");
        // Case insensitive:
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("www.domain.com", ".COM"), "www.domain", "removeEndIgnoreCase(\"www.domain.com\", \".COM\")");
        Assertions.assertEquals(StringUtils.removeEndIgnoreCase("www.domain.COM", ".com"), "www.domain", "removeEndIgnoreCase(\"www.domain.COM\", \".com\")");
    }

    @Test
    public void testRemove_String() {
        // StringUtils.remove(null, *)        = null
        Assertions.assertNull(StringUtils.remove(null, null));
        Assertions.assertNull(StringUtils.remove(null, ""));
        Assertions.assertNull(StringUtils.remove(null, "a"));
        // StringUtils.remove("", *)          = ""
        Assertions.assertEquals("", StringUtils.remove("", null));
        Assertions.assertEquals("", StringUtils.remove("", ""));
        Assertions.assertEquals("", StringUtils.remove("", "a"));
        // StringUtils.remove(*, null)        = *
        Assertions.assertNull(StringUtils.remove(null, null));
        Assertions.assertEquals("", StringUtils.remove("", null));
        Assertions.assertEquals("a", StringUtils.remove("a", null));
        // StringUtils.remove(*, "")          = *
        Assertions.assertNull(StringUtils.remove(null, ""));
        Assertions.assertEquals("", StringUtils.remove("", ""));
        Assertions.assertEquals("a", StringUtils.remove("a", ""));
        // StringUtils.remove("queued", "ue") = "qd"
        Assertions.assertEquals("qd", StringUtils.remove("queued", "ue"));
        // StringUtils.remove("queued", "zz") = "queued"
        Assertions.assertEquals("queued", StringUtils.remove("queued", "zz"));
    }

    @Test
    public void testRemoveIgnoreCase_String() {
        // StringUtils.removeIgnoreCase(null, *) = null
        Assertions.assertNull(StringUtils.removeIgnoreCase(null, null));
        Assertions.assertNull(StringUtils.removeIgnoreCase(null, ""));
        Assertions.assertNull(StringUtils.removeIgnoreCase(null, "a"));
        // StringUtils.removeIgnoreCase("", *) = ""
        Assertions.assertEquals("", StringUtils.removeIgnoreCase("", null));
        Assertions.assertEquals("", StringUtils.removeIgnoreCase("", ""));
        Assertions.assertEquals("", StringUtils.removeIgnoreCase("", "a"));
        // StringUtils.removeIgnoreCase(*, null) = *
        Assertions.assertNull(StringUtils.removeIgnoreCase(null, null));
        Assertions.assertEquals("", StringUtils.removeIgnoreCase("", null));
        Assertions.assertEquals("a", StringUtils.removeIgnoreCase("a", null));
        // StringUtils.removeIgnoreCase(*, "") = *
        Assertions.assertNull(StringUtils.removeIgnoreCase(null, ""));
        Assertions.assertEquals("", StringUtils.removeIgnoreCase("", ""));
        Assertions.assertEquals("a", StringUtils.removeIgnoreCase("a", ""));
        // StringUtils.removeIgnoreCase("queued", "ue") = "qd"
        Assertions.assertEquals("qd", StringUtils.removeIgnoreCase("queued", "ue"));
        // StringUtils.removeIgnoreCase("queued", "zz") = "queued"
        Assertions.assertEquals("queued", StringUtils.removeIgnoreCase("queued", "zz"));
        // IgnoreCase
        // StringUtils.removeIgnoreCase("quEUed", "UE") = "qd"
        Assertions.assertEquals("qd", StringUtils.removeIgnoreCase("quEUed", "UE"));
        // StringUtils.removeIgnoreCase("queued", "zZ") = "queued"
        Assertions.assertEquals("queued", StringUtils.removeIgnoreCase("queued", "zZ"));
    }

    @Test
    public void testRemove_char() {
        // StringUtils.remove(null, *)       = null
        Assertions.assertNull(StringUtils.remove(null, 'a'));
        Assertions.assertNull(StringUtils.remove(null, 'a'));
        Assertions.assertNull(StringUtils.remove(null, 'a'));
        // StringUtils.remove("", *)          = ""
        Assertions.assertEquals("", StringUtils.remove("", 'a'));
        Assertions.assertEquals("", StringUtils.remove("", 'a'));
        Assertions.assertEquals("", StringUtils.remove("", 'a'));
        // StringUtils.remove("queued", 'u') = "qeed"
        Assertions.assertEquals("qeed", StringUtils.remove("queued", 'u'));
        // StringUtils.remove("queued", 'z') = "queued"
        Assertions.assertEquals("queued", StringUtils.remove("queued", 'z'));
    }

    @Test
    public void testRemoveAll_StringString() {
        Assertions.assertNull(StringUtils.removeAll(null, ""));
        Assertions.assertEquals("any", StringUtils.removeAll("any", null));
        Assertions.assertEquals("any", StringUtils.removeAll("any", ""));
        Assertions.assertEquals("", StringUtils.removeAll("any", ".*"));
        Assertions.assertEquals("", StringUtils.removeAll("any", ".+"));
        Assertions.assertEquals("", StringUtils.removeAll("any", ".?"));
        Assertions.assertEquals("A\nB", StringUtils.removeAll("A<__>\n<__>B", "<.*>"));
        Assertions.assertEquals("AB", StringUtils.removeAll("A<__>\n<__>B", "(?s)<.*>"));
        Assertions.assertEquals("ABC123", StringUtils.removeAll("ABCabc123abc", "[a-z]"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> StringUtils.removeAll("any", "{badRegexSyntax}"), "StringUtils.removeAll expecting PatternSyntaxException");
    }

    @Test
    public void testRemoveFirst_StringString() {
        Assertions.assertNull(StringUtils.removeFirst(null, ""));
        Assertions.assertEquals("any", StringUtils.removeFirst("any", null));
        Assertions.assertEquals("any", StringUtils.removeFirst("any", ""));
        Assertions.assertEquals("", StringUtils.removeFirst("any", ".*"));
        Assertions.assertEquals("", StringUtils.removeFirst("any", ".+"));
        Assertions.assertEquals("bc", StringUtils.removeFirst("abc", ".?"));
        Assertions.assertEquals("A\n<__>B", StringUtils.removeFirst("A<__>\n<__>B", "<.*>"));
        Assertions.assertEquals("AB", StringUtils.removeFirst("A<__>\n<__>B", "(?s)<.*>"));
        Assertions.assertEquals("ABCbc123", StringUtils.removeFirst("ABCabc123", "[a-z]"));
        Assertions.assertEquals("ABC123abc", StringUtils.removeFirst("ABCabc123abc", "[a-z]+"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> StringUtils.removeFirst("any", "{badRegexSyntax}"), "StringUtils.removeFirst expecting PatternSyntaxException");
    }

    @Test
    public void testDifferenceAt_StringArray() {
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(((String[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(new String[]{  }));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(new String[]{ "abc" }));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(new String[]{ null, null }));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(new String[]{ "", "" }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ "", null }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ "abc", null, null }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ null, null, "abc" }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ "", "abc" }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ "abc", "" }));
        Assertions.assertEquals((-1), StringUtils.indexOfDifference(new String[]{ "abc", "abc" }));
        Assertions.assertEquals(1, StringUtils.indexOfDifference(new String[]{ "abc", "a" }));
        Assertions.assertEquals(2, StringUtils.indexOfDifference(new String[]{ "ab", "abxyz" }));
        Assertions.assertEquals(2, StringUtils.indexOfDifference(new String[]{ "abcde", "abxyz" }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ "abcde", "xyz" }));
        Assertions.assertEquals(0, StringUtils.indexOfDifference(new String[]{ "xyz", "abcde" }));
        Assertions.assertEquals(7, StringUtils.indexOfDifference(new String[]{ "i am a machine", "i am a robot" }));
    }

    @Test
    public void testGetCommonPrefix_StringArray() {
        Assertions.assertEquals("", StringUtils.getCommonPrefix(((String[]) (null))));
        Assertions.assertEquals("", StringUtils.getCommonPrefix());
        Assertions.assertEquals("abc", StringUtils.getCommonPrefix("abc"));
        Assertions.assertEquals("", StringUtils.getCommonPrefix(null, null));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("", ""));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("", null));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("abc", null, null));
        Assertions.assertEquals("", StringUtils.getCommonPrefix(null, null, "abc"));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("", "abc"));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("abc", ""));
        Assertions.assertEquals("abc", StringUtils.getCommonPrefix("abc", "abc"));
        Assertions.assertEquals("a", StringUtils.getCommonPrefix("abc", "a"));
        Assertions.assertEquals("ab", StringUtils.getCommonPrefix("ab", "abxyz"));
        Assertions.assertEquals("ab", StringUtils.getCommonPrefix("abcde", "abxyz"));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("abcde", "xyz"));
        Assertions.assertEquals("", StringUtils.getCommonPrefix("xyz", "abcde"));
        Assertions.assertEquals("i am a ", StringUtils.getCommonPrefix("i am a machine", "i am a robot"));
    }

    @Test
    public void testNormalizeSpace() {
        // Java says a non-breaking whitespace is not a whitespace.
        Assertions.assertFalse(Character.isWhitespace('\u00a0'));
        // 
        Assertions.assertNull(StringUtils.normalizeSpace(null));
        Assertions.assertEquals("", StringUtils.normalizeSpace(""));
        Assertions.assertEquals("", StringUtils.normalizeSpace(" "));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\t"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\n"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\t"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\u000b"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\f"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\u001c"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\u001d"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\u001e"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\u001f"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\f"));
        Assertions.assertEquals("", StringUtils.normalizeSpace("\r"));
        Assertions.assertEquals("a", StringUtils.normalizeSpace("  a  "));
        Assertions.assertEquals("a b c", StringUtils.normalizeSpace("  a  b   c  "));
        Assertions.assertEquals("a b c", StringUtils.normalizeSpace("a\t\f\r  b\u000b   c\n"));
        Assertions.assertEquals("a   b c", StringUtils.normalizeSpace(((("a\t\f\r  " + (StringUtilsTest.HARD_SPACE)) + (StringUtilsTest.HARD_SPACE)) + "b\u000b   c\n")));
        Assertions.assertEquals("b", StringUtils.normalizeSpace("\u0000b"));
        Assertions.assertEquals("b", StringUtils.normalizeSpace("b\u0000"));
    }

    @Test
    public void testLANG666() {
        Assertions.assertEquals("12", StringUtils.stripEnd("120.00", ".0"));
        Assertions.assertEquals("121", StringUtils.stripEnd("121.00", ".0"));
    }

    // Methods on StringUtils that are immutable in spirit (i.e. calculate the length)
    // should take a CharSequence parameter. Methods that are mutable in spirit (i.e. capitalize)
    // should take a String or String[] parameter and return String or String[].
    // This test enforces that this is done.
    @Test
    public void testStringUtilsCharSequenceContract() {
        final Class<StringUtils> c = StringUtils.class;
        // Methods that are expressly excluded from testStringUtilsCharSequenceContract()
        final String[] excludeMethods = new String[]{ "public static int org.apache.commons.lang3.StringUtils.compare(java.lang.String,java.lang.String)", "public static int org.apache.commons.lang3.StringUtils.compare(java.lang.String,java.lang.String,boolean)", "public static int org.apache.commons.lang3.StringUtils.compareIgnoreCase(java.lang.String,java.lang.String)", "public static int org.apache.commons.lang3.StringUtils.compareIgnoreCase(java.lang.String,java.lang.String,boolean)" };
        final Method[] methods = c.getMethods();
        for (final Method m : methods) {
            final String methodStr = m.toString();
            if (((m.getReturnType()) == (String.class)) || ((m.getReturnType()) == (String[].class))) {
                // Assume this is mutable and ensure the first parameter is not CharSequence.
                // It may be String or it may be something else (String[], Object, Object[]) so
                // don't actively test for that.
                final Class<?>[] params = m.getParameterTypes();
                if (((params.length) > 0) && (((params[0]) == (CharSequence.class)) || ((params[0]) == (CharSequence[].class)))) {
                    Assertions.assertTrue((!(ArrayUtils.contains(excludeMethods, methodStr))), (("The method \"" + methodStr) + "\" appears to be mutable in spirit and therefore must not accept a CharSequence"));
                }
            } else {
                // Assume this is immutable in spirit and ensure the first parameter is not String.
                // As above, it may be something other than CharSequence.
                final Class<?>[] params = m.getParameterTypes();
                if (((params.length) > 0) && (((params[0]) == (String.class)) || ((params[0]) == (String[].class)))) {
                    Assertions.assertTrue(ArrayUtils.contains(excludeMethods, methodStr), (("The method \"" + methodStr) + "\" appears to be immutable in spirit and therefore must not accept a String"));
                }
            }
        }
    }

    /**
     * Tests {@link StringUtils#toString(byte[], String)}
     *
     * @throws java.io.UnsupportedEncodingException
     * 		because the method under test max throw it
     * @see StringUtils#toString(byte[], String)
     */
    @Test
    public void testToString() throws UnsupportedEncodingException {
        final String expectedString = "The quick brown fox jumps over the lazy dog.";
        byte[] expectedBytes = expectedString.getBytes(Charset.defaultCharset());
        // sanity check start
        Assertions.assertArrayEquals(expectedBytes, expectedString.getBytes());
        // sanity check end
        Assertions.assertEquals(expectedString, StringUtils.toString(expectedBytes, null));
        Assertions.assertEquals(expectedString, StringUtils.toString(expectedBytes, SystemUtils.FILE_ENCODING));
        final String encoding = "UTF-16";
        expectedBytes = expectedString.getBytes(Charset.forName(encoding));
        Assertions.assertEquals(expectedString, StringUtils.toString(expectedBytes, encoding));
    }

    @Test
    public void testEscapeSurrogatePairs() {
        Assertions.assertEquals("\ud83d\ude30", StringEscapeUtils.escapeCsv("\ud83d\ude30"));
        // Examples from https://en.wikipedia.org/wiki/UTF-16
        Assertions.assertEquals("\ud800\udc00", StringEscapeUtils.escapeCsv("\ud800\udc00"));
        Assertions.assertEquals("\ud834\udd1e", StringEscapeUtils.escapeCsv("\ud834\udd1e"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.escapeCsv("\udbff\udffd"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.escapeHtml3("\udbff\udffd"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.escapeHtml4("\udbff\udffd"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.escapeXml("\udbff\udffd"));
    }

    /**
     * Tests LANG-858.
     */
    @Test
    public void testEscapeSurrogatePairsLang858() {
        Assertions.assertEquals("\\uDBFF\\uDFFD", StringEscapeUtils.escapeJava("\udbff\udffd"));// fail LANG-858

        Assertions.assertEquals("\\uDBFF\\uDFFD", StringEscapeUtils.escapeEcmaScript("\udbff\udffd"));// fail LANG-858

    }

    @Test
    public void testUnescapeSurrogatePairs() {
        Assertions.assertEquals("\ud83d\ude30", StringEscapeUtils.unescapeCsv("\ud83d\ude30"));
        // Examples from https://en.wikipedia.org/wiki/UTF-16
        Assertions.assertEquals("\ud800\udc00", StringEscapeUtils.unescapeCsv("\ud800\udc00"));
        Assertions.assertEquals("\ud834\udd1e", StringEscapeUtils.unescapeCsv("\ud834\udd1e"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.unescapeCsv("\udbff\udffd"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.unescapeHtml3("\udbff\udffd"));
        Assertions.assertEquals("\udbff\udffd", StringEscapeUtils.unescapeHtml4("\udbff\udffd"));
    }

    /**
     * Tests {@code appendIfMissing}.
     */
    @Test
    public void testAppendIfMissing() {
        Assertions.assertNull(StringUtils.appendIfMissing(null, null), "appendIfMissing(null,null)");
        Assertions.assertEquals("abc", StringUtils.appendIfMissing("abc", null), "appendIfMissing(abc,null)");
        Assertions.assertEquals("xyz", StringUtils.appendIfMissing("", "xyz"), "appendIfMissing(\"\",xyz)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissing("abc", "xyz"), "appendIfMissing(abc,xyz)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissing("abcxyz", "xyz"), "appendIfMissing(abcxyz,xyz)");
        Assertions.assertEquals("aXYZxyz", StringUtils.appendIfMissing("aXYZ", "xyz"), "appendIfMissing(aXYZ,xyz)");
        Assertions.assertNull(StringUtils.appendIfMissing(null, null, ((CharSequence[]) (null))), "appendIfMissing(null,null,null)");
        Assertions.assertEquals("abc", StringUtils.appendIfMissing("abc", null, ((CharSequence[]) (null))), "appendIfMissing(abc,null,null)");
        Assertions.assertEquals("xyz", StringUtils.appendIfMissing("", "xyz", ((CharSequence[]) (null))), "appendIfMissing(\"\",xyz,null))");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissing("abc", "xyz", new CharSequence[]{ null }), "appendIfMissing(abc,xyz,{null})");
        Assertions.assertEquals("abc", StringUtils.appendIfMissing("abc", "xyz", ""), "appendIfMissing(abc,xyz,\"\")");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissing("abc", "xyz", "mno"), "appendIfMissing(abc,xyz,mno)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissing("abcxyz", "xyz", "mno"), "appendIfMissing(abcxyz,xyz,mno)");
        Assertions.assertEquals("abcmno", StringUtils.appendIfMissing("abcmno", "xyz", "mno"), "appendIfMissing(abcmno,xyz,mno)");
        Assertions.assertEquals("abcXYZxyz", StringUtils.appendIfMissing("abcXYZ", "xyz", "mno"), "appendIfMissing(abcXYZ,xyz,mno)");
        Assertions.assertEquals("abcMNOxyz", StringUtils.appendIfMissing("abcMNO", "xyz", "mno"), "appendIfMissing(abcMNO,xyz,mno)");
    }

    /**
     * Tests {@code appendIfMissingIgnoreCase}.
     */
    @Test
    public void testAppendIfMissingIgnoreCase() {
        Assertions.assertNull(StringUtils.appendIfMissingIgnoreCase(null, null), "appendIfMissingIgnoreCase(null,null)");
        Assertions.assertEquals("abc", StringUtils.appendIfMissingIgnoreCase("abc", null), "appendIfMissingIgnoreCase(abc,null)");
        Assertions.assertEquals("xyz", StringUtils.appendIfMissingIgnoreCase("", "xyz"), "appendIfMissingIgnoreCase(\"\",xyz)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissingIgnoreCase("abc", "xyz"), "appendIfMissingIgnoreCase(abc,xyz)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissingIgnoreCase("abcxyz", "xyz"), "appendIfMissingIgnoreCase(abcxyz,xyz)");
        Assertions.assertEquals("abcXYZ", StringUtils.appendIfMissingIgnoreCase("abcXYZ", "xyz"), "appendIfMissingIgnoreCase(abcXYZ,xyz)");
        Assertions.assertNull(StringUtils.appendIfMissingIgnoreCase(null, null, ((CharSequence[]) (null))), "appendIfMissingIgnoreCase(null,null,null)");
        Assertions.assertEquals("abc", StringUtils.appendIfMissingIgnoreCase("abc", null, ((CharSequence[]) (null))), "appendIfMissingIgnoreCase(abc,null,null)");
        Assertions.assertEquals("xyz", StringUtils.appendIfMissingIgnoreCase("", "xyz", ((CharSequence[]) (null))), "appendIfMissingIgnoreCase(\"\",xyz,null)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissingIgnoreCase("abc", "xyz", new CharSequence[]{ null }), "appendIfMissingIgnoreCase(abc,xyz,{null})");
        Assertions.assertEquals("abc", StringUtils.appendIfMissingIgnoreCase("abc", "xyz", ""), "appendIfMissingIgnoreCase(abc,xyz,\"\")");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissingIgnoreCase("abc", "xyz", "mno"), "appendIfMissingIgnoreCase(abc,xyz,mno)");
        Assertions.assertEquals("abcxyz", StringUtils.appendIfMissingIgnoreCase("abcxyz", "xyz", "mno"), "appendIfMissingIgnoreCase(abcxyz,xyz,mno)");
        Assertions.assertEquals("abcmno", StringUtils.appendIfMissingIgnoreCase("abcmno", "xyz", "mno"), "appendIfMissingIgnoreCase(abcmno,xyz,mno)");
        Assertions.assertEquals("abcXYZ", StringUtils.appendIfMissingIgnoreCase("abcXYZ", "xyz", "mno"), "appendIfMissingIgnoreCase(abcXYZ,xyz,mno)");
        Assertions.assertEquals("abcMNO", StringUtils.appendIfMissingIgnoreCase("abcMNO", "xyz", "mno"), "appendIfMissingIgnoreCase(abcMNO,xyz,mno)");
    }

    /**
     * Tests {@code prependIfMissing}.
     */
    @Test
    public void testPrependIfMissing() {
        Assertions.assertNull(StringUtils.prependIfMissing(null, null), "prependIfMissing(null,null)");
        Assertions.assertEquals("abc", StringUtils.prependIfMissing("abc", null), "prependIfMissing(abc,null)");
        Assertions.assertEquals("xyz", StringUtils.prependIfMissing("", "xyz"), "prependIfMissing(\"\",xyz)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissing("abc", "xyz"), "prependIfMissing(abc,xyz)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissing("xyzabc", "xyz"), "prependIfMissing(xyzabc,xyz)");
        Assertions.assertEquals("xyzXYZabc", StringUtils.prependIfMissing("XYZabc", "xyz"), "prependIfMissing(XYZabc,xyz)");
        Assertions.assertNull(StringUtils.prependIfMissing(null, null, ((CharSequence[]) (null))), "prependIfMissing(null,null null)");
        Assertions.assertEquals("abc", StringUtils.prependIfMissing("abc", null, ((CharSequence[]) (null))), "prependIfMissing(abc,null,null)");
        Assertions.assertEquals("xyz", StringUtils.prependIfMissing("", "xyz", ((CharSequence[]) (null))), "prependIfMissing(\"\",xyz,null)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissing("abc", "xyz", new CharSequence[]{ null }), "prependIfMissing(abc,xyz,{null})");
        Assertions.assertEquals("abc", StringUtils.prependIfMissing("abc", "xyz", ""), "prependIfMissing(abc,xyz,\"\")");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissing("abc", "xyz", "mno"), "prependIfMissing(abc,xyz,mno)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissing("xyzabc", "xyz", "mno"), "prependIfMissing(xyzabc,xyz,mno)");
        Assertions.assertEquals("mnoabc", StringUtils.prependIfMissing("mnoabc", "xyz", "mno"), "prependIfMissing(mnoabc,xyz,mno)");
        Assertions.assertEquals("xyzXYZabc", StringUtils.prependIfMissing("XYZabc", "xyz", "mno"), "prependIfMissing(XYZabc,xyz,mno)");
        Assertions.assertEquals("xyzMNOabc", StringUtils.prependIfMissing("MNOabc", "xyz", "mno"), "prependIfMissing(MNOabc,xyz,mno)");
    }

    /**
     * Tests {@code prependIfMissingIgnoreCase}.
     */
    @Test
    public void testPrependIfMissingIgnoreCase() {
        Assertions.assertNull(StringUtils.prependIfMissingIgnoreCase(null, null), "prependIfMissingIgnoreCase(null,null)");
        Assertions.assertEquals("abc", StringUtils.prependIfMissingIgnoreCase("abc", null), "prependIfMissingIgnoreCase(abc,null)");
        Assertions.assertEquals("xyz", StringUtils.prependIfMissingIgnoreCase("", "xyz"), "prependIfMissingIgnoreCase(\"\",xyz)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissingIgnoreCase("abc", "xyz"), "prependIfMissingIgnoreCase(abc,xyz)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissingIgnoreCase("xyzabc", "xyz"), "prependIfMissingIgnoreCase(xyzabc,xyz)");
        Assertions.assertEquals("XYZabc", StringUtils.prependIfMissingIgnoreCase("XYZabc", "xyz"), "prependIfMissingIgnoreCase(XYZabc,xyz)");
        Assertions.assertNull(StringUtils.prependIfMissingIgnoreCase(null, null, ((CharSequence[]) (null))), "prependIfMissingIgnoreCase(null,null null)");
        Assertions.assertEquals("abc", StringUtils.prependIfMissingIgnoreCase("abc", null, ((CharSequence[]) (null))), "prependIfMissingIgnoreCase(abc,null,null)");
        Assertions.assertEquals("xyz", StringUtils.prependIfMissingIgnoreCase("", "xyz", ((CharSequence[]) (null))), "prependIfMissingIgnoreCase(\"\",xyz,null)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissingIgnoreCase("abc", "xyz", new CharSequence[]{ null }), "prependIfMissingIgnoreCase(abc,xyz,{null})");
        Assertions.assertEquals("abc", StringUtils.prependIfMissingIgnoreCase("abc", "xyz", ""), "prependIfMissingIgnoreCase(abc,xyz,\"\")");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissingIgnoreCase("abc", "xyz", "mno"), "prependIfMissingIgnoreCase(abc,xyz,mno)");
        Assertions.assertEquals("xyzabc", StringUtils.prependIfMissingIgnoreCase("xyzabc", "xyz", "mno"), "prependIfMissingIgnoreCase(xyzabc,xyz,mno)");
        Assertions.assertEquals("mnoabc", StringUtils.prependIfMissingIgnoreCase("mnoabc", "xyz", "mno"), "prependIfMissingIgnoreCase(mnoabc,xyz,mno)");
        Assertions.assertEquals("XYZabc", StringUtils.prependIfMissingIgnoreCase("XYZabc", "xyz", "mno"), "prependIfMissingIgnoreCase(XYZabc,xyz,mno)");
        Assertions.assertEquals("MNOabc", StringUtils.prependIfMissingIgnoreCase("MNOabc", "xyz", "mno"), "prependIfMissingIgnoreCase(MNOabc,xyz,mno)");
    }

    /**
     * Tests {@link StringUtils#toEncodedString(byte[], Charset)}
     *
     * @see StringUtils#toEncodedString(byte[], Charset)
     */
    @Test
    public void testToEncodedString() {
        final String expectedString = "The quick brown fox jumps over the lazy dog.";
        String encoding = SystemUtils.FILE_ENCODING;
        byte[] expectedBytes = expectedString.getBytes(Charset.defaultCharset());
        // sanity check start
        Assertions.assertArrayEquals(expectedBytes, expectedString.getBytes());
        // sanity check end
        Assertions.assertEquals(expectedString, StringUtils.toEncodedString(expectedBytes, Charset.defaultCharset()));
        Assertions.assertEquals(expectedString, StringUtils.toEncodedString(expectedBytes, Charset.forName(encoding)));
        encoding = "UTF-16";
        expectedBytes = expectedString.getBytes(Charset.forName(encoding));
        Assertions.assertEquals(expectedString, StringUtils.toEncodedString(expectedBytes, Charset.forName(encoding)));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testWrap_StringChar() {
        Assertions.assertNull(StringUtils.wrap(null, CharUtils.NUL));
        Assertions.assertNull(StringUtils.wrap(null, '1'));
        Assertions.assertEquals("", StringUtils.wrap("", CharUtils.NUL));
        Assertions.assertEquals("xabx", StringUtils.wrap("ab", 'x'));
        Assertions.assertEquals("\"ab\"", StringUtils.wrap("ab", '\"'));
        Assertions.assertEquals("\"\"ab\"\"", StringUtils.wrap("\"ab\"", '\"'));
        Assertions.assertEquals("'ab'", StringUtils.wrap("ab", '\''));
        Assertions.assertEquals("''abcd''", StringUtils.wrap("'abcd'", '\''));
        Assertions.assertEquals("\'\"abcd\"\'", StringUtils.wrap("\"abcd\"", '\''));
        Assertions.assertEquals("\"\'abcd\'\"", StringUtils.wrap("'abcd'", '\"'));
    }

    @Test
    public void testWrapIfMissing_StringChar() {
        Assertions.assertNull(StringUtils.wrapIfMissing(null, CharUtils.NUL));
        Assertions.assertNull(StringUtils.wrapIfMissing(null, '1'));
        Assertions.assertEquals("", StringUtils.wrapIfMissing("", CharUtils.NUL));
        Assertions.assertEquals("xabx", StringUtils.wrapIfMissing("ab", 'x'));
        Assertions.assertEquals("\"ab\"", StringUtils.wrapIfMissing("ab", '\"'));
        Assertions.assertEquals("\"ab\"", StringUtils.wrapIfMissing("\"ab\"", '\"'));
        Assertions.assertEquals("'ab'", StringUtils.wrapIfMissing("ab", '\''));
        Assertions.assertEquals("'abcd'", StringUtils.wrapIfMissing("'abcd'", '\''));
        Assertions.assertEquals("\'\"abcd\"\'", StringUtils.wrapIfMissing("\"abcd\"", '\''));
        Assertions.assertEquals("\"\'abcd\'\"", StringUtils.wrapIfMissing("'abcd'", '\"'));
        Assertions.assertEquals("/x/", StringUtils.wrapIfMissing("x", '/'));
        Assertions.assertEquals("/x/y/z/", StringUtils.wrapIfMissing("x/y/z", '/'));
        Assertions.assertEquals("/x/y/z/", StringUtils.wrapIfMissing("/x/y/z", '/'));
        Assertions.assertEquals("/x/y/z/", StringUtils.wrapIfMissing("x/y/z/", '/'));
        Assertions.assertEquals("/", StringUtils.wrapIfMissing("/", '/'));
    }

    @Test
    public void testWrapIfMissing_StringString() {
        Assertions.assertNull(StringUtils.wrapIfMissing(null, "\u0000"));
        Assertions.assertNull(StringUtils.wrapIfMissing(null, "1"));
        Assertions.assertEquals("", StringUtils.wrapIfMissing("", "\u0000"));
        Assertions.assertEquals("xabx", StringUtils.wrapIfMissing("ab", "x"));
        Assertions.assertEquals("\"ab\"", StringUtils.wrapIfMissing("ab", "\""));
        Assertions.assertEquals("\"ab\"", StringUtils.wrapIfMissing("\"ab\"", "\""));
        Assertions.assertEquals("'ab'", StringUtils.wrapIfMissing("ab", "\'"));
        Assertions.assertEquals("'abcd'", StringUtils.wrapIfMissing("'abcd'", "\'"));
        Assertions.assertEquals("\'\"abcd\"\'", StringUtils.wrapIfMissing("\"abcd\"", "\'"));
        Assertions.assertEquals("\"\'abcd\'\"", StringUtils.wrapIfMissing("'abcd'", "\""));
        Assertions.assertEquals("/x/", StringUtils.wrapIfMissing("x", "/"));
        Assertions.assertEquals("/x/y/z/", StringUtils.wrapIfMissing("x/y/z", "/"));
        Assertions.assertEquals("/x/y/z/", StringUtils.wrapIfMissing("/x/y/z", "/"));
        Assertions.assertEquals("/x/y/z/", StringUtils.wrapIfMissing("x/y/z/", "/"));
        Assertions.assertEquals("/", StringUtils.wrapIfMissing("/", "/"));
        Assertions.assertEquals("ab/ab", StringUtils.wrapIfMissing("/", "ab"));
        Assertions.assertEquals("ab/ab", StringUtils.wrapIfMissing("ab/ab", "ab"));
    }

    @Test
    public void testWrap_StringString() {
        Assertions.assertNull(StringUtils.wrap(null, null));
        Assertions.assertNull(StringUtils.wrap(null, ""));
        Assertions.assertNull(StringUtils.wrap(null, "1"));
        Assertions.assertNull(StringUtils.wrap(null, null));
        Assertions.assertEquals("", StringUtils.wrap("", ""));
        Assertions.assertEquals("ab", StringUtils.wrap("ab", null));
        Assertions.assertEquals("xabx", StringUtils.wrap("ab", "x"));
        Assertions.assertEquals("\"ab\"", StringUtils.wrap("ab", "\""));
        Assertions.assertEquals("\"\"ab\"\"", StringUtils.wrap("\"ab\"", "\""));
        Assertions.assertEquals("'ab'", StringUtils.wrap("ab", "'"));
        Assertions.assertEquals("''abcd''", StringUtils.wrap("'abcd'", "'"));
        Assertions.assertEquals("\'\"abcd\"\'", StringUtils.wrap("\"abcd\"", "'"));
        Assertions.assertEquals("\"\'abcd\'\"", StringUtils.wrap("'abcd'", "\""));
    }

    @Test
    public void testUnwrap_StringString() {
        Assertions.assertNull(StringUtils.unwrap(null, null));
        Assertions.assertNull(StringUtils.unwrap(null, ""));
        Assertions.assertNull(StringUtils.unwrap(null, "1"));
        Assertions.assertEquals("abc", StringUtils.unwrap("abc", null));
        Assertions.assertEquals("abc", StringUtils.unwrap("abc", ""));
        Assertions.assertEquals("abc", StringUtils.unwrap("\'abc\'", "\'"));
        Assertions.assertEquals("abc", StringUtils.unwrap("\"abc\"", "\""));
        Assertions.assertEquals("abc\"xyz", StringUtils.unwrap("\"abc\"xyz\"", "\""));
        Assertions.assertEquals("abc\"xyz\"", StringUtils.unwrap("\"abc\"xyz\"\"", "\""));
        Assertions.assertEquals("abc\'xyz\'", StringUtils.unwrap("\"abc\'xyz\'\"", "\""));
        Assertions.assertEquals("\"abc\'xyz\'\"", StringUtils.unwrap("AA\"abc\'xyz\'\"AA", "AA"));
        Assertions.assertEquals("\"abc\'xyz\'\"", StringUtils.unwrap("123\"abc\'xyz\'\"123", "123"));
        Assertions.assertEquals("AA\"abc\'xyz\'\"", StringUtils.unwrap("AA\"abc\'xyz\'\"", "AA"));
        Assertions.assertEquals("AA\"abc\'xyz\'\"AA", StringUtils.unwrap("AAA\"abc\'xyz\'\"AAA", "A"));
        Assertions.assertEquals("\"abc\'xyz\'\"AA", StringUtils.unwrap("\"abc\'xyz\'\"AA", "AA"));
    }

    @Test
    public void testUnwrap_StringChar() {
        Assertions.assertNull(StringUtils.unwrap(null, null));
        Assertions.assertNull(StringUtils.unwrap(null, CharUtils.NUL));
        Assertions.assertNull(StringUtils.unwrap(null, '1'));
        Assertions.assertEquals("abc", StringUtils.unwrap("abc", null));
        Assertions.assertEquals("abc", StringUtils.unwrap("\'abc\'", '\''));
        Assertions.assertEquals("abc", StringUtils.unwrap("AabcA", 'A'));
        Assertions.assertEquals("AabcA", StringUtils.unwrap("AAabcAA", 'A'));
        Assertions.assertEquals("abc", StringUtils.unwrap("abc", 'b'));
        Assertions.assertEquals("#A", StringUtils.unwrap("#A", '#'));
        Assertions.assertEquals("A#", StringUtils.unwrap("A#", '#'));
        Assertions.assertEquals("ABA", StringUtils.unwrap("AABAA", 'A'));
    }

    @Test
    public void testToCodePoints() {
        final int orphanedHighSurrogate = 55297;
        final int orphanedLowSurrogate = 56320;
        final int supplementary = 132878;
        final int[] codePoints = new int[]{ 'a', orphanedHighSurrogate, 'b', 'c', supplementary, 'd', orphanedLowSurrogate, 'e' };
        final String s = new String(codePoints, 0, codePoints.length);
        Assertions.assertArrayEquals(codePoints, StringUtils.toCodePoints(s));
        Assertions.assertNull(StringUtils.toCodePoints(null));
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, StringUtils.toCodePoints(""));
    }

    @Test
    public void testGetDigits() {
        Assertions.assertNull(StringUtils.getDigits(null));
        Assertions.assertEquals("", StringUtils.getDigits(""));
        Assertions.assertEquals("", StringUtils.getDigits("abc"));
        Assertions.assertEquals("1000", StringUtils.getDigits("1000$"));
        Assertions.assertEquals("12345", StringUtils.getDigits("123password45"));
        Assertions.assertEquals("5417543010", StringUtils.getDigits("(541) 754-3010"));
        Assertions.assertEquals("\u0967\u0968\u0969", StringUtils.getDigits("\u0967\u0968\u0969"));
    }
}

