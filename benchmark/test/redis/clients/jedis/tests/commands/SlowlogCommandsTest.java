package redis.clients.jedis.tests.commands;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.util.Slowlog;


public class SlowlogCommandsTest extends JedisCommandTestBase {
    @Test
    public void slowlog() {
        // do something
        jedis.configSet("slowlog-log-slower-than", "0");
        jedis.set("foo", "bar");
        jedis.set("foo2", "bar2");
        List<Slowlog> reducedLog = jedis.slowlogGet(1);
        Assert.assertEquals(1, reducedLog.size());
        Slowlog log = reducedLog.get(0);
        Assert.assertTrue(((log.getId()) > 0));
        Assert.assertTrue(((log.getTimeStamp()) > 0));
        Assert.assertTrue(((log.getExecutionTime()) > 0));
        Assert.assertNotNull(log.getArgs());
        List<byte[]> breducedLog = jedis.slowlogGetBinary(1);
        Assert.assertEquals(1, breducedLog.size());
        List<Slowlog> log1 = jedis.slowlogGet();
        List<byte[]> blog1 = jedis.slowlogGetBinary();
        Assert.assertNotNull(log1);
        Assert.assertNotNull(blog1);
        long len1 = jedis.slowlogLen();
        jedis.slowlogReset();
        List<Slowlog> log2 = jedis.slowlogGet();
        List<byte[]> blog2 = jedis.slowlogGetBinary();
        long len2 = jedis.slowlogLen();
        Assert.assertTrue((len1 > len2));
        Assert.assertTrue(((log1.size()) > (log2.size())));
        Assert.assertTrue(((blog1.size()) > (blog2.size())));
    }
}

