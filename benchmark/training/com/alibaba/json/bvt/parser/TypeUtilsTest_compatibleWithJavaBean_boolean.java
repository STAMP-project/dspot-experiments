package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeUtilsTest_compatibleWithJavaBean_boolean extends TestCase {
    private boolean origin_compatibleWithJavaBean;

    public void test_true() throws Exception {
        String text = JSON.toJSONString(new TypeUtilsTest_compatibleWithJavaBean_boolean.VO(true));
        Assert.assertEquals("{\"ID\":true}", text);
        Assert.assertEquals(true, JSON.parseObject(text, TypeUtilsTest_compatibleWithJavaBean_boolean.VO.class).isID());
    }

    public static class VO {
        private boolean id;

        public VO() {
        }

        public VO(boolean id) {
            this.id = id;
        }

        public boolean isID() {
            return id;
        }

        public void setID(boolean id) {
            this.id = id;
        }
    }
}

