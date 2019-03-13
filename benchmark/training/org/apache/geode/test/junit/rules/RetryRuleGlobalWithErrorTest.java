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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Unit tests for {@link RetryRule} involving global scope (ie rule affects all tests in the test
 * class) with failures due to an {@code Error}.
 *
 * @see org.apache.geode.test.junit.rules.RetryRule
 */
public class RetryRuleGlobalWithErrorTest {
    @Test
    public void zeroIsIllegal() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.ZeroIsIllegal.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(IllegalArgumentException.class).hasMessage(RetryRuleGlobalWithErrorTest.ZeroIsIllegal.message);
        assertThat(RetryRuleGlobalWithErrorTest.ZeroIsIllegal.count).isEqualTo(0);
    }

    @Test
    public void failsWithOne() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.FailsWithOne.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(RetryRuleGlobalWithErrorTest.FailsWithOne.message);
        assertThat(RetryRuleGlobalWithErrorTest.FailsWithOne.count).isEqualTo(1);
    }

    @Test
    public void passesWithOne() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.PassesWithOne.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleGlobalWithErrorTest.PassesWithOne.count).isEqualTo(1);
    }

    @Test
    public void passesWithUnused() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.PassesWhenUnused.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleGlobalWithErrorTest.PassesWhenUnused.count).isEqualTo(1);
    }

    @Test
    public void failsOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.message);
        assertThat(RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void passesOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void failsOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.message);
        assertThat(RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.count).isEqualTo(3);
    }

    @Test
    public void passesOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.count).isEqualTo(3);
    }

    /**
     * Used by test {@link #zeroIsIllegal()}
     */
    public static class ZeroIsIllegal {
        static final String message = "Retry count must be greater than zero";

        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithErrorTest.ZeroIsIllegal.count = 0;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(0);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.ZeroIsIllegal.count)++;
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
            RetryRuleGlobalWithErrorTest.FailsWithOne.count = 0;
            RetryRuleGlobalWithErrorTest.FailsWithOne.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(1);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.FailsWithOne.count)++;
            RetryRuleGlobalWithErrorTest.FailsWithOne.message = "Failing " + (RetryRuleGlobalWithErrorTest.FailsWithOne.count);
            Assert.fail(RetryRuleGlobalWithErrorTest.FailsWithOne.message);
        }
    }

    /**
     * Used by test {@link #passesWithOne()}
     */
    public static class PassesWithOne {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithErrorTest.PassesWithOne.count = 0;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(1);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.PassesWithOne.count)++;
        }
    }

    /**
     * Used by test {@link #passesWithUnused()}
     */
    public static class PassesWhenUnused {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleGlobalWithErrorTest.PassesWhenUnused.count = 0;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.PassesWhenUnused.count)++;
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
            RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.count = 0;
            RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.count)++;
            RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.message = "Failing " + (RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.count);
            Assert.fail(RetryRuleGlobalWithErrorTest.FailsOnSecondAttempt.message);
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
            RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.count = 0;
            RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.count)++;
            if ((RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.count) < 2) {
                RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.message = "Failing " + (RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.count);
                Assert.fail(RetryRuleGlobalWithErrorTest.PassesOnSecondAttempt.message);
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
            RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.count = 0;
            RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(3);

        @Test
        @Retry(3)
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.count)++;
            RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.message = "Failing " + (RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.count);
            Assert.fail(RetryRuleGlobalWithErrorTest.FailsOnThirdAttempt.message);
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
            RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.count = 0;
            RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule(3);

        @Test
        public void doTest() throws Exception {
            (RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.count)++;
            if ((RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.count) < 3) {
                RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.message = "Failing " + (RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.count);
                Assert.fail(RetryRuleGlobalWithErrorTest.PassesOnThirdAttempt.message);
            }
        }
    }
}

