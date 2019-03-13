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


import Token.BARE_COMPOSITE_KEYWORD_TOKEN;
import Token.PERCENT_TOKEN;
import Token.RIGHT_PARENTHESIS_TOKEN;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static Token.COMPOSITE_KEYWORD;
import static Token.FORMAT_MODIFIER;
import static Token.LITERAL;
import static Token.OPTION;
import static Token.RIGHT_PARENTHESIS;
import static Token.SIMPLE_KEYWORD;


public class TokenStreamTest {
    @Test
    public void testEmpty() throws ScanException {
        try {
            new TokenStream("").tokenize();
            Assert.fail("empty string not allowed");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testSingleLiteral() throws ScanException {
        List<Token> tl = new TokenStream("hello").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(new Token(LITERAL, "hello"));
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testLiteralWithPercent() throws ScanException {
        {
            List<Token> tl = new TokenStream("hello\\%world").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "hello%world"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("hello\\%").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "hello%"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("\\%").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "%"));
            Assert.assertEquals(witness, tl);
        }
    }

    @Test
    public void testBasic() throws ScanException {
        // test "%c"
        {
            List<Token> tl = new TokenStream("%c").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "c"));
            Assert.assertEquals(witness, tl);
        }
        {
            // test "xyz%-34c"
            List<Token> tl = new TokenStream("%a%b").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "a"));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "b"));
            Assert.assertEquals(witness, tl);
        }
        {
            // test "xyz%-34c"
            List<Token> tl = new TokenStream("xyz%-34c").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "xyz"));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(FORMAT_MODIFIER, "-34"));
            witness.add(new Token(SIMPLE_KEYWORD, "c"));
            Assert.assertEquals(witness, tl);
        }
    }

    @Test
    public void testComplexNR() throws ScanException {
        List<Token> tl = new TokenStream("%d{1234} [%34.-67toto] %n").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "d"));
        List<String> ol = new ArrayList<String>();
        ol.add("1234");
        witness.add(new Token(OPTION, ol));
        witness.add(new Token(LITERAL, " ["));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(FORMAT_MODIFIER, "34.-67"));
        witness.add(new Token(SIMPLE_KEYWORD, "toto"));
        witness.add(new Token(LITERAL, "] "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "n"));
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testEmptyP() throws ScanException {
        List<Token> tl = new TokenStream("()").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(new Token(LITERAL, "("));
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testEmptyP2() throws ScanException {
        List<Token> tl = new TokenStream("%()").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(PERCENT_TOKEN);
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testEscape() throws ScanException {
        {
            List<Token> tl = new TokenStream("\\%").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "%"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("\\%\\(\\t\\)\\r\\n").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "%(\t)\r\n"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("\\\\%x").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "\\"));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%x\\)").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            witness.add(new Token(LITERAL, ")"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%x\\_a").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            witness.add(new Token(LITERAL, "a"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%x\\_%b").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "b"));
            Assert.assertEquals(witness, tl);
        }
    }

    @Test
    public void testOptions() throws ScanException {
        {
            List<Token> tl = new TokenStream("%x{t}").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            List<String> ol = new ArrayList<String>();
            ol.add("t");
            witness.add(new Token(OPTION, ol));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%x{t,y}").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            List<String> ol = new ArrayList<String>();
            ol.add("t");
            ol.add("y");
            witness.add(new Token(OPTION, ol));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%x{\"hello world.\", \"12y  \"}").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            List<String> ol = new ArrayList<String>();
            ol.add("hello world.");
            ol.add("12y  ");
            witness.add(new Token(OPTION, ol));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%x{'opt}'}").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "x"));
            List<String> ol = new ArrayList<String>();
            ol.add("opt}");
            witness.add(new Token(OPTION, ol));
            Assert.assertEquals(witness, tl);
        }
    }

    @Test
    public void testSimpleP() throws ScanException {
        List<Token> tl = new TokenStream("%(hello %class{.4?})").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(PERCENT_TOKEN);
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(new Token(LITERAL, "hello "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "class"));
        List<String> ol = new ArrayList<String>();
        ol.add(".4?");
        witness.add(new Token(OPTION, ol));
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testSimpleP2() throws ScanException {
        List<Token> tl = new TokenStream("X %a %-12.550(hello %class{.4?})").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(new Token(LITERAL, "X "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "a"));
        witness.add(new Token(LITERAL, " "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(FORMAT_MODIFIER, "-12.550"));
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(new Token(LITERAL, "hello "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "class"));
        List<String> ol = new ArrayList<String>();
        ol.add(".4?");
        witness.add(new Token(OPTION, ol));
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testMultipleRecursion() throws ScanException {
        List<Token> tl = new TokenStream("%-1(%d %45(%class %file))").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(FORMAT_MODIFIER, "-1"));
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "d"));
        witness.add(new Token(LITERAL, " "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(FORMAT_MODIFIER, "45"));
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "class"));
        witness.add(new Token(LITERAL, " "));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "file"));
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testNested() throws ScanException {
        List<Token> tl = new TokenStream("%(%a%(%b))").tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(PERCENT_TOKEN);
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "a"));
        witness.add(PERCENT_TOKEN);
        witness.add(BARE_COMPOSITE_KEYWORD_TOKEN);
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "b"));
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        witness.add(RIGHT_PARENTHESIS_TOKEN);
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void testEscapedParanteheses() throws ScanException {
        {
            List<Token> tl = new TokenStream("\\(%h\\)").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "("));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "h"));
            witness.add(new Token(LITERAL, ")"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("(%h\\)").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "("));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "h"));
            witness.add(new Token(LITERAL, ")"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%a(x\\)").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(COMPOSITE_KEYWORD, "a"));
            witness.add(new Token(LITERAL, "x)"));
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = new TokenStream("%a\\(x)").tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "a"));
            witness.add(new Token(LITERAL, "(x"));
            witness.add(new Token(RIGHT_PARENTHESIS));
            Assert.assertEquals(witness, tl);
        }
    }

    @Test
    public void testWindowsLikeBackSlashes() throws ScanException {
        List<Token> tl = tokenize();
        List<Token> witness = new ArrayList<Token>();
        witness.add(new Token(LITERAL, "c:\\hello\\world."));
        witness.add(PERCENT_TOKEN);
        witness.add(new Token(SIMPLE_KEYWORD, "i"));
        Assert.assertEquals(witness, tl);
    }

    @Test
    public void compositedKeyword() throws ScanException {
        {
            List<Token> tl = tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(COMPOSITE_KEYWORD, "d"));
            witness.add(new Token(LITERAL, "A"));
            witness.add(RIGHT_PARENTHESIS_TOKEN);
            Assert.assertEquals(witness, tl);
        }
        {
            List<Token> tl = tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(new Token(LITERAL, "a "));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(COMPOSITE_KEYWORD, "subst"));
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(SIMPLE_KEYWORD, "b"));
            witness.add(new Token(LITERAL, " C"));
            witness.add(RIGHT_PARENTHESIS_TOKEN);
            Assert.assertEquals(witness, tl);
        }
    }

    @Test
    public void compositedKeywordFollowedByOptions() throws ScanException {
        {
            List<Token> tl = tokenize();
            List<Token> witness = new ArrayList<Token>();
            witness.add(PERCENT_TOKEN);
            witness.add(new Token(COMPOSITE_KEYWORD, "d"));
            witness.add(new Token(LITERAL, "A"));
            witness.add(RIGHT_PARENTHESIS_TOKEN);
            List<String> ol = new ArrayList<String>();
            ol.add("o");
            witness.add(new Token(OPTION, ol));
            Assert.assertEquals(witness, tl);
        }
    }
}

