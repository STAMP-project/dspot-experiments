package com.alibaba.json.bvt.parser;


import JSONScanner.END;
import JSONScanner.NOT_MATCH;
import JSONToken.COMMA;
import JSONToken.EOF;
import JSONToken.RBRACE;
import JSONToken.RBRACKET;
import com.alibaba.fastjson.parser.JSONScanner;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ????':'???
 *
 * @author wenshao[szujobs@hotmail.com]
 */
public class JSONScannerTest_scanSymbol extends TestCase {
    public void test_0() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\\n\"");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        TestCase.assertEquals(0, hashCode);
        Assert.assertEquals(NOT_MATCH, lexer.matchStat());
    }

    public void test_1() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\"},");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        Assert.assertEquals(JSONScannerTest_scanSymbol.fnv_hash("aa"), hashCode);
        Assert.assertEquals(END, lexer.matchStat());
        Assert.assertEquals(COMMA, lexer.token());
    }

    public void test_2() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\"}]");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        Assert.assertEquals(JSONScannerTest_scanSymbol.fnv_hash("aa"), hashCode);
        Assert.assertEquals(END, lexer.matchStat());
        Assert.assertEquals(RBRACKET, lexer.token());
    }

    public void test_3() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\"}}");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        Assert.assertEquals(JSONScannerTest_scanSymbol.fnv_hash("aa"), hashCode);
        Assert.assertEquals(END, lexer.matchStat());
        Assert.assertEquals(RBRACE, lexer.token());
    }

    public void test_4() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\"}");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        Assert.assertEquals(JSONScannerTest_scanSymbol.fnv_hash("aa"), hashCode);
        Assert.assertEquals(END, lexer.matchStat());
        Assert.assertEquals(EOF, lexer.token());
    }

    public void test_6() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\"}{");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        Assert.assertEquals(0, hashCode);
        Assert.assertEquals(NOT_MATCH, lexer.matchStat());
    }

    public void test_7() throws Exception {
        JSONScanner lexer = new JSONScanner("\"value\":\"aa\"");
        long hashCode = lexer.scanFieldSymbol("\"value\":".toCharArray());
        Assert.assertEquals(0, hashCode);
        Assert.assertEquals(NOT_MATCH, lexer.matchStat());
    }
}

