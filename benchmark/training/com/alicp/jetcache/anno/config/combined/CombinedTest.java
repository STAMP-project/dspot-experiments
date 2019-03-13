package com.alicp.jetcache.anno.config.combined;


import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.alicp.jetcache.test.anno.TestUtil;
import com.alicp.jetcache.test.spring.SpringTest;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created on 2017/2/14.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CombinedTest extends SpringTest {
    @Test
    public void test() throws Exception {
        // context = new ClassPathXmlApplicationContext("combined/combined.xml", "combined/combined-aop1.xml", "combined/combined-aop2.xml");
        // testImpl();
        context = new ClassPathXmlApplicationContext("combined/combined.xml", "combined/combined-aop1.xml", "combined/combined-aop3.xml");
        testImpl();
    }

    @Configuration
    @EnableMethodCache(basePackages = { "com.alicp.jetcache.test.beans", "com.alicp.jetcache.anno.config.combined" })
    public static class A {
        @Bean
        public SpringConfigProvider springConfigProvider() {
            return new SpringConfigProvider();
        }

        @Bean
        public GlobalCacheConfig config(SpringConfigProvider configProvider) {
            GlobalCacheConfig pc = TestUtil.createGloableConfig(configProvider);
            return pc;
        }
    }
}

