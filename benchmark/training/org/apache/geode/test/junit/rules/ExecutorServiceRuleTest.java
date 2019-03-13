/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.test.junit.rules;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.geode.test.awaitility.GeodeAwaitility;
import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Unit tests for {@link ExecutorServiceRule}.
 */
public class ExecutorServiceRuleTest {
    private static volatile CountDownLatch hangLatch;

    private static volatile CountDownLatch terminateLatch;

    private static volatile ExecutorService executorService;

    @Test
    public void providesExecutorService() {
        Result result = TestRunner.runTest(ExecutorServiceRuleTest.HasExecutorService.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(ExecutorServiceRuleTest.executorService).isInstanceOf(ExecutorService.class);
    }

    @Test
    public void shutsDownAfterTest() {
        Result result = TestRunner.runTest(ExecutorServiceRuleTest.HasExecutorService.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(ExecutorServiceRuleTest.executorService.isShutdown()).isTrue();
    }

    @Test
    public void terminatesAfterTest() {
        Result result = TestRunner.runTest(ExecutorServiceRuleTest.HasExecutorService.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(ExecutorServiceRuleTest.executorService.isTerminated()).isTrue();
    }

    @Test
    public void shutsDownHungThread() {
        Result result = TestRunner.runTest(ExecutorServiceRuleTest.Hangs.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(ExecutorServiceRuleTest.isTestHung()).isTrue();
        assertThat(ExecutorServiceRuleTest.executorService.isShutdown()).isTrue();
        ExecutorServiceRuleTest.awaitLatch(ExecutorServiceRuleTest.terminateLatch);
    }

    @Test
    public void terminatesHungThread() {
        Result result = TestRunner.runTest(ExecutorServiceRuleTest.Hangs.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(ExecutorServiceRuleTest.isTestHung()).isTrue();
        GeodeAwaitility.await().untilAsserted(() -> assertThat(ExecutorServiceRuleTest.executorService.isTerminated()).isTrue());
        ExecutorServiceRuleTest.awaitLatch(ExecutorServiceRuleTest.terminateLatch);
    }

    @Test
    public void futureTimesOut() {
        Result result = TestRunner.runTest(ExecutorServiceRuleTest.TimesOut.class);
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getFailures()).hasSize(1);
        Failure failure = result.getFailures().get(0);
        assertThat(failure.getException()).isInstanceOf(TimeoutException.class);
    }

    public abstract static class HasExecutorServiceRule {
        @Rule
        public ExecutorServiceRule executorServiceRule = new ExecutorServiceRule();

        @Before
        public void setUpHasAsynchronousRule() {
            ExecutorServiceRuleTest.executorService = executorServiceRule.getExecutorService();
        }
    }

    public static class HasExecutorService extends ExecutorServiceRuleTest.HasExecutorServiceRule {
        @Test
        public void doTest() {
            // nothing
        }
    }

    public static class Hangs extends ExecutorServiceRuleTest.HasExecutorServiceRule {
        @Test
        public void doTest() {
            executorServiceRule.runAsync(() -> {
                try {
                    org.apache.geode.test.junit.rules.hangLatch.await();
                } catch ( e) {
                    throw new <e>RuntimeException();
                } finally {
                    org.apache.geode.test.junit.rules.terminateLatch.countDown();
                }
            });
        }
    }

    public static class TimesOut extends ExecutorServiceRuleTest.HasExecutorServiceRule {
        @Test
        public void doTest() throws Exception {
            Future<Void> future = executorServiceRule.runAsync(() -> {
                try {
                    org.apache.geode.test.junit.rules.hangLatch.await();
                } catch ( e) {
                    throw new <e>RuntimeException();
                } finally {
                    org.apache.geode.test.junit.rules.terminateLatch.countDown();
                }
            });
            // this is expected to timeout
            future.get(1, TimeUnit.MILLISECONDS);
        }
    }
}

