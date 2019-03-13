package com.netflix.hystrix.contrib.javanica.test.common.configuration.fallback;


import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.CommonUtils;
import org.junit.Assert;
import org.junit.Test;


public abstract class BasicFallbackDefaultPropertiesTest extends BasicHystrixTest {
    private BasicFallbackDefaultPropertiesTest.Service service;

    @Test
    public void testFallbackInheritsDefaultGroupKey() {
        service.commandWithFallbackInheritsDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackInheritsDefaultProperties");
        Assert.assertEquals("DefaultGroupKey", fallbackCommand.getCommandGroup().name());
    }

    @Test
    public void testFallbackInheritsDefaultThreadPoolKey() {
        service.commandWithFallbackInheritsDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackInheritsDefaultProperties");
        Assert.assertEquals("DefaultThreadPoolKey", fallbackCommand.getThreadPoolKey().name());
    }

    @Test
    public void testFallbackInheritsDefaultCommandProperties() {
        service.commandWithFallbackInheritsDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackInheritsDefaultProperties");
        Assert.assertEquals(456, fallbackCommand.getProperties().executionTimeoutInMilliseconds().get().intValue());
    }

    @Test
    public void testFallbackInheritsThreadPollProperties() {
        service.commandWithFallbackInheritsDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackInheritsDefaultProperties");
        HystrixThreadPoolProperties properties = getThreadPoolProperties(fallbackCommand);
        Assert.assertEquals(123, properties.maxQueueSize().get().intValue());
    }

    @Test
    public void testFallbackOverridesDefaultGroupKey() {
        service.commandWithFallbackOverridesDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackOverridesDefaultProperties");
        Assert.assertEquals("FallbackGroupKey", fallbackCommand.getCommandGroup().name());
    }

    @Test
    public void testFallbackOverridesDefaultThreadPoolKey() {
        service.commandWithFallbackOverridesDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackOverridesDefaultProperties");
        Assert.assertEquals("FallbackThreadPoolKey", fallbackCommand.getThreadPoolKey().name());
    }

    @Test
    public void testFallbackOverridesDefaultCommandProperties() {
        service.commandWithFallbackOverridesDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackOverridesDefaultProperties");
        Assert.assertEquals(654, fallbackCommand.getProperties().executionTimeoutInMilliseconds().get().intValue());
    }

    @Test
    public void testFallbackOverridesThreadPollProperties() {
        service.commandWithFallbackOverridesDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackOverridesDefaultProperties");
        HystrixThreadPoolProperties properties = getThreadPoolProperties(fallbackCommand);
        Assert.assertEquals(321, properties.maxQueueSize().get().intValue());
    }

    @Test
    public void testCommandOverridesDefaultPropertiesWithFallbackInheritsDefaultProperties() {
        service.commandOverridesDefaultPropertiesWithFallbackInheritsDefaultProperties();
        com.netflix.hystrix.HystrixInvokableInfo fallbackCommand = CommonUtils.getHystrixCommandByKey("fallbackInheritsDefaultProperties");
        HystrixThreadPoolProperties properties = getThreadPoolProperties(fallbackCommand);
        Assert.assertEquals("DefaultGroupKey", fallbackCommand.getCommandGroup().name());
        Assert.assertEquals("DefaultThreadPoolKey", fallbackCommand.getThreadPoolKey().name());
        Assert.assertEquals(456, fallbackCommand.getProperties().executionTimeoutInMilliseconds().get().intValue());
        Assert.assertEquals(123, properties.maxQueueSize().get().intValue());
    }

    @DefaultProperties(groupKey = "DefaultGroupKey", threadPoolKey = "DefaultThreadPoolKey", commandProperties = { @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "456") }, threadPoolProperties = { @HystrixProperty(name = "maxQueueSize", value = "123") })
    public static class Service {
        @HystrixCommand(fallbackMethod = "fallbackInheritsDefaultProperties")
        public Object commandWithFallbackInheritsDefaultProperties() {
            throw new RuntimeException();
        }

        @HystrixCommand(fallbackMethod = "fallbackOverridesDefaultProperties")
        public Object commandWithFallbackOverridesDefaultProperties() {
            throw new RuntimeException();
        }

        @HystrixCommand(groupKey = "CommandGroupKey", threadPoolKey = "CommandThreadPoolKey", commandProperties = { @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "654") }, threadPoolProperties = { @HystrixProperty(name = "maxQueueSize", value = "321") }, fallbackMethod = "fallbackInheritsDefaultProperties")
        public Object commandOverridesDefaultPropertiesWithFallbackInheritsDefaultProperties() {
            throw new RuntimeException();
        }

        @HystrixCommand
        private Object fallbackInheritsDefaultProperties() {
            return null;
        }

        @HystrixCommand(groupKey = "FallbackGroupKey", threadPoolKey = "FallbackThreadPoolKey", commandProperties = { @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "654") }, threadPoolProperties = { @HystrixProperty(name = "maxQueueSize", value = "321") })
        private Object fallbackOverridesDefaultProperties() {
            return null;
        }
    }
}

