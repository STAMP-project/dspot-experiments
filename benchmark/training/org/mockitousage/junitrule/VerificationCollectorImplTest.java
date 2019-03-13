/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrule;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.VerificationCollector;
import org.mockitousage.IMethods;


public class VerificationCollectorImplTest {
    @Test
    public void should_not_throw_any_exceptions_when_verifications_are_successful() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        IMethods methods = Mockito.mock(IMethods.class);
        methods.simpleMethod();
        Mockito.verify(methods).simpleMethod();
        collector.collectAndReport();
    }

    @Test(expected = MockitoAssertionError.class)
    public void should_collect_verification_failures() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        IMethods methods = Mockito.mock(IMethods.class);
        Mockito.verify(methods).simpleMethod();
        collector.collectAndReport();
    }

    @Test
    public void should_collect_multiple_verification_failures() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        IMethods methods = Mockito.mock(IMethods.class);
        methods.intArgumentMethod(6);
        Mockito.verify(methods).simpleMethod();
        Mockito.verify(methods).byteReturningMethod();
        Mockito.verify(methods).intArgumentMethod(8);
        Mockito.verify(methods).longArg(8L);
        try {
            collector.collectAndReport();
            failBecauseExceptionWasNotThrown(MockitoAssertionError.class);
        } catch (MockitoAssertionError error) {
            assertThat(error).hasMessageContaining("1. Wanted but not invoked:");
            assertThat(error).hasMessageContaining("2. Wanted but not invoked:");
            assertThat(error).hasMessageContaining("3. Argument(s) are different! Wanted:");
            assertThat(error).hasMessageContaining("4. Wanted but not invoked:");
        }
    }

    @Test
    public void should_collect_matching_error_from_non_matching_arguments() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        IMethods methods = Mockito.mock(IMethods.class);
        methods.intArgumentMethod(6);
        methods.longArg(8L);
        methods.forShort(((short) (6)));
        Mockito.verify(methods).intArgumentMethod(8);
        Mockito.verify(methods).longArg(ArgumentMatchers.longThat(new ArgumentMatcher<Long>() {
            @Override
            public boolean matches(Long argument) {
                throw new AssertionError("custom error message");
            }
        }));
        Mockito.verify(methods).forShort(ArgumentMatchers.shortThat(new ArgumentMatcher<Short>() {
            @Override
            public boolean matches(Short argument) {
                return false;
            }
        }));
        try {
            collector.collectAndReport();
            failBecauseExceptionWasNotThrown(MockitoAssertionError.class);
        } catch (MockitoAssertionError error) {
            assertThat(error).hasMessageContaining("1. Argument(s) are different! Wanted:");
            assertThat(error).hasMessageContaining("2. custom error message");
            assertThat(error).hasMessageContaining("3. Argument(s) are different! Wanted:");
        }
    }

    @Test
    public void should_only_collect_failures_ignore_successful_verifications() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        IMethods methods = Mockito.mock(IMethods.class);
        Mockito.verify(methods, Mockito.never()).simpleMethod();
        Mockito.verify(methods).byteReturningMethod();
        this.assertExactlyOneFailure(collector);
    }

    @Test
    public void should_continue_collecting_after_failing_verification() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        IMethods methods = Mockito.mock(IMethods.class);
        methods.simpleMethod();
        Mockito.verify(methods).byteReturningMethod();
        Mockito.verify(methods).simpleMethod();
        this.assertExactlyOneFailure(collector);
    }

    @Test
    public void should_invoke_collector_rule_after_test() {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(VerificationCollectorImplTest.VerificationCollectorRuleInner.class);
        assertThat(result.getFailureCount()).as("failureCount").isEqualTo(2);
        assertThat(result.getFailures().get(0).getMessage()).as("failure1").contains("1. Wanted but not invoked:");
        assertThat(result.getFailures().get(1).getMessage()).as("failure2").contains("1. Argument(s) are different! Wanted:").contains("2. Wanted but not invoked:");
    }

    // This class is picked up when running a test suite using an IDE. It fails on purpose.
    public static class VerificationCollectorRuleInner {
        @Rule
        public VerificationCollector collector = MockitoJUnit.collector();

        @Test
        public void should_fail() {
            IMethods methods = Mockito.mock(IMethods.class);
            Mockito.verify(methods).simpleMethod();
        }

        @Test
        public void should_not_fail() {
            IMethods methods = Mockito.mock(IMethods.class);
            methods.simpleMethod();
            Mockito.verify(methods).simpleMethod();
        }

        @Test
        public void should_fail_with_args() {
            IMethods methods = Mockito.mock(IMethods.class);
            methods.intArgumentMethod(8);
            Mockito.verify(methods).intArgumentMethod(9);
            Mockito.verify(methods).byteReturningMethod();
        }
    }
}

