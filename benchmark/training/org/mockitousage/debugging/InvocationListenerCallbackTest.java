/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.debugging;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.DescribedInvocation;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;


/**
 * Ensures that custom listeners can be registered and will be called every time
 * a method on a mock is invoked.
 */
public class InvocationListenerCallbackTest {
    @Test
    public void should_call_single_listener_when_mock_return_normally() throws Exception {
        // given
        InvocationListenerCallbackTest.RememberingListener listener = new InvocationListenerCallbackTest.RememberingListener();
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().invocationListeners(listener));
        BDDMockito.willReturn("basil").given(foo).giveMeSomeString("herb");
        // when
        foo.giveMeSomeString("herb");
        // then
        assertThat(listener).is(InvocationListenerCallbackTest.notifiedFor("basil", getClass().getSimpleName()));
    }

    @Test
    public void should_call_listeners_in_order() throws Exception {
        // given
        List<InvocationListener> container = new ArrayList<InvocationListener>();
        InvocationListenerCallbackTest.RememberingListener listener1 = new InvocationListenerCallbackTest.RememberingListener(container);
        InvocationListenerCallbackTest.RememberingListener listener2 = new InvocationListenerCallbackTest.RememberingListener(container);
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().invocationListeners(listener1, listener2));
        // when
        foo.giveMeSomeString("herb");
        // then
        assertThat(container).containsExactly(listener1, listener2);
    }

    @Test
    public void should_allow_same_listener() throws Exception {
        // given
        List<InvocationListener> container = new ArrayList<InvocationListener>();
        InvocationListenerCallbackTest.RememberingListener listener1 = new InvocationListenerCallbackTest.RememberingListener(container);
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().invocationListeners(listener1, listener1));
        // when
        foo.giveMeSomeString("a");
        foo.giveMeSomeString("b");
        // then each listener was notified 2 times (notified 4 times in total)
        assertThat(container).containsExactly(listener1, listener1, listener1, listener1);
    }

    @Test
    public void should_call_all_listener_when_mock_return_normally() throws Exception {
        // given
        InvocationListenerCallbackTest.RememberingListener listener1 = new InvocationListenerCallbackTest.RememberingListener();
        InvocationListenerCallbackTest.RememberingListener listener2 = new InvocationListenerCallbackTest.RememberingListener();
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().invocationListeners(listener1, listener2));
        BDDMockito.given(foo.giveMeSomeString("herb")).willReturn("rosemary");
        // when
        foo.giveMeSomeString("herb");
        // then
        assertThat(listener1).is(InvocationListenerCallbackTest.notifiedFor("rosemary", getClass().getSimpleName()));
        assertThat(listener2).is(InvocationListenerCallbackTest.notifiedFor("rosemary", getClass().getSimpleName()));
    }

    @Test
    public void should_call_all_listener_when_mock_throws_exception() throws Exception {
        // given
        InvocationListener listener1 = Mockito.mock(InvocationListener.class, "listener1");
        InvocationListener listener2 = Mockito.mock(InvocationListener.class, "listener2");
        Foo foo = Mockito.mock(Foo.class, Mockito.withSettings().invocationListeners(listener1, listener2));
        Mockito.doThrow(new InvocationListenerCallbackTest.OvenNotWorking()).when(foo).doSomething("cook");
        // when
        try {
            foo.doSomething("cook");
            Assert.fail("Exception expected.");
        } catch (InvocationListenerCallbackTest.OvenNotWorking actualException) {
            // then
            InOrder orderedVerify = Mockito.inOrder(listener1, listener2);
            orderedVerify.verify(listener1).reportInvocation(ArgumentMatchers.any(MethodInvocationReport.class));
            orderedVerify.verify(listener2).reportInvocation(ArgumentMatchers.any(MethodInvocationReport.class));
        }
    }

    private static class RememberingListener implements InvocationListener {
        private final List<InvocationListener> listenerContainer;

        DescribedInvocation invocation;

        Object returnValue;

        String locationOfStubbing;

        RememberingListener(List<InvocationListener> listenerContainer) {
            this.listenerContainer = listenerContainer;
        }

        RememberingListener() {
            this(new ArrayList<InvocationListener>());
        }

        public void reportInvocation(MethodInvocationReport mcr) {
            this.invocation = mcr.getInvocation();
            this.returnValue = mcr.getReturnedValue();
            this.locationOfStubbing = mcr.getLocationOfStubbing();
            listenerContainer.add(this);// so that we can assert on order

        }
    }

    private static class OvenNotWorking extends RuntimeException {}
}

