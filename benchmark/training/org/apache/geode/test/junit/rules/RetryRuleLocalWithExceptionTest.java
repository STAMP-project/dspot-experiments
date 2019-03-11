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
 * Unit tests for {@link RetryRule} involving local scope (ie rule affects only the test methods
 * annotated with {@code @Retry}) with failures due to an {@code Exception}.
 *
 * @see org.apache.geode.test.junit.Retry
 * @see org.apache.geode.test.junit.rules.RetryRule
 */
public class RetryRuleLocalWithExceptionTest {
    @Test
    public void failsUnused() {
        Result result = TestRunner.runTest(RetryRuleLocalWithExceptionTest.FailsUnused.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(RetryRuleLocalWithExceptionTest.CustomException.class).hasMessage(RetryRuleLocalWithExceptionTest.FailsUnused.message);
        assertThat(RetryRuleLocalWithExceptionTest.FailsUnused.count).isEqualTo(1);
    }

    @Test
    public void passesUnused() {
        Result result = TestRunner.runTest(RetryRuleLocalWithExceptionTest.PassesUnused.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleLocalWithExceptionTest.PassesUnused.count).isEqualTo(1);
    }

    @Test
    public void failsOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(RetryRuleLocalWithExceptionTest.CustomException.class).hasMessage(RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.message);
        assertThat(RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void passesOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void failsOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(RetryRuleLocalWithExceptionTest.CustomException.class).hasMessage(RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.message);
        assertThat(RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.count).isEqualTo(3);
    }

    @Test
    public void passesOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.count).isEqualTo(3);
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
     * Used by test {@link #failsUnused()}
     */
    public static class FailsUnused {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithExceptionTest.FailsUnused.count = 0;
            RetryRuleLocalWithExceptionTest.FailsUnused.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        public void doTest() throws Exception {
            (RetryRuleLocalWithExceptionTest.FailsUnused.count)++;
            RetryRuleLocalWithExceptionTest.FailsUnused.message = "Failing " + (RetryRuleLocalWithExceptionTest.FailsUnused.count);
            throw new RetryRuleLocalWithExceptionTest.CustomException(RetryRuleLocalWithExceptionTest.FailsUnused.message);
        }
    }

    /**
     * Used by test {@link #passesUnused()}
     */
    public static class PassesUnused {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithExceptionTest.PassesUnused.count = 0;
            RetryRuleLocalWithExceptionTest.PassesUnused.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        public void doTest() throws Exception {
            (RetryRuleLocalWithExceptionTest.PassesUnused.count)++;
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
            RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.count = 0;
            RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.count)++;
            RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.message = "Failing " + (RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.count);
            throw new RetryRuleLocalWithExceptionTest.CustomException(RetryRuleLocalWithExceptionTest.FailsOnSecondAttempt.message);
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
            RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.count = 0;
            RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.count)++;
            if ((RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.count) < 2) {
                RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.message = "Failing " + (RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.count);
                throw new RetryRuleLocalWithExceptionTest.CustomException(RetryRuleLocalWithExceptionTest.PassesOnSecondAttempt.message);
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
            RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.count = 0;
            RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(3)
        public void doTest() throws Exception {
            (RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.count)++;
            RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.message = "Failing " + (RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.count);
            throw new RetryRuleLocalWithExceptionTest.CustomException(RetryRuleLocalWithExceptionTest.FailsOnThirdAttempt.message);
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
            RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.count = 0;
            RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(3)
        public void doTest() throws Exception {
            (RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.count)++;
            if ((RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.count) < 3) {
                RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.message = "Failing " + (RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.count);
                throw new RetryRuleLocalWithExceptionTest.CustomException(RetryRuleLocalWithExceptionTest.PassesOnThirdAttempt.message);
            }
        }
    }
}

