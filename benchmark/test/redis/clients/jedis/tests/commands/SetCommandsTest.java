package redis.clients.jedis.tests.commands;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;


public class SetCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bbar = new byte[]{ 5, 6, 7, 8 };

    final byte[] bcar = new byte[]{ 9, 10, 11, 12 };

    final byte[] ba = new byte[]{ 10 };

    final byte[] bb = new byte[]{ 11 };

    final byte[] bc = new byte[]{ 12 };

    final byte[] bd = new byte[]{ 13 };

    final byte[] bx = new byte[]{ 66 };

    final byte[] bbar1 = new byte[]{ 5, 6, 7, 8, 10 };

    final byte[] bbar2 = new byte[]{ 5, 6, 7, 8, 11 };

    final byte[] bbar3 = new byte[]{ 5, 6, 7, 8, 12 };

    final byte[] bbarstar = new byte[]{ 5, 6, 7, 8, '*' };

    @Test
    public void sadd() {
        long status = jedis.sadd("foo", "a");
        Assert.assertEquals(1, status);
        status = jedis.sadd("foo", "a");
        Assert.assertEquals(0, status);
        long bstatus = jedis.sadd(bfoo, ba);
        Assert.assertEquals(1, bstatus);
        bstatus = jedis.sadd(bfoo, ba);
        Assert.assertEquals(0, bstatus);
    }

    @Test
    public void smembers() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        Set<String> expected = new HashSet<String>();
        expected.add("a");
        expected.add("b");
        Set<String> members = jedis.smembers("foo");
        Assert.assertEquals(expected, members);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(ba);
        Set<byte[]> bmembers = jedis.smembers(bfoo);
        assertEquals(bexpected, bmembers);
    }

    @Test
    public void srem() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        long status = jedis.srem("foo", "a");
        Set<String> expected = new HashSet<String>();
        expected.add("b");
        Assert.assertEquals(1, status);
        Assert.assertEquals(expected, jedis.smembers("foo"));
        status = jedis.srem("foo", "bar");
        Assert.assertEquals(0, status);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        long bstatus = jedis.srem(bfoo, ba);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        Assert.assertEquals(1, bstatus);
        assertEquals(bexpected, jedis.smembers(bfoo));
        bstatus = jedis.srem(bfoo, bbar);
        Assert.assertEquals(0, bstatus);
    }

    @Test
    public void spop() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        String member = jedis.spop("foo");
        Assert.assertTrue((("a".equals(member)) || ("b".equals(member))));
        Assert.assertEquals(1, jedis.smembers("foo").size());
        member = jedis.spop("bar");
        Assert.assertNull(member);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        byte[] bmember = jedis.spop(bfoo);
        Assert.assertTrue(((Arrays.equals(ba, bmember)) || (Arrays.equals(bb, bmember))));
        Assert.assertEquals(1, jedis.smembers(bfoo).size());
        bmember = jedis.spop(bbar);
        Assert.assertNull(bmember);
    }

    @Test
    public void spopWithCount() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        Set<String> expected = new HashSet<String>();
        expected.add("a");
        expected.add("b");
        Set<String> members = jedis.spop("foo", 2);
        Assert.assertEquals(2, members.size());
        Assert.assertEquals(expected, members);
        members = jedis.spop("foo", 2);
        Assert.assertTrue(members.isEmpty());
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(ba);
        Set<byte[]> bmembers = jedis.spop(bfoo, 2);
        Assert.assertEquals(2, bmembers.size());
        assertEquals(bexpected, bmembers);
        bmembers = jedis.spop(bfoo, 2);
        Assert.assertTrue(bmembers.isEmpty());
    }

    @Test
    public void smove() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("bar", "c");
        long status = jedis.smove("foo", "bar", "a");
        Set<String> expectedSrc = new HashSet<String>();
        expectedSrc.add("b");
        Set<String> expectedDst = new HashSet<String>();
        expectedDst.add("c");
        expectedDst.add("a");
        Assert.assertEquals(status, 1);
        Assert.assertEquals(expectedSrc, jedis.smembers("foo"));
        Assert.assertEquals(expectedDst, jedis.smembers("bar"));
        status = jedis.smove("foo", "bar", "a");
        Assert.assertEquals(status, 0);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bbar, bc);
        long bstatus = jedis.smove(bfoo, bbar, ba);
        Set<byte[]> bexpectedSrc = new HashSet<byte[]>();
        bexpectedSrc.add(bb);
        Set<byte[]> bexpectedDst = new HashSet<byte[]>();
        bexpectedDst.add(bc);
        bexpectedDst.add(ba);
        Assert.assertEquals(bstatus, 1);
        assertEquals(bexpectedSrc, jedis.smembers(bfoo));
        assertEquals(bexpectedDst, jedis.smembers(bbar));
        bstatus = jedis.smove(bfoo, bbar, ba);
        Assert.assertEquals(bstatus, 0);
    }

    @Test
    public void scard() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        long card = jedis.scard("foo");
        Assert.assertEquals(2, card);
        card = jedis.scard("bar");
        Assert.assertEquals(0, card);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        long bcard = jedis.scard(bfoo);
        Assert.assertEquals(2, bcard);
        bcard = jedis.scard(bbar);
        Assert.assertEquals(0, bcard);
    }

    @Test
    public void sismember() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        Assert.assertTrue(jedis.sismember("foo", "a"));
        Assert.assertFalse(jedis.sismember("foo", "c"));
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        Assert.assertTrue(jedis.sismember(bfoo, ba));
        Assert.assertFalse(jedis.sismember(bfoo, bc));
    }

    @Test
    public void sinter() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("bar", "b");
        jedis.sadd("bar", "c");
        Set<String> expected = new HashSet<String>();
        expected.add("b");
        Set<String> intersection = jedis.sinter("foo", "bar");
        Assert.assertEquals(expected, intersection);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bbar, bb);
        jedis.sadd(bbar, bc);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        Set<byte[]> bintersection = jedis.sinter(bfoo, bbar);
        assertEquals(bexpected, bintersection);
    }

    @Test
    public void sinterstore() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("bar", "b");
        jedis.sadd("bar", "c");
        Set<String> expected = new HashSet<String>();
        expected.add("b");
        long status = jedis.sinterstore("car", "foo", "bar");
        Assert.assertEquals(1, status);
        Assert.assertEquals(expected, jedis.smembers("car"));
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bbar, bb);
        jedis.sadd(bbar, bc);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        long bstatus = jedis.sinterstore(bcar, bfoo, bbar);
        Assert.assertEquals(1, bstatus);
        assertEquals(bexpected, jedis.smembers(bcar));
    }

    @Test
    public void sunion() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("bar", "b");
        jedis.sadd("bar", "c");
        Set<String> expected = new HashSet<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        Set<String> union = jedis.sunion("foo", "bar");
        Assert.assertEquals(expected, union);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bbar, bb);
        jedis.sadd(bbar, bc);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(bc);
        bexpected.add(ba);
        Set<byte[]> bunion = jedis.sunion(bfoo, bbar);
        assertEquals(bexpected, bunion);
    }

    @Test
    public void sunionstore() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("bar", "b");
        jedis.sadd("bar", "c");
        Set<String> expected = new HashSet<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        long status = jedis.sunionstore("car", "foo", "bar");
        Assert.assertEquals(3, status);
        Assert.assertEquals(expected, jedis.smembers("car"));
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bbar, bb);
        jedis.sadd(bbar, bc);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(bc);
        bexpected.add(ba);
        long bstatus = jedis.sunionstore(bcar, bfoo, bbar);
        Assert.assertEquals(3, bstatus);
        assertEquals(bexpected, jedis.smembers(bcar));
    }

    @Test
    public void sdiff() {
        jedis.sadd("foo", "x");
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("foo", "c");
        jedis.sadd("bar", "c");
        jedis.sadd("car", "a");
        jedis.sadd("car", "d");
        Set<String> expected = new HashSet<String>();
        expected.add("x");
        expected.add("b");
        Set<String> diff = jedis.sdiff("foo", "bar", "car");
        Assert.assertEquals(expected, diff);
        // Binary
        jedis.sadd(bfoo, bx);
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bfoo, bc);
        jedis.sadd(bbar, bc);
        jedis.sadd(bcar, ba);
        jedis.sadd(bcar, bd);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bb);
        bexpected.add(bx);
        Set<byte[]> bdiff = jedis.sdiff(bfoo, bbar, bcar);
        assertEquals(bexpected, bdiff);
    }

    @Test
    public void sdiffstore() {
        jedis.sadd("foo", "x");
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        jedis.sadd("foo", "c");
        jedis.sadd("bar", "c");
        jedis.sadd("car", "a");
        jedis.sadd("car", "d");
        Set<String> expected = new HashSet<String>();
        expected.add("d");
        expected.add("a");
        long status = jedis.sdiffstore("tar", "foo", "bar", "car");
        Assert.assertEquals(2, status);
        Assert.assertEquals(expected, jedis.smembers("car"));
        // Binary
        jedis.sadd(bfoo, bx);
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        jedis.sadd(bfoo, bc);
        jedis.sadd(bbar, bc);
        jedis.sadd(bcar, ba);
        jedis.sadd(bcar, bd);
        Set<byte[]> bexpected = new HashSet<byte[]>();
        bexpected.add(bd);
        bexpected.add(ba);
        long bstatus = jedis.sdiffstore("tar".getBytes(), bfoo, bbar, bcar);
        Assert.assertEquals(2, bstatus);
        assertEquals(bexpected, jedis.smembers(bcar));
    }

    @Test
    public void srandmember() {
        jedis.sadd("foo", "a");
        jedis.sadd("foo", "b");
        String member = jedis.srandmember("foo");
        Assert.assertTrue((("a".equals(member)) || ("b".equals(member))));
        Assert.assertEquals(2, jedis.smembers("foo").size());
        member = jedis.srandmember("bar");
        Assert.assertNull(member);
        // Binary
        jedis.sadd(bfoo, ba);
        jedis.sadd(bfoo, bb);
        byte[] bmember = jedis.srandmember(bfoo);
        Assert.assertTrue(((Arrays.equals(ba, bmember)) || (Arrays.equals(bb, bmember))));
        Assert.assertEquals(2, jedis.smembers(bfoo).size());
        bmember = jedis.srandmember(bbar);
        Assert.assertNull(bmember);
    }

    @Test
    public void sscan() {
        jedis.sadd("foo", "a", "b");
        ScanResult<String> result = jedis.sscan("foo", ScanParams.SCAN_POINTER_START);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        jedis.sadd(bfoo, ba, bb);
        ScanResult<byte[]> bResult = jedis.sscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void sscanMatch() {
        ScanParams params = new ScanParams();
        params.match("a*");
        jedis.sadd("foo", "b", "a", "aa");
        ScanResult<String> result = jedis.sscan("foo", ScanParams.SCAN_POINTER_START, params);
        assertEquals(ScanParams.SCAN_POINTER_START, result.getCursor());
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.match(bbarstar);
        jedis.sadd(bfoo, bbar1, bbar2, bbar3);
        ScanResult<byte[]> bResult = jedis.sscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertArrayEquals(ScanParams.SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
        Assert.assertFalse(bResult.getResult().isEmpty());
    }

    @Test
    public void sscanCount() {
        ScanParams params = new ScanParams();
        params.count(2);
        jedis.sadd("foo", "a1", "a2", "a3", "a4", "a5");
        ScanResult<String> result = jedis.sscan("foo", ScanParams.SCAN_POINTER_START, params);
        Assert.assertFalse(result.getResult().isEmpty());
        // binary
        params = new ScanParams();
        params.count(2);
        jedis.sadd(bfoo, bbar1, bbar2, bbar3);
        ScanResult<byte[]> bResult = jedis.sscan(bfoo, ScanParams.SCAN_POINTER_START_BINARY, params);
        Assert.assertFalse(bResult.getResult().isEmpty());
    }
}

