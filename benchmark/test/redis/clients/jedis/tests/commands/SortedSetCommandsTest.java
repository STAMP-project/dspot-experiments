package redis.clients.jedis.tests.commands;


import ZParams.Aggregate.SUM;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.SafeEncoder;


public class SortedSetCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    final byte[] bcar = new byte[]{ 9, 10, 11, 12 };

    final byte[] ba = new byte[]{ 10 };

    final byte[] bb = new byte[]{ 11 };

    final byte[] bc = new byte[]{ 12 };

    final byte[] bInclusiveB = new byte[]{ 91, 11 };

    final byte[] bExclusiveC = new byte[]{ 40, 12 };

    final byte[] bLexMinusInf = new byte[]{ 45 };

    final byte[] bLexPlusInf = new byte[]{ 43 };

    final byte[] bbar1 = new byte[]{ 5, 6, 7, 8, 10 };

    final byte[] bbar2 = new byte[]{ 5, 6, 7, 8, 11 };

    final byte[] bbar3 = new byte[]{ 5, 6, 7, 8, 12 };

    final byte[] bbarstar = new byte[]{ 5, 6, 7, 8, '*' };

    @Test
    public void zadd() {
        long status = jedis.zadd("foo", 1.0, "a");
        Assert.assertEquals(1, status);
        status = jedis.zadd("foo", 10.0, "b");
        Assert.assertEquals(1, status);
        status = jedis.zadd("foo", 0.1, "c");
        Assert.assertEquals(1, status);
        status = jedis.zadd("foo", 2.0, "a");
        Assert.assertEquals(0, status);
        // Binary
        long bstatus = jedis.zadd(bfoo, 1.0, ba);
        Assert.assertEquals(1, bstatus);
        bstatus = jedis.zadd(bfoo, 10.0, bb);
        Assert.assertEquals(1, bstatus);
        bstatus = jedis.zadd(bfoo, 0.1, bc);
        Assert.assertEquals(1, bstatus);
        bstatus = jedis.zadd(bfoo, 2.0, ba);
        Assert.assertEquals(0, bstatus);
    }

    @Test
    public void zaddWithParams() {
        jedis.del("foo");
        // xx: never add new member
        long status = jedis.zadd("foo", 1.0, "a", ZAddParams.zAddParams().xx());
        Assert.assertEquals(0L, status);
        jedis.zadd("foo", 1.0, "a");
        // nx: never update current member
        status = jedis.zadd("foo", 2.0, "a", ZAddParams.zAddParams().nx());
        Assert.assertEquals(0L, status);
        Assert.assertEquals(Double.valueOf(1.0), jedis.zscore("foo", "a"));
        Map<String, Double> scoreMembers = new HashMap<String, Double>();
        scoreMembers.put("a", 2.0);
        scoreMembers.put("b", 1.0);
        // ch: return count of members not only added, but also updated
        status = jedis.zadd("foo", scoreMembers, ZAddParams.zAddParams().ch());
        Assert.assertEquals(2L, status);
        // binary
        jedis.del(bfoo);
        // xx: never add new member
        status = jedis.zadd(bfoo, 1.0, ba, ZAddParams.zAddParams().xx());
        Assert.assertEquals(0L, status);
        jedis.zadd(bfoo, 1.0, ba);
        // nx: never update current member
        status = jedis.zadd(bfoo, 2.0, ba, ZAddParams.zAddParams().nx());
        Assert.assertEquals(0L, status);
        Assert.assertEquals(Double.valueOf(1.0), jedis.zscore(bfoo, ba));
        Map<byte[], Double> binaryScoreMembers = new HashMap<byte[], Double>();
        binaryScoreMembers.put(ba, 2.0);
        binaryScoreMembers.put(bb, 1.0);
        // ch: return count of members not only added, but also updated
        status = jedis.zadd(bfoo, binaryScoreMembers, ZAddParams.zAddParams().ch());
        Assert.assertEquals(2L, status);
    }

    @Test
    public void zrange() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("c");
        expected.add("a");
        Set<String> range = jedis.zrange("foo", 0, 1);
        Assert.assertEquals(expected, range);
        expected.add("b");
        range = jedis.zrange("foo", 0, 100);
        Assert.assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bc);
        bexpected.add(ba);
        Set<byte[]> brange = jedis.zrange(bfoo, 0, 1);
        assertEquals(bexpected, brange);
        bexpected.add(bb);
        brange = jedis.zrange(bfoo, 0, 100);
        assertEquals(bexpected, brange);
    }

    @Test
    public void zrangeByLex() {
        jedis.zadd("foo", 1, "aa");
        jedis.zadd("foo", 1, "c");
        jedis.zadd("foo", 1, "bb");
        jedis.zadd("foo", 1, "d");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("bb");
        expected.add("c");
        // exclusive aa ~ inclusive c
        Assert.assertEquals(expected, jedis.zrangeByLex("foo", "(aa", "[c"));
        expected.clear();
        expected.add("bb");
        expected.add("c");
        // with LIMIT
        Assert.assertEquals(expected, jedis.zrangeByLex("foo", "-", "+", 1, 2));
    }

    @Test
    public void zrangeByLexBinary() {
        // binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 1, bc);
        jedis.zadd(bfoo, 1, bb);
        Set<byte[]> bExpected = new LinkedHashSet<byte[]>();
        bExpected.add(bb);
        assertEquals(bExpected, jedis.zrangeByLex(bfoo, bInclusiveB, bExclusiveC));
        bExpected.clear();
        bExpected.add(ba);
        bExpected.add(bb);
        // with LIMIT
        assertEquals(bExpected, jedis.zrangeByLex(bfoo, bLexMinusInf, bLexPlusInf, 0, 2));
    }

    @Test
    public void zrevrangeByLex() {
        jedis.zadd("foo", 1, "aa");
        jedis.zadd("foo", 1, "c");
        jedis.zadd("foo", 1, "bb");
        jedis.zadd("foo", 1, "d");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("c");
        expected.add("bb");
        // exclusive aa ~ inclusive c
        Assert.assertEquals(expected, jedis.zrevrangeByLex("foo", "[c", "(aa"));
        expected.clear();
        expected.add("c");
        expected.add("bb");
        // with LIMIT
        Assert.assertEquals(expected, jedis.zrevrangeByLex("foo", "+", "-", 1, 2));
    }

    @Test
    public void zrevrangeByLexBinary() {
        // binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 1, bc);
        jedis.zadd(bfoo, 1, bb);
        Set<byte[]> bExpected = new LinkedHashSet<byte[]>();
        bExpected.add(bb);
        assertEquals(bExpected, jedis.zrevrangeByLex(bfoo, bExclusiveC, bInclusiveB));
        bExpected.clear();
        bExpected.add(bb);
        bExpected.add(ba);
        // with LIMIT
        assertEquals(bExpected, jedis.zrevrangeByLex(bfoo, bLexPlusInf, bLexMinusInf, 0, 2));
    }

    @Test
    public void zrevrange() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("b");
        expected.add("a");
        Set<String> range = jedis.zrevrange("foo", 0, 1);
        Assert.assertEquals(expected, range);
        expected.add("c");
        range = jedis.zrevrange("foo", 0, 100);
        Assert.assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(ba);
        Set<byte[]> brange = jedis.zrevrange(bfoo, 0, 1);
        assertEquals(bexpected, brange);
        bexpected.add(bc);
        brange = jedis.zrevrange(bfoo, 0, 100);
        assertEquals(bexpected, brange);
    }

    @Test
    public void zrem() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 2.0, "b");
        long status = jedis.zrem("foo", "a");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("b");
        Assert.assertEquals(1, status);
        Assert.assertEquals(expected, jedis.zrange("foo", 0, 100));
        status = jedis.zrem("foo", "bar");
        Assert.assertEquals(0, status);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 2.0, bb);
        long bstatus = jedis.zrem(bfoo, ba);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bb);
        Assert.assertEquals(1, bstatus);
        assertEquals(bexpected, jedis.zrange(bfoo, 0, 100));
        bstatus = jedis.zrem(bfoo, bbar);
        Assert.assertEquals(0, bstatus);
    }

    @Test
    public void zincrby() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 2.0, "b");
        double score = jedis.zincrby("foo", 2.0, "a");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("a");
        expected.add("b");
        Assert.assertEquals(3.0, score, 0);
        Assert.assertEquals(expected, jedis.zrange("foo", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 2.0, bb);
        double bscore = jedis.zincrby(bfoo, 2.0, ba);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(ba);
        Assert.assertEquals(3.0, bscore, 0);
        assertEquals(bexpected, jedis.zrange(bfoo, 0, 100));
    }

    @Test
    public void zincrbyWithParams() {
        jedis.del("foo");
        // xx: never add new member
        Double score = jedis.zincrby("foo", 2.0, "a", ZIncrByParams.zIncrByParams().xx());
        Assert.assertNull(score);
        jedis.zadd("foo", 2.0, "a");
        // nx: never update current member
        score = jedis.zincrby("foo", 1.0, "a", ZIncrByParams.zIncrByParams().nx());
        Assert.assertNull(score);
        Assert.assertEquals(Double.valueOf(2.0), jedis.zscore("foo", "a"));
        // Binary
        jedis.del(bfoo);
        // xx: never add new member
        score = jedis.zincrby(bfoo, 2.0, ba, ZIncrByParams.zIncrByParams().xx());
        Assert.assertNull(score);
        jedis.zadd(bfoo, 2.0, ba);
        // nx: never update current member
        score = jedis.zincrby(bfoo, 1.0, ba, ZIncrByParams.zIncrByParams().nx());
        Assert.assertNull(score);
        Assert.assertEquals(Double.valueOf(2.0), jedis.zscore(bfoo, ba));
    }

    @Test
    public void zrank() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 2.0, "b");
        long rank = jedis.zrank("foo", "a");
        Assert.assertEquals(0, rank);
        rank = jedis.zrank("foo", "b");
        Assert.assertEquals(1, rank);
        Assert.assertNull(jedis.zrank("car", "b"));
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 2.0, bb);
        long brank = jedis.zrank(bfoo, ba);
        Assert.assertEquals(0, brank);
        brank = jedis.zrank(bfoo, bb);
        Assert.assertEquals(1, brank);
        Assert.assertNull(jedis.zrank(bcar, bb));
    }

    @Test
    public void zrevrank() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 2.0, "b");
        long rank = jedis.zrevrank("foo", "a");
        Assert.assertEquals(1, rank);
        rank = jedis.zrevrank("foo", "b");
        Assert.assertEquals(0, rank);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 2.0, bb);
        long brank = jedis.zrevrank(bfoo, ba);
        Assert.assertEquals(1, brank);
        brank = jedis.zrevrank(bfoo, bb);
        Assert.assertEquals(0, brank);
    }

    @Test
    public void zrangeWithScores() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("c", 0.1));
        expected.add(new Tuple("a", 2.0));
        Set<Tuple> range = jedis.zrangeWithScores("foo", 0, 1);
        assertEquals(expected, range);
        expected.add(new Tuple("b", 10.0));
        range = jedis.zrangeWithScores("foo", 0, 100);
        assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bc, 0.1));
        bexpected.add(new Tuple(ba, 2.0));
        Set<Tuple> brange = jedis.zrangeWithScores(bfoo, 0, 1);
        assertEquals(bexpected, brange);
        bexpected.add(new Tuple(bb, 10.0));
        brange = jedis.zrangeWithScores(bfoo, 0, 100);
        assertEquals(bexpected, brange);
    }

    @Test
    public void zrevrangeWithScores() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("b", 10.0));
        expected.add(new Tuple("a", 2.0));
        Set<Tuple> range = jedis.zrevrangeWithScores("foo", 0, 1);
        assertEquals(expected, range);
        expected.add(new Tuple("c", 0.1));
        range = jedis.zrevrangeWithScores("foo", 0, 100);
        assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bb, 10.0));
        bexpected.add(new Tuple(ba, 2.0));
        Set<Tuple> brange = jedis.zrevrangeWithScores(bfoo, 0, 1);
        assertEquals(bexpected, brange);
        bexpected.add(new Tuple(bc, 0.1));
        brange = jedis.zrevrangeWithScores(bfoo, 0, 100);
        assertEquals(bexpected, brange);
    }

    @Test
    public void zcard() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        long size = jedis.zcard("foo");
        Assert.assertEquals(3, size);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        long bsize = jedis.zcard(bfoo);
        Assert.assertEquals(3, bsize);
    }

    @Test
    public void zscore() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Double score = jedis.zscore("foo", "b");
        Assert.assertEquals(((Double) (10.0)), score);
        score = jedis.zscore("foo", "c");
        Assert.assertEquals(((Double) (0.1)), score);
        score = jedis.zscore("foo", "s");
        Assert.assertNull(score);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Double bscore = jedis.zscore(bfoo, bb);
        Assert.assertEquals(((Double) (10.0)), bscore);
        bscore = jedis.zscore(bfoo, bc);
        Assert.assertEquals(((Double) (0.1)), bscore);
        bscore = jedis.zscore(bfoo, SafeEncoder.encode("s"));
        Assert.assertNull(bscore);
    }

    @Test
    public void zcount() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        long result = jedis.zcount("foo", 0.01, 2.1);
        Assert.assertEquals(2, result);
        result = jedis.zcount("foo", "(0.01", "+inf");
        Assert.assertEquals(3, result);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        long bresult = jedis.zcount(bfoo, 0.01, 2.1);
        Assert.assertEquals(2, bresult);
        bresult = jedis.zcount(bfoo, SafeEncoder.encode("(0.01"), SafeEncoder.encode("+inf"));
        Assert.assertEquals(3, bresult);
    }

    @Test
    public void zlexcount() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 1, "b");
        jedis.zadd("foo", 1, "c");
        jedis.zadd("foo", 1, "aa");
        long result = jedis.zlexcount("foo", "[aa", "(c");
        Assert.assertEquals(2, result);
        result = jedis.zlexcount("foo", "-", "+");
        Assert.assertEquals(4, result);
        result = jedis.zlexcount("foo", "-", "(c");
        Assert.assertEquals(3, result);
        result = jedis.zlexcount("foo", "[aa", "+");
        Assert.assertEquals(3, result);
    }

    @Test
    public void zlexcountBinary() {
        // Binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 1, bc);
        jedis.zadd(bfoo, 1, bb);
        long result = jedis.zlexcount(bfoo, bInclusiveB, bExclusiveC);
        Assert.assertEquals(1, result);
        result = jedis.zlexcount(bfoo, bLexMinusInf, bLexPlusInf);
        Assert.assertEquals(3, result);
    }

    @Test
    public void zrangebyscore() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Set<String> range = jedis.zrangeByScore("foo", 0.0, 2.0);
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("c");
        expected.add("a");
        Assert.assertEquals(expected, range);
        range = jedis.zrangeByScore("foo", 0.0, 2.0, 0, 1);
        expected = new LinkedHashSet<String>();
        expected.add("c");
        Assert.assertEquals(expected, range);
        range = jedis.zrangeByScore("foo", 0.0, 2.0, 1, 1);
        Set<String> range2 = jedis.zrangeByScore("foo", "-inf", "(2");
        Assert.assertEquals(expected, range2);
        expected = new LinkedHashSet<String>();
        expected.add("a");
        Assert.assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<byte[]> brange = jedis.zrangeByScore(bfoo, 0.0, 2.0);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bc);
        bexpected.add(ba);
        assertEquals(bexpected, brange);
        brange = jedis.zrangeByScore(bfoo, 0.0, 2.0, 0, 1);
        bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bc);
        assertEquals(bexpected, brange);
        brange = jedis.zrangeByScore(bfoo, 0.0, 2.0, 1, 1);
        Set<byte[]> brange2 = jedis.zrangeByScore(bfoo, SafeEncoder.encode("-inf"), SafeEncoder.encode("(2"));
        assertEquals(bexpected, brange2);
        bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(ba);
        assertEquals(bexpected, brange);
    }

    @Test
    public void zrevrangebyscore() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 2.0, "b");
        jedis.zadd("foo", 3.0, "c");
        jedis.zadd("foo", 4.0, "d");
        jedis.zadd("foo", 5.0, "e");
        Set<String> range = jedis.zrevrangeByScore("foo", 3.0, Double.NEGATIVE_INFINITY, 0, 1);
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("c");
        Assert.assertEquals(expected, range);
        range = jedis.zrevrangeByScore("foo", 3.5, Double.NEGATIVE_INFINITY, 0, 2);
        expected = new LinkedHashSet<String>();
        expected.add("c");
        expected.add("b");
        Assert.assertEquals(expected, range);
        range = jedis.zrevrangeByScore("foo", 3.5, Double.NEGATIVE_INFINITY, 1, 1);
        expected = new LinkedHashSet<String>();
        expected.add("b");
        Assert.assertEquals(expected, range);
        range = jedis.zrevrangeByScore("foo", 4.0, 2.0);
        expected = new LinkedHashSet<String>();
        expected.add("d");
        expected.add("c");
        expected.add("b");
        Assert.assertEquals(expected, range);
        range = jedis.zrevrangeByScore("foo", "+inf", "(4");
        expected = new LinkedHashSet<String>();
        expected.add("e");
        Assert.assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<byte[]> brange = jedis.zrevrangeByScore(bfoo, 2.0, 0.0);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bc);
        bexpected.add(ba);
        assertEquals(bexpected, brange);
        brange = jedis.zrevrangeByScore(bfoo, 2.0, 0.0, 0, 1);
        bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(ba);
        assertEquals(bexpected, brange);
        Set<byte[]> brange2 = jedis.zrevrangeByScore(bfoo, SafeEncoder.encode("+inf"), SafeEncoder.encode("(2"));
        bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bb);
        assertEquals(bexpected, brange2);
        brange = jedis.zrevrangeByScore(bfoo, 2.0, 0.0, 1, 1);
        bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bc);
        assertEquals(bexpected, brange);
    }

    @Test
    public void zrangebyscoreWithScores() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        Set<Tuple> range = jedis.zrangeByScoreWithScores("foo", 0.0, 2.0);
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("c", 0.1));
        expected.add(new Tuple("a", 2.0));
        assertEquals(expected, range);
        range = jedis.zrangeByScoreWithScores("foo", 0.0, 2.0, 0, 1);
        expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("c", 0.1));
        assertEquals(expected, range);
        range = jedis.zrangeByScoreWithScores("foo", 0.0, 2.0, 1, 1);
        expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("a", 2.0));
        assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<Tuple> brange = jedis.zrangeByScoreWithScores(bfoo, 0.0, 2.0);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bc, 0.1));
        bexpected.add(new Tuple(ba, 2.0));
        assertEquals(bexpected, brange);
        brange = jedis.zrangeByScoreWithScores(bfoo, 0.0, 2.0, 0, 1);
        bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bc, 0.1));
        assertEquals(bexpected, brange);
        brange = jedis.zrangeByScoreWithScores(bfoo, 0.0, 2.0, 1, 1);
        bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(ba, 2.0));
        assertEquals(bexpected, brange);
    }

    @Test
    public void zrevrangebyscoreWithScores() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 2.0, "b");
        jedis.zadd("foo", 3.0, "c");
        jedis.zadd("foo", 4.0, "d");
        jedis.zadd("foo", 5.0, "e");
        Set<Tuple> range = jedis.zrevrangeByScoreWithScores("foo", 3.0, Double.NEGATIVE_INFINITY, 0, 1);
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("c", 3.0));
        assertEquals(expected, range);
        range = jedis.zrevrangeByScoreWithScores("foo", 3.5, Double.NEGATIVE_INFINITY, 0, 2);
        expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("c", 3.0));
        expected.add(new Tuple("b", 2.0));
        assertEquals(expected, range);
        range = jedis.zrevrangeByScoreWithScores("foo", 3.5, Double.NEGATIVE_INFINITY, 1, 1);
        expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("b", 2.0));
        assertEquals(expected, range);
        range = jedis.zrevrangeByScoreWithScores("foo", 4.0, 2.0);
        expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("d", 4.0));
        expected.add(new Tuple("c", 3.0));
        expected.add(new Tuple("b", 2.0));
        assertEquals(expected, range);
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        Set<Tuple> brange = jedis.zrevrangeByScoreWithScores(bfoo, 2.0, 0.0);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bc, 0.1));
        bexpected.add(new Tuple(ba, 2.0));
        assertEquals(bexpected, brange);
        brange = jedis.zrevrangeByScoreWithScores(bfoo, 2.0, 0.0, 0, 1);
        bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(ba, 2.0));
        assertEquals(bexpected, brange);
        brange = jedis.zrevrangeByScoreWithScores(bfoo, 2.0, 0.0, 1, 1);
        bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bc, 0.1));
        assertEquals(bexpected, brange);
    }

    @Test
    public void zremrangeByRank() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        long result = jedis.zremrangeByRank("foo", 0, 0);
        Assert.assertEquals(1, result);
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("a");
        expected.add("b");
        Assert.assertEquals(expected, jedis.zrange("foo", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        long bresult = jedis.zremrangeByRank(bfoo, 0, 0);
        Assert.assertEquals(1, bresult);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(ba);
        bexpected.add(bb);
        assertEquals(bexpected, jedis.zrange(bfoo, 0, 100));
    }

    @Test
    public void zremrangeByScore() {
        jedis.zadd("foo", 1.0, "a");
        jedis.zadd("foo", 10.0, "b");
        jedis.zadd("foo", 0.1, "c");
        jedis.zadd("foo", 2.0, "a");
        long result = jedis.zremrangeByScore("foo", 0, 2);
        Assert.assertEquals(2, result);
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("b");
        Assert.assertEquals(expected, jedis.zrange("foo", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1.0, ba);
        jedis.zadd(bfoo, 10.0, bb);
        jedis.zadd(bfoo, 0.1, bc);
        jedis.zadd(bfoo, 2.0, ba);
        long bresult = jedis.zremrangeByScore(bfoo, 0, 2);
        Assert.assertEquals(2, bresult);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bb);
        assertEquals(bexpected, jedis.zrange(bfoo, 0, 100));
    }

    @Test
    public void zremrangeByLex() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 1, "b");
        jedis.zadd("foo", 1, "c");
        jedis.zadd("foo", 1, "aa");
        long result = jedis.zremrangeByLex("foo", "[aa", "(c");
        Assert.assertEquals(2, result);
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("a");
        expected.add("c");
        Assert.assertEquals(expected, jedis.zrangeByLex("foo", "-", "+"));
    }

    @Test
    public void zremrangeByLexBinary() {
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 1, bc);
        jedis.zadd(bfoo, 1, bb);
        long bresult = jedis.zremrangeByLex(bfoo, bInclusiveB, bExclusiveC);
        Assert.assertEquals(1, bresult);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(ba);
        bexpected.add(bc);
        assertEquals(bexpected, jedis.zrangeByLex(bfoo, bLexMinusInf, bLexPlusInf));
    }

    @Test
    public void zunionstore() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 2, "b");
        jedis.zadd("bar", 2, "a");
        jedis.zadd("bar", 2, "b");
        long result = jedis.zunionstore("dst", "foo", "bar");
        Assert.assertEquals(2, result);
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("b", new Double(4)));
        expected.add(new Tuple("a", new Double(3)));
        assertEquals(expected, jedis.zrangeWithScores("dst", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 2, bb);
        jedis.zadd(bbar, 2, ba);
        jedis.zadd(bbar, 2, bb);
        long bresult = jedis.zunionstore(SafeEncoder.encode("dst"), bfoo, bbar);
        Assert.assertEquals(2, bresult);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bb, new Double(4)));
        bexpected.add(new Tuple(ba, new Double(3)));
        assertEquals(bexpected, jedis.zrangeWithScores(SafeEncoder.encode("dst"), 0, 100));
    }

    @Test
    public void zunionstoreParams() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 2, "b");
        jedis.zadd("bar", 2, "a");
        jedis.zadd("bar", 2, "b");
        ZParams params = new ZParams();
        params.weights(2, 2.5);
        params.aggregate(SUM);
        long result = jedis.zunionstore("dst", params, "foo", "bar");
        Assert.assertEquals(2, result);
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("b", new Double(9)));
        expected.add(new Tuple("a", new Double(7)));
        assertEquals(expected, jedis.zrangeWithScores("dst", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 2, bb);
        jedis.zadd(bbar, 2, ba);
        jedis.zadd(bbar, 2, bb);
        ZParams bparams = new ZParams();
        bparams.weights(2, 2.5);
        bparams.aggregate(SUM);
        long bresult = jedis.zunionstore(SafeEncoder.encode("dst"), bparams, bfoo, bbar);
        Assert.assertEquals(2, bresult);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(bb, new Double(9)));
        bexpected.add(new Tuple(ba, new Double(7)));
        assertEquals(bexpected, jedis.zrangeWithScores(SafeEncoder.encode("dst"), 0, 100));
    }

    @Test
    public void zinterstore() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 2, "b");
        jedis.zadd("bar", 2, "a");
        long result = jedis.zinterstore("dst", "foo", "bar");
        Assert.assertEquals(1, result);
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("a", new Double(3)));
        assertEquals(expected, jedis.zrangeWithScores("dst", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 2, bb);
        jedis.zadd(bbar, 2, ba);
        long bresult = jedis.zinterstore(SafeEncoder.encode("dst"), bfoo, bbar);
        Assert.assertEquals(1, bresult);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(ba, new Double(3)));
        assertEquals(bexpected, jedis.zrangeWithScores(SafeEncoder.encode("dst"), 0, 100));
    }

    @Test
    public void zintertoreParams() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 2, "b");
        jedis.zadd("bar", 2, "a");
        ZParams params = new ZParams();
        params.weights(2, 2.5);
        params.aggregate(SUM);
        long result = jedis.zinterstore("dst", params, "foo", "bar");
        Assert.assertEquals(1, result);
        Set<Tuple> expected = new LinkedHashSet<Tuple>();
        expected.add(new Tuple("a", new Double(7)));
        assertEquals(expected, jedis.zrangeWithScores("dst", 0, 100));
        // Binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 2, bb);
        jedis.zadd(bbar, 2, ba);
        ZParams bparams = new ZParams();
        bparams.weights(2, 2.5);
        bparams.aggregate(SUM);
        long bresult = jedis.zinterstore(SafeEncoder.encode("dst"), bparams, bfoo, bbar);
        Assert.assertEquals(1, bresult);
        Set<Tuple> bexpected = new LinkedHashSet<Tuple>();
        bexpected.add(new Tuple(ba, new Double(7)));
        assertEquals(bexpected, jedis.zrangeWithScores(SafeEncoder.encode("dst"), 0, 100));
    }

    @Test
    public void tupleCompare() {
        Tuple t1 = new Tuple("foo", 1.0);
        Tuple t2 = new Tuple("bar", 2.0);
        Assert.assertEquals((-1), t1.compareTo(t2));
        Assert.assertEquals(1, t2.compareTo(t1));
        Assert.assertEquals(0, t2.compareTo(t2));
    }

    @Test
    public void zscan() {
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 2, "b");
        ScanResult<Tuple> result = jedis.zscan("foo", ScanParams.SCAN_POINTER_START);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        jedis.zadd(bfoo, 1, ba);
        jedis.zadd(bfoo, 1, bb);
        ScanResult<Tuple> bResult = jedis.zscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void zscanMatch() {
        ScanParams params = new ScanParams();
        params.match("a*");
        jedis.zadd("foo", 2, "b");
        jedis.zadd("foo", 1, "a");
        jedis.zadd("foo", 11, "aa");
        ScanResult<Tuple> result = jedis.zscan("foo", ScanParams.SCAN_POINTER_START, params);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.match(bbarstar);
        jedis.zadd(bfoo, 2, bbar1);
        jedis.zadd(bfoo, 1, bbar2);
        jedis.zadd(bfoo, 11, bbar3);
        ScanResult<Tuple> bResult = jedis.zscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void zscanCount() {
        ScanParams params = new ScanParams();
        params.count(2);
        jedis.zadd("foo", 1, "a1");
        jedis.zadd("foo", 2, "a2");
        jedis.zadd("foo", 3, "a3");
        jedis.zadd("foo", 4, "a4");
        jedis.zadd("foo", 5, "a5");
        ScanResult<Tuple> result = jedis.zscan("foo", ScanParams.SCAN_POINTER_START, params);
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.count(2);
        jedis.zadd(bfoo, 2, bbar1);
        jedis.zadd(bfoo, 1, bbar2);
        jedis.zadd(bfoo, 11, bbar3);
        ScanResult<Tuple> bResult = jedis.zscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertFalse(bResult.getResult().isEmpty());
    }
}

