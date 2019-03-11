package com.alibaba.json.bvt.basicType;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/08/2017.
 */
public class LongTest2 extends TestCase {
    public void test_0() throws Exception {
        String json = "{\"v1\":-1883391953414482124,\"v2\":-3019416596934963650,\"v3\":6497525620823745793,\"v4\":2136224289077142499,\"v5\":-2090575024006307745}";
        String json2 = "{\"v1\":\"-1883391953414482124\",\"v2\":\"-3019416596934963650\",\"v3\":\"6497525620823745793\",\"v4\":\"2136224289077142499\",\"v5\":\"-2090575024006307745\"}";
        LongTest2.Model m1 = JSON.parseObject(json, LongTest2.Model.class);
        LongTest2.Model m2 = JSON.parseObject(json2, LongTest2.Model.class);
        TestCase.assertNotNull(m1);
        TestCase.assertNotNull(m2);
        TestCase.assertEquals((-1883391953414482124L), m1.v1);
        TestCase.assertEquals((-3019416596934963650L), m1.v2);
        TestCase.assertEquals(6497525620823745793L, m1.v3);
        TestCase.assertEquals(2136224289077142499L, m1.v4);
        TestCase.assertEquals((-2090575024006307745L), m1.v5);
        TestCase.assertEquals((-1883391953414482124L), m2.v1);
        TestCase.assertEquals((-3019416596934963650L), m2.v2);
        TestCase.assertEquals(6497525620823745793L, m2.v3);
        TestCase.assertEquals(2136224289077142499L, m2.v4);
        TestCase.assertEquals((-2090575024006307745L), m2.v5);
    }

    public void test_1() throws Exception {
        String json = "{\"v1\":-1883391953414482124,\"v2\":-3019416596934963650,\"v3\":6497525620823745793,\"v4\":2136224289077142499,\"v5\":-2090575024006307745}";
        String json2 = "{\"v1\":\"-1883391953414482124\",\"v2\":\"-3019416596934963650\",\"v3\":\"6497525620823745793\",\"v4\":\"2136224289077142499\",\"v5\":\"-2090575024006307745\"}";
        LongTest2.Model m1 = new JSONReader(new StringReader(json)).readObject(LongTest2.Model.class);
        LongTest2.Model m2 = new JSONReader(new StringReader(json2)).readObject(LongTest2.Model.class);
        TestCase.assertNotNull(m1);
        TestCase.assertNotNull(m2);
        TestCase.assertEquals((-1883391953414482124L), m1.v1);
        TestCase.assertEquals((-3019416596934963650L), m1.v2);
        TestCase.assertEquals(6497525620823745793L, m1.v3);
        TestCase.assertEquals(2136224289077142499L, m1.v4);
        TestCase.assertEquals((-2090575024006307745L), m1.v5);
        TestCase.assertEquals((-1883391953414482124L), m2.v1);
        TestCase.assertEquals((-3019416596934963650L), m2.v2);
        TestCase.assertEquals(6497525620823745793L, m2.v3);
        TestCase.assertEquals(2136224289077142499L, m2.v4);
        TestCase.assertEquals((-2090575024006307745L), m2.v5);
    }

    public void test_2() throws Exception {
        String json = "[-1883391953414482124,-3019416596934963650,6497525620823745793,2136224289077142499,-2090575024006307745]";
        String json2 = "[\"-1883391953414482124\",\"-3019416596934963650\",\"6497525620823745793\",\"2136224289077142499\",\"-2090575024006307745\"]";
        LongTest2.Model m1 = readObject(LongTest2.Model.class);
        LongTest2.Model m2 = readObject(LongTest2.Model.class);
        TestCase.assertNotNull(m1);
        TestCase.assertNotNull(m2);
        TestCase.assertEquals((-1883391953414482124L), m1.v1);
        TestCase.assertEquals((-3019416596934963650L), m1.v2);
        TestCase.assertEquals(6497525620823745793L, m1.v3);
        TestCase.assertEquals(2136224289077142499L, m1.v4);
        TestCase.assertEquals((-2090575024006307745L), m1.v5);
        TestCase.assertEquals((-1883391953414482124L), m2.v1);
        TestCase.assertEquals((-3019416596934963650L), m2.v2);
        TestCase.assertEquals(6497525620823745793L, m2.v3);
        TestCase.assertEquals(2136224289077142499L, m2.v4);
        TestCase.assertEquals((-2090575024006307745L), m2.v5);
    }

    public static class Model {
        public long v1;

        public long v2;

        public long v3;

        public long v4;

        public long v5;
    }
}

