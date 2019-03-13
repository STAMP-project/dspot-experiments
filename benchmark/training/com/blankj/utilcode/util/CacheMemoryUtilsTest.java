package com.blankj.utilcode.util;


import org.junit.Assert;
import org.junit.Test;

import static CacheMemoryUtils.SEC;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/06/13
 *     desc  : test CacheMemoryUtils
 * </pre>
 */
public class CacheMemoryUtilsTest extends BaseTest {
    private CacheMemoryUtils mCacheMemoryUtils1 = CacheMemoryUtils.getInstance();

    private CacheMemoryUtils mCacheMemoryUtils2 = CacheMemoryUtils.getInstance(3);

    @Test
    public void get() {
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, mCacheMemoryUtils1.get(String.valueOf(i)));
        }
        for (int i = 0; i < 10; i++) {
            if (i < 7) {
                Assert.assertNull(mCacheMemoryUtils2.get(String.valueOf(i)));
            } else {
                Assert.assertEquals(i, mCacheMemoryUtils2.get(String.valueOf(i)));
            }
        }
    }

    @Test
    public void getExpired() throws Exception {
        mCacheMemoryUtils1.put("10", 10, (2 * (SEC)));
        Assert.assertEquals(10, mCacheMemoryUtils1.get("10"));
        Thread.sleep(1500);
        Assert.assertEquals(10, mCacheMemoryUtils1.get("10"));
        Thread.sleep(1500);
        Assert.assertNull(mCacheMemoryUtils1.get("10"));
    }

    @Test
    public void getDefault() {
        Assert.assertNull(mCacheMemoryUtils1.get("10"));
        Assert.assertEquals("10", mCacheMemoryUtils1.get("10", "10"));
    }

    @Test
    public void getCacheCount() {
        Assert.assertEquals(10, mCacheMemoryUtils1.getCacheCount());
        Assert.assertEquals(3, mCacheMemoryUtils2.getCacheCount());
    }

    @Test
    public void remove() {
        Assert.assertEquals(0, mCacheMemoryUtils1.remove("0"));
        Assert.assertNull(mCacheMemoryUtils1.get("0"));
        Assert.assertNull(mCacheMemoryUtils1.remove("0"));
    }

    @Test
    public void clear() {
        mCacheMemoryUtils1.clear();
        mCacheMemoryUtils2.clear();
        for (int i = 0; i < 10; i++) {
            Assert.assertNull(mCacheMemoryUtils1.get(String.valueOf(i)));
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertNull(mCacheMemoryUtils2.get(String.valueOf(i)));
        }
        Assert.assertEquals(0, mCacheMemoryUtils1.getCacheCount());
        Assert.assertEquals(0, mCacheMemoryUtils2.getCacheCount());
    }
}

