/**
 * Created on 2018/5/12.
 */
package com.alicp.jetcache.anno.method;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.support.CacheInvalidateAnnoConfig;
import com.alicp.jetcache.anno.support.ConfigMap;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import com.alicp.jetcache.testsupport.CountClass;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CacheHandlerInvalidateTest {
    private GlobalCacheConfig globalCacheConfig;

    private CacheInvokeConfig cacheInvokeConfig;

    private CountClass count;

    private Cache cache;

    private ConfigMap configMap;

    private CacheInvalidateAnnoConfig invalidateAnnoConfig;

    private CacheInvokeContext cacheInvokeContext;

    @Test
    public void testInvalidate() throws Throwable {
        cache.put("KEY", "V");
        CacheHandler.invoke(cacheInvokeContext);
        Assertions.assertNull(cache.get("KEY"));
    }

    @Test
    public void testConditionTrue() throws Throwable {
        cache.put("KEY", "V");
        invalidateAnnoConfig.setCondition("args[1]==1000");
        CacheHandler.invoke(cacheInvokeContext);
        Assertions.assertNull(cache.get("KEY"));
    }

    @Test
    public void testConditionFalse() throws Throwable {
        cache.put("KEY", "V");
        invalidateAnnoConfig.setCondition("args[1]!=1000");
        CacheHandler.invoke(cacheInvokeContext);
        Assertions.assertNotNull(cache.get("KEY"));
    }

    @Test
    public void testBadCondition() throws Throwable {
        cache.put("KEY", "V");
        invalidateAnnoConfig.setCondition("bad condition");
        CacheHandler.invoke(cacheInvokeContext);
        Assertions.assertNotNull(cache.get("KEY"));
    }

    @Test
    public void testBadKey() throws Throwable {
        cache.put("KEY", "V");
        invalidateAnnoConfig.setKey("bad key script");
        CacheHandler.invoke(cacheInvokeContext);
        Assertions.assertNotNull(cache.get("KEY"));
    }

    static class TestMulti {
        public void update(String keys) {
        }

        public void update(String[] keys) {
        }
    }

    @Test
    public void testMulti() throws Throwable {
        {
            Method method = CacheHandlerInvalidateTest.TestMulti.class.getMethod("update", String[].class);
            invalidateAnnoConfig.setDefineMethod(method);
            invalidateAnnoConfig.setKey("args[0]");
            cacheInvokeContext.setMethod(method);
            cacheInvokeContext.setArgs(new Object[]{ new String[]{ "K1", "K2" } });
            cacheInvokeContext.setInvoker(() -> method.invoke(new com.alicp.jetcache.anno.method.TestMulti(), cacheInvokeContext.getArgs()));
            cache.put("K1", "V1");
            cache.put("K2", "V1");
            CacheHandler.invoke(cacheInvokeContext);
            Assertions.assertNotNull(cache.get("K1"));
            Assertions.assertNotNull(cache.get("K2"));
            invalidateAnnoConfig.setMulti(true);
            cacheInvokeContext.setArgs(new Object[]{ null });
            CacheHandler.invoke(cacheInvokeContext);
            Assertions.assertNotNull(cache.get("K1"));
            Assertions.assertNotNull(cache.get("K2"));
            cacheInvokeContext.setArgs(new Object[]{ new String[]{ "K1", "K2" } });
            CacheHandler.invoke(cacheInvokeContext);
            Assertions.assertNull(cache.get("K1"));
            Assertions.assertNull(cache.get("K2"));
        }
        {
            cache.put("K1", "V1");
            Method method = CacheHandlerInvalidateTest.TestMulti.class.getMethod("update", String.class);
            invalidateAnnoConfig.setDefineMethod(method);
            invalidateAnnoConfig.setKey("args[0]");
            cacheInvokeContext.setMethod(method);
            cacheInvokeContext.setArgs(new Object[]{ "K1" });
            cacheInvokeContext.setInvoker(() -> method.invoke(new com.alicp.jetcache.anno.method.TestMulti(), cacheInvokeContext.getArgs()));
            invalidateAnnoConfig.setMulti(true);
            CacheHandler.invoke(cacheInvokeContext);
            Assertions.assertNotNull(cache.get("K1"));
        }
    }
}

