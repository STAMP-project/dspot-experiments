package redis.clients.jedis.tests;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;


public class HostAndPortTest {
    @Test
    public void checkExtractParts() throws Exception {
        String host = "2a11:1b1:0:111:e111:1f11:1111:1f1e:1999";
        String port = "6379";
        Assert.assertArrayEquals(new String[]{ host, port }, HostAndPort.extractParts(((host + ":") + port)));
        host = "";
        port = "";
        Assert.assertArrayEquals(new String[]{ host, port }, HostAndPort.extractParts(((host + ":") + port)));
        host = "localhost";
        port = "";
        Assert.assertArrayEquals(new String[]{ host, port }, HostAndPort.extractParts(((host + ":") + port)));
        host = "";
        port = "6379";
        Assert.assertArrayEquals(new String[]{ host, port }, HostAndPort.extractParts(((host + ":") + port)));
        host = "11:22:33:44:55";
        port = "";
        Assert.assertArrayEquals(new String[]{ host, port }, HostAndPort.extractParts(((host + ":") + port)));
    }

    @Test
    public void checkParseString() throws Exception {
        String host = "2a11:1b1:0:111:e111:1f11:1111:1f1e:1999";
        int port = 6379;
        HostAndPort hp = HostAndPort.parseString(((host + ":") + (Integer.toString(port))));
        Assert.assertEquals(host, hp.getHost());
        Assert.assertEquals(port, hp.getPort());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkParseStringWithoutPort() throws Exception {
        String host = "localhost";
        HostAndPort.parseString((host + ":"));
    }

    @Test
    public void checkConvertHost() {
        String host = "2a11:1b1:0:111:e111:1f11:1111:1f1e";
        Assert.assertEquals(host, HostAndPort.convertHost(host));
    }
}

