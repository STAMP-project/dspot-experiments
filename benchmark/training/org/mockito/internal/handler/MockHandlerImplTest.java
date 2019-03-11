/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.handler;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.misusing.WrongTypeOfReturnValue;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.progress.ArgumentMatcherStorage;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.stubbing.StubbedInvocationMatcher;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.Invocation;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;
import org.mockitoutil.TestBase;


@SuppressWarnings({ "unchecked", "serial" })
public class MockHandlerImplTest extends TestBase {
    private StubbedInvocationMatcher stubbedInvocationMatcher = Mockito.mock(StubbedInvocationMatcher.class);

    private Invocation invocation = Mockito.mock(Invocation.class);

    @Test
    public void should_remove_verification_mode_even_when_invalid_matchers() throws Throwable {
        // given
        Invocation invocation = new InvocationBuilder().toInvocation();
        @SuppressWarnings("rawtypes")
        MockHandlerImpl<?> handler = new MockHandlerImpl(new MockSettingsImpl());
        ThreadSafeMockingProgress.mockingProgress().verificationStarted(VerificationModeFactory.atLeastOnce());
        handler.matchersBinder = new MatchersBinder() {
            public InvocationMatcher bindMatchers(ArgumentMatcherStorage argumentMatcherStorage, Invocation invocation) {
                throw new InvalidUseOfMatchersException();
            }
        };
        try {
            // when
            handler.handle(invocation);
            // then
            Assert.fail();
        } catch (InvalidUseOfMatchersException ignored) {
        }
        Assert.assertNull(ThreadSafeMockingProgress.mockingProgress().pullVerificationMode());
    }

    @Test(expected = MockitoException.class)
    public void should_throw_mockito_exception_when_invocation_handler_throws_anything() throws Throwable {
        // given
        InvocationListener throwingListener = Mockito.mock(InvocationListener.class);
        Mockito.doThrow(new Throwable()).when(throwingListener).reportInvocation(ArgumentMatchers.any(MethodInvocationReport.class));
        MockHandlerImpl<?> handler = create_correctly_stubbed_handler(throwingListener);
        // when
        handler.handle(invocation);
    }

    @Test(expected = WrongTypeOfReturnValue.class)
    public void should_report_bogus_default_answer() throws Throwable {
        MockSettingsImpl mockSettings = Mockito.mock(MockSettingsImpl.class);
        MockHandlerImpl<?> handler = new MockHandlerImpl(mockSettings);
        BDDMockito.given(mockSettings.getDefaultAnswer()).willReturn(new Returns(MockHandlerImplTest.AWrongType.WRONG_TYPE));
        // otherwise cast is not done
        @SuppressWarnings("unused")
        String there_should_not_be_a_CCE_here = ((String) (handler.handle(new InvocationBuilder().method(Object.class.getDeclaredMethod("toString")).toInvocation())));
    }

    private static class AWrongType {
        public static final MockHandlerImplTest.AWrongType WRONG_TYPE = new MockHandlerImplTest.AWrongType();
    }
}

