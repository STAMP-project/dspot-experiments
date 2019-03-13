package com.alibaba.json.bvt;


import com.alibaba.fastjson.util.IOUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class Base64Test extends TestCase {
    public void test_base64() throws Exception {
        Assert.assertEquals(IOUtils.decodeBase64(new char[0], 0, 0).length, 0);
        Assert.assertEquals(IOUtils.decodeBase64("ABC".toCharArray(), 0, 3).length, 2);
    }
}

