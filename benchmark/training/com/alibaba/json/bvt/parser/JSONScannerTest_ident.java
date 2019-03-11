package com.alibaba.json.bvt.parser;


import JSONToken.FALSE;
import JSONToken.IDENTIFIER;
import JSONToken.NEW;
import JSONToken.NULL;
import JSONToken.TRUE;
import com.alibaba.fastjson.parser.JSONScanner;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_ident extends TestCase {
    public void test_true() throws Exception {
        JSONScanner lexer = new JSONScanner("true");
        lexer.scanIdent();
        Assert.assertEquals(TRUE, lexer.token());
    }

    public void test_false() throws Exception {
        JSONScanner lexer = new JSONScanner("false");
        lexer.scanIdent();
        Assert.assertEquals(FALSE, lexer.token());
    }

    public void test_null() throws Exception {
        JSONScanner lexer = new JSONScanner("null");
        lexer.scanIdent();
        Assert.assertEquals(NULL, lexer.token());
    }

    public void test_new() throws Exception {
        JSONScanner lexer = new JSONScanner("new");
        lexer.scanIdent();
        Assert.assertEquals(NEW, lexer.token());
    }

    public void test_Date() throws Exception {
        String text = "Date";
        JSONScanner lexer = new JSONScanner(text);
        lexer.scanIdent();
        Assert.assertEquals(IDENTIFIER, lexer.token());
        Assert.assertEquals(text, lexer.stringVal());
    }
}

