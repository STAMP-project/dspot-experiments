/**
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.json.bvt;


import JSONToken.EOF;
import JSONToken.ERROR;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.JSONScanner;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class LexerTest extends TestCase {
    public void test_float() throws Exception {
        String text = "123456789.0123";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken();
        BigDecimal decimalValue = lexer.decimalValue();
        Assert.assertEquals(new BigDecimal("123456789.0123"), decimalValue);
    }

    public void test_string() throws Exception {
        {
            JSONScanner lexer = new JSONScanner("\"\u4e2d\u56fd\"");
            lexer.nextToken();
            Assert.assertEquals("??", lexer.stringVal());
        }
        {
            JSONScanner lexer = new JSONScanner("\"\u4e2d\u56fd\t\"");
            lexer.nextToken();
            Assert.assertEquals("\u4e2d\u56fd\t", lexer.stringVal());
        }
        {
            JSONScanner lexer = new JSONScanner("\"\u4e2d\u56fd\tV5\"");
            lexer.nextToken();
            Assert.assertEquals("\u4e2d\u56fd\tV5", lexer.stringVal());
        }
        StringBuilder buf = new StringBuilder();
        buf.append('"');
        buf.append(("\\\\\\/\\b\\f\\n\\r\\t\\u" + (Integer.toHexString('?'))));
        buf.append('"');
        buf.append('\u2001');
        String text = buf.toString();
        JSONScanner lexer = new JSONScanner(text.toCharArray(), ((text.length()) - 1));
        lexer.nextToken();
        Assert.assertEquals(0, lexer.pos());
        String stringVal = lexer.stringVal();
        Assert.assertEquals("\"\\\\/\\b\\f\\n\\r\\t\u4e2d\"", JSON.toJSONString(stringVal));
    }

    public void test_string2() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('"');
        for (int i = 0; i < 200; ++i) {
            buf.append(("\\\\\\/\\b\\f\\n\\r\\t\\u" + (Integer.toHexString('?'))));
        }
        buf.append('"');
        String text = buf.toString();
        JSONScanner lexer = new JSONScanner(text.toCharArray(), text.length());
        lexer.nextToken();
        Assert.assertEquals(0, lexer.pos());
        lexer.stringVal();
    }

    public void test_string3() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('"');
        for (int i = 0; i < 200; ++i) {
            buf.append("abcdefghijklmn012345689ABCDEFG");
        }
        buf.append('"');
        String text = buf.toString();
        JSONScanner lexer = new JSONScanner(text.toCharArray(), text.length());
        lexer.nextToken();
        Assert.assertEquals(0, lexer.pos());
        lexer.stringVal();
    }

    public void test_string4() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('"');
        for (int i = 0; i < 200; ++i) {
            buf.append("\\tabcdefghijklmn012345689ABCDEFG");
        }
        buf.append('"');
        String text = buf.toString();
        JSONScanner lexer = new JSONScanner(text.toCharArray(), text.length());
        lexer.nextToken();
        Assert.assertEquals(0, lexer.pos());
        lexer.stringVal();
        // Assert.assertEquals("\"\\\\\\/\\b\\f\\n\\r\\t?\"",
        // JSON.toJSONString(stringVal));
    }

    public void test_empty() throws Exception {
        JSONScanner lexer = new JSONScanner("".toCharArray(), 0);
        lexer.nextToken();
        Assert.assertEquals(EOF, lexer.token());
    }

    public void test_isWhitespace() throws Exception {
        new JSONScanner("".toCharArray(), 0);
        Assert.assertTrue(JSONScanner.isWhitespace(' '));
        Assert.assertTrue(JSONScanner.isWhitespace('\b'));
        Assert.assertTrue(JSONScanner.isWhitespace('\f'));
        Assert.assertTrue(JSONScanner.isWhitespace('\n'));
        Assert.assertTrue(JSONScanner.isWhitespace('\r'));
        Assert.assertTrue(JSONScanner.isWhitespace('\t'));
        Assert.assertFalse(JSONScanner.isWhitespace('k'));
    }

    public void test_error() throws Exception {
        JSONScanner lexer = new JSONScanner("k");
        lexer.nextToken();
        Assert.assertEquals(ERROR, lexer.token());
    }

    public void test_error1() throws Exception {
        Exception error = null;
        try {
            JSONScanner lexer = new JSONScanner("\"\\k\"");
            lexer.nextToken();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_number() throws Exception {
        String text = "[0,1,-1,2E3,2E+3,2E-3,2e3,2e+3,2e-3]";
        JSONArray array = JSON.parseArray(text);
        Assert.assertEquals(0, array.get(0));
        Assert.assertEquals(1, array.get(1));
        Assert.assertEquals((-1), array.get(2));
        Assert.assertEquals(new BigDecimal("2E3"), array.get(3));
        Assert.assertEquals(new BigDecimal("2E3"), array.get(4));
        Assert.assertEquals(new BigDecimal("2E-3"), array.get(5));
        Assert.assertEquals(new BigDecimal("2E3"), array.get(6));
        Assert.assertEquals(new BigDecimal("2E3"), array.get(7));
        Assert.assertEquals(new BigDecimal("2E-3"), array.get(8));
        for (long i = Long.MIN_VALUE; i <= ((Long.MIN_VALUE) + (1000 * 10)); ++i) {
            Assert.assertEquals(i, JSON.parse(Long.toString(i)));
        }
        for (long i = (Long.MAX_VALUE) - (1000 * 10); (i <= (Long.MAX_VALUE)) && (i > 0); ++i) {
            Assert.assertEquals(i, JSON.parse(Long.toString(i)));
        }
    }

    public void test_big_integer_1() throws Exception {
        String text = (Long.MAX_VALUE) + "1234";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken();
        Assert.assertEquals(new BigInteger(text), lexer.integerValue());
    }

    public void test_big_integer_2() throws Exception {
        String text = (Long.MIN_VALUE) + "1234";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken();
        Assert.assertEquals(new BigInteger(text), lexer.integerValue());
    }

    public void test_big_integer_3() throws Exception {
        String text = "9223372036854775809";
        JSONScanner lexer = new JSONScanner(text);
        lexer.nextToken();
        Assert.assertEquals(new BigInteger(text), lexer.integerValue());
    }

    public void test_error2() {
        Exception error = null;
        try {
            JSONScanner lexer = new JSONScanner("--");
            lexer.nextToken();
            lexer.integerValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error3() {
        Exception error = null;
        try {
            JSONScanner lexer = new JSONScanner("");
            lexer.nextToken();
            lexer.nextToken();
            lexer.integerValue();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}

