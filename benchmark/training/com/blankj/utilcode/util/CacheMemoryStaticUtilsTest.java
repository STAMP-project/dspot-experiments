package com.blankj.utilcode.util;


import org.junit.Assert;
import org.junit.Test;

import static CacheMemoryUtils.SEC;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/01/04
 *     desc  : test CacheMemoryStaticUtils
 * </pre>
 */
public class CacheMemoryStaticUtilsTest extends BaseTest {
    private CacheMemoryUtils mCacheMemoryUtils = CacheMemoryUtils.getInstance(3);

    @Test
    public void get() {
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, CacheMemoryStaticUtils.get(String.valueOf(i)));
        }
        for (int i = 0; i < 10; i++) {
            if (i < 7) {
                Assert.assertNull(CacheMemoryStaticUtils.get(String.valueOf(i), mCacheMemoryUtils));
            } else {
                Assert.assertEquals(i, CacheMemoryStaticUtils.get(String.valueOf(i), mCacheMemoryUtils));
            }
        }
    }

    @Test
    public void getExpired() throws Exception {
        CacheMemoryStaticUtils.put("10", 10, (2 * (SEC)));
        Assert.assertEquals(10, CacheMemoryStaticUtils.get("10"));
        Thread.sleep(1500);
        Assert.assertEquals(10, CacheMemoryStaticUtils.get("10"));
        Thread.sleep(1500);
        Assert.assertNull(CacheMemoryStaticUtils.get("10"));
    }

    @Test
    public void getDefault() {
        Assert.assertNull(CacheMemoryStaticUtils.get("10"));
        Assert.assertEquals("10", CacheMemoryStaticUtils.get("10", "10"));
    }

    @Test
    public void getCacheCount() {
        Assert.assertEquals(10, CacheMemoryStaticUtils.getCacheCount());
        Assert.assertEquals(3, CacheMemoryStaticUtils.getCacheCount(mCacheMemoryUtils));
    }

    @Test
    public void remove() {
        Assert.assertEquals(0, CacheMemoryStaticUtils.remove("0"));
        Assert.assertNull(CacheMemoryStaticUtils.get("0"));
        Assert.assertNull(CacheMemoryStaticUtils.remove("0"));
    }

    @Test
    public void clear() {
        CacheMemoryStaticUtils.clear();
        CacheMemoryStaticUtils.clear(mCacheMemoryUtils);
        for (int i = 0; i < 10; i++) {
            Assert.assertNull(CacheMemoryStaticUtils.get(String.valueOf(i)));
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertNull(CacheMemoryStaticUtils.get(String.valueOf(i), mCacheMemoryUtils));
        }
        Assert.assertEquals(0, CacheMemoryStaticUtils.getCacheCount());
        Assert.assertEquals(0, CacheMemoryStaticUtils.getCacheCount(mCacheMemoryUtils));
    }
}

