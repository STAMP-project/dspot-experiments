/**
 * Copyright 2006 The Bazel Authors. All Rights Reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import com.google.devtools.build.lib.events.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of tokenization behavior of the {@link Lexer}.
 */
@RunWith(JUnit4.class)
public class LexerTest {
    private String lastError;

    private Location lastErrorLocation;

    @Test
    public void testBasics1() throws Exception {
        assertThat(LexerTest.names(tokens("wiz) "))).isEqualTo("IDENTIFIER RPAREN NEWLINE EOF");
        assertThat(LexerTest.names(tokens("wiz )"))).isEqualTo("IDENTIFIER RPAREN NEWLINE EOF");
        assertThat(LexerTest.names(tokens(" wiz)"))).isEqualTo("INDENT IDENTIFIER RPAREN NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.names(tokens(" wiz ) "))).isEqualTo("INDENT IDENTIFIER RPAREN NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("wiz\t)"))).isEqualTo("IDENTIFIER RPAREN NEWLINE EOF");
    }

    @Test
    public void testBasics2() throws Exception {
        assertThat(LexerTest.names(tokens(")"))).isEqualTo("RPAREN NEWLINE EOF");
        assertThat(LexerTest.names(tokens(" )"))).isEqualTo("INDENT RPAREN NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.names(tokens(" ) "))).isEqualTo("INDENT RPAREN NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.names(tokens(") "))).isEqualTo("RPAREN NEWLINE EOF");
    }

    @Test
    public void testBasics3() throws Exception {
        assertThat(LexerTest.names(tokens("123#456\n789"))).isEqualTo("INT NEWLINE INT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("123 #456\n789"))).isEqualTo("INT NEWLINE INT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("123#456 \n789"))).isEqualTo("INT NEWLINE INT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("123#456\n 789"))).isEqualTo("INT NEWLINE INDENT INT NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("123#456\n789 "))).isEqualTo("INT NEWLINE INT NEWLINE EOF");
    }

    @Test
    public void testBasics4() throws Exception {
        assertThat(LexerTest.names(tokens(""))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens("# foo"))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens("1 2 3 4"))).isEqualTo("INT INT INT INT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("1.234"))).isEqualTo("INT DOT INT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("foo(bar, wiz)"))).isEqualTo(("IDENTIFIER LPAREN IDENTIFIER COMMA IDENTIFIER RPAREN " + "NEWLINE EOF"));
    }

    @Test
    public void testNonAsciiIdentifiers() throws Exception {
        tokens("?mlaut");
        assertThat(lastError.toString()).contains("invalid character: '?'");
        tokens("uml?ut");
        assertThat(lastError.toString()).contains("invalid character: '?'");
    }

    @Test
    public void testCrLf() throws Exception {
        assertThat(LexerTest.names(tokens("\r\n\r\n"))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens("\r\n\r1\r\r\n"))).isEqualTo("INT NEWLINE EOF");
        assertThat(LexerTest.names(tokens("# foo\r\n# bar\r\n"))).isEqualTo("NEWLINE EOF");
    }

    @Test
    public void testIntegers() throws Exception {
        // Detection of MINUS immediately following integer constant proves we
        // don't consume too many chars.
        // decimal
        assertThat(LexerTest.values(tokens("12345-"))).isEqualTo("INT(12345) MINUS NEWLINE EOF");
        // octal
        assertThat(LexerTest.values(tokens("012345-"))).isEqualTo("INT(5349) MINUS NEWLINE EOF");
        assertThat(LexerTest.values(tokens("0o12345-"))).isEqualTo("INT(5349) MINUS NEWLINE EOF");
        assertThat(LexerTest.values(tokens("0O77"))).isEqualTo("INT(63) NEWLINE EOF");
        // octal (bad)
        assertThat(LexerTest.values(tokens("012349-"))).isEqualTo("INT(0) MINUS NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: invalid base-8 integer constant: 012349");
        assertThat(LexerTest.values(tokens("0o"))).isEqualTo("INT(0) NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: invalid base-8 integer constant: 0o");
        // hexadecimal (uppercase)
        assertThat(LexerTest.values(tokens("0X12345F-"))).isEqualTo("INT(1193055) MINUS NEWLINE EOF");
        // hexadecimal (lowercase)
        assertThat(LexerTest.values(tokens("0x12345f-"))).isEqualTo("INT(1193055) MINUS NEWLINE EOF");
        // hexadecimal (lowercase) [note: "g" cause termination of token]
        assertThat(LexerTest.values(tokens("0x12345g-"))).isEqualTo("INT(74565) IDENTIFIER(g) MINUS NEWLINE EOF");
    }

    @Test
    public void testIntegersAndDot() throws Exception {
        assertThat(LexerTest.values(tokens("1.2345"))).isEqualTo("INT(1) DOT INT(2345) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1.2.345"))).isEqualTo("INT(1) DOT INT(2) DOT INT(345) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1.23E10"))).isEqualTo("INT(1) DOT INT(0) NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: invalid base-10 integer constant: 23E10");
        assertThat(LexerTest.values(tokens("1.23E-10"))).isEqualTo("INT(1) DOT INT(0) MINUS INT(10) NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: invalid base-10 integer constant: 23E");
        assertThat(LexerTest.values(tokens(". 123"))).isEqualTo("DOT INT(123) NEWLINE EOF");
        assertThat(LexerTest.values(tokens(".123"))).isEqualTo("DOT INT(123) NEWLINE EOF");
        assertThat(LexerTest.values(tokens(".abc"))).isEqualTo("DOT IDENTIFIER(abc) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("foo.123"))).isEqualTo("IDENTIFIER(foo) DOT INT(123) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("foo.bcd"))).isEqualTo("IDENTIFIER(foo) DOT IDENTIFIER(bcd) NEWLINE EOF");// 'b' are hex chars

        assertThat(LexerTest.values(tokens("foo.xyz"))).isEqualTo("IDENTIFIER(foo) DOT IDENTIFIER(xyz) NEWLINE EOF");
    }

    @Test
    public void testStringDelimiters() throws Exception {
        assertThat(LexerTest.values(tokens("\"foo\""))).isEqualTo("STRING(foo) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("'foo'"))).isEqualTo("STRING(foo) NEWLINE EOF");
    }

    @Test
    public void testQuotesInStrings() throws Exception {
        assertThat(LexerTest.values(tokens("\'foo\\\'bar\'"))).isEqualTo("STRING(foo'bar) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\"foo\'bar\""))).isEqualTo("STRING(foo'bar) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\'foo\"bar\'"))).isEqualTo("STRING(foo\"bar) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\"foo\\\"bar\""))).isEqualTo("STRING(foo\"bar) NEWLINE EOF");
    }

    @Test
    public void testStringEscapes() throws Exception {
        assertThat(LexerTest.values(tokens("\'a\\tb\\nc\\rd\'"))).isEqualTo("STRING(a\tb\nc\rd) NEWLINE EOF");// \t \r \n

        assertThat(LexerTest.values(tokens("\'x\\hx\'"))).isEqualTo("STRING(x\\hx) NEWLINE EOF");// \h is unknown => "\h"

        assertThat(LexerTest.values(tokens("\'\\$$\'"))).isEqualTo("STRING(\\$$) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\'a\\\nb\'"))).isEqualTo("STRING(ab) NEWLINE EOF");// escape end of line

        assertThat(LexerTest.values(tokens("\"ab\\ucd\""))).isEqualTo("STRING(abcd) NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: escape sequence not implemented: \\u");
    }

    @Test
    public void testEscapedCrlfInString() throws Exception {
        assertThat(LexerTest.values(tokens("\'a\\\r\nb\'"))).isEqualTo("STRING(ab) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\"a\\\r\nb\""))).isEqualTo("STRING(ab) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\"\"\"a\\\r\nb\"\"\""))).isEqualTo("STRING(ab) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\'\'\'a\\\r\nb\'\'\'"))).isEqualTo("STRING(ab) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("r\'a\\\r\nb\'"))).isEqualTo("STRING(a\\\nb) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("r\"a\\\r\nb\""))).isEqualTo("STRING(a\\\nb) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("r\"a\\\r\n\\\nb\""))).isEqualTo("STRING(a\\\n\\\nb) NEWLINE EOF");
    }

    @Test
    public void testRawString() throws Exception {
        assertThat(LexerTest.values(tokens("r'abcd'"))).isEqualTo("STRING(abcd) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("r\"abcd\""))).isEqualTo("STRING(abcd) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("r\'a\\tb\\nc\\rd\'"))).isEqualTo("STRING(a\\tb\\nc\\rd) NEWLINE EOF");// r'a\tb\nc\rd'

        assertThat(LexerTest.values(tokens("r\"a\\\"\""))).isEqualTo("STRING(a\\\") NEWLINE EOF");// r"a\""

        assertThat(LexerTest.values(tokens("r\'a\\\\b\'"))).isEqualTo("STRING(a\\\\b) NEWLINE EOF");// r'a\\b'

        assertThat(LexerTest.values(tokens("r'ab'r"))).isEqualTo("STRING(ab) IDENTIFIER(r) NEWLINE EOF");
        // Unterminated raw string
        LexerTest.values(tokens("r\'\\\'"));// r'\'

        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: unterminated string literal at eof");
    }

    @Test
    public void testTripleRawString() throws Exception {
        // r'''a\ncd'''
        assertThat(LexerTest.values(tokens("r\'\'\'ab\\ncd\'\'\'"))).isEqualTo("STRING(ab\\ncd) NEWLINE EOF");
        // r"""ab
        // cd"""
        assertThat(LexerTest.values(tokens("\"\"\"ab\ncd\"\"\""))).isEqualTo("STRING(ab\ncd) NEWLINE EOF");
        // Unterminated raw string
        LexerTest.values(tokens("r\'\'\'\\\'\'\'"));// r'''\'''

        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: unterminated string literal at eof");
    }

    @Test
    public void testOctalEscapes() throws Exception {
        // Regression test for a bug.
        assertThat(LexerTest.values(tokens("\'\\0 \\1 \\11 \\77 \\111 \\1111 \\377\'"))).isEqualTo("STRING(\u0000 \u0001 \t ? I I1 \u00ff) NEWLINE EOF");
        // Test boundaries (non-octal char, EOF).
        assertThat(LexerTest.values(tokens("\'\\1b \\1\'"))).isEqualTo("STRING(\u0001b \u0001) NEWLINE EOF");
    }

    @Test
    public void testOctalEscapeOutOfRange() throws Exception {
        assertThat(LexerTest.values(tokens("\'\\777\'"))).isEqualTo("STRING(\u00ff) NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: octal escape sequence out of range (maximum is \\377)");
    }

    @Test
    public void testTripleQuotedStrings() throws Exception {
        assertThat(LexerTest.values(tokens("\"\"\"a\"b\'c \n d\"\"e\"\"\""))).isEqualTo("STRING(a\"b\'c \n d\"\"e) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\'\'\'a\"b\'c \n d\"\"e\'\'\'"))).isEqualTo("STRING(a\"b\'c \n d\"\"e) NEWLINE EOF");
    }

    @Test
    public void testBadChar() throws Exception {
        assertThat(LexerTest.values(tokens("a$b"))).isEqualTo("IDENTIFIER(a) IDENTIFIER(b) NEWLINE EOF");
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:1: invalid character: '$'");
    }

    @Test
    public void testIndentation() throws Exception {
        assertThat(LexerTest.values(tokens("1\n2\n3"))).isEqualTo("INT(1) NEWLINE INT(2) NEWLINE INT(3) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1\n  2\n  3\n4 "))).isEqualTo(("INT(1) NEWLINE INDENT INT(2) NEWLINE INT(3) NEWLINE OUTDENT " + "INT(4) NEWLINE EOF"));
        assertThat(LexerTest.values(tokens("1\n  2\n  3"))).isEqualTo(("INT(1) NEWLINE INDENT INT(2) NEWLINE INT(3) NEWLINE OUTDENT " + "NEWLINE EOF"));
        assertThat(LexerTest.values(tokens("1\n  2\n    3"))).isEqualTo(("INT(1) NEWLINE INDENT INT(2) NEWLINE INDENT INT(3) NEWLINE " + "OUTDENT OUTDENT NEWLINE EOF"));
        assertThat(LexerTest.values(tokens("1\n  2\n    3\n  4\n5"))).isEqualTo(("INT(1) NEWLINE INDENT INT(2) NEWLINE INDENT INT(3) NEWLINE " + "OUTDENT INT(4) NEWLINE OUTDENT INT(5) NEWLINE EOF"));
        assertThat(LexerTest.values(tokens("1\n  2\n    3\n   4\n5"))).isEqualTo(("INT(1) NEWLINE INDENT INT(2) NEWLINE INDENT INT(3) NEWLINE " + "OUTDENT INT(4) NEWLINE OUTDENT INT(5) NEWLINE EOF"));
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:4: indentation error");
    }

    @Test
    public void testIndentationWithTab() throws Exception {
        tokens("def x():\n\tpass");
        assertThat(lastError).contains("Tab characters are not allowed");
    }

    @Test
    public void testIndentationWithCrLf() throws Exception {
        assertThat(LexerTest.values(tokens("1\r\n  2\r\n"))).isEqualTo("INT(1) NEWLINE INDENT INT(2) NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1\r\n  2\r\n\r\n"))).isEqualTo("INT(1) NEWLINE INDENT INT(2) NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1\r\n  2\r\n    3\r\n  4\r\n5"))).isEqualTo(("INT(1) NEWLINE INDENT INT(2) NEWLINE INDENT INT(3) NEWLINE OUTDENT INT(4) " + "NEWLINE OUTDENT INT(5) NEWLINE EOF"));
        assertThat(LexerTest.values(tokens("1\r\n  2\r\n\r\n  3\r\n4"))).isEqualTo("INT(1) NEWLINE INDENT INT(2) NEWLINE INT(3) NEWLINE OUTDENT INT(4) NEWLINE EOF");
    }

    @Test
    public void testIndentationInsideParens() throws Exception {
        // Indentation is ignored inside parens:
        assertThat(LexerTest.values(tokens("1 (\n  2\n    3\n  4\n5"))).isEqualTo("INT(1) LPAREN INT(2) INT(3) INT(4) INT(5) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1 {\n  2\n    3\n  4\n5"))).isEqualTo("INT(1) LBRACE INT(2) INT(3) INT(4) INT(5) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1 [\n  2\n    3\n  4\n5"))).isEqualTo("INT(1) LBRACKET INT(2) INT(3) INT(4) INT(5) NEWLINE EOF");
        assertThat(LexerTest.values(tokens("1 [\n  2]\n    3\n    4\n5"))).isEqualTo(("INT(1) LBRACKET INT(2) RBRACKET NEWLINE INDENT INT(3) " + "NEWLINE INT(4) NEWLINE OUTDENT INT(5) NEWLINE EOF"));
    }

    @Test
    public void testIndentationAtEOF() throws Exception {
        // Matching OUTDENTS are created at EOF:
        assertThat(LexerTest.values(tokens("\n  1"))).isEqualTo("INDENT INT(1) NEWLINE OUTDENT NEWLINE EOF");
    }

    @Test
    public void testIndentationOnFirstLine() throws Exception {
        assertThat(LexerTest.values(tokens("    1"))).isEqualTo("INDENT INT(1) NEWLINE OUTDENT NEWLINE EOF");
        assertThat(LexerTest.values(tokens("\n\n    1"))).isEqualTo("INDENT INT(1) NEWLINE OUTDENT NEWLINE EOF");
    }

    @Test
    public void testBlankLineIndentation() throws Exception {
        // Blank lines and comment lines should not generate any newlines indents
        // (but note that every input ends with NEWLINE EOF).
        assertThat(LexerTest.names(tokens("\n      #\n"))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens("      #"))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens("      #\n"))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens("      #comment\n"))).isEqualTo("NEWLINE EOF");
        assertThat(LexerTest.names(tokens(("def f(x):\n" + ((("  # comment\n" + "\n") + "  \n") + "  return x\n"))))).isEqualTo(("DEF IDENTIFIER LPAREN IDENTIFIER RPAREN COLON NEWLINE " + ("INDENT RETURN IDENTIFIER NEWLINE " + "OUTDENT NEWLINE EOF")));
    }

    @Test
    public void testBackslash() throws Exception {
        assertThat(LexerTest.names(tokens("a\\\nb"))).isEqualTo("IDENTIFIER IDENTIFIER NEWLINE EOF");
        assertThat(LexerTest.names(tokens("a\\\r\nb"))).isEqualTo("IDENTIFIER IDENTIFIER NEWLINE EOF");
        assertThat(LexerTest.names(tokens("a\\ b"))).isEqualTo("IDENTIFIER ILLEGAL IDENTIFIER NEWLINE EOF");
        assertThat(LexerTest.names(tokens("a(\\\n2)"))).isEqualTo("IDENTIFIER LPAREN INT RPAREN NEWLINE EOF");
    }

    @Test
    public void testTokenPositions() throws Exception {
        // foo (     bar   ,     {      1       :
        assertThat(LexerTest.positions(tokens("foo(bar, {1: \'quux\'}, \"\"\"b\"\"\", r\"\")"))).isEqualTo(("[0,3) [3,4) [4,7) [7,8) [9,10) [10,11) [11,12)" + // 'quux'  }       ,       """b""" ,       r""     )       NEWLINE EOF
        " [13,19) [19,20) [20,21) [22,29) [29,30) [31,34) [34,35) [35,35) [35,35)"));
    }

    @Test
    public void testLineNumbers() throws Exception {
        assertThat(linenums("foo = 1\nbar = 2\n\nwiz = 3")).isEqualTo("1 1 1 1 2 2 2 2 4 4 4 4 4");
        assertThat(LexerTest.values(tokens("foo = 1\nbar = 2\n\nwiz = $\nbar = 2"))).isEqualTo(("IDENTIFIER(foo) EQUALS INT(1) NEWLINE " + (("IDENTIFIER(bar) EQUALS INT(2) NEWLINE " + "IDENTIFIER(wiz) EQUALS NEWLINE ") + "IDENTIFIER(bar) EQUALS INT(2) NEWLINE EOF")));
        assertThat(lastError.toString()).isEqualTo("/some/path.txt:4: invalid character: '$'");
        // '\\n' in string should not increment linenum:
        String s = "1\n\'foo\\nbar\'\u0003";
        assertThat(LexerTest.values(tokens(s))).isEqualTo("INT(1) NEWLINE STRING(foo\nbar) NEWLINE EOF");
        assertThat(linenums(s)).isEqualTo("1 1 2 2 2");
    }

    @Test
    public void testContainsErrors() throws Exception {
        Lexer lexerSuccess = createLexer("foo");
        allTokens(lexerSuccess);// ensure the file has been completely scanned

        assertThat(lexerSuccess.containsErrors()).isFalse();
        Lexer lexerFail = createLexer("f$o");
        allTokens(lexerFail);
        assertThat(lexerFail.containsErrors()).isTrue();
        String s = "'unterminated";
        lexerFail = createLexer(s);
        allTokens(lexerFail);
        assertThat(lexerFail.containsErrors()).isTrue();
        assertThat(lastErrorLocation.getStartOffset()).isEqualTo(0);
        assertThat(lastErrorLocation.getEndOffset()).isEqualTo(s.length());
        assertThat(LexerTest.values(tokens(s))).isEqualTo("STRING(unterminated) NEWLINE EOF");
    }

    @Test
    public void testUnterminatedRawStringWithEscapingError() throws Exception {
        assertThat(LexerTest.names(tokens("r\'\\"))).isEqualTo("STRING NEWLINE EOF");
        assertThat(lastError).isEqualTo("/some/path.txt:1: unterminated string literal at eof");
    }

    @Test
    public void testLexerLocationCodec() throws Exception {
        runTests();
    }
}

