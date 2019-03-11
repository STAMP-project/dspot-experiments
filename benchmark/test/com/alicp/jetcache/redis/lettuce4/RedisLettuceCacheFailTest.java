/**
 * Created on 2018/5/10.
 */
package com.alicp.jetcache.redis.lettuce4;


import CacheResultCode.FAIL;
import com.alicp.jetcache.Cache;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class RedisLettuceCacheFailTest {
    private RedisClient client;

    private RedisAsyncCommands asyncCommands;

    private Cache cache;

    @Test
    public void test_GET() {
        Mockito.when(asyncCommands.get(ArgumentMatchers.any())).thenThrow(new RuntimeException("err")).thenReturn(mockFuture(null, new RuntimeException()));
        CacheGetResult cr = cache.GET("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertNull(cr.getValue());
        cr = cache.GET("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertNull(cr.getValue());
    }

    @Test
    public void test_GET_ALL() {
        Mockito.when(asyncCommands.mget(ArgumentMatchers.any())).thenThrow(new RuntimeException("err")).thenReturn(mockFuture(null, new RuntimeException()));
        HashSet s = new HashSet();
        s.add("K");
        MultiGetResult cr = cache.GET_ALL(s);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertNull(cr.getValues());
        cr = cache.GET_ALL(s);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertNull(cr.getValues());
    }

    @Test
    public void test_PUT() {
        Mockito.when(asyncCommands.psetex(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenThrow(new RuntimeException("err")).thenReturn(mockFuture(null, new RuntimeException())).thenReturn(mockFuture("XXX", null));
        CacheResult cr = cache.PUT("K", "V");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.PUT("K", "V");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.PUT("K", "V");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertEquals("XXX", cr.getMessage());
    }

    @Test
    public void test_PUT_ALL() {
        Mockito.when(asyncCommands.psetex(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenThrow(new RuntimeException("err"));
        Map m = new HashMap();
        m.put("K", "V");
        CacheResult cr = cache.PUT_ALL(m);
        Assertions.assertEquals(FAIL, cr.getResultCode());
    }

    @Test
    public void test_REMOVE() {
        Mockito.when(asyncCommands.del(((byte[]) (ArgumentMatchers.any())))).thenThrow(new RuntimeException("err")).thenReturn(mockFuture(null, new RuntimeException())).thenReturn(mockFuture(null, null)).thenReturn(mockFuture(1000L, null));
        CacheResult cr = cache.REMOVE("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.REMOVE("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.REMOVE("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.REMOVE("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
    }

    @Test
    public void test_REMOVE_ALL() {
        Mockito.when(asyncCommands.del(ArgumentMatchers.any())).thenThrow(new RuntimeException("err")).thenReturn(mockFuture(null, new RuntimeException()));
        HashSet s = new HashSet();
        s.add("K");
        CacheResult cr = cache.REMOVE_ALL(s);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.REMOVE_ALL(s);
        Assertions.assertEquals(FAIL, cr.getResultCode());
    }

    @Test
    public void test_PUT_IF_ABSENT() {
        Mockito.when(asyncCommands.set(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RuntimeException("err")).thenReturn(mockFuture(null, new RuntimeException())).thenReturn(mockFuture("XXX", null));
        CacheResult cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertEquals("XXX", cr.getMessage());
    }
}

