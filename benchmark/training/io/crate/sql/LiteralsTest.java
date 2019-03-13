/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.sql;


import Literals.ESCAPED_UNICODE_ERROR;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LiteralsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testEscape() throws Exception {
        MatcherAssert.assertThat(Literals.escapeStringLiteral(""), Is.is(""));
        MatcherAssert.assertThat(Literals.escapeStringLiteral("foobar"), Is.is("foobar"));
        MatcherAssert.assertThat(Literals.escapeStringLiteral("'"), Is.is("''"));
        MatcherAssert.assertThat(Literals.escapeStringLiteral("''"), Is.is("''''"));
        MatcherAssert.assertThat(Literals.escapeStringLiteral("'fooBar'"), Is.is("''fooBar''"));
    }

    @Test
    public void testQuote() throws Exception {
        MatcherAssert.assertThat(Literals.quoteStringLiteral(""), Is.is("''"));
        MatcherAssert.assertThat(Literals.quoteStringLiteral("foobar"), Is.is("'foobar'"));
        MatcherAssert.assertThat(Literals.quoteStringLiteral("'"), Is.is("''''"));
        MatcherAssert.assertThat(Literals.quoteStringLiteral("''"), Is.is("''''''"));
        MatcherAssert.assertThat(Literals.quoteStringLiteral("'fooBar'"), Is.is("'''fooBar'''"));
    }

    @Test
    public void testThatNoEscapedCharsAreNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars(""), Is.is(""));
        MatcherAssert.assertThat(Literals.replaceEscapedChars("Hello World"), Is.is("Hello World"));
    }

    // Single escaped chars supported
    @Test
    public void testThatEscapedTabLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\t"), Is.is("\t"));
    }

    @Test
    public void testThatEscapedTabInMiddleOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("Hello\\tWorld"), Is.is("Hello\tWorld"));
    }

    @Test
    public void testThatEscapedTabAtBeginningOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\tHelloWorld"), Is.is("\tHelloWorld"));
    }

    @Test
    public void testThatEscapedTabAtEndOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("HelloWorld\\t"), Is.is("HelloWorld\t"));
    }

    @Test
    public void testThatEscapedBackspaceInTheMiddleOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("Hello\\bWorld"), Is.is("Hello\bWorld"));
    }

    @Test
    public void testThatEscapedFormFeedInTheMiddleOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("Hello\\fWorld"), Is.is("Hello\fWorld"));
    }

    @Test
    public void testThatEscapedNewLineInTheMiddleOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("Hello\\nWorld"), Is.is("Hello\nWorld"));
    }

    @Test
    public void testThatCarriageReturnInTheMiddleOfLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("Hello\\rWorld"), Is.is("Hello\rWorld"));
    }

    @Test
    public void testThatMultipleConsecutiveSingleEscapedCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\t\\n\\f"), Is.is("\t\n\f"));
    }

    // Invalid escaped literals
    @Test
    public void testThatCharEscapeWithoutAnySequenceIsNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\"), Is.is("\\"));
    }

    @Test
    public void testThatEscapedBackslashCharIsReplacedAndNotConsideredAsEscapeChar() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\\\141"), Is.is("\\141"));
    }

    @Test
    public void testThatEscapedQuoteCharIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\\'141"), Is.is("'141"));
    }

    @Test
    public void testThatInvalidEscapeSequenceIsNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\s"), Is.is("\\s"));
    }

    @Test
    public void testThatInvalidEscapeSequenceAtBeginningOfLiteralIsNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\shello"), Is.is("\\shello"));
    }

    @Test
    public void testThatInvalidEscapeSequenceAtEndOfLiteralIsNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("hello\\s"), Is.is("hello\\s"));
    }

    // Octal Byte Values
    @Test
    public void testThatEscapedOctalValueLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\141"), Is.is("a"));
    }

    @Test
    public void testThatMultipleConsecutiveEscapedOctalValuesAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\141\\141\\141"), Is.is("aaa"));
    }

    @Test
    public void testThatDigitFollowingEscapedOctalValueIsNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\1411"), Is.is("a1"));
    }

    @Test
    public void testThatSingleDigitEscapedOctalValueIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\0"), Is.is("\u0000"));
    }

    @Test
    public void testThatDoubleDigitEscapedOctalValueIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\07"), Is.is("\u0007"));
    }

    // Hexadecimal Byte Values
    @Test
    public void testThatEscapedHexValueLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\x61"), Is.is("a"));
    }

    @Test
    public void testThatMultipleConsecutiveEscapedHexValueLiteralAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\x61\\x61\\x61"), Is.is("aaa"));
    }

    @Test
    public void testThatMultipleNonConsecutiveEscapedHexValueLiteralAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\x61 \\x61"), Is.is("a a"));
    }

    @Test
    public void testThatDigitsFollowingEscapedHexValueLiteralAreNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\x610000"), Is.is("a0000"));
    }

    @Test
    public void testThatSingleDigitEscapedHexValueLiteralIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\xDg0000"), Is.is("\rg0000"));
    }

    @Test
    public void testThatEscapedHexValueInTheMiddleOfTheLiteralAreReplacedIsReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("What \\x61 wonderful world"), Is.is("What a wonderful world"));
    }

    @Test
    public void testThatInvalidEscapedHexLiteralsAreNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\x\\x"), Is.is("xx"));
    }

    // 16-bit Unicode Character Values
    @Test
    public void testThatEscaped16BitUnicodeCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\u0061"), Is.is("a"));
    }

    @Test
    public void testThatMultipleConsecutiveEscaped16BitUnicodeCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\u0061\\u0061\\u0061"), Is.is("aaa"));
    }

    @Test
    public void testThatMultipleNonConsecutiveEscaped16BitUnicodeCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\u0061 \\u0061"), Is.is("a a"));
    }

    @Test
    public void testThatDigitsFollowingEscaped16BitUnicodeCharsAreNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\u00610000"), Is.is("a0000"));
    }

    @Test
    public void testThatEscaped16BitUnicodeCharsInTheMiddleOfTheLiteralAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("What \\u0061 wonderful world"), Is.is("What a wonderful world"));
    }

    @Test
    public void testThatInvalidLengthEscapedUnicode16SequenceThrowsException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ESCAPED_UNICODE_ERROR);
        Literals.replaceEscapedChars("\\u006");
    }

    @Test
    public void testThatInvalidHexEscapedUnicode16SequenceThrowsException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ESCAPED_UNICODE_ERROR);
        Literals.replaceEscapedChars("\\u006G");
    }

    @Test
    public void testThatEscaped32BitUnicodeCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\U00000061"), Is.is("a"));
    }

    @Test
    public void testThatMultipleConsecutiveEscaped32BitUnicodeCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\U00000061\\U00000061\\U00000061"), Is.is("aaa"));
    }

    @Test
    public void testThatMultipleNonConsecutiveEscaped32BitUnicodeCharsAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\U00000061 \\U00000061"), Is.is("a a"));
    }

    @Test
    public void testThatDigitsFollowingEscaped32BitUnicodeCharsAreNotReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("\\U000000610000"), Is.is("a0000"));
    }

    @Test
    public void testThatEscaped32BitUnicodeCharsInTheMiddleOfTheLiteralAreReplaced() throws Exception {
        MatcherAssert.assertThat(Literals.replaceEscapedChars("What \\U00000061 wonderful world"), Is.is("What a wonderful world"));
    }

    @Test
    public void testThatInvalidLengthEscapedUnicode32SequenceThrowsException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ESCAPED_UNICODE_ERROR);
        Literals.replaceEscapedChars("\\U0061");
    }

    @Test
    public void testThatInvalidHexEscapedUnicode32SequenceThrowsException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ESCAPED_UNICODE_ERROR);
        Literals.replaceEscapedChars("\\U0000006G");
    }
}

