package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.JSONScanner;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * parseLong
 *
 * @author wenshao[szujobs@hotmail.com]
 */
public class JSONScannerTest_long extends TestCase {
    public void test_parse_long_2() throws Exception {
        System.out.println(System.currentTimeMillis());
        JSONScanner lexer = new JSONScanner(Long.toString(Long.MIN_VALUE));
        lexer.scanNumber();
        Assert.assertEquals(new Long(Long.MIN_VALUE), ((Long) (lexer.integerValue())));
        Assert.assertEquals(Long.MIN_VALUE, lexer.longValue());
    }

    public void test_error_0() {
        Exception error = null;
        try {
            JSONScanner lexer = new JSONScanner("--");
            lexer.scanNumber();
            lexer.longValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() {
        Exception error = null;
        try {
            String text = (Long.MAX_VALUE) + "1234";
            JSONScanner lexer = new JSONScanner(text);
            lexer.scanNumber();
            lexer.longValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() {
        Exception error = null;
        try {
            String text = (Long.MIN_VALUE) + "1234";
            JSONScanner lexer = new JSONScanner(text);
            lexer.scanNumber();
            lexer.longValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() {
        Exception error = null;
        try {
            String text = "9223372036854775809";
            JSONScanner lexer = new JSONScanner(text);
            lexer.scanNumber();
            lexer.longValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}

