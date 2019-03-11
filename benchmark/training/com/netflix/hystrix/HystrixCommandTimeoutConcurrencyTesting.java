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
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import rx.Observable;


public class HystrixCommandTimeoutConcurrencyTesting {
    private static final int NUM_CONCURRENT_COMMANDS = 30;

    @Test
    public void testTimeoutRace() throws InterruptedException {
        final int NUM_TRIALS = 10;
        for (int i = 0; i < NUM_TRIALS; i++) {
            List<Observable<String>> observables = new ArrayList<Observable<String>>();
            HystrixRequestContext context = null;
            try {
                context = HystrixRequestContext.initializeContext();
                for (int j = 0; j < (HystrixCommandTimeoutConcurrencyTesting.NUM_CONCURRENT_COMMANDS); j++) {
                    observables.add(observe());
                }
                Observable<String> overall = Observable.merge(observables);
                List<String> results = overall.toList().toBlocking().first();// wait for all commands to complete

                for (String s : results) {
                    if (s == null) {
                        System.err.println("Received NULL!");
                        throw new RuntimeException("Received NULL");
                    }
                }
                for (HystrixInvokableInfo<?> hi : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()) {
                    if (!(hi.isResponseTimedOut())) {
                        System.err.println("Timeout not found in executed command");
                        throw new RuntimeException("Timeout not found in executed command");
                    }
                    if ((hi.isResponseTimedOut()) && ((hi.getExecutionEvents().size()) == 1)) {
                        System.err.println("Missing fallback status!");
                        throw new RuntimeException("Missing fallback status on timeout.");
                    }
                }
            } catch (Exception e) {
                System.err.println(("Error: " + (e.getMessage())));
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                System.out.println(HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
                if (context != null) {
                    context.shutdown();
                }
            }
            System.out.println((("*************** TRIAL " + i) + " ******************"));
            System.out.println();
            Thread.sleep(50);
        }
        Hystrix.reset();
    }

    public static class TestCommand extends HystrixCommand<String> {
        protected TestCommand() {
            super(Setter.withGroupKey(Factory.asKey("testTimeoutConcurrency")).andCommandKey(HystrixCommandKey.Factory.asKey("testTimeoutConcurrencyCommand")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(3).withCircuitBreakerEnabled(false).withFallbackIsolationSemaphoreMaxConcurrentRequests(HystrixCommandTimeoutConcurrencyTesting.NUM_CONCURRENT_COMMANDS)).andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(HystrixCommandTimeoutConcurrencyTesting.NUM_CONCURRENT_COMMANDS).withMaxQueueSize(HystrixCommandTimeoutConcurrencyTesting.NUM_CONCURRENT_COMMANDS).withQueueSizeRejectionThreshold(HystrixCommandTimeoutConcurrencyTesting.NUM_CONCURRENT_COMMANDS)));
        }

        @Override
        protected String run() throws Exception {
            // System.out.println(System.currentTimeMillis() + " : " + Thread.currentThread().getName() + " sleeping");
            Thread.sleep(500);
            // System.out.println(System.currentTimeMillis() + " : " + Thread.currentThread().getName() + " awake and returning");
            return "hello";
        }

        @Override
        protected String getFallback() {
            return "failed";
        }
    }
}

