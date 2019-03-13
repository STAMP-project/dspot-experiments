package com.alibaba.json.bvt.parser;


import Feature.AllowArbitraryCommas;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultExtJSONParserTest_4 extends TestCase {
    public void test_0() throws Exception {
        List<?> res = Arrays.asList(1, 2, 3);
        String[] tests = new String[]{ "[1,2,3]", "[1,,2,3]", "[1,2,,,3]", "[1 2,,,3]", "[1 2 3]", "[1, 2, 3,,]", "[,,1, 2, 3,,]" };
        for (String t : tests) {
            DefaultJSONParser ext = new DefaultJSONParser(t);
            ext.config(AllowArbitraryCommas, true);
            List<Object> extRes = ext.parseArray(Object.class);
            Assert.assertEquals(res, extRes);
            DefaultJSONParser basic = new DefaultJSONParser(t);
            basic.config(AllowArbitraryCommas, true);
            List<Object> basicRes = new ArrayList<Object>();
            basic.parseArray(basicRes);
            Assert.assertEquals(res, basicRes);
        }
    }

    public void test_1() throws Exception {
        JSONObject res = new JSONObject();
        res.put("a", 1);
        res.put("b", 2);
        res.put("c", 3);
        String[] tests = new String[]{ "{ 'a':1, 'b':2, 'c':3 }", "{ 'a':1,,'b':2, 'c':3 }", "{,'a':1, 'b':2, 'c':3 }", "{'a':1, 'b':2, 'c':3,,}", "{,,'a':1,,,,'b':2,'c':3,,,,,}" };
        for (String t : tests) {
            DefaultJSONParser ext = new DefaultJSONParser(t);
            ext.config(AllowArbitraryCommas, true);
            JSONObject extRes = ext.parseObject();
            Assert.assertEquals(res, extRes);
            DefaultJSONParser basic = new DefaultJSONParser(t);
            basic.config(AllowArbitraryCommas, true);
            JSONObject basicRes = basic.parseObject();
            Assert.assertEquals(res, basicRes);
        }
    }

    public void test_2() throws Exception {
        DefaultExtJSONParserTest_4.A res = new DefaultExtJSONParserTest_4.A();
        res.setA(1);
        res.setB(2);
        res.setC(3);
        String[] tests = new String[]{ "{ 'a':1, 'b':2, 'c':3 }", "{ 'a':1,,'b':2, 'c':3 }", "{,'a':1, 'b':2, 'c':3 }", "{'a':1, 'b':2, 'c':3,,}", "{,,'a':1,,,,'b':2,,'c':3,,,,,}" };
        for (String t : tests) {
            DefaultJSONParser ext = new DefaultJSONParser(t);
            ext.config(AllowArbitraryCommas, true);
            DefaultExtJSONParserTest_4.A extRes = ext.parseObject(DefaultExtJSONParserTest_4.A.class);
            Assert.assertEquals(res, extRes);
        }
    }

    public static class A {
        private int a;

        private int b;

        private int c;

        public A() {
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        @Override
        public boolean equals(Object obj) {
            DefaultExtJSONParserTest_4.A o = ((DefaultExtJSONParserTest_4.A) (obj));
            return (((a) == (o.a)) && ((b) == (o.b))) && ((c) == (o.c));
        }
    }
}

