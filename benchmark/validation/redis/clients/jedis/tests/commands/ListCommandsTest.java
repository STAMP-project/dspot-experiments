package redis.clients.jedis.tests.commands;


import Client.LIST_POSITION.AFTER;
import Client.LIST_POSITION.BEFORE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;


public class ListCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    final byte[] bcar = new byte[]{ 9, 10, 11, 12 };

    final byte[] bA = new byte[]{ 10 };

    final byte[] bB = new byte[]{ 11 };

    final byte[] bC = new byte[]{ 12 };

    final byte[] b1 = new byte[]{ 1 };

    final byte[] b2 = new byte[]{ 2 };

    final byte[] b3 = new byte[]{ 3 };

    final byte[] bhello = new byte[]{ 4, 2 };

    final byte[] bx = new byte[]{ 2, 4 };

    final byte[] bdst = new byte[]{ 17, 18, 19, 20 };

    @Test
    public void rpush() {
        long size = jedis.rpush("foo", "bar");
        Assert.assertEquals(1, size);
        size = jedis.rpush("foo", "foo");
        Assert.assertEquals(2, size);
        size = jedis.rpush("foo", "bar", "foo");
        Assert.assertEquals(4, size);
        // Binary
        long bsize = jedis.rpush(bfoo, bbar);
        Assert.assertEquals(1, bsize);
        bsize = jedis.rpush(bfoo, bfoo);
        Assert.assertEquals(2, bsize);
        bsize = jedis.rpush(bfoo, bbar, bfoo);
        Assert.assertEquals(4, bsize);
    }

    @Test
    public void lpush() {
        long size = jedis.lpush("foo", "bar");
        Assert.assertEquals(1, size);
        size = jedis.lpush("foo", "foo");
        Assert.assertEquals(2, size);
        size = jedis.lpush("foo", "bar", "foo");
        Assert.assertEquals(4, size);
        // Binary
        long bsize = jedis.lpush(bfoo, bbar);
        Assert.assertEquals(1, bsize);
        bsize = jedis.lpush(bfoo, bfoo);
        Assert.assertEquals(2, bsize);
        bsize = jedis.lpush(bfoo, bbar, bfoo);
        Assert.assertEquals(4, bsize);
    }

    @Test
    public void llen() {
        Assert.assertEquals(0, jedis.llen("foo").intValue());
        jedis.lpush("foo", "bar");
        jedis.lpush("foo", "car");
        Assert.assertEquals(2, jedis.llen("foo").intValue());
        // Binary
        Assert.assertEquals(0, jedis.llen(bfoo).intValue());
        jedis.lpush(bfoo, bbar);
        jedis.lpush(bfoo, bcar);
        Assert.assertEquals(2, jedis.llen(bfoo).intValue());
    }

    @Test
    public void llenNotOnList() {
        try {
            jedis.set("foo", "bar");
            jedis.llen("foo");
            Assert.fail("JedisDataException expected");
        } catch (final JedisDataException e) {
        }
        // Binary
        try {
            jedis.set(bfoo, bbar);
            jedis.llen(bfoo);
            Assert.fail("JedisDataException expected");
        } catch (final JedisDataException e) {
        }
    }

    @Test
    public void lrange() {
        jedis.rpush("foo", "a");
        jedis.rpush("foo", "b");
        jedis.rpush("foo", "c");
        List<String> expected = new ArrayList<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        List<String> range = jedis.lrange("foo", 0, 2);
        Assert.assertEquals(expected, range);
        range = jedis.lrange("foo", 0, 20);
        Assert.assertEquals(expected, range);
        expected = new ArrayList<String>();
        expected.add("b");
        expected.add("c");
        range = jedis.lrange("foo", 1, 2);
        Assert.assertEquals(expected, range);
        expected = new ArrayList<String>();
        range = jedis.lrange("foo", 2, 1);
        Assert.assertEquals(expected, range);
        // Binary
        jedis.rpush(bfoo, bA);
        jedis.rpush(bfoo, bB);
        jedis.rpush(bfoo, bC);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bA);
        bexpected.add(bB);
        bexpected.add(bC);
        List<byte[]> brange = jedis.lrange(bfoo, 0, 2);
        assertEquals(bexpected, brange);
        brange = jedis.lrange(bfoo, 0, 20);
        assertEquals(bexpected, brange);
        bexpected = new ArrayList<byte[]>();
        bexpected.add(bB);
        bexpected.add(bC);
        brange = jedis.lrange(bfoo, 1, 2);
        assertEquals(bexpected, brange);
        bexpected = new ArrayList<byte[]>();
        brange = jedis.lrange(bfoo, 2, 1);
        assertEquals(bexpected, brange);
    }

    @Test
    public void ltrim() {
        jedis.lpush("foo", "1");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "3");
        String status = jedis.ltrim("foo", 0, 1);
        List<String> expected = new ArrayList<String>();
        expected.add("3");
        expected.add("2");
        Assert.assertEquals("OK", status);
        Assert.assertEquals(2, jedis.llen("foo").intValue());
        Assert.assertEquals(expected, jedis.lrange("foo", 0, 100));
        // Binary
        jedis.lpush(bfoo, b1);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b3);
        String bstatus = jedis.ltrim(bfoo, 0, 1);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b3);
        bexpected.add(b2);
        Assert.assertEquals("OK", bstatus);
        Assert.assertEquals(2, jedis.llen(bfoo).intValue());
        assertEquals(bexpected, jedis.lrange(bfoo, 0, 100));
    }

    @Test
    public void lindex() {
        jedis.lpush("foo", "1");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "3");
        List<String> expected = new ArrayList<String>();
        expected.add("3");
        expected.add("bar");
        expected.add("1");
        String status = jedis.lset("foo", 1, "bar");
        Assert.assertEquals("OK", status);
        Assert.assertEquals(expected, jedis.lrange("foo", 0, 100));
        // Binary
        jedis.lpush(bfoo, b1);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b3);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b3);
        bexpected.add(bbar);
        bexpected.add(b1);
        String bstatus = jedis.lset(bfoo, 1, bbar);
        Assert.assertEquals("OK", bstatus);
        assertEquals(bexpected, jedis.lrange(bfoo, 0, 100));
    }

    @Test
    public void lset() {
        jedis.lpush("foo", "1");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "3");
        Assert.assertEquals("3", jedis.lindex("foo", 0));
        assertEquals(null, jedis.lindex("foo", 100));
        // Binary
        jedis.lpush(bfoo, b1);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b3);
        Assert.assertArrayEquals(b3, jedis.lindex(bfoo, 0));
        assertEquals(null, jedis.lindex(bfoo, 100));
    }

    @Test
    public void lrem() {
        jedis.lpush("foo", "hello");
        jedis.lpush("foo", "hello");
        jedis.lpush("foo", "x");
        jedis.lpush("foo", "hello");
        jedis.lpush("foo", "c");
        jedis.lpush("foo", "b");
        jedis.lpush("foo", "a");
        long count = jedis.lrem("foo", (-2), "hello");
        List<String> expected = new ArrayList<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        expected.add("hello");
        expected.add("x");
        Assert.assertEquals(2, count);
        Assert.assertEquals(expected, jedis.lrange("foo", 0, 1000));
        Assert.assertEquals(0, jedis.lrem("bar", 100, "foo").intValue());
        // Binary
        jedis.lpush(bfoo, bhello);
        jedis.lpush(bfoo, bhello);
        jedis.lpush(bfoo, bx);
        jedis.lpush(bfoo, bhello);
        jedis.lpush(bfoo, bC);
        jedis.lpush(bfoo, bB);
        jedis.lpush(bfoo, bA);
        long bcount = jedis.lrem(bfoo, (-2), bhello);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bA);
        bexpected.add(bB);
        bexpected.add(bC);
        bexpected.add(bhello);
        bexpected.add(bx);
        Assert.assertEquals(2, bcount);
        assertEquals(bexpected, jedis.lrange(bfoo, 0, 1000));
        Assert.assertEquals(0, jedis.lrem(bbar, 100, bfoo).intValue());
    }

    @Test
    public void lpop() {
        jedis.rpush("foo", "a");
        jedis.rpush("foo", "b");
        jedis.rpush("foo", "c");
        String element = jedis.lpop("foo");
        Assert.assertEquals("a", element);
        List<String> expected = new ArrayList<String>();
        expected.add("b");
        expected.add("c");
        Assert.assertEquals(expected, jedis.lrange("foo", 0, 1000));
        jedis.lpop("foo");
        jedis.lpop("foo");
        element = jedis.lpop("foo");
        Assert.assertEquals(null, element);
        // Binary
        jedis.rpush(bfoo, bA);
        jedis.rpush(bfoo, bB);
        jedis.rpush(bfoo, bC);
        byte[] belement = jedis.lpop(bfoo);
        Assert.assertArrayEquals(bA, belement);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bB);
        bexpected.add(bC);
        assertEquals(bexpected, jedis.lrange(bfoo, 0, 1000));
        jedis.lpop(bfoo);
        jedis.lpop(bfoo);
        belement = jedis.lpop(bfoo);
        Assert.assertEquals(null, belement);
    }

    @Test
    public void rpop() {
        jedis.rpush("foo", "a");
        jedis.rpush("foo", "b");
        jedis.rpush("foo", "c");
        String element = jedis.rpop("foo");
        Assert.assertEquals("c", element);
        List<String> expected = new ArrayList<String>();
        expected.add("a");
        expected.add("b");
        Assert.assertEquals(expected, jedis.lrange("foo", 0, 1000));
        jedis.rpop("foo");
        jedis.rpop("foo");
        element = jedis.rpop("foo");
        Assert.assertEquals(null, element);
        // Binary
        jedis.rpush(bfoo, bA);
        jedis.rpush(bfoo, bB);
        jedis.rpush(bfoo, bC);
        byte[] belement = jedis.rpop(bfoo);
        Assert.assertArrayEquals(bC, belement);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bA);
        bexpected.add(bB);
        assertEquals(bexpected, jedis.lrange(bfoo, 0, 1000));
        jedis.rpop(bfoo);
        jedis.rpop(bfoo);
        belement = jedis.rpop(bfoo);
        Assert.assertEquals(null, belement);
    }

    @Test
    public void rpoplpush() {
        jedis.rpush("foo", "a");
        jedis.rpush("foo", "b");
        jedis.rpush("foo", "c");
        jedis.rpush("dst", "foo");
        jedis.rpush("dst", "bar");
        String element = jedis.rpoplpush("foo", "dst");
        Assert.assertEquals("c", element);
        List<String> srcExpected = new ArrayList<String>();
        srcExpected.add("a");
        srcExpected.add("b");
        List<String> dstExpected = new ArrayList<String>();
        dstExpected.add("c");
        dstExpected.add("foo");
        dstExpected.add("bar");
        Assert.assertEquals(srcExpected, jedis.lrange("foo", 0, 1000));
        Assert.assertEquals(dstExpected, jedis.lrange("dst", 0, 1000));
        // Binary
        jedis.rpush(bfoo, bA);
        jedis.rpush(bfoo, bB);
        jedis.rpush(bfoo, bC);
        jedis.rpush(bdst, bfoo);
        jedis.rpush(bdst, bbar);
        byte[] belement = jedis.rpoplpush(bfoo, bdst);
        Assert.assertArrayEquals(bC, belement);
        List<byte[]> bsrcExpected = new ArrayList<byte[]>();
        bsrcExpected.add(bA);
        bsrcExpected.add(bB);
        List<byte[]> bdstExpected = new ArrayList<byte[]>();
        bdstExpected.add(bC);
        bdstExpected.add(bfoo);
        bdstExpected.add(bbar);
        assertEquals(bsrcExpected, jedis.lrange(bfoo, 0, 1000));
        assertEquals(bdstExpected, jedis.lrange(bdst, 0, 1000));
    }

    @Test
    public void blpop() throws InterruptedException {
        List<String> result = jedis.blpop(1, "foo");
        Assert.assertNull(result);
        jedis.lpush("foo", "bar");
        result = jedis.blpop(1, "foo");
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("foo", result.get(0));
        Assert.assertEquals("bar", result.get(1));
        // Binary
        jedis.lpush(bfoo, bbar);
        List<byte[]> bresult = jedis.blpop(1, bfoo);
        Assert.assertNotNull(bresult);
        Assert.assertEquals(2, bresult.size());
        Assert.assertArrayEquals(bfoo, bresult.get(0));
        Assert.assertArrayEquals(bbar, bresult.get(1));
    }

    @Test
    public void brpop() throws InterruptedException {
        List<String> result = jedis.brpop(1, "foo");
        Assert.assertNull(result);
        jedis.lpush("foo", "bar");
        result = jedis.brpop(1, "foo");
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("foo", result.get(0));
        Assert.assertEquals("bar", result.get(1));
        // Binary
        jedis.lpush(bfoo, bbar);
        List<byte[]> bresult = jedis.brpop(1, bfoo);
        Assert.assertNotNull(bresult);
        Assert.assertEquals(2, bresult.size());
        Assert.assertArrayEquals(bfoo, bresult.get(0));
        Assert.assertArrayEquals(bbar, bresult.get(1));
    }

    @Test
    public void lpushx() {
        long status = jedis.lpushx("foo", "bar");
        Assert.assertEquals(0, status);
        jedis.lpush("foo", "a");
        status = jedis.lpushx("foo", "b");
        Assert.assertEquals(2, status);
        // Binary
        long bstatus = jedis.lpushx(bfoo, bbar);
        Assert.assertEquals(0, bstatus);
        jedis.lpush(bfoo, bA);
        bstatus = jedis.lpushx(bfoo, bB);
        Assert.assertEquals(2, bstatus);
    }

    @Test
    public void rpushx() {
        long status = jedis.rpushx("foo", "bar");
        Assert.assertEquals(0, status);
        jedis.lpush("foo", "a");
        status = jedis.rpushx("foo", "b");
        Assert.assertEquals(2, status);
        // Binary
        long bstatus = jedis.rpushx(bfoo, bbar);
        Assert.assertEquals(0, bstatus);
        jedis.lpush(bfoo, bA);
        bstatus = jedis.rpushx(bfoo, bB);
        Assert.assertEquals(2, bstatus);
    }

    @Test
    public void linsert() {
        long status = jedis.linsert("foo", BEFORE, "bar", "car");
        Assert.assertEquals(0, status);
        jedis.lpush("foo", "a");
        status = jedis.linsert("foo", AFTER, "a", "b");
        Assert.assertEquals(2, status);
        List<String> actual = jedis.lrange("foo", 0, 100);
        List<String> expected = new ArrayList<String>();
        expected.add("a");
        expected.add("b");
        Assert.assertEquals(expected, actual);
        status = jedis.linsert("foo", BEFORE, "bar", "car");
        Assert.assertEquals((-1), status);
        // Binary
        long bstatus = jedis.linsert(bfoo, BEFORE, bbar, bcar);
        Assert.assertEquals(0, bstatus);
        jedis.lpush(bfoo, bA);
        bstatus = jedis.linsert(bfoo, AFTER, bA, bB);
        Assert.assertEquals(2, bstatus);
        List<byte[]> bactual = jedis.lrange(bfoo, 0, 100);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bA);
        bexpected.add(bB);
        assertEquals(bexpected, bactual);
        bstatus = jedis.linsert(bfoo, BEFORE, bbar, bcar);
        Assert.assertEquals((-1), bstatus);
    }

    @Test
    public void brpoplpush() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                    Jedis j = createJedis();
                    j.lpush("foo", "a");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        String element = jedis.brpoplpush("foo", "bar", 0);
        Assert.assertEquals("a", element);
        Assert.assertEquals(1, jedis.llen("bar").longValue());
        Assert.assertEquals("a", jedis.lrange("bar", 0, (-1)).get(0));
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                    Jedis j = createJedis();
                    j.lpush("foo", "a");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        byte[] brpoplpush = jedis.brpoplpush("foo".getBytes(), "bar".getBytes(), 0);
        Assert.assertTrue(Arrays.equals("a".getBytes(), brpoplpush));
        Assert.assertEquals(1, jedis.llen("bar").longValue());
        Assert.assertEquals("a", jedis.lrange("bar", 0, (-1)).get(0));
    }
}

