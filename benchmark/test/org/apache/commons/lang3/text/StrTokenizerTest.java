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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit test for Tokenizer.
 */
@Deprecated
public class StrTokenizerTest {
    private static final String CSV_SIMPLE_FIXTURE = "A,b,c";

    private static final String TSV_SIMPLE_FIXTURE = "A\tb\tc";

    // -----------------------------------------------------------------------
    @Test
    public void test1() {
        final String input = "a;b;c;\"d;\"\"e\";f; ; ;  ";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterChar(';');
        tok.setQuoteChar('"');
        tok.setIgnoredMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", "c", "d;\"e", "f", "", "", "" };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void test2() {
        final String input = "a;b;c ;\"d;\"\"e\";f; ; ;";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterChar(';');
        tok.setQuoteChar('"');
        tok.setIgnoredMatcher(StrMatcher.noneMatcher());
        tok.setIgnoreEmptyTokens(false);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", "c ", "d;\"e", "f", " ", " ", "" };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void test3() {
        final String input = "a;b; c;\"d;\"\"e\";f; ; ;";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterChar(';');
        tok.setQuoteChar('"');
        tok.setIgnoredMatcher(StrMatcher.noneMatcher());
        tok.setIgnoreEmptyTokens(false);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", " c", "d;\"e", "f", " ", " ", "" };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void test4() {
        final String input = "a;b; c;\"d;\"\"e\";f; ; ;";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterChar(';');
        tok.setQuoteChar('"');
        tok.setIgnoredMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(true);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", "c", "d;\"e", "f" };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void test5() {
        final String input = "a;b; c;\"d;\"\"e\";f; ; ;";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterChar(';');
        tok.setQuoteChar('"');
        tok.setIgnoredMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", "c", "d;\"e", "f", null, null, null };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void test6() {
        final String input = "a;b; c;\"d;\"\"e\";f; ; ;";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterChar(';');
        tok.setQuoteChar('"');
        tok.setIgnoredMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        // tok.setTreatingEmptyAsNull(true);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", " c", "d;\"e", "f", null, null, null };
        int nextCount = 0;
        while (tok.hasNext()) {
            tok.next();
            nextCount++;
        } 
        int prevCount = 0;
        while (tok.hasPrevious()) {
            tok.previous();
            prevCount++;
        } 
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        Assertions.assertEquals(nextCount, expected.length, ("could not cycle through entire token list" + " using the 'hasNext' and 'next' methods"));
        Assertions.assertEquals(prevCount, expected.length, ("could not cycle through entire token list" + " using the 'hasPrevious' and 'previous' methods"));
    }

    @Test
    public void test7() {
        final String input = "a   b c \"d e\" f ";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterMatcher(StrMatcher.spaceMatcher());
        tok.setQuoteMatcher(StrMatcher.doubleQuoteMatcher());
        tok.setIgnoredMatcher(StrMatcher.noneMatcher());
        tok.setIgnoreEmptyTokens(false);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "", "", "b", "c", "d e", "f", "" };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void test8() {
        final String input = "a   b c \"d e\" f ";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setDelimiterMatcher(StrMatcher.spaceMatcher());
        tok.setQuoteMatcher(StrMatcher.doubleQuoteMatcher());
        tok.setIgnoredMatcher(StrMatcher.noneMatcher());
        tok.setIgnoreEmptyTokens(true);
        final String[] tokens = tok.getTokenArray();
        final String[] expected = new String[]{ "a", "b", "c", "d e", "f" };
        Assertions.assertEquals(expected.length, tokens.length, ArrayUtils.toString(tokens));
        for (int i = 0; i < (expected.length); i++) {
            Assertions.assertEquals(expected[i], tokens[i], (((((("token[" + i) + "] was '") + (tokens[i])) + "' but was expected to be '") + (expected[i])) + "'"));
        }
    }

    @Test
    public void testBasic1() {
        final String input = "a  b c";
        final StrTokenizer tok = new StrTokenizer(input);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasic2() {
        final String input = "a \nb\fc";
        final StrTokenizer tok = new StrTokenizer(input);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasic3() {
        final String input = "a \nb\u0001\fc";
        final StrTokenizer tok = new StrTokenizer(input);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b\u0001", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasic4() {
        final String input = "a \"b\" c";
        final StrTokenizer tok = new StrTokenizer(input);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("\"b\"", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasic5() {
        final String input = "a:b':c";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b'", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicDelim1() {
        final String input = "a:b:c";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicDelim2() {
        final String input = "a:b:c";
        final StrTokenizer tok = new StrTokenizer(input, ',');
        Assertions.assertEquals("a:b:c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicEmpty1() {
        final String input = "a  b c";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setIgnoreEmptyTokens(false);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicEmpty2() {
        final String input = "a  b c";
        final StrTokenizer tok = new StrTokenizer(input);
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted1() {
        final String input = "a 'b' c";
        final StrTokenizer tok = new StrTokenizer(input, ' ', '\'');
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted2() {
        final String input = "a:'b':";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted3() {
        final String input = "a:'b''c'";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b'c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted4() {
        final String input = "a: 'b' 'c' :d";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b c", tok.next());
        Assertions.assertEquals("d", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted5() {
        final String input = "a: 'b'x'c' :d";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("bxc", tok.next());
        Assertions.assertEquals("d", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted6() {
        final String input = "a:\'b\'\"c\':d";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setQuoteMatcher(StrMatcher.quoteMatcher());
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b\"c:d", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuoted7() {
        final String input = "a:\"There\'s a reason here\":b";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setQuoteMatcher(StrMatcher.quoteMatcher());
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("There's a reason here", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicQuotedTrimmed1() {
        final String input = "a: 'b' :";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicTrimmed1() {
        final String input = "a: b :  ";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicTrimmed2() {
        final String input = "a:  b  :";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setTrimmerMatcher(StrMatcher.stringMatcher("  "));
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicIgnoreTrimmed1() {
        final String input = "a: bIGNOREc : ";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setIgnoredMatcher(StrMatcher.stringMatcher("IGNORE"));
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("bc", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicIgnoreTrimmed2() {
        final String input = "IGNOREaIGNORE: IGNORE bIGNOREc IGNORE : IGNORE ";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setIgnoredMatcher(StrMatcher.stringMatcher("IGNORE"));
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("bc", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicIgnoreTrimmed3() {
        final String input = "IGNOREaIGNORE: IGNORE bIGNOREc IGNORE : IGNORE ";
        final StrTokenizer tok = new StrTokenizer(input, ':');
        tok.setIgnoredMatcher(StrMatcher.stringMatcher("IGNORE"));
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("  bc  ", tok.next());
        Assertions.assertEquals("  ", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    @Test
    public void testBasicIgnoreTrimmed4() {
        final String input = "IGNOREaIGNORE: IGNORE 'bIGNOREc'IGNORE'd' IGNORE : IGNORE ";
        final StrTokenizer tok = new StrTokenizer(input, ':', '\'');
        tok.setIgnoredMatcher(StrMatcher.stringMatcher("IGNORE"));
        tok.setTrimmerMatcher(StrMatcher.trimMatcher());
        tok.setIgnoreEmptyTokens(false);
        tok.setEmptyTokenAsNull(true);
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("bIGNOREcd", tok.next());
        Assertions.assertNull(tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testListArray() {
        final String input = "a  b c";
        final StrTokenizer tok = new StrTokenizer(input);
        final String[] array = tok.getTokenArray();
        final List<?> list = tok.getTokenList();
        Assertions.assertEquals(Arrays.asList(array), list);
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void testCSVEmpty() {
        this.testEmpty(StrTokenizer.getCSVInstance());
        this.testEmpty(StrTokenizer.getCSVInstance(""));
    }

    @Test
    public void testCSVSimple() {
        this.testCSV(StrTokenizerTest.CSV_SIMPLE_FIXTURE);
    }

    @Test
    public void testCSVSimpleNeedsTrim() {
        this.testCSV(("   " + (StrTokenizerTest.CSV_SIMPLE_FIXTURE)));
        this.testCSV(("   \n\t  " + (StrTokenizerTest.CSV_SIMPLE_FIXTURE)));
        this.testCSV((("   \n  " + (StrTokenizerTest.CSV_SIMPLE_FIXTURE)) + "\n\n\r"));
    }

    @Test
    public void testGetContent() {
        final String input = "a   b c \"d e\" f ";
        StrTokenizer tok = new StrTokenizer(input);
        Assertions.assertEquals(input, tok.getContent());
        tok = new StrTokenizer(input.toCharArray());
        Assertions.assertEquals(input, tok.getContent());
        tok = new StrTokenizer();
        Assertions.assertNull(tok.getContent());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testChaining() {
        final StrTokenizer tok = new StrTokenizer();
        Assertions.assertEquals(tok, tok.reset());
        Assertions.assertEquals(tok, tok.reset(""));
        Assertions.assertEquals(tok, tok.reset(new char[0]));
        Assertions.assertEquals(tok, tok.setDelimiterChar(' '));
        Assertions.assertEquals(tok, tok.setDelimiterString(" "));
        Assertions.assertEquals(tok, tok.setDelimiterMatcher(null));
        Assertions.assertEquals(tok, tok.setQuoteChar(' '));
        Assertions.assertEquals(tok, tok.setQuoteMatcher(null));
        Assertions.assertEquals(tok, tok.setIgnoredChar(' '));
        Assertions.assertEquals(tok, tok.setIgnoredMatcher(null));
        Assertions.assertEquals(tok, tok.setTrimmerMatcher(null));
        Assertions.assertEquals(tok, tok.setEmptyTokenAsNull(false));
        Assertions.assertEquals(tok, tok.setIgnoreEmptyTokens(false));
    }

    /**
     * Tests that the {@link StrTokenizer#clone()} clone method catches {@link CloneNotSupportedException} and returns
     * <code>null</code>.
     */
    @Test
    public void testCloneNotSupportedException() {
        final Object notCloned = new StrTokenizer() {
            @Override
            Object cloneReset() throws CloneNotSupportedException {
                throw new CloneNotSupportedException("test");
            }
        }.clone();
        Assertions.assertNull(notCloned);
    }

    @Test
    public void testCloneNull() {
        final StrTokenizer tokenizer = new StrTokenizer(((char[]) (null)));
        // Start sanity check
        Assertions.assertNull(tokenizer.nextToken());
        tokenizer.reset();
        Assertions.assertNull(tokenizer.nextToken());
        // End sanity check
        final StrTokenizer clonedTokenizer = ((StrTokenizer) (tokenizer.clone()));
        tokenizer.reset();
        Assertions.assertNull(tokenizer.nextToken());
        Assertions.assertNull(clonedTokenizer.nextToken());
    }

    @Test
    public void testCloneReset() {
        final char[] input = new char[]{ 'a' };
        final StrTokenizer tokenizer = new StrTokenizer(input);
        // Start sanity check
        Assertions.assertEquals("a", tokenizer.nextToken());
        tokenizer.reset(input);
        Assertions.assertEquals("a", tokenizer.nextToken());
        // End sanity check
        final StrTokenizer clonedTokenizer = ((StrTokenizer) (tokenizer.clone()));
        input[0] = 'b';
        tokenizer.reset(input);
        Assertions.assertEquals("b", tokenizer.nextToken());
        Assertions.assertEquals("a", clonedTokenizer.nextToken());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_String() {
        StrTokenizer tok = new StrTokenizer("a b");
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer("");
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(((String) (null)));
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_String_char() {
        StrTokenizer tok = new StrTokenizer("a b", ' ');
        Assertions.assertEquals(1, tok.getDelimiterMatcher().isMatch(" ".toCharArray(), 0, 0, 1));
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer("", ' ');
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(((String) (null)), ' ');
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_String_char_char() {
        StrTokenizer tok = new StrTokenizer("a b", ' ', '"');
        Assertions.assertEquals(1, tok.getDelimiterMatcher().isMatch(" ".toCharArray(), 0, 0, 1));
        Assertions.assertEquals(1, tok.getQuoteMatcher().isMatch("\"".toCharArray(), 0, 0, 1));
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer("", ' ', '"');
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(((String) (null)), ' ', '"');
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_charArray() {
        StrTokenizer tok = new StrTokenizer("a b".toCharArray());
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(new char[0]);
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(((char[]) (null)));
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_charArray_char() {
        StrTokenizer tok = new StrTokenizer("a b".toCharArray(), ' ');
        Assertions.assertEquals(1, tok.getDelimiterMatcher().isMatch(" ".toCharArray(), 0, 0, 1));
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(new char[0], ' ');
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(((char[]) (null)), ' ');
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_charArray_char_char() {
        StrTokenizer tok = new StrTokenizer("a b".toCharArray(), ' ', '"');
        Assertions.assertEquals(1, tok.getDelimiterMatcher().isMatch(" ".toCharArray(), 0, 0, 1));
        Assertions.assertEquals(1, tok.getQuoteMatcher().isMatch("\"".toCharArray(), 0, 0, 1));
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(new char[0], ' ', '"');
        Assertions.assertFalse(tok.hasNext());
        tok = new StrTokenizer(((char[]) (null)), ' ', '"');
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReset() {
        final StrTokenizer tok = new StrTokenizer("a b c");
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok.reset();
        Assertions.assertEquals("a", tok.next());
        Assertions.assertEquals("b", tok.next());
        Assertions.assertEquals("c", tok.next());
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReset_String() {
        final StrTokenizer tok = new StrTokenizer("x x x");
        tok.reset("d e");
        Assertions.assertEquals("d", tok.next());
        Assertions.assertEquals("e", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok.reset(((String) (null)));
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReset_charArray() {
        final StrTokenizer tok = new StrTokenizer("x x x");
        final char[] array = new char[]{ 'a', 'b', 'c' };
        tok.reset(array);
        Assertions.assertEquals("abc", tok.next());
        Assertions.assertFalse(tok.hasNext());
        tok.reset(((char[]) (null)));
        Assertions.assertFalse(tok.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTSV() {
        this.testXSVAbc(StrTokenizer.getTSVInstance(StrTokenizerTest.TSV_SIMPLE_FIXTURE));
        this.testXSVAbc(StrTokenizer.getTSVInstance(StrTokenizerTest.TSV_SIMPLE_FIXTURE.toCharArray()));
    }

    @Test
    public void testTSVEmpty() {
        this.testEmpty(StrTokenizer.getTSVInstance());
        this.testEmpty(StrTokenizer.getTSVInstance(""));
    }

    @Test
    public void testIteration() {
        final StrTokenizer tkn = new StrTokenizer("a b c");
        Assertions.assertFalse(tkn.hasPrevious());
        Assertions.assertThrows(NoSuchElementException.class, tkn::previous);
        Assertions.assertTrue(tkn.hasNext());
        Assertions.assertEquals("a", tkn.next());
        Assertions.assertThrows(UnsupportedOperationException.class, tkn::remove);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> tkn.set("x"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> tkn.add("y"));
        Assertions.assertTrue(tkn.hasPrevious());
        Assertions.assertTrue(tkn.hasNext());
        Assertions.assertEquals("b", tkn.next());
        Assertions.assertTrue(tkn.hasPrevious());
        Assertions.assertTrue(tkn.hasNext());
        Assertions.assertEquals("c", tkn.next());
        Assertions.assertTrue(tkn.hasPrevious());
        Assertions.assertFalse(tkn.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, tkn::next);
        Assertions.assertTrue(tkn.hasPrevious());
        Assertions.assertFalse(tkn.hasNext());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTokenizeSubclassInputChange() {
        final StrTokenizer tkn = new StrTokenizer("a b c d e") {
            @Override
            protected List<String> tokenize(final char[] chars, final int offset, final int count) {
                return super.tokenize("w x y z".toCharArray(), 2, 5);
            }
        };
        Assertions.assertEquals("x", tkn.next());
        Assertions.assertEquals("y", tkn.next());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTokenizeSubclassOutputChange() {
        final StrTokenizer tkn = new StrTokenizer("a b c") {
            @Override
            protected List<String> tokenize(final char[] chars, final int offset, final int count) {
                final List<String> list = super.tokenize(chars, offset, count);
                Collections.reverse(list);
                return list;
            }
        };
        Assertions.assertEquals("c", tkn.next());
        Assertions.assertEquals("b", tkn.next());
        Assertions.assertEquals("a", tkn.next());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToString() {
        final StrTokenizer tkn = new StrTokenizer("a b c d e");
        Assertions.assertEquals("StrTokenizer[not tokenized yet]", tkn.toString());
        tkn.next();
        Assertions.assertEquals("StrTokenizer[a, b, c, d, e]", tkn.toString());
    }
}

