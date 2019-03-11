/**
 * Copyright (C) 2010 The Android Open Source Project
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
package org.json;


import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


/**
 * This black box test was written without inspecting the non-free org.json sourcecode.
 */
public class JSONTokenerTest extends TestCase {
    public void testNulls() throws JSONException {
        // JSONTokener accepts null, only to fail later on almost all APIs!
        new JSONTokener(null).back();
        try {
            new JSONTokener(null).more();
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).next();
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).next(3);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).next('A');
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).nextClean();
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).nextString('"');
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).nextTo('A');
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).nextTo("ABC");
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).nextValue();
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).skipPast("ABC");
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            new JSONTokener(null).skipTo('A');
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        TestCase.assertEquals("foo! at character 0 of null", new JSONTokener(null).syntaxError("foo!").getMessage());
        TestCase.assertEquals(" at character 0 of null", new JSONTokener(null).toString());
    }

    public void testEmptyString() throws JSONException {
        JSONTokener backTokener = new JSONTokener("");
        backTokener.back();
        TestCase.assertEquals(" at character 0 of ", backTokener.toString());
        TestCase.assertFalse(new JSONTokener("").more());
        TestCase.assertEquals('\u0000', new JSONTokener("").next());
        try {
            new JSONTokener("").next(3);
            TestCase.fail();
        } catch (JSONException expected) {
        }
        try {
            new JSONTokener("").next('A');
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals('\u0000', new JSONTokener("").nextClean());
        try {
            new JSONTokener("").nextString('"');
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals("", new JSONTokener("").nextTo('A'));
        TestCase.assertEquals("", new JSONTokener("").nextTo("ABC"));
        try {
            new JSONTokener("").nextValue();
            TestCase.fail();
        } catch (JSONException e) {
        }
        new JSONTokener("").skipPast("ABC");
        TestCase.assertEquals('\u0000', new JSONTokener("").skipTo('A'));
        TestCase.assertEquals("foo! at character 0 of ", new JSONTokener("").syntaxError("foo!").getMessage());
        TestCase.assertEquals(" at character 0 of ", new JSONTokener("").toString());
    }

    public void testCharacterNavigation() throws JSONException {
        JSONTokener abcdeTokener = new JSONTokener("ABCDE");
        TestCase.assertEquals('A', abcdeTokener.next());
        TestCase.assertEquals('B', abcdeTokener.next('B'));
        TestCase.assertEquals("CD", abcdeTokener.next(2));
        try {
            abcdeTokener.next(2);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals('E', abcdeTokener.nextClean());
        TestCase.assertEquals('\u0000', abcdeTokener.next());
        TestCase.assertFalse(abcdeTokener.more());
        abcdeTokener.back();
        TestCase.assertTrue(abcdeTokener.more());
        TestCase.assertEquals('E', abcdeTokener.next());
    }

    public void testBackNextAndMore() throws JSONException {
        JSONTokener abcTokener = new JSONTokener("ABC");
        TestCase.assertTrue(abcTokener.more());
        abcTokener.next();
        abcTokener.next();
        TestCase.assertTrue(abcTokener.more());
        abcTokener.next();
        TestCase.assertFalse(abcTokener.more());
        abcTokener.back();
        TestCase.assertTrue(abcTokener.more());
        abcTokener.next();
        TestCase.assertFalse(abcTokener.more());
        abcTokener.back();
        abcTokener.back();
        abcTokener.back();
        abcTokener.back();// you can back up before the beginning of a String!

        TestCase.assertEquals('A', abcTokener.next());
    }

    public void testNextMatching() throws JSONException {
        JSONTokener abcdTokener = new JSONTokener("ABCD");
        TestCase.assertEquals('A', abcdTokener.next('A'));
        try {
            abcdTokener.next('C');// although it failed, this op consumes a character of input

            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals('C', abcdTokener.next('C'));
        TestCase.assertEquals('D', abcdTokener.next('D'));
        try {
            abcdTokener.next('E');
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testNextN() throws JSONException {
        JSONTokener abcdeTokener = new JSONTokener("ABCDEF");
        TestCase.assertEquals("", abcdeTokener.next(0));
        try {
            abcdeTokener.next(7);
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals("ABC", abcdeTokener.next(3));
        try {
            abcdeTokener.next(4);
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testNextNWithAllRemaining() throws JSONException {
        JSONTokener tokener = new JSONTokener("ABCDEF");
        tokener.next(3);
        try {
            tokener.next(3);
        } catch (JSONException e) {
            AssertionFailedError error = new AssertionFailedError("off-by-one error?");
            error.initCause(e);
            throw error;
        }
    }

    public void testNext0() throws JSONException {
        JSONTokener tokener = new JSONTokener("ABCDEF");
        tokener.next(5);
        tokener.next();
        try {
            tokener.next(0);
        } catch (JSONException e) {
            Error error = new AssertionFailedError("Returning an empty string should be valid");
            error.initCause(e);
            throw error;
        }
    }

    public void testNextCleanComments() throws JSONException {
        JSONTokener tokener = new JSONTokener("  A  /*XX*/B/*XX//XX\n//XX\nXX*/C//X//X//X\nD/*X*///X\n");
        TestCase.assertEquals('A', tokener.nextClean());
        TestCase.assertEquals('B', tokener.nextClean());
        TestCase.assertEquals('C', tokener.nextClean());
        TestCase.assertEquals('D', tokener.nextClean());
        TestCase.assertEquals('\u0000', tokener.nextClean());
    }

    public void testNextCleanNestedCStyleComments() throws JSONException {
        JSONTokener tokener = new JSONTokener("A /* B /* C */ D */ E");
        TestCase.assertEquals('A', tokener.nextClean());
        TestCase.assertEquals('D', tokener.nextClean());
        TestCase.assertEquals('*', tokener.nextClean());
        TestCase.assertEquals('/', tokener.nextClean());
        TestCase.assertEquals('E', tokener.nextClean());
    }

    /**
     * Some applications rely on parsing '#' to lead an end-of-line comment.
     * http://b/2571423
     */
    public void testNextCleanHashComments() throws JSONException {
        JSONTokener tokener = new JSONTokener("A # B */ /* C */ \nD #");
        TestCase.assertEquals('A', tokener.nextClean());
        TestCase.assertEquals('D', tokener.nextClean());
        TestCase.assertEquals('\u0000', tokener.nextClean());
    }

    public void testNextCleanCommentsTrailingSingleSlash() throws JSONException {
        JSONTokener tokener = new JSONTokener(" / S /");
        TestCase.assertEquals('/', tokener.nextClean());
        TestCase.assertEquals('S', tokener.nextClean());
        TestCase.assertEquals('/', tokener.nextClean());
        TestCase.assertEquals("nextClean doesn't consume a trailing slash", '\u0000', tokener.nextClean());
    }

    public void testNextCleanTrailingOpenComment() throws JSONException {
        try {
            new JSONTokener("  /* ").nextClean();
            TestCase.fail();
        } catch (JSONException e) {
        }
        TestCase.assertEquals('\u0000', new JSONTokener("  // ").nextClean());
    }

    public void testNextCleanNewlineDelimiters() throws JSONException {
        TestCase.assertEquals('B', new JSONTokener("  // \r\n  B ").nextClean());
        TestCase.assertEquals('B', new JSONTokener("  // \n  B ").nextClean());
        TestCase.assertEquals('B', new JSONTokener("  // \r  B ").nextClean());
    }

    public void testNextCleanSkippedWhitespace() throws JSONException {
        TestCase.assertEquals("character tabulation", 'A', new JSONTokener("\tA").nextClean());
        TestCase.assertEquals("line feed", 'A', new JSONTokener("\nA").nextClean());
        TestCase.assertEquals("carriage return", 'A', new JSONTokener("\rA").nextClean());
        TestCase.assertEquals("space", 'A', new JSONTokener(" A").nextClean());
    }

    /**
     * Tests which characters tokener treats as ignorable whitespace. See Kevin Bourrillion's
     * <a href="https://spreadsheets.google.com/pub?key=pd8dAQyHbdewRsnE5x5GzKQ">list
     * of whitespace characters</a>.
     */
    public void testNextCleanRetainedWhitespace() throws JSONException {
        assertNotClean("null", '\u0000');
        assertNotClean("next line", '\u0085');
        assertNotClean("non-breaking space", '\u00a0');
        assertNotClean("ogham space mark", '\u1680');
        assertNotClean("mongolian vowel separator", '\u180e');
        assertNotClean("en quad", '\u2000');
        assertNotClean("em quad", '\u2001');
        assertNotClean("en space", '\u2002');
        assertNotClean("em space", '\u2003');
        assertNotClean("three-per-em space", '\u2004');
        assertNotClean("four-per-em space", '\u2005');
        assertNotClean("six-per-em space", '\u2006');
        assertNotClean("figure space", '\u2007');
        assertNotClean("punctuation space", '\u2008');
        assertNotClean("thin space", '\u2009');
        assertNotClean("hair space", '\u200a');
        assertNotClean("zero-width space", '\u200b');
        assertNotClean("left-to-right mark", '\u200e');
        assertNotClean("right-to-left mark", '\u200f');
        assertNotClean("line separator", '\u2028');
        assertNotClean("paragraph separator", '\u2029');
        assertNotClean("narrow non-breaking space", '\u202f');
        assertNotClean("medium mathematical space", '\u205f');
        assertNotClean("ideographic space", '\u3000');
        assertNotClean("line tabulation", '\u000b');
        assertNotClean("form feed", '\f');
        assertNotClean("information separator 4", '\u001c');
        assertNotClean("information separator 3", '\u001d');
        assertNotClean("information separator 2", '\u001e');
        assertNotClean("information separator 1", '\u001f');
    }

    public void testNextString() throws JSONException {
        TestCase.assertEquals("", new JSONTokener("'").nextString('\''));
        TestCase.assertEquals("", new JSONTokener("\"").nextString('\"'));
        TestCase.assertEquals("ABC", new JSONTokener("ABC'DEF").nextString('\''));
        TestCase.assertEquals("ABC", new JSONTokener("ABC'''DEF").nextString('\''));
        // nextString permits slash-escaping of arbitrary characters!
        TestCase.assertEquals("ABC", new JSONTokener("A\\B\\C\'DEF").nextString('\''));
        JSONTokener tokener = new JSONTokener(" \'abc\' \'def\' \"ghi\"");
        tokener.next();
        TestCase.assertEquals('\'', tokener.next());
        TestCase.assertEquals("abc", tokener.nextString('\''));
        tokener.next();
        TestCase.assertEquals('\'', tokener.next());
        TestCase.assertEquals("def", tokener.nextString('\''));
        tokener.next();
        TestCase.assertEquals('"', tokener.next());
        TestCase.assertEquals("ghi", tokener.nextString('\"'));
        TestCase.assertFalse(tokener.more());
    }

    public void testNextStringNoDelimiter() throws JSONException {
        try {
            new JSONTokener("").nextString('\'');
            TestCase.fail();
        } catch (JSONException e) {
        }
        JSONTokener tokener = new JSONTokener(" 'abc");
        tokener.next();
        tokener.next();
        try {
            tokener.next('\'');
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testNextStringEscapedQuote() throws JSONException {
        try {
            new JSONTokener("abc\\").nextString('"');
            TestCase.fail();
        } catch (JSONException e) {
        }
        // we're mixing Java escaping like \" and JavaScript escaping like \\\"
        // which makes these tests extra tricky to read!
        TestCase.assertEquals("abc\"def", new JSONTokener("abc\\\"def\"ghi").nextString('"'));
        TestCase.assertEquals("abc\\def", new JSONTokener("abc\\\\def\"ghi").nextString('"'));
        TestCase.assertEquals("abc/def", new JSONTokener("abc\\/def\"ghi").nextString('"'));
        TestCase.assertEquals("abc\bdef", new JSONTokener("abc\\bdef\"ghi").nextString('"'));
        TestCase.assertEquals("abc\fdef", new JSONTokener("abc\\fdef\"ghi").nextString('"'));
        TestCase.assertEquals("abc\ndef", new JSONTokener("abc\\ndef\"ghi").nextString('"'));
        TestCase.assertEquals("abc\rdef", new JSONTokener("abc\\rdef\"ghi").nextString('"'));
        TestCase.assertEquals("abc\tdef", new JSONTokener("abc\\tdef\"ghi").nextString('"'));
    }

    public void testNextStringUnicodeEscaped() throws JSONException {
        // we're mixing Java escaping like \\ and JavaScript escaping like \\u
        TestCase.assertEquals("abc def", new JSONTokener("abc\\u0020def\"ghi").nextString('"'));
        TestCase.assertEquals("abcU0020def", new JSONTokener("abc\\U0020def\"ghi").nextString('"'));
        // JSON requires 4 hex characters after a unicode escape
        try {
            new JSONTokener("abc\\u002\"").nextString('"');
            TestCase.fail();
        } catch (NumberFormatException e) {
        } catch (JSONException e) {
        }
        try {
            new JSONTokener("abc\\u").nextString('"');
            TestCase.fail();
        } catch (JSONException e) {
        }
        try {
            new JSONTokener("abc\\u    \"").nextString('"');
            TestCase.fail();
        } catch (NumberFormatException e) {
        }
        TestCase.assertEquals("abc\"def", new JSONTokener("abc\\u0022def\"ghi").nextString('"'));
        try {
            new JSONTokener("abc\\u000G\"").nextString('"');
            TestCase.fail();
        } catch (NumberFormatException e) {
        }
    }

    public void testNextStringNonQuote() throws JSONException {
        TestCase.assertEquals("AB", new JSONTokener("ABC").nextString('C'));
        TestCase.assertEquals("ABCD", new JSONTokener("AB\\CDC").nextString('C'));
        TestCase.assertEquals("AB\nC", new JSONTokener("AB\\nCn").nextString('n'));
    }

    public void testNextTo() throws JSONException {
        TestCase.assertEquals("ABC", new JSONTokener("ABCDEFG").nextTo("DHI"));
        TestCase.assertEquals("ABCDEF", new JSONTokener("ABCDEF").nextTo(""));
        JSONTokener tokener = new JSONTokener("ABC\rDEF\nGHI\r\nJKL");
        TestCase.assertEquals("ABC", tokener.nextTo("M"));
        TestCase.assertEquals('\r', tokener.next());
        TestCase.assertEquals("DEF", tokener.nextTo("M"));
        TestCase.assertEquals('\n', tokener.next());
        TestCase.assertEquals("GHI", tokener.nextTo("M"));
        TestCase.assertEquals('\r', tokener.next());
        TestCase.assertEquals('\n', tokener.next());
        TestCase.assertEquals("JKL", tokener.nextTo("M"));
        tokener = new JSONTokener("ABCDEFGHI");
        TestCase.assertEquals("ABC", tokener.nextTo("DEF"));
        TestCase.assertEquals("", tokener.nextTo("DEF"));
        TestCase.assertEquals('D', tokener.next());
        TestCase.assertEquals("", tokener.nextTo("DEF"));
        TestCase.assertEquals('E', tokener.next());
        TestCase.assertEquals("", tokener.nextTo("DEF"));
        TestCase.assertEquals('F', tokener.next());
        TestCase.assertEquals("GHI", tokener.nextTo("DEF"));
        TestCase.assertEquals("", tokener.nextTo("DEF"));
        tokener = new JSONTokener(" \t \fABC \t DEF");
        TestCase.assertEquals("ABC", tokener.nextTo("DEF"));
        TestCase.assertEquals('D', tokener.next());
        tokener = new JSONTokener(" \t \fABC \n DEF");
        TestCase.assertEquals("ABC", tokener.nextTo("\n"));
        TestCase.assertEquals("", tokener.nextTo("\n"));
        tokener = new JSONTokener("");
        try {
            tokener.nextTo(null);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testNextToTrimming() {
        TestCase.assertEquals("ABC", new JSONTokener("\t ABC \tDEF").nextTo("DE"));
        TestCase.assertEquals("ABC", new JSONTokener("\t ABC \tDEF").nextTo('D'));
    }

    public void testNextToTrailing() {
        TestCase.assertEquals("ABC DEF", new JSONTokener("\t ABC DEF \t").nextTo("G"));
        TestCase.assertEquals("ABC DEF", new JSONTokener("\t ABC DEF \t").nextTo('G'));
    }

    public void testNextToDoesntStopOnNull() {
        String message = "nextTo() shouldn\'t stop after \\0 characters";
        JSONTokener tokener = new JSONTokener(" \u0000\t \fABC \n DEF");
        TestCase.assertEquals(message, "ABC", tokener.nextTo("D"));
        TestCase.assertEquals(message, '\n', tokener.next());
        TestCase.assertEquals(message, "", tokener.nextTo("D"));
    }

    public void testNextToConsumesNull() {
        String message = "nextTo shouldn\'t consume \\0.";
        JSONTokener tokener = new JSONTokener("ABC\u0000DEF");
        TestCase.assertEquals(message, "ABC", tokener.nextTo("\u0000"));
        TestCase.assertEquals(message, '\u0000', tokener.next());
        TestCase.assertEquals(message, "DEF", tokener.nextTo("\u0000"));
    }

    public void testSkipPast() {
        JSONTokener tokener = new JSONTokener("ABCDEF");
        tokener.skipPast("ABC");
        TestCase.assertEquals('D', tokener.next());
        tokener.skipPast("EF");
        TestCase.assertEquals('\u0000', tokener.next());
        tokener = new JSONTokener("ABCDEF");
        tokener.skipPast("ABCDEF");
        TestCase.assertEquals('\u0000', tokener.next());
        tokener = new JSONTokener("ABCDEF");
        tokener.skipPast("G");
        TestCase.assertEquals('\u0000', tokener.next());
        tokener = new JSONTokener("ABC\u0000ABC");
        tokener.skipPast("ABC");
        TestCase.assertEquals('\u0000', tokener.next());
        TestCase.assertEquals('A', tokener.next());
        tokener = new JSONTokener("\u0000ABC");
        tokener.skipPast("ABC");
        TestCase.assertEquals('\u0000', tokener.next());
        tokener = new JSONTokener("ABC\nDEF");
        tokener.skipPast("DEF");
        TestCase.assertEquals('\u0000', tokener.next());
        tokener = new JSONTokener("ABC");
        tokener.skipPast("ABCDEF");
        TestCase.assertEquals('\u0000', tokener.next());
        tokener = new JSONTokener("ABCDABCDABCD");
        tokener.skipPast("ABC");
        TestCase.assertEquals('D', tokener.next());
        tokener.skipPast("ABC");
        TestCase.assertEquals('D', tokener.next());
        tokener.skipPast("ABC");
        TestCase.assertEquals('D', tokener.next());
        tokener = new JSONTokener("");
        try {
            tokener.skipPast(null);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testSkipTo() {
        JSONTokener tokener = new JSONTokener("ABCDEF");
        tokener.skipTo('A');
        TestCase.assertEquals('A', tokener.next());
        tokener.skipTo('D');
        TestCase.assertEquals('D', tokener.next());
        tokener.skipTo('G');
        TestCase.assertEquals('E', tokener.next());
        tokener.skipTo('A');
        TestCase.assertEquals('F', tokener.next());
        tokener = new JSONTokener("ABC\nDEF");
        tokener.skipTo('F');
        TestCase.assertEquals('F', tokener.next());
        tokener = new JSONTokener("ABCfDEF");
        tokener.skipTo('F');
        TestCase.assertEquals('F', tokener.next());
        tokener = new JSONTokener("ABC/* DEF */");
        tokener.skipTo('D');
        TestCase.assertEquals('D', tokener.next());
    }

    public void testSkipToStopsOnNull() {
        JSONTokener tokener = new JSONTokener("ABC\u0000DEF");
        tokener.skipTo('F');
        TestCase.assertEquals("skipTo shouldn\'t stop when it sees \'\\0\'", 'F', tokener.next());
    }

    public void testBomIgnoredAsFirstCharacterOfDocument() throws JSONException {
        JSONTokener tokener = new JSONTokener("\ufeff[]");
        JSONArray array = ((JSONArray) (tokener.nextValue()));
        TestCase.assertEquals(0, array.length());
    }

    public void testBomTreatedAsCharacterInRestOfDocument() throws JSONException {
        JSONTokener tokener = new JSONTokener("[\ufeff]");
        JSONArray array = ((JSONArray) (tokener.nextValue()));
        TestCase.assertEquals(1, array.length());
    }

    public void testDehexchar() {
        TestCase.assertEquals(0, JSONTokener.dehexchar('0'));
        TestCase.assertEquals(1, JSONTokener.dehexchar('1'));
        TestCase.assertEquals(2, JSONTokener.dehexchar('2'));
        TestCase.assertEquals(3, JSONTokener.dehexchar('3'));
        TestCase.assertEquals(4, JSONTokener.dehexchar('4'));
        TestCase.assertEquals(5, JSONTokener.dehexchar('5'));
        TestCase.assertEquals(6, JSONTokener.dehexchar('6'));
        TestCase.assertEquals(7, JSONTokener.dehexchar('7'));
        TestCase.assertEquals(8, JSONTokener.dehexchar('8'));
        TestCase.assertEquals(9, JSONTokener.dehexchar('9'));
        TestCase.assertEquals(10, JSONTokener.dehexchar('A'));
        TestCase.assertEquals(11, JSONTokener.dehexchar('B'));
        TestCase.assertEquals(12, JSONTokener.dehexchar('C'));
        TestCase.assertEquals(13, JSONTokener.dehexchar('D'));
        TestCase.assertEquals(14, JSONTokener.dehexchar('E'));
        TestCase.assertEquals(15, JSONTokener.dehexchar('F'));
        TestCase.assertEquals(10, JSONTokener.dehexchar('a'));
        TestCase.assertEquals(11, JSONTokener.dehexchar('b'));
        TestCase.assertEquals(12, JSONTokener.dehexchar('c'));
        TestCase.assertEquals(13, JSONTokener.dehexchar('d'));
        TestCase.assertEquals(14, JSONTokener.dehexchar('e'));
        TestCase.assertEquals(15, JSONTokener.dehexchar('f'));
        for (int c = 0; c <= 65535; c++) {
            if ((((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F'))) || ((c >= 'a') && (c <= 'f'))) {
                continue;
            }
            TestCase.assertEquals(("dehexchar " + c), (-1), JSONTokener.dehexchar(((char) (c))));
        }
    }
}

