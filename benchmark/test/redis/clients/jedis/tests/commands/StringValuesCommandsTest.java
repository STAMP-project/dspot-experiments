package redis.clients.jedis.tests.commands;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisDataException;


public class StringValuesCommandsTest extends JedisCommandTestBase {
    @Test
    public void setAndGet() {
        String status = jedis.set("foo", "bar");
        Assert.assertEquals("OK", status);
        String value = jedis.get("foo");
        Assert.assertEquals("bar", value);
        assertEquals(null, jedis.get("bar"));
    }

    @Test
    public void getSet() {
        String value = jedis.getSet("foo", "bar");
        Assert.assertEquals(null, value);
        value = jedis.get("foo");
        Assert.assertEquals("bar", value);
    }

    @Test
    public void mget() {
        List<String> values = jedis.mget("foo", "bar");
        List<String> expected = new ArrayList<String>();
        expected.add(null);
        expected.add(null);
        Assert.assertEquals(expected, values);
        jedis.set("foo", "bar");
        expected = new ArrayList<String>();
        expected.add("bar");
        expected.add(null);
        values = jedis.mget("foo", "bar");
        Assert.assertEquals(expected, values);
        jedis.set("bar", "foo");
        expected = new ArrayList<String>();
        expected.add("bar");
        expected.add("foo");
        values = jedis.mget("foo", "bar");
        Assert.assertEquals(expected, values);
    }

    @Test
    public void setnx() {
        long status = jedis.setnx("foo", "bar");
        Assert.assertEquals(1, status);
        Assert.assertEquals("bar", jedis.get("foo"));
        status = jedis.setnx("foo", "bar2");
        Assert.assertEquals(0, status);
        Assert.assertEquals("bar", jedis.get("foo"));
    }

    @Test
    public void setex() {
        String status = jedis.setex("foo", 20, "bar");
        Assert.assertEquals("OK", status);
        long ttl = jedis.ttl("foo");
        Assert.assertTrue(((ttl > 0) && (ttl <= 20)));
    }

    @Test
    public void mset() {
        String status = jedis.mset("foo", "bar", "bar", "foo");
        Assert.assertEquals("OK", status);
        Assert.assertEquals("bar", jedis.get("foo"));
        Assert.assertEquals("foo", jedis.get("bar"));
    }

    @Test
    public void msetnx() {
        long status = jedis.msetnx("foo", "bar", "bar", "foo");
        Assert.assertEquals(1, status);
        Assert.assertEquals("bar", jedis.get("foo"));
        Assert.assertEquals("foo", jedis.get("bar"));
        status = jedis.msetnx("foo", "bar1", "bar2", "foo2");
        Assert.assertEquals(0, status);
        Assert.assertEquals("bar", jedis.get("foo"));
        Assert.assertEquals("foo", jedis.get("bar"));
    }

    @Test(expected = JedisDataException.class)
    public void incrWrongValue() {
        jedis.set("foo", "bar");
        jedis.incr("foo");
    }

    @Test
    public void incr() {
        long value = jedis.incr("foo");
        Assert.assertEquals(1, value);
        value = jedis.incr("foo");
        Assert.assertEquals(2, value);
    }

    @Test(expected = JedisDataException.class)
    public void incrByWrongValue() {
        jedis.set("foo", "bar");
        jedis.incrBy("foo", 2);
    }

    @Test
    public void incrBy() {
        long value = jedis.incrBy("foo", 2);
        Assert.assertEquals(2, value);
        value = jedis.incrBy("foo", 2);
        Assert.assertEquals(4, value);
    }

    @Test(expected = JedisDataException.class)
    public void incrByFloatWrongValue() {
        jedis.set("foo", "bar");
        jedis.incrByFloat("foo", 2.0);
    }

    @Test(expected = JedisDataException.class)
    public void decrWrongValue() {
        jedis.set("foo", "bar");
        jedis.decr("foo");
    }

    @Test
    public void decr() {
        long value = jedis.decr("foo");
        Assert.assertEquals((-1), value);
        value = jedis.decr("foo");
        Assert.assertEquals((-2), value);
    }

    @Test(expected = JedisDataException.class)
    public void decrByWrongValue() {
        jedis.set("foo", "bar");
        jedis.decrBy("foo", 2);
    }

    @Test
    public void decrBy() {
        long value = jedis.decrBy("foo", 2);
        Assert.assertEquals((-2), value);
        value = jedis.decrBy("foo", 2);
        Assert.assertEquals((-4), value);
    }

    @Test
    public void append() {
        long value = jedis.append("foo", "bar");
        Assert.assertEquals(3, value);
        Assert.assertEquals("bar", jedis.get("foo"));
        value = jedis.append("foo", "bar");
        Assert.assertEquals(6, value);
        Assert.assertEquals("barbar", jedis.get("foo"));
    }

    @Test
    public void substr() {
        jedis.set("s", "This is a string");
        Assert.assertEquals("This", jedis.substr("s", 0, 3));
        Assert.assertEquals("ing", jedis.substr("s", (-3), (-1)));
        Assert.assertEquals("This is a string", jedis.substr("s", 0, (-1)));
        Assert.assertEquals(" string", jedis.substr("s", 9, 100000));
    }

    @Test
    public void strlen() {
        jedis.set("s", "This is a string");
        Assert.assertEquals("This is a string".length(), jedis.strlen("s").intValue());
    }

    @Test
    public void incrLargeNumbers() {
        long value = jedis.incr("foo");
        Assert.assertEquals(1, value);
        Assert.assertEquals((1L + (Integer.MAX_VALUE)), ((long) (jedis.incrBy("foo", Integer.MAX_VALUE))));
    }

    @Test(expected = JedisDataException.class)
    public void incrReallyLargeNumbers() {
        jedis.set("foo", Long.toString(Long.MAX_VALUE));
        long value = jedis.incr("foo");
        Assert.assertEquals(Long.MIN_VALUE, value);
    }

    @Test
    public void incrByFloat() {
        double value = jedis.incrByFloat("foo", 10.5);
        Assert.assertEquals(10.5, value, 0.0);
        value = jedis.incrByFloat("foo", 0.1);
        Assert.assertEquals(10.6, value, 0.0);
    }

    @Test
    public void psetex() {
        String status = jedis.psetex("foo", 20000, "bar");
        Assert.assertEquals("OK", status);
        long ttl = jedis.ttl("foo");
        Assert.assertTrue(((ttl > 0) && (ttl <= 20000)));
    }
}

