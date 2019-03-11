package cc.blynk.server.admin.http.handlers;


import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 26.01.16.
 */
public class IpFilterHandlerTest {
    @Test
    public void testSingleIPFilterWork() throws Exception {
        String[] data = new String[1];
        data[0] = "192.168.0.50";
        IpFilterHandler ipFilterHandler = new IpFilterHandler(data);
        Assert.assertTrue(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.0.50")));
        Assert.assertFalse(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.0.51")));
        Assert.assertFalse(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.1.50")));
    }

    @Test
    public void testCIDRNotationIPFilterWork() throws Exception {
        String[] data = new String[2];
        data[0] = "192.168.100.100";
        data[1] = "192.168.0.50/24";
        IpFilterHandler ipFilterHandler = new IpFilterHandler(data);
        for (int i = 0; i <= 255; i++) {
            Assert.assertTrue(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress(String.format("192.168.0.%d", i))));
        }
        Assert.assertTrue(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.100.100")));
        Assert.assertFalse(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.1.0")));
        Assert.assertFalse(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.100.0")));
        Assert.assertFalse(ipFilterHandler.accept(null, IpFilterHandlerTest.newSockAddress("192.168.100.101")));
    }
}

