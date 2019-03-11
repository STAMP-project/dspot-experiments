/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification.checkers;


import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.verification.InOrderContextImpl;
import org.mockito.internal.verification.api.InOrderContext;
import org.mockito.invocation.Invocation;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockitousage.IMethods;


public class MissingInvocationInOrderCheckerTest {
    private InvocationMatcher wanted;

    private List<Invocation> invocations;

    @Mock
    private IMethods mock;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private InOrderContext context = new InOrderContextImpl();

    @Test
    public void shouldPassWhenMatchingInteractionFound() throws Exception {
        invocations = Arrays.asList(buildSimpleMethod().toInvocation());
        wanted = buildSimpleMethod().toInvocationMatcher();
        MissingInvocationChecker.checkMissingInvocation(invocations, wanted, context);
    }

    @Test
    public void shouldReportWantedButNotInvoked() throws Exception {
        invocations = Arrays.asList(buildDifferentMethod().toInvocation());
        wanted = buildSimpleMethod().toInvocationMatcher();
        exception.expect(WantedButNotInvoked.class);
        exception.expectMessage("Wanted but not invoked:");
        exception.expectMessage("mock.simpleMethod()");
        MissingInvocationChecker.checkMissingInvocation(invocations, wanted, context);
    }

    @Test
    public void shouldReportArgumentsAreDifferent() throws Exception {
        invocations = Arrays.asList(buildIntArgMethod().arg(1111).toInvocation());
        wanted = buildIntArgMethod().arg(2222).toInvocationMatcher();
        exception.expect(ArgumentsAreDifferent.class);
        exception.expectMessage("Argument(s) are different! Wanted:");
        exception.expectMessage("mock.intArgumentMethod(2222);");
        exception.expectMessage("Actual invocation has different arguments:");
        exception.expectMessage("mock.intArgumentMethod(1111);");
        MissingInvocationChecker.checkMissingInvocation(invocations, wanted, context);
    }

    @Test
    public void shouldReportWantedDiffersFromActual() throws Exception {
        Invocation invocation1 = buildIntArgMethod().arg(1111).toInvocation();
        Invocation invocation2 = buildIntArgMethod().arg(2222).toInvocation();
        context.markVerified(invocation2);
        invocations = Arrays.asList(invocation1, invocation2);
        wanted = buildIntArgMethod().arg(2222).toInvocationMatcher();
        exception.expect(VerificationInOrderFailure.class);
        exception.expectMessage("Verification in order failure");
        exception.expectMessage("Wanted but not invoked:");
        exception.expectMessage("mock.intArgumentMethod(2222);");
        exception.expectMessage("Wanted anywhere AFTER following interaction:");
        exception.expectMessage("mock.intArgumentMethod(2222);");
        MissingInvocationChecker.checkMissingInvocation(invocations, wanted, context);
    }
}

