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


import java.util.List;
import org.apache.geode.test.junit.Retry;
import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Unit tests for {@link RetryRule} involving global scope (ie rule affects all tests in the test
 * class) with failures due to an {@code Exception}.
 *
 * @see org.apache.geode.test.junit.rules.RetryRule
 */
public class RetryRuleGlobalWithExceptionTest {
    @Test
    public void zeroIsIllegal() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.ZeroIsIllegal.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(IllegalArgumentException.class).hasMessage(RetryRuleGlobalWithExceptionTest.ZeroIsIllegal.message);
        assertThat(RetryRuleGlobalWithExceptionTest.ZeroIsIllegal.count).isEqualTo(0);
    }

    @Test
    public void failsWithOne() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.FailsWithOne.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(RetryRuleGlobalWithExceptionTest.CustomException.class).hasMessage(RetryRuleGlobalWithExceptionTest.FailsWithOne.message);
        assertThat(RetryRuleGlobalWithExceptionTest.FailsWithOne.count).isEqualTo(1);
    }

    @Test
    public void passesWithOne() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.PassesWithOne.class);
        assertThat(result.wasSuccessful()).isTrue();
    }

    @Test
    public void passesWithUnused() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.PassesWhenUnused.class);
        assertThat(result.wasSuccessful()).isTrue();
    }

    @Test
    public void failsOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(RetryRuleGlobalWithExceptionTest.CustomException.class).hasMessage(RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.message);
        assertThat(RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void passesOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void failsOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(RetryRuleGlobalWithExceptionTest.CustomException.class).hasMessage(RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.message);
        assertThat(RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.count).isEqualTo(3);
    }

    @Test
    public void passesOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.count).isEqualTo(3);
    }

    /**
     * Custom exception used by several tests
     */
    public static class CustomException extends Exception {
        public CustomException(final String message) {
            super(message);
        }
    }

    /**
     * Used by test {@link #zeroIsIllegal()}
     */
    public static class ZeroIsIllegal {
        static int count = 0;

        static final String message = "Retry count must be greater than zero";

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.ZeroIsIllegal.count = 0;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(0);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.ZeroIsIllegal.count)++;
        }
    }

    /**
     * Used by test {@link #failsWithOne()}
     */
    public static class FailsWithOne {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.FailsWithOne.count = 0;
            RetryRuleGlobalWithExceptionTest.FailsWithOne.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(1);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.FailsWithOne.count)++;
            RetryRuleGlobalWithExceptionTest.FailsWithOne.message = "Failing " + (RetryRuleGlobalWithExceptionTest.FailsWithOne.count);
            throw new RetryRuleGlobalWithExceptionTest.CustomException(RetryRuleGlobalWithExceptionTest.FailsWithOne.message);
        }
    }

    /**
     * Used by test {@link #passesWithOne()}
     */
    public static class PassesWithOne {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.PassesWithOne.count = 0;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(1);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.PassesWithOne.count)++;
        }
    }

    /**
     * Used by test {@link #passesWithUnused()}
     */
    public static class PassesWhenUnused {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.PassesWhenUnused.count = 0;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.PassesWhenUnused.count)++;
        }
    }

    /**
     * Used by test {@link #failsOnSecondAttempt()}
     */
    public static class FailsOnSecondAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.count = 0;
            RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.count)++;
            RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.message = "Failing " + (RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.count);
            throw new RetryRuleGlobalWithExceptionTest.CustomException(RetryRuleGlobalWithExceptionTest.FailsOnSecondAttempt.message);
        }
    }

    /**
     * Used by test {@link #passesOnSecondAttempt()}
     */
    public static class PassesOnSecondAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.count = 0;
            RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.count)++;
            if ((RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.count) < 2) {
                RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.message = "Failing " + (RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.count);
                throw new RetryRuleGlobalWithExceptionTest.CustomException(RetryRuleGlobalWithExceptionTest.PassesOnSecondAttempt.message);
            }
        }
    }

    /**
     * Used by test {@link #failsOnThirdAttempt()}
     */
    public static class FailsOnThirdAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.count = 0;
            RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(3);

        @Test
        @Retry(3)
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.count)++;
            RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.message = "Failing " + (RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.count);
            throw new RetryRuleGlobalWithExceptionTest.CustomException(RetryRuleGlobalWithExceptionTest.FailsOnThirdAttempt.message);
        }
    }

    /**
     * Used by test {@link #passesOnThirdAttempt()}
     */
    public static class PassesOnThirdAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.count = 0;
            RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(3);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.count)++;
            if ((RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.count) < 3) {
                RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.message = "Failing " + (RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.count);
                throw new RetryRuleGlobalWithExceptionTest.CustomException(RetryRuleGlobalWithExceptionTest.PassesOnThirdAttempt.message);
            }
        }
    }
}

