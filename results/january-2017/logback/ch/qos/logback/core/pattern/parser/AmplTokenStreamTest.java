/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */


package ch.qos.logback.core.pattern.parser;


public class AmplTokenStreamTest {
    @org.junit.Test
    public void testEmpty() throws ch.qos.logback.core.spi.ScanException {
        try {
            new ch.qos.logback.core.pattern.parser.TokenStream("").tokenize();
            org.junit.Assert.fail("empty string not allowed");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @org.junit.Test
    public void testSingleLiteral() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello"));
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testLiteralWithPercent() throws ch.qos.logback.core.spi.ScanException {
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\%world").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%world"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\%").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    @org.junit.Test
    public void testBasic() throws ch.qos.logback.core.spi.ScanException {
        // test "%c"
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%c").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "c"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            // test "xyz%-34c"
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a%b").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            // test "xyz%-34c"
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("xyz%-34c").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "xyz"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.FORMAT_MODIFIER, "-34"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "c"));
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    @org.junit.Test
    public void testComplexNR() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%d{1234} [%34.-67toto] %n").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "d"));
        java.util.List ol = new java.util.ArrayList<java.lang.String>();
        ol.add("1234");
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " ["));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.FORMAT_MODIFIER, "34.-67"));
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "toto"));
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "] "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "n"));
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testEmptyP() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("()").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testEmptyP2() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%()").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testEscape() throws ch.qos.logback.core.spi.ScanException {
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%\\(\\t\\)\\r\\n").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\)").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_%b").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    @org.junit.Test
    public void testOptions() throws ch.qos.logback.core.spi.ScanException {
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x{t}").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            java.util.List ol = new java.util.ArrayList<java.lang.String>();
            ol.add("t");
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x{t,y}").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            java.util.List ol = new java.util.ArrayList<java.lang.String>();
            ol.add("t");
            ol.add("y");
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x{\"hello world.\", \"12y  \"}").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            java.util.List ol = new java.util.ArrayList<java.lang.String>();
            ol.add("hello world.");
            ol.add("12y  ");
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x{'opt}'}").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            java.util.List ol = new java.util.ArrayList<java.lang.String>();
            ol.add("opt}");
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    @org.junit.Test
    public void testSimpleP() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(hello %class{.4?})").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "class"));
        java.util.List ol = new java.util.ArrayList<java.lang.String>();
        ol.add(".4?");
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testSimpleP2() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("X %a %-12.550(hello %class{.4?})").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "X "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.FORMAT_MODIFIER, "-12.550"));
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "class"));
        java.util.List ol = new java.util.ArrayList<java.lang.String>();
        ol.add(".4?");
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testMultipleRecursion() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%-1(%d %45(%class %file))").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.FORMAT_MODIFIER, "-1"));
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "d"));
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.FORMAT_MODIFIER, "45"));
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "class"));
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " "));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "file"));
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testNested() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(%a%(%b))").tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void testEscapedParanteheses() throws ch.qos.logback.core.spi.ScanException {
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\(%h\\)").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%h\\)").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\(x)").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    @org.junit.Test
    public void testWindowsLikeBackSlashes() throws ch.qos.logback.core.spi.ScanException {
        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("c:\\hello\\world.%i", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "c:\\hello\\world."));
        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "i"));
        org.junit.Assert.assertEquals(witness, tl);
    }

    @org.junit.Test
    public void compositedKeyword() throws ch.qos.logback.core.spi.ScanException {
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%d(A)", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "d"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "A"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
            org.junit.Assert.assertEquals(witness, tl);
        }
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("a %subst(%b C)", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a "));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "subst"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " C"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    @org.junit.Test
    public void compositedKeywordFollowedByOptions() throws ch.qos.logback.core.spi.ScanException {
        {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%d(A){o}", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "d"));
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "A"));
            witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
            java.util.List ol = new java.util.ArrayList<java.lang.String>();
            ol.add("o");
            witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
            org.junit.Assert.assertEquals(witness, tl);
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#compositedKeyword */
    @org.junit.Test(timeout = 10000)
    public void compositedKeyword_cf37_failAssert36_add132_literalMutation3850_failAssert13() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%d(A)", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "d"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "A"));
                    // AssertGenerator replace invocation
                    boolean o_compositedKeyword_cf37_failAssert36_add132__15 = // MethodCallAdder
witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertTrue(o_compositedKeyword_cf37_failAssert36_add132__15);
                    witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("a %sub{st(%b C)", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a "));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "subst"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " C"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_0 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_0);
                    // StatementAdderMethod cloned existing statement
                    vc_0.tokenize();
                }
                org.junit.Assert.fail("compositedKeyword_cf37 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("compositedKeyword_cf37_failAssert36_add132_literalMutation3850 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }
}

