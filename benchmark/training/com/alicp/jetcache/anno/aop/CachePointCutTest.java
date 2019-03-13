/**
 * Created on  13-09-22 18:46
 */
package com.alicp.jetcache.anno.aop;


import CacheType.BOTH;
import com.alicp.jetcache.anno.method.CacheInvokeConfig;
import com.alicp.jetcache.anno.support.CachedAnnoConfig;
import com.alicp.jetcache.anno.support.ConfigMap;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.junit.Assert;
import org.junit.Test;
import otherpackage.OtherService;

import static CacheType.BOTH;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CachePointCutTest {
    private CachePointcut pc;

    private ConfigMap map;

    interface I1 {
        @Cached
        int foo();
    }

    class C1 implements CachePointCutTest.I1 {
        public int foo() {
            return 0;
        }
    }

    @Test
    public void testMatches1() throws Exception {
        Assert.assertTrue(pc.matches(CachePointCutTest.C1.class));
        Assert.assertTrue(pc.matches(CachePointCutTest.I1.class));
        Method m1 = CachePointCutTest.I1.class.getMethod("foo");
        Method m2 = CachePointCutTest.C1.class.getMethod("foo");
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.C1.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.C1.class));
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.I1.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.I1.class));
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C1.class)).isEnableCacheContext());
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I1.class)).isEnableCacheContext());
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C1.class)).isEnableCacheContext());
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I1.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C1.class)).getCachedAnnoConfig());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I1.class)).getCachedAnnoConfig());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C1.class)).getCachedAnnoConfig());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I1.class)).getCachedAnnoConfig());
        Object o1 = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ CachePointCutTest.I1.class }, ( proxy, method, args) -> null);
        Assert.assertTrue(pc.matches(m1, o1.getClass()));
        Assert.assertTrue(pc.matches(m2, o1.getClass()));
        Assert.assertTrue(pc.matches(o1.getClass().getMethod("foo"), o1.getClass()));
    }

    interface I2 {
        int foo();
    }

    class C2 implements CachePointCutTest.I2 {
        @Cached
        public int foo() {
            return 0;
        }
    }

    @Test
    public void testMatches2() throws Exception {
        Method m1 = CachePointCutTest.I2.class.getMethod("foo");
        Method m2 = CachePointCutTest.C2.class.getMethod("foo");
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.C2.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.C2.class));
        Assert.assertFalse(pc.matches(m1, CachePointCutTest.I2.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.I2.class));
        Assert.assertSame(CacheInvokeConfig.getNoCacheInvokeConfigInstance(), map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I2.class)));
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C2.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C2.class)).getCachedAnnoConfig());
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I2.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I2.class)).getCachedAnnoConfig());
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C2.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C2.class)).getCachedAnnoConfig());
        Object o1 = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ CachePointCutTest.I1.class }, ( proxy, method, args) -> null);
        Assert.assertTrue(pc.matches(m1, o1.getClass()));
        Assert.assertTrue(pc.matches(m2, o1.getClass()));
        Assert.assertTrue(pc.matches(o1.getClass().getMethod("foo"), o1.getClass()));
    }

    interface I3_Parent {
        @EnableCache
        @Cached(enabled = false, area = "A1", expire = 1, cacheType = BOTH, localLimit = 2)
        int foo();
    }

    interface I3 extends CachePointCutTest.I3_Parent {
        int foo();
    }

    class C3 implements CachePointCutTest.I3 {
        public int foo() {
            return 0;
        }
    }

    @Test
    public void testMatches3() throws Exception {
        Method m1 = CachePointCutTest.I3_Parent.class.getMethod("foo");
        Method m2 = CachePointCutTest.I3.class.getMethod("foo");
        Method m3 = CachePointCutTest.C3.class.getMethod("foo");
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.C3.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.C3.class));
        Assert.assertTrue(pc.matches(m3, CachePointCutTest.C3.class));
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.I3.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.I3.class));
        Assert.assertTrue(pc.matches(m3, CachePointCutTest.I3.class));
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I3.class)).isEnableCacheContext());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C3.class)).isEnableCacheContext());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I3.class)).isEnableCacheContext());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C3.class)).isEnableCacheContext());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m3, CachePointCutTest.I3.class)).isEnableCacheContext());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m3, CachePointCutTest.C3.class)).isEnableCacheContext());
        CachedAnnoConfig cac = map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I3.class)).getCachedAnnoConfig();
        Assert.assertEquals("A1", cac.getArea());
        Assert.assertEquals(false, cac.isEnabled());
        Assert.assertEquals(1, cac.getExpire());
        Assert.assertEquals(BOTH, cac.getCacheType());
        Assert.assertEquals(2, cac.getLocalLimit());
    }

    interface I4 {
        @Cached(enabled = false)
        int foo();
    }

    interface I4_Sub extends CachePointCutTest.I4 {}

    class C4 implements CachePointCutTest.I4_Sub {
        @EnableCache
        public int foo() {
            return 0;
        }
    }

    @Test
    public void testMatches4() throws Exception {
        Method m1 = CachePointCutTest.I4.class.getMethod("foo");
        Method m2 = CachePointCutTest.C4.class.getMethod("foo");
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.C4.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.C4.class));
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.I4.class));
        Assert.assertTrue(pc.matches(m2, CachePointCutTest.I4.class));
        Assert.assertFalse(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I4.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.I4.class)).getCachedAnnoConfig());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C4.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m1, CachePointCutTest.C4.class)).getCachedAnnoConfig());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I4.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.I4.class)).getCachedAnnoConfig());
        Assert.assertTrue(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C4.class)).isEnableCacheContext());
        Assert.assertNotNull(map.getByMethodInfo(CachePointcut.getKey(m2, CachePointCutTest.C4.class)).getCachedAnnoConfig());
    }

    class C5 implements OtherService {
        public int bar() {
            return 0;
        }

        @CacheInvalidate(name = "c1", key = "k1")
        public int bar2() {
            return 0;
        }
    }

    @Test
    public void testMatches5() throws Exception {
        Assert.assertTrue(pc.matches(CachePointCutTest.C5.class));
        Method m3 = OtherService.class.getMethod("bar");
        Method m4 = CachePointCutTest.C5.class.getMethod("bar");
        Assert.assertFalse(pc.matches(m3, OtherService.class));
        Assert.assertFalse(pc.matches(m4, OtherService.class));
        Assert.assertFalse(pc.matches(m3, CachePointCutTest.C5.class));
        Assert.assertFalse(pc.matches(m4, CachePointCutTest.C5.class));
        Assert.assertTrue(pc.matches(CachePointCutTest.C5.class.getMethod("bar2"), CachePointCutTest.C5.class));
    }

    interface I6 {
        int foo();
    }

    class C6_1 implements CachePointCutTest.I6 {
        public int foo() {
            return 0;
        }
    }

    class C6_2 implements CachePointCutTest.I6 {
        @CacheUpdate(name = "c1", key = "k1", value = "v1")
        public int foo() {
            return 0;
        }
    }

    @Test
    public void testMatches6() throws Exception {
        Method m1 = CachePointCutTest.I6.class.getMethod("foo");
        Method m2 = CachePointCutTest.C6_1.class.getMethod("foo");
        Method m3 = CachePointCutTest.C6_2.class.getMethod("foo");
        Assert.assertFalse(pc.matches(m1, CachePointCutTest.I6.class));
        Assert.assertFalse(pc.matches(m1, CachePointCutTest.C6_1.class));
        Assert.assertTrue(pc.matches(m1, CachePointCutTest.C6_2.class));
        Assert.assertFalse(pc.matches(m2, CachePointCutTest.I6.class));
        Assert.assertFalse(pc.matches(m2, CachePointCutTest.C6_1.class));
        Assert.assertTrue(pc.matches(m3, CachePointCutTest.I6.class));
        Assert.assertTrue(pc.matches(m3, CachePointCutTest.C6_2.class));
    }
}

