package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeUtilsTest_compatibleWithJavaBean extends TestCase {
    private boolean origin_compatibleWithJavaBean;

    public void test_true() throws Exception {
        String text = JSON.toJSONString(new TypeUtilsTest_compatibleWithJavaBean.VO(123));
        Assert.assertEquals("{\"ID\":123}", text);
        Assert.assertEquals(123, JSON.parseObject(text, TypeUtilsTest_compatibleWithJavaBean.VO.class).getID());
    }

    public static class VO {
        private int id;

        public VO() {
        }

        public VO(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }

        public void setID(int id) {
            this.id = id;
        }
    }
}

