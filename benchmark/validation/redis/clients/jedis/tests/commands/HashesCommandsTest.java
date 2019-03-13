package redis.clients.jedis.tests.commands;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;


public class HashesCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    final byte[] bcar = new byte[]{ 9, 10, 11, 12 };

    final byte[] bbar1 = new byte[]{ 5, 6, 7, 8, 10 };

    final byte[] bbar2 = new byte[]{ 5, 6, 7, 8, 11 };

    final byte[] bbar3 = new byte[]{ 5, 6, 7, 8, 12 };

    final byte[] bbarstar = new byte[]{ 5, 6, 7, 8, '*' };

    @Test
    public void hset() {
        long status = jedis.hset("foo", "bar", "car");
        Assert.assertEquals(1, status);
        status = jedis.hset("foo", "bar", "foo");
        Assert.assertEquals(0, status);
        // Binary
        long bstatus = jedis.hset(bfoo, bbar, bcar);
        Assert.assertEquals(1, bstatus);
        bstatus = jedis.hset(bfoo, bbar, bfoo);
        Assert.assertEquals(0, bstatus);
    }

    @Test
    public void hget() {
        jedis.hset("foo", "bar", "car");
        assertEquals(null, jedis.hget("bar", "foo"));
        assertEquals(null, jedis.hget("foo", "car"));
        Assert.assertEquals("car", jedis.hget("foo", "bar"));
        // Binary
        jedis.hset(bfoo, bbar, bcar);
        assertEquals(null, jedis.hget(bbar, bfoo));
        assertEquals(null, jedis.hget(bfoo, bcar));
        Assert.assertArrayEquals(bcar, jedis.hget(bfoo, bbar));
    }

    @Test
    public void hsetnx() {
        long status = jedis.hsetnx("foo", "bar", "car");
        Assert.assertEquals(1, status);
        Assert.assertEquals("car", jedis.hget("foo", "bar"));
        status = jedis.hsetnx("foo", "bar", "foo");
        Assert.assertEquals(0, status);
        Assert.assertEquals("car", jedis.hget("foo", "bar"));
        status = jedis.hsetnx("foo", "car", "bar");
        Assert.assertEquals(1, status);
        Assert.assertEquals("bar", jedis.hget("foo", "car"));
        // Binary
        long bstatus = jedis.hsetnx(bfoo, bbar, bcar);
        Assert.assertEquals(1, bstatus);
        Assert.assertArrayEquals(bcar, jedis.hget(bfoo, bbar));
        bstatus = jedis.hsetnx(bfoo, bbar, bfoo);
        Assert.assertEquals(0, bstatus);
        Assert.assertArrayEquals(bcar, jedis.hget(bfoo, bbar));
        bstatus = jedis.hsetnx(bfoo, bcar, bbar);
        Assert.assertEquals(1, bstatus);
        Assert.assertArrayEquals(bbar, jedis.hget(bfoo, bcar));
    }

    @Test
    public void hmset() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        String status = jedis.hmset("foo", hash);
        Assert.assertEquals("OK", status);
        Assert.assertEquals("car", jedis.hget("foo", "bar"));
        Assert.assertEquals("bar", jedis.hget("foo", "car"));
        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        String bstatus = jedis.hmset(bfoo, bhash);
        Assert.assertEquals("OK", bstatus);
        Assert.assertArrayEquals(bcar, jedis.hget(bfoo, bbar));
        Assert.assertArrayEquals(bbar, jedis.hget(bfoo, bcar));
    }

    @Test
    public void hmget() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);
        List<String> values = jedis.hmget("foo", "bar", "car", "foo");
        List<String> expected = new ArrayList<String>();
        expected.add("car");
        expected.add("bar");
        expected.add(null);
        Assert.assertEquals(expected, values);
        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);
        List<byte[]> bvalues = jedis.hmget(bfoo, bbar, bcar, bfoo);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bcar);
        bexpected.add(bbar);
        bexpected.add(null);
        assertEquals(bexpected, bvalues);
    }

    @Test
    public void hincrBy() {
        long value = jedis.hincrBy("foo", "bar", 1);
        Assert.assertEquals(1, value);
        value = jedis.hincrBy("foo", "bar", (-1));
        Assert.assertEquals(0, value);
        value = jedis.hincrBy("foo", "bar", (-10));
        Assert.assertEquals((-10), value);
        // Binary
        long bvalue = jedis.hincrBy(bfoo, bbar, 1);
        Assert.assertEquals(1, bvalue);
        bvalue = jedis.hincrBy(bfoo, bbar, (-1));
        Assert.assertEquals(0, bvalue);
        bvalue = jedis.hincrBy(bfoo, bbar, (-10));
        Assert.assertEquals((-10), bvalue);
    }

    @Test
    public void hincrByFloat() {
        Double value = jedis.hincrByFloat("foo", "bar", 1.5);
        Assert.assertEquals(((Double) (1.5)), value);
        value = jedis.hincrByFloat("foo", "bar", (-1.5));
        Assert.assertEquals(((Double) (0.0)), value);
        value = jedis.hincrByFloat("foo", "bar", (-10.7));
        Assert.assertEquals(Double.compare((-10.7), value), 0);
        // Binary
        double bvalue = jedis.hincrByFloat(bfoo, bbar, 1.5);
        Assert.assertEquals(Double.compare(1.5, bvalue), 0);
        bvalue = jedis.hincrByFloat(bfoo, bbar, (-1.5));
        Assert.assertEquals(Double.compare(0.0, bvalue), 0);
        bvalue = jedis.hincrByFloat(bfoo, bbar, (-10.7));
        Assert.assertEquals(Double.compare((-10.7), value), 0);
    }

    @Test
    public void hexists() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);
        Assert.assertFalse(jedis.hexists("bar", "foo"));
        Assert.assertFalse(jedis.hexists("foo", "foo"));
        Assert.assertTrue(jedis.hexists("foo", "bar"));
        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);
        Assert.assertFalse(jedis.hexists(bbar, bfoo));
        Assert.assertFalse(jedis.hexists(bfoo, bfoo));
        Assert.assertTrue(jedis.hexists(bfoo, bbar));
    }

    @Test
    public void hdel() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);
        Assert.assertEquals(0, jedis.hdel("bar", "foo").intValue());
        Assert.assertEquals(0, jedis.hdel("foo", "foo").intValue());
        Assert.assertEquals(1, jedis.hdel("foo", "bar").intValue());
        assertEquals(null, jedis.hget("foo", "bar"));
        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);
        Assert.assertEquals(0, jedis.hdel(bbar, bfoo).intValue());
        Assert.assertEquals(0, jedis.hdel(bfoo, bfoo).intValue());
        Assert.assertEquals(1, jedis.hdel(bfoo, bbar).intValue());
        assertEquals(null, jedis.hget(bfoo, bbar));
    }

    @Test
    public void hlen() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);
        Assert.assertEquals(0, jedis.hlen("bar").intValue());
        Assert.assertEquals(2, jedis.hlen("foo").intValue());
        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);
        Assert.assertEquals(0, jedis.hlen(bbar).intValue());
        Assert.assertEquals(2, jedis.hlen(bfoo).intValue());
    }

    @Test
    public void hkeys() {
        Map<String, String> hash = new LinkedHashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);
        Set<String> keys = jedis.hkeys("foo");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("bar");
        expected.add("car");
        Assert.assertEquals(expected, keys);
        // Binary
        Map<byte[], byte[]> bhash = new LinkedHashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);
        Set<byte[]> bkeys = jedis.hkeys(bfoo);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bbar);
        bexpected.add(bcar);
        assertEquals(bexpected, bkeys);
    }

    @Test
    public void hvals() {
        Map<String, String> hash = new LinkedHashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);
        List<String> vals = jedis.hvals("foo");
        Assert.assertEquals(2, vals.size());
        Assert.assertTrue(vals.contains("bar"));
        Assert.assertTrue(vals.contains("car"));
        // Binary
        Map<byte[], byte[]> bhash = new LinkedHashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);
        List<byte[]> bvals = jedis.hvals(bfoo);
        Assert.assertEquals(2, bvals.size());
        Assert.assertTrue(arrayContains(bvals, bbar));
        Assert.assertTrue(arrayContains(bvals, bcar));
    }

    @Test
    public void hgetAll() {
        Map<String, String> h = new HashMap<String, String>();
        h.put("bar", "car");
        h.put("car", "bar");
        jedis.hmset("foo", h);
        Map<String, String> hash = jedis.hgetAll("foo");
        Assert.assertEquals(2, hash.size());
        Assert.assertEquals("car", hash.get("bar"));
        Assert.assertEquals("bar", hash.get("car"));
        // Binary
        Map<byte[], byte[]> bh = new HashMap<byte[], byte[]>();
        bh.put(bbar, bcar);
        bh.put(bcar, bbar);
        jedis.hmset(bfoo, bh);
        Map<byte[], byte[]> bhash = jedis.hgetAll(bfoo);
        Assert.assertEquals(2, bhash.size());
        Assert.assertArrayEquals(bcar, bhash.get(bbar));
        Assert.assertArrayEquals(bbar, bhash.get(bcar));
    }

    @Test
    public void hgetAllPipeline() {
        Map<byte[], byte[]> bh = new HashMap<byte[], byte[]>();
        bh.put(bbar, bcar);
        bh.put(bcar, bbar);
        jedis.hmset(bfoo, bh);
        Pipeline pipeline = jedis.pipelined();
        Response<Map<byte[], byte[]>> bhashResponse = pipeline.hgetAll(bfoo);
        pipeline.sync();
        Map<byte[], byte[]> bhash = bhashResponse.get();
        Assert.assertEquals(2, bhash.size());
        Assert.assertArrayEquals(bcar, bhash.get(bbar));
        Assert.assertArrayEquals(bbar, bhash.get(bcar));
    }

    @Test
    public void hscan() {
        jedis.hset("foo", "b", "b");
        jedis.hset("foo", "a", "a");
        ScanResult<Map.Entry<String, String>> result = jedis.hscan("foo", ScanParams.SCAN_POINTER_START);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        jedis.hset(bfoo, bbar, bcar);
        ScanResult<Map.Entry<byte[], byte[]>> bResult = jedis.hscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void hscanMatch() {
        ScanParams params = new ScanParams();
        params.match("a*");
        jedis.hset("foo", "b", "b");
        jedis.hset("foo", "a", "a");
        jedis.hset("foo", "aa", "aa");
        ScanResult<Map.Entry<String, String>> result = jedis.hscan("foo", ScanParams.SCAN_POINTER_START, params);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.match(bbarstar);
        jedis.hset(bfoo, bbar, bcar);
        jedis.hset(bfoo, bbar1, bcar);
        jedis.hset(bfoo, bbar2, bcar);
        jedis.hset(bfoo, bbar3, bcar);
        ScanResult<Map.Entry<byte[], byte[]>> bResult = jedis.hscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void hscanCount() {
        ScanParams params = new ScanParams();
        params.count(2);
        for (int i = 0; i < 10; i++) {
            jedis.hset("foo", ("a" + i), ("a" + i));
        }
        ScanResult<Map.Entry<String, String>> result = jedis.hscan("foo", ScanParams.SCAN_POINTER_START, params);
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.count(2);
        jedis.hset(bfoo, bbar, bcar);
        jedis.hset(bfoo, bbar1, bcar);
        jedis.hset(bfoo, bbar2, bcar);
        jedis.hset(bfoo, bbar3, bcar);
        ScanResult<Map.Entry<byte[], byte[]>> bResult = jedis.hscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertFalse(bResult.getResult().isEmpty());
    }
}

