/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier;


import CommandSyntaxException.BUILT_IN_EXCEPTIONS;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StringReaderTest {
    @Test
    public void canRead() throws Exception {
        final StringReader reader = new StringReader("abc");
        Assert.assertThat(reader.canRead(), Matchers.is(true));
        reader.skip();// 'a'

        Assert.assertThat(reader.canRead(), Matchers.is(true));
        reader.skip();// 'b'

        Assert.assertThat(reader.canRead(), Matchers.is(true));
        reader.skip();// 'c'

        Assert.assertThat(reader.canRead(), Matchers.is(false));
    }

    @Test
    public void getRemainingLength() throws Exception {
        final StringReader reader = new StringReader("abc");
        Assert.assertThat(reader.getRemainingLength(), Matchers.is(3));
        reader.setCursor(1);
        Assert.assertThat(reader.getRemainingLength(), Matchers.is(2));
        reader.setCursor(2);
        Assert.assertThat(reader.getRemainingLength(), Matchers.is(1));
        reader.setCursor(3);
        Assert.assertThat(reader.getRemainingLength(), Matchers.is(0));
    }

    @Test
    public void canRead_length() throws Exception {
        final StringReader reader = new StringReader("abc");
        Assert.assertThat(reader.canRead(1), Matchers.is(true));
        Assert.assertThat(reader.canRead(2), Matchers.is(true));
        Assert.assertThat(reader.canRead(3), Matchers.is(true));
        Assert.assertThat(reader.canRead(4), Matchers.is(false));
        Assert.assertThat(reader.canRead(5), Matchers.is(false));
    }

    @Test
    public void peek() throws Exception {
        final StringReader reader = new StringReader("abc");
        Assert.assertThat(reader.peek(), Matchers.is('a'));
        Assert.assertThat(reader.getCursor(), Matchers.is(0));
        reader.setCursor(2);
        Assert.assertThat(reader.peek(), Matchers.is('c'));
        Assert.assertThat(reader.getCursor(), Matchers.is(2));
    }

    @Test
    public void peek_length() throws Exception {
        final StringReader reader = new StringReader("abc");
        Assert.assertThat(reader.peek(0), Matchers.is('a'));
        Assert.assertThat(reader.peek(2), Matchers.is('c'));
        Assert.assertThat(reader.getCursor(), Matchers.is(0));
        reader.setCursor(1);
        Assert.assertThat(reader.peek(1), Matchers.is('c'));
        Assert.assertThat(reader.getCursor(), Matchers.is(1));
    }

    @Test
    public void read() throws Exception {
        final StringReader reader = new StringReader("abc");
        Assert.assertThat(reader.read(), Matchers.is('a'));
        Assert.assertThat(reader.read(), Matchers.is('b'));
        Assert.assertThat(reader.read(), Matchers.is('c'));
        Assert.assertThat(reader.getCursor(), Matchers.is(3));
    }

    @Test
    public void skip() throws Exception {
        final StringReader reader = new StringReader("abc");
        reader.skip();
        Assert.assertThat(reader.getCursor(), Matchers.is(1));
    }

    @Test
    public void getRemaining() throws Exception {
        final StringReader reader = new StringReader("Hello!");
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("Hello!"));
        reader.setCursor(3);
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("lo!"));
        reader.setCursor(6);
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void getRead() throws Exception {
        final StringReader reader = new StringReader("Hello!");
        Assert.assertThat(reader.getRead(), Matchers.equalTo(""));
        reader.setCursor(3);
        Assert.assertThat(reader.getRead(), Matchers.equalTo("Hel"));
        reader.setCursor(6);
        Assert.assertThat(reader.getRead(), Matchers.equalTo("Hello!"));
    }

    @Test
    public void skipWhitespace_none() throws Exception {
        final StringReader reader = new StringReader("Hello!");
        reader.skipWhitespace();
        Assert.assertThat(reader.getCursor(), Matchers.is(0));
    }

    @Test
    public void skipWhitespace_mixed() throws Exception {
        final StringReader reader = new StringReader(" \t \t\nHello!");
        reader.skipWhitespace();
        Assert.assertThat(reader.getCursor(), Matchers.is(5));
    }

    @Test
    public void skipWhitespace_empty() throws Exception {
        final StringReader reader = new StringReader("");
        reader.skipWhitespace();
        Assert.assertThat(reader.getCursor(), Matchers.is(0));
    }

    @Test
    public void readUnquotedString() throws Exception {
        final StringReader reader = new StringReader("hello world");
        Assert.assertThat(reader.readUnquotedString(), Matchers.equalTo("hello"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("hello"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" world"));
    }

    @Test
    public void readUnquotedString_empty() throws Exception {
        final StringReader reader = new StringReader("");
        Assert.assertThat(reader.readUnquotedString(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readUnquotedString_empty_withRemaining() throws Exception {
        final StringReader reader = new StringReader(" hello world");
        Assert.assertThat(reader.readUnquotedString(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" hello world"));
    }

    @Test
    public void readQuotedString() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello world"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"hello world\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readSingleQuotedString() throws Exception {
        final StringReader reader = new StringReader("'hello world'");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello world"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("'hello world'"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readMixedQuotedString_doubleInsideSingle() throws Exception {
        final StringReader reader = new StringReader("\'hello \"world\"\'");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello \"world\""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\'hello \"world\"\'"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readMixedQuotedString_singleInsideDouble() throws Exception {
        final StringReader reader = new StringReader("\"hello \'world\'\"");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello 'world'"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"hello \'world\'\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readQuotedString_empty() throws Exception {
        final StringReader reader = new StringReader("");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readQuotedString_emptyQuoted() throws Exception {
        final StringReader reader = new StringReader("\"\"");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readQuotedString_emptyQuoted_withRemaining() throws Exception {
        final StringReader reader = new StringReader("\"\" hello world");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo(""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" hello world"));
    }

    @Test
    public void readQuotedString_withEscapedQuote() throws Exception {
        final StringReader reader = new StringReader("\"hello \\\"world\\\"\"");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello \"world\""));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"hello \\\"world\\\"\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readQuotedString_withEscapedEscapes() throws Exception {
        final StringReader reader = new StringReader("\"\\\\o/\"");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("\\o/"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"\\\\o/\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readQuotedString_withRemaining() throws Exception {
        final StringReader reader = new StringReader("\"hello world\" foo bar");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello world"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"hello world\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" foo bar"));
    }

    @Test
    public void readQuotedString_withImmediateRemaining() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"foo bar");
        Assert.assertThat(reader.readQuotedString(), Matchers.equalTo("hello world"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"hello world\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("foo bar"));
    }

    @Test
    public void readQuotedString_noOpen() throws Exception {
        try {
            new StringReader("hello world\"").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readQuotedString_noClose() throws Exception {
        try {
            new StringReader("\"hello world").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote()));
            Assert.assertThat(ex.getCursor(), Matchers.is(12));
        }
    }

    @Test
    public void readQuotedString_invalidEscape() throws Exception {
        try {
            new StringReader("\"hello\\nworld\"").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidEscape()));
            Assert.assertThat(ex.getCursor(), Matchers.is(7));
        }
    }

    @Test
    public void readQuotedString_invalidQuoteEscape() throws Exception {
        try {
            new StringReader("\'hello\\\"\'world").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidEscape()));
            Assert.assertThat(ex.getCursor(), Matchers.is(7));
        }
    }

    @Test
    public void readString_noQuotes() throws Exception {
        final StringReader reader = new StringReader("hello world");
        Assert.assertThat(reader.readString(), Matchers.equalTo("hello"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("hello"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" world"));
    }

    @Test
    public void readString_singleQuotes() throws Exception {
        final StringReader reader = new StringReader("'hello world'");
        Assert.assertThat(reader.readString(), Matchers.equalTo("hello world"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("'hello world'"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readString_doubleQuotes() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"");
        Assert.assertThat(reader.readString(), Matchers.equalTo("hello world"));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("\"hello world\""));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readInt() throws Exception {
        final StringReader reader = new StringReader("1234567890");
        Assert.assertThat(reader.readInt(), Matchers.is(1234567890));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readInt_negative() throws Exception {
        final StringReader reader = new StringReader("-1234567890");
        Assert.assertThat(reader.readInt(), Matchers.is((-1234567890)));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("-1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readInt_invalid() throws Exception {
        try {
            new StringReader("12.34").readInt();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidInt()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readInt_none() throws Exception {
        try {
            new StringReader("").readInt();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedInt()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readInt_withRemaining() throws Exception {
        final StringReader reader = new StringReader("1234567890 foo bar");
        Assert.assertThat(reader.readInt(), Matchers.is(1234567890));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" foo bar"));
    }

    @Test
    public void readInt_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("1234567890foo bar");
        Assert.assertThat(reader.readInt(), Matchers.is(1234567890));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("foo bar"));
    }

    @Test
    public void readLong() throws Exception {
        final StringReader reader = new StringReader("1234567890");
        Assert.assertThat(reader.readLong(), Matchers.is(1234567890L));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readLong_negative() throws Exception {
        final StringReader reader = new StringReader("-1234567890");
        Assert.assertThat(reader.readLong(), Matchers.is((-1234567890L)));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("-1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readLong_invalid() throws Exception {
        try {
            new StringReader("12.34").readLong();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidLong()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readLong_none() throws Exception {
        try {
            new StringReader("").readLong();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedLong()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readLong_withRemaining() throws Exception {
        final StringReader reader = new StringReader("1234567890 foo bar");
        Assert.assertThat(reader.readLong(), Matchers.is(1234567890L));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" foo bar"));
    }

    @Test
    public void readLong_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("1234567890foo bar");
        Assert.assertThat(reader.readLong(), Matchers.is(1234567890L));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("1234567890"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("foo bar"));
    }

    @Test
    public void readDouble() throws Exception {
        final StringReader reader = new StringReader("123");
        Assert.assertThat(reader.readDouble(), Matchers.is(123.0));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("123"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readDouble_withDecimal() throws Exception {
        final StringReader reader = new StringReader("12.34");
        Assert.assertThat(reader.readDouble(), Matchers.is(12.34));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("12.34"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readDouble_negative() throws Exception {
        final StringReader reader = new StringReader("-123");
        Assert.assertThat(reader.readDouble(), Matchers.is((-123.0)));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("-123"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readDouble_invalid() throws Exception {
        try {
            new StringReader("12.34.56").readDouble();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidDouble()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readDouble_none() throws Exception {
        try {
            new StringReader("").readDouble();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedDouble()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readDouble_withRemaining() throws Exception {
        final StringReader reader = new StringReader("12.34 foo bar");
        Assert.assertThat(reader.readDouble(), Matchers.is(12.34));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("12.34"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" foo bar"));
    }

    @Test
    public void readDouble_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("12.34foo bar");
        Assert.assertThat(reader.readDouble(), Matchers.is(12.34));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("12.34"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("foo bar"));
    }

    @Test
    public void readFloat() throws Exception {
        final StringReader reader = new StringReader("123");
        Assert.assertThat(reader.readFloat(), Matchers.is(123.0F));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("123"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readFloat_withDecimal() throws Exception {
        final StringReader reader = new StringReader("12.34");
        Assert.assertThat(reader.readFloat(), Matchers.is(12.34F));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("12.34"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readFloat_negative() throws Exception {
        final StringReader reader = new StringReader("-123");
        Assert.assertThat(reader.readFloat(), Matchers.is((-123.0F)));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("-123"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void readFloat_invalid() throws Exception {
        try {
            new StringReader("12.34.56").readFloat();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidFloat()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readFloat_none() throws Exception {
        try {
            new StringReader("").readFloat();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedFloat()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readFloat_withRemaining() throws Exception {
        final StringReader reader = new StringReader("12.34 foo bar");
        Assert.assertThat(reader.readFloat(), Matchers.is(12.34F));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("12.34"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" foo bar"));
    }

    @Test
    public void readFloat_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("12.34foo bar");
        Assert.assertThat(reader.readFloat(), Matchers.is(12.34F));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("12.34"));
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo("foo bar"));
    }

    @Test
    public void expect_correct() throws Exception {
        final StringReader reader = new StringReader("abc");
        reader.expect('a');
        Assert.assertThat(reader.getCursor(), Matchers.is(1));
    }

    @Test
    public void expect_incorrect() throws Exception {
        final StringReader reader = new StringReader("bcd");
        try {
            reader.expect('a');
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedSymbol()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void expect_none() throws Exception {
        final StringReader reader = new StringReader("");
        try {
            reader.expect('a');
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedSymbol()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readBoolean_correct() throws Exception {
        final StringReader reader = new StringReader("true");
        Assert.assertThat(reader.readBoolean(), Matchers.is(true));
        Assert.assertThat(reader.getRead(), Matchers.equalTo("true"));
    }

    @Test
    public void readBoolean_incorrect() throws Exception {
        final StringReader reader = new StringReader("tuesday");
        try {
            reader.readBoolean();
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerInvalidBool()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void readBoolean_none() throws Exception {
        final StringReader reader = new StringReader("");
        try {
            reader.readBoolean();
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedBool()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }
}

