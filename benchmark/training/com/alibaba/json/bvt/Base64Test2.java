package com.alibaba.json.bvt;


import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.json.test.Base64;
import junit.framework.TestCase;
import org.junit.Assert;


public class Base64Test2 extends TestCase {
    public void test_base64_2() throws Exception {
        String text = "";
        for (int i = 0; i < 1000; ++i) {
            byte[] bytes = text.getBytes("UTF-8");
            {
                String str = Base64.encodeToString(bytes, true);
                Assert.assertEquals(text, new String(IOUtils.decodeBase64(str.toCharArray(), 0, str.length()), "UTF-8"));
                Assert.assertEquals(text, new String(IOUtils.decodeBase64(str), "UTF-8"));
                Assert.assertEquals(text, new String(IOUtils.decodeBase64(str, 0, str.length()), "UTF-8"));
            }
            {
                String str = Base64.encodeToString(bytes, false);
                Assert.assertEquals(text, new String(IOUtils.decodeBase64(str.toCharArray(), 0, str.length()), "UTF-8"));
                Assert.assertEquals(text, new String(IOUtils.decodeBase64(str), "UTF-8"));
                Assert.assertEquals(text, new String(IOUtils.decodeBase64(str, 0, str.length()), "UTF-8"));
            }
            text += ((char) (i));
        }
    }

    public void test_illegal() throws Exception {
        String text = "abc";
        byte[] bytes = text.getBytes("UTF-8");
        String str = ("\u0000" + (Base64.encodeToString(bytes, false))) + "\u0000";
        Assert.assertEquals(text, new String(IOUtils.decodeBase64(str.toCharArray(), 0, str.length()), "UTF-8"));
        Assert.assertEquals(text, new String(IOUtils.decodeBase64(str), "UTF-8"));
        Assert.assertEquals(text, new String(IOUtils.decodeBase64(str, 0, str.length()), "UTF-8"));
    }
}

