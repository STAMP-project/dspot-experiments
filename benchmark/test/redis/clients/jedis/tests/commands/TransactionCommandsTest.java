package redis.clients.jedis.tests.commands;


import Keyword.OK;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;


public class TransactionCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    final byte[] ba = new byte[]{ 10 };

    final byte[] bb = new byte[]{ 11 };

    final byte[] bmykey = new byte[]{ 66, 2, 3, 4 };

    Jedis nj;

    @Test
    public void multi() {
        Transaction trans = jedis.multi();
        trans.sadd("foo", "a");
        trans.sadd("foo", "b");
        trans.scard("foo");
        List<Object> response = trans.exec();
        List<Object> expected = new ArrayList<Object>();
        expected.add(1L);
        expected.add(1L);
        expected.add(2L);
        Assert.assertEquals(expected, response);
        // Binary
        trans = jedis.multi();
        trans.sadd(bfoo, ba);
        trans.sadd(bfoo, bb);
        trans.scard(bfoo);
        response = trans.exec();
        expected = new ArrayList<Object>();
        expected.add(1L);
        expected.add(1L);
        expected.add(2L);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void watch() throws IOException, UnknownHostException {
        jedis.watch("mykey", "somekey");
        Transaction t = jedis.multi();
        nj.connect();
        nj.auth("foobared");
        nj.set("mykey", "bar");
        nj.disconnect();
        t.set("mykey", "foo");
        List<Object> resp = t.exec();
        Assert.assertEquals(null, resp);
        Assert.assertEquals("bar", jedis.get("mykey"));
        // Binary
        jedis.watch(bmykey, "foobar".getBytes());
        t = jedis.multi();
        nj.connect();
        nj.auth("foobared");
        nj.set(bmykey, bbar);
        nj.disconnect();
        t.set(bmykey, bfoo);
        resp = t.exec();
        Assert.assertEquals(null, resp);
        Assert.assertTrue(Arrays.equals(bbar, jedis.get(bmykey)));
    }

    @Test
    public void unwatch() throws IOException, UnknownHostException {
        jedis.watch("mykey");
        String val = jedis.get("mykey");
        val = "foo";
        String status = jedis.unwatch();
        Assert.assertEquals("OK", status);
        Transaction t = jedis.multi();
        nj.connect();
        nj.auth("foobared");
        nj.set("mykey", "bar");
        nj.disconnect();
        t.set("mykey", val);
        List<Object> resp = t.exec();
        Assert.assertEquals(1, resp.size());
        Assert.assertEquals("OK", resp.get(0));
        // Binary
        jedis.watch(bmykey);
        byte[] bval = jedis.get(bmykey);
        bval = bfoo;
        status = jedis.unwatch();
        Assert.assertEquals(OK.name(), status);
        t = jedis.multi();
        nj.connect();
        nj.auth("foobared");
        nj.set(bmykey, bbar);
        nj.disconnect();
        t.set(bmykey, bval);
        resp = t.exec();
        Assert.assertEquals(1, resp.size());
        Assert.assertEquals("OK", resp.get(0));
    }

    @Test(expected = JedisDataException.class)
    public void validateWhenInMulti() {
        jedis.multi();
        jedis.ping();
    }

    @Test
    public void discard() {
        Transaction t = jedis.multi();
        String status = t.discard();
        Assert.assertEquals("OK", status);
    }

    @Test
    public void transactionResponse() {
        jedis.set("string", "foo");
        jedis.lpush("list", "foo");
        jedis.hset("hash", "foo", "bar");
        jedis.zadd("zset", 1, "foo");
        jedis.sadd("set", "foo");
        Transaction t = jedis.multi();
        Response<String> string = t.get("string");
        Response<String> list = t.lpop("list");
        Response<String> hash = t.hget("hash", "foo");
        Response<Set<String>> zset = t.zrange("zset", 0, (-1));
        Response<String> set = t.spop("set");
        t.exec();
        Assert.assertEquals("foo", string.get());
        Assert.assertEquals("foo", list.get());
        Assert.assertEquals("bar", hash.get());
        Assert.assertEquals("foo", zset.get().iterator().next());
        Assert.assertEquals("foo", set.get());
    }

    @Test
    public void transactionResponseBinary() {
        jedis.set("string", "foo");
        jedis.lpush("list", "foo");
        jedis.hset("hash", "foo", "bar");
        jedis.zadd("zset", 1, "foo");
        jedis.sadd("set", "foo");
        Transaction t = jedis.multi();
        Response<byte[]> string = t.get("string".getBytes());
        Response<byte[]> list = t.lpop("list".getBytes());
        Response<byte[]> hash = t.hget("hash".getBytes(), "foo".getBytes());
        Response<Set<byte[]>> zset = t.zrange("zset".getBytes(), 0, (-1));
        Response<byte[]> set = t.spop("set".getBytes());
        t.exec();
        Assert.assertArrayEquals("foo".getBytes(), string.get());
        Assert.assertArrayEquals("foo".getBytes(), list.get());
        Assert.assertArrayEquals("bar".getBytes(), hash.get());
        Assert.assertArrayEquals("foo".getBytes(), zset.get().iterator().next());
        Assert.assertArrayEquals("foo".getBytes(), set.get());
    }

    @Test(expected = JedisDataException.class)
    public void transactionResponseWithinPipeline() {
        jedis.set("string", "foo");
        Transaction t = jedis.multi();
        Response<String> string = t.get("string");
        string.get();
        t.exec();
    }

    @Test
    public void transactionResponseWithError() {
        Transaction t = jedis.multi();
        t.set("foo", "bar");
        Response<Set<String>> error = t.smembers("foo");
        Response<String> r = t.get("foo");
        List<Object> l = t.exec();
        Assert.assertEquals(JedisDataException.class, l.get(1).getClass());
        try {
            error.get();
            Assert.fail("We expect exception here!");
        } catch (JedisDataException e) {
            // that is fine we should be here
        }
        Assert.assertEquals(r.get(), "bar");
    }

    @Test
    public void execGetResponse() {
        Transaction t = jedis.multi();
        t.set("foo", "bar");
        t.smembers("foo");
        t.get("foo");
        List<Response<?>> lr = t.execGetResponse();
        try {
            lr.get(1).get();
            Assert.fail("We expect exception here!");
        } catch (JedisDataException e) {
            // that is fine we should be here
        }
        Assert.assertEquals("bar", lr.get(2).get());
    }

    @Test
    public void select() {
        jedis.select(1);
        jedis.set("foo", "bar");
        jedis.watch("foo");
        Transaction t = jedis.multi();
        t.select(0);
        t.set("bar", "foo");
        Jedis jedis2 = createJedis();
        jedis2.select(1);
        jedis2.set("foo", "bar2");
        List<Object> results = t.exec();
        Assert.assertNull(results);
    }

    @Test
    public void testResetStateWhenInMulti() {
        jedis.auth("foobared");
        Transaction t = jedis.multi();
        t.set("foooo", "barrr");
        jedis.resetState();
        assertEquals(null, jedis.get("foooo"));
    }

    @Test
    public void testResetStateWhenInMultiWithinPipeline() {
        jedis.auth("foobared");
        Pipeline p = jedis.pipelined();
        p.multi();
        p.set("foooo", "barrr");
        jedis.resetState();
        assertEquals(null, jedis.get("foooo"));
    }

    @Test
    public void testResetStateWhenInWatch() {
        jedis.watch("mykey", "somekey");
        // state reset : unwatch
        jedis.resetState();
        Transaction t = jedis.multi();
        nj.connect();
        nj.auth("foobared");
        nj.set("mykey", "bar");
        nj.disconnect();
        t.set("mykey", "foo");
        List<Object> resp = t.exec();
        Assert.assertNotNull(resp);
        Assert.assertEquals(1, resp.size());
        Assert.assertEquals("foo", jedis.get("mykey"));
    }

    @Test
    public void testResetStateWithFullyExecutedTransaction() {
        Jedis jedis2 = new Jedis(jedis.getClient().getHost(), jedis.getClient().getPort());
        jedis2.auth("foobared");
        Transaction t = jedis2.multi();
        t.set("mykey", "foo");
        t.get("mykey");
        List<Object> resp = t.exec();
        Assert.assertNotNull(resp);
        Assert.assertEquals(2, resp.size());
        jedis2.resetState();
        jedis2.close();
    }

    @Test
    public void testCloseable() throws IOException {
        // we need to test with fresh instance of Jedis
        Jedis jedis2 = new Jedis(JedisCommandTestBase.hnp.getHost(), JedisCommandTestBase.hnp.getPort(), 500);
        jedis2.auth("foobared");
        Transaction transaction = jedis2.multi();
        transaction.set("a", "1");
        transaction.set("b", "2");
        transaction.close();
        try {
            transaction.exec();
            Assert.fail("close should discard transaction");
        } catch (JedisDataException e) {
            Assert.assertTrue(e.getMessage().contains("EXEC without MULTI"));
            // pass
        }
    }
}

