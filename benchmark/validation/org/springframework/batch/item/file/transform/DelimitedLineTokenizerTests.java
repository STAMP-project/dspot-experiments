/**
 * Copyright 2006-2014 the original author or authors.
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
package org.springframework.batch.item.file.transform;


import DelimitedLineTokenizer.DEFAULT_QUOTE_CHARACTER;
import org.junit.Assert;
import org.junit.Test;


public class DelimitedLineTokenizerTests {
    private static final String TOKEN_MATCHES = "token equals the expected string";

    private DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

    @Test
    public void testTokenizeRegularUse() {
        FieldSet tokens = tokenizer.tokenize("sfd,\"Well,I have no idea what to do in the afternoon\",sFj, asdf,,as\n");
        Assert.assertEquals(6, tokens.getFieldCount());
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(0).equals("sfd"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(1).equals("Well,I have no idea what to do in the afternoon"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(2).equals("sFj"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(3).equals("asdf"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(4).equals(""));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(5).equals("as"));
        tokens = tokenizer.tokenize("First string,");
        Assert.assertEquals(2, tokens.getFieldCount());
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(0).equals("First string"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(1).equals(""));
    }

    @Test
    public void testBlankString() {
        FieldSet tokens = tokenizer.tokenize("   ");
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(0).equals(""));
    }

    @Test
    public void testEmptyString() {
        FieldSet tokens = tokenizer.tokenize("\"\"");
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(0).equals(""));
    }

    @Test
    public void testInvalidConstructorArgument() {
        try {
            new DelimitedLineTokenizer(String.valueOf(DEFAULT_QUOTE_CHARACTER));
            Assert.fail("Quote character can't be used as delimiter for delimited line tokenizer!");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelimitedLineTokenizer() {
        FieldSet line = tokenizer.tokenize("a,b,c");
        Assert.assertEquals(3, line.getFieldCount());
    }

    @Test
    public void testNames() {
        tokenizer.setNames(new String[]{ "A", "B", "C" });
        FieldSet line = tokenizer.tokenize("a,b,c");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("a", line.readString("A"));
    }

    @Test
    public void testTooFewNames() {
        tokenizer.setNames(new String[]{ "A", "B" });
        try {
            tokenizer.tokenize("a,b,c");
            Assert.fail("Expected IncorrectTokenCountException");
        } catch (IncorrectTokenCountException e) {
            Assert.assertEquals(2, e.getExpectedCount());
            Assert.assertEquals(3, e.getActualCount());
            Assert.assertEquals("a,b,c", e.getInput());
        }
    }

    @Test
    public void testTooFewNamesNotStrict() {
        tokenizer.setNames(new String[]{ "A", "B" });
        tokenizer.setStrict(false);
        FieldSet tokens = tokenizer.tokenize("a,b,c");
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(0).equals("a"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(1).equals("b"));
    }

    @Test
    public void testTooManyNames() {
        tokenizer.setNames(new String[]{ "A", "B", "C", "D" });
        try {
            tokenizer.tokenize("a,b,c");
        } catch (IncorrectTokenCountException e) {
            Assert.assertEquals(4, e.getExpectedCount());
            Assert.assertEquals(3, e.getActualCount());
            Assert.assertEquals("a,b,c", e.getInput());
        }
    }

    @Test
    public void testTooManyNamesNotStrict() {
        tokenizer.setNames(new String[]{ "A", "B", "C", "D", "E" });
        tokenizer.setStrict(false);
        FieldSet tokens = tokenizer.tokenize("a,b,c");
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(0).equals("a"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(1).equals("b"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(2).equals("c"));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(3).equals(""));
        Assert.assertTrue(DelimitedLineTokenizerTests.TOKEN_MATCHES, tokens.readString(4).equals(""));
    }

    @Test
    public void testDelimitedLineTokenizerChar() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(" ");
        FieldSet line = tokenizer.tokenize("a b c");
        Assert.assertEquals(3, line.getFieldCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimitedLineTokenizerNullDelimiter() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(null);
        tokenizer.tokenize("a b c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimitedLineTokenizerEmptyString() throws Exception {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("");
        tokenizer.afterPropertiesSet();
        tokenizer.tokenize("a b c");
    }

    @Test
    public void testDelimitedLineTokenizerString() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(" b ");
        FieldSet line = tokenizer.tokenize("a b c");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("a", line.readString(0));
        Assert.assertEquals("c", line.readString(1));
    }

    @Test
    public void testDelimitedLineTokenizerStringBeginningOfLine() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(" | ");
        FieldSet line = tokenizer.tokenize(" | a | b");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("", line.readString(0));
        Assert.assertEquals("a", line.readString(1));
        Assert.assertEquals("b", line.readString(2));
    }

    @Test
    public void testDelimitedLineTokenizerStringEndOfLine() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(" | ");
        FieldSet line = tokenizer.tokenize("a | b | ");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("a", line.readString(0));
        Assert.assertEquals("b", line.readString(1));
        Assert.assertEquals("", line.readString(2));
    }

    @Test
    public void testDelimitedLineTokenizerStringsOverlap() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(" | ");
        FieldSet line = tokenizer.tokenize("a | | | b");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("a", line.readString(0));
        Assert.assertEquals("|", line.readString(1));
        Assert.assertEquals("b", line.readString(2));
    }

    @Test
    public void testDelimitedLineTokenizerStringsOverlapWithoutSeparation() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer(" | ");
        FieldSet line = tokenizer.tokenize("a | | b");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("a", line.readString(0));
        Assert.assertEquals("| b", line.readString(1));
    }

    @Test
    public void testDelimitedLineTokenizerNewlineToken() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer("\n");
        FieldSet line = tokenizer.tokenize("a b\n c");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("a b", line.readString(0));
        Assert.assertEquals("c", line.readString(1));
    }

    @Test
    public void testDelimitedLineTokenizerWrappedToken() {
        AbstractLineTokenizer tokenizer = new DelimitedLineTokenizer("\nrap");
        FieldSet line = tokenizer.tokenize("a b\nrap c");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("a b", line.readString(0));
        Assert.assertEquals("c", line.readString(1));
    }

    @Test
    public void testTokenizeWithQuotes() {
        FieldSet line = tokenizer.tokenize("a,b,\"c\"");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("c", line.readString(2));
    }

    @Test
    public void testTokenizeWithNotDefaultQuotes() {
        tokenizer.setQuoteCharacter('\'');
        FieldSet line = tokenizer.tokenize("a,b,'c'");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("c", line.readString(2));
    }

    @Test
    public void testTokenizeWithEscapedQuotes() {
        FieldSet line = tokenizer.tokenize("a,\"\"b,\"\"\"c\"");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("\"\"b", line.readString(1));
        Assert.assertEquals("\"c", line.readString(2));
    }

    @Test
    public void testTokenizeWithUnclosedQuotes() {
        tokenizer.setQuoteCharacter('\'');
        FieldSet line = tokenizer.tokenize("a,\"b,c");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("\"b", line.readString(1));
        Assert.assertEquals("c", line.readString(2));
    }

    @Test
    public void testTokenizeWithSpaceInField() {
        FieldSet line = tokenizer.tokenize("a,b ,c");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("b ", line.readRawString(1));
    }

    @Test
    public void testTokenizeWithSpaceAtEnd() {
        FieldSet line = tokenizer.tokenize("a,b,c ");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("c ", line.readRawString(2));
    }

    @Test
    public void testTokenizeWithQuoteAndSpaceAtEnd() {
        FieldSet line = tokenizer.tokenize("a,b,\"c\" ");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("c", line.readString(2));
    }

    @Test
    public void testTokenizeWithQuoteAndSpaceBeforeDelimiter() {
        FieldSet line = tokenizer.tokenize("a,\"b\" ,c");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("b", line.readString(1));
    }

    @Test
    public void testTokenizeWithDelimiterAtEnd() {
        FieldSet line = tokenizer.tokenize("a,b,c,");
        Assert.assertEquals(4, line.getFieldCount());
        Assert.assertEquals("c", line.readString(2));
        Assert.assertEquals("", line.readString(3));
    }

    @Test
    public void testEmptyLine() throws Exception {
        FieldSet line = tokenizer.tokenize("");
        Assert.assertEquals(0, line.getFieldCount());
    }

    @Test
    public void testEmptyLineWithNames() {
        tokenizer.setNames(new String[]{ "A", "B" });
        try {
            tokenizer.tokenize("");
        } catch (IncorrectTokenCountException ex) {
            Assert.assertEquals(2, ex.getExpectedCount());
            Assert.assertEquals(0, ex.getActualCount());
            Assert.assertEquals("", ex.getInput());
        }
    }

    @Test
    public void testWhitespaceLine() throws Exception {
        FieldSet line = tokenizer.tokenize("  ");
        // whitespace counts as text
        Assert.assertEquals(1, line.getFieldCount());
    }

    @Test
    public void testNullLine() throws Exception {
        FieldSet line = tokenizer.tokenize(null);
        // null doesn't...
        Assert.assertEquals(0, line.getFieldCount());
    }

    @Test
    public void testMultiLineField() throws Exception {
        FieldSet line = tokenizer.tokenize("a,b,c\nrap");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("c\nrap", line.readString(2));
    }

    @Test
    public void testMultiLineFieldWithQuotes() throws Exception {
        FieldSet line = tokenizer.tokenize("a,b,\"c\nrap\"");
        Assert.assertEquals(3, line.getFieldCount());
        Assert.assertEquals("c\nrap", line.readString(2));
    }

    @Test
    public void testTokenizeWithQuotesEmptyValue() {
        FieldSet line = tokenizer.tokenize("\"a\",\"b\",\"\",\"d\"");
        Assert.assertEquals(4, line.getFieldCount());
        Assert.assertEquals("", line.readString(2));
    }

    @Test
    public void testTokenizeWithIncludedFields() {
        tokenizer.setIncludedFields(new int[]{ 1, 2 });
        FieldSet line = tokenizer.tokenize("\"a\",\"b\",\"c\",\"d\"");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("c", line.readString(1));
    }

    @Test
    public void testTokenizeWithIncludedFieldsAndEmptyEnd() {
        tokenizer.setIncludedFields(new int[]{ 1, 3 });
        FieldSet line = tokenizer.tokenize("\"a\",\"b\",\"c\",");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("", line.readString(1));
    }

    @Test
    public void testTokenizeWithIncludedFieldsAndNames() {
        tokenizer.setIncludedFields(new int[]{ 1, 2 });
        tokenizer.setNames(new String[]{ "foo", "bar" });
        FieldSet line = tokenizer.tokenize("\"a\",\"b\",\"c\",\"d\"");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("c", line.readString("bar"));
    }

    @Test(expected = IncorrectTokenCountException.class)
    public void testTokenizeWithIncludedFieldsAndTooFewNames() {
        tokenizer.setIncludedFields(new int[]{ 1, 2 });
        tokenizer.setNames(new String[]{ "foo" });
        FieldSet line = tokenizer.tokenize("\"a\",\"b\",\"c\",\"d\"");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("c", line.readString("bar"));
    }

    @Test(expected = IncorrectTokenCountException.class)
    public void testTokenizeWithIncludedFieldsAndTooManyNames() {
        tokenizer.setIncludedFields(new int[]{ 1, 2 });
        tokenizer.setNames(new String[]{ "foo", "bar", "spam" });
        FieldSet line = tokenizer.tokenize("\"a\",\"b\",\"c\",\"d\"");
        Assert.assertEquals(2, line.getFieldCount());
        Assert.assertEquals("c", line.readString("bar"));
    }

    @Test
    public void testTokenizeOverMultipleLines() {
        tokenizer = new DelimitedLineTokenizer(";");
        FieldSet line = tokenizer.tokenize("value1;\"value2\nvalue2cont\";value3;value4");
        Assert.assertEquals(4, line.getFieldCount());
        Assert.assertEquals("value2\nvalue2cont", line.readString(1));
    }
}

