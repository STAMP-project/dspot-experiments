package cn.myperf4j.base.test;


import cn.myperf4j.base.config.ProfilingFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/28
 */
public class ProfilingFilterTest {
    @Test
    public void test() {
        Assert.assertFalse(ProfilingFilter.isNeedInject("org.junit.Before"));
        Assert.assertTrue(ProfilingFilter.isNeedInject("org/junit/Before"));
        Assert.assertTrue(ProfilingFilter.isNotNeedInject("org/junit/rules/ErrorCollector"));
        Assert.assertTrue(ProfilingFilter.isNotNeedInjectMethod("toString"));
        Assert.assertTrue(ProfilingFilter.isNotNeedInjectMethod("hello"));
        Assert.assertFalse(ProfilingFilter.isNotNeedInjectMethod("assertFalse"));
        Assert.assertTrue(ProfilingFilter.isNotNeedInjectClassLoader("org.apache.catalina.loader.WebappClassLoader"));
        Assert.assertFalse(ProfilingFilter.isNotNeedInjectClassLoader("org.springframework.boot.loader.LaunchedURLClassLoader"));
    }

    @Test
    public void testWildcardMatch() {
        Assert.assertFalse(ProfilingFilter.isNeedInject("cn/junit/test/a"));
        Assert.assertFalse(ProfilingFilter.isNeedInject("cn/junit/test2"));
        ProfilingFilter.addIncludePackage("cn.junit.test.*");
        Assert.assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/a"));
        Assert.assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/2"));
        Assert.assertFalse(ProfilingFilter.isNotNeedInject("com/junit/test/a"));
        Assert.assertFalse(ProfilingFilter.isNotNeedInject("com/junit/test2"));
        ProfilingFilter.addExcludePackage("com.junit.test.*");
        Assert.assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/a"));
        Assert.assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/2"));
    }
}

