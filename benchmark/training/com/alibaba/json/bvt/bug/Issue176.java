package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue176 extends TestCase {
    public void test_for_parent() throws Exception {
        String text = "{\"content\":\"result\"}";
        Issue176.ParentClass parentClass = JSON.parseObject(text, Issue176.ParentClass.class);
        Assert.assertEquals(parentClass.getTest(), "result");
        String text2 = JSON.toJSONString(parentClass);
        Assert.assertEquals(text, text2);
    }

    public void test_for_sub() throws Exception {
        String text = "{\"content\":\"result\"}";
        Issue176.SubClass parentClass = JSON.parseObject(text, Issue176.SubClass.class);
        Assert.assertEquals(parentClass.getTest(), "result");
        String text2 = JSON.toJSONString(parentClass);
        Assert.assertEquals(text, text2);
    }

    public static class ParentClass {
        @JSONField(name = "content")
        protected String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    public static class SubClass extends Issue176.ParentClass {}
}

