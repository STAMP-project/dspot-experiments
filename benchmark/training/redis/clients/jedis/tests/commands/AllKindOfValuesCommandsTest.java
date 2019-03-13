package redis.clients.jedis.tests.commands;


import Keyword.OK;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.util.SafeEncoder;


public class AllKindOfValuesCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bfoo1 = new byte[]{ 1, 2, 3, 4, 10 };

    final byte[] bfoo2 = new byte[]{ 1, 2, 3, 4, 11 };

    final byte[] bfoo3 = new byte[]{ 1, 2, 3, 4, 12 };

    final byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    final byte[] bbar1 = new byte[]{ 5, 6, 7, 8, 10 };

    final byte[] bbar2 = new byte[]{ 5, 6, 7, 8, 11 };

    final byte[] bbar3 = new byte[]{ 5, 6, 7, 8, 12 };

    final byte[] bfoobar = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 };

    final byte[] bfoostar = new byte[]{ 1, 2, 3, 4, '*' };

    final byte[] bbarstar = new byte[]{ 5, 6, 7, 8, '*' };

    final byte[] bnx = new byte[]{ 110, 120 };

    final byte[] bex = new byte[]{ 101, 120 };

    final int expireSeconds = 2;

    @Test
    public void ping() {
        String status = jedis.ping();
        Assert.assertEquals("PONG", status);
    }

    @Test
    public void exists() {
        String status = jedis.set("foo", "bar");
        Assert.assertEquals("OK", status);
        status = jedis.set(bfoo, bbar);
        Assert.assertEquals("OK", status);
        boolean reply = jedis.exists("foo");
        Assert.assertTrue(reply);
        reply = jedis.exists(bfoo);
        Assert.assertTrue(reply);
        long lreply = jedis.del("foo");
        Assert.assertEquals(1, lreply);
        lreply = jedis.del(bfoo);
        Assert.assertEquals(1, lreply);
        reply = jedis.exists("foo");
        Assert.assertFalse(reply);
        reply = jedis.exists(bfoo);
        Assert.assertFalse(reply);
    }

    @Test
    public void existsMany() {
        String status = jedis.set("foo1", "bar1");
        Assert.assertEquals("OK", status);
        status = jedis.set("foo2", "bar2");
        Assert.assertEquals("OK", status);
        long reply = jedis.exists("foo1", "foo2");
        Assert.assertEquals(2, reply);
        long lreply = jedis.del("foo1");
        Assert.assertEquals(1, lreply);
        reply = jedis.exists("foo1", "foo2");
        Assert.assertEquals(1, reply);
    }

    @Test
    public void del() {
        jedis.set("foo1", "bar1");
        jedis.set("foo2", "bar2");
        jedis.set("foo3", "bar3");
        long reply = jedis.del("foo1", "foo2", "foo3");
        Assert.assertEquals(3, reply);
        Boolean breply = jedis.exists("foo1");
        Assert.assertFalse(breply);
        breply = jedis.exists("foo2");
        Assert.assertFalse(breply);
        breply = jedis.exists("foo3");
        Assert.assertFalse(breply);
        jedis.set("foo1", "bar1");
        reply = jedis.del("foo1", "foo2");
        Assert.assertEquals(1, reply);
        reply = jedis.del("foo1", "foo2");
        Assert.assertEquals(0, reply);
        // Binary ...
        jedis.set(bfoo1, bbar1);
        jedis.set(bfoo2, bbar2);
        jedis.set(bfoo3, bbar3);
        reply = jedis.del(bfoo1, bfoo2, bfoo3);
        Assert.assertEquals(3, reply);
        breply = jedis.exists(bfoo1);
        Assert.assertFalse(breply);
        breply = jedis.exists(bfoo2);
        Assert.assertFalse(breply);
        breply = jedis.exists(bfoo3);
        Assert.assertFalse(breply);
        jedis.set(bfoo1, bbar1);
        reply = jedis.del(bfoo1, bfoo2);
        Assert.assertEquals(1, reply);
        reply = jedis.del(bfoo1, bfoo2);
        Assert.assertEquals(0, reply);
    }

    @Test
    public void type() {
        jedis.set("foo", "bar");
        String status = jedis.type("foo");
        Assert.assertEquals("string", status);
        // Binary
        jedis.set(bfoo, bbar);
        status = jedis.type(bfoo);
        Assert.assertEquals("string", status);
    }

    @Test
    public void keys() {
        jedis.set("foo", "bar");
        jedis.set("foobar", "bar");
        Set<String> keys = jedis.keys("foo*");
        Set<String> expected = new HashSet<String>();
        expected.add("foo");
        expected.add("foobar");
        Assert.assertEquals(expected, keys);
        expected = new HashSet<String>();
        keys = jedis.keys("bar*");
        Assert.assertEquals(expected, keys);
        // Binary
        jedis.set(bfoo, bbar);
        jedis.set(bfoobar, bbar);
        Set<byte[]> bkeys = jedis.keys(bfoostar);
        Assert.assertEquals(2, bkeys.size());
        Assert.assertTrue(setContains(bkeys, bfoo));
        Assert.assertTrue(setContains(bkeys, bfoobar));
        bkeys = jedis.keys(bbarstar);
        Assert.assertEquals(0, bkeys.size());
    }

    @Test
    public void randomKey() {
        assertEquals(null, jedis.randomKey());
        jedis.set("foo", "bar");
        Assert.assertEquals("foo", jedis.randomKey());
        jedis.set("bar", "foo");
        String randomkey = jedis.randomKey();
        Assert.assertTrue(((randomkey.equals("foo")) || (randomkey.equals("bar"))));
        // Binary
        jedis.del("foo");
        jedis.del("bar");
        assertEquals(null, jedis.randomKey());
        jedis.set(bfoo, bbar);
        Assert.assertArrayEquals(bfoo, jedis.randomBinaryKey());
        jedis.set(bbar, bfoo);
        byte[] randomBkey = jedis.randomBinaryKey();
        Assert.assertTrue(((Arrays.equals(randomBkey, bfoo)) || (Arrays.equals(randomBkey, bbar))));
    }

    @Test
    public void rename() {
        jedis.set("foo", "bar");
        String status = jedis.rename("foo", "bar");
        Assert.assertEquals("OK", status);
        String value = jedis.get("foo");
        Assert.assertEquals(null, value);
        value = jedis.get("bar");
        Assert.assertEquals("bar", value);
        // Binary
        jedis.set(bfoo, bbar);
        String bstatus = jedis.rename(bfoo, bbar);
        Assert.assertEquals("OK", bstatus);
        byte[] bvalue = jedis.get(bfoo);
        Assert.assertEquals(null, bvalue);
        bvalue = jedis.get(bbar);
        Assert.assertArrayEquals(bbar, bvalue);
    }

    @Test
    public void renameOldAndNewAreTheSame() {
        jedis.set("foo", "bar");
        jedis.rename("foo", "foo");
        // Binary
        jedis.set(bfoo, bbar);
        jedis.rename(bfoo, bfoo);
    }

    @Test
    public void renamenx() {
        jedis.set("foo", "bar");
        long status = jedis.renamenx("foo", "bar");
        Assert.assertEquals(1, status);
        jedis.set("foo", "bar");
        status = jedis.renamenx("foo", "bar");
        Assert.assertEquals(0, status);
        // Binary
        jedis.set(bfoo, bbar);
        long bstatus = jedis.renamenx(bfoo, bbar);
        Assert.assertEquals(1, bstatus);
        jedis.set(bfoo, bbar);
        bstatus = jedis.renamenx(bfoo, bbar);
        Assert.assertEquals(0, bstatus);
    }

    @Test
    public void dbSize() {
        long size = jedis.dbSize();
        Assert.assertEquals(0, size);
        jedis.set("foo", "bar");
        size = jedis.dbSize();
        Assert.assertEquals(1, size);
        // Binary
        jedis.set(bfoo, bbar);
        size = jedis.dbSize();
        Assert.assertEquals(2, size);
    }

    @Test
    public void expire() {
        long status = jedis.expire("foo", 20);
        Assert.assertEquals(0, status);
        jedis.set("foo", "bar");
        status = jedis.expire("foo", 20);
        Assert.assertEquals(1, status);
        // Binary
        long bstatus = jedis.expire(bfoo, 20);
        Assert.assertEquals(0, bstatus);
        jedis.set(bfoo, bbar);
        bstatus = jedis.expire(bfoo, 20);
        Assert.assertEquals(1, bstatus);
    }

    @Test
    public void expireAt() {
        long unixTime = ((System.currentTimeMillis()) / 1000L) + 20;
        long status = jedis.expireAt("foo", unixTime);
        Assert.assertEquals(0, status);
        jedis.set("foo", "bar");
        unixTime = ((System.currentTimeMillis()) / 1000L) + 20;
        status = jedis.expireAt("foo", unixTime);
        Assert.assertEquals(1, status);
        // Binary
        long bstatus = jedis.expireAt(bfoo, unixTime);
        Assert.assertEquals(0, bstatus);
        jedis.set(bfoo, bbar);
        unixTime = ((System.currentTimeMillis()) / 1000L) + 20;
        bstatus = jedis.expireAt(bfoo, unixTime);
        Assert.assertEquals(1, bstatus);
    }

    @Test
    public void ttl() {
        long ttl = jedis.ttl("foo");
        Assert.assertEquals((-2), ttl);
        jedis.set("foo", "bar");
        ttl = jedis.ttl("foo");
        Assert.assertEquals((-1), ttl);
        jedis.expire("foo", 20);
        ttl = jedis.ttl("foo");
        Assert.assertTrue(((ttl >= 0) && (ttl <= 20)));
        // Binary
        long bttl = jedis.ttl(bfoo);
        Assert.assertEquals((-2), bttl);
        jedis.set(bfoo, bbar);
        bttl = jedis.ttl(bfoo);
        Assert.assertEquals((-1), bttl);
        jedis.expire(bfoo, 20);
        bttl = jedis.ttl(bfoo);
        Assert.assertTrue(((bttl >= 0) && (bttl <= 20)));
    }

    @Test
    public void select() {
        jedis.set("foo", "bar");
        String status = jedis.select(1);
        Assert.assertEquals("OK", status);
        assertEquals(null, jedis.get("foo"));
        status = jedis.select(0);
        Assert.assertEquals("OK", status);
        Assert.assertEquals("bar", jedis.get("foo"));
        // Binary
        jedis.set(bfoo, bbar);
        String bstatus = jedis.select(1);
        Assert.assertEquals("OK", bstatus);
        assertEquals(null, jedis.get(bfoo));
        bstatus = jedis.select(0);
        Assert.assertEquals("OK", bstatus);
        Assert.assertArrayEquals(bbar, jedis.get(bfoo));
    }

    @Test
    public void getDB() {
        Assert.assertEquals(0, jedis.getDB());
        jedis.select(1);
        Assert.assertEquals(1, jedis.getDB());
    }

    @Test
    public void move() {
        long status = jedis.move("foo", 1);
        Assert.assertEquals(0, status);
        jedis.set("foo", "bar");
        status = jedis.move("foo", 1);
        Assert.assertEquals(1, status);
        assertEquals(null, jedis.get("foo"));
        jedis.select(1);
        Assert.assertEquals("bar", jedis.get("foo"));
        // Binary
        jedis.select(0);
        long bstatus = jedis.move(bfoo, 1);
        Assert.assertEquals(0, bstatus);
        jedis.set(bfoo, bbar);
        bstatus = jedis.move(bfoo, 1);
        Assert.assertEquals(1, bstatus);
        assertEquals(null, jedis.get(bfoo));
        jedis.select(1);
        Assert.assertArrayEquals(bbar, jedis.get(bfoo));
    }

    @Test
    public void flushDB() {
        jedis.set("foo", "bar");
        Assert.assertEquals(1, jedis.dbSize().intValue());
        jedis.set("bar", "foo");
        jedis.move("bar", 1);
        String status = jedis.flushDB();
        Assert.assertEquals("OK", status);
        Assert.assertEquals(0, jedis.dbSize().intValue());
        jedis.select(1);
        Assert.assertEquals(1, jedis.dbSize().intValue());
        jedis.del("bar");
        // Binary
        jedis.select(0);
        jedis.set(bfoo, bbar);
        Assert.assertEquals(1, jedis.dbSize().intValue());
        jedis.set(bbar, bfoo);
        jedis.move(bbar, 1);
        String bstatus = jedis.flushDB();
        Assert.assertEquals("OK", bstatus);
        Assert.assertEquals(0, jedis.dbSize().intValue());
        jedis.select(1);
        Assert.assertEquals(1, jedis.dbSize().intValue());
    }

    @Test
    public void flushAll() {
        jedis.set("foo", "bar");
        Assert.assertEquals(1, jedis.dbSize().intValue());
        jedis.set("bar", "foo");
        jedis.move("bar", 1);
        String status = jedis.flushAll();
        Assert.assertEquals("OK", status);
        Assert.assertEquals(0, jedis.dbSize().intValue());
        jedis.select(1);
        Assert.assertEquals(0, jedis.dbSize().intValue());
        // Binary
        jedis.select(0);
        jedis.set(bfoo, bbar);
        Assert.assertEquals(1, jedis.dbSize().intValue());
        jedis.set(bbar, bfoo);
        jedis.move(bbar, 1);
        String bstatus = jedis.flushAll();
        Assert.assertEquals("OK", bstatus);
        Assert.assertEquals(0, jedis.dbSize().intValue());
        jedis.select(1);
        Assert.assertEquals(0, jedis.dbSize().intValue());
    }

    @Test
    public void persist() {
        jedis.setex("foo", (60 * 60), "bar");
        Assert.assertTrue(((jedis.ttl("foo")) > 0));
        long status = jedis.persist("foo");
        Assert.assertEquals(1, status);
        Assert.assertEquals((-1), jedis.ttl("foo").intValue());
        // Binary
        jedis.setex(bfoo, (60 * 60), bbar);
        Assert.assertTrue(((jedis.ttl(bfoo)) > 0));
        long bstatus = jedis.persist(bfoo);
        Assert.assertEquals(1, bstatus);
        Assert.assertEquals((-1), jedis.ttl(bfoo).intValue());
    }

    @Test
    public void echo() {
        String result = jedis.echo("hello world");
        Assert.assertEquals("hello world", result);
        // Binary
        byte[] bresult = jedis.echo(SafeEncoder.encode("hello world"));
        Assert.assertArrayEquals(SafeEncoder.encode("hello world"), bresult);
    }

    @Test
    public void dumpAndRestore() {
        jedis.set("foo1", "bar1");
        byte[] sv = jedis.dump("foo1");
        jedis.restore("foo2", 0, sv);
        Assert.assertTrue(jedis.exists("foo2"));
    }

    @Test
    public void pexpire() {
        long status = jedis.pexpire("foo", 10000);
        Assert.assertEquals(0, status);
        jedis.set("foo1", "bar1");
        status = jedis.pexpire("foo1", 10000);
        Assert.assertEquals(1, status);
        jedis.set("foo2", "bar2");
        status = jedis.pexpire("foo2", 200000000000L);
        Assert.assertEquals(1, status);
        long pttl = jedis.pttl("foo2");
        Assert.assertTrue((pttl > 100000000000L));
    }

    @Test
    public void pexpireAt() {
        long unixTime = (System.currentTimeMillis()) + 10000;
        long status = jedis.pexpireAt("foo", unixTime);
        Assert.assertEquals(0, status);
        jedis.set("foo", "bar");
        unixTime = (System.currentTimeMillis()) + 10000;
        status = jedis.pexpireAt("foo", unixTime);
        Assert.assertEquals(1, status);
    }

    @Test
    public void pttl() {
        long pttl = jedis.pttl("foo");
        Assert.assertEquals((-2), pttl);
        jedis.set("foo", "bar");
        pttl = jedis.pttl("foo");
        Assert.assertEquals((-1), pttl);
        jedis.pexpire("foo", 20000);
        pttl = jedis.pttl("foo");
        Assert.assertTrue(((pttl >= 0) && (pttl <= 20000)));
    }

    @Test
    public void psetex() {
        long pttl = jedis.pttl("foo");
        Assert.assertEquals((-2), pttl);
        String status = jedis.psetex("foo", 200000000000L, "bar");
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        pttl = jedis.pttl("foo");
        Assert.assertTrue((pttl > 100000000000L));
    }

    @Test
    public void scan() {
        jedis.set("b", "b");
        jedis.set("a", "a");
        ScanResult<String> result = jedis.scan(ScanParams.SCAN_POINTER_START);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        ScanResult<byte[]> bResult = jedis.scan(ScanParams.SCAN_POINTER_START_BINARY);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void scanMatch() {
        ScanParams params = new ScanParams();
        params.match("a*");
        jedis.set("b", "b");
        jedis.set("a", "a");
        jedis.set("aa", "aa");
        ScanResult<String> result = jedis.scan(ScanParams.SCAN_POINTER_START, params);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.match(bfoostar);
        jedis.set(bfoo1, bbar);
        jedis.set(bfoo2, bbar);
        jedis.set(bfoo3, bbar);
        ScanResult<byte[]> bResult = jedis.scan(ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void scanCount() {
        ScanParams params = new ScanParams();
        params.count(2);
        for (int i = 0; i < 10; i++) {
            jedis.set(("a" + i), ("a" + i));
        }
        ScanResult<String> result = jedis.scan(ScanParams.SCAN_POINTER_START, params);
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.count(2);
        jedis.set(bfoo1, bbar);
        jedis.set(bfoo2, bbar);
        jedis.set(bfoo3, bbar);
        ScanResult<byte[]> bResult = jedis.scan(ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void setNxExAndGet() {
        String status = jedis.set("hello", "world", setParams().nx().ex(expireSeconds));
        Assert.assertTrue(OK.name().equalsIgnoreCase(status));
        String value = jedis.get("hello");
        Assert.assertEquals("world", value);
        jedis.set("hello", "bar", setParams().nx().ex(expireSeconds));
        value = jedis.get("hello");
        Assert.assertEquals("world", value);
        long ttl = jedis.ttl("hello");
        Assert.assertTrue(((ttl > 0) && (ttl <= (expireSeconds))));
        // binary
        byte[] bworld = new byte[]{ 119, 111, 114, 108, 100 };
        byte[] bhello = new byte[]{ 104, 101, 108, 108, 111 };
        String bstatus = jedis.set(bworld, bhello, setParams().nx().ex(expireSeconds));
        Assert.assertTrue(OK.name().equalsIgnoreCase(bstatus));
        byte[] bvalue = jedis.get(bworld);
        Assert.assertTrue(Arrays.equals(bhello, bvalue));
        jedis.set(bworld, bbar, setParams().nx().ex(expireSeconds));
        bvalue = jedis.get(bworld);
        Assert.assertTrue(Arrays.equals(bhello, bvalue));
        long bttl = jedis.ttl(bworld);
        Assert.assertTrue(((bttl > 0) && (bttl <= (expireSeconds))));
    }
}

