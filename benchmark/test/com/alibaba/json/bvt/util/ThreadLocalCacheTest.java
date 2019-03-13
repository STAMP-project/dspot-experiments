package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.util.ThreadLocalCache;
import junit.framework.TestCase;
import org.junit.Assert;


public class ThreadLocalCacheTest extends TestCase {
    public void test() throws Exception {
        ThreadLocalCacheTest.clearChars();
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(0).length, (1024 * 64));
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(1024).length, (1024 * 64));
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(2048).length, (1024 * 64));
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(0).length, (1024 * 64));
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars((1024 * 128)).length, (1024 * 128));
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(0).length, (1024 * 64));
        ThreadLocalCacheTest.clearChars();
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(2048).length, (1024 * 64));
        ThreadLocalCacheTest.clearChars();
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars((1024 * 256)).length, (1024 * 256));
        Assert.assertEquals(ThreadLocalCacheTest.allocateChars(0).length, (1024 * 64));
        ThreadLocalCacheTest.clearChars();
    }

    public void testBytes() throws Exception {
        ThreadLocalCacheTest.clearBytes();
        Assert.assertEquals(ThreadLocalCacheTest.getBytes(0).length, 8192);
        Assert.assertEquals(ThreadLocalCacheTest.getBytes(1024).length, 8192);
        Assert.assertEquals(ThreadLocalCacheTest.getBytes((8192 * 2)).length, (8192 * 2));
        Assert.assertEquals(ThreadLocalCacheTest.getBytes(0).length, 8192);
        Assert.assertSame(ThreadLocalCacheTest.getBytes(0), ThreadLocalCacheTest.getBytes(1204));
        Assert.assertNotSame(ThreadLocalCacheTest.getBytes(9000), ThreadLocalCacheTest.getBytes(9000));
        ThreadLocalCacheTest.clearBytes();
        Assert.assertEquals(ThreadLocalCacheTest.getBytes(2048).length, 8192);
        ThreadLocalCacheTest.clearBytes();
        Assert.assertEquals(ThreadLocalCacheTest.getBytes((1024 * 256)).length, (1024 * 256));
        Assert.assertEquals(ThreadLocalCacheTest.getBytes(0).length, 8192);
        ThreadLocalCacheTest.clearBytes();
    }

    public void test_chars() throws Exception {
        ThreadLocalCache.getChars(10);
        ThreadLocalCache.getChars(10);
        ThreadLocalCache.getChars(20);
        ThreadLocalCache.getChars(30);
        ThreadLocalCacheTest.clearChars();
        ThreadLocalCache.getChars(10);
        ThreadLocalCache.getChars(10);
        ThreadLocalCache.getChars(20);
        ThreadLocalCache.getChars(30);
        ThreadLocalCache.clearChars();
        ThreadLocalCache.getUTF8Decoder();
        ThreadLocalCache.getUTF8Decoder();
    }

    public void test_bytes() throws Exception {
        ThreadLocalCache.getBytes(10);
        ThreadLocalCache.getBytes(10);
        ThreadLocalCache.getBytes(20);
        ThreadLocalCache.getBytes(30);
        ThreadLocalCacheTest.clearBytes();
        ThreadLocalCache.getBytes(10);
        ThreadLocalCache.getBytes(10);
        ThreadLocalCache.getBytes(20);
        ThreadLocalCache.getBytes(30);
    }
}

