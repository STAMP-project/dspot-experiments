package com.alibaba.json.bvt.parser;


import JSONToken.LITERAL_INT;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import com.alibaba.fastjson.parser.JSONScanner;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_8 extends TestCase {
    public void test_ident() throws Exception {
        JSONScanner lexer = new JSONScanner("123");
        lexer.nextIdent();
        Assert.assertEquals(LITERAL_INT, lexer.token());
        lexer.close();
    }

    public void test_ident_2() throws Exception {
        JSONScanner lexer = new JSONScanner("\ufeff123");
        lexer.nextIdent();
        Assert.assertEquals(LITERAL_INT, lexer.token());
        lexer.close();
    }

    public void test_ident_3() throws Exception {
        JSONReaderScanner lexer = new JSONReaderScanner("\ufeff123");
        lexer.nextIdent();
        Assert.assertEquals(LITERAL_INT, lexer.token());
        lexer.close();
    }
}

