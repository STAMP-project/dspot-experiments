package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.JSONScanner;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * parseInt
 *
 * @author wenshao[szujobs@hotmail.com]
 */
public class JSONScannerTest_int extends TestCase {
    public void test_parse_long_2() throws Exception {
        System.out.println(System.currentTimeMillis());
        JSONScanner lexer = new JSONScanner(Long.toString(Integer.MIN_VALUE));
        lexer.scanNumber();
        Assert.assertEquals(new Integer(Integer.MIN_VALUE), ((Integer) (lexer.integerValue())));
        Assert.assertEquals(Integer.MIN_VALUE, lexer.intValue());
    }

    public void test_error_0() {
        Exception error = null;
        try {
            JSONScanner lexer = new JSONScanner("--");
            lexer.scanNumber();
            lexer.intValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() {
        Exception error = null;
        try {
            String text = (Integer.MAX_VALUE) + "1234";
            JSONScanner lexer = new JSONScanner(text);
            lexer.scanNumber();
            lexer.intValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() {
        Exception error = null;
        try {
            String text = (Integer.MIN_VALUE) + "1234";
            JSONScanner lexer = new JSONScanner(text);
            lexer.scanNumber();
            lexer.intValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() {
        Exception error = null;
        try {
            String text = "2147483648";
            JSONScanner lexer = new JSONScanner(text);
            lexer.scanNumber();
            lexer.intValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}

