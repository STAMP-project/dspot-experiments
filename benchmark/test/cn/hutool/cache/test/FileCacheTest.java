package cn.hutool.cache.test;


import cn.hutool.cache.file.LFUFileCache;
import org.junit.Assert;
import org.junit.Test;


/**
 * ????????
 *
 * @author looly
 */
public class FileCacheTest {
    @Test
    public void lfuFileCacheTest() {
        LFUFileCache cache = new LFUFileCache(1000, 500, 2000);
        Assert.assertNotNull(cache);
    }
}

