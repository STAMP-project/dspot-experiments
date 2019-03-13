package redis.clients.jedis.tests;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.util.KeyMergeUtil;
import redis.clients.util.SafeEncoder;


public class KeyMergeUtilTest {
    @Test
    public void testMergeBinaryKeys() throws Exception {
        byte[] key = SafeEncoder.encode("hello");
        byte[][] keys = new byte[2][];
        keys[0] = SafeEncoder.encode("world");
        keys[1] = SafeEncoder.encode("jedis");
        byte[][] mergedKeys = KeyMergeUtil.merge(key, keys);
        Assert.assertNotNull(mergedKeys);
        Assert.assertEquals(3, mergedKeys.length);
        Assert.assertEquals(key, mergedKeys[0]);
        Assert.assertEquals(keys[0], mergedKeys[1]);
        Assert.assertEquals(keys[1], mergedKeys[2]);
    }

    @Test
    public void testMergeStringKeys() throws Exception {
        String key = "hello";
        String[] keys = new String[2];
        keys[0] = "world";
        keys[1] = "jedis";
        String[] mergedKeys = KeyMergeUtil.merge(key, keys);
        Assert.assertNotNull(mergedKeys);
        Assert.assertEquals(3, mergedKeys.length);
        Assert.assertEquals(key, mergedKeys[0]);
        Assert.assertEquals(keys[0], mergedKeys[1]);
        Assert.assertEquals(keys[1], mergedKeys[2]);
    }
}

