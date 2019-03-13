package redis.clients.jedis.tests;


import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.tests.commands.JedisCommandTestBase;


public class TupleSortedSetTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] ba = new byte[]{ 10 };

    final byte[] bb = new byte[]{ 11 };

    final byte[] bc = new byte[]{ 12 };

    final byte[] bd = new byte[]{ 13 };

    final byte[] be = new byte[]{ 14 };

    final byte[] bf = new byte[]{ 15 };

    @Test
    public void testBinary() {
        SortedSet<Tuple> sortedSet = new TreeSet<Tuple>();
        jedis.zadd(bfoo, 0.0, ba);
        sortedSet.add(new Tuple(ba, 0.0));
        jedis.zadd(bfoo, 1.0, bb);
        sortedSet.add(new Tuple(bb, 1.0));
        Set<Tuple> zrange = jedis.zrangeWithScores(bfoo, 0, (-1));
        Assert.assertEquals(sortedSet, zrange);
        jedis.zadd(bfoo, (-0.3), bc);
        sortedSet.add(new Tuple(bc, (-0.3)));
        jedis.zadd(bfoo, 0.3, bf);
        sortedSet.add(new Tuple(bf, 0.3));
        jedis.zadd(bfoo, 0.3, be);
        sortedSet.add(new Tuple(be, 0.3));
        jedis.zadd(bfoo, 0.3, bd);
        sortedSet.add(new Tuple(bd, 0.3));
        zrange = jedis.zrangeWithScores(bfoo, 0, (-1));
        Assert.assertEquals(sortedSet, zrange);
    }

    @Test
    public void testString() {
        SortedSet<Tuple> sortedSet = new TreeSet<Tuple>();
        jedis.zadd("foo", 0.0, "a");
        sortedSet.add(new Tuple("a", 0.0));
        jedis.zadd("foo", 1.0, "b");
        sortedSet.add(new Tuple("b", 1.0));
        Set<Tuple> range = jedis.zrangeWithScores("foo", 0, (-1));
        Assert.assertEquals(sortedSet, range);
        jedis.zadd("foo", (-0.3), "c");
        sortedSet.add(new Tuple("c", (-0.3)));
        jedis.zadd("foo", 0.3, "f");
        sortedSet.add(new Tuple("f", 0.3));
        jedis.zadd("foo", 0.3, "e");
        sortedSet.add(new Tuple("e", 0.3));
        jedis.zadd("foo", 0.3, "d");
        sortedSet.add(new Tuple("d", 0.3));
        range = jedis.zrangeWithScores("foo", 0, (-1));
        Assert.assertEquals(sortedSet, range);
    }
}

