/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006,2008 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs.util;


import org.junit.Test;


public class StringsTest {
    public static String[] escapedStrings = new String[]{ // mixed entities/unicode escape sequences
    "a b c 1 2 3 &amp; &lt; &gt; &quot; &apos; \\u0005 \\u0013 &#955; \\\\u0007", // *even* series of prefixed slashes + \\u -> do escape
    "a b c \\\\\\u0005", // *odd* series of prefixed slashes + \\u -> don't escape
    "a b c \\\\\\\\u0005", // mixed even/odd prefixed slashes
    "a b c \\\\\\u0005 \\\\\\\\u0013", // make sure slashes work on their own (no double un/escaping)
    "\\\\\\", // make sure that normal characters are handled correctly if they
    // appear after escapes
    "a b c 1 2 3 &amp; &lt; &gt; &quot; &apos; \\u0005 \\u0013 &#955; \\\\u0007 a b c 1 2 3", // escaping a null string should be safe
    null, // an empty string should be safe too
    "" };

    public static String[] unescapedStrings = new String[]{ "a b c 1 2 3 & < > \" \' \u0005 \u0013 \u03bb \\\\u0007", "a b c \\\\\u0005", "a b c \\\\\\\\u0005", "a b c \\\\\u0005 \\\\\\\\u0013", "\\\\\\", "a b c 1 2 3 & < > \" \' \u0005 \u0013 \u03bb \\\\u0007 a b c 1 2 3", null, "" };

    @Test
    public void testEscapeXml() {
        assert (StringsTest.escapedStrings.length) == (StringsTest.unescapedStrings.length);
        for (int i = 0; i < (StringsTest.unescapedStrings.length); i++) {
            if ((StringsTest.unescapedStrings[i]) == null) {
                assert (Strings.escapeXml(StringsTest.unescapedStrings[i])) == null;
            } else {
                assert (Strings.escapeXml(StringsTest.unescapedStrings[i]).compareTo(StringsTest.escapedStrings[i])) == 0;
            }
        }
    }

    @Test
    public void testUnescapeXml() {
        assert (StringsTest.escapedStrings.length) == (StringsTest.unescapedStrings.length);
        for (int i = 0; i < (StringsTest.escapedStrings.length); i++) {
            if ((StringsTest.escapedStrings[i]) == null) {
                assert (Strings.unescapeXml(StringsTest.escapedStrings[i])) == null;
            } else {
                assert (Strings.unescapeXml(StringsTest.escapedStrings[i]).compareTo(StringsTest.unescapedStrings[i])) == 0;
            }
        }
    }

    @Test
    public void testEscapeLFCRBackSlash() {
        checkEscapeLFCRBackSlash("abc", "abc");
        checkEscapeLFCRBackSlash("\\n", "\n");
        checkEscapeLFCRBackSlash("\\r", "\r");
        checkEscapeLFCRBackSlash("\\\\a\\r", "\\a\r");
    }
}

