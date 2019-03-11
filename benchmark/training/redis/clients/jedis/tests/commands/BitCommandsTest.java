package redis.clients.jedis.tests.commands;


import BitOP.AND;
import BitOP.NOT;
import BitOP.OR;
import BitOP.XOR;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Protocol;


public class BitCommandsTest extends JedisCommandTestBase {
    @Test
    public void setAndgetbit() {
        boolean bit = jedis.setbit("foo", 0, true);
        Assert.assertEquals(false, bit);
        bit = jedis.getbit("foo", 0);
        Assert.assertEquals(true, bit);
        boolean bbit = jedis.setbit("bfoo".getBytes(), 0, "1".getBytes());
        Assert.assertFalse(bbit);
        bbit = jedis.getbit("bfoo".getBytes(), 0);
        Assert.assertTrue(bbit);
    }

    @Test
    public void bitpos() {
        String foo = "foo";
        jedis.set(foo, String.valueOf(0));
        jedis.setbit(foo, 3, true);
        jedis.setbit(foo, 7, true);
        jedis.setbit(foo, 13, true);
        jedis.setbit(foo, 39, true);
        /* byte: 0 1 2 3 4 bit: 00010001 / 00000100 / 00000000 / 00000000 / 00000001 */
        long offset = jedis.bitpos(foo, true);
        Assert.assertEquals(2, offset);
        offset = jedis.bitpos(foo, false);
        Assert.assertEquals(0, offset);
        offset = jedis.bitpos(foo, true, new BitPosParams(1));
        Assert.assertEquals(13, offset);
        offset = jedis.bitpos(foo, false, new BitPosParams(1));
        Assert.assertEquals(8, offset);
        offset = jedis.bitpos(foo, true, new BitPosParams(2, 3));
        Assert.assertEquals((-1), offset);
        offset = jedis.bitpos(foo, false, new BitPosParams(2, 3));
        Assert.assertEquals(16, offset);
        offset = jedis.bitpos(foo, true, new BitPosParams(3, 4));
        Assert.assertEquals(39, offset);
    }

    @Test
    public void bitposBinary() {
        // binary
        byte[] bfoo = new byte[]{ 1, 2, 3, 4 };
        jedis.set(bfoo, Protocol.toByteArray(0));
        jedis.setbit(bfoo, 3, true);
        jedis.setbit(bfoo, 7, true);
        jedis.setbit(bfoo, 13, true);
        jedis.setbit(bfoo, 39, true);
        /* byte: 0 1 2 3 4 bit: 00010001 / 00000100 / 00000000 / 00000000 / 00000001 */
        long offset = jedis.bitpos(bfoo, true);
        Assert.assertEquals(2, offset);
        offset = jedis.bitpos(bfoo, false);
        Assert.assertEquals(0, offset);
        offset = jedis.bitpos(bfoo, true, new BitPosParams(1));
        Assert.assertEquals(13, offset);
        offset = jedis.bitpos(bfoo, false, new BitPosParams(1));
        Assert.assertEquals(8, offset);
        offset = jedis.bitpos(bfoo, true, new BitPosParams(2, 3));
        Assert.assertEquals((-1), offset);
        offset = jedis.bitpos(bfoo, false, new BitPosParams(2, 3));
        Assert.assertEquals(16, offset);
        offset = jedis.bitpos(bfoo, true, new BitPosParams(3, 4));
        Assert.assertEquals(39, offset);
    }

    @Test
    public void bitposWithNoMatchingBitExist() {
        String foo = "foo";
        jedis.set(foo, String.valueOf(0));
        for (int idx = 0; idx < 8; idx++) {
            jedis.setbit(foo, idx, true);
        }
        /* byte: 0 bit: 11111111 */
        long offset = jedis.bitpos(foo, false);
        // offset should be last index + 1
        Assert.assertEquals(8, offset);
    }

    @Test
    public void bitposWithNoMatchingBitExistWithinRange() {
        String foo = "foo";
        jedis.set(foo, String.valueOf(0));
        for (int idx = 0; idx < (8 * 5); idx++) {
            jedis.setbit(foo, idx, true);
        }
        /* byte: 0 1 2 3 4 bit: 11111111 / 11111111 / 11111111 / 11111111 / 11111111 */
        long offset = jedis.bitpos(foo, false, new BitPosParams(2, 3));
        // offset should be -1
        Assert.assertEquals((-1), offset);
    }

    @Test
    public void setAndgetrange() {
        jedis.set("key1", "Hello World");
        long reply = jedis.setrange("key1", 6, "Jedis");
        Assert.assertEquals(11, reply);
        Assert.assertEquals(jedis.get("key1"), "Hello Jedis");
        Assert.assertEquals("Hello", jedis.getrange("key1", 0, 4));
        Assert.assertEquals("Jedis", jedis.getrange("key1", 6, 11));
    }

    @Test
    public void bitCount() {
        jedis.del("foo");
        jedis.setbit("foo", 16, true);
        jedis.setbit("foo", 24, true);
        jedis.setbit("foo", 40, true);
        jedis.setbit("foo", 56, true);
        long c4 = jedis.bitcount("foo");
        Assert.assertEquals(4, c4);
        long c3 = jedis.bitcount("foo", 2L, 5L);
        Assert.assertEquals(3, c3);
        jedis.del("foo");
    }

    @Test
    public void bitOp() {
        jedis.set("key1", "`");
        jedis.set("key2", "D");
        jedis.bitop(AND, "resultAnd", "key1", "key2");
        String resultAnd = jedis.get("resultAnd");
        Assert.assertEquals("@", resultAnd);
        jedis.bitop(OR, "resultOr", "key1", "key2");
        String resultOr = jedis.get("resultOr");
        Assert.assertEquals("d", resultOr);
        jedis.bitop(XOR, "resultXor", "key1", "key2");
        String resultXor = jedis.get("resultXor");
        Assert.assertEquals("$", resultXor);
        jedis.del("resultAnd");
        jedis.del("resultOr");
        jedis.del("resultXor");
        jedis.del("key1");
        jedis.del("key2");
    }

    @Test
    public void bitOpNot() {
        jedis.del("key");
        jedis.setbit("key", 0, true);
        jedis.setbit("key", 4, true);
        jedis.bitop(NOT, "resultNot", "key");
        String resultNot = jedis.get("resultNot");
        Assert.assertEquals("w", resultNot);
        jedis.del("key");
        jedis.del("resultNot");
    }
}

