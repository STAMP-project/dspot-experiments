/**
 * Created on  13-09-23 09:29
 */
package com.alicp.jetcache.anno.method;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.test.support.DynamicQuery;
import com.alicp.jetcache.testsupport.CountClass;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CacheHandlerTest {
    private GlobalCacheConfig globalCacheConfig;

    private CachedAnnoConfig cachedAnnoConfig;

    private CacheInvokeConfig cacheInvokeConfig;

    private CountClass count;

    private Cache cache;

    private ConfigMap configMap;

    // basic test
    @Test
    public void testStaticInvoke1() throws Throwable {
        Method method = CountClass.class.getMethod("count");
        cachedAnnoConfig.setDefineMethod(method);
        int x1;
        int x2;
        int x3;
        method.invoke(count);
        x1 = invokeQuery(method, null);
        x2 = invokeQuery(method, null);
        x3 = invokeQuery(method, null);
        Assertions.assertEquals(x1, x2);
        Assertions.assertEquals(x1, x3);
        method = CountClass.class.getMethod("count", int.class);
        int X1;
        int X2;
        int X3;
        int X4;
        X1 = invokeQuery(method, new Object[]{ 1000 });
        X2 = invokeQuery(method, new Object[]{ 2000 });
        X3 = invokeQuery(method, new Object[]{ 1000 });
        X4 = invokeQuery(method, new Object[]{ 2000 });
        Assertions.assertEquals(X1, X3);
        Assertions.assertEquals(X2, X4);
    }

    // basic test
    @Test
    public void testStaticInvoke2() throws Throwable {
        Method method = CountClass.class.getMethod("count", String.class, int.class);
        cachedAnnoConfig.setDefineMethod(method);
        int x1;
        int x2;
        int x3;
        int x4;
        int x5;
        int x6;
        x1 = invokeQuery(method, new Object[]{ "aaa", 10 });
        x2 = invokeQuery(method, new Object[]{ "bbb", 100 });
        x3 = invokeQuery(method, new Object[]{ "ccc", 10 });
        x4 = invokeQuery(method, new Object[]{ "aaa", 10 });
        x5 = invokeQuery(method, new Object[]{ "bbb", 100 });
        x6 = invokeQuery(method, new Object[]{ "ccc", 10 });
        Assertions.assertEquals(x1, x4);
        Assertions.assertEquals(x2, x5);
        Assertions.assertEquals(x3, x6);
    }

    // basic test
    @Test
    public void testStaticInvoke3() throws Throwable {
        DynamicQuery q1 = new DynamicQuery();
        DynamicQuery q2 = new DynamicQuery();
        q2.setId(1000);
        DynamicQuery q3 = new DynamicQuery();
        q3.setId(1000);
        q3.setName("N1");
        DynamicQuery q4 = new DynamicQuery();
        q4.setId(1000);
        q4.setName("N2");
        DynamicQuery q5 = new DynamicQuery();
        q5.setId(1000);
        q5.setName("N2");
        q5.setEmail("");
        DynamicQuery q6 = new DynamicQuery();// q6=q4

        q6.setId(1000);
        q6.setName("N2");
        DynamicQuery[] querys = new DynamicQuery[]{ q1, q2, q3, q4, q5, q6 };
        int[] ps = new int[]{ 10, 9000000, 10 };
        for (DynamicQuery Q1 : querys) {
            for (DynamicQuery Q2 : querys) {
                for (int P1 : ps) {
                    for (int P2 : ps) {
                        if ((Q1 == Q2) && (P1 == P2)) {
                            assertResultEquals(Q1, P1, Q2, P2);
                        } else
                            if (((P1 == P2) && ((Q1 == q4) || (Q1 == q6))) && ((Q2 == q4) || (Q2 == q6))) {
                                assertResultEquals(Q1, P1, Q2, P2);
                            } else {
                                assertResultNotEquals(Q1, P1, Q2, P2);
                            }

                    }
                }
            }
        }
    }

    @Test
    public void testStaticInvokeNull() throws Throwable {
        Method method = CountClass.class.getMethod("countNull");
        cachedAnnoConfig.setDefineMethod(method);
        Integer x1;
        Integer x2;
        Integer x3;
        cache.config().setCacheNullValue(false);
        x1 = invokeQuery(method, null);// null, not cached

        x2 = invokeQuery(method, null);// not null, so cached

        x3 = invokeQuery(method, null);// hit cache

        Assertions.assertNull(x1);
        Assertions.assertNotNull(x2);
        Assertions.assertNotNull(x3);
        Assertions.assertEquals(x2, x3);
        setup();
        cache.config().setCacheNullValue(true);
        x1 = invokeQuery(method, null);// null,cached

        x2 = invokeQuery(method, null);
        x3 = invokeQuery(method, null);
        Assertions.assertNull(x1);
        Assertions.assertNull(x2);
        Assertions.assertNull(x3);
    }

    @Test
    public void testStaticInvokeCondition() throws Throwable {
        Method method = CountClass.class.getMethod("count", int.class);
        cachedAnnoConfig.setDefineMethod(method);
        int x1;
        int x2;
        cachedAnnoConfig.setCondition("mvel{args[0]>10}");
        x1 = invokeQuery(method, new Object[]{ 10 });
        x2 = invokeQuery(method, new Object[]{ 10 });
        Assertions.assertNotEquals(x1, x2);
        x1 = invokeQuery(method, new Object[]{ 11 });
        x2 = invokeQuery(method, new Object[]{ 11 });
        Assertions.assertEquals(x1, x2);
    }

    @Test
    public void testStaticInvokePostCondition() throws Throwable {
        Method method = CountClass.class.getMethod("count");
        cachedAnnoConfig.setDefineMethod(method);
        int x1;
        int x2;
        int x3;
        cachedAnnoConfig.setPostCondition("mvel{result%2==1}");
        cacheInvokeConfig.getCachedAnnoConfig().setPostConditionEvaluator(null);
        x1 = invokeQuery(method, null);// return 0, postCondition=false, so not cached

        x2 = invokeQuery(method, null);// return 1, postCondition=true, so cached

        x3 = invokeQuery(method, null);// cache hit

        Assertions.assertNotEquals(x1, x2);
        Assertions.assertEquals(x2, x3);
    }

    // test enableCache
    @Test
    public void testStaticInvoke_CacheContext() throws Throwable {
        final Method method = CountClass.class.getMethod("count");
        int x1;
        int x2;
        int x3;
        Invoker invoker = () -> method.invoke(count);
        CacheInvokeContext context = createCachedInvokeContext(invoker, method, null);
        cachedAnnoConfig.setEnabled(false);
        x1 = ((Integer) (CacheHandler.invoke(context)));
        context = createCachedInvokeContext(invoker, method, null);
        context.getCacheInvokeConfig().setCachedAnnoConfig(null);
        x2 = ((Integer) (CacheHandler.invoke(context)));
        context = createCachedInvokeContext(invoker, method, null);
        context.getCacheInvokeConfig().setCachedAnnoConfig(null);
        x3 = ((Integer) (CacheHandler.invoke(context)));
        Assertions.assertTrue((((x1 != x2) && (x1 != x3)) && (x2 != x3)));
        cachedAnnoConfig.setEnabled(false);
        x1 = invokeQuery(method, null);
        x2 = invokeQuery(method, null);
        x3 = invokeQuery(method, null);
        Assertions.assertTrue((((x1 != x2) && (x1 != x3)) && (x2 != x3)));
        cachedAnnoConfig.setEnabled(false);
        CacheContext.enableCache(() -> {
            try {
                int xx1 = invokeQuery(method, null);
                int xx2 = invokeQuery(method, null);
                int xx3 = invokeQuery(method, null);
                assertEquals(xx1, xx2);
                assertEquals(xx1, xx3);
            } catch ( e) {
                fail(e);
            }
            return null;
        });
        cachedAnnoConfig.setEnabled(false);
        cacheInvokeConfig.setEnableCacheContext(true);
        x1 = invokeQuery(method, null);
        x2 = invokeQuery(method, null);
        x3 = invokeQuery(method, null);
        Assertions.assertEquals(x1, x2);
        Assertions.assertEquals(x1, x3);
    }

    @Test
    public void testInstanceInvoke() throws Throwable {
        Method method = CountClass.class.getMethod("count");
        cachedAnnoConfig.setDefineMethod(method);
        final CacheInvokeConfig cac = new CacheInvokeConfig();
        cac.setCachedAnnoConfig(cachedAnnoConfig);
        ConfigMap configMap = new ConfigMap() {
            @Override
            public CacheInvokeConfig getByMethodInfo(String key) {
                return cac;
            }
        };
        Invoker invoker = () -> method.invoke(count);
        CacheHandler ch = new CacheHandler(count, configMap, () -> createCachedInvokeContext(invoker, method, null), null);
        int x1 = ((Integer) (ch.invoke(null, method, null)));
        int x2 = ((Integer) (ch.invoke(null, method, null)));
        Assertions.assertEquals(x1, x2);
        cachedAnnoConfig.setEnabled(false);
        x1 = ((Integer) (ch.invoke(null, method, null)));
        x2 = ((Integer) (ch.invoke(null, method, null)));
        Assertions.assertNotEquals(x1, x2);
        cac.setEnableCacheContext(true);
        x1 = ((Integer) (ch.invoke(null, method, null)));
        x2 = ((Integer) (ch.invoke(null, method, null)));
        Assertions.assertEquals(x1, x2);
    }
}

