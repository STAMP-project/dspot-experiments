package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.net.InetAddress;
import junit.framework.TestCase;
import org.junit.Assert;


public class InetAddressTest extends TestCase {
    public void test_inetAddress() throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        String text = JSON.toJSONString(address);
        Assert.assertEquals(JSON.toJSONString(address.getHostAddress()), text);
        InetAddress address2 = JSON.parseObject(text, InetAddress.class);
        Assert.assertEquals(address, address2);
        ParserConfig.getGlobalInstance().getDeserializer(InetAddress.class);
    }

    public void test_null() throws Exception {
        Assert.assertEquals(null, JSON.parseObject("null", InetAddress.class));
    }

    public void test_empty() throws Exception {
        Assert.assertEquals(null, JSON.parseObject("\"\"", InetAddress.class));
    }
}

