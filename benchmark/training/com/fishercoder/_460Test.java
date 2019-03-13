package com.fishercoder;


import _460.LFUCache;
import org.junit.Assert;
import org.junit.Test;


public class _460Test {
    private static LFUCache lfuCache;

    @Test
    public void test1() {
        _460Test.lfuCache = new LFUCache(2);
        _460Test.lfuCache.put(1, 1);
        _460Test.lfuCache.put(2, 2);
        Assert.assertEquals(1, _460Test.lfuCache.get(1));
        _460Test.lfuCache.put(3, 3);
        Assert.assertEquals((-1), _460Test.lfuCache.get(2));
        Assert.assertEquals(3, _460Test.lfuCache.get(3));
        _460Test.lfuCache.put(4, 4);
        Assert.assertEquals((-1), _460Test.lfuCache.get(1));
        Assert.assertEquals(3, _460Test.lfuCache.get(3));
        Assert.assertEquals(4, _460Test.lfuCache.get(4));
    }
}

