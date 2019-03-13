package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_304 extends TestCase {
    public void test_doubleQuote() throws Exception {
        String ss = "{\"value\":\"Intki_E96?\u001a Build\"}";
        Assert.assertEquals("Intki_E96?\u001a Build", JSON.parseObject(ss).get("value"));
    }

    public void test_doubleQuote_vo() throws Exception {
        String ss = "{\"value\":\"Intki_E96?\u001a Build\"}";
        Assert.assertEquals("Intki_E96?\u001a Build", JSON.parseObject(ss, Bug_for_issue_304.VO.class).value);
    }

    public void test_doubleQuote_vo_private() throws Exception {
        String ss = "{\"value\":\"Intki_E96?\u001a Build\"}";
        Assert.assertEquals("Intki_E96?\u001a Build", JSON.parseObject(ss, Bug_for_issue_304.V1.class).value);
    }

    public void test_singleQuote() throws Exception {
        String ss = "{\'value\':\'Intki_E96?\u001a Build\'}";
        Assert.assertEquals("Intki_E96?\u001a Build", JSON.parseObject(ss).get("value"));
    }

    public void test_singleQuote_vo() throws Exception {
        String ss = "{\'value\':\'Intki_E96?\u001a Build\'}";
        Assert.assertEquals("Intki_E96?\u001a Build", JSON.parseObject(ss, Bug_for_issue_304.VO.class).value);
    }

    public static class VO {
        public String value;
    }

    private static class V1 {
        public String value;
    }
}

