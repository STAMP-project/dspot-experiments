/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification.checkers;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.invocation.Invocation;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitousage.IMethods;


@RunWith(MockitoJUnitRunner.class)
public class NumberOfInvocationsCheckerTest {
    private InvocationMatcher wanted;

    private List<Invocation> invocations;

    @Mock
    private IMethods mock;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void shouldReportTooLittleActual() throws Exception {
        wanted = buildSimpleMethod().toInvocationMatcher();
        invocations = Arrays.asList(buildSimpleMethod().toInvocation(), buildSimpleMethod().toInvocation());
        exception.expect(TooLittleActualInvocations.class);
        exception.expectMessage("mock.simpleMethod()");
        exception.expectMessage("Wanted 100 times");
        exception.expectMessage("But was 2 times");
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 100);
    }

    @Test
    public void shouldReportAllInvocationsStackTrace() throws Exception {
        wanted = buildSimpleMethod().toInvocationMatcher();
        invocations = Arrays.asList(buildSimpleMethod().toInvocation(), buildSimpleMethod().toInvocation());
        exception.expect(TooLittleActualInvocations.class);
        exception.expectMessage("mock.simpleMethod()");
        exception.expectMessage("Wanted 100 times");
        exception.expectMessage("But was 2 times");
        exception.expectMessage(NumberOfInvocationsCheckerTest.containsTimes("-> at", 3));
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 100);
    }

    @Test
    public void shouldNotReportWithLastInvocationStackTraceIfNoInvocationsFound() throws Exception {
        invocations = Collections.emptyList();
        wanted = buildSimpleMethod().toInvocationMatcher();
        exception.expect(TooLittleActualInvocations.class);
        exception.expectMessage("mock.simpleMethod()");
        exception.expectMessage("Wanted 100 times");
        exception.expectMessage("But was 0 times");
        exception.expectMessage(NumberOfInvocationsCheckerTest.containsTimes("-> at", 1));
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 100);
    }

    @Test
    public void shouldReportWithAllInvocationsStackTrace() throws Exception {
        Invocation first = buildSimpleMethod().toInvocation();
        Invocation second = buildSimpleMethod().toInvocation();
        Invocation third = buildSimpleMethod().toInvocation();
        invocations = Arrays.asList(first, second, third);
        wanted = buildSimpleMethod().toInvocationMatcher();
        exception.expect(TooManyActualInvocations.class);
        exception.expectMessage(("" + (first.getLocation())));
        exception.expectMessage(("" + (second.getLocation())));
        exception.expectMessage(("" + (third.getLocation())));
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 2);
    }

    @Test
    public void shouldReportTooManyActual() throws Exception {
        Invocation first = buildSimpleMethod().toInvocation();
        Invocation second = buildSimpleMethod().toInvocation();
        invocations = Arrays.asList(first, second);
        wanted = buildSimpleMethod().toInvocationMatcher();
        exception.expectMessage("Wanted 1 time");
        exception.expectMessage("But was 2 times");
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 1);
    }

    @Test
    public void shouldReportNeverWantedButInvoked() throws Exception {
        Invocation first = buildSimpleMethod().toInvocation();
        invocations = Arrays.asList(first);
        wanted = buildSimpleMethod().toInvocationMatcher();
        exception.expect(NeverWantedButInvoked.class);
        exception.expectMessage("Never wanted here");
        exception.expectMessage("But invoked here");
        exception.expectMessage(("" + (first.getLocation())));
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 0);
    }

    @Test
    public void shouldMarkInvocationsAsVerified() throws Exception {
        Invocation invocation = buildSimpleMethod().toInvocation();
        assertThat(invocation.isVerified()).isFalse();
        invocations = Arrays.asList(invocation);
        wanted = buildSimpleMethod().toInvocationMatcher();
        NumberOfInvocationsChecker.checkNumberOfInvocations(invocations, wanted, 1);
        assertThat(invocation.isVerified()).isTrue();
    }

    private static class StringContainsNumberMatcher extends TypeSafeMatcher<String> {
        private final String expected;

        private final int amount;

        StringContainsNumberMatcher(String expected, int amount) {
            this.expected = expected;
            this.amount = amount;
        }

        public boolean matchesSafely(String text) {
            int lastIndex = 0;
            int count = 0;
            while (lastIndex != (-1)) {
                lastIndex = text.indexOf(expected, lastIndex);
                if (lastIndex != (-1)) {
                    count++;
                    lastIndex += expected.length();
                }
            } 
            return count == (amount);
        }

        public void describeTo(Description description) {
            description.appendText((((("containing '" + (expected)) + "' exactly ") + (amount)) + " times"));
        }
    }
}

