package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import junit.framework.TestCase;
import org.junit.Assert;


public class InetSocketAddressTest extends TestCase {
    public void test_timezone() throws Exception {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 80);
        String text = JSON.toJSONString(address);
        InetSocketAddress address2 = JSON.parseObject(text, InetSocketAddress.class);
        Assert.assertEquals(address, address2);
    }
}

