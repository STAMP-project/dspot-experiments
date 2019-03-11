/**
 * Created on 2018/5/10.
 */
package com.alicp.jetcache.redis;


import CacheResultCode.FAIL;
import com.alicp.jetcache.Cache;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class RedisCacheFailTest {
    private Cache cache;

    private Jedis jedis;

    @Test
    public void test_GET() {
        Mockito.when(jedis.get(((byte[]) (ArgumentMatchers.any())))).thenThrow(new JedisConnectionException("err"));
        CacheGetResult cr = cache.GET("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertNull(cr.getValue());
    }

    @Test
    public void test_GET_ALL() {
        Mockito.when(jedis.mget(((byte[][]) (ArgumentMatchers.any())))).thenThrow(new JedisDataException("err"));
        HashSet s = new HashSet();
        s.add("K");
        MultiGetResult cr = cache.GET_ALL(s);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertNull(cr.getValues());
    }

    @Test
    public void test_PUT() {
        Mockito.when(jedis.psetex(((byte[]) (ArgumentMatchers.any())), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenThrow(new JedisConnectionException("err")).thenReturn("XXX");
        CacheResult cr = cache.PUT("K", "V");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.PUT("K", "V");
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertEquals("XXX", cr.getMessage());
    }

    @Test
    public void test_PUT_ALL() {
        Mockito.when(jedis.pipelined()).thenThrow(new JedisConnectionException("err"));
        Map m = new HashMap();
        m.put("K", "V");
        CacheResult cr = cache.PUT_ALL(m);
        Assertions.assertEquals(FAIL, cr.getResultCode());
    }

    @Test
    public void test_REMOVE() {
        Mockito.when(jedis.del(((byte[]) (ArgumentMatchers.any())))).thenThrow(new JedisConnectionException("err"));
        CacheResult cr = cache.REMOVE("K");
        Assertions.assertEquals(FAIL, cr.getResultCode());
    }

    @Test
    public void test_REMOVE_ALL() {
        Mockito.when(jedis.del(((byte[][]) (ArgumentMatchers.any())))).thenThrow(new JedisConnectionException("err"));
        HashSet s = new HashSet();
        s.add("K");
        CacheResult cr = cache.REMOVE_ALL(s);
        Assertions.assertEquals(FAIL, cr.getResultCode());
    }

    @Test
    public void test_PUT_IF_ABSENT() {
        Mockito.when(jedis.set(((byte[]) (ArgumentMatchers.any())), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenThrow(new JedisConnectionException("err")).thenReturn("XXX");
        CacheResult cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        Assertions.assertEquals(FAIL, cr.getResultCode());
        Assertions.assertEquals("XXX", cr.getMessage());
    }
}

