package redis.clients.jedis.tests.commands;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.tests.HostAndPortUtil;


public class ConnectionHandlingCommandsTest extends JedisCommandTestBase {
    protected static HostAndPort hnp = HostAndPortUtil.getRedisServers().get(0);

    @Test
    public void quit() {
        Assert.assertEquals("OK", jedis.quit());
    }

    @Test
    public void binary_quit() {
        BinaryJedis bj = new BinaryJedis(ConnectionHandlingCommandsTest.hnp.getHost());
        Assert.assertEquals("OK", bj.quit());
    }
}

