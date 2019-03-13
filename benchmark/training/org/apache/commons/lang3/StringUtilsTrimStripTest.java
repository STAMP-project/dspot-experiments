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


/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Trim/Strip methods
 */
public class StringUtilsTrimStripTest {
    private static final String FOO = "foo";

    @Test
    public void testTrim() {
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trim(((StringUtilsTrimStripTest.FOO) + "  ")));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trim(((" " + (StringUtilsTrimStripTest.FOO)) + "  ")));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trim((" " + (StringUtilsTrimStripTest.FOO))));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trim(((StringUtilsTrimStripTest.FOO) + "")));
        Assertions.assertEquals("", StringUtils.trim(" \t\r\n\b "));
        Assertions.assertEquals("", StringUtils.trim(StringUtilsTest.TRIMMABLE));
        Assertions.assertEquals(StringUtilsTest.NON_TRIMMABLE, StringUtils.trim(StringUtilsTest.NON_TRIMMABLE));
        Assertions.assertEquals("", StringUtils.trim(""));
        Assertions.assertNull(StringUtils.trim(null));
    }

    @Test
    public void testTrimToNull() {
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToNull(((StringUtilsTrimStripTest.FOO) + "  ")));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToNull(((" " + (StringUtilsTrimStripTest.FOO)) + "  ")));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToNull((" " + (StringUtilsTrimStripTest.FOO))));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToNull(((StringUtilsTrimStripTest.FOO) + "")));
        Assertions.assertNull(StringUtils.trimToNull(" \t\r\n\b "));
        Assertions.assertNull(StringUtils.trimToNull(StringUtilsTest.TRIMMABLE));
        Assertions.assertEquals(StringUtilsTest.NON_TRIMMABLE, StringUtils.trimToNull(StringUtilsTest.NON_TRIMMABLE));
        Assertions.assertNull(StringUtils.trimToNull(""));
        Assertions.assertNull(StringUtils.trimToNull(null));
    }

    @Test
    public void testTrimToEmpty() {
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToEmpty(((StringUtilsTrimStripTest.FOO) + "  ")));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToEmpty(((" " + (StringUtilsTrimStripTest.FOO)) + "  ")));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToEmpty((" " + (StringUtilsTrimStripTest.FOO))));
        Assertions.assertEquals(StringUtilsTrimStripTest.FOO, StringUtils.trimToEmpty(((StringUtilsTrimStripTest.FOO) + "")));
        Assertions.assertEquals("", StringUtils.trimToEmpty(" \t\r\n\b "));
        Assertions.assertEquals("", StringUtils.trimToEmpty(StringUtilsTest.TRIMMABLE));
        Assertions.assertEquals(StringUtilsTest.NON_TRIMMABLE, StringUtils.trimToEmpty(StringUtilsTest.NON_TRIMMABLE));
        Assertions.assertEquals("", StringUtils.trimToEmpty(""));
        Assertions.assertEquals("", StringUtils.trimToEmpty(null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testStrip_String() {
        Assertions.assertNull(StringUtils.strip(null));
        Assertions.assertEquals("", StringUtils.strip(""));
        Assertions.assertEquals("", StringUtils.strip("        "));
        Assertions.assertEquals("abc", StringUtils.strip("  abc  "));
        Assertions.assertEquals(StringUtilsTest.NON_WHITESPACE, StringUtils.strip((((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)) + (StringUtilsTest.WHITESPACE))));
    }

    @Test
    public void testStripToNull_String() {
        Assertions.assertNull(StringUtils.stripToNull(null));
        Assertions.assertNull(StringUtils.stripToNull(""));
        Assertions.assertNull(StringUtils.stripToNull("        "));
        Assertions.assertNull(StringUtils.stripToNull(StringUtilsTest.WHITESPACE));
        Assertions.assertEquals("ab c", StringUtils.stripToNull("  ab c  "));
        Assertions.assertEquals(StringUtilsTest.NON_WHITESPACE, StringUtils.stripToNull((((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)) + (StringUtilsTest.WHITESPACE))));
    }

    @Test
    public void testStripToEmpty_String() {
        Assertions.assertEquals("", StringUtils.stripToEmpty(null));
        Assertions.assertEquals("", StringUtils.stripToEmpty(""));
        Assertions.assertEquals("", StringUtils.stripToEmpty("        "));
        Assertions.assertEquals("", StringUtils.stripToEmpty(StringUtilsTest.WHITESPACE));
        Assertions.assertEquals("ab c", StringUtils.stripToEmpty("  ab c  "));
        Assertions.assertEquals(StringUtilsTest.NON_WHITESPACE, StringUtils.stripToEmpty((((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)) + (StringUtilsTest.WHITESPACE))));
    }

    @Test
    public void testStrip_StringString() {
        // null strip
        Assertions.assertNull(StringUtils.strip(null, null));
        Assertions.assertEquals("", StringUtils.strip("", null));
        Assertions.assertEquals("", StringUtils.strip("        ", null));
        Assertions.assertEquals("abc", StringUtils.strip("  abc  ", null));
        Assertions.assertEquals(StringUtilsTest.NON_WHITESPACE, StringUtils.strip((((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)) + (StringUtilsTest.WHITESPACE)), null));
        // "" strip
        Assertions.assertNull(StringUtils.strip(null, ""));
        Assertions.assertEquals("", StringUtils.strip("", ""));
        Assertions.assertEquals("        ", StringUtils.strip("        ", ""));
        Assertions.assertEquals("  abc  ", StringUtils.strip("  abc  ", ""));
        Assertions.assertEquals(StringUtilsTest.WHITESPACE, StringUtils.strip(StringUtilsTest.WHITESPACE, ""));
        // " " strip
        Assertions.assertNull(StringUtils.strip(null, " "));
        Assertions.assertEquals("", StringUtils.strip("", " "));
        Assertions.assertEquals("", StringUtils.strip("        ", " "));
        Assertions.assertEquals("abc", StringUtils.strip("  abc  ", " "));
        // "ab" strip
        Assertions.assertNull(StringUtils.strip(null, "ab"));
        Assertions.assertEquals("", StringUtils.strip("", "ab"));
        Assertions.assertEquals("        ", StringUtils.strip("        ", "ab"));
        Assertions.assertEquals("  abc  ", StringUtils.strip("  abc  ", "ab"));
        Assertions.assertEquals("c", StringUtils.strip("abcabab", "ab"));
        Assertions.assertEquals(StringUtilsTest.WHITESPACE, StringUtils.strip(StringUtilsTest.WHITESPACE, ""));
    }

    @Test
    public void testStripStart_StringString() {
        // null stripStart
        Assertions.assertNull(StringUtils.stripStart(null, null));
        Assertions.assertEquals("", StringUtils.stripStart("", null));
        Assertions.assertEquals("", StringUtils.stripStart("        ", null));
        Assertions.assertEquals("abc  ", StringUtils.stripStart("  abc  ", null));
        Assertions.assertEquals(((StringUtilsTest.NON_WHITESPACE) + (StringUtilsTest.WHITESPACE)), StringUtils.stripStart((((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)) + (StringUtilsTest.WHITESPACE)), null));
        // "" stripStart
        Assertions.assertNull(StringUtils.stripStart(null, ""));
        Assertions.assertEquals("", StringUtils.stripStart("", ""));
        Assertions.assertEquals("        ", StringUtils.stripStart("        ", ""));
        Assertions.assertEquals("  abc  ", StringUtils.stripStart("  abc  ", ""));
        Assertions.assertEquals(StringUtilsTest.WHITESPACE, StringUtils.stripStart(StringUtilsTest.WHITESPACE, ""));
        // " " stripStart
        Assertions.assertNull(StringUtils.stripStart(null, " "));
        Assertions.assertEquals("", StringUtils.stripStart("", " "));
        Assertions.assertEquals("", StringUtils.stripStart("        ", " "));
        Assertions.assertEquals("abc  ", StringUtils.stripStart("  abc  ", " "));
        // "ab" stripStart
        Assertions.assertNull(StringUtils.stripStart(null, "ab"));
        Assertions.assertEquals("", StringUtils.stripStart("", "ab"));
        Assertions.assertEquals("        ", StringUtils.stripStart("        ", "ab"));
        Assertions.assertEquals("  abc  ", StringUtils.stripStart("  abc  ", "ab"));
        Assertions.assertEquals("cabab", StringUtils.stripStart("abcabab", "ab"));
        Assertions.assertEquals(StringUtilsTest.WHITESPACE, StringUtils.stripStart(StringUtilsTest.WHITESPACE, ""));
    }

    @Test
    public void testStripEnd_StringString() {
        // null stripEnd
        Assertions.assertNull(StringUtils.stripEnd(null, null));
        Assertions.assertEquals("", StringUtils.stripEnd("", null));
        Assertions.assertEquals("", StringUtils.stripEnd("        ", null));
        Assertions.assertEquals("  abc", StringUtils.stripEnd("  abc  ", null));
        Assertions.assertEquals(((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)), StringUtils.stripEnd((((StringUtilsTest.WHITESPACE) + (StringUtilsTest.NON_WHITESPACE)) + (StringUtilsTest.WHITESPACE)), null));
        // "" stripEnd
        Assertions.assertNull(StringUtils.stripEnd(null, ""));
        Assertions.assertEquals("", StringUtils.stripEnd("", ""));
        Assertions.assertEquals("        ", StringUtils.stripEnd("        ", ""));
        Assertions.assertEquals("  abc  ", StringUtils.stripEnd("  abc  ", ""));
        Assertions.assertEquals(StringUtilsTest.WHITESPACE, StringUtils.stripEnd(StringUtilsTest.WHITESPACE, ""));
        // " " stripEnd
        Assertions.assertNull(StringUtils.stripEnd(null, " "));
        Assertions.assertEquals("", StringUtils.stripEnd("", " "));
        Assertions.assertEquals("", StringUtils.stripEnd("        ", " "));
        Assertions.assertEquals("  abc", StringUtils.stripEnd("  abc  ", " "));
        // "ab" stripEnd
        Assertions.assertNull(StringUtils.stripEnd(null, "ab"));
        Assertions.assertEquals("", StringUtils.stripEnd("", "ab"));
        Assertions.assertEquals("        ", StringUtils.stripEnd("        ", "ab"));
        Assertions.assertEquals("  abc  ", StringUtils.stripEnd("  abc  ", "ab"));
        Assertions.assertEquals("abc", StringUtils.stripEnd("abcabab", "ab"));
        Assertions.assertEquals(StringUtilsTest.WHITESPACE, StringUtils.stripEnd(StringUtilsTest.WHITESPACE, ""));
    }

    @Test
    public void testStripAll() {
        // test stripAll method, merely an array version of the above strip
        final String[] empty = new String[0];
        final String[] fooSpace = new String[]{ ("  " + (StringUtilsTrimStripTest.FOO)) + "  ", "  " + (StringUtilsTrimStripTest.FOO), (StringUtilsTrimStripTest.FOO) + "  " };
        final String[] fooDots = new String[]{ (".." + (StringUtilsTrimStripTest.FOO)) + "..", ".." + (StringUtilsTrimStripTest.FOO), (StringUtilsTrimStripTest.FOO) + ".." };
        final String[] foo = new String[]{ StringUtilsTrimStripTest.FOO, StringUtilsTrimStripTest.FOO, StringUtilsTrimStripTest.FOO };
        Assertions.assertNull(StringUtils.stripAll(((String[]) (null))));
        // Additional varargs tests
        Assertions.assertArrayEquals(empty, StringUtils.stripAll());// empty array

        Assertions.assertArrayEquals(new String[]{ null }, StringUtils.stripAll(((String) (null))));// == new String[]{null}

        Assertions.assertArrayEquals(empty, StringUtils.stripAll(empty));
        Assertions.assertArrayEquals(foo, StringUtils.stripAll(fooSpace));
        Assertions.assertNull(StringUtils.stripAll(null, null));
        Assertions.assertArrayEquals(foo, StringUtils.stripAll(fooSpace, null));
        Assertions.assertArrayEquals(foo, StringUtils.stripAll(fooDots, "."));
    }

    @Test
    public void testStripAccents() {
        final String cue = "\u00c7\u00fa\u00ea";
        Assertions.assertEquals("Cue", StringUtils.stripAccents(cue), ("Failed to strip accents from " + cue));
        final String lots = "\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5\u00c7\u00c8\u00c9" + ("\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf\u00d1\u00d2\u00d3" + "\u00d4\u00d5\u00d6\u00d9\u00da\u00db\u00dc\u00dd");
        Assertions.assertEquals("AAAAAACEEEEIIIINOOOOOUUUUY", StringUtils.stripAccents(lots), ("Failed to strip accents from " + lots));
        Assertions.assertNull(StringUtils.stripAccents(null), "Failed null safety");
        Assertions.assertEquals("", StringUtils.stripAccents(""), "Failed empty String");
        Assertions.assertEquals("control", StringUtils.stripAccents("control"), "Failed to handle non-accented text");
        Assertions.assertEquals("eclair", StringUtils.stripAccents("\u00e9clair"), "Failed to handle easy example");
        Assertions.assertEquals("ALOSZZCN aloszzcn", StringUtils.stripAccents(("\u0104\u0141\u00d3\u015a\u017b\u0179\u0106\u0143 " + "\u0105\u0142\u00f3\u015b\u017c\u017a\u0107\u0144")));
    }
}

