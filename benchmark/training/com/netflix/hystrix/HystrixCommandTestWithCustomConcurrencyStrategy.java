/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix;


import HystrixCommandGroupKey.Factory;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class HystrixCommandTestWithCustomConcurrencyStrategy {
    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : true
     * HystrixCommand
     * * useRequestCache   : true
     * * useRequestLog     : true
     *
     * OUTCOME: RequestLog set up properly in command
     */
    @Test
    public void testCommandRequiresContextConcurrencyStrategyProvidesItContextSetUpCorrectly() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(true);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is set up properly
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(true, true);
        Assert.assertTrue(cmd.execute());
        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNotNull(HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
        Assert.assertNotNull(cmd.currentRequestLog);
        context.shutdown();
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : true
     * HystrixCommand
     * * useRequestCache   : true
     * * useRequestLog     : true
     *
     * OUTCOME: RequestLog not set up properly in command, static access is null
     */
    @Test
    public void testCommandRequiresContextConcurrencyStrategyProvidesItContextLeftUninitialized() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(true);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is not set up
        HystrixRequestContext.setContextOnCurrentThread(null);
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(true, true);
        Assert.assertTrue(cmd.execute());// command execution not affected by missing context

        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : false
     * HystrixCommand
     * * useRequestCache   : true
     * * useRequestLog     : true
     *
     * OUTCOME: RequestLog not set up in command, not available statically
     */
    @Test
    public void testCommandRequiresContextConcurrencyStrategyDoesNotProvideItContextSetUpCorrectly() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(false);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is set up properly
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(true, true);
        Assert.assertTrue(cmd.execute());
        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
        context.shutdown();
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : false
     * HystrixCommand
     * * useRequestCache   : true
     * * useRequestLog     : true
     *
     * OUTCOME: RequestLog not set up in command, not available statically
     */
    @Test
    public void testCommandRequiresContextConcurrencyStrategyDoesNotProvideItContextLeftUninitialized() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(false);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is not set up
        HystrixRequestContext.setContextOnCurrentThread(null);
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(true, true);
        Assert.assertTrue(cmd.execute());// command execution not affected by missing context

        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : true
     * HystrixCommand
     * * useRequestCache   : false
     * * useRequestLog     : false
     *
     * OUTCOME: RequestLog not set up in command, static access works properly
     */
    @Test
    public void testCommandDoesNotRequireContextConcurrencyStrategyProvidesItContextSetUpCorrectly() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(true);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is set up properly
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(false, false);
        Assert.assertTrue(cmd.execute());
        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNotNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNotNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
        context.shutdown();
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : true
     * HystrixCommand
     * * useRequestCache   : false
     * * useRequestLog     : false
     *
     * OUTCOME: RequestLog not set up in command, static access is null
     */
    @Test
    public void testCommandDoesNotRequireContextConcurrencyStrategyProvidesItContextLeftUninitialized() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(true);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is not set up
        HystrixRequestContext.setContextOnCurrentThread(null);
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(false, false);
        Assert.assertTrue(cmd.execute());// command execution not affected by missing context

        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : false
     * HystrixCommand
     * * useRequestCache   : false
     * * useRequestLog     : false
     *
     * OUTCOME: RequestLog not set up in command, not available statically
     */
    @Test
    public void testCommandDoesNotRequireContextConcurrencyStrategyDoesNotProvideItContextSetUpCorrectly() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(false);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is set up properly
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(true, true);
        Assert.assertTrue(cmd.execute());
        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
        context.shutdown();
    }

    /**
     * HystrixConcurrencyStrategy
     * * useDefaultRequestContext : false
     * HystrixCommand
     * * useRequestCache   : false
     * * useRequestLog     : false
     *
     * OUTCOME: RequestLog not set up in command, not available statically
     */
    @Test
    public void testCommandDoesNotRequireContextConcurrencyStrategyDoesNotProvideItContextLeftUninitialized() {
        HystrixConcurrencyStrategy strategy = new HystrixCommandTestWithCustomConcurrencyStrategy.CustomConcurrencyStrategy(false);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
        // context is not set up
        HystrixRequestContext.setContextOnCurrentThread(null);
        HystrixCommand<Boolean> cmd = new HystrixCommandTestWithCustomConcurrencyStrategy.TestCommand(true, true);
        Assert.assertTrue(cmd.execute());// command execution unaffected by missing context

        HystrixCommandTestWithCustomConcurrencyStrategy.printRequestLog();
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
        Assert.assertNull(HystrixRequestLog.getCurrentRequest(strategy));
        Assert.assertNull(cmd.currentRequestLog);
    }

    public static class TestCommand extends HystrixCommand<Boolean> {
        public TestCommand(boolean cacheEnabled, boolean logEnabled) {
            super(Setter.withGroupKey(Factory.asKey("TEST")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withRequestCacheEnabled(cacheEnabled).withRequestLogEnabled(logEnabled)));
        }

        @Override
        protected Boolean run() throws Exception {
            return true;
        }
    }

    public static class CustomConcurrencyStrategy extends HystrixConcurrencyStrategy {
        private final boolean useDefaultRequestContext;

        public CustomConcurrencyStrategy(boolean useDefaultRequestContext) {
            this.useDefaultRequestContext = useDefaultRequestContext;
        }

        @Override
        public <T> Callable<T> wrapCallable(Callable<T> callable) {
            return new HystrixCommandTestWithCustomConcurrencyStrategy.LoggingCallable<T>(callable);
        }

        @Override
        public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
            if (useDefaultRequestContext) {
                // this is the default RequestVariable implementation that requires a HystrixRequestContext
                return super.getRequestVariable(rv);
            } else {
                // this ignores the HystrixRequestContext
                return new com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault<T>() {
                    @Override
                    public T initialValue() {
                        return null;
                    }

                    @Override
                    public T get() {
                        return null;
                    }

                    @Override
                    public void set(T value) {
                        // do nothing
                    }

                    @Override
                    public void remove() {
                        // do nothing
                    }

                    @Override
                    public void shutdown(T value) {
                        // do nothing
                    }
                };
            }
        }
    }

    public static class LoggingCallable<T> implements Callable<T> {
        private final Callable<T> callable;

        public LoggingCallable(Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        public T call() throws Exception {
            System.out.println("********start call()");
            return callable.call();
        }
    }
}

