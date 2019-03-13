package com.netflix.hystrix.contrib.javanica.test.common.configuration.command;


import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by dmgcodevil.
 */
public abstract class BasicCommandDefaultPropertiesTest extends BasicHystrixTest {
    private BasicCommandDefaultPropertiesTest.Service service;

    @Test
    public void testCommandInheritsDefaultGroupKey() {
        service.commandInheritsDefaultProperties();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("DefaultGroupKey", command.getCommandGroup().name());
    }

    @Test
    public void testCommandOverridesDefaultGroupKey() {
        service.commandOverridesGroupKey();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("SpecificGroupKey", command.getCommandGroup().name());
    }

    @Test
    public void testCommandInheritsDefaultThreadPoolKey() {
        service.commandInheritsDefaultProperties();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("DefaultThreadPoolKey", command.getThreadPoolKey().name());
    }

    @Test
    public void testCommandOverridesDefaultThreadPoolKey() {
        service.commandOverridesThreadPoolKey();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("SpecificThreadPoolKey", command.getThreadPoolKey().name());
    }

    @Test
    public void testCommandInheritsDefaultCommandProperties() {
        service.commandInheritsDefaultProperties();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals(456, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
    }

    @Test
    public void testCommandOverridesDefaultCommandProperties() {
        service.commandOverridesDefaultCommandProperties();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals(654, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
    }

    @Test
    public void testCommandInheritsThreadPollProperties() {
        service.commandInheritsDefaultProperties();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        HystrixThreadPoolProperties properties = getThreadPoolProperties(command);
        Assert.assertEquals(123, properties.maxQueueSize().get().intValue());
    }

    @Test
    public void testCommandOverridesDefaultThreadPollProperties() {
        service.commandOverridesDefaultThreadPoolProperties();
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        HystrixThreadPoolProperties properties = getThreadPoolProperties(command);
        Assert.assertEquals(321, properties.maxQueueSize().get().intValue());
    }

    @DefaultProperties(groupKey = "DefaultGroupKey", threadPoolKey = "DefaultThreadPoolKey", commandProperties = { @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "456") }, threadPoolProperties = { @HystrixProperty(name = "maxQueueSize", value = "123") })
    public static class Service {
        @HystrixCommand
        public Object commandInheritsDefaultProperties() {
            return null;
        }

        @HystrixCommand(groupKey = "SpecificGroupKey")
        public Object commandOverridesGroupKey() {
            return null;
        }

        @HystrixCommand(threadPoolKey = "SpecificThreadPoolKey")
        public Object commandOverridesThreadPoolKey() {
            return null;
        }

        @HystrixCommand(commandProperties = { @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "654") })
        public Object commandOverridesDefaultCommandProperties() {
            return null;
        }

        @HystrixCommand(threadPoolProperties = { @HystrixProperty(name = "maxQueueSize", value = "321") })
        public Object commandOverridesDefaultThreadPoolProperties() {
            return null;
        }
    }
}

