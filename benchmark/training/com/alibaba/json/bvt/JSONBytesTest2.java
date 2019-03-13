package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONBytesTest2 extends TestCase {
    public void test_codec() throws Exception {
        String text = "??????????????????????????????????????????????????????????????";
        byte[] bytes = JSON.toJSONBytes(text);
        String text2 = ((String) (JSON.parse(bytes)));
        Assert.assertEquals(text.length(), text2.length());
        for (int i = 0; i < (text.length()); ++i) {
            char c1 = text.charAt(i);
            char c2 = text2.charAt(i);
            Assert.assertEquals(c1, c2);
        }
    }
}

