package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.net.URI;
import java.net.URL;
import junit.framework.TestCase;
import org.junit.Assert;


public class URLTest extends TestCase {
    public void test_file() throws Exception {
        URL url = URI.create("http://www.alibaba.com/").toURL();
        String text = JSON.toJSONString(url);
        Assert.assertEquals(JSON.toJSONString(url.toString()), text);
        URL url2 = JSON.parseObject(text, URL.class);
        Assert.assertEquals(url.toString(), url2.toString());
    }
}

