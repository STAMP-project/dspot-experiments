/**
 * Created on 2018/1/23.
 */
package com.alicp.jetcache.anno.method;


import com.alicp.jetcache.CacheConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CacheConfigUtilTest {
    interface I {
        @Cached
        void m1();

        @Cached
        @CacheRefresh(refresh = 100)
        void m1_2();

        @EnableCache
        void m2();

        @CacheInvalidate(name = "foo")
        void m3();

        @CacheUpdate(name = "foo", value = "bar")
        void m4();

        @Cached
        @CacheInvalidate(name = "foo")
        void m5();

        @Cached
        @CacheUpdate(name = "foo", value = "bar")
        void m6();

        @CacheInvalidate(name = "foo")
        @CacheUpdate(name = "foo", value = "bar")
        void m7();
    }

    @Test
    public void test() throws Exception {
        CacheInvokeConfig cic = new CacheInvokeConfig();
        CacheConfigUtil.parse(cic, CacheConfigUtilTest.I.class.getMethod("m1"));
        Assertions.assertNotNull(cic.getCachedAnnoConfig());
        Assertions.assertNull(cic.getCachedAnnoConfig().getRefreshPolicy());
        cic = new CacheInvokeConfig();
        CacheConfigUtil.parse(cic, CacheConfigUtilTest.I.class.getMethod("m1_2"));
        Assertions.assertNotNull(cic.getCachedAnnoConfig());
        Assertions.assertNotNull(cic.getCachedAnnoConfig().getRefreshPolicy());
        cic = new CacheInvokeConfig();
        CacheConfigUtil.parse(cic, CacheConfigUtilTest.I.class.getMethod("m2"));
        Assertions.assertTrue(cic.isEnableCacheContext());
        cic = new CacheInvokeConfig();
        CacheConfigUtil.parse(cic, CacheConfigUtilTest.I.class.getMethod("m3"));
        Assertions.assertNotNull(cic.getInvalidateAnnoConfig());
        cic = new CacheInvokeConfig();
        CacheConfigUtil.parse(cic, CacheConfigUtilTest.I.class.getMethod("m4"));
        Assertions.assertNotNull(cic.getUpdateAnnoConfig());
        CacheInvokeConfig cic2 = new CacheInvokeConfig();
        Assertions.assertThrows(CacheConfigException.class, () -> CacheConfigUtil.parse(cic2, CacheConfigUtilTest.I.class.getMethod("m5")));
        Assertions.assertThrows(CacheConfigException.class, () -> CacheConfigUtil.parse(cic2, CacheConfigUtilTest.I.class.getMethod("m6")));
        cic = new CacheInvokeConfig();
        CacheConfigUtil.parse(cic, CacheConfigUtilTest.I.class.getMethod("m7"));
        Assertions.assertNotNull(cic.getInvalidateAnnoConfig());
        Assertions.assertNotNull(cic.getUpdateAnnoConfig());
    }
}

