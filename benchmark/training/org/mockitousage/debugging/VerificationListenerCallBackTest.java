/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.debugging;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoFramework;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.listeners.VerificationListener;
import org.mockito.verification.VerificationEvent;
import org.mockito.verification.VerificationMode;
import org.mockitoutil.TestBase;


public class VerificationListenerCallBackTest extends TestBase {
    @Test
    public void should_call_single_listener_on_verify() throws NoSuchMethodException {
        // given
        VerificationListenerCallBackTest.RememberingListener listener = new VerificationListenerCallBackTest.RememberingListener();
        MockitoFramework mockitoFramework = Mockito.framework();
        mockitoFramework.addListener(listener);
        Method invocationWanted = Foo.class.getDeclaredMethod("doSomething", String.class);
        Foo foo = Mockito.mock(Foo.class);
        // when
        VerificationMode never = Mockito.never();
        Mockito.verify(foo, never).doSomething("");
        // then
        assertThat(listener).is(VerificationListenerCallBackTest.notifiedFor(foo, never, invocationWanted));
    }

    @Test
    public void should_call_all_listeners_on_verify() throws NoSuchMethodException {
        // given
        VerificationListenerCallBackTest.RememberingListener listener1 = new VerificationListenerCallBackTest.RememberingListener();
        VerificationListenerCallBackTest.RememberingListener2 listener2 = new VerificationListenerCallBackTest.RememberingListener2();
        Mockito.framework().addListener(listener1).addListener(listener2);
        Method invocationWanted = Foo.class.getDeclaredMethod("doSomething", String.class);
        Foo foo = Mockito.mock(Foo.class);
        // when
        VerificationMode never = Mockito.never();
        Mockito.verify(foo, never).doSomething("");
        // then
        assertThat(listener1).is(VerificationListenerCallBackTest.notifiedFor(foo, never, invocationWanted));
        assertThat(listener2).is(VerificationListenerCallBackTest.notifiedFor(foo, never, invocationWanted));
    }

    @Test
    public void should_not_call_listener_when_verify_was_called_incorrectly() {
        // when
        VerificationListener listener = Mockito.mock(VerificationListener.class);
        Mockito.framework().addListener(listener);
        Foo foo = null;
        try {
            Mockito.verify(foo).doSomething("");
            Assert.fail("Exception expected.");
        } catch (Exception e) {
            // then
            Mockito.verify(listener, Mockito.never()).onVerification(ArgumentMatchers.any(VerificationEvent.class));
        }
    }

    @Test
    public void should_notify_when_verification_throws_type_error() {
        // given
        VerificationListenerCallBackTest.RememberingListener listener = new VerificationListenerCallBackTest.RememberingListener();
        MockitoFramework mockitoFramework = Mockito.framework();
        mockitoFramework.addListener(listener);
        Foo foo = Mockito.mock(Foo.class);
        // when
        try {
            Mockito.verify(foo).doSomething("");
            Assert.fail("Exception expected.");
        } catch (Throwable e) {
            // then
            assertThat(listener.cause).isInstanceOf(MockitoAssertionError.class);
        }
    }

    @Test
    public void should_notify_when_verification_throws_runtime_exception() {
        // given
        VerificationListenerCallBackTest.RememberingListener listener = new VerificationListenerCallBackTest.RememberingListener();
        MockitoFramework mockitoFramework = Mockito.framework();
        mockitoFramework.addListener(listener);
        Foo foo = Mockito.mock(Foo.class);
        // when
        try {
            Mockito.verify(foo, new VerificationListenerCallBackTest.RuntimeExceptionVerificationMode()).doSomething("");
            Assert.fail("Exception expected.");
        } catch (Throwable e) {
            // then
            assertThat(listener.cause).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void should_call_verification_listeners() {
        // given
        VerificationListenerCallBackTest.RememberingListener listener = new VerificationListenerCallBackTest.RememberingListener();
        MockitoFramework mockitoFramework = Mockito.framework();
        mockitoFramework.addListener(listener);
        JUnitCore runner = new JUnitCore();
        // when
        runner.run(VerificationListenerCallBackTest.VerificationListenerSample.class);
        // then
        assertThat(listener.mock).isNotNull();
        assertThat(listener.mode).isEqualToComparingFieldByField(Mockito.times(1));
    }

    public static class VerificationListenerSample {
        @Test
        public void verificationTest() {
            Foo foo = Mockito.mock(Foo.class);
            foo.doSomething("");
            Mockito.verify(foo).doSomething("");
        }
    }

    private static class RememberingListener implements VerificationListener {
        Object mock;

        VerificationMode mode;

        VerificationData data;

        Throwable cause;

        @Override
        public void onVerification(VerificationEvent verificationEvent) {
            this.mock = verificationEvent.getMock();
            this.mode = verificationEvent.getMode();
            this.data = verificationEvent.getData();
            this.cause = verificationEvent.getVerificationError();
        }
    }

    private static class RememberingListener2 extends VerificationListenerCallBackTest.RememberingListener {}

    private static class RuntimeExceptionVerificationMode implements VerificationMode {
        @Override
        public void verify(VerificationData data) {
            throw new RuntimeException();
        }

        @Override
        public VerificationMode description(String description) {
            return null;
        }
    }
}

