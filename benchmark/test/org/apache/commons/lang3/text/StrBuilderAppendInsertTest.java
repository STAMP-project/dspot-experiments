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
package org.apache.commons.lang3.text;


import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests for {@link org.apache.commons.lang3.text.StrBuilder}.
 */
@Deprecated
public class StrBuilderAppendInsertTest {
    /**
     * The system line separator.
     */
    private static final String SEP = System.lineSeparator();

    /**
     * Test subclass of Object, with a toString method.
     */
    private static final Object FOO = new Object() {
        @Override
        public String toString() {
            return "foo";
        }
    };

    // -----------------------------------------------------------------------
    @Test
    public void testAppendNewLine() {
        StrBuilder sb = new StrBuilder("---");
        sb.appendNewLine().append("+++");
        Assertions.assertEquals((("---" + (StrBuilderAppendInsertTest.SEP)) + "+++"), sb.toString());
        sb = new StrBuilder("---");
        sb.setNewLineText("#").appendNewLine().setNewLineText(null).appendNewLine();
        Assertions.assertEquals(("---#" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendWithNullText() {
        final StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL");
        Assertions.assertEquals("", sb.toString());
        sb.appendNull();
        Assertions.assertEquals("NULL", sb.toString());
        sb.append(((Object) (null)));
        Assertions.assertEquals("NULLNULL", sb.toString());
        sb.append(StrBuilderAppendInsertTest.FOO);
        Assertions.assertEquals("NULLNULLfoo", sb.toString());
        sb.append(((String) (null)));
        Assertions.assertEquals("NULLNULLfooNULL", sb.toString());
        sb.append("");
        Assertions.assertEquals("NULLNULLfooNULL", sb.toString());
        sb.append("bar");
        Assertions.assertEquals("NULLNULLfooNULLbar", sb.toString());
        sb.append(((StringBuffer) (null)));
        Assertions.assertEquals("NULLNULLfooNULLbarNULL", sb.toString());
        sb.append(new StringBuffer("baz"));
        Assertions.assertEquals("NULLNULLfooNULLbarNULLbaz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_Object() {
        final StrBuilder sb = new StrBuilder();
        sb.appendNull();
        Assertions.assertEquals("", sb.toString());
        sb.append(((Object) (null)));
        Assertions.assertEquals("", sb.toString());
        sb.append(StrBuilderAppendInsertTest.FOO);
        Assertions.assertEquals("foo", sb.toString());
        sb.append(((StringBuffer) (null)));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuffer("baz"));
        Assertions.assertEquals("foobaz", sb.toString());
        sb.append(new StrBuilder("yes"));
        Assertions.assertEquals("foobazyes", sb.toString());
        sb.append(((CharSequence) ("Seq")));
        Assertions.assertEquals("foobazyesSeq", sb.toString());
        sb.append(new StringBuilder("bld"));// Check it supports StringBuilder

        Assertions.assertEquals("foobazyesSeqbld", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_StringBuilder() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((String) (null)));
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new StringBuilder("foo"));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuilder(""));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuilder("bar"));
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_String() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((String) (null)));
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append("foo");
        Assertions.assertEquals("foo", sb.toString());
        sb.append("");
        Assertions.assertEquals("foo", sb.toString());
        sb.append("bar");
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_String_int_int() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((String) (null)), 0, 1);
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append("foo", 0, 3);
        Assertions.assertEquals("foo", sb.toString());
        final StrBuilder sb1 = sb;
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append("bar", (-1), 1), "append(char[], -1,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append("bar", 3, 1), "append(char[], 3,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append("bar", 1, (-1)), "append(char[],, -1) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append("bar", 1, 3), "append(char[], 1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append("bar", (-1), 3), "append(char[], -1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append("bar", 4, 0), "append(char[], 4, 0) expected IndexOutOfBoundsException");
        sb.append("bar", 3, 0);
        Assertions.assertEquals("foo", sb.toString());
        sb.append("abcbardef", 3, 3);
        Assertions.assertEquals("foobar", sb.toString());
        sb.append(((CharSequence) ("abcbardef")), 4, 3);
        Assertions.assertEquals("foobarard", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_StringBuilder_int_int() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((String) (null)), 0, 1);
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new StringBuilder("foo"), 0, 3);
        Assertions.assertEquals("foo", sb.toString());
        final StrBuilder sb1 = sb;
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuilder("bar"), (-1), 1), "append(StringBuilder, -1,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuilder("bar"), 3, 1), "append(StringBuilder, 3,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuilder("bar"), 1, (-1)), "append(StringBuilder,, -1) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuilder("bar"), 1, 3), "append(StringBuilder, 1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuilder("bar"), (-1), 3), "append(StringBuilder, -1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuilder("bar"), 4, 0), "append(StringBuilder, 4, 0) expected IndexOutOfBoundsException");
        sb.append(new StringBuilder("bar"), 3, 0);
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuilder("abcbardef"), 3, 3);
        Assertions.assertEquals("foobar", sb.toString());
        sb.append(new StringBuilder("abcbardef"), 4, 3);
        Assertions.assertEquals("foobarard", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_StringBuffer() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((StringBuffer) (null)));
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new StringBuffer("foo"));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuffer(""));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuffer("bar"));
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_StringBuffer_int_int() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((StringBuffer) (null)), 0, 1);
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new StringBuffer("foo"), 0, 3);
        Assertions.assertEquals("foo", sb.toString());
        final StrBuilder sb1 = sb;
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuffer("bar"), (-1), 1), "append(char[], -1,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuffer("bar"), 3, 1), "append(char[], 3,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuffer("bar"), 1, (-1)), "append(char[],, -1) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuffer("bar"), 1, 3), "append(char[], 1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuffer("bar"), (-1), 3), "append(char[], -1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StringBuffer("bar"), 4, 0), "append(char[], 4, 0) expected IndexOutOfBoundsException");
        sb.append(new StringBuffer("bar"), 3, 0);
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StringBuffer("abcbardef"), 3, 3);
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_StrBuilder() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((StrBuilder) (null)));
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new StrBuilder("foo"));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StrBuilder(""));
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StrBuilder("bar"));
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_StrBuilder_int_int() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((StrBuilder) (null)), 0, 1);
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new StrBuilder("foo"), 0, 3);
        Assertions.assertEquals("foo", sb.toString());
        final StrBuilder sb1 = sb;
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StrBuilder("bar"), (-1), 1), "append(char[], -1,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StrBuilder("bar"), 3, 1), "append(char[], 3,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StrBuilder("bar"), 1, (-1)), "append(char[],, -1) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StrBuilder("bar"), 1, 3), "append(char[], 1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StrBuilder("bar"), (-1), 3), "append(char[], -1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new StrBuilder("bar"), 4, 0), "append(char[], 4, 0) expected IndexOutOfBoundsException");
        sb.append(new StrBuilder("bar"), 3, 0);
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new StrBuilder("abcbardef"), 3, 3);
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_CharArray() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((char[]) (null)));
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new char[0]);
        Assertions.assertEquals("", sb.toString());
        sb.append(new char[]{ 'f', 'o', 'o' });
        Assertions.assertEquals("foo", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_CharArray_int_int() {
        StrBuilder sb = new StrBuilder();
        sb.setNullText("NULL").append(((char[]) (null)), 0, 1);
        Assertions.assertEquals("NULL", sb.toString());
        sb = new StrBuilder();
        sb.append(new char[]{ 'f', 'o', 'o' }, 0, 3);
        Assertions.assertEquals("foo", sb.toString());
        final StrBuilder sb1 = sb;
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new char[]{ 'b', 'a', 'r' }, (-1), 1), "append(char[], -1,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new char[]{ 'b', 'a', 'r' }, 3, 1), "append(char[], 3,) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new char[]{ 'b', 'a', 'r' }, 1, (-1)), "append(char[],, -1) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new char[]{ 'b', 'a', 'r' }, 1, 3), "append(char[], 1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new char[]{ 'b', 'a', 'r' }, (-1), 3), "append(char[], -1, 3) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.append(new char[]{ 'b', 'a', 'r' }, 4, 0), "append(char[], 4, 0) expected IndexOutOfBoundsException");
        sb.append(new char[]{ 'b', 'a', 'r' }, 3, 0);
        Assertions.assertEquals("foo", sb.toString());
        sb.append(new char[]{ 'a', 'b', 'c', 'b', 'a', 'r', 'd', 'e', 'f' }, 3, 3);
        Assertions.assertEquals("foobar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_Boolean() {
        final StrBuilder sb = new StrBuilder();
        sb.append(true);
        Assertions.assertEquals("true", sb.toString());
        sb.append(false);
        Assertions.assertEquals("truefalse", sb.toString());
        sb.append('!');
        Assertions.assertEquals("truefalse!", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_PrimitiveNumber() {
        final StrBuilder sb = new StrBuilder();
        sb.append(0);
        Assertions.assertEquals("0", sb.toString());
        sb.append(1L);
        Assertions.assertEquals("01", sb.toString());
        sb.append(2.3F);
        Assertions.assertEquals("012.3", sb.toString());
        sb.append(4.5);
        Assertions.assertEquals("012.34.5", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_FormattedString() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final String str) {
                (count[0])++;
                return super.append(str);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln("Hello %s", "Alice");
        Assertions.assertEquals(("Hello Alice" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(2, count[0]);// appendNewLine() calls append(String)

        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_Object() {
        final StrBuilder sb = new StrBuilder();
        sb.appendln(((Object) (null)));
        Assertions.assertEquals(("" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        sb.appendln(StrBuilderAppendInsertTest.FOO);
        Assertions.assertEquals((((StrBuilderAppendInsertTest.SEP) + "foo") + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        sb.appendln(Integer.valueOf(6));
        Assertions.assertEquals((((((StrBuilderAppendInsertTest.SEP) + "foo") + (StrBuilderAppendInsertTest.SEP)) + "6") + (StrBuilderAppendInsertTest.SEP)), sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_String() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final String str) {
                (count[0])++;
                return super.append(str);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln("foo");
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(2, count[0]);// appendNewLine() calls append(String)

        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_String_int_int() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final String str, final int startIndex, final int length) {
                (count[0])++;
                return super.append(str, startIndex, length);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln("foo", 0, 3);
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_StringBuffer() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final StringBuffer str) {
                (count[0])++;
                return super.append(str);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln(new StringBuffer("foo"));
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_StringBuilder() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final StringBuilder str) {
                (count[0])++;
                return super.append(str);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln(new StringBuilder("foo"));
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_StringBuffer_int_int() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final StringBuffer str, final int startIndex, final int length) {
                (count[0])++;
                return super.append(str, startIndex, length);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln(new StringBuffer("foo"), 0, 3);
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_StringBuilder_int_int() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final StringBuilder str, final int startIndex, final int length) {
                (count[0])++;
                return super.append(str, startIndex, length);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln(new StringBuilder("foo"), 0, 3);
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_StrBuilder() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final StrBuilder str) {
                (count[0])++;
                return super.append(str);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln(new StrBuilder("foo"));
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_StrBuilder_int_int() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final StrBuilder str, final int startIndex, final int length) {
                (count[0])++;
                return super.append(str, startIndex, length);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln(new StrBuilder("foo"), 0, 3);
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_CharArray() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final char[] str) {
                (count[0])++;
                return super.append(str);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln("foo".toCharArray());
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_CharArray_int_int() {
        final int[] count = new int[2];
        final StrBuilder sb = new StrBuilder() {
            private static final long serialVersionUID = 1L;

            @Override
            public StrBuilder append(final char[] str, final int startIndex, final int length) {
                (count[0])++;
                return super.append(str, startIndex, length);
            }

            @Override
            public StrBuilder appendNewLine() {
                (count[1])++;
                return super.appendNewLine();
            }
        };
        sb.appendln("foo".toCharArray(), 0, 3);
        Assertions.assertEquals(("foo" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        Assertions.assertEquals(1, count[0]);
        Assertions.assertEquals(1, count[1]);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_Boolean() {
        final StrBuilder sb = new StrBuilder();
        sb.appendln(true);
        Assertions.assertEquals(("true" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        sb.clear();
        sb.appendln(false);
        Assertions.assertEquals(("false" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendln_PrimitiveNumber() {
        final StrBuilder sb = new StrBuilder();
        sb.appendln(0);
        Assertions.assertEquals(("0" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        sb.clear();
        sb.appendln(1L);
        Assertions.assertEquals(("1" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        sb.clear();
        sb.appendln(2.3F);
        Assertions.assertEquals(("2.3" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
        sb.clear();
        sb.appendln(4.5);
        Assertions.assertEquals(("4.5" + (StrBuilderAppendInsertTest.SEP)), sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendPadding() {
        final StrBuilder sb = new StrBuilder();
        sb.append("foo");
        Assertions.assertEquals("foo", sb.toString());
        sb.appendPadding((-1), '-');
        Assertions.assertEquals("foo", sb.toString());
        sb.appendPadding(0, '-');
        Assertions.assertEquals("foo", sb.toString());
        sb.appendPadding(1, '-');
        Assertions.assertEquals("foo-", sb.toString());
        sb.appendPadding(16, '-');
        Assertions.assertEquals(20, sb.length());
        // 12345678901234567890
        Assertions.assertEquals("foo-----------------", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendFixedWidthPadLeft() {
        final StrBuilder sb = new StrBuilder();
        sb.appendFixedWidthPadLeft("foo", (-1), '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft("foo", 0, '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft("foo", 1, '-');
        Assertions.assertEquals("o", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft("foo", 2, '-');
        Assertions.assertEquals("oo", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft("foo", 3, '-');
        Assertions.assertEquals("foo", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft("foo", 4, '-');
        Assertions.assertEquals("-foo", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft("foo", 10, '-');
        Assertions.assertEquals(10, sb.length());
        // 1234567890
        Assertions.assertEquals("-------foo", sb.toString());
        sb.clear();
        sb.setNullText("null");
        sb.appendFixedWidthPadLeft(null, 5, '-');
        Assertions.assertEquals("-null", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendFixedWidthPadLeft_int() {
        final StrBuilder sb = new StrBuilder();
        sb.appendFixedWidthPadLeft(123, (-1), '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft(123, 0, '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft(123, 1, '-');
        Assertions.assertEquals("3", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft(123, 2, '-');
        Assertions.assertEquals("23", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft(123, 3, '-');
        Assertions.assertEquals("123", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft(123, 4, '-');
        Assertions.assertEquals("-123", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadLeft(123, 10, '-');
        Assertions.assertEquals(10, sb.length());
        // 1234567890
        Assertions.assertEquals("-------123", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendFixedWidthPadRight() {
        final StrBuilder sb = new StrBuilder();
        sb.appendFixedWidthPadRight("foo", (-1), '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight("foo", 0, '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight("foo", 1, '-');
        Assertions.assertEquals("f", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight("foo", 2, '-');
        Assertions.assertEquals("fo", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight("foo", 3, '-');
        Assertions.assertEquals("foo", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight("foo", 4, '-');
        Assertions.assertEquals("foo-", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight("foo", 10, '-');
        Assertions.assertEquals(10, sb.length());
        // 1234567890
        Assertions.assertEquals("foo-------", sb.toString());
        sb.clear();
        sb.setNullText("null");
        sb.appendFixedWidthPadRight(null, 5, '-');
        Assertions.assertEquals("null-", sb.toString());
    }

    // See: http://issues.apache.org/jira/browse/LANG-299
    @Test
    public void testLang299() {
        final StrBuilder sb = new StrBuilder(1);
        sb.appendFixedWidthPadRight("foo", 1, '-');
        Assertions.assertEquals("f", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendFixedWidthPadRight_int() {
        final StrBuilder sb = new StrBuilder();
        sb.appendFixedWidthPadRight(123, (-1), '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight(123, 0, '-');
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight(123, 1, '-');
        Assertions.assertEquals("1", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight(123, 2, '-');
        Assertions.assertEquals("12", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight(123, 3, '-');
        Assertions.assertEquals("123", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight(123, 4, '-');
        Assertions.assertEquals("123-", sb.toString());
        sb.clear();
        sb.appendFixedWidthPadRight(123, 10, '-');
        Assertions.assertEquals(10, sb.length());
        // 1234567890
        Assertions.assertEquals("123-------", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppend_FormattedString() {
        StrBuilder sb;
        sb = new StrBuilder();
        sb.append("Hi", ((Object[]) (null)));
        Assertions.assertEquals("Hi", sb.toString());
        sb = new StrBuilder();
        sb.append("Hi", "Alice");
        Assertions.assertEquals("Hi", sb.toString());
        sb = new StrBuilder();
        sb.append("Hi %s", "Alice");
        Assertions.assertEquals("Hi Alice", sb.toString());
        sb = new StrBuilder();
        sb.append("Hi %s %,d", "Alice", 5000);
        // group separator depends on system locale
        final char groupingSeparator = DecimalFormatSymbols.getInstance().getGroupingSeparator();
        final String expected = ("Hi Alice 5" + groupingSeparator) + "000";
        Assertions.assertEquals(expected, sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendAll_Array() {
        final StrBuilder sb = new StrBuilder();
        sb.appendAll(((Object[]) (null)));
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendAll();
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendAll("foo", "bar", "baz");
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.appendAll("foo", "bar", "baz");
        Assertions.assertEquals("foobarbaz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendAll_Collection() {
        final StrBuilder sb = new StrBuilder();
        sb.appendAll(((Collection<?>) (null)));
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendAll(Collections.EMPTY_LIST);
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendAll(Arrays.asList(new Object[]{ "foo", "bar", "baz" }));
        Assertions.assertEquals("foobarbaz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendAll_Iterator() {
        final StrBuilder sb = new StrBuilder();
        sb.appendAll(((Iterator<?>) (null)));
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendAll(Collections.EMPTY_LIST.iterator());
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendAll(Arrays.asList(new Object[]{ "foo", "bar", "baz" }).iterator());
        Assertions.assertEquals("foobarbaz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendWithSeparators_Array() {
        final StrBuilder sb = new StrBuilder();
        sb.appendWithSeparators(((Object[]) (null)), ",");
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendWithSeparators(new Object[0], ",");
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendWithSeparators(new Object[]{ "foo", "bar", "baz" }, ",");
        Assertions.assertEquals("foo,bar,baz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(new Object[]{ "foo", "bar", "baz" }, null);
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(new Object[]{ "foo", null, "baz" }, ",");
        Assertions.assertEquals("foo,,baz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendWithSeparators_Collection() {
        final StrBuilder sb = new StrBuilder();
        sb.appendWithSeparators(((Collection<?>) (null)), ",");
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Collections.EMPTY_LIST, ",");
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", "bar", "baz" }), ",");
        Assertions.assertEquals("foo,bar,baz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", "bar", "baz" }), null);
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", null, "baz" }), ",");
        Assertions.assertEquals("foo,,baz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendWithSeparators_Iterator() {
        final StrBuilder sb = new StrBuilder();
        sb.appendWithSeparators(((Iterator<?>) (null)), ",");
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Collections.EMPTY_LIST.iterator(), ",");
        Assertions.assertEquals("", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", "bar", "baz" }).iterator(), ",");
        Assertions.assertEquals("foo,bar,baz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", "bar", "baz" }).iterator(), null);
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", null, "baz" }).iterator(), ",");
        Assertions.assertEquals("foo,,baz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendWithSeparatorsWithNullText() {
        final StrBuilder sb = new StrBuilder();
        sb.setNullText("null");
        sb.appendWithSeparators(new Object[]{ "foo", null, "baz" }, ",");
        Assertions.assertEquals("foo,null,baz", sb.toString());
        sb.clear();
        sb.appendWithSeparators(Arrays.asList(new Object[]{ "foo", null, "baz" }), ",");
        Assertions.assertEquals("foo,null,baz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendSeparator_String() {
        final StrBuilder sb = new StrBuilder();
        sb.appendSeparator(",");// no effect

        Assertions.assertEquals("", sb.toString());
        sb.append("foo");
        Assertions.assertEquals("foo", sb.toString());
        sb.appendSeparator(",");
        Assertions.assertEquals("foo,", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendSeparator_String_String() {
        final StrBuilder sb = new StrBuilder();
        final String startSeparator = "order by ";
        final String standardSeparator = ",";
        final String foo = "foo";
        sb.appendSeparator(null, null);
        Assertions.assertEquals("", sb.toString());
        sb.appendSeparator(standardSeparator, null);
        Assertions.assertEquals("", sb.toString());
        sb.appendSeparator(standardSeparator, startSeparator);
        Assertions.assertEquals(startSeparator, sb.toString());
        sb.appendSeparator(null, null);
        Assertions.assertEquals(startSeparator, sb.toString());
        sb.appendSeparator(null, startSeparator);
        Assertions.assertEquals(startSeparator, sb.toString());
        sb.append(foo);
        Assertions.assertEquals((startSeparator + foo), sb.toString());
        sb.appendSeparator(standardSeparator, startSeparator);
        Assertions.assertEquals(((startSeparator + foo) + standardSeparator), sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendSeparator_char() {
        final StrBuilder sb = new StrBuilder();
        sb.appendSeparator(',');// no effect

        Assertions.assertEquals("", sb.toString());
        sb.append("foo");
        Assertions.assertEquals("foo", sb.toString());
        sb.appendSeparator(',');
        Assertions.assertEquals("foo,", sb.toString());
    }

    @Test
    public void testAppendSeparator_char_char() {
        final StrBuilder sb = new StrBuilder();
        final char startSeparator = ':';
        final char standardSeparator = ',';
        final String foo = "foo";
        sb.appendSeparator(standardSeparator, startSeparator);// no effect

        Assertions.assertEquals(String.valueOf(startSeparator), sb.toString());
        sb.append(foo);
        Assertions.assertEquals(((String.valueOf(startSeparator)) + foo), sb.toString());
        sb.appendSeparator(standardSeparator, startSeparator);
        Assertions.assertEquals((((String.valueOf(startSeparator)) + foo) + standardSeparator), sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendSeparator_String_int() {
        final StrBuilder sb = new StrBuilder();
        sb.appendSeparator(",", 0);// no effect

        Assertions.assertEquals("", sb.toString());
        sb.append("foo");
        Assertions.assertEquals("foo", sb.toString());
        sb.appendSeparator(",", 1);
        Assertions.assertEquals("foo,", sb.toString());
        sb.appendSeparator(",", (-1));// no effect

        Assertions.assertEquals("foo,", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendSeparator_char_int() {
        final StrBuilder sb = new StrBuilder();
        sb.appendSeparator(',', 0);// no effect

        Assertions.assertEquals("", sb.toString());
        sb.append("foo");
        Assertions.assertEquals("foo", sb.toString());
        sb.appendSeparator(',', 1);
        Assertions.assertEquals("foo,", sb.toString());
        sb.appendSeparator(',', (-1));// no effect

        Assertions.assertEquals("foo,", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testInsert() {
        final StrBuilder sb = new StrBuilder();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), StrBuilderAppendInsertTest.FOO), "insert(-1, Object) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, StrBuilderAppendInsertTest.FOO), "insert(7, Object) expected StringIndexOutOfBoundsException");
        sb.insert(0, ((Object) (null)));
        Assertions.assertEquals("barbaz", sb.toString());
        sb.insert(0, StrBuilderAppendInsertTest.FOO);
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), "foo"), "insert(-1, String) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, "foo"), "insert(7, String) expected StringIndexOutOfBoundsException");
        sb.insert(0, ((String) (null)));
        Assertions.assertEquals("barbaz", sb.toString());
        sb.insert(0, "foo");
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), new char[]{ 'f', 'o', 'o' }), "insert(-1, char[]) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, new char[]{ 'f', 'o', 'o' }), "insert(7, char[]) expected StringIndexOutOfBoundsException");
        sb.insert(0, ((char[]) (null)));
        Assertions.assertEquals("barbaz", sb.toString());
        sb.insert(0, new char[0]);
        Assertions.assertEquals("barbaz", sb.toString());
        sb.insert(0, new char[]{ 'f', 'o', 'o' });
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 3, 3), "insert(-1, char[], 3, 3) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 3, 3), "insert(7, char[], 3, 3) expected StringIndexOutOfBoundsException");
        sb.insert(0, null, 0, 0);
        Assertions.assertEquals("barbaz", sb.toString());
        sb.insert(0, new char[0], 0, 0);
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(0, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, (-1), 3), "insert(0, char[], -1, 3) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(0, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 10, 3), "insert(0, char[], 10, 3) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(0, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 0, (-1)), "insert(0, char[], 0, -1) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(0, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 0, 10), "insert(0, char[], 0, 10) expected StringIndexOutOfBoundsException");
        sb.insert(0, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 0, 0);
        Assertions.assertEquals("barbaz", sb.toString());
        sb.insert(0, new char[]{ 'a', 'b', 'c', 'f', 'o', 'o', 'd', 'e', 'f' }, 3, 3);
        Assertions.assertEquals("foobarbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), true), "insert(-1, boolean) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, true), "insert(7, boolean) expected StringIndexOutOfBoundsException");
        sb.insert(0, true);
        Assertions.assertEquals("truebarbaz", sb.toString());
        sb.insert(0, false);
        Assertions.assertEquals("falsetruebarbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), '!'), "insert(-1, char) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, '!'), "insert(7, char) expected StringIndexOutOfBoundsException");
        sb.insert(0, '!');
        Assertions.assertEquals("!barbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), 0), "insert(-1, int) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, 0), "insert(7, int) expected StringIndexOutOfBoundsException");
        sb.insert(0, '0');
        Assertions.assertEquals("0barbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), 1L), "insert(-1, long) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, 1L), "insert(7, long) expected StringIndexOutOfBoundsException");
        sb.insert(0, 1L);
        Assertions.assertEquals("1barbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), 2.3F), "insert(-1, float) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, 2.3F), "insert(7, float) expected StringIndexOutOfBoundsException");
        sb.insert(0, 2.3F);
        Assertions.assertEquals("2.3barbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), 4.5), "insert(-1, double) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, 4.5), "insert(7, double) expected StringIndexOutOfBoundsException");
        sb.insert(0, 4.5);
        Assertions.assertEquals("4.5barbaz", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testInsertWithNullText() {
        final StrBuilder sb = new StrBuilder();
        sb.setNullText("null");
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), StrBuilderAppendInsertTest.FOO), "insert(-1, Object) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, StrBuilderAppendInsertTest.FOO), "insert(7, Object) expected StringIndexOutOfBoundsException");
        sb.insert(0, ((Object) (null)));
        Assertions.assertEquals("nullbarbaz", sb.toString());
        sb.insert(0, StrBuilderAppendInsertTest.FOO);
        Assertions.assertEquals("foonullbarbaz", sb.toString());
        sb.clear();
        sb.append("barbaz");
        Assertions.assertEquals("barbaz", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert((-1), "foo"), "insert(-1, String) expected StringIndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.insert(7, "foo"), "insert(7, String) expected StringIndexOutOfBoundsException");
        sb.insert(0, ((String) (null)));
        Assertions.assertEquals("nullbarbaz", sb.toString());
        sb.insert(0, "foo");
        Assertions.assertEquals("foonullbarbaz", sb.toString());
        sb.insert(0, ((char[]) (null)));
        Assertions.assertEquals("nullfoonullbarbaz", sb.toString());
        sb.insert(0, null, 0, 0);
        Assertions.assertEquals("nullnullfoonullbarbaz", sb.toString());
    }
}

