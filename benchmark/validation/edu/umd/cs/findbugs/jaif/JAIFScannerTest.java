/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2007 University of Maryland
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
package edu.umd.cs.findbugs.jaif;


import JAIFTokenKind.COLON;
import JAIFTokenKind.COMMA;
import JAIFTokenKind.DECIMAL_LITERAL;
import JAIFTokenKind.EQUALS;
import JAIFTokenKind.FLOATING_POINT_LITERAL;
import JAIFTokenKind.HEX_LITERAL;
import JAIFTokenKind.IDENTIFIER_OR_KEYWORD;
import JAIFTokenKind.LPAREN;
import JAIFTokenKind.NEWLINE;
import JAIFTokenKind.OCTAL_LITERAL;
import JAIFTokenKind.RPAREN;
import JAIFTokenKind.STRING_LITERAL;
import org.junit.Test;


public class JAIFScannerTest {
    @Test
    public void testScanColon() throws Exception {
        JAIFScanner scanner = getScanner(":");
        checkToken(scanner, ":", COLON);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanParens() throws Exception {
        JAIFScanner scanner = getScanner("()");
        checkToken(scanner, "(", LPAREN);
        checkToken(scanner, ")", RPAREN);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanComma() throws Exception {
        JAIFScanner scanner = getScanner(",");
        checkToken(scanner, ",", COMMA);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanEquals() throws Exception {
        JAIFScanner scanner = getScanner("=");
        checkToken(scanner, "=", EQUALS);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanIdentifier() throws Exception {
        JAIFScanner scanner = getScanner("  \t  \t\t@foobar Baz123   ( Boing Boing) @Yum@Yum __123  $plotz");
        checkToken(scanner, "@foobar", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "Baz123", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "(", LPAREN);
        checkToken(scanner, "Boing", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "Boing", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, ")", RPAREN);
        checkToken(scanner, "@Yum", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "@Yum", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "__123", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "$plotz", IDENTIFIER_OR_KEYWORD);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanFloatingPointLiteral() throws Exception {
        JAIFScanner scanner = getScanner("1e1f    2.f     .3f     0f      3.14f   6.022137e+23f");
        checkToken(scanner, "1e1f", FLOATING_POINT_LITERAL);
        checkToken(scanner, "2.f", FLOATING_POINT_LITERAL);
        checkToken(scanner, ".3f", FLOATING_POINT_LITERAL);
        checkToken(scanner, "0f", FLOATING_POINT_LITERAL);
        checkToken(scanner, "3.14f", FLOATING_POINT_LITERAL);
        checkToken(scanner, "6.022137e+23f", FLOATING_POINT_LITERAL);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanFloatingPointLiteral2() throws Exception {
        JAIFScanner scanner = getScanner("1e1     2.      .3      0.0     3.14    1e-9d   1e137");
        checkToken(scanner, "1e1", FLOATING_POINT_LITERAL);
        checkToken(scanner, "2.", FLOATING_POINT_LITERAL);
        checkToken(scanner, ".3", FLOATING_POINT_LITERAL);
        checkToken(scanner, "0.0", FLOATING_POINT_LITERAL);
        checkToken(scanner, "3.14", FLOATING_POINT_LITERAL);
        checkToken(scanner, "1e-9d", FLOATING_POINT_LITERAL);
        checkToken(scanner, "1e137", FLOATING_POINT_LITERAL);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanOctalLiteral() throws Exception {
        JAIFScanner scanner = getScanner("0237   01575L  027365l");
        checkToken(scanner, "0237", OCTAL_LITERAL);
        checkToken(scanner, "01575L", OCTAL_LITERAL);
        checkToken(scanner, "027365l", OCTAL_LITERAL);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanHexLiteral() throws Exception {
        JAIFScanner scanner = getScanner("0xDEADbeef   0xcafeBabeL   0X123EEfl");
        checkToken(scanner, "0xDEADbeef", HEX_LITERAL);
        checkToken(scanner, "0xcafeBabeL", HEX_LITERAL);
        checkToken(scanner, "0X123EEfl", HEX_LITERAL);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanDecimalLiteral() throws Exception {
        JAIFScanner scanner = getScanner("1234     5678L    91919191l");
        checkToken(scanner, "1234", DECIMAL_LITERAL);
        checkToken(scanner, "5678L", DECIMAL_LITERAL);
        checkToken(scanner, "91919191l", DECIMAL_LITERAL);
        checkToken(scanner, "\n", NEWLINE);
    }

    @Test
    public void testScanStringLiteral() throws Exception {
        JAIFScanner scanner = getScanner("\"hello\"    \"foobie bletch\"  \"\\\"\"  \"\\\\\\6\\45\\037\"  \"\\b\\t\\f\\n\"  ");
        checkToken(scanner, "\"hello\"", STRING_LITERAL);
        checkToken(scanner, "\"foobie bletch\"", STRING_LITERAL);
        checkToken(scanner, "\"\\\"\"", STRING_LITERAL);
        checkToken(scanner, "\"\\\\\\6\\45\\037\"", STRING_LITERAL);
        checkToken(scanner, "\"\\b\\t\\f\\n\"", STRING_LITERAL);
        checkToken(scanner, "\n", NEWLINE);
    }
}

