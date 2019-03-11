package com.alibaba.json.bvt.basicType;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 04/08/2017.
 */
public class DoubleTest extends TestCase {
    public void test_obj() throws Exception {
        String json = "{\"v1\":-0.012671709,\"v2\":0.22676692048907365,\"v3\":0.13231707,\"v4\":0.80090785,\"v5\":0.6192943}";
        String json2 = "{\"v1\":\"-0.012671709\",\"v2\":\"0.22676692048907365\",\"v3\":\"0.13231707\",\"v4\":\"0.80090785\",\"v5\":\"0.6192943\"}";
        DoubleTest.Model m1 = JSON.parseObject(json, DoubleTest.Model.class);
        DoubleTest.Model m2 = JSON.parseObject(json2, DoubleTest.Model.class);
        TestCase.assertNotNull(m1);
        TestCase.assertNotNull(m2);
        TestCase.assertEquals((-0.012671709), m1.v1);
        TestCase.assertEquals(0.22676692048907365, m1.v2);
        TestCase.assertEquals(0.13231707, m1.v3);
        TestCase.assertEquals(0.80090785, m1.v4);
        TestCase.assertEquals(0.6192943, m1.v5);
        TestCase.assertEquals((-0.012671709), m2.v1);
        TestCase.assertEquals(0.22676692048907365, m2.v2);
        TestCase.assertEquals(0.13231707, m2.v3);
        TestCase.assertEquals(0.80090785, m2.v4);
        TestCase.assertEquals(0.6192943, m2.v5);
    }

    public void test_array_mapping() throws Exception {
        String json = "[-0.012671709,0.22676692048907365,0.13231707,0.80090785,0.6192943]";
        String json2 = "[\"-0.012671709\",\"0.22676692048907365\",\"0.13231707\",\"0.80090785\",\"0.6192943\"]";
        DoubleTest.Model m1 = JSON.parseObject(json, DoubleTest.Model.class, SupportArrayToBean);
        DoubleTest.Model m2 = JSON.parseObject(json2, DoubleTest.Model.class, SupportArrayToBean);
        TestCase.assertNotNull(m1);
        TestCase.assertNotNull(m2);
        TestCase.assertEquals((-0.012671709), m1.v1);
        TestCase.assertEquals(0.22676692048907365, m1.v2);
        TestCase.assertEquals(0.13231707, m1.v3);
        TestCase.assertEquals(0.80090785, m1.v4);
        TestCase.assertEquals(0.6192943, m1.v5);
        TestCase.assertEquals((-0.012671709), m2.v1);
        TestCase.assertEquals(0.22676692048907365, m2.v2);
        TestCase.assertEquals(0.13231707, m2.v3);
        TestCase.assertEquals(0.80090785, m2.v4);
        TestCase.assertEquals(0.6192943, m2.v5);
    }

    public static class Model {
        public double v1;

        public double v2;

        public double v3;

        public double v4;

        public double v5;
    }
}

