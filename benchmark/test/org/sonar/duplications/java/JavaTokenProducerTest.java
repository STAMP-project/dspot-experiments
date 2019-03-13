/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.duplications.java;


import java.io.File;
import org.junit.Test;
import org.sonar.duplications.DuplicationsTestUtil;
import org.sonar.duplications.token.Token;
import org.sonar.duplications.token.TokenChunker;


public class JavaTokenProducerTest {
    private static final Token NUMERIC_LITTERAL = new Token("$NUMBER", 1, 0);

    private static final Token STRING_LITTERAL = new Token("$CHARS", 1, 0);

    private final TokenChunker chunker = JavaTokenProducer.build();

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.6">White Space</a>
     */
    @Test
    public void shouldIgnoreWhitespaces() {
        assertThat(chunk(" \t\f\n\r")).isEmpty();
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.7">Comments</a>
     */
    @Test
    public void shouldIgnoreEndOfLineComment() {
        assertThat(chunk("// This is a comment")).isEmpty();
        assertThat(chunk("// This is a comment \n and_this_is_not")).containsExactly(new Token("and_this_is_not", 2, 1));
    }

    @Test
    public void shouldIgnoreTraditionalComment() {
        assertThat(chunk("/* This is a comment \n and the second line */")).isEmpty();
        assertThat(chunk("/** This is a javadoc \n and the second line */")).isEmpty();
        assertThat(chunk("/* this \n comment /* \n // /** ends \n here: */")).isEmpty();
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.8">Identifiers</a>
     */
    @Test
    public void shouldPreserveIdentifiers() {
        assertThat(chunk("String")).containsExactly(new Token("String", 1, 0));
        assertThat(chunk("i3")).containsExactly(new Token("i3", 1, 0));
        assertThat(chunk("MAX_VALUE")).containsExactly(new Token("MAX_VALUE", 1, 0));
        assertThat(chunk("isLetterOrDigit")).containsExactly(new Token("isLetterOrDigit", 1, 0));
        assertThat(chunk("_")).containsExactly(new Token("_", 1, 0));
        assertThat(chunk("_123_")).containsExactly(new Token("_123_", 1, 0));
        assertThat(chunk("_Field")).containsExactly(new Token("_Field", 1, 0));
        assertThat(chunk("_Field5")).containsExactly(new Token("_Field5", 1, 0));
        assertThat(chunk("$")).containsExactly(new Token("$", 1, 0));
        assertThat(chunk("$field")).containsExactly(new Token("$field", 1, 0));
        assertThat(chunk("i2j")).containsExactly(new Token("i2j", 1, 0));
        assertThat(chunk("from1to4")).containsExactly(new Token("from1to4", 1, 0));
        // identifier with unicode
        assertThat(chunk("???")).containsExactly(new Token("???", 1, 0));
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.9">Keywords</a>
     */
    @Test
    public void shouldPreserverKeywords() {
        assertThat(chunk("private static final")).containsExactly(new Token("private", 1, 0), new Token("static", 1, 8), new Token("final", 1, 15));
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.1">Integer Literals</a>
     */
    @Test
    public void shouldNormalizeDecimalIntegerLiteral() {
        assertThat(chunk("543")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("543l")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("543L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    @Test
    public void shouldNormalizeOctalIntegerLiteral() {
        assertThat(chunk("077")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("077l")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("077L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    @Test
    public void shouldNormalizeHexIntegerLiteral() {
        assertThat(chunk("0xFF")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xFFl")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xFFL")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XFF")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XFFl")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XFFL")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    /**
     * New in Java 7.
     */
    @Test
    public void shouldNormalizeBinaryIntegerLiteral() {
        assertThat(chunk("0b10")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0b10l")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0b10L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0B10")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0B10l")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0B10L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.2">Floating-Point Literals</a>
     */
    @Test
    public void shouldNormalizeDecimalFloatingPointLiteral() {
        // with dot at the end
        assertThat(chunk("1234.")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234.E1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234.e+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234.E-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234.f")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        // with dot between
        assertThat(chunk("12.34")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34E1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34e+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34E-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34f")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34E1F")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34E+1d")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("12.34e-1D")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        // with dot at the beginning
        assertThat(chunk(".1234")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234e1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234E+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234E-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234f")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234E1F")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234e+1d")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1234E-1D")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        // without dot
        assertThat(chunk("1234e1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234E+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234E-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234E1f")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234e+1d")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1234E-1D")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    @Test
    public void shouldNormalizeHexadecimalFloatingPointLiteral() {
        // with dot at the end
        assertThat(chunk("0xAF.")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAF.P1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAF.p+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAF.p-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAF.f")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        // with dot between
        assertThat(chunk("0XAF.BC")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAF.BCP1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAF.BCp+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAF.BCP-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAF.BCf")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAF.BCp1F")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAF.BCP+1d")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAF.BCp-1D")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        // without dot
        assertThat(chunk("0xAFp1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAFp+1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAFp-1")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAFp1f")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xAFp+1d")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0XAFp-1D")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    /**
     * New in Java 7.
     */
    @Test
    public void shouldNormalizeNumericLiteralsWithUnderscores() {
        assertThat(chunk("54_3L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("07_7L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0b1_0L")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xF_FL")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1_234.")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1_2.3_4")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk(".1_234")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("1_234e1_0")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xA_F.")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0xA_F.B_C")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
        assertThat(chunk("0x1.ffff_ffff_ffff_fP1_023")).containsExactly(JavaTokenProducerTest.NUMERIC_LITTERAL);
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.3">Boolean Literals</a>
     */
    @Test
    public void shouldPreserveBooleanLiterals() {
        assertThat(chunk("true false")).containsExactly(new Token("true", 1, 0), new Token("false", 1, 5));
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.4">Character Literals</a>
     */
    @Test
    public void shouldNormalizeCharacterLiterals() {
        // single character
        assertThat(chunk("'a'")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // escaped LF
        assertThat(chunk("\'\\n\'")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // escaped quote
        assertThat(chunk("\'\\\'\'")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // octal escape
        assertThat(chunk("\'\\177\'")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // unicode escape
        assertThat(chunk("\'\\u03a9\'")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.5">String Literals</a>
     */
    @Test
    public void shouldNormalizeStringLiterals() {
        // regular string
        assertThat(chunk("\"string\"")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // empty string
        assertThat(chunk("\"\"")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // escaped LF
        assertThat(chunk("\"\\n\"")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // escaped double quotes
        assertThat(chunk("\"string, which contains \\\"escaped double quotes\\\"\"")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // octal escape
        assertThat(chunk("\"string \\177\"")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
        // unicode escape
        assertThat(chunk("\"string \\u03a9\"")).containsExactly(JavaTokenProducerTest.STRING_LITTERAL);
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.7">The Null Literal</a>
     */
    @Test
    public void shouldPreserverNullLiteral() {
        assertThat(chunk("null")).containsExactly(new Token("null", 1, 0));
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.11">Separators</a>
     */
    @Test
    public void shouldPreserveSeparators() {
        assertThat(chunk("(){}[];,.")).containsExactly(new Token("(", 1, 0), new Token(")", 1, 1), new Token("{", 1, 2), new Token("}", 1, 3), new Token("[", 1, 4), new Token("]", 1, 5), new Token(";", 1, 6), new Token(",", 1, 7), new Token(".", 1, 8));
    }

    /**
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.12">Operators</a>
     */
    @Test
    public void shouldPreserveOperators() {
        assertThat(chunk("+=")).containsExactly(new Token("+", 1, 0), new Token("=", 1, 1));
        assertThat(chunk("--")).containsExactly(new Token("-", 1, 0), new Token("-", 1, 1));
    }

    @Test
    public void realExamples() {
        File testFile = DuplicationsTestUtil.findFile("/java/MessageResources.java");
        assertThat(chunk(testFile)).isNotEmpty();
        testFile = DuplicationsTestUtil.findFile("/java/RequestUtils.java");
        assertThat(chunk(testFile)).isNotEmpty();
    }
}

