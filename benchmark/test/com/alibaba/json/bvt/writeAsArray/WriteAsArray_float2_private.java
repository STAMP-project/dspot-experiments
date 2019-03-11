package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_float2_private extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_float2_private.VO vo = new WriteAsArray_float2_private.VO();
        vo.setId(123.0F);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123.0,\"wenshao\"]", text);
        WriteAsArray_float2_private.VO vo2 = JSON.parseObject(text, WriteAsArray_float2_private.VO.class, SupportArrayToBean);
        Assert.assertTrue(((vo.id) == (vo2.id)));
        Assert.assertEquals(vo.name, vo2.name);
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[123.A,\"wenshao\"]", WriteAsArray_float2_private.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[\"A\",\"wenshao\"]", WriteAsArray_float2_private.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[123:\"wenshao\"]", WriteAsArray_float2_private.VO.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class VO {
        private float id;

        private String name;

        public float getId() {
            return id;
        }

        public void setId(float id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

