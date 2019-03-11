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
import HystrixRequestLog.MAX_STORAGE;
import com.hystrix.junit.HystrixRequestContextRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import static HystrixRequestLog.MAX_STORAGE;


public class HystrixRequestLogTest {
    private static final String DIGITS_REGEX = "\\[\\d+";

    @Rule
    public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

    @Test
    public void testSuccess() {
        execute();
        String log = HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString();
        // strip the actual count so we can compare reliably
        log = log.replaceAll(HystrixRequestLogTest.DIGITS_REGEX, "[");
        Assert.assertEquals("TestCommand[SUCCESS][ms]", log);
    }

    @Test
    public void testSuccessFromCache() {
        // 1 success
        execute();
        // 4 success from cache
        execute();
        execute();
        execute();
        execute();
        String log = HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString();
        // strip the actual count so we can compare reliably
        log = log.replaceAll(HystrixRequestLogTest.DIGITS_REGEX, "[");
        Assert.assertEquals("TestCommand[SUCCESS][ms], TestCommand[SUCCESS, RESPONSE_FROM_CACHE][ms]x4", log);
    }

    @Test
    public void testFailWithFallbackSuccess() {
        // 1 failure
        execute();
        // 4 failures from cache
        execute();
        execute();
        execute();
        execute();
        String log = HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString();
        // strip the actual count so we can compare reliably
        log = log.replaceAll(HystrixRequestLogTest.DIGITS_REGEX, "[");
        Assert.assertEquals("TestCommand[FAILURE, FALLBACK_SUCCESS][ms], TestCommand[FAILURE, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE][ms]x4", log);
    }

    @Test
    public void testFailWithFallbackFailure() {
        // 1 failure
        try {
            execute();
        } catch (Exception e) {
        }
        // 1 failure from cache
        try {
            execute();
        } catch (Exception e) {
        }
        String log = HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString();
        // strip the actual count so we can compare reliably
        log = log.replaceAll(HystrixRequestLogTest.DIGITS_REGEX, "[");
        Assert.assertEquals("TestCommand[FAILURE, FALLBACK_FAILURE][ms], TestCommand[FAILURE, FALLBACK_FAILURE, RESPONSE_FROM_CACHE][ms]", log);
    }

    @Test
    public void testTimeout() {
        Observable<String> result = null;
        // 1 timeout
        try {
            for (int i = 0; i < 1; i++) {
                result = observe();
            }
        } catch (Exception e) {
        }
        try {
            result.toBlocking().single();
        } catch (Throwable ex) {
            // ex.printStackTrace();
        }
        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " -> done with awaiting all observables"));
        String log = HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString();
        // strip the actual count so we can compare reliably
        log = log.replaceAll(HystrixRequestLogTest.DIGITS_REGEX, "[");
        Assert.assertEquals("TestCommand[TIMEOUT, FALLBACK_MISSING][ms]", log);
    }

    @Test
    public void testManyTimeouts() {
        for (int i = 0; i < 10; i++) {
            testTimeout();
            ctx.reset();
        }
    }

    @Test
    public void testMultipleCommands() {
        // 1 success
        execute();
        // 1 success
        execute();
        // 1 success
        execute();
        // 1 success from cache
        execute();
        // 1 failure
        try {
            execute();
        } catch (Exception e) {
        }
        // 1 failure from cache
        try {
            execute();
        } catch (Exception e) {
        }
        String log = HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString();
        // strip the actual count so we can compare reliably
        log = log.replaceAll(HystrixRequestLogTest.DIGITS_REGEX, "[");
        Assert.assertEquals("GetData[SUCCESS][ms], PutData[SUCCESS][ms], GetValues[SUCCESS][ms], GetValues[SUCCESS, RESPONSE_FROM_CACHE][ms], TestCommand[FAILURE, FALLBACK_FAILURE][ms], TestCommand[FAILURE, FALLBACK_FAILURE, RESPONSE_FROM_CACHE][ms]", log);
    }

    @Test
    public void testMaxLimit() {
        for (int i = 0; i < (MAX_STORAGE); i++) {
            execute();
        }
        // then execute again some more
        for (int i = 0; i < 10; i++) {
            execute();
        }
        Assert.assertEquals(MAX_STORAGE, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    private static class TestCommand extends HystrixCommand<String> {
        private final String value;

        private final boolean fail;

        private final boolean failOnFallback;

        private final boolean timeout;

        private final boolean useFallback;

        private final boolean useCache;

        public TestCommand(String commandName, String value, boolean fail, boolean failOnFallback) {
            super(Setter.withGroupKey(Factory.asKey("RequestLogTestCommand")).andCommandKey(HystrixCommandKey.Factory.asKey(commandName)));
            this.value = value;
            this.fail = fail;
            this.failOnFallback = failOnFallback;
            this.timeout = false;
            this.useFallback = true;
            this.useCache = true;
        }

        public TestCommand(String value, boolean fail, boolean failOnFallback) {
            super(Factory.asKey("RequestLogTestCommand"));
            this.value = value;
            this.fail = fail;
            this.failOnFallback = failOnFallback;
            this.timeout = false;
            this.useFallback = true;
            this.useCache = true;
        }

        public TestCommand(String value, boolean fail, boolean failOnFallback, boolean timeout) {
            super(Setter.withGroupKey(Factory.asKey("RequestLogTestCommand")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500)));
            this.value = value;
            this.fail = fail;
            this.failOnFallback = failOnFallback;
            this.timeout = timeout;
            this.useFallback = false;
            this.useCache = false;
        }

        @Override
        protected String run() {
            System.out.println((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())));
            if (fail) {
                throw new RuntimeException("forced failure");
            } else
                if (timeout) {
                    try {
                        Thread.sleep(10000);
                        System.out.println("Woke up from sleep!");
                    } catch (InterruptedException ex) {
                        System.out.println(((Thread.currentThread().getName()) + " Interrupted by timeout"));
                    }
                }

            return value;
        }

        @Override
        protected String getFallback() {
            if (useFallback) {
                if (failOnFallback) {
                    throw new RuntimeException("forced fallback failure");
                } else {
                    return (value) + "-fallback";
                }
            } else {
                throw new UnsupportedOperationException("no fallback implemented");
            }
        }

        @Override
        protected String getCacheKey() {
            if (useCache) {
                return value;
            } else {
                return null;
            }
        }
    }
}

