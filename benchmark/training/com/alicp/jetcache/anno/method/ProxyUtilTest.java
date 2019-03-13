/**
 * Created on  13-09-23 17:35
 */
package com.alicp.jetcache.anno.method;


import com.alicp.jetcache.anno.support.CacheContext;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static CacheType.BOTH;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class ProxyUtilTest {
    private GlobalCacheConfig globalCacheConfig;

    public interface I1 {
        int count();

        int countWithoutCache();
    }

    public class C1 implements ProxyUtilTest.I1 {
        int count;

        @Cached
        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    // annotation on class
    @Test
    public void testGetProxyByAnnotation1() {
        ProxyUtilTest.I1 c1 = new ProxyUtilTest.C1();
        ProxyUtilTest.I1 c2 = ProxyUtil.getProxyByAnnotation(c1, globalCacheConfig);
        Assertions.assertNotEquals(c1.count(), c1.count());
        Assertions.assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
        Assertions.assertEquals(c2.count(), c2.count());
        Assertions.assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
    }

    public interface I2 {
        @Cached
        int count();

        int countWithoutCache();
    }

    public class C2 implements ProxyUtilTest.I2 {
        int count;

        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    public class C22 implements ProxyUtilTest.I2 {
        int count;

        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    // annotation on intface
    @Test
    public void testGetProxyByAnnotation2() {
        ProxyUtilTest.I2 c1 = new ProxyUtilTest.C2();
        ProxyUtilTest.I2 c2 = ProxyUtil.getProxyByAnnotation(c1, globalCacheConfig);
        Assertions.assertNotEquals(c1.count(), c1.count());
        Assertions.assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
        Assertions.assertEquals(c2.count(), c2.count());
        Assertions.assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
        ProxyUtilTest.I2 c3 = new ProxyUtilTest.C22();
        ProxyUtilTest.I2 c4 = ProxyUtil.getProxyByAnnotation(c3, globalCacheConfig);
        Assertions.assertEquals(c2.count(), c4.count());
    }

    public interface I3_1 {
        @Cached
        int count();
    }

    public interface I3_2 extends ProxyUtilTest.I3_1 {
        int count();

        int countWithoutCache();
    }

    public class C3 implements ProxyUtilTest.I3_2 {
        int count;

        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    // annotation on super interface
    @Test
    public void testGetProxyByAnnotation3() {
        ProxyUtilTest.I3_2 c1 = new ProxyUtilTest.C3();
        ProxyUtilTest.I3_2 c2 = ProxyUtil.getProxyByAnnotation(c1, globalCacheConfig);
        Assertions.assertNotEquals(c1.count(), c1.count());
        Assertions.assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
        Assertions.assertEquals(c2.count(), c2.count());
        Assertions.assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
    }

    public interface I4_1 {
        int count();

        int countWithoutCache();
    }

    public interface I4_2 extends ProxyUtilTest.I4_1 {
        @Cached
        int count();
    }

    public class C4 implements ProxyUtilTest.I4_2 {
        int count;

        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    // with super interface
    @Test
    public void testGetProxyByAnnotation4() {
        ProxyUtilTest.I4_1 c1 = new ProxyUtilTest.C4();
        ProxyUtilTest.I4_1 c2 = ProxyUtil.getProxyByAnnotation(c1, globalCacheConfig);
        Assertions.assertNotEquals(c1.count(), c1.count());
        Assertions.assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
        Assertions.assertEquals(c2.count(), c2.count());
        Assertions.assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
    }

    public interface I5 {
        int count();

        int countWithoutCache();
    }

    public class C5 implements ProxyUtilTest.I5 {
        int count;

        @Cached(enabled = false)
        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    // enabled=false
    @Test
    public void testGetProxyByAnnotation5() {
        ProxyUtilTest.I5 c1 = new ProxyUtilTest.C5();
        ProxyUtilTest.I5 c2 = ProxyUtil.getProxyByAnnotation(c1, globalCacheConfig);
        Assertions.assertNotEquals(c1.count(), c1.count());
        Assertions.assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
        Assertions.assertNotEquals(c2.count(), c2.count());
        Assertions.assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
        CacheContext.enableCache(() -> {
            assertNotEquals(c1.count(), c1.count());
            assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
            assertEquals(c2.count(), c2.count());
            assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
            return null;
        });
    }

    public interface I6 {
        int count();

        int countWithoutCache();
    }

    public class C6 implements ProxyUtilTest.I6 {
        int count;

        @EnableCache
        @Cached(enabled = false)
        public int count() {
            return (count)++;
        }

        public int countWithoutCache() {
            return (count)++;
        }
    }

    // enabled=false+EnableCache
    @Test
    public void testGetProxyByAnnotation6() {
        ProxyUtilTest.I6 c1 = new ProxyUtilTest.C6();
        ProxyUtilTest.I6 c2 = ProxyUtil.getProxyByAnnotation(c1, globalCacheConfig);
        Assertions.assertNotEquals(c1.count(), c1.count());
        Assertions.assertNotEquals(c1.countWithoutCache(), c1.countWithoutCache());
        Assertions.assertEquals(c2.count(), c2.count());
        Assertions.assertNotEquals(c2.countWithoutCache(), c2.countWithoutCache());
    }

    public interface I7_1 {
        int count();

        int countWithoutCache();
    }

    public class C7_1 implements ProxyUtilTest.I7_1 {
        int count;

        @Cached(enabled = false)
        public int count() {
            return (count)++;
        }

        @Override
        public int countWithoutCache() {
            return (count)++;
        }
    }

    public interface I7_2 {
        int count();

        int countWithoutCache();
    }

    public class C7_2 implements ProxyUtilTest.I7_2 {
        ProxyUtilTest.I7_1 service;

        @EnableCache
        public int count() {
            return service.count();
        }

        @EnableCache
        public int countWithoutCache() {
            return service.countWithoutCache();
        }
    }

    // enabled=false+EnableCache?enable in caller?
    @Test
    public void testGetProxyByAnnotation7() {
        ProxyUtilTest.I7_1 c1_1 = new ProxyUtilTest.C7_1();
        ProxyUtilTest.I7_1 c1_2 = ProxyUtil.getProxyByAnnotation(c1_1, globalCacheConfig);
        ProxyUtilTest.C7_2 c2_1 = new ProxyUtilTest.C7_2();
        c2_1.service = c1_2;
        ProxyUtilTest.I7_2 c2_2 = ProxyUtil.getProxyByAnnotation(c2_1, globalCacheConfig);
        Assertions.assertNotEquals(c2_1.count(), c2_1.count());
        Assertions.assertNotEquals(c2_2.countWithoutCache(), c2_2.countWithoutCache());
        Assertions.assertEquals(c2_2.count(), c2_2.count());
    }

    public interface I8 {
        @Cached(name = "c1", key = "args[0]")
        int count(String id);

        @CacheUpdate(name = "c1", key = "#id", value = "args[1]")
        void update(String id, int value);

        @CacheUpdate(name = "c2", key = "args[0]", value = "args[1]")
        void update2(String id, int value);

        @CacheInvalidate(name = "c1", key = "#id")
        void delete(String id);

        @CacheInvalidate(name = "c2", key = "args[0]")
        void delete2(String id);

        @CacheUpdate(name = "c1", key = "#id", value = "#result")
        int randomUpdate(String id);

        @CacheUpdate(name = "c1", key = "#id", value = "result")
        int randomUpdate2(String id);
    }

    public class C8 implements ProxyUtilTest.I8 {
        int count;

        Map<String, Integer> m = new HashMap<>();

        @Override
        public int count(String id) {
            Integer v = m.get(id);
            if (v == null) {
                v = (count)++;
            }
            v++;
            m.put(id, v);
            return v;
        }

        @Override
        public void update(String theId, int value) {
            m.put(theId, value);
        }

        @Override
        public void delete(String theId) {
            m.remove(theId);
        }

        @Override
        public void update2(String theId, int value) {
            m.put(theId, value);
        }

        @Override
        public void delete2(String theId) {
            m.remove(theId);
        }

        @Override
        public int randomUpdate(String id) {
            return new Random().nextInt();
        }

        @Override
        public int randomUpdate2(String id) {
            return new Random().nextInt();
        }
    }

    // @CacheUpdate and @CacheInvalidate test
    @Test
    public void testGetProxyByAnnotation8() {
        ProxyUtilTest.I8 i8 = new ProxyUtilTest.C8();
        ProxyUtilTest.I8 i8_proxy = ProxyUtil.getProxyByAnnotation(i8, globalCacheConfig);
        int v1 = i8_proxy.count("K1");
        Assertions.assertEquals(v1, i8_proxy.count("K1"));
        i8_proxy.delete("K1");
        int v2 = i8_proxy.count("K1");
        Assertions.assertNotEquals(v1, v2);
        i8_proxy.delete2("K1");
        Assertions.assertEquals(v2, i8_proxy.count("K1"));
        i8_proxy.update("K1", 200);
        Assertions.assertEquals(200, i8_proxy.count("K1"));
        i8_proxy.update2("K1", 300);
        Assertions.assertEquals(200, i8_proxy.count("K1"));
        Assertions.assertEquals(i8_proxy.count("K1"), i8_proxy.count("K1"));
        Assertions.assertNotEquals(i8_proxy.count("K1"), i8_proxy.count("K2"));
        Assertions.assertEquals(i8_proxy.randomUpdate("K1"), i8_proxy.count("K1"));
        Assertions.assertEquals(i8_proxy.randomUpdate2("K1"), i8_proxy.count("K1"));
    }

    public interface I9 {
        @Cached
        @CacheRefresh(refresh = 100, timeUnit = TimeUnit.MILLISECONDS)
        int count();

        @Cached(key = "#a", cacheType = BOTH)
        @CacheRefresh(refresh = 100, timeUnit = TimeUnit.MILLISECONDS)
        int count(int a, int b);
    }

    public class C9 implements ProxyUtilTest.I9 {
        int count1;

        int count2;

        public int count() {
            return (count1)++;
        }

        @Override
        public int count(int a, int b) {
            return (a + b) + ((count2)++);
        }
    }

    // refresh test
    @Test
    public void testGetProxyByAnnotation9() throws Exception {
        ProxyUtilTest.I9 beanProxy = ProxyUtil.getProxyByAnnotation(new ProxyUtilTest.C9(), globalCacheConfig);
        {
            int x1 = beanProxy.count();
            int x2 = beanProxy.count();
            Assertions.assertEquals(x1, x2);
            int i = 0;
            while (true) {
                // auto refreshment may take some time to init
                Assertions.assertTrue((i < 10));
                Thread.sleep(150);
                if (x2 == (beanProxy.count())) {
                    i++;
                    continue;
                } else {
                    break;
                }
            } 
        }
        {
            int x1 = beanProxy.count(1, 2);
            int x2 = beanProxy.count(1, 200);
            Assertions.assertEquals(x1, x2);
            Thread.sleep(150);
            Assertions.assertEquals((x1 + 1), beanProxy.count(1, 400));
        }
    }

    public interface I10 {
        @Cached
        int count1(int p);

        @Cached
        @CachePenetrationProtect
        int count2(int p);
    }

    public class C10 implements ProxyUtilTest.I10 {
        AtomicInteger count1 = new AtomicInteger(0);

        AtomicInteger count2 = new AtomicInteger(0);

        @Override
        public int count1(int p) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return count1.incrementAndGet();
        }

        @Override
        public int count2(int p) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return count2.incrementAndGet();
        }
    }

    // protect test
    @Test
    public void testGetProxyByAnnotation10() throws Exception {
        ProxyUtilTest.I10 beanProxy = ProxyUtil.getProxyByAnnotation(new ProxyUtilTest.C10(), globalCacheConfig);
        // preheat
        beanProxy.count1(1);
        beanProxy.count2(1);
        {
            int[] x = new int[1];
            int[] y = new int[1];
            CountDownLatch countDownLatch = new CountDownLatch(2);
            new Thread(() -> {
                x[0] = beanProxy.count1(2);
                countDownLatch.countDown();
            }).start();
            new Thread(() -> {
                y[0] = beanProxy.count1(2);
                countDownLatch.countDown();
            }).start();
            countDownLatch.await();
            Assertions.assertNotEquals(x[0], y[0]);
        }
        {
            int[] x = new int[1];
            int[] y = new int[1];
            CountDownLatch countDownLatch = new CountDownLatch(2);
            new Thread(() -> {
                x[0] = beanProxy.count2(2);
                countDownLatch.countDown();
            }).start();
            new Thread(() -> {
                y[0] = beanProxy.count2(2);
                countDownLatch.countDown();
            }).start();
            countDownLatch.await();
            Assertions.assertEquals(x[0], y[0]);
        }
    }
}

