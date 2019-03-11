package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestInnerClass2 extends TestCase {
    public void test_inner() throws Exception {
        TestInnerClass2.VO vo = new TestInnerClass2.VO(234);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"value\":234}", text);
        Exception error = null;
        try {
            JSON.parseObject(text, TestInnerClass2.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private class VO {
        private int value;

        public VO(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

