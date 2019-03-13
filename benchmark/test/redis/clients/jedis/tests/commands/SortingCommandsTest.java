package redis.clients.jedis.tests.commands;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.SortingParams;


public class SortingCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bbar1 = new byte[]{ 5, 6, 7, 8, '1' };

    final byte[] bbar2 = new byte[]{ 5, 6, 7, 8, '2' };

    final byte[] bbar3 = new byte[]{ 5, 6, 7, 8, '3' };

    final byte[] bbar10 = new byte[]{ 5, 6, 7, 8, '1', '0' };

    final byte[] bbarstar = new byte[]{ 5, 6, 7, 8, '*' };

    final byte[] bcar1 = new byte[]{ 10, 11, 12, 13, '1' };

    final byte[] bcar2 = new byte[]{ 10, 11, 12, 13, '2' };

    final byte[] bcar10 = new byte[]{ 10, 11, 12, 13, '1', '0' };

    final byte[] bcarstar = new byte[]{ 10, 11, 12, 13, '*' };

    final byte[] b1 = new byte[]{ '1' };

    final byte[] b2 = new byte[]{ '2' };

    final byte[] b3 = new byte[]{ '3' };

    final byte[] b10 = new byte[]{ '1', '0' };

    @Test
    public void sort() {
        jedis.lpush("foo", "3");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "1");
        List<String> result = jedis.sort("foo");
        List<String> expected = new ArrayList<String>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        Assert.assertEquals(expected, result);
        // Binary
        jedis.lpush(bfoo, b3);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b1);
        List<byte[]> bresult = jedis.sort(bfoo);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b1);
        bexpected.add(b2);
        bexpected.add(b3);
        assertEquals(bexpected, bresult);
    }

    @Test
    public void sortBy() {
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "3");
        jedis.lpush("foo", "1");
        jedis.set("bar1", "3");
        jedis.set("bar2", "2");
        jedis.set("bar3", "1");
        SortingParams sp = new SortingParams();
        sp.by("bar*");
        List<String> result = jedis.sort("foo", sp);
        List<String> expected = new ArrayList<String>();
        expected.add("3");
        expected.add("2");
        expected.add("1");
        Assert.assertEquals(expected, result);
        // Binary
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b3);
        jedis.lpush(bfoo, b1);
        jedis.set(bbar1, b3);
        jedis.set(bbar2, b2);
        jedis.set(bbar3, b1);
        SortingParams bsp = new SortingParams();
        bsp.by(bbarstar);
        List<byte[]> bresult = jedis.sort(bfoo, bsp);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b3);
        bexpected.add(b2);
        bexpected.add(b1);
        assertEquals(bexpected, bresult);
    }

    @Test
    public void sortDesc() {
        jedis.lpush("foo", "3");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "1");
        SortingParams sp = new SortingParams();
        sp.desc();
        List<String> result = jedis.sort("foo", sp);
        List<String> expected = new ArrayList<String>();
        expected.add("3");
        expected.add("2");
        expected.add("1");
        Assert.assertEquals(expected, result);
        // Binary
        jedis.lpush(bfoo, b3);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b1);
        SortingParams bsp = new SortingParams();
        bsp.desc();
        List<byte[]> bresult = jedis.sort(bfoo, bsp);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b3);
        bexpected.add(b2);
        bexpected.add(b1);
        assertEquals(bexpected, bresult);
    }

    @Test
    public void sortLimit() {
        for (int n = 10; n > 0; n--) {
            jedis.lpush("foo", String.valueOf(n));
        }
        SortingParams sp = new SortingParams();
        sp.limit(0, 3);
        List<String> result = jedis.sort("foo", sp);
        List<String> expected = new ArrayList<String>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        Assert.assertEquals(expected, result);
        // Binary
        jedis.rpush(bfoo, new byte[]{ ((byte) ('4')) });
        jedis.rpush(bfoo, new byte[]{ ((byte) ('3')) });
        jedis.rpush(bfoo, new byte[]{ ((byte) ('2')) });
        jedis.rpush(bfoo, new byte[]{ ((byte) ('1')) });
        SortingParams bsp = new SortingParams();
        bsp.limit(0, 3);
        List<byte[]> bresult = jedis.sort(bfoo, bsp);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b1);
        bexpected.add(b2);
        bexpected.add(b3);
        assertEquals(bexpected, bresult);
    }

    @Test
    public void sortAlpha() {
        jedis.lpush("foo", "1");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "10");
        SortingParams sp = new SortingParams();
        sp.alpha();
        List<String> result = jedis.sort("foo", sp);
        List<String> expected = new ArrayList<String>();
        expected.add("1");
        expected.add("10");
        expected.add("2");
        Assert.assertEquals(expected, result);
        // Binary
        jedis.lpush(bfoo, b1);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b10);
        SortingParams bsp = new SortingParams();
        bsp.alpha();
        List<byte[]> bresult = jedis.sort(bfoo, bsp);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b1);
        bexpected.add(b10);
        bexpected.add(b2);
        assertEquals(bexpected, bresult);
    }

    @Test
    public void sortGet() {
        jedis.lpush("foo", "1");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "10");
        jedis.set("bar1", "bar1");
        jedis.set("bar2", "bar2");
        jedis.set("bar10", "bar10");
        jedis.set("car1", "car1");
        jedis.set("car2", "car2");
        jedis.set("car10", "car10");
        SortingParams sp = new SortingParams();
        sp.get("car*", "bar*");
        List<String> result = jedis.sort("foo", sp);
        List<String> expected = new ArrayList<String>();
        expected.add("car1");
        expected.add("bar1");
        expected.add("car2");
        expected.add("bar2");
        expected.add("car10");
        expected.add("bar10");
        Assert.assertEquals(expected, result);
        // Binary
        jedis.lpush(bfoo, b1);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b10);
        jedis.set(bbar1, bbar1);
        jedis.set(bbar2, bbar2);
        jedis.set(bbar10, bbar10);
        jedis.set(bcar1, bcar1);
        jedis.set(bcar2, bcar2);
        jedis.set(bcar10, bcar10);
        SortingParams bsp = new SortingParams();
        bsp.get(bcarstar, bbarstar);
        List<byte[]> bresult = jedis.sort(bfoo, bsp);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bcar1);
        bexpected.add(bbar1);
        bexpected.add(bcar2);
        bexpected.add(bbar2);
        bexpected.add(bcar10);
        bexpected.add(bbar10);
        assertEquals(bexpected, bresult);
    }

    @Test
    public void sortStore() {
        jedis.lpush("foo", "1");
        jedis.lpush("foo", "2");
        jedis.lpush("foo", "10");
        long result = jedis.sort("foo", "result");
        List<String> expected = new ArrayList<String>();
        expected.add("1");
        expected.add("2");
        expected.add("10");
        Assert.assertEquals(3, result);
        Assert.assertEquals(expected, jedis.lrange("result", 0, 1000));
        // Binary
        jedis.lpush(bfoo, b1);
        jedis.lpush(bfoo, b2);
        jedis.lpush(bfoo, b10);
        byte[] bkresult = new byte[]{ 9, 10, 11, 12 };
        long bresult = jedis.sort(bfoo, bkresult);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(b1);
        bexpected.add(b2);
        bexpected.add(b10);
        Assert.assertEquals(3, bresult);
        assertEquals(bexpected, jedis.lrange(bkresult, 0, 1000));
    }
}

