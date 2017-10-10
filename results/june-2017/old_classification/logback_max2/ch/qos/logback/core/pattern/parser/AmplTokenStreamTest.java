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
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#compositedKeyword_cf54 */
    @org.junit.Test(timeout = 10000)
    public void compositedKeyword_cf54_failAssert46_literalMutation538_failAssert2() throws ch.qos.logback.core.spi.ScanException {
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
                    witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("a %su{bst(%b C)", new ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil()).tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a "));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "subst"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, " C"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                    // StatementAdderOnAssert create random local variable
                    java.lang.StringBuffer vc_13 = new java.lang.StringBuffer();
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_1 = "a %subst(%b C)";
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_8 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_8.optionEscape(String_vc_1, vc_13);
                }
                org.junit.Assert.fail("compositedKeyword_cf54 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("compositedKeyword_cf54_failAssert46_literalMutation538 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEmptyP2 */
    @org.junit.Test
    public void testEmptyP2_literalMutation40949_failAssert7() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%)").tokenize();
            java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
            witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
            witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
            witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
            org.junit.Assert.fail("testEmptyP2_literalMutation40949 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEmptyP2 */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEmptyP2_cf40969 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyP2_cf40969_failAssert20_literalMutation41119_failAssert29() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
                witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_9561 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_9559 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_9556 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_9556.optionEscape(vc_9559, vc_9561);
                org.junit.Assert.fail("testEmptyP2_cf40969 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEmptyP2_cf40969_failAssert20_literalMutation41119 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEmptyP2 */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEmptyP2_cf40967 */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEmptyP2_cf40967_failAssert18_literalMutation41101 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyP2_cf40967_failAssert18_literalMutation41101_literalMutation44312_failAssert6() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                // AssertGenerator replace invocation
                boolean o_testEmptyP2_cf40967_failAssert18_literalMutation41101__8 = witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                // MethodAssertGenerator build local variable
                Object o_10_0 = o_testEmptyP2_cf40967_failAssert18_literalMutation41101__8;
                // AssertGenerator replace invocation
                boolean o_testEmptyP2_cf40967_failAssert18_literalMutation41101__9 = witness.add(ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN);
                // MethodAssertGenerator build local variable
                Object o_14_0 = o_testEmptyP2_cf40967_failAssert18_literalMutation41101__9;
                // AssertGenerator replace invocation
                boolean o_testEmptyP2_cf40967_failAssert18_literalMutation41101__10 = witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                // MethodAssertGenerator build local variable
                Object o_18_0 = o_testEmptyP2_cf40967_failAssert18_literalMutation41101__10;
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_9561 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1365 = "xj2";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_9556 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_9556.optionEscape(String_vc_1365, vc_9561);
                org.junit.Assert.fail("testEmptyP2_cf40967 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEmptyP2_cf40967_failAssert18_literalMutation41101_literalMutation44312 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape */
    @org.junit.Test
    public void testEscape_literalMutation45126_failAssert21() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%\\(\\{\\)\\r\\n").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_%b").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
            }
            org.junit.Assert.fail("testEscape_literalMutation45126 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape */
    @org.junit.Test
    public void testEscape_literalMutation45144_failAssert39() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%\\(\\t\\)\\r\\n").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%8\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_%b").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
            }
            org.junit.Assert.fail("testEscape_literalMutation45144 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape_cf45187 */
    @org.junit.Test(timeout = 10000)
    public void testEscape_cf45187_failAssert75_literalMutation46667_failAssert6() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%\\(\\t\\)\\r\\n").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_%b").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
                    // StatementAdderOnAssert create random local variable
                    java.lang.StringBuffer vc_11129 = new java.lang.StringBuffer();
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_1589 = "a";
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_11124 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_11124.optionEscape(String_vc_1589, vc_11129);
                }
                org.junit.Assert.fail("testEscape_cf45187 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEscape_cf45187_failAssert75_literalMutation46667 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape_cf45188 */
    @org.junit.Test(timeout = 10000)
    public void testEscape_cf45188_failAssert76_literalMutation46717_failAssert14() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%\\H\\t\\)\\r\\n").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_%b").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
                    // StatementAdderOnAssert create null value
                    java.lang.StringBuffer vc_11128 = (java.lang.StringBuffer)null;
                    // StatementAdderOnAssert create random local variable
                    java.lang.String vc_11127 = new java.lang.String();
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_11124 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_11124.optionEscape(vc_11127, vc_11128);
                }
                org.junit.Assert.fail("testEscape_cf45188 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEscape_cf45188_failAssert76_literalMutation46717 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape_cf45184 */
    @org.junit.Test(timeout = 10000)
    public void testEscape_cf45184_failAssert72_literalMutation46486_failAssert15_literalMutation55248_failAssert22() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%\\(\\t\\)\\r\\n").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%\\)").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\h%b").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
                        // StatementAdderOnAssert create null value
                        java.lang.StringBuffer vc_11128 = (java.lang.StringBuffer)null;
                        // StatementAdderOnAssert create null value
                        java.lang.String vc_11126 = (java.lang.String)null;
                        // StatementAdderOnAssert create null value
                        ch.qos.logback.core.pattern.parser.TokenStream vc_11124 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                        // StatementAdderMethod cloned existing statement
                        vc_11124.optionEscape(vc_11126, vc_11128);
                    }
                    org.junit.Assert.fail("testEscape_cf45184 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testEscape_cf45184_failAssert72_literalMutation46486 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("testEscape_cf45184_failAssert72_literalMutation46486_failAssert15_literalMutation55248 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscape_literalMutation45144 */
    @org.junit.Test
    public void testEscape_literalMutation45144_failAssert39_literalMutation45664_failAssert12_literalMutation55032() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    // AssertGenerator replace invocation
                    boolean o_testEscape_literalMutation45144_failAssert39_literalMutation45664_failAssert12_literalMutation55032__11 = witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "_"));
                    // AssertGenerator add assertion
                    org.junit.Assert.assertTrue(o_testEscape_literalMutation45144_failAssert39_literalMutation45664_failAssert12_literalMutation55032__11);
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\!\\(\\t\\)\\r\\n").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%(\t)\r\n"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\\\%x").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "\\"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%8\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_a").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "a"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x\\_%b").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "b"));
                }
                org.junit.Assert.fail("testEscape_literalMutation45144 should have thrown ScanException");
            } catch (ch.qos.logback.core.spi.ScanException eee) {
            }
            org.junit.Assert.fail("testEscape_literalMutation45144_failAssert39_literalMutation45664 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses */
    @org.junit.Test
    public void testEscapedParanteheses_literalMutation55549_failAssert17() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\(%7\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%h\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\(x)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
            }
            org.junit.Assert.fail("testEscapedParanteheses_literalMutation55549 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses */
    @org.junit.Test
    public void testEscapedParanteheses_literalMutation55551_failAssert19() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\V(%h\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%h\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\(x)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
            }
            org.junit.Assert.fail("testEscapedParanteheses_literalMutation55551 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses_cf55601 */
    @org.junit.Test(timeout = 10000)
    public void testEscapedParanteheses_cf55601_failAssert62_literalMutation56509_failAssert19() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\(%|\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%h\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\(x)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
                    // StatementAdderOnAssert create null value
                    java.lang.StringBuffer vc_12318 = (java.lang.StringBuffer)null;
                    // StatementAdderOnAssert create null value
                    java.lang.String vc_12316 = (java.lang.String)null;
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_12314 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_12314.optionEscape(vc_12316, vc_12318);
                }
                org.junit.Assert.fail("testEscapedParanteheses_cf55601 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEscapedParanteheses_cf55601_failAssert62_literalMutation56509 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses_literalMutation55581 */
    @org.junit.Test
    public void testEscapedParanteheses_literalMutation55581_failAssert49_literalMutation56063() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\(%h\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                // AssertGenerator replace invocation
                boolean o_testEscapedParanteheses_literalMutation55581_failAssert49_literalMutation56063__12 = witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, ""));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testEscapedParanteheses_literalMutation55581_failAssert49_literalMutation56063__12);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%h\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\q(x)").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
            }
            org.junit.Assert.fail("testEscapedParanteheses_literalMutation55581 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses_cf55591 */
    @org.junit.Test(timeout = 10000)
    public void testEscapedParanteheses_cf55591_failAssert58_literalMutation56293_failAssert7_literalMutation63242_failAssert9() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\9(%h\\)").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%\\)").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
                    }
                    {
                        java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\(x)").tokenize();
                        java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                        witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
                        witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
                        // StatementAdderOnAssert create null value
                        java.lang.StringBuffer vc_12312 = (java.lang.StringBuffer)null;
                        // StatementAdderOnAssert create literal from method
                        java.lang.String String_vc_1758 = "(x";
                        // StatementAdderOnAssert create null value
                        ch.qos.logback.core.pattern.parser.TokenStream vc_12308 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                        // StatementAdderMethod cloned existing statement
                        vc_12308.escape(String_vc_1758, vc_12312);
                    }
                    org.junit.Assert.fail("testEscapedParanteheses_cf55591 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testEscapedParanteheses_cf55591_failAssert58_literalMutation56293 should have thrown ScanException");
            } catch (ch.qos.logback.core.spi.ScanException eee) {
            }
            org.junit.Assert.fail("testEscapedParanteheses_cf55591_failAssert58_literalMutation56293_failAssert7_literalMutation63242 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testEscapedParanteheses_cf55605 */
    @org.junit.Test(timeout = 10000)
    public void testEscapedParanteheses_cf55605_failAssert66_literalMutation56750_literalMutation58156_failAssert0() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\(%\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    // AssertGenerator replace invocation
                    boolean o_testEscapedParanteheses_cf55605_failAssert66_literalMutation56750__12 = witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "^"));
                    // MethodAssertGenerator build local variable
                    Object o_14_0 = o_testEscapedParanteheses_cf55605_failAssert66_literalMutation56750__12;
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("(%h\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "("));
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "h"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ")"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a(x\\)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.COMPOSITE_KEYWORD, "a"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "x)"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%a\\(x)").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "a"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "(x"));
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS));
                    // StatementAdderOnAssert create null value
                    java.lang.StringBuffer vc_12318 = (java.lang.StringBuffer)null;
                    // StatementAdderOnAssert create random local variable
                    java.lang.String vc_12317 = new java.lang.String();
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_12314 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_12314.optionEscape(vc_12317, vc_12318);
                }
                org.junit.Assert.fail("testEscapedParanteheses_cf55605 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEscapedParanteheses_cf55605_failAssert66_literalMutation56750_literalMutation58156 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent_cf63805 */
    @org.junit.Test(timeout = 10000)
    public void testLiteralWithPercent_cf63805_failAssert27_literalMutation63987_failAssert30() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\%world").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%world"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                    // StatementAdderOnAssert create null value
                    java.lang.StringBuffer vc_13362 = (java.lang.StringBuffer)null;
                    // StatementAdderOnAssert create null value
                    java.lang.String vc_13360 = (java.lang.String)null;
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_13358 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_13358.escape(vc_13360, vc_13362);
                }
                org.junit.Assert.fail("testLiteralWithPercent_cf63805 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testLiteralWithPercent_cf63805_failAssert27_literalMutation63987 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent_cf63807 */
    @org.junit.Test(timeout = 10000)
    public void testLiteralWithPercent_cf63807_failAssert29_literalMutation64034_failAssert26() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\^%world").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%world"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                    // StatementAdderOnAssert create null value
                    java.lang.StringBuffer vc_13362 = (java.lang.StringBuffer)null;
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_1908 = "hello%world";
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_13358 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_13358.escape(String_vc_1908, vc_13362);
                }
                org.junit.Assert.fail("testLiteralWithPercent_cf63807 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testLiteralWithPercent_cf63807_failAssert29_literalMutation64034 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent_cf63803 */
    @org.junit.Test(timeout = 10000)
    public void testLiteralWithPercent_cf63803_failAssert26_literalMutation63961_literalMutation65437_failAssert12() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\%world").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%world"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    // AssertGenerator replace invocation
                    boolean o_testLiteralWithPercent_cf63803_failAssert26_literalMutation63961__17 = witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, ""));
                    // MethodAssertGenerator build local variable
                    Object o_19_0 = o_testLiteralWithPercent_cf63803_failAssert26_literalMutation63961__17;
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_13356 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_13356.tokenize();
                }
                org.junit.Assert.fail("testLiteralWithPercent_cf63803 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testLiteralWithPercent_cf63803_failAssert26_literalMutation63961_literalMutation65437 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testLiteralWithPercent_cf63820 */
    @org.junit.Test(timeout = 10000)
    public void testLiteralWithPercent_cf63820_failAssert36_literalMutation64258_literalMutation66490_failAssert3() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\?%world").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello%world"));
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("hello\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    // AssertGenerator replace invocation
                    boolean o_testLiteralWithPercent_cf63820_failAssert36_literalMutation64258__17 = witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "ZQO[bX"));
                    // MethodAssertGenerator build local variable
                    Object o_19_0 = o_testLiteralWithPercent_cf63820_failAssert36_literalMutation64258__17;
                }
                {
                    java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("\\%").tokenize();
                    java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                    witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "%"));
                    // StatementAdderOnAssert create random local variable
                    java.lang.StringBuffer vc_13369 = new java.lang.StringBuffer();
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_1909 = "hello\\%world";
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.TokenStream vc_13364 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                    // StatementAdderMethod cloned existing statement
                    vc_13364.optionEscape(String_vc_1909, vc_13369);
                }
                org.junit.Assert.fail("testLiteralWithPercent_cf63820 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testLiteralWithPercent_cf63820_failAssert36_literalMutation64258_literalMutation66490 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testMultipleRecursion */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testMultipleRecursion_cf69564 */
    @org.junit.Test(timeout = 10000)
    public void testMultipleRecursion_cf69564_failAssert51_literalMutation70056_failAssert1() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%-1(%d %45(%class{%file))").tokenize();
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
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_14573 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_2081 = " ";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_14568 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_14568.optionEscape(String_vc_2081, vc_14573);
                org.junit.Assert.fail("testMultipleRecursion_cf69564 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testMultipleRecursion_cf69564_failAssert51_literalMutation70056 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested */
    @org.junit.Test
    public void testNested_literalMutation81056_failAssert13() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(%a%(%))").tokenize();
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
            org.junit.Assert.fail("testNested_literalMutation81056 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested_cf81065 */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf81065_failAssert21_literalMutation81177_failAssert3() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(%a%(%))").tokenize();
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
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_16821 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create null value
                java.lang.String vc_16818 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_16816 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_16816.escape(vc_16818, vc_16821);
                org.junit.Assert.fail("testNested_cf81065 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testNested_cf81065_failAssert21_literalMutation81177 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested_cf81081 */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf81081_failAssert31_literalMutation81389_failAssert4() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(%a%(%b{))").tokenize();
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
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_16827 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_16825 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_16822 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_16822.optionEscape(vc_16825, vc_16827);
                org.junit.Assert.fail("testNested_cf81081 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testNested_cf81081_failAssert31_literalMutation81389 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testNested_cf81081 */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf81081_failAssert31_add81386_literalMutation84627_failAssert4() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(%a%(%))").tokenize();
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
                // AssertGenerator replace invocation
                boolean o_testNested_cf81081_failAssert31_add81386__19 = // MethodCallAdder
witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                // MethodAssertGenerator build local variable
                Object o_21_0 = o_testNested_cf81081_failAssert31_add81386__19;
                witness.add(ch.qos.logback.core.pattern.parser.Token.RIGHT_PARENTHESIS_TOKEN);
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_16827 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_16825 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_16822 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_16822.optionEscape(vc_16825, vc_16827);
                org.junit.Assert.fail("testNested_cf81081 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testNested_cf81081_failAssert31_add81386_literalMutation84627 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testOptions */
    @org.junit.Test
    public void testOptions_literalMutation87694_failAssert43() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x{t}").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                java.util.List ol = new java.util.ArrayList<java.lang.String>();
                ol.add("t");
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
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
            }
            {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%x{\'opt}}").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(ch.qos.logback.core.pattern.parser.Token.PERCENT_TOKEN);
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.SIMPLE_KEYWORD, "x"));
                java.util.List ol = new java.util.ArrayList<java.lang.String>();
                ol.add("opt}");
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.OPTION, ol));
            }
            org.junit.Assert.fail("testOptions_literalMutation87694 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testSimpleP */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testSimpleP_cf96221 */
    @org.junit.Test(timeout = 10000)
    public void testSimpleP_cf96221_failAssert25_literalMutation96378_failAssert16() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("%(hello %class{.4?)").tokenize();
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
                // StatementAdderOnAssert create null value
                java.lang.StringBuffer vc_19942 = (java.lang.StringBuffer)null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_2848 = "class";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_19938 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_19938.escape(String_vc_2848, vc_19942);
                org.junit.Assert.fail("testSimpleP_cf96221 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSimpleP_cf96221_failAssert25_literalMutation96378 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testSingleLiteral */
    /* amplification of ch.qos.logback.core.pattern.parser.TokenStreamTest#testSingleLiteral_cf115102 */
    @org.junit.Test(timeout = 10000)
    public void testSingleLiteral_cf115102_failAssert19_literalMutation115232_failAssert11() throws ch.qos.logback.core.spi.ScanException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.List tl = new ch.qos.logback.core.pattern.parser.TokenStream("8.i(%").tokenize();
                java.util.List<ch.qos.logback.core.pattern.parser.Token> witness = new java.util.ArrayList<ch.qos.logback.core.pattern.parser.Token>();
                witness.add(new ch.qos.logback.core.pattern.parser.Token(ch.qos.logback.core.pattern.parser.Token.LITERAL, "hello"));
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuffer vc_24107 = new java.lang.StringBuffer();
                // StatementAdderOnAssert create null value
                java.lang.String vc_24104 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.TokenStream vc_24102 = (ch.qos.logback.core.pattern.parser.TokenStream)null;
                // StatementAdderMethod cloned existing statement
                vc_24102.optionEscape(vc_24104, vc_24107);
                org.junit.Assert.fail("testSingleLiteral_cf115102 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSingleLiteral_cf115102_failAssert19_literalMutation115232 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }
}

