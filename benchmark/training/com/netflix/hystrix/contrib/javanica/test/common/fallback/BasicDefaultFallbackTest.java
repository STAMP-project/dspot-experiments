package com.netflix.hystrix.contrib.javanica.test.common.fallback;


import HystrixEventType.FAILURE;
import HystrixEventType.FALLBACK_SUCCESS;
import HystrixEventType.SUCCESS;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.CommonUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by dmgcodevil.
 */
public abstract class BasicDefaultFallbackTest extends BasicHystrixTest {
    private BasicDefaultFallbackTest.ServiceWithDefaultFallback serviceWithDefaultFallback;

    private BasicDefaultFallbackTest.ServiceWithDefaultCommandFallback serviceWithDefaultCommandFallback;

    @Test
    public void testClassScopeDefaultFallback() {
        String res = serviceWithDefaultFallback.requestString("");
        Assert.assertEquals(BasicDefaultFallbackTest.ServiceWithDefaultFallback.DEFAULT_RESPONSE, res);
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("requestString", command.getCommandKey().name());
        // confirm that 'requestString' command has failed
        Assert.assertTrue(command.getExecutionEvents().contains(FAILURE));
        // and that default fallback waw successful
        Assert.assertTrue(command.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testSpecificCommandFallbackOverridesDefault() {
        Integer res = serviceWithDefaultFallback.commandWithSpecificFallback("");
        Assert.assertEquals(Integer.valueOf(res), res);
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("commandWithSpecificFallback", command.getCommandKey().name());
        // confirm that 'commandWithSpecificFallback' command has failed
        Assert.assertTrue(command.getExecutionEvents().contains(FAILURE));
        // and that default fallback waw successful
        Assert.assertTrue(command.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testCommandScopeDefaultFallback() {
        Long res = serviceWithDefaultFallback.commandWithDefaultFallback(1L);
        Assert.assertEquals(Long.valueOf(0L), res);
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("commandWithDefaultFallback", command.getCommandKey().name());
        // confirm that 'requestString' command has failed
        Assert.assertTrue(command.getExecutionEvents().contains(FAILURE));
        // and that default fallback waw successful
        Assert.assertTrue(command.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testClassScopeCommandDefaultFallback() {
        String res = serviceWithDefaultCommandFallback.requestString("");
        Assert.assertEquals(BasicDefaultFallbackTest.ServiceWithDefaultFallback.DEFAULT_RESPONSE, res);
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> requestStringCommand = CommonUtils.getHystrixCommandByKey("requestString");
        com.netflix.hystrix.HystrixInvokableInfo fallback = CommonUtils.getHystrixCommandByKey("classDefaultFallback");
        Assert.assertEquals("requestString", requestStringCommand.getCommandKey().name());
        // confirm that command has failed
        Assert.assertTrue(requestStringCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(requestStringCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
        // confirm that fallback was successful
        Assert.assertTrue(fallback.getExecutionEvents().contains(SUCCESS));
    }

    @Test
    public void testCommandScopeCommandDefaultFallback() {
        Long res = serviceWithDefaultCommandFallback.commandWithDefaultFallback(1L);
        Assert.assertEquals(Long.valueOf(0L), res);
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> requestStringCommand = CommonUtils.getHystrixCommandByKey("commandWithDefaultFallback");
        com.netflix.hystrix.HystrixInvokableInfo fallback = CommonUtils.getHystrixCommandByKey("defaultCommandFallback");
        Assert.assertEquals("commandWithDefaultFallback", requestStringCommand.getCommandKey().name());
        // confirm that command has failed
        Assert.assertTrue(requestStringCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(requestStringCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
        // confirm that fallback was successful
        Assert.assertTrue(fallback.getExecutionEvents().contains(SUCCESS));
    }

    // case when class level default fallback
    @DefaultProperties(defaultFallback = "classDefaultFallback")
    public static class ServiceWithDefaultFallback {
        static final String DEFAULT_RESPONSE = "class_def";

        @HystrixCommand
        public String requestString(String str) {
            throw new RuntimeException();
        }

        public String classDefaultFallback() {
            return BasicDefaultFallbackTest.ServiceWithDefaultFallback.DEFAULT_RESPONSE;
        }

        @HystrixCommand(defaultFallback = "defaultCommandFallback")
        Long commandWithDefaultFallback(long l) {
            throw new RuntimeException();
        }

        Long defaultCommandFallback() {
            return 0L;
        }

        @HystrixCommand(fallbackMethod = "specificFallback")
        Integer commandWithSpecificFallback(String str) {
            throw new RuntimeException();
        }

        Integer specificFallback(String str) {
            return 0;
        }
    }

    // class level default fallback is annotated with @HystrixCommand and should be executed as Hystrix command
    @DefaultProperties(defaultFallback = "classDefaultFallback")
    public static class ServiceWithDefaultCommandFallback {
        static final String DEFAULT_RESPONSE = "class_def";

        @HystrixCommand
        public String requestString(String str) {
            throw new RuntimeException();
        }

        @HystrixCommand
        public String classDefaultFallback() {
            return BasicDefaultFallbackTest.ServiceWithDefaultCommandFallback.DEFAULT_RESPONSE;
        }

        @HystrixCommand(defaultFallback = "defaultCommandFallback")
        Long commandWithDefaultFallback(long l) {
            throw new RuntimeException();
        }

        @HystrixCommand(fallbackMethod = "defaultCommandFallback")
        Long defaultCommandFallback() {
            return 0L;
        }
    }
}

