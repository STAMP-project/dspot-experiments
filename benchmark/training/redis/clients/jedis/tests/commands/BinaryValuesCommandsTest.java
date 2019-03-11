package redis.clients.jedis.tests.commands;


import Keyword.OK;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisDataException;


public class BinaryValuesCommandsTest extends JedisCommandTestBase {
    byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    byte[] bxx = new byte[]{ 120, 120 };

    byte[] bnx = new byte[]{ 110, 120 };

    byte[] bex = new byte[]{ 101, 120 };

    byte[] bpx = new byte[]{ 112, 120 };

    int expireSeconds = 2;

    long expireMillis = (expireSeconds) * 1000;

    byte[] binaryValue;

    @Test
    public void setAndGet() {
        String status = jedis.set(bfoo, binaryValue);
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        byte[] value = jedis.get(bfoo);
        Assert.assertTrue(Arrays.equals(binaryValue, value));
        Assert.assertNull(jedis.get(bbar));
    }

    @Test
    public void setNxExAndGet() {
        String status = jedis.set(bfoo, binaryValue, setParams().nx().ex(expireSeconds));
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        byte[] value = jedis.get(bfoo);
        Assert.assertTrue(Arrays.equals(binaryValue, value));
        Assert.assertNull(jedis.get(bbar));
    }

    @Test
    public void setIfNotExistAndGet() {
        String status = jedis.set(bfoo, binaryValue);
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        // nx should fail if value exists
        String statusFail = jedis.set(bfoo, binaryValue, setParams().nx().ex(expireSeconds));
        Assert.assertNull(statusFail);
        byte[] value = jedis.get(bfoo);
        Assert.assertTrue(Arrays.equals(binaryValue, value));
        Assert.assertNull(jedis.get(bbar));
    }

    @Test
    public void setIfExistAndGet() {
        String status = jedis.set(bfoo, binaryValue);
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        // nx should fail if value exists
        String statusSuccess = jedis.set(bfoo, binaryValue, setParams().xx().ex(expireSeconds));
        Assert.assertTrue(OK.name().equalsIgnoreCase(statusSuccess));
        byte[] value = jedis.get(bfoo);
        Assert.assertTrue(Arrays.equals(binaryValue, value));
        Assert.assertNull(jedis.get(bbar));
    }

    @Test
    public void setFailIfNotExistAndGet() {
        // xx should fail if value does NOT exists
        String statusFail = jedis.set(bfoo, binaryValue, setParams().xx().ex(expireSeconds));
        Assert.assertNull(statusFail);
    }

    @Test
    public void setAndExpireMillis() {
        String status = jedis.set(bfoo, binaryValue, setParams().nx().px(expireMillis));
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        long ttl = jedis.ttl(bfoo);
        Assert.assertTrue(((ttl > 0) && (ttl <= (expireSeconds))));
    }

    @Test
    public void setAndExpire() {
        String status = jedis.set(bfoo, binaryValue, setParams().nx().ex(expireSeconds));
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        long ttl = jedis.ttl(bfoo);
        Assert.assertTrue(((ttl > 0) && (ttl <= (expireSeconds))));
    }

    @Test
    public void getSet() {
        byte[] value = jedis.getSet(bfoo, binaryValue);
        Assert.assertNull(value);
        value = jedis.get(bfoo);
        Assert.assertTrue(Arrays.equals(binaryValue, value));
    }

    @Test
    public void mget() {
        List<byte[]> values = jedis.mget(bfoo, bbar);
        List<byte[]> expected = new ArrayList<byte[]>();
        expected.add(null);
        expected.add(null);
        assertEquals(expected, values);
        jedis.set(bfoo, binaryValue);
        expected = new ArrayList<byte[]>();
        expected.add(binaryValue);
        expected.add(null);
        values = jedis.mget(bfoo, bbar);
        assertEquals(expected, values);
        jedis.set(bbar, bfoo);
        expected = new ArrayList<byte[]>();
        expected.add(binaryValue);
        expected.add(bfoo);
        values = jedis.mget(bfoo, bbar);
        assertEquals(expected, values);
    }

    @Test
    public void setnx() {
        long status = jedis.setnx(bfoo, binaryValue);
        Assert.assertEquals(1, status);
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.get(bfoo)));
        status = jedis.setnx(bfoo, bbar);
        Assert.assertEquals(0, status);
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.get(bfoo)));
    }

    @Test
    public void setex() {
        String status = jedis.setex(bfoo, 20, binaryValue);
        Assert.assertEquals(OK.name(), status);
        long ttl = jedis.ttl(bfoo);
        Assert.assertTrue(((ttl > 0) && (ttl <= 20)));
    }

    @Test
    public void mset() {
        String status = jedis.mset(bfoo, binaryValue, bbar, bfoo);
        Assert.assertEquals(OK.name(), status);
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.get(bfoo)));
        Assert.assertTrue(Arrays.equals(bfoo, jedis.get(bbar)));
    }

    @Test
    public void msetnx() {
        long status = jedis.msetnx(bfoo, binaryValue, bbar, bfoo);
        Assert.assertEquals(1, status);
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.get(bfoo)));
        Assert.assertTrue(Arrays.equals(bfoo, jedis.get(bbar)));
        status = jedis.msetnx(bfoo, bbar, "bar2".getBytes(), "foo2".getBytes());
        Assert.assertEquals(0, status);
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.get(bfoo)));
        Assert.assertTrue(Arrays.equals(bfoo, jedis.get(bbar)));
    }

    @Test(expected = JedisDataException.class)
    public void incrWrongValue() {
        jedis.set(bfoo, binaryValue);
        jedis.incr(bfoo);
    }

    @Test
    public void incr() {
        long value = jedis.incr(bfoo);
        Assert.assertEquals(1, value);
        value = jedis.incr(bfoo);
        Assert.assertEquals(2, value);
    }

    @Test(expected = JedisDataException.class)
    public void incrByWrongValue() {
        jedis.set(bfoo, binaryValue);
        jedis.incrBy(bfoo, 2);
    }

    @Test
    public void incrBy() {
        long value = jedis.incrBy(bfoo, 2);
        Assert.assertEquals(2, value);
        value = jedis.incrBy(bfoo, 2);
        Assert.assertEquals(4, value);
    }

    @Test(expected = JedisDataException.class)
    public void decrWrongValue() {
        jedis.set(bfoo, binaryValue);
        jedis.decr(bfoo);
    }

    @Test
    public void decr() {
        long value = jedis.decr(bfoo);
        Assert.assertEquals((-1), value);
        value = jedis.decr(bfoo);
        Assert.assertEquals((-2), value);
    }

    @Test(expected = JedisDataException.class)
    public void decrByWrongValue() {
        jedis.set(bfoo, binaryValue);
        jedis.decrBy(bfoo, 2);
    }

    @Test
    public void decrBy() {
        long value = jedis.decrBy(bfoo, 2);
        Assert.assertEquals((-2), value);
        value = jedis.decrBy(bfoo, 2);
        Assert.assertEquals((-4), value);
    }

    @Test
    public void append() {
        byte[] first512 = new byte[512];
        System.arraycopy(binaryValue, 0, first512, 0, 512);
        long value = jedis.append(bfoo, first512);
        Assert.assertEquals(512, value);
        Assert.assertTrue(Arrays.equals(first512, jedis.get(bfoo)));
        byte[] rest = new byte[(binaryValue.length) - 512];
        System.arraycopy(binaryValue, 512, rest, 0, ((binaryValue.length) - 512));
        value = jedis.append(bfoo, rest);
        Assert.assertEquals(binaryValue.length, value);
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.get(bfoo)));
    }

    @Test
    public void substr() {
        jedis.set(bfoo, binaryValue);
        byte[] first512 = new byte[512];
        System.arraycopy(binaryValue, 0, first512, 0, 512);
        byte[] rfirst512 = jedis.substr(bfoo, 0, 511);
        Assert.assertTrue(Arrays.equals(first512, rfirst512));
        byte[] last512 = new byte[512];
        System.arraycopy(binaryValue, ((binaryValue.length) - 512), last512, 0, 512);
        Assert.assertTrue(Arrays.equals(last512, jedis.substr(bfoo, (-512), (-1))));
        Assert.assertTrue(Arrays.equals(binaryValue, jedis.substr(bfoo, 0, (-1))));
        Assert.assertTrue(Arrays.equals(last512, jedis.substr(bfoo, ((binaryValue.length) - 512), 100000)));
    }

    @Test
    public void strlen() {
        jedis.set(bfoo, binaryValue);
        Assert.assertEquals(binaryValue.length, jedis.strlen(bfoo).intValue());
    }
}

