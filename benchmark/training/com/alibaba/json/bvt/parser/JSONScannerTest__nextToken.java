package com.alibaba.json.bvt.parser;


import JSONToken.EOF;
import JSONToken.LBRACE;
import JSONToken.LBRACKET;
import JSONToken.LITERAL_INT;
import JSONToken.LITERAL_STRING;
import com.alibaba.fastjson.parser.JSONScanner;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest__nextToken extends TestCase {
    public void test_next() throws Exception {
        String text = "\"aaa\"";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(LITERAL_INT);
        Assert.assertEquals(LITERAL_STRING, lexer.token());
    }

    public void test_next_1() throws Exception {
        String text = "[";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(LITERAL_INT);
        Assert.assertEquals(LBRACKET, lexer.token());
    }

    public void test_next_2() throws Exception {
        String text = "{";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(LITERAL_INT);
        Assert.assertEquals(LBRACE, lexer.token());
    }

    public void test_next_3() throws Exception {
        String text = "{";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(LBRACKET);
        Assert.assertEquals(LBRACE, lexer.token());
    }

    public void test_next_4() throws Exception {
        String text = "";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(LBRACKET);
        Assert.assertEquals(EOF, lexer.token());
    }

    public void test_next_5() throws Exception {
        String text = " \n\r\t\f\b 1";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(LBRACKET);
        Assert.assertEquals(LITERAL_INT, lexer.token());
    }

    public void test_next_6() throws Exception {
        String text = "";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(EOF);
        Assert.assertEquals(EOF, lexer.token());
    }

    public void test_next_7() throws Exception {
        String text = "{";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken(EOF);
        Assert.assertEquals(LBRACE, lexer.token());
    }

    public void test_next_8() throws Exception {
        String text = "\n\r\t\f\b :{";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextTokenWithColon(LBRACE);
        Assert.assertEquals(LBRACE, lexer.token());
    }

    public void test_next_9() throws Exception {
        String text = "\n\r\t\f\b :[";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextTokenWithColon(LBRACE);
        Assert.assertEquals(LBRACKET, lexer.token());
    }

    public void test_next_10() throws Exception {
        String text = "\n\r\t\f\b :";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextTokenWithColon(LBRACE);
        Assert.assertEquals(EOF, lexer.token());
    }

    public void test_next_11() throws Exception {
        String text = "\n\r\t\f\b :{";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextTokenWithColon(LBRACKET);
        Assert.assertEquals(LBRACE, lexer.token());
    }

    public void test_next_12() throws Exception {
        String text = "\n\r\t\f\b :";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextTokenWithColon(LBRACKET);
        Assert.assertEquals(EOF, lexer.token());
    }

    public void test_next_13() throws Exception {
        String text = "\n\r\t\f\b :\n\r\t\f\b ";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextTokenWithColon(LBRACKET);
        Assert.assertEquals(EOF, lexer.token());
    }
}

