package cn.hutool.cache.test;


import DateUnit.SECOND;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.thread.ThreadUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author Looly
 */
public class CacheTest {
    @Test
    public void fifoCacheTest() {
        Cache<String, String> fifoCache = CacheUtil.newFIFOCache(3);
        fifoCache.put("key1", "value1", ((SECOND.getMillis()) * 3));
        fifoCache.put("key2", "value2", ((SECOND.getMillis()) * 3));
        fifoCache.put("key3", "value3", ((SECOND.getMillis()) * 3));
        fifoCache.put("key4", "value4", ((SECOND.getMillis()) * 3));
        // ????????3???????????????FIFO??????????????
        String value1 = fifoCache.get("key1");
        Assert.assertTrue((null == value1));
    }

    @Test
    public void lfuCacheTest() {
        Cache<String, String> lfuCache = CacheUtil.newLFUCache(3);
        lfuCache.put("key1", "value1", ((SECOND.getMillis()) * 3));
        // ????+1
        lfuCache.get("key1");
        lfuCache.put("key2", "value2", ((SECOND.getMillis()) * 3));
        lfuCache.put("key3", "value3", ((SECOND.getMillis()) * 3));
        lfuCache.put("key4", "value4", ((SECOND.getMillis()) * 3));
        // ????????3???????????????LRU?????????????2,3????
        String value2 = lfuCache.get("key2");
        String value3 = lfuCache.get("key3");
        Assert.assertTrue((null == value2));
        Assert.assertTrue((null == value3));
    }

    @Test
    public void lruCacheTest() {
        Cache<String, String> lruCache = CacheUtil.newLRUCache(3);
        // ?????????
        // LRUCache<String, String> lruCache = new LRUCache<String, String>(3);
        lruCache.put("key1", "value1", ((SECOND.getMillis()) * 3));
        lruCache.put("key2", "value2", ((SECOND.getMillis()) * 3));
        lruCache.put("key3", "value3", ((SECOND.getMillis()) * 3));
        // ??????
        lruCache.get("key1");
        lruCache.put("key4", "value4", ((SECOND.getMillis()) * 3));
        // ????????3???????????????LRU?????????????2????
        String value2 = lruCache.get("key2");
        Assert.assertTrue((null == value2));
    }

    @Test
    public void timedCacheTest() {
        TimedCache<String, String> timedCache = CacheUtil.newTimedCache(4);
        // TimedCache<String, String> timedCache = new TimedCache<String, String>(DateUnit.SECOND.getMillis() * 3);
        timedCache.put("key1", "value1", 1);// 1????

        timedCache.put("key2", "value2", ((SECOND.getMillis()) * 5));// 5???

        timedCache.put("key3", "value3");// ????(4??)

        // ????????5?????????
        timedCache.schedulePrune(5);
        // ??5??
        ThreadUtil.sleep(5);
        // 5?????value2???5?????????value2?????
        String value1 = timedCache.get("key1");
        Assert.assertTrue((null == value1));
        String value2 = timedCache.get("key2");
        Assert.assertFalse((null == value2));
        // 5??????????????key3????4??????null
        String value3 = timedCache.get("key3");
        Assert.assertTrue((null == value3));
        // ??????
        timedCache.cancelPruneSchedule();
    }
}

