/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.handler;


import java.text.ParseException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.Invocation;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class InvocationNotifierHandlerTest {
    private static final String SOME_LOCATION = "some location";

    private static final RuntimeException SOME_EXCEPTION = new RuntimeException();

    private static final OutOfMemoryError SOME_ERROR = new OutOfMemoryError();

    private static final Answer<?> SOME_ANSWER = Mockito.mock(Answer.class);

    @Mock
    private InvocationListener listener1;

    @Mock
    private InvocationListener listener2;

    @Spy
    private InvocationNotifierHandlerTest.CustomListener customListener;

    @Mock
    private Invocation invocation;

    @Mock
    private MockHandlerImpl<ArrayList<Answer<?>>> mockHandler;

    private InvocationNotifierHandler<ArrayList<Answer<?>>> notifier;

    @Test
    public void should_notify_all_listeners_when_calling_delegate_handler() throws Throwable {
        // given
        BDDMockito.given(mockHandler.handle(invocation)).willReturn("returned value");
        // when
        notifier.handle(invocation);
        // then
        Mockito.verify(listener1).reportInvocation(new NotifiedMethodInvocationReport(invocation, "returned value"));
        Mockito.verify(listener2).reportInvocation(new NotifiedMethodInvocationReport(invocation, "returned value"));
    }

    @Test
    public void should_notify_all_listeners_when_called_delegate_handler_returns_ex() throws Throwable {
        // given
        Exception computedException = new Exception("computed");
        BDDMockito.given(mockHandler.handle(invocation)).willReturn(computedException);
        // when
        notifier.handle(invocation);
        // then
        Mockito.verify(listener1).reportInvocation(new NotifiedMethodInvocationReport(invocation, ((Object) (computedException))));
        Mockito.verify(listener2).reportInvocation(new NotifiedMethodInvocationReport(invocation, ((Object) (computedException))));
    }

    @Test(expected = ParseException.class)
    public void should_notify_all_listeners_when_called_delegate_handler_throws_exception_and_rethrow_it() throws Throwable {
        // given
        ParseException parseException = new ParseException("", 0);
        BDDMockito.given(mockHandler.handle(invocation)).willThrow(parseException);
        // when
        try {
            notifier.handle(invocation);
            Assert.fail();
        } finally {
            // then
            Mockito.verify(listener1).reportInvocation(new NotifiedMethodInvocationReport(invocation, parseException));
            Mockito.verify(listener2).reportInvocation(new NotifiedMethodInvocationReport(invocation, parseException));
        }
    }

    @Test
    public void should_report_listener_exception() throws Throwable {
        BDDMockito.willThrow(new NullPointerException()).given(customListener).reportInvocation(ArgumentMatchers.any(MethodInvocationReport.class));
        try {
            notifier.handle(invocation);
            Assert.fail();
        } catch (MockitoException me) {
            assertThat(me.getMessage()).contains("invocation listener").contains("CustomListener").contains("threw an exception").contains("NullPointerException");
        }
    }

    @Test
    public void should_delegate_all_MockHandlerInterface_to_the_parameterized_MockHandler() throws Exception {
        notifier.getInvocationContainer();
        notifier.getMockSettings();
        Mockito.verify(mockHandler).getInvocationContainer();
        Mockito.verify(mockHandler).getMockSettings();
    }

    private static class CustomListener implements InvocationListener {
        public void reportInvocation(MethodInvocationReport methodInvocationReport) {
            // nop
        }
    }
}

