package redis.clients.jedis.tests.commands;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.util.SafeEncoder;


public class HyperLogLogCommandsTest extends JedisCommandTestBase {
    @Test
    public void pfadd() {
        long status = jedis.pfadd("foo", "a");
        Assert.assertEquals(1, status);
        status = jedis.pfadd("foo", "a");
        Assert.assertEquals(0, status);
    }

    @Test
    public void pfaddBinary() {
        byte[] bFoo = SafeEncoder.encode("foo");
        byte[] bBar = SafeEncoder.encode("bar");
        byte[] bBar2 = SafeEncoder.encode("bar2");
        long status = jedis.pfadd(bFoo, bBar, bBar2);
        Assert.assertEquals(1, status);
        status = jedis.pfadd(bFoo, bBar, bBar2);
        Assert.assertEquals(0, status);
    }

    @Test
    public void pfcount() {
        long status = jedis.pfadd("hll", "foo", "bar", "zap");
        Assert.assertEquals(1, status);
        status = jedis.pfadd("hll", "zap", "zap", "zap");
        Assert.assertEquals(0, status);
        status = jedis.pfadd("hll", "foo", "bar");
        Assert.assertEquals(0, status);
        status = jedis.pfcount("hll");
        Assert.assertEquals(3, status);
    }

    @Test
    public void pfcounts() {
        long status = jedis.pfadd("hll_1", "foo", "bar", "zap");
        Assert.assertEquals(1, status);
        status = jedis.pfadd("hll_2", "foo", "bar", "zap");
        Assert.assertEquals(1, status);
        status = jedis.pfadd("hll_3", "foo", "bar", "baz");
        Assert.assertEquals(1, status);
        status = jedis.pfcount("hll_1");
        Assert.assertEquals(3, status);
        status = jedis.pfcount("hll_2");
        Assert.assertEquals(3, status);
        status = jedis.pfcount("hll_3");
        Assert.assertEquals(3, status);
        status = jedis.pfcount("hll_1", "hll_2");
        Assert.assertEquals(3, status);
        status = jedis.pfcount("hll_1", "hll_2", "hll_3");
        Assert.assertEquals(4, status);
    }

    @Test
    public void pfcountBinary() {
        byte[] bHll = SafeEncoder.encode("hll");
        byte[] bFoo = SafeEncoder.encode("foo");
        byte[] bBar = SafeEncoder.encode("bar");
        byte[] bZap = SafeEncoder.encode("zap");
        long status = jedis.pfadd(bHll, bFoo, bBar, bZap);
        Assert.assertEquals(1, status);
        status = jedis.pfadd(bHll, bZap, bZap, bZap);
        Assert.assertEquals(0, status);
        status = jedis.pfadd(bHll, bFoo, bBar);
        Assert.assertEquals(0, status);
        status = jedis.pfcount(bHll);
        Assert.assertEquals(3, status);
    }

    @Test
    public void pfmerge() {
        long status = jedis.pfadd("hll1", "foo", "bar", "zap", "a");
        Assert.assertEquals(1, status);
        status = jedis.pfadd("hll2", "a", "b", "c", "foo");
        Assert.assertEquals(1, status);
        String mergeStatus = jedis.pfmerge("hll3", "hll1", "hll2");
        Assert.assertEquals("OK", mergeStatus);
        status = jedis.pfcount("hll3");
        Assert.assertEquals(6, status);
    }

    @Test
    public void pfmergeBinary() {
        byte[] bHll1 = SafeEncoder.encode("hll1");
        byte[] bHll2 = SafeEncoder.encode("hll2");
        byte[] bHll3 = SafeEncoder.encode("hll3");
        byte[] bFoo = SafeEncoder.encode("foo");
        byte[] bBar = SafeEncoder.encode("bar");
        byte[] bZap = SafeEncoder.encode("zap");
        byte[] bA = SafeEncoder.encode("a");
        byte[] bB = SafeEncoder.encode("b");
        byte[] bC = SafeEncoder.encode("c");
        long status = jedis.pfadd(bHll1, bFoo, bBar, bZap, bA);
        Assert.assertEquals(1, status);
        status = jedis.pfadd(bHll2, bA, bB, bC, bFoo);
        Assert.assertEquals(1, status);
        String mergeStatus = jedis.pfmerge(bHll3, bHll1, bHll2);
        Assert.assertEquals("OK", mergeStatus);
        status = jedis.pfcount("hll3");
        Assert.assertEquals(6, status);
    }
}

